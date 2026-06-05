package com.nikol.network

sealed interface NetworkError {
    data object BadRequest : NetworkError
    data object Unauthorized : NetworkError
    data object Forbidden : NetworkError
    data object NotFound : NetworkError

    data class NoConnection(val message: String?) : NetworkError
    data class ServerFailure(val code: Int) : NetworkError
    data class UnknownException(val message: String?) : NetworkError
}