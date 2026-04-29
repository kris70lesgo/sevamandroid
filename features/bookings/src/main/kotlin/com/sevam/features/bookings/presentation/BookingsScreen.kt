package com.sevam.features.bookings.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Call
import androidx.compose.material.icons.outlined.Navigation
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.sevam.core.common.model.Booking
import com.sevam.core.common.model.BookingStage
import com.sevam.core.common.model.BookingStepState
import com.sevam.core.ui.SevamCard
import com.sevam.core.ui.SevamColors
import com.sevam.core.ui.SevamPrimaryButton
import com.sevam.core.ui.SevamRemoteImage
import com.sevam.core.ui.SevamSecondaryButton
import com.sevam.core.ui.SevamSectionHeader

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
) {
    val bookings = when (selectedStage) {
        BookingStage.ACTIVE -> activeBookings
        BookingStage.UPCOMING -> upcomingBookings
        BookingStage.PAST -> pastBookings
    }

    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item {
            SevamSectionHeader(
                title = "My Bookings",
                subtitle = "Track current jobs, upcoming appointments, and past orders",
            )
        }
        item {
            ScrollableTabRow(selectedTabIndex = BookingStage.entries.indexOf(selectedStage), edgePadding = 0.dp) {
                BookingStage.entries.forEach { stage ->
                    Tab(
                        selected = stage == selectedStage,
                        onClick = { onStageSelected(stage) },
                        text = { Text(stage.name.lowercase().replaceFirstChar(Char::uppercase)) },
                    )
                }
            }
        }
        if (selectedStage == BookingStage.ACTIVE && activeBookings.isNotEmpty()) {
            item {
                ActiveBookingCard(
                    booking = activeBookings.first(),
                    onOpenTracking = { onOpenTracking(activeBookings.first().id) },
                    onOpenSupport = { onOpenSupport(activeBookings.first().id) },
                )
            }
        } else {
            items(bookings, key = { it.id }) { booking ->
                BookingCard(
                    booking = booking,
                    onPrimaryAction = {
                        when (booking.stage) {
                            BookingStage.UPCOMING -> onOpenSupport(booking.id)
                            BookingStage.PAST -> onRebook(booking.id)
                            BookingStage.ACTIVE -> onOpenTracking(booking.id)
                        }
                    },
                    onSecondaryAction = {
                        if (booking.stage == BookingStage.UPCOMING) {
                            onOpenSupport(booking.id)
                        } else {
                            onRebook(booking.id)
                        }
                    },
                )
            }
        }
    }
}

@Composable
private fun ActiveBookingCard(
    booking: Booking,
    onOpenTracking: () -> Unit,
    onOpenSupport: () -> Unit,
) {
    SevamCard {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp),
        ) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(booking.service.name, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                    Text("${booking.dateLabel}  •  ${booking.timeLabel}", color = SevamColors.TextSecondary)
                    Text("${booking.address.line1}, ${booking.address.line2}, ${booking.address.city}", color = SevamColors.TextSecondary)
                }
                Surface(shape = RoundedCornerShape(999.dp), color = SevamColors.SuccessContainer) {
                    Text(
                        text = booking.statusLabel,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        color = SevamColors.Success,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            }
            booking.worker?.let { worker ->
                Surface(shape = RoundedCornerShape(18.dp), color = SevamColors.SurfaceAlt) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        horizontalArrangement = Arrangement.spacedBy(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Box(
                            modifier = Modifier
                                .background(SevamColors.OrangeContainer, CircleShape)
                                .padding(horizontal = 18.dp, vertical = 16.dp),
                        ) {
                            Text(worker.name.take(1), color = SevamColors.Orange, fontWeight = FontWeight.Bold)
                        }
                        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(worker.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                            Text("${worker.role} • ${worker.completedJobs} jobs", style = MaterialTheme.typography.bodySmall, color = SevamColors.TextSecondary)
                            Text("${worker.rating} rating • Verified", style = MaterialTheme.typography.bodySmall, color = SevamColors.Success)
                        }
                        Surface(shape = RoundedCornerShape(999.dp), color = Color.White) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                            ) {
                                androidx.compose.material3.Icon(Icons.Outlined.Call, contentDescription = null, tint = SevamColors.Orange)
                                Text("Call", color = SevamColors.Orange, fontWeight = FontWeight.Medium)
                            }
                        }
                    }
                }
            }
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("Service Progress", fontWeight = FontWeight.SemiBold)
                Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                    booking.steps.forEach { step ->
                        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                            Box(
                                modifier = Modifier
                                    .background(
                                        when (step.state) {
                                            BookingStepState.COMPLETE -> SevamColors.Success
                                            BookingStepState.CURRENT -> SevamColors.Orange
                                            BookingStepState.UPCOMING -> SevamColors.Border
                                        },
                                        CircleShape,
                                    )
                                    .padding(10.dp),
                            ) {
                                Text(
                                    text = if (step.state == BookingStepState.COMPLETE) "✓" else "",
                                    color = Color.White,
                                )
                            }
                            Text(
                                text = step.label,
                                style = MaterialTheme.typography.bodySmall,
                                color = if (step.state == BookingStepState.UPCOMING) SevamColors.TextSecondary else MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.padding(top = 6.dp),
                            )
                        }
                    }
                }
            }
            Surface(shape = RoundedCornerShape(18.dp), color = Color(0xFFFFF7E6)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Column {
                        Text("Estimated Completion", fontWeight = FontWeight.SemiBold)
                        Text("Professional is working on your service", color = SevamColors.TextSecondary, style = MaterialTheme.typography.bodySmall)
                    }
                    Text(booking.etaLabel ?: "Live", color = SevamColors.Orange, fontWeight = FontWeight.Bold)
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                SevamSecondaryButton(text = "Contact Support", modifier = Modifier.weight(1f), onClick = onOpenSupport)
                SevamPrimaryButton(text = "Live Track", modifier = Modifier.weight(1f), onClick = onOpenTracking)
            }
        }
    }
}

@Composable
private fun BookingCard(
    booking: Booking,
    onPrimaryAction: () -> Unit,
    onSecondaryAction: () -> Unit,
) {
    SevamCard {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Column(modifier = Modifier.weight(0.9f)) {
                SevamRemoteImage(imageUrl = booking.service.imageUrl, modifier = Modifier.fillMaxWidth().height(128.dp))
            }
            Column(modifier = Modifier.weight(1.3f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(booking.service.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                Text(booking.bookingReference, style = MaterialTheme.typography.labelLarge, color = SevamColors.TextSecondary)
                Text(booking.dateLabel, style = MaterialTheme.typography.bodySmall, color = SevamColors.TextSecondary)
                Text("Paid Rs ${booking.totalAmount}", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    SevamSecondaryButton(
                        text = if (booking.stage == BookingStage.UPCOMING) "Help" else "Rate",
                        modifier = Modifier.weight(1f),
                        onClick = onSecondaryAction,
                    )
                    SevamPrimaryButton(
                        text = if (booking.stage == BookingStage.UPCOMING) "Reschedule" else "Rebook",
                        modifier = Modifier.weight(1f),
                        onClick = onPrimaryAction,
                    )
                }
            }
        }
    }
}
