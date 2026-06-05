package com.nikol.cache

import arrow.core.Either
import com.nikol.network.NetworkError

suspend inline fun <ResultType, RequestType> networkFirstWithFallback(
    crossinline fetch: suspend () -> Either<NetworkError, RequestType>,
    crossinline saveFetchResult: suspend (RequestType) -> Unit,
    crossinline queryCache: suspend () -> ResultType?,
    crossinline mapDtoToDomain: (RequestType) -> ResultType
): Either<NetworkError, ResultType> {
    return when (val response = fetch()) {
        is Either.Right -> {
            saveFetchResult(response.value)
            Either.Right(mapDtoToDomain(response.value))
        }

        is Either.Left -> {
            val cached = queryCache()
            if (cached != null) {
                Either.Right(cached)
            } else {
                Either.Left(response.value)
            }
        }
    }
}
