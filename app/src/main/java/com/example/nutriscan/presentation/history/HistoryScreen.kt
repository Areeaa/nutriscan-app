package com.example.nutriscan.presentation.history

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.nutriscan.domain.model.ScanHistory

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
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Riwayat Scan",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Kembali",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background // PureWhite
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp)
        ) {
            // Search Bar dengan border halus
            HistorySearchBar(
                query = searchQuery,
                onQueryChange = { viewModel.onSearchQueryChanged(it) }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Filter Bulan Tahun yang lebih clean
            MonthYearFilter(
                monthName = monthNames[selectedMonth],
                year = selectedYear,
                onPreviousClick = { viewModel.previousMonth() },
                onNextClick = { viewModel.nextMonth() }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Info Ringkas (Ganti dari Card Besar ke Row Minimalis)
            TotalScanRow(total = filteredHistory.size)

            Divider(
                modifier = Modifier.padding(vertical = 16.dp),
                color = MaterialTheme.colorScheme.surfaceVariant // SoftGray
            )

            // Daftar Riwayat
            HistoryList(
                isLoading = isLoading,
                historyList = filteredHistory,
                onItemClick = onNavigateToDetail
            )
        }
    }
}

@Composable
fun HistorySearchBar(query: String, onQueryChange: (String) -> Unit) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        placeholder = {
            Text(
                "Cari riwayat...",
                color = MaterialTheme.colorScheme.onSurfaceVariant, // TextSub
                fontSize = 14.sp
            )
        },
        leadingIcon = {
            Icon(
                Icons.Default.Search,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary, // AccentGreen
                modifier = Modifier.size(20.dp)
            )
        },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedBorderColor = MaterialTheme.colorScheme.surfaceVariant,
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
            focusedContainerColor = MaterialTheme.colorScheme.background
        ),
        singleLine = true
    )
}

@Composable
fun MonthYearFilter(monthName: String, year: Int, onPreviousClick: () -> Unit, onNextClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onPreviousClick) {
            Icon(Icons.Default.ChevronLeft, contentDescription = null, tint = MaterialTheme.colorScheme.onSurface)
        }
        Text(
            text = "$monthName $year",
            style = MaterialTheme.typography.bodyLarge.copy(
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            ),
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        IconButton(onClick = onNextClick) {
            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.onSurface)
        }
    }
}

@Composable
fun TotalScanRow(total: Int) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(50))
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "Ditemukan ",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 14.sp
        )
        Text(
            text = "$total Label",
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp
        )
    }
}

@Composable
fun HistoryList(isLoading: Boolean, historyList: List<ScanHistory>, onItemClick: (String) -> Unit) {
    if (isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(
                color = MaterialTheme.colorScheme.primary,
                strokeWidth = 2.dp
            )
        }
    } else if (historyList.isEmpty()) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "Kosong",
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                "Tidak ada riwayat bulan ini",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 13.sp
            )
        }
    } else {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(bottom = 32.dp)
        ) {
            items(historyList) { history ->
                HistoryItemCard(history = history, onClick = { onItemClick(history.id) })
            }
        }
    }
}

@Composable
fun HistoryItemCard(history: ScanHistory, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Indikator Visual Kecil di Samping
        Box(
            modifier = Modifier
                .width(4.dp)
                .height(40.dp)
                .background(
                    MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                    RoundedCornerShape(2.dp)
                )
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = history.labelName,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            )
            Text(
                text = "Energi: ${history.totalEnergi} kkal",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }

        Icon(
            Icons.Default.KeyboardArrowRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
            modifier = Modifier.size(20.dp)
        )
    }
}