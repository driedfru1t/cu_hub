package com.nikol.storage.di.module

import android.content.Context
import androidx.room.Room
import com.nikol.calendar.data.local.CalendarDao
import com.nikol.calendar.data.local.CalendarEventDao
import com.nikol.lms.backround.FileDAO
import com.nikol.lms.data.local.dao.CourseOverviewDao
import com.nikol.lms.data.local.dao.CoursesDao
import com.nikol.lms.data.local.dao.LongreadDao
import com.nikol.lms.data.local.dao.TaskSummaryDao
import com.nikol.storage.db.AppDatabase
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
internal class DBModule {
    @Singleton
    @Provides
    fun provideDB(context: Context): AppDatabase {
        return Room.databaseBuilder(
            context = context,
            klass = AppDatabase::class.java,
            name = "cu_hub_database"
        ).build()
    }

    @Singleton
    @Provides
    fun provideCourseDao(db: AppDatabase): CoursesDao {
        return db.courseDao()
    }

    @Singleton
    @Provides
    fun provideCourseOverviewDao(db: AppDatabase): CourseOverviewDao {
        return db.courseOverviewDao()
    }

    @Singleton
    @Provides
    fun provideCalendarEventDao(db: AppDatabase): CalendarEventDao {
        return db.calendarEventDao()
    }

    @Singleton
    @Provides
    fun provideCalendarDao(db: AppDatabase): CalendarDao {
        return db.calendarDao()
    }

    @Singleton
    @Provides
    fun provideFileDao(db: AppDatabase): FileDAO {
        return db.fileDao()
    }

    @Singleton
    @Provides
    fun provideTaskSummaryDao(db: AppDatabase): TaskSummaryDao {
        return db.taskSummaryDao()
    }

    @Singleton
    @Provides
    fun provideLongreadDao(db: AppDatabase): LongreadDao {
        return db.longreadDao()
    }
}