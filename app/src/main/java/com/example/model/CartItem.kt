package com.example.model

data class CartItem(
    val id: String = java.util.UUID.randomUUID().toString(),
    val product: FoodProduct = FoodProduct(),
    val selectedSize: MealSize = MealSize("regular", "Regular", 0),
    val selectedAddons: List<OptionItem> = emptyList(),
    val isCombo: Boolean = false,
    val specialInstructions: String = "",
    val quantity: Int = 1
) {
    fun unitPriceCents(): Int {
        var price = product.displayPriceCents + selectedSize.extraPriceCents
        if (isCombo) {
            price += 250 // Combo add-on: fries & soda ($2.50)
        }
        for (addon in selectedAddons) {
            price += addon.extraPriceCents
        }
        return price
    }

    fun totalPriceCents(): Int {
        return unitPriceCents() * quantity
    }

    fun customizationSummary(): String {
        val parts = mutableListOf<String>()
        if (selectedSize.name.isNotBlank() && selectedSize.id != "regular") {
            parts.add(selectedSize.name)
        }
        if (isCombo) {
            parts.add("Meal Combo (Fries + Soda)")
        }
        selectedAddons.forEach { parts.add(it.name) }
        if (specialInstructions.isNotBlank()) {
            parts.add("Note: $specialInstructions")
        }
        return parts.joinToString(" • ")
    }
}
