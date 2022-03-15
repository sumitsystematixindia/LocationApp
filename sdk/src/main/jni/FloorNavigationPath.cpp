#include "FloorNavigationPath.h"
#include "FacilityConf.h"

FloorNavigationPath::FloorNavigationPath() :
        Z(-100) {

}

FloorNavigationPath::FloorNavigationPath(double floor, const list<GisSegment *> &p) :
        Z(floor),
        path(p.begin(), p.end()) {
}


// UGLY PLACE For FacilityConf
FacilityConf *FacilityConf::instance = NULL;
