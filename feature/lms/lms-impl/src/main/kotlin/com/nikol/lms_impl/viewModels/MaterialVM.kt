package com.nikol.lms_impl.viewModels

import com.nikol.lms.backround.DownloadStatus
import com.nikol.lms.backround.FileDAO
import com.nikol.lms.backround.FileEntity
import com.nikol.lms.backround.formatFileName
import com.nikol.lms.domain.model.ExerciseCodingMaterial
import com.nikol.lms.domain.model.FileMaterial
import com.nikol.lms.domain.useCase.GetThemeMaterialsUseCase
import com.nikol.lms.domain.useCase.ThemeMaterialParam
import com.nikol.lms.ui.MaterialToUiMapper
import com.nikol.lms_impl.mvi.intent.ThemeMaterialIntent
import com.nikol.lms_impl.mvi.state.ThemeMaterialState
import direct.direct_core.DirectEffect
import direct.direct_core.on
import direct.direct_core.onLatest
import direct.direct_viewmodel.DirectViewModel
import kotlinx.collections.immutable.persistentMapOf
import kotlinx.collections.immutable.toImmutableMap
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject

sealed interface ThemeMaterialEffect : DirectEffect {
    data object StartWorkManager : ThemeMaterialEffect
    data class OpenFile(val uri: String, val mimeType: String?) : ThemeMaterialEffect
}

typealias ThemeMaterialsStore = DirectViewModel<ThemeMaterialIntent, ThemeMaterialState, ThemeMaterialEffect>

class MaterialVM @Inject constructor(
    private val id: Int,
    private val materialToUiMapper: MaterialToUiMapper,
    private val getThemeMaterialsUseCase: GetThemeMaterialsUseCase,
    private val fileDAO: FileDAO,
) : ThemeMaterialsStore() {

    override fun createInitialState(): ThemeMaterialState = ThemeMaterialState.Loading

    private val fileFlow = MutableStateFlow<List<String>>(emptyList())

    override fun handleIntents() = intents {
        onLatest<ThemeMaterialIntent.Load> {
            val fileList = mutableListOf<FileEntity>()
            val newState = getThemeMaterialsUseCase(ThemeMaterialParam(id)).fold(
                ifLeft = { ThemeMaterialState.Error },
                ifRight = { materials ->
                    for (it in materials) {
                        when (it) {
                            is FileMaterial -> {
                                fileList.add(
                                    FileEntity(
                                        id = formatFileName(it.content.name, it.version),
                                        fileName = it.filename,
                                        name = it.content.name,
                                        version = it.version,
                                        size = it.content.length
                                    )
                                )
                            }

                            is ExerciseCodingMaterial -> {
                                fileList.addAll(
                                    it.attachments.map { file ->
                                        FileEntity(
                                            id = formatFileName(file.name, file.version),
                                            fileName = file.filename,
                                            name = file.name,
                                            version = file.version,
                                            size = file.length
                                        )
                                    }
                                )
                            }

                            else -> continue
                        }
                    }

                    fileDAO.insertIgnore(fileList)

                    val mappedData = materialToUiMapper.map(materials)
                    ThemeMaterialState.ThemeMaterialSuccess(mappedData, persistentMapOf())
                }
            )
            setState { newState }
            fileFlow.tryEmit(fileList.map { it.id })
        }

        on<ThemeMaterialIntent.ClickToFile> {
            val fileId = formatFileName(it.name, it.version)
            val file = fileDAO.getFileById(fileId)

            if (file?.status == DownloadStatus.COMPLETED && !file.localUri.isNullOrEmpty()) {
                setEffect { ThemeMaterialEffect.OpenFile(file.localUri!!, file.mimeType) }
            } else if (file?.status != DownloadStatus.PENDING && file?.status != DownloadStatus.DOWNLOADING) {
                fileDAO.updateStatus(listOf(fileId), DownloadStatus.PENDING)
                setEffect { ThemeMaterialEffect.StartWorkManager }
            }
        }

        listen(
            fileFlow
                .filter { it.isNotEmpty() }
                .flatMapLatest { fileDAO.getFileByIdFlow(it) }
        ) {
            serial { list ->
                val map = list.associate { it.id to it.status }.toImmutableMap()
                setState {
                    when (this) {
                        is ThemeMaterialState.ThemeMaterialSuccess -> copy(files = map)
                        else -> this
                    }
                }
            }
        }
    }

    init {
        setIntent(ThemeMaterialIntent.Load)
    }
}