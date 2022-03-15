#ifndef aStarMath__H
#define aStarMath__H

#include "PointF.h"
#include "GisPoint.h"
#include "GisSegment.h"
#include "Log.h"
#include "aStarPoint.h"

class aStarMath {
private:
    static const string TAG;

    static double toRadians(double degrees);

public:
    static void findPoint(const GisPoint &p1, GisPoint &retPoint);

    static GisSegment *findCloseSegment(const GisPoint &p1);

    static double findDistance(const GisPoint &p1, const GisPoint &p2);

    static double findLatLonDistance(const GisPoint &p1, const GisPoint &p2);

    static void findClosePointOnSegment(const GisPoint &p, const GisSegment &s,
                                        GisPoint &closestPoint);

    static void findClosePointOnLine(const GisPoint &p, const GisLine &s, GisPoint &closestPoint);

    static bool isNeighbors(const GisSegment &s1, const GisSegment &s2);

    static double gePathweight(const list<GisSegment *> &path);

    static double calcH(const GisSegment &s, const aStarPoint &epoint);

    static void divideLine(const GisSegment &s, int pixels, list<PointF> &result);

    static PointF subPoint(const GisPoint &startPoint, const GisPoint &endPoint,
                           int segment, int totalSegments);

    static float getAngleToNext(float sangle, float pangle);

    static float getSegmentAngle(const GisSegment &line);

};

#endif // aStarMath__H
