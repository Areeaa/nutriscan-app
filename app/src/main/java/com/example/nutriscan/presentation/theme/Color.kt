package com.example.nutriscan.presentation.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// --- Core Brand Colors ---
val PrimaryTeal         = Color(0xFF2D7A87)
val PrimaryDeep         = Color(0xFF1A4A54)
val PrimaryLight        = Color(0xFF4AA8B8)
val SecondaryTerracotta = Color(0xFFD48C70)
val TertiarySage        = Color(0xFF8FA8A0)

// --- Backgrounds & Surfaces ---
val BackgroundCream  = Color(0xFFF5F4F1)
val SurfaceWhite     = Color(0xFFFEFEFD)
val SurfaceVariant   = Color(0xFFEDF1F1)
val GlassSurface     = Color(0xCCFFFFFF)

val GrayDisabled     = Color(0xFFD9D9D9)
val GrayDisabledDark = Color(0xFF3E3E3E)

// --- Typography ---
val TextPrimary   = Color(0xFF1E3235)
val TextSecondary = Color(0xFF5E7375)
val TextTertiary  = Color(0xFFAAB5B6)

// --- Functional ---
val BorderColor   = Color(0xFFE3E8E8)
val ErrorRed      = Color(0xFFBA1A1A)
val SuccessGreen  = Color(0xFF3DAD72)
val WarningYellow = Color(0xFFF5C842)

// --- Nutrition Traffic-Light ---
val NutritionSafe    = Color(0xFF3DAD72)
val NutritionWarning = Color(0xFFF5C842)
val NutritionDanger  = Color(0xFFEB5757)

// --- Gradient Brushes ---
val GradientHero = Brush.linearGradient(
    colors = listOf(Color(0xFF1A4A54), Color(0xFF2D7A87), Color(0xFF3D9E8A))
)
val GradientHeroVertical = Brush.verticalGradient(
    colors = listOf(Color(0xFF1A4A54), Color(0xFF2D7A87), Color(0xFF48AEAD))
)
val GradientCard = Brush.linearGradient(
    colors = listOf(Color(0xFF2D7A87), Color(0xFF3D9E8A))
)
val GradientButton = Brush.horizontalGradient(
    colors = listOf(Color(0xFF2D7A87), Color(0xFF3DAD8A))
)
val GradientWarm = Brush.linearGradient(
    colors = listOf(Color(0xFFD48C70), Color(0xFFE8A882))
)
val GradientSuccess = Brush.horizontalGradient(
    colors = listOf(Color(0xFF3DAD72), Color(0xFF52C98A))
)
val GradientDanger = Brush.horizontalGradient(
    colors = listOf(Color(0xFFEB5757), Color(0xFFFF7A7A))
)

// --- Blob Colors ---
val BlobTeal1      = Color(0x3348AEAD)
val BlobTeal2      = Color(0x222D7A87)
val BlobTerracotta = Color(0x22D48C70)
val BlobSage       = Color(0x228FA8A0)