package com.hotel.model;

import java.util.Arrays;
import java.util.List;

/**
 * DeluxeRoom class demonstrating Inheritance and Polymorphism (method overriding).
 */
public class DeluxeRoom extends Room {
    private static final double SERVICE_CHARGE_PERCENT = 0.08; // 8% service charge

    public DeluxeRoom(String roomNumber, String status, double baseRate) {
        super(roomNumber, status, baseRate);
    }

    @Override
    public String getRoomType() {
        return "Deluxe";
    }

    @Override
    public List<String> getAmenities() {
        return Arrays.asList("Wi-Fi", "Air Conditioning", "TV", "Mini Fridge", "Balcony", "Coffee Maker");
    }

    /**
     * Polymorphic implementation of calculateTotalPrice.
     * Incorporates an 8% deluxe service charge.
     */
    @Override
    public double calculateTotalPrice(int nights) {
        double baseTotal = super.calculateTotalPrice(nights);
        return baseTotal * (1 + SERVICE_CHARGE_PERCENT);
    }
}
