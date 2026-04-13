package com.example.nutriscan.presentation.onboarding

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.nutriscan.presentation.theme.Dimens
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun OnboardingScreen(
    viewModel: OnboardingViewModel = hiltViewModel(),
    onFinishOnboarding: () -> Unit
) {
    // 1. Ambil State dari ViewModel
    val state by viewModel.state.collectAsState()

    // 2. State UI Lokal
    val pagerState = rememberPagerState(pageCount = { 5 })
    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState()
    var showSkipSheet by remember { mutableStateOf(false) }

    // 3. Logika Validasi Button "Selanjutnya" (Per Halaman)
    val isFormValid = when (pagerState.currentPage) {
        0 -> state.goal.isNotEmpty()
        1 -> state.diseases.isNotEmpty()
        2 -> state.diet.isNotEmpty()
        3 -> state.activityLevel.isNotEmpty()
        4 -> state.gender.isNotEmpty() && state.age.isNotEmpty() &&
                state.height.isNotEmpty() && state.weight.isNotEmpty()
        else -> false
    }

    // Button aktif jika Form Valid DAN tidak sedang Loading
    val isNextEnabled = isFormValid && !state.isLoading

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding() // Aman dari status bar
            .imePadding(), // Aman dari keyboard
        topBar = {
            OnboardingTopBar(
                modifier = Modifier,
                currentStep = pagerState.currentPage + 1,
                totalStep = pagerState.pageCount,
                onBackClick = {
                    scope.launch {
                        // Jika di halaman pertama, back tidak ngapa-ngapain (atau bisa exit app)
                        if (pagerState.currentPage > 0) {
                            pagerState.animateScrollToPage(pagerState.currentPage - 1)
                        }
                    }
                }
            )
        },
        bottomBar = {
            OnboardingFooter(
                isLastStep = pagerState.currentPage == 4, // Cek halaman terakhir
                isNextEnabled = isNextEnabled,
                onNextClick = {
                    scope.launch {
                        if (pagerState.currentPage < 4) {
                            // Geser ke halaman berikutnya
                            pagerState.animateScrollToPage(pagerState.currentPage + 1)
                        } else {
                            // Halaman Terakhir -> SIMPAN DATA LENGKAP
                            viewModel.submitData(onSuccess = onFinishOnboarding)
                        }
                    }
                },
                onSkipClick = {
                    // Munculkan Bottom Sheet Konfirmasi
                    showSkipSheet = true
                }
            )
        }
    ) { paddingValues ->

        // 4. KONTEN PAGER (Halaman-halaman)
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize(),
            userScrollEnabled = false // Matikan swipe manual (User wajib isi & klik next)
        ) { page ->

            // Container scrollable agar aman di layar kecil
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(vertical = Dimens.SpaceMedium),
            ) {
                when (page) {
                    0 -> GoalPage(selected = state.goal, onSelect = viewModel::updateGoal)
                    1 -> DiseasePage(selected = state.diseases, onSelect = viewModel::toggleDisease)
                    2 -> DietPage(selected = state.diet, onSelect = viewModel::updateDiet)
                    3 -> ActivityPage(selected = state.activityLevel, onSelect = viewModel::updateActivity)
                    4 -> ProfileFormPage(
                        state = state,
                        onUpdate = viewModel::updateProfile
                    )
                }
            }
        }

        // 5. BOTTOM SHEET (Konfirmasi Lewati)
        if (showSkipSheet) {
            SkipConfirmationBottomSheet(
                sheetState = sheetState,
                onDismissRequest = { showSkipSheet = false },
                onConfirm = {
                    showSkipSheet = false
                    // Panggil fungsi Skip di ViewModel -> Simpan data kosong -> Masuk Home
                    viewModel.skipOnboarding(onSuccess = onFinishOnboarding)
                }
            )
        }
    }
}
