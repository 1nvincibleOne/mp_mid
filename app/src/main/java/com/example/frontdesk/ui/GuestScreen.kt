package com.example.frontdesk.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun GuestScreen(
    state: FrontDeskUiState,
    roomNumber: Int,
    onCheckOut: () -> Unit,
    onBack: () -> Unit,
) {
    val guest = state.guests.find { it.room.number == roomNumber }
    val bill = state.bills[roomNumber]

    Scaffold(topBar = { BackTopBar(title = "Room $roomNumber", onBack = onBack) }) { padding ->
        if (guest == null || bill == null) {
            Text("This room is free", modifier = Modifier.padding(padding).padding(16.dp))
            return@Scaffold
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Card(modifier = Modifier.fillMaxWidth()) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    RoomIcon(type = guest.room.type, roomNumber = guest.room.number)
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(guest.name, style = MaterialTheme.typography.titleLarge)
                        Text("${guest.room.type.label} room · ${guest.nights} nights")
                        val car = guest.vehicle?.let { "${it.type} ${it.plate} · spot ${guest.parkingSpot}" }
                        Text(car ?: "No car")
                    }
                }
            }
            BillCard(bill = bill, nights = guest.nights, nightlyRate = guest.room.type.nightlyRate)
            Button(onClick = onCheckOut, modifier = Modifier.fillMaxWidth()) {
                Text("Check out · ${bill.total} KZT")
            }
        }
    }
}
