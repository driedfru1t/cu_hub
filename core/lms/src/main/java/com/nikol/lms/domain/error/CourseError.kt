package com.nikol.lms.domain.error

sealed interface CourseError {
    object NotFound : CourseError
    object AccessDenied : CourseError
    object Unauthorized : CourseError
    data class NetworkError(val message: String?) : CourseError
    object Unknown : CourseError
}
