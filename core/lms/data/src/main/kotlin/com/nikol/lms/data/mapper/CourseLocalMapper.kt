package com.nikol.lms.data.mapper

import com.nikol.lms.data.local.CourseOverviewWithThemes
import com.nikol.lms.data.local.LongreadWithExercises
import com.nikol.lms.data.local.ThemeWithLongreads
import com.nikol.lms.data.local.entity.ActivityDefinitionEmbedded
import com.nikol.lms.data.local.entity.CourseOverviewEntity
import com.nikol.lms.data.local.entity.CourseSettingsEmbedded
import com.nikol.lms.data.local.entity.CourseSettingsEntity
import com.nikol.lms.data.local.entity.CourseSummaryEntity
import com.nikol.lms.data.local.entity.CourseThemeEntity
import com.nikol.lms.data.local.entity.ExerciseEntity
import com.nikol.lms.data.local.entity.LongreadEntity
import com.nikol.lms.data.remote.model.course.ActivityDefinitionDto
import com.nikol.lms.data.remote.model.course.CourseOverviewByIdResponseDto
import com.nikol.lms.data.remote.model.course.CourseSettingsDTO
import com.nikol.lms.data.remote.model.course.CourseSummaryItemDto
import com.nikol.lms.domain.model.ActivityDefinition
import com.nikol.lms.domain.model.CourseCategory
import com.nikol.lms.domain.model.CourseOverview
import com.nikol.lms.domain.model.CourseSettings
import com.nikol.lms.domain.model.CourseSkillLevel
import com.nikol.lms.domain.model.CourseSummary
import com.nikol.lms.domain.model.CourseTheme
import com.nikol.lms.domain.model.Exercise
import com.nikol.lms.domain.model.Longread
import com.nikol.lms.domain.model.PublicationState
import java.time.Instant

data class FlatCourseOverviewEntities(
    val course: CourseOverviewEntity,
    val themes: List<CourseThemeEntity>,
    val longreads: List<LongreadEntity>,
    val exercises: List<ExerciseEntity>
)

// LOCAL ENTITY -> DOMAIN

fun CourseSettingsEntity.toDomain(): CourseSettings = CourseSettings(
    skillLevel = runCatching { CourseSkillLevel.valueOf(skillLevel.uppercase()) }
        .getOrDefault(CourseSkillLevel.NONE),
    isSkillLevelEnabled = isSkillLevelEnabled,
    syllabusUrl = syllabusUrl,
    timeChannelUrl = timeChannelUrl
)

fun CourseSummaryEntity.toDomain(): CourseSummary = CourseSummary(
    id = id,
    name = name,
    state = runCatching { PublicationState.valueOf(state.uppercase()) }
        .getOrDefault(PublicationState.DRAFT),
    publishDate = publishDate?.let { Instant.parse(it) },
    publishedAt = publishedAt?.let { Instant.parse(it) },
    settings = settings.toDomain(),
    subjectId = subjectId,
    isArchived = isArchived,
    category = runCatching { CourseCategory.valueOf(category.uppercase()) }
        .getOrDefault(CourseCategory.WITHOUT_CATEGORY),
    participationType = participationType,
    allOrderIndex = allOrderIndex,
    categoryOrderIndex = categoryOrderIndex,
)

fun ActivityDefinitionEmbedded.toDomain(): ActivityDefinition = ActivityDefinition(
    id = id,
    name = name,
    weight = weight
)

fun CourseSettingsEmbedded.toDomain(): CourseSettings = CourseSettings(
    skillLevel = skillLevel,
    isSkillLevelEnabled = isSkillLevelEnabled,
    syllabusUrl = syllabusUrl,
    timeChannelUrl = timeChannelUrl
)

fun ExerciseEntity.toDomain(): Exercise = Exercise(
    id = id,
    name = name,
    maxScore = maxScore,
    activity = activity.toDomain(),
    deadline = Instant.parse(deadline)
)

fun LongreadWithExercises.toDomain(): Longread = Longread(
    id = longread.id,
    type = longread.type,
    name = longread.name,
    state = longread.state,
    publishDate = longread.publishDate?.let { Instant.parse(it) },
    publishedAt = longread.publishedAt?.let { Instant.parse(it) },
    exercises = exercises.map { it.toDomain() }
)

fun ThemeWithLongreads.toDomain(): CourseTheme = CourseTheme(
    id = theme.id,
    name = theme.name,
    order = theme.order,
    state = theme.state,
    publishDate = theme.publishDate?.let { Instant.parse(it) },
    publishedAt = theme.publishedAt?.let { Instant.parse(it) },
    longreads = longreads.map { it.toDomain() }
)

fun CourseOverviewWithThemes.toDomain(): CourseOverview = CourseOverview(
    id = course.id,
    name = course.name,
    isArchived = course.isArchived,
    state = course.state,
    publishDate = course.publishDate?.let { Instant.parse(it) },
    publishedAt = course.publishedAt?.let { Instant.parse(it) },
    settings = course.settings.toDomain(),
    themes = themes.map { it.toDomain() }
)

// DTO -> LOCAL ENTITY

fun CourseSettingsDTO.dtoToEntity(): CourseSettingsEntity = CourseSettingsEntity(
    skillLevel = skillLevel.name,
    isSkillLevelEnabled = isSkillLevelEnabled,
    syllabusUrl = syllabusUrl,
    timeChannelUrl = timeChannelUrl
)

fun CourseSummaryItemDto.dtoToEntity(): CourseSummaryEntity = CourseSummaryEntity(
    id = id,
    name = name,
    state = state.name,
    publishDate = publishDate,
    publishedAt = publishedAt,
    settings = settings.dtoToEntity(),
    subjectId = subjectId,
    isArchived = isArchived,
    category = category.name,
    participationType = participationType.toDomain(),
    allOrderIndex = allOrderIndex,
    categoryOrderIndex = categoryOrderIndex
)

fun CourseSettingsDTO.toLocal(): CourseSettingsEmbedded = CourseSettingsEmbedded(
    skillLevel = skillLevel.toDomain(),
    isSkillLevelEnabled = isSkillLevelEnabled,
    syllabusUrl = syllabusUrl,
    timeChannelUrl = timeChannelUrl
)

fun ActivityDefinitionDto.toLocal(): ActivityDefinitionEmbedded = ActivityDefinitionEmbedded(
    id = id,
    name = name,
    weight = weight
)

fun CourseOverviewByIdResponseDto.toLocalEntities(): FlatCourseOverviewEntities {
    val courseId = id

    val courseEntity = CourseOverviewEntity(
        id = courseId,
        name = name,
        isArchived = isArchived,
        state = state.toDomain(),
        publishDate = publishDate,
        publishedAt = publishedAt,
        settings = settings.toLocal()
    )

    val themeEntities = mutableListOf<CourseThemeEntity>()
    val longreadEntities = mutableListOf<LongreadEntity>()
    val exerciseEntities = mutableListOf<ExerciseEntity>()

    for (themeDto in themes) {
        themeEntities.add(
            CourseThemeEntity(
                id = themeDto.id,
                courseId = courseId,
                name = themeDto.name,
                order = themeDto.order,
                state = themeDto.state.toDomain(),
                publishDate = themeDto.publishDate,
                publishedAt = themeDto.publishedAt
            )
        )

        for (longreadDto in themeDto.longreads) {
            longreadEntities.add(
                LongreadEntity(
                    id = longreadDto.id,
                    themeId = themeDto.id,
                    type = longreadDto.type.toDomain(),
                    name = longreadDto.name,
                    state = longreadDto.state.toDomain(),
                    publishDate = longreadDto.publishDate,
                    publishedAt = longreadDto.publishedAt
                )
            )

            for (exerciseDto in longreadDto.exercises) {
                exerciseEntities.add(
                    ExerciseEntity(
                        id = exerciseDto.id,
                        longreadId = longreadDto.id,
                        name = exerciseDto.name,
                        maxScore = exerciseDto.maxScore,
                        activity = exerciseDto.activity.toLocal(),
                        deadline = exerciseDto.deadline
                    )
                )
            }
        }
    }

    return FlatCourseOverviewEntities(
        course = courseEntity,
        themes = themeEntities,
        longreads = longreadEntities,
        exercises = exerciseEntities
    )
}

fun CourseOverviewByIdResponseDto.toLocalRelation(): CourseOverviewWithThemes {
    val courseId = this.id
    return CourseOverviewWithThemes(
        course = CourseOverviewEntity(
            id = courseId,
            name = name,
            isArchived = isArchived,
            state = state.toDomain(),
            publishDate = publishDate,
            publishedAt = publishedAt,
            settings = settings.toLocal()
        ),
        themes = themes.map { themeDto ->
            ThemeWithLongreads(
                theme = CourseThemeEntity(
                    id = themeDto.id,
                    courseId = courseId,
                    name = themeDto.name,
                    order = themeDto.order,
                    state = themeDto.state.toDomain(),
                    publishDate = themeDto.publishDate,
                    publishedAt = themeDto.publishedAt
                ),
                longreads = themeDto.longreads.map { longreadDto ->
                    LongreadWithExercises(
                        longread = LongreadEntity(
                            id = longreadDto.id,
                            themeId = themeDto.id,
                            type = longreadDto.type.toDomain(),
                            name = longreadDto.name,
                            state = longreadDto.state.toDomain(),
                            publishDate = longreadDto.publishDate,
                            publishedAt = longreadDto.publishedAt
                        ),
                        exercises = longreadDto.exercises.map { exerciseDto ->
                            ExerciseEntity(
                                id = exerciseDto.id,
                                longreadId = longreadDto.id,
                                name = exerciseDto.name,
                                maxScore = exerciseDto.maxScore,
                                activity = exerciseDto.activity.toLocal(),
                                deadline = exerciseDto.deadline
                            )
                        }
                    )
                }
            )
        }
    )
}
