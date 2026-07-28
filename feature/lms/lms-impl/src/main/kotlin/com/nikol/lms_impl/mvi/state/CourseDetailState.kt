package com.nikol.lms_impl.mvi.state

import androidx.compose.runtime.Immutable
import arrow.optics.optics
import com.nikol.lms.domain.error.CourseError
import com.nikol.lms.domain.error.GradeError
import com.nikol.lms.domain.error.TaskError
import com.nikol.lms.domain.model.ActivityPerformanceItem
import com.nikol.lms.domain.model.Blocker
import com.nikol.lms.domain.model.CourseExerciseItem
import com.nikol.lms.domain.model.CourseOverview
import com.nikol.lms.domain.model.CourseScore
import com.nikol.lms.domain.model.CourseSettings
import com.nikol.lms.domain.model.DeadlineCourse
import com.nikol.lms.domain.model.TaskStudentPerformance
import com.nikol.ui.state.Lce
import direct.direct_core.DirectState
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

enum class CourseTab {
    Materials,
    Grades
}

@optics
@Immutable
data class CourseDetailsContent(
    val courseWithExercises: CourseWithExercisesUi,
    val activitiesPerformance: CourseActivitiesPerformanceUi,
    val tasksPerformance: CourseTasksPerformanceUi,
    val score: CourseScore
) {
    companion object
}

// Модели UI
@optics
@Immutable
data class CourseWithExercisesUi(
    val id: Int,
    val name: String,
    val isArchived: Boolean,
    val settings: CourseSettings,
    val blocker: Blocker,
    val exercises: ImmutableList<CourseExerciseItem>
) {
    companion object
}

@optics
@Immutable
data class CourseActivitiesPerformanceUi(
    val items: ImmutableList<ActivityPerformanceItem>,
    val blockerEnabled: Boolean,
    val blockerScore: Double?,
    val courseBlockerTriggered: Boolean,
    val activitiesBlockerTriggered: Boolean,
    val totalScore: Double,
    val totalWeight: Double
) {
    companion object
}

@optics
@Immutable
data class CourseTasksPerformanceUi(
    val tasks: ImmutableList<TaskStudentPerformance>,
    val total: Double,
    val blockerEnabled: Boolean,
    val courseBlockerTriggered: Boolean,
    val activitiesBlockerTriggered: Boolean,
    val blockerScore: Double?
) {
    companion object
}

@Immutable
sealed interface CourseDetailsError {
    data class Task(val error: TaskError) : CourseDetailsError
    data class Grade(val error: GradeError) : CourseDetailsError
    data class Course(val error: CourseError) : CourseDetailsError
}

@Immutable
@optics
data class MaterialsData(
    val materials: CourseOverview,
    val deadlines: ImmutableList<DeadlineCourse>
) : DirectState {
    companion object
}

@Immutable
@optics
data class CourseDetailState(
    val currentTab: CourseTab = CourseTab.Materials
) : DirectState {
    companion object
}