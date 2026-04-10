package com.sevam.features.home.api

interface HomeFeatureEntry {
    val route: String
}

object HomeFeatureRoutes {
    const val ROOT = "home"
    const val BOOKING_FORM = "home/booking"
}

object DefaultHomeFeatureEntry : HomeFeatureEntry {
    override val route: String = HomeFeatureRoutes.ROOT
}
