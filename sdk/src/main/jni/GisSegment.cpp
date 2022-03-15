#include "GisSegment.h"


list<GisSegment *> GisSegment::EMPTY_SEGMENT_LIST;

GisSegment::GisSegment(const GisLine &l, int sid) :
        segmentId(sid),
        weight(0),
        parent(-1),
        g(0),
        Line(l) {
    weight = calcweight();

}

GisSegment::GisSegment() :
        segmentId(0),
        weight(0),
        parent(-1),
        g(0) {

}

double GisSegment::calcweight() {
    double result = 0;
    double p1x = getLine().getPoint1().getX();
    double p1y = getLine().getPoint1().getY();
    double p2x = getLine().getPoint2().getX();
    double p2y = getLine().getPoint2().getY();

    result = sqrt((p1x - p2x) * (p1x - p2x) + (p1y - p2y)
                                              * (p1y - p2y));

    return result;
}


string GisSegment::toString() const {
    // return string("Segment:") + Line.toString() + ": ID(" + segmentId + "), parent(" + parent;
    return "GISSEgment";
}
