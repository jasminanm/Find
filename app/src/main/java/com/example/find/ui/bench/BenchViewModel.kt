package com.example.find.ui.bench

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.find.data.model.Bench
import com.example.find.data.model.BenchType
import com.example.find.data.model.Rating
import com.example.find.data.repository.BenchRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

data class BenchListUiState(
    val isLoading: Boolean = false,
    val benches: List<Bench> = emptyList(),
    val error: String? = null
)

data class BenchDetailUiState(
    val isLoading: Boolean = false,
    val bench: Bench? = null,
    val ratings: List<Rating> = emptyList(),
    val error: String? = null,
    val successMessage: String? = null
)

class BenchViewModel(private val benchRepository: BenchRepository) : ViewModel() {

    private val _listState = MutableStateFlow(BenchListUiState())
    val listState: StateFlow<BenchListUiState> = _listState.asStateFlow()

    private val _detailState = MutableStateFlow(BenchDetailUiState())
    val detailState: StateFlow<BenchDetailUiState> = _detailState.asStateFlow()

    fun loadBenchesIfNeeded() {
        if (_listState.value.isLoading || _listState.value.benches.isNotEmpty()) return
        loadBenches()
    }

    fun loadBenches(
        type: BenchType? = null,
        color: String? = null,
        minWidth: Double? = null,
        maxWidth: Double? = null
    ) {
        if (_listState.value.isLoading) return
        viewModelScope.launch {
            _listState.value = _listState.value.copy(isLoading = true, error = null)
            benchRepository.search(type, color, minWidth, maxWidth)
                .onSuccess { benches ->
                    _listState.value = BenchListUiState(benches = benches)
                }
                .onFailure { error ->
                    _listState.value = _listState.value.copy(
                        isLoading = false,
                        error = error.message
                    )
                }
        }
    }

    fun loadBenchDetail(benchId: Long) {
        viewModelScope.launch {
            _detailState.value = BenchDetailUiState(isLoading = true)
            coroutineScope {
                val benchDeferred = async { benchRepository.getById(benchId) }
                val ratingsDeferred = async { benchRepository.getRatings(benchId) }
                val benchResult = benchDeferred.await()
                val ratingsResult = ratingsDeferred.await()

                benchResult
                    .onSuccess { bench ->
                        val ratings = ratingsResult.getOrElse { emptyList() }
                        _detailState.value = BenchDetailUiState(bench = bench, ratings = ratings)
                    }
                    .onFailure { error ->
                        _detailState.value = BenchDetailUiState(error = error.message)
                    }
            }
        }
    }

    fun reportBench(
        latitude: Double,
        longitude: Double,
        type: BenchType,
        color: String,
        widthMeters: Double,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            _detailState.value = _detailState.value.copy(isLoading = true, error = null)
            benchRepository.report(latitude, longitude, type, color, widthMeters)
                .onSuccess {
                    _detailState.value = _detailState.value.copy(
                        isLoading = false,
                        successMessage = "Banco reportado com sucesso. Aguarda aprovação."
                    )
                    onSuccess()
                }
                .onFailure { error ->
                    _detailState.value = _detailState.value.copy(
                        isLoading = false,
                        error = error.message
                    )
                }
        }
    }

    fun rateBench(benchId: Long, stars: Int) {
        viewModelScope.launch {
            benchRepository.rate(benchId, stars)
                .onSuccess {
                    loadBenchDetail(benchId)
                    _detailState.value = _detailState.value.copy(successMessage = "Avaliação guardada")
                }
                .onFailure { error ->
                    _detailState.value = _detailState.value.copy(error = error.message)
                }
        }
    }

    fun loadPending() {
        viewModelScope.launch {
            _listState.value = BenchListUiState(isLoading = true)
            benchRepository.getPending()
                .onSuccess { benches ->
                    _listState.value = BenchListUiState(benches = benches)
                }
                .onFailure { error ->
                    _listState.value = BenchListUiState(error = error.message)
                }
        }
    }

    fun approveBench(id: Long) {
        viewModelScope.launch {
            benchRepository.approve(id).onSuccess { loadPending() }
        }
    }

    fun rejectBench(id: Long) {
        viewModelScope.launch {
            benchRepository.reject(id).onSuccess { loadPending() }
        }
    }

    fun clearMessages() {
        _detailState.value = _detailState.value.copy(error = null, successMessage = null)
    }
}
