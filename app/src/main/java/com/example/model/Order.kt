package com.example.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.ServerTimestamp

enum class OrderStatus(val title: String, val subtitle: String) {
    PLACED("Order Placed", "Your order has been received"),
    CONFIRMED("Order Confirmed", "Restaurant accepted your order"),
    PREPARING("Preparing Food", "Chefs are preparing your meal with care"),
    READY_FOR_PICKUP("Ready for Delivery", "Packaged and ready for the courier"),
    OUT_FOR_DELIVERY("Out for Delivery", "Driver is on the way to your door"),
    DELIVERED("Delivered", "Enjoy your delicious meal!"),
    CANCELLED("Cancelled", "This order was cancelled")
}

@IgnoreExtraProperties
data class OrderItem(
    val productId: String = "",
    val productName: String = "",
    val priceCents: Int = 0,
    val quantity: Int = 1,
    val customizationSummary: String = "",
    val imageUrl: String = ""
)

@IgnoreExtraProperties
data class Order(
    @DocumentId val id: String = "",
    val userId: String = "",
    val customerName: String = "",
    val customerPhone: String = "",
    val deliveryAddress: String = "",
    val deliveryInstructions: String = "",
    val isDelivery: Boolean = true,
    val items: List<OrderItem> = emptyList(),
    val subtotal: Int = 0,
    val discount: Int = 0,
    val deliveryFee: Int = 0,
    val tax: Int = 0,
    val total: Int = 0,
    val couponCode: String = "",
    val paymentMethod: String = "CASH_ON_DELIVERY",
    val paymentStatus: String = "PENDING",
    val orderStatus: String = "PLACED",
    val driverName: String = "Marco Rossi",
    val driverPhone: String = "+1 (555) 329-8472",
    val driverVehicle: String = "Red Honda Scooter (Plate #QB-920)",
    val estimatedMinutesRemaining: Int = 25,
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
) {
    val statusEnum: OrderStatus
        get() = try {
            OrderStatus.valueOf(orderStatus)
        } catch (e: Exception) {
            OrderStatus.PLACED
        }
}
