package com.example.nutriscan.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.graphics.Matrix
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.core.content.ContextCompat

fun capturePhoto(
    context: Context,
    imageCapture: ImageCapture,
    onSuccess: (Bitmap) -> Unit,
    onError: (Exception) -> Unit
) {
    val executor = ContextCompat.getMainExecutor(context)

    imageCapture.takePicture(
        executor,
        object : ImageCapture.OnImageCapturedCallback() {
            override fun onCaptureSuccess(image: ImageProxy) {
                // 1. Ambil gambar mentah
                val originalBitmap = image.toBitmap()

                // 2. Baca seberapa melenceng derajat rotasinya (biasanya 90 derajat kalau portrait)
                val rotationDegrees = image.imageInfo.rotationDegrees

                // 3. Putar gambarnya kalau ternyata dia butuh diputar
                val rotatedBitmap = if (rotationDegrees != 0) {
                    val matrix = Matrix()
                    matrix.postRotate(rotationDegrees.toFloat())
                    Bitmap.createBitmap(
                        originalBitmap,
                        0,
                        0,
                        originalBitmap.width,
                        originalBitmap.height,
                        matrix,
                        true
                    )
                } else {
                    originalBitmap // Kalau udah 0 derajat (pas), gak usah diputar
                }

                // 4. Lempar gambar yang sudah tegak ke UI/ViewModel
                onSuccess(rotatedBitmap)

                // 5. Tutup image proxy biar kamera bisa jepret lagi
                image.close()
            }

            override fun onError(exception: ImageCaptureException) {
                Log.e("CameraUtils", "Gagal menjepret foto", exception)
                onError(exception)
            }
        }
    )
}

fun uriToBitmap(context: Context, uri: Uri): Bitmap? {
    return try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            // Untuk Android 9 (Pie) ke atas
            val source = ImageDecoder.createSource(context.contentResolver, uri)
            ImageDecoder.decodeBitmap(source) { decoder, _, _ ->
                decoder.isMutableRequired = true // Memastikan bitmap bisa diproses ML Kit
            }
        } else {
            // Untuk Android 8 ke bawah
            @Suppress("DEPRECATION")
            MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
        }
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}