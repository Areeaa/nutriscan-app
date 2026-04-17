package com.example.nutriscan.data.repository

import android.content.Context // <-- INI YANG BENAR
import android.net.Uri
import com.example.nutriscan.common.Resource
import com.example.nutriscan.data.network.CloudinaryApi
import com.example.nutriscan.domain.model.User
import com.example.nutriscan.domain.repository.UserRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth,
    private val cloudinaryApi: CloudinaryApi,
    private val context: Context
) : UserRepository {

    private val CLOUD_NAME = "drsr72xr8"
    private val UPLOAD_PRESET = "ml_default"

    override suspend fun saveUser(user: User): Resource<Boolean> {
        return try {
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

        val listener = docRef.addSnapshotListener { snapshot, error ->
            if (error != null) {
                trySend(Resource.Error(error.localizedMessage ?: "Gagal mengambil data"))
                return@addSnapshotListener
            }

            if (snapshot != null && snapshot.exists()) {
                try {
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
            firestore.collection("users").document(uid).delete()
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "Gagal menghapus data pengguna")
        }
    }

    override fun uploadProfilePicture(uri: Uri): Flow<Resource<String>> = callbackFlow {
        try {
            trySend(Resource.Loading())
            val uid = auth.currentUser?.uid ?: throw Exception("Pengguna belum login")

            val file = getFileFromUri(context, uri) ?: throw Exception("Gagal membaca gambar")

            val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
            val body = MultipartBody.Part.createFormData("file", file.name, requestFile)
            val preset = UPLOAD_PRESET.toRequestBody("text/plain".toMediaTypeOrNull())

            val response = cloudinaryApi.uploadImage(CLOUD_NAME, body, preset)
            val downloadUrl = response.secureUrl

            firestore.collection("users").document(uid)
                .update("profilePictureUrl", downloadUrl).await()

            file.delete()

            trySend(Resource.Success(downloadUrl))
        } catch (e: Exception) {
            trySend(Resource.Error(e.localizedMessage ?: "Gagal mengunggah foto profil"))
        }
        awaitClose { }
    }

    private fun getFileFromUri(context: Context, uri: Uri): File? {
        val contentResolver = context.contentResolver
        val tempFile = File(context.cacheDir, "temp_profile_pic.jpg")
        try {
            val inputStream = contentResolver.openInputStream(uri) ?: return null
            val outputStream = FileOutputStream(tempFile)
            inputStream.copyTo(outputStream)
            inputStream.close()
            outputStream.close()
            return tempFile
        } catch (e: Exception) {
            return null
        }
    }
}