package com.sevam.features.home.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items as rowItems
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Bolt
import androidx.compose.material.icons.outlined.Brush
import androidx.compose.material.icons.outlined.Carpenter
import androidx.compose.material.icons.outlined.CleaningServices
import androidx.compose.material.icons.outlined.ElectricalServices
import androidx.compose.material.icons.outlined.LocalDining
import androidx.compose.material.icons.outlined.LocalLaundryService
import androidx.compose.material.icons.outlined.PestControl
import androidx.compose.material.icons.outlined.Plumbing
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Spa
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sevam.core.common.model.Booking
import com.sevam.core.common.model.PromoBanner
import com.sevam.core.common.model.ServiceCategory
import com.sevam.core.common.model.ServiceItem
import com.sevam.core.ui.SevamColors
import com.sevam.core.ui.SevamRemoteImage
import kotlinx.coroutines.delay
import kotlin.math.absoluteValue
import kotlin.math.roundToInt

private val HomeBlue = Color(0xFF174A9C)
private val HomeBlueDark = Color(0xFF0D2F68)
private val HomeBlueSoft = Color(0xFFEAF2FF)
private val HomeText = Color(0xFF111827)
private val HomeMuted = Color(0xFF667085)

@Composable
fun HomeScreen(
    banners: List<PromoBanner>,
    categories: List<ServiceCategory>,
    flashDeals: List<ServiceItem>,
    nearbyServices: List<ServiceItem>,
    recentBookings: List<Booking>,
    referralCode: String,
    onOpenSearch: () -> Unit,
    onBrowseServices: () -> Unit,
    onViewAllServices: () -> Unit,
    onServiceClick: (String) -> Unit,
    onBookNow: (String) -> Unit,
    onRebook: (String) -> Unit,
) {
    val heroSlides = remember(banners) {
        val seeded = if (banners.isNotEmpty()) banners else listOf(
            PromoBanner(
                id = "fallback",
                locationLabel = "Koramangala, Bangalore",
                title = "Same-Day Service, Guaranteed",
                subtitle = "Book before noon and get a verified professional at your doorstep today.",
                primaryAction = "Browse Services",
                secondaryAction = "View All Services",
                highlights = listOf("Verified Pros", "4.8+ Rated", "60-min Response"),
            ),
        )
        buildList {
            addAll(seeded.take(3))
            if (size < 3) {
                add(
                    PromoBanner(
                        id = "flash-offers",
                        locationLabel = seeded.first().locationLabel,
                        title = "Flash Deals Near You",
                        subtitle = "Discover limited-time savings on cleaning, repairs, and grooming.",
                        primaryAction = "Explore Deals",
                        secondaryAction = "Book Again",
                        highlights = listOf("Up to 50% Off", "Instant Slots", "Live Tracking"),
                    ),
                )
            }
            if (size < 3) {
                add(
                    PromoBanner(
                        id = "trusted-pros",
                        locationLabel = seeded.first().locationLabel,
                        title = "Trusted Pros at Home",
                        subtitle = "Verified professionals, transparent pricing, and support you can count on.",
                        primaryAction = "View All Services",
                        secondaryAction = "Browse Services",
                        highlights = listOf("Background Checked", "Cashless Payments", "Support 24/7"),
                    ),
                )
            }
        }.take(3)
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(start = 16.dp, top = 8.dp, end = 16.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp),
    ) {
        item {
            HomeSearchCard(onClick = onOpenSearch)
        }
        item {
            HeroBannerCarousel(
                banners = heroSlides,
                imageUrl = nearbyServices.firstOrNull()?.imageUrl ?: flashDeals.firstOrNull()?.imageUrl,
                onBrowseServices = onBrowseServices,
                onViewAllServices = onViewAllServices,
            )
        }
        item {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                HomeSectionHeader(
                    title = "Categories",
                    actionLabel = "See all",
                    onAction = onViewAllServices,
                )
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                    contentPadding = PaddingValues(end = 12.dp),
                ) {
                    rowItems(items = categories.filter { it.id != "all" }, key = { it.id }) { category ->
                        CategoryRailItem(
                            category = category,
                            onClick = onViewAllServices,
                        )
                    }
                }
            }
        }
        item {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                HomeSectionHeader(
                    title = "Popular Services",
                    actionLabel = "View all",
                    onAction = onViewAllServices,
                )
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                    contentPadding = PaddingValues(end = 12.dp),
                ) {
                    rowItems(items = nearbyServices.take(6), key = { it.id }) { service ->
                        PopularServiceCard(
                            service = service,
                            onClick = { onServiceClick(service.id) },
                            onBookNow = { onBookNow(service.id) },
                        )
                    }
                }
            }
        }
        item {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                HomeSectionHeader(
                    title = "Flash Deals",
                    subtitle = "Limited-time savings near you",
                    actionLabel = "View deals",
                    onAction = onViewAllServices,
                )
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(end = 12.dp),
                ) {
                    rowItems(items = flashDeals.take(4), key = { "deal-${it.id}" }) { service ->
                        FlashDealCard(
                            service = service,
                            onClick = { onServiceClick(service.id) },
                            onGrabDeal = { onBookNow(service.id) },
                        )
                    }
                }
            }
        }
        item {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                HomeSectionHeader(
                    title = "Book Again",
                    subtitle = "Your recently booked services",
                    actionLabel = "View all bookings",
                    onAction = onViewAllServices,
                )
                LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp), contentPadding = PaddingValues(end = 12.dp)) {
                    rowItems(items = recentBookings, key = { it.id }) { booking ->
                        RebookCard(
                            booking = booking,
                            onClick = { onServiceClick(booking.service.id) },
                            onRebook = { onRebook(booking.id) },
                        )
                    }
                }
            }
        }
        item {
            ReferAndEarnCard(referralCode = referralCode)
        }
    }
}

@Composable
private fun HomeSectionHeader(
    title: String,
    subtitle: String? = null,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom,
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            Text(
                text = title,
                fontSize = 20.sp,
                lineHeight = 25.sp,
                fontWeight = FontWeight.SemiBold,
                color = HomeText,
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = HomeMuted,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
        if (actionLabel != null && onAction != null) {
            Text(
                text = actionLabel,
                modifier = Modifier
                    .padding(start = 12.dp, bottom = if (subtitle == null) 2.dp else 4.dp)
                    .clickable(onClick = onAction),
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold,
                color = HomeBlue,
                maxLines = 1,
            )
        }
    }
}

@Composable
private fun HomeSearchCard(onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(18.dp),
        color = Color(0xFFF1F5FB),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 15.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = Icons.Outlined.Search,
                contentDescription = null,
                tint = Color(0xFF65748B),
                modifier = Modifier.size(19.dp),
            )
            Text(
                text = "Search for services...",
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF8C98AB),
            )
        }
    }
}

@Composable
private fun HeroBannerCarousel(
    banners: List<PromoBanner>,
    imageUrl: String?,
    onBrowseServices: () -> Unit,
    onViewAllServices: () -> Unit,
) {
    val pagerState = rememberPagerState(pageCount = { banners.size })

    LaunchedEffect(banners.size) {
        if (banners.size > 1) {
            while (true) {
                delay(3200)
                val nextPage = (pagerState.currentPage + 1) % banners.size
                pagerState.animateScrollToPage(nextPage)
            }
        }
    }

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxWidth(),
        ) { page ->
            HeroBanner(
                banner = banners[page],
                imageUrl = imageUrl,
                onPrimaryAction = onBrowseServices,
                onSecondaryAction = onViewAllServices,
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
        ) {
            repeat(banners.size) { index ->
                val selected = pagerState.currentPage == index
                Surface(
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .width(if (selected) 18.dp else 8.dp)
                        .height(8.dp),
                    shape = RoundedCornerShape(999.dp),
                    color = if (selected) SevamColors.Orange else SevamColors.Border,
                ) {}
            }
        }
    }
}

@Composable
private fun HeroBanner(
    banner: PromoBanner,
    imageUrl: String?,
    onPrimaryAction: () -> Unit,
    onSecondaryAction: () -> Unit,
) {
    val gradient = Brush.linearGradient(
        listOf(HomeBlueDark, HomeBlue, Color(0xFF286CD8)),
    )
    Surface(
        modifier = Modifier.shadow(8.dp, RoundedCornerShape(22.dp), clip = false),
        shape = RoundedCornerShape(22.dp),
        color = Color.Transparent,
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(188.dp)
                .background(gradient),
        ) {
            imageUrl?.let {
                SevamRemoteImage(
                    imageUrl = it,
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .padding(end = 12.dp)
                        .size(width = 138.dp, height = 146.dp),
                )
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .size(width = 164.dp, height = 188.dp)
                        .background(
                            Brush.horizontalGradient(
                                listOf(Color.Transparent, Color.White.copy(alpha = 0.12f)),
                            ),
                        ),
                )
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth(0.6f)
                    .padding(start = 16.dp, top = 14.dp, bottom = 14.dp),
                verticalArrangement = Arrangement.spacedBy(7.dp),
            ) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = Color.White.copy(alpha = 0.18f),
                ) {
                    Text(
                        text = "HOURLY SERVICE",
                        modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Medium,
                        color = Color.White,
                    )
                }
                Text(
                    text = banner.title,
                    fontSize = 21.sp,
                    lineHeight = 24.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = banner.subtitle,
                    fontSize = 12.sp,
                    lineHeight = 16.sp,
                    color = Color.White.copy(alpha = 0.92f),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                Surface(
                    modifier = Modifier.clickable(onClick = onPrimaryAction),
                    shape = RoundedCornerShape(10.dp),
                    color = Color.White,
                ) {
                    Text(
                        text = "Book Now",
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = HomeBlue,
                    )
                }
            }
        }
    }
}

@Composable
private fun CategoryRailItem(
    category: ServiceCategory,
    onClick: () -> Unit,
) {
    val accents = listOf(
        HomeBlueSoft,
        Color(0xFFE6F0FF),
        Color(0xFFF2F6FD),
        Color(0xFFEAF7FF),
    )
    val accent = accents[(category.id.hashCode().absoluteValue) % accents.size]
    val icon = iconForCategory(category.id)
    Column(
        modifier = Modifier
            .width(84.dp)
            .clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Surface(
            modifier = Modifier.shadow(2.dp, RoundedCornerShape(20.dp), clip = false),
            shape = RoundedCornerShape(20.dp),
            color = accent,
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = HomeBlue,
                    modifier = Modifier.size(24.dp),
                )
            }
        }
        Text(
            text = category.title,
            style = MaterialTheme.typography.labelMedium,
            color = HomeText,
            textAlign = TextAlign.Center,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
private fun PopularServiceCard(
    service: ServiceItem,
    onClick: () -> Unit,
    onBookNow: () -> Unit,
) {
    PremiumHomeCard(
        modifier = Modifier
            .width(158.dp)
            .clickable(onClick = onClick),
    ) {
        Column(
            modifier = Modifier.height(204.dp),
            verticalArrangement = Arrangement.spacedBy(7.dp),
        ) {
            SevamRemoteImage(
                imageUrl = service.imageUrl,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(106.dp),
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
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Star,
                        contentDescription = null,
                        tint = Color(0xFF111827),
                        modifier = Modifier.size(14.dp),
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
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = HomeText,
                )
                Surface(
                    modifier = Modifier
                        .height(31.dp)
                        .width(70.dp)
                        .clickable(onClick = onBookNow),
                    shape = RoundedCornerShape(11.dp),
                    color = Color.White,
                    border = BorderStroke(1.dp, SevamColors.Border),
                ) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            text = "Add",
                            color = SevamColors.Orange,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun FlashDealCard(
    service: ServiceItem,
    onClick: () -> Unit,
    onGrabDeal: () -> Unit,
) {
    val originalPrice = service.originalPrice ?: (service.price + 300)
    val discount = (((originalPrice - service.price).toDouble() / originalPrice) * 100).roundToInt().coerceAtLeast(8)
    val urgency = when (service.id.hashCode().absoluteValue % 3) {
        0 -> "Ends tonight"
        1 -> "Limited slots"
        else -> "Today only"
    }

    Surface(
        modifier = Modifier
            .width(236.dp)
            .shadow(6.dp, RoundedCornerShape(22.dp), clip = false)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(22.dp),
        color = Color.White,
        border = BorderStroke(1.dp, Color(0xFFE8EEF8)),
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Box {
                SevamRemoteImage(
                    imageUrl = service.imageUrl,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(104.dp),
                )
                Surface(
                    modifier = Modifier.padding(8.dp),
                    shape = RoundedCornerShape(999.dp),
                    color = SevamColors.OrangeContainer,
                ) {
                    Text(
                        text = "$discount% OFF",
                        modifier = Modifier.padding(horizontal = 9.dp, vertical = 5.dp),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = SevamColors.Orange,
                    )
                }
            }
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = dealTitleFor(service),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = HomeText,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = urgency,
                    style = MaterialTheme.typography.labelMedium,
                    color = HomeMuted,
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.Bottom,
                ) {
                    Text(
                        text = "Rs ${service.price}",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = HomeText,
                    )
                    Text(
                        text = "Rs $originalPrice",
                        style = MaterialTheme.typography.labelMedium,
                        color = HomeMuted,
                        textDecoration = TextDecoration.LineThrough,
                    )
                }
                Surface(
                    modifier = Modifier
                        .height(32.dp)
                        .clickable(onClick = onGrabDeal),
                    shape = RoundedCornerShape(999.dp),
                    color = SevamColors.Orange,
                ) {
                    Box(
                        modifier = Modifier.padding(horizontal = 13.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = "Grab Deal",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun RebookCard(
    booking: Booking,
    onClick: () -> Unit,
    onRebook: () -> Unit,
) {
    val completedText = if (booking.dateLabel.startsWith("Completed", ignoreCase = true)) {
        booking.dateLabel
    } else {
        "Completed on ${booking.dateLabel}"
    }

    PremiumHomeCard(
        modifier = Modifier
            .width(158.dp)
            .clickable(onClick = onClick),
    ) {
        Column(
            modifier = Modifier.height(204.dp),
            verticalArrangement = Arrangement.spacedBy(7.dp),
        ) {
            SevamRemoteImage(
                imageUrl = booking.service.imageUrl,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(106.dp),
            )
            Column(
                modifier = Modifier.padding(horizontal = 10.dp),
                verticalArrangement = Arrangement.spacedBy(3.dp),
            ) {
                Text(
                    text = booking.service.name,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = completedText,
                    style = MaterialTheme.typography.labelSmall,
                    color = SevamColors.TextSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
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
                    text = "Rs ${booking.service.price}",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = HomeText,
                )
                Surface(
                    modifier = Modifier
                        .height(31.dp)
                        .width(74.dp)
                        .clickable(onClick = onRebook),
                    shape = RoundedCornerShape(999.dp),
                    color = SevamColors.Orange,
                ) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            text = "Rebook",
                            textAlign = TextAlign.Center,
                            fontSize = 12.sp,
                            lineHeight = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White,
                            maxLines = 1,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PremiumHomeCard(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Surface(
        modifier = modifier.shadow(4.dp, RoundedCornerShape(20.dp), clip = false),
        shape = RoundedCornerShape(20.dp),
        color = Color.White,
        border = BorderStroke(1.dp, Color(0xFFE8EEF8)),
        content = content,
    )
}

@Composable
private fun ReferAndEarnCard(referralCode: String) {
    Surface(
        modifier = Modifier.shadow(5.dp, RoundedCornerShape(24.dp), clip = false),
        shape = RoundedCornerShape(24.dp),
        color = HomeBlue,
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Text(
                text = "Refer & Earn Rs 500",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = Color.White,
            )
            Text(
                text = "Invite your friends to Sevam and unlock booking rewards after their first service.",
                style = MaterialTheme.typography.bodySmall,
                color = Color.White.copy(alpha = 0.86f),
            )
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = Color.White.copy(alpha = 0.1f),
            ) {
                Text(
                    text = referralCode,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                    color = Color.White,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
    }
}

private fun dealTitleFor(service: ServiceItem): String {
    val name = service.name.lowercase()
    return when {
        "ac" in name -> "AC Service"
        "clean" in name -> "Deep Cleaning"
        "hair" in name || "salon" in name -> "Salon at Home"
        "plumb" in name || "pipe" in name || "tap" in name -> "Plumbing Checkup"
        else -> service.name
    }
}

private fun iconForCategory(categoryId: String): ImageVector = when (categoryId) {
    "grooming" -> Icons.Outlined.Spa
    "appliance" -> Icons.Outlined.CleaningServices
    "cleaning" -> Icons.Outlined.LocalLaundryService
    "electrical" -> Icons.Outlined.Bolt
    "plumbing" -> Icons.Outlined.Plumbing
    "carpentry" -> Icons.Outlined.Carpenter
    "pest" -> Icons.Outlined.PestControl
    "labour" -> Icons.Outlined.ElectricalServices
    "cooking" -> Icons.Outlined.LocalDining
    "painting" -> Icons.Outlined.Brush
    else -> Icons.Outlined.CleaningServices
}
