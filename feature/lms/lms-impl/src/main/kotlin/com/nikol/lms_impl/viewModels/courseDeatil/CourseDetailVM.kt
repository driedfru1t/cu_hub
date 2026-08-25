package com.nikol.lms_impl.viewModels.courseDeatil

import androidx.lifecycle.viewModelScope
import arrow.core.Tuple4
import arrow.core.getOrElse
import arrow.core.raise.context.bind
import arrow.core.raise.either
import arrow.fx.coroutines.parZip
import arrow.optics.copy
import com.nikol.lms.domain.error.CourseError
import com.nikol.lms.domain.useCase.CourseDeadlinesP
import com.nikol.lms.domain.useCase.CourseParam
import com.nikol.lms.domain.useCase.GetCourseActivitiesPerformanceUseCase
import com.nikol.lms.domain.useCase.GetCourseDeadlines
import com.nikol.lms.domain.useCase.GetCourseExercisesUseCase
import com.nikol.lms.domain.useCase.GetCourseScoreUseCase
import com.nikol.lms.domain.useCase.GetCourseTasksPerformanceUseCase
import com.nikol.lms.domain.useCase.GetCourseUseCase
import com.nikol.lms.domain.useCase.ScoreParam
import com.nikol.lms_impl.mvi.intent.CourseDetailIntent
import com.nikol.lms_impl.mvi.state.CourseDetailState
import com.nikol.lms_impl.mvi.state.CourseDetailsContent
import com.nikol.lms_impl.mvi.state.CourseDetailsError
import com.nikol.lms_impl.mvi.state.MaterialsData
import com.nikol.lms_impl.mvi.state.currentTab
import com.nikol.lms_impl.mvi.state.mapper.mapToUiContent
import com.nikol.ui.state.Lce
import com.nikol.viewmodel.Router
import com.nikol.viewmodel.RouterViewModel
import direct.direct_core.DirectEffect
import direct.direct_core.DirectIntent
import direct.direct_core.on
import direct.direct_core.onSingle
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.async
import javax.inject.Inject

interface CourseDetailRouter : Router {
    fun onBack()
    fun onInfo(time: String?, sillabus: String?)
}

typealias CourseDetailStore = RouterViewModel<CourseDetailIntent, CourseDetailState, DirectEffect, CourseDetailRouter>

class CourseDetailVM @Inject constructor() : CourseDetailStore() {

    override fun createInitialState(): CourseDetailState {
        return CourseDetailState()
    }

    override fun handleIntents() = intents {
        on<CourseDetailIntent.SwitchTab> { intent ->
            setState { copy { CourseDetailState.currentTab.set(intent.tab) } }
        }
        onNavigate<CourseDetailIntent.Back> { onBack() }
        onNavigate<CourseDetailIntent.Info> { onInfo(it.timeChannelUrl, it.sillabusUrl) }
    }
}

sealed interface CourseMaterialsIntent : DirectIntent {
    data object Load : CourseMaterialsIntent
    data class OnMaterialClick(val id: Int, val name: String) : CourseMaterialsIntent
}

typealias CourseMaterialsState = Lce<CourseError, MaterialsData>
typealias CourseMaterialsStore = RouterViewModel<CourseMaterialsIntent, CourseMaterialsState, DirectEffect, CourseMaterialsR>

fun interface CourseMaterialsR : Router {
    fun onMaterialDetail(id: Int, name: String)
}

class CourseMaterialsVM @Inject constructor(
    private val id: Int,
    private val getCourseUseCase: GetCourseUseCase,
    private val getCourseDeadlines: GetCourseDeadlines
) : CourseMaterialsStore() {

    override fun createInitialState(): CourseMaterialsState = Lce.Loading

    init {
        setIntent(CourseMaterialsIntent.Load)
    }

    override fun handleIntents() = intents {
        onSingle<CourseMaterialsIntent.Load> {
            val materialDef = viewModelScope.async { getCourseUseCase(CourseParam(id)) }
            val deadlineDef = viewModelScope.async { getCourseDeadlines(CourseDeadlinesP(id)) }

            val material = materialDef.await()
            val deadlines = deadlineDef.await()

            val newState = either {
                val mat = material.bind()
                val dead = deadlines.bind()
                Lce.Content(MaterialsData(mat, dead.toImmutableList()))
            }.getOrElse { error ->
                Lce.Failure(error)
            }

            setState { newState }
        }

        onNavigate<CourseMaterialsIntent.OnMaterialClick> { onMaterialDetail(it.id, it.name) }
    }
}

sealed interface CourseGradesIntent : DirectIntent {
    data object Load : CourseGradesIntent
}

typealias CourseGradesState = Lce<CourseDetailsError, CourseDetailsContent>
typealias CourseGradesStore = RouterViewModel<CourseGradesIntent, CourseGradesState, DirectEffect, Router>

class CourseGradesVM @Inject constructor(
    private val id: Int,
    private val getCourseExercisesUseCase: GetCourseExercisesUseCase,
    private val getCourseActivitiesPerformanceUseCase: GetCourseActivitiesPerformanceUseCase,
    private val getCourseTasksPerformanceUseCase: GetCourseTasksPerformanceUseCase,
    private val getCourseScoreUseCase: GetCourseScoreUseCase
) : CourseGradesStore() {

    override fun createInitialState(): CourseGradesState = Lce.Loading

    init {
        setIntent(CourseGradesIntent.Load)
    }

    override fun handleIntents() = intents {
        onSingle<CourseGradesIntent.Load> {
            val (exerciseRes, activitiesRes, tasksRes, scoreRes) = parZip(
                { getCourseExercisesUseCase(CourseParam(id)) },
                { getCourseActivitiesPerformanceUseCase(CourseParam(id)) },
                { getCourseTasksPerformanceUseCase(CourseParam(id)) },
                { getCourseScoreUseCase(ScoreParam(id)) }
            ) { res1, res2, res3, res4 -> Tuple4(res1, res2, res3, res4) }

            val result = either {
                val courseExercise = exerciseRes
                    .mapLeft { CourseDetailsError.Task(it) }
                    .bind()

                val courseActivities = activitiesRes
                    .mapLeft { CourseDetailsError.Grade(it) }
                    .bind()

                val courseTaskPerformance = tasksRes
                    .mapLeft { CourseDetailsError.Grade(it) }
                    .bind()

                val score = scoreRes.mapLeft { CourseDetailsError.Course(it) }.bind()

                mapToUiContent(courseExercise, courseActivities, courseTaskPerformance, score)
            }.fold(
                ifLeft = { Lce.Failure(it) },
                ifRight = { Lce.Content(it) }
            )

            setState { result }
        }
    }
}
