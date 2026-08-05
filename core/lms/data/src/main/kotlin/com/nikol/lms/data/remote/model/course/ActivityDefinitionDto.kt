package com.nikol.lms.data.remote.model.course

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class ActivityDefinitionDto(
    @SerialName("id") val id: Int,
    @SerialName("name") val name: String,
    @SerialName("weight") val weight: Double
)
