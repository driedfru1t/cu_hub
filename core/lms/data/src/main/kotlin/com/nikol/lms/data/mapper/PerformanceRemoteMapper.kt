package com.nikol.lms.data.mapper

import com.nikol.lms.data.remote.model.course.CourseExerciseActivityDto
import com.nikol.lms.data.remote.model.course.CourseExerciseItemDto
import com.nikol.lms.data.remote.model.course.CourseExerciseThemeDto
import com.nikol.lms.data.remote.model.course.CourseWithExercisesResponseDto
import com.nikol.lms.data.remote.model.performance.ActivityPerformanceDTO
import com.nikol.lms.data.remote.model.performance.ActivityPerformanceItemDTO
import com.nikol.lms.data.remote.model.performance.ActivityPerformanceItemsDTO
import com.nikol.lms.data.remote.model.performance.ActivityStudentPerformanceDto
import com.nikol.lms.data.remote.model.performance.TaskStudentPerformanceDto
import com.nikol.lms.data.remote.model.performance.TaskStudentPerformanceItemsDto
import com.nikol.lms.domain.model.ActivityPerformance
import com.nikol.lms.domain.model.ActivityPerformanceItem
import com.nikol.lms.domain.model.ActivityStudentPerformance
import com.nikol.lms.domain.model.CourseActivitiesPerformance
import com.nikol.lms.domain.model.CourseExerciseActivity
import com.nikol.lms.domain.model.CourseExerciseItem
import com.nikol.lms.domain.model.CourseExerciseTheme
import com.nikol.lms.domain.model.CourseTasksPerformance
import com.nikol.lms.domain.model.CourseWithExercises
import com.nikol.lms.domain.model.TaskStudentPerformance

fun ActivityPerformanceItemsDTO.toDomain(): CourseActivitiesPerformance {
    return CourseActivitiesPerformance(
        items = items.map { it.toDomain() },
        blockerEnabled = blockerEnabled,
        blockerScore = blockerScore,
        courseBlockerTriggered = courseBlockerTriggered,
        activitiesBlockerTriggered = activitiesBlockerTriggered,
        totalScore = totalScore,
        totalWeight = totalWeight
    )
}

fun ActivityPerformanceDTO.toDomain(): ActivityPerformance {
    return ActivityPerformance(
        id = id,
        name = name,
        weight = weight,
        maxExercisesCount = maxExercisesCount,
        isBlocker = isBlocker,
        bestScoresCount = bestScoresCount
    )
}

fun ActivityPerformanceItemDTO.toDomain(): ActivityPerformanceItem {
    return ActivityPerformanceItem(
        activity = activity.toDomain(),
        total = total,
        average = average,
        blockerTriggered = blockerTriggered
    )
}

fun TaskStudentPerformanceDto.toDomain(): TaskStudentPerformance {
    return TaskStudentPerformance(
        id = id,
        state = state.toDomain(),
        score = score,
        scoreSkillLevel = scoreSkillLevel,
        extraScore = extraScore,
        exerciseId = exerciseId,
        maxScore = maxScore,
        activity = activity.toDomain()
    )
}

fun TaskStudentPerformanceItemsDto.toDomain(): CourseTasksPerformance {
    return CourseTasksPerformance(
        tasks = tasks.map { it.toDomain() },
        total = total,
        blockerEnabled = blockerEnabled,
        courseBlockerTriggered = courseBlockerTriggered,
        activitiesBlockerTriggered = activitiesBlockerTriggered,
        blockerScore = blockerScore
    )
}

fun ActivityStudentPerformanceDto.toDomain(): ActivityStudentPerformance {
    return ActivityStudentPerformance(
        id = id,
        name = name,
        weight = weight,
        maxExercisesCount = maxExercisesCount,
        averageScoreThreshold = averageScoreThreshold,
        isBlocker = isBlocker,
        bestScoresCount = bestScoresCount
    )
}

fun CourseExerciseActivityDto.toDomain(): CourseExerciseActivity {
    return CourseExerciseActivity(
        id = id,
        name = name,
        bestScoresCount = bestScoresCount,
        isBlocker = isBlocker
    )
}

fun CourseExerciseThemeDto.toDomain(): CourseExerciseTheme {
    return CourseExerciseTheme(
        id = id,
        name = name,
        order = order
    )
}

fun CourseExerciseItemDto.toDomain(): CourseExerciseItem {
    return CourseExerciseItem(
        id = id,
        name = name,
        type = type.toDomain(),
        activity = activity.toDomain(),
        longread = longread.toDomain(),
        theme = theme.toDomain()
    )
}

fun CourseWithExercisesResponseDto.toDomain(): CourseWithExercises {
    return CourseWithExercises(
        id = id,
        name = name,
        isArchived = isArchived,
        settings = settings.toDomain(),
        blocker = blocker.toDomain(),
        exercises = exercises.map { it.toDomain() }
    )
}