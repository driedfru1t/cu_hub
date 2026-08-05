package com.nikol.lms_impl.mvi.intent

import direct.direct_core.DirectIntent

sealed interface CourseArchiveIntent : DirectIntent {
    data object OnBack : CourseArchiveIntent
    data class OnMaterial(val id: Int, val name: String) : CourseArchiveIntent
    data class OnCourseAction(val id: Int, val name: String) : CourseArchiveIntent
}