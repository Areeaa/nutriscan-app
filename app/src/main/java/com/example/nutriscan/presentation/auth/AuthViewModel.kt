package com.example.nutriscan.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nutriscan.common.Resource
import com.example.nutriscan.data.repository.AuthRepository
import com.example.nutriscan.domain.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repository: AuthRepository
) : ViewModel() {

    private val _authState = MutableStateFlow(AuthState())
    val authState = _authState.asStateFlow()

    fun login(email: String, pass: String) {
        viewModelScope.launch {
            // Reset state ke loading sebelum request
            _authState.value = AuthState(isLoading = true)

            repository.login(email, pass).collect { result ->
                // KITA KONVERSI 'RESOURCE' KE 'AUTHSTATE' DI SINI
                when (result) {
                    is Resource.Loading -> {
                        _authState.value = AuthState(isLoading = true)
                    }
                    is Resource.Success -> {
                        _authState.value = AuthState(isLoading = false, user = result.data)
                    }
                    is Resource.Error -> {
                        _authState.value = AuthState(isLoading = false, error = result.message)
                    }
                }
            }
        }
    }

    fun register(email: String, pass: String, name: String) {
        viewModelScope.launch {
            _authState.value = AuthState(isLoading = true)

            // Logika asli Anda: Membuat object User di sini
            val newUser = User(
                displayName = name,
                email = email,
                // Pastikan properti User Anda sesuai (displayName/name)
            )

            repository.register(email, pass, newUser).collect { result ->
                when (result) {
                    is Resource.Loading -> {
                        _authState.value = AuthState(isLoading = true)
                    }
                    is Resource.Success -> {
                        _authState.value = AuthState(isLoading = false, user = result.data)
                    }
                    is Resource.Error -> {
                        _authState.value = AuthState(isLoading = false, error = result.message)
                    }
                }
            }
        }
    }

    // Reset state jika perlu (misal saat pindah halaman)
    fun resetState() {
        _authState.value = AuthState()
    }

    private val _resetPasswordState = MutableStateFlow<Resource<String>?>(null)
    val resetPasswordState = _resetPasswordState.asStateFlow()

    fun resetPassword(email: String) {
        viewModelScope.launch {
            repository.resetPassword(email).collect { result ->
                _resetPasswordState.value = result
            }
        }
    }

    fun clearResetPasswordState() {
        _resetPasswordState.value = null
    }
}