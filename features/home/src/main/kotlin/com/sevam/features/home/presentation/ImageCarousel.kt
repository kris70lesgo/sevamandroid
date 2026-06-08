package com.sevam.features.home.presentation

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.collectIsDraggedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

@Composable
fun ImageCarousel(
    imageResIds: List<Int>,
    modifier: Modifier = Modifier,
    autoScroll: Boolean = true,
) {
    if (imageResIds.isEmpty()) return

    val pagerState = rememberPagerState(pageCount = { imageResIds.size })
    val isDragged by pagerState.interactionSource.collectIsDraggedAsState()

    LaunchedEffect(autoScroll, isDragged, imageResIds.size) {
        if (!autoScroll || imageResIds.size <= 1 || isDragged) return@LaunchedEffect

        while (true) {
            delay(4_500L)
            if (!isDragged) {
                val nextPage = (pagerState.currentPage + 1) % imageResIds.size
                pagerState.animateScrollToPage(
                    page = nextPage,
                    animationSpec = tween(
                        durationMillis = 500,
                        easing = FastOutSlowInEasing,
                    ),
                )
            }
        }
    }

    androidx.compose.foundation.layout.Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 20.dp),
            pageSpacing = 16.dp,
        ) { page ->
            CarouselBanner(
                imageResId = imageResIds[page],
                modifier = Modifier.fillMaxWidth(),
            )
        }

        CarouselIndicator(
            pageCount = imageResIds.size,
            pagerState = pagerState,
            modifier = Modifier.padding(top = 20.dp),
        )
    }
}

@Composable
private fun CarouselBanner(
    imageResId: Int,
    modifier: Modifier = Modifier,
) {
    Image(
        painter = painterResource(id = imageResId),
        contentDescription = null,
        modifier = modifier
            .height(180.dp)
            .clip(RoundedCornerShape(24.dp)),
        contentScale = ContentScale.Crop,
    )
}

@Composable
fun CarouselIndicator(
    pageCount: Int,
    pagerState: PagerState,
    modifier: Modifier = Modifier,
) {
    val trackWidth = 72.dp
    val thumbWidth = 28.dp
    val progress = remember {
        derivedStateOf {
            (pagerState.currentPage + pagerState.currentPageOffsetFraction)
                .coerceIn(0f, (pageCount - 1).toFloat())
        }
    }
    val thumbOffset = if (pageCount <= 1) {
        0.dp
    } else {
        ((trackWidth - thumbWidth) / (pageCount - 1)) * progress.value
    }

    Box(
        modifier = modifier
            .width(trackWidth)
            .height(5.dp)
            .clip(RoundedCornerShape(999.dp))
            .background(Color(0xFFE9E9E9)),
        contentAlignment = Alignment.CenterStart,
    ) {
        Box(
            modifier = Modifier
                .offset(x = thumbOffset)
                .width(thumbWidth)
                .height(5.dp)
                .clip(RoundedCornerShape(999.dp))
                .background(Color(0xFF565656)),
        )
    }
}
