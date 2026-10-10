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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.model.Order
import com.example.model.OrderStatus
import com.example.ui.components.formatCents
import com.example.ui.theme.BrandError
import com.example.ui.theme.BrandPrimary
import com.example.ui.theme.BrandSuccess
import com.example.viewmodel.FastFoodUiState
import com.example.viewmodel.ScreenDestination

@Composable
fun OrderHistoryScreen(
    state: FastFoodUiState,
    onTrackOrder: (String) -> Unit,
    onReorder: (Order) -> Unit,
    onBrowseMenu: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedFilter by remember { mutableStateOf("ALL") }
    var selectedReceiptOrder by remember { mutableStateOf<Order?>(null) }

    val filteredOrders = state.orders.filter { order ->
        when (selectedFilter) {
            "ACTIVE" -> order.orderStatus != "DELIVERED" && order.orderStatus != "CANCELLED"
            "COMPLETED" -> order.orderStatus == "DELIVERED"
            "CANCELLED" -> order.orderStatus == "CANCELLED"
            else -> true
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("order_history_screen")
    ) {
        // Top App Bar
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp)) {
            Text(
                text = "Order History",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold)
            )
            Text(
                text = "Track current orders and view receipts",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Filter Tabs
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                FilterChip(
                    selected = selectedFilter == "ALL",
                    onClick = { selectedFilter = "ALL" },
                    label = { Text("All Orders (${state.orders.size})") }
                )
            }
            item {
                FilterChip(
                    selected = selectedFilter == "ACTIVE",
                    onClick = { selectedFilter = "ACTIVE" },
                    label = { Text("Active") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = BrandPrimary,
                        selectedLabelColor = Color.White
                    )
                )
            }
            item {
                FilterChip(
                    selected = selectedFilter == "COMPLETED",
                    onClick = { selectedFilter = "COMPLETED" },
                    label = { Text("Completed") }
                )
            }
            item {
                FilterChip(
                    selected = selectedFilter == "CANCELLED",
                    onClick = { selectedFilter = "CANCELLED" },
                    label = { Text("Cancelled") }
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (filteredOrders.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier.size(80.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Receipt,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(40.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "No Orders in this Category",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Your completed orders and receipts will appear here.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = onBrowseMenu,
                        colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text("Start an Order")
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 90.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                items(filteredOrders, key = { it.id }) { order ->
                    OrderHistoryCard(
                        order = order,
                        onTrack = { onTrackOrder(order.id) },
                        onReorder = { onReorder(order) },
                        onViewReceipt = { selectedReceiptOrder = order }
                    )
                }
            }
        }

        // Digital Receipt Dialog
        if (selectedReceiptOrder != null) {
            val rec = selectedReceiptOrder!!
            AlertDialog(
                onDismissRequest = { selectedReceiptOrder = null },
                title = { Text("Digital Receipt #${rec.id}", fontWeight = FontWeight.Bold) },
                text = {
                    Column {
                        Text("Payment Method: ${rec.paymentMethod}")
                        Text("Delivery to: ${rec.deliveryAddress}")
                        Spacer(modifier = Modifier.height(10.dp))
                        HorizontalDivider()
                        Spacer(modifier = Modifier.height(8.dp))
                        rec.items.forEach { item ->
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("${item.quantity}x ${item.productName}", style = MaterialTheme.typography.bodySmall)
                                Text(formatCents(item.priceCents * item.quantity), style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold))
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        HorizontalDivider()
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Total Paid", fontWeight = FontWeight.Bold)
                            Text(formatCents(rec.total), fontWeight = FontWeight.ExtraBold, color = BrandPrimary)
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = { selectedReceiptOrder = null },
                        colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary)
                    ) {
                        Text("Close")
                    }
                }
            )
        }
    }
}

@Composable
fun OrderHistoryCard(
    order: Order,
    onTrack: () -> Unit,
    onReorder: () -> Unit,
    onViewReceipt: () -> Unit,
    modifier: Modifier = Modifier
) {
    val status = order.statusEnum
    val isActive = status != OrderStatus.DELIVERED && status != OrderStatus.CANCELLED

    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Order #${order.id}",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.ExtraBold)
                    )
                    Text(
                        text = "${order.items.size} items • ${formatCents(order.total)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = when (status) {
                        OrderStatus.DELIVERED -> BrandSuccess.copy(alpha = 0.15f)
                        OrderStatus.CANCELLED -> BrandError.copy(alpha = 0.15f)
                        else -> BrandPrimary.copy(alpha = 0.15f)
                    }
                ) {
                    Text(
                        text = status.title,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = when (status) {
                                OrderStatus.DELIVERED -> BrandSuccess
                                OrderStatus.CANCELLED -> BrandError
                                else -> BrandPrimary
                            }
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Items preview text
            Text(
                text = order.items.joinToString(", ") { "${it.quantity}x ${it.productName}" },
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(onClick = onViewReceipt) {
                    Icon(Icons.Default.Receipt, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Receipt")
                }

                Spacer(modifier = Modifier.width(6.dp))

                if (isActive) {
                    Button(
                        onClick = onTrack,
                        colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Track Live")
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(16.dp))
                    }
                } else {
                    OutlinedButton(
                        onClick = onReorder,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Replay, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Reorder")
                    }
                }
            }
        }
    }
}
