package com.example.nutriscan.presentation.navigation

import ProfileScreen
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.nutriscan.presentation.auth.LoginScreen
import com.example.nutriscan.presentation.auth.RegisterScreen
import com.example.nutriscan.presentation.home.HomeScreen
import com.example.nutriscan.presentation.onboarding.OnboardingScreen
import com.example.nutriscan.presentation.scanner.ScannerScreen
import com.example.nutriscan.presentation.result.ResultScreen
import com.example.nutriscan.presentation.scanner.ScannerViewModel
import com.example.nutriscan.presentation.history.HistoryScreen
import com.example.nutriscan.presentation.history.DetailHistoryScreen
import com.example.nutriscan.presentation.history.HistoryViewModel
import com.example.nutriscan.presentation.profile.ProfileViewModel
import com.google.firebase.auth.FirebaseAuth

import com.example.nutriscan.presentation.splash.SplashScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val currentUser = FirebaseAuth.getInstance().currentUser

    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {

        // ==========================================
        // 0. SPLASH SCREEN
        // ==========================================

        composable(route = Screen.Splash.route) {
            SplashScreen(
                onSplashFinished = {
                    val destination = if (currentUser != null) Screen.Home.route else Screen.Login.route
                    navController.navigate(destination) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }


        composable(route = Screen.Login.route) {
            LoginScreen(
                onNavigateToRegister = {
                    navController.navigate(Screen.Register.route)
                },
                onLoginSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }

        composable(route = Screen.Register.route) {
            RegisterScreen(
                onNavigateToLogin = {
                    navController.popBackStack()
                },
                onRegisterSuccess = {
                    navController.navigate(Screen.Onboarding.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }

        composable(route = Screen.Onboarding.route) {
            OnboardingScreen(
                onFinishOnboarding = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Onboarding.route) { inclusive = true }
                    }
                }
            )
        }

        // ==========================================
        // 2. MAIN MENU (HOME)
        // ==========================================

        composable(route = Screen.Home.route) {
            HomeScreen(
                onNavigateToScan = {
                    navController.navigate(Screen.Scan.route)
                },
                onNavigateToProfile = {
                    // Navigasi ke halaman Profil diaktifkan
                    navController.navigate(Screen.Profile.route)
                },
                onNavigateToHome = {
                    // Tetap di Home
                },
                onNavigateToHistori = {
                    navController.navigate(Screen.Histori.route)
                }
            )
        }

        // ==========================================
        // 3. SCAN & RESULT (Shared ScannerViewModel)
        // ==========================================

        composable(route = Screen.Scan.route) { backStackEntry ->
            // Inisiasi ViewModel yang terikat (scoped) ke rute Scan ini
            val scannerViewModel: ScannerViewModel = hiltViewModel(backStackEntry)

            ScannerScreen(
                viewModel = scannerViewModel,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToResult = {
                    navController.navigate(Screen.Result.route)
                }
            )
        }

        composable(route = Screen.Result.route) { backStackEntry ->

            // Ubah key remember-nya menjadi backStackEntry
            val scanBackStackEntry = remember(backStackEntry) {
                navController.getBackStackEntry(Screen.Scan.route)
            }
            val sharedScannerViewModel: ScannerViewModel = hiltViewModel(scanBackStackEntry)

            ResultScreen(
                viewModel = sharedScannerViewModel,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onRetake = {
                    navController.popBackStack(route = Screen.Scan.route, inclusive = false)
                },
                onSave = { namaLabel ->
                    sharedScannerViewModel.saveScanDataToDatabase(namaLabel) { isSuccess ->
                        if (isSuccess) {
                            navController.navigate(Screen.Home.route) {
                                // Hapus history agar user tidak bisa 'back' lagi ke result
                                popUpTo(Screen.Home.route) { inclusive = true }
                            }
                        }
                    }
                }
            )
        }

        // ==========================================
        // 4. HISTORY & DETAIL (Shared HistoryViewModel)
        // ==========================================

        composable(route = Screen.Histori.route) {
            // Inisiasi ViewModel yang terikat ke rute Histori
            val historyViewModel: HistoryViewModel = hiltViewModel()

            HistoryScreen(
                viewModel = historyViewModel,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToDetail = { historyId ->
                    navController.navigate(Screen.DetailHistory.createRoute(historyId))
                }
            )
        }

        composable(route = Screen.DetailHistory.route) { backStackEntry ->
            // Tangkap ID dari parameter navigasi
            val historyId = backStackEntry.arguments?.getString("historyId") ?: return@composable

            // Ambil ViewModel yang sama dengan layar HistoryScreen
            val historyBackStackEntry = remember(navController.currentBackStackEntry) {
                navController.getBackStackEntry(Screen.Histori.route)
            }
            val sharedHistoryViewModel: HistoryViewModel = hiltViewModel(historyBackStackEntry)

            // Ambil state daftar riwayat dari ViewModel
            val historyList by sharedHistoryViewModel.filteredHistory.collectAsState()

            // Cari data spesifik berdasarkan ID
            val selectedHistory = historyList.find { it.id == historyId }

            if (selectedHistory != null) {
                DetailHistoryScreen(
                    history = selectedHistory,
                    onNavigateBack = {
                        navController.popBackStack()
                    }
                )
            }
        }

        // ==========================================
        // 5. PROFILE SCREEN
        // ==========================================

        composable(route = Screen.Profile.route) {
            val profileViewModel: ProfileViewModel = hiltViewModel()

            ProfileScreen(
                viewModel = profileViewModel,
                onNavigateBack = { navController.popBackStack() },
                onLogoutClick = {
                    FirebaseAuth.getInstance().signOut()
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onDeleteAccountClick = {
                    // Akun sudah dihapus dari Firebase, navigasi user ke layar Login
                    FirebaseAuth.getInstance().signOut()
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
    }
}