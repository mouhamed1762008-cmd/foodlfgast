package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.FoodCatalogData
import com.example.model.FoodProduct
import com.example.ui.components.CartBadge
import com.example.ui.components.CategoryTabItem
import com.example.ui.components.FoodItemCard
import com.example.ui.components.LocationSelectorBar
import com.example.ui.components.PromoBannerCard
import com.example.ui.theme.BrandAccent
import com.example.ui.theme.BrandPrimary
import com.example.viewmodel.FastFoodUiState
import com.example.viewmodel.ScreenDestination

@Composable
fun HomeScreen(
    state: FastFoodUiState,
    onNavigate: (ScreenDestination) -> Unit,
    onSelectCategory: (String) -> Unit,
    onSearchQueryChange: (String) -> Unit,
    onToggleFavorite: (String) -> Unit,
    onQuickAddToCart: (FoodProduct) -> Unit,
    modifier: Modifier = Modifier
) {
    var showBranchDialog by remember { mutableStateOf(false) }
    var selectedBranch by remember { mutableStateOf(state.selectedBranch) }

    val branches = listOf(
        "Central Downtown (0.8 mi • 15 min)",
        "Westside Mall Branch (1.4 mi • 20 min)",
        "University Campus Hub (2.1 mi • 25 min)",
        "Airport Plaza Express (3.5 mi • 35 min)"
    )

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("home_screen"),
        contentPadding = PaddingValues(bottom = 90.dp)
    ) {
        // TOP APP BAR: Logo, Branch selector, Notifications, Cart
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_quickbite_logo),
                        contentDescription = "QuickBite",
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    LocationSelectorBar(
                        branchName = selectedBranch,
                        onClick = { showBranchDialog = true }
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    CartBadge(
                        count = state.cartItemCount,
                        onClick = { onNavigate(ScreenDestination.Cart) }
                    )
                }
            }
        }

        // GREETING
        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)) {
                val greetingName = state.userProfile.fullName.ifBlank { "Foodie" }.split(" ").first()
                Text(
                    text = "Hello, $greetingName! 👋",
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
                Text(
                    text = "What are you craving today?",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                )
            }
        }

        // SEARCH BAR
        item {
            Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)) {
                OutlinedTextField(
                    value = state.searchQuery,
                    onValueChange = onSearchQueryChange,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("home_search_bar"),
                    placeholder = { Text("Search delicious burgers, pizza, chicken...") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = BrandPrimary
                        )
                    },
                    trailingIcon = {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = BrandPrimary,
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .clickable { onNavigate(ScreenDestination.Categories) }
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.FilterList,
                                    contentDescription = "Filter",
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    },
                    shape = RoundedCornerShape(18.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                        focusedBorderColor = BrandPrimary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.surfaceVariant
                    ),
                    singleLine = true
                )
            }
        }

        // PROMOTIONAL BANNERS CAROUSEL
        item {
            Column(modifier = Modifier.padding(top = 6.dp)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Special Offers & Deals 🔥",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }

                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    items(FoodCatalogData.promoBanners) { banner ->
                        PromoBannerCard(
                            banner = banner,
                            onClick = {
                                // Match product or open details
                                when (banner.id) {
                                    "b1" -> onNavigate(
                                        ScreenDestination.ProductDetail(
                                            FoodCatalogData.products.first { it.id == "burger_smash_double" }
                                        )
                                    )
                                    "b2" -> onNavigate(
                                        ScreenDestination.ProductDetail(
                                            FoodCatalogData.products.first { it.id == "chicken_crispy_bucket" }
                                        )
                                    )
                                    "b3" -> onNavigate(
                                        ScreenDestination.ProductDetail(
                                            FoodCatalogData.products.first { it.id == "pizza_pepperoni_supreme" }
                                        )
                                    )
                                }
                            },
                            modifier = Modifier.width(320.dp)
                        )
                    }
                }
            }
        }

        // CATEGORIES SELECTOR
        item {
            Column(modifier = Modifier.padding(top = 18.dp)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Food Categories",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    TextButton(onClick = { onNavigate(ScreenDestination.Categories) }) {
                        Text("See All", color = BrandPrimary, fontWeight = FontWeight.Bold)
                    }
                }

                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(state.categories) { category ->
                        CategoryTabItem(
                            category = category,
                            isSelected = category.id == state.selectedCategoryId,
                            onClick = {
                                onSelectCategory(category.id)
                                onNavigate(ScreenDestination.Categories)
                            }
                        )
                    }
                }
            }
        }

        // POPULAR MEALS (HORIZONTAL SCROLL)
        item {
            Column(modifier = Modifier.padding(top = 20.dp)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Popular Right Now ⭐️",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    TextButton(onClick = { onNavigate(ScreenDestination.Categories) }) {
                        Text("View Menu", color = BrandPrimary, fontWeight = FontWeight.Bold)
                    }
                }

                val popularItems = FoodCatalogData.products.filter { it.isPopular }
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    items(popularItems) { product ->
                        FoodItemCard(
                            product = product,
                            isFavorite = state.favorites.contains(product.id),
                            onFavoriteClick = { onToggleFavorite(product.id) },
                            onProductClick = { onNavigate(ScreenDestination.ProductDetail(product)) },
                            onQuickAddClick = { onQuickAddToCart(product) },
                            modifier = Modifier.width(185.dp)
                        )
                    }
                }
            }
        }

        // BEST SELLERS & RECOMMENDED
        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 18.dp)) {
                Text(
                    text = "Chef's Recommendations 👨‍🍳",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
                Spacer(modifier = Modifier.height(10.dp))
            }
        }

        val recommendedItems = FoodCatalogData.products.filter { it.isBestSeller }
        items(recommendedItems.chunked(2)) { pair ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                pair.forEach { product ->
                    FoodItemCard(
                        product = product,
                        isFavorite = state.favorites.contains(product.id),
                        onFavoriteClick = { onToggleFavorite(product.id) },
                        onProductClick = { onNavigate(ScreenDestination.ProductDetail(product)) },
                        onQuickAddClick = { onQuickAddToCart(product) },
                        modifier = Modifier.weight(1f)
                    )
                }
                if (pair.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }

    // Branch selection dialog
    if (showBranchDialog) {
        AlertDialog(
            onDismissRequest = { showBranchDialog = false },
            title = { Text("Select QuickBite Branch", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    branches.forEach { branch ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { selectedBranch = branch }
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = selectedBranch == branch,
                                onClick = { selectedBranch = branch }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = branch, style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showBranchDialog = false }) {
                    Text("Confirm", fontWeight = FontWeight.Bold, color = BrandPrimary)
                }
            }
        )
    }
}
