package com.example.nutriscan.presentation.splash

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.nutriscan.presentation.theme.*
import kotlinx.coroutines.delay
import kotlin.math.cos
import kotlin.math.sin

// Data class for floating particles
private data class Particle(
    val x: Float,
    val y: Float,
    val radius: Float,
    val alpha: Float,
    val speed: Float,
    val angle: Float
)

@Composable
fun SplashScreen(
    onSplashFinished: () -> Unit
) {
    // === Animation phase tracking ===
    var animationPhase by remember { mutableStateOf(0) }

    // === Background gradient animated ===
    val bgAlpha by animateFloatAsState(
        targetValue = if (animationPhase >= 0) 1f else 0f,
        animationSpec = tween(600, easing = EaseOut),
        label = "bg"
    )

    // === Logo: scale pop ===
    val logoScale by animateFloatAsState(
        targetValue = when (animationPhase) {
            0 -> 0.3f
            1 -> 1.08f
            else -> 1f
        },
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "logoScale"
    )

    val logoAlpha by animateFloatAsState(
        targetValue = if (animationPhase >= 1) 1f else 0f,
        animationSpec = tween(500),
        label = "logoAlpha"
    )

    // === Glow pulse ring ===
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.35f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha"
    )

    // === Rotating arc ring ===
    val arcRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing)
        ),
        label = "arc"
    )

    // === Tagline slide-up + fade ===
    val taglineOffset by animateFloatAsState(
        targetValue = if (animationPhase >= 2) 0f else 30f,
        animationSpec = tween(600, easing = EaseOutQuart),
        label = "tagline"
    )
    val taglineAlpha by animateFloatAsState(
        targetValue = if (animationPhase >= 2) 1f else 0f,
        animationSpec = tween(600),
        label = "taglineAlpha"
    )

    // === App name letter reveal ===
    val appNameAlpha by animateFloatAsState(
        targetValue = if (animationPhase >= 2) 1f else 0f,
        animationSpec = tween(700, delayMillis = 100),
        label = "appName"
    )
    val appNameScale by animateFloatAsState(
        targetValue = if (animationPhase >= 2) 1f else 0.85f,
        animationSpec = tween(700, delayMillis = 100, easing = EaseOutBack),
        label = "appNameScale"
    )

    // === Bottom dots loader ===
    val dot1Alpha by infiniteTransition.animateFloat(
        initialValue = 0.25f, targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(600), repeatMode = RepeatMode.Reverse,
            initialStartOffset = StartOffset(0)
        ), label = "d1"
    )
    val dot2Alpha by infiniteTransition.animateFloat(
        initialValue = 0.25f, targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(600), repeatMode = RepeatMode.Reverse,
            initialStartOffset = StartOffset(200)
        ), label = "d2"
    )
    val dot3Alpha by infiniteTransition.animateFloat(
        initialValue = 0.25f, targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(600), repeatMode = RepeatMode.Reverse,
            initialStartOffset = StartOffset(400)
        ), label = "d3"
    )
    val loaderAlpha by animateFloatAsState(
        targetValue = if (animationPhase >= 3) 1f else 0f,
        animationSpec = tween(400),
        label = "loader"
    )

    // === Floating particles (static positions for determinism) ===
    val particles = remember {
        listOf(
            Particle(0.15f, 0.18f, 5f, 0.25f, 0f, 0f),
            Particle(0.82f, 0.12f, 8f, 0.2f, 0f, 0f),
            Particle(0.70f, 0.25f, 4f, 0.3f, 0f, 0f),
            Particle(0.10f, 0.40f, 6f, 0.18f, 0f, 0f),
            Particle(0.90f, 0.55f, 7f, 0.22f, 0f, 0f),
            Particle(0.05f, 0.75f, 5f, 0.2f, 0f, 0f),
            Particle(0.85f, 0.80f, 9f, 0.15f, 0f, 0f),
            Particle(0.20f, 0.88f, 4f, 0.28f, 0f, 0f),
            Particle(0.55f, 0.08f, 6f, 0.2f, 0f, 0f),
            Particle(0.35f, 0.92f, 5f, 0.22f, 0f, 0f),
        )
    }

    val particleFloat by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(4000, easing = LinearEasing)),
        label = "particleFloat"
    )
    val particleAlpha by animateFloatAsState(
        targetValue = if (animationPhase >= 1) 1f else 0f,
        animationSpec = tween(800, delayMillis = 200),
        label = "particleAlpha"
    )

    // === Orchestrate animation phases ===
    LaunchedEffect(Unit) {
        delay(100)
        animationPhase = 1   // Logo pops in
        delay(600)
        animationPhase = 2   // App name + tagline appears
        delay(400)
        animationPhase = 3   // Loader dots appear
        delay(1600)
        onSplashFinished()   // Navigate away
    }

    // ============================================================
    // UI
    // ============================================================
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF2D5A63),
                        Color(0xFF3E7580),
                        Color(0xFF4A8A97),
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        // --- Particle canvas ---
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .alpha(particleAlpha)
        ) {
            particles.forEachIndexed { i, p ->
                val floatOffset = ((particleFloat + i * 0.1f) % 1f)
                val yShift = sin(floatOffset * Math.PI.toFloat() * 2) * 12f
                drawCircle(
                    color = Color.White,
                    radius = p.radius.dp.toPx(),
                    center = Offset(
                        x = p.x * size.width,
                        y = p.y * size.height + yShift
                    ),
                    alpha = p.alpha
                )
            }
        }

        // --- Decorative top-right blob ---
        Box(
            modifier = Modifier
                .size(280.dp)
                .offset(x = 80.dp, y = (-160).dp)
                .alpha(0.08f)
                .clip(CircleShape)
                .background(Color.White)
                .align(Alignment.TopEnd)
        )

        // --- Decorative bottom-left blob ---
        Box(
            modifier = Modifier
                .size(220.dp)
                .offset(x = (-80).dp, y = 120.dp)
                .alpha(0.06f)
                .clip(CircleShape)
                .background(Color.White)
                .align(Alignment.BottomStart)
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Spacer(Modifier.weight(1f))

            // --- Logo area ---
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .scale(logoScale)
                    .alpha(logoAlpha)
            ) {
                // Glow pulse ring (outermost)
                Box(
                    modifier = Modifier
                        .size(148.dp)
                        .scale(pulseScale)
                        .alpha(pulseAlpha)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.15f))
                )

                // Rotating arc ring
                Canvas(modifier = Modifier.size(130.dp)) {
                    rotate(arcRotation) {
                        drawArc(
                            brush = Brush.sweepGradient(
                                listOf(
                                    Color.White.copy(alpha = 0f),
                                    Color.White.copy(alpha = 0.7f),
                                    Color.White.copy(alpha = 0f),
                                )
                            ),
                            startAngle = 0f,
                            sweepAngle = 240f,
                            useCenter = false,
                            style = Stroke(width = 2.5.dp.toPx(), cap = StrokeCap.Round)
                        )
                    }
                }

                // White circle background
                Box(
                    modifier = Modifier
                        .size(110.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.95f)),
                    contentAlignment = Alignment.Center
                ) {
                    // Inner logo icon — leaf + scan lines
                    Canvas(modifier = Modifier.size(62.dp)) {
                        val cx = size.width / 2
                        val cy = size.height / 2
                        val teal = Color(0xFF3E7580)
                        val terracotta = Color(0xFFD48C70)
                        val sage = Color(0xFF8FA8A0)

                        // Leaf shape (simplified as arc paths)
                        drawCircle(
                            color = teal,
                            radius = 22.dp.toPx(),
                            center = Offset(cx - 3.dp.toPx(), cy + 2.dp.toPx()),
                            alpha = 0.15f
                        )

                        // Main leaf body
                        drawArc(
                            color = teal,
                            startAngle = -60f,
                            sweepAngle = 200f,
                            useCenter = false,
                            style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
                        )

                        // Leaf vein
                        drawLine(
                            color = teal,
                            start = Offset(cx - 14.dp.toPx(), cy + 16.dp.toPx()),
                            end = Offset(cx + 14.dp.toPx(), cy - 14.dp.toPx()),
                            strokeWidth = 2.5.dp.toPx(),
                            cap = StrokeCap.Round
                        )

                        // Scan lines (terracotta accent)
                        for (i in 0..2) {
                            val yOff = (i - 1) * 8.dp.toPx()
                            drawLine(
                                color = terracotta,
                                start = Offset(cx - 12.dp.toPx(), cy + yOff),
                                end = Offset(cx + 12.dp.toPx(), cy + yOff),
                                strokeWidth = 1.8.dp.toPx(),
                                cap = StrokeCap.Round,
                                alpha = 0.75f
                            )
                        }

                        // Corner brackets
                        val brSize = 8.dp.toPx()
                        val brStroke = 2.5.dp.toPx()
                        val margin = 6.dp.toPx()
                        // Top-left
                        drawLine(color = sage, start = Offset(margin, margin), end = Offset(margin + brSize, margin), strokeWidth = brStroke, cap = StrokeCap.Round)
                        drawLine(color = sage, start = Offset(margin, margin), end = Offset(margin, margin + brSize), strokeWidth = brStroke, cap = StrokeCap.Round)
                        // Top-right
                        drawLine(color = sage, start = Offset(size.width - margin, margin), end = Offset(size.width - margin - brSize, margin), strokeWidth = brStroke, cap = StrokeCap.Round)
                        drawLine(color = sage, start = Offset(size.width - margin, margin), end = Offset(size.width - margin, margin + brSize), strokeWidth = brStroke, cap = StrokeCap.Round)
                        // Bottom-left
                        drawLine(color = sage, start = Offset(margin, size.height - margin), end = Offset(margin + brSize, size.height - margin), strokeWidth = brStroke, cap = StrokeCap.Round)
                        drawLine(color = sage, start = Offset(margin, size.height - margin), end = Offset(margin, size.height - margin - brSize), strokeWidth = brStroke, cap = StrokeCap.Round)
                        // Bottom-right
                        drawLine(color = sage, start = Offset(size.width - margin, size.height - margin), end = Offset(size.width - margin - brSize, size.height - margin), strokeWidth = brStroke, cap = StrokeCap.Round)
                        drawLine(color = sage, start = Offset(size.width - margin, size.height - margin), end = Offset(size.width - margin, size.height - margin - brSize), strokeWidth = brStroke, cap = StrokeCap.Round)
                    }
                }
            }

            Spacer(Modifier.height(32.dp))

            // --- App name ---
            Text(
                text = "NutriScan",
                fontSize = 38.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                letterSpacing = 1.5.sp,
                modifier = Modifier
                    .scale(appNameScale)
                    .alpha(appNameAlpha)
            )

            Spacer(Modifier.height(8.dp))

            // --- Tagline ---
            Text(
                text = "Scan Labelnya, Pahami Gizinya",
                fontSize = 15.sp,
                fontWeight = FontWeight.Normal,
                color = Color.White.copy(alpha = 0.75f),
                letterSpacing = 0.5.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .offset(y = taglineOffset.dp)
                    .alpha(taglineAlpha)
            )

            Spacer(Modifier.weight(1f))

            // --- Animated loader dots ---
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier
                    .padding(bottom = 60.dp)
                    .alpha(loaderAlpha)
            ) {
                LoaderDot(alpha = dot1Alpha)
                LoaderDot(alpha = dot2Alpha)
                LoaderDot(alpha = dot3Alpha)
            }
        }
    }
}

@Composable
private fun LoaderDot(alpha: Float) {
    Box(
        modifier = Modifier
            .size(8.dp)
            .alpha(alpha)
            .clip(CircleShape)
            .background(Color.White.copy(alpha = 0.8f))
    )
}