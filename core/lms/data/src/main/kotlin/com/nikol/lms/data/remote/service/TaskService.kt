package com.nikol.lms.data.remote.service

import arrow.core.Either
import arrow.core.raise.context.Raise
import com.nikol.lms.data.remote.model.course.CourseWithExercisesResponseDto
import com.nikol.lms.data.remote.model.task.TaskByIdResponseDto
import com.nikol.lms.data.remote.model.task.TaskSummaryDto
import com.nikol.lms.domain.model.TaskState
import com.nikol.network.BaseRemoteDataSource
import com.nikol.network.NetworkError
import com.nikol.network.di.qualifers.CuHttpClient
import com.nikol.network.di.qualifers.HttpClient as Http
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import kotlinx.serialization.json.Json
import javax.inject.Inject

class TaskService @Inject constructor(
    @param:Http(CuHttpClient.CU) private val cuClient: HttpClient,
    json: Json
) : BaseRemoteDataSource(json) {
    suspend fun taskDetail(id: Int): Either<NetworkError, TaskByIdResponseDto> =
        safeApiCall { cuClient.get("tasks/$id") }

    context(raise: Raise<NetworkError>)
    suspend fun getCourseWithExercise(courseId: Int): CourseWithExercisesResponseDto =
        raise.safeApiCall { cuClient.get("courses/$courseId/exercises") }

    context(raise: Raise<NetworkError>)
    suspend fun getTask(
        states: List<TaskState>,
        courseIds: List<Int>
    ): List<TaskSummaryDto> =
        raise.safeApiCall {
            cuClient.get("tasks/student") {
                states.forEach {
                    parameter("state", it.name.lowercase())
                }
                courseIds.forEach {
                    parameter("course", it)
                }
            }
        }

}