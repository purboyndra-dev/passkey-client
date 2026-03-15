package com.example.presence.presentation.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.presence.services.AuthService
import com.example.presence.services.LoginPasskeyVerifyRequestBody
import com.example.presence.services.PasskeyCredentialRequest
import com.example.presence.services.PasskeyVerifyRequestBody
import com.example.presence.services.ResponsePasskeyCredential
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject
import org.koin.core.annotation.KoinViewModel


@Serializable
data class LoginState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val success: String? = null,
)

@KoinViewModel
class LoginViewModel(
    private val authService: AuthService
) : ViewModel() {

    private val _uiState: MutableStateFlow<LoginState> = MutableStateFlow(LoginState())
    val uiState = _uiState.asStateFlow()
    private val _userName: MutableStateFlow<String> = MutableStateFlow("")
    val userName = _userName.asStateFlow()


    fun setUserName(userName: String) {
        _userName.update {
            userName
        }
    }

    fun loginOptions() {
        viewModelScope.launch {

            _uiState.update {
                it.copy(
                    isLoading = true,
                    error = null,
                    success = null
                )
            }

            val response = authService.loginOptions("${_userName.value}@gmail.com")

            if (response == null) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = "Error occurred"
                    )
                }


            } else {
                val loginPasskeyBody = LoginPasskeyVerifyRequestBody(
                    requireUserVerification = true,
                    email = "${_userName.value}@gmail.com",
                    credential = PasskeyCredentialRequest(
                        id = response.id,
                        rawId = response.rawId,
                        type = response.type,
                        clientExtensionResults = response.clientExtensionResults,
                        authenticatorAttachment = response.authenticatorAttachment,
                        response = ResponsePasskeyCredential(
                            attestationObject = response.response.attestationObject,
                            clientDataJSON = response.response.clientDataJSON,
                            transports = response.response.transports,
                            authenticatorData = response.response.authenticatorData,
                            publicKeyAlgorithm = response.response.publicKeyAlgorithm,
                            publicKey = response.response.publicKey,
                        )
                    )
                )

                println("Login passkey body: $loginPasskeyBody")

                val responseVerify = authService.loginVerify(loginPasskeyBody)

                if (responseVerify != null) {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            success = responseVerify
                        )
                    }
                } else {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = "Error occurred"
                        )
                    }

                }
            }
        }
    }
}