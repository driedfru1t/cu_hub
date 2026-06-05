package com.nikol.lms.data.remote.service

import arrow.core.Either
import com.nikol.lms.data.remote.model.common.PublicationStateDto
import com.nikol.lms.data.remote.model.course.CourseOverviewByIdResponseDto
import com.nikol.lms.data.remote.model.course.CourseSummaryItemDto
import com.nikol.lms.data.remote.model.course.CourseThemeSummaryByIdResponseDto
import com.nikol.lms.data.remote.model.course.ListStudentCoursesResponseDto
import com.nikol.lms.data.remote.model.material.LongreadMaterialsByIdResponseDto
import com.nikol.lms.domain.common.UnstableLmsApi
import com.nikol.network.BaseRemoteDataSource
import com.nikol.network.NetworkError
import com.nikol.network.di.qualifers.CuHttpClient
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import kotlinx.serialization.json.Json
import javax.inject.Inject

class CourseService @Inject constructor(
    @param:CuHttpClient private val cuClient: HttpClient,
    json: Json
) : BaseRemoteDataSource(json) {

    suspend fun getCourses(limit: Int = 10000): Either<NetworkError, ListStudentCoursesResponseDto> =
        safeApiCall {
            cuClient.get("courses/student") {
                parameter("limit", limit)
                parameter("offset", 0)
                parameter("state", PublicationStateDto.PUBLISHED.name.lowercase())
            }
        }

    suspend fun getCourseSummary(courseId: Int): Either<NetworkError, CourseSummaryItemDto> =
        safeApiCall {
            cuClient.get("/micro-lms/courses/$courseId")
        }

    suspend fun getCourseOverview(courseId: Int): Either<NetworkError, CourseOverviewByIdResponseDto> =
        safeApiCall {
            cuClient.get("/micro-lms/courses/$courseId/overview")
        }

    suspend fun getThemeSummary(themeId: Int): Either<NetworkError, CourseThemeSummaryByIdResponseDto> =
        safeApiCall {
            cuClient.get("/micro-lms/themes/$themeId")
        }

    @UnstableLmsApi
    suspend fun getThemeMaterials(
        longreadId: Int,
        limit: Int = 10000,
        offset: Int = 0
    ): Either<NetworkError, LongreadMaterialsByIdResponseDto> =
        safeApiCall {
            cuClient.get("/micro-lms/longreads/$longreadId/materials") {
                parameter("limit", limit)
                parameter("offset", offset)
            }
        }

}
