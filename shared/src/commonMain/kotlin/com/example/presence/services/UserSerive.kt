package com.example.presence.services

import com.example.presence.data.FetchUserResponse
import io.ktor.client.statement.bodyAsText
import kotlinx.serialization.json.Json


private const val BASE_URL = "https://bc1d-103-153-135-203.ngrok-free.app"

class UserService(
    private val client: KtorClient
) {
    suspend fun fetchUserByEmail(email: String): FetchUserResponse {
        val response = client.get("${BASE_URL}/users/${email}")
        val body = response.bodyAsText()
        return Json.decodeFromString<FetchUserResponse>(body)
    }
}