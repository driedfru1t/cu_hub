package com.nikol.lms.data.mapper

import com.nikol.lms.data.remote.model.material.FileMediaTypeDto
import com.nikol.lms.data.remote.model.task.*
import com.nikol.lms.domain.model.*
import java.time.Duration
import java.time.Instant

import com.nikol.lms.data.remote.model.task.TaskSolutionDto
import com.nikol.lms.data.remote.model.task.TaskSolutionAnswersItemDto
import com.nikol.lms.domain.model.TaskSolution
import com.nikol.lms.domain.model.TaskSolutionAnswersItem

// ENUMS

fun TaskTypeDto.toDomain(): TaskType = when (this) {
    TaskTypeDto.CODING -> TaskType.CODING
    TaskTypeDto.QUESTIONS -> TaskType.QUESTIONS
}

fun TaskStateDto.toDomain(): TaskState = when (this) {
    TaskStateDto.BACKLOG -> TaskState.BACKLOG
    TaskStateDto.IN_PROGRESS -> TaskState.IN_PROGRESS
    TaskStateDto.SUBMITTED -> TaskState.SUBMITTED
    TaskStateDto.REVIEW -> TaskState.REVIEW
    TaskStateDto.EVALUATED -> TaskState.EVALUATED
    TaskStateDto.FAILED -> TaskState.FAILED
}

fun TaskScoreSkillLevelDto.toDomain(): TaskScoreSkillLevel = when (this) {
    TaskScoreSkillLevelDto.NONE -> TaskScoreSkillLevel.NONE
    TaskScoreSkillLevelDto.BASIC -> TaskScoreSkillLevel.BASIC
    TaskScoreSkillLevelDto.INTERMEDIATE -> TaskScoreSkillLevel.INTERMEDIATE
    TaskScoreSkillLevelDto.ADVANCED -> TaskScoreSkillLevel.ADVANCED
}

fun FileMediaTypeDto.toDomain(): FileMediaType = when (this) {
    FileMediaTypeDto.FILE -> FileMediaType.FILE
    FileMediaTypeDto.IMAGE -> FileMediaType.IMAGE
    FileMediaTypeDto.VIDEO -> FileMediaType.VIDEO
    FileMediaTypeDto.AUDIO -> FileMediaType.AUDIO
}

// COMPONENTS

fun TaskExerciseActivityDto.toDomain(): TaskExerciseActivity = TaskExerciseActivity(
    id = id,
    name = name,
    weight = weight,
    isLateDaysEnabled = isLateDaysEnabled
)

fun TaskExerciseQuestionOrderOptionOrderDto.toDomain(): TaskExerciseQuestionOrderOptionOrder =
    TaskExerciseQuestionOrderOptionOrder(
        optionId = id,
        order = order
    )

fun TaskExerciseQuestionOrderDto.toDomain(): TaskExerciseQuestionOrder = TaskExerciseQuestionOrder(
    questionId = questionId,
    order = order,
    optionOrders = optionOrders.map { it.toDomain() }
)

fun TaskAttachmentDto.toDomain(): TaskAttachment = TaskAttachment(
    name = name,
    filename = filename,
    mediaType = mediaType.toDomain(),
    length = length,
    version = version
)

fun TaskExerciseDto.toDomain(): TaskExercise = TaskExercise(
    id = id,
    name = name,
    type = type.toDomain(),
    maxScore = maxScore,
    startDate = Instant.parse(startDate),
    deadline = Instant.parse(deadline),
    timer = timer?.let { Duration.parse(it) },
    activity = activity.toDomain(),
    questionOrders = questionOrders?.map { it.toDomain() },
    areQuestionsShuffled = areQuestionsShuffled,
    quizId = quizId,
    mode = mode,
    viewContent = viewContent,
    exerciseUrl = exerciseUrl,
    attachments = attachments?.map { it.toDomain() },
    settings = settings?.toDomain()
)

fun TaskCourseDto.toDomain(): TaskCourse = TaskCourse(
    id = id,
    name = name,
    isArchived = isArchived
)

fun ShortCourseThemeDto.toDomain(): TaskCourseTheme = TaskCourseTheme(
    id = id,
    name = name
)

fun ShortLongreadDto.toDomain(): TaskLongread = TaskLongread(
    id = id,
    name = name
)

fun TaskReviewerDto.toDomain(): TaskReviewer = TaskReviewer(
    id = id,
    name = name,
    identityEmail = identityEmail,
    email = email,
    identityIdentifier = identityIdentifier,
    timeAccount = timeAccount,
    lastName = lastName,
    firstName = firstName,
    middleName = middleName
)

fun TaskScoresItemDto.toDomain(): TaskScoresItem = TaskScoresItem(
    questionId = questionId,
    score = score
)

// SOLUTION & HIERARCHY

fun TaskSolutionAnswersQuestionOptionsItemDto.toDomain(): TaskSolutionAnswersQuestionOptionsItem =
    TaskSolutionAnswersQuestionOptionsItem(
        id = id,
        isCorrect = isCorrect,
        recommendation = recommendation
    )

fun TaskSolutionAnswersQuestionInputAnswerDto.toDomain(): TaskSolutionAnswersQuestionInputAnswer =
    TaskSolutionAnswersQuestionInputAnswer(
        values = values,
        value = value,
        recommendation = recommendation
    )

fun TaskSolutionAnswersQuestionInputNumberAnswerDto.toDomain(): TaskSolutionAnswersQuestionInputNumberAnswer =
    TaskSolutionAnswersQuestionInputNumberAnswer(
        values = values,
        value = value,
        recommendation = recommendation
    )

fun TaskSolutionAnswersQuestionDto.toDomain(): TaskSolutionAnswersQuestion = when (this) {
    is TaskSolutionAnswersQuestionDtoSingleChoiceDto -> TaskSolutionAnswersQuestionSingleChoice(
        id = id,
        answer = answer,
        options = options.map { it.toDomain() }
    )

    is TaskSolutionAnswersQuestionDtoMultipleChoiceDto -> TaskSolutionAnswersQuestionMultipleChoice(
        id = id,
        answer = answer,
        options = options.map { it.toDomain() }
    )

    is TaskSolutionAnswersQuestionInputQuestionDtoItemDto -> TaskSolutionAnswersQuestionInput(
        id = id,
        answer = answer?.toDomain(),
        options = options
    )

    is TaskSolutionAnswersQuestionInputNumberQuestionDtoItem -> TaskSolutionAnswersQuestionInputNumber(
        id = id,
        answer = answer?.toDomain(),
        options = options
    )
}

fun TaskSolutionAnswersItemDto.toDomain(): TaskSolutionAnswersItem = TaskSolutionAnswersItem(
    answer = answer,
    question = question.toDomain()
)

fun TaskSolutionDto.toDomain(): TaskSolution = TaskSolution(
    type = type.toDomain(),
    solutionUrl = solutionUrl,
    answers = answers?.map { it.toDomain() },
    attachments = attachments?.map { it.toDomain() }
)

// ROOT DETAILS
fun TaskByIdResponseDto.toDomain(): TaskDetails = TaskDetails(
    id = id,
    type = type.toDomain(),
    state = state.toDomain(),
    score = score,
    scoreSkillLevel = scoreSkillLevel?.toDomain(),
    isSkillLevelEnabled = isSkillLevelEnabled,
    isLateDaysEnabled = isLateDaysEnabled,
    extraScore = extraScore,
    createdAt = Instant.parse(createdAt),
    startedAt = startedAt?.let { Instant.parse(it) },
    submitAt = submitAt?.let { Instant.parse(it) },
    rejectAt = rejectAt?.let { Instant.parse(it) },
    evaluateAt = evaluateAt?.let { Instant.parse(it) },
    deadline = Instant.parse(deadline),
    lateDays = lateDays,
    exercise = exercise.toDomain(),
    course = course.toDomain(),
    theme = theme.toDomain(),
    longread = longread.toDomain(),
    student = student.toDomain(),
    reviewer = reviewer?.toDomain(),
    solution = solution?.toDomain(),
    scores = scores?.map { it.toDomain() },
    quizSessionId = quizSessionId,
    evaluatedAttemptId = evaluatedAttemptId,
    currentAttemptId = currentAttemptId,
    lastAttemptId = lastAttemptId
)