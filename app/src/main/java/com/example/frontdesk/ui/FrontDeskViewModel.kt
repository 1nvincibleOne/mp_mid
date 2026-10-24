package com.example.frontdesk.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.frontdesk.model.Bill
import com.example.frontdesk.model.Car
import com.example.frontdesk.model.CheckInResult
import com.example.frontdesk.model.Guest
import com.example.frontdesk.model.Hotel
import com.example.frontdesk.model.Room
import com.example.frontdesk.model.RoomType
import com.example.frontdesk.model.StayRecord
import com.example.frontdesk.model.createVehicle
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

val vehicleTypes = listOf("Car", "Motorcycle", "Truck")

data class FrontDeskUiState(
    val guests: List<Guest> = emptyList(),
    val bills: Map<Int, Bill> = emptyMap(), // room number -> current bill
    val freeRooms: Map<RoomType, Int> = emptyMap(),
    val totalRooms: Int = 0,
    val freeParking: Int = 0,
    val parkingCapacity: Int = 0,
    val history: List<StayRecord> = emptyList(),
    val revenue: Int = 0,
    val filter: String = "All",
    val message: String? = null,
    val plate: String = "",
    val scanning: Boolean = false,
) {
    val visibleGuests: List<Guest>
        get() = if (filter == "All") guests else guests.filter { it.room.type.label == filter }
}

class FrontDeskViewModel : ViewModel() {
    private val hotel = Hotel(
        rooms = listOf(
            Room(101, RoomType.SINGLE), Room(102, RoomType.SINGLE), Room(103, RoomType.SINGLE),
            Room(201, RoomType.DOUBLE), Room(202, RoomType.DOUBLE),
            Room(301, RoomType.SUITE),
        ),
        parkingCapacity = 3,
    )

    private val _uiState = MutableStateFlow(FrontDeskUiState())
    val uiState: StateFlow<FrontDeskUiState> = _uiState.asStateFlow()

    init {
        hotel.checkIn("Aliya Nurlanova", 2, RoomType.SINGLE, Car("123ABC01", "Aliya Nurlanova"))
        hotel.checkIn("John Smith", 3, RoomType.DOUBLE, null)
        refresh()
    }

    private var scanJob: Job? = null

    // Clears the plate left from a previous, cancelled check-in
    fun startCheckIn() {
        cancelScan()
        _uiState.update { it.copy(plate = "") }
    }

    fun setPlate(plate: String) {
        cancelScan() // a manual edit wins over a pending scan
        _uiState.update { it.copy(plate = plate) }
    }

    // Simulates a camera reading the plate of the car at the entrance
    fun scanPlate() {
        if (_uiState.value.scanning) return
        scanJob = viewModelScope.launch {
            _uiState.update { it.copy(scanning = true) }
            delay(1500)
            val plate = "${(100..999).random()}${listOf("ABC", "KZT", "AST", "ALA").random()}0${(1..9).random()}"
            _uiState.update { it.copy(scanning = false, plate = plate) }
        }
    }

    private fun cancelScan() {
        scanJob?.cancel()
        scanJob = null
        _uiState.update { it.copy(scanning = false) }
    }

    // Returns an error message, or null when the guest was checked in
    fun checkIn(name: String, nights: Int, type: RoomType, vehicleType: String?): String? {
        val cleanName = name.trim()
        val plate = _uiState.value.plate.trim().uppercase()
        if (cleanName.isEmpty()) return "Enter the guest's name"
        if (vehicleType != null && plate.isEmpty()) return "Enter or scan the car's plate"

        val vehicle = vehicleType?.let { createVehicle(it, plate, cleanName) }
        return when (val result = hotel.checkIn(cleanName, nights, type, vehicle)) {
            is CheckInResult.CheckedIn -> {
                val spot = result.guest.parkingSpot?.let { ", parking spot $it" } ?: ""
                _uiState.update { it.copy(plate = "") }
                refresh("$cleanName checked into room ${result.guest.room.number}$spot")
                null
            }
            CheckInResult.NoFreeRoom -> "No free ${type.label} rooms"
            CheckInResult.ParkingFull -> "The parking lot is full"
            is CheckInResult.CarAlreadyParked -> "${result.plate} is already parked"
        }
    }

    fun checkOut(roomNumber: Int) {
        val record = hotel.checkOut(roomNumber) ?: return
        refresh("${record.guestName} checked out and paid ${record.bill.total} KZT")
    }

    fun setFilter(filter: String) {
        _uiState.update { it.copy(filter = filter) }
    }

    private fun refresh(message: String? = null) {
        _uiState.update {
            it.copy(
                guests = hotel.currentGuests,
                bills = hotel.currentGuests.associate { guest -> guest.room.number to hotel.bill(guest) },
                freeRooms = RoomType.entries.associateWith { type -> hotel.freeRooms(type) },
                totalRooms = hotel.rooms.size,
                freeParking = hotel.freeParking,
                parkingCapacity = hotel.parkingCapacity,
                history = hotel.history,
                revenue = hotel.totalRevenue(),
                message = message,
            )
        }
    }
}
