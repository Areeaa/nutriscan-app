package com.example.nutriscan.presentation.result

import android.graphics.Bitmap
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.nutriscan.domain.model.DetectedNutrient
import com.example.nutriscan.presentation.scanner.ScannerViewModel
import com.example.nutriscan.presentation.theme.*

@Composable
fun ResultScreen(
    viewModel: ScannerViewModel,
    onNavigateBack: () -> Unit,
    onRetake: () -> Unit,
    onSave: (String) -> Unit
) {

    val capturedImage by viewModel.capturedImage.collectAsState()
    val detectedNutrients by viewModel.detectedNutrients.collectAsState()
    val recommendation by viewModel.recommendation.collectAsState()
    val isProcessing by viewModel.isProcessing.collectAsState()

    ResultContent(
        capturedImage = capturedImage,
        detectedNutrients = detectedNutrients,
        recommendation = recommendation,
        isProcessing = isProcessing,
        onNavigateBack = onNavigateBack,
        onRetake = {
            viewModel.clearCapturedImage()
            onRetake()
        },
        onSave = onSave,
        onEditNutrient = { name, newValue ->
            viewModel.updateNutrientValue(name, newValue)
        },
        onGenerateAI = {
            viewModel.generateRecommendationManual()
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultContent(
    capturedImage: Bitmap?,
    detectedNutrients: List<DetectedNutrient>,
    recommendation: String,
    isProcessing: Boolean,
    onNavigateBack: () -> Unit,
    onRetake: () -> Unit,
    onSave: (String) -> Unit,
    onEditNutrient: (String, Float) -> Unit,
    onGenerateAI: () -> Unit
) {

    var showBottomSheet by remember { mutableStateOf(false) }
    var labelName by remember { mutableStateOf("") }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var showEditDialog by remember { mutableStateOf(false) }
    var selectedNutrient by remember { mutableStateOf<DetectedNutrient?>(null) }
    var editValueText by remember { mutableStateOf("") }

    Scaffold(
        topBar = { ResultTopBar(onNavigateBack = onNavigateBack) },
        containerColor = BackgroundCream
    ) { paddingValues ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            item { Spacer(modifier = Modifier.height(16.dp)) }

            item {
                CapturedImageSection(bitmap = capturedImage)
            }

            item {
                Text(
                    text = "Rincian Hasil (Ketuk untuk Edit)",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            }

            if (detectedNutrients.isEmpty() && !isProcessing) {
                item {
                    Text(
                        "Data gizi tidak terdeteksi",
                        color = Color.Gray,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                items(detectedNutrients) { nutrient ->
                    NutrientListItem(
                        nutrient = nutrient,
                        onEditClick = {
                            selectedNutrient = nutrient
                            editValueText = nutrient.value.toString()
                            showEditDialog = true
                        }
                    )
                }
            }

            item {
                Text(
                    text = "Rekomendasi Konsumsi",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            }

            item {
                RecommendationBox(text = recommendation)
            }

            item {
                Button(
                    onClick = onGenerateAI,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    enabled = !recommendation.contains("Sedang menganalisis"),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryTeal)
                ) {
                    Text("Minta Rekomendasi AI", fontWeight = FontWeight.Bold, color = Color.White)
                }
            }

            item {
                ActionButtons(
                    onSave = { showBottomSheet = true },
                    onRetake = onRetake,
                    isProcessing = isProcessing
                )
            }

            item { Spacer(modifier = Modifier.height(32.dp)) }
        }
    }

    // ===== EDIT DIALOG =====
    if (showEditDialog && selectedNutrient != null) {
        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            title = { Text("Koreksi ${selectedNutrient!!.name}", fontWeight = FontWeight.Bold) },
            text = {
                OutlinedTextField(
                    value = editValueText,
                    onValueChange = { editValueText = it },
                    label = { Text("Masukkan Angka yang Benar") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        val newValue = editValueText.toFloatOrNull() ?: 0f
                        onEditNutrient(selectedNutrient!!.name, newValue)
                        showEditDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryTeal)
                ) { Text("Simpan") }
            },
            dismissButton = {
                TextButton(onClick = { showEditDialog = false }) {
                    Text("Batal", color = TextPrimary)
                }
            },
            containerColor = Color.White
        )
    }

    // ===== BOTTOM SHEET =====
    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { showBottomSheet = false; labelName = "" },
            sheetState = sheetState,
            containerColor = Color.White
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Text(
                    "Ingin Menyimpan Hasil Scan?",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = labelName,
                    onValueChange = { labelName = it },
                    placeholder = { Text("Masukkan Nama Label...") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        showBottomSheet = false
                        onSave(labelName)
                        labelName = ""
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = labelName.isNotBlank()
                ) {
                    Text("Simpan")
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}