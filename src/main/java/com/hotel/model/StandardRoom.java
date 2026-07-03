package com.hotel.model;

import java.util.Arrays;
import java.util.List;

/**
 * StandardRoom class demonstrating Inheritance.
 */
public class StandardRoom extends Room {

    public StandardRoom(String roomNumber, String status, double baseRate) {
        super(roomNumber, status, baseRate);
    }

    @Override
    public String getRoomType() {
        return "Standard";
    }

    @Override
    public List<String> getAmenities() {
        return Arrays.asList("Wi-Fi", "Air Conditioning", "TV", "Desk");
    }
}
