package com.example.nutriscan.presentation.history

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.nutriscan.domain.model.DetectedNutrient
import com.example.nutriscan.domain.model.ScanHistory
import com.example.nutriscan.presentation.theme.*
import kotlin.math.cos
import kotlin.math.sin

// ─────────────────────────────────────────────────────────────
//  MAIN SCREEN
// ─────────────────────────────────────────────────────────────

@Composable
fun DetailHistoryScreen(
    history: ScanHistory,
    onNavigateBack: () -> Unit
) {
    Scaffold(
        containerColor = BackgroundCream
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Gradient header
            item {
                DetailHistoryHeader(
                    labelName = history.labelName,
                    onNavigateBack = onNavigateBack
                )
            }

            item { Spacer(Modifier.height(20.dp)) }

            // Total Energy card
            item {
                TotalEnergyCard(
                    energyValue = history.totalEnergi,
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
            }

            item { Spacer(Modifier.height(20.dp)) }

            // Section title
            item {
                Text(
                    "Rincian Nilai Gizi",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
                Spacer(Modifier.height(10.dp))
            }

            // Nutrient list
            val filteredNutrients = history.nutrients.filter {
                !it.name.contains("Energi", ignoreCase = true)
            }
            items(filteredNutrients) { nutrient ->
                NutrientDetailItem(
                    nutrient = nutrient,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 5.dp)
                )
            }

            item { Spacer(Modifier.height(20.dp)) }

            // Recommendation
            item {
                RecommendationCard(
                    recommendationText = history.recommendation,
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
            }

            item { Spacer(Modifier.height(40.dp)) }
        }
    }
}

// ─────────────────────────────────────────────────────────────
//  GRADIENT HEADER
// ─────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DetailHistoryHeader(
    labelName: String,
    onNavigateBack: () -> Unit
) {
    val inf = rememberInfiniteTransition(label = "detailBlob")
    val t by inf.animateFloat(0f, 1f, infiniteRepeatable(tween(7000, easing = LinearEasing)), label = "dt")

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(bottomStart = 28.dp, bottomEnd = 28.dp))
            .background(brush = GradientHeroVertical)
    ) {
        Canvas(modifier = Modifier.fillMaxWidth().height(170.dp)) {
            val w = size.width; val h = size.height
            drawCircle(
                brush = Brush.radialGradient(
                    listOf(Color(0x4448AEAD), Color(0x0048AEAD)),
                    center = Offset(w * 0.85f + cos(t * 6.28f) * 14.dp.toPx(), h * 0.28f),
                    radius = 100.dp.toPx()
                ),
                center = Offset(w * 0.85f + cos(t * 6.28f) * 14.dp.toPx(), h * 0.28f),
                radius = 100.dp.toPx()
            )
            drawCircle(
                brush = Brush.radialGradient(
                    listOf(Color(0x33D48C70), Color(0x00D48C70)),
                    center = Offset(w * 0.12f, h * 0.72f + sin(t * 6.28f) * 10.dp.toPx()),
                    radius = 90.dp.toPx()
                ),
                center = Offset(w * 0.12f, h * 0.72f + sin(t * 6.28f) * 10.dp.toPx()),
                radius = 90.dp.toPx()
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 20.dp)
                .padding(top = 14.dp, bottom = 24.dp)
        ) {
            // Back button + title
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(Color.White.copy(alpha = 0.18f), CircleShape)
                        .clickable { onNavigateBack() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.ArrowBack, "Kembali", tint = Color.White, modifier = Modifier.size(20.dp))
                }
                Spacer(Modifier.width(12.dp))
                Text("Detail Scan", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 18.sp)
            }

            Spacer(Modifier.height(24.dp))

            // Product name + emoji badge
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .background(Color.White.copy(alpha = 0.15f), CircleShape)
                        .border(1.dp, Color.White.copy(alpha = 0.25f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text("🥫", fontSize = 26.sp)
                }
                Spacer(Modifier.width(14.dp))
                Column {
                    Text(
                        labelName,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontSize = 20.sp,
                        lineHeight = 26.sp
                    )
                    Spacer(Modifier.height(4.dp))
                    Box(
                        modifier = Modifier
                            .background(Color.White.copy(alpha = 0.18f), RoundedCornerShape(8.dp))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text("Riwayat Tersimpan", color = Color.White.copy(alpha = 0.85f), fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────
//  TOTAL ENERGY CARD
// ─────────────────────────────────────────────────────────────

@Composable
private fun TotalEnergyCard(energyValue: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(brush = GradientCard)
    ) {
        Canvas(modifier = Modifier.matchParentSize()) {
            drawCircle(
                color = Color(0x22FFFFFF),
                center = Offset(size.width * 0.88f, size.height * 0.18f),
                radius = 64.dp.toPx()
            )
        }
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(Color.White.copy(alpha = 0.2f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text("⚡", fontSize = 24.sp)
            }
            Spacer(Modifier.width(16.dp))
            Column {
                Text(
                    "Total Energi",
                    color = Color.White.copy(alpha = 0.75f),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Normal
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    "$energyValue kkal",
                    color = Color.White,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────
//  NUTRIENT DETAIL ITEM
// ─────────────────────────────────────────────────────────────

@Composable
private fun NutrientDetailItem(nutrient: DetectedNutrient, modifier: Modifier = Modifier) {
    val progress = calculateDetailProgress(nutrient)
    val statusColor = when {
        !nutrient.isPrimary -> TertiarySage
        progress > 0.7f -> NutritionDanger
        progress > 0.4f -> NutritionWarning
        else -> NutritionSafe
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            // Left accent bar
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(topStart = 14.dp, bottomStart = 14.dp))
                    .background(statusColor)
            )

            Column(modifier = Modifier.padding(14.dp).weight(1f)) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column {
                        Text(nutrient.name, fontWeight = FontWeight.SemiBold, color = TextPrimary, fontSize = 14.sp)
                        Spacer(Modifier.height(2.dp))
                        Text("${nutrient.value} ${nutrient.unit}", color = TextSecondary, fontSize = 13.sp)
                    }
                    if (nutrient.isPrimary) {
                        Box(
                            modifier = Modifier
                                .background(statusColor.copy(alpha = 0.12f), RoundedCornerShape(6.dp))
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(
                                when {
                                    progress > 0.7f -> "Tinggi"
                                    progress > 0.4f -> "Sedang"
                                    else -> "Aman"
                                },
                                color = statusColor,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }

                if (nutrient.isPrimary) {
                    Spacer(Modifier.height(8.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(5.dp)
                            .clip(RoundedCornerShape(3.dp))
                            .background(SurfaceVariant)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(progress.coerceIn(0f, 1f))
                                .fillMaxHeight()
                                .clip(RoundedCornerShape(3.dp))
                                .background(
                                    when {
                                        progress > 0.7f -> GradientDanger
                                        progress > 0.4f -> Brush.horizontalGradient(listOf(NutritionWarning, Color(0xFFFDD835)))
                                        else -> GradientSuccess
                                    }
                                )
                        )
                    }
                }
            }
        }
    }
}

private fun calculateDetailProgress(nutrient: DetectedNutrient): Float = when (nutrient.name) {
    "Energi Total"        -> (nutrient.value / 2150f).coerceIn(0f, 1f)
    "Karbohidrat Total"   -> (nutrient.value / 325f).coerceIn(0f, 1f)
    "Gula"                -> (nutrient.value / 50f).coerceIn(0f, 1f)
    "Lemak Total"         -> (nutrient.value / 67f).coerceIn(0f, 1f)
    "Natrium"             -> (nutrient.value / 2000f).coerceIn(0f, 1f)
    "Protein"             -> (nutrient.value / 50f).coerceIn(0f, 1f)
    else                  -> 0f
}

// ─────────────────────────────────────────────────────────────
//  RECOMMENDATION CARD
// ─────────────────────────────────────────────────────────────

@Composable
private fun RecommendationCard(recommendationText: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(brush = GradientCard)
    ) {
        Canvas(modifier = Modifier.matchParentSize()) {
            drawCircle(color = Color(0x22FFFFFF), center = Offset(size.width * 0.88f, size.height * 0.18f), radius = 64.dp.toPx())
            drawCircle(color = Color(0x11FFFFFF), center = Offset(size.width * 0.1f, size.height * 0.85f), radius = 48.dp.toPx())
        }
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .background(Color.White.copy(alpha = 0.2f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text("🤖", fontSize = 16.sp)
                }
                Spacer(Modifier.width(10.dp))
                Text("Rekomendasi AI", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
            }
            Spacer(Modifier.height(12.dp))
            Text(
                text = recommendationText,
                color = Color.White.copy(alpha = 0.92f),
                style = MaterialTheme.typography.bodyMedium,
                lineHeight = 22.sp
            )
        }
    }
}