package com.nikol.lms.domain.error

sealed interface TaskError {
    object NotFound : TaskError
    object AccessDenied : TaskError
    object Unauthorized : TaskError
    data class ValidationError(val fields: Map<String, List<String>>) : TaskError
    data class NetworkError(val message: String?) : TaskError
    object Unknown : TaskError
}