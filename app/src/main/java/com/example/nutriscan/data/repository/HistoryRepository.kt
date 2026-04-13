package com.example.nutriscan.domain.repository

import com.example.nutriscan.common.Resource
import com.example.nutriscan.domain.model.ScanHistory
import kotlinx.coroutines.flow.Flow

interface HistoryRepository {
    fun getScanHistory(uid: String): Flow<Resource<List<ScanHistory>>>
}