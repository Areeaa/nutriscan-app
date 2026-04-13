package com.example.nutriscan.domain.model

import androidx.compose.ui.graphics.Color

data class NutrientData(
    val name: String,
    val value: String,
    val indicatorColor: Color,
    val progress: Float
)

data class DetectedNutrient(
    val name: String,
    val value: Float,
    val unit: String,
    val isPrimary: Boolean,
    val isDetected: Boolean
)