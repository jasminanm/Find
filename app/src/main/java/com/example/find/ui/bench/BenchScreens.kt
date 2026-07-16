package com.example.find.ui.bench

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.find.data.model.BenchType
import com.example.find.data.model.UserSession
import com.example.find.ui.components.ErrorText
import com.example.find.ui.components.SuccessText
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
        topBar = {
            TopAppBar(
                title = { Text("Detalhe do Banco") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Voltar")
                    }
                }
            )
        }
    ) { padding ->
        when {
            detailState.isLoading -> {
                Column(
                    modifier = Modifier.fillMaxSize().padding(padding),
                    verticalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator(modifier = Modifier.padding(24.dp))
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
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("Tipo: ${benchTypeLabel(bench.type)}", style = MaterialTheme.typography.titleMedium)
                    Text("Cor: ${bench.color}")
                    Text("Largura: ${bench.widthMeters} m")
                    Text("Estado: ${bench.status}")
                    Text("Coordenadas: ${bench.latitude}, ${bench.longitude}")
                    bench.averageRating?.let {
                        Text("Média: ${"%.1f".format(it)} (${bench.ratingCount ?: 0} avaliações)")
                    }

                    if (session != null && bench.status.name == "APPROVED") {
                        Text("A tua avaliação:", style = MaterialTheme.typography.titleSmall)
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            (1..5).forEach { star ->
                                IconButton(onClick = { selectedStars = star }) {
                                    Icon(
                                        imageVector = if (star <= selectedStars) Icons.Filled.Star else Icons.Outlined.Star,
                                        contentDescription = "$star estrelas"
                                    )
                                }
                            }
                        }
                        Button(
                            onClick = { benchViewModel.rateBench(benchId, selectedStars) },
                            enabled = selectedStars > 0
                        ) {
                            Text("Guardar avaliação")
                        }
                    }

                    if (detailState.ratings.isNotEmpty()) {
                        Text("Avaliações", style = MaterialTheme.typography.titleSmall)
                        detailState.ratings.forEach { rating ->
                            Text("${rating.userEmail}: ${rating.stars} ★")
                        }
                    }

                    ErrorText(detailState.error)
                    SuccessText(detailState.successMessage)
                }
            }
            else -> {
                ErrorText(detailState.error ?: "Banco não encontrado", modifier = Modifier.padding(padding).padding(16.dp))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportBenchScreen(
    benchViewModel: BenchViewModel,
    locationViewModel: LocationViewModel,
    onNavigateBack: () -> Unit
) {
    val context = androidx.compose.ui.platform.LocalContext.current
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
        topBar = {
            TopAppBar(
                title = { Text("Reportar Banco") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Voltar")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Localização: ${location?.latitude ?: "..."}, ${location?.longitude ?: "..."}")

            BenchType.entries.forEach { type ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(benchTypeLabel(type))
                    Button(
                        onClick = { selectedType = type },
                        enabled = selectedType != type
                    ) {
                        Text(if (selectedType == type) "Selecionado" else "Selecionar")
                    }
                }
            }

            OutlinedTextField(
                value = color,
                onValueChange = { color = it },
                label = { Text("Cor") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = width,
                onValueChange = { width = it },
                label = { Text("Largura (m)") },
                modifier = Modifier.fillMaxWidth()
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
                enabled = location != null && color.isNotBlank() && width.toDoubleOrNull() != null && !detailState.isLoading,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (detailState.isLoading) "A enviar..." else "Reportar banco")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
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
        topBar = {
            TopAppBar(
                title = { Text("Pesquisar Bancos") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Voltar")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = color,
                onValueChange = { color = it },
                label = { Text("Cor") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = minWidth,
                onValueChange = { minWidth = it },
                label = { Text("Largura mínima (m)") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = maxWidth,
                onValueChange = { maxWidth = it },
                label = { Text("Largura máxima (m)") },
                modifier = Modifier.fillMaxWidth()
            )

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                BenchType.entries.forEach { type ->
                    Button(onClick = { selectedType = if (selectedType == type) null else type }) {
                        Text(benchTypeLabel(type))
                    }
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
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Pesquisar")
            }

            ErrorText(listState.error)

            listState.benches.forEach { bench ->
                Button(
                    onClick = { onBenchClick(bench.id) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("${benchTypeLabel(bench.type)} - ${bench.color} (${bench.widthMeters}m)")
                }
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
        topBar = {
            TopAppBar(
                title = { Text("Aprovação de Bancos") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Voltar")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (listState.isLoading) {
                CircularProgressIndicator()
            } else if (listState.benches.isEmpty()) {
                Text("Nenhum banco pendente.")
            } else {
                listState.benches.forEach { bench ->
                    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
                        Text("${benchTypeLabel(bench.type)} - ${bench.color}")
                        Text("${bench.latitude}, ${bench.longitude}")
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(onClick = { benchViewModel.approveBench(bench.id) }) {
                                Text("Aprovar")
                            }
                            Button(onClick = { benchViewModel.rejectBench(bench.id) }) {
                                Text("Rejeitar")
                            }
                        }
                    }
                }
            }
        }
    }
}
