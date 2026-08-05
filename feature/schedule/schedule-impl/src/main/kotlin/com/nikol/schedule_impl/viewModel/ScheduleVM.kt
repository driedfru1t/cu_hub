package com.nikol.schedule_impl.viewModel

import android.util.Log
import androidx.lifecycle.viewModelScope
import arrow.optics.copy
import arrow.optics.optics
import com.nikol.calendar.domain.error.ScheduleError
import com.nikol.calendar.domain.model.ScheduleEvent
import com.nikol.calendar.domain.SummaryParser
import com.nikol.calendar.domain.useCase.GetEventsUseCase
import com.nikol.calendar.domain.useCase.RefreshUseCase
import com.nikol.calendar.domain.useCase.ScheduleParam
import com.nikol.domain.NoParam
import com.nikol.ui.state.Lce
import direct.direct_core.DirectEffect
import direct.direct_core.DirectIntent
import direct.direct_core.DirectState
import direct.direct_core.onLatest
import direct.direct_core.onSingle
import direct.direct_viewmodel.DirectViewModel
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject

@optics
data class ScheduleState(
    val schedule: Lce<ScheduleError, List<ScheduleEvent>> = Lce.Loading,
    val from: Instant,
    val to: Instant
) : DirectState {
    companion object
}

sealed interface ScheduleIntent : DirectIntent {
    data object Refresh : ScheduleIntent
    data class ChangeDateRange(val from: Instant, val to: Instant) : ScheduleIntent
}

class ScheduleVM @Inject constructor(
    private val getEventsUseCase: GetEventsUseCase,
    private val refreshUseCase: RefreshUseCase
) : DirectViewModel<ScheduleIntent, ScheduleState, DirectEffect>() {

    init {
        state
            .map { ScheduleParam(it.from, it.to) }
            .distinctUntilChanged()
            .flatMapLatest { param ->
                getEventsUseCase(param)
            }
            .onEach { result ->
                result.fold(
                    ifLeft = { error ->
                        setState {
                            ScheduleState.schedule.set(this, Lce.Failure(error))
                        }
                    },
                    ifRight = { events ->
                        setState {
                            ScheduleState.schedule.set(this, Lce.Content(events))
                        }
                    }
                )
            }
            .launchIn(viewModelScope)
    }

    override fun createInitialState(): ScheduleState {
        val zoneId = ZoneId.systemDefault()
        val testDate = LocalDate.now()

        val from = testDate.atStartOfDay(zoneId).toInstant()
        val to = testDate.plusDays(1).atStartOfDay(zoneId).toInstant()

        return ScheduleState(
            schedule = Lce.Loading,
            from = from,
            to = to
        )
    }

    override fun handleIntents() = intents {

        onLatest<ScheduleIntent.ChangeDateRange> { intent ->
            setState {
                copy {
                    ScheduleState.from set intent.from
                    ScheduleState.to set intent.to
                    ScheduleState.schedule set Lce.Loading
                }
            }
        }

        onSingle<ScheduleIntent.Refresh> {
            setState { ScheduleState.schedule.set(this, Lce.Loading) }
            refreshUseCase(NoParam)
        }
    }
}