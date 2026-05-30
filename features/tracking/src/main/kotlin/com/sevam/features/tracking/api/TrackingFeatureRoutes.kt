package com.sevam.features.tracking.api

object TrackingFeatureRoutes {
    const val ROOT = "tracking"
    const val ARG_BOOKING_ID = "bookingId"
    const val ROUTE_PATTERN = "$ROOT/{$ARG_BOOKING_ID}"

    fun createRoute(bookingId: String): String = "$ROOT/$bookingId"
}
