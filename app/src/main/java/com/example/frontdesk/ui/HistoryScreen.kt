package com.example.frontdesk.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun HistoryScreen(state: FrontDeskUiState, onBack: () -> Unit) {
    val roomRevenue = state.history.sumOf { it.bill.roomCost }
    val parkingRevenue = state.history.sumOf { it.bill.parkingCost }

    Scaffold(topBar = { BackTopBar(title = "History", onBack = onBack) }) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            item {
                StatsCard(
                    listOf(
                        "Stays" to "${state.history.size}",
                        "Rooms" to "$roomRevenue",
                        "Parking" to "$parkingRevenue",
                    )
                )
            }
            if (state.history.isEmpty()) {
                item { Text("No checked-out guests yet") }
            }
            items(state.history.reversed()) { record ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(record.guestName, style = MaterialTheme.typography.titleMedium)
                        Text("Room ${record.room.number} (${record.room.type.label}) · ${record.nights} nights")
                        BillLine("Paid", record.bill.total, bold = true)
                    }
                }
            }
        }
    }
}
