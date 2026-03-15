package com.example.presence.presentation.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.presence.services.AuthService
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
data class RegisterUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val success: String? = null,
)

@KoinViewModel
class RegisterViewModel(
    private val authService: AuthService
) : ViewModel() {

    private val _uiState: MutableStateFlow<RegisterUiState> = MutableStateFlow(RegisterUiState())
    val uiState = _uiState.asStateFlow()

    private val _fullName: MutableStateFlow<String> = MutableStateFlow("")
    val fullName = _fullName.asStateFlow()

    private val _userName: MutableStateFlow<String> = MutableStateFlow("")
    val userName = _userName.asStateFlow()

    fun setFullName(fullName: String) {
        _fullName.update {
            fullName
        }
    }

    fun setUserName(userName: String) {
        _userName.update {
            userName
        }
    }

    fun register() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = true,
                    error = null,
                    success = null
                )
            }
            val response = authService.register(_fullName.value)

            if (response?.detail != null) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = response.detail.message
                    )
                }
                return@launch
            }

            val passkeyResponse = response?.data?.let {
                authService.registerOptions(it.email, it.id)
            }

            val isError = passkeyResponse?.detail != null
            val success = if (!isError) passkeyResponse?.message else null
            val error = if (isError) passkeyResponse.detail.message else null

            _uiState.update {
                it.copy(
                    isLoading = false,
                    error = error,
                    success = success,
                )
            }
        }
    }
}
