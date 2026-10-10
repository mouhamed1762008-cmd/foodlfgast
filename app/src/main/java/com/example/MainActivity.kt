package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.credentials.CredentialManager
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.navigation.MainAppNavigation
import com.example.ui.screens.AuthScreen
import com.example.ui.screens.signOutUser
import com.example.ui.theme.QuickBiteTheme
import com.example.viewmodel.FastFoodViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            QuickBiteAppRoot()
        }
    }
}

@Composable
fun QuickBiteAppRoot() {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val credentialManager = remember { CredentialManager.create(context) }

    var currentUser by remember { mutableStateOf(Firebase.auth.currentUser) }
    var isGuestMode by remember { mutableStateOf(false) }

    DisposableEffect(Unit) {
        val listener = FirebaseAuth.AuthStateListener { auth ->
            currentUser = auth.currentUser
            if (auth.currentUser != null) {
                isGuestMode = false
            }
        }
        Firebase.auth.addAuthStateListener(listener)
        onDispose {
            Firebase.auth.removeAuthStateListener(listener)
        }
    }

    if (currentUser == null && !isGuestMode) {
        QuickBiteTheme {
            Surface(modifier = Modifier.fillMaxSize()) {
                AuthScreen(
                    onAuthSuccess = {
                        currentUser = Firebase.auth.currentUser
                        isGuestMode = false
                    },
                    onContinueAsGuest = {
                        isGuestMode = true
                    }
                )
            }
        }
    } else {
        val viewModel: FastFoodViewModel = viewModel()
        val state by viewModel.uiState.collectAsStateWithLifecycle()

        QuickBiteTheme {
            Surface(modifier = Modifier.fillMaxSize()) {
                MainAppNavigation(
                    viewModel = viewModel,
                    state = state,
                    onSignOut = {
                        if (currentUser != null) {
                            signOutUser(
                                context = context,
                                credentialManager = credentialManager,
                                scope = coroutineScope,
                                onComplete = {
                                    currentUser = null
                                    isGuestMode = false
                                }
                            )
                        } else {
                            isGuestMode = false
                        }
                    }
                )
            }
        }
    }
}
