package com.nikol.lms_impl.mvi.state.mapper

import com.nikol.lms.domain.model.CourseActivitiesPerformance
import com.nikol.lms.domain.model.CourseScore
import com.nikol.lms.domain.model.CourseTasksPerformance
import com.nikol.lms.domain.model.CourseWithExercises
import com.nikol.lms_impl.mvi.state.CourseActivitiesPerformanceUi
import com.nikol.lms_impl.mvi.state.CourseDetailsContent
import com.nikol.lms_impl.mvi.state.CourseTasksPerformanceUi
import com.nikol.lms_impl.mvi.state.CourseWithExercisesUi
import kotlinx.collections.immutable.toImmutableList

fun mapToUiContent(
    course: CourseWithExercises,
    activities: CourseActivitiesPerformance,
    tasks: CourseTasksPerformance,
    score: CourseScore
): CourseDetailsContent {
    return CourseDetailsContent(
        courseWithExercises = CourseWithExercisesUi(
            id = course.id,
            name = course.name,
            isArchived = course.isArchived,
            settings = course.settings,
            blocker = course.blocker,
            exercises = course.exercises.toImmutableList()
        ),
        activitiesPerformance = CourseActivitiesPerformanceUi(
            items = activities.items.toImmutableList(),
            blockerEnabled = activities.blockerEnabled,
            blockerScore = activities.blockerScore,
            courseBlockerTriggered = activities.courseBlockerTriggered,
            activitiesBlockerTriggered = activities.activitiesBlockerTriggered,
            totalScore = activities.totalScore,
            totalWeight = activities.totalWeight
        ),
        tasksPerformance = CourseTasksPerformanceUi(
            tasks = tasks.tasks.toImmutableList(),
            total = tasks.total,
            blockerEnabled = tasks.blockerEnabled,
            courseBlockerTriggered = tasks.courseBlockerTriggered,
            activitiesBlockerTriggered = tasks.activitiesBlockerTriggered,
            blockerScore = tasks.blockerScore
        ),
        score = score
    )
}