package com.example.fyp_clearcanvas;

public class User {
    private String userId;
    private String name;
    private String email;
    private String dateOfBirth;

    // Default constructor (required for Firebase Realtime Database)
    public User() {
    }

    // Constructor with parameters
    public User(String userId, String name, String email, String dateOfBirth) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.dateOfBirth = dateOfBirth;
    }

    // Getters and setters
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }
}
