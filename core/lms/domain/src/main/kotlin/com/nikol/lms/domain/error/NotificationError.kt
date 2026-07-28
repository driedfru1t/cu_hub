package com.nikol.lms.domain.error

sealed interface NotificationError {
    object Unauthorized : NotificationError
    data class NetworkError(val message: String?) : NotificationError
    object Unknown : NotificationError
}