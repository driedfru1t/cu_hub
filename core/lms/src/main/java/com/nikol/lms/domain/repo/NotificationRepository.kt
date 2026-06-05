package com.nikol.lms.domain.repo

import arrow.core.Either
import com.nikol.lms.domain.error.NotificationError
import com.nikol.lms.domain.model.Notification


interface NotificationRepository {

    /**
     * [Эндпоинт 20] Получение списка внутриплатформенных уведомлений.
     * POST /notification-hub/notifications/in-app
     */
    suspend fun getInAppNotifications(
        limit: Int,
        offset: Int,
        category: Int
    ): Either<NotificationError, List<Notification>>
}