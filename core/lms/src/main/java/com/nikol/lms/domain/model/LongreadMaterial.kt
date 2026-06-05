package com.nikol.lms.domain.model

sealed interface LongreadMaterial {
    val id: Int
    val order: Int
    val state: PublicationState
}

data class MarkdownMaterial(
    override val id: Int,
    override val order: Int,
    override val state: PublicationState,
    val viewContent: String,
    val publishDate: String?,
    val publishedAt: String?
) : LongreadMaterial

data class FileMaterial(
    override val id: Int,
    override val order: Int,
    override val state: PublicationState,
    val mediaType: FileMediaType,
    val filename: String,
    val version: String?,
    val length: Long,
    val publishDate: String?,
    val publishedAt: String?,
    val content: MaterialContent
) : LongreadMaterial

data class ExerciseCodingMaterial(
    override val id: Int,
    override val order: Int,
    override val state: PublicationState,
    val name: String,
    val viewContent: String?,
    val estimation: MaterialEstimation,
    val reviewers: List<String>,
    val assignees: List<String>,
    val attachments: List<MaterialAttachment>,
    val backloggedAt: String?,
    val taskId: Int,
    val isTrackingStudents: Boolean,
    val exerciseUrl: String?,
    val coding: MaterialCoding
) : LongreadMaterial

data class ImageMaterial(
    override val id: Int,
    override val order: Int,
    override val state: PublicationState,
    val imageScale: Int?,
    val mediaType: FileMediaType,
    val filename: String,
    val version: String?,
    val length: Long,
    val publishDate: String?,
    val publishedAt: String?,
    val content: MaterialContent
) : LongreadMaterial

data class ExerciseQuestionsMaterial(
    override val id: Int,
    override val order: Int,
    override val state: PublicationState,
    val name: String,
    val viewContent: String?,
    val estimation: MaterialEstimation,
    val reviewers: List<String>,
    val assignees: List<String>,
    val attachments: List<MaterialAttachment>,
    val backloggedAt: String?,
    val taskId: Int,
    val isTrackingStudents: Boolean,
    val mode: LongreadExerciseQuestionsMode,
    val areQuestionsShuffled: Boolean,
    val settings: QuestionsSettings,
    val quizId: Int,
    val questions: List<QuestionItem>
) : LongreadMaterial

data class VideoPlatformMaterial(
    override val id: Int,
    override val order: Int,
    override val state: PublicationState,
    val name: String,
    val description: String,
    val videoId: String,
    val timecodes: List<String>,
    val publishDate: String?,
    val videoState: VideoPlatformState,
    val url: String
) : LongreadMaterial

data class VideoMaterial(
    override val id: Int,
    override val order: Int,
    override val state: PublicationState,
    val mediaType: FileMediaType,
    val filename: String,
    val version: String?,
    val length: Long,
    val publishDate: String?,
    val publishedAt: String?,
    val content: MaterialContent
) : LongreadMaterial

data class AudioMaterial(
    override val id: Int,
    override val order: Int,
    override val state: PublicationState,
    val mediaType: FileMediaType,
    val filename: String,
    val version: String?,
    val length: Long,
    val publishDate: String?,
    val publishedAt: String?,
    val content: MaterialContent
) : LongreadMaterial
