package com.nikol.lms_impl.mvi.state

import com.nikol.lms.domain.model.ParticipationType
import direct.direct_core.DirectState

sealed interface CoursesState<T> : DirectState {
    val courses: List<T> get() = emptyList()
    val currentPage: ParticipationType get() = ParticipationType.ALL

    val isLoading: Boolean get() = false

    data class Loading<T>(
        override val courses: List<T> = emptyList(),
        override val currentPage: ParticipationType = ParticipationType.ALL
    ) : CoursesState<T> {
        override val isLoading: Boolean = true
    }

    data class Success<T>(
        override val courses: List<T>,
        override val currentPage: ParticipationType = ParticipationType.ALL
    ) : CoursesState<T>
}
