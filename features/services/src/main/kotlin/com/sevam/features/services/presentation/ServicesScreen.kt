package com.sevam.features.services.presentation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.sevam.core.common.model.ServiceCategory
import com.sevam.core.common.model.ServiceItem
import com.sevam.core.ui.SevamBadge
import com.sevam.core.ui.SevamCard
import com.sevam.core.ui.SevamColors
import com.sevam.core.ui.SevamRemoteImage
import com.sevam.core.ui.SevamSectionHeader

@Composable
fun ServicesScreen(
    categories: List<ServiceCategory>,
    services: List<ServiceItem>,
    selectedCategoryId: String,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onCategorySelected: (String) -> Unit,
    onServiceClick: (String) -> Unit,
    onAddToCart: (String) -> Unit,
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            SevamSectionHeader(
                title = "All Services",
                subtitle = "${services.size} services available",
            )
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchQueryChange,
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Search services, cleaning, AC repair...") },
                shape = RoundedCornerShape(18.dp),
            )
            LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                items(categories) { category ->
                    AssistChip(
                        onClick = { onCategorySelected(category.id) },
                        label = { Text(category.title) },
                        colors = AssistChipDefaults.assistChipColors(
                            containerColor = if (selectedCategoryId == category.id) SevamColors.OrangeContainer else MaterialTheme.colorScheme.surface,
                            labelColor = if (selectedCategoryId == category.id) SevamColors.Orange else MaterialTheme.colorScheme.onSurface,
                        ),
                        border = BorderStroke(
                            1.dp,
                            if (selectedCategoryId == category.id) SevamColors.Orange else SevamColors.Border,
                        ),
                    )
                }
            }
        }
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            items(services, key = { it.id }) { service ->
                SevamCard(modifier = Modifier.clickable { onServiceClick(service.id) }) {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Box {
                                SevamRemoteImage(imageUrl = service.imageUrl, modifier = Modifier.fillMaxWidth().height(150.dp))
                                service.badge?.let { badge ->
                                    SevamBadge(text = badge, modifier = Modifier.padding(10.dp))
                                }
                                Row(
                                modifier = Modifier
                                    .align(Alignment.BottomStart)
                                    .padding(10.dp)
                                    .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.88f), RoundedCornerShape(999.dp))
                                    .padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                            ) {
                                Icon(Icons.Outlined.AccessTime, contentDescription = null, modifier = Modifier.size(14.dp), tint = SevamColors.TextSecondary)
                                Text(service.durationLabel, style = MaterialTheme.typography.labelMedium)
                            }
                        }
                        Column(modifier = Modifier.padding(horizontal = 14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(text = service.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                            Text(text = service.description, style = MaterialTheme.typography.bodySmall, color = SevamColors.TextSecondary, maxLines = 2)
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                                Text("Rs ${service.price}", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                                service.originalPrice?.let {
                                    Text("Rs $it", style = MaterialTheme.typography.bodySmall, color = SevamColors.TextSecondary)
                                }
                            }
                        }
                        Box(
                            modifier = Modifier
                                .padding(14.dp)
                                .align(Alignment.End)
                                .background(SevamColors.Orange, CircleShape)
                                .size(44.dp)
                                .clickable { onAddToCart(service.id) },
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(Icons.Outlined.Add, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimary)
                        }
                    }
                }
            }
        }
    }
}
