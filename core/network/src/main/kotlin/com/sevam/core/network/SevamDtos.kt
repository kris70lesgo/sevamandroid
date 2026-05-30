package com.sevam.core.network

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ApiErrorDto(
    val error: String? = null,
)

@Serializable
data class ServiceCatalogResponseDto(
    val categories: List<ServiceCategoryDto> = emptyList(),
)

@Serializable
data class ServiceCategoryDto(
    val id: String,
    val slug: String,
    val name: String,
    @SerialName("iconKey")
    val iconKey: String? = null,
    val color: String? = null,
    val bg: String? = null,
    @SerialName("sortOrder")
    val sortOrder: Int? = null,
    val services: List<ServiceDto> = emptyList(),
)

@Serializable
data class ServiceDto(
    val id: String,
    val slug: String,
    val name: String,
    val description: String,
    val price: Int,
    @SerialName("originalPrice")
    val originalPrice: Int? = null,
    val duration: String,
    val rating: Double,
    val reviews: Int,
    val image: String,
    val process: List<String> = emptyList(),
    @SerialName("deliveryTime")
    val deliveryTime: String? = null,
    @SerialName("jobType")
    val jobType: String? = null,
)

@Serializable
data class CustomerProfileResponseDto(
    val user: CustomerUserDto,
    val profile: CustomerProfileDto,
)

@Serializable
data class CustomerUserDto(
    val id: String,
    val name: String? = null,
    val phone: String? = null,
    @SerialName("userType")
    val userType: String? = null,
)

@Serializable
data class CustomerProfileDto(
    val email: String? = null,
    @SerialName("dateOfBirth")
    val dateOfBirth: String? = null,
    val gender: String? = null,
    @SerialName("preferredLanguage")
    val preferredLanguage: String? = null,
    @SerialName("marketingOptIn")
    val marketingOptIn: Boolean? = null,
)

@Serializable
data class CustomerProfileUpdateRequestDto(
    val name: String? = null,
    val email: String? = null,
    @SerialName("dateOfBirth")
    val dateOfBirth: String? = null,
    val gender: String? = null,
    @SerialName("preferredLanguage")
    val preferredLanguage: String? = null,
    @SerialName("marketingOptIn")
    val marketingOptIn: Boolean? = null,
)

@Serializable
data class AddressesResponseDto(
    val addresses: List<AddressDto> = emptyList(),
)

@Serializable
data class AddressMutationResponseDto(
    val address: AddressDto,
)

@Serializable
data class AddressDto(
    val id: String,
    val label: String,
    val line1: String,
    val line2: String? = null,
    val landmark: String? = null,
    val city: String,
    val state: String? = null,
    val pincode: String? = null,
    val lat: Double? = null,
    val lng: Double? = null,
    @SerialName("isDefault")
    val isDefault: Boolean = false,
)

@Serializable
data class AddressUpsertRequestDto(
    val id: String? = null,
    val label: String? = null,
    val line1: String? = null,
    val line2: String? = null,
    val landmark: String? = null,
    val city: String? = null,
    val state: String? = null,
    val pincode: String? = null,
    val lat: Double? = null,
    val lng: Double? = null,
    @SerialName("isDefault")
    val isDefault: Boolean? = null,
)

@Serializable
data class OrdersResponseDto(
    val orders: List<OrderDto> = emptyList(),
)

@Serializable
data class OrderDto(
    val id: String,
    val type: String,
    val description: String? = null,
    @SerialName("createdAt")
    val createdAt: String? = null,
    @SerialName("completedAt")
    val completedAt: String? = null,
    @SerialName("providerName")
    val providerName: String? = null,
    @SerialName("totalPaid")
    val totalPaid: String,
)

@Serializable
data class LocationSuggestionDto(
    val name: String,
    val lat: Double,
    val lng: Double,
)

@Serializable
data class ReverseLocationDto(
    val name: String,
    val lat: Double,
    val lng: Double,
)

@Serializable
data class SyncProfileRequestDto(
    val name: String? = null,
    val email: String? = null,
    val phone: String? = null,
)

@Serializable
data class SyncProfileResponseDto(
    val user: CustomerUserDto? = null,
    val profile: SyncProfilePayloadDto? = null,
    val synced: Boolean = false,
    val reason: String? = null,
)

@Serializable
data class SyncProfilePayloadDto(
    val name: String? = null,
    val email: String? = null,
    val phone: String? = null,
)

@Serializable
data class CreateOrderRequestDto(
    val amount: Double,
    val currency: String = "INR",
    @SerialName("addressLine")
    val addressLine: String? = null,
    val label: String? = null,
    @SerialName("itemCount")
    val itemCount: Int? = null,
)

@Serializable
data class CreateOrderResponseDto(
    val order: RazorpayOrderDto,
    @SerialName("keyId")
    val keyId: String,
)

@Serializable
data class RazorpayOrderDto(
    val id: String,
    val amount: Int,
    val currency: String,
)

@Serializable
data class VerifyPaymentRequestDto(
    @SerialName("razorpay_order_id")
    val orderId: String,
    @SerialName("razorpay_payment_id")
    val paymentId: String,
    @SerialName("razorpay_signature")
    val signature: String,
)

@Serializable
data class VerifyPaymentResponseDto(
    val ok: Boolean,
    val payment: VerifiedPaymentDto? = null,
)

@Serializable
data class VerifiedPaymentDto(
    @SerialName("orderId")
    val orderId: String,
    @SerialName("paymentId")
    val paymentId: String,
)
