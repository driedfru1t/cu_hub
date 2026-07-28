package com.nikol.lms.domain.model

import com.nikol.lms.domain.common.UnstableLmsApi

// [!] Спецификация отсутствует в OpenAPI.
// На основе ваших заметок (имя, группа, ID), возвращается GET /hub/students/me.
@UnstableLmsApi
data class CoreProfile(
    val id: String, // UUID
    val firstName: String,
    val lastName: String,
    val middleName: String?,
    val groupName: String // Неясно, как бэкенд возвращает группу (ID, объект или строка). Требует уточнения в API.
)

data class LmsProfile(
    val id: String, // UUID
    val lastName: String,
    val firstName: String,
    val middleName: String?,
    val universityEmail: String,
    val timeAccount: String,
    val studyStartYear: Int?,
    val studyLevel: StudyLevel,
    val lateDaysBalance: Int
)

enum class StudyLevel {
    NONE, BACHELOR, MASTER, DPO, DPO_MASTER
}

// [!] Спецификация отсутствует в OpenAPI. GET /micro-lms/performance/student.
// Непонятно, в какой структуре бэкенд возвращает общую успеваемость (проценты, средний балл или список оценок).
// Оставляем пустую заглушку до исследования тела ответа API.
@UnstableLmsApi
data class OverallPerformance(
    val id: Int
)

// [!] Спецификация отсутствует в OpenAPI. GET /micro-lms/gradebook.
// Непонятно, какая структура у зачетки (семестры, предметы, типы зачетов).
// Оставляем заглушку.
@UnstableLmsApi
data class Gradebook(
    val stub: String // TODO: Определить структуру после вызова эндпоинта
)