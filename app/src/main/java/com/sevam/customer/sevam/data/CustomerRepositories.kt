package com.sevam.customer.sevam.data

import com.sevam.core.common.model.Address
import com.sevam.core.common.model.Booking
import com.sevam.core.common.model.NotificationItem
import com.sevam.core.common.model.ServiceCategory
import com.sevam.core.common.model.ServiceItem
import com.sevam.core.common.model.UserProfile
import com.sevam.core.common.model.CartEntry
import com.sevam.core.common.model.PaymentMethod
import com.sevam.core.security.StoredSession

data class CatalogSnapshot(
    val categories: List<ServiceCategory>,
    val services: List<ServiceItem>,
)

data class AuthSessionPayload(
    val phoneNumber: String,
    val accessToken: String,
)

interface AuthRepository {
    val isConfigured: Boolean

    suspend fun restoreSession(): StoredSession?
    suspend fun requestOtp(phoneNumber: String): Result<Unit>
    suspend fun verifyOtp(phoneNumber: String, otp: String): Result<AuthSessionPayload>
    suspend fun logout()
}

interface CatalogRepository {
    suspend fun fetchCatalog(): Result<CatalogSnapshot>
}

interface ProfileRepository {
    suspend fun fetchProfile(): Result<UserProfile>
}

interface AddressRepository {
    suspend fun fetchAddresses(): Result<List<Address>>
    suspend fun createAddress(address: Address): Result<Address>
    suspend fun updateAddress(address: Address): Result<Address>
    suspend fun deleteAddress(addressId: String): Result<Unit>
}

interface BookingsRepository {
    suspend fun fetchBookings(): Result<List<Booking>>
}

interface PaymentsRepository {
    val isConfigured: Boolean

    suspend fun confirmPayment(
        entries: List<CartEntry>,
        address: Address,
        method: PaymentMethod,
        profile: UserProfile,
    ): Result<String>
}

interface NotificationsRepository {
    suspend fun fetchNotifications(): Result<List<NotificationItem>>
}
