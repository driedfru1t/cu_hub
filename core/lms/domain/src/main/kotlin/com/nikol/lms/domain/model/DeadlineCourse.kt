package com.nikol.lms.domain.model

import java.time.Instant

data class DeadlineCourse(
    val id: Int,
    val exercise: DeadlineExercise,
    val state: TaskState,
    val deadline: Instant,
    val createdAt: Instant,
    val rejectAt: Instant?,
    val reviewer: String?,
    val course: TaskCourse,
    val theme: TaskCourseTheme,
    val longread: TaskLongread
)

data class DeadlineActivity(
    val id: Int,
    val name: String,
    val weight: Double,
    val isLateDaysEnabled: Boolean
)


data class DeadlineExercise(
    val id: Int,
    val name: String,
    val type: TaskType,
    val maxScore: Double,
    val startDate: Instant,
    val deadline: Instant,
    val activity: DeadlineActivity
)