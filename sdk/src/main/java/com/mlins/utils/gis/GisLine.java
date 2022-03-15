package com.mlins.utils.gis;

import android.graphics.PointF;


public class GisLine {
    public GisPoint point1;
    public GisPoint point2;
    private double Z;
    private boolean participateInNavigation = true;
    private boolean isOnTheFlyState = false;

    public GisLine(GisPoint p1, GisPoint p2, double z) {
        point1 = p1;
        point2 = p2;
        Z = z;
    }

    public GisLine(GisPoint p1, GisPoint p2, double z, boolean isOnTheFlyState) {
        point1 = p1;
        point2 = p2;
        Z = z;
        this.isOnTheFlyState = isOnTheFlyState;
    }

    public GisLine(PointF p1, PointF p2, double z, boolean isOnTheFlyState) {
        point1 = new GisPoint();
        point2 = new GisPoint();
        Z = z;
        this.isOnTheFlyState = isOnTheFlyState;
        if (p1 != null && p2 != null) {
            point1.setX(p1.x);
            point1.setY(p1.y);
            point1.setZ(Z);
            point2.setX(p2.x);
            point2.setY(p2.y);
            point2.setZ(Z);
        }

    }

    public GisLine() {
        point1 = new GisPoint();
        point2 = new GisPoint();

    }

    public void setLine(GisPoint p1, GisPoint p2, double z) {
        point1 = new GisPoint();
        point2 = new GisPoint();
        Z = z;
        if (p1 != null && p2 != null) {
            point1.setX(p1.getX());
            point1.setY(p1.getY());
            point1.setZ(Z);
            point2.setX(p2.getX());
            point2.setY(p2.getY());
            point2.setZ(Z);
        }
    }

    public boolean isOnTheFlyState() {
        return isOnTheFlyState;
    }

    public GisPoint getPoint1() {
        return point1;
    }

    public void setPoint1(GisPoint point1) {
        this.point1 = point1;
    }

    public GisPoint getPoint2() {
        return point2;
    }

    public void setPoint2(GisPoint point2) {
        this.point2 = point2;
    }

    public void loadLine(String line) {
        String[] fields = line.split("\t");
        point1.setX(Double.parseDouble(fields[1]));
        point1.setY(Double.parseDouble(fields[2]));
        point2.setX(Double.parseDouble(fields[3]));
        point2.setY(Double.parseDouble(fields[4]));
        setZ(Integer.parseInt(fields[5]));
        point1.setZ(Integer.parseInt(fields[5]));
        point1.setZ(Integer.parseInt(fields[5]));
        if (fields.length >= 7 && fields[6] != null) {
            setParticipateInNavigation(Boolean.parseBoolean(fields[6]));
        }
    }

    public double getZ() {
        return Z;
    }

    public void setZ(double z) {
        Z = z;
    }

    public float getAngle() {
        double dx = point1.getX() - point2.getX();
        double dy = point1.getY() - point2.getY();
        float angle = (float) Math.toDegrees(Math.atan2(dy, dx));
        return (angle + 360) % 360; // range 0 - 360.
    }

    public boolean isParticipateInNavigation() {
        return participateInNavigation;
    }

    public void setParticipateInNavigation(boolean participateInNavigation) {
        this.participateInNavigation = participateInNavigation;
    }

    public double getLength(){
        return point1.distanceTo(point2);
    }

    public double getDistanceBetweenPoints(){
        return point1.distanceToLocation(point2);
    }

    @Override
    public String toString() {
        return point1.toString() + " - " + point2.toString();
    }
}
