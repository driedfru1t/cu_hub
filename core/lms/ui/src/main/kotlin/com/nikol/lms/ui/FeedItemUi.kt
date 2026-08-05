package com.nikol.lms.ui

import androidx.compose.runtime.Immutable
import com.nikol.common.CuHubDispatcher.Default
import com.nikol.common.Dispatcher
import com.nikol.lms.domain.model.AudioMaterial
import com.nikol.lms.domain.model.ExerciseCodingMaterial
import com.nikol.lms.domain.model.ExerciseQuestionsMaterial
import com.nikol.lms.domain.model.FileMaterial
import com.nikol.lms.domain.model.ImageMaterial
import com.nikol.lms.domain.model.LongreadMaterial
import com.nikol.lms.domain.model.MarkdownMaterial
import com.nikol.lms.domain.model.VideoMaterial
import com.nikol.lms.domain.model.VideoPlatformMaterial
import com.nikol.ui.getActiveAppLocale
import com.nikol.ui.toLocalizedStyle
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.time.Instant
import javax.inject.Inject
import kotlin.math.log10
import kotlin.math.pow

@Immutable
sealed interface FeedItemUi {
    val id: Int
    val order: Int

    @Immutable
    data class Content(
        override val id: Int,
        override val order: Int,
        val blocks: ImmutableList<HtmlBlock>
    ) : FeedItemUi

    @Immutable
    data class File(
        override val id: Int,
        override val order: Int,
        val name: String,
        val formattedSize: String,
        val filename: String,
        val version: String?
    ) : FeedItemUi

    @Immutable
    data class Homework(
        override val id: Int,
        override val order: Int,
        val title: String,
        val deadline: String?,
        val descriptionBlocks: ImmutableList<HtmlBlock>,
        val attachedFiles: ImmutableList<File>
    ) : FeedItemUi

    @Immutable
    data class Image(
        override val id: Int,
        override val order: Int,
        val filename: String,
        val version: String?
    ) : FeedItemUi
}

class MaterialToUiMapper @Inject constructor(
    private val htmlParser: HtmlToUiParser,
    private val json: Json,
    @param:Dispatcher(Default) private val dispatcher: CoroutineDispatcher,
) {
    suspend fun map(materials: List<LongreadMaterial>): ImmutableList<FeedItemUi> {
        return withContext(dispatcher) {
            materials.mapNotNull { material ->
                when (material) {
                    is ExerciseCodingMaterial -> {
                        val html = extractHtmlFromCodingContent(material.viewContent)
                        FeedItemUi.Homework(
                            id = material.id,
                            order = material.order,
                            title = material.name,
                            deadline = formatDeadline(material.estimation.deadline),
                            descriptionBlocks = htmlParser.parse(html).toImmutableList(),
                            attachedFiles = material.attachments.map { attachment ->
                                FeedItemUi.File(
                                    id = attachment.filename.hashCode(),
                                    order = 0,
                                    name = extractFileName(attachment.filename),
                                    formattedSize = formatBytes(attachment.length),
                                    filename = attachment.filename,
                                    version = attachment.version
                                )
                            }.toImmutableList()
                        )
                    }

                    is FileMaterial -> {
                        FeedItemUi.File(
                            id = material.id,
                            order = material.order,
                            name = extractFileName(material.filename),
                            formattedSize = formatBytes(material.length),
                            filename = material.filename,
                            version = material.version
                        )
                    }

                    is ImageMaterial -> {
                        FeedItemUi.Image(
                            id = material.id,
                            order = material.order,
                            filename = material.filename,
                            version = material.version
                        )
                    }

                    is MarkdownMaterial -> {
                        val html = extractHtmlFromMarkdownContent(material.viewContent)
                        FeedItemUi.Content(
                            id = material.id,
                            order = material.order,
                            blocks = htmlParser.parse(html).toImmutableList()
                        )
                    }

                    is VideoMaterial,
                    is AudioMaterial,
                    is VideoPlatformMaterial,
                    is ExerciseQuestionsMaterial -> null
                }
            }.toImmutableList()
        }
    }

    @Serializable
    private data class MarkdownViewContent(val value: String)

    @Serializable
    private data class CodingViewContent(val description: String?)

    private fun extractHtmlFromMarkdownContent(viewContent: String): String {
        return try {
            json.decodeFromString<MarkdownViewContent>(viewContent).value
        } catch (_: Exception) {
            viewContent
        }
    }

    private fun extractHtmlFromCodingContent(viewContent: String?): String {
        if (viewContent == null) return ""
        return try {
            json.decodeFromString<CodingViewContent>(viewContent).description ?: ""
        } catch (e: Exception) {
            ""
        }
    }

    private fun extractFileName(path: String): String = path.substringAfterLast("/")

    private fun formatBytes(bytes: Long): String {
        if (bytes <= 0) return "0 B"
        val units = arrayOf("B", "KB", "MB", "GB", "TB")
        val digitGroups = (log10(bytes.toDouble()) / log10(1024.0)).toInt()
        return String.format(
            getActiveAppLocale(),
            "%.1f %s",
            bytes / 1024.0.pow(digitGroups.toDouble()),
            units[digitGroups]
        )
    }

    private fun formatDeadline(instant: Instant?): String? {
        return instant?.toLocalizedStyle()
    }
}