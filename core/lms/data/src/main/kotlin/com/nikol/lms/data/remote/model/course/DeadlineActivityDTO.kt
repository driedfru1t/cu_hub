package com.nikol.lms.data.remote.model.course


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DeadlineActivityDTO(
    @SerialName("id")
    val id: Int,
    @SerialName("isLateDaysEnabled")
    val isLateDaysEnabled: Boolean,
    @SerialName("name")
    val name: String,
    @SerialName("weight")
    val weight: Double
)