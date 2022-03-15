#ifndef FloorNavigationPath__H
#define FloorNavigationPath__H

#include <list>
#include "GisSegment.h"

using namespace std;

class FloorNavigationPath {
private:
    double Z;
    list<GisSegment *> path;


public:
    FloorNavigationPath();

    FloorNavigationPath(double floor, const list<GisSegment *> &p);


    double getZ() const {
        return Z;
    }

    void setZ(double z) {
        Z = z;
    }

    const list<GisSegment *> &getPath() const {
        return path;
    }

    void setPath(const list<GisSegment *> &newPath) {
        path = newPath;
    }

};

#endif // FloorNavigationPath
