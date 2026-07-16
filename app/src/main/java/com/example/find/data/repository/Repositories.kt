package com.example.find.data.repository

import com.example.find.data.api.ApiClient
import com.example.find.data.api.BenchRequest
import com.example.find.data.api.LoginRequest
import com.example.find.data.api.RatingRequest
import com.example.find.data.api.RegisterRequest
import com.example.find.data.local.SessionManager
import com.example.find.data.model.AuthResponse
import com.example.find.data.model.Bench
import com.example.find.data.model.BenchType
import com.example.find.data.model.Rating
import com.example.find.data.model.UserSession
import kotlinx.coroutines.flow.first
import retrofit2.HttpException

class AuthRepository(private val sessionManager: SessionManager) {

    suspend fun login(email: String, password: String): Result<UserSession> = runCatching {
        val response = ApiClient.api.login(LoginRequest(email, password))
        saveAndReturn(response)
    }.recoverCatching { throw mapException(it) }

    suspend fun register(email: String, password: String): Result<UserSession> = runCatching {
        val response = ApiClient.api.register(RegisterRequest(email, password))
        saveAndReturn(response)
    }.recoverCatching { throw mapException(it) }

    suspend fun logout() {
        sessionManager.clearSession()
    }

    suspend fun restoreSession() {
        val session = sessionManager.sessionFlow.first()
        session?.let { sessionManager.restoreToken(it.token) }
    }

    private suspend fun saveAndReturn(response: AuthResponse): UserSession {
        val session = UserSession(
            token = response.token,
            userId = response.userId,
            email = response.email,
            role = response.role
        )
        sessionManager.saveSession(session)
        return session
    }

    private fun mapException(throwable: Throwable): Exception {
        if (throwable is HttpException) {
            val errorBody = throwable.response()?.errorBody()?.string()
            return Exception(ApiClient.parseError(errorBody))
        }
        return Exception(throwable.message ?: "Erro de rede")
    }
}

class BenchRepository {

    suspend fun search(
        type: BenchType? = null,
        color: String? = null,
        minWidth: Double? = null,
        maxWidth: Double? = null
    ): Result<List<Bench>> = apiCall { ApiClient.api.searchBenches(type, color, minWidth, maxWidth) }

    suspend fun getById(id: Long): Result<Bench> = apiCall { ApiClient.api.getBench(id) }

    suspend fun report(
        latitude: Double,
        longitude: Double,
        type: BenchType,
        color: String,
        widthMeters: Double
    ): Result<Bench> = apiCall {
        ApiClient.api.reportBench(BenchRequest(latitude, longitude, type, color, widthMeters))
    }

    suspend fun getPending(): Result<List<Bench>> = apiCall { ApiClient.api.getPendingBenches() }

    suspend fun approve(id: Long): Result<Bench> = apiCall { ApiClient.api.approveBench(id) }

    suspend fun reject(id: Long): Result<Bench> = apiCall { ApiClient.api.rejectBench(id) }

    suspend fun getRatings(benchId: Long): Result<List<Rating>> =
        apiCall { ApiClient.api.getRatings(benchId) }

    suspend fun rate(benchId: Long, stars: Int): Result<Rating> =
        apiCall { ApiClient.api.rateBench(benchId, RatingRequest(stars)) }

    private suspend fun <T> apiCall(block: suspend () -> T): Result<T> = runCatching {
        block()
    }.recoverCatching {
        if (it is HttpException) {
            val errorBody = it.response()?.errorBody()?.string()
            throw Exception(ApiClient.parseError(errorBody))
        }
        throw Exception(it.message ?: "Erro de rede")
    }
}
