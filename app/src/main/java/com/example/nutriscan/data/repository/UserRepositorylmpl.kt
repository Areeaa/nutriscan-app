package com.example.nutriscan.data.repository

import com.example.nutriscan.common.Resource
import com.example.nutriscan.domain.model.User
import com.example.nutriscan.domain.repository.UserRepository
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : UserRepository {

    override suspend fun saveUser(user: User): Resource<Boolean> {
        return try {
            // Simpan ke collection "users" dengan ID dokumen = UID User
            // SetOptions.merge() penting agar jika data sudah ada, tidak terhapus total
            firestore.collection("users")
                .document(user.uid)
                .set(user, SetOptions.merge())
                .await()

            Resource.Success(true)
        } catch (e: Exception) {
            e.printStackTrace()
            Resource.Error(e.localizedMessage ?: "Gagal menyimpan data profil")
        }
    }

    override fun getUser(uid: String): Flow<Resource<User>> = callbackFlow {
        trySend(Resource.Loading())

        val docRef = firestore.collection("users").document(uid)

        // Menggunakan SnapshotListener untuk Realtime Updates
        val listener = docRef.addSnapshotListener { snapshot, error ->
            if (error != null) {
                trySend(Resource.Error(error.localizedMessage ?: "Gagal mengambil data"))
                return@addSnapshotListener
            }

            if (snapshot != null && snapshot.exists()) {
                try {
                    // Otomatis convert JSON Firestore ke Object User Kotlin
                    val user = snapshot.toObject(User::class.java)
                    if (user != null) {
                        trySend(Resource.Success(user))
                    } else {
                        trySend(Resource.Error("Format data user salah"))
                    }
                } catch (e: Exception) {
                    trySend(Resource.Error("Gagal memproses data: ${e.message}"))
                }
            } else {
                trySend(Resource.Error("Data pengguna tidak ditemukan"))
            }
        }

        // Tutup listener jika Flow berhenti (agar tidak memory leak)
        awaitClose { listener.remove() }
    }

    override suspend fun updateUserField(uid: String, fieldPath: String, value: Any): Resource<Unit> {
        return try {
            firestore.collection("users").document(uid)
                .update(fieldPath, value)

            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "Gagal memperbarui profil")
        }
    }

    override suspend fun deleteUserData(uid: String): Resource<Unit> {
        return try {
            // Menghapus dokumen user di Firestore
            firestore.collection("users").document(uid).delete()
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "Gagal menghapus data pengguna")
        }
    }


}