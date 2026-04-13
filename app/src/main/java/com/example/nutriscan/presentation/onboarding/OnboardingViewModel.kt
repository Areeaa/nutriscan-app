package com.example.nutriscan.presentation.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nutriscan.common.Resource
import com.example.nutriscan.data.repository.AuthRepository
import com.example.nutriscan.domain.model.HealthConfig
import com.example.nutriscan.domain.model.PhysicalProfile
import com.example.nutriscan.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val userRepository: UserRepository, // Untuk simpan ke Firestore
    private val authRepository: AuthRepository  // Untuk ambil data user yang sedang login
) : ViewModel() {

    // State UI (Flat) untuk menampung input user sementara
    private val _state = MutableStateFlow(OnboardingState())
    val state = _state.asStateFlow()


    fun updateGoal(value: String) {
        _state.update { it.copy(goal = value,) }
    }

    fun updateDiet(value: String) {
        _state.update { it.copy(diet = value,) }
    }

    fun updateActivity(value: String) {
        _state.update { it.copy(activityLevel = value,) }
    }

    fun toggleDisease(disease: String) {
        _state.update { currentState ->
            val oldList = currentState.diseases
            val newList = if (oldList.contains(disease)) {
                oldList - disease
            } else {
                if (disease == "Tidak Satupun") {
                    listOf(disease)
                } else {
                    (oldList - "Tidak Satupun") + disease
                }
            }
            currentState.copy(diseases = newList,)
        }
    }

    fun updateProfile(
        gender: String? = null,
        age: String? = null,
        height: String? = null,
        weight: String? = null
    ) {
        _state.update { currentState ->
            currentState.copy(
                gender = gender ?: currentState.gender,
                age = age ?: currentState.age,
                height = height ?: currentState.height,
                weight = weight ?: currentState.weight,
            )
        }
    }

    // ==========================================
    // 2. LOGIC SIMPAN DATA (Mapping ke Domain)
    // ==========================================

    fun submitData(onSuccess: () -> Unit) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            // 1. Ambil User Auth saat ini (UID, Email, Nama)
            val currentUser = authRepository.getCurrentUser()

            if (currentUser != null) {

                // 2. Mapping data dari UI (String) ke Model Domain (Int/Nullable)
                val physicalProfile = PhysicalProfile(
                    gender = _state.value.gender.ifEmpty { null },
                    age = _state.value.age.toIntOrNull(),
                    height = _state.value.height.toIntOrNull(),
                    weight = _state.value.weight.toIntOrNull()
                )

                val healthConfig = HealthConfig(
                    goal = _state.value.goal.ifEmpty { null },
                    diseases = _state.value.diseases,
                    dietPreference = _state.value.diet.ifEmpty { null },
                    activityLevel = _state.value.activityLevel.ifEmpty { null }
                )

                // 3. Update object currentUser dengan data profil baru
                // Kita pakai .copy() agar UID, Email, dan Nama tidak hilang
                val userToSave = currentUser.copy(
                    physicalProfile = physicalProfile,
                    healthConfig = healthConfig,
                    isOnboardingFinished = true
                )

                // 4. Simpan ke Firestore
                val result = userRepository.saveUser(userToSave)

                _state.update { it.copy(isLoading = false) }

                if (result is Resource.Success) {
                    onSuccess() // Pindah ke Home
                } else if (result is Resource.Error) {
                    // Handle error di sini jika perlu
                }
            } else {
                _state.update { it.copy(isLoading = false) }
            }
        }
    }

    // ==========================================
    // 3. LOGIC LEWATI (Skip Onboarding)
    // ==========================================

    fun skipOnboarding(onSuccess: () -> Unit) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            val currentUser = authRepository.getCurrentUser()

            if (currentUser != null) {
                // Simpan user dengan profil kosong (Default)
                val defaultUser = currentUser.copy()

                val result = userRepository.saveUser(defaultUser)

                _state.update { it.copy(isLoading = false) }

                if (result is Resource.Success) {
                    onSuccess()
                }
            } else {
                _state.update { it.copy(isLoading = false) }
            }
        }
    }
}