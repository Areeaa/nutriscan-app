package com.example.nutriscan.presentation.result

import android.graphics.Bitmap
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
        onEditNutrient = { name, newValue -> viewModel.updateNutrientValue(name, newValue) },
        onGenerateAI = { viewModel.generateRecommendationManual() }
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
            item { Spacer(Modifier.height(16.dp)) }

            item { CapturedImageSection(bitmap = capturedImage) }

            item {
                Text(
                    "Rincian Hasil",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    "Ketuk baris untuk mengoreksi nilai",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }

            if (detectedNutrients.isEmpty() && !isProcessing) {
                item {
                    Text(
                        "Data gizi tidak terdeteksi",
                        color = TextSecondary,
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
                    "Rekomendasi Konsumsi",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            }

            item { RecommendationBox(text = recommendation) }

            // Gradient AI button
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(
                            if (!recommendation.contains("Sedang menganalisis")) GradientWarm
                            else androidx.compose.ui.graphics.Brush.horizontalGradient(
                                listOf(Color(0xFFBBCCCE), Color(0xFFCCD5D5))
                            )
                        )
                        .clickable(enabled = !recommendation.contains("Sedang menganalisis")) {
                            onGenerateAI()
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "🤖  Analisis dengan AI",
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontSize = 15.sp,
                        letterSpacing = 0.3.sp
                    )
                }
            }

            item {
                ActionButtons(
                    onSave = { showBottomSheet = true },
                    onRetake = onRetake,
                    isProcessing = isProcessing
                )
            }

            item { Spacer(Modifier.height(32.dp)) }
        }
    }

    // ── Edit dialog ──
    if (showEditDialog && selectedNutrient != null) {
        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            title = {
                Text("Koreksi ${selectedNutrient!!.name}", fontWeight = FontWeight.Bold, color = TextPrimary)
            },
            text = {
                OutlinedTextField(
                    value = editValueText,
                    onValueChange = { editValueText = it },
                    label = { Text("Masukkan angka yang benar") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryTeal,
                        unfocusedBorderColor = BorderColor
                    )
                )
            },
            confirmButton = {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(GradientButton)
                        .clickable {
                            val newValue = editValueText.toFloatOrNull() ?: 0f
                            onEditNutrient(selectedNutrient!!.name, newValue)
                            showEditDialog = false
                        }
                        .padding(horizontal = 20.dp, vertical = 10.dp)
                ) {
                    Text("Simpan", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditDialog = false }) {
                    Text("Batal", color = TextSecondary)
                }
            },
            containerColor = SurfaceWhite,
            shape = RoundedCornerShape(20.dp)
        )
    }

    // ── Save bottom sheet ──
    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { showBottomSheet = false; labelName = "" },
            sheetState = sheetState,
            containerColor = SurfaceWhite,
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 28.dp)
                    .padding(top = 8.dp, bottom = 36.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("💾", fontSize = 32.sp)
                Spacer(Modifier.height(10.dp))
                Text(
                    "Simpan Hasil Scan",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = TextPrimary
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    "Beri nama label agar mudah dikenali di riwayat",
                    color = TextSecondary,
                    fontSize = 13.sp,
                    textAlign = TextAlign.Center
                )

                Spacer(Modifier.height(20.dp))

                OutlinedTextField(
                    value = labelName,
                    onValueChange = { labelName = it },
                    placeholder = { Text("Nama label makanan…", color = TextTertiary) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryTeal,
                        unfocusedBorderColor = BorderColor
                    )
                )

                Spacer(Modifier.height(20.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(
                            if (labelName.isNotBlank()) GradientButton
                            else androidx.compose.ui.graphics.Brush.horizontalGradient(
                                listOf(Color(0xFFBBCCCE), Color(0xFFCCD5D5))
                            )
                        )
                        .clickable(enabled = labelName.isNotBlank()) {
                            showBottomSheet = false
                            onSave(labelName)
                            labelName = ""
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text("Simpan", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                }
            }
        }
    }
}