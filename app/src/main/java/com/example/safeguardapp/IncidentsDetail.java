package com.example.safeguardapp;

import java.io.Serializable;

public class IncidentsDetail implements Serializable {
    private String details;
    private String updateTime;

    public IncidentsDetail(String details, String updateTime) {
        this.details = details;
        this.updateTime = updateTime;
    }

    public String getDetails() {
        return details;
    }

    public String getUpdateTime() {
        return updateTime;
    }
}
