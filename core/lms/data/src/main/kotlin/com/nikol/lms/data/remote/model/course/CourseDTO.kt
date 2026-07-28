package com.nikol.lms.data.remote.model.course


import com.nikol.lms.data.remote.model.common.PublicationStateDTO
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CourseDTO(
    @SerialName("id")
    val id: Int,
    @SerialName("isArchived")
    val isArchived: Boolean,
    @SerialName("name")
    val name: String,
    @SerialName("state")
    val state: PublicationStateDTO,
    @SerialName("publishDate")
    val publishDate: String?,
    @SerialName("publishedAt")
    val publishedAt: String?,
    @SerialName("settings")
    val settings: CourseSettingsDTO,
    @SerialName("blocker")
    val blocker: BlockerDTO,
    @SerialName("subjectId")
    val subjectId: Int,
    @SerialName("category")
    val category: CourseCategoryDto
)