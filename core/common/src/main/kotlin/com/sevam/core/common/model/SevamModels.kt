package com.sevam.core.common.model

data class PromoBanner(
    val id: String,
    val locationLabel: String,
    val title: String,
    val subtitle: String,
    val primaryAction: String,
    val secondaryAction: String,
    val highlights: List<String>,
)

data class ServiceCategory(
    val id: String,
    val title: String,
    val serviceCount: Int,
)

data class ServiceItem(
    val id: String,
    val categoryId: String,
    val name: String,
    val description: String,
    val durationLabel: String,
    val price: Int,
    val originalPrice: Int? = null,
    val rating: Double,
    val reviewCount: Int,
    val imageUrl: String,
    val badge: String? = null,
    val processSteps: List<String> = emptyList(),
)

data class Address(
    val id: String,
    val label: String,
    val line1: String,
    val line2: String,
    val city: String,
    val isDefault: Boolean = false,
)

data class WorkerProfile(
    val id: String,
    val name: String,
    val role: String,
    val rating: Double,
    val completedJobs: Int,
    val isVerified: Boolean,
    val phoneNumber: String,
    val avatarUrl: String,
)

enum class BookingStage {
    ACTIVE,
    UPCOMING,
    PAST,
}

enum class BookingStepState {
    COMPLETE,
    CURRENT,
    UPCOMING,
}

data class BookingStep(
    val label: String,
    val state: BookingStepState,
)

data class Booking(
    val id: String,
    val stage: BookingStage,
    val service: ServiceItem,
    val dateLabel: String,
    val timeLabel: String,
    val address: Address,
    val paymentMethod: String,
    val totalAmount: Int,
    val bookingReference: String,
    val statusLabel: String,
    val etaLabel: String? = null,
    val supportHint: String? = null,
    val worker: WorkerProfile? = null,
    val steps: List<BookingStep> = emptyList(),
)

data class CartEntry(
    val service: ServiceItem,
    val quantity: Int,
)

data class CartTotals(
    val subtotal: Int,
    val serviceFee: Int,
    val total: Int,
)

data class PaymentMethod(
    val id: String,
    val title: String,
    val subtitle: String,
    val isDefault: Boolean = false,
    val isLive: Boolean = true,
)

data class WalletSummary(
    val balance: Int,
    val isLive: Boolean,
    val historyPreview: List<String>,
)

data class UserProfile(
    val name: String,
    val email: String,
    val phoneNumber: String,
    val dateOfBirth: String,
    val gender: String,
    val memberSince: String,
    val referralCode: String,
)

data class NotificationItem(
    val id: String,
    val title: String,
    val body: String,
    val timeLabel: String,
    val isUnread: Boolean,
    val bookingId: String? = null,
)

data class SearchSuggestion(
    val id: String,
    val label: String,
)

fun calculateCartTotals(entries: List<CartEntry>, serviceFee: Int = 50): CartTotals {
    val subtotal = entries.sumOf { entry -> entry.service.price * entry.quantity }
    val appliedServiceFee = if (entries.isEmpty()) 0 else serviceFee
    return CartTotals(
        subtotal = subtotal,
        serviceFee = appliedServiceFee,
        total = subtotal + appliedServiceFee,
    )
}

fun List<Booking>.activeBookings(): List<Booking> = filter { it.stage == BookingStage.ACTIVE }

fun List<Booking>.upcomingBookings(): List<Booking> = filter { it.stage == BookingStage.UPCOMING }

fun List<Booking>.pastBookings(): List<Booking> = filter { it.stage == BookingStage.PAST }
