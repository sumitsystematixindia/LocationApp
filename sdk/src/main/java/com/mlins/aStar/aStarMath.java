//AddCatch
package com.mlins.aStar;

import android.graphics.PointF;

import com.mlins.utils.MathUtils;
import com.mlins.utils.gis.GisLine;
import com.mlins.utils.gis.GisPoint;
import com.mlins.utils.logging.Log;

import java.util.ArrayList;
import java.util.List;

public class aStarMath {
    private static final String TAG = aStarMath.class.getName();

    public static aStarPoint findPoint(GisPoint p1) {
        Log.getInstance().debug(TAG, "Enter, GisPoint findPoint(GisPoint p1) ");
        aStarPoint point = null;
        aStarSegment s = findCloseSegment(p1);
        if (s == null) {
            return null;
        }
        double d1 = findDistance(p1, s.getLine().getPoint1());
        double d2 = findDistance(p1, s.getLine().getPoint2());
        try {
            if (d1 < d2) {
                point = s.starPoint1;
            } else {
                point = s.starPoint2;
            }
        } catch (Throwable t) {
            Log.getInstance().error(TAG, t.getMessage(), t);
        }
        Log.getInstance().debug(TAG, "Exit, GisPoint findPoint(GisPoint p1) ");
        return point;
    }

    public static aStarSegment findCloseSegment(GisPoint p1) {
        Log.getInstance().debug(TAG, "Enter, GisSegment findCloseSegment(GisPoint p1) ");
        List<aStarSegment> segments = aStarData.getInstance().segmentTable;
        GisPoint point = null;
        aStarSegment segment = null;
        double distance = 0;
        double mindistance = 1000000;
        try {
            for (aStarSegment s : segments) {
                point = findClosePointOnSegment(p1, s);
                distance = findDistance(p1, point);
                if (distance < mindistance) {
                    segment = s;
                    mindistance = distance;
                }
            }
        } catch (Throwable t) {
            Log.getInstance().error(TAG, t.getMessage(), t);
        }
        Log.getInstance().debug(TAG, "Exit, GisSegment findCloseSegment(GisPoint p1) ");
        return segment;
    }

    public static GisSegment findCloseSegment(GisPoint p1, List<GisSegment> segments) {
        Log.getInstance().debug(TAG, "Enter, GisSegment findCloseSegment(GisPoint p1) ");
        GisPoint point = null;
        GisSegment segment = null;
        double distance = 0;
        double mindistance = 1000000;
        try {
            for (GisSegment s : segments) {
                point = findClosePointOnSegment(p1, s);
                distance = findDistance(p1, point);
                if (distance < mindistance) {
                    segment = s;
                    mindistance = distance;
                }
            }
        } catch (Throwable t) {
            Log.getInstance().error(TAG, t.getMessage(), t);
        }
        Log.getInstance().debug(TAG, "Exit, GisSegment findCloseSegment(GisPoint p1) ");
        return segment;
    }

    public static double findDistance(GisPoint p1, GisPoint p2) {
        double p1x = p1.getX();
        double p1y = p1.getY();
        double p2x = p2.getX();
        double p2y = p2.getY();

        double result = Math.sqrt((p1x - p2x) * (p1x - p2x) + (p1y - p2y)
                * (p1y - p2y));

        return result;
    }

    public static double findDistance(double p1x, double p1y, double p2x, double p2y) {
        if(p1x == p2x && p1y == p2y)
            return 0;

        double xDistance = p1x-p2x;
        double yDistance = p1y-p2y;


        return Math.sqrt(
                xDistance*xDistance + yDistance*yDistance
        );
    }

    public static GisPoint findClosePointOnSegment(GisPoint p, GisSegment s) {
        double x1 = s.getLine().getPoint1().getX();
        double y1 = s.getLine().getPoint1().getY();
        double x2 = s.getLine().getPoint2().getX();
        double y2 = s.getLine().getPoint2().getY();
        double px = p.getX();
        double py = p.getY();
        double xDelta = x2 - x1;
        double yDelta = y2 - y1;

        if ((xDelta == 0) && (yDelta == 0)) {
            return s.getLine().getPoint1();
        }

        double u = ((px - x1) * xDelta + (py - y1) * yDelta)
                / (xDelta * xDelta + yDelta * yDelta);

        final GisPoint closestPoint;
        if (u < 0) {
            closestPoint = new GisPoint(x1, y1, s.getLine().getZ());
        } else if (u > 1) {
            closestPoint = new GisPoint(x2, y2, s.getLine().getZ());
        } else {
            closestPoint = new GisPoint(x1 + u * xDelta, y1 + u * yDelta, s
                    .getLine().getZ());
        }

        return closestPoint;
    }


    public static GisPoint findClosePointOnLine(GisPoint p, GisLine s) {
        double x1 = s.getPoint1().getX();
        double y1 = s.getPoint1().getY();
        double x2 = s.getPoint2().getX();
        double y2 = s.getPoint2().getY();
        double px = p.getX();
        double py = p.getY();
        double xDelta = x2 - x1;
        double yDelta = y2 - y1;

        if ((xDelta == 0) && (yDelta == 0)) {
            return s.getPoint1();
        }

        double u = ((px - x1) * xDelta + (py - y1) * yDelta)
                / (xDelta * xDelta + yDelta * yDelta);

        final GisPoint closestPoint;
        if (u < 0) {
            closestPoint = new GisPoint(x1, y1, s.getZ());
        } else if (u > 1) {
            closestPoint = new GisPoint(x2, y2, s.getZ());
        } else {
            closestPoint = new GisPoint(x1 + u * xDelta, y1 + u * yDelta, s.getZ());
        }

        return closestPoint;
    }

    public static boolean isNeighbors(GisSegment s1, GisSegment s2) {
        GisPoint s1p1 = s1.getLine().getPoint1();
        GisPoint s1p2 = s1.getLine().getPoint2();
        GisPoint s2p1 = s2.getLine().getPoint1();
        GisPoint s2p2 = s2.getLine().getPoint2();
        if (s1 != s2) {
            // if ((s1p1 == s2p1) || (s1p1 == s2p2) || (s1p2 == s2p1) || (s1p2
            // == s2p2)) {
            // return true;
            // }
            if (findDistance(s1p1, s2p1) == 0 || findDistance(s1p1, s2p2) == 0
                    || findDistance(s1p2, s2p1) == 0
                    || findDistance(s1p2, s2p2) == 0) {
                return true;
            }
        }
        return false;
    }

    public static PointF getIntersectionPoint(GisSegment s1, GisSegment s2) {
        PointF result = null;
        GisPoint s1p1 = s1.getLine().getPoint1();
        GisPoint s1p2 = s1.getLine().getPoint2();
        GisPoint s2p1 = s2.getLine().getPoint1();
        GisPoint s2p2 = s2.getLine().getPoint2();
        if (s1 != s2) {
            if (findDistance(s1p1, s2p1) == 0 || findDistance(s1p1, s2p2) == 0) {
                result = s1p1.asPointF();
            } else if (findDistance(s1p2, s2p1) == 0 || findDistance(s1p2, s2p2) == 0) {
                result = s1p2.asPointF();
            }
        }
        return result;
    }

    public static double gePathweight(List<GisSegment> path) {
        double result = 0;
        for (GisSegment s : path) {
            result += s.getWeight();
        }
        return result;
    }

    public static double calcH(GisSegment s, aStarPoint epoint) {
        double result = 0;
        GisPoint p2 = findClosePointOnSegment(epoint, s);
        result = findDistance(epoint, p2);
        return result;
    }

    public static List<PointF> divideLine(GisSegment s, int pixels) {
        List<PointF> result = new ArrayList<PointF>();
        double w = s.getWeight();
        double x1 = s.getLine().getPoint1().getX();
        double y1 = s.getLine().getPoint1().getY();
        double x2 = s.getLine().getPoint2().getX();
        double y2 = s.getLine().getPoint2().getY();
        int count = (int) (w / pixels);
        PointF p1 = new PointF((float) x1, (float) y1);
        PointF p2 = new PointF((float) x2, (float) y2);
        result.add(p1);

        for (int i = 1; i < count; i++) {

            PointF p3 = subPoint(s.getLine().point1, s.getLine().point2, i,
                    count - 1);

            result.add(p3);
        }
//		result.add(p2);
        return result;

    }

    public static List<PointF> divideLine(PointF lp1, PointF lp2, int pixels) {
        List<PointF> result = new ArrayList<PointF>();
        double w = MathUtils.distance(lp1, lp2);
        double x1 = lp1.x;
        double y1 = lp1.y;
        double x2 = lp2.x;
        double y2 = lp2.y;
        int count = (int) (w / pixels);
        PointF p1 = new PointF((float) x1, (float) y1);
        PointF p2 = new PointF((float) x2, (float) y2);
        // result.add(p1);

        for (int i = 1; i < count; i++) {

            PointF p3 = subPoint(lp1, lp2, i,
                    count - 1);

            result.add(p3);
        }
//		result.add(p2);
        return result;

    }

    public static PointF subPoint(PointF startPoint, PointF endPoint,
                                  int segment, int totalSegments) {

        float division = (float) ((float) totalSegments / (float) segment);

        PointF divPoint = new PointF();

        float midX = (float) (startPoint.x + ((endPoint.x - startPoint
                .x) / division));
        float midY = (float) (startPoint.y + ((endPoint.y - startPoint
                .y) / division));

        divPoint.x = midX;
        divPoint.y = midY;

        return divPoint;
    }

    public static PointF subPoint(GisPoint startPoint, GisPoint endPoint,
                                  int segment, int totalSegments) {

        float division = (float) ((float) totalSegments / (float) segment);

        PointF divPoint = new PointF();

        float midX = (float) (startPoint.getX() + ((endPoint.getX() - startPoint
                .getX()) / division));
        float midY = (float) (startPoint.getY() + ((endPoint.getY() - startPoint
                .getY()) / division));

        divPoint.x = midX;
        divPoint.y = midY;

        return divPoint;
    }

    public static float getAngleToNext(float sangle, float pangle) {
        float segmentangle = sangle;
        float nexttangle = pangle;
        float result = nexttangle - segmentangle;
        if (result <= -180) {
            result += 360;
        } else if (result >= 180) {
            result -= 360;
        }
        return result;
    }

    public static float getSegmentAngle(GisSegment line) {
        float p1x = (float) line.getLine().getPoint1().getX();
        float p1y = (float) line.getLine().getPoint1().getY();
        float p2x = (float) line.getLine().getPoint2().getX();
        float p2y = (float) line.getLine().getPoint2().getY();
        double dx = p1x - p2x;
        // Minus to correct for coord re-mapping
        double dy = -(p1y - p2y);

        double inRads = Math.atan2(dy, dx);

        // We need to map to coord system when 0 degree is at 3 O'clock, 270 at 12 O'clock
        if (inRads < 0)
            inRads = Math.abs(inRads);
        else
            inRads = 2 * Math.PI - inRads;

        return (float) (Math.toDegrees(inRads) - 90);
    }

//	public static float getSegmentAngle(GisSegment line) {
//		float p1x = (float) line.getLine().getPoint1().getX();
//		float p1y = (float) line.getLine().getPoint1().getY();
//		float p2x = (float) line.getLine().getPoint2().getX();
//		float p2y = (float) line.getLine().getPoint2().getY();
//		float angle = (float) Math.toDegrees(Math.atan2(p1x - p2x, p1y - p2y));
//		if (angle < 0)
//			angle += 360;
//
//		if ((p1x > p2x && (angle < 135 && angle > 45))
//				|| (p2x > p1x && (angle < 315 && angle > 215))) {
//			angle -= 180.0;
//
//		} else if ((((angle < 45 && angle > 0) || (angle < 360 && angle > 315)) && p1y < p2y)) {
//			angle -= 180.0;
//		}
//
//		return angle;
//
//	}

}
