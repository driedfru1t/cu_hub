package com.nikol.calendar.domain.useCase

import arrow.core.Either
import com.nikol.calendar.domain.SummaryParser
import com.nikol.calendar.domain.error.ScheduleError
import com.nikol.calendar.domain.model.CalendarEvent
import com.nikol.calendar.domain.model.ScheduleEvent
import com.nikol.calendar.domain.repo.ScheduleRepository
import com.nikol.domain.FlowUseCase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import java.time.Instant

data class ScheduleParam(
    val from: Instant,
    val to: Instant
)

class GetEventsUseCase(
    private val scheduleRepository: ScheduleRepository,
    private val coroutineDispatcher: CoroutineDispatcher
) : FlowUseCase<ScheduleParam, List<ScheduleEvent>, ScheduleError>() {
    override fun run(params: ScheduleParam): Flow<Either<ScheduleError, List<ScheduleEvent>>> {
        return scheduleRepository
            .observeEvents(start = params.from, end = params.to)
            .map {
                val list = it.map { event -> SummaryParser.parse(event) }
                Either.Right(list)
            }
            .flowOn(coroutineDispatcher)
    }
}
