package com.nikol.lms.data.mapper

import com.nikol.lms.data.local.entity.CourseSettingsEntity
import com.nikol.lms.data.local.entity.CourseSummaryEntity
import com.nikol.lms.data.remote.model.course.ActivityDefinitionDto
import com.nikol.lms.data.remote.model.course.CourseOverviewByIdResponseDto
import com.nikol.lms.data.remote.model.course.CourseSettingsDto
import com.nikol.lms.data.remote.model.course.CourseSummaryItemDto
import com.nikol.lms.data.remote.model.course.CourseThemeItemDto
import com.nikol.lms.data.remote.model.course.ExerciseItemDto
import com.nikol.lms.data.remote.model.course.LongreadItemDto
import com.nikol.lms.domain.model.ActivityDefinition
import com.nikol.lms.domain.model.CourseCategory
import com.nikol.lms.domain.model.CourseCategoryCover
import com.nikol.lms.domain.model.CourseOverview
import com.nikol.lms.domain.model.CourseSettings
import com.nikol.lms.domain.model.CourseSkillLevel
import com.nikol.lms.domain.model.CourseSummary
import com.nikol.lms.domain.model.CourseTheme
import com.nikol.lms.domain.model.Exercise
import com.nikol.lms.domain.model.Longread
import com.nikol.lms.domain.model.LongreadType
import com.nikol.lms.domain.model.PublicationState

fun CourseSettingsDto.toDomain(): CourseSettings {
    return CourseSettings(
        skillLevel = runCatching { CourseSkillLevel.valueOf(skillLevel.name) }
            .getOrDefault(CourseSkillLevel.NONE),
        isSkillLevelEnabled = isSkillLevelEnabled,
        syllabusUrl = syllabusUrl,
        timeChannelUrl = timeChannelUrl
    )
}

fun CourseSummaryItemDto.toDomain(): CourseSummary {
    return CourseSummary(
        id = id,
        name = name,
        state = runCatching { PublicationState.valueOf(state.name) }
            .getOrDefault(PublicationState.DRAFT),
        publishDate = publishDate,
        publishedAt = publishedAt,
        settings = settings.toDomain(),
        subjectId = subjectId,
        isArchived = isArchived,
        category = runCatching { CourseCategory.valueOf(category.name) }
            .getOrDefault(CourseCategory.WITHOUT_CATEGORY),
        categoryCover = runCatching { CourseCategoryCover.valueOf(categoryCover.name) }
            .getOrDefault(CourseCategoryCover.SINE_WAVE)
    )
}

fun CourseSettingsEntity.toDomain(): CourseSettings {
    return CourseSettings(
        skillLevel = runCatching { CourseSkillLevel.valueOf(skillLevel.uppercase()) }
            .getOrDefault(CourseSkillLevel.NONE),
        isSkillLevelEnabled = isSkillLevelEnabled,
        syllabusUrl = syllabusUrl,
        timeChannelUrl = timeChannelUrl
    )
}

fun CourseSummaryEntity.toDomain(): CourseSummary {
    return CourseSummary(
        id = id,
        name = name,
        state = runCatching { PublicationState.valueOf(state.uppercase()) }
            .getOrDefault(PublicationState.DRAFT),
        publishDate = publishDate,
        publishedAt = publishedAt,
        settings = settings.toDomain(),
        subjectId = subjectId,
        isArchived = isArchived,
        category = runCatching { CourseCategory.valueOf(category.uppercase()) }
            .getOrDefault(CourseCategory.WITHOUT_CATEGORY),
        categoryCover = runCatching { CourseCategoryCover.valueOf(categoryCover.uppercase()) }
            .getOrDefault(CourseCategoryCover.SINE_WAVE)
    )
}

fun CourseSettingsDto.dtoToEntity(): CourseSettingsEntity {
    return CourseSettingsEntity(
        skillLevel = skillLevel.name,
        isSkillLevelEnabled = isSkillLevelEnabled,
        syllabusUrl = syllabusUrl,
        timeChannelUrl = timeChannelUrl
    )
}

fun CourseSummaryItemDto.dtoToEntity(): CourseSummaryEntity {
    return CourseSummaryEntity(
        id = id,
        name = name,
        state = state.name,
        publishDate = publishDate,
        publishedAt = publishedAt,
        settings = settings.dtoToEntity(),
        subjectId = subjectId,
        isArchived = isArchived,
        category = category.name,
        categoryCover = categoryCover.name
    )
}

fun ActivityDefinitionDto.toDomain(): ActivityDefinition {
    return ActivityDefinition(
        id = id,
        name = name,
        weight = weight
    )
}

fun ExerciseItemDto.toDomain(): Exercise {
    return Exercise(
        id = id,
        name = name,
        maxScore = maxScore,
        activity = activity.toDomain(),
        deadline = deadline
    )
}

fun LongreadItemDto.toDomain(): Longread {
    return Longread(
        id = id,
        type = runCatching { LongreadType.valueOf(type.name) }
            .getOrDefault(LongreadType.COMMON),
        name = name,
        state = runCatching { PublicationState.valueOf(state.name) }
            .getOrDefault(PublicationState.DRAFT),
        publishDate = publishDate,
        publishedAt = publishedAt,
        exercises = exercises.map { it.toDomain() }
    )
}

fun CourseThemeItemDto.toDomain(): CourseTheme {
    return CourseTheme(
        id = id,
        name = name,
        order = order,
        state = runCatching { PublicationState.valueOf(state.name) }
            .getOrDefault(PublicationState.DRAFT),
        publishDate = publishDate,
        publishedAt = publishedAt,
        longreads = longreads.map { it.toDomain() }
    )
}

fun CourseOverviewByIdResponseDto.toDomain(): CourseOverview {
    return CourseOverview(
        id = id,
        name = name,
        isArchived = isArchived,
        state = runCatching { PublicationState.valueOf(state.name) }
            .getOrDefault(PublicationState.DRAFT),
        publishDate = publishDate,
        publishedAt = publishedAt,
        settings = settings.toDomain(),
        themes = themes.map { it.toDomain() }
    )
}
