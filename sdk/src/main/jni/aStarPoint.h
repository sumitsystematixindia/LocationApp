#ifndef ASTARPOINT__H
#define ASTARPOINT__H

#include <string>
#include <list>
#include "GisSegment.h"

using namespace std;

#include "GisPoint.h"


class aStarPoint {

private:
    static const string TAG;
    GisPoint Point;

public:
    list<GisSegment *> Segments;

    bool connectToLine(const GisPoint &p, const GisSegment &line);


    aStarPoint(const GisPoint &p, const list<GisSegment *> &s);

    aStarPoint();

    const GisPoint &getPoint() const {
        return Point;
    }

    void setPoint(const GisPoint &point) {
        Point = point;
    }

};

#endif //ASTARPOINT__H
