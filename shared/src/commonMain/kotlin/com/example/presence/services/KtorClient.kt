package com.example.presence.services

import io.ktor.client.HttpClient
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText

class KtorClient(private val client: HttpClient) {

    suspend fun get(url: String): HttpResponse {
        val response = client.get(url)
        return response
    }

    suspend fun post(url: String, block: HttpRequestBuilder.() -> Unit = {}): HttpResponse {
        val response = client.post(url, block = block)
        return response
    }
}
