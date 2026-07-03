package com.hotel.model;

import java.util.Arrays;
import java.util.List;

/**
 * SuiteRoom class demonstrating Inheritance and Polymorphism.
 */
public class SuiteRoom extends Room {
    private static final double FLAT_SERVICE_FEE = 50.0; // Flat luxury service fee
    private static final double TAX_PERCENT = 0.12; // 12% luxury tax

    public SuiteRoom(String roomNumber, String status, double baseRate) {
        super(roomNumber, status, baseRate);
    }

    @Override
    public String getRoomType() {
        return "Suite";
    }

    @Override
    public List<String> getAmenities() {
        return Arrays.asList("Wi-Fi", "Air Conditioning", "Smart TV", "Mini Fridge", "Living Area", 
                             "Bathtub", "Free Breakfast", "Personal Concierge");
    }

    /**
     * Polymorphic implementation of calculateTotalPrice.
     * Incorporates flat service fee and 12% luxury tax.
     */
    @Override
    public double calculateTotalPrice(int nights) {
        if (nights <= 0) return 0;
        double baseTotal = super.calculateTotalPrice(nights);
        return (baseTotal + FLAT_SERVICE_FEE) * (1 + TAX_PERCENT);
    }
}
