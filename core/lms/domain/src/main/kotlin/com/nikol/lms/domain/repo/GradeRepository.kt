package com.nikol.lms.domain.repo

import arrow.core.Either
import arrow.core.raise.context.Raise
import com.nikol.lms.domain.error.GradeError
import com.nikol.lms.domain.error.ProfileError
import com.nikol.lms.domain.model.CourseActivitiesPerformance
import com.nikol.lms.domain.model.CourseTasksPerformance
import com.nikol.lms.domain.model.OverallPerformance

interface GradeRepository {
    /**
     * [Эндпоинт 3] Успеваемость (Общая статистика по всем предметам).
     * GET /micro-lms/performance/student
     */
    suspend fun Raise<GradeError>.getOverallPerformance(): OverallPerformance

    /**
     * Успеваемость по активностям (лекции, домашние работы и т.д.) внутри курса.
     * GET /micro-lms/performance/courses/{courseId}/activities
     */
    suspend fun Raise<GradeError>.getCourseActivitiesPerformance(courseId: Int): CourseActivitiesPerformance

    /**
     * Детальная успеваемость по задачам студента внутри курса.
     * GET /micro-lms/performance/courses/{courseId}/tasks
     */
    suspend fun Raise<GradeError>.getCourseTasksPerformance(courseId: Int): CourseTasksPerformance
}
