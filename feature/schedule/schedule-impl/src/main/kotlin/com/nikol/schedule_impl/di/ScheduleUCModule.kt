package com.nikol.schedule_impl.di

import com.nikol.calendar.domain.repo.ScheduleRepository
import com.nikol.calendar.domain.useCase.GetEventsUseCase
import com.nikol.calendar.domain.useCase.RefreshUseCase
import com.nikol.common.CuHubDispatcher
import com.nikol.common.Dispatcher
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.CoroutineDispatcher

@Module
class ScheduleUCModule {
    @Provides
    fun provideGetEventsUseCase(
        @Dispatcher(CuHubDispatcher.IO) coroutineDispatcher: CoroutineDispatcher,
        scheduleRepository: ScheduleRepository
    ): GetEventsUseCase {
        return GetEventsUseCase(scheduleRepository, coroutineDispatcher)
    }

    @Provides
    fun provideRefreshUseCase(
        @Dispatcher(CuHubDispatcher.IO) coroutineDispatcher: CoroutineDispatcher,
        scheduleRepository: ScheduleRepository
    ): RefreshUseCase {
        return RefreshUseCase(scheduleRepository, coroutineDispatcher)
    }
}