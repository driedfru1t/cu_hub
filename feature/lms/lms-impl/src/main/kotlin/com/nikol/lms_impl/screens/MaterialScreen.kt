package com.nikol.lms_impl.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.currentComposer
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.nikol.lms.backround.openDownloadedFile
import com.nikol.lms.backround.startSingleFileWork
import com.nikol.lms.data.local.entity.Longread
import com.nikol.lms.ui.RenderHtmlCompose
import com.nikol.lms_api.TaskDetail
import com.nikol.lms_impl.mvi.intent.CourseArchiveIntent
import com.nikol.lms_impl.mvi.intent.ThemeMaterialIntent
import com.nikol.lms_impl.mvi.state.ThemeMaterialState
import com.nikol.lms_impl.viewModels.MaterialRouter
import com.nikol.lms_impl.viewModels.MaterialVM
import com.nikol.lms_impl.viewModels.ThemeMaterialEffect
import com.nikol.ui.state.Lce
import com.nikol.viewmodel.LocalViewModelFactory
import com.nikol.viewmodel.daggerViewModel

@Composable
fun MaterialScreen(
    onBack: () -> Unit,
    onTask: (TaskDetail) -> Unit,
) {
    val vm = daggerViewModel<MaterialVM, MaterialRouter> {
        object : MaterialRouter {
            override fun onBack() = onBack()
        }
    }
    val context = LocalContext.current
    LaunchedEffect(vm) {
        vm.effect.collect { effect ->
            when (effect) {
                ThemeMaterialEffect.StartWorkManager -> startSingleFileWork(context)
                is ThemeMaterialEffect.OpenFile -> openDownloadedFile(
                    context,
                    effect.uri,
                    effect.mimeType
                )
            }
        }
    }
    val state by vm.state.collectAsStateWithLifecycle()
    MaterialScreen(state, vm::setIntent)
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
private fun MaterialScreen(
    state: ThemeMaterialState,
    onIntent: (ThemeMaterialIntent) -> Unit,
) {
    val pagerState = rememberPagerState(
        initialPage = state.currentIndex,
        pageCount = { state.longi.size }
    )

    LaunchedEffect(state.currentIndex) {
        if (pagerState.currentPage != state.currentIndex) {
            pagerState.animateScrollToPage(state.currentIndex)
        }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize(),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = state.currentName,
                        maxLines = 1,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            onIntent(ThemeMaterialIntent.OnBack)
                        }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = "Назад"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    scrolledContainerColor = MaterialTheme.colorScheme.surface
                ),
            )
        },
        bottomBar = {
            Box(
                modifier = Modifier.fillMaxWidth()
            ) {
                MaterialNavigationBottomBar(
                    onPreviousClick = {
                        onIntent(ThemeMaterialIntent.ClickLeft)
                    },
                    onNextClick = {
                        onIntent(ThemeMaterialIntent.ClickRight)
                    },
                    isPreviousEnabled = state.currentIndex != 0,
                    isNextEnabled = state.currentIndex != state.longi.lastIndex,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        },
        containerColor = MaterialTheme.colorScheme.surface
    ) { paddingValues ->

        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize(),
            userScrollEnabled = false
        ) {
            MaterialPage(
                state = state,
                onIntent = onIntent,
                contentPadding = paddingValues
            )
        }
    }
}

@Composable
private fun MaterialPage(
    state: ThemeMaterialState,
    onIntent: (ThemeMaterialIntent) -> Unit,
    contentPadding: PaddingValues,
) {
    when (val materialState = state.material) {

        is Lce.Loading -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(contentPadding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.primary,
                    strokeWidth = 4.dp
                )
            }
        }

        is Lce.Failure -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(contentPadding),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ErrorOutline,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(48.dp)
                    )

                    Spacer(Modifier.height(12.dp))

                    Text(
                        text = "Не удалось загрузить материалы",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Center
                    )

                    Spacer(Modifier.height(4.dp))

                    Text(
                        text = "Пожалуйста, попробуйте позже.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        is Lce.Content -> {
            val feedItems = materialState.value.list

            if (feedItems.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(contentPadding),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Здесь пока ничего нет",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                val layoutDirection = LocalLayoutDirection.current
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(
                        start = 16.dp + contentPadding.calculateStartPadding(layoutDirection),
                        end = 16.dp + contentPadding.calculateEndPadding(layoutDirection),
                        top = contentPadding.calculateTopPadding(),
                        bottom = contentPadding.calculateBottomPadding() + 8.dp
                    ),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(
                        items = feedItems,
                        key = { it.id }
                    ) { item ->
                        item.RenderHtmlCompose(
                            files = materialState.value.files
                        ) { filename, version ->
                            onIntent(
                                ThemeMaterialIntent.ClickToFile(
                                    name = filename,
                                    version = version
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MaterialNavigationBottomBar(
    onPreviousClick: () -> Unit,
    onNextClick: () -> Unit,
    modifier: Modifier = Modifier,
    isPreviousEnabled: Boolean = true,
    isNextEnabled: Boolean = true
) {
    Surface(
        modifier = modifier
            .navigationBarsPadding()
            .padding(bottom = 16.dp),
        shape = CircleShape,
        color = MaterialTheme.colorScheme.surfaceContainerHigh,
        tonalElevation = 6.dp,
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            FilledTonalIconButton(
                onClick = onPreviousClick,
                enabled = isPreviousEnabled,
                modifier = Modifier.size(48.dp),
                shape = CircleShape
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Предыдущий материал"
                )
            }

            VerticalDivider(
                modifier = Modifier.height(20.dp),
                color = MaterialTheme.colorScheme.outlineVariant
            )

            FilledTonalIconButton(
                onClick = onNextClick,
                enabled = isNextEnabled,
                modifier = Modifier.size(48.dp),
                shape = CircleShape
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = "Следующий материал"
                )
            }
        }
    }
}
