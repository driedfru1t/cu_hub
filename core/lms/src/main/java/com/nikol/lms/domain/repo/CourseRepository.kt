package com.nikol.lms.domain.repo

import arrow.core.Either
import com.nikol.lms.domain.error.CourseError
import com.nikol.lms.domain.model.CourseOverview
import com.nikol.lms.domain.model.CourseSummary
import com.nikol.lms.domain.model.LongreadMaterial
import kotlinx.coroutines.flow.Flow

interface CourseRepository {

    fun observeCourses(limit: Int = 10000): Flow<Either<CourseError, List<CourseSummary>>>

    /**
     * [Эндпоинт 5] Список всех доступных студенту курсов.
     * GET /micro-lms/courses/student
     * @param limit Лимит записей. По умолчанию передается большое число (например, 10000).
     */
    suspend fun getCourses(limit: Int = 10000): Either<CourseError, List<CourseSummary>>

    /**
     * [Эндпоинт 6] Обзор конкретного курса (детали, силлабус, описание).
     * GET /micro-lms/courses/{courseId}/overview
     */
    suspend fun getCourseOverview(courseId: Int): Either<CourseError, CourseOverview>

    /**
     * [Эндпоинт 7] Получение списка материалов (лекции, PDF, задания) внутри конкретной темы.
     * GET /micro-lms/longreads/{longreadId}/materials
     */
    suspend fun getThemeMaterials(
        longreadId: Int,
        limit: Int = 10000
    ): Either<CourseError, List<LongreadMaterial>>

    /**
     * [Эндпоинт 8] Конкретный материал (загрузка деталей одного файла или текста).
     * GET /micro-lms/materials/{materialId}
     */
    suspend fun getMaterialDetails(materialId: Int): Either<CourseError, LongreadMaterial>
}