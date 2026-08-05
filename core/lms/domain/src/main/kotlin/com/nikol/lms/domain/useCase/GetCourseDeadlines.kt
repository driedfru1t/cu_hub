package com.nikol.lms.domain.useCase

import arrow.core.Either
import com.nikol.domain.UseCase
import com.nikol.lms.domain.error.CourseError
import com.nikol.lms.domain.model.DeadlineCourse
import com.nikol.lms.domain.repo.CourseRepository
import kotlinx.coroutines.CoroutineDispatcher

class GetCourseDeadlines(
    private val courseRepository: CourseRepository,
    coroutineDispatcher: CoroutineDispatcher
) : UseCase<CourseDeadlinesP, List<DeadlineCourse>, CourseError>(coroutineDispatcher) {
    override suspend fun run(params: CourseDeadlinesP): Either<CourseError, List<DeadlineCourse>> {
        return courseRepository.getCourseDeadlines(params.courseId)
    }
}

data class CourseDeadlinesP(
    val courseId: Int
)