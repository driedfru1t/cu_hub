package com.nikol.lms.domain.useCase

import arrow.core.Either
import arrow.core.raise.either
import com.nikol.domain.UseCase
import com.nikol.lms.domain.error.GradeError
import com.nikol.lms.domain.error.ProfileError
import com.nikol.lms.domain.model.CourseTasksPerformance
import com.nikol.lms.domain.repo.GradeRepository
import kotlinx.coroutines.CoroutineDispatcher

class GetCourseTasksPerformanceUseCase(
    coroutineDispatcher: CoroutineDispatcher,
    private val gradeRepository: GradeRepository
) : UseCase<CourseParam, CourseTasksPerformance, GradeError>(coroutineDispatcher) {
    override suspend fun run(params: CourseParam): Either<GradeError, CourseTasksPerformance> {
        return either {
            with(gradeRepository) { getCourseTasksPerformance(params.id) }
        }
    }
}