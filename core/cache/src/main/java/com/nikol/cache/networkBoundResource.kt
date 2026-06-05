package com.nikol.cache

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.nikol.network.NetworkError
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

//         entity       dto
inline fun <ResultType, RequestType> networkBoundResource(
    crossinline query: suspend () -> Flow<ResultType>,
    crossinline fetch: suspend () -> Either<NetworkError, RequestType>,
    crossinline saveFetchRequest: suspend (RequestType) -> Unit,
    crossinline shouldFetch: (ResultType) -> Boolean = { true }
): Flow<Either<NetworkError, ResultType>> = flow {
    val cachedData = query().first()
    if (shouldFetch(cachedData)) {
        emit(Either.Right(cachedData))

        when (val response = fetch()) {
            is Either.Right -> {
                saveFetchRequest(response.value)
                emitAll(query().map { Either.Right(it) })
            }

            is Either.Left -> emit(Either.Left(response.value))
        }
    } else {
        emitAll(query().map { Either.Right(it) })
    }
}