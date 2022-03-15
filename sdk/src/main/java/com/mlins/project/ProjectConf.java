package com.mlins.project;

import android.text.format.DateFormat;

import com.google.android.gms.maps.model.LatLng;
import com.mlins.dualmap.PolygonsContainer;
import com.mlins.dualmap.PolygonObject;
import com.mlins.labels.LabelsContainer;
import com.mlins.nav.utils.MultiNavUtils;
import com.mlins.project.bridges.BridgeData;
import com.mlins.utils.CampusLevelResDownloader;
import com.mlins.utils.Cleanable;
import com.mlins.utils.DownloadUtils;
import com.mlins.utils.FacilityConf;
import com.mlins.utils.Lookup;
import com.mlins.utils.MathUtils;
import com.mlins.utils.PoiType;
import com.mlins.utils.PoisContainer;
import com.mlins.utils.PropertyHolder;
import com.mlins.utils.ServerConnection;
import com.mlins.utils.gis.Location;
import com.spreo.nav.interfaces.ILabel;
import com.spreo.nav.interfaces.ILocation;
import com.spreo.nav.interfaces.IPoi;
import com.spreo.sdk.poi.PoisUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class ProjectConf implements Cleanable {

    //	private Campus selectedCampus = null;
    private static final String campusesFilename = "campuses.json";
    private String companyName = null;
    private String projectId = null;
    // private List<Contact> contactsList = null;
    // private List<User> usersList = null;
    private Map<String, Campus> campusesMap = new HashMap<String, Campus>();

    //private List<IPoi> allPoisList = new ArrayList<IPoi>();
    private PoisContainer poisContainer = new PoisContainer();

    private PolygonsContainer polygonsContainer = new PolygonsContainer();

    private LabelsContainer labelsContainer = new LabelsContainer();

    public static ProjectConf getInstance() {
        return Lookup.getInstance().get(ProjectConf.class);
    }

    public static void releaseInstance() {
        Lookup.getInstance().remove(ProjectConf.class);
    }

    public void clean() {
        getCampusesMap().clear();
    }

//	public ProjectConf(String companyId, String companyName) {
//		super();
//		this.companyId = companyId;
//		this.companyName = companyName;
//		campusesMap = new HashMap<String, Campus>();
//	}

    public boolean addCampus(Campus c) {
        if (c == null)
            return false;
        String campusId = c.getId();
        if (!getCampusesMap().containsKey(campusId)) {
            getCampusesMap().put(campusId, c);
            return true;
        }
        return false;
    }

    public String getAsJson() {
        JSONObject jsonObj = new JSONObject();
        try {
            try {
                jsonObj.put("name", companyName);
            } catch (Throwable t) {
                t.printStackTrace();
            }

            try {
                jsonObj.put("id", getProjectId());
            } catch (Throwable t) {
                t.printStackTrace();
            }

            try {
                JSONArray campuses = getCampusesAsJson();
                if (campuses != null) {
                    jsonObj.put("campuses", campuses);
                }
            } catch (Throwable t) {
                t.printStackTrace();
            }

        } catch (Throwable t) {
            t.printStackTrace();
        }

        return jsonObj.toString();
    }

    public JSONArray getCampusesAsJson() {
        JSONArray jsonArr = new JSONArray();
        if (getCampusesMap() == null)
            return null;
        if (getCampusesMap().size() == 0)
            return null;

        for (Campus campus : getCampusesMap().values()) {
            JSONObject facJsonObj = campus.getAsJson();
            if (facJsonObj != null) {
                jsonArr.put(facJsonObj);
            }
        }
        if (jsonArr.length() == 0)
            return null;

        return jsonArr;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }


    public Map<String, Campus> getCampusesMap() {
        return campusesMap;
    }

    public void setCampusesMap(Map<String, Campus> campusesMap) {
        this.campusesMap = campusesMap;
    }

//	public Campus getCampus(LatLng campusloc) {
//		Campus result = null;
//		for (Campus o : campusesMap.values()) {
//			double lat = campusloc.latitude;
//			double lon = campusloc.longitude;
//			if (lat == o.getCenterLatitude() && lon == o.getCenterLongtitude()) {
//				result = o;
//				break;
//			}
//		}
//		return result;
//	}

    public Campus getSelectedCampus() {
        return campusesMap.get(PropertyHolder.getInstance().getCampusId());

    }

    public Campus getCampus(String id) {
        return campusesMap.get(id);
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String generateId() {
        String result = null;
        int min = 1;
        int max = 100000;
        Random r = new Random();
        int rnum = r.nextInt(max - min + 1) + min;
        Date d = new Date();
        CharSequence s = DateFormat.format("yyyy-MM-dd hh:mm:ss", d.getTime());
        result = s.toString() + " " + rnum;
        return result;
    }

    public void downloadCampusesFile() {

        String server = PropertyHolder.getInstance().getServerName();
        String projectid = PropertyHolder.getInstance().getProjectId();
        String url = server + "res/" + projectid + "/" + campusesFilename;
        File dir = PropertyHolder.getInstance().getProjectDir();
        File file = new File(dir, campusesFilename);

        byte[] data = CampusLevelResDownloader.getCInstance().getLocalCopy(url);
        if (data == null || data.length == 0) {
            data = ServerConnection.getInstance().getResourceBytes(url);
        }

        if (data != null && data.length > 0) {
            DownloadUtils.writeLocalCopy(file, data);
        }
        loadCampuses();
    }


    public boolean downloadCampusesJsonRes() {

        boolean res = false;
        String server = PropertyHolder.getInstance().getServerName();
        String projectid = PropertyHolder.getInstance().getProjectId();
        String url = server + "res/" + projectid + "/" + campusesFilename;
        File dir = PropertyHolder.getInstance().getProjectDir();
        File file = new File(dir, campusesFilename);

        byte[] data = CampusLevelResDownloader.getCInstance().getLocalCopy(url);
        if (data == null || data.length == 0) {
            data = ServerConnection.getInstance().getResourceBytes(url);
        }

        if (data != null && data.length > 0) {
            //XXX CHECK VALIDITY
            String dataSantityCheck = new String(data);
            if (dataSantityCheck.contains("campuses")) {
                res = DownloadUtils.writeLocalCopy(file, data);
            }
        }

        return res;


    }

    public void loadZipCampuses() {

        try {

            String url = ServerConnection.getProjectResourcesUrl() + campusesFilename;
            byte[] bytes = CampusLevelResDownloader.getCInstance().getUrl(url);
            if (bytes == null || bytes.length == 0) {
                return;
            }

            String jsonString = new String(bytes);

			/*
			String jsonString = "";
			
			File dir = PropertyHolder.getInstance().getZipProjectDir();
			File f = new File(dir, campusesFilename);
			BufferedReader inlocal = null;
			if (f.exists()) {
				try {
					inlocal = new BufferedReader(new FileReader(f));
					String line = null;
					while ((line = inlocal.readLine()) != null) {
						jsonString += line;
	//					Campus campus = new Campus();
	//					if (campus.parseText(line)) {
	//						campusesMap.put(campus.getId(), campus);
	//					}
					}
					inlocal.close();
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			*/
            try {
                JSONObject campusesjson = new JSONObject(jsonString);
                JSONArray jsonArray = campusesjson.getJSONArray("campuses");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject campusjson = jsonArray.getJSONObject(i);
                    Campus campus = new Campus();
                    if (campus.parseJson(campusjson)) {
                        campusesMap.put(campus.getId(), campus);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            //}
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }


    public void loadCampuses() {

        if (PropertyHolder.useZip) {
            loadZipCampuses();
        } else {
            String jsonString = "";
            File dir = PropertyHolder.getInstance().getProjectDir();
            File f = new File(dir, campusesFilename);
            BufferedReader inlocal = null;
            if (f.exists()) {
                try {
                    inlocal = new BufferedReader(new FileReader(f));
                    String line = null;
                    while ((line = inlocal.readLine()) != null) {
                        jsonString += line;
                        //					Campus campus = new Campus();
                        //					if (campus.parseText(line)) {
                        //						campusesMap.put(campus.getId(), campus);
                        //					}
                    }
                    inlocal.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                try {
                    JSONObject campusesjson = new JSONObject(jsonString);
                    JSONArray jsonArray = campusesjson.getJSONArray("campuses");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject campusjson = jsonArray.getJSONObject(i);
                        Campus campus = new Campus();
                        if (campus.parseJson(campusjson)) {
                            campusesMap.put(campus.getId(), campus);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public PoisContainer getPoisContainer() {
        return poisContainer;
    }

    public void setPoisContainer(PoisContainer poisContainer) {

        if (poisContainer == null) {
            return;
        }

        this.poisContainer = poisContainer;
    }


    public List<IPoi> getAllFloorPoisList(String campusId,
                                          String facilityId, int floor) {
        return poisContainer.getAllFloorPoisList(campusId, facilityId, floor);
    }


    public List<IPoi> getAllFacilityPoisList(String campusId,
                                             String facilityId) {
        return poisContainer.getAllFacilityPoisList(campusId, facilityId);
    }


    public List<IPoi> getAllCampusPoisList(String campusId) {
        return poisContainer.getAllCampusPoisList(campusId);
    }

    public List<IPoi> getAllPoisList() {
        return poisContainer.getAllPois();
    }

//	public void setAllPoisList(List<IPoi> allPoisList) {
//		this.allPoisList = allPoisList;
//	}

    public void setVisiblePoisCategories(List<String> categoriesnames) {
        poisContainer.setVisiblePoisCategories(categoriesnames);
    }

    public List<IPoi> getAllCampusExternalPoisList(String campusId) {
        return poisContainer.getAllCampusExternalPoisList(campusId);
    }

    public List<PoiType> getPoiCategories() {
        return poisContainer.getCategoriesList();
    }

    public List<IPoi> getAllEntrancesAndExits() {
        List<IPoi> list = new ArrayList<IPoi>();
        if (poisContainer != null) {
            list = poisContainer.getAllEntrancesAndExits();
        }
        return list;
    }

    public List<IPoi> getAllExternalPois() {
        List<IPoi> list = new ArrayList<IPoi>();
        if (poisContainer != null) {
            list = poisContainer.getAllExternalPois();
        }
        return list;
    }


    public void loadExternalPoisKDimensionalTree() {
        if (poisContainer != null) {
            poisContainer.loadExternalPoisKDimensionalTree();
        }
    }

    public ArrayList<IPoi> getExternalInRangePois(Location currLoc, float rangeInMeters, int maxReturnedCount) {
        ArrayList<IPoi> list = null;
        if (poisContainer != null) {
            list = poisContainer.getExternalInRangePois(currLoc, rangeInMeters, maxReturnedCount);
        }
        return list;
    }

    //============labels
    public LabelsContainer getLabelsContainer() {
        return labelsContainer;
    }

    public void setLabelsContainer(LabelsContainer labelsContainer) {

        if (labelsContainer == null) {
            return;
        }

        this.labelsContainer = labelsContainer;
    }


    public List<ILabel> getAllFloorLabelsList(String campusId,
                                              String facilityId, int floor) {
        return labelsContainer.getAllFloorLabelsList(campusId, facilityId, floor);
    }


    public List<ILabel> getAllFacilityLabelsList(String campusId,
                                                 String facilityId) {
        return labelsContainer.getAllFacilityLabelsList(campusId, facilityId);
    }


    public List<ILabel> getAllCampusLabelsList(String campusId) {
        return labelsContainer.getAllCampusLabelsList(campusId);
    }

    public List<ILabel> getAllLabelsList() {
        return labelsContainer.getAllLabels();
    }

    public String getFloorPickerFacilityId() {
        String result = null;
        try {
            Campus campus = ProjectConf.getInstance().getSelectedCampus();
            if (campus != null) {
                Map<String, FacilityConf> tmpmap = campus.getFacilitiesConfMap();
                int max = 0;
                for (FacilityConf o : tmpmap.values()) {
                    int numberoffloors = o.getFloorDataList().size();
                    if (numberoffloors > max) {
                        max = numberoffloors;
                        String id = o.getId();
                        if (id != null) {
                            result = id;
                        }
                    }
                }
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
        return result;
    }

    public FacilityConf getFacilityConfById(String campusId, String facilityId) {
        FacilityConf result = null;
        try {
            Campus campus = ProjectConf.getInstance().getCampus(campusId);
            if (campus != null) {
                Map<String, FacilityConf> facmap = campus.getFacilitiesConfMap();
                if (facmap != null) {
                    result = facmap.get(facilityId);
                }
            }

        } catch (Throwable t) {
            t.printStackTrace();
        }
        return result;
    }

    public IPoi getClosestParking (IPoi poi) {
        IPoi result = null;
        try {
            if (poi != null) {
                String associatedparkingid =  poi.getAssociatedParkingId();
                if (associatedparkingid != null && !associatedparkingid.isEmpty()) {
                    result = PoisUtils.getPoiById(associatedparkingid);
                }
                if (result == null) {
                    Campus campus = getSelectedCampus();
                    if (campus != null) {
                        String campusid = campus.getId();
                        if (campusid != null) {
                            List<IPoi> pois = getAllCampusPoisList(campusid);
                            LatLng a = null;
                            if (poi.getPoiNavigationType().equals("external")) {
                                a = new LatLng(poi.getPoiLatitude(), poi.getPoiLongitude());
                            } else if (poi.getPoiNavigationType().equals("internal")) {
                                String facid = poi.getFacilityID();
                                if (facid != null) {
                                    FacilityConf fac = campus.getFacilityConf(facid);
                                    a = convertToLatLng(poi.getX(), poi.getY(), fac);
                                }
                            }
                            if (a != null && pois != null) {
                                double mind = Double.MAX_VALUE;
                                for (IPoi o : pois) {
                                    if (o.getPoiID() != null && o.getPoiID().contains("prk")) {
                                        LatLng b = null;
                                        if (o.getPoiNavigationType().equals("external")) {
                                            b = new LatLng(o.getPoiLatitude(), o.getPoiLongitude());
                                        } else if (o.getPoiNavigationType().equals("internal")) {
                                            String facid = o.getFacilityID();
                                            if (facid != null) {
                                                FacilityConf fac = campus.getFacilityConf(facid);
                                                b = convertToLatLng(o.getX(), o.getY(), fac);
                                            }
                                        }
                                        if (b != null) {
                                            double d = MathUtils.distance(a, b);
                                            if (d < mind) {
                                                mind = d;
                                                result = o;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }

        return result;
    }

    private LatLng convertToLatLng (float x, float y, FacilityConf fac) {
        LatLng result = MultiNavUtils.convertToLatlng((double)x, (double)y, fac);
        return result;
    }

    public FacilityConf getFacilityConfById(IPoi poi){
        return getFacilityConfById(poi.getCampusID(), poi.getFacilityID());
    }

    public FacilityConf getFacilityConf(ILocation location){
        Location.ensureInDoor(location);
        return getFacilityConfById(location.getCampusId(), location.getFacilityId());
    }

    private BridgeData bridges;

    public BridgeData getBridges(){
        if(bridges == null)
            bridges = new BridgeData();

        return bridges;
    }

    public void loadBridges() {
        bridges = new BridgeData();
    }

    public List<PolygonObject> getProjectPolygons() {
        return polygonsContainer.getProjectPolygons();
    }

    public List<PolygonObject> getExternalPolygons() {
        return polygonsContainer.getExternalPolygons();
    }

    public List<PolygonObject> getFailityPolygons(String failityid) {
        return polygonsContainer.getFailityPolygons(failityid);
    }

    public List<PolygonObject> getFloorPolygons(String failityid, int floor) {
        return polygonsContainer.getFloorPolygons(failityid, floor);
    }

    public void setVisiblePolygons(List<String> ids) {
        polygonsContainer.setVisiblePolygons(ids);
    }

    public List<PolygonObject> getPolygonsByParamValue(String param, String value) {
        return polygonsContainer.getPolygonsByParamValue(param, value);
    }
}
