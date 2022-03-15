package com.mlins.locator;

import android.graphics.PointF;
import android.location.Location;

public class GpsTiePoint {
    public final Location gps;
    public final PointF local;
    public final double floor;

    public GpsTiePoint(Location gps, PointF local, double floor) {
        super();
        this.gps = gps;
        this.local = local;
        this.floor = floor;
    }

}
