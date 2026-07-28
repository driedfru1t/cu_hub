package com.nikol.lms.domain.useCase

import arrow.core.Either
import arrow.core.raise.either
import com.nikol.domain.UseCase
import com.nikol.lms.domain.error.TaskError
import com.nikol.lms.domain.model.CourseWithExercises
import com.nikol.lms.domain.repo.TaskRepository
import kotlinx.coroutines.CoroutineDispatcher

class GetCourseExercisesUseCase(
    coroutineDispatcher: CoroutineDispatcher,
    private val taskRepository: TaskRepository
) : UseCase<CourseParam, CourseWithExercises, TaskError>(coroutineDispatcher) {
    override suspend fun run(params: CourseParam): Either<TaskError, CourseWithExercises> {
        return either {
            with(taskRepository) { getCourseExercises(params.id) }
        }
    }
}