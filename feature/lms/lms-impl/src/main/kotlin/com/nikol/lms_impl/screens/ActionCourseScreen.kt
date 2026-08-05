package com.nikol.lms_impl.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Article
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.rounded.ChatBubbleOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.nikol.lms_impl.R

@Composable
fun ActionCourseScreen(
    timeUrl: String?,
    sillabusUrl: String?
) {
    val uriHandler = LocalUriHandler.current

    CourseDashboardBottomSheet(
        onSyllabusClick = {
            sillabusUrl?.let { uriHandler.openUri(it) }
        },
        onChatClick = {
            timeUrl?.let { uriHandler.openUri(it) }
        },
        hasSyllabus = !sillabusUrl.isNullOrEmpty(),
        hasChat = !timeUrl.isNullOrEmpty()
    )
}

@Composable
fun CourseDashboardBottomSheet(
    onSyllabusClick: () -> Unit,
    onChatClick: () -> Unit,
    hasSyllabus: Boolean,
    hasChat: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .padding(top = 8.dp, bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = stringResource(R.string.feature_lms_impl_course_dashboard_title),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )

        if (hasSyllabus) {
            ActionItemCard(
                title = stringResource(R.string.feature_lms_impl_course_dashboard_syllabus_title),
                subtitle = stringResource(R.string.feature_lms_impl_course_dashboard_syllabus_subtitle),
                icon = Icons.AutoMirrored.Rounded.Article,
                iconBgColor = MaterialTheme.colorScheme.primaryContainer,
                iconTintColor = MaterialTheme.colorScheme.onPrimaryContainer,
                onClick = onSyllabusClick
            )
        }

        if (hasChat) {
            ActionItemCard(
                title = stringResource(R.string.feature_lms_impl_course_dashboard_chat_title),
                subtitle = stringResource(R.string.feature_lms_impl_course_dashboard_chat_subtitle),
                icon = Icons.Rounded.ChatBubbleOutline,
                iconBgColor = MaterialTheme.colorScheme.secondaryContainer,
                iconTintColor = MaterialTheme.colorScheme.onSecondaryContainer,
                onClick = onChatClick
            )
        }

        if (!hasSyllabus && !hasChat) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = stringResource(R.string.feature_lms_impl_course_dashboard_empty),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
    }
}

@Composable
private fun ActionItemCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    iconBgColor: Color,
    iconTintColor: Color,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() },
        color = MaterialTheme.colorScheme.surfaceContainerHigh,
        shape = RoundedCornerShape(16.dp)
    ) {
        ListItem(
            colors = ListItemDefaults.colors(containerColor = Color.Transparent),
            headlineContent = {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
            },
            supportingContent = {
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            leadingContent = {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(iconBgColor),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = iconTintColor,
                        modifier = Modifier.size(24.dp)
                    )
                }
            },
            trailingContent = {
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.KeyboardArrowRight,
                    contentDescription = stringResource(R.string.feature_lms_impl_course_dashboard_navigate),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        )
    }
}