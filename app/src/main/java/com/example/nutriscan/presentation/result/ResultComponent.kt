package com.example.nutriscan.presentation.result

import android.graphics.Bitmap
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.nutriscan.domain.model.DetectedNutrient
import com.example.nutriscan.presentation.theme.*

// ─────────────────────────────────────────────────────────────
//  TOP BAR
// ─────────────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultTopBar(onNavigateBack: () -> Unit) {
    TopAppBar(
        title = {
            Text(
                text = "Hasil Scan Nilai Gizi",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        },
        navigationIcon = {
            IconButton(onClick = onNavigateBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Kembali")
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = BackgroundCream)
    )
}

// ─────────────────────────────────────────────────────────────
//  CAPTURED IMAGE SECTION
// ─────────────────────────────────────────────────────────────
@Composable
fun CapturedImageSection(bitmap: Bitmap?) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .clip(RoundedCornerShape(20.dp))
            .border(1.dp, BorderColor, RoundedCornerShape(20.dp))
            .background(SurfaceVariant),
        contentAlignment = Alignment.Center
    ) {
        if (bitmap != null) {
            Image(
                bitmap = bitmap.asImageBitmap(),
                contentDescription = "Foto Label",
                modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(20.dp)),
                contentScale = ContentScale.Fit
            )
        } else {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .background(brush = GradientCard, shape = CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Image, contentDescription = null, tint = Color.White, modifier = Modifier.size(28.dp))
                }
                Spacer(Modifier.height(8.dp))
                Text("Tidak ada foto", color = TextSecondary, fontSize = 13.sp)
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────
//  RECOMMENDATION BOX — gradient card with blob
// ─────────────────────────────────────────────────────────────
@Composable
fun RecommendationBox(text: String) {
    Box(
        modifier = Modifier
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
            Text(text = text, color = Color.White.copy(alpha = 0.92f), style = MaterialTheme.typography.bodyMedium, lineHeight = 22.sp)
        }
    }
}

// ─────────────────────────────────────────────────────────────
//  ACTION BUTTONS — gradient save, outlined retake
// ─────────────────────────────────────────────────────────────
@Composable
fun ActionButtons(
    onSave: () -> Unit,
    onRetake: () -> Unit,
    isProcessing: Boolean
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(if (isProcessing) Brush.horizontalGradient(listOf(Color(0xFFBBCCCE), Color(0xFFCCD5D5))) else GradientSuccess)
                .clickable(enabled = !isProcessing) { onSave() },
            contentAlignment = Alignment.Center
        ) {
            Text("Simpan Hasil", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp, letterSpacing = 0.3.sp)
        }

        OutlinedButton(
            onClick = onRetake,
            enabled = !isProcessing,
            modifier = Modifier.fillMaxWidth().height(52.dp),
            shape = RoundedCornerShape(14.dp),
            border = BorderStroke(1.5.dp, if (isProcessing) Color.LightGray else ErrorRed),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = ErrorRed,
                disabledContentColor = Color.LightGray
            )
        ) {
            Text("Ambil Ulang", fontWeight = FontWeight.Bold, fontSize = 15.sp)
        }
    }
}

// ─────────────────────────────────────────────────────────────
//  NUTRIENT LIST ITEM — card with color-coded left border
// ─────────────────────────────────────────────────────────────
@Composable
fun NutrientList(nutrients: List<DetectedNutrient>, onEditClick: (DetectedNutrient) -> Unit) {
    LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
        items(nutrients) { nutrient ->
            NutrientListItem(nutrient = nutrient, onEditClick = { onEditClick(nutrient) })
        }
    }
}

@Composable
fun NutrientListItem(nutrient: DetectedNutrient, onEditClick: () -> Unit) {
    val progress = calculateProgress(nutrient)
    val statusColor = when {
        !nutrient.isPrimary -> TertiarySage
        progress > 0.7f -> NutritionDanger
        progress > 0.4f -> NutritionWarning
        else -> NutritionSafe
    }

    Card(
        modifier = Modifier.fillMaxWidth().clickable { onEditClick() },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            // Color accent bar
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
                        Text("${nutrient.value} ${nutrient.unit}", color = TextSecondary, fontSize = 13.sp)
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
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
                            Spacer(Modifier.width(8.dp))
                        }
                        Icon(Icons.Default.Edit, contentDescription = "Edit", tint = TertiarySage, modifier = Modifier.size(18.dp))
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

fun calculateProgress(nutrient: DetectedNutrient): Float = when (nutrient.name) {
    "Energi Total"        -> (nutrient.value / 2150f).coerceIn(0f, 1f)
    "Karbohidrat Total"   -> (nutrient.value / 325f).coerceIn(0f, 1f)
    "Gula"                -> (nutrient.value / 50f).coerceIn(0f, 1f)
    "Lemak Total"         -> (nutrient.value / 67f).coerceIn(0f, 1f)
    "Natrium"             -> (nutrient.value / 2000f).coerceIn(0f, 1f)
    else                  -> 0f
}