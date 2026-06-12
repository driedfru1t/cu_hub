package com.nikol.lms_impl.nav

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.CompositionLocalProvider
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.nikol.di.ext.rememberComponent
import com.nikol.lms_api.Course
import com.nikol.lms_api.Courses
import com.nikol.lms_impl.screens.CoursesScreen
import com.nikol.lms_impl.viewModels.CoursesComponentViewModel
import com.nikol.viewmodel.LocalViewModelFactory


fun EntryProviderScope<NavKey>.courses() {
    entry<Courses> {
        val lmsComponent = rememberComponent { CoursesComponentViewModel(it) }
        CompositionLocalProvider(
            LocalViewModelFactory provides lmsComponent.viewModelProvider()
        ) {
            CoursesScreen()
        }
    }
}
