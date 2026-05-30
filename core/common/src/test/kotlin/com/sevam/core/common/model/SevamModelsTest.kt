package com.sevam.core.common.model

import org.junit.Assert.assertEquals
import org.junit.Test

class SevamModelsTest {

    @Test
    fun calculateCartTotals_appliesServiceFeeOnlyWhenCartHasItems() {
        val entries = listOf(
            CartEntry(service = SevamSampleData.services[0], quantity = 1),
            CartEntry(service = SevamSampleData.services[1], quantity = 2),
        )

        val totals = calculateCartTotals(entries, serviceFee = 50)

        assertEquals(1297, totals.subtotal)
        assertEquals(50, totals.serviceFee)
        assertEquals(1347, totals.total)
    }

    @Test
    fun bookingFilters_splitSampleDataByStage() {
        val bookings = SevamSampleData.bookings

        assertEquals(1, bookings.activeBookings().size)
        assertEquals(2, bookings.upcomingBookings().size)
        assertEquals(2, bookings.pastBookings().size)
    }
}
