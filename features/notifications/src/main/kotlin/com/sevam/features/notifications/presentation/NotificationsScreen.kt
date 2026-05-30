package com.sevam.features.notifications.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.sevam.core.common.model.NotificationItem
import com.sevam.core.ui.SevamCard
import com.sevam.core.ui.SevamColors
import com.sevam.core.ui.SevamSectionHeader

@Composable
fun NotificationsScreen(
    notifications: List<NotificationItem>,
    onNotificationClick: (NotificationItem) -> Unit,
) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item {
            SevamSectionHeader(
                title = "Notifications",
                subtitle = "Booking alerts, payment updates, and promotional messages",
            )
        }
        items(notifications, key = { it.id }) { notification ->
            SevamCard(modifier = Modifier.clickable { onNotificationClick(notification) }) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(if (notification.isUnread) SevamColors.OrangeContainer.copy(alpha = 0.35f) else MaterialTheme.colorScheme.surface)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(notification.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                        Text(notification.timeLabel, style = MaterialTheme.typography.bodySmall, color = SevamColors.TextSecondary)
                    }
                    Text(notification.body, color = SevamColors.TextSecondary)
                }
            }
        }
    }
}
