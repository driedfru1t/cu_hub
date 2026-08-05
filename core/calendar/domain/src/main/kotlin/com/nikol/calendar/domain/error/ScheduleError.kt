package com.nikol.calendar.domain.error

sealed interface ScheduleError {

    data object Network : ScheduleError

    data object Parsing : ScheduleError

    data object Storage : ScheduleError

    data object Unauthorized : ScheduleError
}
