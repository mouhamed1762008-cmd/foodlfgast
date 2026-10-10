package com.example.ui.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
import com.example.ui.theme.BrandAccent
import com.example.ui.theme.BrandPrimary
import com.example.ui.theme.BrandPrimaryDark
import com.example.ui.theme.BrandPrimaryLight
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
    Box(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 14.dp, vertical = 8.dp)
            .testTag("bottom_nav_bar")
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(
                    elevation = 16.dp,
                    shape = RoundedCornerShape(26.dp),
                    spotColor = BrandPrimary.copy(alpha = 0.25f),
                    ambientColor = Color.Black
                ),
            shape = RoundedCornerShape(26.dp),
            color = Color(0xFF141622).copy(alpha = 0.98f),
            border = BorderStroke(
                width = 1.dp,
                brush = Brush.verticalGradient(
                    listOf(
                        Color(0xFF2E3245),
                        Color(0xFF1A1C29)
                    )
                )
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(66.dp)
                    .padding(horizontal = 4.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                ModernNavItem(
                    selected = currentTab == BottomTab.HOME,
                    label = "Home",
                    iconFilled = Icons.Filled.Home,
                    iconOutlined = Icons.Outlined.Home,
                    testTag = "nav_tab_home",
                    onClick = { onSelectTab(BottomTab.HOME) }
                )

                ModernNavItem(
                    selected = currentTab == BottomTab.CATEGORIES,
                    label = "Menu",
                    iconFilled = Icons.Filled.LunchDining,
                    iconOutlined = Icons.Outlined.LunchDining,
                    testTag = "nav_tab_categories",
                    onClick = { onSelectTab(BottomTab.CATEGORIES) }
                )

                ModernNavItem(
                    selected = currentTab == BottomTab.FAVORITES,
                    label = "Favorites",
                    iconFilled = Icons.Filled.Favorite,
                    iconOutlined = Icons.Outlined.FavoriteBorder,
                    badgeCount = favoriteCount,
                    badgeColor = BrandPrimary,
                    badgeTextColor = Color.White,
                    testTag = "nav_tab_favorites",
                    onClick = { onSelectTab(BottomTab.FAVORITES) }
                )

                ModernNavItem(
                    selected = currentTab == BottomTab.ORDERS,
                    label = "Orders",
                    iconFilled = Icons.AutoMirrored.Filled.ReceiptLong,
                    iconOutlined = Icons.AutoMirrored.Outlined.ReceiptLong,
                    badgeCount = activeOrdersCount,
                    badgeColor = BrandAccent,
                    badgeTextColor = Color(0xFF14151C),
                    testTag = "nav_tab_orders",
                    onClick = { onSelectTab(BottomTab.ORDERS) }
                )

                ModernNavItem(
                    selected = currentTab == BottomTab.PROFILE,
                    label = "Profile",
                    iconFilled = Icons.Filled.Person,
                    iconOutlined = Icons.Outlined.Person,
                    testTag = "nav_tab_profile",
                    onClick = { onSelectTab(BottomTab.PROFILE) }
                )
            }
        }
    }
}

@Composable
private fun RowScope.ModernNavItem(
    selected: Boolean,
    label: String,
    iconFilled: ImageVector,
    iconOutlined: ImageVector,
    testTag: String,
    badgeCount: Int = 0,
    badgeColor: Color = BrandPrimary,
    badgeTextColor: Color = Color.White,
    onClick: () -> Unit
) {
    val iconScale by animateFloatAsState(
        targetValue = if (selected) 1.15f else 1.0f,
        animationSpec = spring(dampingRatio = 0.6f, stiffness = 450f),
        label = "tab_icon_scale"
    )
    val animatedIconColor by animateColorAsState(
        targetValue = if (selected) BrandPrimary else Color(0xFF8A90A2),
        label = "tab_icon_color"
    )
    val animatedLabelColor by animateColorAsState(
        targetValue = if (selected) BrandPrimary else Color(0xFF8A90A2),
        label = "tab_label_color"
    )
    val animatedBgColor by animateColorAsState(
        targetValue = if (selected) BrandPrimary.copy(alpha = 0.14f) else Color.Transparent,
        label = "tab_bg_color"
    )

    Box(
        modifier = Modifier
            .weight(1f)
            .fillMaxHeight()
            .clip(RoundedCornerShape(20.dp))
            .background(animatedBgColor)
            .then(
                if (selected) {
                    Modifier.border(
                        1.dp,
                        BrandPrimary.copy(alpha = 0.28f),
                        RoundedCornerShape(20.dp)
                    )
                } else Modifier
            )
            .clickable(onClick = onClick)
            .testTag(testTag),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.scale(iconScale)
            ) {
                Icon(
                    imageVector = if (selected) iconFilled else iconOutlined,
                    contentDescription = label,
                    tint = animatedIconColor,
                    modifier = Modifier.size(23.dp)
                )

                if (badgeCount > 0) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .offset(x = 10.dp, y = (-4).dp)
                            .background(badgeColor, RoundedCornerShape(10.dp))
                            .padding(horizontal = 4.dp, vertical = 1.dp)
                    ) {
                        Text(
                            text = if (badgeCount > 99) "99+" else "$badgeCount",
                            color = badgeTextColor,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = label,
                fontSize = 10.sp,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                color = animatedLabelColor,
                maxLines = 1
            )

            AnimatedVisibility(
                visible = selected,
                enter = fadeIn() + scaleIn(),
                exit = fadeOut() + scaleOut()
            ) {
                Box(
                    modifier = Modifier
                        .padding(top = 2.dp)
                        .size(width = 12.dp, height = 2.5.dp)
                        .background(
                            brush = Brush.horizontalGradient(
                                listOf(BrandPrimaryLight, BrandPrimary)
                            ),
                            shape = RoundedCornerShape(2.dp)
                        )
                )
            }
        }
    }
}
