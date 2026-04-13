package com.example.nutriscan.domain.model



data class ScanHistory(
    val id: String = "",
    val labelName: String = "",
    val totalEnergi: String = "-",
    val recommendation: String = "",
    val timestamp: Long = 0L,
    val nutrients: List<DetectedNutrient> = emptyList()
)