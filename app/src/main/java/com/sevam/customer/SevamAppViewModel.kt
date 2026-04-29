package com.sevam.customer

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.sevam.core.common.model.Address
import com.sevam.core.common.model.Booking
import com.sevam.core.common.model.BookingStage
import com.sevam.core.common.model.CartEntry
import com.sevam.core.common.model.NotificationItem
import com.sevam.core.common.model.PaymentMethod
import com.sevam.core.common.model.ServiceItem
import com.sevam.core.common.model.SevamSampleData
import com.sevam.core.common.model.UserProfile
import com.sevam.core.common.model.WalletSummary
import com.sevam.core.common.model.activeBookings
import com.sevam.core.common.model.calculateCartTotals
import com.sevam.core.common.model.pastBookings
import com.sevam.core.common.model.upcomingBookings
import com.sevam.core.security.SessionStore
import com.sevam.core.security.StoredSession
import com.sevam.features.profile.presentation.ProfileSection
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import java.util.UUID

data class SevamAppUiState(
    val isLoggedIn: Boolean = false,
    val phoneNumber: String = "",
    val otp: String = "",
    val selectedCategoryId: String = "all",
    val searchQuery: String = "",
    val cartEntries: List<CartEntry> = emptyList(),
    val addresses: List<Address> = SevamSampleData.addresses,
    val selectedAddressId: String = SevamSampleData.addresses.first().id,
    val bookings: List<Booking> = SevamSampleData.bookings,
    val notifications: List<NotificationItem> = SevamSampleData.notifications,
    val profile: UserProfile = SevamSampleData.userProfile,
    val paymentMethods: List<PaymentMethod> = SevamSampleData.paymentMethods,
    val walletSummary: WalletSummary = SevamSampleData.walletSummary,
    val selectedPaymentMethodId: String = SevamSampleData.paymentMethods.first().id,
    val selectedBookingStage: BookingStage = BookingStage.ACTIVE,
    val selectedProfileSection: ProfileSection = ProfileSection.PERSONAL,
    val selectedServiceId: String? = null,
    val showAddressPicker: Boolean = false,
)

class SevamAppViewModel(application: Application) : AndroidViewModel(application) {
    private val sessionStore = SessionStore.from(application)

    private val _uiState = MutableStateFlow(
        SevamAppUiState(
            cartEntries = listOf(CartEntry(SevamSampleData.services.first(), 1)),
        ),
    )
    val uiState: StateFlow<SevamAppUiState> = _uiState

    init {
        sessionStore.restore()?.let { session ->
            _uiState.update {
                it.copy(
                    isLoggedIn = true,
                    phoneNumber = session.phoneNumber,
                )
            }
        }
    }

    val filteredServices: List<ServiceItem>
        get() {
            val state = _uiState.value
            return SevamSampleData.services.filter { service ->
                val matchesCategory = state.selectedCategoryId == "all" || service.categoryId == state.selectedCategoryId
                val query = state.searchQuery.trim()
                val matchesQuery = query.isBlank() ||
                    service.name.contains(query, ignoreCase = true) ||
                    service.description.contains(query, ignoreCase = true)
                matchesCategory && matchesQuery
            }
        }

    val selectedService: ServiceItem?
        get() = SevamSampleData.services.firstOrNull { it.id == _uiState.value.selectedServiceId }

    val selectedAddress: Address?
        get() = _uiState.value.addresses.firstOrNull { it.id == _uiState.value.selectedAddressId }

    fun updatePhoneNumber(value: String) {
        _uiState.update { it.copy(phoneNumber = value) }
    }

    fun updateOtp(value: String) {
        _uiState.update { it.copy(otp = value.take(4)) }
    }

    fun requestOtp() {
        if (_uiState.value.phoneNumber.isBlank()) {
            _uiState.update { it.copy(phoneNumber = "+91 98765 43210") }
        }
    }

    fun completeLogin() {
        val phone = _uiState.value.phoneNumber.ifBlank { "+91 98765 43210" }
        sessionStore.save(StoredSession(phoneNumber = phone, accessToken = UUID.randomUUID().toString()))
        _uiState.update { it.copy(isLoggedIn = true) }
    }

    fun logout() {
        sessionStore.clear()
        _uiState.update { current ->
            current.copy(
                isLoggedIn = false,
                otp = "",
                selectedServiceId = null,
            )
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
                    service = SevamSampleData.services.first { it.id == serviceId },
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
        _uiState.update { state ->
            val newAddress = Address(
                id = "addr-${state.addresses.size + 1}",
                label = "New Place",
                line1 = "Current location pin",
                line2 = "Mapbox reverse geocode pending",
                city = "Bangalore",
            )
            state.copy(
                addresses = state.addresses + newAddress,
                selectedAddressId = newAddress.id,
                showAddressPicker = false,
            )
        }
    }

    fun setDefaultAddress(addressId: String) {
        _uiState.update { state ->
            state.copy(
                addresses = state.addresses.map { address ->
                    address.copy(isDefault = address.id == addressId)
                },
                selectedAddressId = addressId,
            )
        }
    }

    fun editAddress(addressId: String) {
        _uiState.update { state ->
            state.copy(
                addresses = state.addresses.map { address ->
                    if (address.id == addressId) address.copy(line2 = "${address.line2} | updated") else address
                },
            )
        }
    }

    fun deleteAddress(addressId: String) {
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

    fun confirmPayment(): String {
        val selectedMethod = _uiState.value.paymentMethods.firstOrNull { it.id == _uiState.value.selectedPaymentMethodId }
        if (selectedMethod == null) return "dismissed"
        if (!selectedMethod.isLive) return "dismissed"
        if (_uiState.value.cartEntries.isEmpty()) return "failed"

        val firstService = _uiState.value.cartEntries.first().service
        val newBooking = Booking(
            id = "booking-${UUID.randomUUID()}",
            stage = BookingStage.ACTIVE,
            service = firstService,
            dateLabel = "Today",
            timeLabel = "Next available slot",
            address = selectedAddress ?: SevamSampleData.addresses.first(),
            paymentMethod = selectedMethod.title,
            totalAmount = cartTotals().total,
            bookingReference = "SVM-${(20000..29999).random()}",
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
        _uiState.update { state ->
            state.copy(
                bookings = listOf(newBooking) + state.bookings,
                cartEntries = emptyList(),
                selectedBookingStage = BookingStage.ACTIVE,
            )
        }
        return "success"
    }
}
