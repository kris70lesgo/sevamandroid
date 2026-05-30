package com.sevam.customer.sevam.data

import com.sevam.core.common.model.Address
import com.sevam.core.common.model.Booking
import com.sevam.core.common.model.CartEntry
import com.sevam.core.common.model.NotificationItem
import com.sevam.core.common.model.PaymentMethod
import com.sevam.core.common.model.SevamSampleData
import com.sevam.core.common.model.UserProfile
import com.sevam.core.common.model.calculateCartTotals
import com.sevam.core.network.AddressUpsertRequestDto
import com.sevam.core.network.CreateOrderRequestDto
import com.sevam.core.network.CustomerProfileUpdateRequestDto
import com.sevam.core.network.SevamApiConfig
import com.sevam.core.network.SevamApiService
import com.sevam.core.network.SyncProfileRequestDto
import com.sevam.core.network.VerifyPaymentRequestDto
import com.sevam.core.network.executeApiCall
import com.sevam.core.payments.CheckoutRequest
import com.sevam.core.payments.CheckoutResult
import com.sevam.core.payments.RazorpayGateway
import com.sevam.core.security.SessionStore
import com.sevam.core.security.StoredSession
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.auth.OtpType
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.OTP
import io.github.jan.supabase.createSupabaseClient
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DefaultAuthRepository @Inject constructor(
    private val sessionStore: SessionStore,
    private val apiConfig: SevamApiConfig,
    private val apiService: SevamApiService,
    private val json: Json,
    private val supabaseConfig: SupabaseAuthConfig,
) : AuthRepository {
    override val isConfigured: Boolean
        get() = supabaseConfig.isConfigured

    private val supabaseClient by lazy {
        createSupabaseClient(
            supabaseUrl = supabaseConfig.projectUrl,
            supabaseKey = supabaseConfig.anonKey,
        ) {
            install(Auth)
        }
    }

    override suspend fun restoreSession(): StoredSession? = sessionStore.restore()

    override suspend fun requestOtp(phoneNumber: String): Result<Unit> {
        if (!isConfigured) return Result.failure(IllegalStateException("Supabase auth is not configured"))
        return runCatching {
            supabaseClient.auth.signInWith(OTP) {
                phone = phoneNumber
            }
        }
    }

    override suspend fun verifyOtp(phoneNumber: String, otp: String): Result<AuthSessionPayload> {
        if (!isConfigured) return Result.failure(IllegalStateException("Supabase auth is not configured"))
        return runCatching {
            supabaseClient.auth.verifyPhoneOtp(
                type = OtpType.Phone.SMS,
                phone = phoneNumber,
                token = otp,
            )
            val accessToken = supabaseClient.auth.currentAccessTokenOrNull()
                ?: throw IllegalStateException("Supabase returned no access token")
            val session = AuthSessionPayload(
                phoneNumber = phoneNumber,
                accessToken = accessToken,
            )
            sessionStore.save(
                StoredSession(
                    phoneNumber = phoneNumber,
                    accessToken = accessToken,
                ),
            )
            if (apiConfig.isConfigured) {
                executeApiCall(json) {
                    apiService.syncProfile(
                        authorization = "Bearer $accessToken",
                        body = SyncProfileRequestDto(phone = phoneNumber),
                    )
                }.getOrThrow()
            }
            session
        }
    }

    override suspend fun logout() {
        runCatching { supabaseClient.auth.signOut() }
        sessionStore.clear()
    }
}

@Singleton
class DefaultCatalogRepository @Inject constructor(
    private val apiConfig: SevamApiConfig,
    private val apiService: SevamApiService,
    private val json: Json,
) : CatalogRepository {
    override suspend fun fetchCatalog(): Result<CatalogSnapshot> {
        if (!apiConfig.isConfigured) return Result.failure(IllegalStateException("Backend base URL missing"))
        return executeApiCall(json) { apiService.fetchCatalog() }.map { it.toSnapshot() }
    }
}

@Singleton
class DefaultProfileRepository @Inject constructor(
    private val apiConfig: SevamApiConfig,
    private val apiService: SevamApiService,
    private val json: Json,
) : ProfileRepository {
    override suspend fun fetchProfile(): Result<UserProfile> {
        if (!apiConfig.isConfigured) return Result.failure(IllegalStateException("Backend base URL missing"))
        return executeApiCall(json) { apiService.fetchProfile() }.map { it.toModel() }
    }
}

@Singleton
class DefaultAddressRepository @Inject constructor(
    private val apiConfig: SevamApiConfig,
    private val apiService: SevamApiService,
    private val json: Json,
) : AddressRepository {
    override suspend fun fetchAddresses(): Result<List<Address>> {
        if (!apiConfig.isConfigured) return Result.failure(IllegalStateException("Backend base URL missing"))
        return executeApiCall(json) { apiService.fetchAddresses() }.map { response ->
            response.addresses.map { it.toModel() }
        }
    }

    override suspend fun createAddress(address: Address): Result<Address> {
        if (!apiConfig.isConfigured) return Result.failure(IllegalStateException("Backend base URL missing"))
        return executeApiCall(json) {
            apiService.createAddress(
                AddressUpsertRequestDto(
                    label = address.label.uppercase(),
                    line1 = address.line1,
                    line2 = address.line2,
                    landmark = address.landmark,
                    city = address.city.substringBefore(","),
                    state = address.state,
                    pincode = address.pincode ?: "560001",
                    lat = address.latitude,
                    lng = address.longitude,
                    isDefault = address.isDefault,
                ),
            )
        }.map { it.address.toModel() }
    }

    override suspend fun updateAddress(address: Address): Result<Address> {
        if (!apiConfig.isConfigured) return Result.failure(IllegalStateException("Backend base URL missing"))
        return executeApiCall(json) {
            apiService.updateAddress(
                AddressUpsertRequestDto(
                    id = address.id,
                    label = address.label.uppercase(),
                    line1 = address.line1,
                    line2 = address.line2,
                    landmark = address.landmark,
                    city = address.city.substringBefore(","),
                    state = address.state,
                    pincode = address.pincode ?: "560001",
                    lat = address.latitude,
                    lng = address.longitude,
                    isDefault = address.isDefault,
                ),
            )
        }.map { it.address.toModel() }
    }

    override suspend fun deleteAddress(addressId: String): Result<Unit> {
        if (!apiConfig.isConfigured) return Result.failure(IllegalStateException("Backend base URL missing"))
        return executeApiCall(json) { apiService.deleteAddress(addressId) }.map { Unit }
    }
}

@Singleton
class DefaultBookingsRepository @Inject constructor(
    private val apiConfig: SevamApiConfig,
    private val apiService: SevamApiService,
    private val json: Json,
) : BookingsRepository {
    override suspend fun fetchBookings(): Result<List<Booking>> {
        if (!apiConfig.isConfigured) return Result.failure(IllegalStateException("Backend base URL missing"))
        return executeApiCall(json) { apiService.fetchOrders() }.map { response ->
            val fallbackAddress = SevamSampleData.addresses.first()
            val completed = response.orders.map { it.toBooking(fallbackAddress) }
            SevamSampleData.bookings.filterNot { it.stage == com.sevam.core.common.model.BookingStage.PAST } + completed
        }
    }
}

@Singleton
class DefaultPaymentsRepository @Inject constructor(
    private val apiConfig: SevamApiConfig,
    private val apiService: SevamApiService,
    private val json: Json,
    private val razorpayGateway: RazorpayGateway,
) : PaymentsRepository {
    override val isConfigured: Boolean
        get() = apiConfig.isConfigured

    override suspend fun confirmPayment(
        entries: List<CartEntry>,
        address: Address,
        method: PaymentMethod,
        profile: UserProfile,
    ): Result<String> {
        if (!apiConfig.isConfigured) return Result.failure(IllegalStateException("Backend base URL missing"))
        val totals = calculateCartTotals(entries)
        return runCatching {
            val order = executeApiCall(json) {
                apiService.createCheckoutOrder(
                    CreateOrderRequestDto(
                        amount = totals.total.toDouble(),
                        addressLine = "${address.line1}, ${address.line2}",
                        label = address.label,
                        itemCount = entries.sumOf { it.quantity },
                    ),
                )
            }.getOrThrow()

            val checkout = razorpayGateway.startCheckout(
                CheckoutRequest(
                    orderId = order.order.id,
                    amountPaise = order.order.amount,
                    customerName = profile.name,
                    customerPhone = profile.phoneNumber,
                    customerEmail = profile.email,
                    description = "${entries.size} Sevam services",
                ),
            )
            when (checkout) {
                CheckoutResult.Dismissed -> throw IllegalStateException("Checkout dismissed")
                is CheckoutResult.Failed -> throw IllegalStateException(checkout.reason)
                is CheckoutResult.Success -> {
                    val signature = checkout.signature ?: "mobile_stub_signature"
                    executeApiCall(json) {
                        apiService.verifyPayment(
                            VerifyPaymentRequestDto(
                                orderId = order.order.id,
                                paymentId = checkout.paymentId,
                                signature = signature,
                            ),
                        )
                    }.getOrThrow()
                    order.order.id
                }
            }
        }
    }
}

@Singleton
class DefaultNotificationsRepository @Inject constructor() : NotificationsRepository {
    override suspend fun fetchNotifications(): Result<List<NotificationItem>> {
        return Result.success(SevamSampleData.notifications)
    }
}

data class SupabaseAuthConfig(
    val projectUrl: String,
    val anonKey: String,
) {
    val isConfigured: Boolean
        get() = projectUrl.isNotBlank() && anonKey.isNotBlank()
}
