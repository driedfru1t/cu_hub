package com.nikol.lms_impl.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Assignment
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.rememberBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.NavKey
import com.nikol.lms.domain.model.TaskState
import com.nikol.lms_api.TaskDetail
import com.nikol.lms_impl.R
import com.nikol.lms_impl.viewModels.DayTasksUi
import com.nikol.lms_impl.viewModels.TasksIntent
import com.nikol.lms_impl.viewModels.TasksRouter
import com.nikol.lms_impl.viewModels.TasksState
import com.nikol.lms_impl.viewModels.TasksVM
import com.nikol.ui.TaskCard
import com.nikol.ui.getTaskStateConfig
import com.nikol.ui.state.Lce
import com.nikol.viewmodel.daggerViewModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableSet
import kotlinx.coroutines.launch

@Composable
fun TasksScreen(
    onBack: () -> Unit,
    navigateTo: (NavKey) -> Unit
) {
    val vm = daggerViewModel<TasksVM, TasksRouter> {
        object : TasksRouter {
            override fun onBack() = onBack()
            override fun onTask(id: Int) = navigateTo(TaskDetail(id))
        }
    }

    val state by vm.state.collectAsStateWithLifecycle()
    TasksScreen(
        state = state,
        onIntent = vm::setIntent,
        getWeekTitle = vm::getWeekTitle
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TasksScreen(
    state: TasksState,
    onIntent: (TasksIntent) -> Unit,
    getWeekTitle: (Int) -> String
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Задания",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                },
                actions = {
                    IconButton(onClick = { onIntent(TasksIntent.OpenFilter) }) {
                        BadgedBox(
                            badge = {
                                if (state.taskSate.isNotEmpty()) {
                                    Badge {
                                        Text(state.taskSate.size.toString())
                                    }
                                }
                            }
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.tune),
                                contentDescription = "Фильтры"
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.surface
    ) { paddingValues ->
        PullToRefreshBox(
            isRefreshing = state.isRefresh,
            onRefresh = { onIntent(TasksIntent.Refresh) },
            modifier = Modifier
                .fillMaxSize()
                .padding(top = paddingValues.calculateTopPadding())
        ) {
            when (val lce = state.weeksMap) {
                is Lce.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }

                is Lce.Failure -> {
                    Text(
                        text = "Ошибка загрузки: ${lce.error}",
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                is Lce.Content -> {
                    val weeksMap = lce.value

                    val pagerState = rememberPagerState(
                        initialPage = TasksVM.INITIAL_PAGE,
                        pageCount = { TasksVM.TOTAL_PAGES }
                    )

                    val currentOffset by remember {
                        derivedStateOf { pagerState.currentPage - TasksVM.INITIAL_PAGE }
                    }

                    LaunchedEffect(currentOffset, state.windowOffset) {
                        val diff = currentOffset - state.windowOffset
                        when {
                            diff >= 3 -> onIntent(TasksIntent.ShiftWindow(+1))
                            diff <= -3 -> onIntent(TasksIntent.ShiftWindow(-1))
                        }
                    }

                    val coroutineScope = rememberCoroutineScope()

                    Column(modifier = Modifier.fillMaxSize()) {
                        val currentWeekTitle by remember {
                            derivedStateOf {
                                weeksMap[currentOffset]?.weekRangeStr ?: getWeekTitle(currentOffset)
                            }
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp, vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(
                                onClick = {
                                    coroutineScope.launch {
                                        pagerState.animateScrollToPage(pagerState.currentPage - 1)
                                    }
                                },
                                enabled = pagerState.currentPage > 0
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.chevron_left),
                                    contentDescription = "Предыдущая неделя"
                                )
                            }

                            Text(
                                text = currentWeekTitle,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            IconButton(
                                onClick = {
                                    coroutineScope.launch {
                                        pagerState.animateScrollToPage(pagerState.currentPage + 1)
                                    }
                                },
                                enabled = pagerState.currentPage < TasksVM.TOTAL_PAGES - 1
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.chevron_right),
                                    contentDescription = "Следующая неделя"
                                )
                            }
                        }

                        HorizontalPager(
                            state = pagerState,
                            modifier = Modifier.fillMaxSize(),
                            beyondViewportPageCount = 1,
                            key = { page -> page }
                        ) { page ->
                            val pageOffset = page - TasksVM.INITIAL_PAGE
                            val weekData = weeksMap[pageOffset]

                            when {
                                weekData == null -> {
                                    Box(
                                        modifier = Modifier.fillMaxSize(),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        CircularProgressIndicator()
                                    }
                                }

                                weekData.days.isEmpty() -> {
                                    EmptyTasksView()
                                }

                                else -> {
                                    TasksListForWeek(
                                        days = weekData.days,
                                        paddingValues = paddingValues
                                    ) { taskId ->
                                        onIntent(TasksIntent.OnTaskClick(taskId))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (state.showBottomSheet) {
        TasksFilterBottomSheet(
            selectedStates = state.taskSate,
            onDismiss = { onIntent(TasksIntent.CloseFilter) },
            onApply = { newStates ->
                onIntent(TasksIntent.SetTaskStates(newStates.toImmutableSet()))
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TasksFilterBottomSheet(
    selectedStates: Set<TaskState>,
    onDismiss: () -> Unit,
    onApply: (Set<TaskState>) -> Unit
) {
    val sheetState = rememberBottomSheetState(
        initialValue = SheetValue.Hidden,
        enabledValues = setOf(SheetValue.Hidden, SheetValue.Expanded)
    )

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surfaceContainerLow
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 48.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Фильтры",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                TextButton(
                    onClick = { onApply(emptySet()) },
                    enabled = selectedStates.isNotEmpty()
                ) {
                    Text(
                        text = "Сбросить",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }

            val states = TaskState.entries

            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                for (i in states.indices step 2) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        ModernMorphingTile(
                            taskState = states[i],
                            isSelected = selectedStates.contains(states[i]),
                            modifier = Modifier.weight(1f),
                            onClick = {
                                val newSet = if (selectedStates.contains(states[i])) {
                                    selectedStates - states[i]
                                } else {
                                    selectedStates + states[i]
                                }
                                onApply(newSet)
                            }
                        )

                        if (i + 1 < states.size) {
                            ModernMorphingTile(
                                taskState = states[i + 1],
                                isSelected = selectedStates.contains(states[i + 1]),
                                modifier = Modifier.weight(1f),
                                onClick = {
                                    val newSet = if (selectedStates.contains(states[i + 1])) {
                                        selectedStates - states[i + 1]
                                    } else {
                                        selectedStates + states[i + 1]
                                    }
                                    onApply(newSet)
                                }
                            )
                        } else {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ModernMorphingTile(
    taskState: TaskState,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val config = getTaskStateConfig(taskState)

    val cornerRadius by animateDpAsState(
        targetValue = if (isSelected) 16.dp else 100.dp,
        label = "shape_radius"
    )

    val bgColor by animateColorAsState(
        targetValue = if (isSelected) config.containerColor else MaterialTheme.colorScheme.surfaceContainerHigh,
        label = "bg_color"
    )
    val textColor by animateColorAsState(
        targetValue = if (isSelected) config.contentColor else MaterialTheme.colorScheme.onSurface,
        label = "text_color"
    )
    val iconBgColor by animateColorAsState(
        targetValue = if (isSelected) Color.Transparent else config.containerColor,
        label = "icon_bg_color"
    )

    Surface(
        onClick = onClick,
        modifier = modifier.height(64.dp),
        shape = RoundedCornerShape(cornerRadius),
        color = bgColor
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(iconBgColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = config.icon,
                    contentDescription = null,
                    tint = config.contentColor,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = config.label,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = textColor,
                maxLines = 2,
                lineHeight = 16.sp,
                modifier = Modifier.padding(end = 4.dp)
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun TasksListForWeek(
    days: ImmutableList<DayTasksUi>,
    paddingValues: PaddingValues,
    onTaskClick: (Int) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 80.dp + paddingValues.calculateBottomPadding())
    ) {
        days.forEach { day ->
            stickyHeader(key = day.dateEpoch) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .animateItem()
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    Text(
                        text = day.dayHeader,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    HorizontalDivider(
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                        thickness = 2.dp
                    )
                }
            }

            items(items = day.tasks, key = { it.id }) { task ->
                TaskCard(
                    task = task,
                    modifier = Modifier
                        .animateItem()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    onClick = { onTaskClick(task.id) }
                )
            }
        }
    }
}

@Composable
fun EmptyTasksView() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Rounded.Assignment,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.surfaceVariant
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Нет активных заданий",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = "Можно отдыхать!",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
        )
    }
}