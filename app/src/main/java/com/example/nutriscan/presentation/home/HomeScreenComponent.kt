package com.example.nutriscan.presentation.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.nutriscan.domain.model.User
import com.example.nutriscan.presentation.theme.NutritionAppTheme
import java.util.Calendar
import com.example.nutriscan.R

@Composable
fun HomeHeader(user: User?, onProfileClick: () -> Unit) {
    val greeting = remember {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        when (hour) {
            in 0..10 -> "Selamat Pagi,"
            in 11..14 -> "Selamat Siang,"
            in 15..17 -> "Selamat Sore,"
            else -> "Selamat Malam,"
        }
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable { onProfileClick() }
            .padding(vertical = 8.dp)
    ) {
        AsyncImage(
            model = user?.profilePictureUrl,
            contentDescription = "Foto Profil",
            modifier = Modifier
                .size(54.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentScale = ContentScale.Crop,
            placeholder = rememberVectorPainter(Icons.Default.Person),
            error = rememberVectorPainter(Icons.Default.Person)
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column {
            Text(
                text = greeting,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "${user?.displayName ?: "Pengguna"}!",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
fun DailyTipCard(
    tipText: String,
    isLoading: Boolean,
    onRefreshClick: () -> Unit
) {
    Column {
        Text(
            text = "Tahukah Kamu?",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(12.dp))

        Card(
            // Diubah menjadi Primary penuh agar senada dengan BottomBar dan tombol utama
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primary
            ),
            shape = RoundedCornerShape(20.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(Color.White.copy(alpha = 0.2f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Lightbulb,
                                contentDescription = "Tips",
                                tint = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Tips Hari Ini",
                            color = MaterialTheme.colorScheme.onPrimary,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    if (!isLoading) {
                        Text(
                            text = "Refresh",
                            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f),
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { onRefreshClick() }
                                .padding(4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (isLoading) {
                    Box(modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(24.dp))
                    }
                } else {
                    Text(
                        text = tipText,
                        color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.95f),
                        style = MaterialTheme.typography.bodyMedium,
                        lineHeight = 22.sp
                    )
                }
            }
        }
    }
}

@Composable
fun WaterTrackerCard(glassesDrank: Int, targetGlasses: Int, onWaterClick: (Int) -> Unit) {
    Column {
        Text(
            text = "Ayo Minum Air Putih",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(12.dp))

        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(20.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "$glassesDrank / $targetGlasses Gelas Hari Ini",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Logika pembagian 2 baris otomatis (4 item per baris)
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

                                    // Area Gelas yang bisa diklik
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .clickable { onWaterClick(i) }
                                            .padding(4.dp), // Jarak sentuh agar lebih nyaman
                                        contentAlignment = Alignment.Center
                                    ) {
                                        if (isFilled) {


                                            Image(
                                                painter = painterResource(id = R.drawable.ic_gelas_penuh),
                                                contentDescription = "Gelas Terisi",
                                                modifier = Modifier.size(60.dp)
                                            )


                                        } else {
                                            Image(
                                                painter = painterResource(id = R.drawable.ic_gelas_kosong),
                                                contentDescription = "Gelas Terisi",
                                                modifier = Modifier.size(60.dp)
                                            )

                                        }
                                    }
                                } else {
                                    // Spacer kosong untuk menjaga posisi tetap seimbang
                                    // jika target minumnya ganjil (misal 7 gelas)
                                    Spacer(modifier = Modifier.width(60.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// === KOMPONEN FITUR UNGGULAN (SENADA) ===
@Composable
fun FeaturedSection(
    onNavigateToScan: () -> Unit,
    onNavigateToHistori: () -> Unit
) {
    Column {
        Text(
            text = "Fitur Unggulan",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(12.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            // Kedua Card sekarang menggunakan warna Surface (Putih) agar seragam dengan Water Tracker
            FeatureCard(
                title = "Scan\nLabel AI",
                icon = Icons.Rounded.DocumentScanner,
                modifier = Modifier.weight(1f),
                onClick = onNavigateToScan
            )

            FeatureCard(
                title = "Pantau\nRiwayat",
                icon = Icons.Rounded.History,
                modifier = Modifier.weight(1f),
                onClick = onNavigateToHistori
            )
        }
    }
}

@Composable
fun FeatureCard(
    title: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        // Background putih bersih dengan elevasi halus
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier
            .height(130.dp)
            .clickable { onClick() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    // Ikon dibungkus warna abu-abu lembut (surfaceVariant)
                    .background(MaterialTheme.colorScheme.surfaceVariant, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    // Icon menggunakan warna Primary agar senada
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(22.dp)
                )
            }
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                lineHeight = 20.sp
            )
        }
    }
}

@Composable
fun HomeBottomBar(
    onNavigateToHome: () -> Unit,
    onScanClick: () -> Unit,
    onNavigateToHistori: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(124.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        Surface(
            color = MaterialTheme.colorScheme.primary,
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(90.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 40.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { onNavigateToHome() }
                        .padding(8.dp)
                ) {
                    Icon(Icons.Default.Home, contentDescription = "Home", tint = MaterialTheme.colorScheme.onPrimary)
                    Text("Home", color = MaterialTheme.colorScheme.onPrimary, fontSize = 11.sp, fontWeight = FontWeight.Medium)
                }

                Spacer(modifier = Modifier.width(50.dp))

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { onNavigateToHistori() }
                        .padding(8.dp)
                ) {
                    Icon(Icons.Default.List, contentDescription = "Histori", tint = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.6f))
                    Text("Histori", color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.6f), fontSize = 11.sp, fontWeight = FontWeight.Medium)
                }
            }
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(bottom = 20.dp)
        ) {
            FloatingActionButton(
                onClick = onScanClick,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.primary,
                shape = CircleShape,
                modifier = Modifier
                    .size(68.dp)
                    .offset(y = (-12).dp)
            ) {
                Icon(Icons.Rounded.DocumentScanner, contentDescription = "Scan", modifier = Modifier.size(32.dp))
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
private fun WaterTrackPrev() {
    NutritionAppTheme { 
        WaterTrackerCard(
            glassesDrank = 2,
            targetGlasses = 8
        ) { }
    }
}