package com.sevam.features.home.presentation

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sevam.features.home.R

enum class HomeDockTab {
    Home,
    OrderAgain,
    Categories,
}

@Composable
fun HomeBottomDock(
    selectedTab: HomeDockTab,
    onTabSelected: (HomeDockTab) -> Unit,
    modifier: Modifier = Modifier,
    onDistrictClick: () -> Unit = {},
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.White)
            .drawBehind {
                drawLine(
                    color = Color(0xFFEAEAEA),
                    start = Offset.Zero,
                    end = Offset(size.width, 0f),
                    strokeWidth = 1.dp.toPx(),
                )
            }
            .navigationBarsPadding(),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(66.dp)
                .padding(start = 8.dp, end = 0.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                HomeDockItem(
                    tab = HomeDockTab.Home,
                    label = "Home",
                    selected = selectedTab == HomeDockTab.Home,
                    icon = R.drawable.home,
                    selectedIcon = R.drawable.home_on,
                    iconSize = 28.dp,
                    onClick = onTabSelected,
                )
                HomeDockItem(
                    tab = HomeDockTab.OrderAgain,
                    label = "Order Again",
                    selected = selectedTab == HomeDockTab.OrderAgain,
                    icon = R.drawable.order,
                    selectedIcon = R.drawable.order_on,
                    iconSize = 23.dp,
                    onClick = onTabSelected,
                )
                HomeDockItem(
                    tab = HomeDockTab.Categories,
                    label = "Categories",
                    selected = selectedTab == HomeDockTab.Categories,
                    icon = R.drawable.category,
                    selectedIcon = R.drawable.category_on,
                    iconSize = 23.dp,
                    onClick = onTabSelected,
                )
            }

            DistrictButton(onClick = onDistrictClick)
        }
    }
}

@Composable
private fun RowScope.HomeDockItem(
    tab: HomeDockTab,
    label: String,
    selected: Boolean,
    icon: Int,
    selectedIcon: Int,
    iconSize: androidx.compose.ui.unit.Dp,
    onClick: (HomeDockTab) -> Unit,
) {
    val textColor by animateColorAsState(
        targetValue = if (selected) Color.Black else Color(0xFF666666),
        animationSpec = tween(durationMillis = 250),
        label = "dockTextColor",
    )

    Box(
        modifier = Modifier
            .weight(1f)
            .fillMaxHeight()
            .clip(RoundedCornerShape(12.dp))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
            ) { onClick(tab) },
        contentAlignment = Alignment.Center,
    ) {
        if (selected) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .width(42.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(bottomStart = 4.dp, bottomEnd = 4.dp))
                    .background(Color(0xFF2A70FF)),
            )
        }

        androidx.compose.foundation.layout.Column(
            modifier = Modifier.padding(top = 2.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(3.dp, Alignment.CenterVertically),
        ) {
            Box(
                modifier = Modifier.size(28.dp),
                contentAlignment = Alignment.Center,
            ) {
                Image(
                    painter = painterResource(id = if (selected) selectedIcon else icon),
                    contentDescription = label,
                    modifier = Modifier.size(iconSize),
                )
            }
            Text(
                text = label,
                color = textColor,
                fontSize = 11.sp,
                lineHeight = 12.sp,
                fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
                textAlign = TextAlign.Center,
                maxLines = 1,
            )
        }
    }
}

@Composable
private fun DistrictButton(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .width(112.dp)
            .height(46.dp)
            .clip(RoundedCornerShape(2.dp))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick,
            ),
        contentAlignment = Alignment.CenterStart,
    ) {
        Image(
            painter = painterResource(id = R.drawable.navban),
            contentDescription = "District",
            modifier = Modifier
                .width(114.dp)
                .height(46.dp)
                .offset(x = 3.dp)
                .align(Alignment.CenterEnd),
            contentScale = ContentScale.Fit,
        )
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 92)
@Composable
private fun HomeBottomDockPreview() {
    HomeBottomDock(
        selectedTab = HomeDockTab.Home,
        onTabSelected = {},
    )
}
