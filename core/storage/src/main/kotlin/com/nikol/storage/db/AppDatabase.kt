package com.nikol.storage.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.nikol.lms.data.local.dao.CourseDao
import com.nikol.lms.data.local.entity.CourseSummaryEntity

@Database(entities = [CourseSummaryEntity::class], version = 1)
internal abstract class AppDatabase : RoomDatabase() {
    abstract fun courseDao(): CourseDao
}