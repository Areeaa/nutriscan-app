package com.example.nutriscan.presentation.scanner

import android.graphics.Bitmap
import android.graphics.Matrix
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import com.example.nutriscan.presentation.theme.*
import kotlin.math.abs
import kotlin.math.roundToInt

// ─────────────────────────────────────────────────
//  Data types
// ─────────────────────────────────────────────────

private data class CropRect(
    val left: Float, val top: Float,
    val right: Float, val bottom: Float
) {
    val width  get() = right - left
    val height get() = bottom - top
}

private data class ImageDisplayRect(
    val offsetX: Float, val offsetY: Float,
    val width: Float, val height: Float
)

private enum class DragHandle { NONE, TL, TR, BL, BR, MOVE }

private const val HANDLE_HIT_PX = 60f   // touch target (manhattan distance)
private const val MIN_CROP_PX   = 72f   // minimum crop dimension in display px

// ─────────────────────────────────────────────────
//  Bitmap utilities (public so ViewModel can use)
// ─────────────────────────────────────────────────

fun rotateBitmapCW(src: Bitmap): Bitmap {
    val m = Matrix().apply { postRotate(90f) }
    return Bitmap.createBitmap(src, 0, 0, src.width, src.height, m, true)
}

fun rotateBitmapCCW(src: Bitmap): Bitmap {
    val m = Matrix().apply { postRotate(-90f) }
    return Bitmap.createBitmap(src, 0, 0, src.width, src.height, m, true)
}

private fun applyCropToBitmap(
    src: Bitmap,
    crop: CropRect,
    dispW: Float,
    dispH: Float
): Bitmap {
    val scaleX = src.width  / dispW
    val scaleY = src.height / dispH
    val x = (crop.left   * scaleX).toInt().coerceIn(0, src.width  - 1)
    val y = (crop.top    * scaleY).toInt().coerceIn(0, src.height - 1)
    val w = (crop.width  * scaleX).toInt().coerceAtLeast(1).coerceAtMost(src.width  - x)
    val h = (crop.height * scaleY).toInt().coerceAtLeast(1).coerceAtMost(src.height - y)
    return Bitmap.createBitmap(src, x, y, w, h)
}

// ─────────────────────────────────────────────────
//  Gesture helpers
// ─────────────────────────────────────────────────

private fun hitTest(touch: Offset, crop: CropRect): DragHandle {
    fun dist(ax: Float, ay: Float) = abs(touch.x - ax) + abs(touch.y - ay)

    // Corner priority
    val corners = listOf(
        DragHandle.TL to (crop.left  to crop.top),
        DragHandle.TR to (crop.right to crop.top),
        DragHandle.BL to (crop.left  to crop.bottom),
        DragHandle.BR to (crop.right to crop.bottom),
    )
    val nearest = corners.minByOrNull { (_, p) -> dist(p.first, p.second) }
    if (nearest != null) {
        val (handle, pos) = nearest
        if (dist(pos.first, pos.second) < HANDLE_HIT_PX) return handle
    }

    // Inside crop → move
    if (touch.x in crop.left..crop.right && touch.y in crop.top..crop.bottom)
        return DragHandle.MOVE

    return DragHandle.NONE
}

private fun moveCrop(
    crop: CropRect, handle: DragHandle, dx: Float, dy: Float,
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
        val newL = (crop.left + dx).coerceIn(0f, maxW - crop.width)
        val newT = (crop.top  + dy).coerceIn(0f, maxH - crop.height)
        crop.copy(left = newL, top = newT, right = newL + crop.width, bottom = newT + crop.height)
    }
    DragHandle.NONE -> crop
}

// ─────────────────────────────────────────────────
//  Main composable
// ─────────────────────────────────────────────────

@Composable
fun ImageEditorContent(
    bitmap: Bitmap,
    isProcessing: Boolean,
    onRetake: () -> Unit,
    onConfirm: (Bitmap) -> Unit
) {
    // Current (possibly rotated) working bitmap
    var workingBitmap by remember(bitmap) { mutableStateOf(bitmap) }

    // Container size (the image area, excluding toolbars) – tracked in px
    var containerSizePx by remember { mutableStateOf(IntSize.Zero) }

    // Compute where the image is actually drawn (ContentScale.Fit inside container)
    val imageRect: ImageDisplayRect? = remember(
        workingBitmap.width, workingBitmap.height, containerSizePx
    ) {
        val cW = containerSizePx.width.toFloat()
        val cH = containerSizePx.height.toFloat()
        if (cW <= 0f || cH <= 0f) return@remember null
        val bmpAspect = workingBitmap.width.toFloat() / workingBitmap.height.toFloat()
        val ctnAspect = cW / cH
        val (imgW, imgH) = if (bmpAspect > ctnAspect)
            cW to cW / bmpAspect
        else
            cH * bmpAspect to cH
        ImageDisplayRect(
            offsetX = (cW - imgW) / 2f,
            offsetY = (cH - imgH) / 2f,
            width   = imgW,
            height  = imgH
        )
    }

    // Crop rectangle in image-local display px
    val cropState = remember { mutableStateOf(CropRect(0f, 0f, 0f, 0f)) }

    // Reset crop whenever imageRect changes (rotation or first layout)
    LaunchedEffect(imageRect) {
        imageRect?.let { r ->
            cropState.value = CropRect(0f, 0f, r.width, r.height)
        }
    }

    // Which handle is actively being dragged
    var activeHandle by remember { mutableStateOf(DragHandle.NONE) }

    val density = LocalDensity.current

    Box(modifier = Modifier.fillMaxSize().background(Color(0xFF0D0D0D))) {

        // ── Image ──────────────────────────────────────────────
        Image(
            bitmap = workingBitmap.asImageBitmap(),
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
                .onSizeChanged { containerSizePx = it },
            contentScale = ContentScale.Fit
        )

        // ── Crop overlay + drag handles ────────────────────────
        if (imageRect != null) {
            Box(
                modifier = Modifier
                    .offset {
                        IntOffset(
                            imageRect.offsetX.roundToInt(),
                            imageRect.offsetY.roundToInt()
                        )
                    }
                    .size(
                        width  = with(density) { imageRect.width.toDp() },
                        height = with(density) { imageRect.height.toDp() }
                    )
                    // Single pointer area for all crop dragging
                    .pointerInput(Unit) {
                        detectDragGestures(
                            onDragStart = { offset ->
                                activeHandle = hitTest(offset, cropState.value)
                            },
                            onDrag = { _, delta ->
                                if (activeHandle != DragHandle.NONE) {
                                    cropState.value = moveCrop(
                                        cropState.value, activeHandle,
                                        delta.x, delta.y,
                                        imageRect.width, imageRect.height
                                    )
                                }
                            },
                            onDragEnd    = { activeHandle = DragHandle.NONE },
                            onDragCancel = { activeHandle = DragHandle.NONE }
                        )
                    }
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val c = cropState.value
                    val dim = Color.Black.copy(alpha = 0.58f)

                    // ── Dim 4 surrounding areas ──
                    drawRect(dim, topLeft = Offset(0f, 0f),        size = Size(size.width, c.top))
                    drawRect(dim, topLeft = Offset(0f, c.bottom),  size = Size(size.width, size.height - c.bottom))
                    drawRect(dim, topLeft = Offset(0f, c.top),     size = Size(c.left, c.height))
                    drawRect(dim, topLeft = Offset(c.right, c.top),size = Size(size.width - c.right, c.height))

                    // ── Crop border ──
                    drawRect(
                        color = Color.White,
                        topLeft = Offset(c.left, c.top),
                        size = Size(c.width, c.height),
                        style = Stroke(width = 1.5.dp.toPx())
                    )

                    // ── Rule-of-thirds grid ──
                    val grid = Color.White.copy(alpha = 0.28f)
                    repeat(2) { i ->
                        val xLine = c.left + c.width  * (i + 1) / 3f
                        val yLine = c.top  + c.height * (i + 1) / 3f
                        drawLine(grid, Offset(xLine, c.top), Offset(xLine, c.bottom), 0.8.dp.toPx())
                        drawLine(grid, Offset(c.left, yLine), Offset(c.right, yLine), 0.8.dp.toPx())
                    }

                    // ── Corner handles ──
                    val hLen   = 20.dp.toPx()
                    val hWidth = 3.dp.toPx()
                    val hColor = Color.White

                    fun cornerTL(px: Float, py: Float) {
                        drawLine(hColor, Offset(px, py), Offset(px + hLen, py), hWidth)
                        drawLine(hColor, Offset(px, py), Offset(px, py + hLen), hWidth)
                    }
                    fun cornerTR(px: Float, py: Float) {
                        drawLine(hColor, Offset(px, py), Offset(px - hLen, py), hWidth)
                        drawLine(hColor, Offset(px, py), Offset(px, py + hLen), hWidth)
                    }
                    fun cornerBL(px: Float, py: Float) {
                        drawLine(hColor, Offset(px, py), Offset(px + hLen, py), hWidth)
                        drawLine(hColor, Offset(px, py), Offset(px, py - hLen), hWidth)
                    }
                    fun cornerBR(px: Float, py: Float) {
                        drawLine(hColor, Offset(px, py), Offset(px - hLen, py), hWidth)
                        drawLine(hColor, Offset(px, py), Offset(px, py - hLen), hWidth)
                    }
                    cornerTL(c.left,  c.top)
                    cornerTR(c.right, c.top)
                    cornerBL(c.left,  c.bottom)
                    cornerBR(c.right, c.bottom)

                    // Edge mid-handles (small dots)
                    val dotR = 4.dp.toPx()
                    listOf(
                        Offset((c.left + c.right) / 2f, c.top),
                        Offset((c.left + c.right) / 2f, c.bottom),
                        Offset(c.left,  (c.top + c.bottom) / 2f),
                        Offset(c.right, (c.top + c.bottom) / 2f),
                    ).forEach { drawCircle(Color.White, dotR, it) }
                }
            }
        }

        // ── Top Bar ────────────────────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Back / Retake
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

            // Confirm button
            Box(
                modifier = Modifier
                    .height(40.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(if (!isProcessing) GradientButton else Brush.horizontalGradient(listOf(Color(0xFF888888), Color(0xFFAAAAAA))))
                    .clickable(enabled = !isProcessing) {
                        val rect = imageRect
                        val cropped = if (rect != null)
                            applyCropToBitmap(workingBitmap, cropState.value, rect.width, rect.height)
                        else
                            workingBitmap
                        onConfirm(cropped)
                    }
                    .padding(horizontal = 18.dp),
                contentAlignment = Alignment.Center
            ) {
                if (isProcessing) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Icon(Icons.Default.Check, null, tint = Color.White, modifier = Modifier.size(16.dp))
                        Text("Gunakan", color = Color.White, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelLarge)
                    }
                }
            }
        }

        // ── Bottom Toolbar ─────────────────────────────────────
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .background(
                    Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(0.88f)))
                )
                .navigationBarsPadding()
                .padding(top = 24.dp, bottom = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "Seret sudut untuk memotong · Seret dalam area untuk menggeser",
                color = Color.White.copy(0.55f),
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.padding(bottom = 20.dp)
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(28.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Rotate CCW
                EditorToolButton(
                    icon = Icons.Default.RotateLeft,
                    label = "Putar Kiri",
                    onClick = { workingBitmap = rotateBitmapCCW(workingBitmap) }
                )

                // Reset crop
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
                    Text("⟳", color = Color.White, style = MaterialTheme.typography.titleLarge)
                }

                // Rotate CW
                EditorToolButton(
                    icon = Icons.Default.RotateRight,
                    label = "Putar Kanan",
                    onClick = { workingBitmap = rotateBitmapCW(workingBitmap) }
                )
            }
        }
    }
}

// ─────────────────────────────────────────────────
//  Tool button
// ─────────────────────────────────────────────────

@Composable
private fun EditorToolButton(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit
) {
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
        Text(
            label,
            color = Color.White.copy(0.65f),
            style = MaterialTheme.typography.labelSmall
        )
    }
}