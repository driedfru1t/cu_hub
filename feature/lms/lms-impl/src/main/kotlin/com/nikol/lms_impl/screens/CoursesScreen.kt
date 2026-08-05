package com.nikol.lms_impl.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Archive
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryScrollableTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.NavKey
import com.nikol.designsystem.theme.CUHubTheme
import com.nikol.lms.domain.model.CourseSummary
import com.nikol.lms.domain.model.ParticipationType
import com.nikol.lms_api.ArchiveCourses
import com.nikol.lms_api.Course
import com.nikol.lms_api.CourseAction
import com.nikol.lms_impl.R
import com.nikol.lms_impl.mvi.intent.CoursesIntent
import com.nikol.lms_impl.mvi.state.CoursesState
import com.nikol.lms_impl.viewModels.CoursesRouter
import com.nikol.lms_impl.viewModels.CoursesViewModel
import com.nikol.ui.CourseCard
import com.nikol.ui.CourseSummaryListPreviewProvider
import com.nikol.viewmodel.daggerViewModel
import kotlinx.coroutines.launch

@Composable
fun CoursesScreen(
    navigateTo: (NavKey) -> Unit,
) {
    val viewModel = daggerViewModel<CoursesViewModel, CoursesRouter> {
        object : CoursesRouter {
            override fun toCourse(id: Int, name: String) {
                navigateTo(Course(id, name))
            }

            override fun toMoreInfo(id: Int, name: String) {
                navigateTo(CourseAction(id, name))
            }

            override fun toArchive() = navigateTo(ArchiveCourses)
        }
    }

    val state by viewModel.state.collectAsStateWithLifecycle()
    CoursesScreen(state, viewModel::setIntent)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun CoursesScreen(
    state: CoursesState<CourseSummary>,
    onIntent: (CoursesIntent) -> Unit
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    val filterTypes = remember {
        ParticipationType.entries.toList()
    }

    val pagerState = rememberPagerState(pageCount = { filterTypes.size })
    val coroutineScope = rememberCoroutineScope()

    val showCenterLoader = state.isLoading && state.courses.isEmpty()

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            Column(modifier = Modifier.background(MaterialTheme.colorScheme.surface)) {
                TopAppBar(
                    title = {
                        Text(
                            text = stringResource(R.string.feature_lms_impl_my_courses),
                            fontWeight = FontWeight.SemiBold
                        )
                    },
                    actions = {
                        IconButton(onClick = { onIntent(CoursesIntent.ClickToArchive) }) {
                            Icon(
                                imageVector = Icons.Rounded.Archive,
                                contentDescription = null
                            )
                        }
                    },
                    scrollBehavior = scrollBehavior,
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        scrolledContainerColor = MaterialTheme.colorScheme.surface
                    )
                )
                AnimatedVisibility(
                    visible = state is CoursesState.Loading,
                    modifier = Modifier.fillMaxWidth()
                ) { LinearProgressIndicator() }
                PrimaryScrollableTabRow(
                    selectedTabIndex = pagerState.currentPage,
                    edgePadding = 0.dp,
                    divider = {}
                ) {
                    filterTypes.forEachIndexed { index, filter ->
                        Tab(
                            selected = pagerState.currentPage == index,
                            onClick = {
                                onIntent(CoursesIntent.ChangeTab(filter))
                                coroutineScope.launch {
                                    pagerState.animateScrollToPage(index)
                                }
                            },
                            text = {
                                Text(filter.toUiString())
                            }
                        )
                    }
                }
            }
        },
    ) { paddingValues ->

        if (showCenterLoader) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(modifier = Modifier.size(36.dp), strokeWidth = 3.dp)
            }
        } else {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(top = paddingValues.calculateTopPadding())
            ) { page ->

                val filteredCourses = remember(page, state.courses) {
                    when (val currentFilter = filterTypes[page]) {
                        ParticipationType.ALL -> state.courses
                        ParticipationType.REQUIRED -> state.courses
                            .filter { it.participationType == currentFilter }
                            .sortedBy { it.categoryOrderIndex }

                        ParticipationType.ELECTIVE -> state.courses
                            .filter { it.participationType == currentFilter }
                            .sortedBy { it.categoryOrderIndex }

                        ParticipationType.LISTENER -> state.courses
                            .filter { it.participationType == currentFilter }
                            .sortedBy { it.categoryOrderIndex }

                        ParticipationType.INTERNAL -> state.courses
                            .filter { it.participationType == currentFilter }
                            .sortedBy { it.categoryOrderIndex }
                    }
                }

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(
                        top = 8.dp,
                        start = 16.dp,
                        end = 16.dp,
                        bottom = paddingValues.calculateBottomPadding() + 80.dp
                    )
                ) {
                    items(
                        items = filteredCourses,
                        key = { it.id }
                    ) { course ->
                        CourseCard(
                            courseSummary = course,
                            modifier = Modifier
                                .animateItem(
                                    fadeInSpec = tween(250),
                                    fadeOutSpec = tween(200)
                                ),
                            click = {
                                onIntent(
                                    CoursesIntent.ClickToCourse(
                                        course.id,
                                        course.name
                                    )
                                )
                            },
                            clickToMore = {
                                onIntent(
                                    CoursesIntent.ClickToMore(
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

@Composable
fun ParticipationType.toUiString(): String {
    return when (this) {
        ParticipationType.ALL -> stringResource(R.string.feature_lms_impl_all)
        ParticipationType.REQUIRED -> stringResource(R.string.feature_lms_impl_required)
        ParticipationType.ELECTIVE -> stringResource(R.string.feature_lms_impl_elective)
        ParticipationType.LISTENER -> stringResource(R.string.feature_lms_impl_listener)
        ParticipationType.INTERNAL -> stringResource(R.string.feature_lms_impl_internal)
    }
}

@Preview
@Composable
private fun CoursesScreenPreview(
    @PreviewParameter(CourseSummaryListPreviewProvider::class) courseList: List<CourseSummary>
) {
    val state = remember {
        CoursesState.Success(courseList)
    }
    CUHubTheme {
        CoursesScreen(state, { })
    }
}
