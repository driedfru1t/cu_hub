package com.nikol.calendar.data.mapper

import com.nikol.calendar.data.remote.CalDavError
import com.nikol.calendar.domain.error.ScheduleError
import com.nikol.network.NetworkError

fun CalDavError.toScheduleError(): ScheduleError =
    when (this) {
        is CalDavError.Network -> if (this.error is NetworkError.Unauthorized) {
            ScheduleError.Unauthorized
        } else {
            ScheduleError.Network
        }

        CalDavError.XmlParsingFailed ->
            ScheduleError.Parsing
    }