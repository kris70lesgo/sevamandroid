package com.sevam.features.bookings.presentation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Call
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material.icons.outlined.SupportAgent
import androidx.compose.material.icons.outlined.Verified
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.sevam.core.common.model.Booking
import com.sevam.core.common.model.BookingStage
import com.sevam.core.ui.SevamColors
import com.sevam.core.ui.SevamRemoteImage
import kotlin.math.min

private val BookingBlue = Color(0xFF174A9C)
private val BookingBlueSoft = Color(0xFFEAF2FF)
private val BookingText = Color(0xFF111827)
private val BookingMuted = Color(0xFF667085)
private val BookingBg = Color(0xFFF6F8FC)
private val Danger = Color(0xFFE5484D)
private val DangerSoft = Color(0xFFFFF1F1)

@Composable
fun BookingsScreen(
    selectedStage: BookingStage,
    activeBookings: List<Booking>,
    upcomingBookings: List<Booking>,
    pastBookings: List<Booking>,
    onStageSelected: (BookingStage) -> Unit,
    onOpenTracking: (String) -> Unit,
    onRebook: (String) -> Unit,
    onOpenSupport: (String) -> Unit,
    onScrollProgressChanged: (Float) -> Unit = {},
) {
    val listState = rememberLazyListState()
    val headerCollapseProgress by remember {
        derivedStateOf {
            val scrollDistance = if (listState.firstVisibleItemIndex > 0) {
                360
            } else {
                listState.firstVisibleItemScrollOffset
            }
            min(scrollDistance / 360f, 1f)
        }
    }
    val bookings = when (selectedStage) {
        BookingStage.ACTIVE -> activeBookings
        BookingStage.UPCOMING -> upcomingBookings
        BookingStage.PAST -> pastBookings
    }
    val stageCounts = mapOf(
        BookingStage.ACTIVE to activeBookings.size,
        BookingStage.UPCOMING to upcomingBookings.size,
        BookingStage.PAST to pastBookings.size,
    )

    LaunchedEffect(headerCollapseProgress) {
        onScrollProgressChanged(headerCollapseProgress)
    }

    LazyColumn(
        state = listState,
        modifier = Modifier
            .fillMaxSize()
            .background(BookingBg),
        contentPadding = PaddingValues(start = 16.dp, top = 16.dp, end = 16.dp, bottom = 28.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item {
            BookingStageTabs(
                selectedStage = selectedStage,
                counts = stageCounts,
                onStageSelected = onStageSelected,
            )
        }
        if (selectedStage == BookingStage.ACTIVE && activeBookings.isNotEmpty()) {
            item {
                ActiveBookingCard(
                    booking = activeBookings.first(),
                    onOpenSupport = { onOpenSupport(activeBookings.first().id) },
                )
            }
        } else {
            items(bookings, key = { it.id }) { booking ->
                BookingSummaryCard(
                    booking = booking,
                    onPrimaryAction = {
                        when (booking.stage) {
                            BookingStage.UPCOMING -> onOpenSupport(booking.id)
                            BookingStage.PAST -> onOpenSupport(booking.id)
                            BookingStage.ACTIVE -> onOpenTracking(booking.id)
                        }
                    },
                    onSecondaryAction = {
                        if (booking.stage == BookingStage.UPCOMING) onOpenSupport(booking.id) else onRebook(booking.id)
                    },
                )
            }
        }
    }
}

@Composable
private fun BookingStageTabs(
    selectedStage: BookingStage,
    counts: Map<BookingStage, Int>,
    onStageSelected: (BookingStage) -> Unit,
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        color = Color.White,
        border = BorderStroke(1.dp, SevamColors.Border),
        shadowElevation = 1.dp,
    ) {
        Row(
            modifier = Modifier.padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            BookingStage.entries.forEach { stage ->
                val selected = stage == selectedStage
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(18.dp))
                        .clickable { onStageSelected(stage) },
                    shape = RoundedCornerShape(18.dp),
                    color = if (selected) BookingBlue else Color.Transparent,
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 11.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = stage.label(),
                            style = MaterialTheme.typography.labelLarge,
                            color = if (selected) Color.White else BookingText,
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                        val count = counts[stage].orEmptyCount()
                        if (count > 0) {
                            Spacer(modifier = Modifier.width(6.dp))
                            CountPill(count = count, selected = selected)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CountPill(count: Int, selected: Boolean) {
    Surface(
        shape = CircleShape,
        color = if (selected) Color.White.copy(alpha = 0.18f) else SevamColors.OrangeContainer,
    ) {
        Text(
            text = count.toString(),
            modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp),
            style = MaterialTheme.typography.labelSmall,
            color = if (selected) Color.White else SevamColors.Orange,
            fontWeight = FontWeight.Bold,
        )
    }
}

@Composable
private fun ActiveBookingCard(
    booking: Booking,
    onOpenSupport: () -> Unit,
) {
    PremiumCard {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            ActiveBookingHero(booking = booking)
            BookingDivider()
            ServiceMetaPanel(booking = booking)
            booking.worker?.let { worker ->
                BookingDivider()
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                        Surface(shape = CircleShape, color = BookingBlueSoft) {
                            Box(modifier = Modifier.size(52.dp), contentAlignment = Alignment.Center) {
                                Text(
                                    text = worker.name.take(1),
                                    color = BookingBlue,
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                )
                            }
                        }
                        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                                Text(
                                    text = worker.name,
                                    style = MaterialTheme.typography.titleMedium,
                                    color = BookingText,
                                    fontWeight = FontWeight.SemiBold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                )
                                if (worker.isVerified) {
                                    Icon(
                                        imageVector = Icons.Outlined.Verified,
                                        contentDescription = null,
                                        tint = SevamColors.Success,
                                        modifier = Modifier.size(15.dp),
                                    )
                                }
                            }
                            Text(worker.role, style = MaterialTheme.typography.bodySmall, color = BookingMuted)
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Outlined.Star, contentDescription = null, tint = Color(0xFFFFB020), modifier = Modifier.size(14.dp))
                                Text(
                                    text = "${worker.rating} rating",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = BookingText,
                                    fontWeight = FontWeight.SemiBold,
                                )
                                Text("• ${worker.completedJobs} jobs", style = MaterialTheme.typography.labelMedium, color = BookingMuted)
                            }
                        }
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                BookingActionButton(
                    text = "Support",
                    icon = Icons.Outlined.SupportAgent,
                    modifier = Modifier.weight(1f),
                    style = ActionStyle.Secondary,
                    onClick = onOpenSupport,
                )
                BookingActionButton(
                    text = "Call",
                    icon = Icons.Outlined.Call,
                    modifier = Modifier.weight(1f),
                    style = ActionStyle.Primary,
                    onClick = onOpenSupport,
                )
                BookingActionButton(
                    text = "Cancel",
                    modifier = Modifier.weight(1f),
                    style = ActionStyle.Danger,
                    onClick = onOpenSupport,
                )
            }
        }
    }
}

@Composable
private fun ActiveBookingHero(booking: Booking) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top,
            ) {
                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    StatusBadge(text = booking.statusLabel, stage = BookingStage.ACTIVE)
                    Text(
                        text = booking.service.name,
                        style = MaterialTheme.typography.titleLarge,
                        color = BookingText,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Text(
                        text = "Rs ${booking.totalAmount} • ${booking.paymentMethod}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = BookingMuted,
                    )
                }
                Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text("Booking ID", style = MaterialTheme.typography.labelSmall, color = BookingMuted)
                    Text(
                        text = booking.bookingReference,
                        style = MaterialTheme.typography.labelLarge,
                        color = BookingBlue,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
    }
}
@Composable
private fun BookingDivider() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(SevamColors.Border.copy(alpha = 0.72f)),
    )
}

@Composable
private fun ServiceMetaPanel(booking: Booking) {
    Column(
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
            BookingInfoRow(
                icon = Icons.Outlined.CalendarMonth,
                text = "${booking.dateLabel} • ${booking.timeLabel}",
            )
            BookingInfoRow(
                icon = Icons.Outlined.LocationOn,
                text = "${booking.address.line1}, ${booking.address.line2}, ${booking.address.city}",
            )
    }
}

@Composable
private fun BookingSummaryCard(
    booking: Booking,
    onPrimaryAction: () -> Unit,
    onSecondaryAction: () -> Unit,
) {
    PremiumCard {
        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.Top) {
                SevamRemoteImage(
                    imageUrl = booking.service.imageUrl,
                    modifier = Modifier
                        .size(82.dp)
                        .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(18.dp)),
                )
                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    StatusBadge(text = booking.statusLabel, stage = booking.stage)
                    Text(
                        text = booking.service.name,
                        style = MaterialTheme.typography.titleMedium,
                        color = BookingText,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                    BookingInfoRow(
                        icon = Icons.Outlined.CalendarMonth,
                        text = "${booking.dateLabel} • ${booking.timeLabel}",
                        compact = true,
                    )
                }
                Text(
                    text = "Rs ${booking.totalAmount}",
                    style = MaterialTheme.typography.titleMedium,
                    color = BookingText,
                    fontWeight = FontWeight.Bold,
                )
            }
            BookingInfoRow(
                icon = Icons.Outlined.LocationOn,
                text = "${booking.address.label} • ${booking.address.line2}, ${booking.address.city}",
            )
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = statusStripColor(booking.stage),
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 13.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = when (booking.stage) {
                            BookingStage.UPCOMING -> "Appointment scheduled"
                            BookingStage.PAST -> "Service completed"
                            BookingStage.ACTIVE -> "Service in progress"
                        },
                        style = MaterialTheme.typography.labelLarge,
                        color = statusTextColor(booking.stage),
                        fontWeight = FontWeight.SemiBold,
                    )
                    Text(
                        text = booking.bookingReference,
                        style = MaterialTheme.typography.labelMedium,
                        color = BookingMuted,
                        maxLines = 1,
                    )
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                when (booking.stage) {
                    BookingStage.UPCOMING -> {
                        BookingActionButton(
                            text = "Reschedule",
                            modifier = Modifier.weight(1f),
                            style = ActionStyle.Secondary,
                            onClick = onPrimaryAction,
                        )
                        BookingActionButton(
                            text = "Cancel",
                            modifier = Modifier.weight(1f),
                            style = ActionStyle.Danger,
                            onClick = onSecondaryAction,
                        )
                    }
                    BookingStage.PAST -> {
                        BookingActionButton(
                            text = "Details",
                            modifier = Modifier.weight(1f),
                            style = ActionStyle.Secondary,
                            onClick = onPrimaryAction,
                        )
                        BookingActionButton(
                            text = "Rebook",
                            modifier = Modifier.weight(1f),
                            style = ActionStyle.Primary,
                            onClick = onSecondaryAction,
                        )
                    }
                    BookingStage.ACTIVE -> {
                        BookingActionButton(
                            text = "Support",
                            modifier = Modifier.weight(1f),
                            style = ActionStyle.Secondary,
                            onClick = onSecondaryAction,
                        )
                        BookingActionButton(
                            text = "Call",
                            icon = Icons.Outlined.Call,
                            modifier = Modifier.weight(1f),
                            style = ActionStyle.Primary,
                            onClick = onPrimaryAction,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PremiumCard(content: @Composable () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(6.dp, RoundedCornerShape(24.dp), clip = false),
        shape = RoundedCornerShape(24.dp),
        color = Color.White,
        border = BorderStroke(1.dp, Color(0xFFE8EDF5)),
    ) {
        Box(modifier = Modifier.padding(14.dp)) {
            content()
        }
    }
}

@Composable
private fun BookingInfoRow(
    icon: ImageVector,
    text: String,
    compact: Boolean = false,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(7.dp),
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (compact) BookingBlue else BookingMuted,
            modifier = Modifier.size(if (compact) 14.dp else 16.dp),
        )
        Text(
            text = text,
            style = if (compact) MaterialTheme.typography.labelMedium else MaterialTheme.typography.bodySmall,
            color = BookingMuted,
            maxLines = if (compact) 1 else 2,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
private fun StatusBadge(
    text: String,
    stage: BookingStage,
) {
    Surface(
        shape = RoundedCornerShape(999.dp),
        color = statusBadgeColor(stage),
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 9.dp, vertical = 5.dp),
            style = MaterialTheme.typography.labelSmall,
            color = statusTextColor(stage),
            fontWeight = FontWeight.Bold,
            maxLines = 1,
        )
    }
}

private enum class ActionStyle {
    Primary,
    Secondary,
    Danger,
}

@Composable
private fun BookingActionButton(
    text: String,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    style: ActionStyle,
    onClick: () -> Unit,
) {
    val container = when (style) {
        ActionStyle.Primary -> SevamColors.Orange
        ActionStyle.Secondary -> Color.White
        ActionStyle.Danger -> DangerSoft
    }
    val content = when (style) {
        ActionStyle.Primary -> Color.White
        ActionStyle.Secondary -> BookingBlue
        ActionStyle.Danger -> Danger
    }
    val border = when (style) {
        ActionStyle.Primary -> SevamColors.Orange
        ActionStyle.Secondary -> Color(0xFFD7E2F4)
        ActionStyle.Danger -> Color(0xFFFFD4D4)
    }

    Surface(
        modifier = modifier
            .height(46.dp)
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .border(1.dp, border, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        color = container,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (icon != null) {
                Icon(icon, contentDescription = null, tint = content, modifier = Modifier.size(17.dp))
                Spacer(modifier = Modifier.width(6.dp))
            }
            Text(
                text = text,
                style = MaterialTheme.typography.labelLarge,
                color = content,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

private fun statusBadgeColor(stage: BookingStage): Color = when (stage) {
    BookingStage.ACTIVE -> SevamColors.SuccessContainer
    BookingStage.UPCOMING -> BookingBlueSoft
    BookingStage.PAST -> Color(0xFFF0FDF4)
}

private fun statusStripColor(stage: BookingStage): Color = when (stage) {
    BookingStage.ACTIVE -> BookingBlueSoft
    BookingStage.UPCOMING -> Color(0xFFF3F7FF)
    BookingStage.PAST -> Color(0xFFF2FBF6)
}

private fun statusTextColor(stage: BookingStage): Color = when (stage) {
    BookingStage.ACTIVE -> SevamColors.Success
    BookingStage.UPCOMING -> BookingBlue
    BookingStage.PAST -> Color(0xFF12805C)
}

private fun BookingStage.label(): String = name.lowercase().replaceFirstChar(Char::uppercase)

private fun Int?.orEmptyCount(): Int = this ?: 0
