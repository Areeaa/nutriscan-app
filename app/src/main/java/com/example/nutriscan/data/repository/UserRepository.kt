package com.example.nutriscan.domain.repository

import com.example.nutriscan.common.Resource
import com.example.nutriscan.domain.model.User
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    // Fungsi untuk menyimpan atau update data user (dipakai di Onboarding)
    suspend fun saveUser(user: User): Resource<Boolean>

    // Fungsi untuk mengambil data user secara realtime (dipakai nanti di Home/Profile)
    fun getUser(uid: String): Flow<Resource<User>>

    suspend fun updateUserField(uid: String, fieldPath: String, value: Any): Resource<Unit>

    suspend fun deleteUserData(uid: String): Resource<Unit>
}