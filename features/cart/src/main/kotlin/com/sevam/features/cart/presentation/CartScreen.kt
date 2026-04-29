package com.sevam.features.cart.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Remove
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.sevam.core.common.model.Address
import com.sevam.core.common.model.CartEntry
import com.sevam.core.common.model.CartTotals
import com.sevam.core.ui.FramedMetaChip
import com.sevam.core.ui.SevamCard
import com.sevam.core.ui.SevamColors
import com.sevam.core.ui.SevamPrimaryButton
import com.sevam.core.ui.SevamRemoteImage
import com.sevam.core.ui.SevamSecondaryButton
import com.sevam.core.ui.SevamSectionHeader

@Composable
fun CartScreen(
    entries: List<CartEntry>,
    totals: CartTotals,
    selectedAddress: Address?,
    couponEnabled: Boolean,
    onIncrease: (String) -> Unit,
    onDecrease: (String) -> Unit,
    onSelectAddress: () -> Unit,
    onBrowseServices: () -> Unit,
    onProceedToCheckout: () -> Unit,
) {
    if (entries.isEmpty()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text("Your cart is empty", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Text(
                text = "Add services from the catalog to start a booking.",
                style = MaterialTheme.typography.bodyLarge,
                color = SevamColors.TextSecondary,
                modifier = Modifier.padding(top = 8.dp, bottom = 20.dp),
            )
            SevamPrimaryButton(text = "Browse Services", onClick = onBrowseServices)
        }
        return
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item {
            SevamSectionHeader(
                title = "Your Cart",
                subtitle = "${entries.sumOf { it.quantity }} items ready to book",
            )
        }
        items(entries, key = { it.service.id }) { entry ->
            SevamCard {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                ) {
                    SevamRemoteImage(imageUrl = entry.service.imageUrl, modifier = Modifier.size(100.dp))
                    Column(modifier = Modifier.weight(1.5f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(entry.service.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                        Text(entry.service.durationLabel, style = MaterialTheme.typography.bodySmall, color = SevamColors.TextSecondary)
                        Text("Rs ${entry.service.price}", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            IconButton(onClick = { onDecrease(entry.service.id) }) {
                                Icon(Icons.Outlined.Remove, contentDescription = "Decrease")
                            }
                            Text(entry.quantity.toString(), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            IconButton(onClick = { onIncrease(entry.service.id) }) {
                                Icon(Icons.Outlined.Add, contentDescription = "Increase")
                            }
                        }
                    }
                }
            }
        }
        item {
            SevamCard {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    SevamSectionHeader(title = "Service Address")
                    Surface(shape = RoundedCornerShape(18.dp), color = SevamColors.SurfaceAlt) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.Top) {
                                Icon(Icons.Outlined.LocationOn, contentDescription = null, tint = SevamColors.Orange)
                                Column {
                                    Text(selectedAddress?.label ?: "Select address", fontWeight = FontWeight.SemiBold)
                                    Text(
                                        text = selectedAddress?.let { "${it.line1}, ${it.line2}, ${it.city}" } ?: "Choose where the professional should arrive",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = SevamColors.TextSecondary,
                                    )
                                }
                            }
                            OutlinedButton(onClick = onSelectAddress) {
                                Text("Change")
                            }
                        }
                    }
                    if (!couponEnabled) {
                        FramedMetaChip(text = "Coupons unlock when backend pricing rules are ready")
                    }
                }
            }
        }
        item {
            SevamCard {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    SevamSectionHeader(title = "Price Summary")
                    SummaryRow("Subtotal", "Rs ${totals.subtotal}")
                    SummaryRow("Service Fee", "Rs ${totals.serviceFee}")
                    SummaryRow("Total", "Rs ${totals.total}", emphatic = true)
                }
            }
        }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                SevamSecondaryButton(text = "Continue Browsing", modifier = Modifier.weight(1f), onClick = onBrowseServices)
                SevamPrimaryButton(text = "Proceed to Book", modifier = Modifier.weight(1f), onClick = onProceedToCheckout)
            }
        }
    }
}

@Composable
private fun SummaryRow(label: String, value: String, emphatic: Boolean = false) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(label, color = if (emphatic) MaterialTheme.colorScheme.onSurface else SevamColors.TextSecondary)
        Text(value, fontWeight = if (emphatic) FontWeight.Bold else FontWeight.Medium)
    }
}
