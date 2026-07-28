package com.nikol.lms.domain.useCase

import arrow.core.Either
import arrow.core.raise.either
import com.nikol.domain.UseCase
import com.nikol.lms.domain.error.GradeError
import com.nikol.lms.domain.model.CourseActivitiesPerformance
import com.nikol.lms.domain.repo.GradeRepository
import kotlinx.coroutines.CoroutineDispatcher

class GetCourseActivitiesPerformanceUseCase(
    coroutineDispatcher: CoroutineDispatcher,
    private val gradeRepository: GradeRepository
) : UseCase<CourseParam, CourseActivitiesPerformance, GradeError>(coroutineDispatcher) {
    override suspend fun run(params: CourseParam): Either<GradeError, CourseActivitiesPerformance> {
        return either {
            with(gradeRepository) { getCourseActivitiesPerformance(params.id) }
        }
    }
}