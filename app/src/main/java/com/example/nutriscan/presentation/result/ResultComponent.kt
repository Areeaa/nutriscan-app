package com.example.nutriscan.presentation.result

import android.graphics.Bitmap
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.nutriscan.domain.model.DetectedNutrient
import com.example.nutriscan.domain.model.NutrientData
import com.example.nutriscan.presentation.theme.BackgroundCream
import com.example.nutriscan.presentation.theme.BorderColor
import com.example.nutriscan.presentation.theme.ErrorRed
import com.example.nutriscan.presentation.theme.PrimaryTeal
import com.example.nutriscan.presentation.theme.SuccessGreen
import com.example.nutriscan.presentation.theme.SurfaceWhite
import com.example.nutriscan.presentation.theme.TextPrimary
import com.example.nutriscan.presentation.theme.TextSecondary

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

@Composable
fun CapturedImageSection(bitmap: Bitmap?) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(PrimaryTeal),
        contentAlignment = Alignment.Center
    ) {
        if (bitmap != null) {
            Image(
                bitmap = bitmap.asImageBitmap(),
                contentDescription = "Foto Label",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Fit
            )
        } else {
            Icon(Icons.Default.Image, contentDescription = "Placeholder", tint = Color.White, modifier = Modifier.size(48.dp))
        }
    }
}


@Composable
fun RecommendationBox(text: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(PrimaryTeal)
            .padding(16.dp)
    ) {
        Text(
            text = text,
            color = Color.White,
            style = MaterialTheme.typography.bodyMedium,
            lineHeight = 22.sp
        )
    }
}

@Composable
fun ActionButtons(
    onSave: () -> Unit,
    onRetake: () -> Unit,
    isProcessing: Boolean
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Button(
            onClick = onSave,
            enabled = !isProcessing,
            modifier = Modifier.fillMaxWidth().height(50.dp),
            shape = RoundedCornerShape(25.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = SuccessGreen,
                contentColor = Color.White,
                disabledContainerColor = Color.LightGray,
                disabledContentColor = Color.White
            )
        ) {
            Text("Simpan Hasil", fontWeight = FontWeight.Bold)
        }

        OutlinedButton(
            onClick = onRetake,
            enabled = !isProcessing,
            modifier = Modifier.fillMaxWidth().height(50.dp),
            shape = RoundedCornerShape(25.dp),
            border = BorderStroke(1.dp, if (isProcessing) Color.LightGray else ErrorRed),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = ErrorRed,
                disabledContentColor = Color.LightGray
            )
        ) {
            Text("Ambil Ulang", fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun NutrientList(
    nutrients: List<DetectedNutrient>,
    onEditClick: (DetectedNutrient) -> Unit
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        items(nutrients) { nutrient ->
            NutrientListItem(
                nutrient = nutrient,
                onEditClick = { onEditClick(nutrient) }
            )
        }
    }
}

@Composable
fun NutrientListItem(
    nutrient: DetectedNutrient,
    onEditClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onEditClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
        border = BorderStroke(1.dp, BorderColor)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    Text(
                        text = nutrient.name,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary
                    )
                    Text(
                        text = "${nutrient.value} ${nutrient.unit}",
                        color = TextSecondary
                    )
                }

                Icon(
                    Icons.Default.Edit,
                    contentDescription = "Edit",
                    tint = PrimaryTeal
                )
            }

            if (nutrient.isPrimary) {
                Spacer(modifier = Modifier.height(8.dp))

                val progress = calculateProgress(nutrient)

                LinearProgressIndicator(
                    progress = progress,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = if (progress > 0.7f) ErrorRed else SuccessGreen,
                    trackColor = BorderColor
                )
            }
        }
    }
}
fun calculateProgress(nutrient: DetectedNutrient): Float {
    return when (nutrient.name) {
        "Energi Total" -> (nutrient.value / 2150f).coerceIn(0f, 1f)
        "Karbohidrat Total" -> (nutrient.value / 325f).coerceIn(0f, 1f)
        "Gula" -> (nutrient.value / 50f).coerceIn(0f, 1f)
        "Lemak Total" -> (nutrient.value / 67f).coerceIn(0f, 1f)
        "Natrium" -> (nutrient.value / 2000f).coerceIn(0f, 1f)
        else -> 0f
    }
}