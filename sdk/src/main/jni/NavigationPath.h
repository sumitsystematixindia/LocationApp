#ifndef NAVIGATIONPATH__H
#define NAVIGATIONPATH__H

#include <string>
#include <list>
#include "PointF.h"
#include "Location.h"

using namespace std;

class GisSegment;

class FloorNavigationPath;

class NavigationPath {
private:
    static const string TAG;
    list<FloorNavigationPath *> FullPath;
    Location elevator;

public:
    NavigationPath();

    ~NavigationPath();

    void addFloorNavigationPath(double floor, const list<GisSegment *> &p);

    list<GisSegment *> &getPathByZ(double z) const;


    GisSegment *getClosestSegment(PointF &pt);

    GisSegment *getNext(GisSegment *segment);


    PointF getClosestPointOnPath(const PointF &pt);


    const Location &getElevator() const {
        return elevator;
    }

    void setElevator(Location elevator) {
        this->elevator = elevator;
    }

    const list<FloorNavigationPath *> &getFullPath() const {
        return FullPath;
    }

    void setFullPath(list<FloorNavigationPath *> &fullPath) {
        FullPath = fullPath;
    }


};

#endif // NAVIGATIONPATH__H
