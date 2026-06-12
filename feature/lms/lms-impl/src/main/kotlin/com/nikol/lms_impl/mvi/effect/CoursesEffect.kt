package com.nikol.lms_impl.mvi.effect

import com.nikol.lms.domain.error.CourseError
import direct.direct_core.DirectEffect

sealed interface CoursesEffect : DirectEffect {
    data class ShowError(val error: CourseError) : CoursesEffect
}