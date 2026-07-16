package com.example.find

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.find.navigation.FindNavGraph
import com.example.find.ui.auth.AuthViewModel
import com.example.find.ui.bench.BenchViewModel
import com.example.find.ui.map.LocationViewModel
import com.example.find.ui.theme.FindTheme

@Composable
fun FindApp() {
    val context = LocalContext.current
    val application = context.applicationContext as FindApplication

    val authViewModel: AuthViewModel = viewModel(
        factory = object : androidx.lifecycle.ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                return AuthViewModel(application.authRepository) as T
            }
        }
    )

    val benchViewModel: BenchViewModel = viewModel(
        factory = object : androidx.lifecycle.ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                return BenchViewModel(application.benchRepository) as T
            }
        }
    )

    val locationViewModel: LocationViewModel = viewModel()

    val session by application.sessionManager.sessionFlow
        .collectAsStateWithLifecycle(initialValue = null)

    LaunchedEffect(session) {
        authViewModel.setSession(session)
    }

    FindTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            FindNavGraph(
                authViewModel = authViewModel,
                benchViewModel = benchViewModel,
                locationViewModel = locationViewModel
            )
        }
    }
}
