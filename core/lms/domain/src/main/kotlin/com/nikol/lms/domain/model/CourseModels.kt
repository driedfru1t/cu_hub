package com.nikol.lms.domain.model

import kotlinx.serialization.SerialName
import java.time.Instant


enum class CourseCategory {
    WITHOUT_CATEGORY, GENERAL, MATHEMATICS, BUSINESS, DEVELOPMENT,
    STEM, SOFT_SKILLS, ML, DESIGN, ANALYTICS, CAREER, MANAGEMENT
}

enum class ParticipationType {
    ALL, REQUIRED, ELECTIVE, LISTENER, INTERNAL
}

enum class CourseSkillLevel {
    NONE, ADVANCED,
    BASIC, INTERMEDIATE
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

data class CourseScore(
    val earnedScore: Double,
    val leftToEarnScore: Double,
    val maxScore: Double
)

data class ActivityDefinition(
    val id: Int,
    val name: String,
    val weight: Double
)

data class CourseSummary(
    val id: Int,
    val name: String,
    val state: PublicationState,
    val publishDate: Instant?,
    val publishedAt: Instant?,
    val settings: CourseSettings,
    val subjectId: Int?,
    val isArchived: Boolean,
    val category: CourseCategory,
    val participationType: ParticipationType,
    val allOrderIndex: Int,
    val categoryOrderIndex: Int
)

data class Exercise(
    val id: Int,
    val name: String,
    val maxScore: Double,
    val activity: ActivityDefinition,
    val deadline: Instant
)

data class Longread(
    val id: Int,
    val type: LongreadType,
    val name: String,
    val state: PublicationState,
    val publishDate: Instant?,
    val publishedAt: Instant?,
    val exercises: List<Exercise>
)

data class CourseTheme(
    val id: Int,
    val name: String,
    val order: Int,
    val state: PublicationState,
    val publishDate: Instant?,
    val publishedAt: Instant?,
    val longreads: List<Longread>
)

data class CourseOverview(
    val id: Int,
    val name: String,
    val isArchived: Boolean,
    val state: PublicationState,
    val publishDate: Instant?,
    val publishedAt: Instant?,
    val settings: CourseSettings,
    val themes: List<CourseTheme>
)

data class Blocker(
    val enable: Boolean,
    val enableAt: Instant?,
    val score: Double
)

data class Course(
    val id: Int,
    val name: String,
    val state: PublicationState,
    val publishDate: Instant?,
    val publishedAt: Instant?,
    val settings: CourseSettings,
    val blocker: Blocker,
    val subjectId: Int?,
    val isArchived: Boolean,
    val category: CourseCategory
)
