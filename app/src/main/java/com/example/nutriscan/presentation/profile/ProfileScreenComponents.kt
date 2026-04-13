package com.example.nutriscan.presentation.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Height
import androidx.compose.material.icons.filled.MonitorHeart
import androidx.compose.material.icons.filled.MonitorWeight
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.RestaurantMenu
import androidx.compose.material.icons.filled.TrackChanges
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Wc
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.nutriscan.presentation.components.CustomTextField
import com.example.nutriscan.presentation.onboarding.SelectableOptionItem
import com.example.nutriscan.presentation.theme.BackgroundCream

@Composable
fun ProfileHeaderSection(
    name: String,
    email: String,
    onEditNameClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Placeholder Foto Profil
        Box(
            modifier = Modifier
                .size(70.dp)
                .clip(CircleShape)
                .background(Color.LightGray)
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                Spacer(modifier = Modifier.width(4.dp))
                // Icon Edit di sebelah Nama
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit Nama",
                    tint = Color(0xFF4A707A),
                    modifier = Modifier
                        .size(18.dp)
                        .clickable { onEditNameClick() }
                )
            }
            Text(text = email, color = Color.Gray, fontSize = 14.sp)
        }
    }
}

@Composable
fun PhysicalProfileCard(
    profile: com.example.nutriscan.domain.model.PhysicalProfile,
    onItemClick: (ProfileEditField) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(vertical = 8.dp)) {
            // --- HEADER YANG DISAMAKAN DENGAN HEALTH CONFIG ---
            Text(
                text = "Profil Fisik",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
            )

            ProfileItemRow(Icons.Default.Wc, "Jenis Kelamin", profile.gender ?: "Belum diatur") { onItemClick(ProfileEditField.GENDER) }
            HorizontalDivider(color = Color(0xFFF0F0F0), thickness = 1.dp, modifier = Modifier.padding(horizontal = 16.dp))

            val ageText = if (profile.age != null && profile.age > 0) "${profile.age} Tahun" else "Belum diatur"
            ProfileItemRow(Icons.Default.People, "Umur", ageText) { onItemClick(ProfileEditField.AGE) }
            HorizontalDivider(color = Color(0xFFF0F0F0), thickness = 1.dp, modifier = Modifier.padding(horizontal = 16.dp))

            val heightText = if (profile.height != null && profile.height > 0f) "${profile.height} cm" else "Belum diatur"
            ProfileItemRow(Icons.Default.Height, "Tinggi badan", heightText) { onItemClick(ProfileEditField.HEIGHT) }
            HorizontalDivider(color = Color(0xFFF0F0F0), thickness = 1.dp, modifier = Modifier.padding(horizontal = 16.dp))

            val weightText = if (profile.weight != null && profile.weight > 0f) "${profile.weight} kg" else "Belum diatur"
            ProfileItemRow(Icons.Default.MonitorWeight, "Berat Badan", weightText) { onItemClick(ProfileEditField.WEIGHT) }
            HorizontalDivider(color = Color(0xFFF0F0F0), thickness = 1.dp, modifier = Modifier.padding(horizontal = 16.dp))

            val bmiText = if (profile.bmi != null && profile.bmi!! > 0f) String.format("%.1f", profile.bmi) else "Belum tersedia"
            ProfileItemRow(Icons.Default.MonitorHeart, "BMI", bmiText, showArrow = false) {}
        }
    }
}

@Composable
fun HealthConfigCard(
    config: com.example.nutriscan.domain.model.HealthConfig, // Pastikan import modelnya sesuai
    onItemClick: (ProfileEditField) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(vertical = 8.dp)) {
            Text(
                text = "Konfigurasi kesehatan",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
            )

            // Gunakan data dari 'config', berikan fallback "Belum diatur" jika kosong
            val goalText = config.goal?.ifBlank { "Belum diatur" } ?: "Belum diatur"
            ProfileItemRow(Icons.Default.TrackChanges, "Tujuan", goalText) { onItemClick(ProfileEditField.GOAL) }
            HorizontalDivider(color = Color(0xFFF0F0F0), thickness = 1.dp, modifier = Modifier.padding(horizontal = 16.dp))

            val diseaseText = if (config.diseases.isNotEmpty()) config.diseases.joinToString(", ") else "Belum diatur"
            ProfileItemRow(Icons.Default.FavoriteBorder, "Riwayat Penyakit", diseaseText) { onItemClick(ProfileEditField.DISEASE) }
            HorizontalDivider(color = Color(0xFFF0F0F0), thickness = 1.dp, modifier = Modifier.padding(horizontal = 16.dp))

            val dietText = config.dietPreference?.ifBlank { "Belum diatur" } ?: "Belum diatur"
            ProfileItemRow(Icons.Default.RestaurantMenu, "Preferensi Diet", dietText) { onItemClick(ProfileEditField.DIET) }
            HorizontalDivider(color = Color(0xFFF0F0F0), thickness = 1.dp, modifier = Modifier.padding(horizontal = 16.dp))

            val activityText = config.activityLevel?.ifBlank { "Belum diatur" } ?: "Belum diatur"
            ProfileItemRow(Icons.Default.FitnessCenter, "Aktivitas Fisik", activityText) { onItemClick(ProfileEditField.ACTIVITY) }
        }
    }
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
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(imageVector = icon, contentDescription = title, tint = Color.DarkGray, modifier = Modifier.size(24.dp))
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Text(text = value, color = Color.Gray, fontSize = 12.sp)
        }
        if (showArrow) {
            Icon(imageVector = Icons.Default.ChevronRight, contentDescription = "Edit", tint = Color.Gray)
        }
    }
}

@Composable
fun ActionButtonsSection(onLogoutClick: () -> Unit, onDeleteAccountClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedButton(
            onClick = onLogoutClick,
            modifier = Modifier.fillMaxWidth().height(50.dp),
            shape = RoundedCornerShape(25.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color.Red)
        ) {
            Text("Keluar", fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(onClick = onDeleteAccountClick) {
            Text("Hapus Akun", color = Color(0xFF708CA0), fontWeight = FontWeight.Bold)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileBottomSheet(
    field: ProfileEditField,
    sheetState: SheetState,
    onDismiss: () -> Unit,
    onSave: (String) -> Unit
) {
    // --- 1. DATA SESUAI ONBOARDING KAMPUS ---
    val genderOptions = listOf("Laki-laki", "Perempuan")
    val goalOptions = listOf("Makan Lebih Sehat", "Membangun Otot", "Kontrol Berat Badan", "Kelola Penyakit")
    val diseaseOptions = listOf("Diabetes", "Hipertensi", "Kolesterol", "Tidak Satupun")
    val dietOptions = listOf("Rendah Karbo", "Vegetarian", "Biasa Saja")
    val activityOptions = listOf("Jarang Olahraga", "Ringan", "Sedang", "Aktif", "Sangat Aktif")

    // --- 2. STATE INPUT ---
    var singleSelection by remember { mutableStateOf("") }
    var multiSelection by remember { mutableStateOf(setOf<String>()) }
    var textInput by remember { mutableStateOf("") } // Mengganti nama agar lebih umum (bisa angka/teks)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Ubah ${field.title}",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            // --- 3. RENDER UI KE BAWAH (COLUMN) ---
            when (field) {
                // PILIHAN SINGLE (GENDER, GOAL, DIET, ACTIVITY)
                ProfileEditField.GENDER, ProfileEditField.GOAL,
                ProfileEditField.DIET, ProfileEditField.ACTIVITY -> {
                    val options = when (field) {
                        ProfileEditField.GENDER -> genderOptions
                        ProfileEditField.GOAL -> goalOptions
                        ProfileEditField.DIET -> dietOptions
                        else -> activityOptions
                    }

                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
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

                // PILIHAN MULTIPLE (DISEASE)
                ProfileEditField.DISEASE -> {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
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

                // INPUT TEKS (DISPLAY NAME)
                ProfileEditField.DISPLAY_NAME -> {
                    CustomTextField(
                        value = textInput,
                        onValueChange = { textInput = it },
                        label = "Nama Lengkap",
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Text,
                            imeAction = ImeAction.Done
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                // INPUT ANGKA (AGE, HEIGHT, WEIGHT)
                ProfileEditField.AGE, ProfileEditField.HEIGHT, ProfileEditField.WEIGHT -> {
                    val labelText = when (field) {
                        ProfileEditField.AGE -> "Umur (Tahun)"
                        ProfileEditField.HEIGHT -> "Tinggi (cm)"
                        else -> "Berat (kg)"
                    }

                    CustomTextField(
                        value = textInput,
                        onValueChange = {
                            if (it.all { char -> char.isDigit() || char == '.' }) {
                                textInput = it
                            }
                        },
                        label = labelText,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Done
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // --- 4. VALIDASI TOMBOL SIMPAN ---
            val isSaveEnabled = when (field) {
                ProfileEditField.GENDER, ProfileEditField.GOAL,
                ProfileEditField.DIET, ProfileEditField.ACTIVITY -> singleSelection.isNotBlank()
                ProfileEditField.DISEASE -> multiSelection.isNotEmpty()
                ProfileEditField.DISPLAY_NAME, ProfileEditField.AGE,
                ProfileEditField.HEIGHT, ProfileEditField.WEIGHT -> textInput.isNotBlank()
            }

            Button(
                onClick = {
                    val finalValue = when (field) {
                        ProfileEditField.GENDER, ProfileEditField.GOAL,
                        ProfileEditField.DIET, ProfileEditField.ACTIVITY -> singleSelection
                        ProfileEditField.DISEASE -> multiSelection.joinToString(", ")
                        else -> textInput // Untuk Nama, Umur, Tinggi, dan Berat
                    }
                    onSave(finalValue)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(25.dp),
                enabled = isSaveEnabled,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4A707A))
            ) {
                Text("Simpan", color = Color.White, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

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
        containerColor = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = "Warning",
                tint = Color.Red,
                modifier = Modifier.size(48.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Hapus Akun Permanen?",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Semua data profil dan riwayat scan label Anda akan dihapus selamanya dan tidak dapat dikembalikan. Apakah Anda yakin?",
                fontSize = 14.sp,
                color = Color.Gray,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Tombol Hapus (Merah)
            Button(
                onClick = onConfirmDelete,
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(25.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
            ) {
                Text("Ya, Hapus Akun Saya", color = Color.White, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Tombol Batal
            OutlinedButton(
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(25.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color.LightGray),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.DarkGray)
            ) {
                Text("Batal", fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

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