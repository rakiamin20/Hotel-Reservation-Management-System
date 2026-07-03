package com.hotel.model;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * Reservation class representing a room booking.
 * Demonstrates Encapsulation and modular design.
 */
public class Reservation {
    private int id;
    private int guestId;
    private String guestName; // Added for convenient TableView binding
    private String roomNumber;
    private String roomType;  // Added for convenient TableView binding
    private String checkInDate;  // stored as YYYY-MM-DD
    private String checkOutDate; // stored as YYYY-MM-DD
    private double totalPrice;
    private String status; // "Active", "Checked Out", "Cancelled"

    // Constructor for existing reservations loaded from DB
    public Reservation(int id, int guestId, String guestName, String roomNumber, String roomType,
                       String checkInDate, String checkOutDate, double totalPrice, String status) {
        this.id = id;
        this.guestId = guestId;
        this.guestName = guestName;
        this.roomNumber = roomNumber;
        this.roomType = roomType;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.totalPrice = totalPrice;
        this.status = status;
    }

    // Constructor for new reservations
    public Reservation(int guestId, String roomNumber, String checkInDate, String checkOutDate, 
                       double totalPrice, String status) {
        this.id = -1;
        this.guestId = guestId;
        this.roomNumber = roomNumber;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.totalPrice = totalPrice;
        this.status = status;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getGuestId() {
        return guestId;
    }

    public void setGuestId(int guestId) {
        this.guestId = guestId;
    }

    public String getGuestName() {
        return guestName;
    }

    public void setGuestName(String guestName) {
        this.guestName = guestName;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }

    public String getRoomType() {
        return roomType;
    }

    public void setRoomType(String roomType) {
        this.roomType = roomType;
    }

    public String getCheckInDate() {
        return checkInDate;
    }

    public void setCheckInDate(String checkInDate) {
        this.checkInDate = checkInDate;
    }

    public String getCheckOutDate() {
        return checkOutDate;
    }

    public void setCheckOutDate(String checkOutDate) {
        this.checkOutDate = checkOutDate;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * Helper to calculate the duration of reservation in nights.
     */
    public int getNights() {
        try {
            LocalDate start = LocalDate.parse(checkInDate);
            LocalDate end = LocalDate.parse(checkOutDate);
            long days = ChronoUnit.DAYS.between(start, end);
            return days <= 0 ? 1 : (int) days;
        } catch (Exception e) {
            return 1;
        }
    }
}
