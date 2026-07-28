package com.nikol.lms.domain.useCase

import arrow.core.Either
import com.nikol.domain.UseCase
import com.nikol.lms.domain.error.CourseError
import com.nikol.lms.domain.model.CourseScore
import com.nikol.lms.domain.repo.CourseRepository
import kotlinx.coroutines.CoroutineDispatcher

data class ScoreParam(
    val id: Int
)

class GetCourseScoreUseCase(
    coroutineDispatcher: CoroutineDispatcher,
    private val courseRepository: CourseRepository
) : UseCase<ScoreParam, CourseScore, CourseError>(coroutineDispatcher) {
    override suspend fun run(params: ScoreParam): Either<CourseError, CourseScore> {
        return courseRepository.getProgress(params.id)
    }
}