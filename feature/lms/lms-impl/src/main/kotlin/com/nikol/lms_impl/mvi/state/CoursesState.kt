package com.nikol.lms_impl.mvi.state

import com.nikol.lms.domain.error.CourseError
import com.nikol.lms.domain.model.CourseSummary
import direct.direct_core.DirectState

sealed interface CoursesState : DirectState {
    val courses: List<CourseSummary> get() = emptyList()
    val isRefreshing: Boolean get() = false

    data object Loading : CoursesState

    data class Refreshing(
        override val courses: List<CourseSummary>
    ) : CoursesState {
        override val isRefreshing: Boolean = true
    }

    data class Success(
        override val courses: List<CourseSummary>
    ) : CoursesState
}
