package com.example.safeguardapp;

public class DataClass {

    private String dataTitle;
    private String dataDesc;

    private String dataImage;
    private String key;
    private String path;

    public DataClass(String dataTitle, String dataDesc, String dataImage, String path) {
        this.dataTitle = dataTitle;
        this.dataDesc = dataDesc;
        this.dataImage = dataImage;
        this.path = path;
    }


    public DataClass() {

    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getDataTitle() {
        return dataTitle;
    }

    public String getDataDesc() {
        return dataDesc;
    }

    public String getDataImage() {
        return dataImage;
    }
    public String getPath(){return path;}
}


