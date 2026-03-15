package com.example.presence.di

import com.example.presence.helper.AuthHelper
import com.example.presence.helper.AuthHelperAndroid
import org.koin.dsl.module

class AndroidPlatformModule : AppPlatformModule {
    /**
     * Android implementation of [AppPlatformModule]
     */
    override val platformModule = module {
        single<AuthHelper> { AuthHelperAndroid(get()) }
    }
}
