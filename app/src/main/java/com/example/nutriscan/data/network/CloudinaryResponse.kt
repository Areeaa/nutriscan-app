package com.example.nutriscan.data.network

import com.google.gson.annotations.SerializedName

data class CloudinaryResponse(
    @SerializedName("secure_url")
    val secureUrl: String // Ini adalah link HTTPS yang akan kita simpan ke Firestore
)