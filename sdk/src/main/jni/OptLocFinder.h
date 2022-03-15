/*
 * OptLocFinder.h
 *
 *  Created on: 29  2013
 *      Author: Owner
 */

#ifndef OPTLOCFINDER_H_
#define OPTLOCFINDER_H_

#include <vector>
#include <map>
#include <list>
#include <set>
#include <ctype.h>
//#include "spatial/point_multiset.h" //XXX spatial added
//#include "spatial/region_iterator.h" //XXX spatial added
#include "kdtree.h"

#include <string>
#include "AssociativeData.h"
#include "AssociativeDataSorter.h"
#include "Location.h"
#include "WlBlip.h"
#include "MathUtils.h"

using namespace std;
typedef map<string, double> innerMap;

#define UNKNOWN_FLOOR -100
#define UNKNOWN_ZONE_GROUP "UNKNOWN_ZONE_GROUP"
#define FLOOR_GROUPS_PASS_THR_PARAM "floor_group_pass_thr"

class groupFloorInfo {
public:
    string m_groupdID;
    int m_floorLevel;
    float m_levelTrashhold;
    float m_level_avg_threshold;
    float m_distance;
    string m_reverseGroupID;
    list<string> m_beaconList;
    float m_weigth;
    bool m_passTest;
    string m_type;
    string m_geofence_id;

    void calculateWeights(list<WlBlip> &blips, int floorGroupPassThr);
};

class Param {
public:
    string name;
    string value;

    int getValueAsInt();
};


class OptLocFinder {

private:
    Location lastpt; //PointF lastpt = null;
    string FILE_NAME;

    string matrixBinFileName;

    string FILE_BIN_NAME;
    string FILE_FLOOR_BIN_NAME;
    string FILE_FLOOR_SWITCH_GROUPS_NAME;
    string FILE_LOCATION_GROUPS_NAME;
    string FILE_FLOOR_TXT_NAME;

    //fstream  matrixBinFile;

    //private static AsociativeMemoryLocator instance;

    list<AssociativeData> theList;
    map<string, int> INDEX_MAP;
    list<string> ssidnames;

    list<AssociativeDataSorter> closePoints;
    list<AssociativeDataSorter> floorclosePoints;    //XXX spatial added: comment this line
    vector<float> mins;
    vector<float> maxs;

    bool isFirstLoad;
    float zeroValue;
    AssociativeDataSorter mBestMatch;
    AssociativeDataSorter mSecondMatch;
    Location lastAverage; //PointF lastAverage;
    int LoadedFloor;
    string LoadedFacility;
    // vector<string> ssidfilter;

    //typedef spatial::point_multiset<2,  AssociativeData, spatial::paren_less<AssociativeData> > kdtree; //XXX spatial added
    //kdtree tree; //XXX spatial added
    void *kdimtree;

    map<string, Param> m_params;

    // ADIA support switchFloor new Algo

    map<string, groupFloorInfo> m_groups;
    // ADIA END
    // ============ MB zone group
    map<string, groupFloorInfo> m_zone_groups;
    // ============ end MB zone group

    map<string, groupFloorInfo> location_groups;
    map<string, Param> location_params;

    static bool instanceFlag;
    static OptLocFinder *single;
    //   OptLocFinder();
    string appDirPath;

    void initIndexMap();

    void loadDataList(map<string, innerMap> &pointsMap);

    vector<float> normalizeVector(vector<float> &v, vector<float> &min, vector<float> &max);

    int getArrayPosition(const string &bssid);

    void updateMinMax(vector<float> &v);

    void normalizeList(vector<float> &min, vector<float> &max);


    double distance(const Location &p, const Location &p1);

    vector<float> createVector(const list<WlBlip> &blips);

    static bool compareAssociativeData(AssociativeDataSorter first, AssociativeDataSorter second);

    void findAverage(AssociativeData &result, AssociativeData &result1, AssociativeData &result2,
                     vector<float> &nv, vector<float> &v, Location &loc);

    void findClosestPoint(list<WlBlip> &blips, Location &loc, bool isFirstTime);

    void findFloorSelectClosestPoint(list<WlBlip> &blips, bool isFirstTime); //XXX spatial added

    void getAvePoint(Location &loc);

    void loadKdTree();//XXX spatial added

    vector<string> ssidfilter;
    int LOCATION_CLOSE_RANGE;
//		  bool IS_FLOOR_SELECTION;
    int K;
    float PIXELS_TO_METER;
    int AVERAGE_RANGE;
    int FLOORS_COUNT;

    //XXX new added
    float CLOSE_DEVICES_THRESHOLD;
    float CLOSE_DEVICES_WEIGHT;
    int K_TOP_LEVELS_THR;
    int FIRST_TIME_K_TOP_LEVELS_THR;
    int LEVEL_LOWER_BOUND;


    void loadSwitchFloorGroupsFile(const string &ofile);

    void loadLocationGroupsFile(const string &ofile);
    // set<string> switchFloorFilter;



public:
    bool IS_FLOOR_SELECTION;
    bool IS_NEW_FLOOR_SELECTION_ALG;

    OptLocFinder();

    static OptLocFinder *getInstance();

    static void releaseInstance();

    //OptLocFinder();
    virtual ~OptLocFinder();

    void load(string facility, int floor, bool isselectfloor, bool isBin);

    void findLocation(list<WlBlip> &blips, Location &loc, bool isFirstTime);

    void getLastLocation(Location &loc);

    bool saveBin();

    void setAppDirPath(const string &appDirPath);

    void loadBin(string facility, int floor);

    int newGetFloorByBlips(list<WlBlip> &blips);

    // ============ MB zone group
    bool IS_ZONE_GROUP_CONF_LOADED;

    int getZoneGroupByBlips(list<WlBlip> &blips);

    void loadZoneGroupsFile(const string &ofile);
    // ============end  MB zone group

    int getFloorByBlips(list<WlBlip> &blips, bool isFirstTime);

    int selectFloorByBlips(list<WlBlip> &blips, bool isFirstTime);

    float getZeroValue();

    void initParams(string appDirPath, int locationCloseRange, int k, float pixelsToMeter,
                    int averageRange, vector<string> &ssidsFitr, int floorscount);

    void initParams(string APPDirPath, int locationCloseRange, int k, float pixelsToMeter,
                    int averageRange, vector<string> &ssidsFitr, int floorscount, string scantype);

    void initParams(string APPDirPath, int locationCloseRange, int k, float pixelsToMeter,
                    int averageRange, vector<string> &ssidsFitr, int floorscount, string scantype,
                    float closeDevicesThreshold, float closeDeviceWeight, int kTopLevelThr);

    void initParams(string APPDirPath, int locationCloseRange, int k, float pixelsToMeter,
                    int averageRange, vector<string> &ssidsFitr, int floorscount, string scantype,
                    float closeDevicesThreshold, float closeDeviceWeight, int kTopLevelThr,
                    int levelLowerBound);

    void initParams(string APPDirPath, int locationCloseRange, int k, float pixelsToMeter,
                    int averageRange, vector<string> &ssidsFitr, int floorscount, string scantype,
                    float closeDevicesThreshold, float closeDeviceWeight, int kTopLevelThr,
                    int levelLowerBound, int firstTimekTopLevelThr);


    bool initParamsWithValidation(string APPDirPath, int locationCloseRange, int k,
                                  float pixelsToMeter, int averageRange, vector<string> &ssidsFitr,
                                  int floorscount, string scantype, float closeDevicesThreshold,
                                  float closeDeviceWeight, int kTopLevelThr, int levelLowerBound,
                                  int firstTimekTopLevelThr);


    void setZeroValue();

    Location getLastAverage();

    void setLastAverage(Location &lastAverage);

    void restLastAverage();

    bool ignored(string ssid);


    const list<AssociativeDataSorter> &getClosePoints();

    void setClosePoints(list<AssociativeDataSorter> &sorted);

    void setFloorClosePoints(
            list<AssociativeDataSorter> &sorted); //XXX spatial added: comment this line


    const list<AssociativeData> &getTheList();

    int getTheListSize();

    int getSsidnamesSize();

    const list<string> &getSsidnames();

    string getFileName();

    string getGroupIdByBlips(list<WlBlip> &blips);

    void findLocationInsideGeofence(list<WlBlip> &blips, Location &loc, float topLeftX,
                                    float topLeftY, float bottomRightX, float bottomRightY);


};


#endif /* OPTLOCFINDER_H_ */



//package com.mlins.locator;
//
//import java.io.BufferedInputStream;
//import java.io.BufferedOutputStream;
//import java.io.BufferedReader;
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.FileNotFoundException;
//import java.io.FileOutputStream;
//import java.io.FileReader;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.ObjectInputStream;
//import java.io.ObjectOutputStream;
//import java.io.OutputStream;
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.Comparator;
//import java.util.HashMap;
//import java.util.Iterator;
//import java.util.List;
//import java.util.Map;
//import java.util.Map.Entry;
//import java.util.Set;
//
//import android.annotation.SuppressLint;
//import android.graphics.PointF;
//
//import com.com.mlins.utils.FacilityConf;
//import com.com.mlins.utils.MathUtils;
//import com.com.mlins.utils.PropertyHolder;
//import com.mlins.wireless.WlBlip;

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

