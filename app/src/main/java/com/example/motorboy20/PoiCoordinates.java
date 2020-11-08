package com.example.motorboy20;

public class PoiCoordinates {
    private String latitude;
    private String longitude;
    private String poi_Type;

    PoiCoordinates() {

    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public void setPoi_Type(String poi_Type) {
      this.poi_Type=poi_Type;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public String getPoi_Type() {
        return poi_Type;
    }
}
