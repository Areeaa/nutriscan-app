package com.example.nutriscan.data.repository

import com.example.nutriscan.common.Resource
import com.example.nutriscan.domain.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
): AuthRepository {

    override fun login(email: String, password: String): Flow<Resource<User>> = callbackFlow {
        try {

            trySend(Resource.Loading())

            val authResult = auth.signInWithEmailAndPassword(email, password).await()
            val uid = authResult.user?.uid

            if (uid != null){
                val documentSnapshot = firestore.collection("users").document(uid).get().await()
                val user =documentSnapshot.toObject(User::class.java)

                if ( user !=null){
                    trySend(Resource.Success(user))
                }else{
                    trySend(Resource.Error("User tidak ditemukan"))
                }
            }else{
                trySend(Resource.Error("Gagal mendapatkan uid"))
            }
        }catch (e: Exception){
            trySend(Resource.Error(e.localizedMessage?: "Terjadi kesalahan tak terduga"))
        }
        awaitClose {  }
    }

    override fun register(email: String, password: String, user: User): Flow<Resource<User>> = callbackFlow {
        try {
            trySend(Resource.Loading())

            val authResult = auth.createUserWithEmailAndPassword(email, password).await()
            val uid = authResult.user?.uid

            if (uid != null) {

                val newUser = user.copy(uid = uid, email = email,)


                firestore.collection("users").document(uid).set(newUser).await()

                trySend(Resource.Success(newUser))
            } else {
                trySend(Resource.Error("Gagal membuat user ID"))
            }

        } catch (e: Exception) {
            trySend(Resource.Error(e.localizedMessage ?: "Gagal Mendaftar"))
        }
        awaitClose { }
    }

    override fun isUserLogin(): Boolean {
        return auth.currentUser != null
    }

    override fun getCurrentUserId(): String? {
        return auth.currentUser?.uid
    }

    override fun logout() {
        auth.signOut()
    }

    override fun getCurrentUser(): User? {
        val firebaseUser = auth.currentUser

        return if (firebaseUser != null) {
            // Kita mapping data dari Firebase Auth ke Domain User kita
            User(
                uid = firebaseUser.uid,
                displayName = firebaseUser.displayName ?: "User", // Default jika kosong
                email = firebaseUser.email ?: ""
                // Physical & Health biarkan default (kosong) karena Firebase Auth tidak simpan data itu
            )
        } else {
            null
        }


    }
}