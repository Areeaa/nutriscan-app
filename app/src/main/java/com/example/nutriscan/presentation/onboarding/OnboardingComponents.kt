package com.example.nutriscan.presentation.onboarding

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.nutriscan.presentation.theme.Dimens
import com.example.nutriscan.R
import com.example.nutriscan.presentation.components.ButtonPrimary
import com.example.nutriscan.presentation.theme.NutritionAppTheme
import com.example.nutriscan.presentation.theme.Shapes

//top bar
@Composable
fun OnboardingTopBar(
    currentStep: Int,
    totalStep: Int,
    onBackClick: () -> Unit,
    modifier: Modifier
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        //garis progress
        LinearProgressIndicator(
            progress = {currentStep.toFloat() / totalStep.toFloat()},
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp),
            color = MaterialTheme.colorScheme.primary,
            trackColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
            strokeCap = StrokeCap.Square
        )

        Spacer(modifier = Modifier.height(Dimens.SpaceMedium))

        //navigasi back
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .clickable { onBackClick() }
                .padding(horizontal = Dimens.SpaceMedium, vertical = Dimens.SpaceSmall)
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_back),
                contentDescription = "Kembali",
            )

            Spacer(modifier = Modifier.width(Dimens.SpaceMedium))

            Text(
                text = "Sebelumnya",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurface
            )

        }
    }
}


//Title
@Composable
fun OnboardingQuestion(
    text: String,
    modifier: Modifier
) {
    Text(
        text = text,
        style = MaterialTheme.typography.headlineMedium.copy(
            color = MaterialTheme.colorScheme.onBackground
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Dimens.SpaceLarge)
    )
}


//selectable item
@Composable
fun SelectableOptionItem(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier
) {
    val backroundColor by animateColorAsState(
        targetValue = if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.5f) else Color.Transparent,
        label = "bgColor"
    )

    val borderColor by animateColorAsState(
        targetValue = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
        label = "borderColor"
    )

    val textColor by animateColorAsState(
        targetValue = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
        label = "textColor"
    )

    Surface(
        modifier = Modifier
            .wrapContentWidth()
            .clickable { onClick() },
        shape = Shapes.extraLarge,
        color = backroundColor,
        border = BorderStroke(1.dp, borderColor)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.padding(vertical = 12.dp, horizontal = Dimens.SpaceExtraLarge)
        ){
            Text(
                text = text,
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Normal),
                color = textColor,
                textAlign = TextAlign.Center
            )
        }

    }
}

// footer
@Composable
fun OnboardingFooter(
    modifier: Modifier = Modifier,
    onNextClick: () -> Unit,
    onSkipClick: () -> Unit,
    isLastStep: Boolean = false,
    isNextEnabled: Boolean = false,

) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(Dimens.SpaceLarge),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Tombol Utama (Selanjutnya / Selesai)
        ButtonPrimary(
            text = if (isLastStep) "Selesai" else "Selanjutnya",
            onClick = onNextClick,
            isEnabled = isNextEnabled // <--- DIHUBUNGKAN KE SINI
        )

        Spacer(modifier = Modifier.height(Dimens.SpaceMedium))

        // Divider "Atau"
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            HorizontalDivider(modifier = Modifier.weight(1f))
            Text(
                text = "Atau",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 8.dp)
            )
            HorizontalDivider(modifier = Modifier.weight(1f))
        }

        Spacer(modifier = Modifier.height(Dimens.SpaceSmall))

        // Tombol Lewati
        TextButton(onClick = onSkipClick) {
            Text(
                text = "Lewati untuk saat ini",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}


//BottomSheet skip 
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SkipConfirmationBottomSheet(
    sheetState: SheetState,
    onDismissRequest: () -> Unit,
    onConfirm: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
        dragHandle = { BottomSheetDefaults.DragHandle() }, // Garis kecil di atas sheet
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Dimens.SpaceLarge)
                .padding(bottom = Dimens.SpaceLarge), // Padding bawah agar aman
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // JUDUL
            Text(
                text = "Gunakan Pengaturan Standar?",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(Dimens.SpaceMedium))

            // DESKRIPSI (Warna abu-abu)
            Text(
                text = "Jika dilewati, aplikasi akan memberikan rekomendasi berdasarkan standar gizi umum WHO (2000 kkal)",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(Dimens.SpaceLarge))

            // TOMBOL: BATALKAN (Fokus Utama - Warna Abu sesuai wireframe)
            ButtonPrimary(
                text = "Batalkan",
                onClick = onDismissRequest,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )

            Spacer(modifier = Modifier.height(Dimens.SpaceLarge))

            // TEKS LINK: SETUJU (Aksi Sekunder)
            Text(
                text = "Setuju",
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                color = MaterialTheme.colorScheme.outline, // Warna agak pudar/netral
                modifier = Modifier
                    .clickable { onConfirm() }
                    .padding(8.dp) // Hitbox agar mudah diklik
            )

            // Spacer tambahan untuk margin bawah
            Spacer(modifier = Modifier.height(Dimens.SpaceMedium))
        }
    }
}


@Preview(showBackground = true)
@Composable
private fun TopBarPrev() {
    NutritionAppTheme {
        OnboardingTopBar(
            currentStep = 1,
            totalStep = 5,
            onBackClick = {},
            modifier = Modifier
        )
    }

}

@Preview(showBackground = true)
@Composable
private fun TitlePrev() {
    NutritionAppTheme {
        OnboardingQuestion(
            text = "Siapkah Anda?",
            modifier = Modifier
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SelectItemPrev() {
    NutritionAppTheme {
        SelectableOptionItem(
            text = "Diet Karbo",
            isSelected = false,
            onClick = {},
            modifier = Modifier
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun FooterOnboardingPrev() {
    NutritionAppTheme {
        OnboardingFooter(
            onNextClick = {},
            onSkipClick = {},
            isLastStep = false,
            modifier = Modifier
        )
    }
    
}
