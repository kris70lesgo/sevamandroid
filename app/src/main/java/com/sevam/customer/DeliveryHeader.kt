package com.sevam.customer

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material.icons.outlined.Mic
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import androidx.compose.ui.unit.sp
import com.sevam.features.home.R as HomeR

@Composable
fun DeliveryHeader(
    deliveryLabel: String = "Sevam in",
    deliveryTime: String = "18 minutes",
    addressTitle: String = "HOME",
    addressSubtitle: String,
    searchPlaceholder: String = "Search \"yoga\"",
    onLocationPressed: () -> Unit,
    onProfilePressed: () -> Unit,
    onSearchPressed: () -> Unit,
    onVoiceSearchPressed: () -> Unit,
    modifier: Modifier = Modifier,
    collapseProgress: Float = 0f,
) {
    val progress = collapseProgress.coerceIn(0f, 1f)
    val headerHeight = lerp(200.dp, 116.dp, progress)
    val detailsHeight = lerp(88.dp, 0.dp, progress)
    val contentAlpha = 1f - progress
    val searchTopPadding = lerp(2.dp, 8.dp, progress)
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(headerHeight)
    ) {
        Image(
            painter = painterResource(id = HomeR.drawable.navbg),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0x6600257A)),
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(start = 16.dp, top = searchTopPadding, end = 16.dp, bottom = 12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(detailsHeight)
                .clipToBounds()
                .alpha(contentAlpha),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top,
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 12.dp),
            ) {
                Text(
                    text = deliveryLabel,
                    color = HeaderInk,
                    fontSize = 15.sp,
                    lineHeight = 17.sp,
                    fontFamily = HeaderFont,
                    fontWeight = FontWeight.Bold,
                )
                DeliveryTimeText(deliveryTime = deliveryTime)
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = onLocationPressed,
                        )
                        .padding(top = 2.dp, end = 4.dp, bottom = 2.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = buildAnnotatedString {
                            withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                                append(addressTitle)
                            }
                            append(" - ")
                            withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                                append(addressSubtitle)
                            }
                        },
                        color = HeaderInk,
                        fontSize = 14.sp,
                        lineHeight = 17.sp,
                        fontFamily = HeaderFont,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false),
                    )
                    Icon(
                        imageVector = Icons.Outlined.KeyboardArrowDown,
                        contentDescription = null,
                        tint = HeaderInk,
                        modifier = Modifier.size(18.dp),
                    )
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                ProfileButton(onClick = onProfilePressed)
            }
        }

        SearchBar(
            placeholder = searchPlaceholder,
            onSearchPressed = onSearchPressed,
            onVoiceSearchPressed = onVoiceSearchPressed,
        )
    }
    }
}

@Composable
private fun DeliveryTimeText(deliveryTime: String) {
    if (deliveryTime.length >= 2 && deliveryTime[0].isDigit() && deliveryTime[1].isDigit()) {
        Row(
            modifier = Modifier
                .height(40.dp)
                .offset(y = (-3).dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = deliveryTime.take(1),
                color = HeaderInk,
                fontSize = 34.sp,
                lineHeight = 40.sp,
                fontFamily = HeaderFont,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.sp,
                maxLines = 1,
            )
            Text(
                text = deliveryTime.drop(1),
                color = HeaderInk,
                fontSize = 34.sp,
                lineHeight = 40.sp,
                fontFamily = HeaderFont,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.offset(x = (-3).dp),
            )
        }
    } else {
        Text(
            text = deliveryTime,
            color = HeaderInk,
            fontSize = 34.sp,
            lineHeight = 36.sp,
            fontFamily = HeaderFont,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
private fun ProfileButton(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(46.dp)
            .clip(CircleShape)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick,
            ),
        contentAlignment = Alignment.Center,
    ) {
        Image(
            painter = painterResource(id = HomeR.drawable.profile),
            contentDescription = "Profile",
            modifier = Modifier.size(40.dp),
        )
    }
}

@Composable
private fun SearchBar(
    placeholder: String,
    onSearchPressed: () -> Unit,
    onVoiceSearchPressed: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(44.dp)
            .shadow(2.dp, RoundedCornerShape(10.dp), clip = false)
            .clip(RoundedCornerShape(10.dp))
            .background(Color.White)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onSearchPressed,
            )
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = Icons.Outlined.Search,
            contentDescription = null,
            tint = Color(0xFF666666),
            modifier = Modifier.size(19.dp),
        )
        Text(
            text = placeholder,
            color = Color(0xFF777777),
            fontSize = 14.sp,
            lineHeight = 16.sp,
            fontWeight = FontWeight.Medium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .weight(1f)
                .padding(start = 10.dp),
        )
        Box(
            modifier = Modifier
                .padding(horizontal = 12.dp)
                .height(22.dp)
                .size(width = 1.dp, height = 22.dp)
                .background(Color(0xFFE8E8E8)),
        )
        Box(
            modifier = Modifier
                .size(30.dp)
                .clip(CircleShape)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onVoiceSearchPressed,
                ),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.Outlined.Mic,
                contentDescription = "Voice search",
                tint = Color(0xFF666666),
                modifier = Modifier.size(19.dp),
            )
        }
    }
}

private val HeaderFont = FontFamily(
    Font(R.font.opensans_semibold, FontWeight.Bold),
)
private val HeaderInk = Color(0xFFF3F7FF)

@Preview(showBackground = true, widthDp = 390)
@Composable
private fun DeliveryHeaderPreview() {
    DeliveryHeader(
        addressSubtitle = "The legend Krish",
        onLocationPressed = {},
        onProfilePressed = {},
        onSearchPressed = {},
        onVoiceSearchPressed = {},
    )
}
