package com.example.nutriscan.presentation.scanner

import android.graphics.Bitmap
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.FlashOff
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.rounded.DocumentScanner
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.nutriscan.presentation.theme.PrimaryTeal
import com.example.nutriscan.presentation.theme.SurfaceVariant
import com.example.nutriscan.presentation.theme.TextPrimary

@Composable
fun ScannerTopBar(
    onNavigateBack: () -> Unit,
    onGalleryClick: () -> Unit,
    isFlashOn: Boolean,
    onFlashClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth().padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        IconButton(onClick = onNavigateBack, modifier = Modifier.background(SurfaceVariant, CircleShape).size(48.dp)) {
            Icon(Icons.Default.ArrowBack, "Kembali", tint = TextPrimary)
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            // TOMBOL FLASH TETAP ADA
            IconButton(onClick = onFlashClick, modifier = Modifier.background(SurfaceVariant, CircleShape).size(48.dp)) {
                Icon(if (isFlashOn) Icons.Default.FlashOn else Icons.Default.FlashOff, "Toggle Flash", tint = TextPrimary)
            }
            IconButton(onClick = onGalleryClick, modifier = Modifier.background(SurfaceVariant, CircleShape).size(48.dp)) {
                Icon(Icons.Default.PhotoLibrary, "Pilih dari Galeri", tint = TextPrimary)
            }
        }
    }
}

@Composable
fun ScannerInstructionTooltip(modifier: Modifier = Modifier) {
    Surface(color = SurfaceVariant, shape = CircleShape, modifier = modifier) {
        Text("Arahkan kamera secara sejajar", style = MaterialTheme.typography.labelMedium, color = TextPrimary, fontWeight = FontWeight.Medium, modifier = Modifier.padding(horizontal = 24.dp, vertical = 10.dp))
    }
}

@Composable
fun ScannerFab(onScanClick: () -> Unit, modifier: Modifier = Modifier) {
    FloatingActionButton(onClick = onScanClick, containerColor = Color.White, contentColor = PrimaryTeal, shape = RoundedCornerShape(16.dp), modifier = modifier.size(65.dp)) {
        Icon(Icons.Rounded.DocumentScanner, "Scan", modifier = Modifier.size(32.dp))
    }
}

// ==========================================
// PREVIEW SEDERHANA (DIKEMBALIKAN KE AWAL)
// ==========================================
@Composable
fun ImagePreviewContent(
    bitmap: Bitmap,
    isProcessing: Boolean,
    onRetake: () -> Unit,
    onConfirm: () -> Unit // Tidak lagi menerima parameter Bitmap
) {
    Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
        Image(
            bitmap = bitmap.asImageBitmap(),
            contentDescription = "Preview Gambar",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Fit
        )

        IconButton(
            onClick = onRetake, enabled = !isProcessing,
            modifier = Modifier.align(Alignment.TopStart).padding(16.dp).background(SurfaceVariant, CircleShape).size(48.dp)
        ) {
            Icon(Icons.Default.ArrowBack, "Ulangi", tint = TextPrimary)
        }

        Row(modifier = Modifier.fillMaxWidth().align(Alignment.BottomCenter).padding(32.dp), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            OutlinedButton(
                onClick = onRetake, enabled = !isProcessing,
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                border = BorderStroke(1.dp, Color.White), shape = RoundedCornerShape(12.dp), modifier = Modifier.weight(1f).height(50.dp)
            ) { Text("Ulangi") }

            Button(
                onClick = onConfirm,
                enabled = !isProcessing,
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryTeal),
                shape = RoundedCornerShape(12.dp), modifier = Modifier.weight(1f).height(50.dp)
            ) {
                if (isProcessing) CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                else Text("Gunakan")
            }
        }
    }
}