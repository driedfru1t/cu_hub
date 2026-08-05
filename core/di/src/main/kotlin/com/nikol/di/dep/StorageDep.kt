package com.nikol.di.dep

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.nikol.calendar.data.local.CalendarDao
import com.nikol.calendar.data.local.CalendarEventDao
import com.nikol.lms.data.local.dao.CourseOverviewDao
import com.nikol.lms.data.local.dao.CoursesDao
import com.nikol.prefs.qualifers.TokenDataStore

interface StorageDep : DataStoreTokenDep, LocalLmsDep, LocalScheduleDep

interface DataStoreTokenDep {
    @TokenDataStore
    fun tokenDataStore(): DataStore<Preferences>
}

interface LocalLmsDep {
    fun courseDao(): CoursesDao

    fun courseOverviewDao(): CourseOverviewDao
}

interface LocalScheduleDep {
    fun calendarDao(): CalendarDao
    fun calendarEventDao(): CalendarEventDao
}