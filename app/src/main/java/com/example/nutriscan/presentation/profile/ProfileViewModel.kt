package com.example.nutriscan.presentation.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nutriscan.common.Resource
import com.example.nutriscan.data.repository.AuthRepository
import com.example.nutriscan.domain.model.User
import com.example.nutriscan.domain.repository.UserRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.pow

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _userState = MutableStateFlow<User?>(null)
    val userState = _userState.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    init {
        fetchUserProfile()
    }

    private fun fetchUserProfile() {
        viewModelScope.launch {
            val uid = authRepository.getCurrentUserId() ?: return@launch
            _isLoading.value = true

            userRepository.getUser(uid).collect { result ->
                if (result is Resource.Success) {
                    _userState.value = result.data
                }
                _isLoading.value = false
            }
        }
    }

    // Fungsi untuk memproses input dari Bottom Sheet dan mengirim ke Firestore
    fun updateProfile(field: ProfileEditField, newValue: String) {
        viewModelScope.launch {
            val uid = authRepository.getCurrentUserId() ?: return@launch

            // 1. Tentukan path database (karena datanya nested/bersarang di Firestore)
            val dbFieldPath = when (field) {
                ProfileEditField.DISPLAY_NAME -> "displayName"
                ProfileEditField.GENDER -> "physicalProfile.gender"
                ProfileEditField.AGE -> "physicalProfile.age"
                ProfileEditField.HEIGHT -> "physicalProfile.height"
                ProfileEditField.WEIGHT -> "physicalProfile.weight"
                ProfileEditField.GOAL -> "healthConfig.goal"
                ProfileEditField.DISEASE -> "healthConfig.diseases" // Asumsi di DB jadi List
                ProfileEditField.DIET -> "healthConfig.dietPreference"
                ProfileEditField.ACTIVITY -> "healthConfig.activityLevel"
            }

            // 2. Konversi tipe data agar sesuai dengan model di Firestore
            val parsedValue: Any = when (field) {
                ProfileEditField.AGE -> newValue.toIntOrNull() ?: 0
                ProfileEditField.HEIGHT, ProfileEditField.WEIGHT -> newValue.toFloatOrNull() ?: 0f
                ProfileEditField.DISEASE -> newValue.split(",").map { it.trim() } // Ubah string "Maag, Asma" jadi List
                else -> newValue
            }

            // 3. Kirim update ke Firestore
            _isLoading.value = true
            val result = userRepository.updateUserField(uid, dbFieldPath, parsedValue)

            // 4. Kalkulasi ulang BMI jika Tinggi/Berat badan diubah
            if (result is Resource.Success && (field == ProfileEditField.HEIGHT || field == ProfileEditField.WEIGHT)) {
                recalculateBMI(uid)
            }

            _isLoading.value = false
        }
    }

    private suspend fun recalculateBMI(uid: String) {
        val user = _userState.value ?: return
        // Ambil nilai terbaru (bisa dari state saat ini karena Flow akan update otomatis,
        // tapi untuk pastinya kita ambil dari user object yang terupdate)
        val heightCm = user.physicalProfile.height ?: return
        val weightKg = user.physicalProfile.weight ?: return

        if (heightCm > 0 && weightKg > 0) {
            val heightMeter = heightCm / 100f
            val bmi = weightKg / heightMeter.pow(2)

            // Simpan hasil BMI ke database
            userRepository.updateUserField(uid, "physicalProfile.bmi", bmi)
        }
    }

    fun deleteAccount(onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            val currentUser = FirebaseAuth.getInstance().currentUser
            val uid = currentUser?.uid

            if (currentUser != null && uid != null) {
                // 1. Hapus data dari Firestore Database terlebih dahulu
                val result = userRepository.deleteUserData(uid)

                if (result is Resource.Success) {
                    // 2. Jika sukses, hapus kredensial Autentikasi Firebase
                    currentUser.delete()
                        .addOnSuccessListener {
                            _isLoading.value = false
                            onSuccess() // Beri tahu UI untuk pindah layar
                        }
                        .addOnFailureListener { e ->
                            _isLoading.value = false
                            onError(e.localizedMessage ?: "Gagal menghapus autentikasi. Silakan login ulang dan coba lagi.")
                        }
                } else {
                    _isLoading.value = false
                    onError(result.message ?: "Gagal menghapus data profil dari database.")
                }
            } else {
                _isLoading.value = false
                onError("Sesi pengguna tidak valid.")
            }
        }
    }
}