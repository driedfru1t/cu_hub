package com.nikol.lms.data

import arrow.core.Either
import com.nikol.lms.domain.common.UnstableLmsApi
import com.nikol.lms.domain.error.ChatError
import com.nikol.lms.domain.model.Comment
import com.nikol.lms.domain.model.CommentAttachmentPayload
import com.nikol.lms.domain.model.TaskEvent
import com.nikol.lms.domain.repo.ChatRepository

class ChatRepositoryImpl : ChatRepository {
    /**
     * [Эндпоинт 14] История действий по задаче (логи изменений статусов).
     * GET /micro-lms/tasks/{taskId}/events
     */
    override suspend fun getTaskEvents(taskId: Int): Either<ChatError, List<TaskEvent>> {
        TODO("Not yet implemented")
    }

    /**
     * [Эндпоинт 15] Комментарии (чат с преподавателем/ассистентом по задаче).
     * GET /micro-lms/tasks/{taskId}/comments
     */
    override suspend fun getTaskComments(taskId: Int): Either<ChatError, List<Comment>> {
        TODO("Not yet implemented")
    }

    /**
     * [Эндпоинт 16] Отправить новый комментарий в чат по задаче.
     * POST /micro-lms/comments
     */
    override suspend fun sendComment(
        taskId: Int,
        content: String,
        attachments: List<CommentAttachmentPayload>
    ): Either<ChatError, Comment> {
        TODO("Not yet implemented")
    }
}