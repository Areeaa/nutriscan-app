package com.example.nutriscan.presentation.auth

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.nutriscan.presentation.components.ButtonPrimary
import com.example.nutriscan.presentation.components.CustomTextField
import com.example.nutriscan.presentation.theme.Dimens
import com.example.nutriscan.presentation.theme.NutritionAppTheme

@Composable
fun RegisterScreen(
    viewModel: AuthViewModel = hiltViewModel(),
    onNavigateToLogin: () -> Unit,
    onRegisterSuccess: () -> Unit
) {
    val state by viewModel.authState.collectAsState()
    val context = LocalContext.current

    // Handle Efek Samping (Toast & Navigasi)
    LaunchedEffect(state) {
        if (state.error != null) {
            Toast.makeText(context, state.error, Toast.LENGTH_LONG).show()
        }
        if (state.user != null) {
            Toast.makeText(context, "Akun Berhasil Dibuat!", Toast.LENGTH_SHORT).show()
            onRegisterSuccess()
        }
    }

    // Panggil UI Murni
    RegisterContent(
        isLoading = state.isLoading,
        onRegisterClick = { name, email, password ->
            viewModel.register(email, password, name)
        },
        onNavigateToLogin = onNavigateToLogin
    )
}

@Composable
fun RegisterContent(
    isLoading: Boolean,
    onRegisterClick: (String, String, String) -> Unit, // Callback: Name, Email, Pass
    onNavigateToLogin: () -> Unit
) {
    // State Form Lokal
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    // State Visibility Password
    var isPasswordVisible by remember { mutableStateOf(false) }
    var isConfirmVisible by remember { mutableStateOf(false) }

    // Validasi Sederhana
    val isPasswordMismatch = confirmPassword.isNotEmpty() && password != confirmPassword
    val isFormValid = name.isNotEmpty() && email.isNotEmpty() &&
            password.isNotEmpty() && confirmPassword.isNotEmpty() &&
            !isPasswordMismatch

    AuthScreenContainer(
        title = "Buat Akun",
        subtitle = "Lengkapi data diri Anda"
    ) {
        // Konten Form (Scrollable agar aman saat keyboard muncul)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.Top
        ) {
            // --- INPUT NAMA ---
            CustomTextField(
                value = name,
                onValueChange = { name = it },
                label = "Nama Lengkap",
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                modifier = Modifier
            )

            Spacer(modifier = Modifier.height(Dimens.SpaceMedium))

            // --- INPUT EMAIL ---
            CustomTextField(
                value = email,
                onValueChange = { email = it },
                label = "Email",
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                ),
                modifier = Modifier
            )

            Spacer(modifier = Modifier.height(Dimens.SpaceMedium))

            // --- INPUT PASSWORD ---
            CustomTextField(
                value = password,
                onValueChange = { password = it },
                label = "Kata Sandi",
                visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Next
                ),
                modifier = Modifier,
                trailingIcon = {
                    IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                        Icon(
                            imageVector = if (isPasswordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                            contentDescription = "Toggle Password"
                        )
                    }
                }
            )

            Spacer(modifier = Modifier.height(Dimens.SpaceMedium))

            // --- INPUT KONFIRMASI PASSWORD ---
            CustomTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = "Ulangi Kata Sandi",
                visualTransformation = if (isConfirmVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                isError = isPasswordMismatch,
                errorMessage = "Kata sandi tidak cocok",
                modifier = Modifier,
                trailingIcon = {
                    IconButton(onClick = { isConfirmVisible = !isConfirmVisible }) {
                        Icon(
                            imageVector = if (isConfirmVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                            contentDescription = "Toggle Confirm Password"
                        )
                    }
                }
            )

            Spacer(modifier = Modifier.height(Dimens.SpaceExtraLarge))

            // --- TOMBOL DAFTAR ---
            ButtonPrimary(
                text = if (isLoading) "Memproses..." else "Daftar Sekarang",
                onClick = { onRegisterClick(name, email, password) },
                isEnabled = !isLoading && isFormValid
            )

            Spacer(modifier = Modifier.height(Dimens.SpaceLarge))

            // --- LINK KE LOGIN ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Sudah punya akun? ",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Masuk",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.clickable { onNavigateToLogin() }
                )
            }

            Spacer(modifier = Modifier.height(Dimens.SpaceLarge))


        }
    }
}


@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun RegisterScreenPreview() {
    NutritionAppTheme {
        RegisterContent(
            isLoading = false,
            onRegisterClick = { _, _, _ -> },
            onNavigateToLogin = {}
        )
    }
}