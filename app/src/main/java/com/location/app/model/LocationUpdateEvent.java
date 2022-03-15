package com.location.app.model;

public class LocationUpdateEvent {
    private LocationDTO location;

    public LocationUpdateEvent(LocationDTO locationUpdate) {
        this.location = locationUpdate;
    }

    public LocationDTO getLocation() {
        return location;
    }

    public void setLocation(LocationDTO location) {
        this.location = location;
    }
}