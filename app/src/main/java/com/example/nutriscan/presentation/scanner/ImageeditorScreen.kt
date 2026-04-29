package com.example.nutriscan.presentation.scanner

import android.graphics.Bitmap
import android.graphics.Matrix
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.RotateLeft
import androidx.compose.material.icons.filled.RotateRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import com.example.nutriscan.presentation.theme.*
import kotlin.math.abs
import kotlin.math.roundToInt

// ─────────────────────────────────────────────────────────────
//  Data classes
// ─────────────────────────────────────────────────────────────

private data class CropRect(
    val left: Float,
    val top: Float,
    val right: Float,
    val bottom: Float
) {
    val width  get() = right - left
    val height get() = bottom - top
}

/** Position/size of the image as rendered on screen (ContentScale.Fit letterboxing). All in layout-px. */
private data class ImageDisplayRect(
    val offsetX: Float,
    val offsetY: Float,
    val width: Float,
    val height: Float
)

private enum class DragHandle { NONE, TL, TR, BL, BR, MOVE }

// Handle touch radius in layout-px
private const val HANDLE_TOUCH_PX = 72f
private const val MIN_CROP_PX = 80f

// ─────────────────────────────────────────────────────────────
//  Bitmap helpers (public)
// ─────────────────────────────────────────────────────────────

fun rotateBitmapCW(src: Bitmap): Bitmap =
    Bitmap.createBitmap(src, 0, 0, src.width, src.height,
        Matrix().apply { postRotate(90f) }, true)

fun rotateBitmapCCW(src: Bitmap): Bitmap =
    Bitmap.createBitmap(src, 0, 0, src.width, src.height,
        Matrix().apply { postRotate(-90f) }, true)

/**
 * Crops [src] using [crop] which is expressed in image-display-px space
 * ([dispW] × [dispH] = how many layout-px the image occupies on screen).
 */
private fun cropBitmap(src: Bitmap, crop: CropRect, dispW: Float, dispH: Float): Bitmap {
    val sx = src.width  / dispW
    val sy = src.height / dispH
    val bx = (crop.left   * sx).toInt().coerceIn(0, src.width  - 1)
    val by = (crop.top    * sy).toInt().coerceIn(0, src.height - 1)
    val bw = (crop.width  * sx).toInt().coerceAtLeast(1).coerceAtMost(src.width  - bx)
    val bh = (crop.height * sy).toInt().coerceAtLeast(1).coerceAtMost(src.height - by)
    return Bitmap.createBitmap(src, bx, by, bw, bh)
}

// ─────────────────────────────────────────────────────────────
//  Compute where bitmap is rendered (ContentScale.Fit)
// ─────────────────────────────────────────────────────────────

private fun computeImageRect(containerPx: IntSize, bmp: Bitmap): ImageDisplayRect? {
    val cW = containerPx.width.toFloat()
    val cH = containerPx.height.toFloat()
    if (cW <= 0f || cH <= 0f) return null

    val bmpRatio = bmp.width.toFloat() / bmp.height.toFloat()
    val ctnRatio = cW / cH
    val (imgW, imgH) = if (bmpRatio > ctnRatio) cW to cW / bmpRatio else cH * bmpRatio to cH

    return ImageDisplayRect(
        offsetX = (cW - imgW) / 2f,
        offsetY = (cH - imgH) / 2f,
        width   = imgW,
        height  = imgH
    )
}

// ─────────────────────────────────────────────────────────────
//  Hit-test: find which handle a touch falls on
//  [touch] is in IMAGE-LOCAL coordinates (0..imageRect.width, etc.)
// ─────────────────────────────────────────────────────────────

private fun hitTest(touch: Offset, crop: CropRect): DragHandle {
    fun d(px: Float, py: Float) = abs(touch.x - px) + abs(touch.y - py)

    val corners = listOf(
        DragHandle.TL to Offset(crop.left,  crop.top),
        DragHandle.TR to Offset(crop.right, crop.top),
        DragHandle.BL to Offset(crop.left,  crop.bottom),
        DragHandle.BR to Offset(crop.right, crop.bottom),
    )
    val nearest = corners.minByOrNull { d(it.second.x, it.second.y) }
    if (nearest != null && d(nearest.second.x, nearest.second.y) < HANDLE_TOUCH_PX) {
        return nearest.first
    }
    if (touch.x in crop.left..crop.right && touch.y in crop.top..crop.bottom) {
        return DragHandle.MOVE
    }
    return DragHandle.NONE
}

// ─────────────────────────────────────────────────────────────
//  Apply drag delta to crop
// ─────────────────────────────────────────────────────────────

private fun updateCrop(
    crop: CropRect, handle: DragHandle,
    dx: Float, dy: Float,
    maxW: Float, maxH: Float
): CropRect = when (handle) {
    DragHandle.TL -> crop.copy(
        left = (crop.left + dx).coerceIn(0f, crop.right  - MIN_CROP_PX),
        top  = (crop.top  + dy).coerceIn(0f, crop.bottom - MIN_CROP_PX)
    )
    DragHandle.TR -> crop.copy(
        right = (crop.right + dx).coerceIn(crop.left   + MIN_CROP_PX, maxW),
        top   = (crop.top   + dy).coerceIn(0f, crop.bottom - MIN_CROP_PX)
    )
    DragHandle.BL -> crop.copy(
        left   = (crop.left   + dx).coerceIn(0f, crop.right - MIN_CROP_PX),
        bottom = (crop.bottom + dy).coerceIn(crop.top + MIN_CROP_PX, maxH)
    )
    DragHandle.BR -> crop.copy(
        right  = (crop.right  + dx).coerceIn(crop.left + MIN_CROP_PX, maxW),
        bottom = (crop.bottom + dy).coerceIn(crop.top  + MIN_CROP_PX, maxH)
    )
    DragHandle.MOVE -> {
        val nl = (crop.left + dx).coerceIn(0f, maxW - crop.width)
        val nt = (crop.top  + dy).coerceIn(0f, maxH - crop.height)
        crop.copy(left = nl, top = nt, right = nl + crop.width, bottom = nt + crop.height)
    }
    DragHandle.NONE -> crop
}

// ─────────────────────────────────────────────────────────────
//  Main composable
// ─────────────────────────────────────────────────────────────

@Composable
fun ImageEditorContent(
    bitmap: Bitmap,
    isProcessing: Boolean,
    onRetake: () -> Unit,
    onConfirm: (Bitmap) -> Unit
) {
    // Working bitmap — replaced on each rotation
    var workingBitmap by remember(bitmap) { mutableStateOf(bitmap) }

    // Container size (full-screen Box) in layout-px
    var containerPx by remember { mutableStateOf(IntSize.Zero) }

    // imageRect derived from containerPx + current bitmap aspect ratio
    val imageRect: ImageDisplayRect? by remember(containerPx, workingBitmap.width, workingBitmap.height) {
        derivedStateOf { computeImageRect(containerPx, workingBitmap) }
    }

    // Crop in image-local layout-px. Reset whenever imageRect changes.
    val cropState = remember { mutableStateOf(CropRect(0f, 0f, 0f, 0f)) }
    var lastImageRect by remember { mutableStateOf<ImageDisplayRect?>(null) }
    if (imageRect != lastImageRect) {
        lastImageRect = imageRect
        imageRect?.let { r -> cropState.value = CropRect(0f, 0f, r.width, r.height) }
    }

    // Cached ImageBitmap – recreated only when bitmap changes
    val imageBitmap: ImageBitmap = remember(workingBitmap) { workingBitmap.asImageBitmap() }

    // rememberUpdatedState so pointerInput(Unit) always reads the latest values
    // without needing to restart its coroutine.
    val latestImageRect  = rememberUpdatedState(imageRect)
    val latestCropState  = rememberUpdatedState(cropState)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0D0D0D))
            .onSizeChanged { containerPx = it }
    ) {
        // ── Single Canvas: draws image + crop overlay + all gesture handling ──
        androidx.compose.foundation.Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    // activeHandle lives in the coroutine scope — avoids Compose state overhead
                    // while still being correct across onDragStart / onDrag / onDragEnd.
                    var activeHandle = DragHandle.NONE

                    detectDragGestures(
                        onDragStart = { screenOffset ->
                            val rect = latestImageRect.value ?: return@detectDragGestures
                            val cs   = latestCropState.value

                            // Convert screen-space touch → image-local touch
                            val local = Offset(
                                screenOffset.x - rect.offsetX,
                                screenOffset.y - rect.offsetY
                            )
                            activeHandle = hitTest(local, cs.value)
                        },
                        onDrag = { _, delta ->
                            val rect = latestImageRect.value ?: return@detectDragGestures
                            val cs   = latestCropState.value
                            if (activeHandle != DragHandle.NONE) {
                                cs.value = updateCrop(
                                    cs.value, activeHandle,
                                    delta.x, delta.y,
                                    rect.width, rect.height
                                )
                            }
                        },
                        onDragEnd    = { activeHandle = DragHandle.NONE },
                        onDragCancel = { activeHandle = DragHandle.NONE }
                    )
                }
        ) {
            val rect = imageRect ?: return@Canvas
            val c    = cropState.value

            // ── 1. Draw bitmap ──
            drawImage(
                image     = imageBitmap,
                dstOffset = IntOffset(rect.offsetX.roundToInt(), rect.offsetY.roundToInt()),
                dstSize   = IntSize(rect.width.roundToInt(), rect.height.roundToInt())
            )

            // ── 2. Crop overlay (everything translated to image-local space) ──
            translate(rect.offsetX, rect.offsetY) {

                val dim = Color.Black.copy(alpha = 0.58f)

                // Dim: 4 rectangles outside crop
                drawRect(dim, topLeft = Offset(0f, 0f),           size = Size(rect.width, c.top))
                drawRect(dim, topLeft = Offset(0f, c.bottom),     size = Size(rect.width, rect.height - c.bottom))
                drawRect(dim, topLeft = Offset(0f, c.top),        size = Size(c.left, c.height))
                drawRect(dim, topLeft = Offset(c.right, c.top),   size = Size(rect.width - c.right, c.height))

                // Crop border
                drawRect(
                    color   = Color.White,
                    topLeft = Offset(c.left, c.top),
                    size    = Size(c.width, c.height),
                    style   = Stroke(width = 1.5.dp.toPx())
                )

                // Rule-of-thirds grid
                val gridColor = Color.White.copy(alpha = 0.30f)
                for (i in 1..2) {
                    val x = c.left + c.width  * i / 3f
                    val y = c.top  + c.height * i / 3f
                    drawLine(gridColor, Offset(x, c.top),    Offset(x, c.bottom), strokeWidth = 0.8.dp.toPx())
                    drawLine(gridColor, Offset(c.left, y),   Offset(c.right, y),  strokeWidth = 0.8.dp.toPx())
                }

                // Corner handles (L-shaped)
                val hLen   = 22.dp.toPx()
                val hWidth = 3.5.dp.toPx()
                val w      = Color.White

                // TL
                drawLine(w, Offset(c.left, c.top), Offset(c.left + hLen, c.top), hWidth)
                drawLine(w, Offset(c.left, c.top), Offset(c.left, c.top  + hLen), hWidth)
                // TR
                drawLine(w, Offset(c.right, c.top), Offset(c.right - hLen, c.top), hWidth)
                drawLine(w, Offset(c.right, c.top), Offset(c.right, c.top + hLen), hWidth)
                // BL
                drawLine(w, Offset(c.left, c.bottom), Offset(c.left + hLen, c.bottom), hWidth)
                drawLine(w, Offset(c.left, c.bottom), Offset(c.left, c.bottom - hLen), hWidth)
                // BR
                drawLine(w, Offset(c.right, c.bottom), Offset(c.right - hLen, c.bottom), hWidth)
                drawLine(w, Offset(c.right, c.bottom), Offset(c.right, c.bottom - hLen), hWidth)

                // Edge mid-point dots
                val dotR = 4.5.dp.toPx()
                listOf(
                    Offset((c.left + c.right) / 2f, c.top),
                    Offset((c.left + c.right) / 2f, c.bottom),
                    Offset(c.left,  (c.top + c.bottom) / 2f),
                    Offset(c.right, (c.top + c.bottom) / 2f),
                ).forEach { drawCircle(Color.White, dotR, it) }
            }
        }

        // ── Top Bar ──────────────────────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(Color.Black.copy(0.5f), CircleShape)
                    .clip(CircleShape)
                    .clickable(enabled = !isProcessing) { onRetake() },
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.ArrowBack, "Ulangi", tint = Color.White, modifier = Modifier.size(22.dp))
            }

            Text(
                "Edit Foto",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleMedium,
                letterSpacing = 0.5.sp
            )

            Box(
                modifier = Modifier
                    .height(40.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        if (!isProcessing) GradientButton
                        else Brush.horizontalGradient(listOf(Color(0xFF888888), Color(0xFFAAAAAA)))
                    )
                    .clickable(enabled = !isProcessing) {
                        val rect    = imageRect
                        val cropped = if (rect != null)
                            cropBitmap(workingBitmap, cropState.value, rect.width, rect.height)
                        else
                            workingBitmap
                        onConfirm(cropped)
                    }
                    .padding(horizontal = 18.dp),
                contentAlignment = Alignment.Center
            ) {
                if (isProcessing) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                } else {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Icon(Icons.Default.Check, null, tint = Color.White, modifier = Modifier.size(16.dp))
                        Text("Gunakan", color = Color.White, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelLarge)
                    }
                }
            }
        }

        // ── Bottom Toolbar ────────────────────────────────────────
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .background(Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(0.9f))))
                .navigationBarsPadding()
                .padding(top = 24.dp, bottom = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "Seret sudut untuk memotong  ·  Seret dalam area untuk memindahkan",
                color = Color.White.copy(0.50f),
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.padding(bottom = 20.dp)
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(28.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                EditorToolButton(
                    icon    = Icons.Default.RotateLeft,
                    label   = "Putar Kiri",
                    onClick = { workingBitmap = rotateBitmapCCW(workingBitmap) }
                )

                // Reset crop button
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .background(Color.White.copy(0.12f), CircleShape)
                        .border(1.dp, Color.White.copy(0.28f), CircleShape)
                        .clip(CircleShape)
                        .clickable {
                            imageRect?.let { r ->
                                cropState.value = CropRect(0f, 0f, r.width, r.height)
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text("↺", color = Color.White, style = MaterialTheme.typography.titleLarge)
                }

                EditorToolButton(
                    icon    = Icons.Default.RotateRight,
                    label   = "Putar Kanan",
                    onClick = { workingBitmap = rotateBitmapCW(workingBitmap) }
                )
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────
//  Tool button helper
// ─────────────────────────────────────────────────────────────

@Composable
private fun EditorToolButton(icon: ImageVector, label: String, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .background(Color.White.copy(0.12f), CircleShape)
                .border(1.dp, Color.White.copy(0.28f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, label, tint = Color.White, modifier = Modifier.size(26.dp))
        }
        Spacer(Modifier.height(5.dp))
        Text(label, color = Color.White.copy(0.65f), style = MaterialTheme.typography.labelSmall)
    }
}