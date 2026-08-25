package com.nikol.lms_impl.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.NavKey
import com.nikol.lms_api.CourseInfo
import com.nikol.lms_api.ThemeMaterial
import com.nikol.lms_impl.mvi.intent.CourseDetailIntent
import com.nikol.lms_impl.mvi.state.CourseDetailState
import com.nikol.lms_impl.mvi.state.CourseTab
import com.nikol.lms_impl.viewModels.courseDeatil.CourseDetailRouter
import com.nikol.lms_impl.viewModels.courseDeatil.CourseDetailVM
import com.nikol.lms_impl.viewModels.courseDeatil.CourseGradesVM
import com.nikol.lms_impl.viewModels.courseDeatil.CourseMaterialsR
import com.nikol.lms_impl.viewModels.courseDeatil.CourseMaterialsVM
import com.nikol.ui.state.Lce
import com.nikol.viewmodel.Router
import com.nikol.viewmodel.daggerViewModel
import kotlinx.coroutines.launch

@Composable
fun CourseDetailScreen(
    name: String,
    onBack: () -> Unit,
    navigateTo: (NavKey) -> Unit,
    navigateToRoot: (NavKey) -> Unit
) {
    val viewModel = daggerViewModel<CourseDetailVM, CourseDetailRouter> {
        object : CourseDetailRouter {
            override fun onBack() = onBack()
            override fun onInfo(time: String?, sillabus: String?) =
                navigateTo(CourseInfo(sillabus, time))
        }
    }
    val state by viewModel.state.collectAsStateWithLifecycle()
    CourseDetailScreen(
        state = state,
        name = name,
        onIntent = viewModel::setIntent,
        onMaterialDetail = navigateToRoot
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
private fun CourseDetailScreen(
    state: CourseDetailState,
    name: String,
    onIntent: (CourseDetailIntent) -> Unit,
    onMaterialDetail: (ThemeMaterial) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()

    val materialsVM = daggerViewModel<CourseMaterialsVM, CourseMaterialsR> {
        CourseMaterialsR { p1, p2 ->
            onMaterialDetail(ThemeMaterial(p1, p2))
        }
    }
    val gradesVM = daggerViewModel<CourseGradesVM, Router> {
        object : Router {}
    }

    val materialsListState = rememberLazyListState()
    val gradesListState = rememberLazyListState()

    val scrollBehavior =
        TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())

    var expandedTitleHeightPx by remember { mutableFloatStateOf(0f) }
    val density = LocalDensity.current

    LaunchedEffect(expandedTitleHeightPx) {
        if (expandedTitleHeightPx > 0f) {
            scrollBehavior.state.heightOffsetLimit = -expandedTitleHeightPx
        }
    }

    LaunchedEffect(name) {
        expandedTitleHeightPx = 0f
    }

    val pagerState = rememberPagerState(
        initialPage = when (state.currentTab) {
            CourseTab.Materials -> 0
            CourseTab.Grades -> 1
        },
        pageCount = { 2 }
    )

    LaunchedEffect(pagerState.currentPage) {
        val targetTab = when (pagerState.currentPage) {
            0 -> CourseTab.Materials
            1 -> CourseTab.Grades
            else -> CourseTab.Materials
        }
        if (state.currentTab != targetTab) {
            onIntent(CourseDetailIntent.SwitchTab(targetTab))
        }
    }

    LaunchedEffect(state.currentTab) {
        val targetPage = when (state.currentTab) {
            CourseTab.Materials -> 0
            CourseTab.Grades -> 1
        }
        if (pagerState.currentPage != targetPage) {
            pagerState.animateScrollToPage(targetPage)
        }
    }

    val materialsState by materialsVM.state.collectAsStateWithLifecycle()

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        contentColor = MaterialTheme.colorScheme.surface,
        topBar = {
            Surface(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    TopAppBar(
                        title = {
                            Text(
                                text = name,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier.graphicsLayer {
                                    val collapsedFraction = scrollBehavior.state.collapsedFraction
                                    alpha = ((collapsedFraction - 0.5f) * 2f).coerceIn(0f, 1f)
                                    translationY = (1f - collapsedFraction) * 24.dp.toPx()
                                }
                            )
                        },
                        navigationIcon = {
                            IconButton(onClick = { onIntent(CourseDetailIntent.Back) }) {
                                Icon(
                                    Icons.AutoMirrored.Rounded.ArrowBack,
                                    contentDescription = null
                                )
                            }
                        },
                        actions = {
                            IconButton(onClick = {
                                (materialsState as? Lce.Content)?.let {
                                    onIntent(
                                        CourseDetailIntent.Info(
                                            sillabusUrl = it.value.materials.settings.syllabusUrl,
                                            timeChannelUrl = it.value.materials.settings.timeChannelUrl
                                        )
                                    )
                                }
                            }) {
                                Icon(Icons.Rounded.Info, contentDescription = null)
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = MaterialTheme.colorScheme.surface,
                            scrolledContainerColor = MaterialTheme.colorScheme.surface
                        )
                    )

                    val heightOffset = scrollBehavior.state.heightOffset
                    val expandedBoxModifier = Modifier
                        .fillMaxWidth()
                        .clipToBounds()
                        .then(
                            if (expandedTitleHeightPx == 0f) {
                                Modifier.onGloballyPositioned { coordinates ->
                                    expandedTitleHeightPx = coordinates.size.height.toFloat()
                                }
                            } else {
                                Modifier.height(with(density) {
                                    (expandedTitleHeightPx + heightOffset).coerceAtLeast(0f).toDp()
                                })
                            }
                        )

                    Box(
                        modifier = expandedBoxModifier,
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = name,
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier
                                .padding(horizontal = 16.dp)
                                .padding(bottom = 16.dp)
                                .graphicsLayer {
                                    val fraction = scrollBehavior.state.collapsedFraction
                                    alpha = (1f - fraction * 2f).coerceIn(0f, 1f)
                                    translationY = -(fraction * 24.dp.toPx())
                                }
                        )
                    }

                    PrimaryTabRow(
                        selectedTabIndex = pagerState.currentPage,
                        containerColor = MaterialTheme.colorScheme.surface,
                        divider = {}
                    ) {
                        Tab(
                            selected = pagerState.currentPage == 0,
                            onClick = {
                                coroutineScope.launch { pagerState.animateScrollToPage(0) }
                            },
                            text = { Text("Темы") },
                        )
                        Tab(
                            selected = pagerState.currentPage == 1,
                            onClick = {
                                coroutineScope.launch { pagerState.animateScrollToPage(1) }
                            },
                            text = { Text("Оценки") },
                        )
                    }
                }
            }
        },
    ) { paddingValues ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = paddingValues.calculateTopPadding()),
            color = MaterialTheme.colorScheme.surface
        ) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize()
            ) { page ->
                when (page) {
                    0 -> {
                        CourseMaterialsPage(
                            state = materialsState,
                            listState = materialsListState,
                            paddingValues = paddingValues,
                            onIntent = materialsVM::setIntent
                        )
                    }

                    1 -> {
                        val gradesState by gradesVM.state.collectAsStateWithLifecycle()
                        CourseGradesPage(
                            state = gradesState,
                            listState = gradesListState,
                            paddingValues = paddingValues,
                            onIntent = gradesVM::setIntent
                        )
                    }
                }
            }
        }
    }
}