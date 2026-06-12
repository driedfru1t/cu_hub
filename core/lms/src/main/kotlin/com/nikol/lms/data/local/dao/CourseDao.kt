package com.nikol.lms.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.nikol.lms.data.local.entity.CourseSummaryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CourseDao {

    @Query("SELECT * FROM course_summaries")
    fun loadCourses(): Flow<List<CourseSummaryEntity>>

    @Query("SELECT * FROM course_summaries")
    suspend fun getAllCourses(): List<CourseSummaryEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCourses(courses: List<CourseSummaryEntity>)

    @Query("DELETE FROM course_summaries")
    suspend fun clearCourses()

    @Transaction
    suspend fun syncCourses(courses: List<CourseSummaryEntity>) {
        clearCourses()
        insertCourses(courses)
    }
}