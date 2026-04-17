package com.example.nutriscan.presentation.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.nutriscan.domain.model.User

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    onNavigateToScan: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToHome: () -> Unit,
    onNavigateToHistori: () -> Unit
) {
    // Ambil semua data dari ViewModel
    val user by viewModel.userState.collectAsState()
    val waterCount by viewModel.waterCount.collectAsState()
    val dailyTip by viewModel.dailyTip.collectAsState()
    val isTipLoading by viewModel.isTipLoading.collectAsState()

    // Lempar data ke Stateless Component
    HomeContent(
        user = user,
        waterCount = waterCount,
        dailyTip = dailyTip,
        isTipLoading = isTipLoading,
        onNavigateToScan = onNavigateToScan,
        onNavigateToProfile = onNavigateToProfile,
        onNavigateToHome = onNavigateToHome,
        onNavigateToHistori = onNavigateToHistori,
        onRefreshTip = { viewModel.fetchDailyTip() },

        // 1. PERUBAHAN DI SINI: Tangkap 'index' dan lempar ke ViewModel
        onWaterClick = { index -> viewModel.updateWater(index) }
    )
}

@Composable
fun HomeContent(
    user: User?,
    waterCount: Int,
    dailyTip: String,
    isTipLoading: Boolean,
    onNavigateToScan: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToHome: () -> Unit,
    onNavigateToHistori: () -> Unit,
    onRefreshTip: () -> Unit,
    onWaterClick: (Int) -> Unit
) {
    Scaffold(
        bottomBar = {
            HomeBottomBar(
                onScanClick = onNavigateToScan,
                onNavigateToHome = onNavigateToHome,
                onNavigateToHistori = onNavigateToHistori
            )
        },
        containerColor = MaterialTheme.colorScheme.background // Background kalem
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            // 1. Bagian Header
            HomeHeader(user = user, onProfileClick = onNavigateToProfile)

            Spacer(modifier = Modifier.height(32.dp))

            // 2. Daily Tip (Desain Baru)
            DailyTipCard(
                tipText = dailyTip,
                isLoading = isTipLoading,
                onRefreshClick = onRefreshTip
            )

            Spacer(modifier = Modifier.height(24.dp))

            // 3. Bagian Track Minum Air
            WaterTrackerCard(
                glassesDrank = waterCount,
                targetGlasses = 8,
                onWaterClick = onWaterClick
            )

            Spacer(modifier = Modifier.height(24.dp))

            // 4. Fitur Unggulan (Menggantikan Artikel Gizi)
            FeaturedSection(
                onNavigateToScan = onNavigateToScan,
                onNavigateToHistori = onNavigateToHistori
            )

            Spacer(modifier = Modifier.height(100.dp)) // Spasi aman dari Bottom Bar
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF9F8F6, showSystemUi = true)
@Composable
fun HomeContentPreview() {
    HomeContent(
        user = null,
        waterCount = 3,
        dailyTip = "Fakta: Minum air hangat setelah bangun tidur bisa membantu melancarkan metabolisme tubuhmu sepanjang hari!",
        isTipLoading = false,
        onNavigateToScan = {},
        onNavigateToProfile = {},
        onNavigateToHome = {},
        onNavigateToHistori = {},
        onRefreshTip = {},
        onWaterClick = {}
    )
}