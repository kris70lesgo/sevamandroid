package com.sevam.core.payments

data class CheckoutRequest(
    val orderId: String,
    val amountPaise: Int,
    val customerName: String,
    val customerPhone: String,
    val customerEmail: String? = null,
    val description: String,
)

sealed interface CheckoutResult {
    data class Success(val paymentId: String, val signature: String? = null) : CheckoutResult
    data class Failed(val reason: String) : CheckoutResult
    data object Dismissed : CheckoutResult
}

interface RazorpayGateway {
    suspend fun startCheckout(request: CheckoutRequest): CheckoutResult
}

class StubRazorpayGateway : RazorpayGateway {
    override suspend fun startCheckout(request: CheckoutRequest): CheckoutResult {
        return if (request.amountPaise > 0) {
            CheckoutResult.Success(paymentId = "pay_demo_success")
        } else {
            CheckoutResult.Failed(reason = "Invalid amount")
        }
    }
}
