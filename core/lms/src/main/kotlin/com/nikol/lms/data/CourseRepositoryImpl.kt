package com.nikol.lms.data

import arrow.core.Either
import com.nikol.cache.networkBoundResource
import com.nikol.lms.data.local.dao.CourseDao
import com.nikol.lms.data.mapper.dtoToEntity
import com.nikol.lms.data.mapper.toCourseError
import com.nikol.lms.data.mapper.toDomain
import com.nikol.lms.data.remote.service.CourseService
import com.nikol.lms.domain.common.UnstableLmsApi
import com.nikol.lms.domain.error.CourseError
import com.nikol.lms.domain.model.CourseOverview
import com.nikol.lms.domain.model.CourseSummary
import com.nikol.lms.domain.model.LongreadMaterial
import com.nikol.lms.domain.repo.CourseRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class CourseRepositoryImpl @Inject constructor(
    private val courseService: CourseService,
    private val courseDao: CourseDao
) : CourseRepository {


    override fun observeCourses(limit: Int): Flow<Either<CourseError, List<CourseSummary>>> {
        return networkBoundResource(
            query = { courseDao.loadCourses() },
            fetch = { courseService.getCourses(limit).map { it.items } },
            saveFetchRequest = { courses ->
                courseDao.syncCourses(courses.map { it.dtoToEntity() })
            }
        ).map {
            when (it) {
                is Either.Left -> Either.Left(it.value.toCourseError())
                is Either.Right -> Either.Right(it.value.map { course -> course.toDomain() })
            }
        }
    }

    /**
     * [Эндпоинт 5] Список всех доступных студенту курсов.
     * GET /micro-lms/courses/student
     * @param limit Лимит записей. По умолчанию передается большое число (например, 10000).
     */
    override suspend fun getCourses(limit: Int): Either<CourseError, List<CourseSummary>> =
        courseService.getCourses(limit)
            .map { list -> list.items.map { it.toDomain() } }
            .mapLeft { it.toCourseError() }


    /**
     * [Эндпоинт 6] Обзор конкретного курса (детали, силлабус, описание).
     * GET /micro-lms/courses/{courseId}/overview
     */
    override suspend fun getCourseOverview(courseId: Int): Either<CourseError, CourseOverview> {
        return courseService.getCourseOverview(courseId)
            .map { it.toDomain() }
            .mapLeft { it.toCourseError() }
    }

    /**
     * [Эндпоинт 7] Получение списка материалов (лекции, PDF, задания) внутри конкретной темы.
     * GET /micro-lms/longreads/{longreadId}/materials
     */
    @OptIn(UnstableLmsApi::class)
    override suspend fun getThemeMaterials(
        longreadId: Int,
        limit: Int
    ): Either<CourseError, List<LongreadMaterial>> {
        return courseService.getThemeMaterials(longreadId, limit)
            .map { response -> response.items.map { it.toDomain() } }
            .mapLeft { it.toCourseError() }
    }

    /**
     * [Эндпоинт 8] Конкретный материал (загрузка деталей одного файла или текста).
     * GET /micro-lms/materials/{materialId}
     */
    override suspend fun getMaterialDetails(materialId: Int): Either<CourseError, LongreadMaterial> {
        TODO()
    }
}