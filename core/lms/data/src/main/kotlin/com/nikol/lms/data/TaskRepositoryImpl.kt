package com.nikol.lms.data

import arrow.core.raise.Raise
import arrow.core.raise.withError
import com.nikol.common.CuHubDispatcher
import com.nikol.common.Dispatcher
import com.nikol.lms.data.local.dao.TaskSummaryDao
import com.nikol.lms.data.mapper.toDomain
import com.nikol.lms.data.mapper.toEntity
import com.nikol.lms.data.mapper.toTaskError
import com.nikol.lms.data.remote.model.task.TaskSummaryDto
import com.nikol.lms.data.remote.service.TaskService
import com.nikol.lms.domain.error.TaskError
import com.nikol.lms.domain.model.CourseWithExercises
import com.nikol.lms.domain.model.TaskAttachmentPayload
import com.nikol.lms.domain.model.TaskDetails
import com.nikol.lms.domain.model.TaskState
import com.nikol.lms.domain.model.TaskSummary
import com.nikol.lms.domain.repo.TaskRepository
import com.nikol.network.NetworkError
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import java.time.Instant
import javax.inject.Inject

class TaskRepositoryImpl @Inject constructor(
    private val taskService: TaskService,
    private val taskSummaryDao: TaskSummaryDao,
    @param:Dispatcher(CuHubDispatcher.IO) private val coroutineDispatcher: CoroutineDispatcher
) : TaskRepository {
    /**
     * [Эндпоинт 9] Список всех задач (Канбан) с фильтрацией по статусам.
     * GET /micro-lms/tasks/student
     * @param states Список запрашиваемых статусов (например, inProgress, review, backlog)
     */
    context(raise: Raise<TaskError>)
    override suspend fun getTasks(
        states: List<TaskState>,
        courseIds: List<Int>
    ): List<TaskSummary> {
        return raise.withError(NetworkError::toTaskError) {
            val data = taskService.getTask(states, courseIds).map { it.toDomain() }
            taskSummaryDao.upsert(data.map { it.toEntity() })
            data
        }
    }

    override fun observeTasks(
        states: List<TaskState>,
        courseIds: List<Int>,
        from: Instant,
        to: Instant
    ): Flow<List<TaskSummary>> {
        val flow = if (states.isEmpty() && courseIds.isEmpty()) {
            taskSummaryDao.observeAllTasksInRange(from, to)
        } else if (states.isNotEmpty() && courseIds.isEmpty()) {
            taskSummaryDao.observeTasksByState(states, from, to)
        } else if (states.isEmpty() && courseIds.isNotEmpty()) {
            taskSummaryDao.observeTasksByCourse(courseIds, from, to)
        } else {
            taskSummaryDao.observeTasks(states, courseIds, from, to)
        }

        return flow
            .map { list -> list.map { it.toDomain() } }
            .flowOn(coroutineDispatcher)
    }

    /**
     * [Эндпоинт 10] Детали конкретной задачи (полное описание, дедлайн, статус).
     * GET /micro-lms/tasks/{taskId}
     */
    context(raise: Raise<TaskError>)
    override suspend fun getTaskDetails(taskId: Int): TaskDetails {
        return raise.withError(NetworkError::toTaskError) {
            val data = taskService.taskDetail(taskId).bind()
            data.toDomain()
        }
    }

    /**
     * [Эндпоинт 11] Задачи конкретного курса (все задания, относящиеся к одному предмету).
     * GET /micro-lms/courses/{courseId}/exercises
     */
    context(raise: Raise<TaskError>)
    override suspend fun getCourseExercises(courseId: Int): CourseWithExercises {
        return raise.withError(NetworkError::toTaskError) {
            val data = taskService.getCourseWithExercise(courseId)
            data.toDomain()
        }
    }

    /**
     * [Эндпоинт 12] Начать выполнение задачи. Переводит задачу в статус "В работе".
     * PUT /micro-lms/tasks/{taskId}/start
     */
    context(raise: Raise<TaskError>)
    override suspend fun startTask(taskId: Int) {
        TODO("Not yet implemented")
    }

    /**
     * [Эндпоинт 13] Сдать решение на проверку (ссылка на репозиторий и/или прикрепленные файлы).
     * PUT /micro-lms/tasks/{taskId}/submit
     */
    context(raise: Raise<TaskError>)
    override suspend fun submitTask(
        taskId: Int,
        solutionUrl: String?,
        attachments: List<TaskAttachmentPayload>
    ) {
        TODO("Not yet implemented")
    }

    /**
     * [Эндпоинт 21] Использовать "дни опоздания" (Late Days) для продления дедлайна.
     * PUT /micro-lms/tasks/{taskId}/late-days-prolong
     */
    context(raise: Raise<TaskError>)
    override suspend fun prolongDeadline(
        taskId: Int,
        lateDays: Int
    ) {
        TODO("Not yet implemented")
    }
}