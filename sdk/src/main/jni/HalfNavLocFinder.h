#ifndef HALFNAVLOCFINDER_H_
#define HALFNAVLOCFINDER_H_

#include <vector>
#include <map>
#include <list>
#include <set>


#include <string>
#include "Location.h"
#include "WlBlip.h"
#include "Beacon.h"

using namespace std;

class FloorCandidate {
public:
    int floor;
    int freq;
};

class HalfNavLocFinder {

private:
    static bool instanceFlag;
    static HalfNavLocFinder *single;

    float ITERATIONS;
    float TOPK;
    float DECAY_STEP;
    float INIT_DECAY_PERCENT;
    bool IS_USE_HALF_NAV_ALG;
    int currentFloor;
    list<WlBlip> blipsList;

    float DETECT_FLOOR_TOP_K;
    Location lastLoc;
    double MAX_LEVEL;
    map<string, Beacon> beaconsMap;
    set<int> floors;

    void initData();
    // void getInitialAveragePoint(Location& loc);
    // void getNextPoint(Location& loc,float stepPercent);

    static bool compareWlBlip(WlBlip first, WlBlip second);

    static bool compareBeacon(Beacon first, Beacon second);

    double distance(const Location &p, const Location &p1);

    void getWeightAvgLocation(list<Beacon> &topklist, Location &loc);


    //XXX new open places alg params =====
    int beaconsCountForInitOpenSpaceLocation;
    float thresholdForUpdateOpenSpaceLocation;
    bool useOpenSpaceAlg;
    bool isFirstOpenSpaceRun;
    Location currentOpenSpaceLoc;

    void computeOpenSpaceInitialLocation(list<Beacon> &topklist, Location &loc);

    void findOpenSpaceLocation(list<WlBlip> &blips, Location &loc);

    void updateOpenSpaceLocation(list<Beacon> &topklist, Location &loc);
    // ===== new open places alg params =====

public:

    HalfNavLocFinder();

    static HalfNavLocFinder *getInstance();

    static void releaseInstance();

    virtual ~HalfNavLocFinder();


    //void initParams(list<Beacon>& beacons, int iterations, int topk, float decayStep, float initDecayPecent);

    void findLocation(list<WlBlip> &blips, Location &loc);

    void loadSettings(string halfNavSettingsDirPath);

    void updateStatus(int floor);

    bool isStatusOK();

    int getFloorByBlipsWithoutMatrix(list<WlBlip> &blips);

};


#endif /* HALFNAVLOCFINDER_H_ */

