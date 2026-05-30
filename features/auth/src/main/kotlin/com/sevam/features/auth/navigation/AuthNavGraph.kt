package com.sevam.features.auth.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.PhoneAndroid
import androidx.compose.material.icons.outlined.VerifiedUser
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.text.KeyboardOptions
import com.sevam.core.ui.SevamColors
import com.sevam.core.ui.SevamPrimaryButton
import com.sevam.core.ui.SevamSecondaryButton

@Composable
fun LoginScreen(
    phoneNumber: String,
    errorMessage: String?,
    isSubmitting: Boolean,
    showDebugBypass: Boolean,
    onPhoneNumberChange: (String) -> Unit,
    onContinue: () -> Unit,
    onDebugContinue: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(18.dp)) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .background(SevamColors.Orange, CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Text("S", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 24.sp)
            }
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Welcome to Sevam",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = "Doorstep home services with trusted professionals, upfront pricing, and live tracking.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = SevamColors.TextSecondary,
                )
            }
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = SevamColors.Navy,
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    Text(
                        text = "Sign in with phone OTP",
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold,
                    )
                    OutlinedTextField(
                        value = phoneNumber,
                        onValueChange = onPhoneNumberChange,
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Phone number") },
                        placeholder = { Text("+91 98765 43210") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        leadingIcon = {
                            Icon(Icons.Outlined.PhoneAndroid, contentDescription = null)
                        },
                        shape = RoundedCornerShape(18.dp),
                    )
                    if (!errorMessage.isNullOrBlank()) {
                        Text(
                            text = errorMessage,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.error,
                        )
                    }
                    SevamPrimaryButton(
                        text = if (isSubmitting) "Sending OTP..." else "Send OTP",
                        enabled = !isSubmitting,
                        modifier = Modifier.fillMaxWidth(),
                        onClick = onContinue,
                    )
                    if (showDebugBypass) {
                        SevamSecondaryButton(
                            text = "Enter App in Debug",
                            enabled = !isSubmitting,
                            modifier = Modifier.fillMaxWidth(),
                            onClick = onDebugContinue,
                        )
                    }
                }
            }
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                AuthBenefitRow(Icons.Outlined.VerifiedUser, "Verified and background-checked professionals")
                AuthBenefitRow(Icons.Outlined.Lock, "Encrypted session storage for secure login restore")
                AuthBenefitRow(Icons.Outlined.PhoneAndroid, "Support and booking updates straight to your phone")
            }
        }
        Text(
            text = "By continuing you agree to Sevam's terms and privacy policy.",
            style = MaterialTheme.typography.bodySmall,
            color = SevamColors.TextSecondary,
        )
    }
}

@Composable
fun VerifyOtpScreen(
    phoneNumber: String,
    otp: String,
    errorMessage: String?,
    isSubmitting: Boolean,
    showDebugBypass: Boolean,
    onOtpChange: (String) -> Unit,
    onVerified: () -> Unit,
    onDebugContinue: () -> Unit,
    onBack: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
            Text(
                text = "Verify your OTP",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = "We sent a verification code to $phoneNumber. Enter it to continue to your account.",
                style = MaterialTheme.typography.bodyLarge,
                color = SevamColors.TextSecondary,
            )
            OutlinedTextField(
                value = otp,
                onValueChange = onOtpChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("OTP") },
                placeholder = { Text("123456") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                shape = RoundedCornerShape(18.dp),
            )
            if (!errorMessage.isNullOrBlank()) {
                Text(
                    text = errorMessage,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error,
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                SevamSecondaryButton(
                    text = "Back",
                    enabled = !isSubmitting,
                    modifier = Modifier.weight(1f),
                    onClick = onBack,
                )
                SevamPrimaryButton(
                    text = if (isSubmitting) "Verifying..." else "Verify",
                    enabled = !isSubmitting,
                    modifier = Modifier.weight(1f),
                    onClick = onVerified,
                )
            }
            if (showDebugBypass) {
                SevamSecondaryButton(
                    text = "Continue in Debug",
                    enabled = !isSubmitting,
                    modifier = Modifier.fillMaxWidth(),
                    onClick = onDebugContinue,
                )
            }
        }
        Text(
            text = "Use the same phone number that can receive the SMS OTP. The app will only continue after Supabase confirms the request.",
            style = MaterialTheme.typography.bodySmall,
            color = SevamColors.TextSecondary,
        )
    }
}

@Composable
private fun AuthBenefitRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(SevamColors.OrangeContainer, RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(icon, contentDescription = null, tint = SevamColors.Orange)
        }
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = SevamColors.TextSecondary,
        )
    }
}
