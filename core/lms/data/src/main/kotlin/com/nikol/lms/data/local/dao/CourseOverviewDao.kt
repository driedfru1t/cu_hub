package com.nikol.lms.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.nikol.lms.data.local.entity.CourseOverviewWithThemes
import com.nikol.lms.data.local.entity.CourseOverviewEntity
import com.nikol.lms.data.local.entity.CourseThemeEntity
import com.nikol.lms.data.local.entity.ExerciseEntity
import com.nikol.lms.data.local.entity.LongreadEntity
import com.nikol.lms.data.mapper.FlatCourseOverviewEntities
import kotlinx.coroutines.flow.Flow

@Dao
interface CourseOverviewDao {

    @Transaction
    @Query("SELECT * FROM course WHERE id = :courseId")
    fun getCourseOverview(courseId: Int): Flow<CourseOverviewWithThemes?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCourse(course: CourseOverviewEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertThemes(themes: List<CourseThemeEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLongreads(longreads: List<LongreadEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExercises(exercises: List<ExerciseEntity>)

    @Transaction
    suspend fun saveCourseOverview(flatEntities: FlatCourseOverviewEntities) {
        insertCourse(flatEntities.course)
        insertThemes(flatEntities.themes)
        insertLongreads(flatEntities.longreads)
        insertExercises(flatEntities.exercises)
    }
}