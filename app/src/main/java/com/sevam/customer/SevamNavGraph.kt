package com.sevam.customer

import com.sevam.customer.BuildConfig
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.hilt.navigation.compose.hiltViewModel
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
import com.sevam.features.home.presentation.HomeBottomDock
import com.sevam.features.home.presentation.HomeDockTab
import com.sevam.features.home.presentation.HomeScreen
import com.sevam.features.notifications.api.NotificationsFeatureRoutes
import com.sevam.features.notifications.presentation.NotificationsScreen
import com.sevam.features.payments.api.PaymentsFeatureRoutes
import com.sevam.features.payments.presentation.CheckoutScreen
import com.sevam.features.payments.presentation.PaymentResultScreen
import com.sevam.features.profile.api.ProfileFeatureRoutes
import com.sevam.features.profile.presentation.EditProfileScreen
import com.sevam.features.profile.presentation.ProfileScreen
import com.sevam.features.services.api.ServicesFeatureRoutes
import com.sevam.features.services.presentation.ServicesScreen
import com.sevam.features.tracking.api.TrackingFeatureRoutes
import com.sevam.features.tracking.presentation.TrackingScreen
import kotlinx.coroutines.launch

private const val SEARCH_ROUTE = "search"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SevamNavGraph(
    viewModel: SevamAppViewModel = hiltViewModel(),
) {
    val navController = rememberNavController()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val coroutineScope = rememberCoroutineScope()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    var shellHeaderCollapseProgress by remember { mutableFloatStateOf(0f) }
    val shellRoutes = setOf(
        HomeFeatureRoutes.ROOT,
        ServicesFeatureRoutes.ROOT,
        BookingsFeatureRoutes.ROOT,
    )
    val showBottomBar = currentRoute in shellRoutes
    val showTopBar = currentRoute != AuthFeatureRoutes.LOGIN &&
        currentRoute != AuthFeatureRoutes.VERIFY_OTP &&
        currentRoute != ProfileFeatureRoutes.ROOT &&
        currentRoute != ProfileFeatureRoutes.EDIT

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
                DeliveryHeader(
                    addressSubtitle = viewModel.selectedAddress?.line2 ?: uiState.banners.first().locationLabel,
                    onLocationPressed = { viewModel.showAddressPicker(true) },
                    onProfilePressed = { navController.navigate(ProfileFeatureRoutes.ROOT) },
                    onSearchPressed = { navController.navigate(SEARCH_ROUTE) },
                    onVoiceSearchPressed = { navController.navigate(SEARCH_ROUTE) },
                    collapseProgress = if (currentRoute in shellRoutes) shellHeaderCollapseProgress else 0f,
                )
            }
        },
        bottomBar = {
            if (showBottomBar) {
                HomeBottomDock(
                    selectedTab = when (currentRoute) {
                        BookingsFeatureRoutes.ROOT -> HomeDockTab.OrderAgain
                        ServicesFeatureRoutes.ROOT -> HomeDockTab.Categories
                        else -> HomeDockTab.Home
                    },
                    onTabSelected = { tab ->
                        when (tab) {
                            HomeDockTab.Home -> {
                                val returnedHome = navController.popBackStack(HomeFeatureRoutes.ROOT, false)
                                if (!returnedHome) {
                                    navController.navigate(HomeFeatureRoutes.ROOT) {
                                        launchSingleTop = true
                                    }
                                }
                            }
                            HomeDockTab.OrderAgain -> {
                                navController.navigate(BookingsFeatureRoutes.ROOT) {
                                    launchSingleTop = true
                                    restoreState = true
                                    popUpTo(HomeFeatureRoutes.ROOT) {
                                        saveState = true
                                    }
                                }
                            }
                            HomeDockTab.Categories -> {
                                navController.navigate(ServicesFeatureRoutes.ROOT) {
                                    launchSingleTop = true
                                    restoreState = true
                                    popUpTo(HomeFeatureRoutes.ROOT) {
                                        saveState = true
                                    }
                                }
                            }
                        }
                    }
                )
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
                    errorMessage = uiState.authErrorMessage,
                    isSubmitting = uiState.isRequestingOtp,
                    showDebugBypass = BuildConfig.DEBUG,
                    onPhoneNumberChange = viewModel::updatePhoneNumber,
                    onContinue = {
                        viewModel.requestOtp {
                            navController.navigate(AuthFeatureRoutes.VERIFY_OTP) {
                                launchSingleTop = true
                            }
                        }
                    },
                    onDebugContinue = viewModel::completeDebugLogin,
                )
            }
            composable(AuthFeatureRoutes.VERIFY_OTP) {
                VerifyOtpScreen(
                    phoneNumber = uiState.phoneNumber,
                    otp = uiState.otp,
                    errorMessage = uiState.authErrorMessage,
                    isSubmitting = uiState.isVerifyingOtp,
                    showDebugBypass = BuildConfig.DEBUG,
                    onOtpChange = viewModel::updateOtp,
                    onVerified = viewModel::completeLogin,
                    onDebugContinue = viewModel::completeDebugLogin,
                    onBack = { navController.popBackStack() },
                )
            }
            composable(HomeFeatureRoutes.ROOT) {
                HomeScreen(
                    banners = uiState.banners,
                    categories = uiState.categories,
                    flashDeals = uiState.services.take(4),
                    nearbyServices = uiState.services.takeLast(4),
                    recentBookings = viewModel.pastBookings(),
                    referralCode = uiState.profile.referralCode,
                    onOpenSearch = { navController.navigate(SEARCH_ROUTE) },
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
                    onScrollProgressChanged = { progress ->
                        shellHeaderCollapseProgress = progress
                    },
                )
            }
            composable(ServicesFeatureRoutes.ROOT) {
                ServicesScreen(
                    categories = uiState.categories,
                    services = viewModel.filteredServices,
                    selectedCategoryId = uiState.selectedCategoryId,
                    searchQuery = uiState.searchQuery,
                    onSearchQueryChange = viewModel::updateSearchQuery,
                    onCategorySelected = viewModel::selectCategory,
                    onServiceClick = viewModel::openService,
                    onAddToCart = { serviceId ->
                        viewModel.addToCart(serviceId)
                    },
                    onScrollProgressChanged = { progress ->
                        shellHeaderCollapseProgress = progress
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
                    onScrollProgressChanged = { progress ->
                        shellHeaderCollapseProgress = progress
                    },
                )
            }
            composable(ProfileFeatureRoutes.ROOT) {
                ProfileScreen(
                    profile = uiState.profile,
                    addresses = uiState.addresses,
                    paymentMethods = uiState.paymentMethods,
                    selectedSection = uiState.selectedProfileSection,
                    onSectionSelected = viewModel::selectProfileSection,
                    onAddAddress = { viewModel.showAddressPicker(true) },
                    onSetDefaultAddress = viewModel::setDefaultAddress,
                    onEditAddress = viewModel::editAddress,
                    onDeleteAddress = viewModel::deleteAddress,
                    onBack = { navController.popBackStack() },
                    onEditProfile = { navController.navigate(ProfileFeatureRoutes.EDIT) },
                    onLogout = {
                        viewModel.logout()
                        navController.navigate(AuthFeatureRoutes.LOGIN) {
                            popUpTo(HomeFeatureRoutes.ROOT) { inclusive = true }
                        }
                    },
                )
            }
            composable(ProfileFeatureRoutes.EDIT) {
                EditProfileScreen(
                    onBack = { navController.popBackStack() },
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
                    onPaymentMethodSelected = viewModel::selectPaymentMethod,
                    onConfirmPayment = {
                        coroutineScope.launch {
                            val outcome = viewModel.confirmPayment()
                            navController.navigate(PaymentsFeatureRoutes.resultRoute(outcome))
                        }
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
                    recentSearches = uiState.recentSearches.map { it.label },
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
                    LaunchedEffect(booking.id) {
                        viewModel.startTracking(booking.id)
                    }
                    TrackingScreen(
                        booking = booking,
                        trackingSummary = viewModel.trackingSummaryFor(booking.id),
                        trackingError = viewModel.trackingErrorFor(booking.id),
                        isTrackingActive = viewModel.isTrackingActive(booking.id),
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
