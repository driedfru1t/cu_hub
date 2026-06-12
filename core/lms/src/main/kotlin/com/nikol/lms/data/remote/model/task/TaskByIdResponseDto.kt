package com.nikol.lms.data.remote.model.task

import com.nikol.lms.data.remote.model.student.CurrentStudentResponseDto
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class TaskByIdResponseDto(
    @SerialName("id") val id: Int,
    @SerialName("type") val type: TaskTypeDto,
    @SerialName("state") val state: TaskStateDto,
    @SerialName("score") val score: Double?,
    @SerialName("scoreSkillLevel") val scoreSkillLevel: TaskScoreSkillLevelDto?,
    @SerialName("isSkillLevelEnabled") val isSkillLevelEnabled: Boolean,
    @SerialName("isLateDaysEnabled") val isLateDaysEnabled: Boolean,
    @SerialName("extraScore") val extraScore: Double?,
    @SerialName("createdAt") val createdAt: String,
    @SerialName("startedAt") val startedAt: String?,
    @SerialName("submitAt") val submitAt: String?,
    @SerialName("rejectAt") val rejectAt: String?,
    @SerialName("evaluateAt") val evaluateAt: String?,
    @SerialName("deadline") val deadline: String,
    @SerialName("lateDays") val lateDays: Int?,
    @SerialName("exercise") val exercise: TaskExerciseDto,
    @SerialName("course") val course: TaskCourseDto,
    @SerialName("theme") val theme: TaskCourseThemeDto,
    @SerialName("longread") val longread: TaskLongreadDto,
    @SerialName("student") val student: CurrentStudentResponseDto,
    @SerialName("reviewer") val reviewer: TaskReviewerDto?,
    @SerialName("solution") val solution: TaskSolution?,
    @SerialName("scores") val scores: List<TaskScoresItemDto>?,
    @SerialName("quizSessionId") val quizSessionId: Int?,
    @SerialName("evaluatedAttemptId") val evaluatedAttemptId: Int?,
    @SerialName("currentAttemptId") val currentAttemptId: Int?,
    @SerialName("lastAttemptId") val lastAttemptId: Int?
)
