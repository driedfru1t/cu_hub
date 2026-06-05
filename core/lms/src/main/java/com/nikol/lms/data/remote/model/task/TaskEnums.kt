package com.nikol.lms.data.remote.model.task

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
enum class TaskTypeDto {
    @SerialName("coding")
    CODING,
    @SerialName("questions")
    QUESTIONS
}

@Serializable
enum class TaskStateDto {
    @SerialName("backlog")
    BACKLOG,
    @SerialName("inProgress")
    IN_PROGRESS,
    @SerialName("submitted")
    SUBMITTED,
    @SerialName("review")
    REVIEW,
    @SerialName("evaluated")
    EVALUATED,
    @SerialName("failed")
    FAILED
}

@Serializable
enum class TaskScoreSkillLevelDto {
    @SerialName("none")
    NONE,
    @SerialName("basic")
    BASIC,
    @SerialName("intermediate")
    INTERMEDIATE,
    @SerialName("advanced")
    ADVANCED
}
