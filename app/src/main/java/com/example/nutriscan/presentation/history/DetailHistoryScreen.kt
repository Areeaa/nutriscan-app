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
import androidx.compose.ui.graphics.Color
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
                title = { Text("Detail Riwayat Scan Label", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Kembali")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        },
        containerColor = Color(0xFFF9F8F6)
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
            Spacer(modifier = Modifier.height(16.dp))

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
        fontSize = 20.sp,
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Center,
        modifier = Modifier.padding(vertical = 16.dp)
    )
}

@Composable
fun ImagePlaceholderCard() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .background(color = Color(0xFF4A707A), shape = RoundedCornerShape(16.dp)),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.Image,
            contentDescription = "Image Placeholder",
            tint = Color.White,
            modifier = Modifier.size(64.dp)
        )
    }
}

@Composable
fun TotalEnergyCard(energyValue: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF4A707A))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Total Energi", color = Color.White, fontWeight = FontWeight.Bold)
            Text(energyValue, color = Color.White, fontSize = 14.sp)
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
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            // Buang Energi Total dari list detail
            val filteredNutrients = nutrients.filter {
                !it.name.contains("Energi", ignoreCase = true)
            }

            filteredNutrients.forEachIndexed { index, nutrient ->

                Column(modifier = Modifier.padding(vertical = 8.dp)) {

                    Text(
                        text = nutrient.name,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )

                    Text(
                        text = "${nutrient.value} ${nutrient.unit}",
                        color = if (nutrient.isDetected) Color.Gray else Color.LightGray,
                        fontSize = 12.sp
                    )
                }

                if (index < filteredNutrients.size - 1) {
                    HorizontalDivider(
                        color = Color(0xFFEEEEEE),
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
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Rekomendasi Konsumsi", color = Color.White, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = recommendationText,
                color = Color.White,
                fontSize = 14.sp,
                lineHeight = 20.sp
            )
        }
    }
}