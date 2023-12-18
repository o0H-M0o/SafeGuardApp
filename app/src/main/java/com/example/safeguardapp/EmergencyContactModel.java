package com.example.safeguardapp;

public class EmergencyContactModel {
    private int id;
    private String name;
    private String phoneNumber;

    public EmergencyContactModel(int id, String name, String phoneNumber) {
        this.id = id;
        this.name = name;
        this.phoneNumber = phoneNumber;
    }
    public EmergencyContactModel() {
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}

