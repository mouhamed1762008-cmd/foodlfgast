package com.example.ui.screens

import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.PhoneAndroid
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import androidx.compose.material.icons.filled.Policy
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import com.example.ui.theme.BrandError
import com.example.ui.theme.BrandPrimary
import com.example.viewmodel.FastFoodUiState
import com.example.viewmodel.ScreenDestination

@Composable
fun ProfileScreen(
    state: FastFoodUiState,
    onNavigate: (ScreenDestination) -> Unit,
    onUpdateProfile: (String, String, String) -> Unit,
    onToggleDarkMode: () -> Unit,
    onSetLanguage: (String) -> Unit,
    onSetNotificationPrefs: (Boolean, Boolean) -> Unit,
    onSignOut: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showEditProfileDialog by remember { mutableStateOf(false) }
    var editName by remember { mutableStateOf(state.userProfile.fullName) }
    var editEmail by remember { mutableStateOf(state.userProfile.email) }
    var editPhone by remember { mutableStateOf(state.userProfile.phoneNumber) }

    var showLanguageDialog by remember { mutableStateOf(false) }
    var showHelpDialog by remember { mutableStateOf(false) }
    var showPolicyDialog by remember { mutableStateOf(false) }
    var showAboutDialog by remember { mutableStateOf(false) }
    var showDeleteAccountDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(bottom = 90.dp)
            .testTag("profile_screen")
    ) {
        // Top App Bar
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp)) {
            Text(
                text = "My Profile",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold)
            )
        }

        // User Avatar & Name Banner
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = CircleShape,
                    color = BrandPrimary,
                    modifier = Modifier.size(68.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_quickbite_logo),
                            contentDescription = "Profile Avatar",
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = state.userProfile.fullName.ifBlank { "QuickBite Member" },
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = state.userProfile.email.ifBlank { "customer@quickbite.com" },
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    if (state.userProfile.phoneNumber.isNotBlank()) {
                        Text(
                            text = state.userProfile.phoneNumber,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                IconButton(
                    onClick = {
                        editName = state.userProfile.fullName
                        editEmail = state.userProfile.email
                        editPhone = state.userProfile.phoneNumber
                        showEditProfileDialog = true
                    },
                    modifier = Modifier.testTag("edit_profile_button")
                ) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit Profile", tint = BrandPrimary)
                }
            }
        }

        val isGuest = Firebase.auth.currentUser == null
        if (isGuest) {
            Spacer(modifier = Modifier.height(14.dp))
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Browsing in Guest Mode 🍔",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimaryContainer)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Sign in with Google to sync orders, save addresses, and earn rewards across devices.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Button(
                        onClick = onSignOut,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary)
                    ) {
                        Text("Sign In with Google", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Account & Orders Section
        SectionHeader("Account & Preferences")

        SettingsItemCard(
            icon = Icons.Default.Receipt,
            title = "Order History & Digital Receipts",
            subtitle = "${state.orders.size} total orders placed",
            onClick = { onNavigate(ScreenDestination.OrderHistory) }
        )

        SettingsItemCard(
            icon = Icons.Default.LocationOn,
            title = "Saved Delivery Addresses",
            subtitle = "${state.savedAddresses.size} addresses saved",
            onClick = { onNavigate(ScreenDestination.Checkout) }
        )

        // Dark Mode Switch
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.DarkMode, contentDescription = null, tint = BrandPrimary, modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.width(14.dp))
                    Column {
                        Text("Dark Mode", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                        Text("Toggle app dark theme appearance", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
                Switch(
                    checked = state.isDarkMode,
                    onCheckedChange = { onToggleDarkMode() },
                    colors = SwitchDefaults.colors(checkedThumbColor = BrandPrimary, checkedTrackColor = BrandPrimary.copy(alpha = 0.4f))
                )
            }
        }

        // Notifications Switch
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Notifications, contentDescription = null, tint = BrandPrimary, modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.width(14.dp))
                    Column {
                        Text("Push Notifications", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                        Text("Order status & discount promotions", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
                Switch(
                    checked = state.orderNotificationEnabled,
                    onCheckedChange = { onSetNotificationPrefs(it, it) },
                    colors = SwitchDefaults.colors(checkedThumbColor = BrandPrimary, checkedTrackColor = BrandPrimary.copy(alpha = 0.4f))
                )
            }
        }

        // Language Selector
        SettingsItemCard(
            icon = Icons.Default.Language,
            title = "App Language",
            subtitle = when (state.selectedLanguage) {
                "ar" -> "Arabic (العربية - RTL)"
                "fr" -> "French (Français)"
                else -> "English (US)"
            },
            onClick = { showLanguageDialog = true }
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Support & Legal
        SectionHeader("Support & About")

        SettingsItemCard(
            icon = Icons.AutoMirrored.Filled.Help,
            title = "Help & Customer Support",
            subtitle = "FAQs, live courier support, contact",
            onClick = { showHelpDialog = true }
        )

        SettingsItemCard(
            icon = Icons.Default.Policy,
            title = "Privacy Policy & Terms of Service",
            subtitle = "Food safety, payment, data protection",
            onClick = { showPolicyDialog = true }
        )

        SettingsItemCard(
            icon = Icons.Default.PhoneAndroid,
            title = "App Compatibility",
            subtitle = "Requires Android 7.0 and up (API 24+) • v1.0",
            onClick = { showAboutDialog = true }
        )

        SettingsItemCard(
            icon = Icons.Default.AdminPanelSettings,
            title = "Kitchen Simulator Panel",
            subtitle = "Test real-time status transitions & catalog",
            onClick = { onNavigate(ScreenDestination.AdminPanel) }
        )

        Spacer(modifier = Modifier.height(18.dp))

        // Sign out & Delete Account Buttons
        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            Button(
                onClick = onSignOut,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("sign_out_button"),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null, tint = BrandPrimary, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isGuest) "Exit Guest Mode / Sign In" else "Sign Out",
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            TextButton(
                onClick = { showDeleteAccountDialog = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("delete_account_button")
            ) {
                Icon(Icons.Default.DeleteForever, contentDescription = null, tint = BrandError, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Delete Account & Data", color = BrandError)
            }
        }
    }

    // Edit Profile Dialog
    if (showEditProfileDialog) {
        AlertDialog(
            onDismissRequest = { showEditProfileDialog = false },
            title = { Text("Update Profile", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = editName,
                        onValueChange = { editName = it },
                        label = { Text("Full Name") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = editEmail,
                        onValueChange = { editEmail = it },
                        label = { Text("Email Address") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = editPhone,
                        onValueChange = { editPhone = it },
                        label = { Text("Phone Number") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onUpdateProfile(editName, editEmail, editPhone)
                        showEditProfileDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary)
                ) {
                    Text("Save Changes")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditProfileDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Language Dialog
    if (showLanguageDialog) {
        AlertDialog(
            onDismissRequest = { showLanguageDialog = false },
            title = { Text("Select Language", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    listOf("en" to "English (US)", "ar" to "العربية (Arabic - RTL)", "fr" to "Français (French)").forEach { (code, label) ->
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .clickable {
                                    onSetLanguage(code)
                                    showLanguageDialog = false
                                }
                                .padding(12.dp),
                            color = if (state.selectedLanguage == code) BrandPrimary.copy(alpha = 0.15f) else Color.Transparent
                        ) {
                            Text(
                                text = label,
                                fontWeight = if (state.selectedLanguage == code) FontWeight.Bold else FontWeight.Normal,
                                color = if (state.selectedLanguage == code) BrandPrimary else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showLanguageDialog = false }) {
                    Text("Close")
                }
            }
        )
    }

    // Help Dialog
    if (showHelpDialog) {
        AlertDialog(
            onDismissRequest = { showHelpDialog = false },
            title = { Text("QuickBite Support 🍔", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text("• Order Status Inquiries: Orders are updated in real time by the kitchen.")
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("• Cancellation Policy: Orders can be cancelled while in 'Placed' or 'Confirmed' stages.")
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("• Customer Hotline: 1-800-QUICK-BITE (Available 24/7)")
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("• Email Support: support@quickbite-fastfood.com")
                }
            },
            confirmButton = {
                Button(onClick = { showHelpDialog = false }, colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary)) {
                    Text("Got It")
                }
            }
        )
    }

    // Policy Dialog
    if (showPolicyDialog) {
        AlertDialog(
            onDismissRequest = { showPolicyDialog = false },
            title = { Text("Terms & Privacy Policy", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text("QuickBite adheres strictly to zero-trust customer privacy standards.")
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("1. Payment Security: Credit card credentials and CVVs are processed through secure gateways and are never stored in plain text.")
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("2. Location Data: Used solely during order delivery to calculate accurate driver routes.")
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("3. Allergen Awareness: Please review food ingredient badges prior to checkout.")
                }
            },
            confirmButton = {
                Button(onClick = { showPolicyDialog = false }, colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary)) {
                    Text("Accept")
                }
            }
        )
    }

    // App Compatibility Dialog
    if (showAboutDialog) {
        AlertDialog(
            onDismissRequest = { showAboutDialog = false },
            title = { Text("App Compatibility", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text("• Application: QuickBite v1.0", fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("• System Requirement: Requires Android 7.0 (Nougat, API 24) and up")
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("• Target System: Android 15 / 16 (API 36)")
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("Built with Jetpack Compose & Material 3 for fast and responsive fast food ordering.")
                }
            },
            confirmButton = {
                Button(
                    onClick = { showAboutDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary)
                ) {
                    Text("OK")
                }
            }
        )
    }

    // Delete Account Dialog
    if (showDeleteAccountDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteAccountDialog = false },
            title = { Text("Delete Account Permanently?", fontWeight = FontWeight.Bold, color = BrandError) },
            text = { Text("This will permanently remove your profile, saved addresses, and active orders. This cannot be undone.") },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteAccountDialog = false
                        onSignOut()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = BrandError)
                ) {
                    Text("Permanently Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteAccountDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelLarge.copy(
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        ),
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
    )
}

@Composable
fun SettingsItemCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                Icon(imageVector = icon, contentDescription = null, tint = BrandPrimary, modifier = Modifier.size(24.dp))
                Spacer(modifier = Modifier.width(14.dp))
                Column {
                    Text(text = title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                    Text(text = subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(18.dp))
        }
    }
}
