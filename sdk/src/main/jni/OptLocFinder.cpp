/*
 * OptLocFinder.cpp
 *
 *      Author: Owner
 */

#include "OptLocFinder.h"
#include "MatrixBinRep.h"
#include <sstream>
#include <fstream>

#include <sys/stat.h>

#include <algorithm>

#ifdef __APPLE__
#else

#endif

#include "rapidjson/document.h"
#include "HalfNavLocFinder.h"

//#define NEW_FLOOR_SELECTION 1

typedef map<string, double> innerMap;
bool OptLocFinder::instanceFlag = false;

OptLocFinder *OptLocFinder::single = NULL;

void OptLocFinder::setAppDirPath(const string &appDirPath) {
    //OptLocFinder::appDirPath=appDirPath;
}

OptLocFinder::OptLocFinder() {
    FILE_NAME = "scan results/matrix.txt";
    matrixBinFileName = "matrix.bin";
    FILE_BIN_NAME = "scan results/" + matrixBinFileName;

    FILE_FLOOR_BIN_NAME = "floorselection.bin";
    FILE_FLOOR_TXT_NAME = "floorselection.txt";
    FILE_FLOOR_SWITCH_GROUPS_NAME = "floor_groups.json";
    FILE_LOCATION_GROUPS_NAME = "location_groups.json";

    isFirstLoad = false;
    zeroValue = -127.0f;
    LoadedFloor = -100;
    lastAverage.x = -1.0f;
    lastAverage.y = -1.0f;
    lastAverage.z = -1.0f;
    K = 0;
    PIXELS_TO_METER = 0;
    LOCATION_CLOSE_RANGE = 0;
    AVERAGE_RANGE = 0;
    IS_FLOOR_SELECTION = false;
    IS_NEW_FLOOR_SELECTION_ALG = false;

}

OptLocFinder::~OptLocFinder() {
    instanceFlag = false;
    single = NULL;
}

OptLocFinder *OptLocFinder::getInstance() {
    if (!instanceFlag) {
        single = new OptLocFinder();
        instanceFlag = true;
        return single;
    } else {
        return single;
    }
}

void OptLocFinder::releaseInstance() {
    if (instanceFlag) {
        //__android_log_print(ANDROID_LOG_DEBUG, "OptLocFinder::releaseInstance()","OptLocFinder::releaseInstance() %d", 1);
        delete single;
        instanceFlag = false;
        single = NULL;
    }
}

void OptLocFinder::initIndexMap() {
    INDEX_MAP.clear();
    ssidnames.clear();
    theList.clear();
    closePoints.clear();
    mins.clear();
    maxs.clear();
}

const list<AssociativeData> &OptLocFinder::getTheList() {
    return theList;
}

const list<string> &OptLocFinder::getSsidnames() {
    return ssidnames;
}

string OptLocFinder::getFileName() {
    return FILE_NAME;
}

int OptLocFinder::getTheListSize() {
    return (int) theList.size();
}

int OptLocFinder::getSsidnamesSize() {
    return (int) ssidnames.size();
}

void OptLocFinder::initParams(string APPDirPath, int locationCloseRange, int k, float pixelsToMeter,
                              int averageRange,
                              vector<string> &ssidsFitr, int floorscount) {

    FILE_NAME = "scan results/matrix.txt";

    //__android_log_print(ANDROID_LOG_DEBUG, "OptLocFinder::initParams","FILE_NAME :%s", FILE_NAME.c_str());

    matrixBinFileName = "matrix.bin";

    //__android_log_print(ANDROID_LOG_DEBUG, "OptLocFinder::initParams","matrixBinFileName :%s", matrixBinFileName.c_str());

    FILE_BIN_NAME = "scan results/" + matrixBinFileName;

    //__android_log_print(ANDROID_LOG_DEBUG, "OptLocFinder::initParams","FILE_BIN_NAME :%s", FILE_BIN_NAME.c_str());


    FILE_FLOOR_BIN_NAME = "floorselection.bin";
    FILE_FLOOR_TXT_NAME = "floorselection.txt";

    FILE_FLOOR_SWITCH_GROUPS_NAME = "floor_groups.json";
    FILE_LOCATION_GROUPS_NAME = "location_groups.json";

    isFirstLoad = false;

    LoadedFloor = -100;

    appDirPath = APPDirPath;

    //__android_log_print(ANDROID_LOG_DEBUG, "OptLocFinder::initParams","appDirPath :%s", appDirPath.c_str());

    LOCATION_CLOSE_RANGE = locationCloseRange;
//	IS_FLOOR_SELECTION = isfloorSelection;
    K = k;
    PIXELS_TO_METER = pixelsToMeter;
    AVERAGE_RANGE = averageRange;

    ssidfilter = ssidsFitr;

    FLOORS_COUNT = floorscount;


    zeroValue = -127.0f;


    //XXX spatial added
    lastAverage.x = -1.0f;
    lastAverage.y = -1.0f;
    lastAverage.z = -1.0f;

    //XXX added new
    CLOSE_DEVICES_THRESHOLD = -5;
    CLOSE_DEVICES_WEIGHT = 1;
    K_TOP_LEVELS_THR = -1;
    FIRST_TIME_K_TOP_LEVELS_THR = -1;
    LEVEL_LOWER_BOUND = -90;

}


void OptLocFinder::initParams(string APPDirPath, int locationCloseRange, int k, float pixelsToMeter,
                              int averageRange,
                              vector<string> &ssidsFitr, int floorscount, string scantype) {

    FILE_NAME = "scan results/" + scantype + "matrix.txt";

    //__android_log_print(ANDROID_LOG_DEBUG, "OptLocFinder::initParams","FILE_NAME :%s", FILE_NAME.c_str());

    matrixBinFileName = scantype + "matrix.bin";

    //__android_log_print(ANDROID_LOG_DEBUG, "OptLocFinder::initParams","matrixBinFileName :%s", matrixBinFileName.c_str());

    FILE_BIN_NAME = "scan results/" + matrixBinFileName;

    //__android_log_print(ANDROID_LOG_DEBUG, "OptLocFinder::initParams","FILE_BIN_NAME :%s", FILE_BIN_NAME.c_str());


    FILE_FLOOR_BIN_NAME = scantype + "floorselection.bin";
    FILE_FLOOR_TXT_NAME = scantype + "floorselection.txt";
    FILE_FLOOR_SWITCH_GROUPS_NAME = "floor_groups.json";

    FILE_LOCATION_GROUPS_NAME = "location_groups.json";

    isFirstLoad = false;

    LoadedFloor = -100;

    appDirPath = APPDirPath;

    //__android_log_print(ANDROID_LOG_DEBUG, "OptLocFinder::initParams","appDirPath :%s", appDirPath.c_str());

    LOCATION_CLOSE_RANGE = locationCloseRange;
//	IS_FLOOR_SELECTION = isfloorSelection;
    K = k;
    PIXELS_TO_METER = pixelsToMeter;
    AVERAGE_RANGE = averageRange;

    ssidfilter = ssidsFitr;

    FLOORS_COUNT = floorscount;

    zeroValue = -127.0f;


    //XXX spatial added
    lastAverage.x = -1.0f;
    lastAverage.y = -1.0f;
    lastAverage.z = -1.0f;

    //XXX added new
    CLOSE_DEVICES_THRESHOLD = -5;
    CLOSE_DEVICES_WEIGHT = 1;
    K_TOP_LEVELS_THR = -1;
    FIRST_TIME_K_TOP_LEVELS_THR = -1;
    LEVEL_LOWER_BOUND = -90;
}


void OptLocFinder::initParams(string APPDirPath, int locationCloseRange, int k, float pixelsToMeter,
                              int averageRange,
                              vector<string> &ssidsFitr, int floorscount, string scantype,
                              float closeDevicesThreshold, float closeDeviceWeight,
                              int kTopLevelThr) {

//	FILE_NAME = "scan results/"+scantype+"matrix.txt";
//
//	//__android_log_print(ANDROID_LOG_DEBUG, "OptLocFinder::initParams","FILE_NAME :%s", FILE_NAME.c_str());
//
//	matrixBinFileName = scantype+"matrix.bin";
//
//	//__android_log_print(ANDROID_LOG_DEBUG, "OptLocFinder::initParams","matrixBinFileName :%s", matrixBinFileName.c_str());
//
//	FILE_BIN_NAME = "scan results/" + matrixBinFileName;
//
//	//__android_log_print(ANDROID_LOG_DEBUG, "OptLocFinder::initParams","FILE_BIN_NAME :%s", FILE_BIN_NAME.c_str());
//
//
//	FILE_FLOOR_BIN_NAME=scantype+"floorselection.bin";
//	FILE_FLOOR_TXT_NAME=scantype+"floorselection.txt";
//
//
//
//	isFirstLoad = false;
//
//	LoadedFloor = -100;
//
//	appDirPath = APPDirPath;
//
//	//__android_log_print(ANDROID_LOG_DEBUG, "OptLocFinder::initParams","appDirPath :%s", appDirPath.c_str());
//
//	LOCATION_CLOSE_RANGE = locationCloseRange;
////	IS_FLOOR_SELECTION = isfloorSelection;
//	K = k;
//	PIXELS_TO_METER = pixelsToMeter;
//	AVERAGE_RANGE = averageRange;
//
//	ssidfilter = ssidsFitr;
//
//	FLOORS_COUNT=floorscount;
//
//	zeroValue = -127.0f;
//
//
//	  //XXX spatial added
//	lastAverage.x=-1.0f;
//	lastAverage.y=-1.0f;
//	lastAverage.z=-1.0f;
//
//	CLOSE_DEVICES_THRESHOLD=closeDevicesThreshold;
//	CLOSE_DEVICES_WEIGHT=closeDeviceWeight;
//	K_TOP_LEVELS_THR=kTopLevelThr;
//	LEVEL_LOWER_BOUND = -90;

    int levelLowerBound = -90; //default value - ignored for normal find location;
    initParams(APPDirPath, locationCloseRange, k, pixelsToMeter, averageRange,
               ssidsFitr, floorscount, scantype, closeDevicesThreshold, closeDeviceWeight,
               kTopLevelThr, levelLowerBound);

}


void OptLocFinder::initParams(string APPDirPath, int locationCloseRange, int k, float pixelsToMeter,
                              int averageRange,
                              vector<string> &ssidsFitr, int floorscount, string scantype,
                              float closeDevicesThreshold, float closeDeviceWeight,
                              int kTopLevelThr, int levelLowerBound) {

//	FILE_NAME = "scan results/"+scantype+"matrix.txt";
//
//	//__android_log_print(ANDROID_LOG_DEBUG, "OptLocFinder::initParams","FILE_NAME :%s", FILE_NAME.c_str());
//
//	matrixBinFileName = scantype+"matrix.bin";
//
//	//__android_log_print(ANDROID_LOG_DEBUG, "OptLocFinder::initParams","matrixBinFileName :%s", matrixBinFileName.c_str());
//
//	FILE_BIN_NAME = "scan results/" + matrixBinFileName;
//
//	//__android_log_print(ANDROID_LOG_DEBUG, "OptLocFinder::initParams","FILE_BIN_NAME :%s", FILE_BIN_NAME.c_str());
//
//
//	FILE_FLOOR_BIN_NAME=scantype+"floorselection.bin";
//	FILE_FLOOR_TXT_NAME=scantype+"floorselection.txt";
//	FILE_FLOOR_SWITCH_GROUPS_NAME = "floor_groups.json";
//
//
//	isFirstLoad = false;
//
//	LoadedFloor = -100;
//
//	appDirPath = APPDirPath;
//
//	//__android_log_print(ANDROID_LOG_DEBUG, "OptLocFinder::initParams","appDirPath :%s", appDirPath.c_str());
//
//	LOCATION_CLOSE_RANGE = locationCloseRange;
////	IS_FLOOR_SELECTION = isfloorSelection;
//	K = k;
//	PIXELS_TO_METER = pixelsToMeter;
//	AVERAGE_RANGE = averageRange;
//
//	ssidfilter = ssidsFitr;
//
//	FLOORS_COUNT=floorscount;
//
//	zeroValue = -127.0f;
//
//
//	  //XXX spatial added
//	lastAverage.x=-1.0f;
//	lastAverage.y=-1.0f;
//	lastAverage.z=-1.0f;
//
//	CLOSE_DEVICES_THRESHOLD=closeDevicesThreshold;
//	CLOSE_DEVICES_WEIGHT=closeDeviceWeight;
//	K_TOP_LEVELS_THR=kTopLevelThr;

//	LEVEL_LOWER_BOUND = levelLowerBound;

    int firstTimekTopLevelsThr = -1;
    initParams(APPDirPath, locationCloseRange, k, pixelsToMeter, averageRange,
               ssidsFitr, floorscount, scantype, closeDevicesThreshold, closeDeviceWeight,
               kTopLevelThr, levelLowerBound, firstTimekTopLevelsThr);
}


bool OptLocFinder::initParamsWithValidation(string APPDirPath, int locationCloseRange, int k,
                                            float pixelsToMeter, int averageRange,
                                            vector<string> &ssidsFitr, int floorscount,
                                            string scantype, float closeDevicesThreshold,
                                            float closeDeviceWeight, int kTopLevelThr,
                                            int levelLowerBound, int firstTimekTopLevelThr) {

    struct stat info;
    bool state = 0;

    if (stat(APPDirPath.c_str(), &info) != 0) {
        state = 0;
    } else if (info.st_mode & S_IFDIR) {
        state = 1;
    }

    if (state) {
        initParams(APPDirPath, locationCloseRange, k, pixelsToMeter, averageRange,
                   ssidsFitr, floorscount, scantype, closeDevicesThreshold, closeDeviceWeight,
                   kTopLevelThr, levelLowerBound, firstTimekTopLevelThr);
    }

    return state;
}

void OptLocFinder::initParams(string APPDirPath, int locationCloseRange, int k, float pixelsToMeter,
                              int averageRange,
                              vector<string> &ssidsFitr, int floorscount, string scantype,
                              float closeDevicesThreshold, float closeDeviceWeight,
                              int kTopLevelThr, int levelLowerBound, int firstTimekTopLevelThr) {

    FILE_NAME = "scan results/" + scantype + "matrix.txt";

    //__android_log_print(ANDROID_LOG_DEBUG, "OptLocFinder::initParams","FILE_NAME :%s", FILE_NAME.c_str());

    matrixBinFileName = scantype + "matrix.bin";

    //__android_log_print(ANDROID_LOG_DEBUG, "OptLocFinder::initParams","matrixBinFileName :%s", matrixBinFileName.c_str());

    FILE_BIN_NAME = "scan results/" + matrixBinFileName;

    //__android_log_print(ANDROID_LOG_DEBUG, "OptLocFinder::initParams","FILE_BIN_NAME :%s", FILE_BIN_NAME.c_str());


    FILE_FLOOR_BIN_NAME = scantype + "floorselection.bin";
    FILE_FLOOR_TXT_NAME = scantype + "floorselection.txt";
    FILE_FLOOR_SWITCH_GROUPS_NAME = "floor_groups.json";
    FILE_LOCATION_GROUPS_NAME = "location_groups.json";


    isFirstLoad = false;

    LoadedFloor = -100;

    appDirPath = APPDirPath;

    //__android_log_print(ANDROID_LOG_DEBUG, "OptLocFinder::initParams","appDirPath :%s", appDirPath.c_str());

    LOCATION_CLOSE_RANGE = locationCloseRange;
//	IS_FLOOR_SELECTION = isfloorSelection;
    K = k;
    PIXELS_TO_METER = pixelsToMeter;
    AVERAGE_RANGE = averageRange;

    ssidfilter = ssidsFitr;

    FLOORS_COUNT = floorscount;

    zeroValue = -127.0f;


    //XXX spatial added
    lastAverage.x = -1.0f;
    lastAverage.y = -1.0f;
    lastAverage.z = -1.0f;

    CLOSE_DEVICES_THRESHOLD = closeDevicesThreshold;
    CLOSE_DEVICES_WEIGHT = closeDeviceWeight;
    K_TOP_LEVELS_THR = kTopLevelThr;
    FIRST_TIME_K_TOP_LEVELS_THR = firstTimekTopLevelThr;
    LEVEL_LOWER_BOUND = levelLowerBound;
}

void OptLocFinder::getLastLocation(Location &loc) {
    getAvePoint(loc);
}

float OptLocFinder::getZeroValue() {
    return zeroValue;
}

void OptLocFinder::setZeroValue() {


    zeroValue = -127.0f;

//	if (mins.empty()) {
//		zeroValue = 1;
//		return;
//	}
//	float result = 1;
//	int size = mins.size();
//	for (int i = 0; i < size; i++) {
//		if (mins[i] < result) {
//			result = mins[i];
//		}
//	}
//	result -= 10;
//	zeroValue = result;
}

vector<float> OptLocFinder::normalizeVector(vector<float> &v, vector<float> &min,
                                            vector<float> &max) {

    //		float result[] = new float[v.length];

    int v_size = (int) v.size();
    vector<float> result(v_size, 0);

    for (int i = 0; i < v_size; i++) {

        if (i >= max.size() || i >= min.size()    //XXX protection for indexofbound
            || i >= v.size() || i >= result.size()) {
            continue;
        }

        float diff = (max[i] - min[i]);
        if (v[i] == 0) {
            result[i] = 0;
            continue;
        }

        if (diff != 0) {
            result[i] = (v[i] - min[i]) / diff;
        } else {
            result[i] = 0;
        }
    }
    return result;



//	float result[] = new float[v.length];
//
//		for (int i = 0; i < v.length; i++) {
//			float diff = (max[i] - min[i]);
//			if (v[i] == 0) {
////				if (diff != 0) {
////					result[i] = (float) ((AsociativeMemoryLocator.getInstance().getZeroValue() - min[i]) / diff);
////				} else {
////					result[i] = 0;
////				}
//				result[i] = 0;
//			continue;
//			}
//
//			if (diff != 0) {
//				result[i] = (v[i] - min[i]) / diff;
//			} else {
//				result[i] = 0;
//			}
//		}
//		return result;

}

bool OptLocFinder::ignored(string ssid) {
    //if (PropertyHolder.getInstance().getSsidFilter().contains(ssid)) //XXX SsidFilter
    bool isPresent = (std::find(ssidfilter.begin(), ssidfilter.end(), ssid)
                      != ssidfilter.end());
    if (isPresent)
        return true;
    return false;

}

Location OptLocFinder::getLastAverage() {
    return lastAverage;
}

void OptLocFinder::setLastAverage(Location &lastAverage) {
    this->lastAverage = lastAverage;
}

void OptLocFinder::restLastAverage() {
    this->lastAverage.x = -1.0f;
    this->lastAverage.y = -1.0f;
    this->lastAverage.z = -1.0f;
}

void OptLocFinder::loadDataList(map<string, innerMap> &pointsMap) {

    set<string> pts;  //	Set<String> pts = pointsMap.keySet();
    for (map<string, innerMap>::iterator it = pointsMap.begin();
         it != pointsMap.end(); ++it) {

//		__android_log_print(ANDROID_LOG_DEBUG, "OptLocFinder::loadDataList","pts :%s", it->first.c_str());

        pts.insert(it->first);
    }
    innerMap idMap; //Map<String, Double> idMap;
    mins.clear(); //mins = maxs = null;
    maxs.clear();
    set<string>::iterator pt;

    for (pt = pts.begin(); pt != pts.end(); ++pt) { //for (string pt : pts) {

        idMap = pointsMap.find(*pt)->second; //			idMap = pointsMap.get(pt);
        //vector<float> v;  //float[] v = new float[INDEX_MAP.size()];
        //v.reserve(INDEX_MAP.size());

        vector<float> v(INDEX_MAP.size(), -127.0f);

//		for (int i = 0; i < v.length; i++) {
//						v[i] = -127;
//					}

//			for (Entry<String, Double> e : idMap.entrySet()) {
//				int i = getArrayPosition(e.getKey());
//				if (i != -1) {
//					v[i] = e.getValue().floatValue();
//				}
//			}
        innerMap::iterator iter;
        for (iter = idMap.begin(); iter != idMap.end(); ++iter) {
            string key = iter->first;
            double value = iter->second;
            int i = getArrayPosition(key);
            if (i != -1) {
                v[i] = value; //e.getValue().floatValue();
            }
        }

//		for(int rr=0;rr<v.size();rr++){
//			__android_log_print(ANDROID_LOG_DEBUG, "OptLocFinder::loadDataList",
//							"v :%f", v[rr]);
//		}
        updateMinMax(v);
//			String[] coords = pt.split(",");

        vector<string> coords;
        string word;
        string line = *pt;
        stringstream stream(line);
        while (getline(stream, word, ',')) {
            coords.push_back(word);
        }

//			PointF pf = new PointF(Float.valueOf(coords[0]),
//					Float.valueOf(coords[1]));
        Location pf;
        pf.x = atof(coords[0].c_str());
        pf.y = atof(coords[1].c_str());

//			AssociativeData data = new AssociativeData(pf, v);
        AssociativeData data;
        data.point = pf;
        data.mvector = v;

//			if (coords.length > 2) {
//				data.setZ(Integer.parseInt(coords[2]));
//			}
//			theList.add(data);

        if (coords.size() > 2) {
            data.Z = atoi(coords[2].c_str());
        }

        theList.push_back(data);

    }
    normalizeList(mins, maxs);
    //XXX HOW TO DO THIS HERE?? BaseMatrixDataHelper
//		BaseMatrixDataHelper.getInstance().setMatrix(theList);
//		BaseMatrixDataHelper.getInstance().setSSIDNames(ssidnames);
}

void OptLocFinder::normalizeList(vector<float> &min, vector<float> &max) {

    //	for (AssociativeData p : theList) {
    //		p.normalizedvector = MathUtils.normalizeVector(p.vector, min, max);
    //	}

    for (list<AssociativeData>::iterator it = theList.begin();
         it != theList.end(); it++) {
        AssociativeData &p = *it;
        p.normalizedvector = normalizeVector(p.mvector, min, max);

    }

}

void OptLocFinder::updateMinMax(vector<float> &v) {
    if (maxs.empty()) { //		if (maxs == null) {

        //	maxs = v; //maxs=new float[v.length];

        int v_size = (int) v.size();

        //maxs= new vector(v_size);


        for (int i = 0; i < v_size; i++) {
            maxs.push_back(v[i]);
            //maxs[i]=v[i];

            //__android_log_print(ANDROID_LOG_DEBUG, "OptLocFinder::updateMinMax","maxs :%f", maxs[i]);
        }

//			System.arraycopy(v, 0, maxs, 0, v.length);




    } else {
        int v_size = (int) v.size();


        for (int i = 0; i < v_size; i++) {
            maxs[i] = max(maxs[i], v[i]);
        }
    }

    if (mins.empty()) { //if (mins == null) {
        //mins = v; //			mins = new float[v.length];
//			System.arraycopy(v, 0, mins, 0, v.length);

        int v_size = (int) v.size();

        for (int i = 0; i < v_size; i++) {
            //mins[i]=v[i];
            mins.push_back(v[i]);
            //	__android_log_print(ANDROID_LOG_DEBUG, "OptLocFinder::updateMinMax","mins :%f", mins[i]);
        }

    } else {
        int v_size = (int) v.size();
        for (int i = 0; i < v_size; i++) {
            mins[i] = min(mins[i], v[i]); //mins[i] = Math.min(mins[i], v[i]);
        }
    }
}

int OptLocFinder::getArrayPosition(const string &bssid) {
    map<string, int>::iterator it;
    it = INDEX_MAP.find(bssid);  //int index = INDEX_MAP.get(bssid);

//	  if (index == null) {
//	  			return -1;
//	  		}
    if (it == INDEX_MAP.end())
        return -1;
    int index = it->second;
    return index;
}

const list<AssociativeDataSorter> &OptLocFinder::getClosePoints() {
    return closePoints;
}

void OptLocFinder::getAvePoint(Location &loc) {
    // __android_log_print(ANDROID_LOG_DEBUG, "getAvePoint", "==%s","start");
    float sumx = 0;
    float sumy = 0;
    //list<AssociativeDataSorter> points; //= new ArrayList<AssociativeDataSorter>();

    //  __android_log_print(ANDROID_LOG_DEBUG, "getAvePoint", "ClosePoints.size() :%d",closePoints.size());

    if (!closePoints.empty() &&
        closePoints.size() > 1) { //if (getClosePoints() != null && getClosePoints().size() > 1) {
        //list<AssociativeDataSorter> lpoints = getClosePoints();
        //if (!lpoints.empty()) { //if (lpoints != null) {
        //points.addAll(lpoints);
        //points.insert(lpoints.end(), lpoints.begin(), lpoints.end());

//			for (AssociativeDataSorter p : points) {
//				sumx += p.data.point.x;
//				sumy += p.data.point.y;
//			}
        list<AssociativeDataSorter>::iterator p;
        for (p = closePoints.begin(); p != closePoints.end(); ++p) {
            sumx += p->data.point.x;
            sumy += p->data.point.y;
        }
        float ax = 0;
        float ay = 0;

        if (closePoints.size() > 0) {
            ax = sumx / ((float) closePoints.size());
            ay = sumy / ((float) closePoints.size());
        }
        //PointF avePt = new PointF(ax, ay);
        Location avePt;
        avePt.x = ax;
        avePt.y = ay;
        if (lastAverage.x != -1.0f) {//if (&lastAverage != NULL) {
            //	double distancefromlast = MathUtils.distance(avePt, lastAverage)/ FacilityConf.getInstance().getPixelsToMeter();

            double distancefromlast = distance(avePt, lastAverage)
                                      /
                                      PIXELS_TO_METER; //XXX ? // / FacilityConf.getInstance().getPixelsToMeter();

            int averagerange = AVERAGE_RANGE; // PropertyHolder.getInstance().getAverageRange(); //XXX ?
            if (distancefromlast > averagerange) {
                avePt.x = (avePt.x + lastAverage.x) / 2;
                avePt.y = (avePt.y + lastAverage.y) / 2;
            }
        }
        //lastAverage = avePt;
        lastAverage.x = avePt.x;
        lastAverage.y = avePt.y;
        lastAverage.z = avePt.z;

        loc.x = lastAverage.x;
        loc.y = lastAverage.y;
        loc.z = lastAverage.z;
        //return &lastAverage;

    } else {
        loc.x = -1.0f;
        loc.y = -1.0f;
        loc.z = -1.0f;
        //return null;
        //return; //NULL;
    }

    // __android_log_print(ANDROID_LOG_DEBUG, "getAvePoint", "=%s","stop");

}


double OptLocFinder::distance(const Location &p, const Location &p1) {
    return sqrt((p.x - p1.x) * (p.x - p1.x) + (p.y - p1.y) * (p.y - p1.y));
}

vector<float> OptLocFinder::createVector(const list<WlBlip> &blips) {
    //float[] result = new float[INDEX_MAP.size()];

    vector<float> result(INDEX_MAP.size(), -127.0f);


    list<WlBlip>::const_iterator blip;
//	result.reserve(INDEX_MAP.size());



    for (blip = blips.begin(); blip != blips.end(); ++blip) {
        int index = getArrayPosition(blip->BSSID);
        if (index != -1 && index < result.size() && index >= 0) { //XXX add indexofbound protection
            result[index] = blip->level;
        }
    }
    return result;

//		for (WlBlip blip : blips) {
//			int index = getArrayPosition(blip.BSSID);
//			if (index != -1) {
//				result[index] = blip.level;
//			}
//		}
//		return result;
}

// ============ MB zone group
int OptLocFinder::getZoneGroupByBlips(list<WlBlip> &blips) {

    if (!IS_ZONE_GROUP_CONF_LOADED) {
        return UNKNOWN_FLOOR;
    }


    int floor_group_pass_thr = 0;
    //	  __android_log_print(ANDROID_LOG_INFO, "OptLocFinder::loadSwitchFloorGroupsFile", "bef params floor_group_pass_thr %d",floor_group_pass_thr);
    if (m_params.count(FLOOR_GROUPS_PASS_THR_PARAM) > 0) {
        Param param = m_params[FLOOR_GROUPS_PASS_THR_PARAM];
        floor_group_pass_thr = param.getValueAsInt();
        //        __android_log_print(ANDROID_LOG_INFO, "OptLocFinder::loadSwitchFloorGroupsFile", "in params floor_group_pass_thr %d",floor_group_pass_thr);
    }

    // TODO - Reduce the blip list to be only the beacons playing in the groups -- Performance issue
    map<string, groupFloorInfo>::iterator iter = m_zone_groups.begin();

    for (; iter != m_zone_groups.end(); iter++) {
        iter->second.calculateWeights(blips, floor_group_pass_thr);
    }
    // Next round to check the distance values
    list<int> candidatesFloors;
    int prevFloor = UNKNOWN_FLOOR;

    iter = m_zone_groups.begin();
    for (; iter != m_zone_groups.end(); iter++) {
        if (iter->second.m_passTest &&
            iter->second.m_weigth - m_zone_groups[iter->second.m_reverseGroupID].m_weigth >
            iter->second.m_distance) {
            candidatesFloors.push_back(iter->second.m_floorLevel);
            if (prevFloor == UNKNOWN_FLOOR) // First time we have a floor
            {
                prevFloor = iter->second.m_floorLevel;
            }
            else // We already have a floor - check if the current floor is different
            {
                if (prevFloor != iter->second.m_floorLevel) // Check that we are on the same floor
                {
                    return UNKNOWN_FLOOR; // no real need to continue checking
                }

            }
        }
    }
    // check if we have candidates at all  - if yes 100% we found the floor!
    if (candidatesFloors.size() > 0)
        return *candidatesFloors.begin();

    return UNKNOWN_FLOOR;

    // TODO - Reduce the blip list to be only the beacons playing in the groups -- Performance issue
    /**
    map<string, groupFloorInfo>::iterator iter = m_zone_groups.begin();

    for (;iter != m_zone_groups.end(); iter++)
    {
        iter->second.calculateWeights(blips);
    }
    // Next round to check the distance values
    list<string> candidatesFloors;
    string prevZoneGroup = UNKNOWN_ZONE_GROUP;

    iter = m_zone_groups.begin();
    for (;iter != m_zone_groups.end(); iter++)
    {
        if(iter->second.m_passTest &&
           iter->second.m_weigth - m_zone_groups[iter->second.m_reverseGroupID].m_weigth > iter->second.m_distance)
        {
            candidatesFloors.push_back(iter->second.m_groupdID);
            if ( prevZoneGroup.compare(UNKNOWN_ZONE_GROUP) == 0 ) // First time we have a floor
            {
                  prevZoneGroup = iter->second.m_groupdID;
              }
              else // We already have a zone group - check if the current  zone group is different
              {
                  if(prevZoneGroup != iter->second.m_groupdID) // Check that we are on the same  zone group
                  {
                      return UNKNOWN_ZONE_GROUP; // no real need to continue checking
                  }

             }
        }
    }
    // check if we have candidates at all  - if yes 100% we found the  zone group!
    if(candidatesFloors.size() > 0)
        return *candidatesFloors.begin();

    return UNKNOWN_ZONE_GROUP;
    */

}

void OptLocFinder::loadZoneGroupsFile(const string &ofile) {


    FILE *jsonFile = fopen(ofile.c_str(), "r");

    if (jsonFile == NULL) {
        IS_ZONE_GROUP_CONF_LOADED = false;
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


    const rapidjson::Value &groups = d["groups"];
    for (int i = 0; i < groups.Size(); i++) {
        const rapidjson::Value &group = groups[i];
        groupFloorInfo newGroup;
        newGroup.m_groupdID = group["groupID"].GetString();
        newGroup.m_floorLevel = group["floor"].GetInt();
        newGroup.m_levelTrashhold = group["levelTrashhold"].GetDouble();

        if (group["level_avg_threshold"].IsNull()) {
            newGroup.m_level_avg_threshold = newGroup.m_levelTrashhold;
        }
        else {
            newGroup.m_level_avg_threshold = group["level_avg_threshold"].GetDouble();
        }

        newGroup.m_reverseGroupID = group["revereseGroup"].GetString();
        newGroup.m_distance = group["distance"].GetDouble();


        const rapidjson::Value &beacons = group["beacons"];

        for (int j = 0; j < beacons.Size(); j++) {
            newGroup.m_beaconList.push_back(beacons[j].GetString());
        }
        m_zone_groups[newGroup.m_groupdID] = newGroup;
    }

    IS_ZONE_GROUP_CONF_LOADED = true;

}
// ============ END MB zone group

int Param::getValueAsInt() {
    int intval = 0;
    std::string::const_iterator it = value.begin();
    while (it != value.end() && isdigit(*it))
        ++it;
    if (!value.empty() && it == value.end()) {  // is number
        intval = atoi(value.c_str());
    }

    return intval;
}

void groupFloorInfo::calculateWeights(list<WlBlip> &blips, int floorGroupPassThr) {
    bool IS_USE_NEW_METHOD = false;
    int goodBeacons = 0;
    double totalSum = -115 * (int) (m_beaconList.size());
    for (list<string>::const_iterator iter = m_beaconList.begin();
         iter != m_beaconList.end(); iter++) {
        const string &beaconID = *iter;
        for (list<WlBlip>::const_iterator blipsIter = blips.begin();
             blipsIter != blips.end(); blipsIter++) {
            if (blipsIter->BSSID == beaconID) {
                totalSum += (blipsIter->level + 115);

                if (IS_USE_NEW_METHOD) {
                    // new method : Check if this beacon is good enough
                    if (blipsIter->level > m_level_avg_threshold)
                        goodBeacons++;
                    break;
                }
                else {
                    // Check if this beacon is good enough
                    if (blipsIter->level > m_levelTrashhold)
                        goodBeacons++;
                    break;
                }
            }
        }
    }

    m_passTest = goodBeacons > m_beaconList.size() - (1 + floorGroupPassThr);
    m_weigth = totalSum / (float) m_beaconList.size();
}


void OptLocFinder::loadLocationGroupsFile(const string &ofile) {

//	 __android_log_print(ANDROID_LOG_INFO, "OptLocFinder::loadLocationGroupsFile", "params before %d",1);
//    string jsonFileDir;
//
//    jsonFileDir = appDirPath + "/" + facility + "/" + "location_groups.json";

//	 __android_log_print(ANDROID_LOG_INFO, "OptLocFinder::loadLocationGroupsFile", "file %s",ofile.c_str());

    FILE *jsonFile = fopen(ofile.c_str(), "r");

    if (jsonFile == NULL) {
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

    location_groups.clear();

    const rapidjson::Value &groups = d["groups"];
    for (int i = 0; i < groups.Size(); i++) {
        const rapidjson::Value &group = groups[i];
        groupFloorInfo newGroup;
        newGroup.m_groupdID = group["groupID"].GetString();
        //   __android_log_print(ANDROID_LOG_INFO, "OptLocFinder::loadLocationGroupsFile", "m_groupdID %s",newGroup.m_groupdID.c_str());

        newGroup.m_floorLevel = group["floor"].GetInt();
        //   __android_log_print(ANDROID_LOG_INFO, "OptLocFinder::loadLocationGroupsFile", "m_floorLevel %d",newGroup.m_floorLevel);

        newGroup.m_levelTrashhold = group["levelTrashhold"].GetDouble();

        //  __android_log_print(ANDROID_LOG_INFO, "OptLocFinder::loadLocationGroupsFile", "m_levelTrashhold %f",newGroup.m_levelTrashhold);

        if (!group["type"].IsNull()) {
            newGroup.m_type = group["type"].GetString();
            //__android_log_print(ANDROID_LOG_INFO, "OptLocFinder::loadLocationGroupsFile", "m_type %s",newGroup.m_type.c_str());
        }


        if (!group["geofence_id"].IsNull()) {
            newGroup.m_geofence_id = group["geofence_id"].GetString();
            //__android_log_print(ANDROID_LOG_INFO, "OptLocFinder::loadLocationGroupsFile", "m_geofence_id %s",newGroup.m_geofence_id.c_str());
        }


        if (group["level_avg_threshold"].IsNull()) {
            newGroup.m_level_avg_threshold = newGroup.m_levelTrashhold;
        }
        else {
            newGroup.m_level_avg_threshold = group["level_avg_threshold"].GetDouble();
        }

        newGroup.m_reverseGroupID = group["revereseGroup"].GetString();
        // __android_log_print(ANDROID_LOG_INFO, "OptLocFinder::loadLocationGroupsFile", "m_reverseGroupID %s",newGroup.m_reverseGroupID.c_str());
        newGroup.m_distance = group["distance"].GetDouble();
        //  __android_log_print(ANDROID_LOG_INFO, "OptLocFinder::loadLocationGroupsFile", "m_distance %f",newGroup.m_distance);


        const rapidjson::Value &beacons = group["beacons"];

        for (int j = 0; j < beacons.Size(); j++) {
            newGroup.m_beaconList.push_back(beacons[j].GetString());
        }

        location_groups[newGroup.m_groupdID] = newGroup;
        //__android_log_print(ANDROID_LOG_INFO, "OptLocFinder::loadLocationGroupsFile", "location_groups.size %d",location_groups.size());
    }

    // __android_log_print(ANDROID_LOG_INFO, "OptLocFinder::loadLocationGroupsFile", "params before %d",2);

    if (!d["params"].IsNull()) {

        const rapidjson::Value &params = d["params"];
        //__android_log_print(ANDROID_LOG_INFO, "OptLocFinder::loadLocationGroupsFile", "params size %d",params.Size());

        for (int i = 0; i < params.Size(); i++) {
            const rapidjson::Value &param = params[i];
            Param newParam;
            newParam.name = param["name"].GetString();
            // __android_log_print(ANDROID_LOG_INFO, "OptLocFinder::loadLocationGroupsFile", "param name  %s", newParam.name.c_str());
            newParam.value = param["value"].GetString();
            // __android_log_print(ANDROID_LOG_INFO, "OptLocFinder::loadLocationGroupsFile", "param value  %s", newParam.value.c_str());
            location_params[newParam.name] = newParam;
        }

    }

    // __android_log_print(ANDROID_LOG_INFO, "OptLocFinder::loadLocationGroupsFile", "end location_groups.size %d",location_groups.size());


}

/*
 * Get the fired group id if found otherwise return "UNKNOWN"
 * if more than one group is firing with different floor then the returned group id also "UNKNOWN"
 */
string OptLocFinder::getGroupIdByBlips(list<WlBlip> &blips) {
    //__android_log_print(ANDROID_LOG_INFO, "OptLocFinder::getGroupIdByBlips", "start  %d",1);
    //  string locationGroupsFilePath =  appDirPath + "/" + LoadedFacility + "/" + FILE_LOCATION_GROUPS_NAME;

    // __android_log_print(ANDROID_LOG_INFO, "OptLocFinder::getGroupIdByBlips", "locationGroupsFilePath  %s",locationGroupsFilePath.c_str());

    // 	loadLocationGroupsFile(locationGroupsFilePath);

    string UNKNOWN_GROUP = "UNKNOWN";

    if (location_groups.size() == 0) {
        return UNKNOWN_GROUP;
    }

    //	__android_log_print(ANDROID_LOG_INFO, "OptLocFinder::getGroupIdByBlips", "begin  %d",1);

    string firedGroup = UNKNOWN_GROUP;

    // TODO - Reduce the blip list to be only the beacons playing in the groups -- Performance issue
    map<string, groupFloorInfo>::iterator iter = location_groups.begin();

    // __android_log_print(ANDROID_LOG_INFO, "OptLocFinder::getGroupIdByBlips", "location_groups.size  %d", location_groups.size());

    int floor_group_pass_thr = 0;
    if (location_params.count(FLOOR_GROUPS_PASS_THR_PARAM) > 0) {
        Param param = location_params[FLOOR_GROUPS_PASS_THR_PARAM];
        floor_group_pass_thr = param.getValueAsInt();
        // __android_log_print(ANDROID_LOG_INFO, "OptLocFinder::getGroupIdByBlips", "in params floor_group_pass_thr %d",floor_group_pass_thr);
    }

    for (; iter != location_groups.end(); iter++) {
        iter->second.calculateWeights(blips, floor_group_pass_thr);
    }
    // Next round to check the distance values
    list<int> candidatesFloors;
    // store the group candidates ids
    list<string> candidatesIds;

    int prevFloor = UNKNOWN_FLOOR;

    iter = location_groups.begin();
    for (; iter != location_groups.end(); iter++) {
        if (iter->second.m_passTest &&
            iter->second.m_weigth - location_groups[iter->second.m_reverseGroupID].m_weigth >
            iter->second.m_distance) {
            candidatesFloors.push_back(iter->second.m_floorLevel);
            candidatesIds.push_back(iter->second.m_geofence_id);
            //__android_log_print(ANDROID_LOG_INFO, "OptLocFinder::getGroupIdByBlips", "iter->second.m_geofence_id %s",iter->second.m_geofence_id.c_str());

            if (prevFloor == UNKNOWN_FLOOR) // First time we have a floor
            {
                prevFloor = iter->second.m_floorLevel;
            }
            else // We already have a floor - check if the current floor is different
            {
                if (prevFloor != iter->second.m_floorLevel) // Check that we are on the same floor
                {
                    return UNKNOWN_GROUP; // no real need to continue checking
                }

            }
        }
    }
    // check if we have candidates at all  - if yes 100% we found the floor!
    if (candidatesFloors.size() > 0 &&
        candidatesIds.size() > 0) { // get the fired group id == geofence id
        firedGroup = candidatesIds.front();
        //__android_log_print(ANDROID_LOG_INFO, "OptLocFinder::getGroupIdByBlips", "firedGroup %s",firedGroup.c_str());
        return firedGroup;
        // return *candidatesFloors.begin();
    }

    //__android_log_print(ANDROID_LOG_INFO, "OptLocFinder::getGroupIdByBlips", "end  %d",2);

    return UNKNOWN_GROUP;
}

int OptLocFinder::newGetFloorByBlips(list<WlBlip> &blips) {

    // TODO - Reduce the blip list to be only the beacons playing in the groups -- Performance issue
    map<string, groupFloorInfo>::iterator iter = m_groups.begin();

    int floor_group_pass_thr = 0;
    if (m_params.count(FLOOR_GROUPS_PASS_THR_PARAM) > 0) {
        Param param = m_params[FLOOR_GROUPS_PASS_THR_PARAM];
        floor_group_pass_thr = param.getValueAsInt();
        //   __android_log_print(ANDROID_LOG_INFO, "OptLocFinder::loadSwitchFloorGroupsFile", "in params floor_group_pass_thr %d",floor_group_pass_thr);
    }


    for (; iter != m_groups.end(); iter++) {
        iter->second.calculateWeights(blips, floor_group_pass_thr);
    }
    // Next round to check the distance values
    list<int> candidatesFloors;
    int prevFloor = UNKNOWN_FLOOR;

    iter = m_groups.begin();
    for (; iter != m_groups.end(); iter++) {
        if (iter->second.m_passTest &&
            iter->second.m_weigth - m_groups[iter->second.m_reverseGroupID].m_weigth >
            iter->second.m_distance) {
            candidatesFloors.push_back(iter->second.m_floorLevel);
            if (prevFloor == UNKNOWN_FLOOR) // First time we have a floor
            {
                prevFloor = iter->second.m_floorLevel;
            }
            else // We already have a floor - check if the current floor is different
            {
                if (prevFloor != iter->second.m_floorLevel) // Check that we are on the same floor
                {
                    return UNKNOWN_FLOOR; // no real need to continue checking
                }

            }
        }
    }
    // check if we have candidates at all  - if yes 100% we found the floor!
    if (candidatesFloors.size() > 0)
        return *candidatesFloors.begin();

    return UNKNOWN_FLOOR;

}

int OptLocFinder::getFloorByBlips(list<WlBlip> &blips, bool isFirstTime) {

    int result = -100;

    HalfNavLocFinder *halfNavAlg = HalfNavLocFinder::getInstance();

    bool isGetFloorWithoutMatrix = halfNavAlg->isStatusOK();
    //__android_log_print(ANDROID_LOG_INFO, "getFloorByBlips", "isGetFloorWithoutMatrix %d",isGetFloorWithoutMatrix);
    if (isFirstTime) {
        if (isGetFloorWithoutMatrix) {
            result = halfNavAlg->getFloorByBlipsWithoutMatrix(blips);
            //__android_log_print(ANDROID_LOG_INFO, "getFloorByBlips", "getFloorByBlipsWithoutMatrix 1.1 %d",result);
        }
        else {
            result = selectFloorByBlips(blips, isFirstTime);
            //__android_log_print(ANDROID_LOG_INFO, "getFloorByBlips", "selectFloorByBlips 1.2 %d",result);
        }
    }
    else {

        if (isGetFloorWithoutMatrix &&
            !IS_NEW_FLOOR_SELECTION_ALG) // IS_NEW_FLOOR_SELECTION_ALG = floor groups
        {
            result = halfNavAlg->getFloorByBlipsWithoutMatrix(blips);
            //__android_log_print(ANDROID_LOG_INFO, "getFloorByBlips", "getFloorByBlipsWithoutMatrix 2.1 %d",result);
        }
        else {
            result = selectFloorByBlips(blips, isFirstTime);
            //__android_log_print(ANDROID_LOG_INFO, "getFloorByBlips", "selectFloorByBlips 2.2 %d",result);
        }
    }

    return result;

}

int OptLocFinder::selectFloorByBlips(list<WlBlip> &blips, bool isFirstTime) {

    // __android_log_print(ANDROID_LOG_INFO, "getFloorByBlips", "isFirstTime %",ads->data.Z);
    //	__android_log_print(ANDROID_LOG_DEBUG, "getFloorByBlips", "=%s","start");
    int result = -100;
    //__android_log_print(ANDROID_LOG_INFO, "1getFloorByBlips", "swf.size %d",switchFloorFilter.size());



    if (!isFirstTime) {

        //#ifdef NEW_FLOOR_SELECTION
        // ADIA new algo
        if (IS_NEW_FLOOR_SELECTION_ALG) {
            result = newGetFloorByBlips(blips);
            // halfNavAlg->updateStatus(result);
            //__android_log_print(ANDROID_LOG_INFO, "getFloorByBlips_1","IS_USE_HALF_NAV_ALG :%d, floor %d", halfNavAlg->isStatusOK(), result);
            return result;
            //#endif
        }
        // ADIA new algo - END

        /**
        if(switchFloorFilter.size()>0){
            //__android_log_print(ANDROID_LOG_INFO, "2getFloorByBlips", "swf.size %d",switchFloorFilter.size());
            std::list<WlBlip>::iterator itr = blips.begin();
            while (itr != blips.end())
            {
                string bssid = itr->BSSID;
                if(switchFloorFilter.find (bssid) == switchFloorFilter.end())
                {
                    // __android_log_print(ANDROID_LOG_INFO, "3getFloorByBlips", "blip.bssid %s",bssid.c_str());
                    blips.erase(itr++);
                }
                else
                {
                    ++itr;
                }
            }
        }
         */
        int passLevelLowerBound = 0;
        list<WlBlip>::const_iterator blip;
        for (blip = blips.begin(); blip != blips.end(); ++blip) {
            // __android_log_print(ANDROID_LOG_INFO, "4getFloorByBlips", "blip.bssid %s",blip->BSSID.c_str());
            if (blip->level > LEVEL_LOWER_BOUND) {
                passLevelLowerBound++;
            }
        }

        if (passLevelLowerBound < K_TOP_LEVELS_THR) { // == 0
            // halfNavAlg->updateStatus(result);
            //__android_log_print(ANDROID_LOG_INFO, "getFloorByBlips_2","IS_USE_HALF_NAV_ALG :%d, floor %d", halfNavAlg->isStatusOK(), result);
            return result;
        }
    }


    //	Location dummyLoc;	   //XXX spatial comment this line

    //	findClosestPoint(blips,dummyLoc);  //XXX spatial comment this line
    int max = 0;
    findFloorSelectClosestPoint(blips, isFirstTime); //XXX spatial uncomment this line

    //__android_log_print(ANDROID_LOG_DEBUG, "getFloorByBlips", "closePoints.size :%d",closePoints.size());

    if (closePoints.size() > 0) {//if (getClosePoints() != null && getClosePoints().size() > 0) {
        //list<AssociativeDataSorter> closepoints = getClosePoints();
        //int floors = FacilityConf.getInstance().getFloorDataList().size();
        int floors = FLOORS_COUNT;

        int elvetor_counter = 0;

        for (int i = 0; i < floors; i++) {
            int counter = 0;
            list<AssociativeDataSorter>::iterator ads;
            for (ads = closePoints.begin(); ads !=
                                            closePoints.end(); ++ads) { //for (AssociativeDataSorter ads : closePoints) {
                // __android_log_print(ANDROID_LOG_INFO, "getFloorByBlips", "ads->data.Z :%d",ads->data.Z);
                if (ads->data.Z == i) {

                    counter++;
                }
                if (counter > max) {
                    result = i;
                    max = counter;
                }
            }
        }

        if (!isFirstTime) {
            list<AssociativeDataSorter>::iterator ads;
            for (ads = closePoints.begin(); ads !=
                                            closePoints.end(); ++ads) { //for (AssociativeDataSorter ads : closePoints) {

                if (ads->data.Z == -989) {
                    elvetor_counter++;
                }
            }
            if (elvetor_counter > max) {
                result = -989;
            }
        }
    }

    //	__android_log_print(ANDROID_LOG_DEBUG, "getFloorByBlips", "=%s","end");
    //__android_log_print(ANDROID_LOG_INFO, "getFloorByBlips","max :%d", max);
    //__android_log_print(ANDROID_LOG_INFO, "getFloorByBlips","floor :%d max :%d", result, max);
    //	 halfNavAlg->updateStatus(result);
    //	__android_log_print(ANDROID_LOG_INFO, "getFloorByBlips_3","IS_USE_HALF_NAV_ALG :%d, floor %d", halfNavAlg->isStatusOK(), result);

    return result;
}

void OptLocFinder::findLocation(list<WlBlip> &blips, Location &loc, bool isFirstTime) {

    HalfNavLocFinder *halfNavAlg = HalfNavLocFinder::getInstance();

    //__android_log_print(ANDROID_LOG_INFO, "findLocation","findLocation %d, IS_USE_HALF_NAV_ALG = %d", 1, halfNavAlg->isStatusOK());


    if (halfNavAlg->isStatusOK()) {
        // __android_log_print(ANDROID_LOG_INFO, "findLocation","findLocation %d", 2);
        halfNavAlg->findLocation(blips, loc);
        // __android_log_print(ANDROID_LOG_INFO, "findLocation","IS_USE_HALF_NAV_ALG :%d, loc.x=%f , loc.y=%f ", halfNavAlg->isStatusOK(), loc.x, loc.y);
    }
    else {
        // __android_log_print(ANDROID_LOG_INFO, "findLocation","findClosestPoint %d", 3);
        findClosestPoint(blips, loc, isFirstTime);
    }
}

//XXX spatial commented -> replaced

//void OptLocFinder::findClosestPoint(list<WlBlip>& blips,Location &loc) {
//
//			 AssociativeData::clearTopK();
//
//
//		//	Location lastLoc;
//		//	getAvePoint(lastLoc);
//
//			//__android_log_print(ANDROID_LOG_DEBUG, "findClosestPoint", "=%s","start");
//
//
//			closePoints.clear();
//		//		if (theList.isEmpty()) {
//		//			return null;
//		//		}
//
//			if (theList.empty()) {
//				return; //NULL;
//			}
//
//			vector<float> v = createVector(blips);
//			vector<float> nv = normalizeVector(v, mins, maxs);
//
//		//	int locCloseRange = LOCATION_CLOSE_RANGE; //PropertyHolder.getInstance().getLocationCloseRange();  //XXX
//			list<AssociativeDataSorter> tmpList; //= new ArrayList<AssociativeDataSorter>();
//
//			//__android_log_print(ANDROID_LOG_DEBUG, "findClosestPoint", "theList.size=%d",theList.size());
//			for (list<AssociativeData>::iterator it = theList.begin();
//					it != theList.end(); it++) {
//				AssociativeData & associativeData = *it;
//
//		//		for (Iterator<AssociativeData> iterator = theList.iterator(); iterator
//		//				.hasNext();) {
//		//			AssociativeData associativeData = (AssociativeData) iterator.next();
//
//				// if in range
//				//XXX
//		//		if ( /*&lastLoc != NULL
//		//				&& */ lastLoc.x!=-1.0f && isFlrSelector==false/*&& !(this instanceof FloorSelector)*/) {
//		//			if (abs((int) (associativeData.point.x - (lastLoc.x)))
//		//					<= locCloseRange
//		//					&& abs((int) (associativeData.point.y - (lastLoc.y)))
//		//							<= locCloseRange) {
//		//				double d = associativeData.normalDistance(v, nv);
//		//				//tmpList.add(new AssociativeDataSorter(associativeData, d));
//		//				AssociativeDataSorter ads;
//		//				ads.data = associativeData;
//		//				ads.d = d;
//		//				tmpList.push_back(ads);
//		//			}
//		//		} else {
//					double d = associativeData.normalDistance(v, nv, CLOSE_DEVICES_THRESHOLD,CLOSE_DEVICES_WEIGHT,K_TOP_LEVELS_THR);
//					//tmpList.add(new AssociativeDataSorter(associativeData, d));
//					AssociativeDataSorter ads;
//					ads.data = associativeData;
//					ads.d = d;
//					tmpList.push_back(ads);
//		//		}
//			}
//
//			//		for (Iterator<AssociativeData> iterator = theList.iterator(); iterator
//			//				.hasNext();) {
//			//			AssociativeData associativeData = (AssociativeData) iterator.next();
//			//
//			//			// if in range
//			//			if (lastLoc != null && !(this instanceof FloorSelector)) {
//			//				if (Math.abs((associativeData.getX() - (lastLoc.x))) <= locCloseRange
//			//						&& Math.abs((associativeData.getY() - (lastLoc.y))) <= locCloseRange) {
//			//					double d = associativeData.normalDistance(v, nv);
//			//					tmpList.add(new AssociativeDataSorter(associativeData, d));
//			//				}
//			//			} else {
//			//				double d = associativeData.normalDistance(v, nv);
//			//				tmpList.add(new AssociativeDataSorter(associativeData, d));
//			//			}
//			//		}
//
//			tmpList.sort(compareAssociativeData);
//
//			//__android_log_print(ANDROID_LOG_DEBUG, "findClosestPoint", "tmpList.size=%d",tmpList.size());
//		//		Collections.sort(tmpList, new Comparator<AssociativeDataSorter>() {
//		//			@Override
//		//			public int compare(AssociativeDataSorter lhs,
//		//					AssociativeDataSorter rhs) {
//		//				return lhs.compare(rhs);
//		//			}
//		//		});
//		//
//			setClosePoints(tmpList);
//			setFloorClosePoints(tmpList);
//			mBestMatch = tmpList.front(); //mBestMatch = tmpList.get(0);
//			if (tmpList.size() >= 2) { //mSecondMatch = tmpList.size() >= 2 ? tmpList.get(1) : null;
//				std::list<AssociativeDataSorter>::const_iterator cit = tmpList.begin();
//				++cit;
//				mSecondMatch = *cit; //tmpList[1];
//			} else { //XXX
//				//	*mSecondMatch= NULL;
//			}
//
//		//	if (tmpList.size() >= 3) {
//		//		std::list<AssociativeDataSorter>::const_iterator cit = tmpList.begin();
//		//		++cit;
//		//		AssociativeDataSorter result1 = *cit;	//tmpList[1];
//		//		++cit;
//		//		AssociativeDataSorter result2 = *cit; //tmpList[2];
//		//		//return &(findAverage(tmpList.front().data, result1.data, result2.data, nv, v));
//		//		// findAverage(tmpList.front().data, result1.data, result2.data, nv,v,loc);
//		//		 return;
//		//
//		//	}
//			//loc=&(mBestMatch.data.point);
//
//		//	loc.x=mBestMatch.data.point.x;
//		//    loc.y=mBestMatch.data.point.y;
//		//    loc.z=mBestMatch.data.point.z;
//
//			getAvePoint(loc);
//
//		//	__android_log_print(ANDROID_LOG_DEBUG, "findClosestPoint", "=%s","end");
//
//}


void OptLocFinder::findClosestPoint(list<WlBlip> &blips, Location &loc, bool isFirstTime) {

    //const clock_t begin_time = clock();

    //__android_log_print(ANDROID_LOG_INFO, "findClosestPoint", "=%s","start");
    AssociativeData::clearTopK();

    closePoints.clear();

    if (blips.size() == 0) {
        lastAverage.x = -1.0f;
        lastAverage.y = -1.0f;
        lastAverage.z = -1.0f;

        loc.x = lastAverage.x;
        loc.y = lastAverage.y;
        loc.z = lastAverage.z;
        return;
    }

    if (theList.empty()) {
        return;
    }

    vector<float> v = createVector(blips);
    vector<float> nv = normalizeVector(v, mins, maxs);

    list<AssociativeDataSorter> tmpList;


    //double distX = 0;
    //double distY = 0;
    double d = 0.0;
    int inRange = 0;

    if (lastAverage.x == -1.0f || isFirstTime) {
        //__android_log_print(ANDROID_LOG_INFO, "findClosestPoint", "lastAverage.x=%d",-1);
        //__android_log_print(ANDROID_LOG_DEBUG, "findClosestPoint", "theList.size=%d",theList.size());
        for (list<AssociativeData>::iterator it = theList.begin(); it != theList.end(); it++) {

            AssociativeData &associativeData = *it;
            // first location find request
            d = associativeData.normalDistance(v, nv, CLOSE_DEVICES_THRESHOLD, CLOSE_DEVICES_WEIGHT,
                                               K_TOP_LEVELS_THR);
            AssociativeDataSorter ads;
            ads.data = associativeData;
            ads.d = d;
            tmpList.push_back(ads);
        }
        inRange = 1;
    }
    else {

        //__android_log_print(ANDROID_LOG_INFO, "findClosestPoint", "LOCATION_CLOSE_RANGE=%d",LOCATION_CLOSE_RANGE);
        // define a region to explore
        /* find points closest to the origin and within distance radius */
        struct KdRes *kdres;
        double keyqr[2] = {lastAverage.x, lastAverage.y};

        kdres = kdGetNearestInRange((kdtree *) kdimtree, keyqr, LOCATION_CLOSE_RANGE);

        if (!kdres) {
            //__android_log_print(ANDROID_LOG_INFO, "findClosestPoint", "OOOO=%d",0);
            loc.x = lastAverage.x;
            loc.y = lastAverage.y;
            loc.z = lastAverage.z;
            return;
        }


        //__android_log_print(ANDROID_LOG_INFO, "findClosestPoint", "found results=%d",kdResSize(kdres));


        while (!kdIsResEnd(kdres)) {


            AssociativeData *iter = (AssociativeData *) kdGetResItemData(kdres);

            AssociativeData &associativeData = *iter;
            //__android_log_print(ANDROID_LOG_INFO, "findClosestPoint", "associativeData=(%f,%f)",associativeData.point.x,associativeData.point.y);
            d = associativeData.normalDistance(v, nv, CLOSE_DEVICES_THRESHOLD, CLOSE_DEVICES_WEIGHT,
                                               K_TOP_LEVELS_THR);
            AssociativeDataSorter ads;
            ads.data = associativeData;
            ads.d = d;
            tmpList.push_back(ads);
            ++inRange;

            kdResNext(kdres);
        }

        //__android_log_print(ANDROID_LOG_INFO, "findClosestPoint", "bef free %s","free");
        kdResFree(kdres);
        //	__android_log_print(ANDROID_LOG_INFO, "findClosestPoint", "free %s","free");


    }


    if (inRange == 0) {
        lastAverage.x = -1.0f;
        lastAverage.y = -1.0f;
        lastAverage.z = -1.0f;

        loc.x = lastAverage.x;
        loc.y = lastAverage.y;
        loc.z = lastAverage.z;
        return;
    }

    tmpList.sort(compareAssociativeData);

    //__android_log_print(ANDROID_LOG_INFO, "findClosestPoint", "tmpList.size=%d",tmpList.size());

    setClosePoints(tmpList);


    if (tmpList.size() > 0) {
        mBestMatch = tmpList.front();
    }

    if (tmpList.size() >= 2) {
        std::list<AssociativeDataSorter>::const_iterator cit = tmpList.begin();
        ++cit;
        mSecondMatch = *cit;
    }

    getAvePoint(loc);
    //__android_log_print(ANDROID_LOG_INFO, "findClosestPoint", "loc=(%f,%f)",loc.x,loc.y);
    //__android_log_print(ANDROID_LOG_INFO, "findClosestPoint", "takes=(%f)",float( clock () - begin_time ) /  CLOCKS_PER_SEC);



    //	__android_log_print(ANDROID_LOG_DEBUG, "findClosestPoint", "=%s","end");

}

/*
 * find location inside a given geofence using kdtree for speeding performance
 */
void OptLocFinder::findLocationInsideGeofence(list<WlBlip> &blips, Location &loc, float topLeftX,
                                              float topLeftY, float bottomRightX,
                                              float bottomRightY) {


    //__android_log_print(ANDROID_LOG_INFO, "findLocationInsideGeofence", "=%s","start");
    AssociativeData::clearTopK();

    closePoints.clear();

    if (theList.empty()) {
        return;
    }

    //check if geofence is valid
    if (topLeftX > bottomRightX || topLeftY > bottomRightY) {
        //__android_log_print(ANDROID_LOG_DEBUG, "findLocationInsideGeofence", "(topLeftX > bottomRightX)= (%d)",topLeftX > bottomRightX);
        loc.x = -1;
        loc.y = -1;
        loc.z = -1;
        return;
    }

    vector<float> v = createVector(blips);
    vector<float> nv = normalizeVector(v, mins, maxs);

    list<AssociativeDataSorter> tmpList;

    double d = 0.0;
    // define a region to explore
    struct KdRes *kdres;
    // compute center point of the given geofence
    double midX = topLeftX + (bottomRightX - topLeftX) / 2.0;
    double midY = topLeftY + (bottomRightY - topLeftY) / 2.0;
    double keyqr[2] = {midX, midY};


    //__android_log_print(ANDROID_LOG_INFO, "findLocationInsideGeofence", "(midX,midY)= (%f,%f)",midX,midY);

    kdres = kdGetNearestInRectRange((kdtree *) kdimtree, keyqr, topLeftX, topLeftY, bottomRightX,
                                    bottomRightY);
    //__android_log_print(ANDROID_LOG_INFO, "findLocationInsideGeofence", "(topLeftX,  topLeftY,  bottomRightX,  bottomRightY)= (%f,%f,%f,%f)", topLeftX,  topLeftY,  bottomRightX,  bottomRightY);

    //int ptsize= kdResSize(kdres);
    //__android_log_print(ANDROID_LOG_INFO, "findLocationInsideGeofence", "ptsize = %d",ptsize);

    if (!kdres) {
        //__android_log_print(ANDROID_LOG_INFO, "findLocationInsideGeofence", "OOOO=%d",0);
        loc.x = -1;
        loc.y = -1;
        loc.z = -1;
        return;
    }


    //__android_log_print(ANDROID_LOG_INFO, "findLocationInsideGeofence", "found results=%d",kdResSize(kdres));

    while (!kdIsResEnd(kdres)) {

        AssociativeData *iter = (AssociativeData *) kdGetResItemData(kdres);
        AssociativeData &associativeData = *iter;
        //__android_log_print(ANDROID_LOG_INFO, "findLocationInsideGeofence", "associativeData=(%f,%f)",associativeData.point.x,associativeData.point.y);
        d = associativeData.normalDistance(v, nv, CLOSE_DEVICES_THRESHOLD, CLOSE_DEVICES_WEIGHT,
                                           K_TOP_LEVELS_THR);
        AssociativeDataSorter ads;
        ads.data = associativeData;
        ads.d = d;
        tmpList.push_back(ads);
        kdResNext(kdres);
    }

    //__android_log_print(ANDROID_LOG_INFO, "findLocationInsideGeofence", "bef free %s","free");
    kdResFree(kdres);
    //	__android_log_print(ANDROID_LOG_INFO, "findLocationInsideGeofence", "free %s","free");

    tmpList.sort(compareAssociativeData);
    //__android_log_print(ANDROID_LOG_INFO, "findLocationInsideGeofence", "tmpList.size=%d",tmpList.size());

    setClosePoints(tmpList);

    getAvePoint(loc);
    //__android_log_print(ANDROID_LOG_INFO, "findLocationInsideGeofence", "loc=(%f,%f)",loc.x,loc.y);

    //	__android_log_print(ANDROID_LOG_DEBUG, "findLocationInsideGeofence", "=%s","end");

}



//XXX spatial added
//  void OptLocFinder::findClosestPoint(list<WlBlip>& blips,Location &loc) {
//
//
//	//Location lastLoc;
//	//getAvePoint(lastLoc);
//
//	//__android_log_print(ANDROID_LOG_DEBUG, "findClosestPoint", "=%s","start");
//
//
//	closePoints.clear();
////		if (theList.isEmpty()) {
////			return null;
////		}
//
//	if (theList.empty()) {
//		return; //NULL;
//	}
//
//	vector<float> v = createVector(blips);
//	vector<float> nv = normalizeVector(v, mins, maxs);
//
////	int locCloseRange = LOCATION_CLOSE_RANGE; //PropertyHolder.getInstance().getLocationCloseRange();  //XXX
//	list<AssociativeDataSorter> tmpList; //= new ArrayList<AssociativeDataSorter>();
//
//
//	double distX = 0;
//	double distY = 0;
//	double d=0.0;
//	int inRange=0;
//
//	if(lastAverage.x==-1.0f){
//		//__android_log_print(ANDROID_LOG_DEBUG, "findClosestPoint", "lastAverage=%d",-1);
//		//__android_log_print(ANDROID_LOG_DEBUG, "findClosestPoint", "theList.size=%d",theList.size());
//		for (list<AssociativeData>::iterator it = theList.begin();
//				it != theList.end(); it++) {
//
//			AssociativeData & associativeData = *it;
//				// first location find request
//				 d = associativeData.normalDistance(v, nv, CLOSE_DEVICES_THRESHOLD,CLOSE_DEVICES_WEIGHT,K_TOP_LEVELS_THR);
//				AssociativeDataSorter ads;
//				ads.data = associativeData;
//				ads.d = d;
//				tmpList.push_back(ads);
//		}
//		inRange=1;
//	}
//	else{
//
//		//__android_log_print(ANDROID_LOG_DEBUG, "findClosestPoint", "LOCATION_CLOSE_RANGE=%d",LOCATION_CLOSE_RANGE);
//		// define a region to explore
//		AssociativeData low;
//		low.point.x=(lastAverage.x-LOCATION_CLOSE_RANGE); // / PIXELS_TO_METER;
//		low.point.y=(lastAverage.y-LOCATION_CLOSE_RANGE); // / PIXELS_TO_METER;
//
//		AssociativeData high;
//		high.point.x=(lastAverage.x+LOCATION_CLOSE_RANGE); //  / PIXELS_TO_METER;
//		high.point.y=(lastAverage.y+LOCATION_CLOSE_RANGE); // / PIXELS_TO_METER;
//
//		//__android_log_print(ANDROID_LOG_DEBUG, "findClosestPoint", "low=(%f,%f)",low.point.x,low.point.y);
//		//__android_log_print(ANDROID_LOG_DEBUG, "findClosestPoint", "high=(%f,%f)",high.point.x,high.point.y);
//
//		// find all the points between low and high
//		for (  spatial::region_iterator<kdtree> iter = spatial::region_begin(tree, low, high);
//		     iter != spatial::region_end(tree, low, high); ++iter)
//		{
//		  // do something with elements in the region by using iter...
//			AssociativeData & associativeData = (AssociativeData &) *iter;
//			//__android_log_print(ANDROID_LOG_DEBUG, "findClosestPoint", "associativeData=(%f,%f)",associativeData.point.x,associativeData.point.y);
//			d = associativeData.normalDistance(v, nv, CLOSE_DEVICES_THRESHOLD,CLOSE_DEVICES_WEIGHT,K_TOP_LEVELS_THR);
//			AssociativeDataSorter ads;
//			ads.data = associativeData;
//			ads.d = d;
//			tmpList.push_back(ads);
//			++inRange;
//		}
//		//__android_log_print(ANDROID_LOG_DEBUG, "findClosestPoint", "inRange=%d",inRange);
//
////					distX = abs((associativeData.point.x - (lastLoc.x))) / PIXELS_TO_METER;
////					distY = abs((associativeData.point.y - (lastLoc.y))) / PIXELS_TO_METER;
////
////					if (distX <= LOCATION_CLOSE_RANGE && distY <= LOCATION_CLOSE_RANGE) {
////							double d = associativeData.normalDistance(v, nv);
////							AssociativeDataSorter ads;
////							ads.data = associativeData;
////							ads.d = d;
////							tmpList.push_back(ads);
////					}
////
////
////					//AssociativeData dummySearch=new AssociativeData(lastLoc, null);
////					AssociativeData dummySearch;  //dummy used for search only
////
////					dummySearch.point.x=lastLoc.x;
////					dummySearch.point.y=lastLoc.y;
//
//
////								int keyToStart=Collections.binarySearch(theList, dummySearch);
////								if(keyToStart<0){
////									keyToStart=keyToStart*-1 + 1;
////								}
////
////								int upSearchKey=keyToStart;
////								while(upSearchKey<theList.size()){
////
////									distX = Math.abs((theList.get(upSearchKey).getX() - (lastLoc.x)))
////											/ PropertyHolder.getInstance().getPixelsToMeter();
////									distY = Math.abs((theList.get(upSearchKey).getY() - (lastLoc.y)))
////											/ PropertyHolder.getInstance().getPixelsToMeter();
////									if (distX <= locCloseRange && distY <= locCloseRange) {
////										double d = theList.get(upSearchKey).normalDistance(v, nv);
////										tmpList.add(new AssociativeDataSorter(theList.get(upSearchKey),
////												d));
////									}
////									else{
////										break;
////									}
////									upSearchKey++;
////								}
////
////
////								int downSearchKey=keyToStart;
////
////
////								while(downSearchKey>=0){
////
////									distX = Math.abs((theList.get(downSearchKey).getX() - (lastLoc.x)))
////											/ PropertyHolder.getInstance().getPixelsToMeter();
////									distY = Math.abs((theList.get(downSearchKey).getY() - (lastLoc.y)))
////											/ PropertyHolder.getInstance().getPixelsToMeter();
////									if (distX <= locCloseRange && distY <= locCloseRange) {
////										double d = theList.get(downSearchKey).normalDistance(v, nv);
////										tmpList.add(new AssociativeDataSorter(theList.get(downSearchKey),
////												d));
////									}
////									else{
////										break;
////									}
////									downSearchKey--;
////								}
//
//
//	}
//
//
//	if(inRange==0)
//	{
//		lastAverage.x=-1.0f;
//		lastAverage.y=-1.0f;
//		lastAverage.z=-1.0f;
//
//		loc.x=lastAverage.x;
//		loc.y=lastAverage.y;
//		loc.z=lastAverage.z;
//		return;
//	}
//
//	tmpList.sort(compareAssociativeData);
//
//	//__android_log_print(ANDROID_LOG_DEBUG, "findClosestPoint", "tmpList.size=%d",tmpList.size());
//
//	setClosePoints(tmpList);
//	//setFloorClosePoints(tmpList);  //XXX WHY TO SET FLOOR SELECTION POINTS HERE?
//
//	if (tmpList.size() > 0){
//		mBestMatch = tmpList.front(); //mBestMatch = tmpList.get(0);
//	}
//
//	if (tmpList.size() >= 2) { //mSecondMatch = tmpList.size() >= 2 ? tmpList.get(1) : null;
//		std::list<AssociativeDataSorter>::const_iterator cit = tmpList.begin();
//		++cit;
//		mSecondMatch = *cit; //tmpList[1];
//	}
////	else { //XXX
//		//	*mSecondMatch= NULL;
////	}
//
//
//	getAvePoint(loc);
//
////	__android_log_print(ANDROID_LOG_DEBUG, "findClosestPoint", "=%s","end");
//
//}

//XXX spatial added
void OptLocFinder::findFloorSelectClosestPoint(list<WlBlip> &blips, bool isFirstTime) {

    AssociativeData::clearTopK();
//	Location lastLoc;
//	getAvePoint(lastLoc);

    //__android_log_print(ANDROID_LOG_DEBUG, "findClosestPoint", "=%s","start");


    closePoints.clear();
//	floorclosePoints.clear();
//		if (theList.isEmpty()) {
//			return null;
//		}

    if (theList.empty()) {
        return; //NULL;
    }

    vector<float> v = createVector(blips);
    vector<float> nv = normalizeVector(v, mins, maxs);

//	int locCloseRange = LOCATION_CLOSE_RANGE; //PropertyHolder.getInstance().getLocationCloseRange();  //XXX
    list<AssociativeDataSorter> tmpList; //= new ArrayList<AssociativeDataSorter>();

    //__android_log_print(ANDROID_LOG_DEBUG, "findClosestPoint", "theList.size=%d",theList.size());
    for (list<AssociativeData>::iterator it = theList.begin();
         it != theList.end(); it++) {
        AssociativeData &associativeData = *it;

//		for (Iterator<AssociativeData> iterator = theList.iterator(); iterator
//				.hasNext();) {
//			AssociativeData associativeData = (AssociativeData) iterator.next();

        // if in range
        //XXX
//		if (  lastLoc.x!=-1.0f && isFlrSelector==false) {
//			if (abs((int) (associativeData.point.x - (lastLoc.x)))
//					<= locCloseRange
//					&& abs((int) (associativeData.point.y - (lastLoc.y)))
//							<= locCloseRange) {
//				double d = associativeData.normalDistance(v, nv);
//				//tmpList.add(new AssociativeDataSorter(associativeData, d));
//				AssociativeDataSorter ads;
//				ads.data = associativeData;
//				ads.d = d;
//				tmpList.push_back(ads);
//			}
//		} else {
        //__android_log_print(ANDROID_LOG_INFO, "getFloorByBlips","LEVEL_LOWER_BOUND :%d", LEVEL_LOWER_BOUND);
        double d = 0;
        if (isFirstTime) {
            d = associativeData.normalDistance(v, nv, CLOSE_DEVICES_THRESHOLD, CLOSE_DEVICES_WEIGHT,
                                               FIRST_TIME_K_TOP_LEVELS_THR, LEVEL_LOWER_BOUND,
                                               isFirstTime);
        }
        else {
            d = associativeData.normalDistance(v, nv, CLOSE_DEVICES_THRESHOLD, CLOSE_DEVICES_WEIGHT,
                                               K_TOP_LEVELS_THR, LEVEL_LOWER_BOUND, isFirstTime);
        }
        //tmpList.add(new AssociativeDataSorter(associativeData, d));
        AssociativeDataSorter ads;
        ads.data = associativeData;
        ads.d = d;
        tmpList.push_back(ads);
//		}
    }

    //		for (Iterator<AssociativeData> iterator = theList.iterator(); iterator
    //				.hasNext();) {
    //			AssociativeData associativeData = (AssociativeData) iterator.next();
    //
    //			// if in range
    //			if (lastLoc != null && !(this instanceof FloorSelector)) {
    //				if (Math.abs((associativeData.getX() - (lastLoc.x))) <= locCloseRange
    //						&& Math.abs((associativeData.getY() - (lastLoc.y))) <= locCloseRange) {
    //					double d = associativeData.normalDistance(v, nv);
    //					tmpList.add(new AssociativeDataSorter(associativeData, d));
    //				}
    //			} else {
    //				double d = associativeData.normalDistance(v, nv);
    //				tmpList.add(new AssociativeDataSorter(associativeData, d));
    //			}
    //		}

    tmpList.sort(compareAssociativeData);

    //__android_log_print(ANDROID_LOG_DEBUG, "findClosestPoint", "tmpList.size=%d",tmpList.size());
//		Collections.sort(tmpList, new Comparator<AssociativeDataSorter>() {
//			@Override
//			public int compare(AssociativeDataSorter lhs,
//					AssociativeDataSorter rhs) {
//				return lhs.compare(rhs);
//			}
//		});
//
    //setClosePoints(tmpList);
    setClosePoints(tmpList);
    if (tmpList.size() > 0) {
        mBestMatch = tmpList.front(); //mBestMatch = tmpList.get(0);
    }
    if (tmpList.size() >= 2) { //mSecondMatch = tmpList.size() >= 2 ? tmpList.get(1) : null;
        std::list<AssociativeDataSorter>::const_iterator cit = tmpList.begin();
        ++cit;
        mSecondMatch = *cit; //tmpList[1];
    }
//		else { //XXX
//		//	*mSecondMatch= NULL;
//	}

//	if (tmpList.size() >= 3) {
//		std::list<AssociativeDataSorter>::const_iterator cit = tmpList.begin();
//		++cit;
//		AssociativeDataSorter result1 = *cit;	//tmpList[1];
//		++cit;
//		AssociativeDataSorter result2 = *cit; //tmpList[2];
//		//return &(findAverage(tmpList.front().data, result1.data, result2.data, nv, v));
//		// findAverage(tmpList.front().data, result1.data, result2.data, nv,v,loc);
//		 return;
//
//	}
    //loc=&(mBestMatch.data.point);

//	loc.x=mBestMatch.data.point.x;
//    loc.y=mBestMatch.data.point.y;
//    loc.z=mBestMatch.data.point.z;

    //getAvePoint(loc);

//	__android_log_print(ANDROID_LOG_DEBUG, "findClosestPoint", "=%s","end");

}

void OptLocFinder::findAverage(AssociativeData &result,
                               AssociativeData &result1, AssociativeData &result2,
                               vector<float> &nv,
                               vector<float> &v, Location &loc) {

    //Location* pt;

    //		PointF pt = new PointF();
    float d1 = result.normalDistance(v, nv, CLOSE_DEVICES_THRESHOLD, CLOSE_DEVICES_WEIGHT,
                                     K_TOP_LEVELS_THR);
    float d2 = result1.normalDistance(v, nv, CLOSE_DEVICES_THRESHOLD, CLOSE_DEVICES_WEIGHT,
                                      K_TOP_LEVELS_THR);
    // double d3 = result2.normalDistance(v,nv);
    float sum = d1 + d2;

    // pt.x = (float) (((1.0 - d1 / sum) * result.point.x + (1.0 - d2 / sum)
    // * result1.point.x + (1.0 - d3 / sum) * result2.point.x) / 2.0);
    // pt.y = (float) (((1.0 - d1 / sum) * result.point.y + (1.0 - d2 / sum)
    // * result1.point.y + (1.0 - d3 / sum) * result2.point.y) / 2.0);
    loc.x = (float) ((((sum - d1) / sum) * result.point.x
                      + ((sum - d2) / sum) * result1.point.x));
    loc.y = (float) ((((sum - d1) / sum) * result.point.y
                      + ((sum - d2) / sum) * result1.point.y));


    //__android_log_print(ANDROID_LOG_DEBUG, "OptLocFinder::findAverage", "x= %f  y= %f", loc.x,loc.y);

    //return pt; // result.point;
}

//XXX spatial added comment this  setFloorClosePoints
void OptLocFinder::setFloorClosePoints(list<AssociativeDataSorter> &sorted) {
    floorclosePoints.clear();

    for (list<AssociativeDataSorter>::iterator it = sorted.begin();
         it != sorted.end(); it++) {
        AssociativeDataSorter &element = *it;
        floorclosePoints.push_back(element);
        if (floorclosePoints.size() == K) //XXX //PropertyHolder.getInstance().getFloorSelectionK())
            return;
    }
    //		for (AssociativeDataSorter element : sorted) {
    //			floorclosePoints.add(element);
    //			if (floorclosePoints.size() == PropertyHolder.getInstance()
    //					.getFloorSelectionK())
    //				break;
    //		}
}

void OptLocFinder::setClosePoints(list<AssociativeDataSorter> &sorted) {
    closePoints.clear();
    for (list<AssociativeDataSorter>::iterator it = sorted.begin();
         it != sorted.end(); it++) {
        AssociativeDataSorter &element = *it;
        closePoints.push_back(element);
        if (closePoints.size() == K/*PropertyHolder.getInstance().getK()*/) //XXX
            return;
    }
//		for (AssociativeDataSorter element : sorted) {
//			closePoints.add(element);
//			if (closePoints.size() == PropertyHolder.getInstance().getK())
//				return;
//		}
}

bool OptLocFinder::compareAssociativeData(AssociativeDataSorter first,
                                          AssociativeDataSorter second) {

    if (first.d < second.d)
        return true;
    else if (first.d > second.d)
        return false;
    else
        return true;

}

void OptLocFinder::load(string facility, int floor, bool isselectfloor,
                        bool isBin) {

    IS_FLOOR_SELECTION = isselectfloor;

    if (floor == LoadedFloor && facility.compare(LoadedFacility))
        return;

    //this->ssidfilter = ssidfilter;

    initIndexMap();

    stringstream ld;
    ld << floor;
    string floordir = appDirPath + "/" + facility + "/" + ld.str();

    //__android_log_print(ANDROID_LOG_DEBUG, "OptLocFinder::load", "floordir :%s",	floordir.c_str());

    if (isBin) {
        loadBin(facility, floor);
    } else { // load from matrix.txt

        ifstream infile;

        if (isselectfloor) {
            string facilitydir = appDirPath + "/" + facility + "/"
                                 + FILE_FLOOR_TXT_NAME;
            //XXX debug
            //	string facilitydir = appDirPath + "/floors.txt";
            infile.open(facilitydir.c_str());

        } else {

            string fd = floordir + "/" + FILE_NAME;
            //XXX debug
            //string fd = appDirPath + "/floors.txt";
            infile.open(fd.c_str());
        }

        if (!infile.is_open())
            return;
        double level = 0.0;
        string bssid; // = null;
        string ssid; // = null;
//			BufferedReader br = null;
        map<string, innerMap> pointsMap; //Map<String, Map<String, Double>> pointsMap = new HashMap<String, Map<String, Double>>();
        //	try {
//				br = new BufferedReader(new FileReader(file));
        string line = ""; //string line = null;

        while (!infile.eof()) {
            getline(infile, line);

            //__android_log_print(ANDROID_LOG_DEBUG, "OptLocFinder::load",	"line :%s", line.c_str());

            vector<string> fields;
            string word;
            stringstream stream(line);
            while (getline(stream, word, '\t')) {
                fields.push_back(word);

                //__android_log_print(ANDROID_LOG_DEBUG, "OptLocFinder::load","word :%s", word.c_str());
            }

            if (fields.size() < 5)
                continue;
            bssid = fields[2];

            //__android_log_print(ANDROID_LOG_DEBUG, "OptLocFinder::load","bssid :%s", bssid.c_str());
            ssid = fields[3];

            //__android_log_print(ANDROID_LOG_DEBUG, "OptLocFinder::load","ssid :%s", ssid.c_str());

            if (INDEX_MAP.find(bssid) == INDEX_MAP.end()) {
                if (ignored(ssid) && isFirstLoad == false) {
                    INDEX_MAP.insert(std::pair<string, int>(bssid, -1));
                } else {
                    INDEX_MAP.insert(
                            std::pair<string, int>(bssid, INDEX_MAP.size()));
                }
                ssidnames.push_back(ssid);
            }
            level = atof(fields[4].c_str()); //level = Double.parseDouble(fields[4]);
            string key;
            if (isselectfloor) {
                key = fields[0] + "," + fields[1] + "," + fields[5];

            } else {
                key = fields[0] + "," + fields[1];

                //__android_log_print(ANDROID_LOG_DEBUG, "OptLocFinder::load","key :%s", key.c_str());
            }

            if (pointsMap.find(key) == pointsMap.end()) { //if (!pointsMap.containsKey(key)) {
                map<string, double> idsMap; // = new HashMap<String, Double>();
                idsMap.insert(std::pair<string, int>(bssid, level)); //idsMap.put(bssid, level);
                pointsMap.insert(
                        std::pair<string, innerMap>(key, idsMap)); //pointsMap.put(key, idsMap);
            } else {

                //pointsMap.get(key).put(bssid, level);
                map<string, innerMap>::iterator it;
                it = pointsMap.find(key);
                it->second.insert(std::pair<string, double>(bssid, level));

            }

        }
        //	} catch (exception& e) {
//
//				e.printStackTrace();
//			} catch (IOException e) {
//
//				e.printStackTrace();
//			} finally {
//				if (br != null) {
//					try {
//						br.close();
//					} catch (IOException e) {
//
//						e.printStackTrace();
//					}
//					br = null;

        //	}
        loadDataList(pointsMap);
        setZeroValue();

        //XXX spatial added

        if (IS_FLOOR_SELECTION == false) {
            loadKdTree();
        }

        LoadedFacility = facility;
        LoadedFloor = floor;
    }
}
//XXX spatial added
//	void OptLocFinder:: loadKdTree() {
//
//
//		tree.clear();
//
//		for (list<AssociativeData>::iterator it = theList.begin();
//						it != theList.end(); it++) {
//					AssociativeData & associativeData = *it;
//					tree.insert(associativeData);
//		}
//		//__android_log_print(ANDROID_LOG_DEBUG, "findClosestPoint", "theList.size=%d",theList.size());
//		//__android_log_print(ANDROID_LOG_DEBUG, "findClosestPoint", "tree.size=%d",tree.size());
//
//	}


void OptLocFinder::loadKdTree() {


    //kdFree((kdtree*) kdimtree);

    kdimtree = kdCreate(2);

    for (list<AssociativeData>::iterator it = theList.begin();
         it != theList.end(); it++) {
        AssociativeData &associativeData = *it;

        double key[2] = {associativeData.point.x, associativeData.point.y};
        kdInsert((kdtree *) kdimtree, key, &associativeData);

    }

    //__android_log_print(ANDROID_LOG_DEBUG, "findClosestPoint", "theList.size=%d",theList.size());
    //__android_log_print(ANDROID_LOG_DEBUG, "findClosestPoint", "tree.size=%d",tree.size());

}

void OptLocFinder::loadBin(string facility, int floor) {

    initIndexMap();

//	MatrixBinRep *mbr = new MatrixBinRep(theList, INDEX_MAP, ssidnames, mins,
//			maxs);

    MatrixBinRep mbr;
    //try {
    stringstream ld;
    ld << floor;
    string dir;
    string switchFloorFilter;
    string locationGroupsFilePath;
    if (IS_FLOOR_SELECTION == false) {

        dir = appDirPath + "/" + facility + "/" + ld.str() + "/"
              + FILE_BIN_NAME;
        //XXX debug
        // dir = appDirPath + "/floors.bin";
        // load location_groups file
        locationGroupsFilePath = appDirPath + "/" + facility + "/" + FILE_LOCATION_GROUPS_NAME;
        loadLocationGroupsFile(locationGroupsFilePath);


    } else {

        dir = appDirPath + "/" + facility + "/" + FILE_FLOOR_BIN_NAME;
        //dir = appDirPath + "/floors.bin";

        // load floor_groups file
        switchFloorFilter = appDirPath + "/" + facility + "/" + FILE_FLOOR_SWITCH_GROUPS_NAME;
        loadSwitchFloorGroupsFile(switchFloorFilter);

        // load locatio_groups file
        //locationGroupsFilePath =  appDirPath + "/" + facility + "/" + FILE_LOCATION_GROUPS_NAME;
        //loadLocationGroupsFile(locationGroupsFilePath);

        string halfNavSettingsDirPath = appDirPath + "/" + facility;
        // __android_log_print(ANDROID_LOG_INFO, "OptLocFinder::half", "beacons_location size %s",halfNavSettingsDirPath.c_str());
        HalfNavLocFinder::releaseInstance();
        HalfNavLocFinder *optLocf;
        optLocf = HalfNavLocFinder::getInstance();
        optLocf->loadSettings(halfNavSettingsDirPath);


    }


    //mbr->readObject(dir);

    //string fileName = "/storage/emulated/0/mlins/matrix.bin";

//	mbr.readObject(dir,theList,
//			INDEX_MAP, ssidnames,
//			mins, maxs, IS_FLOOR_SELECTION);

    mbr.readSpecialObject(dir, theList,
                          INDEX_MAP, ssidnames,
                          mins, maxs, IS_FLOOR_SELECTION);

//	} catch (exception& e) {

//	}
    setZeroValue();

    //XXX spatial added
    if (IS_FLOOR_SELECTION == false) {
        loadKdTree();
    }

    LoadedFacility = facility;
    LoadedFloor = floor;

//	delete mbr;


    //__android_log_print(ANDROID_LOG_DEBUG, "MatrixBinRep::loadBin", "INDEX_MAP %d",INDEX_MAP.size());

    //	__android_log_print(ANDROID_LOG_DEBUG, "MatrixBinRep::loadBin", "theList %d",theList.size());


    //	__android_log_print(ANDROID_LOG_DEBUG, "MatrixBinRep::loadBin", "ssidnames %d",ssidnames.size());

    //	__android_log_print(ANDROID_LOG_DEBUG, "MatrixBinRep::loadBin", "maxs %d",maxs.size());
    //	__android_log_print(ANDROID_LOG_DEBUG, "MatrixBinRep::loadBin", "mins %d",mins.size());
}

void OptLocFinder::loadSwitchFloorGroupsFile(const string &ofile) {

    //  __android_log_print(ANDROID_LOG_INFO, "OptLocFinder::loadSwitchFloorGroupsFile", "params before %d",1);
//    string jsonFileDir;
//
//    jsonFileDir = appDirPath + "/" + facility + "/" + "floorGroups.json";

    FILE *jsonFile = fopen(ofile.c_str(), "r");

    if (jsonFile == NULL) {
        IS_NEW_FLOOR_SELECTION_ALG = false;
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


    const rapidjson::Value &groups = d["groups"];
    for (int i = 0; i < groups.Size(); i++) {
        const rapidjson::Value &group = groups[i];
        groupFloorInfo newGroup;
        newGroup.m_groupdID = group["groupID"].GetString();
        newGroup.m_floorLevel = group["floor"].GetInt();
        newGroup.m_levelTrashhold = group["levelTrashhold"].GetDouble();

        if (group["level_avg_threshold"].IsNull()) {
            newGroup.m_level_avg_threshold = newGroup.m_levelTrashhold;
        }
        else {
            newGroup.m_level_avg_threshold = group["level_avg_threshold"].GetDouble();
        }

        newGroup.m_reverseGroupID = group["revereseGroup"].GetString();
        newGroup.m_distance = group["distance"].GetDouble();


        const rapidjson::Value &beacons = group["beacons"];

        for (int j = 0; j < beacons.Size(); j++) {
            newGroup.m_beaconList.push_back(beacons[j].GetString());
        }
        m_groups[newGroup.m_groupdID] = newGroup;
    }

    // __android_log_print(ANDROID_LOG_INFO, "OptLocFinder::loadSwitchFloorGroupsFile", "params before %d",2);

    if (!d["params"].IsNull()) {

        const rapidjson::Value &params = d["params"];
        //__android_log_print(ANDROID_LOG_INFO, "OptLocFinder::loadSwitchFloorGroupsFile", "params size %d",params.Size());

        for (int i = 0; i < params.Size(); i++) {
            const rapidjson::Value &param = params[i];
            Param newParam;
            newParam.name = param["name"].GetString();
            // __android_log_print(ANDROID_LOG_INFO, "OptLocFinder::loadSwitchFloorGroupsFile", "param name  %s", newParam.name.c_str());
            newParam.value = param["value"].GetString();
            //__android_log_print(ANDROID_LOG_INFO, "OptLocFinder::loadSwitchFloorGroupsFile", "param value  %s", newParam.value.c_str());
            m_params[newParam.name] = newParam;
        }

    }
    IS_NEW_FLOOR_SELECTION_ALG = true;

    // ADIA - Add reading the floor group file
    /*
    {
        "groups": [
                   {
                   "groupID": "g1",
                   "level": 1,
                   "levelTrashhold": 20.0,
                   "distance": 13.0,
                   "revereseGroup": "g2",
                   "beacons": [
                               "beaconID1",
                               "beaconID1"
                               ]
                   }
                   ]
    }
    1. Read the JSON
    2. Create the groups
    3. Inside floor selection - use the code
    */
    // ADIA END

    /**
            switchFloorFilter.clear();
            ifstream infile;

            infile.open(ofile.c_str());

            if (!infile.is_open())
                return;

            string bssid;

            string line = ""; //string line = null;

            while (!infile.eof()) {

                getline(infile, line);

                //__android_log_print(ANDROID_LOG_INFO, "loadSwitchFloorGroupsFile",	"line :%s", line.c_str());

                vector<string> fields;
                string word;
                stringstream stream(line);
                while (getline(stream, word, '\t')) {
                    fields.push_back(word);
                    //__android_log_print(ANDROID_LOG_INFO, "loadSwitchFloorGroupsFile","word :%s", word.c_str());
                }

                if (fields.size() < 1)
                    continue;
                bssid = fields[0];
                switchFloorFilter.insert(bssid);
                //__android_log_print(ANDROID_LOG_INFO, "loadSwitchFloorGroupsFile","bssid :%s", bssid.c_str());


            }
        */
}

bool OptLocFinder::saveBin() {

    /*
     // test save bin

     list<AssociativeData> theListTemp;
     map<string, int> INDEX_MAPtemp;
     list<string> ssidnamesTemp;

     int theList_size = 3;
     short vec_size = 3;

     Location p;

     for (int i = 0; i < theList_size; i++) {

     short x = 1;

     short y = 2;

     vector<float> mvectorTemp;

     for (int j = 0; j < vec_size; j++) {
     int intv = 3;

     mvectorTemp.push_back(intv);
     }

     vector<float> normalizedvectorTemp;
     for (int j = 0; j < vec_size; j++) {
     int intv = 4;
     normalizedvectorTemp.push_back(intv / 10000.0);
     }

     AssociativeData data;
     data.point.x = x;
     data.point.y = y;

     //?
     data.mvector = mvectorTemp;
     data.normalizedvector = normalizedvectorTemp;
     theListTemp.push_back(data);
     //?
     // delete &mvectorTemp;
     //delete &normalizedvectorTemp;
     // delete &data;
     }

     vector<float> minsTemp;
     for (int j = 0; j < vec_size; j++) {
     short mn1 = 9;

     minsTemp.push_back(mn1);
     }

     vector<float> maxsTemp;
     for (int j = 0; j < vec_size; j++) {
     short mx1 = 10;

     maxsTemp.push_back(mx1);
     }

     int map_size = 3;
     for (int j = 0; j < map_size; j++) {
     string key = "Seas" +j;
     int value = 1;

     INDEX_MAPtemp.insert(std::pair<string, int>(key, value));
     }

     int ssidnames_size = 3;

     for (int j = 0; j < ssidnames_size; j++) {
     string ssidn = "abcNet";
     ssidnamesTemp.push_back(ssidn);
     }

     MatrixBinRep *mbr = new MatrixBinRep(theListTemp, INDEX_MAPtemp,
     ssidnamesTemp, minsTemp, maxsTemp);

     string fileName = "/storage/emulated/0/mlins/matrix.bin";

     mbr->writeObject(fileName.c_str());
     */

//	MatrixBinRep *mbr = new MatrixBinRep(theList, INDEX_MAP, ssidnames, mins,
//			maxs);

    MatrixBinRep mbr;

    //try {
    stringstream ld;
    ld << LoadedFloor;
    string dir;
    if (IS_FLOOR_SELECTION == false)
        dir = appDirPath + "/" + LoadedFacility + "/" + ld.str() + "/"
              + FILE_BIN_NAME;
        //XXX debug
        // dir = appDirPath + "/floors.bin";
    else {
        dir = appDirPath + "/" + LoadedFacility + "/" + FILE_FLOOR_BIN_NAME;
        // dir = appDirPath + "/floors.bin";
    }

    //__android_log_print(ANDROID_LOG_DEBUG, "OptLocFinder::saveBin", "dir :%s",dir.c_str());

//	mbr.writeObject(dir.c_str(),theList,
//			INDEX_MAP, ssidnames,
//			mins, maxs, IS_FLOOR_SELECTION);

    mbr.writeSpecialObject(dir.c_str(), theList,
                           INDEX_MAP, ssidnames,
                           mins, maxs, IS_FLOOR_SELECTION);

    //	} catch (exception& e) {
    //		return false;
    //	}

    //delete mbr;

    return true;

}

//public class AsociativeMemoryLocator {
//	PointF lastpt = null;
//	private static final String FILE_NAME = "scan results" + File.separator
//			+ "matrix.txt";
//
//	private static final String matrixBinFileName = "matrix.bin";
//	private static final String FILE_BIN_NAME = "scan results" + File.separator
//			+ matrixBinFileName;
//
//	private File matrixBinFile;
//	private static AsociativeMemoryLocator instance;
//	List<AssociativeData> theList = new ArrayList<AssociativeData>();
//	public Map<String, Integer> INDEX_MAP = new HashMap<String, Integer>();
//	protected List<String> ssidnames = new ArrayList<String>();
//	private List<AssociativeDataSorter> closePoints = new ArrayList<AssociativeDataSorter>();
//	private List<AssociativeDataSorter> floorclosePoints = new ArrayList<AssociativeDataSorter>();
//	public float[] mins;
//	public float[] maxs;
//	private boolean isFirstLoad = false;
//	private float zeroValue;
//	private AssociativeDataSorter mBestMatch;
//	private AssociativeDataSorter mSecondMatch;
//	private PointF lastAverage;
//	private int LoadedFloor;
//	private String LoadedFacility;
//
//
//	public PointF getLastAverage() {
//		return lastAverage;
//	}
//
//	public void setLastAverage(PointF lastAverage) {
//		this.lastAverage = lastAverage;
//	}
//
//	/**
//	 * loads the matrix data from file
//	 */
//
//	public void load(boolean isselectfloor) {
//		int floor = FacilityConf.getInstance().getSelectedFloor();
//
//		load(floor, isselectfloor);
//	}
//
//	public void load() {
//		int floor = FacilityConf.getInstance().getSelectedFloor();
//		String facility = PropertyHolder.getInstance().getFacilityID();
//		if (floor == LoadedFloor && facility.equals(LoadedFacility))
//			return;
//		load(floor, false);
//		LoadedFacility = 	PropertyHolder.getInstance().getFacilityID();
//		LoadedFloor = floor;
//	}
//
//	public void load(int floor, boolean isselectfloor) {
//		initIndexMap();
//
//		String floordir = PropertyHolder.getInstance().getFacilityDir() + "/"
//				+ floor;
//
//		if (PropertyHolder.getInstance().isTypeBin()) { // load from matrix.bin
//			loadBin(floor);
//		} else { // load from matrix.txt
//
//			File file;
//			if (isselectfloor) {
//				String facilitydir = PropertyHolder.getInstance()
//						.getFacilityDir().toString();
//				String selectfile = "floorselection.txt";
//				file = new File(facilitydir, selectfile);
//			} else {
//				file = new File(floordir, getFileName());
//			}
//
//			if (!file.isFile())
//				return;
//			Double level = 0.0;
//			String bssid = null;
//			String ssid = null;
//			BufferedReader br = null;
//			Map<String, Map<String, Double>> pointsMap = new HashMap<String, Map<String, Double>>();
//			try {
//				br = new BufferedReader(new FileReader(file));
//				String line = null;
//				while ((line = br.readLine()) != null) {
//					String[] fields = line.split("\t");
//					if (fields.length < 5)
//						continue;
//					bssid = fields[2];
//					ssid = fields[3];
//					if (!INDEX_MAP.containsKey(bssid)) {
//						if (ignored(ssid) && isFirstLoad == false) {
//							INDEX_MAP.put(bssid, -1);
//						} else {
//							INDEX_MAP.put(bssid, INDEX_MAP.size());
//						}
//
//						ssidnames.add(ssid);
//					}
//					level = Double.parseDouble(fields[4]);
//					String key;
//					if (isselectfloor) {
//						key = fields[0] + "," + fields[1] + "," + fields[5];
//					} else {
//						key = fields[0] + "," + fields[1];
//					}
//
//					if (!pointsMap.containsKey(key)) {
//						Map<String, Double> idsMap = new HashMap<String, Double>();
//						idsMap.put(bssid, level);
//						pointsMap.put(key, idsMap);
//					} else {
//						pointsMap.get(key).put(bssid, level);
//					}
//
//				}
//			} catch (FileNotFoundException e) {
//
//				e.printStackTrace();
//			} catch (IOException e) {
//
//				e.printStackTrace();
//			} finally {
//				if (br != null) {
//					try {
//						br.close();
//					} catch (IOException e) {
//
//						e.printStackTrace();
//					}
//					br = null;
//				}
//			}
//
//			loadDataList(pointsMap);
//			setZeroValue();
//			// collectAreaData();
//		}
//	}
//
//	public void loadBin(int floor) {
//
//		initIndexMap();
//		String floordir = PropertyHolder.getInstance().getFacilityDir() + "/"
//				+ floor;
//		File file = new File(floordir, getBinFileName());
//
//		if (!file.isFile())
//			return;
//		InputStream buffer = null;
//		ObjectInputStream input = null;
//		try {
//			MatrixBinRep mbr = new MatrixBinRep(theList, INDEX_MAP, ssidnames,
//					mins, maxs);
//			buffer = new BufferedInputStream(new FileInputStream(file));
//			input = new ObjectInputStream(buffer);
//			mbr.readObject(input);
//
//		} catch (FileNotFoundException e) {
//
//			e.printStackTrace();
//		} catch (IOException e) {
//
//			e.printStackTrace();
//		} catch (Exception e) {
//
//			e.printStackTrace();
//		} finally {
//			try {
//				input.close();
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
//
//		// loadDataList(pointsMap);
//		setZeroValue();
//		// collectAreaData();
//	}
//
//	public boolean saveBin() {
//
//		MatrixBinRep mbr = new MatrixBinRep(theList, INDEX_MAP, ssidnames,
//				mins, maxs);
//
//		File dir = new File(PropertyHolder.getInstance().getFloorDir(),
//				"scan results");
//
//		OutputStream outBin = null;
//
//		matrixBinFile = new File(dir, matrixBinFileName);
//
//		if (!matrixBinFile.exists()) {
//			try {
//				matrixBinFile.createNewFile();
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//		}
//
//		try {
//
//			outBin = new BufferedOutputStream(new FileOutputStream(
//					matrixBinFile, false));
//
//			ObjectOutputStream output = new ObjectOutputStream(outBin);
//
//			mbr.writeObject(output);
//
//			outBin.flush();
//			output.flush();
//			output.close();
//
//			return true;
//		} catch (IOException e) {
//			e.toString();
//		} finally {
//			try {
//				outBin.close();
//
//			} catch (IOException e) {
//
//				e.printStackTrace();
//			}
//
//			outBin = null;
//		}
//		return false;
//
//	}
//
//	private boolean ignored(String ssid) {
//		if (PropertyHolder.getInstance().getSsidFilter().contains(ssid)) {
//			return true;
//		} else {
//			return false;
//		}
//
//	}
//
//	// private void collectAreaData() {
//	// for (AssociativeData dot : theList) {
//	// for (AssociativeData closedot : theList) {
//	// if ((Math.abs(dot.point.x - closedot.point.x) < 100)
//	// && (Math.abs(dot.point.y - closedot.point.y) < 100)) {
//	// dot.getAreadata().add(closedot);
//	// }
//	// }
//	// }
//
//	// }
//
//	@SuppressLint("UseValueOf")
//	protected void initIndexMap() {
//		INDEX_MAP.clear();
//		ssidnames.clear();
//		theList.clear();
//		closePoints.clear();
//		mins = maxs = null;
//	}
//
//	private void loadDataList(Map<String, Map<String, Double>> pointsMap) {
//		Set<String> pts = pointsMap.keySet();
//		Map<String, Double> idMap;
//		mins = maxs = null;
//		for (String pt : pts) {
//			idMap = pointsMap.get(pt);
//			float[] v = new float[INDEX_MAP.size()];
//			for (Entry<String, Double> e : idMap.entrySet()) {
//				int i = getArrayPosition(e.getKey());
//				if (i != -1) {
//					v[i] = e.getValue().floatValue();
//				}
//			}
//			updateMinMax(v);
//			String[] coords = pt.split(",");
//			PointF pf = new PointF(Float.valueOf(coords[0]),
//					Float.valueOf(coords[1]));
//			AssociativeData data = new AssociativeData(pf, v);
//			if (coords.length > 2) {
//				data.setZ(Integer.parseInt(coords[2]));
//			}
//			theList.add(data);
//		}
//		normalizeList(mins, maxs);
//		BaseMatrixDataHelper.getInstance().setMatrix(theList);
//		BaseMatrixDataHelper.getInstance().setSSIDNames(ssidnames);
//	}
//
//	private void updateMinMax(float[] v) {
//		if (maxs == null) {
//			maxs = new float[v.length];
//			System.arraycopy(v, 0, maxs, 0, v.length);
//		} else {
//			for (int i = 0; i < v.length; i++) {
//				maxs[i] = Math.max(maxs[i], v[i]);
//			}
//		}
//		if (mins == null) {
//			mins = new float[v.length];
//			System.arraycopy(v, 0, mins, 0, v.length);
//
//		} else {
//			for (int i = 0; i < v.length; i++) {
//				mins[i] = Math.min(mins[i], v[i]);
//			}
//		}
//	}
//
//	private void normalizeList(float[] min, float max[]) {
//		for (AssociativeData p : theList) {
//			p.normalizedvector = MathUtils.normalizeVector(p.vector, min, max);
//		}
//	}
//
//	private int getArrayPosition(String bssid) {
//		Integer index = INDEX_MAP.get(bssid);
//		if (index == null) {
//			return -1;
//		}
//		return index;
//	}
//
//	public PointF findClosestPoint(List<WlBlip> blips) {
//
//		PointF lastLoc = getAvePoint();
//
//		closePoints.clear();
//		if (theList.isEmpty()) {
//			return null;
//		}
//
//		float[] v = createVector(blips);
//		float[] nv = MathUtils.normalizeVector(v, mins, maxs);
//
//
//		int locCloseRange = PropertyHolder.getInstance()
//				.getLocationCloseRange();
//		List<AssociativeDataSorter> tmpList = new ArrayList<AssociativeDataSorter>();
//		for (Iterator<AssociativeData> iterator = theList.iterator(); iterator
//				.hasNext();) {
//			AssociativeData associativeData = (AssociativeData) iterator.next();
//
//			// if in range
//			if (lastLoc != null && !(this instanceof FloorSelector)) {
//				if (Math.abs((associativeData.getX() - (lastLoc.x))) <= locCloseRange
//						&& Math.abs((associativeData.getY() - (lastLoc.y))) <= locCloseRange) {
//					double d = associativeData.normalDistance(v, nv);
//					tmpList.add(new AssociativeDataSorter(associativeData, d));
//				}
//			} else {
//				double d = associativeData.normalDistance(v, nv);
//				tmpList.add(new AssociativeDataSorter(associativeData, d));
//			}
//		}
//
//		Collections.sort(tmpList, new Comparator<AssociativeDataSorter>() {
//			@Override
//			public int compare(AssociativeDataSorter lhs,
//					AssociativeDataSorter rhs) {
//				return lhs.compare(rhs);
//			}
//		});
//
//		setClosePoints(tmpList);
//		setFloorClosePoints(tmpList);
//		mBestMatch = tmpList.get(0);
//		mSecondMatch = tmpList.size() >= 2 ? tmpList.get(1) : null;
//
//		if (tmpList.size() >= 3) {
//			return findAverage(tmpList.get(0).data, tmpList.get(1).data,
//					tmpList.get(2).data, nv, v);
//		}
//		return mBestMatch.data.point;
//	}
//
//	public AssociativeDataSorter getBestMatch() {
//		return mBestMatch;
//	}
//
//	public AssociativeDataSorter getSecondMatch() {
//		return mSecondMatch;
//	}
//
//	private PointF findAverage(AssociativeData result, AssociativeData result1,
//			AssociativeData result2, float[] nv, float[] v) {
//		PointF pt = new PointF();
//		float d1 = result.normalDistance(v, nv);
//		float d2 = result1.normalDistance(v, nv);
//		// double d3 = result2.normalDistance(v,nv);
//		float sum = d1 + d2;
//
//		// pt.x = (float) (((1.0 - d1 / sum) * result.point.x + (1.0 - d2 / sum)
//		// * result1.point.x + (1.0 - d3 / sum) * result2.point.x) / 2.0);
//		// pt.y = (float) (((1.0 - d1 / sum) * result.point.y + (1.0 - d2 / sum)
//		// * result1.point.y + (1.0 - d3 / sum) * result2.point.y) / 2.0);
//		pt.x = (float) ((((sum - d1) / sum) * result.point.x + ((sum - d2) / sum)
//				* result1.point.x));
//		pt.y = (float) ((((sum - d1) / sum) * result.point.y + ((sum - d2) / sum)
//				* result1.point.y));
//
//		return pt; // result.point;
//	}
//
//	private float[] createVector(List<WlBlip> blips) {
//		float[] result = new float[INDEX_MAP.size()];
//		for (WlBlip blip : blips) {
//			int index = getArrayPosition(blip.BSSID);
//			if (index != -1) {
//				result[index] = blip.level;
//			}
//		}
//		return result;
//	}
//
//	public static AsociativeMemoryLocator getInstance() {
//		if (instance == null) {
//			instance = new AsociativeMemoryLocator();
//		}
//
//		return instance;
//	}
//
//	public List<AssociativeDataSorter> getClosePoints() {
//		return closePoints;
//	}
//
//	public List<AssociativeDataSorter> getFloorClosePoints() {
//		return floorclosePoints;
//	}
//
//	public void setClosePoints(List<AssociativeDataSorter> sorted) {
//		closePoints.clear();
//		for (AssociativeDataSorter element : sorted) {
//			closePoints.add(element);
//			if (closePoints.size() == PropertyHolder.getInstance().getK())
//				return;
//		}
//	}
//
//	public void setFloorClosePoints(List<AssociativeDataSorter> sorted) {
//		floorclosePoints.clear();
//		for (AssociativeDataSorter element : sorted) {
//			floorclosePoints.add(element);
//			if (floorclosePoints.size() == PropertyHolder.getInstance()
//					.getFloorSelectionK())
//				break;
//		}
//	}
//
//	public float getZeroValue() {
//		return zeroValue;
//	}
//
//	public void setZeroValue() {
//		if (mins == null) {
//			this.zeroValue = 1;
//			return;
//		}
//		float result = 1;
//		for (int i = 0; i < mins.length; i++) {
//			if (mins[i] < result) {
//				result = mins[i];
//			}
//		}
//		result -= 10;
//		this.zeroValue = result;
//	}
//
//	public static String getFileName() {
//		return FILE_NAME;
//	}
//
//	public static String getBinFileName() {
//		return FILE_BIN_NAME;
//	}
//
//	public float[] getMins() {
//		return mins;
//	}
//
//	public void setMins(float[] mins) {
//		this.mins = mins;
//	}
//
//	public float[] getMaxs() {
//		return maxs;
//	}
//
//	public void setMaxs(float[] maxs) {
//		this.maxs = maxs;
//	}
//
//	public void setTheList(List<AssociativeData> theList) {
//		this.theList = theList;
//	}
//
//	public void setINDEX_MAP(Map<String, Integer> iNDEX_MAP) {
//		INDEX_MAP = iNDEX_MAP;
//	}
//
//	public void setSsidnames(List<String> ssidnames) {
//		this.ssidnames = ssidnames;
//	}
//
//	public PointF getAvePoint() {
//		float sumx = 0;
//		float sumy = 0;
//		List<AssociativeDataSorter> points = new ArrayList<AssociativeDataSorter>();
//		if (getClosePoints() != null && getClosePoints().size() > 1) {
//			List<AssociativeDataSorter> lpoints = getClosePoints();
//			if (lpoints != null) {
//				points.addAll(lpoints);
//			}
//			for (AssociativeDataSorter p : points) {
//				sumx += p.data.point.x;
//				sumy += p.data.point.y;
//			}
//			float ax = 0;
//			float ay = 0;
//			if (points.size() > 0) {
//				ax = sumx / points.size();
//				ay = sumy / points.size();
//			}
//			PointF avePt = new PointF(ax, ay);
//			if (lastAverage != null) {
//				double distancefromlast = MathUtils
//						.distance(avePt, lastAverage)
//						/ FacilityConf.getInstance().getPixelsToMeter();
//				int averagerange = PropertyHolder.getInstance()
//						.getAverageRange();
//				if (distancefromlast > averagerange) {
//					avePt.x = (avePt.x + lastAverage.x) / 2;
//					avePt.y = (avePt.y + lastAverage.y) / 2;
//				}
//			}
//			lastAverage = avePt;
//			return lastAverage;
//		} else
//			return null;
//
//	}
//
//	public List<AssociativeData> getTheList() {
//		return theList;
//	}
//
//	public Map<String, Integer> getINDEX_MAP() {
//		return INDEX_MAP;
//	}
//
//	public List<String> getSsidnames() {
//		return ssidnames;
//	}
//
//}

