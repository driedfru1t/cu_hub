package com.nikol.lms_impl.mvi.intent

import com.nikol.lms_impl.mvi.state.CourseTab
import direct.direct_core.DirectIntent

interface CourseDetailIntent : DirectIntent {
    data object Back : CourseDetailIntent
    data class Info(
        val sillabusUrl: String?,
        val timeChannelUrl: String?
    ) : CourseDetailIntent

    data class SwitchTab(val tab: CourseTab) : CourseDetailIntent
}