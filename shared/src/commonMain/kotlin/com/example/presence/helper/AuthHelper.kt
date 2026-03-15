package com.example.presence.helper

interface AuthHelper {
    suspend fun signIn(requestJson: String): String?
    suspend fun signUp(requestJson: String): String?
}