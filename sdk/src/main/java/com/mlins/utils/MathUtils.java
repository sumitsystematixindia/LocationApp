package com.mlins.utils;

import android.graphics.PointF;

import com.google.android.gms.maps.model.LatLng;
import com.mlins.utils.gis.GisLine;
import com.mlins.utils.gis.Location;

import java.util.List;

import static java.lang.Math.asin;
import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static java.lang.Math.sqrt;
import static java.lang.Math.toRadians;

public class MathUtils {

    static public float[] normalizeVector(float[] v, float[] min,
                                          float[] max) {
        float result[] = new float[v.length];

        for (int i = 0; i < v.length; i++) {
            float diff = (max[i] - min[i]);
            if (v[i] == 0) {
//				if (diff != 0) {
//					result[i] = (float) ((AsociativeMemoryLocator.getInstance().getZeroValue() - min[i]) / diff);
//				} else {
//					result[i] = 0;
//				}
                result[i] = 0;
                continue;
            }

            if (diff != 0) {
                result[i] = (v[i] - min[i]) / diff;
            } else {
                result[i] = 0;
            }
        }
        return result;

    }

    public static float WifiThreshold(float v, int index) {
        float result = 1.0f;
        float closewifi = PropertyHolder.getInstance().getCloseDeviceThreshold();
        if (v < closewifi || v == 0) {
            result = PropertyHolder.getInstance().getCloseDeviceWeight();
        }
        return result;
    }

    public static PointF findClosestPointOnSegment(PointF p, GisLine l) {
        double x1 = l.getPoint1().getX();
        double y1 = l.getPoint1().getY();
        double x2 = l.getPoint2().getX();
        double y2 = l.getPoint2().getY();
        double px = p.x;
        double py = p.y;
        double xDelta = x2 - x1;
        double yDelta = y2 - y1;

        if ((xDelta == 0) && (yDelta == 0)) {
            //throw new IllegalArgumentException(
            //		"Segment start equals segment end");
            return new PointF((float) x1, (float) y1);
        }

        double u = ((px - x1) * xDelta + (py - y1) * yDelta)
                / (xDelta * xDelta + yDelta * yDelta);

        final PointF closestPoint;
        if (u < 0) {
            closestPoint = new PointF((float) x1, (float) y1);
        } else if (u > 1) {
            closestPoint = new PointF((float) x2, (float) y2);
        } else {
            closestPoint = new PointF((float) (x1 + u * xDelta),
                    (float) (y1 + u * yDelta));
        }

        return closestPoint;
    }

    public static LatLng findClosestPointOnSegment(LatLng p, GisLine l) {
        double lon1 = l.getPoint1().getX();
        double lat1 = l.getPoint1().getY();
        double lon2 = l.getPoint2().getX();
        double lat2 = l.getPoint2().getY();
        double plat = p.latitude;
        double plon = p.longitude;
        double lonDelta = lon2 - lon1;
        double latDelta = lat2 - lat1;

        if ((lonDelta == 0) && (latDelta == 0)) {
            //throw new IllegalArgumentException(
            //		"Segment start equals segment end");
            return new LatLng(lat1, lon1);
        }

        double u = ((plon - lon1) * lonDelta + (plat - lat1) * latDelta)
                / (lonDelta * lonDelta + latDelta * latDelta);

        final LatLng closestPoint;
        if (u < 0) {
            closestPoint = new LatLng(lat1, lon1);
        } else if (u > 1) {
            closestPoint = new LatLng(lat2, lon2);
        } else {
            closestPoint = new LatLng((lat1 + u * latDelta), (lon1 + u * lonDelta));
        }

        return closestPoint;
    }

//	public static LatLng findClosestPointOnSegment(LatLng p, GisLine l) {
//		double y1 = l.getPoint1().getX();
//		double x1 = l.getPoint1().getY();
//		double y2 = l.getPoint2().getX();
//		double x2 = l.getPoint2().getY();
//		double py = p.latitude;
//		double px = p.longitude;
//		double xDelta = x2 - x1;
//		double yDelta = y2 - y1;
//
//		if ((xDelta == 0) && (yDelta == 0)) {
//			//throw new IllegalArgumentException(
//			//		"Segment start equals segment end");
//			return new LatLng(x1,y1);
//		}
//
//		double u = ((px - x1) * xDelta + (py - y1) * yDelta)
//				/ (xDelta * xDelta + yDelta * yDelta);
//
//		final LatLng closestPoint;
//		if (u < 0) {
//			closestPoint = new LatLng(x1, y1);
//		} else if (u > 1) {
//			closestPoint = new LatLng(x2, y2);
//		} else {
//			closestPoint = new LatLng((int)Math.round(x1 + u * xDelta),
//					(int)Math.round(y1 + u * yDelta));
//		}
//
//		return closestPoint;
//	}

    public static double distance(PointF p, PointF p1) {

        return sqrt((p.x - p1.x) * (p.x - p1.x) + (p.y - p1.y)
                * (p.y - p1.y));
    }

    public static double distance(LatLng p, LatLng p1) {
        android.location.Location locationA = new android.location.Location("point A");
        locationA.setLatitude(p.latitude);
        locationA.setLongitude(p.longitude);
        android.location.Location locationB = new android.location.Location("point B");
        locationB.setLatitude(p1.latitude);
        locationB.setLongitude(p1.longitude);
        return locationA.distanceTo(locationB);

//		return Math.sqrt((p.latitude - p1.latitude) * (p.latitude - p1.latitude) + (p.longitude - p1.longitude)
//				* (p.longitude - p1.longitude));
    }

    public static float getLIneAngle(Location l1, Location l2) {
        float p1x = (float) l1.getX();
        float p1y = (float) l1.getY();
        float p2x = (float) l2.getX();
        float p2y = (float) l2.getY();
        double dx = p1x - p2x;
        // Minus to correct for coord re-mapping
        double dy = -(p1y - p2y);

        double inRads = Math.atan2(dy, dx);

        // We need to map to coord system when 0 degree is at 3 O'clock, 270 at
        // 12 O'clock
        if (inRads < 0)
            inRads = Math.abs(inRads);
        else
            inRads = 2 * Math.PI - inRads;

        return (float) (Math.toDegrees(inRads) - 90);
    }

    public static float getLIneAngle(PointF p1, PointF p2) {
        float p1x = (float) p1.x;
        float p1y = (float) p1.y;
        float p2x = (float) p2.x;
        float p2y = (float) p2.y;
        double dx = p1x - p2x;
        // Minus to correct for coord re-mapping
        double dy = -(p1y - p2y);

        double inRads = Math.atan2(dy, dx);

        // We need to map to coord system when 0 degree is at 3 O'clock, 270 at
        // 12 O'clock
        if (inRads < 0)
            inRads = Math.abs(inRads);
        else
            inRads = 2 * Math.PI - inRads;

        return (float) (Math.toDegrees(inRads) - 90);
    }


    public static double angleFromCoordinate(double lat1, double long1, double lat2,
                                             double long2) {

        double lat1Rad = toRadians(lat1);
        double lat2Rad = toRadians(lat2);
        double deltaLonRad = toRadians(long2 - long1);

        double y = sin(deltaLonRad) * cos(lat2Rad);
        double x = cos(lat1Rad) * sin(lat2Rad) - sin(lat1Rad) * cos(lat2Rad)
                * cos(deltaLonRad);
        return radToBearing(Math.atan2(y, x));
    }

    public static double radToBearing(double rad) {
        return (Math.toDegrees(rad) + 360) % 360;
    }

    public static double computeHeading(LatLng from, LatLng to) {
        // http://williams.best.vwh.net/avform.htm#Crs
        double fromLat = toRadians(from.latitude);
        double fromLng = toRadians(from.longitude);
        double toLat = toRadians(to.latitude);
        double toLng = toRadians(to.longitude);
        double dLng = toLng - fromLng;
        double heading = Math.atan2(
                sin(dLng) * cos(toLat),
                cos(fromLat) * sin(toLat) - sin(fromLat) * cos(toLat) * cos(dLng));
        return wrap(Math.toDegrees(heading), -180, 180);
    }

    private static double wrap(double n, double min, double max) {
        return (n >= min && n < max) ? n : (mod(n - min, max - min) + min);
    }

    private static double mod(double x, double m) {
        return ((x % m) + m) % m;
    }

    public static LatLng GetMidPoint(LatLng l1, LatLng l2) {
        LatLng result = null;

        double dLon = toRadians(l2.longitude - l1.longitude);

        //convert to radians
        double lat1 = toRadians(l1.latitude);
        double lat2 = toRadians(l2.latitude);
        double lon1 = toRadians(l1.longitude);

        double Bx = cos(lat2) * cos(dLon);
        double By = cos(lat2) * sin(dLon);
        double lat = Math.atan2(sin(lat1) + sin(lat2), sqrt((cos(lat1) + Bx) * (cos(lat1) + Bx) + By * By));
        double lon = lon1 + Math.atan2(By, cos(lat1) + Bx);

        result = new LatLng(Math.toDegrees(lat), Math.toDegrees(lon));
        return result;
        //print out in degrees
//	    System.out.println(Math.toDegrees(lat3) + " " + Math.toDegrees(lon3));
    }

    public static double getNavigationWeight(PointF a, PointF b){
        double xDistance = a.x-b.x;
        double yDistance = a.y-b.y;
        return xDistance*xDistance + yDistance*yDistance;
    }


    public static double getNavigationWeight(double x1, double y1, double x2, double y2){
        double xDistance = x1-x2;
        double yDistance = y1-y2;
        return xDistance*xDistance + yDistance*yDistance;
    }

    public static float distance(double lat1, double long1, double lat2, double long2){
        float[] resultsCache = new float[1];
        android.location.Location.distanceBetween(
                lat1,
                long1,
                lat2,
                long2,
                resultsCache);
        return resultsCache[0];
    }

    public static double metersToFeet(double meters){
        return meters * 3.28084;
    }

    public static double feetToMeters(double feet){
        return feet / 3.28084;
    }

    /**
     * Returns the length of the given path, in meters, on Earth.
     */
    public static double computeLength(List<LatLng> path) {
        double EARTH_RADIUS = 6371009;
        if (path.size() < 2) {
            return 0;
        }
        double length = 0;
        LatLng prev = path.get(0);
        double prevLat = toRadians(prev.latitude);
        double prevLng = toRadians(prev.longitude);
        for (LatLng point : path) {
            double lat = toRadians(point.latitude);
            double lng = toRadians(point.longitude);
            length += distanceRadians(prevLat, prevLng, lat, lng);
            prevLat = lat;
            prevLng = lng;
        }
        return length * EARTH_RADIUS;
    }

    /**
     * Returns distance on the unit sphere; the arguments are in radians.
     */
    private static double distanceRadians(double lat1, double lng1, double lat2, double lng2) {
        return arcHav(havDistance(lat1, lat2, lng1 - lng2));
    }

    /**
     * Computes inverse haversine. Has good numerical stability around 0.
     * arcHav(x) == acos(1 - 2 * x) == 2 * asin(sqrt(x)).
     * The argument must be in [0, 1], and the result is positive.
     */
    static double arcHav(double x) {
        return 2 * asin(sqrt(x));
    }

    /**
     * Returns hav() of distance from (lat1, lng1) to (lat2, lng2) on the unit sphere.
     */
    static double havDistance(double lat1, double lat2, double dLng) {
        return hav(lat1 - lat2) + hav(dLng) * cos(lat1) * cos(lat2);
    }

    /**
     * Returns haversine(angle-in-radians).
     * hav(x) == (1 - cos(x)) / 2 == sin(x / 2)^2.
     */
    static double hav(double x) {
        double sinHalf = sin(x * 0.5);
        return sinHalf * sinHalf;
    }
}
