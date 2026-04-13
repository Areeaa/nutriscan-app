package com.example.nutriscan.presentation.auth

import com.example.nutriscan.domain.model.User

data class AuthState (
    val isLoading: Boolean = false,
    val user: User? = null,
    val error: String? = null
)