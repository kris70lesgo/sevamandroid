package com.sevam.features.payments.api

object PaymentsFeatureRoutes {
    const val CHECKOUT = "payments/checkout"
    const val RESULT = "payments/result"
    const val ARG_OUTCOME = "outcome"
    const val RESULT_PATTERN = "$RESULT/{$ARG_OUTCOME}"

    fun resultRoute(outcome: String): String = "$RESULT/$outcome"
}
