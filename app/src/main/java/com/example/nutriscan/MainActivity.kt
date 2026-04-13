package com.example.nutriscan


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.nutriscan.presentation.navigation.AppNavigation
import com.example.nutriscan.presentation.theme.NutritionAppTheme

import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NutritionAppTheme {
                AppNavigation()
            }
        }
    }
}

