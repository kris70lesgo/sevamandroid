package com.sevam.features.home.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.sevam.core.common.model.PromoBanner
import com.sevam.core.common.model.ServiceCategory
import com.sevam.core.common.model.ServiceItem
import com.sevam.core.ui.SevamBadge
import com.sevam.core.ui.SevamCard
import com.sevam.core.ui.SevamColors
import com.sevam.core.ui.SevamDivider
import com.sevam.core.ui.SevamDot
import com.sevam.core.ui.SevamPriceLabel
import com.sevam.core.ui.SevamPrimaryButton
import com.sevam.core.ui.SevamRemoteImage
import com.sevam.core.ui.SevamSectionHeader
import com.sevam.core.ui.SevamSecondaryButton
import com.sevam.core.ui.SevamTrustChip

@Composable
fun HomeScreen(
    banner: PromoBanner,
    categories: List<ServiceCategory>,
    flashDeals: List<ServiceItem>,
    nearbyServices: List<ServiceItem>,
    recentBookings: List<Booking>,
    referralCode: String,
    trustHighlights: List<String>,
    onBrowseServices: () -> Unit,
    onViewAllServices: () -> Unit,
    onServiceClick: (String) -> Unit,
    onBookNow: (String) -> Unit,
    onRebook: (String) -> Unit,
) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(22.dp),
    ) {
        item {
            HeroBanner(
                banner = banner,
                onBrowseServices = onBrowseServices,
                onViewAllServices = onViewAllServices,
            )
        }
        item {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                SevamSectionHeader(
                    title = "Browse by Category",
                    subtitle = "Find the perfect service for every need",
                    actionLabel = "View all",
                    onAction = onViewAllServices,
                )
                LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(categories.filter { it.id != "all" }) { category ->
                        CategoryCard(category = category, onClick = onViewAllServices)
                    }
                }
            }
        }
        item {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text("Flash Deals", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
                        SevamBadge(text = "Ends in 02:42:02")
                    }
                    Text("View All Deals", color = SevamColors.Orange, style = MaterialTheme.typography.labelLarge)
                }
                LazyRow(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                    items(flashDeals) { service ->
                        DealCard(service = service, onBookNow = { onBookNow(service.id) }, onClick = { onServiceClick(service.id) })
                    }
                }
            }
        }
        item {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                SevamSectionHeader(
                    title = "Popular Near You",
                    subtitle = "Fast-moving services around your selected location",
                    actionLabel = "See more",
                    onAction = onViewAllServices,
                )
                LazyRow(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                    items(nearbyServices) { service ->
                        CompactServiceCard(
                            service = service,
                            ctaText = "Book Now",
                            onClick = { onServiceClick(service.id) },
                            onCta = { onBookNow(service.id) },
                        )
                    }
                }
            }
        }
        item {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                SevamSectionHeader(
                    title = "Book Again",
                    subtitle = "Your recently booked services",
                    actionLabel = "View all bookings",
                    onAction = onViewAllServices,
                )
                LazyRow(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                    items(recentBookings) { booking ->
                        CompactServiceCard(
                            service = booking.service,
                            metadata = booking.dateLabel,
                            ctaText = "Rebook",
                            onClick = { onServiceClick(booking.service.id) },
                            onCta = { onRebook(booking.id) },
                        )
                    }
                }
            }
        }
        item {
            ReferAndEarnCard(referralCode = referralCode)
        }
        item {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                SevamSectionHeader(title = "Why Customers Choose Sevam")
                LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    items(trustHighlights) { highlight ->
                        SevamTrustChip(highlight)
                    }
                }
            }
        }
    }
}

@Composable
private fun HeroBanner(
    banner: PromoBanner,
    onBrowseServices: () -> Unit,
    onViewAllServices: () -> Unit,
) {
    Surface(
        shape = RoundedCornerShape(28.dp),
        color = SevamColors.Navy,
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp),
        ) {
            SevamBadge(text = banner.locationLabel, accentColor = Color.White, containerColor = Color.White.copy(alpha = 0.12f))
            Text(
                text = banner.title,
                style = MaterialTheme.typography.headlineMedium,
                color = Color.White,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = banner.subtitle,
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White.copy(alpha = 0.86f),
            )
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                SevamPrimaryButton(text = banner.primaryAction, modifier = Modifier.fillMaxWidth(), onClick = onBrowseServices)
                SevamSecondaryButton(text = banner.secondaryAction, modifier = Modifier.fillMaxWidth(), onClick = onViewAllServices)
            }
            LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                items(banner.highlights) { highlight ->
                    Surface(
                        shape = RoundedCornerShape(999.dp),
                        color = Color.White.copy(alpha = 0.08f),
                    ) {
                        Text(
                            text = highlight,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            color = Color.White,
                            style = MaterialTheme.typography.labelLarge,
                        )
                    }
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                SevamDot(active = true)
                SevamDot(active = false)
                SevamDot(active = false)
            }
        }
    }
}

@Composable
private fun CategoryCard(category: ServiceCategory, onClick: () -> Unit) {
    SevamCard(modifier = Modifier.width(140.dp).clickable(onClick = onClick)) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Box(
                modifier = Modifier
                    .background(SevamColors.OrangeContainer, RoundedCornerShape(18.dp))
                    .padding(horizontal = 14.dp, vertical = 18.dp),
            ) {
                Text(text = category.title.take(2).uppercase(), color = SevamColors.Orange, fontWeight = FontWeight.Bold)
            }
            Text(text = category.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            Text(text = "${category.serviceCount} services", style = MaterialTheme.typography.bodySmall, color = SevamColors.TextSecondary)
        }
    }
}

@Composable
private fun DealCard(
    service: ServiceItem,
    onBookNow: () -> Unit,
    onClick: () -> Unit,
) {
    SevamCard(modifier = Modifier.width(280.dp).clickable(onClick = onClick)) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Box {
                SevamRemoteImage(imageUrl = service.imageUrl, modifier = Modifier.fillMaxWidth().height(170.dp))
                service.badge?.let { badge ->
                    SevamBadge(text = badge, modifier = Modifier.padding(12.dp))
                }
            }
            Column(modifier = Modifier.padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(text = service.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                SevamPriceLabel(
                    price = service.price,
                    originalPrice = service.originalPrice,
                    savingsText = service.originalPrice?.let { "Save Rs ${it - service.price}" },
                )
            }
            SevamPrimaryButton(
                text = "Book Now",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 16.dp),
                onClick = onBookNow,
            )
        }
    }
}

@Composable
private fun CompactServiceCard(
    service: ServiceItem,
    metadata: String = "${service.rating} (${service.reviewCount} reviews)",
    ctaText: String,
    onClick: () -> Unit,
    onCta: () -> Unit,
) {
    SevamCard(modifier = Modifier.width(250.dp)) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            SevamRemoteImage(imageUrl = service.imageUrl, modifier = Modifier.fillMaxWidth().height(140.dp))
            Column(modifier = Modifier.padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(text = service.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                Text(text = metadata, style = MaterialTheme.typography.bodySmall, color = SevamColors.TextSecondary)
                Text(text = "Rs ${service.price} onwards", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
            }
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                SevamSecondaryButton(text = "Details", modifier = Modifier.width(100.dp), onClick = onClick)
                SevamPrimaryButton(text = ctaText, modifier = Modifier.width(110.dp), onClick = onCta)
            }
        }
    }
}

@Composable
private fun ReferAndEarnCard(referralCode: String) {
    Surface(shape = RoundedCornerShape(28.dp), color = SevamColors.Navy) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp),
        ) {
            Text(
                text = "Refer & Earn Rs 500",
                style = MaterialTheme.typography.headlineSmall,
                color = Color.White,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = "Invite your friends to Sevam and both of you unlock booking rewards after their first service.",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White.copy(alpha = 0.84f),
            )
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Surface(shape = RoundedCornerShape(18.dp), color = Color.White.copy(alpha = 0.08f), modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("Your referral code", color = Color.White.copy(alpha = 0.7f), style = MaterialTheme.typography.labelLarge)
                        Text(referralCode, color = Color.White, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                    }
                }
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        TrustTile("Max Earnings", "Rs 5,000")
                        TrustTile("Friend Saves", "Rs 200")
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        TrustTile("Per Referral", "Rs 500")
                        TrustTile("Active Users", "2M+")
                    }
                }
            }
        }
    }
}

@Composable
private fun TrustTile(title: String, value: String) {
    Surface(shape = RoundedCornerShape(18.dp), color = Color.White.copy(alpha = 0.08f), modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(text = title, style = MaterialTheme.typography.labelMedium, color = Color.White.copy(alpha = 0.7f))
            Text(text = value, style = MaterialTheme.typography.titleMedium, color = Color.White, fontWeight = FontWeight.Bold)
        }
    }
}
