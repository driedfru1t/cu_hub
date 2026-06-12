package com.nikol.lms.data.mapper

import com.nikol.lms.data.remote.model.material.ExerciseQuestionsSettingsDto
import com.nikol.lms.data.remote.model.material.LongreadAudioMaterialItemDto
import com.nikol.lms.data.remote.model.material.LongreadExerciseCodingMaterialItemDto
import com.nikol.lms.data.remote.model.material.LongreadExerciseQuestionsMaterialItemDto
import com.nikol.lms.data.remote.model.material.LongreadFileMaterialItemDto
import com.nikol.lms.data.remote.model.material.LongreadImageMaterialItemDto
import com.nikol.lms.data.remote.model.material.LongreadMarkdownMaterialItemDto
import com.nikol.lms.data.remote.model.material.LongreadMaterialActivityDto
import com.nikol.lms.data.remote.model.material.LongreadMaterialAttachmentItemDto
import com.nikol.lms.data.remote.model.material.LongreadMaterialCodingDto
import com.nikol.lms.data.remote.model.material.LongreadMaterialContentDto
import com.nikol.lms.data.remote.model.material.LongreadMaterialEstimationDto
import com.nikol.lms.data.remote.model.material.LongreadMaterialItemDto
import com.nikol.lms.data.remote.model.material.LongreadVideoMaterialItemDto
import com.nikol.lms.data.remote.model.material.LongreadVideoPlatformMaterialItemDto
import com.nikol.lms.data.remote.model.question.LongreadExerciseInputQuestionItemDto
import com.nikol.lms.data.remote.model.question.LongreadExerciseMultipleChoiceQuestionItemDto
import com.nikol.lms.data.remote.model.question.LongreadExerciseQuestionItemDto
import com.nikol.lms.data.remote.model.question.LongreadExerciseSingleChoiceQuestionitemDto
import com.nikol.lms.data.remote.model.question.QuestionItemCorrectAnswerDto
import com.nikol.lms.data.remote.model.question.QuestionItemCorrectAnswerInputNumberCorrectAnswerDto
import com.nikol.lms.data.remote.model.question.QuestionItemCorrectAnswerInputStringCorrectAnswerDto
import com.nikol.lms.data.remote.model.question.QuestionItemEvaluationBlockDto
import com.nikol.lms.data.remote.model.question.QuestionItemOptionDto
import com.nikol.lms.domain.model.AudioMaterial
import com.nikol.lms.domain.model.CorrectAnswer
import com.nikol.lms.domain.model.EvaluationBlock
import com.nikol.lms.domain.model.ExerciseCodingMaterial
import com.nikol.lms.domain.model.ExerciseQuestionsMaterial
import com.nikol.lms.domain.model.FileMaterial
import com.nikol.lms.domain.model.FileMediaType
import com.nikol.lms.domain.model.ImageMaterial
import com.nikol.lms.domain.model.InputQuestion
import com.nikol.lms.domain.model.LongreadExerciseQuestionsMode
import com.nikol.lms.domain.model.LongreadMaterial
import com.nikol.lms.domain.model.LongreadMaterialMediaTypeUpper
import com.nikol.lms.domain.model.MarkdownMaterial
import com.nikol.lms.domain.model.MaterialActivity
import com.nikol.lms.domain.model.MaterialAttachment
import com.nikol.lms.domain.model.MaterialCoding
import com.nikol.lms.domain.model.MaterialContent
import com.nikol.lms.domain.model.MaterialEstimation
import com.nikol.lms.domain.model.MultipleChoiceBlock
import com.nikol.lms.domain.model.MultipleChoiceQuestion
import com.nikol.lms.domain.model.NumberCorrectAnswer
import com.nikol.lms.domain.model.PublicationState
import com.nikol.lms.domain.model.QuestionItem
import com.nikol.lms.domain.model.QuestionOption
import com.nikol.lms.domain.model.QuestionsSettings
import com.nikol.lms.domain.model.QuestionsSettingsEvalStrategy
import com.nikol.lms.domain.model.SingleChoiceBlock
import com.nikol.lms.domain.model.SingleChoiceQuestion
import com.nikol.lms.domain.model.StringCorrectAnswer
import com.nikol.lms.domain.model.VideoMaterial
import com.nikol.lms.domain.model.VideoPlatformMaterial
import com.nikol.lms.domain.model.VideoPlatformState


fun LongreadMaterialContentDto.toDomain() = MaterialContent(
    name = name,
    filename = filename,
    mediaType = runCatching { LongreadMaterialMediaTypeUpper.valueOf(mediaType.name) }
        .getOrDefault(LongreadMaterialMediaTypeUpper.FILE),
    version = version,
    length = length
)

fun LongreadMaterialAttachmentItemDto.toDomain() = MaterialAttachment(
    name = name,
    filename = filename,
    mediaType = runCatching { FileMediaType.valueOf(mediaType.name) }
        .getOrDefault(FileMediaType.FILE),
    length = length,
    version = version
)

fun LongreadMaterialActivityDto.toDomain() = MaterialActivity(
    id = id,
    name = name,
    weight = weight,
    maxExercisesCount = maxExercisesCount,
    averageScoreThreshold = averageScoreThreshold,
    isLateDaysEnabled = isLateDaysEnabled
)

fun LongreadMaterialEstimationDto.toDomain() = MaterialEstimation(
    startDate = startDate,
    timer = timer,
    maxScore = maxScore,
    deadline = deadline,
    activity = activity?.toDomain()
)

fun LongreadMaterialCodingDto.toDomain() = MaterialCoding(
    exerciseUrl = exerciseUrl
)

fun ExerciseQuestionsSettingsDto.toDomain() = QuestionsSettings(
    questionsPerAttempt = questionsPerAttempt,
    attemptsLimit = attemptsLimit,
    evaluationStrategy = runCatching { QuestionsSettingsEvalStrategy.valueOf(evaluationStrategy.name) }
        .getOrDefault(QuestionsSettingsEvalStrategy.LAST)
)


fun QuestionItemOptionDto.toDomain() = QuestionOption(
    id = id,
    value = value,
    order = order,
    isCorrect = isCorrect,
    recommendation = recommendation
)

fun QuestionItemEvaluationBlockDto.toDomain() = EvaluationBlock(
    correctAnswer = correctAnswer?.toDomain(),
    autoEvaluation = autoEvaluation,
    recommendation = recommendation
)

fun QuestionItemCorrectAnswerDto.toDomain(): CorrectAnswer {
    return when (this) {
        is QuestionItemCorrectAnswerInputStringCorrectAnswerDto -> StringCorrectAnswer(variants)
        is QuestionItemCorrectAnswerInputNumberCorrectAnswerDto -> NumberCorrectAnswer(
            variants = variants,
            showPrecisionHint = showPrecisionHint,
            autoEvaluationPrecision = autoEvaluationPrecision,
            precision = precision
        )
    }
}

fun LongreadExerciseQuestionItemDto.toDomain(): QuestionItem {
    return when (this) {
        is LongreadExerciseInputQuestionItemDto -> InputQuestion(
            id = id,
            order = order,
            content = content,
            score = score,
            attachments = attachments.map { it.toDomain() },
            correctAnswer = correctAnswer?.toDomain(),
            autoEvaluation = autoEvaluation,
            recommendation = recommendation,
            input = input.toDomain()
        )

        is LongreadExerciseSingleChoiceQuestionitemDto -> SingleChoiceQuestion(
            id = id,
            order = order,
            content = content,
            score = score,
            attachments = attachments.map { it.toDomain() },
            options = options.map { it.toDomain() },
            areOptionsShuffled = areOptionsShuffled,
            singleChoice = SingleChoiceBlock(singleChoice.options.map { it.toDomain() })
        )

        is LongreadExerciseMultipleChoiceQuestionItemDto -> MultipleChoiceQuestion(
            id = id,
            order = order,
            content = content,
            score = score,
            attachments = attachments.map { it.toDomain() },
            options = options.map { it.toDomain() },
            areOptionsShuffled = areOptionsShuffled,
            multipleChoice = MultipleChoiceBlock(multipleChoice.options.map { it.toDomain() })
        )
    }
}


fun LongreadMaterialItemDto.toDomain(): LongreadMaterial {
    val pubState = runCatching { PublicationState.valueOf(state.name) }
        .getOrDefault(PublicationState.DRAFT)

    return when (this) {
        is LongreadMarkdownMaterialItemDto -> MarkdownMaterial(
            id = id,
            order = order,
            state = pubState,
            viewContent = viewContent,
            publishDate = publishDate,
            publishedAt = publishedAt
        )

        is LongreadFileMaterialItemDto -> FileMaterial(
            id = id,
            order = order,
            state = pubState,
            mediaType = runCatching { FileMediaType.valueOf(mediaType.name) }.getOrDefault(
                FileMediaType.FILE
            ),
            filename = filename,
            version = version,
            length = length,
            publishDate = publishDate,
            publishedAt = publishedAt,
            content = content.toDomain()
        )

        is LongreadExerciseCodingMaterialItemDto -> ExerciseCodingMaterial(
            id = id,
            order = order,
            state = pubState,
            name = name,
            viewContent = viewContent,
            estimation = estimation.toDomain(),
            reviewers = reviewers,
            assignees = assignees,
            attachments = attachments.map { it.toDomain() },
            backloggedAt = backloggedAt,
            taskId = taskId,
            isTrackingStudents = isTrackingStudents,
            exerciseUrl = exerciseUrl,
            coding = coding.toDomain()
        )

        is LongreadImageMaterialItemDto -> ImageMaterial(
            id = id,
            order = order,
            state = pubState,
            imageScale = imageScale,
            mediaType = runCatching { FileMediaType.valueOf(mediaType.name) }.getOrDefault(
                FileMediaType.IMAGE
            ),
            filename = filename,
            version = version,
            length = length,
            publishDate = publishDate,
            publishedAt = publishedAt,
            content = content.toDomain()
        )

        is LongreadExerciseQuestionsMaterialItemDto -> ExerciseQuestionsMaterial(
            id = id,
            order = order,
            state = pubState,
            name = name,
            viewContent = viewContent,
            estimation = estimation.toDomain(),
            reviewers = reviewers,
            assignees = assignees,
            attachments = attachments.map { it.toDomain() },
            backloggedAt = backloggedAt,
            taskId = taskId,
            isTrackingStudents = isTrackingStudents,
            mode = runCatching { LongreadExerciseQuestionsMode.valueOf(mode.name) }
                .getOrDefault(LongreadExerciseQuestionsMode.AT_MOMENT),
            areQuestionsShuffled = areQuestionsShuffled,
            settings = settings.toDomain(),
            quizId = quizId,
            questions = questions.map { it.toDomain() } // Каскадный маппинг полиморфных вопросов
        )

        is LongreadVideoPlatformMaterialItemDto -> VideoPlatformMaterial(
            id = id,
            order = order,
            state = pubState,
            name = name,
            description = description,
            videoId = videoId,
            timecodes = timecodes,
            publishDate = publishDate,
            videoState = runCatching { VideoPlatformState.valueOf(videoState.name) }
                .getOrDefault(VideoPlatformState.READY),
            url = url
        )

        is LongreadVideoMaterialItemDto -> VideoMaterial(
            id = id,
            order = order,
            state = pubState,
            mediaType = runCatching { FileMediaType.valueOf(mediaType.name) }.getOrDefault(
                FileMediaType.VIDEO
            ),
            filename = filename,
            version = version,
            length = length,
            publishDate = publishDate,
            publishedAt = publishedAt,
            content = content.toDomain()
        )

        is LongreadAudioMaterialItemDto -> AudioMaterial(
            id = id,
            order = order,
            state = pubState,
            mediaType = runCatching { FileMediaType.valueOf(mediaType.name) }.getOrDefault(
                FileMediaType.AUDIO
            ),
            filename = filename,
            version = version,
            length = length,
            publishDate = publishDate,
            publishedAt = publishedAt,
            content = content.toDomain()
        )
    }
}