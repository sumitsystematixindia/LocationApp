#ifndef ASTAR_ALGORITHM__H
#define ASTAR_ALGORITHM__H

#include <string>
#include <list>
#include <map>
#include "GisPoint.h"
#include "GisData.h"
#include "GisSegment.h"
#include "aStarPoint.h"


using namespace std;

class aStarAlgorithm {
private:

    static const string TAG;

    aStarPoint *startPoint;
    aStarPoint *endPoint;

    list<GisSegment *> open;
    list<GisSegment *> closed;
    GisPoint realStart;
    GisPoint realEnd;


public:
    map<int, GisSegment *> parents;

    aStarAlgorithm(const GisPoint &s, const GisPoint &e);

    void getPath(list<GisSegment *> &);

    void buildpaths(const GisSegment &startSegment, list<GisSegment *> &outList);

    GisSegment *findLowF(const list<GisSegment *> &open, const aStarPoint &epoint);

    GisSegment *findStartLowF(const list<GisSegment *> &segments, const aStarPoint &epoint);

    void fixEdges(list<GisSegment *> &path);

    bool isSameFakePoint();

    void setSegmentsDirection(list<GisSegment *> &minpath);

    aStarPoint &getStartPoint();

    void setStartPoint(const GisPoint &p1);

    aStarPoint &getEndPoint();

    void setEndPoint(const GisPoint &p2);

    string to_string(int num);

};

#endif // ASTAR_ALGORITHM__H
