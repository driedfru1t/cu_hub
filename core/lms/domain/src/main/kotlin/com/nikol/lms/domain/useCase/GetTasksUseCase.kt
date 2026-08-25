package com.nikol.lms.domain.useCase

import arrow.core.Either
import arrow.core.raise.either
import com.nikol.domain.UseCase
import com.nikol.lms.domain.error.TaskError
import com.nikol.lms.domain.model.TaskState
import com.nikol.lms.domain.model.TaskSummary
import com.nikol.lms.domain.repo.TaskRepository
import kotlinx.coroutines.CoroutineDispatcher

data class TaskParam(
    val states: List<TaskState>,
    val courseIds: List<Int>
)

class GetTasksUseCase(
    dispatcher: CoroutineDispatcher,
    private val taskRepository: TaskRepository
) : UseCase<TaskParam, List<TaskSummary>, TaskError>(dispatcher) {
    override suspend fun run(params: TaskParam): Either<TaskError, List<TaskSummary>> =
        either {
            taskRepository.getTasks(params.states, params.courseIds)
        }

}