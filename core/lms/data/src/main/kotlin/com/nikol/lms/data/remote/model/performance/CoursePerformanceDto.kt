package com.nikol.lms.data.remote.model.performance

import com.nikol.lms.data.remote.model.course.ParticipationTypeDto
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class CourseListResponseDto(
    @SerialName("courses") val courses: List<CoursePerformanceDto>
)

@Serializable
data class CoursePerformanceDto(
    @SerialName("id") val id: Int,
    @SerialName("name") val name: String,
    @SerialName("blockerEnabled") val blockerEnabled: Boolean,
    @SerialName("blockerScore") val blockerScore: Double?,
    @SerialName("isArchived") val isArchived: Boolean,
    @SerialName("semesterId") val semesterId: String?,
    @SerialName("semesterNumber") val semesterNumber: Int?,
    @SerialName("courseStudentsStatus") val courseStudentsStatus: ParticipationTypeDto,
    @SerialName("courseBlockerTriggered") val courseBlockerTriggered: Boolean,
    @SerialName("activitiesBlockerTriggered") val activitiesBlockerTriggered: Boolean,
    @SerialName("total") val total: Double
)