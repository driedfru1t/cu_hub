package com.nikol.schedule_impl.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Notes
import androidx.compose.material.icons.automirrored.rounded.OpenInNew
import androidx.compose.material.icons.rounded.AccessTime
import androidx.compose.material.icons.rounded.Apartment
import androidx.compose.material.icons.rounded.ErrorOutline
import androidx.compose.material.icons.rounded.Language
import androidx.compose.material.icons.rounded.Link
import androidx.compose.material.icons.rounded.LocationOff
import androidx.compose.material.icons.rounded.Notes
import androidx.compose.material.icons.rounded.OpenInNew
import androidx.compose.material.icons.rounded.Place
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.WarningAmber
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withLink
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nikol.calendar.domain.model.Campus
import com.nikol.calendar.domain.model.CampusBuilding
import com.nikol.calendar.domain.model.EventLocation
import com.nikol.calendar.domain.model.EventType
import com.nikol.calendar.domain.model.ScheduleEvent
import com.nikol.schedule_impl.viewModel.EventDetailIntent
import com.nikol.schedule_impl.viewModel.EventDetailRouter
import com.nikol.schedule_impl.viewModel.EventDetailState
import com.nikol.schedule_impl.viewModel.EventDetailVM
import com.nikol.ui.getActiveAppLocale
import com.nikol.ui.state.Lce
import com.nikol.viewmodel.daggerViewModel
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
fun EventDetailScreen(
    onBack: () -> Unit
) {
    val vm = daggerViewModel<EventDetailVM, EventDetailRouter>() {
        EventDetailRouter { onBack() }
    }

    val state by vm.state.collectAsStateWithLifecycle()
    EventDetailScreen(state, vm::setIntent)
}

@Composable
private fun EventDetailScreen(
    state: EventDetailState,
    onIntent: (EventDetailIntent) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 24.dp)
            .padding(bottom = 24.dp)
    ) {
        when (state) {
            is Lce.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(260.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            is Lce.Failure -> {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(260.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Rounded.ErrorOutline,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Не удалось загрузить событие",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = state.error.toString(),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    FilledTonalButton(
                        onClick = { onIntent(EventDetailIntent.Init) }
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Refresh,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "Повторить")
                    }
                }
            }

            is Lce.Content -> {
                EventDetailContent(
                    event = state.value,
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun EventDetailContent(
    event: ScheduleEvent,
    modifier: Modifier = Modifier
) {
    val zoneId = remember { ZoneId.systemDefault() }
    val timeFormatter = remember { DateTimeFormatter.ofPattern("HH:mm") }
    val dateFormatter =
        remember { DateTimeFormatter.ofPattern("d MMMM yyyy, EEEE", getActiveAppLocale()) }

    val startZoned = remember(event.start) { event.start.atZone(zoneId) }
    val endZoned = remember(event.end) { event.end.atZone(zoneId) }

    val formattedDate = remember(startZoned) {
        startZoned.format(dateFormatter).replaceFirstChar { it.uppercase() }
    }
    val formattedTime = remember(startZoned, endZoned) {
        "${startZoned.format(timeFormatter)} – ${endZoned.format(timeFormatter)}"
    }

    val typeLabel = event.customTypeLabel ?: event.eventType.displayName

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Surface(
                color = getEventTypeContainerColor(event.eventType),
                contentColor = getEventTypeContentColor(event.eventType),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = typeLabel,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                )
            }

            if (event.isRetakeWeek) {
                Surface(
                    color = MaterialTheme.colorScheme.errorContainer,
                    contentColor = MaterialTheme.colorScheme.onErrorContainer,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.WarningAmber,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = "Неделя пересдач",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }

        Text(
            text = event.title,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )

        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            DetailInfoRow(
                icon = Icons.Rounded.AccessTime,
                title = formattedTime,
                subtitle = formattedDate
            )
            LocationDetailRow(location = event.eventLocation)

            if (!event.description.isNullOrBlank()) {
                DescriptionDetailRow(
                    description = event.description!!
                )
            }
        }
    }
}

@Composable
private fun LocationDetailRow(
    location: EventLocation,
    modifier: Modifier = Modifier
) {
    when (location) {
        is EventLocation.University -> {
            val roomsText = if (location.rooms.isNotEmpty()) {
                location.rooms.joinToString(", ")
            } else {
                "Аудитория не указана"
            }

            val subtitleParts = buildList {
                when (location.campus) {
                    Campus.CENTRAL_TELEGRAPH -> add("ЦТ")
                    Campus.DUKAT -> add("Дукат")
                    Campus.UNKNOWN -> Unit
                }
                if (location.building != CampusBuilding.UNKNOWN) {
                    add(location.building.displayName)
                }
                location.floor?.let { add("$it этаж") }
            }

            val subtitle = if (subtitleParts.isNotEmpty()) {
                subtitleParts.joinToString(" • ")
            } else {
                null
            }

            DetailInfoRow(
                icon = Icons.Rounded.Apartment,
                title = roomsText,
                subtitle = subtitle,
                modifier = modifier
            )
        }

        is EventLocation.Online -> {
            DetailInfoRow(
                icon = Icons.Rounded.Language,
                title = "Онлайн",
                subtitle = "Дистанционное занятие",
                modifier = modifier
            )
        }

        is EventLocation.External -> {
            DetailInfoRow(
                icon = Icons.Rounded.Place,
                title = location.description,
                subtitle = "Внешняя локация",
                modifier = modifier
            )
        }

        EventLocation.Unknown -> {
            DetailInfoRow(
                icon = Icons.Rounded.LocationOff,
                title = "Место не указано",
                subtitle = null,
                modifier = modifier
            )
        }
    }
}

@Composable
private fun DetailInfoRow(
    icon: ImageVector,
    title: String,
    subtitle: String? = null,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(22.dp)
            )
        }

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
            if (!subtitle.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun getEventTypeContainerColor(type: EventType): Color {
    return when (type) {
        EventType.EXAM, EventType.CREDIT, EventType.TEST, EventType.COLLOQUIUM ->
            MaterialTheme.colorScheme.tertiaryContainer

        EventType.LECTURE ->
            MaterialTheme.colorScheme.primaryContainer

        EventType.SEMINAR, EventType.OFFICE_HOURS ->
            MaterialTheme.colorScheme.secondaryContainer

        else ->
            MaterialTheme.colorScheme.surfaceVariant
    }
}

@Composable
private fun getEventTypeContentColor(type: EventType): Color {
    return when (type) {
        EventType.EXAM, EventType.CREDIT, EventType.TEST, EventType.COLLOQUIUM ->
            MaterialTheme.colorScheme.onTertiaryContainer

        EventType.LECTURE ->
            MaterialTheme.colorScheme.onPrimaryContainer

        EventType.SEMINAR, EventType.OFFICE_HOURS ->
            MaterialTheme.colorScheme.onSecondaryContainer

        else ->
            MaterialTheme.colorScheme.onSurfaceVariant
    }
}

@Composable
private fun DescriptionDetailRow(
    description: String,
    modifier: Modifier = Modifier
) {
    val uriHandler = LocalUriHandler.current
    val urlRegex = remember { Regex("""https?://[^\s]+""") }
    val isPureUrl = remember(description) { description.trim().matches(urlRegex) }

    val linkColor = MaterialTheme.colorScheme.primary

    val annotatedDescription = remember(description, linkColor) {
        buildAnnotatedString {
            var lastIndex = 0
            urlRegex.findAll(description).forEach { matchResult ->
                val start = matchResult.range.first
                val end = matchResult.range.last + 1
                append(description.substring(lastIndex, start))
                val url = matchResult.value
                val linkAnnotation = LinkAnnotation.Url(
                    url = url,
                    styles = TextLinkStyles(
                        style = SpanStyle(
                            color = linkColor,
                            textDecoration = TextDecoration.Underline,
                            fontWeight = FontWeight.Medium
                        )
                    )
                )
                withLink(linkAnnotation) {
                    append(url)
                }

                lastIndex = end
            }

            if (lastIndex < description.length) {
                append(description.substring(lastIndex))
            }
        }
    }

    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (isPureUrl) Icons.Rounded.Link else Icons.AutoMirrored.Rounded.Notes,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(22.dp)
            )
        }

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            if (!isPureUrl) {
                Text(
                    text = "Описание",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            if (isPureUrl) {
                FilledTonalButton(
                    onClick = {
                        try {
                            uriHandler.openUri(description.trim())
                        } catch (_: Exception) {
                        }
                    },
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.OpenInNew,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Перейти по ссылке")
                }
            } else {
                Text(
                    text = annotatedDescription,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
