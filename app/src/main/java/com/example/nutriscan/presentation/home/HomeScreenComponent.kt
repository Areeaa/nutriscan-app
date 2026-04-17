package com.example.nutriscan.presentation.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.rounded.DocumentScanner
import androidx.compose.material.icons.rounded.Lightbulb
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.nutriscan.domain.model.User
import com.example.nutriscan.presentation.theme.NutritionAppTheme
import com.example.nutriscan.presentation.theme.PrimaryTeal
import com.example.nutriscan.presentation.theme.SurfaceVariant
import com.example.nutriscan.presentation.theme.TertiarySage
import com.example.nutriscan.presentation.theme.TextPrimary
import com.example.nutriscan.presentation.theme.WarningYellow
import java.util.Calendar


@Composable
fun HomeHeader(user: User?, onProfileClick: () -> Unit) {
    // Menentukan sapaan berdasarkan jam di HP
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
            .clip(RoundedCornerShape(8.dp))
            .clickable { onProfileClick() }
            .padding(vertical = 8.dp)
    ) {

        AsyncImage(
            model = user?.profilePictureUrl,
            contentDescription = "Foto Profil",
            modifier = Modifier
                .size(50.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant), // Menggunakan warna tema sebagai background saat loading
            contentScale = ContentScale.Crop,
            // Tampilkan ikon orang jika URL kosong atau gagal dimuat
            placeholder = rememberVectorPainter(Icons.Default.Person),
            error = rememberVectorPainter(Icons.Default.Person)
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column {
            Text(
                text = greeting,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant // Sesuaikan dengan warna teks sekunder kamu
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
    Text(
        text = "Tahukah Kamu?",
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        color = TextPrimary
    )
    Spacer(modifier = Modifier.height(8.dp))

    Card(
        colors = CardDefaults.cardColors(containerColor = PrimaryTeal),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Rounded.Lightbulb,
                        contentDescription = "Tips",
                        tint = WarningYellow,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Tips Kesehatan Hari Ini",
                        color = Color.White,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                // Tombol Refresh untuk minta tips baru ke Gemini
                if (!isLoading) {
                    Text(
                        text = "Refresh",
                        color = Color.White.copy(alpha = 0.7f),
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier
                            .clickable { onRefreshClick() }
                            .padding(4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (isLoading) {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = WarningYellow, modifier = Modifier.size(24.dp))
                }
            } else {
                Text(
                    text = tipText,
                    color = Color.White.copy(alpha = 0.9f),
                    style = MaterialTheme.typography.bodyMedium,
                    lineHeight = 22.sp
                )
            }
        }
    }
}



@Composable
fun WaterTrackerCard(glassesDrank: Int, targetGlasses: Int, onWaterClick: (Int) -> Unit) {
    Column {
        Text(text = "Ayo Minum Air Putih", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))

        Card(
            colors = CardDefaults.cardColors(containerColor = SurfaceVariant),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "$glassesDrank / $targetGlasses Gelas Hari Ini",
                    fontWeight = FontWeight.Bold,
                    color = PrimaryTeal
                )
                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Looping dari 1 sampai 8
                    for (i in 1..targetGlasses) {
                        val isFilled = i <= glassesDrank
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(if (isFilled) TertiarySage else Color.White)
                                // MENGIRIMKAN ANGKA GELAS YANG DIKLIK (i)
                                .clickable { onWaterClick(i) }
                        ) {
                            if (!isFilled) {
                                Box(
                                    modifier = Modifier
                                        .size(28.dp)
                                        .clip(CircleShape)
                                        .background(SurfaceVariant)
                                )
                            }
                        }
                    }
                }
            }
        }
    }

}


@Composable
fun NutritionArticleSection() {
    Column {
        Text(text = "Kenali Informasi Seputar Gizi", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Card(
                colors = CardDefaults.cardColors(containerColor = SurfaceVariant),
                modifier = Modifier
                    .weight(1f)
                    .height(150.dp)
            ) {}

            Card(
                colors = CardDefaults.cardColors(containerColor = SurfaceVariant),
                modifier = Modifier
                    .weight(1f)
                    .height(150.dp)
            ) {}
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
            color = PrimaryTeal,
            shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(95.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 40.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 1. Tombol Home (Kiri)
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp)) // Biar efek ripple kliknya rapi
                        .clickable { onNavigateToHome() }
                        .padding(8.dp) // Jarak aman untuk sentuhan jari
                ) {
                    Icon(Icons.Default.Home, contentDescription = "Home", tint = Color.White)
                    Text("Home", color = Color.White, fontSize = 10.sp)
                }

                // Spacer tengah untuk memberi ruang pada FAB Scan
                Spacer(modifier = Modifier.width(50.dp))

                // 2. Tombol Histori (Kanan)
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { onNavigateToHistori() }
                        .padding(8.dp)
                ) {
                    Icon(Icons.Default.List, contentDescription = "Histori", tint = Color.White.copy(alpha = 0.6f))
                    Text("Histori", color = Color.White.copy(alpha = 0.6f), fontSize = 10.sp)
                }
            }
        }

        // 3. Tombol FAB Scan di tengah atas
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(bottom = 16.dp)
        ) {
            FloatingActionButton(
                onClick = onScanClick,
                containerColor = Color.White,
                contentColor = PrimaryTeal,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .size(65.dp)
                    .offset(y = (-16).dp)
            ) {
                Icon(Icons.Rounded.DocumentScanner, contentDescription = "Scan", modifier = Modifier.size(32.dp))
            }
            Text("Scan Label", color = Color.White, fontSize = 10.sp)
        }
    }
}



@Preview(showBackground = true, backgroundColor = 0xFFF9F8F6)
@Composable
fun HomeHeaderPreview() {
    NutritionAppTheme {
        // Pakai padding biar kelihatan jaraknya di preview
        Box(modifier = Modifier.padding(16.dp)) {
            HomeHeader(user = null, onProfileClick = {})
        }
    }

}


@Preview(showBackground = true, backgroundColor = 0xFFF9F8F6)
@Composable
fun WaterTrackerCardPreview() {
    NutritionAppTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            // Contoh kalau udah minum 4 gelas dari target 8 gelas
            WaterTrackerCard(
                glassesDrank = 4, targetGlasses = 8,
                onWaterClick = {}
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF9F8F6)
@Composable
fun NutritionArticleSectionPreview() {
    NutritionAppTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            NutritionArticleSection()
        }
    }

}

@Preview(showBackground = true)
@Composable
fun HomeBottomBarPreview() {
    NutritionAppTheme {
        HomeBottomBar(
            onScanClick = {},
            onNavigateToHome = {},
            onNavigateToHistori = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun DailyTipPrev() {
    NutritionAppTheme {
        DailyTipCard(
            tipText = "Kamu tau tidak ini semua kan tidak ada yang abadi",
            isLoading = false
        ) { }
    }
}