package com.nikol.schedule_impl.viewModel

import com.nikol.calendar.domain.error.ScheduleError
import com.nikol.calendar.domain.model.ScheduleEvent
import com.nikol.calendar.domain.useCase.CalendarParam
import com.nikol.calendar.domain.useCase.GetSingleEvent
import com.nikol.ui.state.Lce
import com.nikol.viewmodel.Router
import com.nikol.viewmodel.RouterViewModel
import direct.direct_core.DirectEffect
import direct.direct_core.DirectIntent
import direct.direct_core.onSingle
import java.time.Instant
import javax.inject.Inject

typealias EventDetailState = Lce<ScheduleError, ScheduleEvent>

sealed interface EventDetailIntent : DirectIntent {
    data object Init : EventDetailIntent
    data object Back : EventDetailIntent
}

fun interface EventDetailRouter : Router {
    fun onBack()
}

typealias EventDetailStore = RouterViewModel<EventDetailIntent, EventDetailState, DirectEffect, EventDetailRouter>

class EventDetailVM @Inject constructor(
    private val href: String, private val start: Long, private val getSingleEvent: GetSingleEvent
) : EventDetailStore() {
    override fun createInitialState(): EventDetailState = Lce.Loading

    override fun handleIntents() = intents {
        onSingle<EventDetailIntent.Init> {
            val newState = getSingleEvent(
                CalendarParam(href, Instant.ofEpochMilli(start))
            ).fold(
                ifLeft = { Lce.Failure(it) },
                ifRight = { Lce.Content(it) }
            )

            setState { newState }
        }
        onNavigate<EventDetailIntent.Back>(true) { onBack() }
    }

    init {
        setIntent(EventDetailIntent.Init)
    }
}