package com.example.safeguardapp;

import java.util.List;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class Incidents {
    private String incidentId;
    private String date;
    private String time;
    private String status;
    private String photoData;
    private String type;
    private List<IncidentsDetail> detailsList;

    // Empty constructor for Firebase
    public Incidents() {
    }

    public Incidents(String incidentId, String date, String time, String status, String type, String photoData, List<IncidentsDetail> detailsList) {
        this.incidentId = incidentId;
        this.date = date;
        this.time = time;
        this.status = status;
        this.type = type;
        this.photoData = photoData;
        this.detailsList = detailsList;
    }

    public String getIncidentId() {
        return incidentId;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }
    public String getType() {
        return type;
    }

    public String getStatus() {
        return status;
    }
    public String getPhotoData() {return photoData;}

    public List<IncidentsDetail> getDetailsList() {
        return detailsList;
    }
}
