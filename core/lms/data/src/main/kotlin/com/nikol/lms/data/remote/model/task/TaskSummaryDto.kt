package com.nikol.lms.data.remote.model.task

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ExerciseTaskSummaryDto(
    val id: Int,
    val name: String,
    val type: TaskTypeDto,
    val maxScore: Double,
    val startDate: String,
    val deadline: String,
    val activity: TaskExerciseActivityDto
)

@Serializable
data class TaskSummaryDto(
    val id: Int,
    val state: TaskStateDto,
    @SerialName("score") val score: Double?,
    @SerialName("scoreSkillLevel") val scoreSkillLevel: TaskScoreSkillLevelDto?,
    @SerialName("isLateDaysEnabled") val isLateDaysEnabled: Boolean,
    @SerialName("extraScore") val extraScore: Double?,
    @SerialName("createdAt") val createdAt: String,
    @SerialName("startedAt") val startedAt: String?,
    @SerialName("submitAt") val submitAt: String?,
    @SerialName("rejectAt") val rejectAt: String?,
    @SerialName("evaluateAt") val evaluateAt: String?,
    @SerialName("deadline") val deadline: String,
    @SerialName("lateDays") val lateDays: Int?,
    @SerialName("exercise") val exercise: ExerciseTaskSummaryDto,
    @SerialName("course") val course: TaskCourseDto,
    @SerialName("theme") val theme: ShortCourseThemeDto,
    @SerialName("longread") val longread: ShortLongreadDto,
    @SerialName("reviewer") val reviewer: TaskReviewerDto?,
    @SerialName("quizSessionId") val quizSessionId: Int?,
)