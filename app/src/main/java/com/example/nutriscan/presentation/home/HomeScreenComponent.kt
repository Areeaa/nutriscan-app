package com.example.nutriscan.presentation.home

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.rounded.DocumentScanner
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.Lightbulb
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.nutriscan.R
import com.example.nutriscan.domain.model.User
import com.example.nutriscan.presentation.theme.*
import java.util.Calendar
import kotlin.math.cos
import kotlin.math.sin

// ─────────────────────────────────────────────────────────────
//  HOME HEADER  — gradient hero with blobs
// ─────────────────────────────────────────────────────────────
@Composable
fun HomeHeader(user: User?, onProfileClick: () -> Unit) {
    val greeting = remember {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        when (hour) {
            in 0..10 -> "Selamat Pagi"
            in 11..14 -> "Selamat Siang"
            in 15..17 -> "Selamat Sore"
            else -> "Selamat Malam"
        }
    }

    // Animated blobs
    val inf = rememberInfiniteTransition(label = "hdrBlob")
    val blobT by inf.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(7000, easing = LinearEasing)),
        label = "hdrBlobT"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(bottomStart = 28.dp, bottomEnd = 28.dp))
            .background(brush = GradientHeroVertical)
            .padding(bottom = 28.dp)
    ) {
        // Blob canvas
        Canvas(modifier = Modifier.fillMaxWidth().height(180.dp)) {
            val w = size.width; val h = size.height
            drawCircle(
                brush = Brush.radialGradient(
                    listOf(Color(0x5548AEAD), Color(0x0048AEAD)),
                    center = Offset(w * 0.88f + cos(blobT * 6.28f) * 14.dp.toPx(), h * 0.25f),
                    radius = 110.dp.toPx()
                ),
                center = Offset(w * 0.88f + cos(blobT * 6.28f) * 14.dp.toPx(), h * 0.25f),
                radius = 110.dp.toPx()
            )
            drawCircle(
                brush = Brush.radialGradient(
                    listOf(Color(0x33D48C70), Color(0x00D48C70)),
                    center = Offset(w * 0.12f, h * 0.75f + sin(blobT * 6.28f) * 10.dp.toPx()),
                    radius = 90.dp.toPx()
                ),
                center = Offset(w * 0.12f, h * 0.75f + sin(blobT * 6.28f) * 10.dp.toPx()),
                radius = 90.dp.toPx()
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 24.dp)
                .padding(top = 20.dp)
        ) {
            // Top row: greeting + avatar
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    Text(
                        text = greeting,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.72f)
                    )
                    Text(
                        text = "${user?.displayName ?: "Pengguna"} 👋",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .border(2.dp, Color.White.copy(alpha = 0.4f), CircleShape)
                        .clickable { onProfileClick() },
                    contentAlignment = Alignment.Center
                ) {
                    AsyncImage(
                        model = user?.profilePictureUrl,
                        contentDescription = "Profil",
                        modifier = Modifier.fillMaxSize().clip(CircleShape),
                        contentScale = ContentScale.Crop,
                        placeholder = rememberVectorPainter(Icons.Default.Person),
                        error = rememberVectorPainter(Icons.Default.Person)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Quick scan CTA banner
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.White.copy(alpha = 0.14f))
                    .border(1.dp, Color.White.copy(alpha = 0.22f), RoundedCornerShape(16.dp))
                    .padding(horizontal = 18.dp, vertical = 14.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column {
                        Text(
                            text = "Scan Label Makanan",
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontSize = 15.sp
                        )
                        Text(
                            text = "AI analisis gizi seketika",
                            color = Color.White.copy(alpha = 0.7f),
                            fontSize = 12.sp
                        )
                    }
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(Color.White.copy(alpha = 0.2f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Rounded.DocumentScanner,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────
//  DAILY TIP CARD  — gradient card with glassmorphism feel
// ─────────────────────────────────────────────────────────────
@Composable
fun DailyTipCard(
    tipText: String,
    isLoading: Boolean,
    onRefreshClick: () -> Unit
) {
    Column {
        SectionTitle(title = "Tips Hari Ini")
        Spacer(modifier = Modifier.height(10.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(brush = GradientCard)
                .padding(20.dp)
        ) {
            // Subtle blob inside card
            Canvas(modifier = Modifier.matchParentSize()) {
                drawCircle(
                    color = Color(0x22FFFFFF),
                    center = Offset(size.width * 0.85f, size.height * 0.2f),
                    radius = 60.dp.toPx()
                )
            }

            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .background(Color.White.copy(alpha = 0.2f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Rounded.Lightbulb,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(Modifier.width(10.dp))
                        Text(
                            "Fakta Gizi",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    }
                    if (!isLoading) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color.White.copy(alpha = 0.18f))
                                .clickable { onRefreshClick() }
                                .padding(horizontal = 10.dp, vertical = 5.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Rounded.Refresh,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(Modifier.width(4.dp))
                                Text(
                                    "Refresh",
                                    color = Color.White,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                if (isLoading) {
                    Box(Modifier.fillMaxWidth().padding(vertical = 12.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(22.dp), strokeWidth = 2.dp)
                    }
                } else {
                    Text(
                        text = tipText,
                        color = Color.White.copy(alpha = 0.92f),
                        style = MaterialTheme.typography.bodyMedium,
                        lineHeight = 22.sp
                    )
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────
//  WATER TRACKER CARD  — modern with gradient progress
// ─────────────────────────────────────────────────────────────
@Composable
fun WaterTrackerCard(
    glassesDrank: Int,
    targetGlasses: Int,
    onWaterClick: (Int) -> Unit
) {
    val progress = glassesDrank.toFloat() / targetGlasses.toFloat()

    Column {
        SectionTitle(title = "Tracker Air Minum")
        Spacer(modifier = Modifier.height(10.dp))

        Card(
            colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
            shape = RoundedCornerShape(20.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, BorderColor, RoundedCornerShape(20.dp))
        ) {
            Column(modifier = Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {

                // Progress fraction text
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column {
                        Text(
                            "$glassesDrank / $targetGlasses Gelas",
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary,
                            fontSize = 16.sp
                        )
                        Text(
                            "Target harian terpenuhi ${(progress * 100).toInt()}%",
                            color = TextSecondary,
                            fontSize = 12.sp
                        )
                    }
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(
                                if (glassesDrank >= targetGlasses)
                                    Brush.radialGradient(listOf(Color(0xFF3DAD72), Color(0xFF52C98A)))
                                else
                                    Brush.radialGradient(listOf(PrimaryTeal, PrimaryLight))
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            if (glassesDrank >= targetGlasses) "✅" else "💧",
                            fontSize = 20.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Gradient progress bar
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(SurfaceVariant)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(progress.coerceIn(0f, 1f))
                            .fillMaxHeight()
                            .clip(RoundedCornerShape(4.dp))
                            .background(brush = GradientButton)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Glass grid
                val itemsPerRow = 4
                val rows = (targetGlasses + itemsPerRow - 1) / itemsPerRow
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    for (r in 0 until rows) {
                        Row(
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            for (c in 0 until itemsPerRow) {
                                val i = r * itemsPerRow + c + 1
                                if (i <= targetGlasses) {
                                    val isFilled = i <= glassesDrank
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .clickable { onWaterClick(i) }
                                            .padding(4.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Image(
                                            painter = painterResource(
                                                if (isFilled) R.drawable.ic_gelas_penuh
                                                else R.drawable.ic_gelas_kosong
                                            ),
                                            contentDescription = null,
                                            modifier = Modifier.size(58.dp)
                                        )
                                    }
                                } else {
                                    Spacer(Modifier.width(58.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────
//  FEATURED SECTION — modern gradient feature cards
// ─────────────────────────────────────────────────────────────
@Composable
fun FeaturedSection(
    onNavigateToScan: () -> Unit,
    onNavigateToHistori: () -> Unit
) {
    Column {
        SectionTitle(title = "Fitur Unggulan")
        Spacer(modifier = Modifier.height(10.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            FeatureCard(
                title = "Scan\nLabel AI",
                subtitle = "Analisis instan",
                icon = Icons.Rounded.DocumentScanner,
                gradient = GradientCard,
                modifier = Modifier.weight(1f),
                onClick = onNavigateToScan
            )
            FeatureCard(
                title = "Pantau\nRiwayat",
                subtitle = "Telusuri riwayat scanmu",
                icon = Icons.Rounded.History,
                gradient = GradientWarm,
                modifier = Modifier.weight(1f),
                onClick = onNavigateToHistori
            )
        }
    }
}

@Composable
fun FeatureCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    gradient: Brush,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .height(138.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(gradient)
            .clickable { onClick() }
            .padding(18.dp)
    ) {
        // Inner blob
        Canvas(modifier = Modifier.matchParentSize()) {
            drawCircle(
                color = Color(0x22FFFFFF),
                center = Offset(size.width * 0.85f, size.height * 0.15f),
                radius = 50.dp.toPx()
            )
        }

        Column(verticalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(Color.White.copy(alpha = 0.2f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(22.dp))
            }
            Column {
                Text(title, fontWeight = FontWeight.Bold, color = Color.White, lineHeight = 20.sp, fontSize = 15.sp)
                Text(subtitle, color = Color.White.copy(alpha = 0.72f), fontSize = 11.sp)
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────
//  BOTTOM BAR — glassmorphism
// ─────────────────────────────────────────────────────────────
@Composable
fun HomeBottomBar(
    onNavigateToHome: () -> Unit,
    onScanClick: () -> Unit,
    onNavigateToHistori: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .background(Color.Transparent),
        contentAlignment = Alignment.BottomCenter
    ) {
        // Glass bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(78.dp)
                .align(Alignment.BottomCenter)
                .background(
                    Brush.verticalGradient(
                        listOf(Color(0xF2FFFFFF), Color(0xFFFFFFFF))
                    )
                )
                .border(
                    width = 1.dp,
                    brush = Brush.verticalGradient(listOf(Color(0x33000000), Color.Transparent)),
                    shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
                )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 36.dp)
                    .align(Alignment.Center),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                NavBarItem(
                    icon = Icons.Default.Home,
                    label = "Home",
                    isActive = true,
                    onClick = onNavigateToHome
                )
                Spacer(Modifier.width(72.dp)) // FAB gap
                NavBarItem(
                    icon = Icons.Default.List,
                    label = "Riwayat",
                    isActive = false,
                    onClick = onNavigateToHistori
                )
            }
        }

        // Gradient FAB
        Box(
            modifier = Modifier
                .size(62.dp)
                .offset(y = (-14).dp)
                .shadow(8.dp, CircleShape)
                .clip(CircleShape)
                .background(brush = GradientButton)
                .clickable { onScanClick() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Rounded.DocumentScanner,
                contentDescription = "Scan",
                tint = Color.White,
                modifier = Modifier.size(28.dp)
            )
        }
    }
}

@Composable
private fun NavBarItem(
    icon: ImageVector,
    label: String,
    isActive: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Icon(
            icon,
            contentDescription = label,
            tint = if (isActive) PrimaryTeal else TextTertiary,
            modifier = Modifier.size(22.dp)
        )
        Spacer(Modifier.height(3.dp))
        Text(
            label,
            color = if (isActive) PrimaryTeal else TextTertiary,
            fontSize = 11.sp,
            fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal
        )
    }
}

// ─────────────────────────────────────────────────────────────
//  SECTION TITLE helper
// ─────────────────────────────────────────────────────────────
@Composable
fun SectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        color = TextPrimary
    )
}

@Composable
fun GradientButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    Box(
        modifier = modifier
            .height(52.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(
                if (enabled) GradientButton else Brush.horizontalGradient(
                    listOf(Color(0xFFBBCCCE), Color(0xFFCCD5D5))
                )
            )
            .then(
                if (enabled) Modifier else Modifier
            ),
        contentAlignment = Alignment.Center
    ) {
        Button(
            onClick = onClick,
            enabled = enabled,
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Transparent,
                contentColor = Color.White,
                disabledContainerColor = Color.Transparent,
                disabledContentColor = Color.White.copy(alpha = 0.6f)
            ),
            modifier = Modifier.fillMaxSize(),
            elevation = ButtonDefaults.buttonElevation(0.dp, 0.dp, 0.dp)
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )
            )
        }
    }
}

@Preview(
    showBackground = true,
    showSystemUi = true,
    backgroundColor = 0xFFF5F4F1,
    name = "Home Full Preview"
)
@Composable
fun HomeFullPreview() {
    NutritionAppTheme {

        val dummyUser = User(
            displayName = "Ari",
            profilePictureUrl = null
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF5F4F1))
        ) {

            HomeHeader(
                user = dummyUser,
                onProfileClick = {}
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {

                DailyTipCard(
                    tipText = "Kurangi konsumsi gula berlebih untuk menjaga kesehatan tubuh.",
                    isLoading = false,
                    onRefreshClick = {}
                )

                WaterTrackerCard(
                    glassesDrank = 3,
                    targetGlasses = 8,
                    onWaterClick = {}
                )

                FeaturedSection(
                    onNavigateToScan = {},
                    onNavigateToHistori = {}
                )
            }
        }
    }
}