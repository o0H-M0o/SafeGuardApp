package com.example.safeguardapp;

public class Incident {
    private String type;
    private String date;
    private String time;
    private String location;
    private String photoData;
    private String description;

    // Constructor
    public Incident(String type, String date, String time, String location, String photoData, String description) {
        this.type = type;
        this.date = date;
        this.time = time;
        this.location = location;
        this.photoData = photoData;
        this.description = description;
    }

    public void setType(String type){
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setDate(String date){
        this.date = date;
    }

    public String getDate(){
        return date;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTime() {
        return time;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getLocation(){
        return location;
    }

    public void setPhotoData(String photoData) {
        this.photoData = photoData;
    }

    public String getPhotoData() {
        return photoData;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}