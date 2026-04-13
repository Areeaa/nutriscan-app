package com.example.nutriscan.presentation.onboarding

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.nutriscan.presentation.components.CustomTextField
import com.example.nutriscan.presentation.theme.Dimens
import com.example.nutriscan.presentation.theme.NutritionAppTheme


//goal page
@Composable
fun GoalPage(selected: String, onSelect: (String) -> Unit) {
    OnboardingQuestion(
        text = "Apa Tujuan Kamu Menggunakan Aplikasi ini?",
        modifier = Modifier
    )
    Spacer(modifier = Modifier.height(Dimens.SpaceLarge))

    OptionGrid {
        listOf("Makan Lebih Sehat", "Membangun Otot", "Kontrol Berat Badan", "Kelola Penyakit").forEach { item ->
            SelectableOptionItem(
                modifier = Modifier,
                text = item,
                isSelected = selected == item,
                onClick = { onSelect(item) }
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun OptionGrid(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    // FlowRow akan otomatis membuat baris baru kalau layarnya tidak muat
    FlowRow(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = Dimens.SpaceMedium),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        content()
    }
}


//penyakit page
@Composable
fun DiseasePage(selected: List<String>, onSelect: (String) -> Unit) {
    OnboardingQuestion(
        text = "Apa Kamu Memiliki Riwayat Penyakit Tertentu?",
        modifier = Modifier
    )
    Spacer(modifier = Modifier.height(Dimens.SpaceLarge))

    OptionGrid {
        listOf("Diabetes", "Hipertensi", "Kolesterol", "Tidak Satupun").forEach { item ->
            SelectableOptionItem(
                text = item,
                isSelected = selected.contains(item),
                onClick = { onSelect(item) },
                modifier = Modifier
            )
        }
    }
}


// preferensi diet
@Composable
fun DietPage(selected: String, onSelect: (String) -> Unit) {
    OnboardingQuestion(
        text = "Bagaimana Preferensi Dietmu?",
        modifier = Modifier
    )
    Spacer(modifier = Modifier.height(Dimens.SpaceLarge))

    OptionGrid {
        listOf("Rendah Karbo", "Vegetarian", "Biasa Saja").forEach { item ->
            SelectableOptionItem(
                text = item,
                isSelected = selected == item,
                onClick = { onSelect(item) },
                modifier = Modifier
            )
        }
    }
}


//page tingkat aktivitas
@Composable
fun ActivityPage(selected: String, onSelect: (String) -> Unit) {
    OnboardingQuestion(
        text = "Bagaimana Tingkat Aktivitas Fisikmu?",
        modifier = Modifier
    )
    Spacer(modifier = Modifier.height(Dimens.SpaceLarge))

    OptionGrid {
        listOf("Jarang Olahraga", "Ringan", "Sedang", "Aktif", "Sangat Aktif").forEach { item ->
            SelectableOptionItem(
                text = item,
                isSelected = selected == item,
                onClick = { onSelect(item) },
                modifier = Modifier
            )
        }
    }
}


//page input profil
@Composable
fun ProfileFormPage(
    state: OnboardingState,
    onUpdate: (String?, String?, String?, String?) -> Unit
) {
    OnboardingQuestion(
        text = "Selangkah Lagi, Sampai Semua Selesai",
        modifier = Modifier
    )

    Column(modifier = Modifier.padding(Dimens.SpaceLarge)) {

        // --- INPUT GENDER ---
        Text("Apa Jenis Kelaminmu?", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
        Spacer(modifier = Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf("Laki-laki", "Perempuan").forEach { genderOption ->
                SelectableOptionItem(
                    text = genderOption,
                    isSelected = state.gender == genderOption,
                    onClick = { onUpdate(genderOption, null, null, null) }, // Update Gender saja
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Spacer(modifier = Modifier.height(Dimens.SpaceLarge))

        // --- INPUT UMUR ---
        Text("Berapa Umurmu?", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
        CustomTextField(
            value = state.age,
            onValueChange = {
                // Hanya terima angka
                if(it.all { char -> char.isDigit() }) onUpdate(null, it, null, null)
            },
            label = "Umur (Tahun)",
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next),
            modifier = Modifier
        )

        Spacer(modifier = Modifier.height(Dimens.SpaceLarge))

        // --- INPUT TINGGI ---
        Text("Berapa Tinggi Badanmu?", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
        CustomTextField(
            value = state.height,
            onValueChange = {
                if(it.all { char -> char.isDigit() }) onUpdate(null, null, it, null)
            },
            label = "Tinggi (cm)",
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next),
            modifier = Modifier
        )

        Spacer(modifier = Modifier.height(Dimens.SpaceLarge))

        // --- INPUT BERAT ---
        Text("Berapa Berat Badanmu?", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
        CustomTextField(
            value = state.weight,
            onValueChange = {
                if(it.all { char -> char.isDigit() }) onUpdate(null, null, null, it)
            },
            label = "Berat (kg)",
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done),
            modifier = Modifier
        )
    }
}

@Preview
@Composable
private fun GoalPagePrev() {
    NutritionAppTheme { 
        GoalPage(
            selected = ""
        ) { }
    }

}




