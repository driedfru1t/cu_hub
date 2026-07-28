package com.nikol.lms.data.remote.model.common

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
enum class PublicationStateDTO {
    @SerialName("published")
    PUBLISHED,
    @SerialName("archived")
    ARCHIVED,
    @SerialName("draft")
    DRAFT
}
