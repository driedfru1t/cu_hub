package com.nikol.ui

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.nikol.lms.domain.model.CourseCategory
import com.nikol.lms.domain.model.CourseSettings
import com.nikol.lms.domain.model.CourseSkillLevel
import com.nikol.lms.domain.model.CourseSummary
import com.nikol.lms.domain.model.ParticipationType
import com.nikol.lms.domain.model.PublicationState
import java.time.Instant

class CourseSummaryPreviewProvider : PreviewParameterProvider<CourseSummary> {
    override val values: Sequence<CourseSummary> = sequenceOf(
        CoursePreviewData.singleCourse
    )
}

class CourseSummaryListPreviewProvider : PreviewParameterProvider<List<CourseSummary>> {
    override val values: Sequence<List<CourseSummary>> = sequenceOf(
        CoursePreviewData.courseList
    )
}

object CoursePreviewData {
    val singleCourse = CourseSummary(
        id = 1,
        name = "Android Development with Kotlin",
        state = PublicationState.PUBLISHED,
        publishDate = Instant.parse("2026-01-15T00:00:00Z"),
        publishedAt = Instant.parse("2026-01-15T12:00:00Z"),
        settings = CourseSettings(
            skillLevel = CourseSkillLevel.NONE,
            isSkillLevelEnabled = false,
            syllabusUrl = "https://example.com/syllabus",
            timeChannelUrl = "https://t.me/android_course"
        ),
        subjectId = 101,
        isArchived = false,
        category = CourseCategory.DEVELOPMENT,
        participationType = ParticipationType.REQUIRED,
        allOrderIndex = 0,
        categoryOrderIndex = 0
    )

    val courseList = listOf(
        singleCourse,
        CourseSummary(
            id = 2,
            name = "UI/UX Design Basics",
            state = PublicationState.DRAFT,
            publishDate = null,
            publishedAt = null,
            settings = CourseSettings(
                skillLevel = CourseSkillLevel.NONE,
                isSkillLevelEnabled = false,
                syllabusUrl = null,
                timeChannelUrl = null
            ),
            subjectId = null,
            isArchived = false,
            category = CourseCategory.DESIGN,
            participationType = ParticipationType.ELECTIVE,
            allOrderIndex = 1,
            categoryOrderIndex = 0
        ),
        CourseSummary(
            id = 3,
            name = "Linear Algebra for ML",
            state = PublicationState.PUBLISHED,
            publishDate = Instant.parse("2025-12-01T00:00:00Z"),
            publishedAt = Instant.parse("2025-12-01T10:00:00Z"),
            settings = CourseSettings(
                skillLevel = CourseSkillLevel.NONE,
                isSkillLevelEnabled = false,
                syllabusUrl = "https://example.com/math-syllabus",
                timeChannelUrl = null
            ),
            subjectId = 102,
            isArchived = false,
            category = CourseCategory.MATHEMATICS,
            participationType = ParticipationType.LISTENER,
            allOrderIndex = 2,
            categoryOrderIndex = 0
        )
    )
}