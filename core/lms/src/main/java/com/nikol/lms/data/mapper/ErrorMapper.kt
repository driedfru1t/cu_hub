package com.nikol.lms.data.mapper

import com.nikol.lms.domain.error.CourseError
import com.nikol.network.NetworkError

fun NetworkError.toCourseError(): CourseError {
    return when (this) {
        NetworkError.BadRequest -> CourseError.Unknown
        NetworkError.Unauthorized -> CourseError.Unauthorized
        NetworkError.Forbidden -> CourseError.AccessDenied
        NetworkError.NotFound -> CourseError.NotFound

        is NetworkError.NoConnection -> CourseError.NetworkError(message)

        is NetworkError.ServerFailure -> when (code) {
            in 500..599 -> CourseError.NetworkError("Server error: $code")
            else -> CourseError.Unknown
        }

        is NetworkError.UnknownException -> CourseError.NetworkError(message)
    }
}