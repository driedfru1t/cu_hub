package com.nikol.storage.di.module

import android.content.Context
import androidx.room.Room
import com.nikol.lms.data.local.dao.CourseDao
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
    fun provideCourseDao(db: AppDatabase): CourseDao {
        return db.courseDao()
    }
}