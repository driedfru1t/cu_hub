package com.nikol.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Assignment
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.rounded.Send
import androidx.compose.material.icons.rounded.Cancel
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Replay
import androidx.compose.material.icons.rounded.Visibility
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import com.nikol.designsystem.theme.CUHubTheme
import com.nikol.lms.domain.model.TaskState
import com.nikol.lms.domain.model.TaskSummary
import com.nikol.ui.prewiewData.TaskSummaryPreviewProvider
import java.time.format.FormatStyle
import java.util.Locale

@Composable
fun TaskCard(
    task: TaskSummary,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val config = getTaskStateConfig(task.state)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        )
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {

            ListItem(
                modifier = Modifier,
                leadingContent = {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(config.containerColor),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = config.icon,
                            contentDescription = null,
                            tint = config.contentColor
                        )
                    }
                },
                trailingContent = {
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.KeyboardArrowRight,
                        contentDescription = "Детали",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                overlineContent = {
                    Text(
                        text = task.course.name,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
//                supportingContent = {
//                    Text(
//                        text = "${task.exercise.activity.name} • ${task.theme.name}",
//                        style = MaterialTheme.typography.bodyMedium,
//                        color = MaterialTheme.colorScheme.onSurfaceVariant,
//                        maxLines = 1,
//                        overflow = TextOverflow.Ellipsis
//                    )
//                },
                colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                elevation = ListItemDefaults.elevation(),
                content = {
                    Text(
                        text = task.exercise.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                },
            )

            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 16.dp),
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Срок сдачи",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(2.dp))

                    val formattedDeadline = remember(task.deadline) {
                        task.deadline.toLocalizedStyle(
                            dateStyle = FormatStyle.SHORT,
                            timeStyle = FormatStyle.SHORT
                        )
                    }
                    Text(
                        text = formattedDeadline,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = config.containerColor.copy(alpha = 0.6f)
                ) {
                    Column(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        horizontalAlignment = Alignment.End
                    ) {
                        Text(
                            text = config.label,
                            style = MaterialTheme.typography.labelSmall,
                            color = config.contentColor
                        )

                        val scoreText =
                            if (task.state == TaskState.EVALUATED && task.score != null) {
                                "${String.format(Locale.US, "%.1f", task.score)} / ${
                                    String.format(
                                        Locale.US,
                                        "%.1f",
                                        task.exercise.maxScore
                                    )
                                }"
                            } else {
                                "- / ${String.format(Locale.US, "%.1f", task.exercise.maxScore)}"
                            }

                        Text(
                            text = scoreText,
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = config.contentColor
                        )
                    }
                }
            }
        }
    }
}

@Immutable
data class TaskStateUiConfig(
    val label: String,
    val icon: ImageVector,
    val containerColor: Color,
    val contentColor: Color
)

@Composable
fun getTaskStateConfig(state: TaskState): TaskStateUiConfig {
    val isDark = isSystemInDarkTheme()

    return when (state) {
        // 1. Серая нейтральность
        TaskState.BACKLOG -> TaskStateUiConfig(
            label = "К выполнению",
            icon = Icons.AutoMirrored.Rounded.Assignment,
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            contentColor = MaterialTheme.colorScheme.onSurfaceVariant
        )

        // 2. Синий акцент (динамический системный)
        TaskState.IN_PROGRESS -> TaskStateUiConfig(
            label = "В работе",
            icon = Icons.Rounded.Edit,
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
        )

        // 3. Плотный серый/возвышенный
        TaskState.SUBMITTED -> TaskStateUiConfig(
            label = "Отправлено",
            icon = Icons.AutoMirrored.Rounded.Send,
            containerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
            contentColor = MaterialTheme.colorScheme.onSurface
        )

        // 4. Фиолетовый/Розовый акцент (динамический системный)
        TaskState.REVIEW -> TaskStateUiConfig(
            label = "На проверке",
            icon = Icons.Rounded.Visibility,
            containerColor = MaterialTheme.colorScheme.tertiaryContainer,
            contentColor = MaterialTheme.colorScheme.onTertiaryContainer
        )

        // 5. Оранжевый (На доработке) — Мягкий персиковый/песочный (Каноничный MD3 Warning)
        TaskState.REWORKING -> TaskStateUiConfig(
            label = "На доработке",
            icon = Icons.Rounded.Replay,
            containerColor = if (isDark) Color(0xFF4D2700) else Color(0xFFFFDCC2), // Тон 30 : Тон 90
            contentColor = if (isDark) Color(0xFFFFDCC2) else Color(0xFF2E1500)   // Тон 90 : Тон 10
        )

        // 6. Зеленый (Оценено) — Приглушенный мятный/шалфейный (Каноничный MD3 Success)
        TaskState.EVALUATED -> TaskStateUiConfig(
            label = "Оценено",
            icon = Icons.Rounded.CheckCircle,
            containerColor = if (isDark) Color(0xFF005221) else Color(0xFFC4EED0), // Тон 30 : Тон 90
            contentColor = if (isDark) Color(0xFFC4EED0) else Color(0xFF003915)   // Тон 90 : Тон 10
        )

        // 7. Красный акцент (динамический системный)
        TaskState.FAILED -> TaskStateUiConfig(
            label = "Не сдано",
            icon = Icons.Rounded.Cancel,
            containerColor = MaterialTheme.colorScheme.errorContainer,
            contentColor = MaterialTheme.colorScheme.onErrorContainer
        )
    }
}


@ThemePreview
@Composable
fun PreviewTaskCard(
    @PreviewParameter(TaskSummaryPreviewProvider::class)
    task: TaskSummary,
) {
    CUHubTheme {
        Surface(
            color = MaterialTheme.colorScheme.background
        ) {
            TaskCard(
                task,
                {},
                Modifier.padding(16.dp)
            )
        }
    }
}