package com.mlins.locator;

import android.graphics.PointF;

import com.mlins.wireless.WlBlip;

import java.util.ArrayList;
import java.util.List;

public class MatrixPoint {
    private List<WlBlip> blips = new ArrayList<WlBlip>();
    private PointF Point = new PointF();
    private Integer z = null;

    public MatrixPoint() {

    }

    public List<WlBlip> getBlips() {
        return blips;
    }

    public void setBlips(List<WlBlip> blips) {
        this.blips = blips;
    }

    public PointF getPoint() {
        return Point;
    }

    public void setPoint(PointF point) {
        Point = point;
    }

    public Integer getZ() {
        return z;
    }

    public void setZ(Integer z) {
        this.z = z;
    }
}
