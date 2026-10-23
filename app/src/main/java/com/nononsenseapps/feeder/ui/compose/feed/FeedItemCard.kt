package com.nononsenseapps.feeder.ui.compose.feed

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeightIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ContentAlpha
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Feed
import androidx.compose.material.icons.filled.Grade
import androidx.compose.material.icons.outlined.Grade
import androidx.compose.material.icons.outlined.Terrain
import androidx.compose.material.icons.outlined.Timelapse
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.size.Size
import com.nononsenseapps.feeder.R
import com.nononsenseapps.feeder.db.room.ID_UNSET
import com.nononsenseapps.feeder.model.MediaImage
import com.nononsenseapps.feeder.ui.compose.coil.rememberTintedVectorPainter
import com.nononsenseapps.feeder.ui.compose.components.safeSemantics
import com.nononsenseapps.feeder.ui.compose.feedarticle.wordsToReadTimeSecs
import com.nononsenseapps.feeder.ui.compose.minimumTouchSize
import com.nononsenseapps.feeder.ui.compose.text.WithBidiDeterminedLayoutDirection
import com.nononsenseapps.feeder.ui.compose.theme.FeedListItemDateStyle
import com.nononsenseapps.feeder.ui.compose.theme.FeedListItemFeedTitleStyle
import com.nononsenseapps.feeder.ui.compose.theme.FeedListItemSnippetTextStyle
import com.nononsenseapps.feeder.ui.compose.theme.FeedListItemTitleTextStyle
import com.nononsenseapps.feeder.ui.compose.theme.PreviewTheme
import com.nononsenseapps.feeder.ui.compose.theme.titleFontWeight
import com.nononsenseapps.feeder.ui.compose.utils.PreviewThemes
import com.nononsenseapps.feeder.ui.compose.utils.onKeyEventLikeEscape
import com.nononsenseapps.feeder.util.logDebug
import java.net.URL
import java.time.Instant

@Composable
fun FeedItemCard(
    item: FeedListItem,
    showThumbnail: Boolean,
    onOpenFeedItemInReader: () -> Unit,
    onOpenFeedItemInCustomTab: () -> Unit,
    onOpenFeedItemInBrowser: () -> Unit,
    onMarkAboveAsRead: () -> Unit,
    onMarkBelowAsRead: () -> Unit,
    onShareItem: () -> Unit,
    onToggleBookmark: () -> Unit,
    dropDownMenuExpanded: Boolean,
    onDismissDropdown: () -> Unit,
    bookmarkIndicator: Boolean,
    maxLines: Int,
    showOnlyTitle: Boolean,
    showReadingTime: Boolean,
    modifier: Modifier = Modifier,
) {
    ElevatedCard(
        shape = RoundedCornerShape(20.dp),
        modifier = modifier,
    ) {
        Box {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(12.dp)
                        .requiredHeightIn(min = minimumTouchSize)
                        .alpha(if (!item.unread) 0.75f else 1.0f),
            ) {
                // 1. Header Row: Favicon + (feedTitle / pubDate) + Bookmark icon (top right)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier =
                            Modifier
                                .size(50.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primaryContainer),
                    ) {                  val initial = item.feedTitle.trim().take(1).uppercase()
                        if (initial.isNotBlank()) {
                            Text(
                                text = initial,
                                style = MaterialTheme.typography.headlineLarge,
                                fontWeight = FontWeight.Bold,

                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Outlined.Terrain,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp),
                                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                            )
                        }}

                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.Center,
                    ) {
                        WithBidiDeterminedLayoutDirection(paragraph = item.feedTitle) {
                            Text(
                                text = item.feedTitle,
                                style = FeedListItemFeedTitleStyle(),
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                        }
                        if (item.pubDate.isNotBlank()) {
                            WithBidiDeterminedLayoutDirection(paragraph = item.pubDate) {
                                Text(
                                    text = item.pubDate,
                                    style = FeedListItemDateStyle(),
                                    color = Color(0xFF576F76),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                )
                            }
                        }
                    }

                    if (bookmarkIndicator || item.bookmarked) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.padding(8.dp),
                        ) {
                            Icon(
                                imageVector =
                                    if (item.bookmarked) {
                                        Icons.Filled.Grade
                                    } else {
                                        Icons.Outlined.Grade
                                    },
                                contentDescription =
                                    stringResource(
                                        if (item.bookmarked) R.string.unsave_article else R.string.save_article,
                                    ),
                                modifier = Modifier.size(18.dp),
                                tint =
                                    if (item.bookmarked) {
                                        MaterialTheme.colorScheme.primary
                                    } else {
                                        MaterialTheme.colorScheme.onSurfaceVariant
                                    },
                            )
                        }

                    }
                }

                // 2. item.title (omit if empty to avoid leaving gap)
                if (item.title.isNotBlank()) {
                    WithBidiDeterminedLayoutDirection(paragraph = item.title) {
                        Text(
                            text = item.title,
                            style = FeedListItemTitleTextStyle(),
                            fontWeight = titleFontWeight(item.unread),
                            overflow = TextOverflow.Ellipsis,
                            maxLines = maxLines,
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                }

                // 3. item.snippet
                if (!showOnlyTitle && item.snippet.isNotBlank()) {
                    WithBidiDeterminedLayoutDirection(paragraph = item.snippet) {
                        Text(
                            text = item.snippet,
                            style = FeedListItemSnippetTextStyle(),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            overflow = TextOverflow.Ellipsis,
                            maxLines = maxLines,
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                }

                // 4. Large Thumbnail / Image (Full size with dynamic height)
                if (showThumbnail) {
                    item.image?.let { image ->
                        if ((image.width ?: 1000) >= 10 && (image.height ?: 1000) >= 10) {
                            val imageModifier =
                                if (image.width != null && image.height != null && image.width!! > 0 && image.height!! > 0) {
                                    Modifier
                                        .fillMaxWidth()
                                        .aspectRatio(image.width!!.toFloat() / image.height!!.toFloat())
                                } else {
                                    Modifier.fillMaxWidth()
                                }
                            val cornerShape = RoundedCornerShape(20.dp)
                            AsyncImage(
                                model =
                                    ImageRequest
                                        .Builder(LocalContext.current)
                                        .data(image.url)
                                        .listener(
                                            onError = { a, b ->
                                                logDebug("FEEDER_CARD", "error ${a.data}", b.throwable)
                                            },
                                        ).size(Size.ORIGINAL)
                                        .build(),
                                placeholder = rememberTintedVectorPainter(Icons.Outlined.Terrain),
                                //error = rememberTintedVectorPainter(Icons.Outlined.ErrorOutline),
                                error = null,
                                contentDescription = stringResource(id = R.string.article_image),
                                contentScale = ContentScale.FillWidth,
                                alignment = Alignment.Center,
                                modifier =
                                    imageModifier
                                        .clip(cornerShape)
                                        .background(MaterialTheme.colorScheme.surfaceVariant)
                                        .border(BorderStroke(1.dp, MaterialTheme.colorScheme.onSurfaceVariant), shape = cornerShape)
                                        .safeSemantics {
                                            testTag = "card_image"
                                        },
                            )
                        }
                    }
                }

                // 5. Action Chips Row (readTime, wordCount - only shown if available)
                if (showReadingTime) {
                    val readTimeSecs = remember(item.wordCount) { wordsToReadTimeSecs(item.wordCount) }
                    val readTimeMins = (readTimeSecs / 60).coerceAtLeast(1)
                    val hasReadTime = readTimeSecs > 0
                    val hasWordCount = item.wordCount > 0

                    if (hasReadTime || hasWordCount) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            if (hasReadTime) {
                                val readTimeText =
                                    pluralStringResource(id = R.plurals.n_minutes, count = readTimeMins).format(readTimeMins)
                                // Chip 1: Read time pill with clock icon
                                Surface(
                                    shape = CircleShape,
                                    color = MaterialTheme.colorScheme.surfaceVariant,
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                    ) {
                                        Icon(
                                            imageVector = Icons.Outlined.Timelapse,
                                            contentDescription = null,
                                            modifier = Modifier.size(16.dp),
                                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                        )
                                        Text(
                                            text = readTimeText,
                                            style = MaterialTheme.typography.labelMedium,
                                            fontWeight = FontWeight.Medium,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        )
                                    }
                                }
                            }

                            if (hasWordCount) {
                                val wordCountText =
                                    pluralStringResource(id = R.plurals.n_words, count = item.wordCount).format(item.wordCount)
                                // Chip 2: Word count pill with comment icon
                                Surface(
                                    shape = CircleShape,
                                    color = MaterialTheme.colorScheme.surfaceVariant,
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                    ) {
                                        Icon(
                                            imageVector = Icons.AutoMirrored.Outlined.Feed,
                                            contentDescription = null,
                                            modifier = Modifier.size(16.dp),
                                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                        )
                                        Text(
                                            text = wordCountText,
                                            style = MaterialTheme.typography.labelMedium,
                                            fontWeight = FontWeight.Medium,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Dropdown Menu
            DropdownMenu(
                expanded = dropDownMenuExpanded,
                onDismissRequest = onDismissDropdown,
                modifier = Modifier.onKeyEventLikeEscape(onDismissDropdown),
            ) {
                DropdownMenuItem(
                    onClick = {
                        onDismissDropdown()
                        onOpenFeedItemInReader()
                    },
                    text = {
                        Text(
                            text = stringResource(id = R.string.open_article_in_reader),
                        )
                    },
                )
                DropdownMenuItem(
                    onClick = {
                        onDismissDropdown()
                        onOpenFeedItemInCustomTab()
                    },
                    text = {
                        Text(
                            text = stringResource(id = R.string.open_article_in_custom_tab),
                        )
                    },
                )
                DropdownMenuItem(
                    onClick = {
                        onDismissDropdown()
                        onOpenFeedItemInBrowser()
                    },
                    text = {
                        Text(
                            text = stringResource(id = R.string.open_article_in_default_browser),
                        )
                    },
                )
                DropdownMenuItem(
                    onClick = {
                        onDismissDropdown()
                        onToggleBookmark()
                    },
                    text = {
                        Text(
                            text =
                                stringResource(
                                    if (item.bookmarked) R.string.unsave_article else R.string.save_article,
                                ),
                        )
                    },
                )
                DropdownMenuItem(
                    onClick = {
                        onDismissDropdown()
                        onMarkAboveAsRead()
                    },
                    text = {
                        Text(
                            text = stringResource(id = R.string.mark_items_above_as_read),
                        )
                    },
                )
                DropdownMenuItem(
                    onClick = {
                        onDismissDropdown()
                        onMarkBelowAsRead()
                    },
                    text = {
                        Text(
                            text = stringResource(id = R.string.mark_items_below_as_read),
                        )
                    },
                )
                DropdownMenuItem(
                    onClick = {
                        onDismissDropdown()
                        onShareItem()
                    },
                    text = {
                        Text(
                            text = stringResource(R.string.share),
                        )
                    },
                )
                // Hidden button for TalkBack
                val closeMenuText = stringResource(id = R.string.close_menu)
                DropdownMenuItem(
                    onClick = onDismissDropdown,
                    text = {},
                    modifier =
                        Modifier
                            .height(0.dp)
                            .safeSemantics {
                                contentDescription = closeMenuText
                                role = Role.Button
                            },
                )
            }
        }
    }
}

@Composable
fun RowScope.FeedItemText(
    item: FeedListItem,
    onOpenFeedItemInReader: () -> Unit,
    onOpenFeedItemInCustomTab: () -> Unit,
    onOpenFeedItemInBrowser: () -> Unit,
    onMarkAboveAsRead: () -> Unit,
    onMarkBelowAsRead: () -> Unit,
    onShareItem: () -> Unit,
    onToggleBookmark: () -> Unit,
    dropDownMenuExpanded: Boolean,
    onDismissDropdown: () -> Unit,
    maxLines: Int,
    showOnlyTitle: Boolean,
    showReadingTime: Boolean,
    modifier: Modifier = Modifier,
) {
    val snippetStyle = FeedListItemSnippetTextStyle()
    val joinedText =
        remember(item, showOnlyTitle) {
            buildAnnotatedString {
                if (item.title.isNotBlank()) {
                    append(item.title)
                    if (!showOnlyTitle && item.snippet.isNotBlank()) {
                        withStyle(snippetStyle.toSpanStyle()) {
                            append('\n')
                            append(item.snippet)
                        }
                    }
                } else {
                    append(item.snippet)
                }
            }
        }
    val closeMenuText = stringResource(id = R.string.close_menu)
    Column(
        verticalArrangement = Arrangement.spacedBy(4.dp),
        modifier =
            modifier
                .weight(1f),
    ) {
        WithBidiDeterminedLayoutDirection(paragraph = joinedText.text) {
            Text(
                text = joinedText,
                style = FeedListItemTitleTextStyle(),
                fontWeight = titleFontWeight(item.unread),
                overflow = TextOverflow.Ellipsis,
                maxLines = maxLines,
                modifier =
                    Modifier
                        .fillMaxWidth(),
            )
        }
        Box {
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                modifier =
                    Modifier
                        .fillMaxWidth(),
            ) {
                CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                    WithBidiDeterminedLayoutDirection(paragraph = item.feedTitle) {
                        Text(
                            text = item.feedTitle,
                            style = FeedListItemFeedTitleStyle(),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier =
                                Modifier
                                    .weight(1f),
                        )
                    }
                    WithBidiDeterminedLayoutDirection(paragraph = item.pubDate) {
                        Text(
                            text = item.pubDate,
                            style = FeedListItemDateStyle(),
                            maxLines = 1,
                            overflow = TextOverflow.Clip,
                            modifier = Modifier,
                        )
                    }
                }
            }
            DropdownMenu(
                expanded = dropDownMenuExpanded,
                onDismissRequest = onDismissDropdown,
                modifier = Modifier.onKeyEventLikeEscape(onDismissDropdown),
            ) {
                DropdownMenuItem(
                    onClick = {
                        onDismissDropdown()
                        onOpenFeedItemInReader()
                    },
                    text = {
                        Text(
                            text = stringResource(id = R.string.open_article_in_reader),
                        )
                    },
                )
                DropdownMenuItem(
                    onClick = {
                        onDismissDropdown()
                        onOpenFeedItemInCustomTab()
                    },
                    text = {
                        Text(
                            text = stringResource(id = R.string.open_article_in_custom_tab),
                        )
                    },
                )
                DropdownMenuItem(
                    onClick = {
                        onDismissDropdown()
                        onOpenFeedItemInBrowser()
                    },
                    text = {
                        Text(
                            text = stringResource(id = R.string.open_article_in_default_browser),
                        )
                    },
                )
                DropdownMenuItem(
                    onClick = {
                        onDismissDropdown()
                        onToggleBookmark()
                    },
                    text = {
                        Text(
                            text =
                                stringResource(
                                    if (item.bookmarked) R.string.unsave_article else R.string.save_article,
                                ),
                        )
                    },
                )
                DropdownMenuItem(
                    onClick = {
                        onDismissDropdown()
                        onMarkAboveAsRead()
                    },
                    text = {
                        Text(
                            text = stringResource(id = R.string.mark_items_above_as_read),
                        )
                    },
                )
                DropdownMenuItem(
                    onClick = {
                        onDismissDropdown()
                        onMarkBelowAsRead()
                    },
                    text = {
                        Text(
                            text = stringResource(id = R.string.mark_items_below_as_read),
                        )
                    },
                )
                DropdownMenuItem(
                    onClick = {
                        onDismissDropdown()
                        onShareItem()
                    },
                    text = {
                        Text(
                            text = stringResource(R.string.share),
                        )
                    },
                )
                DropdownMenuItem(
                    onClick = onDismissDropdown,
                    text = {},
                    modifier =
                        Modifier
                            .height(0.dp)
                            .safeSemantics {
                                contentDescription = closeMenuText
                                role = Role.Button
                            },
                )
            }
        }
        if (showReadingTime) {
            val readTimeSecs =
                remember(item.wordCount) {
                    wordsToReadTimeSecs(item.wordCount)
                }
            if (readTimeSecs > 0) {
                CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                    val seconds = "%02d".format(readTimeSecs % 60)
                    val readTimeText =
                        pluralStringResource(id = R.plurals.n_minutes, count = readTimeSecs / 60)
                            .format(
                                "${readTimeSecs / 60}:$seconds",
                            )
                    val wordCountText =
                        pluralStringResource(id = R.plurals.n_words, count = item.wordCount)
                            .format(item.wordCount)
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        modifier =
                            Modifier
                                .fillMaxWidth(),
                    ) {
                        WithBidiDeterminedLayoutDirection(paragraph = readTimeText) {
                            Text(
                                text = readTimeText,
                                style = FeedListItemFeedTitleStyle(),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.weight(1f),
                            )
                        }
                        WithBidiDeterminedLayoutDirection(paragraph = wordCountText) {
                            Text(
                                text = wordCountText,
                                style = FeedListItemFeedTitleStyle(),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
@PreviewThemes
@Suppress("ktlint:standard:max-line-length")
private fun Preview() {
    PreviewTheme {
        FeedItemCard(
            item =
                FeedListItem(
                    title = "item.title",
                    snippet = "item.snippet",
                    feedTitle = "item.feedTitle",
                    pubDate = "item.pubDate",
                    unread = true,
                    image = null,
                    link = null,
                    id = ID_UNSET,
                    bookmarked = true,
                    feedImageUrl = null,
                    primarySortTime = Instant.EPOCH,
                    rawPubDate = null,
                    wordCount = 588,
                ),
            showThumbnail = true,
            onOpenFeedItemInReader = {},
            onOpenFeedItemInCustomTab = {},
            onOpenFeedItemInBrowser = {},
            onMarkAboveAsRead = {},
            onMarkBelowAsRead = {},
            onShareItem = {},
            onToggleBookmark = {},
            dropDownMenuExpanded = false,
            onDismissDropdown = {},
            bookmarkIndicator = true,
            maxLines = 2,
            showOnlyTitle = false,
            showReadingTime = true,
        )
    }
}

@Composable
@PreviewThemes
@Suppress("ktlint:standard:max-line-length")
private fun PreviewWithEmptyTitle() {
    PreviewTheme {
        FeedItemCard(
            item =
                FeedListItem(
                    title = "",
                    snippet = "This is a snippet when there is no title present.",
                    feedTitle = "Feed Without Titles",
                    pubDate = "Jun 9, 2021",
                    unread = true,
                    image = null,
                    link = null,
                    id = ID_UNSET,
                    bookmarked = false,
                    feedImageUrl = null,
                    primarySortTime = Instant.EPOCH,
                    rawPubDate = null,
                    wordCount = 300,
                ),
            showThumbnail = true,
            onOpenFeedItemInReader = {},
            onOpenFeedItemInCustomTab = {},
            onOpenFeedItemInBrowser = {},
            onMarkAboveAsRead = {},
            onMarkBelowAsRead = {},
            onShareItem = {},
            onToggleBookmark = {},
            dropDownMenuExpanded = false,
            onDismissDropdown = {},
            bookmarkIndicator = true,
            maxLines = 2,
            showOnlyTitle = false,
            showReadingTime = true,
        )
    }
}

@Composable
@PreviewThemes
@Suppress("ktlint:standard:max-line-length")
private fun PreviewWithImageUnread() {
    PreviewTheme {
        Box(
            modifier = Modifier.width((300 - 2 * 16).dp),
        ) {
            FeedItemCard(
                item =
                    FeedListItem(
                        title = "item.title",
                        snippet = "item.snippet",
                        feedTitle = "item.feedTitle",
                        pubDate = "item.pubDate",
                        unread = true,
                        image = MediaImage("blabal"),
                        link = null,
                        id = ID_UNSET,
                        bookmarked = false,
                        feedImageUrl = URL("https://foo/bar.png"),
                        primarySortTime = Instant.EPOCH,
                        rawPubDate = null,
                        wordCount = 939,
                    ),
                showThumbnail = true,
                onOpenFeedItemInReader = {},
                onOpenFeedItemInCustomTab = {},
                onOpenFeedItemInBrowser = {},
                onMarkAboveAsRead = {},
                onMarkBelowAsRead = {},
                onShareItem = {},
                onToggleBookmark = {},
                dropDownMenuExpanded = false,
                onDismissDropdown = {},
                bookmarkIndicator = true,
                maxLines = 2,
                showOnlyTitle = false,
                showReadingTime = true,
            )
        }
    }
}

@Composable
@PreviewThemes
@Suppress("ktlint:standard:max-line-length")
private fun PreviewWithImageRead() {
    PreviewTheme {
        Box(
            modifier = Modifier.width((300 - 2 * 16).dp),
        ) {
            FeedItemCard(
                item =
                    FeedListItem(
                        title = "title can be one line",
                        snippet = "snippet which is quite long as you might expect from a snipper of a story. It keeps going and going and going and going and going and going and going and going and going and going and going and going and going and going and going and going and going and going and going and going and going and going and going and going and going and going and going and going and going and going and going and going and going and going and snowing",
                        feedTitle = "Super Duper Feed",
                        pubDate = "Jun 9, 2021",
                        unread = false,
                        image = MediaImage("blabal"),
                        link = null,
                        id = ID_UNSET,
                        bookmarked = true,
                        feedImageUrl = null,
                        primarySortTime = Instant.EPOCH,
                        rawPubDate = null,
                        wordCount = 910,
                    ),
                showThumbnail = true,
                onOpenFeedItemInReader = {},
                onOpenFeedItemInCustomTab = {},
                onOpenFeedItemInBrowser = {},
                onMarkAboveAsRead = {},
                onMarkBelowAsRead = {},
                onShareItem = {},
                onToggleBookmark = {},
                dropDownMenuExpanded = false,
                onDismissDropdown = {},
                bookmarkIndicator = true,
                maxLines = 2,
                showOnlyTitle = false,
                showReadingTime = true,
            )
        }
    }
}
