package com.example.presence.helper

import android.annotation.SuppressLint
import android.content.Context
import android.credentials.GetCredentialException
import android.os.Build
import android.service.credentials.CreateCredentialRequest
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.credentials.CreatePublicKeyCredentialRequest
import androidx.credentials.CreatePublicKeyCredentialResponse
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.GetPasswordOption
import androidx.credentials.GetPublicKeyCredentialOption
import androidx.credentials.PasswordCredential
import androidx.credentials.PublicKeyCredential
import androidx.credentials.exceptions.CreateCredentialCancellationException
import androidx.credentials.exceptions.CreateCredentialCustomException
import androidx.credentials.exceptions.CreateCredentialException
import androidx.credentials.exceptions.CreateCredentialInterruptedException
import androidx.credentials.exceptions.CreateCredentialProviderConfigurationException
import androidx.credentials.exceptions.publickeycredential.CreatePublicKeyCredentialDomException
import com.example.presence.services.PasskeyCredentialRequest
import com.example.presence.services.ResponsePasskeyCredential
import kotlinx.coroutines.coroutineScope
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlin.jvm.java

/**
 * Android implementation of [AuthHelper]
 * Context unavailable in [shared] module
 */
class AuthHelperAndroid(private val context: Context) : AuthHelper {
    private val TAG = "AUTH_HELPER_ANDROID"
    private val credentialManager = CredentialManager.create(context)

    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    override suspend fun signIn(requestJson: String): String? {
        return coroutineScope {
            try {
                val getPasswordOption = GetPasswordOption()
                val getPublicKeyCredentialOption = GetPublicKeyCredentialOption(
                    requestJson = requestJson
                )

                val credentialRequest = GetCredentialRequest(
                    listOf(
                        getPasswordOption,
                        getPublicKeyCredentialOption
                    ),
                )

                val result = credentialManager.getCredential(
                    context = context,
                    request = credentialRequest
                )

                val credential = result.credential
                Log.d(TAG, "Credential: $credential")
                when (credential) {
                    is PublicKeyCredential -> {
                        val responseJson = credential.authenticationResponseJson
                        println("TAG: $TAG PublicKeyCredential: $responseJson")
                        responseJson
                    }

                    is PasswordCredential -> {
                        val username = credential.id
                        val password = credential.password
                        println("TAG: $TAG PasswordCredential: $username, $password")
                        null
                    }

                    else -> {
                        Log.e(TAG, "Unexpected type of credential")
                        null
                    }
                }
            } catch (e: CreateCredentialException) {

                null
            } catch (e: GetCredentialException) {
                println("TAG: $TAG GetCredentialException: ${e.message}")
                null
            }
        }
    }

    @SuppressLint("PublicKeyCredential")
    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    override suspend fun signUp(requestJson: String): String? {
        val credentialManager = CredentialManager.create(context)
        return coroutineScope {
            try {
                val createCredentialRequest = CreatePublicKeyCredentialRequest(requestJson)
                val result = credentialManager.createCredential(context, createCredentialRequest)
                if (result is CreatePublicKeyCredentialResponse) {
                    val responseJson = result.registrationResponseJson
                    responseJson
                } else {
                    Log.w(TAG, "Unexpected credential type returned: ${result.type}")
                    null
                }
            } catch (e: CreateCredentialException) {
                null
            } catch (e: GetCredentialException) {
                println("TAG: $TAG GetCredentialException: ${e.message}")
                null
            }
        }
    }
}