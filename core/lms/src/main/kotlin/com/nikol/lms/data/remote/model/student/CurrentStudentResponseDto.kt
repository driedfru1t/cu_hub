package com.nikol.lms.data.remote.model.student

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class CurrentStudentResponseDto(
    @SerialName("id") val id: String, // UUID
    @SerialName("lastName") val lastName: String,
    @SerialName("firstName") val firstName: String,
    @SerialName("middleName") val middleName: String?,
    @SerialName("universityEmail") val universityEmail: String,
    @SerialName("timeAccount") val timeAccount: String,
    @SerialName("studyStartYear") val studyStartYear: Int?,
    @SerialName("studyLevel") val studyLevelDto: StudyLevelDto,
    @SerialName("lateDaysBalance") val lateDaysBalance: Int
)
