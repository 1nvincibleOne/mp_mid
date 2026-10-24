package com.example.frontdesk.model

enum class RoomType(val label: String, val nightlyRate: Int) {
    SINGLE("Single", 15000),
    DOUBLE("Double", 22000),
    SUITE("Suite", 40000),
}

data class Room(val number: Int, val type: RoomType)

data class Guest(
    val name: String,
    val nights: Int,
    val room: Room,
    val vehicle: Vehicle?,
    val parkingSpot: Int?,
)

data class Bill(val roomCost: Int, val parkingCost: Int) {
    val total: Int
        get() = roomCost + parkingCost
}

data class StayRecord(val guestName: String, val room: Room, val nights: Int, val bill: Bill)

sealed class CheckInResult {
    data class CheckedIn(val guest: Guest) : CheckInResult()
    object NoFreeRoom : CheckInResult()
    object ParkingFull : CheckInResult()
    data class CarAlreadyParked(val plate: String) : CheckInResult()
}

fun createVehicle(type: String, plate: String, owner: String): Vehicle = when (type) {
    "Motorcycle" -> Motorcycle(plate, owner)
    "Truck" -> Truck(plate, owner)
    else -> Car(plate, owner)
}

// Guests' cars are billed for the full stay, 24 hours per night
fun parkingCost(vehicle: Vehicle?, nights: Int): Int = vehicle?.fee(nights * 24) ?: 0

class Hotel(val rooms: List<Room>, parkingCapacity: Int) {
    private val parking = ParkingLot(parkingCapacity)
    private val guests = mutableMapOf<Int, Guest>() // room number -> guest
    private val records = mutableListOf<StayRecord>()

    val parkingCapacity: Int = parking.capacity

    val currentGuests: List<Guest>
        get() = guests.values.sortedBy { it.room.number }

    val history: List<StayRecord>
        get() = records.toList()

    val freeParking: Int
        get() = parking.capacity - parking.occupied

    fun freeRooms(type: RoomType): Int = rooms.count { it.type == type && it.number !in guests }

    fun checkIn(name: String, nights: Int, type: RoomType, vehicle: Vehicle?): CheckInResult {
        require(nights > 0) { "Nights must be positive, got $nights" }
        val room = rooms.firstOrNull { it.type == type && it.number !in guests }
            ?: return CheckInResult.NoFreeRoom

        var spot: Int? = null
        if (vehicle != null) {
            when (val result = parking.park(vehicle)) {
                is ParkingResult.Parked -> spot = result.spot
                is ParkingResult.AlreadyParked -> return CheckInResult.CarAlreadyParked(result.plate)
                ParkingResult.LotFull -> return CheckInResult.ParkingFull
            }
        }

        val guest = Guest(name, nights, room, vehicle, spot)
        guests[room.number] = guest
        return CheckInResult.CheckedIn(guest)
    }

    fun bill(guest: Guest): Bill =
        Bill(guest.nights * guest.room.type.nightlyRate, parkingCost(guest.vehicle, guest.nights))

    fun checkOut(roomNumber: Int): StayRecord? {
        val guest = guests.remove(roomNumber) ?: return null
        guest.vehicle?.let { parking.leave(it.plate) }

        val record = StayRecord(guest.name, guest.room, guest.nights, bill(guest))
        records.add(record)
        return record
    }

    fun totalRevenue(): Int = records.sumOf { it.bill.total }
}
