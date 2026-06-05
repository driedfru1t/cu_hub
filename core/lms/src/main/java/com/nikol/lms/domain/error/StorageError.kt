package com.nikol.lms.domain.error

sealed interface StorageError {
    object AccessDenied : StorageError
    object Unauthorized : StorageError
    data class NetworkError(val message: String?) : StorageError
    object Unknown : StorageError
}
