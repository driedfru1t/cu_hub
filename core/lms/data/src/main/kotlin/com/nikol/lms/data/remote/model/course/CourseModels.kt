package com.nikol.lms.data.remote.model.course

import com.nikol.lms.data.remote.model.common.PagingDto
import com.nikol.lms.data.remote.model.common.PublicationStateDTO
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CourseSummaryItemDto(
    @SerialName("id") val id: Int,
    @SerialName("name") val name: String,
    @SerialName("state") val state: PublicationStateDTO,
    @SerialName("publishDate") val publishDate: String?,
    @SerialName("publishedAt") val publishedAt: String?,
    @SerialName("settings") val settings: CourseSettingsDTO,
    @SerialName("subjectId") val subjectId: Int?,
    @SerialName("isArchived") val isArchived: Boolean,
    @SerialName("category") val category: CourseCategoryDto,
    @SerialName("participationType") val participationType: ParticipationTypeDto? = null,
    val allOrderIndex: Int = 0,
    val categoryOrderIndex: Int = 0
)

@Serializable
data class ListStudentCoursesResponseDto(
    @SerialName("items") val items: List<CourseSummaryItemDto>,
    @SerialName("paging") val pagingDto: PagingDto
)

@Serializable
data class ExerciseItemDto(
    @SerialName("id") val id: Int,
    @SerialName("name") val name: String,
    @SerialName("maxScore") val maxScore: Double,
    @SerialName("activity") val activity: ActivityDefinitionDto,
    @SerialName("deadline") val deadline: String
)

@Serializable
data class LongreadItemDto(
    @SerialName("id") val id: Int,
    @SerialName("type") val type: LongreadTypeDto,
    @SerialName("name") val name: String,
    @SerialName("state") val state: PublicationStateDTO,
    @SerialName("publishDate") val publishDate: String?,
    @SerialName("publishedAt") val publishedAt: String?,
    @SerialName("exercises") val exercises: List<ExerciseItemDto>
)

@Serializable
data class CourseThemeItemDto(
    @SerialName("id") val id: Int,
    @SerialName("name") val name: String,
    @SerialName("order") val order: Int,
    @SerialName("state") val state: PublicationStateDTO,
    @SerialName("publishDate") val publishDate: String?,
    @SerialName("publishedAt") val publishedAt: String?,
    @SerialName("longreads") val longreads: List<LongreadItemDto>
)

@Serializable
data class CourseOverviewByIdResponseDto(
    @SerialName("id") val id: Int,
    @SerialName("name") val name: String,
    @SerialName("isArchived") val isArchived: Boolean,
    @SerialName("state") val state: PublicationStateDTO,
    @SerialName("publishDate") val publishDate: String?,
    @SerialName("publishedAt") val publishedAt: String?,
    @SerialName("settings") val settings: CourseSettingsDTO,
    @SerialName("themes") val themes: List<CourseThemeItemDto>
)

@Serializable
data class CourseThemeSummaryByIdResponseDto(
    @SerialName("id") val id: Int,
    @SerialName("name") val name: String,
    @SerialName("state") val state: PublicationStateDTO,
    @SerialName("publishDate") val publishDate: String?,
    @SerialName("publishedAt") val publishedAt: String?,
    @SerialName("order") val order: Int,
    @SerialName("category") val category: CourseCategoryDto,
    @SerialName("categoryCover") val categoryCover: Int
)

@Serializable
data class LongreadSummaryByIdResponseDto(
    @SerialName("id") val id: Int,
    @SerialName("type") val type: LongreadTypeDto,
    @SerialName("name") val name: String,
    @SerialName("order") val order: Int,
    @SerialName("state") val state: PublicationStateDTO,
    @SerialName("publishDate") val publishDate: String?,
    @SerialName("publishedAt") val publishedAt: String?
)
