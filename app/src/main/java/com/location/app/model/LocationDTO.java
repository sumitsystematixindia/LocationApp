package com.location.app.model;

public class LocationDTO {
    public double latitude;
    public double longitude;
    public double speed;

    @Override
    public String toString() {
        return "lat:"+latitude +"- lng:"+longitude + "- speed:"+speed;
    }
}