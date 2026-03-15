package com.example.presence

import android.app.Application
import com.example.presence.di.AndroidPlatformModule
import com.example.presence.di.initKoin
import com.example.presence.helper.AndroidContextFactory
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.dsl.module

class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        
        val androidPlatformModule = AndroidPlatformModule()
        
        initKoin(
            appDeclaration = {
                androidLogger()
                androidContext(this@MainApplication)
            },
            platformModule = module {
                includes(androidPlatformModule.platformModule)
                single<AndroidContextFactory> { AndroidContextFactory(this@MainApplication) }
            }
        )
    }
}
