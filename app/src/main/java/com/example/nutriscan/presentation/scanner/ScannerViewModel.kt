package com.example.nutriscan.presentation.scanner

import com.example.nutriscan.BuildConfig
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nutriscan.common.Resource
import com.example.nutriscan.data.repository.AuthRepository
import com.example.nutriscan.domain.model.DetectedNutrient
import com.example.nutriscan.domain.model.User
import com.example.nutriscan.domain.nutrition.NutritionDictionary
import com.example.nutriscan.domain.repository.UserRepository
import com.example.nutriscan.util.uriToBitmap
import com.google.ai.client.generativeai.GenerativeModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class ScannerViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _isProcessing = MutableStateFlow(false)
    val isProcessing = _isProcessing.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage = _errorMessage.asStateFlow()

    private val _capturedImage = MutableStateFlow<Bitmap?>(null)
    val capturedImage = _capturedImage.asStateFlow()

//    private val _nutrientResults = MutableStateFlow<List<NutrientData>>(emptyList())
//    val nutrientResults = _nutrientResults.asStateFlow()

    private val _detectedNutrients = MutableStateFlow<List<DetectedNutrient>>(emptyList())
    val detectedNutrients = _detectedNutrients.asStateFlow()

    // --- UBAH: Teks default sesuai permintaan ---
    private val _recommendation = MutableStateFlow("Silakan periksa kembali angka gizi di atas. Jika sudah sesuai, klik tombol di bawah untuk mendapat rekomendasi")
    val recommendation = _recommendation.asStateFlow()

    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser = _currentUser.asStateFlow()

    private val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
    private val generativeModel = GenerativeModel(
        modelName = "gemini-2.5-flash",
        apiKey = BuildConfig.GEMINI_API_KEY
    )

    // --- TAMBAH: Untuk menyimpan data mentah sebelum diedit/dikirim ke AI ---
//    private val currentRawNutrients = mutableMapOf<String, Float>()

    init {
        fetchUserData()
    }

    private fun fetchUserData() {
        viewModelScope.launch {
            val currentUid = authRepository.getCurrentUserId()
            if (currentUid != null) {
                userRepository.getUser(currentUid).collect { result ->
                    if (result is Resource.Success) {
                        _currentUser.value = result.data
                    }
                }
            }
        }
    }

    // ==========================================
    // CONFUSION-AWARE WEIGHTED LEVENSHTEIN
    // ==========================================
    private val confusionCost = mapOf(
        Pair('g','9') to 0.2, Pair('9','g') to 0.2,
        Pair('o','0') to 0.2, Pair('0','o') to 0.2,
        Pair('l','1') to 0.3, Pair('1','l') to 0.3,
        Pair('s','5') to 0.3, Pair('5','s') to 0.3
    )

    private fun weightedLevenshtein(s1: String, s2: String): Double {
        val dp = Array(s1.length + 1) { DoubleArray(s2.length + 1) }
        for (i in 0..s1.length) dp[i][0] = i.toDouble()
        for (j in 0..s2.length) dp[0][j] = j.toDouble()
        for (i in 1..s1.length) {
            for (j in 1..s2.length) {
                val cost = if (s1[i-1] == s2[j-1]) 0.0 else confusionCost[Pair(s1[i-1], s2[j-1])] ?: 1.0
                dp[i][j] = minOf(dp[i-1][j] + 1, dp[i][j-1] + 1, dp[i-1][j-1] + cost)
            }
        }
        return dp[s1.length][s2.length]
    }

    fun onImageCaptured(bitmap: Bitmap) {
        _capturedImage.value = bitmap
    }

    fun onImageSelectedFromGallery(context: Context, uri: Uri): Boolean {
        val bitmap = uriToBitmap(context, uri)
        if (bitmap != null) {
            _capturedImage.value = bitmap
            return true
        }
        return false
    }

    fun clearCapturedImage() {
        _capturedImage.value = null
        _detectedNutrients.value = emptyList()
        _recommendation.value = "Silakan periksa kembali angka gizi di atas. Jika sudah sesuai, klik tombol di bawah untuk mendapat rekomendasi"
    }

    fun processConfirmedImage(onComplete: () -> Unit) {
        val bitmap = _capturedImage.value ?: return

        _isProcessing.value = true
        _errorMessage.value = null

        val image = InputImage.fromBitmap(bitmap, 0)

        recognizer.process(image)
            .addOnSuccessListener { visionText ->
                val baselineResult = extractNutrientsBaseline(visionText)
                Log.d("BASELINE", baselineResult.toString())

                extractNutrientsSpatial(visionText)
                onComplete()
                _isProcessing.value = false
            }
            .addOnFailureListener { e ->
                _errorMessage.value = "Gagal membaca teks: ${e.localizedMessage}"
                _isProcessing.value = false
            }
    }

    private fun extractNutrientsSpatial(visionText: Text) {

        Log.d("OCR_RAW", visionText.text)

        val allLines = visionText.textBlocks
            .flatMap { it.lines }
            .filter { it.boundingBox != null }

        if (allLines.isEmpty()) {
            _errorMessage.value = "Teks tidak terdeteksi"
            return
        }

        val resultMap = mutableMapOf<String, Pair<Float, String>>()

        fun normalize(text: String): String {
            return text.lowercase()
                .replace(",", ".")
                .trim()
        }

        fun normalizeNumericOCR(text: String): String {
            return text
                .replace(Regex("(\\d)\\s*9\\b"), "$1 g")
                .replace(Regex("(\\d)9\\b"), "$1 g")
        }

        fun isNumericLine(text: String): Boolean {
            return Regex("\\d").containsMatchIn(text)
        }

        fun isValidUnit(field: String, unit: String): Boolean {
            return when (field) {
                "Energi Total", "Energi dari Lemak" -> unit.contains("kkal") || unit.contains("kal")
                "Natrium" -> unit == "mg"
                else -> unit == "g"
            }
        }

        fun isValidRange(field: String, value: Float): Boolean {
            return when (field) {
                "Energi Total", "Energi dari Lemak" -> value in 0f..1000f
                "Karbohidrat Total" -> value in 0f..300f
                "Gula" -> value in 0f..200f
                "Protein" -> value in 0f..100f
                "Lemak Total", "Lemak Jenuh" -> value in 0f..200f
                "Natrium" -> value in 0f..5000f
                else -> true
            }
        }

        for (line in allLines) {

            val keywordText = normalize(line.text)
            val keywordBox = line.boundingBox ?: continue
            val keywordY = keywordBox.centerY()
            val keywordX = keywordBox.centerX()

            for ((fieldName, aliases) in NutritionDictionary.nutrientAliases) {

                var bestSimilarity = 0.0

                for (alias in aliases) {

                    val cleanAlias = alias.lowercase()

                    if (keywordText.length >= cleanAlias.length) {

                        for (i in 0..(keywordText.length - cleanAlias.length)) {

                            val sub = keywordText.substring(i, i + cleanAlias.length)
                            val distance = weightedLevenshtein(sub, cleanAlias)
                            val similarity =
                                1 - (distance / cleanAlias.length.toDouble())

                            if (similarity > bestSimilarity) {
                                bestSimilarity = similarity
                            }
                        }
                    }
                }

                if (bestSimilarity > 0.7) {

                    var bestScore = Double.MAX_VALUE
                    var bestValue: Float? = null
                    var bestUnit: String? = null

                    for (candidate in allLines) {

                        val rawCandidate = normalize(candidate.text)
                        val candidateText = normalizeNumericOCR(rawCandidate)
                        if (!isNumericLine(candidateText)) continue

                        val box = candidate.boundingBox ?: continue

                        val candidateY = box.centerY()
                        val candidateX = box.centerX()

                        val dyRaw = candidateY - keywordY
                        val dxRaw = candidateX - keywordX

                        // =============================
                        // DIRECTION FILTER
                        // =============================

                        if (dyRaw < -10) continue       // terlalu jauh di atas
                        if (dyRaw > 90) continue        // terlalu jauh di bawah
                        if (dxRaw < -40) continue       // terlalu ke kiri

                        val regex = when (fieldName) {
                            "Energi Total",
                            "Energi dari Lemak" ->
                                Regex("(\\d+[.]?\\d*)\\s*(kkal|kal)")
                            "Natrium" ->
                                Regex("(\\d+[.]?\\d*)\\s*(mg)")
                            else ->
                                Regex("(\\d+[.]?\\d*)\\s*(g)")
                        }

                        val match = regex.find(candidateText)
                        val rawValue = match?.groupValues?.get(1)
                        val unit = match?.groupValues?.get(2)

                        val value = rawValue
                            ?.replace(" ", "")
                            ?.toFloatOrNull()

                        if (value != null && unit != null) {

                            if (!isValidUnit(fieldName, unit)) continue
                            if (!isValidRange(fieldName, value)) continue

                            // =============================
                            // DIRECTION-AWARE SCORING
                            // =============================

                            val dyScore = kotlin.math.abs(dyRaw) * 2
                            val dxScore = kotlin.math.abs(dxRaw) * 0.5

                            var score = dyScore + dxScore

                            val isRightSide = dxRaw >= 0
                            val isVerticallyAligned = kotlin.math.abs(dyRaw) <= 50

                            if (isRightSide) score *= 0.8
                            if (isVerticallyAligned) score *= 0.85

                            if (score < bestScore) {
                                bestScore = score
                                bestValue = value
                                bestUnit = unit
                            }
                        }
                    }

                    if (bestValue != null && bestUnit != null) {
                        resultMap[fieldName] = Pair(bestValue, bestUnit)
                    }
                }
            }
        }

        val finalList = NutritionDictionary.primaryFields.map { field ->

            val data = resultMap[field]

            DetectedNutrient(
                name = field,
                value = data?.first ?: 0f,
                unit = data?.second ?: when(field) {
                    "Energi Total",
                    "Energi dari Lemak" -> "kkal"
                    "Natrium" -> "mg"
                    else -> "g"
                },
                isPrimary = true,
                isDetected = data != null
            )
        }

        _detectedNutrients.value = finalList
    }


    // --- TAMBAH: Fungsi untuk Mengedit Angka secara Manual ---
    fun updateNutrientValue(displayName: String, newValue: Float) {

        _detectedNutrients.value =
            _detectedNutrients.value.map {
                if (it.name == displayName) {
                    it.copy(value = newValue)
                } else {
                    it
                }
            }
        // Reset teks rekomendasi karena angkanya baru berubah
        _recommendation.value = "Silakan periksa kembali angka gizi di atas. Jika sudah sesuai, klik tombol di bawah untuk mendapat rekomendasi"
    }

    private fun getValueByName(name: String): Float {
        return _detectedNutrients.value
            .firstOrNull { it.name == name }
            ?.value ?: 0f
    }

    // --- TAMBAH: Fungsi untuk memanggil AI secara Manual dengan Tombol ---
    fun generateRecommendationManual() {
        val energi = getValueByName("Energi Total")
        val gula = getValueByName("Gula")
        val lemak = getValueByName("Lemak Total")
        val protein = getValueByName("Protein")
        val natrium = getValueByName("Natrium")
        val karbo = getValueByName("Karbohidrat Total")

        _recommendation.value = "Sedang menganalisis kandungan gizi dengan AI..."
        fetchGeminiRecommendation(
            energi.toString(), karbo.toString(), gula.toString(), protein.toString(), lemak.toString(), natrium.toString()
        )
    }

    // ==========================================
    // FUNGSI AI & DATABASE
    // ==========================================
    private fun fetchGeminiRecommendation(energi: String, karbohidrat: String, gula: String, protein: String, lemak: String, natrium: String) {
        viewModelScope.launch {
            try {
                val user = _currentUser.value
                val userProfileText = if (user != null) {
                    val pp = user.physicalProfile
                    val hc = user.healthConfig
                    """
                    Profil Pengguna: 
                    - Nama: ${user.displayName.ifBlank { "Pengguna" }}
                    - Umur: ${pp.age ?: "Tidak diketahui"}
                    - Fisik: BMI ${pp.bmi?.let { String.format("%.1f", it) } ?: "-"}
                    - Kondisi: ${hc.diseases.joinToString(", ").ifBlank { "Sehat" }}
                    - Tujuan: ${hc.goal ?: "Umum"}
                    """.trimIndent()
                } else { "Profil Pengguna: Standar umum." }

                val prompt = """
                    Berdasarkan profil anda, berikan rekomendasi ahli gizi untuk produk ini.
                    
                    $userProfileText
                    
                    Kandungan (per sajian): Energi $energi kkal, karbohidrat $karbohidrat g, Gula $gula g, Lemak $lemak g, Protein $protein g, Natrium $natrium mg.
                    
                    Aturan:
                    1. Berikan rekomendasi akhir: AMAN, BATASI, atau HINDARI.
                    2. Perhatikan penyakit pengguna (misal Hipertensi harus batasi Natrium) dan perhatikan aturan WHO.
                    3. Maksimal 3 kalimat singkat.
                    4. Awali dengan kalimat "Berdasarkan profil anda".
                    5. Jika profil kosong/null/belum diatur rekomendasikan sesuai standar umum dari WHO.
                """.trimIndent()

                val response = generativeModel.generateContent(prompt)
                _recommendation.value = response.text?.trim() ?: "Gagal memuat rekomendasi."
            } catch (e: Exception) {
                _recommendation.value = "Koneksi AI gagal."
            }
        }
    }

    fun saveScanDataToDatabase(labelName: String, onComplete: (Boolean) -> Unit) {

        val uid = auth.currentUser?.uid ?: return onComplete(false)

        val historyData = hashMapOf(
            "userId" to uid,
            "labelName" to labelName,
            "totalEnergi" to detectedNutrients.value
                .firstOrNull { it.name == "Energi Total" }
                ?.value
                ?.toString(),
            "recommendation" to recommendation.value,
            "timestamp" to System.currentTimeMillis(),
            "nutrients" to detectedNutrients.value.map {
                mapOf(
                    "name" to it.name,
                    "value" to it.value,
                    "unit" to it.unit,
                    "isPrimary" to it.isPrimary,
                    "isDetected" to it.isDetected
                )
            }
        )

        firestore.collection("users")
            .document(uid)
            .collection("scan_history")
            .add(historyData)
            .addOnSuccessListener { onComplete(true) }
            .addOnFailureListener { onComplete(false) }
    }

    //untuk perbandingan dengan baseline
    fun extractNutrientsBaseline(visionText: Text): Map<String, Float> {

        val text = visionText.text.lowercase().replace(",", ".")

        fun extractValue(keyword: String, unit: String): Float {
            val pattern = Regex("$keyword[^0-9]*(\\d+[.]?\\d*)\\s*$unit")
            val match = pattern.find(text)
            return match?.groupValues?.get(1)?.toFloatOrNull() ?: 0f
        }

        return mapOf(
            "Energi" to extractValue("energi", "kkal"),
            "Karbohidrat" to extractValue("karbohidrat", "g"),
            "Gula" to extractValue("gula", "g"),
            "Protein" to extractValue("protein", "g"),
            "Lemak" to extractValue("lemak", "g"),
            "Natrium" to extractValue("natrium", "mg")
        )
    }


}