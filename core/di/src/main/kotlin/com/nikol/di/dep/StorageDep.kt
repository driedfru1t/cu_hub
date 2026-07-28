package com.nikol.di.dep

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.nikol.lms.data.local.dao.CourseOverviewDao
import com.nikol.lms.data.local.dao.CoursesDao
import com.nikol.prefs.qualifers.TokenDataStore

interface StorageDep : DataStoreTokenDep, LocalLmsDep

interface DataStoreTokenDep {
    @TokenDataStore
    fun tokenDataStore(): DataStore<Preferences>
}

interface LocalLmsDep {
    fun courseDao(): CoursesDao

    fun courseOverviewDao(): CourseOverviewDao
}