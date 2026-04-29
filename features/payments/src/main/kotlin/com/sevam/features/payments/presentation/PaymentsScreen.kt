package com.sevam.features.payments.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.sevam.core.common.model.Address
import com.sevam.core.common.model.CartEntry
import com.sevam.core.common.model.CartTotals
import com.sevam.core.common.model.PaymentMethod
import com.sevam.core.common.model.WalletSummary
import com.sevam.core.ui.SevamCard
import com.sevam.core.ui.SevamColors
import com.sevam.core.ui.SevamPrimaryButton
import com.sevam.core.ui.SevamSectionHeader

@Composable
fun CheckoutScreen(
    cartEntries: List<CartEntry>,
    totals: CartTotals,
    selectedAddress: Address?,
    paymentMethods: List<PaymentMethod>,
    selectedPaymentMethodId: String,
    walletSummary: WalletSummary,
    onPaymentMethodSelected: (String) -> Unit,
    onConfirmPayment: () -> Unit,
) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item {
            SevamSectionHeader(
                title = "Review Booking",
                subtitle = "Confirm items, address, and payment method before checkout",
            )
        }
        item {
            SevamCard {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Services", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
                    cartEntries.forEach { entry ->
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("${entry.quantity} x ${entry.service.name}")
                            Text("Rs ${entry.service.price * entry.quantity}")
                        }
                    }
                }
            }
        }
        item {
            SevamCard {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Address", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
                    Text(selectedAddress?.label ?: "No address selected", fontWeight = FontWeight.Medium)
                    Text(
                        selectedAddress?.let { "${it.line1}, ${it.line2}, ${it.city}" } ?: "Select an address from cart before checkout",
                        color = SevamColors.TextSecondary,
                    )
                }
            }
        }
        item {
            SevamCard {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Payment Method", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
                    paymentMethods.forEach { method ->
                        Surface(
                            shape = RoundedCornerShape(18.dp),
                            color = if (method.id == selectedPaymentMethodId) SevamColors.OrangeContainer else SevamColors.SurfaceAlt,
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(method.title, fontWeight = FontWeight.SemiBold)
                                    Text(method.subtitle, color = SevamColors.TextSecondary, style = MaterialTheme.typography.bodySmall)
                                    if (!method.isLive) {
                                        Text("Launch after backend contract", color = SevamColors.Orange, style = MaterialTheme.typography.labelMedium)
                                    }
                                }
                                RadioButton(
                                    selected = method.id == selectedPaymentMethodId,
                                    onClick = { onPaymentMethodSelected(method.id) },
                                )
                            }
                        }
                    }
                    if (!walletSummary.isLive) {
                        Surface(shape = RoundedCornerShape(18.dp), color = SevamColors.SurfaceAlt) {
                            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text("Sevam Wallet", fontWeight = FontWeight.SemiBold)
                                Text("Balance Rs ${walletSummary.balance}", color = SevamColors.TextSecondary)
                                walletSummary.historyPreview.forEach { line ->
                                    Text(line, style = MaterialTheme.typography.bodySmall, color = SevamColors.TextSecondary)
                                }
                            }
                        }
                    }
                }
            }
        }
        item {
            SevamCard {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Subtotal")
                        Text("Rs ${totals.subtotal}")
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Service Fee")
                        Text("Rs ${totals.serviceFee}")
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Total", fontWeight = FontWeight.Bold)
                        Text("Rs ${totals.total}", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
        item {
            SevamPrimaryButton(text = "Pay with Razorpay", modifier = Modifier.fillMaxWidth(), onClick = onConfirmPayment)
        }
    }
}

@Composable
fun PaymentResultScreen(
    outcome: String,
    onGoToBookings: () -> Unit,
) {
    val (title, body) = when (outcome) {
        "success" -> "Booking Confirmed" to "Your payment has been marked successful and the booking is now live in Bookings."
        "failed" -> "Payment Failed" to "The transaction did not complete. You can retry checkout without losing the cart."
        else -> "Payment Dismissed" to "No worries. Your cart is still waiting for you when you want to finish the booking."
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        Text(title, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Text(body, style = MaterialTheme.typography.bodyLarge, color = SevamColors.TextSecondary)
        SevamPrimaryButton(text = "Go to Bookings", onClick = onGoToBookings)
    }
}
