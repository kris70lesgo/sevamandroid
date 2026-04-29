package com.sevam.features.tracking.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Call
import androidx.compose.material.icons.outlined.Map
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.sevam.core.common.model.Booking
import com.sevam.core.ui.SevamCard
import com.sevam.core.ui.SevamColors
import com.sevam.core.ui.SevamPrimaryButton
import com.sevam.core.ui.SevamSecondaryButton

@Composable
fun TrackingScreen(
    booking: Booking,
    onCallWorker: () -> Unit,
    onContactSupport: () -> Unit,
) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item {
            SevamCard {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    Text(booking.service.name, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                    Text("Booking ${booking.bookingReference}", color = SevamColors.TextSecondary)
                    Text("ETA: ${booking.etaLabel ?: "Live update pending"}", color = SevamColors.Orange, fontWeight = FontWeight.SemiBold)
                }
            }
        }
        item {
            Surface(shape = RoundedCornerShape(28.dp), color = SevamColors.Navy) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Outlined.Map, contentDescription = null, tint = Color.White)
                        Text("Live Tracking", style = MaterialTheme.typography.titleLarge, color = Color.White, fontWeight = FontWeight.SemiBold)
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(260.dp)
                            .background(Color.White.copy(alpha = 0.1f), RoundedCornerShape(24.dp)),
                        contentAlignment = Alignment.Center,
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text("Mapbox tracking surface ready", color = Color.White, fontWeight = FontWeight.SemiBold)
                            Text(
                                "Add your Mapbox access token and realtime coordinates to render the live map here.",
                                color = Color.White.copy(alpha = 0.82f),
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(horizontal = 24.dp),
                            )
                        }
                    }
                }
            }
        }
        item {
            SevamCard {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Text("Worker Status", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
                    booking.worker?.let { worker ->
                        Text("${worker.name} • ${worker.role}", style = MaterialTheme.typography.titleMedium)
                    }
                    Text(
                        booking.supportHint ?: "If the worker is delayed or location data drops, support can step in quickly.",
                        color = SevamColors.TextSecondary,
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        SevamSecondaryButton(text = "Call Worker", modifier = Modifier.weight(1f), onClick = onCallWorker)
                        SevamPrimaryButton(text = "Support", modifier = Modifier.weight(1f), onClick = onContactSupport)
                    }
                }
            }
        }
    }
}
