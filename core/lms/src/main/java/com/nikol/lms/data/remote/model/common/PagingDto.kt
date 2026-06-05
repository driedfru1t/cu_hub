package com.nikol.lms.data.remote.model.common

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class PagingDto(
    @SerialName("limit") val limit: Int,
    @SerialName("offset") val offset: Int,
    @SerialName("totalCount") val totalCount: Int
)
