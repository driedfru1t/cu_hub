package com.nikol.cache

import arrow.core.Either
import com.nikol.network.NetworkError

suspend inline fun <ResultType, RequestType> cacheFirstWithTimeout(
    cacheKey: String,
    timeoutMillis: Long,
    crossinline getLastUpdated: suspend (String) -> Long,
    crossinline updateMetadata: suspend (String, Long) -> Unit,
    crossinline queryCache: suspend () -> ResultType?,
    crossinline fetch: suspend () -> Either<NetworkError, RequestType>,
    crossinline saveFetchResult: suspend (RequestType) -> Unit,
    crossinline mapDtoToDomain: (RequestType) -> ResultType
): Either<NetworkError, ResultType> {

    val lastUpdated = getLastUpdated(cacheKey)
    val isCacheValid = (System.currentTimeMillis() - lastUpdated) < timeoutMillis

    if (isCacheValid) {
        val cached = queryCache()
        if (cached != null) {
            return Either.Right(cached)
        }
    }

    return when (val response = fetch()) {
        is Either.Right -> {
            saveFetchResult(response.value)
            updateMetadata(cacheKey, System.currentTimeMillis())
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