package com.nikol.lms.domain.useCase

import arrow.core.Either
import com.nikol.domain.UseCase
import com.nikol.lms.domain.error.CourseError
import com.nikol.lms.domain.model.CourseOverview
import com.nikol.lms.domain.repo.CourseRepository
import kotlinx.coroutines.CoroutineDispatcher

data class CourseParam(
    val id: Int
)

class GetCourseUseCase(
    coroutineDispatcher: CoroutineDispatcher,
    private val courseRepository: CourseRepository
) : UseCase<CourseParam, CourseOverview, CourseError>(coroutineDispatcher) {
    override suspend fun run(params: CourseParam): Either<CourseError, CourseOverview> {
        return courseRepository.getCourseOverview(params.id)
    }
}
