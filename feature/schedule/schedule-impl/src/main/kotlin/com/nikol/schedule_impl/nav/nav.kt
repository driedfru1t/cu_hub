package com.nikol.schedule_impl.nav

import androidx.compose.runtime.CompositionLocalProvider
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.nikol.di.ext.LocalAppDep
import com.nikol.di.ext.rememberComponent
import com.nikol.navigation.BottomSheetSceneStrategy
import com.nikol.schedule_api.EventDetail
import com.nikol.schedule_api.Schedule
import com.nikol.schedule_impl.screen.EventDetailScreen
import com.nikol.schedule_impl.screen.ScheduleScreen
import com.nikol.schedule_impl.viewModel.component.DetailEventComponentVM
import com.nikol.viewmodel.LocalViewModelFactory

fun EntryProviderScope<NavKey>.schedule(
    onBack: () -> Unit,
    onNavigate: (NavKey) -> Unit
) {
    entry<Schedule> {
        ScheduleScreen(onNavigate)
    }

    entry<EventDetail>(
        metadata = BottomSheetSceneStrategy.bottomSheet()
    ) { event ->
        val component = rememberComponent { DetailEventComponentVM(it, event.start, event.href) }
        CompositionLocalProvider(
            LocalViewModelFactory provides component.viewModelFactory()
        ) {
            EventDetailScreen(onBack)
        }
    }
}