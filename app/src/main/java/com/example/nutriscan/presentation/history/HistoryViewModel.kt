package com.example.nutriscan.presentation.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nutriscan.common.Resource
import com.example.nutriscan.data.repository.AuthRepository
import com.example.nutriscan.domain.model.ScanHistory
import com.example.nutriscan.domain.repository.HistoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val historyRepository: HistoryRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage = _errorMessage.asStateFlow()

    // Menyimpan data asli dari Firebase
    private val _allHistory = MutableStateFlow<List<ScanHistory>>(emptyList())

    // Menyimpan data yang SUDAH di-filter (yang akan ditampilkan di UI)
    private val _filteredHistory = MutableStateFlow<List<ScanHistory>>(emptyList())
    val filteredHistory = _filteredHistory.asStateFlow()

    // State untuk Pencarian & Filter Bulan
    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    // Menyimpan bulan (0-11) dan tahun yang sedang dipilih. Default: Bulan dan Tahun saat ini
    private val currentCalendar = Calendar.getInstance()
    private val _selectedMonth = MutableStateFlow(currentCalendar.get(Calendar.MONTH))
    private val _selectedYear = MutableStateFlow(currentCalendar.get(Calendar.YEAR))

    val selectedMonth = _selectedMonth.asStateFlow()
    val selectedYear = _selectedYear.asStateFlow()

    init {
        fetchHistoryData()
    }

    private fun fetchHistoryData() {
        viewModelScope.launch {
            val uid = authRepository.getCurrentUserId() ?: return@launch

            historyRepository.getScanHistory(uid).collect { result ->
                when (result) {
                    is Resource.Loading -> _isLoading.value = true
                    is Resource.Success -> {
                        _isLoading.value = false
                        _allHistory.value = result.data ?: emptyList()
                        applyFilters() // Jalankan filter setiap kali data baru masuk
                    }
                    is Resource.Error -> {
                        _isLoading.value = false
                        _errorMessage.value = result.message
                    }
                }
            }
        }
    }

    // --- FUNGSI INTERAKSI DARI UI ---

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
        applyFilters()
    }

    fun nextMonth() {
        if (_selectedMonth.value == 11) {
            _selectedMonth.value = 0
            _selectedYear.value += 1
        } else {
            _selectedMonth.value += 1
        }
        applyFilters()
    }

    fun previousMonth() {
        if (_selectedMonth.value == 0) {
            _selectedMonth.value = 11
            _selectedYear.value -= 1
        } else {
            _selectedMonth.value -= 1
        }
        applyFilters()
    }

    // --- LOGIKA FILTERING ---
    private fun applyFilters() {
        val query = _searchQuery.value.trim().lowercase()
        val month = _selectedMonth.value
        val year = _selectedYear.value

        val filtered = _allHistory.value.filter { history ->
            // 1. Cek kecocokan tanggal (Bulan & Tahun)
            val cal = Calendar.getInstance().apply { timeInMillis = history.timestamp }
            val matchDate = cal.get(Calendar.MONTH) == month && cal.get(Calendar.YEAR) == year

            // 2. Cek kecocokan teks pencarian
            val matchSearch = if (query.isEmpty()) true else {
                history.labelName.lowercase().contains(query)
            }

            matchDate && matchSearch
        }

        _filteredHistory.value = filtered
    }
}