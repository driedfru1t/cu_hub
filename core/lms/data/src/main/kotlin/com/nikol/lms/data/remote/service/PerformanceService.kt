package com.nikol.lms.data.remote.service

import arrow.core.raise.Raise
import com.nikol.lms.data.remote.model.performance.ActivityPerformanceItemsDTO
import com.nikol.lms.data.remote.model.performance.TaskStudentPerformanceItemsDto
import com.nikol.lms.domain.error.ChatError
import com.nikol.network.BaseRemoteDataSource
import com.nikol.network.NetworkError
import com.nikol.network.di.qualifers.CuHttpClient
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import kotlinx.serialization.json.Json
import javax.inject.Inject
import com.nikol.network.di.qualifers.HttpClient as Http

class PerformanceService @Inject constructor(
    @param:Http(CuHttpClient.CU) private val cuClient: HttpClient,
    json: Json
) : BaseRemoteDataSource(json) {
    context(raise: Raise<NetworkError>)
    suspend fun getCourseActivitiesPerformance(courseId: Int): ActivityPerformanceItemsDTO =
        raise.safeApiCall { cuClient.get("courses/$courseId/activities-performance") }

    context(raise: Raise<NetworkError>)
    suspend fun getCourseTasksPerformance(courseId: Int): TaskStudentPerformanceItemsDto =
        raise.safeApiCall { cuClient.get("courses/$courseId/student-performance") }
}