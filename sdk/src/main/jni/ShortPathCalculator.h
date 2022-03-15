#ifndef ShortPathCalculator__H
#define ShortPathCalculator__H

#include <math.h>
#include <vector>
#include "Location.h"
//#include  <android/log.h>
using namespace std;

class ShortPathCalculator {


private:
    double toRadians(double degrees);

    double getDistanceByXYZInMeters(Location &l1, Location &l2, double p2m);

    double getDistanceByLatLonInMeters(Location &l1, Location &l2);

public:

    ShortPathCalculator();

    virtual ~ShortPathCalculator();

    void findBestEnterAndExist(Location &origin,
                               Location &dest,
                               vector<Location> &origin_exits,
                               vector<Location> &dest_exits,
                               Location &selectedOriginExit,
                               Location &selectedDestExit,
                               double p2mOrigin,
                               double p2mDest);


};

#endif  // ShortPathCalculator
