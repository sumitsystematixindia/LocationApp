/*
 * HalfNavLocFinder.cpp
 *
 *      Author: Owner
 */

#include "HalfNavLocFinder.h"

#include "rapidjson/document.h"

#include <algorithm>

#ifdef __APPLE__
#else

#endif

bool HalfNavLocFinder::instanceFlag = false;

HalfNavLocFinder *HalfNavLocFinder::single = NULL;


HalfNavLocFinder::HalfNavLocFinder() {

    MAX_LEVEL = -50;

    ITERATIONS = 10;
    TOPK = 4;
    DECAY_STEP = 1.0;
    INIT_DECAY_PERCENT = 1.0;

    //XXX new open places alg params =====
    beaconsCountForInitOpenSpaceLocation = -1;
    thresholdForUpdateOpenSpaceLocation = -1;
    useOpenSpaceAlg = false;
    isFirstOpenSpaceRun = true;
    currentOpenSpaceLoc.x = -1;
    currentOpenSpaceLoc.y = -1;
    // ===== new open places alg params =====

}

HalfNavLocFinder::~HalfNavLocFinder() {
    instanceFlag = false;
    single = NULL;
}

HalfNavLocFinder *HalfNavLocFinder::getInstance() {
    if (!instanceFlag) {
        single = new HalfNavLocFinder();
        instanceFlag = true;
        return single;
    } else {
        return single;
    }
}

void HalfNavLocFinder::releaseInstance() {
    if (instanceFlag) {
        delete single;
        instanceFlag = false;
        single = NULL;
    }
}

void HalfNavLocFinder::initData() {
    blipsList.clear();

}


void HalfNavLocFinder::loadSettings(string filePath) {

    MAX_LEVEL = -50;

    string ofile = filePath + "/" + "half_nav_settings.json";

    // reset data
    lastLoc.x = -1.0f;
    lastLoc.y = -1.0f;
    lastLoc.z = -1.0f;
    beaconsMap.clear();
    floors.clear();
    IS_USE_HALF_NAV_ALG = false;
    currentFloor = -999;


    //XXX new open places alg params =====
    beaconsCountForInitOpenSpaceLocation = -1;
    thresholdForUpdateOpenSpaceLocation = -1;
    useOpenSpaceAlg = false;
    isFirstOpenSpaceRun = true;
    currentOpenSpaceLoc.x = -1;
    currentOpenSpaceLoc.y = -1;
    // ===== new open places alg params =====

    FILE *jsonFile = fopen(ofile.c_str(), "r");

    if (jsonFile == NULL) {
        //IS_ALG_INITIALIZED = false;
        return;
    }


    fseek(jsonFile, 0L, SEEK_END);
    long jsonLength = ftell(jsonFile);
    fseek(jsonFile, 0L, SEEK_SET);

    char *jsonText = new char[jsonLength + 1];
    jsonText[jsonLength] = 0;

    fread(jsonText, sizeof(char), jsonLength, jsonFile);
    fclose(jsonFile);
    rapidjson::Document d;
    d.Parse<0>(jsonText);
    delete[] jsonText;
    if (d.HasParseError()) {
        printf("JSON Parse Error: %s", d.GetParseError());
        return;
    }


    if (d.HasMember("iterations")) {

        ITERATIONS = (float) d["iterations"].GetInt();
        //__android_log_print(ANDROID_LOG_INFO, "HalfNavLocFinder::loadSettings", "ITERATIONS %f",ITERATIONS);

    }

    if (d.HasMember("topk")) {
        TOPK = (float) d["topk"].GetInt();
        //__android_log_print(ANDROID_LOG_INFO, "HalfNavLocFinder::loadSettings", "TOPK %f",TOPK);
    }

    if (d.HasMember("decay_step")) {
        DECAY_STEP = (float) d["decay_step"].GetDouble();
        //__android_log_print(ANDROID_LOG_INFO, "HalfNavLocFinder::loadSettings", "DECAY_STEP %f",DECAY_STEP);
    }

    if (d.HasMember("init_decay_percent")) {
        INIT_DECAY_PERCENT = (float) d["init_decay_percent"].GetDouble();
        INIT_DECAY_PERCENT = 1.0 - INIT_DECAY_PERCENT;
        //__android_log_print(ANDROID_LOG_INFO, "HalfNavLocFinder::loadSettings", "INIT_DECAY_PERCENT %f",INIT_DECAY_PERCENT);
    }

    if (d.HasMember("detect_floor_top_k")) {
        DETECT_FLOOR_TOP_K = (float) d["detect_floor_top_k"].GetInt();
        // __android_log_print(ANDROID_LOG_INFO, "HalfNavLocFinder::loadSettings", "DETECT_FLOOR_TOP_K %f",DETECT_FLOOR_TOP_K);
    }

    if (d.HasMember("openspace_init_beacons_count")) {
        beaconsCountForInitOpenSpaceLocation = (int) d["openspace_init_beacons_count"].GetInt();
        //__android_log_print(ANDROID_LOG_ERROR, "HalfNavLocFinder::loadSettings", "beaconsCountForInitOpenSpaceLocation %d",beaconsCountForInitOpenSpaceLocation);
    }

    if (d.HasMember("openspace_updateloc_thr")) {
        thresholdForUpdateOpenSpaceLocation = (float) d["openspace_updateloc_thr"].GetDouble();
        //__android_log_print(ANDROID_LOG_ERROR, "HalfNavLocFinder::loadSettings", "thresholdForUpdateOpenSpaceLocation %f",thresholdForUpdateOpenSpaceLocation);
    }

    if (d.HasMember("openspace_use")) {
        useOpenSpaceAlg = (bool) d["openspace_use"].GetBool();
        // __android_log_print(ANDROID_LOG_ERROR, "HalfNavLocFinder::loadSettings", "useOpenSpaceAlg %d",useOpenSpaceAlg);
    }


    if (!d["beacons_location"].IsNull()) {

        const rapidjson::Value &settings = d["beacons_location"];
        // __android_log_print(ANDROID_LOG_ERROR, "HalfNavLocFinder::loadSettings", "beacons_location size %d",settings.Size());

        for (int i = 0; i < settings.Size(); i++) {
            const rapidjson::Value &bec = settings[i];
            Beacon beacon;
            beacon.BSSID = bec["id"].GetString();
            beacon.x = (float) bec["x"].GetDouble();
            beacon.y = (float) bec["y"].GetDouble();
            beacon.z = bec["floor"].GetInt();
            //	__android_log_print(ANDROID_LOG_INFO, "HalfNavLocFinder::loadSettings", "bssid %s, x = %f, y= %f, z=%d" ,beacon.BSSID.c_str(), beacon.x, beacon.y, beacon.z);
            beaconsMap[beacon.BSSID] = beacon;
            floors.insert(beacon.z);
        }

        IS_USE_HALF_NAV_ALG = true;

    }


}


void HalfNavLocFinder::updateStatus(int floor) {
    IS_USE_HALF_NAV_ALG = floors.find(floor) != floors.end();
    if (IS_USE_HALF_NAV_ALG) {
        currentFloor = floor;
    }
}

bool HalfNavLocFinder::isStatusOK() {
    return IS_USE_HALF_NAV_ALG;
}

/*
void HalfNavLocFinder::initParams(list<Beacon>& beacons, int iterations, int topk,  float decayStep,float initDecayPecent) {
    
    
    ITERATIONS = iterations;
    TOPK = topk;
    
    DECAY_STEP = decayStep;
    
    INIT_DECAY_PERCENT = 1.0 - initDecayPecent;
    
    // create  copy of the beacons
    beaconsMap.clear();
    for (list<Beacon>::iterator it = beacons.begin(); it != beacons.end(); it++) {
        
        Beacon & beaconData = *it;
        Beacon temp;
        temp.BSSID = beaconData.BSSID;
        temp.level = beaconData.level;
        temp.x=beaconData.x;
        temp.y=beaconData.y;
        temp.z=beaconData.z;
        beaconsMap[temp.BSSID]= temp;
    }
    
    
    lastLoc.x=-1.0f;
    lastLoc.y=-1.0f;
    lastLoc.z=-1.0f;
    IS_USE_HALF_NAV_ALG = false;
    
}
*/

/*
void HalfNavLocFinder::getInitialAveragePoint(Location& loc) {
    
    float sumx = 0;
    float sumy = 0;
    int count = 0;
    int i =0;
    
    //    if(lastLoc.x!=-1.0f && lastLoc.y!=-1.0f){
    //    	 loc.x = lastLoc.x;
    //    	 loc.y= lastLoc.y;
    //    }
    //   else
    //    {
    //
    //		float min = 1;
    //		float max = 1000;
    //		float r = (float)rand() / (float)RAND_MAX;
    //		float xy= min + r * (max - min);
    //
    //		loc.x = xy;
    //		loc.y= xy;
    //
    //		lastLoc.x = loc.x;
    //		lastLoc.y= loc.y;
    
    // initilaize with no location state
    loc.x = -1;
    loc.y= -1;
    
    if (blipsList.size()>0) {
        //__android_log_print(ANDROID_LOG_INFO, "NdkHalfNavLocFinder_getInitialAveragePoint : size", "(%d)", blipsList.size() );
        
        for (list<WlBlip>::iterator it = blipsList.begin(); it != blipsList.end(); it++) {
            
            if(i< TOPK){
                
                WlBlip & blipData = *it;
                string bssid= blipData.BSSID;
                
                if (beaconsMap.find(bssid) == beaconsMap.end()) {
                    continue;
                }
                
                
                
                Beacon& beacon = beaconsMap[bssid];
                
                if(beacon.z != currentFloor){
                    continue;
                }
                
                sumx += beacon.x;
                sumy += beacon.y;
                
                //__android_log_print(ANDROID_LOG_INFO, "NdkHalfNavLocFinder_getInitialAveragePoint : ", "(%s , %f, %f)", bssid.c_str(), beacon.x, beacon.y);
                
                count++;
            }
            
            i++;
        }
        
        
        float ax = 0;
        float ay = 0;
        
        if (count > 0) {
            ax = sumx / ((float)count);
            ay = sumy / ((float)count);
        }
        
        // no location can be computed
        if(ax!=0 && ay!=0){
            loc.x=ax;
            loc.y=ay;
            lastLoc.y=ay;
            lastLoc.x=ay;
        }
    }
    //}
    
    
    // __android_log_print(ANDROID_LOG_INFO, "NdkHalfNavLocFinder_getInitialAveragePoint : ", "(%f, %f)",  loc.x, loc.y);
    
}
**/

/**
 void HalfNavLocFinder::findLocation(list<WlBlip>& blips,Location &loc) {
 
 
 
 // no loc
 if (blips.size()==0) {
 loc.x= -1;
 loc.y=-1;
 return;
 }
 
 initData();
 
 // create temporary copy of the blips and sort them
 for (list<WlBlip>::iterator it = blips.begin(); it != blips.end(); it++) {
 
 WlBlip & blipData = *it;
 
 // use only the beacons that registered for half nav
 if (beaconsMap.find(blipData.BSSID) == beaconsMap.end()) {
 continue;
 }
 
 WlBlip temp;
 temp.BSSID = blipData.BSSID;
 temp.SSID = blipData.SSID;
 temp.frequency=blipData.frequency;
 temp.level = blipData.level;
 temp.timestamp = blipData.timestamp;
 blipsList.push_back(temp);
 }
 
 // no beacons were scanned ==> // no loc
 if(blipsList.size()==0){
 loc.x= -1;
 loc.y=-1;
 return;
 }
 
 // sort if number of blips greater than topK otherwise no need to sort
 if(blipsList.size() > TOPK){
 blipsList.sort(compareWlBlip);
 }
 
 //			// debug
 //			for (list<WlBlip>::iterator it = blipsList.begin(); it != blipsList.end(); it++) {
 //
 //					WlBlip & blipData = *it;
 //					WlBlip temp;
 //					temp.BSSID = blipData.BSSID;
 //					temp.level = blipData.level;
 //
 //					__android_log_print(ANDROID_LOG_INFO, "HalfNavLocFinder::findLocation:blipsList","BSSID :%s  level :%d", temp.BSSID.c_str(),temp.level);
 //			}
 
 //compute initial starting point
 Location locPoint;
 getInitialAveragePoint(locPoint);
 //			__android_log_print(ANDROID_LOG_INFO, "NdkHalfNavLocFinder_findLocation :init locPoint ", "(%f , %f)", locPoint.x, locPoint.y);
 
 // no loc found
 if(locPoint.x==-1 && locPoint.y==-1){
 return;
 }
 
 float nextDecayStep = INIT_DECAY_PERCENT;
 
 for(int i=0; i < ITERATIONS; i++)
 {
 getNextPoint(locPoint, nextDecayStep);
 nextDecayStep = nextDecayStep * DECAY_STEP;
 }
 
 loc.x = locPoint.x;
 loc.y = locPoint.y;
 
 lastLoc.y=loc.x;
 lastLoc.x=loc.y;
 
 }
 */


void HalfNavLocFinder::findLocation(list<WlBlip> &blips, Location &loc) {

    //XXX functions for open spaces alg =============
    if (useOpenSpaceAlg == true) {
        findOpenSpaceLocation(blips, loc);
        return;
    }
    //==== functions for open spaces alg =============

    // no blips
    if (blips.size() == 0) {
        loc.x = -1;
        loc.y = -1;
        return;
    }

    list<Beacon> topklist;

    // create temporary copy of the blips and sort them
    for (list<WlBlip>::iterator it = blips.begin(); it != blips.end(); it++) {

        WlBlip &blipData = *it;

        // use only the beacons that registered for half nav
        if (beaconsMap.find(blipData.BSSID) == beaconsMap.end()) {
            continue;
        }


        int level = blipData.level;

        if (beaconsMap[blipData.BSSID].z == currentFloor) {
            Beacon beacon;
            beacon.BSSID = blipData.BSSID;
            beacon.level = level;
            beacon.x = beaconsMap[blipData.BSSID].x;
            beacon.y = beaconsMap[blipData.BSSID].y;
            topklist.push_back(beacon);

        }


    }




    // no beacons were scanned ==> // no loc
    if (topklist.size() == 0) {
        loc.x = -1;
        loc.y = -1;
        return;
    }

    // sort if number of blips greater than topK otherwise no need to sort
    if (topklist.size() > TOPK) {
        topklist.sort(compareBeacon);
    }

    //compute WeightAvgLoc
    getWeightAvgLocation(topklist, loc);


}


void HalfNavLocFinder::getWeightAvgLocation(list<Beacon> &topklist, Location &loc) {

    loc.x = -1;
    loc.y = -1;


    if (topklist.size() >= 2) {


        // compute average
        list<Beacon>::iterator it = topklist.begin();
        Beacon &A = *it;
        it++;
        Beacon &B = *it;

        // if one of the beacon have level above -40 then take it as the current location
        if (A.level >= MAX_LEVEL) {
            loc.x = A.x;
            loc.y = A.y;
        }
        else if (B.level >= MAX_LEVEL) {
            loc.x = B.x;
            loc.y = B.y;
        }
        else { // otherwise compute weighted average
            double levelA = A.level - MAX_LEVEL;
            double levelB = B.level - MAX_LEVEL;

            double base = levelA + levelB;
            double levelRateA = levelA / base;
            double percentA = 1.0 - levelRateA;

            double levelRateB = levelB / base;
            double percentB = 1.0 - levelRateB;

            loc.x = percentA * A.x + percentB * B.x;

            loc.y = percentA * A.y + percentB * B.y;


            //__android_log_print(ANDROID_LOG_INFO, "HalfNavLocFinder::getWeightAvg (ssid, level, levelRate, percent) ==> loc", "(%s ,  %d, %f , %f) ==> (%f ,%f)", bssid.c_str(), level,  levelRate, percent, nextLoc.x,nextLoc.y);

        }
        //__android_log_print(ANDROID_LOG_INFO, "HalfNavLocFinder::getWeightAvgLocation ==> loc", "(%s: %d,%s: %d) ==> (%f, f%)", A.BSSID.c_str(),A.level, B.BSSID.c_str(), B.level, loc.x, loc.y);



    }

}

/**
void HalfNavLocFinder::getNextPoint(Location& loc, float nextDecayStep) {
    
    Location nextLoc;
    nextLoc.x = loc.x;
    nextLoc.y = loc.y;
    
    double MIN_LEVEL = -140.0;
    // double decayK = 0.25;
    //double decayPow = 2;
    
    int i = 0;
    //int minThr = -100;
    
    for (list<WlBlip>::iterator it = blipsList.begin(); it != blipsList.end(); it++) {
        
        if(i < TOPK){
            
            WlBlip & blipData = *it;
            string bssid= blipData.BSSID;
            
            if (beaconsMap.find(bssid) == beaconsMap.end()) {
                continue;
            }
            
            int level =blipData.level;
            
            //				if(level < minThr){
            //					continue;
            //				}
            
            Beacon& beacon = beaconsMap[bssid];
            
            if(beacon.z != currentFloor){
                continue;
            }
            
            Location beaconLoc;
            beaconLoc.x = beacon.x;
            beaconLoc.y = beacon.y;
            
            //double dist = distance(beaconLoc, nextLoc);
            
            double levelRate= ((double)level)/MIN_LEVEL;
            
            double percent = 1.0 - levelRate; //exp(decayK - (decayK / (1.0 - pow(levelRate,decayPow))));
            
            
            percent = percent * nextDecayStep;
            
            if(percent<0){
                percent = 0;
            }
            
            //double moveX= (nextLoc.x - beaconLoc.x) * percent;
            //double moveY= (nextLoc.y - beaconLoc.y) * percent;
            
            
            
            nextLoc.x = (1.0f - percent)  * nextLoc.x + percent * beaconLoc.x;
            
            nextLoc.y = (1.0f - percent) * nextLoc.y +  percent * beaconLoc.y;
            
            
            //			   __android_log_print(ANDROID_LOG_INFO, "HalfNavLocFinder::getNextPoint (ssid, level, decayStep, levelRate, percent) ==> loc", "(%s ,  %d, %f , %f , %f) ==> (%f ,%f)", bssid.c_str(), level, nextDecayStep,  levelRate, percent, nextLoc.x,nextLoc.y);
            
            
            //nextLoc.x = nextLoc.x + moveX;
            //nextLoc.y = nextLoc.y + moveY;
            
        }
        i++;
        
    }
    
    // update next loc
    loc.x = nextLoc.x;
    loc.y = nextLoc.y;
    
}
*/


int HalfNavLocFinder::getFloorByBlipsWithoutMatrix(list<WlBlip> &blips) {

    int result = -100;

    if (blips.size() == 0) {
        return result;
    }


    list<WlBlip> tempBlipsList;


    // create temporary copy of the blips and sort them
    for (list<WlBlip>::iterator it = blips.begin(); it != blips.end(); it++) {

        WlBlip &blipData = *it;

        // use only the beacons that registered for half nav
        if (beaconsMap.find(blipData.BSSID) == beaconsMap.end()) {
            continue;
        }

        WlBlip temp;
        temp.BSSID = blipData.BSSID;
        temp.SSID = blipData.SSID;
        temp.frequency = blipData.frequency;
        temp.level = blipData.level;
        temp.timestamp = blipData.timestamp;
        tempBlipsList.push_back(temp);
    }

    // no beacons were scanned ==> // no floor
    if (tempBlipsList.size() == 0) {
        return result;
    }

    // sort if number of blips greater than topK otherwise no need to sort
    if (tempBlipsList.size() > DETECT_FLOOR_TOP_K) {
        tempBlipsList.sort(compareWlBlip);
    }


    // debug
    //			for (list<WlBlip>::iterator it = tempBlipsList.begin(); it != tempBlipsList.end(); it++) {
    //
    //					WlBlip & blipData = *it;
    //					WlBlip temp;
    //					temp.BSSID = blipData.BSSID;
    //					temp.level = blipData.level;
    //
    //					__android_log_print(ANDROID_LOG_INFO, "HalfNavLocFinder:getFloorByBlipsWithoutMatrix","BSSID :%s  level :%d", temp.BSSID.c_str(),temp.level);
    //			}

    // compute frequencies of floors
    map<int, FloorCandidate> floorCandMap;
    int i = 0;
    for (list<WlBlip>::iterator it = tempBlipsList.begin(); it != tempBlipsList.end(); it++) {

        if (i < DETECT_FLOOR_TOP_K) {

            WlBlip &blipData = *it;
            string bssid = blipData.BSSID;

            //int level =blipData.level;

            Beacon &beacon = beaconsMap[bssid];

            int z = beacon.z;


            if (floorCandMap.find(z) == floorCandMap.end()) {
                FloorCandidate floorCand;
                floorCand.floor = z;
                floorCand.freq = 1;
                floorCandMap[z] = floorCand;
            } else {
                FloorCandidate &floorCand = floorCandMap[z];
                floorCand.floor = z;
                floorCand.freq++;
            }

        }
        i++;
    }


    // find max floor freq
    int max = -100;
    int maxFreq = 0;
    for (map<int, FloorCandidate>::iterator it = floorCandMap.begin();
         it != floorCandMap.end(); ++it) {
        FloorCandidate &floorCand = it->second;
        //				__android_log_print(ANDROID_LOG_INFO, "HalfNavLocFinder:getFloorByBlipsWithoutMatrix:floorCandMap"," floorCand floor :%d  level :%d",  floorCand.floor, floorCand.freq);
        int freq = floorCand.freq;
        if (freq > maxFreq) {
            max = it->first;
            maxFreq = freq;
        }
    }

    result = max;
    updateStatus(result);
    return result;
}


double HalfNavLocFinder::distance(const Location &p, const Location &p1) {
    return sqrt((p.x - p1.x) * (p.x - p1.x) + (p.y - p1.y) * (p.y - p1.y));
}

bool HalfNavLocFinder::compareWlBlip(WlBlip first, WlBlip second) {

    if (first.level > second.level)
        return true;
    else if (first.level < second.level)
        return false;
    else
        return true;
}

bool HalfNavLocFinder::compareBeacon(Beacon first, Beacon second) {

    if (first.level > second.level)
        return true;
    else if (first.level < second.level)
        return false;
    else
        return true;
}

//XXX functions for open spaces alg =============

void HalfNavLocFinder::findOpenSpaceLocation(list<WlBlip> &blips, Location &loc) {



    // no blips
    if (blips.size() == 0) {
        loc.x = currentOpenSpaceLoc.x;
        loc.y = currentOpenSpaceLoc.y;
        return;
    }

    list<Beacon> topklist;

    // create temporary copy of the blips and sort them
    for (list<WlBlip>::iterator it = blips.begin(); it != blips.end(); it++) {

        WlBlip &blipData = *it;

        // use only the beacons that registered for half nav
        if (beaconsMap.find(blipData.BSSID) == beaconsMap.end()) {
            continue;
        }


        int level = blipData.level;

        if (beaconsMap[blipData.BSSID].z == currentFloor) {
            Beacon beacon;
            beacon.BSSID = blipData.BSSID;
            beacon.level = level;
            beacon.x = beaconsMap[blipData.BSSID].x;
            beacon.y = beaconsMap[blipData.BSSID].y;
            topklist.push_back(beacon);

        }


    }




    // no beacons were scanned ==> // no loc
    if (topklist.size() == 0) {
        loc.x = currentOpenSpaceLoc.x;
        loc.y = currentOpenSpaceLoc.y;
        return;
    }

    topklist.sort(compareBeacon);

    if (isFirstOpenSpaceRun) {
        computeOpenSpaceInitialLocation(topklist, loc);
        isFirstOpenSpaceRun = false;
    }
    else {
        updateOpenSpaceLocation(topklist, loc);
    }


}


void HalfNavLocFinder::computeOpenSpaceInitialLocation(list<Beacon> &topklist, Location &loc) {

    loc.x = currentOpenSpaceLoc.x;
    loc.y = currentOpenSpaceLoc.y;

    if (topklist.size() >= 2) {

        // compute average
        list<Beacon>::iterator it = topklist.begin();
        Beacon &A = *it;
        it++;
        Beacon &B = *it;

        double levelA = A.level - MAX_LEVEL;
        double levelB = B.level - MAX_LEVEL;

        double base = levelA + levelB;
        double levelRateA = levelA / base;
        double percentA = 1.0 - levelRateA;

        double levelRateB = levelB / base;
        double percentB = 1.0 - levelRateB;

        loc.x = percentA * A.x + percentB * B.x;

        loc.y = percentA * A.y + percentB * B.y;

        currentOpenSpaceLoc.x = loc.x;
        currentOpenSpaceLoc.y = loc.y;

    }

/*
    if(topklist.size()>=2){

        // compute average
        list<Beacon>::iterator it = topklist.begin();
        Beacon & A = *it;
        it++;
        Beacon & B = *it;

        // if one of the beacon have level above thr then take it as the current location
        if(A.level >= MAX_LEVEL){
            loc.x = A.x;
            loc.y = A.y;
            currentOpenSpaceLoc.x = loc.x;
            currentOpenSpaceLoc.y = loc.y;

        }
        else if(B.level >= MAX_LEVEL){
            loc.x = B.x;
            loc.y = B.y;

            currentOpenSpaceLoc.x = loc.x;
            currentOpenSpaceLoc.y = loc.y;

        }
        else{
        	double base = 0;
        	int i=0;
			// otherwise compute weighted average
        	for (list<Beacon>::iterator itr = topklist.begin(); itr != topklist.end(); itr++) {
        			if(i < beaconsCountForInitOpenSpaceLocation){
        				Beacon & bec = *itr;
        				double level = bec.level - MAX_LEVEL;
        				base = base + level;
        				//__android_log_print(ANDROID_LOG_INFO, "HalfNavLocFinder::getWeightAvg (ssid, level, levelRate, percent) ==> loc", "(%s ,  %d, %f , %f) ==> (%f ,%f)", bssid.c_str(), level,  levelRate, percent, nextLoc.x,nextLoc.y);
        			}
        			i++;
        	 }

        	if(base > 0){

        		i = 0;
        		loc.x = 0;
        		loc.y = 0;

				for (list<Beacon>::iterator itr = topklist.begin(); itr != topklist.end(); itr++) {
						if(i < beaconsCountForInitOpenSpaceLocation){
								Beacon & bec = *itr;
								double level = bec.level - MAX_LEVEL;
								double levelRate= level/ base;
								double percent = 1.0 - levelRate;

								loc.x = loc.x + percent * bec.x;

								loc.y = loc.y + percent * bec.y;
						}
						i++;
				}


	            currentOpenSpaceLoc.x = loc.x;
	            currentOpenSpaceLoc.y = loc.y;

        	}
        }
        //__android_log_print(ANDROID_LOG_INFO, "HalfNavLocFinder::getWeightAvgLocation ==> loc", "(%s: %d,%s: %d) ==> (%f, f%)", A.BSSID.c_str(),A.level, B.BSSID.c_str(), B.level, loc.x, loc.y);
    }
*/

}


void HalfNavLocFinder::updateOpenSpaceLocation(list<Beacon> &topklist, Location &loc) {

    loc.x = currentOpenSpaceLoc.x;
    loc.y = currentOpenSpaceLoc.y;

    if (topklist.size() > 0) {

        for (list<Beacon>::iterator itr = topklist.begin(); itr != topklist.end(); itr++) {

            Beacon &bec = *itr;
            double level = bec.level;
            if (level > thresholdForUpdateOpenSpaceLocation) {
                loc.x = bec.x;
                loc.y = bec.y;
                currentOpenSpaceLoc.x = loc.x;
                currentOpenSpaceLoc.y = loc.y;

                return;
            }

        }

    }

}

// =============  functions for open spaces alg =============



