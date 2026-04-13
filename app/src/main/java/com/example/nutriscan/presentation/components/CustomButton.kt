package com.example.nutriscan.presentation.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.nutriscan.presentation.theme.NutritionAppTheme

@Composable
fun ButtonPrimary(
    text: String,                       // Judul tombol
    onClick: () -> Unit,                // Aksi/Tujuan
    modifier: Modifier = Modifier,      // Modifier tambahan (opsional)
    isEnabled: Boolean = true,          // Status Aktif/Non-aktif
    containerColor: Color = MaterialTheme.colorScheme.primary, // Default warna Primary
    contentColor: Color = Color.White   // Default warna teks Putih
) {
    Button(
        onClick = onClick,
        enabled = isEnabled,
        modifier = modifier
            .fillMaxWidth()     // Lebar penuh
            .height(50.dp),     // Tinggi standar 50dp
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor,
            disabledContainerColor = MaterialTheme.colorScheme.outlineVariant, // Warna saat mati
            disabledContentColor = Color.White
        ),

        // Mengambil bentuk Pill/Capsule dari Shapes.kt (extraLarge = RadiusFull)
        shape = MaterialTheme.shapes.extraLarge
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Medium
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ButtonPrimaryPreview() {
    NutritionAppTheme {
        ButtonPrimary(
            text = "Masuk Sekarang",
            isEnabled = false,
            onClick = {}
        )
    }
}