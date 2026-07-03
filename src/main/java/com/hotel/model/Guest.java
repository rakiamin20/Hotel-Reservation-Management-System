package com.hotel.model;

/**
 * Guest class representing a customer of the hotel.
 * Demonstrates Inheritance and Polymorphism (overriding getDetails).
 */
public class Guest extends Person {
    private int id;
    private String idCard;

    // Constructor for existing guests loaded from database
    public Guest(int id, String firstName, String lastName, String email, String phone, String idCard) {
        super(firstName, lastName, email, phone);
        this.id = id;
        this.idCard = idCard;
    }

    // Constructor for new guests before database insertion
    public Guest(String firstName, String lastName, String email, String phone, String idCard) {
        super(firstName, lastName, email, phone);
        this.id = -1; // -1 represents new unsaved guest
        this.idCard = idCard;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getIdCard() {
        return idCard;
    }

    public void setIdCard(String idCard) {
        this.idCard = idCard;
    }

    /**
     * Polymorphic implementation of getDetails from Person.
     */
    @Override
    public String getDetails() {
        return "Guest [ID: " + (id == -1 ? "N/A" : id) + ", Name: " + getFullName() + 
               ", ID Card: " + idCard + ", Email: " + getEmail() + ", Phone: " + getPhone() + "]";
    }
}
