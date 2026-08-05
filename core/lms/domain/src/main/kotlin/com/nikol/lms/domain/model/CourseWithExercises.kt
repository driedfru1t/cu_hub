package com.nikol.lms.domain.model

data class CourseWithExercises(
    val id: Int,
    val name: String,
    val isArchived: Boolean,
    val settings: CourseSettings,
    val blocker: Blocker,
    val exercises: List<CourseExerciseItem>
)

data class CourseExerciseItem(
    val id: Int,
    val name: String,
    val type: TaskType,
    val activity: CourseExerciseActivity,
    val longread: TaskLongread,
    val theme: CourseExerciseTheme
)

data class CourseExerciseActivity(
    val id: Int,
    val name: String,
    val bestScoresCount: Double?,
    val isBlocker: Boolean
)

data class CourseExerciseTheme(
    val id: Int,
    val name: String,
    val order: Int
)
