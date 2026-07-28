package com.nikol.lms.domain.model

import com.nikol.lms.domain.common.UnstableLmsApi
import java.time.Instant


// [!] Спецификация отсутствует в OpenAPI. GET /micro-lms/tasks/{taskId}/events.
// Модель логов действий по задаче составлена гипотетически на основе стандартных событий тасок.
@UnstableLmsApi
data class TaskEvent(
    val id: String,
    val taskId: Int,
    val actorName: String, // Кто совершил (студент или преподаватель)
    val type: String, // e.g. "STARTED", "SUBMITTED", "EVALUATED"
    val timestamp: Instant,
    val comment: String? // Опциональное примечание
)

// [!] Спецификация отсутствует в OpenAPI. GET /micro-lms/tasks/{taskId}/comments.
// Модель комментария в чате составлена на основе JSON тела POST-запроса /micro-lms/comments.
@UnstableLmsApi
data class Comment(
    val id: Int,
    val entityId: Int, // taskId
    val authorName: String,
    val authorRole: String, // e.g. "STUDENT", "TEACHER"
    val content: String,
    val createdAt: Instant,
    val attachments: List<CommentAttachment>
)

data class CommentAttachment(
    val name: String,
    val filename: String,
    val mediaType: FileMediaType,
    val length: Long,
    val version: String?
)

// Вспомогательный класс-нагрузка для отправки вложений с комментарием
data class CommentAttachmentPayload(
    val name: String,
    val filename: String,
    val mediaType: FileMediaType,
    val length: Long,
    val version: String?
)
