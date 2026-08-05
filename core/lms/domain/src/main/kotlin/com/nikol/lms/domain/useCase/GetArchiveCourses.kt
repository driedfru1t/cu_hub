package com.nikol.lms.domain.useCase

import arrow.core.Either
import com.nikol.domain.NoParam
import com.nikol.domain.UseCase
import com.nikol.lms.domain.error.CourseError
import com.nikol.lms.domain.model.CourseSummary
import com.nikol.lms.domain.model.PublicationState
import com.nikol.lms.domain.repo.CourseRepository
import kotlinx.coroutines.CoroutineDispatcher

class GetArchiveCourses(
    dispatcher: CoroutineDispatcher,
    private val courseRepository: CourseRepository
) : UseCase<NoParam, List<CourseSummary>, CourseError>(dispatcher) {
    override suspend fun run(params: NoParam): Either<CourseError, List<CourseSummary>> {
        return courseRepository.getCourses(PublicationState.ARCHIVED)
    }
}