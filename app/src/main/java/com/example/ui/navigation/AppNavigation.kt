package com.example.ui.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.automirrored.outlined.ReceiptLong
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LunchDining
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.LunchDining
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ui.screens.AdminPanelScreen
import com.example.ui.screens.CartScreen
import com.example.ui.screens.CategoriesScreen
import com.example.ui.screens.CheckoutScreen
import com.example.ui.screens.FavoritesScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.OrderHistoryScreen
import com.example.ui.screens.OrderTrackingScreen
import com.example.ui.screens.ProductDetailScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.theme.BrandPrimary
import com.example.viewmodel.BottomTab
import com.example.viewmodel.FastFoodUiState
import com.example.viewmodel.FastFoodViewModel
import com.example.viewmodel.ScreenDestination

@Composable
fun MainAppNavigation(
    viewModel: FastFoodViewModel,
    state: FastFoodUiState,
    onSignOut: () -> Unit,
    modifier: Modifier = Modifier
) {
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state.toastMessage) {
        state.toastMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearToast()
        }
    }

    val isTopLevelScreen = state.currentDestination is ScreenDestination.Home ||
            state.currentDestination is ScreenDestination.Categories ||
            state.currentDestination is ScreenDestination.Favorites ||
            state.currentDestination is ScreenDestination.OrderHistory ||
            state.currentDestination is ScreenDestination.Profile

    BackHandler(enabled = !isTopLevelScreen) {
        viewModel.navigateBack()
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            if (isTopLevelScreen) {
                FastFoodBottomBar(
                    currentTab = state.currentTab,
                    favoriteCount = state.favorites.size,
                    activeOrdersCount = state.orders.count { it.orderStatus != "DELIVERED" && it.orderStatus != "CANCELLED" },
                    onSelectTab = { tab -> viewModel.selectBottomTab(tab) }
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            AnimatedContent(
                targetState = state.currentDestination,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "screen_transition"
            ) { destination ->
                when (destination) {
                    is ScreenDestination.Home -> HomeScreen(
                        state = state,
                        onNavigate = { viewModel.navigateTo(it) },
                        onSelectCategory = { viewModel.selectCategory(it) },
                        onSearchQueryChange = { viewModel.updateSearchQuery(it) },
                        onToggleFavorite = { viewModel.toggleFavorite(it) },
                        onQuickAddToCart = { viewModel.addToCart(it) }
                    )

                    is ScreenDestination.Categories -> CategoriesScreen(
                        state = state,
                        onNavigate = { viewModel.navigateTo(it) },
                        onSearchQueryChange = { viewModel.updateSearchQuery(it) },
                        onSelectCategory = { viewModel.selectCategory(it) },
                        onToggleVegetarian = { viewModel.toggleVegetarianFilter() },
                        onToggleSpicy = { viewModel.toggleSpicyFilter() },
                        onSetSortBy = { viewModel.setSortBy(it) },
                        onToggleFavorite = { viewModel.toggleFavorite(it) },
                        onQuickAddToCart = { viewModel.addToCart(it) }
                    )

                    is ScreenDestination.ProductDetail -> ProductDetailScreen(
                        product = destination.product,
                        isFavorite = state.favorites.contains(destination.product.id),
                        onBack = { viewModel.navigateBack() },
                        onToggleFavorite = { viewModel.toggleFavorite(destination.product.id) },
                        onAddToCart = { prod, size, addons, isCombo, instructions, qty ->
                            viewModel.addToCart(prod, size, addons, isCombo, instructions, qty)
                        }
                    )

                    is ScreenDestination.Cart -> CartScreen(
                        state = state,
                        onBack = { viewModel.navigateBack() },
                        onUpdateQuantity = { id, delta -> viewModel.updateCartItemQuantity(id, delta) },
                        onRemoveItem = { viewModel.removeCartItem(it) },
                        onApplyCoupon = { viewModel.applyCoupon(it) },
                        onRemoveCoupon = { viewModel.removeCoupon() },
                        onSetDeliveryMode = { viewModel.setDeliveryMode(it) },
                        onProceedToCheckout = { viewModel.navigateTo(ScreenDestination.Checkout) },
                        onBrowseMenu = { viewModel.selectBottomTab(BottomTab.CATEGORIES) }
                    )

                    is ScreenDestination.Checkout -> CheckoutScreen(
                        state = state,
                        onBack = { viewModel.navigateBack() },
                        onSelectAddress = { viewModel.setSelectedAddress(it) },
                        onAddNewAddress = { viewModel.addSavedAddress(it) },
                        onSetDeliveryInstructions = { viewModel.setDeliveryInstructions(it) },
                        onPlaceOrder = { paymentMethod -> viewModel.placeOrder(paymentMethod) }
                    )

                    is ScreenDestination.OrderTracking -> {
                        val trackingOrder = state.orders.firstOrNull { it.id == destination.orderId }
                            ?: state.activeTrackingOrder
                        if (trackingOrder != null) {
                            OrderTrackingScreen(
                                order = trackingOrder,
                                onBack = { viewModel.selectBottomTab(BottomTab.ORDERS) },
                                onCancelOrder = { viewModel.cancelOrder(it) },
                                onUpdateStatus = { id, st -> viewModel.updateOrderStatus(id, st) }
                            )
                        } else {
                            OrderHistoryScreen(
                                state = state,
                                onTrackOrder = { viewModel.navigateTo(ScreenDestination.OrderTracking(it)) },
                                onReorder = { viewModel.reorder(it) },
                                onBrowseMenu = { viewModel.selectBottomTab(BottomTab.CATEGORIES) }
                            )
                        }
                    }

                    is ScreenDestination.OrderHistory -> OrderHistoryScreen(
                        state = state,
                        onTrackOrder = { viewModel.navigateTo(ScreenDestination.OrderTracking(it)) },
                        onReorder = { viewModel.reorder(it) },
                        onBrowseMenu = { viewModel.selectBottomTab(BottomTab.CATEGORIES) }
                    )

                    is ScreenDestination.Favorites -> FavoritesScreen(
                        state = state,
                        onNavigate = { viewModel.navigateTo(it) },
                        onToggleFavorite = { viewModel.toggleFavorite(it) },
                        onQuickAddToCart = { viewModel.addToCart(it) },
                        onBrowseMenu = { viewModel.selectBottomTab(BottomTab.CATEGORIES) }
                    )

                    is ScreenDestination.Profile -> ProfileScreen(
                        state = state,
                        onNavigate = { viewModel.navigateTo(it) },
                        onUpdateProfile = { name, email, phone -> viewModel.updateUserProfile(name, email, phone) },
                        onToggleDarkMode = { viewModel.toggleDarkMode() },
                        onSetLanguage = { viewModel.setLanguage(it) },
                        onSetNotificationPrefs = { order, promo -> viewModel.setNotificationPrefs(order, promo) },
                        onSignOut = onSignOut
                    )

                    is ScreenDestination.AdminPanel -> AdminPanelScreen(
                        state = state,
                        onBack = { viewModel.navigateBack() },
                        onUpdateOrderStatus = { id, st -> viewModel.updateOrderStatus(id, st) }
                    )
                }
            }
        }
    }
}

@Composable
fun FastFoodBottomBar(
    currentTab: BottomTab,
    favoriteCount: Int,
    activeOrdersCount: Int,
    onSelectTab: (BottomTab) -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationBar(
        modifier = modifier.testTag("bottom_nav_bar"),
        tonalElevation = 8.dp
    ) {
        NavigationBarItem(
            selected = currentTab == BottomTab.HOME,
            onClick = { onSelectTab(BottomTab.HOME) },
            icon = {
                Icon(
                    imageVector = if (currentTab == BottomTab.HOME) Icons.Filled.Home else Icons.Outlined.Home,
                    contentDescription = "Home"
                )
            },
            label = { Text("Home", fontWeight = if (currentTab == BottomTab.HOME) FontWeight.Bold else FontWeight.Normal) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = BrandPrimary,
                selectedTextColor = BrandPrimary,
                indicatorColor = BrandPrimary.copy(alpha = 0.12f)
            ),
            modifier = Modifier.testTag("nav_tab_home")
        )

        NavigationBarItem(
            selected = currentTab == BottomTab.CATEGORIES,
            onClick = { onSelectTab(BottomTab.CATEGORIES) },
            icon = {
                Icon(
                    imageVector = if (currentTab == BottomTab.CATEGORIES) Icons.Filled.LunchDining else Icons.Outlined.LunchDining,
                    contentDescription = "Menu"
                )
            },
            label = { Text("Menu", fontWeight = if (currentTab == BottomTab.CATEGORIES) FontWeight.Bold else FontWeight.Normal) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = BrandPrimary,
                selectedTextColor = BrandPrimary,
                indicatorColor = BrandPrimary.copy(alpha = 0.12f)
            ),
            modifier = Modifier.testTag("nav_tab_categories")
        )

        NavigationBarItem(
            selected = currentTab == BottomTab.FAVORITES,
            onClick = { onSelectTab(BottomTab.FAVORITES) },
            icon = {
                BadgedBox(
                    badge = {
                        if (favoriteCount > 0) {
                            Badge(containerColor = BrandPrimary) { Text("$favoriteCount") }
                        }
                    }
                ) {
                    Icon(
                        imageVector = if (currentTab == BottomTab.FAVORITES) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                        contentDescription = "Favorites"
                    )
                }
            },
            label = { Text("Favorites", fontWeight = if (currentTab == BottomTab.FAVORITES) FontWeight.Bold else FontWeight.Normal) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = BrandPrimary,
                selectedTextColor = BrandPrimary,
                indicatorColor = BrandPrimary.copy(alpha = 0.12f)
            ),
            modifier = Modifier.testTag("nav_tab_favorites")
        )

        NavigationBarItem(
            selected = currentTab == BottomTab.ORDERS,
            onClick = { onSelectTab(BottomTab.ORDERS) },
            icon = {
                BadgedBox(
                    badge = {
                        if (activeOrdersCount > 0) {
                            Badge(containerColor = BrandPrimary) { Text("$activeOrdersCount") }
                        }
                    }
                ) {
                    Icon(
                        imageVector = if (currentTab == BottomTab.ORDERS) Icons.AutoMirrored.Filled.ReceiptLong else Icons.AutoMirrored.Outlined.ReceiptLong,
                        contentDescription = "Orders"
                    )
                }
            },
            label = { Text("Orders", fontWeight = if (currentTab == BottomTab.ORDERS) FontWeight.Bold else FontWeight.Normal) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = BrandPrimary,
                selectedTextColor = BrandPrimary,
                indicatorColor = BrandPrimary.copy(alpha = 0.12f)
            ),
            modifier = Modifier.testTag("nav_tab_orders")
        )

        NavigationBarItem(
            selected = currentTab == BottomTab.PROFILE,
            onClick = { onSelectTab(BottomTab.PROFILE) },
            icon = {
                Icon(
                    imageVector = if (currentTab == BottomTab.PROFILE) Icons.Filled.Person else Icons.Outlined.Person,
                    contentDescription = "Profile"
                )
            },
            label = { Text("Profile", fontWeight = if (currentTab == BottomTab.PROFILE) FontWeight.Bold else FontWeight.Normal) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = BrandPrimary,
                selectedTextColor = BrandPrimary,
                indicatorColor = BrandPrimary.copy(alpha = 0.12f)
            ),
            modifier = Modifier.testTag("nav_tab_profile")
        )
    }
}
