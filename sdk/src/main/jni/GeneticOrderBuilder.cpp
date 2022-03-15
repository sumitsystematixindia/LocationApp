#include "GeneticOrderBuilder.h"

GeneticOrderBuilder::GeneticOrderBuilder() {
    poiToFacilityMap.clear();
    //centerToFacilityMap.clear();
    externalPois.clear();
    centersList.clear();
    destsList.clear();

    applicationDir = "";
    projectId = "";
    campusId = "";
    genCount = 0;
}


GeneticOrderBuilder::~GeneticOrderBuilder() {
}


void GeneticOrderBuilder::init() {

    poiToFacilityMap.clear();
    externalPois.clear();
    //centerToFacilityMap.clear();

    for (list<Location>::iterator it = destsList.begin();
         it != destsList.end(); it++) {

        Location &poi = *it;

        if (poi.isInternal()) {  // load poi according to its facility
            string facid = poi.getFacilityId();

            if (poiToFacilityMap.find(facid) == poiToFacilityMap.end()) {
                TYPE_LOCATIONS_LIST plist;
                plist.push_back(poi);
                poiToFacilityMap[facid] = plist;
            } else {
                map<string, TYPE_LOCATIONS_LIST>::iterator it;
                it = poiToFacilityMap.find(facid);
                TYPE_LOCATIONS_LIST &plist = it->second;
                plist.push_back(poi);
                poiToFacilityMap[facid] = plist;
            }

        } else {
            // external poi
            externalPois.push_back(poi);
        }
    }


//	for (list<Location>::iterator it = centersList.begin();
//				it != centersList.end(); it++) {
//			Location & center = *it;
//			center.isFacilityCenter = 1;
//			string facid =  center.getFacilityId();
//			centerToFacilityMap[facid] = center;
//	}



}

void GeneticOrderBuilder::buildOrder(string appDir, string projectid, string campusid,
                                     int generationsCount, Location &origin,
                                     list<Location> &poisDestList,
                                     list<Location> &facilitiesCenters,
                                     list<Location> &fullSolutionPath) {

    try {

        destsList = poisDestList;
        centersList = facilitiesCenters;
        projectId = projectid;
        campusId = campusid;
        genCount = generationsCount;
        originLocation = origin;
        applicationDir = appDir;
        init();

        if (originLocation.isInternal()) { // origin is indoor

            //make copy
            Location originLoc;
            originLoc.x = originLocation.x;
            originLoc.y = originLocation.y;
            originLoc.z = originLocation.z;
            originLoc.type = originLocation.type;
            originLoc.lat = originLocation.lat;
            originLoc.lon = originLocation.lon;
            originLoc.x = originLocation.lat;
            originLoc.y = originLocation.lon;
            originLoc.facilityId = originLocation.facilityId;
            originLoc.isFacilityCenter = originLocation.isFacilityCenter;

            // check if exists dest from origin facility
            string originFacId = originLocation.facilityId;
            std::map<string, std::list<Location> >::iterator facMapItr = poiToFacilityMap.find(
                    originFacId);
            if (facMapItr == poiToFacilityMap.end()) {
                mergeOrder(fullSolutionPath, originLoc);
            }
            else {
                // make order for pois in origin facility
                list<Location> poislist = facMapItr->second;
                if (poislist.size() > 0) {
                    string switchFloorFilePath =
                            applicationDir + "/" + projectId + "/" + campusId + "/" + originFacId +
                            "/" + "switchfloor.txt";
                    Location oLoc;
                    oLoc.x = origin.x;
                    oLoc.y = origin.y;
                    oLoc.z = origin.z;
                    oLoc.type = origin.type;
                    oLoc.lat = origin.lat;
                    oLoc.lon = origin.lon;
                    oLoc.x = origin.lat;
                    oLoc.y = origin.lon;
                    oLoc.facilityId = origin.facilityId;
                    oLoc.isFacilityCenter = origin.isFacilityCenter;
                    list<Location> orderSolOfFacility;
                    GeneticPathFinder orderFinder;
                    orderFinder.getPoisOrder(switchFloorFilePath, genCount, oLoc, poislist,
                                             orderSolOfFacility);

                    //XXX debug only
//						for (list<Location>::iterator itr = orderSolOfFacility.begin(); itr != orderSolOfFacility.end(); itr++) {
//									Location & destLocation = *itr;
//									__android_log_print(ANDROID_LOG_ERROR, "orderSolOfFacility --> GeneticOrderBuilder::buildOrder",
//																		"(x :%f, y :%f, z :%f, lat:%f , lon:%f, type: %d, facid: %s)",
//																		destLocation.x, destLocation.y,destLocation.z,
//																		destLocation.lat, destLocation.lon,
//																		destLocation.type, destLocation.facilityId.c_str());
//						}


                    std::list<Location>::iterator itsol = fullSolutionPath.end();
                    fullSolutionPath.insert(itsol, orderSolOfFacility.begin(),
                                            orderSolOfFacility.end());

                }
                // make order for the rest of facilities if exists
                Location centerOriginLoc;
                centerOriginLoc.isFacilityCenter = 1;
                centerOriginLoc.facilityId = originFacId;
                list<Location> orderMergeSol;
                mergeOrder(orderMergeSol, centerOriginLoc);

                //XXX debug only
//					for (list<Location>::iterator itr = orderMergeSol.begin(); itr != orderMergeSol.end(); itr++) {
//								Location & destLocation = *itr;
//								__android_log_print(ANDROID_LOG_ERROR, "orderMergeSol --> GeneticOrderBuilder::buildOrder",
//																	"(x :%f, y :%f, z :%f, lat:%f , lon:%f, type: %d, facid: %s)",
//																	destLocation.x, destLocation.y,destLocation.z,
//																	destLocation.lat, destLocation.lon,
//																	destLocation.type, destLocation.facilityId.c_str());
//					}

                // combine order

                std::list<Location>::iterator itsol = fullSolutionPath.end();
                fullSolutionPath.insert(itsol, orderMergeSol.begin(), orderMergeSol.end());
            }
        }
        else { // origin is outdoor

            Location centerOriginLoc;
            centerOriginLoc.isFacilityCenter = -100;
            centerOriginLoc.facilityId = "";
            mergeOrder(fullSolutionPath, centerOriginLoc);
        }


    } catch (exception e) {
        if (poisDestList.size() > 0) {
            fullSolutionPath.clear();
            std::list<Location>::iterator it = fullSolutionPath.end();
            fullSolutionPath.insert(it, poisDestList.begin(), poisDestList.end());
        }
    }
}


void GeneticOrderBuilder::mergeOrder(list<Location> &fullSolutionPath,
                                     Location &originFacilityLocation) {


    list<Location> centersAndOutdoorDestList;
    centersAndOutdoorDestList.clear();

    // combine external dests and centers of all facilities
    if (externalPois.size() > 0) {
        for (list<Location>::iterator extIt = externalPois.begin();
             extIt != externalPois.end(); extIt++) {
            Location &destLocation = *extIt;
            centersAndOutdoorDestList.push_back(destLocation);
//						__android_log_print(ANDROID_LOG_ERROR, "externalPois --> GeneticOrderBuilder::mergeOrder",
//																							"(x :%f, y :%f, z :%f, lat:%f , lon:%f, type: %d, facid: %s)",
//																							destLocation.x, destLocation.y,destLocation.z,
//																							destLocation.lat, destLocation.lon,
//																							destLocation.type, destLocation.facilityId.c_str());
        }
    }

    if (centersList.size() > 0) {
        for (list<Location>::iterator centerIt = centersList.begin();
             centerIt != centersList.end(); centerIt++) {
            Location &destLocation = *centerIt;
            string originInFacility = originFacilityLocation.facilityId;
            string centerFacility = destLocation.facilityId;

            if (strcmp(originInFacility.c_str(), centerFacility.c_str()) != 0) {
                destLocation.isFacilityCenter = 1;
                centersAndOutdoorDestList.push_back(destLocation);
//							__android_log_print(ANDROID_LOG_ERROR, "centersList --> GeneticOrderBuilder::mergeOrder",
//																	"(x :%f, y :%f, z :%f, lat:%f , lon:%f, type: %d, facid: %s)",
//																	destLocation.x, destLocation.y,destLocation.z,
//																	destLocation.lat, destLocation.lon,
//																	destLocation.type, destLocation.facilityId.c_str());
            }
        }
    }

    Location startOrigin = originLocation;
    if (originFacilityLocation.isFacilityCenter != -100) {
        startOrigin = originFacilityLocation;
    }

    // order centers and outdoor dests
    list<Location> orderFailitiesList;
    GeneticPathFinder orderFinder1;
    orderFinder1.getOutdoorOrder(genCount, startOrigin, centersAndOutdoorDestList,
                                 orderFailitiesList);

    vector<Location> orderCenteresVector;
    for (list<Location>::iterator itr = orderFailitiesList.begin();
         itr != orderFailitiesList.end(); ++itr) {
        Location &cntr = *itr;
        orderCenteresVector.push_back(cntr);
//				__android_log_print(ANDROID_LOG_ERROR, "orderCenteresVector --> GeneticOrderBuilder::mergeOrder",
//										"(x :%f, y :%f, z :%f, lat:%f , lon:%f, type: %d, facid: %s)",
//										cntr.x, cntr.y,cntr.z,
//										cntr.lat, cntr.lon,
//										cntr.type, cntr.facilityId.c_str());
    }

    Location prevOrigin;
    list<Location> facOrderSolution;
    for (int i = 0; i < orderCenteresVector.size(); i++) {

        if (i == 0) {
            prevOrigin = startOrigin;
        }

        GeneticPathFinder orderFinder;
        facOrderSolution.clear();
        string facId = orderCenteresVector[i].facilityId;
        int isFacCenter = orderCenteresVector[i].isFacilityCenter;
        if (isFacCenter == 1) {
            std::map<string, std::list<Location> >::iterator facMapItr = poiToFacilityMap.find(
                    facId);
            if (facMapItr != poiToFacilityMap.end()) {
                list<Location> poislist = facMapItr->second;
                if (poislist.size() > 0) {
                    string switchFloorFilePath =
                            applicationDir + "/" + projectId + "/" + campusId + "/" + facId + "/" +
                            "switchfloor.txt";
                    // make copy
                    Location oLoc;
                    oLoc.x = prevOrigin.x;
                    oLoc.y = prevOrigin.y;
                    oLoc.z = prevOrigin.z;
                    oLoc.type = prevOrigin.type;
                    oLoc.lat = prevOrigin.lat;
                    oLoc.lon = prevOrigin.lon;
                    oLoc.x = prevOrigin.lat;
                    oLoc.y = prevOrigin.lon;
                    oLoc.facilityId = prevOrigin.facilityId;
                    oLoc.isFacilityCenter = prevOrigin.isFacilityCenter;
                    orderFinder.getPoisOrder(switchFloorFilePath, genCount, oLoc, poislist,
                                             facOrderSolution);
                    std::list<Location>::iterator itsol = fullSolutionPath.end();
                    fullSolutionPath.insert(itsol, facOrderSolution.begin(),
                                            facOrderSolution.end());
                }
            }
        }
        else {
//					__android_log_print(ANDROID_LOG_ERROR, "GeneticOrderBuilder::mergeOrder",
//						"outdoor dest (x :%f, y :%f, z :%f, lat:%f , lon:%f, type: %d, facid: %s)",
//						orderCenteresVector[i].x, orderCenteresVector[i].y, orderCenteresVector[i].z,
//						orderCenteresVector[i].lat, orderCenteresVector[i].lon,
//						orderCenteresVector[i].type, orderCenteresVector[i].facilityId.c_str());
            fullSolutionPath.push_back(orderCenteresVector[i]);
        }

        prevOrigin = orderCenteresVector[i];

    }
}




