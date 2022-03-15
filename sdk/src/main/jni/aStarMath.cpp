#include "aStarMath.h"
#include "aStarData.h"

const string aStarMath::TAG = "aStarMath";

void aStarMath::findPoint(const GisPoint &p1, GisPoint &retPoint) {
    Log::getInstance().debug(TAG, "Enter, GisPoint findPoint(GisPoint p1) ");
    GisSegment *ss = findCloseSegment(p1);
    if (ss == NULL) {
        retPoint = GisPoint::Null;
        return;
    }
    GisSegment &s = *ss;
    double d1 = findDistance(p1, s.getLine().getPoint1());
    double d2 = findDistance(p1, s.getLine().getPoint2());
    try {
        if (d1 < d2) {
            retPoint = s.getLine().getPoint1();
        } else {
            retPoint = s.getLine().getPoint2();
        }
    } catch (exception e) {
        Log::getInstance().error(TAG, e.what());
    }
    Log::getInstance().debug(TAG, "Exit, GisPoint findPoint(GisPoint p1) ");

}

GisSegment *aStarMath::findCloseSegment(const GisPoint &p1) {
    Log::getInstance().debug(TAG, "Enter, GisSegment findCloseSegment(GisPoint p1) ");
    list<GisSegment *> &segments = aStarData::getInstance().segmentTable;
    GisPoint point;
    GisSegment *segment = NULL;
    double distance = 0;
    double mindistance = 1000000;
    try {
        for (list<GisSegment *>::iterator s = segments.begin(); s != segments.end(); s++) {
            findClosePointOnSegment(p1, *(*s), point);
            distance = findDistance(p1, point);
            if (distance < mindistance) {
                segment = *s;
                mindistance = distance;
            }
        }
    } catch (exception t) {
        Log::getInstance().error(TAG, t.what());
    }
    Log::getInstance().debug(TAG, "Exit, GisSegment findCloseSegment(GisPoint p1) ");
    return segment;
}

double aStarMath::findDistance(const GisPoint &p1, const GisPoint &p2) {
    double result = 0;
    double p1x = p1.getX();
    double p1y = p1.getY();
    double p2x = p2.getX();
    double p2y = p2.getY();

    result = sqrt((p1x - p2x) * (p1x - p2x) + (p1y - p2y)
                                              * (p1y - p2y));

    return result;
}

double aStarMath::findLatLonDistance(const GisPoint &p1, const GisPoint &p2) {

    double lat1 = p1.getY();
    double lon1 = p1.getX();

    double lat2 = p2.getY();
    double lon2 = p2.getX();

    double R = 6371000; // metres
    double phi1 = toRadians(lat1);
    double phi2 = toRadians(lat2);
    double delta_phi = toRadians(lat2 - lat1);
    double delta_lamda = toRadians(lon2 - lon1);

    double a = sin(delta_phi / 2) * sin(delta_phi / 2) +
               cos(phi1) * cos(phi2) * sin(delta_lamda / 2) * sin(delta_lamda / 2);
    double c = 2 * atan2(sqrt(a), sqrt(1 - a));

    double d = R * c;
    return d;


}

double aStarMath::toRadians(double degrees) {
    double radians = degrees * M_PI / 180;
    return radians;
}

void aStarMath::findClosePointOnSegment(const GisPoint &p, const GisSegment &s,
                                        GisPoint &closestPoint) {
    double x1 = s.getLine().getPoint1().getX();
    double y1 = s.getLine().getPoint1().getY();
    double x2 = s.getLine().getPoint2().getX();
    double y2 = s.getLine().getPoint2().getY();
    double px = p.getX();
    double py = p.getY();
    double xDelta = x2 - x1;
    double yDelta = y2 - y1;

    if ((xDelta == 0) && (yDelta == 0)) {
        closestPoint = s.getLine().getPoint1();
        return;
    }

    double u = ((px - x1) * xDelta + (py - y1) * yDelta)
               / (xDelta * xDelta + yDelta * yDelta);

    if (u < 0) {
        closestPoint = GisPoint(x1, y1, s.getLine().getZ());
    } else if (u > 1) {
        closestPoint = GisPoint(x2, y2, s.getLine().getZ());
    } else {
        closestPoint = GisPoint(x1 + u * xDelta, y1 + u * yDelta, s
                .getLine().getZ());
    }
}


void aStarMath::findClosePointOnLine(const GisPoint &p, const GisLine &s, GisPoint &closestPoint) {
    double x1 = s.getPoint1().getX();
    double y1 = s.getPoint1().getY();
    double x2 = s.getPoint2().getX();
    double y2 = s.getPoint2().getY();
    double px = p.getX();
    double py = p.getY();
    double xDelta = x2 - x1;
    double yDelta = y2 - y1;

    if ((xDelta == 0) && (yDelta == 0)) {
        closestPoint = s.getPoint1();
        return;
    }

    double u = ((px - x1) * xDelta + (py - y1) * yDelta)
               / (xDelta * xDelta + yDelta * yDelta);

    if (u < 0) {
        closestPoint = GisPoint(x1, y1, s.getZ());
    } else if (u > 1) {
        closestPoint = GisPoint(x2, y2, s.getZ());
    } else {
        closestPoint = GisPoint(x1 + u * xDelta, y1 + u * yDelta, s.getZ());
    }
}

bool aStarMath::isNeighbors(const GisSegment &s1, const GisSegment &s2) {
    const GisPoint &s1p1 = s1.getLine().getPoint1();
    const GisPoint &s1p2 = s1.getLine().getPoint2();
    const GisPoint &s2p1 = s2.getLine().getPoint1();
    const GisPoint &s2p2 = s2.getLine().getPoint2();
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

double aStarMath::gePathweight(const list<GisSegment *> &path) {
    double result = 0;
    for (list<GisSegment *>::const_iterator s = path.begin(); s != path.end(); s++) {
        result += (*s)->getWeight();
    }
    return result;
}

double aStarMath::calcH(const GisSegment &s, const aStarPoint &epoint) {
    double result = 0.0;
    GisPoint p2;
    findClosePointOnSegment(epoint.getPoint(), s, p2);
    result = findDistance(epoint.getPoint(), p2);
    return result;
}

void aStarMath::divideLine(const GisSegment &s, int pixels, list<PointF> &result) {
    double w = s.getWeight();
    double x1 = s.getLine().getPoint1().getX();
    double y1 = s.getLine().getPoint1().getY();
    double x2 = s.getLine().getPoint2().getX();
    double y2 = s.getLine().getPoint2().getY();
    int count = (int) (w / pixels);
    PointF p1((float) x1, (float) y1);
    PointF p2((float) x2, (float) y2);
    // result.add(p1);

    for (int i = 1; i < count; i++) {

        PointF p3 = subPoint(s.getLine().point1, s.getLine().point2, i,
                             count - 1);

        result.push_back(p3);
    }

}

PointF aStarMath::subPoint(const GisPoint &startPoint, const GisPoint &endPoint,
                           int segment, int totalSegments) {

    float division = (float) ((float) totalSegments / (float) segment);

    PointF divPoint;

    float midX = (float) (startPoint.getX() + ((endPoint.getX() - startPoint
            .getX()) / division));
    float midY = (float) (startPoint.getY() + ((endPoint.getY() - startPoint
            .getY()) / division));

    divPoint.x = midX;
    divPoint.y = midY;

    return divPoint;
}

float aStarMath::getAngleToNext(float sangle, float pangle) {
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

float aStarMath::getSegmentAngle(const GisSegment &line) {
    float p1x = (float) line.getLine().getPoint1().getX();
    float p1y = (float) line.getLine().getPoint1().getY();
    float p2x = (float) line.getLine().getPoint2().getX();
    float p2y = (float) line.getLine().getPoint2().getY();
    double dx = p1x - p2x;
    // Minus to correct for coord re-mapping
    double dy = -(p1y - p2y);

    double inRads = atan2(dy, dx);

    // We need to map to coord system when 0 degree is at 3 O'clock, 270 at 12 O'clock
    if (inRads < 0)
        inRads = fabs(inRads);
    else
        inRads = 2 * M_PI - inRads;

    return (float) (180 * (inRads) / M_PI - 90);
}
