package com.nikol.lms.data.remote.model.material

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
enum class LongreadMaterialTypeDto {
    @SerialName("markdown")
    MARKDOWN,

    @SerialName("content")
    CONTENT,

    @SerialName("coding")
    CODING,

    @SerialName("image")
    IMAGE,

    @SerialName("questions")
    QUESTIONS,

    @SerialName("videoPlatform")
    VIDEO_PLATFORM
}

@Serializable
enum class LongreadMaterialViewTypeDto {
    @SerialName("ngxMarkdown")
    NGX_MARKDOWN,

    @SerialName("file")
    FILE,

    @SerialName("exerciseCoding")
    EXERCISE_CODING,

    @SerialName("image")
    IMAGE,

    @SerialName("exerciseQuestions")
    EXERCISE_QUESTIONS,

    @SerialName("videoPlatform")
    VIDEO_PLATFORM,

    @SerialName("video")
    VIDEO,

    @SerialName("audio")
    AUDIO
}

@Serializable
enum class FileMediaTypeDto {
    @SerialName("file")
    FILE,

    @SerialName("image")
    IMAGE,

    @SerialName("video")
    VIDEO,

    @SerialName("audio")
    AUDIO
}

@Serializable
enum class LongreadMaterialMediaTypeUpperDto {
    @SerialName("File")
    FILE,

    @SerialName("Image")
    IMAGE,

    @SerialName("Video")
    VIDEO,

    @SerialName("Audio")
    AUDIO
}

@Serializable
enum class LongreadExerciseQuestionsModeDto {
    @SerialName("atMoment")
    AT_MOMENT,

    @SerialName("byDeadline")
    BY_DEADLINE
}

@Serializable
enum class QuestionsSettingsEvalStrategyDto {
    @SerialName("last")
    LAST,

    @SerialName("best")
    BEST
}

@Serializable
enum class VideoPlatformStateDto {
    @SerialName("unspecified")
    UNSPECIFIED,

    @SerialName("empty")
    EMPTY,

    @SerialName("uploaded")
    UPLOADED,

    @SerialName("transcoding")
    TRANSCODING,

    @SerialName("viewable")
    VIEWABLE,

    @SerialName("ready")
    READY,

    @SerialName("partiallyReady")
    PARTIALLY_READY,

    @SerialName("error")
    ERROR
}
