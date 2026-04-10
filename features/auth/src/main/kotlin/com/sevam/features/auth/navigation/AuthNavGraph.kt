package com.sevam.features.auth.navigation

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
import com.sevam.features.auth.api.AuthFeatureRoutes

fun NavGraphBuilder.authNavGraph(
    onLoginSuccess: () -> Unit,
) {
    navigation(
        route = AuthFeatureRoutes.ROOT,
        startDestination = AuthFeatureRoutes.LOGIN,
    ) {
        composable(AuthFeatureRoutes.LOGIN) {
            LoginScreen(onContinue = onLoginSuccess)
        }
        composable(AuthFeatureRoutes.VERIFY_OTP) {
            VerifyOtpScreen(onVerified = onLoginSuccess)
        }
    }
}

@Composable
private fun LoginScreen(onContinue: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = "Sevam Login",
            style = MaterialTheme.typography.headlineSmall,
        )
        Button(onClick = onContinue, modifier = Modifier.padding(top = 16.dp)) {
            Text(text = "Continue to OTP")
        }
    }
}

@Composable
private fun VerifyOtpScreen(onVerified: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = "Verify OTP",
            style = MaterialTheme.typography.headlineSmall,
        )
        Button(onClick = onVerified, modifier = Modifier.padding(top = 16.dp)) {
            Text(text = "Enter Home")
        }
    }
}
