package com.nikol.schedule_impl.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Assignment
import androidx.compose.material.icons.automirrored.rounded.HelpOutline
import androidx.compose.material.icons.automirrored.rounded.MenuBook
import androidx.compose.material.icons.rounded.Apartment
import androidx.compose.material.icons.rounded.Assignment
import androidx.compose.material.icons.rounded.Computer
import androidx.compose.material.icons.rounded.Event
import androidx.compose.material.icons.rounded.HelpOutline
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material.icons.rounded.MeetingRoom
import androidx.compose.material.icons.rounded.MenuBook
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Place
import androidx.compose.material.icons.rounded.School
import androidx.compose.material.icons.rounded.SupportAgent
import androidx.compose.material.icons.rounded.Today
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.NavKey
import com.nikol.calendar.domain.model.Campus
import com.nikol.calendar.domain.model.CampusBuilding
import com.nikol.calendar.domain.model.EventBadgeColor
import com.nikol.calendar.domain.model.EventLocation
import com.nikol.calendar.domain.model.EventType
import com.nikol.calendar.domain.model.ScheduleEvent
import com.nikol.di.ext.rememberComponent
import com.nikol.schedule_api.EventDetail
import com.nikol.schedule_impl.viewModel.ScheduleIntent
import com.nikol.schedule_impl.viewModel.ScheduleRouter
import com.nikol.schedule_impl.viewModel.ScheduleState
import com.nikol.schedule_impl.viewModel.ScheduleVM
import com.nikol.schedule_impl.viewModel.component.ScheduleComponentVM
import com.nikol.ui.getActiveAppLocale
import com.nikol.ui.state.Lce
import com.nikol.viewmodel.LocalViewModelFactory
import com.nikol.viewmodel.daggerViewModel
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.time.temporal.ChronoUnit

@Composable
fun ScheduleScreen(
    onNavigate: (NavKey) -> Unit
) {
    val component = rememberComponent { ScheduleComponentVM(it) }
    CompositionLocalProvider(
        LocalViewModelFactory provides component.viewModelFactory()
    ) {
        val vm = daggerViewModel<ScheduleVM, ScheduleRouter> {
            ScheduleRouter { href, start -> onNavigate(EventDetail(href, start)) }
        }
        val state by vm.state.collectAsStateWithLifecycle()
        ScheduleScreen(state, vm::setIntent)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ScheduleScreen(
    state: ScheduleState,
    onIntent: (ScheduleIntent) -> Unit
) {
    val zoneId = remember { ZoneId.systemDefault() }
    val selectedDate = remember(state.from) { state.from.atZone(zoneId).toLocalDate() }

    val monthFormatter = remember { DateTimeFormatter.ofPattern("LLLL yyyy", getActiveAppLocale()) }
    val topBarTitle = remember(selectedDate) {
        selectedDate.format(monthFormatter).replaceFirstChar { it.uppercase() }
    }

    val coroutineScope = rememberCoroutineScope()
    val baseDate = rememberSaveable { selectedDate }
    val centerPage = remember { 5000 }

    val today = remember { LocalDate.now(zoneId) }
    val todayPage = remember(baseDate, today) {
        centerPage + ChronoUnit.DAYS.between(baseDate, today).toInt()
    }

    val pagerState = rememberPagerState(
        initialPage = todayPage,
        pageCount = { 10000 }
    )

    val isNotToday by remember {
        derivedStateOf { pagerState.currentPage != todayPage }
    }

    LaunchedEffect(pagerState.currentPage) {
        val pageDate = baseDate.plusDays((pagerState.currentPage - centerPage).toLong())
        if (pageDate != selectedDate) {
            val newFrom = pageDate.atStartOfDay(zoneId).toInstant()
            val newTo = pageDate.plusDays(1).atStartOfDay(zoneId).toInstant()
            onIntent(ScheduleIntent.ChangeDateRange(from = newFrom, to = newTo))
        }
    }

    LaunchedEffect(selectedDate) {
        val targetPage = centerPage + ChronoUnit.DAYS.between(baseDate, selectedDate).toInt()
        if (pagerState.currentPage != targetPage) {
            pagerState.animateScrollToPage(targetPage)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = topBarTitle,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.surface
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                WeekDateSelector(
                    selectedDate = selectedDate,
                    onDateClick = { clickedDate ->
                        val targetPage =
                            centerPage + ChronoUnit.DAYS.between(baseDate, clickedDate).toInt()
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(targetPage)
                        }
                    }
                )
                PullToRefreshBox(
                    isRefreshing = state.isLoading,
                    onRefresh = { onIntent(ScheduleIntent.Refresh) },
                    modifier = Modifier.fillMaxSize()
                ) {
                    Column(modifier = Modifier.fillMaxSize()) {


                        Spacer(modifier = Modifier.height(8.dp))

                        HorizontalPager(
                            state = pagerState,
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                        ) { page ->

                            val pageDate = baseDate.plusDays((page - centerPage).toLong())

                            if (pageDate == selectedDate) {
                                LazyColumn(
                                    modifier = Modifier.fillMaxSize(),
                                    contentPadding = PaddingValues(
                                        top = 8.dp,
                                        bottom = 120.dp
                                    ),
                                    verticalArrangement = Arrangement.spacedBy(16.dp)
                                ) {
                                    when (val lce = state.schedule) {
                                        is Lce.Loading -> {
                                            item {
                                                Box(
                                                    modifier = Modifier.fillParentMaxSize(),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    CircularProgressIndicator()
                                                }
                                            }
                                        }

                                        is Lce.Content -> {
                                            if (lce.value.isEmpty()) {
                                                item {
                                                    Box(
                                                        modifier = Modifier.fillParentMaxSize(),
                                                        contentAlignment = Alignment.Center
                                                    ) {
                                                        EmptyScheduleView()
                                                    }
                                                }
                                            } else {
                                                items(
                                                    items = lce.value,
                                                    key = { it.id }
                                                ) { event ->
                                                    ScheduleEventCard(
                                                        event = event,
                                                        zoneId = zoneId,
                                                        onIntent = onIntent
                                                    )
                                                }
                                            }
                                        }

                                        is Lce.Failure -> {
                                            item {
                                                Box(
                                                    modifier = Modifier.fillParentMaxSize(),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Text(
                                                        text = "Ошибка: ${lce.error}",
                                                        color = MaterialTheme.colorScheme.error
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            } else {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator(
                                        color = MaterialTheme.colorScheme.surfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }

            AnimatedVisibility(
                visible = isNotToday,
                enter = fadeIn() + slideInVertically(initialOffsetY = { it / 2 }) + scaleIn(
                    initialScale = 0.8f
                ),
                exit = fadeOut() + slideOutVertically(targetOffsetY = { it / 2 }) + scaleOut(
                    targetScale = 0.8f
                ),
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 75.dp)
                    .navigationBarsPadding()
            ) {
                Surface(
                    onClick = {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(todayPage)
                        }
                    },
                    shape = RoundedCornerShape(percent = 50),
                    color = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Today,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = "Сегодня",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun WeekDateSelector(
    selectedDate: LocalDate,
    onDateClick: (LocalDate) -> Unit,
) {
    val startOfWeek = remember(selectedDate) {
        selectedDate.with(DayOfWeek.MONDAY)
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        for (i in 0..6) {
            val date = startOfWeek.plusDays((i).toLong())
            val isSelected = date == selectedDate

            val dayOfWeekStr =
                date.dayOfWeek.getDisplayName(TextStyle.SHORT, getActiveAppLocale()).uppercase()
            val dayOfMonthStr = date.dayOfMonth.toString()

            Column(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent)
                    .clickable { onDateClick(date) }
                    .padding(vertical = 12.dp, horizontal = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = dayOfWeekStr,
                    style = MaterialTheme.typography.labelMedium,
                    color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = dayOfMonthStr,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

@Composable
fun EmptyScheduleView(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Rounded.Event,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.surfaceVariant
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "На этот день пар нет!",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = "Можно отдыхать (или делать домашку)",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
        )
    }
}

@Composable
fun ScheduleEventCard(
    event: ScheduleEvent,
    zoneId: ZoneId,
    onIntent: (ScheduleIntent) -> Unit,
    modifier: Modifier = Modifier
) {
    val timeFormatter = remember { DateTimeFormatter.ofPattern("HH:mm") }
    val startTime =
        remember(event.start, zoneId) { event.start.atZone(zoneId).format(timeFormatter) }
    val endTime = remember(event.end, zoneId) { event.end.atZone(zoneId).format(timeFormatter) }

    val (iconBg, iconTint, typeIcon) = when (event.eventType) {
        EventType.LECTURE -> Triple(
            MaterialTheme.colorScheme.primaryContainer,
            MaterialTheme.colorScheme.onPrimaryContainer,
            Icons.AutoMirrored.Rounded.MenuBook
        )

        EventType.SEMINAR -> Triple(
            MaterialTheme.colorScheme.secondaryContainer,
            MaterialTheme.colorScheme.onSecondaryContainer,
            Icons.Rounded.School
        )

        EventType.EXAM, EventType.TEST, EventType.CREDIT, EventType.COLLOQUIUM -> Triple(
            MaterialTheme.colorScheme.errorContainer,
            MaterialTheme.colorScheme.onErrorContainer,
            Icons.AutoMirrored.Rounded.Assignment
        )

        EventType.OFFICE_HOURS -> Triple(
            MaterialTheme.colorScheme.tertiaryContainer,
            MaterialTheme.colorScheme.onTertiaryContainer,
            Icons.Rounded.SupportAgent
        )

        else -> Triple(
            MaterialTheme.colorScheme.surfaceVariant,
            MaterialTheme.colorScheme.onSurfaceVariant,
            Icons.Rounded.Event
        )
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
    ) {
        Column(
            modifier = Modifier.width(52.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = startTime,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = endTime,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    onIntent(
                        ScheduleIntent.Detail(
                            event.id,
                            event.start.toEpochMilli()
                        )
                    )
                },
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f, fill = false)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(iconBg),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = typeIcon,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = iconTint
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = event.customTypeLabel ?: event.eventType.displayName,
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.SemiBold,
                            color = iconTint,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    if (event.isRetakeWeek) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = MaterialTheme.colorScheme.errorContainer
                        ) {
                            Text(
                                text = "Пересдача",
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onErrorContainer
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = event.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

//                if (!event.description.isNullOrBlank()) {
//                    Spacer(modifier = Modifier.height(4.dp))
//                    Text(
//                        text = event.description!!,
//                        style = MaterialTheme.typography.bodySmall,
//                        color = MaterialTheme.colorScheme.onSurfaceVariant,
//                        maxLines = 2,
//                        overflow = TextOverflow.Ellipsis
//                    )
//                }

                Spacer(modifier = Modifier.height(12.dp))

                EventLocationInfo(location = event.eventLocation)
            }
        }
    }
}

private fun Campus.toUi(): String {
    return when (this) {
        Campus.CENTRAL_TELEGRAPH -> "ЦТ"
        Campus.DUKAT -> "Дукат"
        Campus.UNKNOWN -> "Неизвестно"
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun EventLocationInfo(
    location: EventLocation,
    modifier: Modifier = Modifier
) {
    FlowRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        when (location) {
            is EventLocation.University -> {
                val campusBuildingText = buildString {
                    if (location.campus != Campus.UNKNOWN) {
                        append(location.campus.toUi())
                    }
                    if (location.building != CampusBuilding.UNKNOWN) {
                        if (isNotEmpty()) append(" • ")
                        append(location.building.displayName)
                    }
                }

                if (campusBuildingText.isNotEmpty()) {
                    InfoBadge(
                        icon = Icons.Rounded.Apartment,
                        text = campusBuildingText,
                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                        contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                val roomsText = buildString {
                    if (location.rooms.isNotEmpty()) {
                        append(location.rooms.joinToString(", "))
                    } else {
                        append("Ауд. не указана")
                    }
                    if (location.floor != null) {
                        append(" (${location.floor} эт.)")
                    }
                }

                InfoBadge(
                    icon = Icons.Rounded.MeetingRoom,
                    text = roomsText,
                    containerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            is EventLocation.Online -> {
                InfoBadge(
                    icon = Icons.Rounded.Computer,
                    text = "Онлайн",
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                    contentColor = MaterialTheme.colorScheme.onTertiaryContainer
                )
            }

            is EventLocation.External -> {
                InfoBadge(
                    icon = Icons.Rounded.Place,
                    text = location.description,
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            EventLocation.Unknown -> {
                InfoBadge(
                    icon = Icons.AutoMirrored.Rounded.HelpOutline,
                    text = "Место не указано",
                    containerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )
            }
        }
    }
}

@Composable
private fun InfoBadge(
    icon: ImageVector,
    text: String,
    containerColor: Color,
    contentColor: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        color = containerColor
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(14.dp),
                tint = contentColor
            )
            Spacer(modifier = Modifier.width(5.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Medium,
                color = contentColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}