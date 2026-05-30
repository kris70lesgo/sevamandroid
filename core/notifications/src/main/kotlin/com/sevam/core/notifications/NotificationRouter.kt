package com.sevam.core.notifications

data class NotificationPayload(
    val type: String,
    val bookingId: String? = null,
    val title: String? = null,
    val body: String? = null,
)

object NotificationRoutes {
    const val BOOKINGS = "bookings"
    const val TRACKING = "tracking"
    const val OFFERS = "offers"
}

object NotificationRouter {
    fun routeFor(payload: NotificationPayload): String {
        return when {
            payload.type.equals("worker_tracking", ignoreCase = true) && payload.bookingId != null ->
                "${NotificationRoutes.TRACKING}/${payload.bookingId}"
            payload.type.equals("booking_update", ignoreCase = true) ->
                NotificationRoutes.BOOKINGS
            else -> NotificationRoutes.OFFERS
        }
    }
}
