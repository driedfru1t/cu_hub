package com.nikol.calendar.domain.useCase

import arrow.core.Either
import arrow.core.raise.either
import com.nikol.calendar.domain.SummaryParser
import com.nikol.calendar.domain.error.ScheduleError
import com.nikol.calendar.domain.model.ScheduleEvent
import com.nikol.calendar.domain.repo.ScheduleRepository
import com.nikol.domain.UseCase
import kotlinx.coroutines.CoroutineDispatcher
import java.time.Instant

data class CalendarParam(
    val href: String,
    val start: Instant
)

class GetSingleEvent(
    private val scheduleRepository: ScheduleRepository,
    coroutineDispatcher: CoroutineDispatcher
) : UseCase<CalendarParam, ScheduleEvent, ScheduleError>(coroutineDispatcher) {
    override suspend fun run(params: CalendarParam): Either<ScheduleError, ScheduleEvent> {
        return either {
            val event = scheduleRepository.getEvent(params.href, params.start)
            SummaryParser.parse(event)
        }
    }
}