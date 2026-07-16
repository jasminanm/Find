package com.example.find.data.api

import com.example.find.data.model.AuthResponse
import com.example.find.data.model.Bench
import com.example.find.data.model.BenchType
import com.example.find.data.model.Rating
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

data class RegisterRequest(val email: String, val password: String)
data class LoginRequest(val email: String, val password: String)
data class BenchRequest(
    val latitude: Double,
    val longitude: Double,
    val type: BenchType,
    val color: String,
    val widthMeters: Double
)
data class RatingRequest(val stars: Int)
data class ErrorResponse(val error: String?)

interface FindApiService {

    @POST("api/auth/register")
    suspend fun register(@Body request: RegisterRequest): AuthResponse

    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): AuthResponse

    @GET("api/benches")
    suspend fun searchBenches(
        @Query("type") type: BenchType? = null,
        @Query("color") color: String? = null,
        @Query("minWidth") minWidth: Double? = null,
        @Query("maxWidth") maxWidth: Double? = null
    ): List<Bench>

    @GET("api/benches/{id}")
    suspend fun getBench(@Path("id") id: Long): Bench

    @POST("api/benches")
    suspend fun reportBench(@Body request: BenchRequest): Bench

    @GET("api/benches/{benchId}/ratings")
    suspend fun getRatings(@Path("benchId") benchId: Long): List<Rating>

    @POST("api/benches/{benchId}/ratings")
    suspend fun rateBench(@Path("benchId") benchId: Long, @Body request: RatingRequest): Rating

    @GET("api/admin/benches/pending")
    suspend fun getPendingBenches(): List<Bench>

    @PUT("api/admin/benches/{id}/approve")
    suspend fun approveBench(@Path("id") id: Long): Bench

    @PUT("api/admin/benches/{id}/reject")
    suspend fun rejectBench(@Path("id") id: Long): Bench
}
