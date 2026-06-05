package com.nikol.lms.domain.model

import com.nikol.lms.domain.common.UnstableLmsApi

// [!] Спецификация отсутствует в OpenAPI. POST /notification-hub/notifications/in-app.
// Составлено гипотетически на основе стандартных пушей/уведомлений.
@UnstableLmsApi
data class Notification(
    val id: String,
    val title: String,
    val message: String,
    val category: Int,
    val isRead: Boolean,
    val createdAt: String
)
