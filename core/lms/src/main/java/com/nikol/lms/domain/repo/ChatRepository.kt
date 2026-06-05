package com.nikol.lms.domain.repo

import arrow.core.Either
import com.nikol.lms.domain.error.ChatError
import com.nikol.lms.domain.model.Comment
import com.nikol.lms.domain.model.CommentAttachmentPayload
import com.nikol.lms.domain.model.TaskEvent

interface ChatRepository {

    /**
     * [Эндпоинт 14] История действий по задаче (логи изменений статусов).
     * GET /micro-lms/tasks/{taskId}/events
     */
    suspend fun getTaskEvents(taskId: Int): Either<ChatError, List<TaskEvent>>

    /**
     * [Эндпоинт 15] Комментарии (чат с преподавателем/ассистентом по задаче).
     * GET /micro-lms/tasks/{taskId}/comments
     */
    suspend fun getTaskComments(taskId: Int): Either<ChatError, List<Comment>>

    /**
     * [Эндпоинт 16] Отправить новый комментарий в чат по задаче.
     * POST /micro-lms/comments
     */
    suspend fun sendComment(
        taskId: Int,
        content: String,
        attachments: List<CommentAttachmentPayload>
    ): Either<ChatError, Comment>
}