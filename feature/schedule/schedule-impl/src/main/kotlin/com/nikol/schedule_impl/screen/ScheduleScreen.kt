package com.nikol.schedule_impl.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Computer
import androidx.compose.material.icons.rounded.Event
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.School
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nikol.calendar.domain.model.EventBadgeColor
import com.nikol.calendar.domain.model.EventType
import com.nikol.calendar.domain.model.ScheduleEvent
import com.nikol.di.ext.rememberComponent
import com.nikol.schedule_impl.viewModel.ScheduleIntent
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
fun ScheduleScreen() {
    val component = rememberComponent { ScheduleComponentVM(it) }
    CompositionLocalProvider(
        LocalViewModelFactory provides component.viewModelFactory()
    ) {
        val vm = daggerViewModel<ScheduleVM>()
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
    val centerPage = 5000

    val pagerState = rememberPagerState(
        initialPage = centerPage + ChronoUnit.DAYS.between(baseDate, selectedDate).toInt(),
        pageCount = { 10000 }
    )

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
                actions = {
                    IconButton({ onIntent(ScheduleIntent.Refresh) }) {
                        Icon(Icons.Rounded.Refresh, contentDescription = "Обновить")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.surface
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            WeekDateSelector(
                selectedDate = selectedDate,
                onDateClick = { clickedDate ->
                    val targetPage = centerPage + ChronoUnit.DAYS.between(baseDate, clickedDate).toInt()
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(targetPage)
                    }
                }
            )

            Spacer(modifier = Modifier.height(8.dp))

            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxWidth().weight(1f),
                verticalAlignment = Alignment.Top
            ) { page ->
                val pageDate = baseDate.plusDays((page - centerPage).toLong())

                if (pageDate == selectedDate) {
                    when (val lce = state.schedule) {
                        is Lce.Loading -> {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator()
                            }
                        }
                        is Lce.Failure -> {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Text(
                                    "Ошибка: ${lce.error}",
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                        is Lce.Content -> {
                            val events = lce.value
                            if (events.isEmpty()) {
                                EmptyScheduleView()
                            } else {
                                LazyColumn(
                                    contentPadding = PaddingValues(bottom = 80.dp, top = 8.dp),
                                    verticalArrangement = Arrangement.spacedBy(16.dp)
                                ) {
                                    items(items = events, key = { it.id }) { event ->
                                        ScheduleEventCard(event = event, zoneId = zoneId)
                                    }
                                }
                            }
                        }
                    }
                } else {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.surfaceVariant)
                    }
                }
            }
        }
    }
}

@Composable
fun WeekDateSelector(
    selectedDate: LocalDate,
    onDateClick: (LocalDate) -> Unit
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
            val date = startOfWeek.plusDays(i.toLong())
            val isSelected = date == selectedDate

            val dayOfWeekStr = date.dayOfWeek.getDisplayName(TextStyle.SHORT, getActiveAppLocale()).uppercase()
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
fun EmptyScheduleView() {
    Column(
        modifier = Modifier.fillMaxSize(),
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
    zoneId: ZoneId
) {
    val timeFormatter = remember { DateTimeFormatter.ofPattern("HH:mm") }
    val startTime = remember(event.start) { event.start.atZone(zoneId).format(timeFormatter) }
    val endTime = remember(event.end) { event.end.atZone(zoneId).format(timeFormatter) }

    val (iconBg, iconTint) = when (event.eventType) {
        EventType.LECTURE -> MaterialTheme.colorScheme.primaryContainer to MaterialTheme.colorScheme.onPrimaryContainer
        EventType.SEMINAR -> MaterialTheme.colorScheme.secondaryContainer to MaterialTheme.colorScheme.onSecondaryContainer
        EventType.EXAM, EventType.TEST, EventType.CREDIT -> MaterialTheme.colorScheme.errorContainer to MaterialTheme.colorScheme.onErrorContainer
        else -> MaterialTheme.colorScheme.surfaceVariant to MaterialTheme.colorScheme.onSurfaceVariant
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Column(
            modifier = Modifier.width(56.dp),
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
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(iconBg),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.School,
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
                            color = iconTint
                        )
                    }

                    if (event.badges.isNotEmpty()) {
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            event.badges.forEach { badge ->
                                val color = when (badge) {
                                    EventBadgeColor.BLUE -> Color(0xFF3B82F6)
                                    EventBadgeColor.RED -> Color(0xFFEF4444)
                                    EventBadgeColor.BLACK -> Color(0xFF1F2937)
                                }
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(color)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = event.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (event.roomInfo != null) {
                        val isOnline = event.roomInfo!!.isOnline
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (isOnline) MaterialTheme.colorScheme.tertiaryContainer
                            else MaterialTheme.colorScheme.surfaceContainerHighest
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = if (isOnline) Icons.Rounded.Computer else Icons.Rounded.LocationOn,
                                    contentDescription = null,
                                    modifier = Modifier.size(14.dp),
                                    tint = if (isOnline) MaterialTheme.colorScheme.onTertiaryContainer
                                    else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (isOnline) "Online" else event.roomInfo!!.rooms.joinToString(),
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Medium,
                                    color = if (isOnline) MaterialTheme.colorScheme.onTertiaryContainer
                                    else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    if (!event.teacherName.isNullOrBlank()) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Rounded.Person,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = event.teacherName!!,
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}