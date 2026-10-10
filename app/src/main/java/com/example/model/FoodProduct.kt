package com.example.model

data class MealSize(
    val id: String = "regular",
    val name: String = "Regular",
    val extraPriceCents: Int = 0
)

data class OptionItem(
    val id: String = "",
    val name: String = "",
    val extraPriceCents: Int = 0
)

data class FoodProduct(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val categoryId: String = "",
    val imageUrl: String = "",
    val localDrawableRes: Int? = null,
    val priceCents: Int = 0,
    val discountPriceCents: Int? = null,
    val rating: Double = 4.8,
    val ratingCount: Int = 128,
    val preparationTimeMinutes: Int = 15,
    val isAvailable: Boolean = true,
    val isVegetarian: Boolean = false,
    val isSpicy: Boolean = false,
    val isPopular: Boolean = false,
    val isBestSeller: Boolean = false,
    val calories: Int = 550,
    val allergens: List<String> = emptyList(),
    val availableSizes: List<MealSize> = listOf(
        MealSize("regular", "Regular", 0),
        MealSize("large", "Large (+ $1.50)", 150),
        MealSize("super", "Super Feast (+ $2.99)", 299)
    ),
    val availableAddons: List<OptionItem> = listOf(
        OptionItem("cheese", "Extra Melted Cheddar", 99),
        OptionItem("bacon", "Crispy Beef Bacon", 149),
        OptionItem("jalapeno", "Pickled Jalapeños", 50),
        OptionItem("sauce", "Signature QuickBite Dip", 75)
    )
) {
    val displayPriceCents: Int
        get() = discountPriceCents ?: priceCents
}
