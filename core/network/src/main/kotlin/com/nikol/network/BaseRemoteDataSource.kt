package com.nikol.network

import arrow.core.Either
import io.ktor.client.statement.HttpResponse
import kotlinx.serialization.json.Json
import java.io.IOException

abstract class BaseRemoteDataSource(protected val json: Json) {
    protected suspend inline fun <reified T> safeApiCall(
        crossinline apiCall: suspend () -> HttpResponse
    ): Either<NetworkError, T> {
        return try {
            val response = apiCall()
            when (val apiResponse = response.toApiResponse<T>()) {
                is ApiResponse.Success -> Either.Right(apiResponse.body)
                is ApiResponse.Error -> Either.Left(
                    parseNetworkError(
                        apiResponse.code,
                        apiResponse.errorBody
                    )
                )
            }
        } catch (e: IOException) {
            Either.Left(NetworkError.NoConnection(e.message))
        } catch (e: Exception) {
            Either.Left(NetworkError.UnknownException(e.message))
        }
    }


    protected fun parseNetworkError(code: Int, errorBody: String): NetworkError {
        return when (code) {
            400 -> NetworkError.BadRequest
            401 -> NetworkError.Unauthorized
            403 -> NetworkError.Forbidden
            404 -> NetworkError.NotFound
            else -> NetworkError.ServerFailure(code)
        }
    }
}