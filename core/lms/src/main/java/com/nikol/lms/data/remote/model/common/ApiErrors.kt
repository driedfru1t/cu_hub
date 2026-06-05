package com.nikol.lms.data.remote.model.common

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class BaseProblemErrorDto(
    @SerialName("type") val type: String? = null,
    @SerialName("title") val title: String,
    @SerialName("status") val status: Int,
    @SerialName("traceId") val traceId: String? = null
)


@Serializable
data class BaseBadRequestErrorDto(
    @SerialName("title") val title: String,
    @SerialName("status") val status: Int,
    @SerialName("errors") val errors: Map<String, List<String>>
)


@Serializable
data class BaseUnauthorizedErrorDto(
    @SerialName("type") val type: String? = null,
    @SerialName("title") val title: String,
    @SerialName("status") val status: Int,
    @SerialName("traceId") val traceId: String
)


@Serializable
data class BaseForbiddenErrorDto(
    @SerialName("type") val type: String? = null,
    @SerialName("title") val title: String,
    @SerialName("status") val status: Int,
    @SerialName("detail") val detail: String? = null,
    @SerialName("instance") val instance: String? = null,
    @SerialName("traceId") val traceId: String
)

@Serializable
data class BaseNotFoundErrorDto(
    @SerialName("title") val title: String,
    @SerialName("status") val status: Int,
    @SerialName("detail") val detail: String? = null,
    @SerialName("instance") val instance: String? = null,
    @SerialName("entityId") val entityId: Int? = null,
    @SerialName("traceId") val traceId: String
)