#include "NavigationPath.h"
#include "Log.h"
#include "FloorNavigationPath.h"
#include "FacilityConf.h"
#include "aStarMath.h"

const string NavigationPath::TAG = "NavigationPath";


NavigationPath::NavigationPath() {
}

NavigationPath::~NavigationPath() {
    for (list<FloorNavigationPath *>::iterator iter = FullPath.begin();
         iter != FullPath.end(); iter++) {
        delete *iter;
    }
}

void NavigationPath::addFloorNavigationPath(double floor, const list<GisSegment *> &p) {
    if (p.size() != 0) {
        FloorNavigationPath *fpath = new FloorNavigationPath(floor, p);
        FullPath.push_back(fpath);
    }
}

list<GisSegment *> &NavigationPath::getPathByZ(double z) const {

    list<GisSegment *> &result = GisSegment::EMPTY_SEGMENT_LIST;
    for (list<FloorNavigationPath *>::const_iterator o = FullPath.begin();
         o != FullPath.end(); o++) {
        if ((*o)->getZ() == z) {
            result = (*o)->getPath();
            break;
        }
    }
    return result;
}


GisSegment *NavigationPath::getClosestSegment(PointF &pt) {
    Log::getInstance().debug(TAG, "Enter, GISSegment aStarNavigationPath");
    GisSegment *closest = NULL;
    try {
        int z = FacilityConf::getInstance().getSelectedFloor();
        GisPoint p1((double) pt.x, (double) pt.y, z);
        list<GisSegment *> &path = getPathByZ(z);
        if (!path.empty()) {
            double distance = 0;
            double mindistance = DBL_MAX;
            GisPoint p2;
            for (list<GisSegment *>::iterator s = path.begin(); s != path.end(); s++) {
                aStarMath::findClosePointOnSegment(p1, *(*s), p2);
                distance = aStarMath::findDistance(p1, p2);
                if (distance < mindistance) {
                    closest = *s;
                    mindistance = distance;
                }
            }

        }
        Log::getInstance().debug(TAG, "Exit, GISSegment aStarNavigationPath");
    } catch (exception t) {
        Log::getInstance().error(TAG, string("Exception getClosestSegment : ") + t.what());
    }
    return closest;
}


GisSegment *NavigationPath::getNext(GisSegment *segment) {
    GisSegment *result = NULL;
    if (FullPath.size() > 0) {
        for (list<FloorNavigationPath *>::iterator o = FullPath.begin(); o != FullPath.end(); o++) {
            list<GisSegment *> path = (*o)->getPath();
            int index = 0;
            for (list<GisSegment *>::iterator iter = path.begin();
                 iter != path.end(); iter++, index++)
                if (*iter == segment) { // We found our segment
                    // Check to see if the next segment is not out of Array
                    if (path.size() > index + 1) {
                        result = *(++iter);
                        break;//ADIA ADDED - Check with Meir
                    }
                }
        }
    }
    return result;
}

PointF NavigationPath::getClosestPointOnPath(const PointF &pt) {
    int z = FacilityConf::getInstance().getSelectedFloor();
    GisPoint p1((double) pt.x, (double) pt.y, z);
    PointF result;
    GisPoint minpt(-1, -1, -1);
    GisPoint point;
    list<GisSegment *> &tmpPath = getPathByZ(z);
    if (tmpPath.empty()) {
        return PointF(-1, -1);
    }

    double distance = 0;
    double mindistance = 1000000;
    for (list<GisSegment *>::iterator s = tmpPath.begin(); s != tmpPath.end(); s++) {
        aStarMath::findClosePointOnSegment(p1, *(*s), point);
        distance = aStarMath::findDistance(p1, point);
        if (distance < mindistance) {
            minpt = point;
            mindistance = distance;
        }
    }
    if (minpt.getX() != -1) {
        result.set((float) minpt.getX(), (float) minpt.getY());
    }

    return result;
}

	
