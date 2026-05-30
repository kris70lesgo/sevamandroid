package com.sevam.features.profile.presentation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Badge
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.CreditCard
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Female
import androidx.compose.material.icons.outlined.HelpOutline
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.LocalOffer
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Logout
import androidx.compose.material.icons.outlined.Payments
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material.icons.outlined.ReceiptLong
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Verified
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sevam.core.common.model.Address
import com.sevam.core.common.model.PaymentMethod
import com.sevam.core.common.model.UserProfile
import com.sevam.core.ui.SevamColors
import com.sevam.core.ui.SevamPrimaryButton
import com.sevam.core.ui.SevamSecondaryButton

enum class ProfileSection {
    PERSONAL,
    ADDRESSES,
    PAYMENTS,
    SECURITY,
}

private val ProfileBlue = Color(0xFF174A9C)
private val ProfileBlueDark = Color(0xFF0D2F68)
private val ProfileBlueSoft = Color(0xFFEAF2FF)
private val ProfileText = Color(0xFF111827)
private val ProfileMuted = Color(0xFF667085)
private val ProfileSurface = Color.White
private val ProfileBorder = Color(0xFFE8EEF8)

@Composable
fun ProfileScreen(
    profile: UserProfile,
    addresses: List<Address>,
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
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(start = 16.dp, top = 12.dp, end = 16.dp, bottom = 28.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp),
    ) {
        item {
            ProfileHeaderCard(
                profile = profile,
            )
        }
        item {
            QuickActionsGrid(onSectionSelected = onSectionSelected)
        }
        if (selectedSection != ProfileSection.PERSONAL) {
            item {
                AccountSectionTitle(
                    title = when (selectedSection) {
                        ProfileSection.PERSONAL -> "Personal Information"
                        ProfileSection.ADDRESSES -> "Saved Addresses"
                        ProfileSection.PAYMENTS -> "Payment Methods"
                        ProfileSection.SECURITY -> "Security"
                    },
                    subtitle = when (selectedSection) {
                        ProfileSection.PERSONAL -> "Your contact details and profile basics"
                        ProfileSection.ADDRESSES -> "Manage service locations for faster bookings"
                        ProfileSection.PAYMENTS -> "Saved UPI and card options"
                        ProfileSection.SECURITY -> "Login and account protection"
                    },
                )
            }
        }
        when (selectedSection) {
            ProfileSection.PERSONAL -> {
                item { ReferralCard(referralCode = profile.referralCode) }
            }

            ProfileSection.ADDRESSES -> {
                items(addresses, key = { it.id }) { address ->
                    AddressCard(
                        address = address,
                        onSetDefault = { onSetDefaultAddress(address.id) },
                        onEdit = { onEditAddress(address.id) },
                        onDelete = { onDeleteAddress(address.id) },
                    )
                }
                item {
                    SevamPrimaryButton(
                        text = "Add New Address",
                        modifier = Modifier.fillMaxWidth(),
                        onClick = onAddAddress,
                    )
                }
            }

            ProfileSection.PAYMENTS -> {
                items(paymentMethods.filterNot { it.id.equals("wallet", ignoreCase = true) }, key = { it.id }) { method ->
                    PaymentMethodCard(method)
                }
            }

            ProfileSection.SECURITY -> {
                item { SecurityCard() }
            }
        }
        item {
            AccountSettingsList(onSectionSelected = onSectionSelected)
        }
        item {
            SignOutButton(onLogout = onLogout)
        }
    }
}

@Composable
private fun ProfileHeaderCard(
    profile: UserProfile,
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(8.dp, RoundedCornerShape(26.dp), clip = false),
        shape = RoundedCornerShape(26.dp),
        color = ProfileBlueDark,
    ) {
        Box(
            modifier = Modifier
                .background(
                    Brush.linearGradient(
                        listOf(ProfileBlueDark, ProfileBlue, Color(0xFF286CD8)),
                    ),
                )
                .padding(18.dp),
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Surface(
                        modifier = Modifier.size(62.dp),
                        shape = CircleShape,
                        color = Color.White,
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = profile.name.take(1).uppercase(),
                                color = ProfileBlue,
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Bold,
                            )
                        }
                    }
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(3.dp),
                    ) {
                        Text(
                            text = profile.name,
                            color = Color.White,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                        Text(
                            text = profile.email.ifBlank { profile.phoneNumber },
                            color = Color.White.copy(alpha = 0.86f),
                            style = MaterialTheme.typography.bodyMedium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                        Text(
                            text = "Member since ${profile.memberSince}",
                            color = Color.White.copy(alpha = 0.7f),
                            style = MaterialTheme.typography.labelMedium,
                            maxLines = 1,
                        )
                    }
                    Surface(
                        modifier = Modifier.clickable(onClick = {}),
                        shape = CircleShape,
                        color = Color.White.copy(alpha = 0.16f),
                        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.22f)),
                    ) {
                        Box(
                            modifier = Modifier.size(42.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Edit,
                                contentDescription = "Edit profile",
                                tint = Color.White,
                                modifier = Modifier.size(19.dp),
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StatusPill(
    icon: ImageVector,
    text: String,
) {
    Surface(
        shape = RoundedCornerShape(999.dp),
        color = Color.White.copy(alpha = 0.12f),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.16f)),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(15.dp),
            )
            Text(
                text = text,
                color = Color.White,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
            )
        }
    }
}

@Composable
private fun QuickActionsGrid(onSectionSelected: (ProfileSection) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            QuickActionCard(
                label = "My Bookings",
                icon = Icons.Outlined.ReceiptLong,
                modifier = Modifier.weight(1f),
                onClick = {},
            )
            QuickActionCard(
                label = "Addresses",
                icon = Icons.Outlined.LocationOn,
                modifier = Modifier.weight(1f),
                onClick = { onSectionSelected(ProfileSection.ADDRESSES) },
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            QuickActionCard(
                label = "Payments",
                icon = Icons.Outlined.CreditCard,
                modifier = Modifier.weight(1f),
                onClick = { onSectionSelected(ProfileSection.PAYMENTS) },
            )
            QuickActionCard(
                label = "Help & Support",
                icon = Icons.Outlined.HelpOutline,
                modifier = Modifier.weight(1f),
                onClick = {},
            )
        }
    }
}

@Composable
private fun QuickActionCard(
    label: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Surface(
        modifier = modifier
            .height(82.dp)
            .shadow(3.dp, RoundedCornerShape(20.dp), clip = false)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        color = ProfileSurface,
        border = BorderStroke(1.dp, ProfileBorder),
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = ProfileBlueSoft,
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = ProfileBlue,
                    modifier = Modifier
                        .padding(8.dp)
                        .size(19.dp),
                )
            }
            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold,
                color = ProfileText,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
private fun AccountSectionTitle(
    title: String,
    subtitle: String,
) {
    Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold,
            color = ProfileText,
        )
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodyMedium,
            color = ProfileMuted,
        )
    }
}

@Composable
private fun ReferralCard(referralCode: String) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(5.dp, RoundedCornerShape(24.dp), clip = false),
        shape = RoundedCornerShape(24.dp),
        color = ProfileBlue,
    ) {
        Box(
            modifier = Modifier
                .background(
                    Brush.linearGradient(
                        listOf(ProfileBlueDark, ProfileBlue, Color(0xFF2B6EDB)),
                    ),
                )
                .padding(18.dp),
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Surface(
                    modifier = Modifier.size(46.dp),
                    shape = RoundedCornerShape(16.dp),
                    color = Color.White.copy(alpha = 0.14f),
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Outlined.LocalOffer,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(23.dp),
                        )
                    }
                }
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Text(
                        text = "Refer & Earn",
                        color = Color.White,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                    )
                    Text(
                        text = "Share your code and earn Rs 100 per successful referral.",
                        color = Color.White.copy(alpha = 0.82f),
                        style = MaterialTheme.typography.bodySmall,
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Surface(
                            shape = RoundedCornerShape(999.dp),
                            color = Color.White.copy(alpha = 0.14f),
                            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.18f)),
                        ) {
                            Text(
                                text = referralCode,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                color = Color.White,
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold,
                            )
                        }
                        Surface(
                            shape = RoundedCornerShape(999.dp),
                            color = Color.White,
                        ) {
                            Text(
                                text = "Share Code",
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                color = ProfileBlue,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.SemiBold,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AccountSettingsList(onSectionSelected: (ProfileSection) -> Unit) {
    PremiumProfileCard {
        Column {
            Text(
                text = "Account & Settings",
                modifier = Modifier.padding(start = 16.dp, top = 16.dp, end = 16.dp, bottom = 6.dp),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = ProfileText,
            )
            SettingsRow(Icons.Outlined.Home, "Manage Addresses", "Home, office and other locations", onClick = { onSectionSelected(ProfileSection.ADDRESSES) })
            ProfileDivider()
            SettingsRow(Icons.Outlined.Payments, "Manage Payment Methods", "UPI and cards", onClick = { onSectionSelected(ProfileSection.PAYMENTS) })
            ProfileDivider()
            SettingsRow(Icons.Outlined.Lock, "Security", "Login and session controls", onClick = { onSectionSelected(ProfileSection.SECURITY) })
            ProfileDivider()
            SettingsRow(Icons.Outlined.Settings, "Settings", "Preferences and notifications", onClick = {})
        }
    }
}

@Composable
private fun SettingsRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 13.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Surface(
            modifier = Modifier.size(38.dp),
            shape = RoundedCornerShape(13.dp),
            color = ProfileBlueSoft,
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = ProfileBlue,
                    modifier = Modifier.size(18.dp),
                )
            }
        }
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = ProfileText,
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelMedium,
                color = ProfileMuted,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        Icon(
            imageVector = Icons.Outlined.ChevronRight,
            contentDescription = null,
            tint = Color(0xFF98A2B3),
            modifier = Modifier.size(20.dp),
        )
    }
}

@Composable
private fun SignOutButton(onLogout: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onLogout),
        shape = RoundedCornerShape(18.dp),
        color = Color.White,
        border = BorderStroke(1.dp, Color(0xFFFFD4D4)),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = Icons.Outlined.Logout,
                contentDescription = null,
                tint = Color(0xFFE5484D),
                modifier = Modifier.size(18.dp),
            )
            Spacer(modifier = Modifier.size(8.dp))
            Text(
                text = "Sign out",
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFFE5484D),
                fontWeight = FontWeight.SemiBold,
            )
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
    PremiumProfileCard {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(address.label, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                if (address.isDefault) {
                    Text("Default", color = SevamColors.Success, style = MaterialTheme.typography.labelLarge)
                }
            }
            Text("${address.line1}, ${address.line2}", style = MaterialTheme.typography.bodyMedium, color = ProfileMuted)
            Text(address.city, style = MaterialTheme.typography.bodySmall, color = ProfileMuted)
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                if (!address.isDefault) {
                    SevamSecondaryButton(text = "Set Default", modifier = Modifier.weight(1f), onClick = onSetDefault)
                }
                SevamSecondaryButton(text = "Edit", modifier = Modifier.weight(1f), onClick = onEdit)
            }
            SevamSecondaryButton(text = "Delete", modifier = Modifier.fillMaxWidth(), onClick = onDelete)
        }
    }
}

@Composable
private fun PaymentMethodCard(method: PaymentMethod) {
    PremiumProfileCard {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(method.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            Text(method.subtitle, style = MaterialTheme.typography.bodySmall, color = ProfileMuted)
            if (!method.isLive) {
                Text("Available soon for live payments.", style = MaterialTheme.typography.bodySmall, color = SevamColors.Orange)
            }
        }
    }
}

@Composable
private fun SecurityCard() {
    PremiumProfileCard {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text("Security", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
            Text(
                "Phone OTP is the live login path right now. Session tools and extra account controls are ready for the backend when needed.",
                style = MaterialTheme.typography.bodyMedium,
                color = ProfileMuted,
            )
            SevamSecondaryButton(text = "Review Active Session", modifier = Modifier.fillMaxWidth(), onClick = {})
        }
    }
}

@Composable
private fun PremiumProfileCard(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(22.dp), clip = false),
        shape = RoundedCornerShape(22.dp),
        color = ProfileSurface,
        border = BorderStroke(1.dp, ProfileBorder),
        content = content,
    )
}

@Composable
private fun ProfileDivider() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(Color(0xFFF0F3F8)),
    )
}
