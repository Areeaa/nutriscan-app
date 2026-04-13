package com.example.nutriscan.domain.model

data class User(
    val uid: String = "",
    val displayName: String = "",
    val email: String = "",
    val physicalProfile: PhysicalProfile = PhysicalProfile(),
    val healthConfig: HealthConfig = HealthConfig(),
    val isOnboardingFinished: Boolean = false
)

data class PhysicalProfile(
    val gender: String? = null, // "Laki-laki" / "Perempuan"
    val height: Int? = null,    // cm
    val weight: Int? = null,
    val age: Int? = null

) {
    val bmi: Double?
        get() {
            if (height != null && weight != null && height > 0) {
                val heightInMeter = height / 100.0
                return weight / (heightInMeter * heightInMeter)
            }
            return null
        }
}

data class HealthConfig(
    val goal: String? = null,
    val diseases: List<String> = emptyList(),
    val dietPreference: String? = null,
    val activityLevel: String? = null
)