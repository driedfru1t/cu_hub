package com.nikol.lms.data.mapper

import com.nikol.lms.domain.error.ChatError
import com.nikol.lms.domain.error.CourseError
import com.nikol.lms.domain.error.GradeError
import com.nikol.lms.domain.error.NotificationError
import com.nikol.lms.domain.error.ProfileError
import com.nikol.lms.domain.error.StorageError
import com.nikol.lms.domain.error.TaskError
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

fun NetworkError.toGradeError(): GradeError {
    return when (this) {
        NetworkError.BadRequest -> GradeError.Unknown
        NetworkError.Unauthorized -> GradeError.Unauthorized
        NetworkError.Forbidden -> GradeError.AccessDenied
        NetworkError.NotFound -> GradeError.NotFound
        is NetworkError.NoConnection -> GradeError.NetworkError(message)
        is NetworkError.ServerFailure -> when (code) {
            in 500..599 -> GradeError.NetworkError("Server error: $code")
            else -> GradeError.Unknown
        }

        is NetworkError.UnknownException -> GradeError.NetworkError(message)
    }
}

fun NetworkError.toTaskError(): TaskError {
    return when (this) {
        NetworkError.BadRequest -> TaskError.Unknown
        NetworkError.Unauthorized -> TaskError.Unauthorized
        NetworkError.Forbidden -> TaskError.AccessDenied
        NetworkError.NotFound -> TaskError.NotFound
        is NetworkError.NoConnection -> TaskError.NetworkError(message)
        is NetworkError.ServerFailure -> when (code) {
            in 500..599 -> TaskError.NetworkError("Server error: $code")
            else -> TaskError.Unknown
        }

        is NetworkError.UnknownException -> TaskError.NetworkError(message)
    }
}

fun NetworkError.toProfileError(): ProfileError {
    return when (this) {
        NetworkError.Unauthorized -> ProfileError.Unauthorized
        is NetworkError.NoConnection -> ProfileError.NetworkError(message)
        is NetworkError.ServerFailure -> when (code) {
            in 500..599 -> ProfileError.NetworkError("Server error: $code")
            else -> ProfileError.Unknown
        }

        is NetworkError.UnknownException -> ProfileError.NetworkError(message)
        else -> ProfileError.Unknown
    }
}

fun NetworkError.toChatError(): ChatError {
    return when (this) {
        NetworkError.NotFound -> ChatError.NotFound
        NetworkError.Unauthorized -> ChatError.Unauthorized
        is NetworkError.NoConnection -> ChatError.NetworkError(message)
        is NetworkError.ServerFailure -> when (code) {
            in 500..599 -> ChatError.NetworkError("Server error: $code")
            else -> ChatError.Unknown
        }

        is NetworkError.UnknownException -> ChatError.NetworkError(message)
        else -> ChatError.Unknown
    }
}

fun NetworkError.toStorageError(): StorageError {
    return when (this) {
        NetworkError.Forbidden -> StorageError.AccessDenied
        NetworkError.Unauthorized -> StorageError.Unauthorized
        is NetworkError.NoConnection -> StorageError.NetworkError(message)
        is NetworkError.ServerFailure -> when (code) {
            in 500..599 -> StorageError.NetworkError("Server error: $code")
            else -> StorageError.Unknown
        }

        is NetworkError.UnknownException -> StorageError.NetworkError(message)
        else -> StorageError.Unknown
    }
}

fun NetworkError.toNotificationError(): NotificationError {
    return when (this) {
        NetworkError.Unauthorized -> NotificationError.Unauthorized
        is NetworkError.NoConnection -> NotificationError.NetworkError(message)
        is NetworkError.ServerFailure -> when (code) {
            in 500..599 -> NotificationError.NetworkError("Server error: $code")
            else -> NotificationError.Unknown
        }

        is NetworkError.UnknownException -> NotificationError.NetworkError(message)
        else -> NotificationError.Unknown
    }
}