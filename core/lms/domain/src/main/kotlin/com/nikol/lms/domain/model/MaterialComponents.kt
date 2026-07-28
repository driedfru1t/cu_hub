package com.nikol.lms.domain.model

import java.time.Duration
import java.time.Instant

enum class FileMediaType {
    FILE, IMAGE, VIDEO, AUDIO
}

enum class LongreadMaterialMediaTypeUpper {
    FILE, IMAGE, VIDEO, AUDIO
}

enum class LongreadExerciseQuestionsMode {
    AT_MOMENT, BY_DEADLINE
}

enum class QuestionsSettingsEvalStrategy {
    LAST, BEST
}

enum class VideoPlatformState {
    UNSPECIFIED, EMPTY, UPLOADED, TRANSCODING, VIEWABLE, READY, PARTIALLY_READY, ERROR
}

data class MaterialContent(
    val name: String,
    val filename: String,
    val mediaType: LongreadMaterialMediaTypeUpper,
    val version: String?,
    val length: Long
)

data class MaterialAttachment(
    val name: String,
    val filename: String,
    val mediaType: FileMediaType,
    val length: Long,
    val version: String?
)

data class MaterialActivity(
    val id: Int,
    val name: String,
    val weight: Double,
    val maxExercisesCount: Double,
    val averageScoreThreshold: Double?,
    val isLateDaysEnabled: Boolean
)

data class MaterialEstimation(
    val startDate: Instant,
    val timer: Duration?,
    val maxScore: Double?,
    val deadline: Instant?,
    val activity: MaterialActivity?
)

data class MaterialCoding(
    val exerciseUrl: String?
)

data class QuestionsSettings(
    val questionsPerAttempt: String?,
    val attemptsLimit: Int,
    val evaluationStrategy: QuestionsSettingsEvalStrategy
)
