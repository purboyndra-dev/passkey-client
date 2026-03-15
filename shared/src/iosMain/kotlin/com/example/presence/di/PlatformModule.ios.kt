package com.example.presence.di

import com.example.presence.helper.AuthHelper
import org.koin.dsl.module

actual val platformModule = module {
    single { AuthHelper() }
}