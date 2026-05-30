package com.sevam.customer.sevam.data

import com.sevam.core.common.model.Address
import com.sevam.core.common.model.Booking
import com.sevam.core.common.model.BookingStage
import com.sevam.core.common.model.BookingStep
import com.sevam.core.common.model.BookingStepState
import com.sevam.core.common.model.ServiceCategory
import com.sevam.core.common.model.ServiceItem
import com.sevam.core.common.model.SevamSampleData
import com.sevam.core.common.model.UserProfile
import com.sevam.core.network.AddressDto
import com.sevam.core.network.CustomerProfileResponseDto
import com.sevam.core.network.OrderDto
import com.sevam.core.network.ServiceCatalogResponseDto
import java.util.Locale

fun ServiceCatalogResponseDto.toSnapshot(): CatalogSnapshot {
    val services = categories.flatMap { category ->
        category.services.map { service ->
            ServiceItem(
                id = service.id,
                categoryId = category.slug,
                name = service.name,
                description = service.description,
                durationLabel = service.duration,
                price = service.price,
                originalPrice = service.originalPrice,
                rating = service.rating,
                reviewCount = service.reviews,
                imageUrl = service.image,
                badge = service.originalPrice?.takeIf { it > service.price }?.let {
                    val discountPercent = (((it - service.price).toDouble() / it.toDouble()) * 100).toInt()
                    "$discountPercent% OFF"
                },
                processSteps = service.process,
            )
        }
    }

    val categories = buildList {
        add(ServiceCategory(id = "all", title = "All", serviceCount = services.size))
        addAll(
            this@toSnapshot.categories.map { category ->
                ServiceCategory(
                    id = category.slug,
                    title = category.name,
                    serviceCount = category.services.size,
                )
            },
        )
    }

    return CatalogSnapshot(
        categories = categories,
        services = services,
    )
}

fun AddressDto.toModel(): Address {
    val cityLine = buildString {
        append(city)
        state?.takeIf { it.isNotBlank() }?.let { append(", ").append(it) }
        pincode?.takeIf { it.isNotBlank() }?.let { append(" ").append(it) }
    }
    return Address(
        id = id,
        label = label.lowercase().replaceFirstChar(Char::uppercase),
        line1 = line1,
        line2 = line2 ?: landmark ?: "",
        city = cityLine.ifBlank { city },
        state = state,
        pincode = pincode,
        landmark = landmark,
        latitude = lat,
        longitude = lng,
        isDefault = isDefault,
    )
}

fun Address.toUpdatableCopy(
    line2: String = this.line2,
    isDefault: Boolean = this.isDefault,
): Address {
    return copy(line2 = line2, isDefault = isDefault)
}

fun CustomerProfileResponseDto.toModel(
    fallback: UserProfile = SevamSampleData.userProfile,
): UserProfile {
    return UserProfile(
        name = user.name ?: fallback.name,
        email = profile.email ?: fallback.email,
        phoneNumber = user.phone ?: fallback.phoneNumber,
        dateOfBirth = profile.dateOfBirth?.substringBefore("T") ?: fallback.dateOfBirth,
        gender = profile.gender ?: fallback.gender,
        memberSince = fallback.memberSince,
        referralCode = fallback.referralCode,
    )
}

fun OrderDto.toBooking(
    fallbackAddress: Address,
): Booking {
    val normalizedType = type
        .replace('_', ' ')
        .lowercase()
        .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
    val matchedService = SevamSampleData.services.firstOrNull { service ->
        service.name.contains(normalizedType, ignoreCase = true) ||
            normalizedType.contains(service.name, ignoreCase = true)
    }
    val price = totalPaid.filter { it.isDigit() }.toIntOrNull() ?: matchedService?.price ?: 0
    val service = matchedService ?: ServiceItem(
        id = "order-$id",
        categoryId = "past",
        name = normalizedType,
        description = description ?: normalizedType,
        durationLabel = "Completed",
        price = price,
        originalPrice = null,
        rating = 4.7,
        reviewCount = 0,
        imageUrl = SevamSampleData.services.first().imageUrl,
    )
    val completedLabel = completedAt?.substringBefore("T") ?: createdAt?.substringBefore("T") ?: "Recently"
    return Booking(
        id = id,
        stage = BookingStage.PAST,
        service = service,
        dateLabel = "Completed on $completedLabel",
        timeLabel = providerName ?: "Sevam Partner",
        address = fallbackAddress,
        paymentMethod = "Online",
        totalAmount = price,
        bookingReference = id.takeLast(8).uppercase(),
        statusLabel = "Completed",
        worker = null,
        steps = listOf(
            BookingStep("Confirmed", BookingStepState.COMPLETE),
            BookingStep("En Route", BookingStepState.COMPLETE),
            BookingStep("Arrived", BookingStepState.COMPLETE),
            BookingStep("In Progress", BookingStepState.COMPLETE),
            BookingStep("Done", BookingStepState.COMPLETE),
        ),
    )
}
