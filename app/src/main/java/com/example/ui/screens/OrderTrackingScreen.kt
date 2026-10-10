package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.DeliveryDining
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.TwoWheeler
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Order
import com.example.model.OrderStatus
import com.example.ui.components.OrderStatusTimeline
import com.example.ui.components.formatCents
import com.example.ui.theme.BrandAccent
import com.example.ui.theme.BrandError
import com.example.ui.theme.BrandPrimary
import com.example.ui.theme.BrandSuccess

@Composable
fun OrderTrackingScreen(
    order: Order,
    onBack: () -> Unit,
    onCancelOrder: (String) -> Unit,
    onUpdateStatus: (String, OrderStatus) -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onBack() }

    var showCancelDialog by remember { mutableStateOf(false) }
    var showCallDialog by remember { mutableStateOf(false) }
    var showSimulatorDialog by remember { mutableStateOf(false) }

    val currentStatus = order.statusEnum
    val canCancel = currentStatus == OrderStatus.PLACED || currentStatus == OrderStatus.CONFIRMED

    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("order_tracking_screen")
    ) {
        // Top App Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack, modifier = Modifier.testTag("tracking_back_button")) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
                Spacer(modifier = Modifier.width(6.dp))
                Column {
                    Text(
                        text = "Live Order Tracking",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = "Order #${order.id}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Quick live simulator button
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = BrandAccent.copy(alpha = 0.2f),
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { showSimulatorDialog = true }
                    .testTag("status_simulator_button")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = null, tint = BrandPrimary, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Test Status", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = BrandPrimary))
                }
            }
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Live Status Card & ETA Banner
            Card(
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = when (currentStatus) {
                                OrderStatus.DELIVERED -> BrandSuccess
                                OrderStatus.CANCELLED -> BrandError
                                else -> BrandPrimary
                            }
                        ) {
                            Text(
                                text = currentStatus.title.uppercase(),
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color.White
                                )
                            )
                        }

                        if (currentStatus != OrderStatus.DELIVERED && currentStatus != OrderStatus.CANCELLED) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(shape = CircleShape, color = BrandSuccess, modifier = Modifier.size(8.dp)) {}
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "LIVE UPDATES",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = BrandSuccess
                                    )
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    if (currentStatus != OrderStatus.DELIVERED && currentStatus != OrderStatus.CANCELLED) {
                        Text(
                            text = "Estimated Arrival",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "20 - 25 minutes",
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.ExtraBold,
                                color = BrandPrimary
                            )
                        )
                    } else if (currentStatus == OrderStatus.DELIVERED) {
                        Text(
                            text = "Your food has arrived! 🎉",
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = BrandSuccess
                            )
                        )
                        Text(
                            text = "Thank you for choosing QuickBite. Enjoy your meal!",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Simulated live route visual
                    SimulatedDeliveryRouteVisual(currentStatus = currentStatus)
                }
            }

            // Driver Card (Active when Out for delivery or Preparing)
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = BrandPrimary.copy(alpha = 0.15f),
                            modifier = Modifier.size(50.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.TwoWheeler,
                                    contentDescription = null,
                                    tint = BrandPrimary,
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Text(
                                text = order.driverName,
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = order.driverVehicle,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "Rating: 4.9 ★ (1,240 deliveries)",
                                style = MaterialTheme.typography.labelSmall.copy(color = BrandSuccess)
                            )
                        }
                    }

                    Row {
                        Surface(
                            shape = CircleShape,
                            color = BrandPrimary,
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .clickable { showCallDialog = true }
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.Call, contentDescription = "Call driver", tint = Color.White, modifier = Modifier.size(20.dp))
                            }
                        }
                    }
                }
            }

            // Order Status Timeline
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "Order Progress Timeline",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    OrderStatusTimeline(currentStatus = currentStatus)
                }
            }

            // Delivery Details & Items Card
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "Delivery Destination",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.LocationOn, contentDescription = null, tint = BrandPrimary, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = order.deliveryAddress, style = MaterialTheme.typography.bodyMedium)
                    }

                    Divider(modifier = Modifier.padding(vertical = 12.dp))

                    Text(
                        text = "Order Items (${order.items.size})",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    order.items.forEach { item ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "${item.quantity}x ${item.productName}",
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
                                )
                                if (item.customizationSummary.isNotBlank()) {
                                    Text(
                                        text = item.customizationSummary,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                            Text(
                                text = formatCents(item.priceCents * item.quantity),
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                            )
                        }
                    }

                    Divider(modifier = Modifier.padding(vertical = 10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Total Paid (${order.paymentMethod})", fontWeight = FontWeight.Bold)
                        Text(
                            text = formatCents(order.total),
                            fontWeight = FontWeight.ExtraBold,
                            color = BrandPrimary,
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }
            }

            // Cancel Order Button
            if (canCancel) {
                OutlinedButton(
                    onClick = { showCancelDialog = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("cancel_order_button"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = BrandError)
                ) {
                    Icon(Icons.Default.Cancel, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Cancel Order", fontWeight = FontWeight.Bold)
                }
            }
        }

        // Cancel confirmation dialog
        if (showCancelDialog) {
            AlertDialog(
                onDismissRequest = { showCancelDialog = false },
                title = { Text("Cancel Order #${order.id}?", fontWeight = FontWeight.Bold) },
                text = { Text("Are you sure you want to cancel? This action cannot be undone.") },
                confirmButton = {
                    Button(
                        onClick = {
                            onCancelOrder(order.id)
                            showCancelDialog = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = BrandError)
                    ) {
                        Text("Yes, Cancel Order")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showCancelDialog = false }) {
                        Text("Keep Order")
                    }
                }
            )
        }

        // Call driver dialog
        if (showCallDialog) {
            AlertDialog(
                onDismissRequest = { showCallDialog = false },
                title = { Text("Contact Courier Driver", fontWeight = FontWeight.Bold) },
                text = {
                    Column {
                        Text("Driver: ${order.driverName}")
                        Text("Phone: ${order.driverPhone}")
                        Text("Vehicle: ${order.driverVehicle}")
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Courier is currently following the fastest route to your location.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = { showCallDialog = false },
                        colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary)
                    ) {
                        Text("Close")
                    }
                }
            )
        }

        // Live Order Status Simulator Dialog (Testing Tool)
        if (showSimulatorDialog) {
            AlertDialog(
                onDismissRequest = { showSimulatorDialog = false },
                title = { Text("Live Status Simulator", fontWeight = FontWeight.Bold) },
                text = {
                    Column {
                        Text("Select a lifecycle stage to simulate real-time updates:")
                        Spacer(modifier = Modifier.height(10.dp))
                        OrderStatus.values().forEach { status ->
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 3.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable {
                                        onUpdateStatus(order.id, status)
                                        showSimulatorDialog = false
                                    },
                                color = if (status == currentStatus) BrandPrimary.copy(alpha = 0.15f) else Color.Transparent
                            ) {
                                Text(
                                    text = status.title,
                                    modifier = Modifier.padding(10.dp),
                                    fontWeight = if (status == currentStatus) FontWeight.Bold else FontWeight.Normal,
                                    color = if (status == currentStatus) BrandPrimary else MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showSimulatorDialog = false }) {
                        Text("Done")
                    }
                }
            )
        }
    }
}

@Composable
fun SimulatedDeliveryRouteVisual(
    currentStatus: OrderStatus,
    modifier: Modifier = Modifier
) {
    val progress = when (currentStatus) {
        OrderStatus.PLACED -> 0.05f
        OrderStatus.CONFIRMED -> 0.20f
        OrderStatus.PREPARING -> 0.45f
        OrderStatus.READY_FOR_PICKUP -> 0.65f
        OrderStatus.OUT_FOR_DELIVERY -> 0.85f
        OrderStatus.DELIVERED -> 1.0f
        OrderStatus.CANCELLED -> 0.0f
    }

    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = 800),
        label = "route_progress"
    )

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(110.dp),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
    ) {
        Box(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            // Background Road Canvas
            Canvas(modifier = Modifier.fillMaxSize()) {
                val startX = 30f
                val endX = size.width - 30f
                val roadY = size.height / 2

                // Road base line
                drawLine(
                    color = Color.LightGray,
                    start = Offset(startX, roadY),
                    end = Offset(endX, roadY),
                    strokeWidth = 10f,
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(15f, 10f), 0f)
                )

                // Completed route in BrandPrimary
                if (animatedProgress > 0) {
                    val currentX = startX + (endX - startX) * animatedProgress
                    drawLine(
                        color = Color(0xFFFF6B35),
                        start = Offset(startX, roadY),
                        end = Offset(currentX, roadY),
                        strokeWidth = 10f
                    )
                }
            }

            // Left Restaurant Icon
            Surface(
                shape = CircleShape,
                color = BrandPrimary,
                modifier = Modifier
                    .size(36.dp)
                    .align(Alignment.CenterStart)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.Restaurant, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                }
            }

            // Right Destination House Icon
            Surface(
                shape = CircleShape,
                color = if (currentStatus == OrderStatus.DELIVERED) BrandSuccess else Color.Gray,
                modifier = Modifier
                    .size(36.dp)
                    .align(Alignment.CenterEnd)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.Home, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                }
            }

            // Center moving courier icon
            if (currentStatus != OrderStatus.DELIVERED && currentStatus != OrderStatus.CANCELLED) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 24.dp)
                ) {
                    Surface(
                        shape = CircleShape,
                        color = BrandAccent,
                        shadowElevation = 4.dp,
                        modifier = Modifier
                            .size(34.dp)
                            .align(
                                when {
                                    animatedProgress < 0.35f -> Alignment.CenterStart
                                    animatedProgress < 0.70f -> Alignment.Center
                                    else -> Alignment.CenterEnd
                                }
                            )
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.DeliveryDining, contentDescription = null, tint = Color.Black, modifier = Modifier.size(20.dp))
                        }
                    }
                }
            }
        }
    }
}
