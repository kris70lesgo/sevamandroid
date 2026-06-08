package com.sevam.features.services.presentation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sevam.core.common.model.ServiceCategory
import com.sevam.core.common.model.ServiceItem
import com.sevam.features.services.R

private val ServicesPageBg = Color.White
private val ServicesText = Color(0xFF111111)
private val ServicesHeadingBlue = Color(0xFF0756B8)
private val ServicesCardBorder = Color(0xFFEAEAEA)
private val ServicesHeadingFont = FontFamily(
    Font(R.font.poppins_semibold, FontWeight.SemiBold),
)
private val ServicesRegularFont = FontFamily(
    Font(R.font.poppins_regular, FontWeight.Normal),
)
private val ServicesSemiBoldFont = FontFamily(
    Font(R.font.poppins_semibold, FontWeight.SemiBold),
)

private data class CategoryCardData(
    val title: String,
    val imageRes: Int,
)

private val CategoryCards = listOf(
    CategoryCardData("Electric Work", R.drawable.cat_electric),
    CategoryCardData("Plumbing Work", R.drawable.cat_plumbing),
    CategoryCardData("Cleaning Work", R.drawable.cat_cleaning),
    CategoryCardData("Cooking Work", R.drawable.cat_cook),
    CategoryCardData("Grooming Work", R.drawable.cat_groom),
    CategoryCardData("Labour Work", R.drawable.cat_labour),
    CategoryCardData("Wood Work", R.drawable.cat_wood),
    CategoryCardData("Other Services", R.drawable.cat_others),
)

private data class SubServiceCardData(
    val title: String,
    val imageRes: Int,
    val reviews: String,
    val price: Int,
    val originalPrice: Int,
)

private val SubServiceCards = listOf(
    SubServiceCardData("Bathroom Cleaning", R.drawable.card_bathroomclean, "38.5k", 25, 150),
    SubServiceCardData("Utensils", R.drawable.card_dishes, "31.3k", 25, 125),
    SubServiceCardData("Kitchen Prep", R.drawable.card_kitchenprep, "5.1k", 25, 125),
    SubServiceCardData("Haircut", R.drawable.card_haircut, "3.8k", 149, 199),
    SubServiceCardData("Laundry", R.drawable.card_laundry, "6.2k", 199, 249),
)

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
    onScrollProgressChanged: (Float) -> Unit = {},
) {
    var selectedCategory by remember { mutableStateOf<CategoryCardData?>(null) }

    LaunchedEffect(Unit) {
        onScrollProgressChanged(0f)
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(ServicesPageBg),
        contentPadding = PaddingValues(start = 20.dp, top = 20.dp, end = 20.dp, bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp),
    ) {
        val activeCategory = selectedCategory
        if (activeCategory == null) {
            item {
                Text(
                    text = "Our Services :",
                    color = ServicesHeadingBlue,
                    fontFamily = ServicesHeadingFont,
                    fontSize = 24.sp,
                    lineHeight = 29.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 0.sp,
                )
            }

            items(CategoryCards.chunked(3)) { rowCards ->
                BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
                    val cardWidth = (maxWidth - 16.dp) / 3
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        rowCards.forEach { card ->
                            CategoryServiceCard(
                                card = card,
                                modifier = Modifier.width(cardWidth),
                                onClick = {
                                    selectedCategory = card
                                    onCategorySelected(card.title)
                                },
                            )
                        }
                        repeat(3 - rowCards.size) {
                            Spacer(modifier = Modifier.width(cardWidth))
                        }
                    }
                }
            }
        } else {
            item {
                SelectedCategoryHeader(
                    title = "${activeCategory.title.toServiceLabel()} services :",
                    onBackClick = { selectedCategory = null },
                )
            }

            items(SubServiceCards.chunked(3)) { rowCards ->
                BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
                    val cardWidth = (maxWidth - 16.dp) / 3
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        rowCards.forEach { card ->
                            SubServiceCard(
                                card = card,
                                modifier = Modifier.width(cardWidth),
                                onClick = { onServiceClick(card.title) },
                            )
                        }
                        repeat(3 - rowCards.size) {
                            Spacer(modifier = Modifier.width(cardWidth))
                        }
                    }
                }
            }
        }
    }
}

private fun String.toServiceLabel(): String = removeSuffix(" Work")
    .removeSuffix(" Services")
    .replaceFirstChar { it.uppercase() }

@Composable
private fun SelectedCategoryHeader(
    title: String,
    onBackClick: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Surface(
            modifier = Modifier
                .size(36.dp)
                .shadow(3.dp, CircleShape, clip = false)
                .clickable(onClick = onBackClick),
            shape = CircleShape,
            color = Color.White.copy(alpha = 0.95f),
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Outlined.ArrowBack,
                    contentDescription = "Back",
                    tint = Color(0xFF252525),
                    modifier = Modifier.size(20.dp),
                )
            }
        }
        Text(
            text = title,
            color = ServicesHeadingBlue,
            fontFamily = ServicesHeadingFont,
            fontSize = 24.sp,
            lineHeight = 29.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 0.sp,
        )
    }
}

@Composable
private fun SubServiceCard(
    card: SubServiceCardData,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
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
            .clip(cardShape)
            .clickable(onClick = onClick),
        shape = cardShape,
        color = Color.White,
        border = BorderStroke(0.5.dp, ServicesCardBorder),
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
                        color = ServicesText,
                        style = MaterialTheme.typography.titleMedium,
                        fontFamily = ServicesSemiBoldFont,
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
                            color = ServicesText,
                            style = MaterialTheme.typography.titleMedium,
                            fontFamily = ServicesSemiBoldFont,
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
                            fontFamily = ServicesRegularFont,
                            fontSize = 11.sp,
                            lineHeight = 13.sp,
                            fontWeight = FontWeight.Normal,
                            textDecoration = TextDecoration.LineThrough,
                            maxLines = 1,
                        )
                    }
                }
            }
            SubServiceRatingBadge(
                rating = "4.9",
                reviews = card.reviews,
                modifier = Modifier.align(Alignment.TopEnd),
            )
        }
    }
}

@Composable
private fun SubServiceRatingBadge(
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
            fontFamily = ServicesRegularFont,
            fontSize = 14.sp,
            lineHeight = 14.sp,
            fontWeight = FontWeight.Normal,
            maxLines = 1,
        )
        Text(
            text = "$rating ($reviews)",
            color = Color(0xFF6E7478),
            style = MaterialTheme.typography.labelMedium,
            fontFamily = ServicesRegularFont,
            fontSize = 11.sp,
            lineHeight = 12.sp,
            fontWeight = FontWeight.Normal,
            maxLines = 1,
        )
    }
}

@Composable
private fun CategoryServiceCard(
    card: CategoryCardData,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    val cardShape = RoundedCornerShape(16.dp)
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
            .clip(cardShape)
            .clickable(onClick = onClick),
        shape = cardShape,
        color = Color.White,
        border = BorderStroke(0.5.dp, ServicesCardBorder),
        tonalElevation = 0.dp,
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.58f),
                contentAlignment = Alignment.Center,
            ) {
                Image(
                    painter = painterResource(id = card.imageRes),
                    contentDescription = card.title,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                )
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.42f)
                    .padding(horizontal = 8.dp, vertical = 7.dp),
                contentAlignment = Alignment.TopCenter,
            ) {
                Text(
                    text = card.title,
                    color = ServicesText,
                    fontFamily = ServicesSemiBoldFont,
                    fontSize = 14.sp,
                    lineHeight = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center,
                    maxLines = 2,
                    overflow = TextOverflow.Clip,
                )
            }
        }
    }
}
