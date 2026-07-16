package com.example.find.ui.map

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Login
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.find.data.model.UserRole
import com.example.find.data.model.UserSession
import com.example.find.ui.auth.AuthViewModel
import com.example.find.ui.bench.BenchViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    benchViewModel: BenchViewModel,
    locationViewModel: LocationViewModel,
    authViewModel: AuthViewModel,
    session: UserSession?,
    onBenchClick: (Long) -> Unit,
    onNavigateToSearch: () -> Unit,
    onNavigateToLogin: () -> Unit,
    onNavigateToReport: () -> Unit,
    onNavigateToAdmin: () -> Unit,
    onLogout: () -> Unit
) {
    val context = LocalContext.current
    val listState by benchViewModel.listState.collectAsState()
    val locationState by locationViewModel.uiState.collectAsState()

    var hasLocationPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        hasLocationPermission = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true
        if (hasLocationPermission) {
            locationViewModel.fetchCurrentLocation(context)
        }
    }

    LaunchedEffect(Unit) {
        benchViewModel.loadBenchesIfNeeded()
        if (hasLocationPermission) {
            locationViewModel.fetchCurrentLocation(context)
        } else {
            permissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    val defaultLocation = LatLng(38.7223, -9.1393)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(defaultLocation, 14f)
    }

    LaunchedEffect(locationState.shouldCenterCamera, locationState.currentLocation) {
        val location = locationState.currentLocation
        if (location != null && locationState.shouldCenterCamera) {
            val zoom = if ((locationState.accuracyMeters ?: 50f) <= 20f) 17f else 15f
            cameraPositionState.animate(CameraUpdateFactory.newLatLngZoom(location, zoom))
            locationViewModel.onCameraCentered()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Find - Bancos de Jardim") },
                actions = {
                    IconButton(onClick = onNavigateToSearch) {
                        Icon(Icons.Default.Search, contentDescription = "Pesquisar")
                    }
                    if (session == null) {
                        IconButton(onClick = onNavigateToLogin) {
                            Icon(Icons.Default.Login, contentDescription = "Entrar")
                        }
                    } else {
                        if (session.role == UserRole.ADMIN) {
                            IconButton(onClick = onNavigateToAdmin) {
                                Icon(Icons.Default.AdminPanelSettings, contentDescription = "Admin")
                            }
                        }
                        IconButton(onClick = onLogout) {
                            Icon(Icons.Default.Logout, contentDescription = "Sair")
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            if (session != null) {
                FloatingActionButton(onClick = onNavigateToReport) {
                    Icon(Icons.Default.Add, contentDescription = "Reportar banco")
                }
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                properties = MapProperties(isMyLocationEnabled = hasLocationPermission),
                uiSettings = MapUiSettings(
                    myLocationButtonEnabled = false,
                    zoomControlsEnabled = true
                )
            ) {
                listState.benches.forEach { bench ->
                    key(bench.id) {
                        val position = remember(bench.id) {
                            LatLng(bench.latitude, bench.longitude)
                        }
                        Marker(
                            state = rememberMarkerState(position = position),
                            title = "${bench.color} - ${bench.type.name}",
                            snippet = "${bench.widthMeters}m",
                            icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN),
                            onClick = {
                                onBenchClick(bench.id)
                                true
                            }
                        )
                    }
                }
            }

            if (listState.isLoading && listState.benches.isEmpty()) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(16.dp)
                )
            }

            if (hasLocationPermission) {
                FloatingActionButton(
                    onClick = {
                        locationViewModel.fetchCurrentLocation(context, forceRefresh = true)
                    },
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(16.dp)
                ) {
                    Icon(Icons.Default.MyLocation, contentDescription = "Minha localização")
                }
            }
        }
    }
}
