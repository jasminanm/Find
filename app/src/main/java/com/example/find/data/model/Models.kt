package com.example.find.data.model

enum class BenchType {
    WOOD, CEMENT, METAL, STONE, OTHER
}

enum class BenchStatus {
    PENDING, APPROVED, REJECTED
}

enum class UserRole {
    USER, ADMIN
}

data class Bench(
    val id: Long,
    val latitude: Double,
    val longitude: Double,
    val type: BenchType,
    val color: String,
    val widthMeters: Double,
    val status: BenchStatus,
    val reportedByEmail: String?,
    val createdAt: String?,
    val averageRating: Double?,
    val ratingCount: Long?
)

data class AuthResponse(
    val token: String,
    val userId: Long,
    val email: String,
    val role: UserRole
)

data class Rating(
    val id: Long,
    val stars: Int,
    val userEmail: String,
    val createdAt: String
)

data class UserSession(
    val token: String,
    val userId: Long,
    val email: String,
    val role: UserRole
)
