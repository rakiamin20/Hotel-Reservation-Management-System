package com.hotel.model;

import java.util.List;

/**
 * Abstract class representing a room in the hotel.
 * Demonstrates Abstraction and Encapsulation.
 */
public abstract class Room {
    private String roomNumber;
    private String status; // "Available", "Occupied", "Maintenance"
    private double baseRate;

    public Room(String roomNumber, String status, double baseRate) {
        this.roomNumber = roomNumber;
        this.status = status;
        this.baseRate = baseRate;
    }

    // Getters and Setters
    public String getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public double getBaseRate() {
        return baseRate;
    }

    public void setBaseRate(double baseRate) {
        this.baseRate = baseRate;
    }

    /**
     * Calculates total price for a given number of nights.
     * Demonstrates Polymorphism (can be overridden by subclasses).
     */
    public double calculateTotalPrice(int nights) {
        if (nights <= 0) return 0;
        return baseRate * nights;
    }

    /**
     * Abstract method demonstrating Abstraction.
     * Returns the type of room (e.g., "Standard", "Deluxe", "Suite").
     */
    public abstract String getRoomType();

    /**
     * Abstract method returning list of amenities unique to the room type.
     */
    public abstract List<String> getAmenities();
}
