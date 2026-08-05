package com.nikol.lms.data.mapper

import com.nikol.lms.data.remote.model.common.PublicationStateDTO
import com.nikol.lms.data.remote.model.common.ShortThemeDTO
import com.nikol.lms.data.remote.model.course.ActivityDefinitionDto
import com.nikol.lms.data.remote.model.course.BlockerDTO
import com.nikol.lms.data.remote.model.course.CourseCategoryDto
import com.nikol.lms.data.remote.model.course.CourseDTO
import com.nikol.lms.data.remote.model.course.CourseOverviewByIdResponseDto
import com.nikol.lms.data.remote.model.course.CourseScoreDTO
import com.nikol.lms.data.remote.model.course.CourseSettingsDTO
import com.nikol.lms.data.remote.model.course.CourseSkillLevelDto
import com.nikol.lms.data.remote.model.course.CourseSummaryItemDto
import com.nikol.lms.data.remote.model.course.CourseThemeItemDto
import com.nikol.lms.data.remote.model.course.DeadlineActivityDTO
import com.nikol.lms.data.remote.model.course.DeadlineExerciseDTO
import com.nikol.lms.data.remote.model.course.DeadlineItemDTO
import com.nikol.lms.data.remote.model.course.ExerciseItemDto
import com.nikol.lms.data.remote.model.course.LongreadItemDto
import com.nikol.lms.data.remote.model.course.LongreadTypeDto
import com.nikol.lms.data.remote.model.course.ParticipationTypeDto
import com.nikol.lms.domain.model.ActivityDefinition
import com.nikol.lms.domain.model.Blocker
import com.nikol.lms.domain.model.Course
import com.nikol.lms.domain.model.CourseCategory
import com.nikol.lms.domain.model.CourseOverview
import com.nikol.lms.domain.model.CourseScore
import com.nikol.lms.domain.model.CourseSettings
import com.nikol.lms.domain.model.CourseSkillLevel
import com.nikol.lms.domain.model.CourseSummary
import com.nikol.lms.domain.model.CourseTheme
import com.nikol.lms.domain.model.DeadlineActivity
import com.nikol.lms.domain.model.DeadlineExercise
import com.nikol.lms.domain.model.DeadlineCourse
import com.nikol.lms.domain.model.Exercise
import com.nikol.lms.domain.model.Longread
import com.nikol.lms.domain.model.LongreadType
import com.nikol.lms.domain.model.ParticipationType
import com.nikol.lms.domain.model.PublicationState
import com.nikol.lms.domain.model.TaskCourseTheme
import java.time.Instant

fun PublicationStateDTO.toDomain(): PublicationState = when (this) {
    PublicationStateDTO.PUBLISHED -> PublicationState.PUBLISHED
    PublicationStateDTO.ARCHIVED -> PublicationState.ARCHIVED
    PublicationStateDTO.DRAFT -> PublicationState.DRAFT
}

fun CourseSkillLevelDto.toDomain(): CourseSkillLevel = when (this) {
    CourseSkillLevelDto.NONE -> CourseSkillLevel.NONE
    CourseSkillLevelDto.ADVANCED -> CourseSkillLevel.ADVANCED
    CourseSkillLevelDto.BASIC -> CourseSkillLevel.BASIC
    CourseSkillLevelDto.INTERMEDIATE -> CourseSkillLevel.INTERMEDIATE
}

fun LongreadTypeDto.toDomain(): LongreadType = when (this) {
    LongreadTypeDto.COMMON -> LongreadType.COMMON
    LongreadTypeDto.HANDOUT -> LongreadType.HANDOUT
}

fun CourseCategoryDto.toDomain(): CourseCategory = when (this) {
    CourseCategoryDto.WITHOUT_CATEGORY -> CourseCategory.WITHOUT_CATEGORY
    CourseCategoryDto.GENERAL -> CourseCategory.GENERAL
    CourseCategoryDto.MATHEMATICS -> CourseCategory.MATHEMATICS
    CourseCategoryDto.BUSINESS -> CourseCategory.BUSINESS
    CourseCategoryDto.DEVELOPMENT -> CourseCategory.DEVELOPMENT
    CourseCategoryDto.STEM -> CourseCategory.STEM
    CourseCategoryDto.SOFT_SKILLS -> CourseCategory.SOFT_SKILLS
    CourseCategoryDto.ML -> CourseCategory.ML
    CourseCategoryDto.DESIGN -> CourseCategory.DESIGN
    CourseCategoryDto.ANALYTICS -> CourseCategory.ANALYTICS
    CourseCategoryDto.CAREER -> CourseCategory.CAREER
    CourseCategoryDto.MANAGEMENT -> CourseCategory.MANAGEMENT
}

fun ParticipationTypeDto?.toDomain(): ParticipationType {
    return when (this) {
        ParticipationTypeDto.REQUIRED -> ParticipationType.REQUIRED
        ParticipationTypeDto.ELECTIVE -> ParticipationType.ELECTIVE
        ParticipationTypeDto.LISTENER -> ParticipationType.LISTENER
        ParticipationTypeDto.INTERNAL -> ParticipationType.INTERNAL
        null -> ParticipationType.ALL
    }
}

fun CourseSettingsDTO.toDomain(): CourseSettings = CourseSettings(
    skillLevel = skillLevel.toDomain(),
    isSkillLevelEnabled = isSkillLevelEnabled,
    syllabusUrl = syllabusUrl,
    timeChannelUrl = timeChannelUrl
)

fun CourseSummaryItemDto.toDomain(): CourseSummary = CourseSummary(
    id = id,
    name = name,
    state = state.toDomain(),
    publishDate = publishDate?.let { Instant.parse(it) },
    publishedAt = publishedAt?.let { Instant.parse(it) },
    settings = settings.toDomain(),
    subjectId = subjectId,
    isArchived = isArchived,
    category = category.toDomain(),
    participationType = participationType.toDomain(),
    allOrderIndex = allOrderIndex,
    categoryOrderIndex = categoryOrderIndex
)

fun ActivityDefinitionDto.toDomain(): ActivityDefinition = ActivityDefinition(
    id = id,
    name = name,
    weight = weight
)

fun ExerciseItemDto.toDomain(): Exercise = Exercise(
    id = id,
    name = name,
    maxScore = maxScore,
    activity = activity.toDomain(),
    deadline = Instant.parse(deadline)
)

fun LongreadItemDto.toDomain(): Longread = Longread(
    id = id,
    type = type.toDomain(),
    name = name,
    state = state.toDomain(),
    publishDate = publishDate?.let { Instant.parse(it) },
    publishedAt = publishedAt?.let { Instant.parse(it) },
    exercises = exercises.map { it.toDomain() }
)

fun CourseThemeItemDto.toDomain(): CourseTheme = CourseTheme(
    id = id,
    name = name,
    order = order,
    state = state.toDomain(),
    publishDate = publishDate?.let { Instant.parse(it) },
    publishedAt = publishedAt?.let { Instant.parse(it) },
    longreads = longreads.map { it.toDomain() }
)

fun CourseOverviewByIdResponseDto.toDomain(): CourseOverview = CourseOverview(
    id = id,
    name = name,
    isArchived = isArchived,
    state = state.toDomain(),
    publishDate = publishDate?.let { Instant.parse(it) },
    publishedAt = publishedAt?.let { Instant.parse(it) },
    settings = settings.toDomain(),
    themes = themes.map { it.toDomain() }
)

fun CourseScoreDTO.toDomain(): CourseScore = CourseScore(
    earnedScore = earnedScore ?: 0.0,
    leftToEarnScore = leftToEarnScore ?: 0.0,
    maxScore = maxScore ?: 0.0
)

fun BlockerDTO.toDomain(): Blocker = Blocker(
    enable = enabled ?: false,
    enableAt = enableAt?.let { Instant.parse(it) },
    score = score ?: 0.0
)

fun CourseDTO.toDomain(): Course = Course(
    id = id,
    name = name,
    state = state.toDomain(),
    publishDate = publishDate?.let { Instant.parse(it) },
    publishedAt = publishedAt?.let { Instant.parse(it) },
    settings = settings.toDomain(),
    blocker = blocker.toDomain(),
    subjectId = subjectId,
    isArchived = isArchived,
    category = category.toDomain()
)

fun DeadlineActivityDTO.toDomain(): DeadlineActivity {
    return DeadlineActivity(
        id = id,
        name = name,
        weight = weight,
        isLateDaysEnabled = isLateDaysEnabled
    )
}

fun DeadlineExerciseDTO.toDomain(): DeadlineExercise {
    return DeadlineExercise(
        id = id,
        name = name,
        type = type.toDomain(),
        maxScore = maxScore,
        startDate = Instant.parse(startDate),
        deadline = Instant.parse(deadline),
        activity = deadlineActivityDto.toDomain()
    )
}

fun ShortThemeDTO.toDomain(): TaskCourseTheme {
    return TaskCourseTheme(
        id = id,
        name = name
    )
}

fun DeadlineItemDTO.toDomain(): DeadlineCourse {
    return DeadlineCourse(
        id = id,
        exercise = exercise.toDomain(),
        state = state.toDomain(),
        deadline = Instant.parse(deadline),
        createdAt = Instant.parse(createdAt),
        rejectAt = rejectAt?.let { Instant.parse(it) },
        reviewer = reviewer,
        course = courseDto.toDomain(),
        theme = themeDto.toDomain(),
        longread = longreadDto.toDomain()
    )
}
