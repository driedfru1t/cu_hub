package com.nikol.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import com.nikol.designsystem.theme.CUHubTheme
import com.nikol.lms.domain.model.CourseCategory
import com.nikol.lms.domain.model.CourseSummary
import com.nikol.lms.domain.model.PublicationState

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CourseCard(
    courseSummary: CourseSummary,
    modifier: Modifier = Modifier,
    click: () -> Unit,
    clickToMore: () -> Unit
) {
    val isArchived = courseSummary.state == PublicationState.ARCHIVED || courseSummary.isArchived

    val iconColor = remember(courseSummary.category) { courseSummary.category.toGoogleIconColor() }
    val categoryName = remember(courseSummary.category) {
        courseSummary.category.name.lowercase().replace('_', ' ')
            .replaceFirstChar { it.uppercase() }
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .clickable { click() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLowest
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 0.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(88.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                iconColor.copy(alpha = 0.2f),
                                iconColor.copy(alpha = 0.05f)
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(courseSummary.category.toIcon()),
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(36.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = categoryName,
                    style = MaterialTheme.typography.labelMedium,
                    color = iconColor,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = courseSummary.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = MaterialTheme.typography.titleMedium.lineHeight * 1.1f
                )

                Spacer(modifier = Modifier.height(8.dp))

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    if (courseSummary.state == PublicationState.DRAFT) {
                        StatusBadge(
                            text = "Черновик",
                            containerColor = MaterialTheme.colorScheme.errorContainer,
                            contentColor = MaterialTheme.colorScheme.onErrorContainer
                        )
                    } else if (isArchived) {
                        StatusBadge(
                            text = "Архив",
                            containerColor = MaterialTheme.colorScheme.surfaceVariant,
                            contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    if (courseSummary.settings.isSkillLevelEnabled) {
                        StatusBadge(
                            text = courseSummary.settings.skillLevel.name,
                            containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f),
                            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                }
            }

            IconButton(
                onClick = clickToMore,
                modifier = Modifier.align(Alignment.Top)
            ) {
                Icon(
                    painter = painterResource(R.drawable.more_vert),
                    contentDescription = "Ещё",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun StatusBadge(
    text: String,
    containerColor: Color,
    contentColor: Color
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(containerColor)
            .padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
        Text(
            text = text.uppercase(),
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = contentColor
        )
    }
}


private fun CourseCategory.toIcon(): Int {
    return when (this) {
        CourseCategory.WITHOUT_CATEGORY -> R.drawable.without_category
        CourseCategory.GENERAL -> R.drawable.school
        CourseCategory.MATHEMATICS -> R.drawable.calculate
        CourseCategory.BUSINESS -> R.drawable.business_center
        CourseCategory.DEVELOPMENT -> R.drawable.code_blocks
        CourseCategory.STEM -> R.drawable.science
        CourseCategory.SOFT_SKILLS -> R.drawable.soft
        CourseCategory.ML -> R.drawable.memory
        CourseCategory.DESIGN -> R.drawable.design_services
        CourseCategory.ANALYTICS -> R.drawable.finance_mode
        CourseCategory.CAREER -> R.drawable.work
        CourseCategory.MANAGEMENT -> R.drawable.manage_accounts
    }
}

private fun CourseCategory.toGoogleIconColor(): Color = when (this) {
    CourseCategory.WITHOUT_CATEGORY -> Color(0xFF757575)
    CourseCategory.GENERAL -> Color(0xFF00796B)
    CourseCategory.MATHEMATICS -> Color(0xFF7B1FA2)
    CourseCategory.BUSINESS -> Color(0xFF2E7D32)
    CourseCategory.DEVELOPMENT -> Color(0xFF1565C0)
    CourseCategory.STEM -> Color(0xFF00838F)
    CourseCategory.SOFT_SKILLS -> Color(0xFFEF6C00)
    CourseCategory.ML -> Color(0xFF283593)
    CourseCategory.DESIGN -> Color(0xFFC2185B)
    CourseCategory.ANALYTICS -> Color(0xFF0097A7)
    CourseCategory.CAREER -> Color(0xFF5D4037)
    CourseCategory.MANAGEMENT -> Color(0xFF455A64)
}

@ThemePreview
@Composable
fun CourseCardPreview(
    @PreviewParameter(CourseSummaryPreviewProvider::class) course: CourseSummary
) {
    CUHubTheme {
        Surface(
            color = MaterialTheme.colorScheme.background
        ) {
            CourseCard(course, modifier = Modifier.padding(16.dp), {}, {})
        }
    }
}
