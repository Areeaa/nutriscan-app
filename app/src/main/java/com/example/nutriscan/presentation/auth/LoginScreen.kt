package com.example.nutriscan.presentation.auth

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
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
fun LoginScreen(
    viewModel: AuthViewModel = hiltViewModel(),
    onNavigateToRegister: () -> Unit,
    onLoginSuccess: () -> Unit
) {
    val state by viewModel.authState.collectAsState()
    val context = LocalContext.current


    // Efek Samping (Toast & Navigasi)
    LaunchedEffect(state) {
        if (state.error != null) {
            Toast.makeText(context, state.error, Toast.LENGTH_LONG).show()
        }
        if (state.user != null) {
            Toast.makeText(context, "Login Berhasil!", Toast.LENGTH_SHORT).show()
            onLoginSuccess()
        }
    }

    LoginContent(
        isLoading = state.isLoading, // Kirim status loading
        onLoginClick = { email, password ->
            viewModel.login(email, password) // Panggil fungsi login asli
        },
        onNavigateToRegister = onNavigateToRegister
    )

}

@Composable
fun LoginContent(
    isLoading: Boolean,
    onLoginClick: (String, String) -> Unit,
    onNavigateToRegister: () -> Unit
) {
    // State Input
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }

    AuthScreenContainer(
        title = "Masuk",
        subtitle = "Silakan masuk untuk melanjutkan"
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .verticalScroll(rememberScrollState()), // Agar aman di layar kecil
            verticalArrangement = Arrangement.Top
        ) {
            Spacer(modifier = Modifier.height(Dimens.SpaceMedium))

            //input email
            CustomTextField(
                value = email,
                onValueChange = { email = it },
                label = "Email",
                modifier = Modifier,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                )
            )

            Spacer(modifier = Modifier.height(Dimens.SpaceMedium))

            //input password
            CustomTextField(
                value = password,
                onValueChange = {password = it},
                label = "Password",
                modifier = Modifier,
                visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                )
            )

            Spacer(modifier = Modifier.height(Dimens.SpaceLarge))

            //button login
            ButtonPrimary(
                text = "Masuk",
                onClick = {onLoginClick(email, password)},
                modifier = Modifier,
                isEnabled = !isLoading && email.isNotEmpty() && password.isNotEmpty(),
            )

            Spacer(modifier = Modifier.height(Dimens.SpaceLarge))

            //footer
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Belum punya akun?",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.width(Dimens.SpaceExtraSmall))

                Text(
                    text = "Daftar",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.clickable{onNavigateToRegister()}
                )
            }

            Spacer(modifier = Modifier.height(Dimens.SpaceLarge))

        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun LoginScreenPrev(){
    NutritionAppTheme {
        LoginContent(
            isLoading = false,
            onLoginClick = {_,_ ->}
        ) { }
    }

}

