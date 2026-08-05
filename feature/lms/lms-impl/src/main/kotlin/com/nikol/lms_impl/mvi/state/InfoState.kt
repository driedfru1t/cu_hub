package com.nikol.lms_impl.mvi.state

import com.nikol.lms.domain.model.CourseScore

sealed interface InfoState {
    data object Loading : InfoState
    data class Success(
        val score: CourseScore
    )

    data object Error : InfoState
}