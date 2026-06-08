package com.sevam.features.profile.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.CardGiftcard
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.ExpandMore
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.IosShare
import androidx.compose.material.icons.outlined.LocalOffer
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Logout
import androidx.compose.material.icons.outlined.MenuBook
import androidx.compose.material.icons.outlined.NotificationsNone
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material.icons.outlined.ReceiptLong
import androidx.compose.material.icons.outlined.RadioButtonUnchecked
import androidx.compose.material.icons.outlined.ToggleOff
import androidx.compose.material.icons.outlined.WbSunny
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sevam.core.common.model.Address
import com.sevam.core.common.model.PaymentMethod
import com.sevam.core.common.model.UserProfile
import com.sevam.features.profile.R

enum class ProfileSection {
    PERSONAL,
    ADDRESSES,
    PAYMENTS,
    SECURITY,
}

private val ProfileBlueTop = Color(0xFF0D57D7)
private val ProfileBlueMid = Color(0xFF1268F2)
private val ProfilePageBg = Color(0xFFF5F5F7)
private val ProfileCard = Color.White
private val ProfileText = Color(0xFF333333)
private val ProfileMuted = Color(0xFF737780)
private val ProfileDivider = Color(0xFFF0F0F2)
private val ProfileBlue = Color(0xFF1268F2)
private val ProfileChevron = Color(0xFFB9BCC3)
private val ProfileRadius = RoundedCornerShape(20.dp)
private val ProfileFont = FontFamily(
    Font(R.font.poppins_regular, FontWeight.Normal),
    Font(R.font.poppins_semibold, FontWeight.SemiBold),
)

private data class ProfileMenuRow(
    val title: String,
    val icon: ImageVector,
    val onClick: (() -> Unit)? = null,
)

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
    onEditProfile: () -> Unit,
    onBack: () -> Unit = {},
) {
    val listState = rememberLazyListState()
    var showLogoutDialog by remember { mutableStateOf(false) }
    val showPinnedToolbar by remember {
        derivedStateOf {
            listState.firstVisibleItemIndex > 0 || listState.firstVisibleItemScrollOffset > 390
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ProfilePageBg),
    ) {
        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize(),
        ) {
            item {
                ProfileHero(
                    name = "Agastya",
                    phoneNumber = "6398317816",
                    dateOfBirth = "25 Apr 2006",
                    onBack = onBack,
                    onEditProfile = onEditProfile,
                )
            }
            item {
                ProfileContent(
                    onLogout = { showLogoutDialog = true },
                    onSectionSelected = onSectionSelected,
                )
            }
        }

        AnimatedVisibility(
            visible = showPinnedToolbar,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.align(Alignment.TopCenter),
        ) {
            ProfilePinnedToolbar(onBack = onBack)
        }

        if (showLogoutDialog) {
            LogoutDialog(
                onCurrentDevice = {
                    showLogoutDialog = false
                    onLogout()
                },
                onAllDevices = {
                    showLogoutDialog = false
                    onLogout()
                },
                onCancel = { showLogoutDialog = false },
            )
        }
    }
}

@Composable
fun EditProfileScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var firstName by remember { mutableStateOf("Agastya") }
    var lastName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(ProfilePageBg),
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(102.dp),
                color = Color.White,
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(start = 30.dp, top = 14.dp, end = 24.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        imageVector = Icons.Outlined.ArrowBack,
                        contentDescription = "Back",
                        tint = Color(0xFF222222),
                        modifier = Modifier
                            .size(30.dp)
                            .clickable(onClick = onBack),
                    )
                    Spacer(modifier = Modifier.width(34.dp))
                    Text(
                        text = "Profile details",
                        color = ProfileText,
                        fontFamily = ProfileFont,
                        fontSize = 24.sp,
                        lineHeight = 30.sp,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            }

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 20.dp, top = 12.dp, end = 20.dp),
                shape = RoundedCornerShape(18.dp),
                color = Color.White,
            ) {
                Column(
                    modifier = Modifier.padding(start = 20.dp, top = 16.dp, end = 20.dp, bottom = 94.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Box(
                        modifier = Modifier.size(152.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.profile),
                            contentDescription = "Profile image",
                            modifier = Modifier
                                .size(144.dp)
                                .clip(CircleShape),
                            contentScale = ContentScale.Fit,
                        )
                        Surface(
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(end = 18.dp, bottom = 12.dp)
                                .size(38.dp),
                            shape = CircleShape,
                            color = Color(0xFFE0F1FF),
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Outlined.Edit,
                                    contentDescription = null,
                                    tint = ProfileBlue,
                                    modifier = Modifier.size(21.dp),
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    ProfileInputField(
                        label = "First Name",
                        value = firstName,
                        onValueChange = { firstName = it },
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    ProfileInputField(
                        label = "Last Name",
                        value = lastName,
                        placeholder = "Last Name",
                        onValueChange = { lastName = it },
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    ProfileInputField(
                        label = "Mobile",
                        value = "6398317816",
                        onValueChange = {},
                        enabled = false,
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    ProfileInputField(
                        label = "Email",
                        value = email,
                        placeholder = "Email",
                        onValueChange = { email = it },
                    )
                }
            }
        }

        Surface(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(start = 20.dp, end = 20.dp, bottom = 30.dp)
                .height(66.dp)
                .clickable { },
            shape = RoundedCornerShape(16.dp),
            color = ProfileBlue,
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = "Update profile",
                    color = Color.White,
                    fontFamily = ProfileFont,
                    fontSize = 21.sp,
                    lineHeight = 26.sp,
                    fontWeight = FontWeight.SemiBold,
                )
            }
        }
    }
}

@Composable
private fun ProfileInputField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    enabled: Boolean = true,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier
            .fillMaxWidth()
            .height(62.dp),
        enabled = enabled,
        singleLine = true,
        label = {
            Text(
                text = label,
                fontFamily = ProfileFont,
                fontSize = 14.sp,
                lineHeight = 18.sp,
            )
        },
        placeholder = {
            Text(
                text = placeholder,
                color = Color(0xFFB5B5B5),
                fontFamily = ProfileFont,
                fontSize = 16.sp,
                lineHeight = 20.sp,
            )
        },
        textStyle = androidx.compose.ui.text.TextStyle(
            color = if (enabled) ProfileText else Color(0xFF8E8E8E),
            fontFamily = ProfileFont,
            fontSize = 16.sp,
            lineHeight = 20.sp,
            fontWeight = FontWeight.Medium,
        ),
        shape = RoundedCornerShape(14.dp),
    )
}

@Composable
private fun ProfileHero(
    name: String,
    phoneNumber: String,
    dateOfBirth: String,
    onBack: () -> Unit,
    onEditProfile: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(360.dp)
            .background(
                Brush.verticalGradient(
                    0f to ProfileBlueTop,
                    0.48f to ProfileBlueMid,
                    0.82f to Color(0xFFEAF2FF),
                    1f to ProfilePageBg,
                ),
            ),
    ) {
        Surface(
            modifier = Modifier
                .padding(start = 18.dp, top = 28.dp)
                .size(36.dp)
                .shadow(3.dp, CircleShape, clip = false)
                .clickable(onClick = onBack),
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

        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 58.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Image(
                painter = painterResource(id = R.drawable.profile),
                contentDescription = "Profile image",
                modifier = Modifier
                    .size(86.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Fit,
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = name,
                color = Color.White,
                fontFamily = ProfileFont,
                fontSize = 24.sp,
                lineHeight = 28.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 0.sp,
            )
            Spacer(modifier = Modifier.height(5.dp))
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    imageVector = Icons.Outlined.Phone,
                    contentDescription = null,
                    tint = Color.White.copy(alpha = 0.84f),
                    modifier = Modifier.size(14.dp),
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = phoneNumber,
                    color = Color.White.copy(alpha = 0.84f),
                    fontFamily = ProfileFont,
                    fontSize = 13.sp,
                    lineHeight = 17.sp,
                )
                Spacer(modifier = Modifier.width(10.dp))
                Icon(
                    imageVector = Icons.Outlined.CalendarMonth,
                    contentDescription = null,
                    tint = Color.White.copy(alpha = 0.84f),
                    modifier = Modifier.size(14.dp),
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = dateOfBirth,
                    color = Color.White.copy(alpha = 0.84f),
                    fontFamily = ProfileFont,
                    fontSize = 13.sp,
                    lineHeight = 17.sp,
                )
            }
            Spacer(modifier = Modifier.height(7.dp))
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .clickable(onClick = onEditProfile)
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    imageVector = Icons.Outlined.Edit,
                    contentDescription = null,
                    tint = Color.White.copy(alpha = 0.9f),
                    modifier = Modifier.size(13.dp),
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Edit profile",
                    color = Color.White.copy(alpha = 0.9f),
                    fontFamily = ProfileFont,
                    fontSize = 12.sp,
                    lineHeight = 14.sp,
                    fontWeight = FontWeight.Medium,
                )
            }
        }

        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(start = 12.dp, end = 12.dp, bottom = 0.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            ProfileActionCard(
                iconRes = R.drawable.profilebookings,
                title = "Your orders",
                modifier = Modifier.weight(1f),
            )
            ProfileActionCard(
                iconRes = R.drawable.profilewallet,
                title = "Blinkit\nMoney",
                modifier = Modifier.weight(1f),
            )
            ProfileActionCard(
                iconRes = R.drawable.profilehelp,
                title = "Need help?",
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun ProfileContent(
    onLogout: () -> Unit,
    onSectionSelected: (ProfileSection) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(ProfilePageBg)
            .padding(start = 14.dp, top = 12.dp, end = 14.dp, bottom = 88.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        AppearanceCard()
        ProfileRowsCard(
            title = "Your information",
            rows = listOf(
                ProfileMenuRow("Address book", Icons.Outlined.LocationOn) {
                    onSectionSelected(ProfileSection.ADDRESSES)
                },
                ProfileMenuRow("Bookmarked recipes", Icons.Outlined.MenuBook),
                ProfileMenuRow("Your wishlist", Icons.Outlined.FavoriteBorder),
                ProfileMenuRow("GST details", Icons.Outlined.ReceiptLong),
                ProfileMenuRow("E-gift cards", Icons.Outlined.CardGiftcard),
                ProfileMenuRow("Claim Gift card", Icons.Outlined.LocalOffer),
                ProfileMenuRow("Your collected rewards", Icons.Outlined.AccountBalanceWallet),
            ),
        )
        ProfileRowsCard(
            title = "Other Information",
            rows = listOf(
                ProfileMenuRow("Share the app", Icons.Outlined.IosShare),
                ProfileMenuRow("About us", Icons.Outlined.Info),
                ProfileMenuRow("Account privacy", Icons.Outlined.Lock),
                ProfileMenuRow("Notification preferences", Icons.Outlined.NotificationsNone),
                ProfileMenuRow("Log out", Icons.Outlined.Logout, onClick = onLogout),
            ),
        )
        ProfileFooter()
    }
}

@Composable
private fun ProfileActionCard(
    iconRes: Int,
    title: String,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier
            .height(96.dp)
            .shadow(1.dp, ProfileRadius, clip = false),
        shape = ProfileRadius,
        color = ProfileCard,
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Image(
                painter = painterResource(id = iconRes),
                contentDescription = null,
                modifier = Modifier.size(30.dp),
                contentScale = ContentScale.Fit,
            )
            Spacer(modifier = Modifier.height(7.dp))
            Text(
                text = title,
                color = ProfileText,
                fontFamily = ProfileFont,
                fontSize = 14.sp,
                lineHeight = 17.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
                maxLines = 2,
            )
        }
    }
}

@Composable
private fun AppearanceCard() {
    var expanded by remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(1.dp, ProfileRadius, clip = false),
        shape = ProfileRadius,
        color = ProfileCard,
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(58.dp)
                    .clickable { expanded = !expanded }
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (!expanded) {
                    Icon(
                        imageVector = Icons.Outlined.WbSunny,
                        contentDescription = null,
                        tint = Color(0xFF333333),
                        modifier = Modifier.size(22.dp),
                    )
                    Spacer(modifier = Modifier.width(14.dp))
                }
                Text(
                    text = "Appearance",
                    color = ProfileText,
                    fontFamily = ProfileFont,
                    fontSize = 16.sp,
                    lineHeight = 20.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.weight(1f),
                )
                if (!expanded) {
                    Text(
                        text = "LIGHT",
                        color = Color(0xFF25264A),
                        fontFamily = ProfileFont,
                        fontSize = 13.sp,
                        lineHeight = 16.sp,
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                }
                Icon(
                    imageVector = Icons.Outlined.ExpandMore,
                    contentDescription = null,
                    tint = ProfileChevron,
                    modifier = Modifier
                        .size(20.dp)
                        .graphicsLayer(rotationZ = if (expanded) 180f else 0f),
                )
            }

            if (expanded) {
                ProfileDividerLine(modifier = Modifier.padding(horizontal = 16.dp))
                AppearanceOptionRow(
                    icon = Icons.Outlined.WbSunny,
                    title = "Light Theme",
                    selected = true,
                )
                ProfileDividerLine(modifier = Modifier.padding(start = 16.dp))
                AppearanceOptionRow(
                    icon = Icons.Outlined.DarkMode,
                    title = "Dark Theme",
                    selected = false,
                )
                ProfileDividerLine(modifier = Modifier.padding(start = 16.dp))
                AppearanceOptionRow(
                    icon = Icons.Outlined.ToggleOff,
                    title = "System Theme",
                    selected = false,
                )
            }
        }
    }
}

@Composable
private fun AppearanceOptionRow(
    icon: ImageVector,
    title: String,
    selected: Boolean,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color(0xFF303236),
            modifier = Modifier.size(22.dp),
        )
        Spacer(modifier = Modifier.width(14.dp))
        Text(
            text = title,
            color = ProfileText,
            fontFamily = ProfileFont,
            fontSize = 15.sp,
            lineHeight = 19.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(1f),
        )
        Box(
            modifier = Modifier.size(28.dp),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.Outlined.RadioButtonUnchecked,
                contentDescription = null,
                tint = ProfileBlue,
                modifier = Modifier.size(26.dp),
            )
            if (selected) {
                Box(
                    modifier = Modifier
                        .size(14.dp)
                        .clip(CircleShape)
                        .background(ProfileBlue),
                )
            }
        }
    }
}

@Composable
private fun ProfileRowsCard(
    title: String,
    rows: List<ProfileMenuRow>,
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(1.dp, ProfileRadius, clip = false),
        shape = ProfileRadius,
        color = ProfileCard,
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = title,
                color = ProfileText,
                fontFamily = ProfileFont,
                fontSize = 18.sp,
                lineHeight = 23.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(start = 16.dp, top = 16.dp, end = 16.dp, bottom = 10.dp),
            )
            ProfileDividerLine()
            rows.forEachIndexed { index, row ->
                ProfileSettingsRow(row = row)
                if (index != rows.lastIndex) {
                    ProfileDividerLine(modifier = Modifier.padding(start = 16.dp))
                }
            }
        }
    }
}

@Composable
private fun ProfileSettingsRow(
    row: ProfileMenuRow,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(54.dp)
            .clickable(enabled = row.onClick != null) { row.onClick?.invoke() }
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = row.icon,
            contentDescription = null,
            tint = Color(0xFF303236),
            modifier = Modifier.size(22.dp),
        )
        Spacer(modifier = Modifier.width(14.dp))
        Text(
            text = row.title,
            color = ProfileText,
            fontFamily = ProfileFont,
            fontSize = 15.sp,
            lineHeight = 19.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(1f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Icon(
            imageVector = Icons.Outlined.ChevronRight,
            contentDescription = null,
            tint = ProfileChevron,
            modifier = Modifier.size(20.dp),
        )
    }
}

@Composable
private fun LogoutDialog(
    onCurrentDevice: () -> Unit,
    onAllDevices: () -> Unit,
    onCancel: () -> Unit,
) {
    Dialog(onDismissRequest = onCancel) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            color = Color.White,
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = "Log out from?",
                    color = ProfileText,
                    fontFamily = ProfileFont,
                    fontSize = 26.sp,
                    lineHeight = 32.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(top = 26.dp, bottom = 24.dp),
                )
                ProfileDividerLine()
                LogoutOption(text = "Current Device", onClick = onCurrentDevice)
                ProfileDividerLine()
                LogoutOption(text = "All Devices", onClick = onAllDevices)
                ProfileDividerLine()
                LogoutOption(text = "Cancel", onClick = onCancel)
            }
        }
    }
}

@Composable
private fun LogoutOption(
    text: String,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(58.dp)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            color = ProfileBlue,
            fontFamily = ProfileFont,
            fontSize = 19.sp,
            lineHeight = 24.sp,
            fontWeight = FontWeight.Medium,
        )
    }
}

@Composable
private fun ProfileFooter() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 54.dp, bottom = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "sevam",
            color = Color(0xFFBFC1C7),
            fontFamily = ProfileFont,
            fontSize = 30.sp,
            lineHeight = 34.sp,
            fontWeight = FontWeight.Bold,
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "v1.0.0",
            color = Color(0xFFB2B4BB),
            fontFamily = ProfileFont,
            fontSize = 13.sp,
            lineHeight = 17.sp,
        )
    }
}

@Composable
private fun ProfileSimpleCard(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .shadow(1.dp, ProfileRadius, clip = false),
        shape = ProfileRadius,
        color = ProfileCard,
        content = content,
    )
}

@Composable
private fun ProfilePinnedToolbar(onBack: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(82.dp)
            .shadow(5.dp, RoundedCornerShape(0.dp), clip = false),
        color = Color.White,
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 24.dp, top = 22.dp, end = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = Icons.Outlined.ArrowBack,
                contentDescription = "Back",
                tint = Color(0xFF222222),
                modifier = Modifier
                    .size(30.dp)
                    .clickable(onClick = onBack),
            )
            Spacer(modifier = Modifier.width(28.dp))
            Text(
            text = "Profile",
            color = ProfileText,
            fontFamily = ProfileFont,
            fontSize = 16.sp,
            lineHeight = 20.sp,
                fontWeight = FontWeight.Medium,
            )
        }
    }
}

@Composable
private fun ProfileDividerLine(
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(ProfileDivider),
    )
}
