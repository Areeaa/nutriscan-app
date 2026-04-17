import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.nutriscan.common.Resource
import com.example.nutriscan.presentation.profile.ActionButtonsSection
import com.example.nutriscan.presentation.profile.DeleteAccountBottomSheet
import com.example.nutriscan.presentation.profile.EditProfileBottomSheet
import com.example.nutriscan.presentation.profile.HealthConfigCard
import com.example.nutriscan.presentation.profile.PhysicalProfileCard
import com.example.nutriscan.presentation.profile.ProfileEditField
import com.example.nutriscan.presentation.profile.ProfileHeaderSection
import com.example.nutriscan.presentation.profile.ProfileTopBar
import com.example.nutriscan.presentation.profile.ProfileViewModel
import com.example.nutriscan.presentation.theme.BackgroundCream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel,
    onNavigateBack: () -> Unit,
    onLogoutClick: () -> Unit,
    onDeleteAccountClick: () -> Unit
) {
    // Tarik data asli dari Firebase (bersifat real-time jika pakai Flow!)
    val user by viewModel.userState.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    var sheetFieldToEdit by remember { mutableStateOf<ProfileEditField?>(null) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var showDeleteConfirmation by remember { mutableStateOf(false) }
    val deleteSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val context = LocalContext.current
    val uploadState by viewModel.uploadPhotoState.collectAsState()

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { viewModel.uploadProfilePicture(it) }
    }

    LaunchedEffect(uploadState) {
        when (val state = uploadState) {
            is Resource.Success -> {
                Toast.makeText(context, "Foto profil berhasil diperbarui", Toast.LENGTH_SHORT).show()
                viewModel.clearUploadPhotoState()
            }
            is Resource.Error -> {
                Toast.makeText(context, state.message, Toast.LENGTH_LONG).show()
                viewModel.clearUploadPhotoState()
            }
            else -> {}
        }
    }

    Scaffold(
        topBar = {
            ProfileTopBar(onNavigateBack = onNavigateBack)
        },
        containerColor = BackgroundCream
    ) { paddingValues ->
        if (user == null) {
            // Tampilkan loading kalau data belum masuk
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
            ) {
                // Tampilkan Nama & Email dari Auth/Database
                ProfileHeaderSection(
                    name = user?.displayName ?: "Pengguna",
                    email = user?.email ?: "",
                    profilePictureUrl = user?.profilePictureUrl, // Kirim link fotonya
                    isUploadingPhoto = uploadState is Resource.Loading, // Cek status loading
                    onEditNameClick = { /* Aksi edit nama sebelumnya */ },
                    onPhotoSelected = { uri ->
                        // Panggil fungsi upload di ViewModel kamu
                        viewModel.uploadProfilePicture(uri)
                    }
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Kartu Profil Fisik (Kirim objek physicalProfile untuk dibaca datanya)
                PhysicalProfileCard(
                    profile = user!!.physicalProfile,
                    onItemClick = { field -> sheetFieldToEdit = field }
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Kartu Konfigurasi Kesehatan
                HealthConfigCard(
                    config = user!!.healthConfig,
                    onItemClick = { field -> sheetFieldToEdit = field }
                )

                Spacer(modifier = Modifier.height(32.dp))

                ActionButtonsSection(
                    onLogoutClick = onLogoutClick,
                    onDeleteAccountClick = { showDeleteConfirmation = true }
                )

                Spacer(modifier = Modifier.height(40.dp))
            }
        }

        // Tampilkan Bottom Sheet jika sedang ada field yang diklik
        if (sheetFieldToEdit != null) {
            EditProfileBottomSheet(
                field = sheetFieldToEdit!!,
                sheetState = sheetState,
                onDismiss = { sheetFieldToEdit = null },
                onSave = { newValue ->

                    viewModel.updateProfile(sheetFieldToEdit!!, newValue)
                    sheetFieldToEdit = null // Tutup modal
                }
            )
        }

        if (showDeleteConfirmation){
            DeleteAccountBottomSheet(
                sheetState = deleteSheetState,
                onDismiss = { showDeleteConfirmation = false },
                onConfirmDelete = {
                    showDeleteConfirmation = false
                    // Panggil ViewModel
                    viewModel.deleteAccount(
                        onSuccess = { onDeleteAccountClick() }, // Pindah ke Login
                        onError = { errorMessage ->
                            println("Error hapus akun: $errorMessage")
                        }
                    )
                }
            )
        }
    }
}

