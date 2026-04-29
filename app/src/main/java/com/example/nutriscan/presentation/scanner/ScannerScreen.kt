package com.example.nutriscan.presentation.scanner

import android.Manifest
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.ImageCapture
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.nutriscan.presentation.theme.GradientHeroVertical
import com.example.nutriscan.util.capturePhoto
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun ScannerScreen(
    viewModel: ScannerViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
    onNavigateToResult: () -> Unit
) {
    val context      = LocalContext.current
    val capturedImage by viewModel.capturedImage.collectAsState()
    val isProcessing  by viewModel.isProcessing.collectAsState()

    // Camera permission
    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
                    == PackageManager.PERMISSION_GRANTED
        )
    }
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted -> hasCameraPermission = granted }

    LaunchedEffect(Unit) {
        if (!hasCameraPermission) permissionLauncher.launch(Manifest.permission.CAMERA)
    }

    // Flash + capture
    var isLedFlashOn by remember { mutableStateOf(false) }
    val imageCapture  = remember { ImageCapture.Builder().build() }
    LaunchedEffect(isLedFlashOn) {
        imageCapture.flashMode =
            if (isLedFlashOn) ImageCapture.FLASH_MODE_ON else ImageCapture.FLASH_MODE_OFF
    }

    // Gallery picker
    val photoPickerLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            if (!viewModel.onImageSelectedFromGallery(context, uri)) {
                Toast.makeText(context, "Gagal memproses gambar", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Capture flash animation
    val haptic = LocalHapticFeedback.current
    val coroutineScope = rememberCoroutineScope()
    var isFlashing by remember { mutableStateOf(false) }
    val flashAlpha by animateFloatAsState(
        targetValue = if (isFlashing) 1f else 0f,
        animationSpec = tween(150), label = "Flash"
    )

    when {
        !hasCameraPermission -> {
            // ── Permission denied UI ──
            Box(
                modifier = Modifier.fillMaxSize().background(brush = GradientHeroVertical),
                contentAlignment = Alignment.Center
            ) {
                Button(onClick = { permissionLauncher.launch(Manifest.permission.CAMERA) }) {
                    Text("Izinkan Akses Kamera")
                }
            }
        }

        capturedImage == null -> {
            // ── Camera viewfinder ──
            ScannerContent(
                imageCapture   = imageCapture,
                flashAlpha     = flashAlpha,
                isFlashOn      = isLedFlashOn,
                onFlashToggle  = { isLedFlashOn = !isLedFlashOn },
                onNavigateBack = onNavigateBack,
                onGalleryClick = {
                    photoPickerLauncher.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                },
                onScanClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    coroutineScope.launch {
                        isFlashing = true; delay(100); isFlashing = false
                        capturePhoto(
                            context      = context,
                            imageCapture = imageCapture,
                            onSuccess    = { bitmap -> viewModel.onImageCaptured(bitmap) },
                            onError      = { Toast.makeText(context, "Gagal menjepret foto", Toast.LENGTH_SHORT).show() }
                        )
                    }
                }
            )
        }

        else -> {
            // ── Image Editor (crop + rotate) ──
            ImageEditorContent(
                bitmap      = capturedImage!!,
                isProcessing = isProcessing,
                onRetake    = { viewModel.clearCapturedImage() },
                onConfirm   = { editedBitmap ->
                    // Store the cropped/rotated result, then kick off OCR
                    viewModel.onEditedImageConfirmed(editedBitmap)
                    viewModel.processConfirmedImage {
                        onNavigateToResult()
                    }
                }
            )
        }
    }
}

@Composable
fun ScannerContent(
    imageCapture: ImageCapture,
    flashAlpha: Float,
    isFlashOn: Boolean,
    onFlashToggle: () -> Unit,
    onNavigateBack: () -> Unit,
    onGalleryClick: () -> Unit,
    onScanClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = GradientHeroVertical)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 40.dp, start = 16.dp, end = 16.dp, bottom = 80.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(Color.Black)
        ) {
            CameraPreview(imageCapture = imageCapture, modifier = Modifier.fillMaxSize())

            ScannerTopBar(
                onNavigateBack = onNavigateBack,
                onGalleryClick = onGalleryClick,
                isFlashOn      = isFlashOn,
                onFlashClick   = onFlashToggle,
                modifier       = Modifier.align(Alignment.TopCenter)
            )

            ScannerInstructionTooltip(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 48.dp)
            )
        }

        ScannerFab(
            onScanClick = onScanClick,
            modifier    = Modifier.align(Alignment.BottomCenter).padding(bottom = 48.dp)
        )

        if (flashAlpha > 0f) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White.copy(alpha = flashAlpha))
            )
        }
    }
}