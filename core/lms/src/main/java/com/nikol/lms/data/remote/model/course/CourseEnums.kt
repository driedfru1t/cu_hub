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
enum class CourseCategoryCoverDto {
    @SerialName("sineWave")
    SINE_WAVE,
    @SerialName("curvesGrid")
    CURVES_GRID,
    @SerialName("ellipsesOverlap")
    ELLIPSES_OVERLAP,
    @SerialName("geometricTriangle")
    GEOMETRIC_TRIANGLE,
    @SerialName("verticalLines")
    VERTICAL_LINES,
    @SerialName("gridPlanes")
    GRID_PLANES,
    @SerialName("spiralLoops")
    SPIRAL_LOOPS,
    @SerialName("symmetricCircles")
    SYMMETRIC_CIRCLES
}

@Serializable
enum class CourseSkillLevelDto {
    @SerialName("none")
    NONE
}

@Serializable
enum class LongreadTypeDto {
    @SerialName("common")
    COMMON,
    @SerialName("handout")
    HANDOUT
}
