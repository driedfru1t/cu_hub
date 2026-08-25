package com.nikol.lms_impl.nav

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.nikol.di.ext.rememberComponent
import com.nikol.lms_api.TaskDetail
import com.nikol.lms_api.Tasks
import com.nikol.lms_impl.screens.TasksScreen
import com.nikol.lms_impl.viewModels.TaskDetailVM
import com.nikol.lms_impl.viewModels.components.TaskComponentVM
import com.nikol.viewmodel.LocalViewModelFactory
import com.nikol.viewmodel.daggerViewModel

fun EntryProviderScope<NavKey>.task(
    onBack: () -> Unit,
    navigateTo: (NavKey) -> Unit
) {
    entry<Tasks> {
        val taskComponent = rememberComponent { TaskComponentVM(it) }
        CompositionLocalProvider(
            LocalViewModelFactory provides taskComponent.viewModelFactory()
        ) {
            TasksScreen(onBack, navigateTo)
        }
    }

    entry<TaskDetail> {
        val taskComponent = rememberComponent { dep -> TaskComponentVM(dep) }
        val factory = remember(taskComponent) {
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return taskComponent.factoryTaskDetailVm().create(it.id) as T
                }
            }
        }
        CompositionLocalProvider(
            LocalViewModelFactory provides factory
        ) {
        }
    }
}