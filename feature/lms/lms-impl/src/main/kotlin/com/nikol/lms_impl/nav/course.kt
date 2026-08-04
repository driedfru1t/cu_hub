package com.nikol.lms_impl.nav

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.CompositionLocalProvider
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.metadata
import com.example.nav3recipes.bottomsheet.BottomSheetSceneStrategy
import com.nikol.di.ext.rememberComponent
import com.nikol.lms_api.ArchiveCourses
import com.nikol.lms_api.Course
import com.nikol.lms_api.CourseAction
import com.nikol.lms_api.CourseInfo
import com.nikol.lms_api.Courses
import com.nikol.lms_impl.screens.ActionCourseScreen
import com.nikol.lms_impl.screens.ArchiveCourseScreen
import com.nikol.lms_impl.screens.CourseDetailScreen
import com.nikol.lms_impl.screens.CoursesScreen
import com.nikol.lms_impl.viewModels.components.CourseDetailComponentVM
import com.nikol.lms_impl.viewModels.components.CoursesComponentViewModel
import com.nikol.viewmodel.LocalViewModelFactory


@OptIn(ExperimentalMaterial3Api::class)
fun EntryProviderScope<NavKey>.courses(
    onBack: () -> Unit,
    navigateTo: (NavKey) -> Unit
) {
    entry<Courses> {
        val lmsComponent = rememberComponent { CoursesComponentViewModel(it) }
        CompositionLocalProvider(
            LocalViewModelFactory provides lmsComponent.viewModelFactory()
        ) {
            CoursesScreen(navigateTo)
        }
    }
    entry<Course>(
        //потом вернуть на кастом
        metadata = metadata {
//            put(NavDisplay.TransitionKey) {
//                slideInVertically(
//                    initialOffsetY = { it },
//                    animationSpec = tween(400)
//                ) togetherWith fadeOut(tween(300))
//            }
//            put(NavDisplay.PopTransitionKey) {
//                fadeIn(tween(300)) togetherWith slideOutVertically(
//                    targetOffsetY = { it },
//                    animationSpec = tween(400)
//                )
//            }
//            put(NavDisplay.PredictivePopTransitionKey) {
//                fadeIn(tween(300)) togetherWith slideOutVertically(
//                    targetOffsetY = { it },
//                    animationSpec = tween(400)
//                )
//            }
        }
    ) { course ->
        val lmsComponent = rememberComponent { CourseDetailComponentVM(it, course.id) }
        CompositionLocalProvider(
            LocalViewModelFactory provides lmsComponent.viewModelFactory()
        ) {
            CourseDetailScreen(
                name = course.name,
                onBack = onBack,
                navigateTo = navigateTo
            )
        }
    }

    entry<CourseAction>(
        metadata = BottomSheetSceneStrategy.bottomSheet()
    ) { course ->
        val lmsComponent = rememberComponent { CourseDetailComponentVM(it, course.id) }
        CompositionLocalProvider(
            LocalViewModelFactory provides lmsComponent.viewModelFactory()
        ) {
            Text("Это круто)")
        }
    }

    entry<CourseInfo>(
        metadata = BottomSheetSceneStrategy.bottomSheet()
    ) { course ->
        ActionCourseScreen(course.timeChannelUrl, course.sillabusUrl)
    }

    entry<ArchiveCourses> {
        val lmsComponent = rememberComponent { CoursesComponentViewModel(it) }
        CompositionLocalProvider(
            LocalViewModelFactory provides lmsComponent.viewModelFactory()
        ) {
            ArchiveCourseScreen(navigateTo, onBack)
        }
    }
}
