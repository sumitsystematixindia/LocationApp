#include "OptFloorSelectorFinder.h"

bool OptFloorSelectorFinder::instanceFlagOFS = false;

OptFloorSelectorFinder *OptFloorSelectorFinder::singleOFS = NULL;

OptFloorSelectorFinder::OptFloorSelectorFinder() {

    IS_FLOOR_SELECTION = true;

}

OptFloorSelectorFinder::~OptFloorSelectorFinder() {
    instanceFlagOFS = false;
    singleOFS = NULL;
}

OptFloorSelectorFinder *OptFloorSelectorFinder::getInstance() {
    if (!instanceFlagOFS) {
        singleOFS = new OptFloorSelectorFinder();
        instanceFlagOFS = true;
        return singleOFS;
    } else {
        return singleOFS;
    }
}

void OptFloorSelectorFinder::releaseInstance() {
    if (instanceFlagOFS) {
        delete singleOFS;
        instanceFlagOFS = false;
        singleOFS = NULL;
    }
}
