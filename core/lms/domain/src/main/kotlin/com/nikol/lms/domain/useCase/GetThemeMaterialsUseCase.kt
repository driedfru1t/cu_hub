package com.nikol.lms.domain.useCase

import arrow.core.Either
import com.nikol.domain.UseCase
import com.nikol.lms.domain.error.CourseError
import com.nikol.lms.domain.model.LongreadMaterial
import com.nikol.lms.domain.repo.CourseRepository
import kotlinx.coroutines.CoroutineDispatcher

class GetThemeMaterialsUseCase(
    private val courseRepository: CourseRepository,
    dispatcher: CoroutineDispatcher
) : UseCase<ThemeMaterialParam, List<LongreadMaterial>, CourseError>(dispatcher) {
    override suspend fun run(params: ThemeMaterialParam): Either<CourseError, List<LongreadMaterial>> {
        return courseRepository.getThemeMaterials(params.id)
    }
}

data class ThemeMaterialParam(
    val id: Int
)