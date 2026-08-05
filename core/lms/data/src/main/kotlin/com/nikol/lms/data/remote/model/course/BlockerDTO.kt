package com.nikol.lms.data.remote.model.course


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BlockerDTO(
    @SerialName("enableAt")
    val enableAt: String?,
    @SerialName("enabled")
    val enabled: Boolean?,
    @SerialName("score")
    val score: Double?
)