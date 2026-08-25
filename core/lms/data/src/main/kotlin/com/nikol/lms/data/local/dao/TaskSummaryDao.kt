package com.nikol.lms.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.nikol.lms.data.local.entity.TaskSummaryEntity
import com.nikol.lms.domain.model.TaskState
import kotlinx.coroutines.flow.Flow
import java.time.Instant

@Dao
interface TaskSummaryDao {
    @Query("""
        SELECT * FROM task_summary
        WHERE deadline >= :from AND deadline < :to
        ORDER BY deadline ASC
    """)
    fun observeAllTasksInRange(from: Instant, to: Instant): Flow<List<TaskSummaryEntity>>

    @Query("""
        SELECT * FROM task_summary
        WHERE state IN (:states) AND deadline >= :from AND deadline < :to
        ORDER BY deadline ASC
    """)
    fun observeTasksByState(states: List<TaskState>, from: Instant, to: Instant): Flow<List<TaskSummaryEntity>>

    @Query("""
        SELECT * FROM task_summary
        WHERE course_id IN (:courseIds) AND deadline >= :from AND deadline < :to
        ORDER BY deadline ASC
    """)
    fun observeTasksByCourse(courseIds: List<Int>, from: Instant, to: Instant): Flow<List<TaskSummaryEntity>>

    @Query("""
        SELECT * FROM task_summary
        WHERE state IN (:states) AND course_id IN (:courseIds) AND deadline >= :from AND deadline < :to
        ORDER BY deadline ASC
    """)
    fun observeTasks(states: List<TaskState>, courseIds: List<Int>, from: Instant, to: Instant): Flow<List<TaskSummaryEntity>>

    @Upsert
    suspend fun upsert(tasks: List<TaskSummaryEntity>)
}