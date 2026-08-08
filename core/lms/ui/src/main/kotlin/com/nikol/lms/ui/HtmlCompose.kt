package com.nikol.lms.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.automirrored.filled.InsertDriveFile
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.rounded.Schedule
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import com.nikol.network.ImageFileRequest

@Composable
fun FeedItemUi.RenderHtmlCompose(
    modifier: Modifier = Modifier,
    onFileClick: (filename: String, version: String?) -> Unit = { _, _ -> },
) {
    Box(modifier = modifier.fillMaxWidth()) {
        when (this@RenderHtmlCompose) {
            is FeedItemUi.Content -> {
                HtmlContentRenderer(blocks = blocks)
            }

            is FeedItemUi.File -> {
                FileCard(
                    name = name,
                    size = formattedSize,
                    filename = filename,
                    version = version,
                    onClick = { i1, i2 -> onFileClick(i1, i2) }
                )
            }

            is FeedItemUi.Homework -> {
                HomeworkCard(
                    title = title,
                    deadline = deadline,
                    descriptionBlocks = descriptionBlocks,
                    attachedFiles = attachedFiles,
                    onFileClick = onFileClick
                )
            }

            is FeedItemUi.Image -> {
                ImageBlock(filename = filename, version = version)
            }
        }
    }
}

@Composable
fun FileCard(
    name: String,
    size: String,
    onClick: (String, String?) -> Unit,
    filename: String,
    version: String?,
    modifier: Modifier = Modifier,
    containerColor: Color = MaterialTheme.colorScheme.surfaceContainer
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick(filename, version) },
        color = containerColor,
        shape = RoundedCornerShape(16.dp)
    ) {
        ListItem(
            colors = ListItemDefaults.colors(containerColor = Color.Transparent),
            headlineContent = {
                Text(
                    text = name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                )
            },
            supportingContent = {
                Text(
                    text = size,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            leadingContent = {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.secondaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.InsertDriveFile,
                        contentDescription = "File",
                        tint = MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier.size(20.dp)
                    )
                }
            },
            trailingContent = {
                Icon(
                    imageVector = Icons.Default.Download,
                    contentDescription = "Download",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        )
    }
}

@Composable
fun HomeworkCard(
    title: String,
    deadline: String?,
    descriptionBlocks: List<HtmlBlock>,
    attachedFiles: List<FeedItemUi.File>,
    onFileClick: (String, String?) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Assignment,
                        contentDescription = "Homework",
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            if (deadline != null) {
                Spacer(modifier = Modifier.height(12.dp))
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
                            text = "Дедлайн: $deadline",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                }
            }

            if (descriptionBlocks.isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))
                HtmlContentRenderer(blocks = descriptionBlocks)
            }

            if (attachedFiles.isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))

                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surfaceContainer
                ) {
                    Column {
                        attachedFiles.forEachIndexed { index, file ->
                            FileCard(
                                name = file.name,
                                size = file.formattedSize,
                                filename = file.filename,
                                version = file.version,
                                onClick = onFileClick,
                                containerColor = Color.Transparent
                            )
                            if (index < attachedFiles.lastIndex) {
                                HorizontalDivider(
                                    modifier = Modifier.padding(horizontal = 16.dp),
                                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ImageBlock(
    filename: String,
    version: String?,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    val imageRequest = remember(filename, version) {
        ImageRequest.Builder(context)
            .data(ImageFileRequest(filename, version))
            .build()
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surfaceContainer)
    ) {
        AsyncImage(
            model = imageRequest,
            contentDescription = "Image Material",
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 100.dp, max = 280.dp),
            contentScale = ContentScale.Crop
        )
    }
}

@Composable
fun HtmlContentRenderer(
    blocks: List<HtmlBlock>,
    modifier: Modifier = Modifier
) {
    val uriHandler = LocalUriHandler.current

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        blocks.forEach { block ->
            when (block) {
                is HtmlBlock.Heading -> {
                    val typography = when (block.level) {
                        1 -> MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
                        2 -> MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                        else -> MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
                    }
                    Text(
                        text = rememberThemeAppliedHtml(block.content),
                        style = typography,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(top = if (block.level == 1) 8.dp else 4.dp)
                    )
                }

                is HtmlBlock.Text -> {
                    val styledText = rememberThemeAppliedHtml(block.content)
                    ClickableText(
                        text = styledText,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            color = MaterialTheme.colorScheme.onSurface
                        ),
                        onClick = { offset ->
                            styledText.getStringAnnotations("URL", offset, offset)
                                .firstOrNull()?.let { annotation ->
                                    uriHandler.openUri(annotation.item)
                                }
                        }
                    )
                }

                is HtmlBlock.Quote -> {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.surfaceContainer)
                            .padding(end = 14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .width(4.dp)
                                .height(40.dp)
                                .clip(RoundedCornerShape(topStart = 12.dp, bottomStart = 12.dp))
                                .background(MaterialTheme.colorScheme.primary)
                        )
                        Spacer(modifier = Modifier.width(14.dp))
                        Text(
                            text = rememberThemeAppliedHtml(block.content),
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontStyle = FontStyle.Italic,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            ),
                            modifier = Modifier
                                .padding(vertical = 12.dp)
                                .weight(1f)
                        )
                    }
                }

                is HtmlBlock.BulletList -> {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        block.items.forEach { item ->
                            Row(
                                modifier = Modifier.padding(start = 4.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                Box(
                                    modifier = Modifier
                                        .padding(top = 8.dp)
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.primary)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = rememberThemeAppliedHtml(item),
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }

                is HtmlBlock.OrderedList -> {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        block.items.forEachIndexed { index, item ->
                            Row(
                                modifier = Modifier.padding(start = 4.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                Text(
                                    text = "${index + 1}.",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(end = 8.dp)
                                )
                                Text(
                                    text = rememberThemeAppliedHtml(item),
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }

                is HtmlBlock.Code -> {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        color = Color(0xFF1E2128),
                        border = null
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            if (block.language.isNotEmpty()) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(bottom = 8.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = block.language.uppercase(),
                                        style = MaterialTheme.typography.labelMedium,
                                        color = Color(0xFF8B949E),
                                        fontWeight = FontWeight.Medium
                                    )
//                                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
//                                        // "Кнопочки" как в mac/IDE для декорации
//                                        Box(
//                                            modifier = Modifier
//                                                .size(8.dp)
//                                                .clip(CircleShape)
//                                                .background(Color(0xFFED6A5E))
//                                        )
//                                        Box(
//                                            modifier = Modifier
//                                                .size(8.dp)
//                                                .clip(CircleShape)
//                                                .background(Color(0xFFF4BF4F))
//                                        )
//                                        Box(
//                                            modifier = Modifier
//                                                .size(8.dp)
//                                                .clip(CircleShape)
//                                                .background(Color(0xFF61C554))
//                                        )
//                                    }
                                }
                            }
                            Text(
                                text = block.codeText,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontFamily = FontFamily.Monospace,
                                    lineHeight = MaterialTheme.typography.bodyMedium.lineHeight * 1.2f,
                                    color = Color(0xFFE5E7EB)
                                ),
                                modifier = Modifier.horizontalScroll(rememberScrollState())
                            )
                        }
                    }
                }

                is HtmlBlock.Divider -> {
                    HorizontalDivider(
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun rememberThemeAppliedHtml(annotatedString: AnnotatedString): AnnotatedString {
    val codeBackgroundColor = MaterialTheme.colorScheme.surfaceVariant
    val codeTextColor = MaterialTheme.colorScheme.error
    val linkColor = MaterialTheme.colorScheme.primary

    return remember(annotatedString, codeBackgroundColor, codeTextColor, linkColor) {
        buildAnnotatedString {
            append(annotatedString)

            annotatedString.getStringAnnotations("CODE", 0, annotatedString.length)
                .forEach { range ->
                    addStyle(
                        style = SpanStyle(
                            background = codeBackgroundColor,
                            color = codeTextColor
                        ),
                        start = range.start,
                        end = range.end
                    )
                }

            annotatedString.getStringAnnotations("URL", 0, annotatedString.length)
                .forEach { range ->
                    addStyle(
                        style = SpanStyle(color = linkColor),
                        start = range.start,
                        end = range.end
                    )
                }
        }
    }
}