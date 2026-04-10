package com.sevam.features.home.navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.sevam.features.home.api.HomeFeatureRoutes

fun NavGraphBuilder.homeNavGraph(
    onOpenBookingForm: () -> Unit,
    onOpenLegacyTasks: () -> Unit,
) {
    navigation(
        route = HomeFeatureRoutes.ROOT,
        startDestination = HomeFeatureRoutes.ROOT,
    ) {
        composable(HomeFeatureRoutes.ROOT) {
            HomeScreen(
                onOpenBookingForm = onOpenBookingForm,
                onOpenLegacyTasks = onOpenLegacyTasks,
            )
        }
        composable(HomeFeatureRoutes.BOOKING_FORM) {
            BookingFormScreen()
        }
    }
}

@Composable
private fun HomeScreen(
    onOpenBookingForm: () -> Unit,
    onOpenLegacyTasks: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = "Sevam Home",
            style = MaterialTheme.typography.headlineSmall,
        )
        Button(onClick = onOpenBookingForm, modifier = Modifier.padding(top = 16.dp)) {
            Text(text = "Start Booking")
        }
        Button(onClick = onOpenLegacyTasks, modifier = Modifier.padding(top = 12.dp)) {
            Text(text = "Open Legacy Tasks")
        }
    }
}

@Composable
private fun BookingFormScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = "Booking Form",
            style = MaterialTheme.typography.headlineSmall,
        )
    }
}
