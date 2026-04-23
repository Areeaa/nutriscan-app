package com.example.nutriscan.presentation.history

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.nutriscan.domain.model.ScanHistory
import com.example.nutriscan.presentation.theme.*
import kotlin.math.cos
import kotlin.math.sin

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    viewModel: HistoryViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToDetail: (String) -> Unit
) {
    val searchQuery by viewModel.searchQuery.collectAsState()
    val filteredHistory by viewModel.filteredHistory.collectAsState()
    val selectedMonth by viewModel.selectedMonth.collectAsState()
    val selectedYear by viewModel.selectedYear.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    val monthNames = arrayOf(
        "Januari", "Februari", "Maret", "April", "Mei", "Juni",
        "Juli", "Agustus", "September", "Oktober", "November", "Desember"
    )

    Scaffold(
        containerColor = BackgroundCream
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // ── Gradient header ──
            HistoryHeader(
                monthName = monthNames[selectedMonth],
                year = selectedYear,
                total = filteredHistory.size,
                onNavigateBack = onNavigateBack,
                onPreviousClick = { viewModel.previousMonth() },
                onNextClick = { viewModel.nextMonth() }
            )

            // ── Content ──
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp)
            ) {
                Spacer(Modifier.height(20.dp))

                // Search bar
                HistorySearchBar(
                    query = searchQuery,
                    onQueryChange = { viewModel.onSearchQueryChanged(it) }
                )

                Spacer(Modifier.height(20.dp))

                // Divider
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(BorderColor)
                )

                Spacer(Modifier.height(16.dp))

                // List
                HistoryList(
                    isLoading = isLoading,
                    historyList = filteredHistory,
                    onItemClick = onNavigateToDetail
                )
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────
//  GRADIENT HEADER
// ─────────────────────────────────────────────────────────────
@Composable
fun HistoryHeader(
    monthName: String,
    year: Int,
    total: Int,
    onNavigateBack: () -> Unit,
    onPreviousClick: () -> Unit,
    onNextClick: () -> Unit
) {
    val inf = rememberInfiniteTransition(label = "histBlob")
    val blobT by inf.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(6000, easing = LinearEasing)), label = "bt"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(bottomStart = 28.dp, bottomEnd = 28.dp))
            .background(brush = GradientHeroVertical)
    ) {
        Canvas(modifier = Modifier.fillMaxWidth().height(170.dp)) {
            val w = size.width; val h = size.height
            drawCircle(
                brush = Brush.radialGradient(listOf(Color(0x4448AEAD), Color(0x0048AEAD)),
                    center = Offset(w * 0.82f + cos(blobT * 6.28f) * 12.dp.toPx(), h * 0.3f), radius = 100.dp.toPx()),
                center = Offset(w * 0.82f + cos(blobT * 6.28f) * 12.dp.toPx(), h * 0.3f), radius = 100.dp.toPx()
            )
            drawCircle(
                brush = Brush.radialGradient(listOf(Color(0x33D48C70), Color(0x00D48C70)),
                    center = Offset(w * 0.15f, h * 0.7f + sin(blobT * 6.28f) * 8.dp.toPx()), radius = 80.dp.toPx()),
                center = Offset(w * 0.15f, h * 0.7f + sin(blobT * 6.28f) * 8.dp.toPx()), radius = 80.dp.toPx()
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 20.dp)
                .padding(top = 14.dp, bottom = 24.dp)
        ) {
            // Back button row
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(Color.White.copy(alpha = 0.18f), CircleShape)
                        .clickable { onNavigateBack() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Kembali", tint = Color.White, modifier = Modifier.size(20.dp))
                }
                Spacer(Modifier.width(12.dp))
                Text("Riwayat Scan", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 18.sp)
            }

            Spacer(Modifier.height(20.dp))

            // Month-year navigator + stats
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Month filter pill
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.White.copy(alpha = 0.15f))
                        .border(1.dp, Color.White.copy(alpha = 0.22f), RoundedCornerShape(12.dp))
                        .padding(horizontal = 4.dp, vertical = 2.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = onPreviousClick, modifier = Modifier.size(34.dp)) {
                            Icon(Icons.Default.ChevronLeft, contentDescription = null, tint = Color.White)
                        }
                        Text(
                            "$monthName $year",
                            color = Color.White,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp
                        )
                        IconButton(onClick = onNextClick, modifier = Modifier.size(34.dp)) {
                            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color.White)
                        }
                    }
                }

                // Total chip
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.White.copy(alpha = 0.18f))
                        .padding(horizontal = 14.dp, vertical = 8.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("$total", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 22.sp)
                        Text("Label", color = Color.White.copy(alpha = 0.75f), fontSize = 11.sp)
                    }
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────
//  SEARCH BAR
// ─────────────────────────────────────────────────────────────
@Composable
fun HistorySearchBar(query: String, onQueryChange: (String) -> Unit) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        placeholder = {
            Text("Cari riwayat scan...", color = TextTertiary, fontSize = 14.sp)
        },
        leadingIcon = {
            Icon(Icons.Default.Search, contentDescription = null, tint = PrimaryTeal, modifier = Modifier.size(20.dp))
        },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedBorderColor = BorderColor,
            focusedBorderColor = PrimaryTeal,
            unfocusedContainerColor = SurfaceWhite,
            focusedContainerColor = SurfaceWhite
        ),
        singleLine = true
    )
}

// ─────────────────────────────────────────────────────────────
//  HISTORY LIST
// ─────────────────────────────────────────────────────────────
@Composable
fun HistoryList(isLoading: Boolean, historyList: List<ScanHistory>, onItemClick: (String) -> Unit) {
    if (isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = PrimaryTeal, strokeWidth = 2.dp)
        }
    } else if (historyList.isEmpty()) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("📋", fontSize = 40.sp)
            Spacer(Modifier.height(12.dp))
            Text("Belum ada riwayat", fontWeight = FontWeight.Bold, color = TextPrimary)
            Text("Scan label makanan pertamamu!", color = TextSecondary, fontSize = 13.sp)
        }
    } else {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(bottom = 32.dp)
        ) {
            items(historyList) { history ->
                HistoryItemCard(history = history, onClick = { onItemClick(history.id) })
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────
//  HISTORY ITEM CARD
// ─────────────────────────────────────────────────────────────
@Composable
fun HistoryItemCard(history: ScanHistory, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(SurfaceWhite)
            .border(1.dp, BorderColor, RoundedCornerShape(14.dp))
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left gradient dot
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(brush = GradientCard),
                contentAlignment = Alignment.Center
            ) {
                Text("🥫", fontSize = 20.sp)
            }

            Spacer(Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = history.labelName,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary,
                    fontSize = 15.sp
                )
                Spacer(Modifier.height(2.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .background(PrimaryTeal.copy(alpha = 0.1f), RoundedCornerShape(6.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text("${history.totalEnergi} kkal", color = PrimaryTeal, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }

            Icon(
                Icons.Default.KeyboardArrowRight,
                contentDescription = null,
                tint = TextTertiary,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

// Legacy helpers kept for compatibility
@Composable
fun MonthYearFilter(monthName: String, year: Int, onPreviousClick: () -> Unit, onNextClick: () -> Unit) {}

@Composable
fun TotalScanRow(total: Int) {}