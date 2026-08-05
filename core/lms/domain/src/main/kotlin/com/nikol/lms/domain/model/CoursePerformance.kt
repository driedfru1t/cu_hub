package com.nikol.lms.domain.model

data class CourseActivitiesPerformance(
    val items: List<ActivityPerformanceItem>,
    val blockerEnabled: Boolean,
    val blockerScore: Double?,
    val courseBlockerTriggered: Boolean,
    val activitiesBlockerTriggered: Boolean,
    val totalScore: Double,
    val totalWeight: Double
)

data class ActivityPerformanceItem(
    val activity: ActivityPerformance,
    val total: Double,
    val average: Double,
    val blockerTriggered: Boolean
)

data class ActivityPerformance(
    val id: Int,
    val name: String,
    val weight: Double,
    val maxExercisesCount: Double,
    val isBlocker: Boolean,
    val bestScoresCount: Double?
)

data class CourseTasksPerformance(
    val tasks: List<TaskStudentPerformance>,
    val total: Double,
    val blockerEnabled: Boolean,
    val courseBlockerTriggered: Boolean,
    val activitiesBlockerTriggered: Boolean,
    val blockerScore: Double?
)

data class TaskStudentPerformance(
    val id: Int,
    val state: TaskState,
    val score: Double,
    val scoreSkillLevel: TaskScoreSkillLevel?,
    val extraScore: Double?,
    val exerciseId: Int,
    val maxScore: Double,
    val activity: ActivityStudentPerformance
)

data class ActivityStudentPerformance(
    val id: Int,
    val name: String,
    val weight: Double,
    val maxExercisesCount: Double,
    val averageScoreThreshold: Double?,
    val isBlocker: Boolean,
    val bestScoresCount: Double?
)