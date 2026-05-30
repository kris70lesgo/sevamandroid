package com.sevam.customer

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.sevam.core.common.model.Address
import com.sevam.core.common.model.Booking
import com.sevam.core.common.model.BookingStage
import com.sevam.core.common.model.CartEntry
import com.sevam.core.common.model.NotificationItem
import com.sevam.core.common.model.PaymentMethod
import com.sevam.core.common.model.PromoBanner
import com.sevam.core.common.model.SearchSuggestion
import com.sevam.core.common.model.ServiceCategory
import com.sevam.core.common.model.ServiceItem
import com.sevam.core.common.model.SevamSampleData
import com.sevam.core.common.model.UserProfile
import com.sevam.core.common.model.activeBookings
import com.sevam.core.common.model.calculateCartTotals
import com.sevam.core.common.model.pastBookings
import com.sevam.core.common.model.upcomingBookings
import com.sevam.core.location.LocationSearchClient
import com.sevam.core.realtime.SevamRealtimeClient
import com.sevam.core.realtime.WorkerLocationUpdate
import com.sevam.customer.sevam.data.AddressRepository
import com.sevam.customer.sevam.data.AuthRepository
import com.sevam.customer.sevam.data.BookingsRepository
import com.sevam.customer.sevam.data.CatalogRepository
import com.sevam.customer.sevam.data.NotificationsRepository
import com.sevam.customer.sevam.data.PaymentsRepository
import com.sevam.customer.sevam.data.ProfileRepository
import com.sevam.features.profile.presentation.ProfileSection
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.util.UUID

data class SevamAppUiState(
    val isLoggedIn: Boolean = false,
    val isDebugSession: Boolean = false,
    val phoneNumber: String = "",
    val otp: String = "",
    val authErrorMessage: String? = null,
    val isRequestingOtp: Boolean = false,
    val isVerifyingOtp: Boolean = false,
    val banners: List<PromoBanner> = SevamSampleData.banners,
    val categories: List<ServiceCategory> = SevamSampleData.categories,
    val services: List<ServiceItem> = SevamSampleData.services,
    val recentSearches: List<SearchSuggestion> = SevamSampleData.recentSearches,
    val trustHighlights: List<String> = SevamSampleData.trustHighlights,
    val selectedCategoryId: String = "all",
    val searchQuery: String = "",
    val cartEntries: List<CartEntry> = emptyList(),
    val addresses: List<Address> = SevamSampleData.addresses,
    val selectedAddressId: String = SevamSampleData.addresses.first().id,
    val bookings: List<Booking> = SevamSampleData.bookings,
    val notifications: List<NotificationItem> = SevamSampleData.notifications,
    val profile: UserProfile = SevamSampleData.userProfile,
    val paymentMethods: List<PaymentMethod> = SevamSampleData.paymentMethods,
    val selectedPaymentMethodId: String = SevamSampleData.paymentMethods.first().id,
    val selectedBookingStage: BookingStage = BookingStage.ACTIVE,
    val selectedProfileSection: ProfileSection = ProfileSection.PERSONAL,
    val selectedServiceId: String? = null,
    val showAddressPicker: Boolean = false,
    val activeTrackingBookingId: String? = null,
    val trackingUpdates: Map<String, WorkerLocationUpdate> = emptyMap(),
    val trackingErrors: Map<String, String> = emptyMap(),
)

private const val OTP_MAX_LENGTH = 6

@HiltViewModel
class SevamAppViewModel @Inject constructor(
    application: Application,
    private val authRepository: AuthRepository,
    private val catalogRepository: CatalogRepository,
    private val profileRepository: ProfileRepository,
    private val addressRepository: AddressRepository,
    private val bookingsRepository: BookingsRepository,
    private val paymentsRepository: PaymentsRepository,
    private val notificationsRepository: NotificationsRepository,
    private val locationSearchClient: LocationSearchClient,
    private val realtimeClient: SevamRealtimeClient,
) : AndroidViewModel(application) {

    private var trackingJob: Job? = null

    private val _uiState = MutableStateFlow(SevamAppUiState())
    val uiState: StateFlow<SevamAppUiState> = _uiState

    init {
        viewModelScope.launch {
            authRepository.restoreSession()?.let { session ->
                _uiState.update {
                    it.copy(
                        isLoggedIn = true,
                        isDebugSession = false,
                        phoneNumber = session.phoneNumber,
                    )
                }
            }
            refreshCatalog()
            refreshNotifications()
            if (_uiState.value.isLoggedIn) {
                refreshAuthenticatedData()
            }
        }
    }

    val filteredServices: List<ServiceItem>
        get() {
            val state = _uiState.value
            return state.services.filter { service ->
                val matchesCategory = state.selectedCategoryId == "all" || service.categoryId == state.selectedCategoryId
                val query = state.searchQuery.trim()
                val matchesQuery = query.isBlank() ||
                    service.name.contains(query, ignoreCase = true) ||
                    service.description.contains(query, ignoreCase = true)
                matchesCategory && matchesQuery
            }
        }

    val selectedService: ServiceItem?
        get() = _uiState.value.services.firstOrNull { it.id == _uiState.value.selectedServiceId }

    val selectedAddress: Address?
        get() = _uiState.value.addresses.firstOrNull { it.id == _uiState.value.selectedAddressId }

    fun updatePhoneNumber(value: String) {
        _uiState.update { it.copy(phoneNumber = value, authErrorMessage = null) }
    }

    fun updateOtp(value: String) {
        val sanitizedOtp = value.filter(Char::isDigit).take(OTP_MAX_LENGTH)
        _uiState.update { it.copy(otp = sanitizedOtp, authErrorMessage = null) }
    }

    fun requestOtp(onSuccess: () -> Unit = {}) {
        val phone = normalizePhoneNumber(_uiState.value.phoneNumber.ifBlank { "+91 98765 43210" })
        _uiState.update {
            it.copy(
                phoneNumber = phone,
                authErrorMessage = null,
                isRequestingOtp = true,
            )
        }
        viewModelScope.launch {
            if (authRepository.isConfigured) {
                authRepository.requestOtp(phone)
                    .onSuccess {
                        _uiState.update { state -> state.copy(isRequestingOtp = false) }
                        onSuccess()
                    }
                    .onFailure { throwable ->
                        _uiState.update { state ->
                            state.copy(
                                isRequestingOtp = false,
                                authErrorMessage = throwable.toAuthMessage(defaultMessage = "We couldn't send the OTP. Please try again."),
                            )
                        }
                    }
            } else {
                _uiState.update { state -> state.copy(isRequestingOtp = false) }
                onSuccess()
            }
        }
    }

    fun completeLogin() {
        val phone = normalizePhoneNumber(_uiState.value.phoneNumber.ifBlank { "+91 98765 43210" })
        val otp = _uiState.value.otp
        _uiState.update { it.copy(authErrorMessage = null, isVerifyingOtp = true) }
        viewModelScope.launch {
            if (!authRepository.isConfigured) {
                _uiState.update { it.copy(isLoggedIn = true, isVerifyingOtp = false) }
                return@launch
            }
            authRepository.verifyOtp(phone, otp)
                .onSuccess {
                    _uiState.update { state ->
                        state.copy(
                            isLoggedIn = true,
                            isDebugSession = false,
                            phoneNumber = it.phoneNumber,
                            isVerifyingOtp = false,
                            authErrorMessage = null,
                        )
                    }
                    refreshAuthenticatedData()
                }
                .onFailure { throwable ->
                    _uiState.update { state ->
                        state.copy(
                            isVerifyingOtp = false,
                            authErrorMessage = throwable.toAuthMessage(defaultMessage = "We couldn't verify that OTP. Please check the code and try again."),
                        )
                    }
                }
        }
    }

    fun completeDebugLogin() {
        val phone = normalizePhoneNumber(_uiState.value.phoneNumber.ifBlank { "+91 98765 43210" })
        _uiState.update {
            it.copy(
                isLoggedIn = true,
                isDebugSession = true,
                phoneNumber = phone,
                otp = "",
                authErrorMessage = null,
                isRequestingOtp = false,
                isVerifyingOtp = false,
            )
        }
        viewModelScope.launch {
            refreshAuthenticatedData()
        }
    }

    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
            _uiState.update { current ->
                current.copy(
                    isLoggedIn = false,
                    otp = "",
                    authErrorMessage = null,
                    isRequestingOtp = false,
                    isVerifyingOtp = false,
                    isDebugSession = false,
                    selectedServiceId = null,
                    addresses = SevamSampleData.addresses,
                    bookings = SevamSampleData.bookings,
                    profile = SevamSampleData.userProfile,
                    activeTrackingBookingId = null,
                    trackingUpdates = emptyMap(),
                    trackingErrors = emptyMap(),
                )
            }
        }
    }

    fun updateSearchQuery(value: String) {
        _uiState.update { it.copy(searchQuery = value) }
    }

    fun selectCategory(categoryId: String) {
        _uiState.update { it.copy(selectedCategoryId = categoryId) }
    }

    fun openService(serviceId: String) {
        _uiState.update { it.copy(selectedServiceId = serviceId) }
    }

    fun closeServiceSheet() {
        _uiState.update { it.copy(selectedServiceId = null) }
    }

    fun addToCart(serviceId: String) {
        _uiState.update { state ->
            val existing = state.cartEntries.firstOrNull { it.service.id == serviceId }
            val updated = if (existing == null) {
                state.cartEntries + CartEntry(
                    service = state.services.first { it.id == serviceId },
                    quantity = 1,
                )
            } else {
                state.cartEntries.map { entry ->
                    if (entry.service.id == serviceId) entry.copy(quantity = entry.quantity + 1) else entry
                }
            }
            state.copy(cartEntries = updated)
        }
    }

    fun increaseQuantity(serviceId: String) = addToCart(serviceId)

    fun decreaseQuantity(serviceId: String) {
        _uiState.update { state ->
            val updated = state.cartEntries.mapNotNull { entry ->
                if (entry.service.id != serviceId) {
                    entry
                } else if (entry.quantity <= 1) {
                    null
                } else {
                    entry.copy(quantity = entry.quantity - 1)
                }
            }
            state.copy(cartEntries = updated)
        }
    }

    fun showAddressPicker(show: Boolean) {
        _uiState.update { it.copy(showAddressPicker = show) }
    }

    fun selectAddress(addressId: String) {
        _uiState.update {
            it.copy(
                selectedAddressId = addressId,
                showAddressPicker = false,
            )
        }
    }

    fun addMockAddress() {
        viewModelScope.launch {
            val deviceLocation = locationSearchClient.currentLocationHint().firstOrNull()
            val reverse = deviceLocation?.let {
                locationSearchClient.reverseGeocode(it.latitude, it.longitude).firstOrNull()
            }
            val fallbackAddress = Address(
                id = "addr-${_uiState.value.addresses.size + 1}",
                label = "Current Location",
                line1 = reverse?.shortLabel ?: "Current location pin",
                line2 = reverse?.fullAddress ?: "Mapbox reverse geocode pending",
                city = "Bangalore",
                latitude = deviceLocation?.latitude,
                longitude = deviceLocation?.longitude,
            )
            val createdAddress = addressRepository.createAddress(fallbackAddress).getOrNull() ?: fallbackAddress
            _uiState.update { state ->
                state.copy(
                    addresses = state.addresses + createdAddress,
                    selectedAddressId = createdAddress.id,
                    showAddressPicker = false,
                )
            }
        }
    }

    fun setDefaultAddress(addressId: String) {
        viewModelScope.launch {
            val current = _uiState.value.addresses.firstOrNull { it.id == addressId } ?: return@launch
            val updatedAddress = addressRepository.updateAddress(current.copy(isDefault = true)).getOrNull()
            _uiState.update { state ->
                state.copy(
                    addresses = state.addresses.map { address ->
                        when {
                            address.id == addressId && updatedAddress != null -> updatedAddress
                            else -> address.copy(isDefault = address.id == addressId)
                        }
                    },
                    selectedAddressId = addressId,
                )
            }
        }
    }

    fun editAddress(addressId: String) {
        viewModelScope.launch {
            val current = _uiState.value.addresses.firstOrNull { it.id == addressId } ?: return@launch
            val edited = current.copy(line2 = "${current.line2} | updated")
            val remote = addressRepository.updateAddress(edited).getOrNull() ?: edited
            _uiState.update { state ->
                state.copy(
                    addresses = state.addresses.map { address ->
                        if (address.id == addressId) remote else address
                    },
                )
            }
        }
    }

    fun deleteAddress(addressId: String) {
        viewModelScope.launch {
            addressRepository.deleteAddress(addressId)
            _uiState.update { state ->
                val updated = state.addresses.filterNot { it.id == addressId }
                if (updated.isEmpty()) {
                    state
                } else {
                    state.copy(
                        addresses = updated,
                        selectedAddressId = if (state.selectedAddressId == addressId) updated.first().id else state.selectedAddressId,
                    )
                }
            }
        }
    }

    fun selectBookingStage(stage: BookingStage) {
        _uiState.update { it.copy(selectedBookingStage = stage) }
    }

    fun selectProfileSection(section: ProfileSection) {
        _uiState.update { it.copy(selectedProfileSection = section) }
    }

    fun selectPaymentMethod(methodId: String) {
        _uiState.update { it.copy(selectedPaymentMethodId = methodId) }
    }

    fun cartCount(): Int = _uiState.value.cartEntries.sumOf { it.quantity }

    fun cartTotals() = calculateCartTotals(_uiState.value.cartEntries)

    fun activeBookings(): List<Booking> = _uiState.value.bookings.activeBookings()

    fun upcomingBookings(): List<Booking> = _uiState.value.bookings.upcomingBookings()

    fun pastBookings(): List<Booking> = _uiState.value.bookings.pastBookings()

    fun rebook(bookingId: String) {
        val booking = _uiState.value.bookings.firstOrNull { it.id == bookingId } ?: return
        addToCart(booking.service.id)
    }

    fun markNotificationRead(notificationId: String) {
        _uiState.update { state ->
            state.copy(
                notifications = state.notifications.map { notification ->
                    if (notification.id == notificationId) notification.copy(isUnread = false) else notification
                },
            )
        }
    }

    fun unreadNotificationsCount(): Int = _uiState.value.notifications.count { it.isUnread }

    fun bookingById(bookingId: String?): Booking? = _uiState.value.bookings.firstOrNull { it.id == bookingId }

    fun trackingSummaryFor(bookingId: String): String? {
        val update = _uiState.value.trackingUpdates[bookingId] ?: return null
        val coordinates = listOfNotNull(update.latitude?.let { "Lat ${"%.5f".format(it)}" }, update.longitude?.let { "Lng ${"%.5f".format(it)}" })
            .joinToString("  |  ")
        val timestamp = update.timestampIso?.let { "Last update $it" }
        return listOfNotNull(
            coordinates.ifBlank { null },
            timestamp,
        ).joinToString("\n").ifBlank { null }
    }

    fun trackingErrorFor(bookingId: String): String? = _uiState.value.trackingErrors[bookingId]

    fun isTrackingActive(bookingId: String): Boolean = _uiState.value.activeTrackingBookingId == bookingId

    suspend fun confirmPayment(): String {
        val selectedMethod = _uiState.value.paymentMethods.firstOrNull { it.id == _uiState.value.selectedPaymentMethodId }
        if (selectedMethod == null) return "dismissed"
        if (!selectedMethod.isLive) return "dismissed"
        if (_uiState.value.cartEntries.isEmpty()) return "failed"
        val selectedAddress = selectedAddress ?: SevamSampleData.addresses.first()

        if (paymentsRepository.isConfigured) {
            val remoteResult = paymentsRepository.confirmPayment(
                entries = _uiState.value.cartEntries,
                address = selectedAddress,
                method = selectedMethod,
                profile = _uiState.value.profile,
            )
            if (remoteResult.isSuccess) {
                val bookingReference = remoteResult.getOrNull().orEmpty()
                val newBooking = buildConfirmedBooking(selectedMethod, selectedAddress, bookingReference)
                _uiState.update { state ->
                    state.copy(
                        bookings = listOf(newBooking) + state.bookings,
                        cartEntries = emptyList(),
                        selectedBookingStage = BookingStage.ACTIVE,
                        notifications = listOf(
                            NotificationItem(
                                id = "notif-${UUID.randomUUID()}",
                                title = "Booking confirmed",
                                body = "${newBooking.service.name} is now live in your bookings.",
                                timeLabel = "Just now",
                                isUnread = true,
                                bookingId = newBooking.id,
                            ),
                        ) + state.notifications,
                    )
                }
                return "success"
            }
        }

        val newBooking = buildConfirmedBooking(selectedMethod, selectedAddress, "SVM-${(20000..29999).random()}")
        _uiState.update { state ->
            state.copy(
                bookings = listOf(newBooking) + state.bookings,
                cartEntries = emptyList(),
                selectedBookingStage = BookingStage.ACTIVE,
                notifications = listOf(
                    NotificationItem(
                        id = "notif-${UUID.randomUUID()}",
                        title = "Booking confirmed",
                        body = "${newBooking.service.name} is ready to track in Bookings.",
                        timeLabel = "Just now",
                        isUnread = true,
                        bookingId = newBooking.id,
                    ),
                ) + state.notifications,
            )
        }
        return "success"
    }

    fun startTracking(bookingId: String) {
        if (_uiState.value.activeTrackingBookingId == bookingId && trackingJob?.isActive == true) return
        trackingJob?.cancel()
        _uiState.update {
            it.copy(
                activeTrackingBookingId = bookingId,
                trackingErrors = it.trackingErrors - bookingId,
            )
        }
        trackingJob = viewModelScope.launch {
            runCatching {
                realtimeClient.trackWorkerLocation(bookingId).collect { update ->
                    _uiState.update { state ->
                        state.copy(
                            trackingUpdates = state.trackingUpdates + (bookingId to update),
                            trackingErrors = state.trackingErrors - bookingId,
                        )
                    }
                }
            }.onFailure { throwable ->
                _uiState.update { state ->
                    state.copy(
                        trackingErrors = state.trackingErrors + (
                            bookingId to (
                                throwable.message?.takeIf { it.isNotBlank() }
                                    ?: "Live worker tracking is unavailable for this session right now."
                                )
                            ),
                    )
                }
            }
        }
    }

    private suspend fun refreshCatalog() {
        catalogRepository.fetchCatalog().onSuccess { snapshot ->
            _uiState.update {
                it.copy(
                    categories = snapshot.categories,
                    services = snapshot.services,
                )
            }
        }
    }

    private suspend fun refreshAuthenticatedData() {
        profileRepository.fetchProfile().onSuccess { profile ->
            _uiState.update { it.copy(profile = profile) }
        }
        addressRepository.fetchAddresses().onSuccess { addresses ->
            if (addresses.isNotEmpty()) {
                _uiState.update {
                    it.copy(
                        addresses = addresses,
                        selectedAddressId = addresses.firstOrNull { address -> address.isDefault }?.id ?: addresses.first().id,
                    )
                }
            }
        }
        bookingsRepository.fetchBookings().onSuccess { bookings ->
            _uiState.update { it.copy(bookings = bookings) }
        }
    }

    private suspend fun refreshNotifications() {
        notificationsRepository.fetchNotifications().onSuccess { notifications ->
            _uiState.update { it.copy(notifications = notifications) }
        }
    }

    private fun buildConfirmedBooking(
        selectedMethod: PaymentMethod,
        selectedAddress: Address,
        bookingReference: String,
    ): Booking {
        val firstService = _uiState.value.cartEntries.first().service
        return Booking(
            id = "booking-${UUID.randomUUID()}",
            stage = BookingStage.ACTIVE,
            service = firstService,
            dateLabel = "Today",
            timeLabel = "Next available slot",
            address = selectedAddress,
            paymentMethod = selectedMethod.title,
            totalAmount = cartTotals().total,
            bookingReference = bookingReference,
            statusLabel = "Confirmed",
            etaLabel = "Worker assignment in progress",
            supportHint = "You will receive push updates as soon as a professional accepts the job.",
            worker = null,
            steps = listOf(
                com.sevam.core.common.model.BookingStep("Confirmed", com.sevam.core.common.model.BookingStepState.CURRENT),
                com.sevam.core.common.model.BookingStep("En Route", com.sevam.core.common.model.BookingStepState.UPCOMING),
                com.sevam.core.common.model.BookingStep("Arrived", com.sevam.core.common.model.BookingStepState.UPCOMING),
                com.sevam.core.common.model.BookingStep("In Progress", com.sevam.core.common.model.BookingStepState.UPCOMING),
                com.sevam.core.common.model.BookingStep("Done", com.sevam.core.common.model.BookingStepState.UPCOMING),
            ),
        )
    }

    private fun normalizePhoneNumber(rawPhoneNumber: String): String {
        val trimmed = rawPhoneNumber.trim()
        if (trimmed.isBlank()) return trimmed
        return if (trimmed.startsWith("+")) {
            buildString {
                append('+')
                append(trimmed.drop(1).filter(Char::isDigit))
            }
        } else {
            val digits = trimmed.filter(Char::isDigit)
            when {
                digits.length == 10 -> "+91$digits"
                digits.length == 12 && digits.startsWith("91") -> "+$digits"
                else -> digits.ifBlank { trimmed }
            }
        }
    }

    private fun Throwable.toAuthMessage(defaultMessage: String): String {
        val message = message?.trim().orEmpty()
        if (message.isBlank()) return defaultMessage
        return when {
            "sms" in message.lowercase() && "disabled" in message.lowercase() ->
                "SMS login is disabled in Supabase. Enable phone auth and the SMS provider for this project."
            "phone provider" in message.lowercase() ->
                "Phone auth isn't fully configured in Supabase yet."
            else -> message
        }
    }
}
