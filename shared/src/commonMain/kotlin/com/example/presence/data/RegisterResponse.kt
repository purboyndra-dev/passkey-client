package com.example.presence.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/// THIS CLASS FOR ERROR HANDLING
@Serializable
data class DetailResponse(
    val message: String
)

@Serializable
data class UserResponse(
    @SerialName("full_name")
    val fullName: String,
    val email: String,
    val role: String,
    val id: String,
    @SerialName("created_at")
    val createdAt: String,

    @SerialName("deleted_at")
    val deletedAt: String?,

    @SerialName("updated_at")
    val updatedAt: String,
)

@Serializable
data class FetchUserResponse(
    val data: UserResponse? = null,
    val message: String? = null,
    val detail: DetailResponse? = null
)

@Serializable
data class RegisterResponse(
    val message: String? = null,
    val data: RegisterDataResponse? = null,
    val detail: DetailResponse? = null,
)

@Serializable
data class RegisterDataResponse(
    val id: String,
    @SerialName("full_name")
    val fullName: String,
    val email: String,
    val role: String,
    @SerialName("created_at")
    val createdAt: String,
    @SerialName("deleted_at")
    val deletedAt: String?,
    @SerialName("updated_at")
    val updatedAt: String,
)
