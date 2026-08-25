package com.nikol.storage.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.nikol.calendar.data.local.CalendarDao
import com.nikol.calendar.data.local.CalendarEntity
import com.nikol.calendar.data.local.CalendarEventDao
import com.nikol.calendar.data.local.CalendarEventEntity
import com.nikol.calendar.data.local.JsonConverters
import com.nikol.lms.backround.FileDAO
import com.nikol.lms.backround.FileEntity
import com.nikol.lms.data.local.DatabaseConverters
import com.nikol.lms.data.local.dao.CourseOverviewDao
import com.nikol.lms.data.local.dao.CoursesDao
import com.nikol.lms.data.local.dao.LongreadDao
import com.nikol.lms.data.local.dao.TaskSummaryDao
import com.nikol.lms.data.local.entity.CourseOverviewEntity
import com.nikol.lms.data.local.entity.CourseSummaryEntity
import com.nikol.lms.data.local.entity.CourseThemeEntity
import com.nikol.lms.data.local.entity.ExerciseEntity
import com.nikol.lms.data.local.entity.LongreadEntity
import com.nikol.lms.data.local.entity.TaskSummaryEntity

@Database(
    entities = [
        CourseSummaryEntity::class,
        CourseOverviewEntity::class,
        CourseThemeEntity::class,
        LongreadEntity::class,
        ExerciseEntity::class,
        CalendarEventEntity::class,
        CalendarEntity::class,
        FileEntity::class,
        TaskSummaryEntity::class
    ], version = 1, exportSchema = false
)
@TypeConverters(DatabaseConverters::class, CalendarConverters::class, JsonConverters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun courseDao(): CoursesDao
    abstract fun courseOverviewDao(): CourseOverviewDao
    abstract fun calendarEventDao(): CalendarEventDao
    abstract fun calendarDao(): CalendarDao
    abstract fun fileDao(): FileDAO
    abstract fun taskSummaryDao(): TaskSummaryDao
    abstract fun longreadDao(): LongreadDao
}