package com.example.data

import com.example.R
import com.example.model.FoodCategory
import com.example.model.FoodProduct
import com.example.model.MealSize
import com.example.model.OptionItem
import com.example.model.PromoBanner
import com.example.model.Coupon

object FoodCatalogData {

    val categories = listOf(
        FoodCategory("all", "All Menu", "restaurant", 16),
        FoodCategory("burgers", "Burgers", "lunch_dining", 4),
        FoodCategory("chicken", "Fried Chicken", "egg", 3),
        FoodCategory("pizza", "Pizza", "local_pizza", 3),
        FoodCategory("tacos", "Tacos & Wraps", "bakery_dining", 2),
        FoodCategory("sides", "Crispy Fries", "fastfood", 2),
        FoodCategory("drinks", "Drinks & Shakes", "local_bar", 2),
        FoodCategory("desserts", "Desserts", "cake", 2)
    )

    val promoBanners = listOf(
        PromoBanner(
            id = "b1",
            title = "Double Smash Burger Fest",
            subtitle = "Juicy Angus beef, melted cheddar & seasoned fries",
            discountTag = "30% OFF",
            couponCode = "QUICK30",
            drawableRes = R.drawable.banner_burger_promo
        ),
        PromoBanner(
            id = "b2",
            title = "Crispy Southern Chicken Bucket",
            subtitle = "Hand-breaded golden drumsticks with honey dip",
            discountTag = "FREE DRINK",
            couponCode = "CHICKENLOVE",
            drawableRes = R.drawable.banner_fried_chicken
        ),
        PromoBanner(
            id = "b3",
            title = "Woodfire Pepperoni Pizza",
            subtitle = "Stringy mozzarella & fresh basil slice by slice",
            discountTag = "HOT DEAL",
            couponCode = "PIZZA20",
            drawableRes = R.drawable.banner_pizza_deal
        )
    )

    val coupons = listOf(
        Coupon("QUICK30", "30% off orders over $15", discountPercent = 30, minOrderCents = 1500),
        Coupon("FREEDEL", "Free Delivery on orders over $12", discountAmountCents = 299, minOrderCents = 1200),
        Coupon("SAVE5", "$5.00 off orders over $20", discountAmountCents = 500, minOrderCents = 2000)
    )

    val products = listOf(
        // BURGERS
        FoodProduct(
            id = "burger_smash_double",
            name = "Double Smashed Angus Burger",
            description = "Two seasoned prime Angus smash patties, double sharp cheddar, caramelized sweet onions, crisp butter lettuce, and house secret sauce on a toasted brioche bun.",
            categoryId = "burgers",
            localDrawableRes = R.drawable.banner_burger_promo,
            imageUrl = "https://images.unsplash.com/photo-1568901346375-23c9450c58cd?w=600&auto=format&fit=crop&q=80",
            priceCents = 999,
            discountPriceCents = 849,
            rating = 4.9,
            ratingCount = 342,
            preparationTimeMinutes = 12,
            isPopular = true,
            isBestSeller = true,
            calories = 780,
            allergens = listOf("Dairy", "Gluten", "Sesame")
        ),
        FoodProduct(
            id = "burger_smoky_bacon",
            name = "Smoky BBQ Bacon Melt",
            description = "Charbroiled Angus beef, hardwood smoked bacon strips, crispy onion tanglers, Monterey Jack cheese, and smoky chipotle BBQ sauce.",
            categoryId = "burgers",
            imageUrl = "https://images.unsplash.com/photo-1586190848861-99aa4a171e90?w=600&auto=format&fit=crop&q=80",
            priceCents = 1049,
            rating = 4.8,
            ratingCount = 189,
            preparationTimeMinutes = 14,
            isPopular = true,
            calories = 850,
            allergens = listOf("Dairy", "Gluten")
        ),
        FoodProduct(
            id = "burger_spicy_jalapeno",
            name = "Firehouse Jalapeño Burger",
            description = "Grilled beef patty spiked with fire-roasted jalapeños, pepper jack cheese, spicy sriracha aioli, and crispy shallots.",
            categoryId = "burgers",
            imageUrl = "https://images.unsplash.com/photo-1550547660-d9450f859349?w=600&auto=format&fit=crop&q=80",
            priceCents = 949,
            rating = 4.7,
            ratingCount = 115,
            preparationTimeMinutes = 13,
            isSpicy = true,
            calories = 720,
            allergens = listOf("Dairy", "Gluten")
        ),
        FoodProduct(
            id = "burger_veggie_delight",
            name = "Green Harvest Veggie Burger",
            description = "Housemade crispy quinoa & black bean patty, ripe avocado slices, heirloom tomato, baby arugula, and garlic tahini crema.",
            categoryId = "burgers",
            imageUrl = "https://images.unsplash.com/photo-1520072959219-c595dc870360?w=600&auto=format&fit=crop&q=80",
            priceCents = 899,
            rating = 4.6,
            ratingCount = 88,
            preparationTimeMinutes = 10,
            isVegetarian = true,
            calories = 510,
            allergens = listOf("Gluten", "Sesame")
        ),

        // FRIED CHICKEN
        FoodProduct(
            id = "chicken_crispy_bucket",
            name = "Southern Fried Chicken Bucket",
            description = "6 pieces of fresh, never frozen chicken marinated for 24 hours in buttermilk and 11 secret herbs, hand-breaded and fried golden.",
            categoryId = "chicken",
            localDrawableRes = R.drawable.banner_fried_chicken,
            imageUrl = "https://images.unsplash.com/photo-1626082927389-6cd097cdc6ec?w=600&auto=format&fit=crop&q=80",
            priceCents = 1499,
            discountPriceCents = 1299,
            rating = 4.9,
            ratingCount = 412,
            preparationTimeMinutes = 15,
            isPopular = true,
            isBestSeller = true,
            calories = 940,
            allergens = listOf("Gluten", "Dairy")
        ),
        FoodProduct(
            id = "chicken_nashville_hot",
            name = "Nashville Hot Chicken Sandwich",
            description = "Crispy buttermilk chicken breast tossed in cayenne hot oil, topped with sweet slaw, dill pickles, and comeback sauce on brioche.",
            categoryId = "chicken",
            imageUrl = "https://images.unsplash.com/photo-1606755962773-d324e0a13086?w=600&auto=format&fit=crop&q=80",
            priceCents = 929,
            rating = 4.8,
            ratingCount = 276,
            preparationTimeMinutes = 12,
            isSpicy = true,
            calories = 760,
            allergens = listOf("Gluten", "Dairy", "Egg")
        ),
        FoodProduct(
            id = "chicken_crispy_tenders",
            name = "Golden Crisp Chicken Tenders",
            description = "5 whole white-meat chicken tenders served with honey mustard and smoky BBQ dipping sauces.",
            categoryId = "chicken",
            imageUrl = "https://images.unsplash.com/photo-1562967914-608f82629710?w=600&auto=format&fit=crop&q=80",
            priceCents = 799,
            rating = 4.7,
            ratingCount = 145,
            preparationTimeMinutes = 10,
            calories = 540,
            allergens = listOf("Gluten")
        ),

        // PIZZA
        FoodProduct(
            id = "pizza_pepperoni_supreme",
            name = "Artisan Pepperoni Supreme Pizza",
            description = "Hand-tossed sourdough crust with San Marzano tomato sauce, generous whole-milk mozzarella, crispy cupped pepperoni, and fresh oregano.",
            categoryId = "pizza",
            localDrawableRes = R.drawable.banner_pizza_deal,
            imageUrl = "https://images.unsplash.com/photo-1534308983496-4fabb1a015ee?w=600&auto=format&fit=crop&q=80",
            priceCents = 1299,
            discountPriceCents = 1099,
            rating = 4.9,
            ratingCount = 310,
            preparationTimeMinutes = 16,
            isBestSeller = true,
            calories = 890,
            allergens = listOf("Dairy", "Gluten")
        ),
        FoodProduct(
            id = "pizza_margherita",
            name = "Classic Margherita Pizza",
            description = "Fresh fior di latte mozzarella, fragrant sweet basil, extra virgin olive oil, and slow-simmered Italian plum tomato sauce.",
            categoryId = "pizza",
            imageUrl = "https://images.unsplash.com/photo-1574071318508-1cdbab80d002?w=600&auto=format&fit=crop&q=80",
            priceCents = 1099,
            rating = 4.8,
            ratingCount = 164,
            preparationTimeMinutes = 14,
            isVegetarian = true,
            calories = 720,
            allergens = listOf("Dairy", "Gluten")
        ),
        FoodProduct(
            id = "pizza_bbq_chicken",
            name = "BBQ Ranch Chicken Pizza",
            description = "Grilled chicken chunks, smoky barbecue sauce, red onion rings, cilantro, and smoked gouda cheese blend.",
            categoryId = "pizza",
            imageUrl = "https://images.unsplash.com/photo-1565299624946-b28f40a0ae38?w=600&auto=format&fit=crop&q=80",
            priceCents = 1349,
            rating = 4.7,
            ratingCount = 142,
            preparationTimeMinutes = 15,
            calories = 880,
            allergens = listOf("Dairy", "Gluten")
        ),

        // TACOS & WRAPS
        FoodProduct(
            id = "tacos_birria_street",
            name = "Trio Birria Beef Tacos",
            description = "Three griddled corn tortillas loaded with slow-cooked shredded beef, melted Oaxaca cheese, onion, cilantro, and rich savory dipping consommé.",
            categoryId = "tacos",
            imageUrl = "https://images.unsplash.com/photo-1565299585323-38d6b0865b47?w=600&auto=format&fit=crop&q=80",
            priceCents = 1099,
            rating = 4.9,
            ratingCount = 224,
            preparationTimeMinutes = 12,
            isPopular = true,
            calories = 680,
            allergens = listOf("Dairy")
        ),
        FoodProduct(
            id = "wrap_crispy_shawarma",
            name = "Signature Chicken Shawarma Wrap",
            description = "Spiced roasted chicken wrapped in warm pita bread with garlic toum sauce, crunchy pickles, and seasoned fries inside.",
            categoryId = "tacos",
            imageUrl = "https://images.unsplash.com/photo-1529006557810-274b9b2fc783?w=600&auto=format&fit=crop&q=80",
            priceCents = 849,
            rating = 4.8,
            ratingCount = 195,
            preparationTimeMinutes = 10,
            calories = 620,
            allergens = listOf("Gluten", "Sesame")
        ),

        // FRIES & SIDES
        FoodProduct(
            id = "fries_truffle_parm",
            name = "Loaded Truffle Parmesan Fries",
            description = "Crispy golden cut Idaho potatoes tossed in white truffle oil, grated aged parmesan, roasted garlic flakes, and fresh parsley.",
            categoryId = "sides",
            imageUrl = "https://images.unsplash.com/photo-1576107232684-1279f3908594?w=600&auto=format&fit=crop&q=80",
            priceCents = 499,
            rating = 4.8,
            ratingCount = 330,
            preparationTimeMinutes = 8,
            isVegetarian = true,
            calories = 420,
            allergens = listOf("Dairy")
        ),
        FoodProduct(
            id = "fries_classic_salted",
            name = "QuickBite Crinkle Cut Fries",
            description = "Extra crunchy crinkle cut fries seasoned with our house sea salt and paprika seasoning blend.",
            categoryId = "sides",
            imageUrl = "https://images.unsplash.com/photo-1541592106381-b31e9677c0e5?w=600&auto=format&fit=crop&q=80",
            priceCents = 349,
            rating = 4.7,
            ratingCount = 410,
            preparationTimeMinutes = 6,
            isVegetarian = true,
            calories = 340,
            allergens = emptyList()
        ),

        // DRINKS
        FoodProduct(
            id = "drink_oreo_shake",
            name = "Cookies & Cream Thick Shake",
            description = "Velvety vanilla bean ice cream blended with crushed Oreo cookies, topped with whipped cream and chocolate fudge drizzle.",
            categoryId = "drinks",
            imageUrl = "https://images.unsplash.com/photo-1572490122747-3968b75cc699?w=600&auto=format&fit=crop&q=80",
            priceCents = 449,
            rating = 4.9,
            ratingCount = 280,
            preparationTimeMinutes = 5,
            isVegetarian = true,
            calories = 480,
            allergens = listOf("Dairy", "Gluten")
        ),
        FoodProduct(
            id = "drink_fountain_soda",
            name = "Sparkling Craft Soda",
            description = "Refreshing chilled fountain beverage with options for Cola, Lemon-Lime, Orange Citrus, or Iced Tea.",
            categoryId = "drinks",
            imageUrl = "https://images.unsplash.com/photo-1622483767028-3f66f32aef97?w=600&auto=format&fit=crop&q=80",
            priceCents = 249,
            rating = 4.6,
            ratingCount = 190,
            preparationTimeMinutes = 2,
            isVegetarian = true,
            calories = 140,
            allergens = emptyList()
        ),

        // DESSERTS
        FoodProduct(
            id = "dessert_warm_churros",
            name = "Cinnamon Sugar Warm Churros",
            description = "Four freshly fried artisan churros dusted in Saigon cinnamon sugar, served with warm Belgian chocolate dipping ganache.",
            categoryId = "desserts",
            imageUrl = "https://images.unsplash.com/photo-1600891964599-f61ba0e24092?w=600&auto=format&fit=crop&q=80",
            priceCents = 499,
            rating = 4.9,
            ratingCount = 205,
            preparationTimeMinutes = 7,
            isVegetarian = true,
            calories = 390,
            allergens = listOf("Gluten", "Dairy")
        ),
        FoodProduct(
            id = "dessert_lava_cake",
            name = "Molten Chocolate Lava Cake",
            description = "Warm rich dark chocolate sponge cake with an oozing liquid chocolate center, dusted with confectioners sugar.",
            categoryId = "desserts",
            imageUrl = "https://images.unsplash.com/photo-1606313564200-e75d5e30476c?w=600&auto=format&fit=crop&q=80",
            priceCents = 549,
            rating = 4.8,
            ratingCount = 145,
            preparationTimeMinutes = 8,
            isVegetarian = true,
            calories = 460,
            allergens = listOf("Dairy", "Gluten", "Egg")
        )
    )
}
