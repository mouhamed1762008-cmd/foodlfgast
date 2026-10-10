package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.FoodCatalogData
import com.example.data.FoodRepository
import com.example.model.CartItem
import com.example.model.Coupon
import com.example.model.DeliveryAddress
import com.example.model.FoodCategory
import com.example.model.FoodProduct
import com.example.model.MealSize
import com.example.model.OptionItem
import com.example.model.Order
import com.example.model.OrderItem
import com.example.model.OrderStatus
import com.example.model.UserProfile
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed class ScreenDestination {
    object Home : ScreenDestination()
    object Categories : ScreenDestination()
    data class ProductDetail(val product: FoodProduct) : ScreenDestination()
    object Cart : ScreenDestination()
    object Checkout : ScreenDestination()
    data class OrderTracking(val orderId: String) : ScreenDestination()
    object OrderHistory : ScreenDestination()
    object Favorites : ScreenDestination()
    object Profile : ScreenDestination()
    object AdminPanel : ScreenDestination()
}

enum class BottomTab {
    HOME, CATEGORIES, FAVORITES, ORDERS, PROFILE
}

data class FastFoodUiState(
    val currentDestination: ScreenDestination = ScreenDestination.Home,
    val backStack: List<ScreenDestination> = emptyList(),
    val currentTab: BottomTab = BottomTab.HOME,

    // User & Location
    val userProfile: UserProfile = UserProfile(),
    val savedAddresses: List<DeliveryAddress> = emptyList(),
    val selectedAddress: DeliveryAddress? = null,
    val selectedBranch: String = "Central Downtown (0.8 mi • 15 min)",
    val favorites: Set<String> = emptySet(),

    // Catalog & Search
    val categories: List<FoodCategory> = emptyList(),
    val selectedCategoryId: String = "all",
    val searchQuery: String = "",
    val isVegetarianFilter: Boolean = false,
    val isSpicyFilter: Boolean = false,
    val sortBy: String = "POPULAR",
    val filteredProducts: List<FoodProduct> = emptyList(),

    // Cart
    val cartItems: List<CartItem> = emptyList(),
    val appliedCoupon: Coupon? = null,
    val isDelivery: Boolean = true,
    val deliveryInstructions: String = "",

    // Orders
    val orders: List<Order> = emptyList(),
    val activeTrackingOrder: Order? = null,

    // Preferences & UI (Light mode removed - exclusively modern dark theme)
    val isDarkMode: Boolean = true,
    val selectedLanguage: String = "en", // en, ar, fr
    val orderNotificationEnabled: Boolean = true,
    val promoNotificationEnabled: Boolean = true,

    // Feedback
    val toastMessage: String? = null,
    val isLoading: Boolean = false
) {
    val cartItemCount: Int
        get() = cartItems.sumOf { it.quantity }

    val subtotalCents: Int
        get() = cartItems.sumOf { it.totalPriceCents() }

    val discountCents: Int
        get() = appliedCoupon?.let { coupon ->
            if (subtotalCents >= coupon.minOrderCents) {
                if (coupon.discountPercent > 0) {
                    (subtotalCents * coupon.discountPercent) / 100
                } else {
                    coupon.discountAmountCents.coerceAtMost(subtotalCents)
                }
            } else 0
        } ?: 0

    val deliveryFeeCents: Int
        get() = if (isDelivery) {
            if (appliedCoupon?.code == "FREEDEL" && subtotalCents >= 1200) 0 else 299
        } else 0

    val taxCents: Int
        get() = ((subtotalCents - discountCents).coerceAtLeast(0) * 8) / 100

    val finalTotalCents: Int
        get() = ((subtotalCents - discountCents).coerceAtLeast(0) + deliveryFeeCents + taxCents)
}

class FastFoodViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = FoodRepository(application)
    private val auth = Firebase.auth

    private val _uiState = MutableStateFlow(FastFoodUiState())
    val uiState: StateFlow<FastFoodUiState> = _uiState.asStateFlow()

    init {
        loadInitialCatalog()
        setupUserListeners()
    }

    private fun loadInitialCatalog() {
        val categories = repository.getCategories()
        val allProducts = repository.getProducts()
        _uiState.update {
            it.copy(
                categories = categories,
                filteredProducts = allProducts,
                savedAddresses = listOf(
                    DeliveryAddress("addr_1", "", "Home", "742 Evergreen Terrace, Apt 4B", "Springfield", "97477", "+1 (555) 234-5678", true),
                    DeliveryAddress("addr_2", "", "Office", "100 Technology Plaza, Floor 8", "Springfield", "97477", "+1 (555) 876-5432", false)
                )
            )
        }
        _uiState.update {
            it.copy(selectedAddress = it.savedAddresses.firstOrNull())
        }
    }

    fun setupUserListeners() {
        val user = auth.currentUser ?: return
        viewModelScope.launch {
            repository.observeUserProfile(user.uid).collect { profile ->
                if (profile != null) {
                    _uiState.update {
                        it.copy(
                            userProfile = profile,
                            favorites = profile.favorites.toSet()
                        )
                    }
                } else {
                    // Create initial profile in Firestore
                    val initialProfile = UserProfile(
                        uid = user.uid,
                        email = user.email ?: "",
                        fullName = user.displayName ?: "QuickBite Foodie",
                        phoneNumber = user.phoneNumber ?: "",
                        favorites = listOf("burger_smash_double", "chicken_crispy_bucket")
                    )
                    repository.saveUserProfile(initialProfile)
                    _uiState.update {
                        it.copy(
                            userProfile = initialProfile,
                            favorites = initialProfile.favorites.toSet()
                        )
                    }
                }
            }
        }

        viewModelScope.launch {
            repository.observeAddresses(user.uid).collect { addresses ->
                if (addresses.isNotEmpty()) {
                    _uiState.update { state ->
                        state.copy(
                            savedAddresses = addresses,
                            selectedAddress = addresses.firstOrNull { it.isDefault } ?: addresses.firstOrNull()
                        )
                    }
                }
            }
        }

        viewModelScope.launch {
            repository.observeOrders(user.uid).collect { orders ->
                _uiState.update { state ->
                    val trackingOrder = state.activeTrackingOrder?.let { active ->
                        orders.firstOrNull { it.id == active.id } ?: active
                    } ?: orders.firstOrNull { it.orderStatus != "DELIVERED" && it.orderStatus != "CANCELLED" }
                    state.copy(
                        orders = orders,
                        activeTrackingOrder = trackingOrder
                    )
                }
            }
        }
    }

    // --- NAVIGATION ---

    fun navigateTo(destination: ScreenDestination) {
        _uiState.update {
            it.copy(
                backStack = it.backStack + it.currentDestination,
                currentDestination = destination
            )
        }
    }

    fun navigateBack(): Boolean {
        val stack = _uiState.value.backStack
        if (stack.isNotEmpty()) {
            val prev = stack.last()
            _uiState.update {
                it.copy(
                    backStack = it.backStack.dropLast(1),
                    currentDestination = prev
                )
            }
            return true
        }
        return false
    }

    fun selectBottomTab(tab: BottomTab) {
        val destination = when (tab) {
            BottomTab.HOME -> ScreenDestination.Home
            BottomTab.CATEGORIES -> ScreenDestination.Categories
            BottomTab.FAVORITES -> ScreenDestination.Favorites
            BottomTab.ORDERS -> ScreenDestination.OrderHistory
            BottomTab.PROFILE -> ScreenDestination.Profile
        }
        _uiState.update {
            it.copy(
                currentTab = tab,
                currentDestination = destination,
                backStack = emptyList()
            )
        }
    }

    // --- SEARCH & FILTER ---

    fun updateSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        applyFilters()
    }

    fun selectCategory(categoryId: String) {
        _uiState.update { it.copy(selectedCategoryId = categoryId) }
        applyFilters()
    }

    fun toggleVegetarianFilter() {
        _uiState.update { it.copy(isVegetarianFilter = !it.isVegetarianFilter) }
        applyFilters()
    }

    fun toggleSpicyFilter() {
        _uiState.update { it.copy(isSpicyFilter = !it.isSpicyFilter) }
        applyFilters()
    }

    fun setSortBy(sortBy: String) {
        _uiState.update { it.copy(sortBy = sortBy) }
        applyFilters()
    }

    private fun applyFilters() {
        val state = _uiState.value
        val filtered = repository.searchProducts(
            query = state.searchQuery,
            categoryId = state.selectedCategoryId,
            vegetarianOnly = state.isVegetarianFilter,
            spicyOnly = state.isSpicyFilter,
            sortBy = state.sortBy
        )
        _uiState.update { it.copy(filteredProducts = filtered) }
    }

    // --- CART ACTIONS ---

    fun addToCart(
        product: FoodProduct,
        size: MealSize = product.availableSizes.firstOrNull() ?: MealSize("regular", "Regular", 0),
        addons: List<OptionItem> = emptyList(),
        isCombo: Boolean = false,
        instructions: String = "",
        quantity: Int = 1
    ) {
        val newItem = CartItem(
            product = product,
            selectedSize = size,
            selectedAddons = addons,
            isCombo = isCombo,
            specialInstructions = instructions,
            quantity = quantity
        )
        _uiState.update {
            it.copy(
                cartItems = it.cartItems + newItem,
                toastMessage = "Added ${product.name} to cart"
            )
        }
    }

    fun updateCartItemQuantity(itemId: String, delta: Int) {
        _uiState.update { state ->
            val updated = state.cartItems.mapNotNull { item ->
                if (item.id == itemId) {
                    val newQty = item.quantity + delta
                    if (newQty > 0) item.copy(quantity = newQty) else null
                } else item
            }
            state.copy(cartItems = updated)
        }
    }

    fun removeCartItem(itemId: String) {
        _uiState.update { state ->
            state.copy(cartItems = state.cartItems.filterNot { it.id == itemId })
        }
    }

    fun clearCart() {
        _uiState.update { it.copy(cartItems = emptyList(), appliedCoupon = null) }
    }

    fun applyCoupon(code: String): Boolean {
        val coupon = repository.getCoupons().firstOrNull { it.code.equals(code.trim(), ignoreCase = true) }
        if (coupon != null) {
            _uiState.update {
                it.copy(
                    appliedCoupon = coupon,
                    toastMessage = "Coupon ${coupon.code} applied!"
                )
            }
            return true
        } else {
            _uiState.update { it.copy(toastMessage = "Invalid coupon code") }
            return false
        }
    }

    fun removeCoupon() {
        _uiState.update { it.copy(appliedCoupon = null) }
    }

    fun setDeliveryMode(isDelivery: Boolean) {
        _uiState.update { it.copy(isDelivery = isDelivery) }
    }

    fun setDeliveryInstructions(instructions: String) {
        _uiState.update { it.copy(deliveryInstructions = instructions) }
    }

    // --- FAVORITES ---

    fun toggleFavorite(productId: String) {
        val currentFavs = _uiState.value.favorites
        val isFav = currentFavs.contains(productId)
        val newFavs = if (isFav) currentFavs - productId else currentFavs + productId
        _uiState.update {
            it.copy(
                favorites = newFavs,
                toastMessage = if (!isFav) "Added to favorites ❤️" else "Removed from favorites"
            )
        }
        val user = auth.currentUser ?: return
        viewModelScope.launch {
            repository.toggleFavorite(productId, currentFavs.toList())
        }
    }

    // --- CHECKOUT & ORDERS ---

    fun setSelectedAddress(address: DeliveryAddress) {
        _uiState.update { it.copy(selectedAddress = address) }
    }

    fun addSavedAddress(address: DeliveryAddress) {
        val user = auth.currentUser
        val complete = address.copy(userId = user?.uid ?: "")
        _uiState.update {
            it.copy(
                savedAddresses = it.savedAddresses + complete,
                selectedAddress = complete,
                toastMessage = "Address saved"
            )
        }
        if (user != null) {
            viewModelScope.launch {
                try {
                    repository.addAddress(complete)
                } catch (e: Exception) {
                    // Fallback to local
                }
            }
        }
    }

    fun placeOrder(paymentMethod: String = "CASH_ON_DELIVERY") {
        val state = _uiState.value
        if (state.cartItems.isEmpty()) return

        val user = auth.currentUser
        val orderItems = state.cartItems.map {
            OrderItem(
                productId = it.product.id,
                productName = it.product.name,
                priceCents = it.unitPriceCents(),
                quantity = it.quantity,
                customizationSummary = it.customizationSummary(),
                imageUrl = it.product.imageUrl
            )
        }

        val orderId = "QB-${(100000..999999).random()}"
        val newOrder = Order(
            id = orderId,
            userId = user?.uid ?: "guest",
            customerName = state.userProfile.fullName.ifBlank { "Alex Customer" },
            customerPhone = state.userProfile.phoneNumber.ifBlank { "+1 555-0192" },
            deliveryAddress = if (state.isDelivery) {
                state.selectedAddress?.let { "${it.addressLine}, ${it.city}" } ?: "123 Main St, Springfield"
            } else "Pickup at: Central Downtown Branch",
            deliveryInstructions = state.deliveryInstructions,
            isDelivery = state.isDelivery,
            items = orderItems,
            subtotal = state.subtotalCents,
            discount = state.discountCents,
            deliveryFee = state.deliveryFeeCents,
            tax = state.taxCents,
            total = state.finalTotalCents,
            couponCode = state.appliedCoupon?.code ?: "",
            paymentMethod = paymentMethod,
            paymentStatus = if (paymentMethod == "CASH_ON_DELIVERY") "PENDING" else "PAID",
            orderStatus = OrderStatus.PLACED.name,
            estimatedMinutesRemaining = 25
        )

        // Optimistically update state
        _uiState.update {
            it.copy(
                orders = listOf(newOrder) + it.orders,
                activeTrackingOrder = newOrder,
                cartItems = emptyList(),
                appliedCoupon = null,
                toastMessage = "Order #$orderId placed successfully! 🍔"
            )
        }

        navigateTo(ScreenDestination.OrderTracking(orderId))

        // Persist to Cloud Firestore if signed in
        if (user != null) {
            viewModelScope.launch {
                try {
                    repository.placeOrder(newOrder)
                } catch (e: Exception) {
                    // Handled via FirestoreErrorHandling
                }
            }
        }
    }

    fun updateOrderStatus(orderId: String, newStatus: OrderStatus) {
        _uiState.update { state ->
            val updatedOrders = state.orders.map {
                if (it.id == orderId) it.copy(orderStatus = newStatus.name) else it
            }
            val updatedTracking = if (state.activeTrackingOrder?.id == orderId) {
                state.activeTrackingOrder.copy(orderStatus = newStatus.name)
            } else state.activeTrackingOrder
            state.copy(
                orders = updatedOrders,
                activeTrackingOrder = updatedTracking,
                toastMessage = "Order status: ${newStatus.title}"
            )
        }
        val user = auth.currentUser
        if (user != null) {
            viewModelScope.launch {
                try {
                    repository.updateOrderStatus(orderId, newStatus)
                } catch (e: Exception) {
                    // Handled
                }
            }
        }
    }

    fun cancelOrder(orderId: String) {
        updateOrderStatus(orderId, OrderStatus.CANCELLED)
    }

    fun reorder(order: Order) {
        order.items.forEach { item ->
            val product = repository.getProductById(item.productId)
            if (product != null) {
                addToCart(product = product, quantity = item.quantity)
            }
        }
        _uiState.update { it.copy(toastMessage = "Items added to cart!") }
        navigateTo(ScreenDestination.Cart)
    }

    // --- USER PROFILE & PREFERENCES ---

    fun updateUserProfile(name: String, email: String, phone: String, photoUrl: String = "") {
        val updated = _uiState.value.userProfile.copy(
            fullName = name,
            email = email,
            phoneNumber = phone,
            profileImageUrl = photoUrl
        )
        _uiState.update { it.copy(userProfile = updated, toastMessage = "Profile updated!") }
        val user = auth.currentUser ?: return
        viewModelScope.launch {
            try {
                repository.saveUserProfile(updated)
            } catch (e: Exception) {
                // Handled
            }
        }
    }

    fun toggleDarkMode() {
        _uiState.update { it.copy(isDarkMode = !it.isDarkMode) }
    }

    fun setLanguage(lang: String) {
        _uiState.update { it.copy(selectedLanguage = lang, toastMessage = "Language set to $lang") }
    }

    fun setNotificationPrefs(order: Boolean, promo: Boolean) {
        _uiState.update {
            it.copy(
                orderNotificationEnabled = order,
                promoNotificationEnabled = promo,
                toastMessage = "Notification preferences updated"
            )
        }
    }

    fun clearToast() {
        _uiState.update { it.copy(toastMessage = null) }
    }
}
