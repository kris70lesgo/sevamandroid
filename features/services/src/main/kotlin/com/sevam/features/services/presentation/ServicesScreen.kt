package com.sevam.features.services.presentation

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.sevam.core.common.model.ServiceCategory
import com.sevam.core.common.model.ServiceItem
import com.sevam.core.ui.SevamCard
import com.sevam.core.ui.SevamColors
import com.sevam.core.ui.SevamRemoteImage
import kotlin.math.absoluteValue

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
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item(span = { androidx.compose.foundation.lazy.grid.GridItemSpan(maxLineSpan) }) {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = onSearchQueryChange,
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = {
                        Icon(
                            Icons.Outlined.Search,
                            contentDescription = null,
                            tint = SevamColors.TextSecondary,
                            modifier = Modifier.size(18.dp),
                        )
                    },
                    placeholder = {
                        Text(
                            text = "Search services...",
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                )
            }
        }
        item(span = { androidx.compose.foundation.lazy.grid.GridItemSpan(maxLineSpan) }) {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(end = 12.dp),
            ) {
                items(categories) { category ->
                    val selected = selectedCategoryId == category.id
                    AssistChip(
                        onClick = { onCategorySelected(category.id) },
                        label = {
                            Text(
                                text = category.title,
                                style = MaterialTheme.typography.labelLarge,
                            )
                        },
                        colors = AssistChipDefaults.assistChipColors(
                            containerColor = if (selected) SevamColors.OrangeContainer else MaterialTheme.colorScheme.surface,
                            labelColor = if (selected) SevamColors.Orange else MaterialTheme.colorScheme.onSurface,
                        ),
                        border = BorderStroke(
                            1.dp,
                            if (selected) SevamColors.Orange else SevamColors.Border,
                        ),
                    )
                }
            }
        }
        items(services, key = { it.id }) { service ->
            ServiceGridCard(
                service = service,
                onClick = { onServiceClick(service.id) },
                onAddToCart = { onAddToCart(service.id) },
            )
        }
    }
}

@Composable
private fun ServiceGridCard(
    service: ServiceItem,
    onClick: () -> Unit,
    onAddToCart: () -> Unit,
) {
    SevamCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
    ) {
        Column(
            modifier = Modifier.height(218.dp),
            verticalArrangement = Arrangement.spacedBy(7.dp),
        ) {
            SevamRemoteImage(
                imageUrl = service.imageUrl,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(112.dp),
            )
            Column(
                modifier = Modifier.padding(horizontal = 10.dp),
                verticalArrangement = Arrangement.spacedBy(3.dp),
            ) {
                Text(
                    text = service.name,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    minLines = 2,
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Star,
                        contentDescription = null,
                        tint = Color(0xFF111827),
                        modifier = Modifier.size(13.dp),
                    )
                    Text(
                        text = "${service.rating} (${service.reviewCount})",
                        style = MaterialTheme.typography.labelMedium,
                        color = SevamColors.TextSecondary,
                        maxLines = 1,
                    )
                }
            }
            Spacer(modifier = Modifier.weight(1f))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp)
                    .padding(bottom = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "Rs ${service.price}",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                )
                Surface(
                    modifier = Modifier
                        .height(32.dp)
                        .fillMaxWidth(0.56f)
                        .clickable(onClick = onAddToCart),
                    shape = RoundedCornerShape(11.dp),
                    color = Color.White,
                    border = BorderStroke(1.dp, SevamColors.Border),
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = "Add",
                            color = SevamColors.Orange,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold,
                            textAlign = TextAlign.Center,
                        )
                    }
                }
            }
        }
    }
}
