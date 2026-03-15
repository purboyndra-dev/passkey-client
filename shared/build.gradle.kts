@file:OptIn(ExperimentalKotlinGradlePluginApi::class)

import org.gradle.kotlin.dsl.invoke
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.konan.target.HostManager

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidMultiplatformLibrary)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.composeHotReload)
    alias(libs.plugins.koin)
}

kotlin {
    androidLibrary {
        namespace = "com.example.presence.composeApp"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
        minSdk = libs.versions.android.minSdk.get().toInt()

        compilerOptions {
            jvmTarget = JvmTarget.JVM_11
        }
        androidResources {
            enable = true
        }
    }

    if (HostManager.hostIsMac) {
        listOf(
            iosArm64(),
            iosSimulatorArm64()
        ).forEach { iosTarget ->
            iosTarget.binaries.framework {
                baseName = "ComposeApp"
                isStatic = true
            }
        }
    }

    dependencies {
        implementation(libs.compose.runtime)
        implementation(libs.compose.foundation)
        implementation(libs.compose.material3)
        implementation(libs.compose.ui)
        implementation(libs.compose.components.resources)
        implementation(libs.compose.uiToolingPreview)

        implementation(libs.androidx.lifecycle.viewmodelCompose)
        implementation(libs.androidx.lifecycle.runtimeCompose)

        /// DATASTORE
        implementation(libs.datasore)
        implementation(libs.datastore.preferences)

//        implementation(libs.material.icon.extended)
//        implementation(libs.compose.material.icons)

        implementation(libs.ktor.client.cio)
        implementation(libs.ktor.client.core)
        implementation(libs.ktor.serialization.kotlinx.json)
        implementation(libs.ktor.client.logging)
        implementation(libs.ktor.client.content.negotiation)

        // KOIN
        implementation(libs.koin.core)
        implementation(libs.koin.annotations)
        implementation(libs.koin.ktor)
        implementation(libs.koin.compose.viewmodel)
        implementation(libs.koin.viewmodel)

        // NAVIGATION
        implementation(libs.navigation.compose)

        // COIL
        implementation(libs.coil.compose)
        implementation(libs.coil.network.ktor)

        // JSON SERIALIZATION
        implementation(libs.kotlinx.serialization.json)
    }
}

dependencies{
    androidRuntimeClasspath(libs.compose.uiTooling)
}
