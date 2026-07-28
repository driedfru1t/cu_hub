package com.nikol.lms.data.remote.model.material

import com.nikol.lms.data.remote.model.common.PagingDto
import com.nikol.lms.data.remote.model.common.PublicationStateDTO
import com.nikol.lms.data.remote.model.question.LongreadExerciseQuestionItemDto
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonClassDiscriminator

@OptIn(ExperimentalSerializationApi::class)
@Serializable
@JsonClassDiscriminator("discriminator")
sealed interface LongreadMaterialItemDto {
    val discriminator: String
    val state: PublicationStateDTO
    val viewType: LongreadMaterialViewTypeDto
    val type: LongreadMaterialTypeDto
    val id: Int
    val order: Int
}

@Serializable
@SerialName("markdown")
data class LongreadMarkdownMaterialItemDto(
    @SerialName("discriminator") override val discriminator: String,
    @SerialName("state") override val state: PublicationStateDTO,
    @SerialName("viewType") override val viewType: LongreadMaterialViewTypeDto,
    @SerialName("type") override val type: LongreadMaterialTypeDto,
    @SerialName("id") override val id: Int,
    @SerialName("order") override val order: Int,
    @SerialName("viewContent") val viewContent: String,
    @SerialName("publishDate") val publishDate: String?,
    @SerialName("publishedAt") val publishedAt: String?
) : LongreadMaterialItemDto

@Serializable
@SerialName("file")
data class LongreadFileMaterialItemDto(
    @SerialName("discriminator") override val discriminator: String,
    @SerialName("state") override val state: PublicationStateDTO,
    @SerialName("viewType") override val viewType: LongreadMaterialViewTypeDto,
    @SerialName("type") override val type: LongreadMaterialTypeDto,
    @SerialName("id") override val id: Int,
    @SerialName("order") override val order: Int,
    @SerialName("mediaType") val mediaType: FileMediaTypeDto,
    @SerialName("filename") val filename: String,
    @SerialName("version") val version: String?,
    @SerialName("length") val length: Long,
    @SerialName("publishDate") val publishDate: String?,
    @SerialName("publishedAt") val publishedAt: String?,
    @SerialName("content") val content: LongreadMaterialContentDto
) : LongreadMaterialItemDto

@Serializable
@SerialName("coding")
data class LongreadExerciseCodingMaterialItemDto(
    @SerialName("discriminator") override val discriminator: String,
    @SerialName("state") override val state: PublicationStateDTO,
    @SerialName("viewType") override val viewType: LongreadMaterialViewTypeDto,
    @SerialName("type") override val type: LongreadMaterialTypeDto,
    @SerialName("id") override val id: Int,
    @SerialName("order") override val order: Int,
    @SerialName("name") val name: String,
    @SerialName("viewContent") val viewContent: String?,
    @SerialName("estimation") val estimation: LongreadMaterialEstimationDto,
    @SerialName("reviewers") val reviewers: List<String>,
    @SerialName("assignees") val assignees: List<String>,
    @SerialName("attachments") val attachments: List<LongreadMaterialAttachmentItemDto>,
    @SerialName("backloggedAt") val backloggedAt: String?,
    @SerialName("taskId") val taskId: Int,
    @SerialName("isTrackingStudents") val isTrackingStudents: Boolean,
    @SerialName("exerciseUrl") val exerciseUrl: String?,
    @SerialName("coding") val coding: LongreadMaterialCodingDto
) : LongreadMaterialItemDto

@Serializable
@SerialName("image")
data class LongreadImageMaterialItemDto(
    @SerialName("discriminator") override val discriminator: String,
    @SerialName("state") override val state: PublicationStateDTO,
    @SerialName("viewType") override val viewType: LongreadMaterialViewTypeDto,
    @SerialName("type") override val type: LongreadMaterialTypeDto,
    @SerialName("id") override val id: Int,
    @SerialName("order") override val order: Int,
    @SerialName("imageScale") val imageScale: Int?,
    @SerialName("mediaType") val mediaType: FileMediaTypeDto,
    @SerialName("filename") val filename: String,
    @SerialName("version") val version: String?,
    @SerialName("length") val length: Long,
    @SerialName("publishDate") val publishDate: String?,
    @SerialName("publishedAt") val publishedAt: String?,
    @SerialName("content") val content: LongreadMaterialContentDto
) : LongreadMaterialItemDto

@Serializable
@SerialName("questions")
data class LongreadExerciseQuestionsMaterialItemDto(
    @SerialName("discriminator") override val discriminator: String,
    @SerialName("state") override val state: PublicationStateDTO,
    @SerialName("viewType") override val viewType: LongreadMaterialViewTypeDto,
    @SerialName("type") override val type: LongreadMaterialTypeDto,
    @SerialName("id") override val id: Int,
    @SerialName("order") override val order: Int,
    @SerialName("name") val name: String,
    @SerialName("viewContent") val viewContent: String?,
    @SerialName("estimation") val estimation: LongreadMaterialEstimationDto,
    @SerialName("reviewers") val reviewers: List<String>,
    @SerialName("assignees") val assignees: List<String>,
    @SerialName("attachments") val attachments: List<LongreadMaterialAttachmentItemDto>,
    @SerialName("backloggedAt") val backloggedAt: String?,
    @SerialName("taskId") val taskId: Int,
    @SerialName("isTrackingStudents") val isTrackingStudents: Boolean,
    @SerialName("mode") val mode: LongreadExerciseQuestionsModeDto,
    @SerialName("areQuestionsShuffled") val areQuestionsShuffled: Boolean,
    @SerialName("settings") val settings: ExerciseQuestionsSettingsDto,
    @SerialName("quizId") val quizId: Int,
    @SerialName("questions") val questions: List<LongreadExerciseQuestionItemDto>
) : LongreadMaterialItemDto

@Serializable
@SerialName("videoPlatform")
data class LongreadVideoPlatformMaterialItemDto(
    @SerialName("discriminator") override val discriminator: String,
    @SerialName("state") override val state: PublicationStateDTO,
    @SerialName("viewType") override val viewType: LongreadMaterialViewTypeDto,
    @SerialName("type") override val type: LongreadMaterialTypeDto,
    @SerialName("id") override val id: Int,
    @SerialName("order") override val order: Int,
    @SerialName("name") val name: String,
    @SerialName("description") val description: String,
    @SerialName("videoId") val videoId: String,
    @SerialName("timecodes") val timecodes: List<String>,
    @SerialName("publishDate") val publishDate: String?,
    @SerialName("videoState") val videoState: VideoPlatformStateDto,
    @SerialName("url") val url: String
) : LongreadMaterialItemDto

@Serializable
@SerialName("video")
data class LongreadVideoMaterialItemDto(
    @SerialName("discriminator") override val discriminator: String,
    @SerialName("state") override val state: PublicationStateDTO,
    @SerialName("viewType") override val viewType: LongreadMaterialViewTypeDto,
    @SerialName("type") override val type: LongreadMaterialTypeDto,
    @SerialName("id") override val id: Int,
    @SerialName("order") override val order: Int,
    @SerialName("mediaType") val mediaType: FileMediaTypeDto,
    @SerialName("filename") val filename: String,
    @SerialName("version") val version: String?,
    @SerialName("length") val length: Long,
    @SerialName("publishDate") val publishDate: String?,
    @SerialName("publishedAt") val publishedAt: String?,
    @SerialName("content") val content: LongreadMaterialContentDto
) : LongreadMaterialItemDto

@Serializable
@SerialName("audio")
data class LongreadAudioMaterialItemDto(
    @SerialName("discriminator") override val discriminator: String,
    @SerialName("state") override val state: PublicationStateDTO,
    @SerialName("viewType") override val viewType: LongreadMaterialViewTypeDto,
    @SerialName("type") override val type: LongreadMaterialTypeDto,
    @SerialName("id") override val id: Int,
    @SerialName("order") override val order: Int,
    @SerialName("mediaType") val mediaType: FileMediaTypeDto,
    @SerialName("filename") val filename: String,
    @SerialName("version") val version: String?,
    @SerialName("length") val length: Long,
    @SerialName("publishDate") val publishDate: String?,
    @SerialName("publishedAt") val publishedAt: String?,
    @SerialName("content") val content: LongreadMaterialContentDto
) : LongreadMaterialItemDto

@Serializable
data class LongreadMaterialsByIdResponseDto(
    @SerialName("items") val items: List<LongreadMaterialItemDto>,
    @SerialName("paging") val paging: PagingDto
)
