package com.example.frontdesk.model

interface Billable {
    fun hourlyRate(): Int

    fun fee(hours: Int): Int = hourlyRate() * hours
}

open class Vehicle(val plate: String, val owner: String) : Billable {
    open val type: String = "Vehicle"

    override fun hourlyRate(): Int = 300

    override fun toString(): String = "$type $plate ($owner)"
}

class Car(plate: String, owner: String) : Vehicle(plate, owner) {
    override val type = "Car"
}

class Motorcycle(plate: String, owner: String) : Vehicle(plate, owner) {
    override val type = "Motorcycle"

    override fun hourlyRate(): Int = 150
}

class Truck(plate: String, owner: String) : Vehicle(plate, owner) {
    override val type = "Truck"

    override fun hourlyRate(): Int = 600

    // Trucks always pay for at least 2 hours
    override fun fee(hours: Int): Int = if (hours < 2) super.fee(2) else super.fee(hours)
}
