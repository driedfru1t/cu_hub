package com.nikol.lms.domain.error

sealed interface ProfileError {
    object Unauthorized : ProfileError
    data class NetworkError(val message: String?) : ProfileError
    object Unknown : ProfileError
}
