package com.example.frontdesk.model

sealed class ParkingResult {
    data class Parked(val spot: Int) : ParkingResult()
    data class AlreadyParked(val plate: String) : ParkingResult()
    object LotFull : ParkingResult()
}

class ParkingLot(val capacity: Int) {
    private val spots = mutableMapOf<Int, Vehicle>() // spot number -> vehicle

    val occupied: Int
        get() = spots.size

    fun park(vehicle: Vehicle): ParkingResult {
        if (spots.values.any { it.plate == vehicle.plate }) return ParkingResult.AlreadyParked(vehicle.plate)
        if (occupied >= capacity) return ParkingResult.LotFull

        var spot = 1
        while (spots.containsKey(spot)) {
            spot++
        }
        spots[spot] = vehicle
        return ParkingResult.Parked(spot)
    }

    fun leave(plate: String) {
        spots.entries.removeAll { it.value.plate == plate }
    }
}
