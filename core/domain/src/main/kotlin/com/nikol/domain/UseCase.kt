package com.nikol.domain

import arrow.core.Either
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

abstract class UseCase<in P, out R, out E>(
    private val dispatcher: CoroutineDispatcher
) {
    suspend operator fun invoke(params: P): Either<E, R> =
        withContext(dispatcher) { run(params) }

    protected abstract suspend fun run(params: P): Either<E, R>
}

abstract class FlowUseCase<in P, out R, out E> {
    operator fun invoke(params: P): Flow<Either<E, R>> = run(params)

    protected abstract fun run(params: P): Flow<Either<E, R>>
}

object NoParam