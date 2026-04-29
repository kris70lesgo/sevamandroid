package com.sevam.customer

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.NotificationsNone
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material.icons.outlined.Storefront
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.sevam.core.common.model.NotificationItem
import com.sevam.core.common.model.SevamSampleData
import com.sevam.core.ui.SevamCard
import com.sevam.core.ui.SevamColors
import com.sevam.core.ui.SevamPrimaryButton
import com.sevam.core.ui.SevamRemoteImage
import com.sevam.features.auth.api.AuthFeatureRoutes
import com.sevam.features.auth.navigation.LoginScreen
import com.sevam.features.auth.navigation.VerifyOtpScreen
import com.sevam.features.bookings.api.BookingsFeatureRoutes
import com.sevam.features.bookings.presentation.BookingsScreen
import com.sevam.features.cart.api.CartFeatureRoutes
import com.sevam.features.cart.presentation.CartScreen
import com.sevam.features.home.api.HomeFeatureRoutes
import com.sevam.features.home.presentation.HomeScreen
import com.sevam.features.notifications.api.NotificationsFeatureRoutes
import com.sevam.features.notifications.presentation.NotificationsScreen
import com.sevam.features.payments.api.PaymentsFeatureRoutes
import com.sevam.features.payments.presentation.CheckoutScreen
import com.sevam.features.payments.presentation.PaymentResultScreen
import com.sevam.features.profile.api.ProfileFeatureRoutes
import com.sevam.features.profile.presentation.ProfileScreen
import com.sevam.features.services.api.ServicesFeatureRoutes
import com.sevam.features.services.presentation.ServicesScreen
import com.sevam.features.tracking.api.TrackingFeatureRoutes
import com.sevam.features.tracking.presentation.TrackingScreen

private const val SEARCH_ROUTE = "search"

private data class BottomNavItem(
    val route: String,
    val label: String,
    val icon: ImageVector,
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SevamNavGraph(
    viewModel: SevamAppViewModel = viewModel(),
) {
    val navController = rememberNavController()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    val shellRoutes = setOf(
        HomeFeatureRoutes.ROOT,
        ServicesFeatureRoutes.ROOT,
        BookingsFeatureRoutes.ROOT,
        ProfileFeatureRoutes.ROOT,
    )
    val showBottomBar = currentRoute in shellRoutes
    val showTopBar = currentRoute != AuthFeatureRoutes.LOGIN && currentRoute != AuthFeatureRoutes.VERIFY_OTP

    LaunchedEffect(uiState.isLoggedIn) {
        if (uiState.isLoggedIn && currentRoute in listOf(null, AuthFeatureRoutes.LOGIN, AuthFeatureRoutes.VERIFY_OTP)) {
            navController.navigate(HomeFeatureRoutes.ROOT) {
                popUpTo(AuthFeatureRoutes.LOGIN) { inclusive = true }
            }
        } else if (!uiState.isLoggedIn && currentRoute !in listOf(AuthFeatureRoutes.LOGIN, AuthFeatureRoutes.VERIFY_OTP)) {
            navController.navigate(AuthFeatureRoutes.LOGIN) {
                popUpTo(navController.graph.startDestinationId) { inclusive = true }
            }
        }
    }

    if (uiState.selectedServiceId != null) {
        ModalBottomSheet(onDismissRequest = viewModel::closeServiceSheet) {
            viewModel.selectedService?.let { service ->
                Column(
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    SevamRemoteImage(imageUrl = service.imageUrl, modifier = Modifier.fillMaxWidth().height(220.dp))
                    Text(service.name, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                    Text(service.description, color = SevamColors.TextSecondary)
                    Text("Our Process", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
                    service.processSteps.forEachIndexed { index, step ->
                        Text("${index + 1}. $step", style = MaterialTheme.typography.bodyMedium)
                    }
                    Text("Duration ${service.durationLabel} | ${service.rating} rating", color = SevamColors.TextSecondary)
                    SevamPrimaryButton(
                        text = "Add to Cart | Rs ${service.price}",
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {
                            viewModel.addToCart(service.id)
                            viewModel.closeServiceSheet()
                        },
                    )
                }
            }
        }
    }

    if (uiState.showAddressPicker) {
        ModalBottomSheet(onDismissRequest = { viewModel.showAddressPicker(false) }) {
            Column(
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                Text("Choose Address", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                uiState.addresses.forEach { address ->
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { viewModel.selectAddress(address.id) },
                        shape = RoundedCornerShape(20.dp),
                        color = if (address.id == uiState.selectedAddressId) SevamColors.OrangeContainer else MaterialTheme.colorScheme.surfaceVariant,
                    ) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(address.label, fontWeight = FontWeight.SemiBold)
                            Text("${address.line1}, ${address.line2}", color = SevamColors.TextSecondary)
                            Text(address.city, color = SevamColors.TextSecondary)
                        }
                    }
                }
                SevamPrimaryButton(
                    text = "Use Current Location",
                    modifier = Modifier.fillMaxWidth(),
                    onClick = viewModel::addMockAddress,
                )
            }
        }
    }

    Scaffold(
        topBar = {
            if (showTopBar) {
                SevamTopBar(
                    locationLabel = viewModel.selectedAddress?.line2 ?: SevamSampleData.banners.first().locationLabel,
                    cartCount = viewModel.cartCount(),
                    notificationCount = viewModel.unreadNotificationsCount(),
                    onOpenSearch = { navController.navigate(SEARCH_ROUTE) },
                    onOpenCart = { navController.navigate(CartFeatureRoutes.ROOT) },
                    onOpenNotifications = { navController.navigate(NotificationsFeatureRoutes.ROOT) },
                )
            }
        },
        bottomBar = {
            if (showBottomBar) {
                val items = listOf(
                    BottomNavItem(HomeFeatureRoutes.ROOT, "Home", Icons.Outlined.Home),
                    BottomNavItem(ServicesFeatureRoutes.ROOT, "Services", Icons.Outlined.Storefront),
                    BottomNavItem(BookingsFeatureRoutes.ROOT, "Bookings", Icons.Outlined.BookmarkBorder),
                    BottomNavItem(ProfileFeatureRoutes.ROOT, "Profile", Icons.Outlined.AccountCircle),
                )
                NavigationBar {
                    items.forEach { item ->
                        val selected = backStackEntry?.destination?.hierarchy?.any { it.route == item.route } == true
                        NavigationBarItem(
                            selected = selected,
                            onClick = {
                                navController.navigate(item.route) {
                                    launchSingleTop = true
                                    restoreState = true
                                    popUpTo(HomeFeatureRoutes.ROOT) { saveState = true }
                                }
                            },
                            icon = { Icon(item.icon, contentDescription = item.label) },
                            label = { Text(item.label) },
                        )
                    }
                }
            }
        },
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = if (uiState.isLoggedIn) HomeFeatureRoutes.ROOT else AuthFeatureRoutes.LOGIN,
            modifier = Modifier.padding(innerPadding),
        ) {
            composable(AuthFeatureRoutes.LOGIN) {
                LoginScreen(
                    phoneNumber = uiState.phoneNumber,
                    onPhoneNumberChange = viewModel::updatePhoneNumber,
                    onContinue = {
                        viewModel.requestOtp()
                        navController.navigate(AuthFeatureRoutes.VERIFY_OTP)
                    },
                )
            }
            composable(AuthFeatureRoutes.VERIFY_OTP) {
                VerifyOtpScreen(
                    phoneNumber = uiState.phoneNumber,
                    otp = uiState.otp,
                    onOtpChange = viewModel::updateOtp,
                    onVerified = {
                        viewModel.completeLogin()
                        navController.navigate(HomeFeatureRoutes.ROOT) {
                            popUpTo(AuthFeatureRoutes.LOGIN) { inclusive = true }
                        }
                    },
                    onBack = { navController.popBackStack() },
                )
            }
            composable(HomeFeatureRoutes.ROOT) {
                HomeScreen(
                    banner = SevamSampleData.banners.first(),
                    categories = SevamSampleData.categories,
                    flashDeals = SevamSampleData.services.take(4),
                    nearbyServices = SevamSampleData.services.takeLast(4),
                    recentBookings = viewModel.pastBookings(),
                    referralCode = uiState.profile.referralCode,
                    trustHighlights = SevamSampleData.trustHighlights,
                    onBrowseServices = { navController.navigate(ServicesFeatureRoutes.ROOT) },
                    onViewAllServices = { navController.navigate(ServicesFeatureRoutes.ROOT) },
                    onServiceClick = viewModel::openService,
                    onBookNow = { serviceId ->
                        viewModel.addToCart(serviceId)
                        navController.navigate(CartFeatureRoutes.ROOT)
                    },
                    onRebook = { bookingId ->
                        viewModel.rebook(bookingId)
                        navController.navigate(CartFeatureRoutes.ROOT)
                    },
                )
            }
            composable(ServicesFeatureRoutes.ROOT) {
                ServicesScreen(
                    categories = SevamSampleData.categories,
                    services = viewModel.filteredServices,
                    selectedCategoryId = uiState.selectedCategoryId,
                    searchQuery = uiState.searchQuery,
                    onSearchQueryChange = viewModel::updateSearchQuery,
                    onCategorySelected = viewModel::selectCategory,
                    onServiceClick = viewModel::openService,
                    onAddToCart = { serviceId ->
                        viewModel.addToCart(serviceId)
                    },
                )
            }
            composable(BookingsFeatureRoutes.ROOT) {
                BookingsScreen(
                    selectedStage = uiState.selectedBookingStage,
                    activeBookings = viewModel.activeBookings(),
                    upcomingBookings = viewModel.upcomingBookings(),
                    pastBookings = viewModel.pastBookings(),
                    onStageSelected = viewModel::selectBookingStage,
                    onOpenTracking = { bookingId -> navController.navigate(TrackingFeatureRoutes.createRoute(bookingId)) },
                    onRebook = { bookingId ->
                        viewModel.rebook(bookingId)
                        navController.navigate(CartFeatureRoutes.ROOT)
                    },
                    onOpenSupport = {},
                )
            }
            composable(ProfileFeatureRoutes.ROOT) {
                ProfileScreen(
                    profile = uiState.profile,
                    addresses = uiState.addresses,
                    walletSummary = uiState.walletSummary,
                    paymentMethods = uiState.paymentMethods,
                    selectedSection = uiState.selectedProfileSection,
                    onSectionSelected = viewModel::selectProfileSection,
                    onAddAddress = { viewModel.showAddressPicker(true) },
                    onSetDefaultAddress = viewModel::setDefaultAddress,
                    onEditAddress = viewModel::editAddress,
                    onDeleteAddress = viewModel::deleteAddress,
                    onLogout = {
                        viewModel.logout()
                        navController.navigate(AuthFeatureRoutes.LOGIN) {
                            popUpTo(HomeFeatureRoutes.ROOT) { inclusive = true }
                        }
                    },
                )
            }
            composable(CartFeatureRoutes.ROOT) {
                CartScreen(
                    entries = uiState.cartEntries,
                    totals = viewModel.cartTotals(),
                    selectedAddress = viewModel.selectedAddress,
                    couponEnabled = false,
                    onIncrease = viewModel::increaseQuantity,
                    onDecrease = viewModel::decreaseQuantity,
                    onSelectAddress = { viewModel.showAddressPicker(true) },
                    onBrowseServices = { navController.navigate(ServicesFeatureRoutes.ROOT) },
                    onProceedToCheckout = { navController.navigate(PaymentsFeatureRoutes.CHECKOUT) },
                )
            }
            composable(PaymentsFeatureRoutes.CHECKOUT) {
                CheckoutScreen(
                    cartEntries = uiState.cartEntries,
                    totals = viewModel.cartTotals(),
                    selectedAddress = viewModel.selectedAddress,
                    paymentMethods = uiState.paymentMethods,
                    selectedPaymentMethodId = uiState.selectedPaymentMethodId,
                    walletSummary = uiState.walletSummary,
                    onPaymentMethodSelected = viewModel::selectPaymentMethod,
                    onConfirmPayment = {
                        val outcome = viewModel.confirmPayment()
                        navController.navigate(PaymentsFeatureRoutes.resultRoute(outcome))
                    },
                )
            }
            composable(
                route = PaymentsFeatureRoutes.RESULT_PATTERN,
                arguments = listOf(navArgument(PaymentsFeatureRoutes.ARG_OUTCOME) { type = NavType.StringType }),
            ) { entry ->
                PaymentResultScreen(
                    outcome = entry.arguments?.getString(PaymentsFeatureRoutes.ARG_OUTCOME).orEmpty(),
                    onGoToBookings = {
                        navController.navigate(BookingsFeatureRoutes.ROOT) {
                            popUpTo(HomeFeatureRoutes.ROOT)
                        }
                    },
                )
            }
            composable(NotificationsFeatureRoutes.ROOT) {
                NotificationsScreen(
                    notifications = uiState.notifications,
                    onNotificationClick = { notification ->
                        viewModel.markNotificationRead(notification.id)
                        handleNotificationNavigation(notification, navController, viewModel)
                    },
                )
            }
            composable(SEARCH_ROUTE) {
                SearchScreen(
                    query = uiState.searchQuery,
                    onQueryChange = viewModel::updateSearchQuery,
                    recentSearches = SevamSampleData.recentSearches.map { it.label },
                    services = viewModel.filteredServices,
                    onSuggestionClick = { suggestion ->
                        viewModel.updateSearchQuery(suggestion)
                    },
                    onServiceClick = viewModel::openService,
                )
            }
            composable(
                route = TrackingFeatureRoutes.ROUTE_PATTERN,
                arguments = listOf(navArgument(TrackingFeatureRoutes.ARG_BOOKING_ID) { type = NavType.StringType }),
            ) { entry ->
                val bookingId = entry.arguments?.getString(TrackingFeatureRoutes.ARG_BOOKING_ID)
                viewModel.bookingById(bookingId)?.let { booking ->
                    TrackingScreen(
                        booking = booking,
                        onCallWorker = {},
                        onContactSupport = {},
                    )
                }
            }
        }
    }
}

private fun handleNotificationNavigation(
    notification: NotificationItem,
    navController: androidx.navigation.NavHostController,
    viewModel: SevamAppViewModel,
) {
    val booking = notification.bookingId?.let(viewModel::bookingById)
    when (booking?.stage) {
        com.sevam.core.common.model.BookingStage.ACTIVE -> {
            navController.navigate(TrackingFeatureRoutes.createRoute(booking.id))
        }
        com.sevam.core.common.model.BookingStage.UPCOMING,
        com.sevam.core.common.model.BookingStage.PAST,
        null -> {
            navController.navigate(BookingsFeatureRoutes.ROOT)
        }
    }
}

@Composable
private fun SevamTopBar(
    locationLabel: String,
    cartCount: Int,
    notificationCount: Int,
    onOpenSearch: () -> Unit,
    onOpenCart: () -> Unit,
    onOpenNotifications: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Outlined.LocationOn, contentDescription = null, tint = SevamColors.Orange)
                Column {
                    Text("Sevam", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Text(locationLabel, style = MaterialTheme.typography.bodySmall, color = SevamColors.TextSecondary)
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
                IconBadgeButton(icon = Icons.Outlined.NotificationsNone, count = notificationCount, onClick = onOpenNotifications)
                IconBadgeButton(icon = Icons.Outlined.ShoppingCart, count = cartCount, onClick = onOpenCart)
            }
        }
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onOpenSearch),
            shape = RoundedCornerShape(18.dp),
            color = MaterialTheme.colorScheme.surfaceVariant,
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(Icons.Outlined.Search, contentDescription = null, tint = SevamColors.TextSecondary)
                Spacer(Modifier.width(10.dp))
                Text(
                    text = "Search services (cleaning, AC repair...)",
                    color = SevamColors.TextSecondary,
                )
            }
        }
    }
}

@Composable
private fun IconBadgeButton(
    icon: ImageVector,
    count: Int,
    onClick: () -> Unit,
) {
    Surface(
        shape = CircleShape,
        color = MaterialTheme.colorScheme.surfaceVariant,
        modifier = Modifier.clickable(onClick = onClick),
    ) {
        BadgedBox(
            badge = {
                if (count > 0) {
                    Badge { Text(count.toString()) }
                }
            },
            modifier = Modifier.padding(12.dp),
        ) {
            Icon(icon, contentDescription = null)
        }
    }
}

@Composable
private fun SearchScreen(
    query: String,
    onQueryChange: (String) -> Unit,
    recentSearches: List<String>,
    services: List<com.sevam.core.common.model.ServiceItem>,
    onSuggestionClick: (String) -> Unit,
    onServiceClick: (String) -> Unit,
) {
    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        androidx.compose.material3.OutlinedTextField(
            value = query,
            onValueChange = onQueryChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Search for any service") },
            shape = RoundedCornerShape(18.dp),
        )
        Text("Recent Searches", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
        LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            items(recentSearches) { suggestion ->
                Surface(
                    shape = RoundedCornerShape(999.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.clickable { onSuggestionClick(suggestion) },
                ) {
                    Text(suggestion, modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp))
                }
            }
        }
        Text("Results", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            services.take(5).forEach { service ->
                SevamCard(modifier = Modifier.clickable { onServiceClick(service.id) }) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        horizontalArrangement = Arrangement.spacedBy(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Box(modifier = Modifier.size(80.dp)) {
                            SevamRemoteImage(imageUrl = service.imageUrl, modifier = Modifier.fillMaxWidth().height(80.dp))
                        }
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(service.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                            Text(service.description, style = MaterialTheme.typography.bodySmall, color = SevamColors.TextSecondary, maxLines = 2)
                            Text("Rs ${service.price}", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
