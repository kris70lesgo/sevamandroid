package com.sevam.customer.sevam.data

import com.google.common.truth.Truth.assertThat
import com.sevam.core.network.AddressDto
import com.sevam.core.network.OrderDto
import com.sevam.core.network.ServiceCatalogResponseDto
import com.sevam.core.network.ServiceCategoryDto
import com.sevam.core.network.ServiceDto
import org.junit.Test

class CustomerMappersTest {

    @Test
    fun `catalog mapper prepends all category and flattens services`() {
        val snapshot = ServiceCatalogResponseDto(
            categories = listOf(
                ServiceCategoryDto(
                    id = "1",
                    slug = "cleaning",
                    name = "Cleaning",
                    services = listOf(
                        ServiceDto(
                            id = "svc-1",
                            slug = "deep-cleaning",
                            name = "Deep Cleaning",
                            description = "Full home clean",
                            price = 499,
                            originalPrice = 799,
                            duration = "2 hrs",
                            rating = 4.8,
                            reviews = 120,
                            image = "https://example.com/image.jpg",
                            process = listOf("Inspect", "Clean"),
                        ),
                    ),
                ),
            ),
        ).toSnapshot()

        assertThat(snapshot.categories.first().id).isEqualTo("all")
        assertThat(snapshot.categories.first().serviceCount).isEqualTo(1)
        assertThat(snapshot.services).hasSize(1)
        assertThat(snapshot.services.first().categoryId).isEqualTo("cleaning")
    }

    @Test
    fun `address mapper preserves geo metadata`() {
        val model = AddressDto(
            id = "addr-1",
            label = "HOME",
            line1 = "42 Residency Road",
            line2 = "Near Metro",
            landmark = "Opp mall",
            city = "Bangalore",
            state = "Karnataka",
            pincode = "560001",
            lat = 12.97,
            lng = 77.59,
            isDefault = true,
        ).toModel()

        assertThat(model.label).isEqualTo("Home")
        assertThat(model.latitude).isEqualTo(12.97)
        assertThat(model.longitude).isEqualTo(77.59)
        assertThat(model.isDefault).isTrue()
    }

    @Test
    fun `order mapper creates past booking`() {
        val booking = OrderDto(
            id = "job_12345678",
            type = "AC_REPAIR",
            description = "Repair visit",
            createdAt = "2026-04-01T10:00:00.000Z",
            completedAt = "2026-04-02T10:00:00.000Z",
            providerName = "Sevam Partner",
            totalPaid = "₹499",
        ).toBooking(fallbackAddress = com.sevam.core.common.model.SevamSampleData.addresses.first())

        assertThat(booking.stage.name).isEqualTo("PAST")
        assertThat(booking.totalAmount).isEqualTo(499)
        assertThat(booking.statusLabel).isEqualTo("Completed")
    }
}
