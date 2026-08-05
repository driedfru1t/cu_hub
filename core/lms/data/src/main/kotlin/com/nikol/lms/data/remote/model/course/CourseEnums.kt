package com.nikol.lms.data.remote.model.course

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
enum class CourseCategoryDto {
    @SerialName("withoutCategory")
    WITHOUT_CATEGORY,

    @SerialName("general")
    GENERAL,

    @SerialName("mathematics")
    MATHEMATICS,

    @SerialName("business")
    BUSINESS,

    @SerialName("development")
    DEVELOPMENT,

    @SerialName("stem")
    STEM,

    @SerialName("softSkills")
    SOFT_SKILLS,

    @SerialName("ml")
    ML,

    @SerialName("design")
    DESIGN,

    @SerialName("analytics")
    ANALYTICS,

    @SerialName("career")
    CAREER,

    @SerialName("management")
    MANAGEMENT
}

@Serializable
enum class CourseSkillLevelDto {
    @SerialName("none")
    NONE,

    @SerialName("advanced")
    ADVANCED,

    @SerialName("basic")
    BASIC,

    @SerialName("intermediate")
    INTERMEDIATE
}

@Serializable
enum class LongreadTypeDto {
    @SerialName("common")
    COMMON,

    @SerialName("handout")
    HANDOUT
}

@Serializable
enum class ParticipationTypeDto {

    @SerialName("required")
    REQUIRED,

    @SerialName("elective")
    ELECTIVE,

    @SerialName("listener")
    LISTENER,

    @SerialName("internal")
    INTERNAL
}
