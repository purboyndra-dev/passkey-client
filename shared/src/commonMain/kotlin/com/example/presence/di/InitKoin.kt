package com.example.presence.di

import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module

fun initKoin(
    platformModule: Module = module { },
    appDeclaration: KoinAppDeclaration = {},
) =
    startKoin {
        appDeclaration()
        modules(platformModule + commonModules())
    }