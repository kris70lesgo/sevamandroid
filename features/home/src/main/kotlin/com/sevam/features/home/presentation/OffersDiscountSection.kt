package com.sevam.features.home.presentation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sevam.features.home.R

private val OfferHeadingFont = FontFamily(
    Font(R.font.pt_sans_narrow_bold, FontWeight.Bold),
)

@Composable
fun OffersDiscountSection(
    modifier: Modifier = Modifier,
) {
    val offerImages = remember {
        listOf(
            R.drawable.slideban,
            R.drawable.slideban2,
            R.drawable.slideban3,
            R.drawable.slideban4,
        )
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 24.dp),
    ) {
        Text(
            text = "Offers & discounts",
            modifier = Modifier.padding(start = 24.dp),
            color = Color.Black,
            fontFamily = OfferHeadingFont,
            fontSize = 22.sp,
            lineHeight = 26.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.35.sp,
        )
        Spacer(modifier = Modifier.height(20.dp))
        ImageCarousel(imageResIds = offerImages)
    }
}
