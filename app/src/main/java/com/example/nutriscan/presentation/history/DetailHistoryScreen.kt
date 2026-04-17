package com.example.nutriscan.presentation.history

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.nutriscan.domain.model.ScanHistory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailHistoryScreen(
    history: ScanHistory,
    onNavigateBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Detail Riwayat Scan Label",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Kembali",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ProductTitleHeader(title = history.labelName)
            ImagePlaceholderCard()
            Spacer(modifier = Modifier.height(24.dp))

            TotalEnergyCard(energyValue = history.totalEnergi)
            Spacer(modifier = Modifier.height(16.dp))

            NutrientDetailsCard(nutrients = history.nutrients)
            Spacer(modifier = Modifier.height(16.dp))

            RecommendationCard(recommendationText = history.recommendation)
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

// ==========================================
// KOMPONEN-KOMPONEN UI DETAIL HISTORY
// ==========================================

@Composable
fun ProductTitleHeader(title: String) {
    Text(
        text = title,
        fontSize = 22.sp,
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Center,
        color = MaterialTheme.colorScheme.onSurface,
        modifier = Modifier.padding(vertical = 16.dp)
    )
}

@Composable
fun ImagePlaceholderCard() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .background(
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                shape = RoundedCornerShape(16.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.Image,
            contentDescription = "Image Placeholder",
            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
            modifier = Modifier.size(64.dp)
        )
    }
}

@Composable
fun TotalEnergyCard(energyValue: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Total Energi",
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "$energyValue kkal",
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
fun NutrientDetailsCard(
    nutrients: List<com.example.nutriscan.domain.model.DetectedNutrient>
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            // Buang Energi Total dari list detail
            val filteredNutrients = nutrients.filter {
                !it.name.contains("Energi", ignoreCase = true)
            }

            filteredNutrients.forEachIndexed { index, nutrient ->

                Column(modifier = Modifier.padding(vertical = 12.dp)) {

                    Text(
                        text = nutrient.name,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 14.sp
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "${nutrient.value} ${nutrient.unit}",
                        color = if (nutrient.isDetected) {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                        },
                        fontSize = 13.sp
                    )
                }

                if (index < filteredNutrients.size - 1) {
                    HorizontalDivider(
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        thickness = 1.dp
                    )
                }
            }
        }
    }
}

@Composable
fun RecommendationCard(recommendationText: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primary
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Rekomendasi Konsumsi",
                color = MaterialTheme.colorScheme.onPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = recommendationText,
                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.9f),
                fontSize = 14.sp,
                lineHeight = 22.sp
            )
        }
    }
}