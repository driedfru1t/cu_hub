package com.nikol.domain

import arrow.core.Either
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

abstract class UseCase<in P, out R, out F>(
    private val dispatcher: CoroutineDispatcher
) {
    suspend operator fun invoke(params: P): Either<F, R> = withContext(dispatcher) { run(params) }

    protected abstract suspend fun run(params: P): Either<F, R>
}

object NoParam