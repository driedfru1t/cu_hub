package com.nikol.di.dep

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.nikol.calendar.data.local.CalendarDao
import com.nikol.calendar.data.local.CalendarEventDao
import com.nikol.lms.backround.FileDAO
import com.nikol.lms.data.local.dao.CourseOverviewDao
import com.nikol.lms.data.local.dao.CoursesDao
import com.nikol.lms.data.local.dao.LongreadDao
import com.nikol.lms.data.local.dao.TaskSummaryDao
import com.nikol.prefs.qualifers.LmsDataStore
import com.nikol.prefs.qualifers.TokenDataStore
import com.nikol.storage.db.AppDatabase

interface StorageDep : DataStoreTokenDep, LocalLmsDep, LocalScheduleDep

interface DataStoreTokenDep {
    @TokenDataStore
    fun tokenDataStore(): DataStore<Preferences>
}

interface DbDep {
    fun appDb(): AppDatabase
}

interface LocalLmsDep : DbDep {
    fun courseDao(): CoursesDao
    fun courseOverviewDao(): CourseOverviewDao
    fun fileDao(): FileDAO
    fun taskSummaryDao(): TaskSummaryDao

    @LmsDataStore
    fun lmsDataStore(): DataStore<Preferences>
    fun longreadDao(): LongreadDao
}

interface LocalScheduleDep : DbDep {
    fun calendarDao(): CalendarDao
    fun calendarEventDao(): CalendarEventDao
}