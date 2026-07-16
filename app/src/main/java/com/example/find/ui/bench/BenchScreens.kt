package com.example.find.ui.bench

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.find.data.model.BenchType
import com.example.find.data.model.UserSession
import com.example.find.ui.components.BenchResultCard
import com.example.find.ui.components.ErrorText
import com.example.find.ui.components.FindTopBar
import com.example.find.ui.components.InfoRow
import com.example.find.ui.components.SectionLabel
import com.example.find.ui.components.StarRatingRow
import com.example.find.ui.components.SuccessText
import com.example.find.ui.components.TypeFilterChip
import com.example.find.ui.components.benchStatusLabel
import com.example.find.ui.components.benchTypeLabel
import com.example.find.ui.map.LocationViewModel
import com.google.android.gms.maps.model.LatLng

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BenchDetailScreen(
    benchId: Long,
    benchViewModel: BenchViewModel,
    session: UserSession?,
    onNavigateBack: () -> Unit
) {
    val detailState by benchViewModel.detailState.collectAsState()
    var selectedStars by remember { mutableIntStateOf(0) }

    LaunchedEffect(benchId) {
        benchViewModel.loadBenchDetail(benchId)
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            FindTopBar(
                title = "Detalhe",
                subtitle = "Informação do banco",
                onBack = onNavigateBack
            )
        }
    ) { padding ->
        when {
            detailState.isLoading -> {
                Column(
                    modifier = Modifier.fillMaxSize().padding(padding),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
            }
            detailState.bench != null -> {
                val bench = detailState.bench!!
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = MaterialTheme.colorScheme.primaryContainer,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Text(
                                text = benchTypeLabel(bench.type),
                                style = MaterialTheme.typography.headlineMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = bench.color,
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = benchStatusLabel(bench.status.name),
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = MaterialTheme.colorScheme.surface,
                        tonalElevation = 1.dp,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            SectionLabel("Características")
                            InfoRow("Largura", "${bench.widthMeters} m")
                            InfoRow("Latitude", "%.5f".format(bench.latitude))
                            InfoRow("Longitude", "%.5f".format(bench.longitude))
                            bench.averageRating?.let {
                                InfoRow("Média", "★ %.1f (%d)".format(it, bench.ratingCount ?: 0))
                            }
                        }
                    }

                    if (session != null && bench.status.name == "APPROVED") {
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = MaterialTheme.colorScheme.surface,
                            tonalElevation = 1.dp,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                SectionLabel("A tua avaliação")
                                Text(
                                    text = "Toca nas estrelas e guarda",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                StarRatingRow(
                                    selectedStars = selectedStars,
                                    onSelect = { selectedStars = it }
                                )
                                Button(
                                    onClick = { benchViewModel.rateBench(benchId, selectedStars) },
                                    enabled = selectedStars > 0,
                                    modifier = Modifier.fillMaxWidth().height(48.dp),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text("Guardar avaliação")
                                }
                            }
                        }
                    } else if (session == null) {
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = MaterialTheme.colorScheme.secondaryContainer,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Entra na tua conta para avaliar este banco.",
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    }

                    if (detailState.ratings.isNotEmpty()) {
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = MaterialTheme.colorScheme.surface,
                            tonalElevation = 1.dp,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                SectionLabel("Avaliações")
                                detailState.ratings.forEach { rating ->
                                    InfoRow(rating.userEmail, "${rating.stars} ★")
                                }
                            }
                        }
                    }

                    ErrorText(detailState.error)
                    SuccessText(detailState.successMessage)
                }
            }
            else -> {
                ErrorText(
                    detailState.error ?: "Banco não encontrado",
                    modifier = Modifier.padding(padding).padding(16.dp)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ReportBenchScreen(
    benchViewModel: BenchViewModel,
    locationViewModel: LocationViewModel,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val detailState by benchViewModel.detailState.collectAsState()
    val locationState by locationViewModel.uiState.collectAsState()

    var selectedType by rememberSaveable { mutableStateOf(BenchType.WOOD) }
    var color by rememberSaveable { mutableStateOf("") }
    var width by rememberSaveable { mutableStateOf("1.8") }

    LaunchedEffect(Unit) {
        locationViewModel.fetchCurrentLocation(context)
    }

    val location: LatLng? = locationState.currentLocation

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            FindTopBar(
                title = "Reportar",
                subtitle = "Novo banco no mapa",
                onBack = onNavigateBack
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.secondaryContainer,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    SectionLabel("Localização GPS")
                    Text(
                        text = if (location != null) {
                            "%.5f, %.5f".format(location.latitude, location.longitude)
                        } else {
                            "A obter localização..."
                        },
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }

            SectionLabel("Tipo de banco")
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                BenchType.entries.forEach { type ->
                    TypeFilterChip(
                        type = type,
                        selected = selectedType == type,
                        onClick = { selectedType = type }
                    )
                }
            }

            OutlinedTextField(
                value = color,
                onValueChange = { color = it },
                label = { Text("Cor") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp)
            )
            OutlinedTextField(
                value = width,
                onValueChange = { width = it },
                label = { Text("Largura (m)") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp)
            )

            ErrorText(detailState.error)
            SuccessText(detailState.successMessage)

            Button(
                onClick = {
                    val lat = location?.latitude ?: return@Button
                    val lng = location?.longitude ?: return@Button
                    val widthValue = width.toDoubleOrNull() ?: return@Button
                    benchViewModel.reportBench(lat, lng, selectedType, color, widthValue) {
                        onNavigateBack()
                    }
                },
                enabled = location != null && color.isNotBlank() &&
                    width.toDoubleOrNull() != null && !detailState.isLoading,
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text(if (detailState.isLoading) "A enviar..." else "Reportar banco")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun SearchScreen(
    benchViewModel: BenchViewModel,
    onBenchClick: (Long) -> Unit,
    onNavigateBack: () -> Unit
) {
    val listState by benchViewModel.listState.collectAsState()
    var color by rememberSaveable { mutableStateOf("") }
    var minWidth by rememberSaveable { mutableStateOf("") }
    var maxWidth by rememberSaveable { mutableStateOf("") }
    var selectedType by rememberSaveable { mutableStateOf<BenchType?>(null) }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            FindTopBar(
                title = "Pesquisar",
                subtitle = "Filtra por características",
                onBack = onNavigateBack
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            OutlinedTextField(
                value = color,
                onValueChange = { color = it },
                label = { Text("Cor") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp)
            )
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = minWidth,
                    onValueChange = { minWidth = it },
                    label = { Text("Larg. mín.") },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(14.dp)
                )
                OutlinedTextField(
                    value = maxWidth,
                    onValueChange = { maxWidth = it },
                    label = { Text("Larg. máx.") },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(14.dp)
                )
            }

            SectionLabel("Tipo")
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                BenchType.entries.forEach { type ->
                    TypeFilterChip(
                        type = type,
                        selected = selectedType == type,
                        onClick = { selectedType = if (selectedType == type) null else type }
                    )
                }
            }

            Button(
                onClick = {
                    benchViewModel.loadBenches(
                        type = selectedType,
                        color = color.ifBlank { null },
                        minWidth = minWidth.toDoubleOrNull(),
                        maxWidth = maxWidth.toDoubleOrNull()
                    )
                },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text("Pesquisar")
            }

            ErrorText(listState.error)

            listState.benches.forEach { bench ->
                BenchResultCard(
                    bench = bench,
                    onClick = { onBenchClick(bench.id) }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminScreen(
    benchViewModel: BenchViewModel,
    onNavigateBack: () -> Unit
) {
    val listState by benchViewModel.listState.collectAsState()

    LaunchedEffect(Unit) {
        benchViewModel.loadPending()
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            FindTopBar(
                title = "Admin",
                subtitle = "Aprovar bancos reportados",
                onBack = onNavigateBack
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            when {
                listState.isLoading -> CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
                listState.benches.isEmpty() -> {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = MaterialTheme.colorScheme.secondaryContainer,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Nenhum banco pendente.",
                            modifier = Modifier.padding(20.dp),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
                else -> {
                    listState.benches.forEach { bench ->
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = MaterialTheme.colorScheme.surface,
                            tonalElevation = 1.dp,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = "${benchTypeLabel(bench.type)} · ${bench.color}",
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Text(
                                    text = "%.5f, %.5f · ${bench.widthMeters} m".format(
                                        bench.latitude,
                                        bench.longitude
                                    ),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Button(
                                        onClick = { benchViewModel.approveBench(bench.id) },
                                        modifier = Modifier.weight(1f),
                                        shape = RoundedCornerShape(12.dp)
                                    ) {
                                        Text("Aprovar")
                                    }
                                    OutlinedButton(
                                        onClick = { benchViewModel.rejectBench(bench.id) },
                                        modifier = Modifier.weight(1f),
                                        shape = RoundedCornerShape(12.dp),
                                        colors = ButtonDefaults.outlinedButtonColors(
                                            contentColor = MaterialTheme.colorScheme.error
                                        )
                                    ) {
                                        Text("Rejeitar")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
