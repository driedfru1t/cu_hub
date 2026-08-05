package com.nikol.lms.domain.repo

import arrow.core.Either
import com.nikol.lms.domain.error.ProfileError
import com.nikol.lms.domain.model.CoreProfile
import com.nikol.lms.domain.model.Gradebook
import com.nikol.lms.domain.model.LmsProfile
import com.nikol.lms.domain.model.OverallPerformance

interface ProfileRepository {

    /**
     * [Эндпоинт 1] Получение базовой информации о студенте (имя, группа, ID).
     * GET /hub/students/me
     */
    suspend fun getCoreProfile(): Either<ProfileError, CoreProfile>

    /**
     * [Эндпоинт 2] LMS Профиль: расширенная информация в контексте обучения.
     * GET /micro-lms/students/me
     */
    suspend fun getLmsProfile(): Either<ProfileError, LmsProfile>

    /**
     * [Эндпоинт 4] Зачетка (Gradebook) с итоговыми оценками.
     * GET /micro-lms/gradebook
     */
    suspend fun getGradebook(): Either<ProfileError, Gradebook>
}