package com.example.frontdesk.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Hotel
import androidx.compose.material.icons.filled.KingBed
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.SingleBed
import androidx.compose.material3.Badge
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.frontdesk.model.Bill
import com.example.frontdesk.model.Guest
import com.example.frontdesk.model.RoomType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BackTopBar(title: String, onBack: () -> Unit) {
    TopAppBar(
        title = { Text(title) },
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }
        },
    )
}

@Composable
fun StatsCard(stats: List<Pair<String, String>>) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.padding(16.dp)) {
            stats.forEach { (label, value) ->
                StatItem(label = label, value = value, modifier = Modifier.weight(1f))
            }
        }
    }
}

@Composable
fun StatItem(label: String, value: String, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Text(label, style = MaterialTheme.typography.labelMedium)
        Text(value, style = MaterialTheme.typography.titleLarge)
    }
}

@Composable
fun ChipSelector(options: List<String>, selected: String, onSelect: (String) -> Unit) {
    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        options.forEach { option ->
            FilterChip(
                selected = option == selected,
                onClick = { onSelect(option) },
                label = { Text(option) },
            )
        }
    }
}

@Composable
fun Stepper(label: String, value: Int, onValueChange: (Int) -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(label, modifier = Modifier.weight(1f))
        IconButton(onClick = { if (value > 1) onValueChange(value - 1) }) {
            Icon(Icons.Filled.Remove, contentDescription = "Decrease")
        }
        Text("$value", style = MaterialTheme.typography.titleMedium)
        IconButton(onClick = { onValueChange(value + 1) }) {
            Icon(Icons.Filled.Add, contentDescription = "Increase")
        }
    }
}

@Composable
fun RoomIcon(type: RoomType, roomNumber: Int) {
    val icon = when (type) {
        RoomType.SINGLE -> Icons.Filled.SingleBed
        RoomType.DOUBLE -> Icons.Filled.KingBed
        RoomType.SUITE -> Icons.Filled.Hotel
    }
    Box {
        Image(
            imageVector = icon,
            contentDescription = type.label,
            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onPrimaryContainer),
            modifier = Modifier
                .size(48.dp)
                .background(MaterialTheme.colorScheme.primaryContainer, CircleShape)
                .padding(10.dp),
        )
        Badge(modifier = Modifier.align(Alignment.BottomEnd)) {
            Text("$roomNumber")
        }
    }
}

@Composable
fun GuestCard(guest: Guest, onClick: () -> Unit) {
    Card(onClick = onClick, modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            RoomIcon(type = guest.room.type, roomNumber = guest.room.number)
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(guest.name, style = MaterialTheme.typography.titleMedium)
                Text("${guest.room.type.label} · ${guest.nights} nights")
            }
            if (guest.vehicle != null) {
                Icon(Icons.Filled.DirectionsCar, contentDescription = "Has a car")
            }
        }
    }
}

@Composable
fun BillLine(label: String, amount: Int, bold: Boolean = false) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Text(label, modifier = Modifier.weight(1f))
        Text("$amount KZT", fontWeight = if (bold) FontWeight.Bold else null)
    }
}

@Composable
fun BillCard(bill: Bill, nights: Int, nightlyRate: Int) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text("Bill", style = MaterialTheme.typography.titleMedium)
            BillLine("Room: $nights × $nightlyRate", bill.roomCost)
            BillLine("Parking", bill.parkingCost)
            BillLine("Total", bill.total, bold = true)
        }
    }
}
