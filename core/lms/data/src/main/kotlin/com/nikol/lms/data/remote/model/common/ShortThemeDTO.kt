package com.nikol.lms.data.remote.model.common

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ShortThemeDTO(
    @SerialName("id")
    val id: Int,
    @SerialName("name")
    val name: String
)