package com.nikol.lms.data.local.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.nikol.lms.data.remote.model.task.TaskStateDto
import com.nikol.lms.domain.model.TaskCourse
import com.nikol.lms.domain.model.TaskCourseTheme
import com.nikol.lms.domain.model.TaskExerciseActivity
import com.nikol.lms.domain.model.TaskLongread
import com.nikol.lms.domain.model.TaskReviewer
import com.nikol.lms.domain.model.TaskScoreSkillLevel
import com.nikol.lms.domain.model.TaskState
import com.nikol.lms.domain.model.TaskType
import java.time.Instant

@Entity(
    tableName = "task_summary",
)
data class TaskSummaryEntity(
    @PrimaryKey
    val id: Int,
    val state: TaskState,
    val score: Double?,
    val scoreSkillLevel: TaskScoreSkillLevel?,
    val isLateDaysEnabled: Boolean,
    val extraScore: Double?,
    val createdAt: Instant,
    val startedAt: Instant?,
    val submitAt: Instant?,
    val rejectAt: Instant?,
    val evaluateAt: Instant?,
    val deadline: Instant,
    val lateDays: Int?,
    @Embedded(prefix = "exercise_") val exercise: ExerciseTaskSummaryEmbedded,
    @Embedded(prefix = "course_") val course: TaskCourse,
    @Embedded(prefix = "theme_") val theme: TaskCourseTheme,
    @Embedded(prefix = "longread_") val longread: TaskLongread,
    @Embedded(prefix = "reviewer_") val reviewer: TaskReviewer?,
    val quizSessionId: Int?,
)

data class ExerciseTaskSummaryEmbedded(
    val id: Int,
    val name: String,
    val type: TaskType,
    val maxScore: Double,
    val startDate: Instant,
    val deadline: Instant,
    @Embedded("activity_") val activity: TaskExerciseActivity
)