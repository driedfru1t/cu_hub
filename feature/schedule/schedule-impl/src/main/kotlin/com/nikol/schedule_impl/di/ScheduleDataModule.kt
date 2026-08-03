package com.nikol.schedule_impl.di

import com.nikol.calendar.data.RecurrenceParser
import com.nikol.calendar.data.RecurrenceParserICal4J
import com.nikol.calendar.data.ScheduleRepositoryImpl
import com.nikol.calendar.domain.repo.ScheduleRepository
import dagger.Binds
import dagger.Module

@Module
interface ScheduleDataModule {
    @Binds
    @ScheduleScope
    fun bindsScheduleRepo(scheduleRepositoryImpl: ScheduleRepositoryImpl): ScheduleRepository

    @Binds
    fun bindsRecurrenceParser(recurrenceParserICal4J: RecurrenceParserICal4J): RecurrenceParser
}
