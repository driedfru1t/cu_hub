package com.nikol.lms.data.remote.model.course


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CourseScoreDTO(
    @SerialName("earnedScore")
    val earnedScore: Double?,
    @SerialName("leftToEarnScore")
    val leftToEarnScore: Double?,
    @SerialName("maxScore")
    val maxScore: Double?
)