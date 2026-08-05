package com.nikol.calendar.domain.useCase

import arrow.core.Either
import arrow.core.raise.either
import com.nikol.calendar.domain.error.ScheduleError
import com.nikol.calendar.domain.repo.ScheduleRepository
import com.nikol.domain.NoParam
import com.nikol.domain.UseCase
import kotlinx.coroutines.CoroutineDispatcher

class RefreshUseCase(
    private val scheduleRepository: ScheduleRepository,
    coroutineDispatcher: CoroutineDispatcher
) : UseCase<NoParam, Unit, ScheduleError>(coroutineDispatcher) {
    override suspend fun run(params: NoParam): Either<ScheduleError, Unit> {
        return either {
            scheduleRepository.refresh()
        }
    }
}