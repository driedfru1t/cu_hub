package com.nikol.lms.domain.useCase

import arrow.core.Either
import arrow.core.raise.either
import com.nikol.domain.UseCase
import com.nikol.lms.domain.error.TaskError
import com.nikol.lms.domain.model.TaskDetails
import com.nikol.lms.domain.repo.TaskRepository
import kotlinx.coroutines.CoroutineDispatcher

class GetTaskDetails(
    private val taskRepository: TaskRepository,
    dispatcher: CoroutineDispatcher
) : UseCase<TaskDetailParam, TaskDetails, TaskError>(dispatcher) {
    override suspend fun run(params: TaskDetailParam): Either<TaskError, TaskDetails> {
        return either { taskRepository.getTaskDetails(params.id) }
    }
}

data class TaskDetailParam(
    val id: Int
)