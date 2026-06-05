package com.nikol.lms.data

import arrow.core.Either
import com.nikol.lms.domain.error.ProfileError
import com.nikol.lms.domain.model.CoreProfile
import com.nikol.lms.domain.model.Gradebook
import com.nikol.lms.domain.model.LmsProfile
import com.nikol.lms.domain.model.OverallPerformance
import com.nikol.lms.domain.repo.ProfileRepository

class ProfileRepositoryImpl : ProfileRepository {
    /**
     * [Эндпоинт 1] Получение базовой информации о студенте (имя, группа, ID).
     * GET /hub/students/me
     */
    override suspend fun getCoreProfile(): Either<ProfileError, CoreProfile> {
        TODO("Not yet implemented")
    }

    /**
     * [Эндпоинт 2] LMS Профиль: расширенная информация в контексте обучения.
     * GET /micro-lms/students/me
     */
    override suspend fun getLmsProfile(): Either<ProfileError, LmsProfile> {
        TODO("Not yet implemented")
    }

    /**
     * [Эндпоинт 3] Успеваемость (Общая статистика по всем предметам).
     * GET /micro-lms/performance/student
     */
    override suspend fun getOverallPerformance(): Either<ProfileError, OverallPerformance> {
        TODO("Not yet implemented")
    }

    /**
     * [Эндпоинт 4] Зачетка (Gradebook) с итоговыми оценками.
     * GET /micro-lms/gradebook
     */
    override suspend fun getGradebook(): Either<ProfileError, Gradebook> {
        TODO("Not yet implemented")
    }
}