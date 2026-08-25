package com.nikol.lms_impl.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Archive
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.NavKey
import com.nikol.lms_api.Course
import com.nikol.lms_api.CourseAction
import com.nikol.lms_impl.mvi.intent.CourseArchiveIntent
import com.nikol.lms_impl.viewModels.ArchiveCourseState
import com.nikol.lms_impl.viewModels.ArchiveCourseVM
import com.nikol.lms_impl.viewModels.ArchiveRouter
import com.nikol.ui.CourseCard
import com.nikol.ui.state.Lce
import com.nikol.viewmodel.daggerViewModel

@Composable
fun ArchiveCourseScreen(
    navigateTo: (NavKey) -> Unit,
    onBack: () -> Unit
) {
    val vm = daggerViewModel<ArchiveCourseVM, ArchiveRouter> {
        object : ArchiveRouter {
            override fun onBack() = onBack()
            override fun onDetail(id: Int, name: String) = navigateTo(Course(id, name))
            override fun onCourseAction(id: Int, name: String) =
                navigateTo(CourseAction(id, name))
        }
    }

    val state by vm.state.collectAsStateWithLifecycle()
    ArchiveCourseScreen(state = state, onIntent = vm::setIntent)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ArchiveCourseScreen(
    state: ArchiveCourseState,
    onIntent: (CourseArchiveIntent) -> Unit
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Архивные",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { onIntent(CourseArchiveIntent.OnBack) }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = "Назад"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.surface
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = paddingValues.calculateTopPadding())
        ) {
            when (state) {
                is Lce.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                is Lce.Failure -> {
                    Text(
                        text = "Ошибка загрузки: ${state.error}",
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                is Lce.Content -> {
                    val courses = state.value

                    if (courses.isEmpty()) {
                        Column(
                            modifier = Modifier.align(Alignment.Center),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Archive,
                                contentDescription = null,
                                modifier = Modifier.size(64.dp),
                                tint = MaterialTheme.colorScheme.surfaceVariant
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Архив пуст",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(
                                start = 16.dp,
                                end = 16.dp,
                                top = 8.dp,
                                bottom = 80.dp + paddingValues.calculateBottomPadding()
                            ),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            items(items = courses, key = { it.id }) { course ->
                                CourseCard(
                                    courseSummary = course,
                                    click = {
                                        onIntent(
                                            CourseArchiveIntent.OnMaterial(
                                                course.id,
                                                course.name
                                            )
                                        )
                                    },
                                    clickToMore = {
                                        onIntent(
                                            CourseArchiveIntent.OnCourseAction(
                                                course.id,
                                                course.name
                                            )
                                        )
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}