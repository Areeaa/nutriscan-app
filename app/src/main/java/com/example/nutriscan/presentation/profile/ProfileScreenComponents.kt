package com.example.nutriscan.presentation.profile

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.nutriscan.domain.model.HealthConfig
import com.example.nutriscan.domain.model.PhysicalProfile
import com.example.nutriscan.presentation.components.CustomTextField
import com.example.nutriscan.presentation.onboarding.SelectableOptionItem
import com.example.nutriscan.presentation.theme.*
import kotlin.math.cos
import kotlin.math.sin

// ─────────────────────────────────────────────────────────────
//  PROFILE TOP BAR — gradient
// ─────────────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileTopBar(onNavigateBack: () -> Unit) {
    TopAppBar(
        title = {
            Text(
                text = "Profil",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        },
        navigationIcon = {
            IconButton(onClick = onNavigateBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Kembali")
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = BackgroundCream)
    )
}

// ─────────────────────────────────────────────────────────────
//  PROFILE HEADER SECTION
// ─────────────────────────────────────────────────────────────
@Composable
fun ProfileHeaderSection(
    name: String,
    email: String,
    profilePictureUrl: String?,
    isUploadingPhoto: Boolean = false,
    onEditNameClick: () -> Unit,
    onPhotoSelected: (Uri) -> Unit
) {
    val galleryLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? -> uri?.let { onPhotoSelected(it) } }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .padding(top = 20.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(SurfaceWhite)
            .border(1.dp, BorderColor, RoundedCornerShape(20.dp))
            .padding(20.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            // Avatar
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clickable { if (!isUploadingPhoto) galleryLauncher.launch("image/*") },
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = profilePictureUrl ?: "",
                    contentDescription = "Foto Profil",
                    modifier = Modifier.fillMaxSize().clip(CircleShape),
                    contentScale = ContentScale.Crop,
                    error = rememberVectorPainter(Icons.Default.Person),
                    placeholder = rememberVectorPainter(Icons.Default.Person)
                )
                // Camera badge
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .size(22.dp)
                        .background(brush = GradientCard, shape = CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.CameraAlt, contentDescription = null, tint = Color.White, modifier = Modifier.size(12.dp))
                }
                if (isUploadingPhoto) {
                    Box(
                        modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.4f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                    }
                }
            }

            Spacer(Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(name, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = TextPrimary)
                    Spacer(Modifier.width(6.dp))
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .background(PrimaryTeal.copy(alpha = 0.1f), CircleShape)
                            .clickable { onEditNameClick() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit", tint = PrimaryTeal, modifier = Modifier.size(14.dp))
                    }
                }
                Spacer(Modifier.height(4.dp))
                Text(email, color = TextSecondary, fontSize = 13.sp)
                Spacer(Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .background(GradientCard, RoundedCornerShape(8.dp))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text("Pengguna Aktif", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────
//  PHYSICAL PROFILE CARD
// ─────────────────────────────────────────────────────────────
@Composable
fun PhysicalProfileCard(profile: PhysicalProfile, onItemClick: (ProfileEditField) -> Unit) {
    ProfileSectionCard(title = "Profil Fisik") {
        ProfileItemRow(Icons.Default.Wc, "Jenis Kelamin", profile.gender ?: "Belum diatur") { onItemClick(ProfileEditField.GENDER) }
        ProfileDivider()
        val ageText = if (profile.age != null && profile.age > 0) "${profile.age} Tahun" else "Belum diatur"
        ProfileItemRow(Icons.Default.People, "Umur", ageText) { onItemClick(ProfileEditField.AGE) }
        ProfileDivider()
        val heightText = if (profile.height != null && profile.height > 0f) "${profile.height} cm" else "Belum diatur"
        ProfileItemRow(Icons.Default.Height, "Tinggi Badan", heightText) { onItemClick(ProfileEditField.HEIGHT) }
        ProfileDivider()
        val weightText = if (profile.weight != null && profile.weight > 0f) "${profile.weight} kg" else "Belum diatur"
        ProfileItemRow(Icons.Default.MonitorWeight, "Berat Badan", weightText) { onItemClick(ProfileEditField.WEIGHT) }
        ProfileDivider()
        val bmiText = if (profile.bmi != null && profile.bmi!! > 0f) String.format("%.1f", profile.bmi) else "Belum tersedia"
        ProfileItemRow(Icons.Default.MonitorHeart, "BMI", bmiText, showArrow = false) {}
    }
}

// ─────────────────────────────────────────────────────────────
//  HEALTH CONFIG CARD
// ─────────────────────────────────────────────────────────────
@Composable
fun HealthConfigCard(config: HealthConfig, onItemClick: (ProfileEditField) -> Unit) {
    ProfileSectionCard(title = "Konfigurasi Kesehatan") {
        val goalText = config.goal?.ifBlank { "Belum diatur" } ?: "Belum diatur"
        ProfileItemRow(Icons.Default.TrackChanges, "Tujuan", goalText) { onItemClick(ProfileEditField.GOAL) }
        ProfileDivider()
        val diseaseText = if (config.diseases.isNotEmpty()) config.diseases.joinToString(", ") else "Belum diatur"
        ProfileItemRow(Icons.Default.FavoriteBorder, "Riwayat Penyakit", diseaseText) { onItemClick(ProfileEditField.DISEASE) }
        ProfileDivider()
        val dietText = config.dietPreference?.ifBlank { "Belum diatur" } ?: "Belum diatur"
        ProfileItemRow(Icons.Default.RestaurantMenu, "Preferensi Diet", dietText) { onItemClick(ProfileEditField.DIET) }
        ProfileDivider()
        val activityText = config.activityLevel?.ifBlank { "Belum diatur" } ?: "Belum diatur"
        ProfileItemRow(Icons.Default.FitnessCenter, "Aktivitas Fisik", activityText) { onItemClick(ProfileEditField.ACTIVITY) }
    }
}

// ─────────────────────────────────────────────────────────────
//  SECTION CARD wrapper
// ─────────────────────────────────────────────────────────────
@Composable
private fun ProfileSectionCard(title: String, content: @Composable ColumnScope.() -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(SurfaceWhite)
            .border(1.dp, BorderColor, RoundedCornerShape(20.dp))
    ) {
        Column(modifier = Modifier.padding(vertical = 4.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 18.dp, vertical = 14.dp)
            ) {
                Spacer(Modifier.width(10.dp))
                Text(title, style = MaterialTheme.typography.titleMedium)
            }
            content()
        }
    }
}

@Composable
private fun ProfileDivider() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 18.dp)
            .height(1.dp)
            .background(BorderColor)
    )
}

@Composable
fun ProfileItemRow(
    icon: ImageVector,
    title: String,
    value: String,
    showArrow: Boolean = true,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = showArrow) { onClick() }
            .padding(horizontal = 18.dp, vertical = 13.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .background(PrimaryTeal.copy(alpha = 0.08f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = title, tint = PrimaryTeal, modifier = Modifier.size(18.dp))
        }
        Spacer(Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = TextPrimary)
            Text(value, color = TextSecondary, fontSize = 12.sp)
        }
        if (showArrow) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .background(SurfaceVariant, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.ChevronRight, contentDescription = null, tint = TextTertiary, modifier = Modifier.size(16.dp))
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────
//  ACTION BUTTONS SECTION
// ─────────────────────────────────────────────────────────────
@Composable
fun ActionButtonsSection(onLogoutClick: () -> Unit, onDeleteAccountClick: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Logout — outlined danger
        OutlinedButton(
            onClick = onLogoutClick,
            modifier = Modifier.fillMaxWidth().height(52.dp),
            shape = RoundedCornerShape(14.dp),
            border = androidx.compose.foundation.BorderStroke(1.5.dp, ErrorRed),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = ErrorRed)
        ) {
            Icon(Icons.Default.ExitToApp, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(8.dp))
            Text("Keluar", fontWeight = FontWeight.Bold)
        }

        TextButton(onClick = onDeleteAccountClick) {
            Text("Hapus Akun", color = TextSecondary, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
        }
    }
}

// ─────────────────────────────────────────────────────────────
//  EDIT PROFILE BOTTOM SHEET
// ─────────────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileBottomSheet(
    field: ProfileEditField,
    sheetState: SheetState,
    onDismiss: () -> Unit,
    onSave: (String) -> Unit
) {
    val genderOptions   = listOf("Laki-laki", "Perempuan")
    val goalOptions     = listOf("Makan Lebih Sehat", "Membangun Otot", "Kontrol Berat Badan", "Kelola Penyakit")
    val diseaseOptions  = listOf("Diabetes", "Hipertensi", "Kolesterol", "Tidak Satupun")
    val dietOptions     = listOf("Rendah Karbo", "Vegetarian", "Biasa Saja")
    val activityOptions = listOf("Jarang Olahraga", "Ringan", "Sedang", "Aktif", "Sangat Aktif")

    var singleSelection by remember { mutableStateOf("") }
    var multiSelection  by remember { mutableStateOf(setOf<String>()) }
    var textInput       by remember { mutableStateOf("") }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = SurfaceWhite,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Title
            Row(verticalAlignment = Alignment.CenterVertically) {

                Text(
                    "Ubah ${field.title}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = TextPrimary
                )
            }
            Spacer(Modifier.height(24.dp))

            when (field) {
                ProfileEditField.GENDER, ProfileEditField.GOAL,
                ProfileEditField.DIET, ProfileEditField.ACTIVITY -> {
                    val options = when (field) {
                        ProfileEditField.GENDER -> genderOptions
                        ProfileEditField.GOAL   -> goalOptions
                        ProfileEditField.DIET   -> dietOptions
                        else                    -> activityOptions
                    }
                    Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        options.forEach { item ->
                            SelectableOptionItem(
                                text = item,
                                isSelected = singleSelection == item,
                                onClick = { singleSelection = item },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
                ProfileEditField.DISEASE -> {
                    Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        diseaseOptions.forEach { item ->
                            val isSelected = multiSelection.contains(item)
                            SelectableOptionItem(
                                text = item,
                                isSelected = isSelected,
                                onClick = {
                                    multiSelection = if (isSelected) {
                                        multiSelection - item
                                    } else {
                                        if (item == "Tidak Satupun") setOf("Tidak Satupun")
                                        else (multiSelection - "Tidak Satupun") + item
                                    }
                                },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
                ProfileEditField.DISPLAY_NAME -> {
                    CustomTextField(
                        value = textInput, onValueChange = { textInput = it },
                        label = "Nama Lengkap",
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text, imeAction = ImeAction.Done),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                ProfileEditField.AGE, ProfileEditField.HEIGHT, ProfileEditField.WEIGHT -> {
                    val labelText = when (field) {
                        ProfileEditField.AGE    -> "Umur (Tahun)"
                        ProfileEditField.HEIGHT -> "Tinggi (cm)"
                        else                    -> "Berat (kg)"
                    }
                    CustomTextField(
                        value = textInput,
                        onValueChange = { if (it.all { c -> c.isDigit() || c == '.' }) textInput = it },
                        label = labelText,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            Spacer(Modifier.height(28.dp))

            val isSaveEnabled = when (field) {
                ProfileEditField.GENDER, ProfileEditField.GOAL,
                ProfileEditField.DIET, ProfileEditField.ACTIVITY -> singleSelection.isNotBlank()
                ProfileEditField.DISEASE -> multiSelection.isNotEmpty()
                else -> textInput.isNotBlank()
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(
                        if (isSaveEnabled) GradientButton
                        else Brush.horizontalGradient(listOf(Color(0xFFBBCCCE), Color(0xFFCCD5D5)))
                    )
                    .clickable(enabled = isSaveEnabled) {
                        val finalValue = when (field) {
                            ProfileEditField.GENDER, ProfileEditField.GOAL,
                            ProfileEditField.DIET, ProfileEditField.ACTIVITY -> singleSelection
                            ProfileEditField.DISEASE -> multiSelection.joinToString(", ")
                            else -> textInput
                        }
                        onSave(finalValue)
                    },
                contentAlignment = Alignment.Center
            ) {
                Text("Simpan", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
            }

            Spacer(Modifier.height(32.dp))
        }
    }
}

// ─────────────────────────────────────────────────────────────
//  DELETE ACCOUNT BOTTOM SHEET
// ─────────────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeleteAccountBottomSheet(
    sheetState: SheetState,
    onDismiss: () -> Unit,
    onConfirmDelete: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = SurfaceWhite,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 28.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .background(ErrorRed.copy(alpha = 0.1f), CircleShape)
                    .border(2.dp, ErrorRed.copy(alpha = 0.3f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Warning, contentDescription = null, tint = ErrorRed, modifier = Modifier.size(30.dp))
            }

            Spacer(Modifier.height(16.dp))

            Text("Hapus Akun Permanen?", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = TextPrimary)
            Spacer(Modifier.height(8.dp))
            Text(
                "Semua data profil dan riwayat scan akan dihapus selamanya dan tidak bisa dikembalikan.",
                fontSize = 14.sp, color = TextSecondary, textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(28.dp))

            // Danger button
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(GradientDanger)
                    .clickable { onConfirmDelete() },
                contentAlignment = Alignment.Center
            ) {
                Text("Ya, Hapus Akun Saya", color = Color.White, fontWeight = FontWeight.Bold)
            }

            Spacer(Modifier.height(12.dp))

            OutlinedButton(
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(14.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderColor),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = TextSecondary)
            ) {
                Text("Batal", fontWeight = FontWeight.SemiBold)
            }

            Spacer(Modifier.height(32.dp))
        }
    }
}