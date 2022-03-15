#ifndef GENITIC_ORDER_BUILDER_H
#define GENITIC_ORDER_BUILDER_H

#include <string>
#include <list>
#include<vector>
#include <math.h>
#include "Location.h"
#include "GeneticPathFinder.h"
#include <stdio.h>
#include <map>

#ifdef __APPLE__
#else

#include <android/log.h>

#endif
using namespace std;

typedef list<Location> TYPE_LOCATIONS_LIST;

class GeneticOrderBuilder {


private:
    map<string, TYPE_LOCATIONS_LIST> poiToFacilityMap;
    //map<string, Location> centerToFacilityMap;
    list<Location> externalPois;
    list<Location> centersList;
    list<Location> destsList;

    string applicationDir;
    string projectId;
    string campusId;
    int genCount;
    Location originLocation;

    void mergeOrder(list<Location> &fullSolutionPath, Location &originFacilityLocation);

public:

    virtual ~GeneticOrderBuilder();

    GeneticOrderBuilder();

    void buildOrder(string appDir, string projectid, string campusid, int generationsCount,
                    Location &origin,
                    list<Location> &poisDestList, list<Location> &facilitiesCenters,
                    list<Location> &fullSolutionPath);


    void init();

};

#endif// GENITIC_ORDER_BUILDER_H
