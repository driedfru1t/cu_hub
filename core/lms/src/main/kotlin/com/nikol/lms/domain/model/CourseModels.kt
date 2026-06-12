package com.nikol.lms.domain.model


enum class CourseCategory {
    WITHOUT_CATEGORY, GENERAL, MATHEMATICS, BUSINESS, DEVELOPMENT,
    STEM, SOFT_SKILLS, ML, DESIGN, ANALYTICS, CAREER, MANAGEMENT
}

enum class CourseCategoryCover {
    SINE_WAVE, CURVES_GRID, ELLIPSES_OVERLAP, GEOMETRIC_TRIANGLE,
    VERTICAL_LINES, GRID_PLANES, SPIRAL_LOOPS, SYMMETRIC_CIRCLES
}

enum class CourseSkillLevel {
    NONE
}

enum class LongreadType {
    COMMON, HANDOUT
}

data class CourseSettings(
    val skillLevel: CourseSkillLevel,
    val isSkillLevelEnabled: Boolean,
    val syllabusUrl: String?,
    val timeChannelUrl: String?
)

data class ActivityDefinition(
    val id: Int,
    val name: String,
    val weight: Double
)

// На основе CourseSummaryItem из OpenAPI
data class CourseSummary(
    val id: Int,
    val name: String,
    val state: PublicationState,
    val publishDate: String?,
    val publishedAt: String?,
    val settings: CourseSettings,
    val subjectId: Int?,
    val isArchived: Boolean,
    val category: CourseCategory,
    val categoryCover: CourseCategoryCover
)

// На основе ExerciseItem из OpenAPI
data class Exercise(
    val id: Int,
    val name: String,
    val maxScore: Double,
    val activity: ActivityDefinition,
    val deadline: String
)

// На основе LongreadItem из OpenAPI
data class Longread(
    val id: Int,
    val type: LongreadType,
    val name: String,
    val state: PublicationState,
    val publishDate: String?,
    val publishedAt: String?,
    val exercises: List<Exercise>
)

// На основе CourseThemeItem из OpenAPI
data class CourseTheme(
    val id: Int,
    val name: String,
    val order: Int,
    val state: PublicationState,
    val publishDate: String?,
    val publishedAt: String?,
    val longreads: List<Longread>
)

// На основе CourseOverviewByIdResponse из OpenAPI
data class CourseOverview(
    val id: Int,
    val name: String,
    val isArchived: Boolean,
    val state: PublicationState,
    val publishDate: String?,
    val publishedAt: String?,
    val settings: CourseSettings,
    val themes: List<CourseTheme>
)
