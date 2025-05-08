package com.example.fyp_clearcanvas;

public class User {
    private String userId;
    private String name;
    private String email;
    private String dateOfBirth;
    private String membership;
    private int numConsultations;

    public User() {
    }

    public User(String userId, String name, String email, String dateOfBirth, String membership, int numConsultations) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.dateOfBirth = dateOfBirth;
        this.membership = membership;
        this.numConsultations = numConsultations;
    }

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

    public String getMembershiph() {
        return membership;
    }

    public void setMembership(String membership) {
        this.membership = membership;  }

    public int getNumConsultations() {
        return numConsultations;
    }

    public void setNumConsultations(int numConsultations) {
        this.numConsultations = numConsultations;
    }
}
