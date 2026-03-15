package com.example.presence.di

import com.example.presence.presentation.login.LoginViewModel
import com.example.presence.presentation.main.MainViewModel
import com.example.presence.presentation.register.RegisterViewModel
import com.example.presence.services.AuthService
import com.example.presence.services.KtorClient
import com.example.presence.services.UserService
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val networkModule = module {
    single {
        HttpClient {
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                    prettyPrint = true
                })
            }
            install(Logging) {
                level = LogLevel.ALL
            }
        }
    }
    single { KtorClient(get()) }
}

val appModule = module {
    single { AuthService(get(), get()) }
    single { UserService(get()) }
    viewModel { LoginViewModel(get()) }
    viewModel { RegisterViewModel(get()) }
    viewModel { MainViewModel(get()) }
}

fun commonModules() = listOf(networkModule, appModule)
