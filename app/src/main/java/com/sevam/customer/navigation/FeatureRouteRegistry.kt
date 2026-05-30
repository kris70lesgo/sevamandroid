package com.sevam.customer.navigation

import com.sevam.features.auth.api.AuthFeatureRoutes
import com.sevam.features.home.api.HomeFeatureRoutes
import com.sevam.features.bookings.api.BookingsFeatureRoutes
import com.sevam.features.profile.api.ProfileFeatureRoutes
import com.sevam.features.services.api.ServicesFeatureRoutes

object FeatureRouteRegistry {
    val topLevelRoutes: Set<String> = setOf(
        AuthFeatureRoutes.ROOT,
        HomeFeatureRoutes.ROOT,
        ServicesFeatureRoutes.ROOT,
        BookingsFeatureRoutes.ROOT,
        ProfileFeatureRoutes.ROOT,
    )
}
