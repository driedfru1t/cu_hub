package com.nikol.lms.data.remote.service

import arrow.core.Either
import arrow.core.raise.Raise
import com.nikol.lms.data.remote.model.common.PublicationStateDTO
import com.nikol.lms.data.remote.model.course.CourseDTO
import com.nikol.lms.data.remote.model.course.CourseOverviewByIdResponseDto
import com.nikol.lms.data.remote.model.course.CourseScoreDTO
import com.nikol.lms.data.remote.model.course.CourseSummaryItemDto
import com.nikol.lms.data.remote.model.course.CourseThemeSummaryByIdResponseDto
import com.nikol.lms.data.remote.model.course.DeadlineItemsDto
import com.nikol.lms.data.remote.model.course.ListStudentCoursesResponseDto
import com.nikol.lms.data.remote.model.material.LongreadMaterialsByIdResponseDto
import com.nikol.lms.domain.model.ParticipationType
import com.nikol.network.BaseRemoteDataSource
import com.nikol.network.NetworkError
import com.nikol.network.di.qualifers.CuHttpClient
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import kotlinx.serialization.json.Json
import javax.inject.Inject
import com.nikol.network.di.qualifers.HttpClient as Http

class CourseService @Inject constructor(
    @param:Http(CuHttpClient.CU) private val cuClient: HttpClient,
    json: Json
) : BaseRemoteDataSource(json) {

    suspend fun getCourses(
        state: PublicationStateDTO = PublicationStateDTO.PUBLISHED,
        participationType: ParticipationType? = null,
        limit: Int = 10000,
        offset: Int = 0
    ): Either<NetworkError, ListStudentCoursesResponseDto> =
        safeApiCall {
            cuClient.get("courses/student") {
                parameter("limit", limit)
                parameter("offset", offset)
                participationType?.let {
                    parameter("participationType", it.name.lowercase())
                }
                parameter("state", state.name.lowercase())
            }
        }

    suspend fun getCourseSummary(courseId: Int): Either<NetworkError, CourseSummaryItemDto> =
        safeApiCall {
            cuClient.get("courses/$courseId")
        }

    suspend fun getCourseOverview(courseId: Int): Either<NetworkError, CourseOverviewByIdResponseDto> =
        safeApiCall {
            cuClient.get("courses/$courseId/overview")
        }

    suspend fun getThemeSummary(themeId: Int): Either<NetworkError, CourseThemeSummaryByIdResponseDto> =
        safeApiCall {
            cuClient.get("themes/$themeId")
        }

    suspend fun getThemeMaterials(
        longreadId: Int,
        limit: Int = 10000,
        offset: Int = 0
    ): Either<NetworkError, LongreadMaterialsByIdResponseDto> =
        safeApiCall {
            cuClient.get("longreads/$longreadId/materials") {
                parameter("limit", limit)
                parameter("offset", offset)
            }
        }

    suspend fun getCourseScore(courseId: Int): Either<NetworkError, CourseScoreDTO> =
        safeApiCall {
            cuClient.get("courses/$courseId/student/progress")
        }

    suspend fun getCourse(courseId: Int): Either<NetworkError, CourseDTO> =
        safeApiCall {
            cuClient.get("courses/$courseId")
        }

    suspend fun getCourseDeadlines(courseId: Int): Either<NetworkError, DeadlineItemsDto> =
        safeApiCall {
            cuClient.get("deadlines") {
                parameter("courseId", courseId)
            }
        }
}
