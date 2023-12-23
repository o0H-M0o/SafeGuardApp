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
    private List<IncidentsDetail> detailsList;

    // Empty constructor for Firebase
    public Incidents() {
    }

    public Incidents(String incidentId, String date, String time, String status, List<IncidentsDetail> detailsList) {
        this.incidentId = incidentId;
        this.date = date;
        this.time = time;
        this.status = status;
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

    public String getStatus() {
        return status;
    }

    public List<IncidentsDetail> getDetailsList() {
        return detailsList;
    }
}
