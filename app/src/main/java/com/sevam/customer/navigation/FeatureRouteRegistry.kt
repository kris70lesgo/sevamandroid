package com.sevam.customer.navigation

import com.sevam.features.auth.api.AuthFeatureRoutes
import com.sevam.features.home.api.HomeFeatureRoutes

object FeatureRouteRegistry {
    val topLevelRoutes: Set<String> = setOf(
        AuthFeatureRoutes.ROOT,
        HomeFeatureRoutes.ROOT,
    )
}
