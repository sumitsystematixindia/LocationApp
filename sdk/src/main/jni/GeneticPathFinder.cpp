#include "GeneticPathFinder.h"

//#include  <android/log.h>

using namespace std;

void GeneticPathFinder::loadPoisMapToFloor() {

    poiToFloorMap.clear();
    externalPois.clear();
    floorsInNavPath.clear();

    for (list<Location>::iterator it = poisToNavigateList.begin();
         it != poisToNavigateList.end(); it++) {
        Location &poi = *it;

        //	if (poi.isInternal()) {  // load poi according to its floor
        int z = (int) poi.getZ();

        if (poiToFloorMap.find(z) == poiToFloorMap.end()) {
            TYPE_FloorPoisList plist;
            plist.push_back(poi);
            poiToFloorMap[z] = plist;
        } else {
            map<int, TYPE_FloorPoisList>::iterator it;
            it = poiToFloorMap.find(z);
            TYPE_FloorPoisList &plist = it->second;
            plist.push_back(poi);
            poiToFloorMap[z] = plist;
        }
    }
    //else {  // external poi
    //		externalPois.push_back(poi);
    //	}
    //}

    // get floors visited sorted list

    int zOrigin = (int) originLocation.z;

    /**
    if (!originLocation.isInternal()) {
        map<int, TYPE_FloorPoisList>::iterator it = poiToFloorMap.begin();
        zOrigin = it->first;  // start from lowest floor
    }
    */


    bool isOriginExist = false;
    if (poiToFloorMap.find(zOrigin) == poiToFloorMap.end()) {
        floorsInNavPath.push_back(zOrigin);
        isOriginExist = true;
    }

    // keys of map are sorted
    vector<int> lowerFloors;

    for (map<int, TYPE_FloorPoisList>::iterator it = poiToFloorMap.begin();
         it != poiToFloorMap.end(); ++it) {
        int f = it->first;
        if ((f == zOrigin && !isOriginExist) || f > zOrigin) {
            floorsInNavPath.push_back(f);
        }
        else {
            lowerFloors.push_back(f);
        }
    }

    if (lowerFloors.size() > 0) {
        for (int i = 0; i < lowerFloors.size(); i++) {
            floorsInNavPath.push_back(lowerFloors[i]);
        }
    }
}

void GeneticPathFinder::findClosestSwitchFloor(Location &loc, SwitchFloorObj &sw, int nextFloor) {

    double minDist = 100000; // how to define max double val

    vector<SwitchFloorObj> swInFloor;
    SwitchFloorObj minSW;
    // get switches in same location floor
    for (list<SwitchFloorObj>::iterator it = switchFloor.begin();
         it != switchFloor.end(); it++) {
        SwitchFloorObj &swObj = *it;
        int z = swObj.getZ();
        if (z == loc.getZ()) {
            swInFloor.push_back(swObj);
        }
    }
    if (swInFloor.size() > 0) {

        for (int i = 0; i < swInFloor.size(); i++) {
            SwitchFloorObj currSw = swInFloor[i];

            double p1x = loc.x;
            double p1y = loc.y;
            double p2x = currSw.getPoint().x;
            double p2y = currSw.getPoint().y;
            double dist = MathUtils::distance(p1x, p1y, p2x, p2y);
            if (dist < minDist) {
                minDist = dist;
                minSW = currSw;
            }

        }

    }

    for (list<SwitchFloorObj>::iterator it = switchFloor.begin();
         it != switchFloor.end(); it++) {

        SwitchFloorObj &swObj = *it;

        if (swObj.getId() == minSW.getId() && swObj.getZ() == nextFloor) {
            sw = swObj;
            break;
        }
    }

}


void GeneticPathFinder::findClosestCord(Location &loc, list<Location> &poisExitsList,
                                        Location &closestCord) {

    double minDist = 100000;


    if (poisExitsList.size() > 0) {


        // if outdoor x,y will contains lat,lon
        double p1x = loc.x;
        double p1y = loc.y;


        for (list<Location>::iterator it = poisExitsList.begin();
             it != poisExitsList.end(); it++) {

            Location &pt = *it;

            double p2x = 0;
            double p2y = 0;

            if (loc.type == 0) { // indoor
                p2x = pt.x;
                p2y = pt.y;
            }
            else if (loc.type == 1) { // outdoor
                p2x = pt.lat;
                p2y = pt.lon;
            }

            double dist = MathUtils::distance(p1x, p1y, p2x, p2y);
            if (dist < minDist) {
                minDist = dist;
                closestCord.x = pt.x;
                closestCord.y = pt.y;
                closestCord.z = pt.z;
                closestCord.type = pt.type;
                closestCord.lat = pt.lat;
                closestCord.lon = pt.lon;
                closestCord.facilityId = pt.facilityId;
                closestCord.isFacilityCenter = pt.isFacilityCenter;
            }

        }

    }

}

void GeneticPathFinder::getMixedPoisOrder(string switchFloorFilePath, int generationsCount,
                                          Location &origin,
                                          list<Location> &poisDestList,
                                          list<Location> &poisExitsList,
                                          list<Location> &fullSolutionPath) {

    if (poisDestList.size() == 0) {
        return;
    }


    try {

        list<Location> outdoorPoisToNavigateList;
        list<Location> indoorPoisToNavigateList;
        for (list<Location>::iterator it = poisDestList.begin();
             it != poisDestList.end(); it++) {
            Location &poi = *it;
            if (poi.type == 1) {
                poi.x = poi.lat;
                poi.y = poi.lon;
                outdoorPoisToNavigateList.push_back(poi);
            }
            else if (poi.type == 0) {
                indoorPoisToNavigateList.push_back(poi);
            }
        }

        //__android_log_print(ANDROID_LOG_ERROR, "NdkGeneticPathOrderFinder", "step %d",1);



        if (generationsCount == -1) {
            Initial_population = 800;
        } else {
            setInitialPopulation(generationsCount);
        }
        //__android_log_print(ANDROID_LOG_ERROR, "NdkGeneticPathOrderFinder", "step %d",2);



        if (origin.type == 1) // outdoor
        {
            origin.x = origin.lat;
            origin.y = origin.lon;
            //__android_log_print(ANDROID_LOG_ERROR, "NdkGeneticPathOrderFinder", "step %d",3);
            if (indoorPoisToNavigateList.size() == 0 &&
                outdoorPoisToNavigateList.size() > 0) { // all outdoor


                list<Location> orderedExternalPois;
                getPoisInNavPathOrder(origin, outdoorPoisToNavigateList, orderedExternalPois);
                // __android_log_print(ANDROID_LOG_ERROR, "NdkGeneticPathOrderFinder outdoor-outdoor", "step %d",4);
                std::list<Location>::iterator it = fullSolutionPath.end();
                //__android_log_print(ANDROID_LOG_ERROR, "NdkGeneticPathOrderFinder outdoor-outdoor", "step %d",5);
                fullSolutionPath.insert(it, orderedExternalPois.begin(), orderedExternalPois.end());
                // __android_log_print(ANDROID_LOG_ERROR, "NdkGeneticPathOrderFinder outdoor-outdoor", "step %d",6);

                // __android_log_print(ANDROID_LOG_INFO, "NdkGeneticPathOrderFinder", " all outdoor nextOrigin (x :%f, y :%f, z :%f, lat:%f, lon:%f)",origin.x, origin.y, origin.z, origin.lat, origin.lon);

                return;

            }
            else if (indoorPoisToNavigateList.size() > 0 &&
                     outdoorPoisToNavigateList.size() > 0) { // mixed - outdoor to indoor

                // sort outdoor
                list<Location> orderedExternalPois;
                getPoisInNavPathOrder(origin, outdoorPoisToNavigateList, orderedExternalPois);
                //__android_log_print(ANDROID_LOG_ERROR, "NdkGeneticPathOrderFinder m outdoor-indoor", "step %d",7);
                std::list<Location>::iterator it = fullSolutionPath.end();
                //__android_log_print(ANDROID_LOG_ERROR, "NdkGeneticPathOrderFinder m outdoor-indoor", "step %d",8);
                fullSolutionPath.insert(it, orderedExternalPois.begin(), orderedExternalPois.end());
                // __android_log_print(ANDROID_LOG_ERROR, "NdkGeneticPathOrderFinder m outdoor-indoor", "step %d",9);

                // find connector outdoor to indoor
                Location lastOutdoorLocation = orderedExternalPois.back();
                // __android_log_print(ANDROID_LOG_ERROR, "NdkGeneticPathOrderFinder m outdoor-indoor", "step %d",10);
                Location nextOrigin;
                findClosestCord(lastOutdoorLocation, poisExitsList, nextOrigin);
                // __android_log_print(ANDROID_LOG_ERROR, "NdkGeneticPathOrderFinder m outdoor-indoor", "step %d",11);
                list<Location> orderedInternalPois;

                // sort indoor
                list<Location> solution;
                getPoisOrder(switchFloorFilePath, generationsCount, nextOrigin,
                             indoorPoisToNavigateList, solution);
                //__android_log_print(ANDROID_LOG_ERROR, "NdkGeneticPathOrderFinder m outdoor-indoor", "step %d",12);
                it = fullSolutionPath.end();
                // __android_log_print(ANDROID_LOG_ERROR, "NdkGeneticPathOrderFinder m outdoor-indoor", "step %d",13);
                fullSolutionPath.insert(it, solution.begin(), solution.end());
                // __android_log_print(ANDROID_LOG_ERROR, "NdkGeneticPathOrderFinder m outdoor-indoor", "step %d",14);
                // __android_log_print(ANDROID_LOG_INFO, "NdkGeneticPathOrderFinder", " outdoor to indoor nextOrigin (x :%f, y :%f, z :%f, lat:%f, lon:%f)",nextOrigin.x, nextOrigin.y, nextOrigin.z, nextOrigin.lat, nextOrigin.lon);
                return;
            }
            else if (indoorPoisToNavigateList.size() > 0 && outdoorPoisToNavigateList.size() == 0) {
                list<Location> orderedInternalPois;
                getPoisOrder(switchFloorFilePath, generationsCount, origin,
                             indoorPoisToNavigateList, orderedInternalPois);
                //__android_log_print(ANDROID_LOG_ERROR, "NdkGeneticPathOrderFinder outdoor-indoor", "step %d",30);
                std::list<Location>::iterator it = fullSolutionPath.end();
                //__android_log_print(ANDROID_LOG_ERROR, "NdkGeneticPathOrderFinder outdoor-indoor", "step %d",31);
                fullSolutionPath.insert(it, orderedInternalPois.begin(), orderedInternalPois.end());
                //__android_log_print(ANDROID_LOG_ERROR, "NdkGeneticPathOrderFinder outdoor-indoor", "step %d",32);
            }
        }
        else if (origin.type == 0) { // indoor

            if (outdoorPoisToNavigateList.size() == 0 &&
                indoorPoisToNavigateList.size() > 0) { // all indoor use the old method
                list<Location> orderedInternalPois;
                getPoisOrder(switchFloorFilePath, generationsCount, origin,
                             indoorPoisToNavigateList, orderedInternalPois);
                //__android_log_print(ANDROID_LOG_ERROR, "NdkGeneticPathOrderFinder indoor-indoor", "step %d",15);
                std::list<Location>::iterator it = fullSolutionPath.end();
                //__android_log_print(ANDROID_LOG_ERROR, "NdkGeneticPathOrderFinder indoor-indoor", "step %d",16);
                fullSolutionPath.insert(it, orderedInternalPois.begin(), orderedInternalPois.end());
                //__android_log_print(ANDROID_LOG_ERROR, "NdkGeneticPathOrderFinder indoor-indoor", "step %d",17);

                // __android_log_print(ANDROID_LOG_INFO, "NdkGeneticPathOrderFinder", " all indoor nextOrigin (x :%f, y :%f, z :%f, lat:%f, lon:%f)",origin.x, origin.y, origin.z, origin.lat, origin.lon);
                return;
            }
            else if (outdoorPoisToNavigateList.size() > 0 &&
                     indoorPoisToNavigateList.size() > 0) { // mixed  - indoor to outdoor

                // sort indoor
                list<Location> orderedInternalPois;
                // __android_log_print(ANDROID_LOG_ERROR, "NdkGeneticPathOrderFinder m indoor-outdoor", "step %d",18);
                getPoisOrder(switchFloorFilePath, generationsCount, origin,
                             indoorPoisToNavigateList, orderedInternalPois);
                // __android_log_print(ANDROID_LOG_ERROR, "NdkGeneticPathOrderFinder m indoor-outdoor", "step %d",19);
                std::list<Location>::iterator it = fullSolutionPath.end();
                //__android_log_print(ANDROID_LOG_ERROR, "NdkGeneticPathOrderFinder m indoor-outdoor", "step %d",20);
                fullSolutionPath.insert(it, orderedInternalPois.begin(), orderedInternalPois.end());
                // __android_log_print(ANDROID_LOG_ERROR, "NdkGeneticPathOrderFinder m indoor-outdoor", "step %d",21);
                //__android_log_print(ANDROID_LOG_INFO, "NdkGeneticPathOrderFinder", " indoor to outdoor nextOrigin (x :%f, y :%f, z :%f, lat:%f, lon:%f)",0.0, 0.0, 0.0, 0.0, 0.0);

                // find indoor to outdoor connector
                Location lastIndoorLocation = orderedInternalPois.back();
                //__android_log_print(ANDROID_LOG_ERROR, "NdkGeneticPathOrderFinder m indoor-outdoor", "step %d",22);
                Location nextOrigin;
                findClosestCord(lastIndoorLocation, poisExitsList, nextOrigin);
                //__android_log_print(ANDROID_LOG_ERROR, "NdkGeneticPathOrderFinder m indoor-outdoor", "step %d",23);
                //__android_log_print(ANDROID_LOG_INFO, "NdkGeneticPathOrderFinder", " indoor to outdoor nextOrigin (x :%f, y :%f, z :%f, lat:%f, lon:%f)",1.0, 1.0, 1.0, 1.0, 1.0);

                // sort outdoor
                list<Location> orderedExternalPois;
                getPoisInNavPathOrder(nextOrigin, outdoorPoisToNavigateList, orderedExternalPois);
                //__android_log_print(ANDROID_LOG_ERROR, "NdkGeneticPathOrderFinder m indoor-outdoor", "step %d",24);
                // __android_log_print(ANDROID_LOG_INFO, "NdkGeneticPathOrderFinder", " indoor to outdoor nextOrigin (x :%f, y :%f, z :%f, lat:%f, lon:%f)",3.0, 3.0, 3.0, 3.0, 3.0);
                it = fullSolutionPath.end();
                //__android_log_print(ANDROID_LOG_ERROR, "NdkGeneticPathOrderFinder m indoor-outdoor", "step %d",25);
                fullSolutionPath.insert(it, orderedExternalPois.begin(), orderedExternalPois.end());
                //__android_log_print(ANDROID_LOG_ERROR, "NdkGeneticPathOrderFinder m indoor-outdoor", "step %d",26);

                //__android_log_print(ANDROID_LOG_INFO, "NdkGeneticPathOrderFinder", " indoor to outdoor nextOrigin (x :%f, y :%f, z :%f, lat:%f, lon:%f)",nextOrigin.x, nextOrigin.y, nextOrigin.z, nextOrigin.lat, nextOrigin.lon);


            }
            else if (outdoorPoisToNavigateList.size() > 0 &&
                     indoorPoisToNavigateList.size() == 0) { // all outdoor
                list<Location> orderedExternalPois;
                getPoisInNavPathOrder(origin, outdoorPoisToNavigateList, orderedExternalPois);
                // __android_log_print(ANDROID_LOG_ERROR, "NdkGeneticPathOrderFinder indoor-outdoor", "step %d",27);
                std::list<Location>::iterator it = fullSolutionPath.end();
                // __android_log_print(ANDROID_LOG_ERROR, "NdkGeneticPathOrderFinder indoor-outdoor", "step %d",28);
                fullSolutionPath.insert(it, orderedExternalPois.begin(), orderedExternalPois.end());
                // __android_log_print(ANDROID_LOG_ERROR, "NdkGeneticPathOrderFinder indoor-outdoor", "step %d",29);
            }

        }

    } catch (exception e) {
        if (poisDestList.size() > 0) {
            std::list<Location>::iterator it = fullSolutionPath.end();
            fullSolutionPath.insert(it, poisDestList.begin(), poisDestList.end());
            //__android_log_print(ANDROID_LOG_ERROR, "NdkGeneticPathOrderFinder try-catch", "step %d",33);
        }
    }

}


void GeneticPathFinder::getOutdoorOrder(int generationsCount, Location &origin,
                                        list<Location> &poisDestList,
                                        list<Location> &fullSolutionPath) {

    if (poisDestList.size() == 0) {
        return;
    }

    try {


        Location originLoc;
        originLoc.x = origin.x;
        originLoc.y = origin.y;
        originLoc.z = origin.z;
        originLoc.type = origin.type;
        originLoc.lat = origin.lat;
        originLoc.lon = origin.lon;
        originLoc.x = origin.lat;
        originLoc.y = origin.lon;
        originLoc.facilityId = origin.facilityId;
        originLoc.isFacilityCenter = origin.isFacilityCenter;

        list<Location> outdoorPoisToNavigateList;

        for (list<Location>::iterator it = poisDestList.begin();
             it != poisDestList.end(); it++) {
            Location &poi = *it;
            if (poi.type == 1) {
                poi.x = poi.lat;
                poi.y = poi.lon;
                outdoorPoisToNavigateList.push_back(poi);
            }
        }

        if (generationsCount == -1) {
            Initial_population = 1000;
        } else {
            setInitialPopulation(generationsCount);
        }


        if (outdoorPoisToNavigateList.size() > 0) { // all outdoor
            list<Location> orderedExternalPois;
            getPoisInNavPathOrder(originLoc, outdoorPoisToNavigateList, orderedExternalPois);
            std::list<Location>::iterator it = fullSolutionPath.end();
            fullSolutionPath.insert(it, orderedExternalPois.begin(), orderedExternalPois.end());

        }


    } catch (exception e) {
        if (poisDestList.size() > 0) {
            std::list<Location>::iterator it = fullSolutionPath.end();
            fullSolutionPath.insert(it, poisDestList.begin(), poisDestList.end());
        }
    }

}


void GeneticPathFinder::getPoisOrder(string switchFloorFilePath,
                                     int generationsCount, Location &origin,
                                     list<Location> &poisDestList,
                                     list<Location> &fullSolutionPath) {

    try {

        if (poisDestList.size() == 0) {
            return;
        }

        if (generationsCount == -1) {
            Initial_population = 800;
        } else {
            setInitialPopulation(generationsCount);
        }

        originLocation = origin;
        poisToNavigateList = poisDestList;

        //string  switchFloorFilePath =  appDirPath + "/" + "switchfloor.txt";
        loadSwitchFloor(switchFloorFilePath.c_str());

        // separate each poi according to its floor
        loadPoisMapToFloor();

        //if (originLocation.isInternal()) {



        Location nextOrigin = originLocation;

        int startingFloorIndex = 0;

        // if there is only one floor
        if (floorsInNavPath.size() == 1) {
            int lastfloor = floorsInNavPath[startingFloorIndex];
            std::map<int, std::list<Location> >::iterator itr = poiToFloorMap.find(lastfloor);
            list<Location> floorpoislist = itr->second;
            list<Location> orderedPoisInFloor;
            getPoisInNavPathOrder(nextOrigin, floorpoislist, orderedPoisInFloor);
            std::list<Location>::iterator it = fullSolutionPath.end();
            fullSolutionPath.insert(it, orderedPoisInFloor.begin(), orderedPoisInFloor.end());

            //XXX uncomment for debug
            //list<Location>::const_iterator itr2;
            //		for (itr2 = fullSolutionPath.begin();
            //			   itr2 != fullSolutionPath.end(); ++itr2) {
            // __android_log_print(ANDROID_LOG_ERROR, "NdkGeneticPathOrderFinder fullSolutionPath", "step  %f,%f",itr2->x,itr2->y);
            //		}

            // __android_log_print(ANDROID_LOG_ERROR, "NdkGeneticPathOrderFinder ", "step %d",1);
            return;
        }


        // check if origin floor have no pois
        int zOrigin = (int) nextOrigin.z;
        std::map<int, std::list<Location> >::iterator originItr = poiToFloorMap.find(zOrigin);
        bool isEmptyPoisInOriginFloor = false;
        if (originItr == poiToFloorMap.end()) {
            isEmptyPoisInOriginFloor = true;
            startingFloorIndex = 1;
        }

        // if there is 2 floors only and origin floor have no pois
        if (isEmptyPoisInOriginFloor && floorsInNavPath.size() == 2) {
            int nextFloor = floorsInNavPath[startingFloorIndex];
            SwitchFloorObj nextSw;
            Location nextLoc = nextOrigin;
            findClosestSwitchFloor(nextLoc, nextSw, nextFloor);
            nextOrigin = nextSw.getAsLocation();

            std::map<int, std::list<Location> >::iterator secitr = poiToFloorMap.find(nextFloor);
            if (secitr != poiToFloorMap.end()) {
                list<Location> floorpoislist = secitr->second;
                list<Location> orderedPoisInFloor;
                getPoisInNavPathOrder(nextOrigin, floorpoislist, orderedPoisInFloor);
                std::list<Location>::iterator fsit = fullSolutionPath.end();
                fullSolutionPath.insert(fsit, orderedPoisInFloor.begin(), orderedPoisInFloor.end());
                //__android_log_print(ANDROID_LOG_ERROR, "NdkGeneticPathOrderFinder ", "step %d",3);
                return;
            }
        }


        // get solution for the floors except the last one
        for (int i = startingFloorIndex; i < floorsInNavPath.size() - 1; i++) {

            int floor = floorsInNavPath[i];
            int nextFloor = floorsInNavPath[i + 1];

            std::map<int, std::list<Location> >::iterator itr = poiToFloorMap.find(floor);

            if (i != 0 && i != startingFloorIndex) {
                SwitchFloorObj nextSw;
                Location nextLoc = fullSolutionPath.back();
                findClosestSwitchFloor(nextLoc, nextSw, nextFloor);
                nextOrigin = nextSw.getAsLocation();
            }

            if (itr != poiToFloorMap.end()) {
                list<Location> floorpoislist = itr->second;
                list<Location> orderedPoisInFloor;
                getPoisInNavPathOrder(nextOrigin, floorpoislist, orderedPoisInFloor);
                std::list<Location>::iterator it = fullSolutionPath.end();
                fullSolutionPath.insert(it, orderedPoisInFloor.begin(), orderedPoisInFloor.end());
            }
        }

        // add last floor
        if (fullSolutionPath.size() > 0) {
            int idx = (int) (floorsInNavPath.size() - 1);
            int lastfloor = floorsInNavPath[idx];
            std::map<int, std::list<Location> >::iterator itr = poiToFloorMap.find(lastfloor);
            list<Location> floorpoislist = itr->second;
            list<Location> orderedPoisInFloor;
            Location nextLoc = fullSolutionPath.back();
            SwitchFloorObj nextSw;
            findClosestSwitchFloor(nextLoc, nextSw, lastfloor);
            nextOrigin = nextSw.getAsLocation();
            getPoisInNavPathOrder(nextOrigin, floorpoislist, orderedPoisInFloor);
            std::list<Location>::iterator it = fullSolutionPath.end();
            fullSolutionPath.insert(it, orderedPoisInFloor.begin(), orderedPoisInFloor.end());
        }

    } catch (exception e) {
        if (poisDestList.size() > 0) {
            std::list<Location>::iterator it = fullSolutionPath.end();
            fullSolutionPath.insert(it, poisDestList.begin(), poisDestList.end());
        }
    }

}

void GeneticPathFinder::getPoisInNavPathOrder(Location &origin,
                                              list<Location> &poisInSameFloorDestList,
                                              list<Location> &solution) {

    Mating_population = Initial_population / 2;
    Favoured_population = Mating_population / 2;

    Mutation_probability = 0.10;
    loops = 1.5;
    Epoch = 0;
    min_cost = 5.0;
    costPerfect = 0;


    //string appDirPath;

    //clean
    poisLocationsList.clear();
    chromosomes.clear();


    if (poisInSameFloorDestList.size() == 0) {
        return;
    }

    if (poisInSameFloorDestList.size() == 1) {
        solution.push_back(poisInSameFloorDestList.front());
        return;
    }

    //System.out.println("GeniticNavPathToPoisFinder init ...");
    //System.out.println("Start ...");

    poisCount = (int) poisInSameFloorDestList.size() + 1;
    cut_length = poisCount / 5;

    // add origin to the list
    Location o;
    o.x = origin.x;
    o.y = origin.y;
    o.z = origin.z;
    o.type = origin.type;
    o.lat = origin.lat;
    o.lon = origin.lon;
    o.facilityId = origin.facilityId;
    o.isFacilityCenter = origin.isFacilityCenter;
    //o.proximity(origin.x, origin.y);
    o.initProximity(origin);
    poisLocationsList.push_back(o);


    list<Location>::const_iterator itr;
    for (itr = poisInSameFloorDestList.begin();
         itr != poisInSameFloorDestList.end(); ++itr) {

        Location poi;
        poi.x = itr->x;
        poi.y = itr->y;
        poi.z = itr->z;
        poi.type = itr->type;
        poi.lat = itr->lat;
        poi.lon = itr->lon;
        poi.facilityId = itr->facilityId;
        poi.isFacilityCenter = itr->isFacilityCenter;
        //poi.proximity(origin.x, origin.y);
        poi.initProximity(origin);
        poisLocationsList.push_back(poi);

    }

    Chromosome perfect;
    perfect.create(poisLocationsList);

    vector<int> cl(poisCount, 0);

    for (int i = 0; i < poisCount; i++) {
        cl[i] = i;
    }

    perfect.set_cities(cl);
    perfect.calculate_cost(poisLocationsList);
    //System.out.println("Cost Perfect = " + perfect.get_cost());
    costPerfect = perfect.get_cost();

    // Randomise the order of the pois
    /*
    for (int i = 0; i < poisCount; i++) {
        int index1 = (int) (0.999999 * MathUtils::getRandom()
                * (double) poisCount);
        int index2 = (int) (0.999999 * MathUtils::getRandom()
                * (double) poisCount);
        // swap the positions

        Location temp = poisLocationsList[index2];
        poisLocationsList[index2] = poisLocationsList[index1];
        poisLocationsList[index1] = temp;
    }
     */

    // generate an initial population of chromosomes

    //chromosomes = new Chromosome[Initial_population];

    for (int i = 0; i < Initial_population; i++) {
        Chromosome chromosome;
        chromosome.create(poisLocationsList);
        chromosome.set_cut(cut_length);
        chromosome.set_mutation(Mutation_probability);
        chromosomes.push_back(chromosome);
    }

//		timestart = System.currentTimeMillis();
//		timend = timestart;
//
//		started = true;

    Sort_chromosomes(Initial_population);

    Epoch = 0;

    geneticCalculation(solution);
    // __android_log_print(ANDROID_LOG_ERROR, "NdkGeneticPathOrderFinder geneticCalculation", "step %d",2);
    //remove origin
    if (solution.size() > 1) {

        list<Location>::const_iterator itr;
        for (itr = solution.begin();
             itr != solution.end(); ++itr) {
            // __android_log_print(ANDROID_LOG_ERROR, "NdkGeneticPathOrderFinder geneticCalculation", "step solution %f,%f",itr->x,itr->y);
        }

        // remove origin from solution
        std::list<Location>::iterator itsol = solution.begin();
        while (itsol != solution.end()) {
            if (fabs(itsol->x - origin.x) < 0.00000001 && fabs(itsol->y - origin.y) < 0.00000001 &&
                fabs(itsol->z - origin.z) == 0) {
                solution.erase(itsol++);
                //__android_log_print(ANDROID_LOG_ERROR, "NdkGeneticPathOrderFinder geneticCalculation", "erased step solution %f,%f",itsol->x,itsol->y);
            }
            else {
                ++itsol;
            }
        }

        // do not add the origin and switch floor to the solution
//		std::list<Location>::iterator itsol2 = solution.begin();
//		solution.insert(itsol2,origin);

        //solution.pop_front();

        // __android_log_print(ANDROID_LOG_ERROR, "NdkGeneticPathOrderFinder geneticCalculation", "step solution %d",2);

    }

}

void GeneticPathFinder::Sort_chromosomes(int num) {
    Chromosome ctemp;
    bool swapped = true;
    while (swapped) {
        swapped = false;
        for (int i = 0; i < num - 1; i++) {
            if (chromosomes[i].get_cost() > chromosomes[i + 1].get_cost()) {
                ctemp = chromosomes[i];
                chromosomes[i] = chromosomes[i + 1];
                chromosomes[i + 1] = ctemp;
                swapped = true;
            }
        }
    }
}

void GeneticPathFinder::geneticCalculation(list<Location> &solution) {

    double this_cost = 500.0;
    double old_cost = 0.0;
    __unused double dcost = 500.0;
    int count_same = 0;

    while (this_cost > costPerfect && count_same < 100) {

        Epoch++;

        int ioffset = Mating_population;
        int mutated = 0;

        // Mate the chromosomes in the favoured population with all in the
        // mating population
        for (int i = 0; i < Favoured_population; i++) {
            Chromosome cmother = chromosomes[i];
            // Select partner from the mating population
            int father = (int) (0.999999 * MathUtils::getRandom()
                                * (double) Mating_population);
            Chromosome cfather = chromosomes[father];

            mutated += cmother.mate(cfather, chromosomes[ioffset],
                                    chromosomes[ioffset + 1]);
            ioffset += 2;
        }

        // The new generation of chromosomes is in position
        // Mating_population ... move them
        // to the right place in the list and calculate their costs

        for (int i = 0; i < Mating_population; i++) {
            chromosomes[i] = chromosomes[i + Mating_population];
            chromosomes[i].calculate_cost(poisLocationsList);
        }

        // Now sort them

        Sort_chromosomes(Mating_population);

        double cost = chromosomes[0].get_cost();
        dcost = fabs(cost - this_cost);
        this_cost = cost;

        //double mutation_rate = 100.0 * (double) mutated / (double) Mating_population;

        //System.out.println("Epoch " + Epoch + " Cost " + (int) this_cost + " Mutated " + mutation_rate + "% Count " + count_same);

        if ((int) this_cost == (int) old_cost) {
            count_same++;
        } else {
            count_same = 0;
            old_cost = this_cost;
        }

    }
    //System.out.println("A solution found after " + Epoch + " epochs!");

    //System.out.println("found solution");

    for (int i = 0; i < poisCount; i++) {
        int ipos = chromosomes[0].get_city(i);
        solution.push_back(poisLocationsList[ipos]);
        //System.out.println(poisLocationsList[ipos].getX() + " " + poisLocationsList[ipos].getY());
    }

}

void GeneticPathFinder::loadSwitchFloor(const string &ofile) {

    switchFloor.clear();
    ifstream infile;

    infile.open(ofile.c_str());

    if (!infile.is_open())
        return;

    string line = "";

    while (!infile.eof()) {

        getline(infile, line);
        if (line.length() > 0) {
            SwitchFloorObj sw;
            sw.parse(line);
            switchFloor.push_back(sw);
            //__android_log_print(ANDROID_LOG_INFO, "loadSwitchFloorGroupsFile","bssid :%s", bssid.c_str());
        }
    }

}

//  void GeneticPathFinder::clean(){
//
//		 Initial_population=-1;
//		 Mating_population=-1;
//		 Favoured_population=-1;
//		 poisCount=-1;
//		 cut_length=-1;
//
//		 Mutation_probability=-1;
//		 loops=-1;
//		 Epoch=-1;
//		 min_cost=-1;
//		 costPerfect=-1;
//
//
//		poisLocationsList.clear();
//		chromosomes.clear();
//		switchFloor.clear();
//		originLocation.x=-1;
//		originLocation.y=-1;
//		originLocation.z=-1;
//		originLocation.lat=-1;
//		originLocation.lon=-1;
//		originLocation.facilityId = "";
//		poisToNavigateList.clear();
//
//		poiToFloorMap.clear();
//		externalPois.clear();
//		floorsInNavPath.clear();
//  }

