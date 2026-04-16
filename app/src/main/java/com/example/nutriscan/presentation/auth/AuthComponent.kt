package com.example.nutriscan.presentation.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.nutriscan.R
import com.example.nutriscan.presentation.components.CustomTextField
import com.example.nutriscan.presentation.theme.Dimens
import com.example.nutriscan.presentation.theme.NutritionAppTheme

@Composable
fun AuthScreenContainer(
    title: String,
    subtitle: String,
    content: @Composable ColumnScope.() -> Unit
) {

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primary)
    ) {
        // Column untuk menata posisi Atas - Tengah - Bawah
        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // --- BAGIAN 1: LOGO (TOP CENTER) ---
            Spacer(modifier = Modifier.height(Dimens.SpaceLarge)) // Margin dari atas layar

            Image(
                painter = painterResource(R.drawable.img_logo),
                contentDescription = "Logo NutriScan",
                modifier = Modifier
                    .width(140.dp)
                    .height(40.dp)
            )


            Spacer(modifier = Modifier.weight(1f))

            // --- BAGIAN 2: HEADER KONTEN (TEKS SELAMAT DATANG) ---
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(horizontal = Dimens.SpaceExtraLarge)
            ) {
                Text(
                    text = "Selamat Datang di NutriScan!",
                    style = MaterialTheme.typography.headlineMedium, // Sedikit dikecilkan agar muat
                    color = MaterialTheme.colorScheme.onPrimary, // Warna teks diatas background primary
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Scan label makananmu, biar gak asal makan.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f),
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(44.dp))

            // --- BAGIAN 3: CARD PUTIH (BOTTOM) ---
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp), // Radius Card
                color = MaterialTheme.colorScheme.background
            ) {
                Column(
                    modifier = Modifier
                        .padding(Dimens.SpaceLarge)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Handle Kecil (Garis Abu)
                    Box(
                        modifier = Modifier
                            .width(40.dp)
                            .height(4.dp)
                            .background(Color.LightGray.copy(alpha = 0.5f), RoundedCornerShape(2.dp))
                    )

                    Spacer(modifier = Modifier.height(Dimens.SpaceLarge))

                    // Judul Halaman (Login/Daftar)
                    Text(
                        text = title,
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.align(Alignment.Start) // Rata kiri sesuai desain umum
                    )
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.align(Alignment.Start)
                    )

                    Spacer(modifier = Modifier.height(Dimens.SpaceMedium))

                    // Isi Form (Input & Button)
                    content()
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForgotPasswordBottomSheet(
    email: String,
    onEmailChange: (String) -> Unit,
    onDismissRequest: () -> Unit,
    onSendClick: () -> Unit,
    isLoading: Boolean
) {
    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp)
                .padding(bottom = 32.dp) // Jarak dari sistem navigasi bawah
        ) {
            Text(
                text = "Lupa Sandi",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Kami akan mengirimkan tautan untuk mengatur ulang kata sandi ke email Anda.",
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(24.dp))

            CustomTextField(
                modifier = Modifier,
                value = email,
                onValueChange = onEmailChange,
                label = "Email Terdaftar",
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Done
                )
            )

            Spacer(modifier = Modifier.height(32.dp))

            CustomButton(
                text = if (isLoading) "Mengirim..." else "Kirim Tautan",
                onClick = onSendClick,
                enabled = !isLoading && email.isNotEmpty(),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun CustomButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier, // Beri default nilai Modifier
    enabled: Boolean = true        // Beri default nilai true
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        // Menggunakan radius 12.dp, kamu bisa menggantinya dengan RadiusMedium dari Shape.kt
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            // Warna saat tombol didisable (misal saat loading atau email kosong)
            disabledContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
            disabledContentColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.5f)
        ),
        modifier = modifier
            .fillMaxWidth() // Mengisi lebar layar secara default
            .height(50.dp)  // Tinggi standar tombol agar nyaman ditekan
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge.copy(
                fontWeight = FontWeight.Bold
            )
        )
    }
}


@Preview(showBackground = true, heightDp = 800)
@Composable
private fun AuthContainerPrev() {
    NutritionAppTheme {
        AuthScreenContainer(
            title = "Daftar",
            subtitle = "Buat akun baru"
        ) {
            // Simulasi konten form
            Box(Modifier.height(100.dp).fillMaxWidth().background(Color.LightGray))
        }
    }
}