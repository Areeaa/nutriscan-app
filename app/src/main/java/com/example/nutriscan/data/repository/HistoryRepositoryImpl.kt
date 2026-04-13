package com.example.nutriscan.data.repository

import com.example.nutriscan.common.Resource
import com.example.nutriscan.domain.model.DetectedNutrient
import com.example.nutriscan.domain.model.ScanHistory
import com.example.nutriscan.domain.repository.HistoryRepository
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class HistoryRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : HistoryRepository {

    override fun getScanHistory(uid: String): Flow<Resource<List<ScanHistory>>> = callbackFlow {

        trySend(Resource.Loading())

        val listenerRegistration = firestore.collection("users")
            .document(uid)
            .collection("scan_history")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->

                if (error != null) {
                    trySend(Resource.Error(error.localizedMessage ?: "Terjadi kesalahan"))
                    return@addSnapshotListener
                }

                if (snapshot != null) {

                    val historyList = snapshot.documents.mapNotNull { doc ->

                        try {

                            val rawNutrients =
                                doc.get("nutrients") as? List<Map<String, Any>> ?: emptyList()

                            val parsedNutrients = rawNutrients.map { map ->

                                DetectedNutrient(
                                    name = map["name"] as? String ?: "",
                                    value = (map["value"] as? Double)?.toFloat() ?: 0f,
                                    unit = map["unit"] as? String ?: "",
                                    isPrimary = map["isPrimary"] as? Boolean ?: false,
                                    isDetected = map["isDetected"] as? Boolean ?: false
                                )
                            }

                            ScanHistory(
                                id = doc.id,
                                labelName = doc.getString("labelName") ?: "",
                                totalEnergi = doc.getString("totalEnergi") ?: "-",
                                recommendation = doc.getString("recommendation") ?: "",
                                timestamp = doc.getLong("timestamp") ?: 0L,
                                nutrients = parsedNutrients
                            )

                        } catch (e: Exception) {
                            null
                        }
                    }

                    trySend(Resource.Success(historyList))
                }
            }

        awaitClose {
            listenerRegistration.remove()
        }
    }
}