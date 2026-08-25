package com.nikol.network

import arrow.core.Either
import arrow.core.raise.Raise
import io.ktor.client.call.body
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.isSuccess
import kotlinx.coroutines.CancellationException
import kotlinx.serialization.json.Json
import java.io.IOException

abstract class BaseRemoteDataSource(protected val json: Json) {

    // эта функйия вернет CancellationException
    protected suspend inline fun <reified T> Raise<NetworkError>.safeApiCall(
        crossinline apiCall: suspend () -> HttpResponse
    ): T {
        return try {
            val response = apiCall()
            when (val apiResponse = response.toApiResponse<T>()) {
                is ApiResponse.Success -> apiResponse.body
                is ApiResponse.Error -> raise(
                    parseNetworkError(
                        apiResponse.code,
                        apiResponse.errorBody
                    )
                )
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: IOException) {
            raise(NetworkError.NoConnection(e.message))
        } catch (e: Exception) {
            raise(NetworkError.UnknownException(e.message))
        }
    }



    // эта функйия вернет CancellationException
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
        } catch (e: CancellationException) {
            throw e
        } catch (e: IOException) {
            Either.Left(NetworkError.NoConnection(e.message))
        } catch (e: Exception) {
            Either.Left(NetworkError.UnknownException(e.message))
        }
    }

    protected suspend inline fun <reified T, reified E> Raise<E>.safeApiCall(
        crossinline apiCall: suspend () -> HttpResponse,
        crossinline mapError: (NetworkError) -> E,
        crossinline transform: Raise<E>.(String) -> T
    ): T {
        val responseString = try {
            val response = apiCall()
            val statusCode = response.status.value

            if (response.status.isSuccess() || statusCode == 207) {
                response.bodyAsText()
            } else {
                raise(mapError(parseNetworkError(statusCode, response.bodyAsText())))
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: IOException) {
            raise(mapError(NetworkError.NoConnection(e.message)))
        } catch (e: Exception) {
            raise(mapError(NetworkError.UnknownException(e.message)))
        }

        return transform(responseString)
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