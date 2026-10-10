package com.example.model

data class PromoBanner(
    val id: String,
    val title: String,
    val subtitle: String,
    val discountTag: String,
    val couponCode: String,
    val drawableRes: Int
)

data class Coupon(
    val code: String = "",
    val description: String = "",
    val discountPercent: Int = 0,
    val discountAmountCents: Int = 0,
    val minOrderCents: Int = 0,
    val isActive: Boolean = true
)

data class Review(
    val id: String = "",
    val userId: String = "",
    val userName: String = "",
    val productId: String = "",
    val rating: Int = 5,
    val comment: String = "",
    val dateText: String = "Today"
)
