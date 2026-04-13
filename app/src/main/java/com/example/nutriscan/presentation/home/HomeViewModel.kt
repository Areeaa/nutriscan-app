package com.example.nutriscan.presentation.home

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nutriscan.common.Resource
import com.example.nutriscan.data.repository.AuthRepository
import com.example.nutriscan.domain.model.User
import com.example.nutriscan.domain.repository.UserRepository
import com.google.ai.client.generativeai.GenerativeModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import androidx.core.content.edit
import com.example.nutriscan.BuildConfig

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
    @ApplicationContext private val context: Context // Inject Context untuk SharedPreferences
) : ViewModel() {

    // ... (Kode state userState dan loading biarkan sama) ...
    private val _userState = MutableStateFlow<User?>(null)
    val userState = _userState.asStateFlow()

    private val _loading = MutableStateFlow(false)
    val loading = _loading.asStateFlow()

    // --- STATE untuk Track Air Minum ---
    private val prefs: SharedPreferences = context.getSharedPreferences("NutriScanPrefs", Context.MODE_PRIVATE)

    private val _waterCount = MutableStateFlow(0)
    val waterCount = _waterCount.asStateFlow()

    private val _dailyTip = MutableStateFlow("Sedang mencari tips gizi khusus untukmu...")
    val dailyTip = _dailyTip.asStateFlow()

    private val _isTipLoading = MutableStateFlow(true)
    val isTipLoading = _isTipLoading.asStateFlow()

    private val generativeModel = GenerativeModel(
        modelName = "gemini-2.5-flash",
        apiKey = BuildConfig.GEMINI_API_KEY
    )


    init {
        fetchUserData()
        fetchDailyTip()
        checkDailyReset()
    }

    private fun fetchUserData() {
        viewModelScope.launch {
            _loading.value = true
            val currentUid = authRepository.getCurrentUserId()

            if (currentUid != null) {
                userRepository.getUser(currentUid).collect { result ->
                    when (result) {
                        is Resource.Success -> {
                            _userState.value = result.data
                            _loading.value = false
                        }
                        is Resource.Error -> {
                            _loading.value = false
                            // Handle error (bisa tambah state error string)
                        }
                        is Resource.Loading -> {
                            _loading.value = true
                        }
                    }
                }
            }
        }
    }

    private fun checkDailyReset() {

        val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val lastSavedDate = prefs.getString("LAST_SAVED_DATE", "")

        if (currentDate != lastSavedDate) {

            prefs.edit().apply {
                putString("LAST_SAVED_DATE", currentDate)
                putInt("WATER_COUNT", 0)
                apply()
            }
            _waterCount.value = 0
        } else {
            // Kalau masih hari yang sama, muat progres minum terakhir
            _waterCount.value = prefs.getInt("WATER_COUNT", 0)
        }
    }


    fun fetchDailyTip() {
        viewModelScope.launch {
            _isTipLoading.value = true
            try {
                // Prompt spesifik agar jawabannya rapi, tidak kepanjangan, dan bervariasi
                val prompt = """
                    Berikan 1 fakta unik atau tips singkat tentang gizi, kesehatan, atau kalori makanan.
                    Syarat:
                    1. Panjang maksimal 2-3 kalimat.
                    2. Gunakan bahasa Indonesia yang santai tapi informatif.
                    3. Jangan gunakan format list atau markdown tebal.
                    4. Berikan fakta yang berbeda dari biasanya.
                """.trimIndent()

                val response = generativeModel.generateContent(prompt)

                // Masukkan hasil ke UI
                _dailyTip.value = response.text ?: "Jangan lupa minum air putih dan makan sayur hari ini ya!"
            } catch (e: Exception) {
                // Kalau internet mati atau API error
                _dailyTip.value = "Gagal memuat tips. Pastikan koneksi internetmu lancar, ya!"
            } finally {
                _isTipLoading.value = false
            }
        }
    }


    fun updateWater(clickedIndex: Int) {
        val current = _waterCount.value

        val newValue = if (clickedIndex == current) {
            clickedIndex - 1
        } else {
            clickedIndex
        }

        _waterCount.value = newValue
        prefs.edit { putInt("WATER_COUNT", newValue) }
    }

}

