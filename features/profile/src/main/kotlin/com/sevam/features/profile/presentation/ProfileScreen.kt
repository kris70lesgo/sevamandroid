package com.sevam.features.profile.presentation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.sevam.core.common.model.Address
import com.sevam.core.common.model.PaymentMethod
import com.sevam.core.common.model.UserProfile
import com.sevam.core.common.model.WalletSummary
import com.sevam.core.ui.SevamCard
import com.sevam.core.ui.SevamColors
import com.sevam.core.ui.SevamPrimaryButton
import com.sevam.core.ui.SevamSectionHeader
import com.sevam.core.ui.SevamSecondaryButton

enum class ProfileSection {
    PERSONAL,
    ADDRESSES,
    PAYMENTS,
    SECURITY,
}

@Composable
fun ProfileScreen(
    profile: UserProfile,
    addresses: List<Address>,
    walletSummary: WalletSummary,
    paymentMethods: List<PaymentMethod>,
    selectedSection: ProfileSection,
    onSectionSelected: (ProfileSection) -> Unit,
    onAddAddress: () -> Unit,
    onSetDefaultAddress: (String) -> Unit,
    onEditAddress: (String) -> Unit,
    onDeleteAddress: (String) -> Unit,
    onLogout: () -> Unit,
) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item {
            Surface(shape = RoundedCornerShape(28.dp), color = SevamColors.Navy) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(14.dp), verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .background(Color.White, CircleShape)
                                .padding(horizontal = 18.dp, vertical = 14.dp),
                        ) {
                            Text(profile.name.take(1), color = SevamColors.Navy, fontWeight = FontWeight.Bold)
                        }
                        Column {
                            Text(profile.name, color = Color.White, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
                            Text(profile.email, color = Color.White.copy(alpha = 0.82f))
                            Text("Member since ${profile.memberSince}", color = Color.White.copy(alpha = 0.72f), style = MaterialTheme.typography.bodySmall)
                        }
                    }
                    Text(
                        text = "Sign Out",
                        color = Color.White,
                        style = MaterialTheme.typography.labelLarge,
                        modifier = Modifier.clickable(onClick = onLogout),
                    )
                }
            }
        }
        item {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                items(ProfileSection.entries) { section ->
                    AssistChip(
                        onClick = { onSectionSelected(section) },
                        label = { Text(section.name.lowercase().replaceFirstChar(Char::uppercase)) },
                        colors = AssistChipDefaults.assistChipColors(
                            containerColor = if (section == selectedSection) SevamColors.OrangeContainer else MaterialTheme.colorScheme.surface,
                            labelColor = if (section == selectedSection) SevamColors.Orange else MaterialTheme.colorScheme.onSurface,
                        ),
                        border = BorderStroke(
                            1.dp,
                            if (section == selectedSection) SevamColors.Orange else SevamColors.Border,
                        ),
                    )
                }
            }
        }
        when (selectedSection) {
            ProfileSection.PERSONAL -> {
                item { PersonalInfoCard(profile = profile) }
                item { ReferralCard(referralCode = profile.referralCode) }
                item { SupportCard() }
            }
            ProfileSection.ADDRESSES -> {
                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                        SevamSectionHeader(title = "Saved Addresses", subtitle = "Home, office, and family places you book for")
                    }
                }
                items(addresses, key = { it.id }) { address ->
                    AddressCard(
                        address = address,
                        onSetDefault = { onSetDefaultAddress(address.id) },
                        onEdit = { onEditAddress(address.id) },
                        onDelete = { onDeleteAddress(address.id) },
                    )
                }
                item {
                    SevamPrimaryButton(text = "Add New Address", modifier = Modifier.fillMaxWidth(), onClick = onAddAddress)
                }
            }
            ProfileSection.PAYMENTS -> {
                item { WalletCard(walletSummary) }
                items(paymentMethods, key = { it.id }) { method ->
                    PaymentMethodCard(method)
                }
            }
            ProfileSection.SECURITY -> {
                item { SecurityCard() }
            }
        }
    }
}

@Composable
private fun PersonalInfoCard(profile: UserProfile) {
    SevamCard {
        Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            SevamSectionHeader(title = "Personal Information", subtitle = "Manage your name, contact details, and profile")
            InfoRow("Full Name", profile.name)
            InfoRow("Phone Number", profile.phoneNumber)
            InfoRow("Email Address", profile.email)
            InfoRow("Date of Birth", profile.dateOfBirth)
            InfoRow("Gender", profile.gender)
        }
    }
}

@Composable
private fun ReferralCard(referralCode: String) {
    Surface(shape = RoundedCornerShape(24.dp), color = SevamColors.Navy) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("Referral Code", color = Color.White.copy(alpha = 0.7f), style = MaterialTheme.typography.labelLarge)
                Text(referralCode, color = Color.White, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                Text("Share and earn Rs 100 per successful referral", color = Color.White.copy(alpha = 0.82f))
            }
            SevamPrimaryButton(text = "Share", onClick = {})
        }
    }
}

@Composable
private fun SupportCard() {
    SevamCard {
        Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text("Need help?", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
            Text("Our support team is available 24/7 for booking and payment issues.", color = SevamColors.TextSecondary)
            SevamPrimaryButton(text = "Contact Support", modifier = Modifier.fillMaxWidth(), onClick = {})
        }
    }
}

@Composable
private fun AddressCard(
    address: Address,
    onSetDefault: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
) {
    SevamCard {
        Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(address.label, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                if (address.isDefault) {
                    Text("Default", color = SevamColors.Success, style = MaterialTheme.typography.labelLarge)
                }
            }
            Text("${address.line1}, ${address.line2}", color = SevamColors.TextSecondary)
            Text(address.city, color = SevamColors.TextSecondary)
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                if (!address.isDefault) {
                    SevamSecondaryButton(text = "Set Default", onClick = onSetDefault)
                }
                SevamSecondaryButton(text = "Edit", onClick = onEdit)
            }
            SevamSecondaryButton(text = "Delete", modifier = Modifier.fillMaxWidth(), onClick = onDelete)
        }
    }
}

@Composable
private fun WalletCard(walletSummary: WalletSummary) {
    SevamCard {
        Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text("Sevam Wallet", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
            Text("Balance Rs ${walletSummary.balance}", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            walletSummary.historyPreview.forEach { line ->
                Text(line, color = SevamColors.TextSecondary)
            }
        }
    }
}

@Composable
private fun PaymentMethodCard(method: PaymentMethod) {
    SevamCard {
        Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(method.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            Text(method.subtitle, color = SevamColors.TextSecondary)
            if (!method.isLive) {
                Text("This section stays visible, but the live flow waits for backend support.", color = SevamColors.Orange)
            }
        }
    }
}

@Composable
private fun SecurityCard() {
    SevamCard {
        Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("Security", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
            Text("Phone OTP is the only live auth path right now. Two-factor, active session management, and password controls are ready for backend hooks.", color = SevamColors.TextSecondary)
            SevamSecondaryButton(text = "Review Active Session", modifier = Modifier.fillMaxWidth(), onClick = {})
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(label, style = MaterialTheme.typography.labelLarge, color = SevamColors.TextSecondary)
        Text(value, style = MaterialTheme.typography.bodyLarge)
    }
}
