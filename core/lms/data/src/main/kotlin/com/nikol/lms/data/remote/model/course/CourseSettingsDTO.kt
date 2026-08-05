package com.nikol.lms.data.remote.model.course

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class CourseSettingsDTO(
    @SerialName("skillLevel") val skillLevel: CourseSkillLevelDto,
    @SerialName("isSkillLevelEnabled") val isSkillLevelEnabled: Boolean,
    @SerialName("syllabusUrl") val syllabusUrl: String?,
    @SerialName("timeChannelUrl") val timeChannelUrl: String?
)
