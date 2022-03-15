#ifndef GeneticPathFinder__H
#define GeneticPathFinder__H

#include <vector>
#include <list>
#include "Location.h"
#include "Chromosome.h"
#include "SwitchFloorObj.h"
#include <stdio.h>
#include <sstream>
#include <istream>
#include <fstream>
#include <stdlib.h>
#include <map>

using namespace std;
typedef list<Location> TYPE_FloorPoisList;

class GeneticPathFinder {

private:

    int Initial_population;
    int Mating_population;
    int Favoured_population;
    int poisCount;
    int cut_length;

    double Mutation_probability;
    double loops;
    int Epoch;
    double min_cost;
    double costPerfect;

    //string appDirPath;


    vector<Location> poisLocationsList;
    vector<Chromosome> chromosomes;
    list<SwitchFloorObj> switchFloor;
    Location originLocation;
    list<Location> poisToNavigateList;

    map<int, TYPE_FloorPoisList> poiToFloorMap;
    vector<Location> externalPois;
    vector<int> floorsInNavPath;

    void geneticCalculation(list<Location> &solution);

    void getPoisInNavPathOrder(Location &origin, list<Location> &poisInSameFloorDestList,
                               list<Location> &solution);

    void loadSwitchFloor(const string &ofile);

    void loadPoisMapToFloor();

    void findClosestSwitchFloor(Location &loc, SwitchFloorObj &sw, int nextFloor);

    void findClosestCord(Location &loc, list<Location> &poisExitsList, Location &closestCord);

public:

    static const GeneticPathFinder Null;

    GeneticPathFinder() :
            Initial_population(800) {

    }

    void getPoisOrder(string switchFloorFilePath, int generationsCount, Location &origin,
                      list<Location> &poisDestList, list<Location> &solution);

    void getMixedPoisOrder(string switchFloorFilePath, int generationsCount, Location &origin,
                           list<Location> &poisDestList, list<Location> &poisExitsList,
                           list<Location> &solution);

    void setInitialPopulation(int Initial_population) {
        this->Initial_population = Initial_population;
    }

    //void setSwitchFloorPath(const string &ofilePath) {
    //		this->appDirPath = ofilePath;
    //	}



    void Sort_chromosomes(int num);

    //void clean();

    void getOutdoorOrder(int generationsCount, Location &origin, list<Location> &poisDestList,
                         list<Location> &fullSolutionPath);


};

#endif //GeneticPathFinder__H
