package com.nikol.lms.data.remote.model.task

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class GenerateContentDownloadLinkResponseDto(
    @SerialName("url") val url: String
)
