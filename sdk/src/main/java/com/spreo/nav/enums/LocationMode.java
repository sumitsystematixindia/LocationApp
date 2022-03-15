package com.spreo.nav.enums;

public enum LocationMode {

    INDOOR_MODE(0), OUTDOOR_MODE(1), GOOGLE_LOCATION_MODE(2);

    private final int value;

    private LocationMode(final int newValue) {
        value = newValue;
    }

    public int getValue() {
        return value;
    }

}
