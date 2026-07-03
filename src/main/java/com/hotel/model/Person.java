package com.hotel.model;

/**
 * Abstract class demonstrating Abstraction and Encapsulation.
 * Represents a person in the hotel reservation system.
 */
public abstract class Person {
    private String firstName;
    private String lastName;
    private String email;
    private String phone;

    // Constructor
    public Person(String firstName, String lastName, String email, String phone) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
    }

    // Getters and Setters (Encapsulation)
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    /**
     * Helper method to return the full name.
     */
    public String getFullName() {
        return firstName + " " + lastName;
    }

    /**
     * Abstract method demonstrating Abstraction.
     * Subclasses must provide their own custom details string.
     */
    public abstract String getDetails();
}
