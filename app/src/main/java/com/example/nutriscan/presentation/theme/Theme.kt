package com.example.nutriscan.presentation.theme
import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// Skema Warna Terang (Fokus Utama)
private val LightColorScheme = lightColorScheme(
    primary = PrimaryTeal,
    onPrimary = SurfaceWhite, // Teks di atas tombol primary
    secondary = SecondaryTerracotta,
    onSecondary = SurfaceWhite,
    tertiary = TertiarySage,

    background = BackgroundCream,
    onBackground = TextPrimary,

    surface = SurfaceWhite,
    onSurface = TextPrimary,

    surfaceVariant = SurfaceVariant, // Container sekunder
    onSurfaceVariant = TextSecondary,

    outline = BorderColor,
    error = ErrorRed,

    outlineVariant = GrayDisabled, // Kita pakai slot ini untuk warna disabled


)

// Skema Gelap (Optional/Auto-generated untuk fallback)
private val DarkColorScheme = darkColorScheme(
    primary = PrimaryTeal,
    onPrimary = SurfaceWhite,
    secondary = SecondaryTerracotta,
    background = TextPrimary, // Background jadi gelap
    surface = TextPrimary,
    onSurface = SurfaceWhite,
    outlineVariant = GrayDisabledDark // Button mati di dark mode jadi gelap
)

@Composable
fun NutritionAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color dimatikan agar desain konsisten dengan brand identity 3E7580
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        // Jika mau support dynamic color Android 12+ (ambil warna wallpaper), ubah ke true
        // Tapi untuk jurnal/branding konsisten, disarankan false.
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = androidx.compose.ui.platform.LocalContext.current
            if (darkTheme) androidx.compose.material3.dynamicDarkColorScheme(context)
            else androidx.compose.material3.dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    // Mengatur warna Status Bar (Icon baterai/jam)
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            // Status bar mengikuti warna background agar terlihat "seamless"
            window.statusBarColor = colorScheme.background.toArgb()

            // Icon status bar jadi gelap (hitam) karena background kita terang
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}