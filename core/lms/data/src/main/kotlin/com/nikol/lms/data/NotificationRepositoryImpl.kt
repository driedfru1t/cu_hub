package com.nikol.lms.data

import arrow.core.Either
import com.nikol.lms.domain.common.UnstableLmsApi
import com.nikol.lms.domain.error.NotificationError
import com.nikol.lms.domain.model.Notification
import com.nikol.lms.domain.repo.NotificationRepository

class NotificationRepositoryImpl : NotificationRepository {
    /**
     * [Эндпоинт 20] Получение списка внутриплатформенных уведомлений.
     * POST /notification-hub/notifications/in-app
     */
    override suspend fun getInAppNotifications(
        limit: Int,
        offset: Int,
        category: Int
    ): Either<NotificationError, List<Notification>> {
        TODO("Not yet implemented")
    }
}