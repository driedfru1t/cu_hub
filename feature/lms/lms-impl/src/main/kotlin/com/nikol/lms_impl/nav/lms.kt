package com.nikol.lms_impl.nav

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.nikol.lms_api.Course
import com.nikol.lms_api.CourseAction
import com.nikol.lms_api.CourseInfo
import com.nikol.lms_api.ThemeMaterial

fun EntryProviderScope<NavKey>.lms(
    onBack: () -> Unit,
    navigateTo: (NavKey) -> Unit
) {
    courses(onBack, navigateTo)
    material()
}
