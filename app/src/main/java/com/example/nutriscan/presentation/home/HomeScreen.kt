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
import com.example.nutriscan.presentation.theme.BackgroundCream

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    onNavigateToScan: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToHome: () -> Unit,
    onNavigateToHistori: () -> Unit
) {
    val user by viewModel.userState.collectAsState()
    val waterCount by viewModel.waterCount.collectAsState()
    val dailyTip by viewModel.dailyTip.collectAsState()
    val isTipLoading by viewModel.isTipLoading.collectAsState()

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
        containerColor = BackgroundCream
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            // 1. Gradient header (full width, no horizontal padding)
            HomeHeader(user = user, onProfileClick = onNavigateToProfile)

            // Content with padding
            Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                Spacer(modifier = Modifier.height(24.dp))

                DailyTipCard(
                    tipText = dailyTip,
                    isLoading = isTipLoading,
                    onRefreshClick = onRefreshTip
                )

                Spacer(modifier = Modifier.height(24.dp))

                WaterTrackerCard(
                    glassesDrank = waterCount,
                    targetGlasses = 8,
                    onWaterClick = onWaterClick
                )

                Spacer(modifier = Modifier.height(24.dp))

                FeaturedSection(
                    onNavigateToScan = onNavigateToScan,
                    onNavigateToHistori = onNavigateToHistori
                )

                Spacer(modifier = Modifier.height(100.dp))
            }
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