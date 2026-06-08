package com.sevam.features.home.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Bolt
import androidx.compose.material.icons.outlined.Brush
import androidx.compose.material.icons.outlined.Carpenter
import androidx.compose.material.icons.outlined.CleaningServices
import androidx.compose.material.icons.outlined.ElectricalServices
import androidx.compose.material.icons.outlined.FavoriteBorder
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
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.zIndex
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
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
import com.sevam.features.home.R
import kotlinx.coroutines.delay
import kotlin.math.absoluteValue
import kotlin.math.min
import kotlin.math.roundToInt

private val HomeBlue = Color(0xFF174A9C)
private val HomeBlueDark = Color(0xFF0D2F68)
private val HomeBlueSoft = Color(0xFFEAF2FF)
private val HomeText = Color(0xFF111827)
private val HomeMuted = Color(0xFF667085)
private val HomeMajorHeadingFont = FontFamily(
    Font(R.font.pt_sans_narrow_bold, FontWeight.Bold),
)
private val PoppinsRegularFont = FontFamily(
    Font(R.font.poppins_regular, FontWeight.Normal),
)
private val PoppinsSemiBoldFont = FontFamily(
    Font(R.font.poppins_semibold, FontWeight.SemiBold),
)
private val ServiceOfferCards = listOf(
    ServiceOfferCardData(
        title = "Bathroom Cleaning",
        imageRes = R.drawable.card_bathroomclean,
        discount = "30% OFF",
        reviews = "38.5k",
        duration = "45 MINS",
        price = 25,
        originalPrice = 150,
    ),
    ServiceOfferCardData(
        title = "Utensils",
        imageRes = R.drawable.card_dishes,
        discount = "18% OFF",
        reviews = "31.3k",
        duration = "30 MINS",
        price = 25,
        originalPrice = 125,
    ),
    ServiceOfferCardData(
        title = "Kitchen Prep",
        imageRes = R.drawable.card_kitchenprep,
        discount = "24% OFF",
        reviews = "5.1k",
        duration = "40 MINS",
        price = 25,
        originalPrice = 125,
    ),
    ServiceOfferCardData(
        title = "Haircut",
        imageRes = R.drawable.card_haircut,
        discount = "27% OFF",
        reviews = "3.8k",
        duration = "35 MINS",
        price = 149,
        originalPrice = 199,
    ),
    ServiceOfferCardData(
        title = "Laundry",
        imageRes = R.drawable.card_laundry,
        discount = "22% OFF",
        reviews = "6.2k",
        duration = "50 MINS",
        price = 199,
        originalPrice = 249,
    ),
)

private data class ServiceOfferCardData(
    val title: String,
    val imageRes: Int,
    val discount: String,
    val reviews: String,
    val duration: String,
    val price: Int,
    val originalPrice: Int,
)

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

    LaunchedEffect(headerCollapseProgress) {
        onScrollProgressChanged(headerCollapseProgress)
    }

    LazyColumn(
        state = listState,
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp),
    ) {
        item {
            GreatSaleSection(modifier = Modifier.zIndex(1f))
            Box(
                modifier = Modifier
                    .zIndex(3f)
                    .fillMaxWidth()
                    .background(Color.White),
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    HomeSectionHeader(
                        title = "Services we offer :",
                        modifier = Modifier
                            .zIndex(2f)
                            .padding(start = 20.dp, top = 28.dp, end = 16.dp, bottom = 16.dp),
                    )
                    ServicesOfferGrid()
                    OffersDiscountSection()
                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
    }
}

@Composable
private fun GreatSaleSection(
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(0.84f),
    ) {
        Image(
            painter = painterResource(id = R.drawable.mainbanner),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
        )
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(start = 12.dp, end = 12.dp, bottom = 18.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.Bottom,
        ) {
            Box(
                modifier = Modifier
                    .weight(0.34f)
                    .aspectRatio(0.51f)
                    .clip(RoundedCornerShape(10.dp)),
            ) {
                Image(
                    painter = painterResource(id = R.drawable.long_ban),
                    contentDescription = "Great sale feature",
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer {
                            scaleY = 1.08f
                        },
                    contentScale = ContentScale.Crop,
                )
            }
            Column(
                modifier = Modifier.weight(0.66f),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    SaleTile(imageRes = R.drawable.cook, contentDescription = "Cooking service", modifier = Modifier.weight(1f))
                    SaleTile(imageRes = R.drawable.hairban, contentDescription = "Hair service", modifier = Modifier.weight(1f))
                }
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    SaleTile(imageRes = R.drawable.elecban, contentDescription = "Electrician service", modifier = Modifier.weight(1f))
                    SaleTile(imageRes = R.drawable.allban, contentDescription = "All services", modifier = Modifier.weight(1f))
                }
            }
        }
        CouponScallopBottomEdge(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .offset(y = 12.dp)
                .zIndex(2f),
        )
    }
}

@Composable
private fun ServicesOfferGrid() {
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 10.dp, end = 10.dp, bottom = 28.dp),
    ) {
        val cardWidth = (maxWidth - 16.dp) / 3
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            ServiceOfferCards.forEach { card ->
                ServiceCard(
                    card = card,
                    modifier = Modifier.width(cardWidth),
                )
            }
        }
    }
}

@Composable
private fun ServiceCard(
    card: ServiceOfferCardData,
    modifier: Modifier = Modifier,
) {
    val cardShape = RoundedCornerShape(16.dp)
    val imageShape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
    Surface(
        modifier = modifier
            .aspectRatio(0.65f)
            .shadow(
                elevation = 1.dp,
                shape = cardShape,
                clip = false,
                ambientColor = Color(0x0F000000),
                spotColor = Color(0x0D000000),
            )
            .clip(cardShape),
        shape = cardShape,
        color = Color.White,
        border = BorderStroke(0.5.dp, Color(0xFFEAEAEA)),
        tonalElevation = 0.dp,
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(modifier = Modifier.fillMaxSize()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(0.52f)
                        .clip(imageShape),
                    contentAlignment = Alignment.Center,
                ) {
                    Image(
                        painter = painterResource(id = card.imageRes),
                        contentDescription = card.title,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(start = 18.dp, top = 24.dp, end = 18.dp, bottom = 16.dp),
                        contentScale = ContentScale.Fit,
                    )
                }
                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(0.5.dp)
                        .background(Color(0xFFF2F2F2)),
                )
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(0.48f)
                        .padding(horizontal = 10.dp, vertical = 8.dp),
                ) {
                    Text(
                        text = card.title,
                        modifier = Modifier.height(40.dp),
                        color = Color(0xFF111111),
                        style = MaterialTheme.typography.titleMedium,
                        fontFamily = PoppinsSemiBoldFont,
                        fontSize = 14.sp,
                        lineHeight = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 2,
                        overflow = TextOverflow.Clip,
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Row(
                        modifier = Modifier.height(18.dp),
                        horizontalArrangement = Arrangement.spacedBy(5.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = "₹${card.price}",
                            modifier = Modifier.alignByBaseline(),
                            color = Color(0xFF111111),
                            style = MaterialTheme.typography.titleMedium,
                            fontFamily = PoppinsSemiBoldFont,
                            fontSize = 14.sp,
                            lineHeight = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 1,
                        )
                        Text(
                            text = "₹${card.originalPrice}",
                            modifier = Modifier.alignByBaseline(),
                            color = Color(0xFFB8B8B8),
                            style = MaterialTheme.typography.bodyMedium,
                            fontFamily = PoppinsRegularFont,
                            fontSize = 11.sp,
                            lineHeight = 13.sp,
                            fontWeight = FontWeight.Normal,
                            textDecoration = TextDecoration.LineThrough,
                            maxLines = 1,
                        )
                    }
                }
            }
            RatingBadge(
                rating = "4.9",
                reviews = card.reviews,
                modifier = Modifier.align(Alignment.TopEnd),
            )
        }
    }
}

@Composable
private fun RatingBadge(
    rating: String,
    reviews: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .height(24.dp)
            .clip(RoundedCornerShape(bottomStart = 14.dp, topEnd = 18.dp))
            .background(Color.White)
            .padding(start = 8.dp, end = 7.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = "★",
            color = Color(0xFFFFD642),
            fontFamily = PoppinsRegularFont,
            fontSize = 14.sp,
            lineHeight = 14.sp,
            fontWeight = FontWeight.Normal,
            maxLines = 1,
        )
        Text(
            text = "$rating ($reviews)",
            color = Color(0xFF6E7478),
            style = MaterialTheme.typography.labelMedium,
            fontFamily = PoppinsRegularFont,
            fontSize = 11.sp,
            lineHeight = 12.sp,
            fontWeight = FontWeight.Normal,
            maxLines = 1,
        )
    }
}

@Composable
private fun CouponScallopBottomEdge(
    modifier: Modifier = Modifier,
) {
    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(24.dp),
    ) {
        val cornerRadius = 18.dp.toPx()
        val notchRadius = 5.dp.toPx()
        val notchDiameter = notchRadius * 2f
        val notchGap = 6.dp.toPx()
        val horizontalInset = 24.dp.toPx()
        val path = Path()

        path.moveTo(0f, size.height)
        path.lineTo(0f, cornerRadius)
        path.quadraticBezierTo(0f, 0f, cornerRadius, 0f)

        var x = horizontalInset
        val scallopEnd = size.width - horizontalInset
        while (x + notchDiameter <= scallopEnd) {
            path.lineTo(x, 0f)
            path.cubicTo(
                x,
                notchRadius * 1.1f,
                x + notchDiameter,
                notchRadius * 1.1f,
                x + notchDiameter,
                0f,
            )
            x += notchDiameter + notchGap
        }

        path.lineTo(size.width - cornerRadius, 0f)
        path.quadraticBezierTo(size.width, 0f, size.width, cornerRadius)
        path.lineTo(size.width, size.height)
        path.close()

        drawPath(path = path, color = Color.White)
    }
}

@Composable
private fun SaleTile(
    imageRes: Int,
    contentDescription: String,
    modifier: Modifier = Modifier,
) {
    Image(
        painter = painterResource(id = imageRes),
        contentDescription = contentDescription,
        modifier = modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(10.dp)),
        contentScale = ContentScale.Crop,
    )
}

@Composable
private fun HomeSectionHeader(
    title: String,
    subtitle: String? = null,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom,
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            Text(
                text = title,
                fontFamily = HomeMajorHeadingFont,
                fontSize = 22.sp,
                lineHeight = 26.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.35.sp,
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
