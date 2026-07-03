package com.hotel.model;

/**
 * User class representing system administrators or hotel staff.
 * Demonstrates Inheritance and Polymorphism.
 */
public class User extends Person {
    private int id;
    private String username;
    private String password;
    private String role; // "ADMIN" or "STAFF"

    public User(int id, String firstName, String lastName, String email, String phone, 
                String username, String password, String role) {
        super(firstName, lastName, email, phone);
        this.id = id;
        this.username = username;
        this.password = password;
        this.role = role;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    /**
     * Polymorphic implementation of getDetails from Person.
     */
    @Override
    public String getDetails() {
        return "User [Role: " + role + ", Username: " + username + ", Name: " + getFullName() + "]";
    }
}
