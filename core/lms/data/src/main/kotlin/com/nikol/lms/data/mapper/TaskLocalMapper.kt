package com.nikol.lms.data.mapper

import com.nikol.lms.data.local.entity.ExerciseTaskSummaryEmbedded
import com.nikol.lms.data.local.entity.TaskSummaryEntity
import com.nikol.lms.domain.model.ExerciseTaskSummary
import com.nikol.lms.domain.model.TaskSummary

fun ExerciseTaskSummaryEmbedded.toDomain(): ExerciseTaskSummary {
    return ExerciseTaskSummary(
        id = id,
        name = name,
        type = type,
        maxScore = maxScore,
        startDate = startDate,
        deadline = deadline,
        activity = activity
    )
}

fun TaskSummaryEntity.toDomain(): TaskSummary {
    return TaskSummary(
        id = id,
        state = state,
        score = score,
        scoreSkillLevel = scoreSkillLevel,
        isLateDaysEnabled = isLateDaysEnabled,
        extraScore = extraScore,
        createdAt = createdAt,
        startedAt = startedAt,
        submitAt = submitAt,
        rejectAt = rejectAt,
        evaluateAt = evaluateAt,
        deadline = deadline,
        lateDays = lateDays,
        exercise = exercise.toDomain(),
        course = course,
        theme = theme,
        longread = longread,
        reviewer = reviewer,
        quizSessionId = quizSessionId
    )
}

fun ExerciseTaskSummary.toEntity(): ExerciseTaskSummaryEmbedded {
    return ExerciseTaskSummaryEmbedded(
        id = id,
        name = name,
        type = type,
        maxScore = maxScore,
        startDate = startDate,
        deadline = deadline,
        activity = activity
    )
}

fun TaskSummary.toEntity(): TaskSummaryEntity {
    return TaskSummaryEntity(
        id = id,
        state = state,
        score = score,
        scoreSkillLevel = scoreSkillLevel,
        isLateDaysEnabled = isLateDaysEnabled,
        extraScore = extraScore,
        createdAt = createdAt,
        startedAt = startedAt,
        submitAt = submitAt,
        rejectAt = rejectAt,
        evaluateAt = evaluateAt,
        deadline = deadline,
        lateDays = lateDays,
        exercise = exercise.toEntity(),
        course = course,
        theme = theme,
        longread = longread,
        reviewer = reviewer,
        quizSessionId = quizSessionId
    )
}