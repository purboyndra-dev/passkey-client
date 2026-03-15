package com.example.presence.services

import com.example.presence.data.DetailResponse
import com.example.presence.data.RegisterDataResponse
import com.example.presence.data.RegisterResponse
import com.example.presence.helper.AuthHelper
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject


private const val BASE_URL = "https://bc1d-103-153-135-203.ngrok-free.app"
private const val EXPECTED_RP_ID = "bc1d-103-153-135-203.ngrok-free.app"

@Serializable
data class ResponsePasskeyCredential(
    val attestationObject: String? = null,
    val clientDataJSON: String,
    val transports: List<String> = emptyList(),
    val authenticatorData: String? = null,
    val publicKeyAlgorithm: Int? = null,
    val publicKey: String? = null,
    val signature: String? = null,
    val userHandle: String? = null
)

@Serializable
data class PublicCredentialRequest(
    val id: String,
    val rawId: String,
    val type: String,
    val clientExtensionResults: JsonObject,
    val authenticatorAttachment: String? = null,
    val response: ResponsePasskeyCredential
)

@Serializable
data class PasskeyCredentialRequest(
    val id: String,
    @SerialName("raw_id")
    val rawId: String,
    val type: String,
    val clientExtensionResults: JsonObject,
    val authenticatorAttachment: String? = null,
    val response: ResponsePasskeyCredential
)

@Serializable
data class PasskeyVerifyRequestBody(
    val credential: PasskeyCredentialRequest,
    @SerialName("expected_rp_id")
    val expectedRpId: String,
    @SerialName("require_user_verification")
    val requireUserVerification: Boolean,
    @SerialName("user_id")
    val userId: String
)

/// FOR LOGIN VERIFY
@Serializable
data class LoginPasskeyVerifyRequestBody(
    @SerialName("require_user_verification")
    val requireUserVerification: Boolean,
    val credential: PasskeyCredentialRequest,
    val email: String
)


class AuthService(
    private val client: KtorClient,
    private val authHelper: AuthHelper
) {

    suspend fun loginVerify(body: LoginPasskeyVerifyRequestBody): String? {
        return try {
            val response = client.post("${BASE_URL}/auth/passkeys/login/verify") {
                contentType(
                    ContentType.Application.Json
                )
                setBody(
                    body
                )
            }

            val bodyText = response.bodyAsText()

            "Authentication successful"

        } catch (e: Exception) {
            println("Error login verify: $e")
            e.printStackTrace()
            null
        }
    }

    suspend fun loginOptions(email: String): PublicCredentialRequest? {
        try {

            val response = client.post("$BASE_URL/auth/passkeys/login/options") {
                contentType(ContentType.Application.Json)
                setBody(
                    mapOf(
                        "email" to email
                    )
                )
            }

            val bodyText = response.bodyAsText()

            val responseCreds = authHelper.signIn(bodyText)

            println("Response login options: $response")

            return responseCreds?.let {
                println("Response login options: $it")
                Json.decodeFromString<PublicCredentialRequest>(it)
            }

        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    suspend fun register(fullName: String): RegisterResponse? {
        return try {
            val response = client.post("$BASE_URL/users") {
                contentType(ContentType.Application.Json)
                setBody(
                    mapOf(
                        "full_name" to fullName,
                        "email" to "${fullName}@gmail.com",
                        "role" to "user"
                    )
                )
            }
            val bodyText = response.bodyAsText()
            val data = Json.decodeFromString<RegisterResponse>(bodyText)
            if (data.detail != null) {
                throw Exception(data.detail.message)
            }
            data
        } catch (e: Exception) {
            e.printStackTrace()
            RegisterResponse(
                detail = DetailResponse(
                    message = e.message ?: "Unknown error"
                )
            )
        }
    }

    suspend fun verifyRegistrationOptions(body: PasskeyVerifyRequestBody): String {
        return try {
            val response =
                client.post("$BASE_URL/auth/passkeys/register/verify") {
                    contentType(ContentType.Application.Json)
                    setBody(
                        body
                    )
                }
            println("[AuthService] verifyRegistrationOptions body: $body")
            println("[AuthService] verifyRegistrationOptions: $response")
            "Success create passkey"
        } catch (e: Exception) {
            e.printStackTrace()
            e.message ?: "Unknown error"
        }
    }

    suspend fun registerOptions(email: String, userId: String): RegisterResponse {
        return try {
            val response =
                client.post("$BASE_URL/auth/passkeys/register/options") {
                    contentType(ContentType.Application.Json)
                    setBody(
                        mapOf(
                            "email" to email,
                            "user_id" to userId
                        )
                    )
                }
            println(response)
            val bodyText = response.bodyAsText()
            val passkeyResponse = authHelper.signUp(bodyText)
            3
            val passkeyCreds = passkeyResponse?.let {
                Json.decodeFromString<PublicCredentialRequest>(it)
            }

            val passkeyCredsResponse = passkeyCreds?.let {
                val verifyBody = PasskeyVerifyRequestBody(
                    expectedRpId = EXPECTED_RP_ID,
                    userId = userId,
                    requireUserVerification = true,
                    credential = PasskeyCredentialRequest(
                        id = it.id,
                        rawId = it.rawId,
                        type = it.type,
                        clientExtensionResults = it.clientExtensionResults,
                        authenticatorAttachment = it.authenticatorAttachment,
                        response = ResponsePasskeyCredential(
                            attestationObject = it.response.attestationObject,
                            clientDataJSON = it.response.clientDataJSON,
                            transports = it.response.transports
                        )
                    )
                )

                val jsonString = Json.encodeToString(verifyBody)
                println("JSON to be sent: $jsonString")

                verifyRegistrationOptions(verifyBody)
            }

            if (passkeyCredsResponse != null) {
                RegisterResponse(
                    message = passkeyResponse
                )
            } else {
                throw Exception("Passkey credentials are null")
            }

        } catch (e: Exception) {
            println("Error occurred: ${e.message}")
            println(e.stackTraceToString())
            RegisterResponse(
                detail = DetailResponse(
                    message = e.message ?: "Unknown error"
                )
            )
        }
    }
}
