package com.nikol.lms.data

import arrow.core.raise.Raise
import arrow.core.raise.withError
import com.nikol.lms.data.mapper.toDomain
import com.nikol.lms.data.mapper.toGradeError
import com.nikol.lms.data.remote.service.PerformanceService
import com.nikol.lms.domain.error.GradeError
import com.nikol.lms.domain.error.ProfileError
import com.nikol.lms.domain.model.CourseActivitiesPerformance
import com.nikol.lms.domain.model.CourseTasksPerformance
import com.nikol.lms.domain.model.OverallPerformance
import com.nikol.lms.domain.repo.GradeRepository
import com.nikol.network.NetworkError
import javax.inject.Inject

class GradeRepositoryImpl @Inject constructor(
    private val performanceService: PerformanceService
) : GradeRepository {
    override suspend fun Raise<GradeError>.getOverallPerformance(): OverallPerformance {
        TODO()
    }

    override suspend fun Raise<GradeError>.getCourseActivitiesPerformance(
        courseId: Int
    ): CourseActivitiesPerformance {
        return withError(NetworkError::toGradeError) {
            val dto = performanceService.getCourseActivitiesPerformance(courseId)
            dto.toDomain()
        }
    }

    override suspend fun Raise<GradeError>.getCourseTasksPerformance(
        courseId: Int
    ): CourseTasksPerformance {
        return withError(NetworkError::toGradeError) {
            val dto = with(performanceService) { getCourseTasksPerformance(courseId) }
            dto.toDomain()
        }
    }
}