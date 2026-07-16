package com.example.find.ui.map

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Login
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import com.example.find.ui.components.FindTopBar
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
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            FindTopBar(
                title = "Find",
                subtitle = if (session != null) session.email else "Bancos de jardim perto de ti",
                actions = {
                    IconButton(onClick = onNavigateToSearch) {
                        Icon(Icons.Default.Search, contentDescription = "Pesquisar")
                    }
                    if (session == null) {
                        IconButton(onClick = onNavigateToLogin) {
                            Icon(Icons.AutoMirrored.Filled.Login, contentDescription = "Entrar")
                        }
                    } else {
                        if (session.role == UserRole.ADMIN) {
                            IconButton(onClick = onNavigateToAdmin) {
                                Icon(Icons.Default.AdminPanelSettings, contentDescription = "Admin")
                            }
                        }
                        IconButton(onClick = onLogout) {
                            Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = "Sair")
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            if (session != null) {
                FloatingActionButton(
                    onClick = onNavigateToReport,
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    elevation = FloatingActionButtonDefaults.elevation(6.dp)
                ) {
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
                contentPadding = PaddingValues(
                    start = 8.dp,
                    top = 8.dp,
                    end = 8.dp,
                    bottom = if (session != null) 96.dp else 24.dp
                ),
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
                            title = "${bench.color} · ${bench.type.name}",
                            snippet = "${bench.widthMeters} m",
                            icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN),
                            onClick = {
                                onBenchClick(bench.id)
                                true
                            }
                        )
                    }
                }
            }

            Surface(
                shape = RoundedCornerShape(20.dp),
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.94f),
                tonalElevation = 2.dp,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(12.dp)
            ) {
                Text(
                    text = if (listState.benches.isEmpty() && !listState.isLoading) {
                        "Nenhum banco no mapa"
                    } else {
                        "${listState.benches.size} bancos no mapa"
                    },
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                )
            }

            if (listState.isLoading && listState.benches.isEmpty()) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(16.dp),
                    color = MaterialTheme.colorScheme.primary
                )
            }

            if (hasLocationPermission) {
                SmallFloatingActionButton(
                    onClick = {
                        locationViewModel.fetchCurrentLocation(context, forceRefresh = true)
                    },
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.primary,
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
