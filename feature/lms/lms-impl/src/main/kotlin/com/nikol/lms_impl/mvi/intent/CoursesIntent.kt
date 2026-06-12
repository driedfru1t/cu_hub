package com.nikol.lms_impl.mvi.intent

import direct.direct_core.DirectIntent

sealed interface CoursesIntent : DirectIntent{
    data object Refresh : CoursesIntent
    data object Load : CoursesIntent
}