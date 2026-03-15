package com.example.presence.presentation.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.presence.data.UserResponse
import com.example.presence.services.UserService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.annotation.KoinViewModel

data class UserState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val user: UserResponse? = null,
)

@KoinViewModel
class MainViewModel(private val userService: UserService) : ViewModel() {

    private val _uiState = MutableStateFlow<UserState>(UserState())
    val uiState = _uiState.asStateFlow()


    fun fetchUserByEmail(email: String) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = true,
                    error = null
                )
            }

            val user = userService.fetchUserByEmail(email)

            if (user.data != null) {
                _uiState.update {
                    it.copy(
                        user = user.data,
                        isLoading = false
                    )
                }
            } else {
                _uiState.update {
                    it.copy(
                        error = user.message,
                        isLoading = false
                    )
                }
            }

        }
    }

}