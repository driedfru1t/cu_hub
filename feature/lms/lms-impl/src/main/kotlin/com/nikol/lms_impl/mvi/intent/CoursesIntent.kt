package com.nikol.lms_impl.mvi.intent

import com.nikol.lms.domain.model.ParticipationType
import direct.direct_core.DirectIntent

sealed interface CoursesIntent : DirectIntent {
    data object Refresh : CoursesIntent
    data object Load : CoursesIntent

    data class ChangeTab(val participationType: ParticipationType) : CoursesIntent
    data class ClickToCourse(val id: Int, val name: String) : CoursesIntent
    data class ClickToMore(val id: Int, val name: String) : CoursesIntent
}