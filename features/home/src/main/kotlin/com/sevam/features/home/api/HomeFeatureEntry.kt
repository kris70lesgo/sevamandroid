package com.sevam.features.home.api

interface HomeFeatureEntry {
    val route: String
}

object HomeFeatureRoutes {
    const val ROOT = "home"
}

object DefaultHomeFeatureEntry : HomeFeatureEntry {
    override val route: String = HomeFeatureRoutes.ROOT
}
