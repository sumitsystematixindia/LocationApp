#ifndef aStarData__H
#define aStarData__H

#include <string>
#include <list>
#include <map>

using namespace std;

#include "PointF.h"
#include "GisPoint.h"
#include "GisSegment.h"
#include "Location.h"
#include "NavigationPath.h"


class aStarPoint;

typedef list<GisSegment *> SegmentList;

class aStarData {

private:
    static aStarData *instance;
    static int SegmentId;
    PointF myLocation;
    NavigationPath currentPath;
    Location Destination;
    static const string TAG;

    void clean();

    aStarData();

    GisLine *findCloseLine(list<GisLine *> &linesList, GisPoint &p1);

    GisLine *findCloseKmlLine(list<GisLine *> &linesList, GisPoint &p1);

public:
    list<GisSegment *> segmentTable;
    list<GisPoint *> gPoints;
    list<aStarPoint *> segmentTree;
    map<int, SegmentList> Neighbors;

    Location poilocation;

    static aStarData &getInstance();

    static void releaseInstance();


    void loadData(const list<GisLine *> &lines);

    void loadData(GisPoint &start, GisPoint &end, const list<GisLine *> &lines, bool mode);

    void testFindRouteFromFile();

    const Location &getPoilocation() const;

    void setPoilocation(const Location &poilocation) {
        this->poilocation = poilocation;
    }

    const PointF &getMyLocation() {
        return myLocation;
    }

    void setMyLocation(const PointF &myLocation) {
        this->myLocation = myLocation;
    }

    const NavigationPath &getCurrentPath() {
        return currentPath;
    }

    void setCurrentPath(const NavigationPath &currentPath) {
        this->currentPath = currentPath;
    }

    void cleanAStar();

    const Location &getDestination() {
        return Destination;
    }

    void setDestination(const Location &destination) {
        Destination = destination;
    }
};

#endif // aStarData__H
