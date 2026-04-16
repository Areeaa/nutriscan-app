package com.example.nutriscan.data.repository

import com.example.nutriscan.common.Resource
import com.example.nutriscan.domain.model.User
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    //login
    fun login(email: String, password: String): Flow<Resource<User>>

    //register
    fun register(email: String, password: String, user: User): Flow<Resource<User>>

    //cek status login
    fun isUserLogin(): Boolean

    //ambil id user
    fun getCurrentUserId(): String?

    //logout
    fun logout()

    fun getCurrentUser(): User?

    fun resetPassword(email: String): Flow<Resource<String>>
}