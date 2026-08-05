package com.nikol.lms.domain.error

interface GradeError {
    object NotFound : GradeError
    object AccessDenied : GradeError
    object Unauthorized : GradeError
    data class NetworkError(val message: String?) : GradeError
    object Unknown : GradeError

}