package com.nikol.lms.domain.error

sealed interface ChatError {
    object NotFound : ChatError
    object Unauthorized : ChatError
    data class NetworkError(val message: String?) : ChatError
    object Unknown : ChatError
}
