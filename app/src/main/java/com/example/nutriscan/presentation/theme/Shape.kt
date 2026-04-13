package com.example.nutriscan.presentation.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

// Definisi Radius Spesifik
val RadiusSmall = RoundedCornerShape(8.dp)
val RadiusMedium = RoundedCornerShape(12.dp)
val RadiusLarge = RoundedCornerShape(16.dp)
val RadiusExtraLarge = RoundedCornerShape(20.dp)
val RadiusFull = RoundedCornerShape(50.dp) // Untuk Tombol (Pill Shape)

val Shapes = Shapes(
    extraSmall = RadiusSmall, // 8dp
    small = RadiusMedium,     // 12dp
    medium = RadiusLarge,     // 16dp
    large = RadiusExtraLarge, // 20dp
    extraLarge = RadiusFull   // 50dp
)