package com.nikol.lms.data

import arrow.core.Either
import com.nikol.lms.domain.error.TaskError
import com.nikol.lms.domain.model.TaskAttachmentPayload
import com.nikol.lms.domain.model.TaskDetails
import com.nikol.lms.domain.model.TaskState
import com.nikol.lms.domain.model.TaskSummary
import com.nikol.lms.domain.repo.TaskRepository

class TaskRepositoryImpl : TaskRepository {
    /**
     * [Эндпоинт 9] Список всех задач (Канбан) с фильтрацией по статусам.
     * GET /micro-lms/tasks/student
     * @param states Список запрашиваемых статусов (например, inProgress, review, backlog)
     */
    override suspend fun getTasks(states: List<TaskState>): Either<TaskError, List<TaskSummary>> {
        TODO("Not yet implemented")
    }

    /**
     * [Эндпоинт 10] Детали конкретной задачи (полное описание, дедлайн, статус).
     * GET /micro-lms/tasks/{taskId}
     */
    override suspend fun getTaskDetails(taskId: Int): Either<TaskError, TaskDetails> {
        TODO("Not yet implemented")
    }

    /**
     * [Эндпоинт 11] Задачи конкретного курса (все задания, относящиеся к одному предмету).
     * GET /micro-lms/courses/{courseId}/exercises
     */
    override suspend fun getCourseExercises(courseId: Int): Either<TaskError, List<TaskSummary>> {
        TODO("Not yet implemented")
    }

    /**
     * [Эндпоинт 12] Начать выполнение задачи. Переводит задачу в статус "В работе".
     * PUT /micro-lms/tasks/{taskId}/start
     */
    override suspend fun startTask(taskId: Int): Either<TaskError, Unit> {
        TODO("Not yet implemented")
    }

    /**
     * [Эндпоинт 13] Сдать решение на проверку (ссылка на репозиторий и/или прикрепленные файлы).
     * PUT /micro-lms/tasks/{taskId}/submit
     */
    override suspend fun submitTask(
        taskId: Int,
        solutionUrl: String?,
        attachments: List<TaskAttachmentPayload>
    ): Either<TaskError, Unit> {
        TODO("Not yet implemented")
    }

    /**
     * [Эндпоинт 21] Использовать "дни опоздания" (Late Days) для продления дедлайна.
     * PUT /micro-lms/tasks/{taskId}/late-days-prolong
     */
    override suspend fun prolongDeadline(
        taskId: Int,
        lateDays: Int
    ): Either<TaskError, Unit> {
        TODO("Not yet implemented")
    }
}