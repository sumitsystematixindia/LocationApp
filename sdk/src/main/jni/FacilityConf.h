#ifndef FacilityConf__H
#define FacilityConf__H

#include <string>

using namespace std;

class FloorData;

class FacilityConf {
    FacilityConf() { }

    static FacilityConf *instance;
public:

    static FacilityConf &getInstance() {
        if (instance == NULL) {
            instance = new FacilityConf();
        }
        return *instance;
    }

    int getSelectedFloor() {
        return 0;//ADIA TODO Fixe this
    }

    FloorData *getFloor(int floor) {
        return NULL;
    }

};

#endif // FacilityConf__H
