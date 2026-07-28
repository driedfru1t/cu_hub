package com.nikol.lms.domain.useCase

import arrow.core.Either
import com.nikol.domain.FlowUseCase
import com.nikol.domain.NoParam
import com.nikol.lms.domain.error.CourseError
import com.nikol.lms.domain.model.CourseSummary
import com.nikol.lms.domain.repo.CourseRepository
import kotlinx.coroutines.flow.Flow

class GetCoursesUseCase(
    private val courseRepository: CourseRepository
) : FlowUseCase<NoParam, List<CourseSummary>, CourseError>() {
    override fun run(params: NoParam): Flow<Either<CourseError, List<CourseSummary>>> {
        return courseRepository.observeCourses()
    }
}