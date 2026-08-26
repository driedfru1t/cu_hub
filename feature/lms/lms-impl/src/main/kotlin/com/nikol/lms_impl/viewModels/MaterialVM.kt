package com.nikol.lms_impl.viewModels

import arrow.optics.copy
import arrow.optics.set
import com.nikol.lms.backround.DownloadStatus
import com.nikol.lms.backround.FileDAO
import com.nikol.lms.backround.FileEntity
import com.nikol.lms.backround.formatFileName
import com.nikol.lms.data.local.dao.LongreadDao
import com.nikol.lms.domain.model.ExerciseCodingMaterial
import com.nikol.lms.domain.model.FileMaterial
import com.nikol.lms.domain.useCase.GetThemeMaterialsUseCase
import com.nikol.lms.domain.useCase.ThemeMaterialParam
import com.nikol.lms.ui.MaterialToUiMapper
import com.nikol.lms_impl.di.qualifire.MaterialId
import com.nikol.lms_impl.di.qualifire.ThemeId
import com.nikol.lms_impl.mvi.intent.ThemeMaterialIntent
import com.nikol.lms_impl.mvi.state.ThemeMaterialState
import com.nikol.lms_impl.mvi.state.ThemeMaterialSuccess
import com.nikol.lms_impl.mvi.state.currentIndex
import com.nikol.lms_impl.mvi.state.currentName
import com.nikol.lms_impl.mvi.state.longi
import com.nikol.lms_impl.mvi.state.material
import com.nikol.ui.state.Lce
import com.nikol.ui.state.map
import com.nikol.viewmodel.Router
import com.nikol.viewmodel.RouterViewModel
import direct.direct_core.DirectEffect
import direct.direct_core.listenLatest
import direct.direct_core.on
import direct.direct_core.onLatest
import direct.direct_viewmodel.DirectViewModel
import kotlinx.collections.immutable.persistentMapOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.collections.immutable.toImmutableMap
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import javax.inject.Inject

sealed interface ThemeMaterialEffect : DirectEffect {
    data object StartWorkManager : ThemeMaterialEffect
    data class OpenFile(val uri: String, val mimeType: String?) : ThemeMaterialEffect
}

interface MaterialRouter : Router {
    fun onBack()
}

typealias ThemeMaterialsStore = RouterViewModel<ThemeMaterialIntent, ThemeMaterialState, ThemeMaterialEffect, MaterialRouter>

class MaterialVM @Inject constructor(
    @param:MaterialId private val id: Int,
    @param:ThemeId private val themeId: Int,
    private val materialToUiMapper: MaterialToUiMapper,
    private val getThemeMaterialsUseCase: GetThemeMaterialsUseCase,
    private val fileDAO: FileDAO,
    private val longreadDao: LongreadDao
) : ThemeMaterialsStore() {

    override fun createInitialState(): ThemeMaterialState = ThemeMaterialState()

    private val fileFlow = MutableStateFlow<List<String>>(emptyList())

    override fun handleIntents() = intents {
        on<ThemeMaterialIntent.Init> {
            val longi = longreadDao.getShortLongread(themeId)
            val currentIndex = longi.indexOfFirst { it.id == id }
            setState {
                copy {
                    ThemeMaterialState.longi set longi.toImmutableList()
                    ThemeMaterialState.currentIndex set currentIndex
                    ThemeMaterialState.currentName set longi[currentIndex].name
                }
            }
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
                    copy(
                        material = material.map { it.copy(files = map) }
                    )
                }
            }
        }

        on<ThemeMaterialIntent.ClickLeft> {
            setState {
                if (currentIndex != 0) {
                    copy {
                        ThemeMaterialState.currentIndex set currentIndex - 1
                        ThemeMaterialState.currentName set longi[currentIndex - 1].name
                    }
                } else {
                    this
                }
            }
        }

        on<ThemeMaterialIntent.ClickRight> {
            setState {
                if (currentIndex != longi.lastIndex) {
                    copy {
                        ThemeMaterialState.currentIndex set currentIndex + 1
                        ThemeMaterialState.currentName set longi[currentIndex + 1].name
                    }
                } else {
                    this
                }
            }
        }

        listenLatest(
            state
                .filter { it.longi.isNotEmpty() }
                .map { it.currentIndex }
                .distinctUntilChanged()
        ) {
            setState { copy { ThemeMaterialState.material set Lce.Loading } }
            val fileList = mutableListOf<FileEntity>()
            val id = state.value.longi[it].id
            val newState = getThemeMaterialsUseCase(ThemeMaterialParam(id)).fold(
                ifLeft = { error -> Lce.Failure(error) },
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
                    Lce.Content(ThemeMaterialSuccess(mappedData, persistentMapOf()))
                }
            )
            setState { copy { ThemeMaterialState.material set newState } }
            fileFlow.tryEmit(fileList.map { file -> file.id })
        }

        onNavigate<ThemeMaterialIntent.OnBack>(true) { onBack() }
    }

    init {
        setIntent(ThemeMaterialIntent.Init)
    }
}