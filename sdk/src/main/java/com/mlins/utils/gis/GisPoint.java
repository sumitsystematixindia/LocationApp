package com.mlins.utils.gis;

import android.graphics.PointF;

import com.mlins.aStar.aStarMath;
import com.mlins.utils.MathUtils;

public class GisPoint {
    private double X;
    private double Y;
    private double Z;

    public GisPoint(GisPoint p) {
        X = p.X;
        Y = p.Y;
        Z = p.Z;
    }


    public GisPoint() {

    }

    public GisPoint(double x, double y, double z) {
        setX(x);
        setY(y);
        setZ(z);
    }

    public GisPoint(Location loc) {
        X = loc.getX();
        Y = loc.getY();
        Z = loc.getZ();
    }
//	public GisPoint(PointF p) {
//		this((double)p.x, (double)p.y);
//	}

    public double getX() {
        return X;
    }

    public void setX(double pointX) {
        this.X = pointX;
    }

    public double getY() {
        return Y;
    }

    public void setY(double pointY) {
        this.Y = pointY;
    }

    public double getZ() {
        return Z;
    }

    public void setZ(double z) {
        Z = z;
    }

    public PointF asPointF() {
        return new PointF((float) getX(), (float) getY());
    }

    public boolean sameAs(GisPoint p){
        return aStarMath.findDistance(X, Y, p.X, p.Y) == 0;
    }

    public double distanceTo(GisPoint p){
        return aStarMath.findDistance(X, Y, p.X, p.Y);
    }

    public double distanceToLocation(GisPoint p){
        return MathUtils.distance(Y, X, p.Y, p.X);
    }

    @Override
    public String toString() {
        return String.format("[%.2f, %.2f, %.2f]", X, Y, Z);
    }
}
