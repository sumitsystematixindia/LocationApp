package com.mlins.project;

import android.graphics.Bitmap;

import com.google.android.gms.maps.model.LatLng;
import com.mlins.overlay.CampusOverlay;
import com.mlins.utils.CampusLevelResDownloader;
import com.mlins.utils.DownloadUtils;
import com.mlins.utils.FacilityConf;
import com.mlins.utils.FacilityContainer;
import com.mlins.utils.MathUtils;
import com.mlins.utils.ParametersConfigsUtils;
import com.mlins.utils.PropertyHolder;
import com.mlins.utils.ServerConnection;
import com.mlins.utils.gis.Location;
import com.mlins.utils.logging.Log;
import com.spreo.nav.interfaces.ILocation;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Campus {
    // private List<Poi> poisList = new ArrayList<Poi>();
    private static String facilitiesFilename = "facilities.json";
    private static String kmlFileName = "campus.kml";
    private String id = null;
    private String name = null;
    private double centerLatitude = 0;
    private double centerLongtitude = 0;
    private float width = 0;
    private float height = 0;
    private Bitmap campusMap = null;
    // private List<GisLine> gisLatLonList = new ArrayList<GisLine>();
    private Map<String, FacilityConf> facilitiesConfMap = null;
    private List<CampusOverlay> overlaysList = new ArrayList<CampusOverlay>();
    private int radius = 1000;
    private double distance_from_kml = 10;
    private double reroute_min_distance = 20;
    private double play_instruction_distance = 7;
    private double end_of_route_radius = 7;
    private double defaultLocationLat = 0;
    private double defaultLocationLon = 0;
    private LatLng defaultLatlng = new LatLng(0, 0);
    private int countForReroute = 1;
    private boolean useFloorsTiles = false;
    private String webInterfaceUrl = null;


    public Campus() {
        setFacilitiesConfMap(new HashMap<String, FacilityConf>());
    }

    public Campus(String id, String name, double centerLatitude,
                  double centerLongtitude, float width, float height) {
        super();
        this.id = id;
        this.name = name;
        this.centerLatitude = centerLatitude;
        this.centerLongtitude = centerLongtitude;
        this.width = width;
        this.height = height;
        setFacilitiesConfMap(new HashMap<String, FacilityConf>());
    }

    public boolean addFacility(FacilityConf facConf) {
        if (facConf == null)
            return false;
        String facId = facConf.getId();
        if (!getFacilitiesConfMap().containsKey(facId)) {
            getFacilitiesConfMap().put(facId, facConf);
            return true;
        }
        return false;
    }

    public Location getDefaultCampusLocation(){
        Location result = new Location(defaultLatlng);
        result.setCampusId(id);
        return result;
    }

    public JSONObject getAsJson() {

        JSONObject jsonObj = new JSONObject();
        try {

            try {
                jsonObj.put("id", id);
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                jsonObj.put("name", name);
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {

                JSONObject locJsonObj = new JSONObject();
                locJsonObj.put("latitude", centerLatitude);
                locJsonObj.put("longtitude", centerLongtitude);
                jsonObj.put("location", locJsonObj);

            } catch (Exception e) {
                e.printStackTrace();
            }

            try {

                JSONObject dimentionsObj = new JSONObject();
                dimentionsObj.put("width", width);
                dimentionsObj.put("height", height);
                jsonObj.put("dimentions", dimentionsObj);

            } catch (Exception e) {
                e.printStackTrace();
            }

            // try {
            // JSONArray latLonGis = campus.getJSONArray("gis");
            // parseLatLonGis(latLonGis);
            // } catch (Exception e) {
            // e.printStackTrace();
            // }
            //
            // try {
            // JSONArray pois = campus.getJSONArray("pois");
            // parsePois(pois);
            // } catch (Exception e) {
            // e.printStackTrace();
            // }

            try {
                JSONArray facilities = getFacilitiesAsJson();
                if (facilities != null) {
                    jsonObj.put("facilities", facilities);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        } catch (Throwable t) {
            t.printStackTrace();
        }

        return jsonObj;

    }

    private JSONArray getFacilitiesAsJson() {

        JSONArray jsonArr = new JSONArray();
        if (getFacilitiesConfMap() == null)
            return null;
        if (getFacilitiesConfMap().size() == 0)
            return null;

        for (FacilityConf f : getFacilitiesConfMap().values()) {
            JSONObject facJsonObj = f.getAsJson();
            if (facJsonObj != null) {
                jsonArr.put(facJsonObj);
            }
        }
        if (jsonArr.length() == 0)
            return null;

        return jsonArr;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getCenterLatitude() {
        return centerLatitude;
    }

    public void setCenterLatitude(float centerLatitude) {
        this.centerLatitude = centerLatitude;
    }

    public double getCenterLongtitude() {
        return centerLongtitude;
    }

    public void setCenterLongtitude(float centerLongtitude) {
        this.centerLongtitude = centerLongtitude;
    }

    public float getWidth() {
        return width;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public Bitmap getCampusMap() {
        return campusMap;
    }

    public void setCampusMap(Bitmap campusMap) {
        this.campusMap = campusMap;
    }

    public Map<String, FacilityConf> getFacilitiesConfMap() {
        return facilitiesConfMap;
    }

    public void setFacilitiesConfMap(Map<String, FacilityConf> facilitiesConfMap) {
        this.facilitiesConfMap = facilitiesConfMap;
    }

    public FacilityConf getFacilityConf(String facilityId) {
        return (facilitiesConfMap != null) ? facilitiesConfMap.get(facilityId) : null;
    }

    public boolean parseJson(JSONObject campusjson) {
        boolean parsed = false;
        try {
            id = campusjson.getString("id");
            name = campusjson.getString("name");
            centerLatitude = campusjson.getDouble("lat");
            centerLongtitude = campusjson.getDouble("lon");
            radius = campusjson.getInt("radius");
            distance_from_kml = campusjson.getDouble("distance_from_kml");
            reroute_min_distance = campusjson.getDouble("reroute_min_distance");
            play_instruction_distance = campusjson.getDouble("play_instruction_distance");
            end_of_route_radius = campusjson.getDouble("end_of_route_radius");
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            defaultLocationLat = campusjson.getDouble("default_location_lat");
            defaultLocationLon = campusjson.getDouble("default_location_lon");
        } catch (Exception e) {
            defaultLocationLat = centerLatitude;
            defaultLocationLon = centerLongtitude;
        }

        defaultLatlng = new LatLng(defaultLocationLat, defaultLocationLon);

        // load overlays meta-data
        try {
            JSONArray overlaysJsonArray = campusjson.getJSONArray("overlays");
            for (int i = 0; i < overlaysJsonArray.length(); i++) {
                try {
                    JSONObject joverlayObj = overlaysJsonArray.getJSONObject(i);
                    CampusOverlay o = new CampusOverlay(joverlayObj.toString(2));
                    o.setCampusId(id);
                    overlaysList.add(o);
                    //String url= o.getUri();
                    //CampusLevelResDownloader.getCInstance().addMd5(url, o.getMd5());
                } catch (Throwable t) {
                    //t.printStackTrace();
                }
            }
        } catch (Throwable t) {
            //t.printStackTrace();
        }

        try {
            countForReroute = campusjson.getInt("count_for_reroute");
        } catch (Throwable t) {

        }

        try {
            useFloorsTiles = campusjson.getInt("use_floors_tiles") == 1;
        } catch (JSONException e) {
            useFloorsTiles = false;
        }

        try {
            String baseurl = "webapp_url";
            webInterfaceUrl = campusjson.getString(baseurl + "_" + getLanguage());
        } catch (Throwable t1) {
            try {
                webInterfaceUrl = campusjson.getString("webapp_url");
            } catch (Throwable t2) {

            }
        }

        if (id != null && name != null) {
            parsed = true;
        }

        return parsed;
    }

    private String getLanguage() {
        String result = "en";
        if (PropertyHolder.getInstance().getAppLanguage().equals("hebrew")) {
            result = "he";
        } else if (PropertyHolder.getInstance().getAppLanguage().equals("arabic")) {
            result = "ar";
        } else if (PropertyHolder.getInstance().getAppLanguage().equals("russian")) {
            result = "ru";
        } else if (PropertyHolder.getInstance().getAppLanguage().equals("spanish")) {
            result = "es";
        }
        return result;
    }


    public void Parse(String config) {
        try {
            JSONTokener tokener = new JSONTokener(config);
            JSONObject json = (JSONObject) tokener.nextValue();
            ParseKml(config, tokener, json);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void ParseKml(String config, JSONTokener tokener, JSONObject json2) {
        try {
            JSONObject json = json2;
            JSONArray kmlarray = json.getJSONArray("kml");
            JSONObject kmlobj = kmlarray.getJSONObject(0);
            String kml = kmlobj.getString("kmlconf");

            try {
                String kml_md5 = kmlobj.getString("kmlconf_md5");
                CampusLevelResDownloader.getCInstance().addMd5(kml, kml_md5);
            } catch (Exception e) {
                e.printStackTrace();
            }
            /**
             String kmlurl = ServerConnection.getResourcesUrl() + kml;
             byte[] buff = ServerConnection.getInstance().getResourceBytes(kmlurl);
             if (buff != null && buff.length > 0)
             {
             File dir = PropertyHolder.getInstance().getCampusDir();
             File kmlfile = new File(dir, kml);
             DownloadUtils.writeLocalCopy(kmlfile, buff);
             }
             */


        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    public void downloadRes() {

        Log.getInstance().info("com.mlins.downloading.DownloadAllCampusesTask", "downloadCampusRes Enter");

        List<String> allUrls = new ArrayList<String>();

        allUrls.add(ServerConnection.getInstance().translateCampusResUrl(kmlFileName));

        List<String> overlaysMapUri = getMapOverlaysUrls();
        if (overlaysMapUri != null && overlaysMapUri.size() > 0) {
            allUrls.addAll(overlaysMapUri);
        }

        int numberOfUrls = allUrls.size();
        String[] murls = new String[numberOfUrls];
        for (int i = 0; i < numberOfUrls; i++) {
            murls[i] = allUrls.get(i);
        }

        CampusLevelResDownloader.getCInstance().onDemandDownload(murls);
        saveCampusResLocalCopy();

        Log.getInstance().info("com.mlins.downloading.DownloadAllCampusesTask", "downloadCampusRes Exit");
    }


    private List<String> getMapOverlaysUrls() {

        if (overlaysList == null || overlaysList.size() == 0) {
            return null;
        }

        List<String> overlaysUrls = new ArrayList<String>();

        for (CampusOverlay o : overlaysList) {
            if (o != null && o.getUri() != null) {
                String url = o.getUri(); //ServerConnection.getInstance().translateCampusResUrl(o.getUri());
                CampusLevelResDownloader.getCInstance().addMd5(url, o.getMd5());
                overlaysUrls.add(ServerConnection.getInstance().translateCampusResUrl(url));
            }

        }

        return overlaysUrls;

    }

    public void saveCampusResLocalCopy() {
        Log.getInstance().info("com.mlins.downloading.saveCampusResLocalCopy", "saveCampusResLocalCopy Enter");

        saveKml();
        //saveMapOverlays();

        Log.getInstance().info("com.mlins.downloading.saveCampusResLocalCopy", "saveCampusResLocalCopy Exit");
    }
    /*
	private void saveMapOverlays() {
		
		if(overlaysList==null || overlaysList.size()==0){
			return;
		}
		
		File dir  = PropertyHolder.getInstance().getCampusDir();
		File overlaysDir = new File(dir,"spreo_map_overlay");
		
		if(!overlaysDir.exists()){
			overlaysDir.mkdirs();
		}
		
		for(MapOverlay o:overlaysList){
			try {
				if(o!=null){
				File f = new File(dir, o.getUri());
				byte[] data = CampusLevelResDownloader.getCInstance().getLocalCopy( o.getUri());
				if(data!=null){
						o.setBlob(data);
						CampusLevelResDownloader.getCInstance().writeLocalCopy(f, data);	
				 }
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		
	}
	*/

    public List<CampusOverlay> getOverlaysList() {

//		File dir  = PropertyHolder.getInstance().getCampusDir();

        for (CampusOverlay o : overlaysList) {
            try {
                if (o != null) {

                    String url = ServerConnection.getInstance().translateCampusResUrl(o.getUri());
                    CampusLevelResDownloader.getCInstance().addMd5(o.getUri(), o.getMd5());
                    byte[] data = CampusLevelResDownloader.getCInstance().getUrl(url);
//				File f = new File(dir, o.getUri());
//				byte[] data = CampusLevelResDownloader.getCInstance().getLocalCopy( o.getUri());
                    if (data != null) {
                        o.setBlob(data);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return overlaysList;
    }

    public boolean saveKml() {


        boolean ans = false;
        try {
            String name = PropertyHolder.getInstance().getCampusDir()
                    .getAbsolutePath()
                    + File.separator
                    + kmlFileName;

            File f = new File(name);
            byte[] data = CampusLevelResDownloader.getCInstance().getLocalCopy(kmlFileName);

            if (data != null) {
                String check = new String(data);
                check = check.toLowerCase();
                if (!check.equals("") && !check.contains("<html") && !check.contains("<header")) {
                    ans = CampusLevelResDownloader.getCInstance().writeLocalCopy(f, data);

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ans;
    }

    public boolean parseText(String line) {
        boolean parsed = false;
        String[] fields = line.split("\t");
        try {
            id = fields[0];
            name = fields[1];
            centerLatitude = Double.parseDouble(fields[2]);
            centerLongtitude = Double.parseDouble(fields[3]);
        } catch (Exception e) {
            // TODO: handle exception
        }

        if (id != null && name != null) {
            parsed = true;
        }

        return parsed;

    }

    public void downloadFacilitiesFile() {
        String projectid = PropertyHolder.getInstance().getProjectId();
        String campusid = id;
        String server = PropertyHolder.getInstance().getServerName();
        String url = server + "res/" + projectid + "/" + campusid + "/" + facilitiesFilename;
        File dir = PropertyHolder.getInstance().getCampusDir();
        File file = new File(dir, facilitiesFilename);
        byte[] data = ServerConnection.getInstance().getResourceBytes(url);
        if (data != null && data.length > 0) {
            DownloadUtils.writeLocalCopy(file, data);
        }
        loadFacilities();
    }

    public boolean downloadFacilitiesJsonRes() {
        boolean res = false;
        String projectid = PropertyHolder.getInstance().getProjectId();
        String campusid = id;
        String server = PropertyHolder.getInstance().getServerName();
        String url = server + "res/" + projectid + "/" + campusid + "/" + facilitiesFilename;
        File dir = PropertyHolder.getInstance().getCampusDir();
        File file = new File(dir, facilitiesFilename);
        byte[] data = ServerConnection.getInstance().getResourceBytes(url);
        if (data != null && data.length > 0) {
            //XXX CHECK VALIDITY
            String dataSantityCheck = new String(data);
            if (dataSantityCheck.contains("facilities")) {
                res = DownloadUtils.writeLocalCopy(file, data);
            }
        }

        return res;
        //loadFacilities();
    }


    public void loadZipFacilities() {


        String jsonString = "";

        String url = ServerConnection.getResourcesUrl() + facilitiesFilename;
        byte[] cont = CampusLevelResDownloader.getCInstance().getUrl(url);

        if (cont != null && cont.length > 0) {
            jsonString = new String(cont);
        }

        try {
            JSONObject facilitiesjson = new JSONObject(jsonString);
            JSONArray jsonArray = facilitiesjson.getJSONArray("facilities");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject facilityjson = jsonArray.getJSONObject(i);
                FacilityConf fac = new FacilityConf();
                if (parseFacilityJson(facilityjson, fac)) {
                    addFacilityConf(fac);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
//		}

    }

    private void addFacilityConf(FacilityConf fac) {
        fac.setCampusID(id);
        facilitiesConfMap.put(fac.getId(), fac);
    }

    public void loadFacilities() {

        if (PropertyHolder.useZip) {
            loadZipFacilities();
        } else {
            String jsonString = "";
            File dir = PropertyHolder.getInstance().getCampusDir();

            if (dir == null) {
                dir = new File(PropertyHolder.getInstance().getProjectDir(), id);
                if (!dir.exists()) {
                    dir.mkdirs();
                }
            }
            File f = new File(dir, facilitiesFilename);
            BufferedReader inlocal = null;
            if (f.exists()) {
                try {
                    inlocal = new BufferedReader(new FileReader(f));
                    String line = null;
                    while ((line = inlocal.readLine()) != null) {
                        jsonString += line;
                        //					FacilityConf fac = new FacilityConf();
                        //					if (parseFacility(line, fac)) {
                        //						facilitiesConfMap.put(fac.getId(), fac);
                        //					}
                    }
                    inlocal.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                try {
                    JSONObject facilitiesjson = new JSONObject(jsonString);
                    JSONArray jsonArray = facilitiesjson.getJSONArray("facilities");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject facilityjson = jsonArray.getJSONObject(i);
                        FacilityConf fac = new FacilityConf();
                        if (parseFacilityJson(facilityjson, fac)) {
                            addFacilityConf(fac);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    public boolean parseFacilityJson(JSONObject facilityjson, FacilityConf fac) {
        boolean parsed = false;
        try {

            fac.setId(facilityjson.getString("id"));
            fac.setName(facilityjson.getString("name"));
            fac.setCenterLatitude(facilityjson.getDouble("lat"));
            fac.setCenterLongtitude(facilityjson.getDouble("lon"));

            try {
                String suffix = getLanguageSuffix();
                if (suffix != null) {
                    fac.setName(facilityjson.getString("fname_" + suffix));
                }
            } catch (Throwable t) {
                t.printStackTrace();
            }

            try {

                JSONObject covJson = facilityjson.getJSONObject("convert_location_params");
                covJson.getDouble("conv_w");
                fac.setMapWidth(covJson.getDouble("conv_w"));
                fac.setMapHight(covJson.getDouble("conv_h"));

                fac.setConvRectTLlat(covJson.getDouble("conv_rect_tl_lat"));
                fac.setConvRectTLlon(covJson.getDouble("conv_rect_tl_lon"));


                fac.setConvRectTRlat(covJson.getDouble("conv_rect_tr_lat"));
                fac.setConvRectTRlon(covJson.getDouble("conv_rect_tr_lon"));


                fac.setConvRectBLlat(covJson.getDouble("conv_rect_bl_lat"));
                fac.setConvRectBLlon(covJson.getDouble("conv_rect_bl_lon"));

                fac.setConvRectBRlat(covJson.getDouble("conv_rect_br_lat"));
                fac.setConvRectBRlon(covJson.getDouble("conv_rect_br_lon"));

                fac.setRot_angle((float) covJson.getDouble("rot_angle"));
                fac.setEntranceFloor(covJson.getInt("entrance_floor"));


            } catch (Throwable t) {
                System.out.println(t);
            }


            FacilityContainer.getInstance().setSelected(fac); //FacilityConf.releaseInstance();
//			PoiDataHelper.releaseInstance();
            PropertyHolder.getInstance().setFacilityID(fac.getId());
            fac.Parse();
            ParametersConfigsUtils.load();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (fac.getId() != null && fac.getName() != null) {
            parsed = true;
        }

        return parsed;
    }

    public boolean parseFacility(String line, FacilityConf fac) {
        boolean parsed = false;
        String[] fields = line.split("\t");
        try {
            fac.setId(fields[0]);
            fac.setName(fields[0]);
            fac.setCenterLatitude(Double.parseDouble(fields[1]));
            fac.setCenterLongtitude(Double.parseDouble(fields[2]));
        } catch (Exception e) {
            // TODO: handle exception
        }

        if (fac.getId() != null && fac.getName() != null) {
            parsed = true;
        }

        return parsed;
    }

    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    public double getDistance_from_kml() {
        return distance_from_kml;
    }

    public void setDistance_from_kml(double distance_from_kml) {
        this.distance_from_kml = distance_from_kml;
    }

    public double getReroute_min_distance() {
        return reroute_min_distance;
    }

    public void setReroute_min_distance(double reroute_min_distance) {
        this.reroute_min_distance = reroute_min_distance;
    }

    public double getPlay_instruction_distance() {
        return play_instruction_distance;
    }

    public void setPlay_instruction_distance(double play_instruction_distance) {
        this.play_instruction_distance = play_instruction_distance;
    }

    public double getEnd_of_route_radius() {
        return end_of_route_radius;
    }

    public void setEnd_of_route_radius(double end_of_route_radius) {
        this.end_of_route_radius = end_of_route_radius;
    }

    public LatLng getDefaultLatlng() {
        return defaultLatlng;
    }

    public int getCountForReroute() {
        return countForReroute;
    }

    public void setCountForReroute(int countForReroute) {
        this.countForReroute = countForReroute;
    }

    public boolean isUsingFloorTiles(){
        return useFloorsTiles;
    }


    public boolean contains(ILocation location) {
        return Location.isInDoor(location) ||
                MathUtils.distance(getCenterLatitude(), getCenterLongtitude(), location.getLat(), getCenterLongtitude()) < radius;
    }

    private String getLanguageSuffix () {
        String result = null;
        String locale = PropertyHolder.getInstance().getAppLanguage();

        if (locale != null) {
            String localeLang = locale.toLowerCase();
            if (localeLang.contains("en")) {
                result = "en";
            } else if (localeLang.contains("he")) {
                result = "he";
            } else if (localeLang.contains("ar")) {
                result = "ar";
            } else if (localeLang.contains("ru")) {
                result = "ru";
            } else if (localeLang.contains("sp")) {
                result = "es";
            } else if (localeLang.contains("ja")) {
                result = "ja";
            }
        }

        return result;
    }

    public String getWebInterfaceUrl() {
        return webInterfaceUrl;
    }

    public void setWebInterfaceUrl(String webInterfaceUrl) {
        this.webInterfaceUrl = webInterfaceUrl;
    }
}
