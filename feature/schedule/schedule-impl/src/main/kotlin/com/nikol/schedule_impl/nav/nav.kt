package com.nikol.schedule_impl.nav

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.nikol.schedule_api.Schedule
import com.nikol.schedule_impl.screen.ScheduleScreen

fun EntryProviderScope<NavKey>.schedule() {
    entry<Schedule> {
        ScheduleScreen()
    }
}