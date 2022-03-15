#ifndef OPTFLOORSELECTORFINDER_H_
#define OPTFLOORSELECTORFINDER_H_

#include "OptFloorSelectorFinder.h"
#include "OptLocFinder.h"
#include <list>
#include "WlBlip.h"

using namespace std;


class OptFloorSelectorFinder : public OptLocFinder {

private:
    static bool instanceFlagOFS;
    static OptFloorSelectorFinder *singleOFS;
public:

    static OptFloorSelectorFinder *getInstance();

    static void releaseInstance();

    virtual ~OptFloorSelectorFinder();

    // int getFloorByBlips(list<WlBlip>& blips);
    OptFloorSelectorFinder();

};

#endif /* OPTFLOORSELECTORFINDER_H_ */
