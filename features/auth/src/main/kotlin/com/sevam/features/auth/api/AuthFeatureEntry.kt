package com.sevam.features.auth.api

interface AuthFeatureEntry {
    val route: String
}

object AuthFeatureRoutes {
    const val ROOT = "auth"
    const val LOGIN = "auth/login"
    const val VERIFY_OTP = "auth/verify-otp"
}

object DefaultAuthFeatureEntry : AuthFeatureEntry {
    override val route: String = AuthFeatureRoutes.ROOT
}
