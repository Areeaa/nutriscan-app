package com.example.nutriscan.presentation.onboarding

data class OnboardingState(
    val goal: String = "",
    val diseases: List<String> = emptyList(),
    val diet: String = "",
    val activityLevel: String = "",
    val gender: String = "",
    val age: String = "",
    val height: String = "",
    val weight: String = "",
    val isLoading: Boolean = false
)