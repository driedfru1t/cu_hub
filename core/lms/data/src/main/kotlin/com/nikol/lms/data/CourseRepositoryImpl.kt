package com.nikol.lms.data

import arrow.core.Either
import arrow.core.raise.either
import com.nikol.cache.networkBoundResource
import com.nikol.cache.networkFirstWithFallback
import com.nikol.common.CuHubDispatcher.IO
import com.nikol.common.Dispatcher
import com.nikol.lms.data.local.dao.CourseOverviewDao
import com.nikol.lms.data.local.dao.CoursesDao
import com.nikol.lms.data.mapper.dtoToEntity
import com.nikol.lms.data.mapper.toCourseError
import com.nikol.lms.data.mapper.toDomain
import com.nikol.lms.data.mapper.toLocalEntities
import com.nikol.lms.data.mapper.toLocalRelation
import com.nikol.lms.data.remote.model.common.PublicationStateDTO
import com.nikol.lms.data.remote.model.course.CourseSummaryItemDto
import com.nikol.lms.data.remote.model.course.ParticipationTypeDto
import com.nikol.lms.data.remote.service.CourseService
import com.nikol.lms.domain.error.CourseError
import com.nikol.lms.domain.model.Course
import com.nikol.lms.domain.model.CourseOverview
import com.nikol.lms.domain.model.CourseScore
import com.nikol.lms.domain.model.CourseSummary
import com.nikol.lms.domain.model.DeadlineCourse
import com.nikol.lms.domain.model.LongreadMaterial
import com.nikol.lms.domain.model.ParticipationType
import com.nikol.lms.domain.model.PublicationState
import com.nikol.lms.domain.repo.CourseRepository
import com.nikol.network.NetworkError
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class CourseRepositoryImpl @Inject constructor(
    private val courseService: CourseService,
    private val coursesDao: CoursesDao,
    private val courseOverviewDao: CourseOverviewDao,
    @param:Dispatcher(IO) private val dispatcher: CoroutineDispatcher
) : CourseRepository {

    private suspend fun fetchMergedCourses(): Either<NetworkError, List<CourseSummaryItemDto>> =
        coroutineScope {
            val electiveDeferred =
                async { courseService.getCourses(participationType = ParticipationType.ELECTIVE) }
            val requiredDeferred =
                async { courseService.getCourses(participationType = ParticipationType.REQUIRED) }
            val listenerDeferred =
                async { courseService.getCourses(participationType = ParticipationType.LISTENER) }
            val internalDeferred =
                async { courseService.getCourses(participationType = ParticipationType.INTERNAL) }
            val allDeferred = async { courseService.getCourses() }
            either {
                val allItems = allDeferred.await().bind().items
                val requiredItems = requiredDeferred.await().bind().items
                val electiveItems = electiveDeferred.await().bind().items
                val listenerItems = listenerDeferred.await().bind().items
                val internalItems = internalDeferred.await().bind().items

                val requiredOrderMap =
                    requiredItems.mapIndexed { index, item -> item.id to index }.toMap()
                val electiveOrderMap =
                    electiveItems.mapIndexed { index, item -> item.id to index }.toMap()
                val listenerOrderMap =
                    listenerItems.mapIndexed { index, item -> item.id to index }.toMap()
                val internalOrderMap =
                    internalItems.mapIndexed { index, item -> item.id to index }.toMap()


                allItems.mapIndexed { globalIndex, dto ->
                    val resolvedType: ParticipationTypeDto
                    val localIndex: Int

                    when (dto.id) {
                        in requiredOrderMap -> {
                            resolvedType = ParticipationTypeDto.REQUIRED
                            localIndex = requiredOrderMap[dto.id] ?: 0
                        }

                        in electiveOrderMap -> {
                            resolvedType = ParticipationTypeDto.ELECTIVE
                            localIndex = electiveOrderMap[dto.id] ?: 0
                        }

                        in listenerOrderMap -> {
                            resolvedType = ParticipationTypeDto.LISTENER
                            localIndex = listenerOrderMap[dto.id] ?: 0
                        }

                        in internalOrderMap -> {
                            resolvedType = ParticipationTypeDto.INTERNAL
                            localIndex = internalOrderMap[dto.id] ?: 0
                        }

                        else -> {
                            resolvedType = ParticipationTypeDto.INTERNAL
                            localIndex = 0
                        }
                    }

                    dto.copy(
                        participationType = resolvedType,
                        allOrderIndex = globalIndex,
                        categoryOrderIndex = localIndex
                    )
                }
            }
        }

    override suspend fun getCourseDeadlines(courseId: Int): Either<CourseError, List<DeadlineCourse>> {
        return courseService.getCourseDeadlines(courseId).mapLeft { it.toCourseError() }
            .map { it.map { deadline -> deadline.toDomain() } }
    }

    override fun observeCourses(limit: Int): Flow<Either<CourseError, List<CourseSummary>>> {
        return networkBoundResource(
            query = { coursesDao.loadCourses() },
            fetch = { fetchMergedCourses() },
            saveFetchRequest = { courses ->
                coursesDao.syncCourses(courses.map { it.dtoToEntity() })
            }
        ).map {
            when (it) {
                is Either.Left -> Either.Left(it.value.toCourseError())
                is Either.Right -> Either.Right(it.value.map { course -> course.toDomain() })
            }
        }.flowOn(dispatcher)
    }

    private fun stateToDto(state: PublicationState): PublicationStateDTO {
        return when (state) {
            PublicationState.PUBLISHED -> PublicationStateDTO.PUBLISHED
            PublicationState.ARCHIVED -> PublicationStateDTO.ARCHIVED
            PublicationState.DRAFT -> PublicationStateDTO.DRAFT
        }
    }

    /**
     * [Эндпоинт 5] Список всех доступных студенту курсов.
     * GET /micro-lms/courses/student
     * @param limit Лимит записей. По умолчанию передается большое число (например, 10000).
     */
    override suspend fun getCourses(
        publicationState: PublicationState,
        limit: Int,
        offset: Int,
        participationType: ParticipationType?
    ): Either<CourseError, List<CourseSummary>> =
        courseService.getCourses(stateToDto(publicationState), participationType, limit, offset)
            .map { list -> list.items.map { it.toDomain() } }
            .mapLeft { it.toCourseError() }


    /**
     * [Эндпоинт 6] Обзор конкретного курса (детали, силлабус, описание).
     * GET /micro-lms/courses/{courseId}/overview
     */
    override suspend fun getCourseOverview(courseId: Int): Either<CourseError, CourseOverview> {
        return networkFirstWithFallback(
            fetch = { courseService.getCourseOverview(courseId) },
            saveFetchResult = { courseOverviewDao.saveCourseOverview(it.toLocalEntities()) },
            queryCache = { courseOverviewDao.getCourseOverview(courseId).first() },
            mapDtoToDomain = { it.toLocalRelation() }
        ).mapLeft {
            it.toCourseError()
        }.map { it.toDomain() }
    }

    /**
     * [Эндпоинт 7] Получение списка материалов (лекции, PDF, задания) внутри конкретной темы.
     * GET /micro-lms/longreads/{longreadId}/materials
     */
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

    override suspend fun getProgress(courseId: Int): Either<CourseError, CourseScore> {
        return courseService.getCourseScore(courseId).mapLeft { it.toCourseError() }
            .map { it.toDomain() }
    }

    override suspend fun getCourseById(courseId: Int): Either<CourseError, Course> {
        return courseService.getCourse(courseId).mapLeft { it.toCourseError() }
            .map { it.toDomain() }
    }
}