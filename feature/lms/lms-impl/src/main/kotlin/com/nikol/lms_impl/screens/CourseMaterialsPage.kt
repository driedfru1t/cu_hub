package com.nikol.lms_impl.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.ExperimentalFoundationApi
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
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Article
import androidx.compose.material.icons.automirrored.rounded.Assignment
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material.icons.rounded.ExpandMore
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.nikol.lms.domain.model.CourseTheme
import com.nikol.lms.domain.model.DeadlineCourse
import com.nikol.lms.domain.model.Longread
import com.nikol.lms.domain.model.LongreadType
import com.nikol.lms.domain.model.PublicationState
import com.nikol.lms_impl.viewModels.courseDeatil.CourseMaterialsIntent
import com.nikol.lms_impl.viewModels.courseDeatil.CourseMaterialsState
import com.nikol.ui.state.Lce
import com.nikol.ui.toLocalizedStyle
import java.time.format.FormatStyle

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CourseMaterialsPage(
    state: CourseMaterialsState,
    listState: LazyListState,
    paddingValues: PaddingValues,
    onIntent: (CourseMaterialsIntent) -> Unit
) {
    LazyColumn(
        state = listState,
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            top = 16.dp,
            bottom = 80.dp + paddingValues.calculateBottomPadding()
        ),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        if (state is Lce.Content && state.value.deadlines.isNotEmpty()) {
            stickyHeader(key = "deadlines_title") {
                Text(
                    text = "Ближайшие дедлайны",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.background)
                        .padding(horizontal = 16.dp, vertical = 4.dp)
                )
            }

            item(key = "deadlines_carousel") {
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(items = state.value.deadlines, key = { it.id }) { deadline ->
                        DeadlineCard(
                            deadlineCourse = deadline,
                            onClick = { }
                        )
                    }
                }
            }
        }

        when (state) {
            is Lce.Loading -> {
                item(key = "loading") {
                    Box(
                        modifier = Modifier.fillParentMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
            }

            is Lce.Failure -> {
                item(key = "error") {
                    Box(
                        modifier = Modifier.fillParentMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Ошибка загрузки материалов: ${state.error}")
                    }
                }
            }

            is Lce.Content -> {
                if (state.value.deadlines.isNotEmpty()) {
                    stickyHeader(key = "material_title") {
                        Text(
                            text = "Материалы",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.background)
                                .padding(horizontal = 16.dp, vertical = 4.dp)
                        )
                    }
                }

                items(items = state.value.materials.themes, key = { it.id }) { theme ->
                    ExpandableModuleCard(
                        theme = theme,
                        onMaterialClick = { onIntent(CourseMaterialsIntent.OnMaterialClick(it)) }
                    )
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeadlineCard(
    deadlineCourse: DeadlineCourse,
    onClick: () -> Unit
) {
    val formattedDeadline = remember(deadlineCourse.deadline) {
        deadlineCourse.deadline.toLocalizedStyle(
            dateStyle = FormatStyle.SHORT,
            timeStyle = FormatStyle.SHORT
        )
    }

    Card(
        onClick = onClick,
        modifier = Modifier.width(260.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = deadlineCourse.exercise.activity.name,
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp)
                )

                if (deadlineCourse.exercise.maxScore > 0) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.secondaryContainer
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Star,
                                contentDescription = null,
                                modifier = Modifier.size(12.dp),
                                tint = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${deadlineCourse.exercise.maxScore.toInt()}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSecondaryContainer,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = deadlineCourse.exercise.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )

            Spacer(modifier = Modifier.height(16.dp))

            Surface(
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.6f)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Schedule,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.onErrorContainer
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = formattedDeadline,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
            }
        }
    }
}

@Composable
fun ExpandableModuleCard(theme: CourseTheme, onMaterialClick: (Int) -> Unit) {
    var isExpanded by rememberSaveable { mutableStateOf(false) }
    val rotationState by animateFloatAsState(
        targetValue = if (isExpanded) 180f else 0f,
        label = "rotation"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .animateContentSize(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            ListItem(
                modifier = Modifier.clickable { isExpanded = !isExpanded },
                colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                overlineContent = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Модуль ${theme.order + 1}",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.primary
                        )

                        if (theme.state == PublicationState.DRAFT) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = MaterialTheme.colorScheme.errorContainer
                            ) {
                                Text(
                                    text = "Черновик",
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onErrorContainer
                                )
                            }
                        }
                    }
                },
                headlineContent = {
                    Text(
                        text = theme.name,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium
                    )
                },
                supportingContent = {
                    Text(
                        text = if (theme.longreads.isNotEmpty()) "Материалов: ${theme.longreads.size}" else "Пока пусто",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                leadingContent = {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.primaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Outlined.Folder,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                },
                trailingContent = {
                    if (theme.longreads.isNotEmpty()) {
                        Icon(
                            Icons.Rounded.ExpandMore,
                            contentDescription = null,
                            modifier = Modifier.rotate(rotationState)
                        )
                    }
                }
            )

            AnimatedVisibility(
                visible = isExpanded && theme.longreads.isNotEmpty(),
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                Column(modifier = Modifier.padding(bottom = 8.dp)) {
                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                    )
                    theme.longreads.forEach { longread ->
                        MaterialSubItemRow(longread = longread, onMaterialClick = onMaterialClick)
                    }
                }
            }
        }
    }
}

@Composable
fun MaterialSubItemRow(longread: Longread, onMaterialClick: (Int) -> Unit) {
    ListItem(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onMaterialClick(longread.id) },
        colors = ListItemDefaults.colors(containerColor = Color.Transparent),
        headlineContent = {
            Text(
                text = longread.name,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )
        },
        supportingContent = {
            val deadline = longread.exercises.firstOrNull()?.deadline
            if (deadline != null) {
                val formattedDeadline = remember(longread.exercises) {
                    deadline.toLocalizedStyle(
                        dateStyle = FormatStyle.SHORT,
                        timeStyle = FormatStyle.SHORT
                    )
                }

                Surface(
                    modifier = Modifier.padding(top = 4.dp),
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.surfaceContainerHighest
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Rounded.Schedule,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = formattedDeadline,
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        },
        leadingContent = {
            val iconBgColor = if (longread.type == LongreadType.HANDOUT)
                MaterialTheme.colorScheme.tertiaryContainer
            else
                MaterialTheme.colorScheme.secondaryContainer

            val iconTintColor = if (longread.type == LongreadType.HANDOUT)
                MaterialTheme.colorScheme.onTertiaryContainer
            else
                MaterialTheme.colorScheme.onSecondaryContainer

            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(iconBgColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = when (longread.type) {
                        LongreadType.COMMON -> Icons.AutoMirrored.Rounded.Article
                        LongreadType.HANDOUT -> Icons.AutoMirrored.Rounded.Assignment
                    },
                    contentDescription = null,
                    tint = iconTintColor,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    )
}