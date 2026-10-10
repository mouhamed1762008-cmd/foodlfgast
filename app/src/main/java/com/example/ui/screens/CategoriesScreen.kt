package com.example.ui.screens

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.model.FoodProduct
import com.example.ui.components.CartBadge
import com.example.ui.components.CategoryTabItem
import com.example.ui.components.FoodItemCard
import com.example.ui.theme.BrandError
import com.example.ui.theme.BrandPrimary
import com.example.ui.theme.BrandSuccess
import com.example.viewmodel.FastFoodUiState
import com.example.viewmodel.ScreenDestination

@Composable
fun CategoriesScreen(
    state: FastFoodUiState,
    onNavigate: (ScreenDestination) -> Unit,
    onSearchQueryChange: (String) -> Unit,
    onSelectCategory: (String) -> Unit,
    onToggleVegetarian: () -> Unit,
    onToggleSpicy: () -> Unit,
    onSetSortBy: (String) -> Unit,
    onToggleFavorite: (String) -> Unit,
    onQuickAddToCart: (FoodProduct) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("categories_screen")
    ) {
        // Top bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Explore Menu",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold)
                )
                Text(
                    text = "${state.filteredProducts.size} delicious items available",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            CartBadge(
                count = state.cartItemCount,
                onClick = { onNavigate(ScreenDestination.Cart) }
            )
        }

        // Search Input
        OutlinedTextField(
            value = state.searchQuery,
            onValueChange = onSearchQueryChange,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp)
                .testTag("categories_search_input"),
            placeholder = { Text("Search by meal or ingredient...") },
            leadingIcon = {
                Icon(Icons.Default.Search, contentDescription = "Search", tint = BrandPrimary)
            },
            trailingIcon = {
                if (state.searchQuery.isNotBlank()) {
                    IconButton(onClick = { onSearchQueryChange("") }) {
                        Icon(Icons.Default.Clear, contentDescription = "Clear")
                    }
                }
            },
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                focusedBorderColor = BrandPrimary,
                unfocusedBorderColor = MaterialTheme.colorScheme.surfaceVariant
            ),
            singleLine = true
        )

        // Categories tabs
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(state.categories) { category ->
                CategoryTabItem(
                    category = category,
                    isSelected = category.id == state.selectedCategoryId,
                    onClick = { onSelectCategory(category.id) }
                )
            }
        }

        // Filter chips (Vegetarian, Spicy, Sort)
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                FilterChip(
                    selected = state.isVegetarianFilter,
                    onClick = onToggleVegetarian,
                    label = { Text("Vegetarian") },
                    leadingIcon = {
                        Icon(
                            Icons.Default.Spa,
                            contentDescription = null,
                            tint = if (state.isVegetarianFilter) Color.White else BrandSuccess,
                            modifier = Modifier.size(16.dp)
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = BrandSuccess,
                        selectedLabelColor = Color.White
                    )
                )
            }

            item {
                FilterChip(
                    selected = state.isSpicyFilter,
                    onClick = onToggleSpicy,
                    label = { Text("Spicy") },
                    leadingIcon = {
                        Icon(
                            Icons.Default.LocalFireDepartment,
                            contentDescription = null,
                            tint = if (state.isSpicyFilter) Color.White else BrandError,
                            modifier = Modifier.size(16.dp)
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = BrandError,
                        selectedLabelColor = Color.White
                    )
                )
            }

            item {
                FilterChip(
                    selected = state.sortBy == "POPULAR",
                    onClick = { onSetSortBy("POPULAR") },
                    label = { Text("Popular") }
                )
            }

            item {
                FilterChip(
                    selected = state.sortBy == "PRICE_LOW",
                    onClick = { onSetSortBy("PRICE_LOW") },
                    label = { Text("Price: Low → High") }
                )
            }

            item {
                FilterChip(
                    selected = state.sortBy == "RATING",
                    onClick = { onSetSortBy("RATING") },
                    label = { Text("Top Rated") }
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Product Grid
        if (state.filteredProducts.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.Fastfood,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "No meals found",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Try adjusting your search keywords or filter options.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 90.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(state.filteredProducts, key = { it.id }) { product ->
                    FoodItemCard(
                        product = product,
                        isFavorite = state.favorites.contains(product.id),
                        onFavoriteClick = { onToggleFavorite(product.id) },
                        onProductClick = { onNavigate(ScreenDestination.ProductDetail(product)) },
                        onQuickAddClick = { onQuickAddToCart(product) }
                    )
                }
            }
        }
    }
}
