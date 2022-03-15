package com.mlins.locator;

import android.graphics.PointF;

import java.util.ArrayList;
import java.util.List;

public class WIFILevelData {
    int level;
    List<PointF> points = new ArrayList<PointF>();

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public void add(PointF pt) {
        points.add(pt);
    }

    public List<PointF> getPoints() {
        return points;
    }

}
