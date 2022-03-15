package com.mlins.utils;

import android.graphics.Bitmap;

import com.google.android.gms.maps.model.LatLng;
import com.mlins.locator.LocationLocator;
import com.mlins.utils.gis.GisData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FacilityConf {

    private static final String MD5_OF_MD5_FILE_NAME = "md5_res_list.md5";
    private static boolean isCalibrationState = false;


    List<FloorData> mData = new ArrayList<FloorData>();
    int selectedIndex = -1;
    private String bannersconfFileName = null;
    private String costumereventsconfFileName = null;
    private String switchconfFileName = null;
    private String poisGalleryconfFileName = null;
    //private static FacilityConf instance = null;
    private List<String> iconList = new ArrayList<String>();


    private boolean isSyncMAtrixLoad = true;
    private String geofenceFileName = null;
    private List<String> FacilityLabelesFileNameList = new ArrayList<String>();
    private int entranceFloor = 0;

    private String campusID;
    private String id = null;
    private String name = null;
    private double centerLatitude = 0f;
    private double centerLongtitude = 0f;
    private float dimentionWidth = 0;
    private float dimentionHeight = 0;
    private Bitmap facilityMap = null;
    private String md5Ofmd5 = null;

    private boolean upToDate = false;
    // private List<GeoFence> geoFencesList = new ArrayList<GeoFence>();

    private String bssidsFileName = null;
    private String paramsFileName = null;
    private String floorSelectionFileName = null;
    private String groupsConfFileName = null;
    private String beaconsPlacementConfFileName = null;
    private String labelsConfFileName = null;
    //params
    private int floorsTopKlevelsThr = 3;
    private int floorselectionLevelLowerBound = -87;
    private int minimumDevicesForEntrance = 1;
    private int blipLevelForEntrance = -150;
    private int loggerMode = 0;
    private int floorSelectionBlips = 5;
    private float locatorRadius = 6.5f;
    private int exitNoDetectionCount = 5;
    private int exitMinBleDetectionDevices = 2;
    private int exitMinBleDetectionLevel = -100;
    private int topKlevelsThr = 4;
    private float distanceFromNavPath = 15;
    private float endOfRouteRadius = 5;
    private float playInstructionDistance = 5;
    private Map<Integer, String> soundToFloorMap = new HashMap<Integer, String>();


    //== loc conversion params

    private double mapWidth = 0;
    private double mapHight = 0;

    private double convRectTLlat = 0;
    private double convRectTLlon = 0;

    private double convRectTRlat = 0;
    private double convRectTRlon = 0;

    private double convRectBLlat = 0;
    private double convRectBLlon = 0;


    private double convRectBRlat = 0;
    private double convRectBRlon = 0;

    private float rot_angle = 0;

    private float poiForInstructionRadius = 20;

    private int NdkCloseRange = 300;

    private boolean projectLocation = true;

    private float placementThresh = 10;


    private int locationLevelThreshold = -127;

    private int floorFilterThreshold = -80;

    private int distanceForTurnBack = 4;

    private int bridgeDevicesForEntrance = 2;
    private int bridgeLevelForEntrance = -95;


    public FacilityConf() {
        super();
        if (!isCalibrationState && !PropertyHolder.useZip) {
            loadMd5Ofmd5();
        }
    }

    public FacilityConf(String facilityId) {
        this.id = facilityId;
        if (!isCalibrationState && !PropertyHolder.useZip) {
            loadMd5Ofmd5();
        }
    }

    public FacilityConf(String id, String name) {
        this.id = id;
        this.name = name;
        if (!isCalibrationState && !PropertyHolder.useZip) {
            loadMd5Ofmd5();
        }
    }

    public static void turnCalibrationState() {
        isCalibrationState = true;
    }

    public static boolean isCalibrationState() {
        return isCalibrationState;
    }

    public double getMapWidth() {
        return mapWidth;
    }

    public void setMapWidth(double mapWidth) {
        this.mapWidth = mapWidth;
    }

    public double getMapHight() {
        return mapHight;
    }

    public void setMapHight(double mapHight) {
        this.mapHight = mapHight;
    }

    public double getConvRectTLlat() {
        return convRectTLlat;
    }

    public void setConvRectTLlat(double convRectTLlat) {
        this.convRectTLlat = convRectTLlat;
    }

    public double getConvRectTLlon() {
        return convRectTLlon;
    }

    public void setConvRectTLlon(double convRectTLlon) {
        this.convRectTLlon = convRectTLlon;
    }

    public double getConvRectTRlat() {
        return convRectTRlat;
    }

    public void setConvRectTRlat(double convRectTRlat) {
        this.convRectTRlat = convRectTRlat;
    }

    public double getConvRectTRlon() {
        return convRectTRlon;
    }

    public void setConvRectTRlon(double convRectTRlon) {
        this.convRectTRlon = convRectTRlon;
    }

    public double getConvRectBLlat() {
        return convRectBLlat;
    }

    public void setConvRectBLlat(double convRectBLlat) {
        this.convRectBLlat = convRectBLlat;
    }

    public double getConvRectBLlon() {
        return convRectBLlon;
    }

    //== loc conversion params

    public void setConvRectBLlon(double convRectBLlon) {
        this.convRectBLlon = convRectBLlon;
    }

    public double getConvRectBRlat() {
        return convRectBRlat;
    }

    public void setConvRectBRlat(double convRectBRlat) {
        this.convRectBRlat = convRectBRlat;
    }

    public double getConvRectBRlon() {
        return convRectBRlon;
    }

//	public static void setInstance(FacilityConf conf) {
//		instance = conf;
//	}

//	public static FacilityConf getInstance() {
//		if (instance == null) {
//			instance = new FacilityConf();
//		}
//		return instance;
//	}
//
//	public static void releaseInstance() {
//		if (instance != null) {
//			instance.clean();
//			instance = null;
//		}
//	}

    public void setConvRectBRlon(double convRectBRlon) {
        this.convRectBRlon = convRectBRlon;
    }

    public String getFloorTitle(int nfloor) {
        FloorData data = getFloor(nfloor);
        if (data != null)
            return data.getTitle();
        return "";
    }

    private void clean() {
        mData.clear();
    }

    public boolean isSyncMAtrixLoad() {
        return isSyncMAtrixLoad;
    }

    public void setSyncMAtrixLoad(boolean isSyncMAtrixLoad) {
        this.isSyncMAtrixLoad = isSyncMAtrixLoad;
    }

    public List<FloorData> getFloorDataList() {
        return mData;
    }

    public void addFloor(FloorData floor) {
        mData.add(floor);
    }

    public FloorData getFloor(int index) {
        FloorData result = null;
        if (index >= 0 && index < mData.size()) { // XXX APK PROTECTING FROM
            // ARRAYINDEXOFBOUNDEXCEPTION
            result = mData.get(index);
        }
        return result;
    }

    public int getSelectedFloor() {
        return selectedIndex;
    }

    public FloorData getSelectedFloorData() {
        if (getSelectedFloor() == -1)
            return null;
        return getFloor(getSelectedFloor());
    }

    public void setSelected(int index) {
        if (index == selectedIndex)
            return;
        selectedIndex = index;
        String floor = String.valueOf(index);
        PropertyHolder.getInstance().setFloorDir(floor);

        FloorData data = getSelectedFloorData();
//		if (data != null) {
//			PropertyHolder.getInstance().setPixelsToMeter(data.pixelsToMeter);
//			PropertyHolder.getInstance().setMapRotation(data.rotation);
//		}
    }

    public void updateFloorData() {
        if (isSyncMAtrixLoad) {
            LocationLocator.getInstance().syncInit();
        } else {
            LocationLocator.getInstance().asyncInit();
        }
        GisData.getInstance().loadGisLines();
    }

    public float getPixelsToMeter() {
        FloorData v = getSelectedFloorData();
        if (v == null && !mData.isEmpty()) {
            v = mData.get(0);
        }
        if (v != null)
            return v.pixelsToMeter;
        return 1;
    }

    public void setPixelsToMeter(float p2m) {
    }

    public float getFloorRotation() {
        FloorData v = getSelectedFloorData();
        if (v != null)
            return v.rotation;
        return 0;
    }

    public void setFloorRotation(float rot) {
        for (FloorData o : mData) {
            o.setRotation(rot);
        }
    }

    public void ParseFloors(String config) {
        ParseFloors(config, null, null);
    }

    public void ParseFloors(String config, JSONTokener tokener, JSONObject json2) {

        String lang = PropertyHolder.getInstance().getAppLanguage();


        boolean includeInParsing = true;
        mData.clear();
        // selectedIndex = 0;
        try {


            if (tokener == null && json2 == null) {
                tokener = new JSONTokener(config);
                json2 = (JSONObject) tokener.nextValue();
                includeInParsing = false;
            }

            JSONObject json = json2;

            JSONArray floors = json.getJSONArray("floors");
            for (int i = 0; i < floors.length(); i++) {
                JSONObject floor = floors.getJSONObject(i);

                String map = "";
                String thumb = "";
                String title = "";

                map = floor.getString("floormap");

                try {
                    if (includeInParsing) {
                        String map_md5 = floor.getString("floormap_md5");
                        ResourceDownloader.getInstance().addMd5(map, map_md5);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                thumb = floor.getString("floorthumb");
                try {
                    if (includeInParsing) {
                        String thumb_md5 = floor.getString("floorthumb_md5");
                        ResourceDownloader.getInstance().addMd5(thumb, thumb_md5);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }


                // Nir: added title in other languages here:
                title = "";
                String localeLang = ""; // "en";
                if (lang != null) {
                    localeLang = lang.toLowerCase();
                }
                if (localeLang.contains("ar")) {
                    try {
                        title = floor.getString("title_ar");
                    } catch (Exception e) {
                        title = floor.getString("title");
                    }
                } else if (localeLang.contains("en")) {
                    try {
                        title = floor.getString("title_en");
                    } catch (Exception e) {
                        title = floor.getString("title");
                    }
                } else if (localeLang.contains("he")) {
                    try {
                        title = floor.getString("title_he");
                    } catch (Exception e) {
                        title = floor.getString("title");
                    }
                } else if (localeLang.contains("ru")) {
                    try {
                        title = floor.getString("title_ru");
                    } catch (Exception e) {
                        title = floor.getString("title");
                    }
                } else if (localeLang.contains("ja")) {
                    try {
                        title = floor.getString("title_ja");
                    } catch (Exception e) {
                        title = floor.getString("title");
                    }
                }

                if (title == null || title.length() == 0) {
                    title = floor.getString("title");
                }


                // String title = floor.getString("title");

                // try {
                // title = new String(title.getBytes("ISO-8859-1"));
                // } catch (UnsupportedEncodingException e1) {
                // e1.printStackTrace();
                // }
                int floorIndex = -100;
                try {
                    floorIndex = floor.getInt("index");
                } catch (Exception e) {
                    floorIndex = -100;
                }

                String scale = floor.optString("scale", "1:75");
                String gis = "";
                if (includeInParsing) {
                    try {
                        gis = floor.getString("gis");

                        if (includeInParsing) {
                            String gis_md5 = floor.getString("gis_md5");
                            ResourceDownloader.getInstance().addMd5(gis, gis_md5);
                        }
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                }

                String[] ratio = scale.split(":");
                float p2m = Float.parseFloat(ratio[1]) / Float.parseFloat(ratio[0]);
                int rot = floor.optInt("rotation", -15);

                FloorData data = new FloorData(floorIndex, map, thumb, title, p2m, rot, gis);

                try {
                    String poidata = floor.getString("poidata");
                    data.setPoi(poidata);
                    if (includeInParsing) {
                        String poidata_md5 = floor.getString("poidata_md5");
                        ResourceDownloader.getInstance().addMd5(poidata, poidata_md5);
                    }
                } catch (Exception e) {
                }
                try {
                    String poidataar = floor.getString("poidataar");
                    data.setPoiar(poidataar);
                    if (includeInParsing) {
                        String poidataar_md5 = floor.getString("poidataar_md5");
                        ResourceDownloader.getInstance().addMd5(poidataar, poidataar_md5);
                    }
                } catch (Exception e) {
                }
                try {
                    String poidataen = floor.getString("poidataen");
                    data.setPoien(poidataen);
                    if (includeInParsing) {
                        String poidataen_md5 = floor.getString("poidataen_md5");
                        ResourceDownloader.getInstance().addMd5(poidataen, poidataen_md5);
                    }
                } catch (Exception e) {
                }
                try {
                    String poidatahe = floor.getString("poidatahe");
                    data.setPoihe(poidatahe);
                    if (includeInParsing) {
                        String poidatahe_md5 = floor.getString("poidatahe_md5");
                        ResourceDownloader.getInstance().addMd5(poidatahe, poidatahe_md5);
                    }
                } catch (Exception e) {
                }
                try {
                    String poidataru = floor.getString("poidataru");
                    data.setPoiru(poidataru);
                    if (includeInParsing) {
                        String poidataru_md5 = floor.getString("poidataru_md5");
                        ResourceDownloader.getInstance().addMd5(poidataru, poidataru_md5);
                    }
                } catch (Exception e) {
                }
                try {
                    String poidataes = floor.getString("poidataes");
                    data.setPoies(poidataes);
                    if (includeInParsing) {
                        String poidataes_md5 = floor.getString("poidataes_md5");
                        ResourceDownloader.getInstance().addMd5(poidataes, poidataes_md5);
                    }
                } catch (Exception e) {
                }
                try {
                    String poidataes = floor.getString("poidataja");
                    data.setPoies(poidataes);
                    if (includeInParsing) {
                        String poidataes_md5 = floor.getString("poidataja_md5");
                        ResourceDownloader.getInstance().addMd5(poidataes, poidataes_md5);
                    }
                } catch (Exception e) {
                }


                if (PropertyHolder.getInstance().isDownloadMatrixes()) {
                    String matrix = "";
                    try {
                        String matrixPrefex = PropertyHolder.getInstance().getMatrixFilePrefix();
                        String matrixTag = "matrix";
                        if (!"".equals(matrixPrefex)) {
                            matrixTag += "_" + matrixPrefex;
                        }

                        matrix = floor.getString(matrixTag);
                        if (includeInParsing) {
                            String matrix_md5 = floor.getString(matrixTag + "_md5");
                            ResourceDownloader.getInstance().addMd5(matrix, matrix_md5);
                        }
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                    data.setMatrix(matrix);
                }

                try {
                    float stickyradius = (float) floor.getDouble("sticky_radius");
                    data.stickyRadius = stickyradius;
                } catch (Exception e) {

                }

                try {
                    JSONArray polyarray = floor.getJSONArray("polygon");
                    if (polyarray != null && polyarray.length() > 0) {
                        List<LatLng> poly = new ArrayList<LatLng>();
                        for (int j = 0; j < polyarray.length(); j++) {
                            JSONObject point = polyarray.getJSONObject(j);
                            if (point != null) {
                                double lat = point.getDouble("lat");
                                double lon = point.getDouble("lon");
                                LatLng latlng = new LatLng(lat, lon);
                                poly.add(latlng);
                            }
                        }
                        data.setPolygon(poly);
                    }
                } catch (Exception e) {

                }

                mData.add(data);
            }

            reArrangeFloorsDataByIndex();

            // selectedIndex = 0;
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void reArrangeFloorsDataByIndex() {
        Collections.sort(mData);
    }

    public void ParseBanners(String config, JSONTokener tokener, JSONObject json2) {
        try {
            JSONObject json = json2;
            JSONArray banners = json.getJSONArray("banners");
            JSONObject banner = banners.getJSONObject(0);
            try {
                String banneruri = banner.getString("bannerconf");
                setBannersconfFileName(banneruri);

                String banneruri_md5 = banner.getString("bannerconf_md5");
                ResourceDownloader.getInstance().addMd5(banneruri, banneruri_md5);

            } catch (Exception e) {
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void ParseEvents(String config, JSONTokener tokener, JSONObject json2) {
        try {
            JSONObject json = json2;
            JSONArray enents = json.getJSONArray("events");
            JSONObject event = enents.getJSONObject(0);
            try {
                String eventsuri = event.getString("eventsconf");
                setCostumereventsconfFileName(eventsuri);

                String eventsuri_md5 = event.getString("eventsconf_md5");
                ResourceDownloader.getInstance().addMd5(eventsuri, eventsuri_md5);

            } catch (Exception e) {
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void ParseSwitch(String config, JSONTokener tokener, JSONObject json2) {
        try {
            JSONObject json = json2;
            JSONArray swts = json.getJSONArray("switch");
            JSONObject switchconfig = swts.getJSONObject(0);

            try {
                String switchsuri = switchconfig.getString("switchconf");
                setSwitchconfFileName(switchsuri);

                String switchuri_md5 = switchconfig.getString("switchconf_md5");
                ResourceDownloader.getInstance().addMd5(switchsuri, switchuri_md5);

            } catch (Exception e) {
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void ParseGallery(String config, JSONTokener tokener, JSONObject json2) {
        try {
            JSONObject json = json2;
            JSONArray gallery = json.getJSONArray("gallery_conf");
            JSONObject gconfig = gallery.getJSONObject(0);

            try {
                String guri = gconfig.getString("gallery");
                setPoisGalleryconfFileName(guri);

                String guri_md5 = gconfig.getString("gallery_md5");
                ResourceDownloader.getInstance().addMd5(guri, guri_md5);

            } catch (Exception e) {
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void parseParams(JSONTokener tokener, JSONObject json) {
        try {
            JSONArray params = json.getJSONArray("facility_params");
            JSONObject paramsconfig = params.getJSONObject(0);

            try {
                String uri = paramsconfig.getString("params");
                setParametersConfFileName(uri);

                String uri_md5 = paramsconfig.getString("params_md5");
                ResourceDownloader.getInstance().addMd5(uri, uri_md5);

            } catch (Exception e) {
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getParametersConfFileName() {
        return paramsFileName;

    }

    private void setParametersConfFileName(String uri) {
        this.paramsFileName = uri;

    }

    private void parseFloorSelection(JSONTokener tokener, JSONObject json) {

        if (PropertyHolder.getInstance().isDownloadMatrixes()) {
            try {

                JSONArray swts = json.getJSONArray("floors_selection");
                JSONObject config = swts.getJSONObject(0);

                try {
                    String floorsMatrixTag = "floorsmatrix";
                    String matrixPrefix = PropertyHolder.getInstance().getMatrixFilePrefix();
                    if (!"".equals(matrixPrefix)) {
                        floorsMatrixTag += "_" + matrixPrefix;
                    }
                    String uri = config.getString(floorsMatrixTag);
                    setFloorSelectionFileName(uri);
                    String uri_md5 = config.getString(floorsMatrixTag + "_md5");
                    ResourceDownloader.getInstance().addMd5(uri, uri_md5);

                } catch (Exception e) {
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

    public String getFloorSelectionFileName() {
        return floorSelectionFileName;
    }

    public void setFloorSelectionFileName(String url) {
        this.floorSelectionFileName = url;
    }

    public void ParseBssids(JSONTokener tokener, JSONObject json) {
        try {

            JSONArray swts = json.getJSONArray("bssids_conf");
            JSONObject bssidsconfig = swts.getJSONObject(0);

            try {
                String uri = bssidsconfig.getString("bssids_conf");
                setBssidsConfFileName(uri);

                String uri_md5 = bssidsconfig.getString("bssids_conf_md5");
                ResourceDownloader.getInstance().addMd5(uri, uri_md5);

            } catch (Exception e) {
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void setBssidsConfFileName(String uri) {
        this.bssidsFileName = uri;

    }

    public void ParseGeofence(String config, JSONTokener tokener, JSONObject json2) {
        try {
            JSONObject json = json2;
            JSONArray geos = json.getJSONArray("geofence_conf");
            JSONObject geoConfig = geos.getJSONObject(0);

            try {
                String geosuri = geoConfig.getString("geofence");
                setGeofenceConfFileName(geosuri);

                String geouri_md5 = geoConfig.getString("geofence_md5");
                ResourceDownloader.getInstance().addMd5(geosuri, geouri_md5);

            } catch (Exception e) {
                e.printStackTrace();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void oldParseGeofence(String config, JSONTokener tokener, JSONObject json2) {
        try {
            JSONObject json = json2;
            JSONArray geos = json.getJSONArray("geofence");
            JSONObject geoConfig = geos.getJSONObject(0);

            try {
                String geosuri = geoConfig.getString("geofenceconf");
                setGeofenceConfFileName(geosuri);

                String geouri_md5 = geoConfig.getString("geofenceconf_md5");
                ResourceDownloader.getInstance().addMd5(geosuri, geouri_md5);

            } catch (Exception e) {
                e.printStackTrace();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void setGeofenceConfFileName(String geosuri) {
        this.geofenceFileName = geosuri;
    }

    public String getGeogfenceConfFileName() {
        return geofenceFileName;
    }

    public void ParseFacilityLabeles(String config, JSONTokener tokener, JSONObject json2) {
        try {
            JSONObject json = json2;
            JSONArray labels = json.getJSONArray("facilitylabeles");
            JSONObject lebelConfig = labels.getJSONObject(0);

            try {
                String labelsuri = lebelConfig.getString("facilitylabelesconf");
                addFacilityLabelesConfFileName(labelsuri);
                String labelsuri_md5 = lebelConfig.getString("facilitylabelesconf_md5");
                ResourceDownloader.getInstance().addMd5(labelsuri, labelsuri_md5);

            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                String labelsuriar = lebelConfig.getString("facilitylabelesconf_ar");
                addFacilityLabelesConfFileName(labelsuriar);
                String labelsuriar_md5 = lebelConfig.getString("facilitylabelesconf_ar_md5");
                ResourceDownloader.getInstance().addMd5(labelsuriar, labelsuriar_md5);

            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                String labelsurien = lebelConfig.getString("facilitylabelesconf_en");
                addFacilityLabelesConfFileName(labelsurien);
                String labelsurien_md5 = lebelConfig.getString("facilitylabelesconf_en_md5");
                ResourceDownloader.getInstance().addMd5(labelsurien, labelsurien_md5);

            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                String labelsurihe = lebelConfig.getString("facilitylabelesconf_he");
                addFacilityLabelesConfFileName(labelsurihe);
                String labelsurihe_md5 = lebelConfig.getString("facilitylabelesconf_he_md5");
                ResourceDownloader.getInstance().addMd5(labelsurihe, labelsurihe_md5);

            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                String labelsuriru = lebelConfig.getString("facilitylabelesconf_ru");
                addFacilityLabelesConfFileName(labelsuriru);
                String labelsuriru_md5 = lebelConfig.getString("facilitylabelesconf_ru_md5");
                ResourceDownloader.getInstance().addMd5(labelsuriru, labelsuriru_md5);

            } catch (Exception e) {
                e.printStackTrace();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void addFacilityLabelesConfFileName(String labeluri) {
        FacilityLabelesFileNameList.add(labeluri);
    }

    // private void saveResListJson(String config) {
    // try {
    // String uri =
    // PropertyHolder.getInstance().getFacilityDir().getAbsolutePath()+
    // File.separator + "md5_res_list.bin";
    // ResourceDownloader.getInstance().writeLocalCopy(uri, config.getBytes());
    // } catch (Throwable e) {
    // e.printStackTrace();
    // }
    // }

    public List<String> getFacilityLabeles() {
        return FacilityLabelesFileNameList;
    }

    private void setPoiTypesMD5(String config, JSONTokener tokener, JSONObject json2) {
        try {
            JSONObject json = json2;
            JSONArray poi = json.getJSONArray("poitypes");
            JSONObject poitypesconfig = poi.getJSONObject(0);

            try {
                String poitypesuri = poitypesconfig.getString("poiconf");
                String poitypesuri_md5 = poitypesconfig.getString("poiconf_md5");
                ResourceDownloader.getInstance().addMd5(poitypesuri, poitypesuri_md5);

            } catch (Exception e) {
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void saveMd5ofMd5(JSONObject json) {

        try {
            md5Ofmd5 = json.getString("md5_of_md5");
            String uri = ServerConnection.getResourcesUrl() + PropertyHolder.getInstance().getFacilityID() + "/" + MD5_OF_MD5_FILE_NAME;
            ResourceDownloader.getInstance().writeLocalCopy(uri, md5Ofmd5.getBytes());
            //String url = ServerConnection.getBaseUrlOfFacilityResList(PropertyHolder.getInstance().getFacilityID(), PropertyHolder.getInstance().getCampusId());
            //ResourceDownloader.getInstance().addMd5(url,md5Ofmd5);
        } catch (Throwable e) {
            e.printStackTrace();
        }

    }

    public String getMd5Ofmd5() {

        // String md5ofmd5 = null;
        //
        // try{
        // byte[] conf=
        // ResourceDownloader.getInstance().getLocalCopy(MD5_OF_MD5_FILE_NAME);
        // if(conf!= null && conf.length != 0){
        // md5ofmd5= new String(conf);
        // }
        //
        // } catch (Exception e) {
        // e.printStackTrace();
        // }
        //
        // return md5ofmd5;
        return md5Ofmd5;
    }

    private void loadMd5Ofmd5() {
        String md5Ofmd5 = null;
        try {
            byte[] conf = ResourceDownloader.getInstance().getLocalCopy(MD5_OF_MD5_FILE_NAME);
            if (conf != null && conf.length != 0) {
                md5Ofmd5 = new String(conf);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        this.md5Ofmd5 = md5Ofmd5;
    }

    public void Parse() {
        String jsonTxt = null;
        String campusid = PropertyHolder.getInstance().getCampusId();
        if (campusid != null) {
            String url = ServerConnection.getBaseUrlOfFacilityResList(id, campusid);
            byte[] data = ResourceDownloader.getInstance().getUrl(url);

            try {
                jsonTxt = new String(data, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        if (jsonTxt != null && !jsonTxt.isEmpty()) {
            Parse(jsonTxt, false);
        }


    }

    public void Parse(String config) {
        Parse(config, true);
    }

    private void Parse(String config, boolean isSaveMd5ofMd5) {
        try {

            JSONTokener tokener = null;
            JSONObject json = null;

            if (!isCalibrationState && config.equals("up_to_date")) {
                this.upToDate = true;
                String uri = ServerConnection.getBaseUrlOfFacilityResList(PropertyHolder.getInstance().getFacilityID(), PropertyHolder.getInstance().getCampusId());
                byte[] conf = ResourceDownloader.getInstance().getLocalCopy(uri);
                String resjson = new String(conf);
                tokener = new JSONTokener(resjson);
                json = (JSONObject) tokener.nextValue();
            } else {
                tokener = new JSONTokener(config);
                json = (JSONObject) tokener.nextValue();
                this.upToDate = false;
                if (!isCalibrationState && isSaveMd5ofMd5) {
                    saveMd5ofMd5(json);
                }
            }

            ParseFloors(config, tokener, json);
            ParseEvents(config, tokener, json);
            ParseBanners(config, tokener, json);
            ParseSwitch(config, tokener, json);
            setPoiTypesMD5(config, tokener, json);
            ParseGeofence(config, tokener, json);
            //ParseFacilityLabeles(config, tokener, json);
            ParseBssids(tokener, json);
            parseParams(tokener, json);
            parseGroupsConf(tokener, json);
            parseFloorSelection(tokener, json);
            ParseGallery(config, tokener, json);
            parseBeaconsPlacementConf(tokener, json);
            parseLabelsConf(tokener, json);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void parseGroupsConf(JSONTokener tokener, JSONObject json) {
        try {
            JSONArray groups = json.getJSONArray("floor_groups_conf");
            JSONObject groupsconfig = groups.getJSONObject(0);

            try {
                String uri = groupsconfig.getString("floor_groups");
                setGroupsConfFileName(uri);

                String uri_md5 = groupsconfig.getString("floor_groups_md5");
                ResourceDownloader.getInstance().addMd5(uri, uri_md5);

            } catch (Exception e) {
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void parseBeaconsPlacementConf(JSONTokener tokener, JSONObject json) {
        try {

            JSONArray jArr = json.getJSONArray("beacons_placement_conf");
            JSONObject jObj = jArr.getJSONObject(0);

            try {
                String uri = jObj.getString("beacons_placement");
                setBeaconsPlacementConfFileName(uri);

                String uri_md5 = jObj.getString("beacons_placement_md5");
                ResourceDownloader.getInstance().addMd5(uri, uri_md5);

            } catch (Exception e) {
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void parseLabelsConf(JSONTokener tokener, JSONObject json) {
        try {


            JSONArray jArr = json.getJSONArray("lables_conf");
            JSONObject jObj = jArr.getJSONObject(0);

            try {
                String uri = jObj.getString("lables");
                setLabelsConfFileName(uri);

                String uri_md5 = jObj.getString("lables_md5");
                ResourceDownloader.getInstance().addMd5(uri, uri_md5);

            } catch (Exception e) {
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public boolean isUpToDate() {
        return upToDate;
    }

    public void saveBanners() {
        try {
            String name = PropertyHolder.getInstance().getFacilityDir().getAbsolutePath() + File.separator + getBannersconfFileName();
            if (name == null)
                return;
            File f = new File(name);
            File bannersDir = new File(PropertyHolder.getInstance().getFacilityDir().getAbsoluteFile() + File.separator + "banners");
            bannersDir.mkdirs();
            byte[] bannersData = ResourceDownloader.getInstance().getLocalCopy(getBannersconfFileName());
            ResourceDownloader.getInstance().writeLocalCopy(f, bannersData);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void saveEvents() {
        try {
            String name = PropertyHolder.getInstance().getFacilityDir().getAbsolutePath() + File.separator + getCostumereventsconfFileName();
            if (name == null)
                return;

            File f = new File(name);
            File eventsDir = new File(PropertyHolder.getInstance().getFacilityDir().getAbsoluteFile() + File.separator + "events");
            eventsDir.mkdirs();
            byte[] eventsData = ResourceDownloader.getInstance().getLocalCopy(getCostumereventsconfFileName());
            ResourceDownloader.getInstance().writeLocalCopy(f, eventsData);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void saveSwitch() {
        try {
            String name = PropertyHolder.getInstance().getFacilityDir().getAbsolutePath() + File.separator + getSwitchconfFileName();
            if (name == null)
                return;

            File f = new File(name);
            byte[] switchData = ResourceDownloader.getInstance().getLocalCopy(getSwitchconfFileName());

            if (switchData != null) {
                String check = new String(switchData);
                check = check.toLowerCase();
                if (!check.equals("") && !check.contains("<html") && !check.contains("<header")) {
                    ResourceDownloader.getInstance().writeLocalCopy(f, switchData);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void saveGalleryConf() {
        try {

            String name = PropertyHolder.getInstance().getFacilityDir().getAbsolutePath() + File.separator + getPoisGalleryconfFileName();

            File f = new File(name);
            byte[] gData = ResourceDownloader.getInstance().getLocalCopy(getPoisGalleryconfFileName());

            if (gData != null) {
                String check = new String(gData);
                check = check.toLowerCase();
                if (!check.equals("") && !check.contains("<html") && !check.contains("<header")) {
                    ResourceDownloader.getInstance().writeLocalCopy(f, gData);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getDir() {
        return PropertyHolder.getInstance().getFacilityDir().getAbsolutePath();
    }

    public void saveBssids() {
        try {
            String name = getDir() + File.separator + getBssidsFileName();


            File f = new File(name);

            byte[] data = ResourceDownloader.getInstance().getLocalCopy(getBssidsFileName());

            if (data != null && data.length > 0) {
                ResourceDownloader.getInstance().writeLocalCopy(f, data);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void saveParametersConf() {
        try {
            String name = getDir() + File.separator + getParametersConfFileName();

            File f = new File(name);

            byte[] data = ResourceDownloader.getInstance().getLocalCopy(getParametersConfFileName());

            if (data != null && data.length > 0) {
                ResourceDownloader.getInstance().writeLocalCopy(f, data);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void saveGroupsConf() {
        try {
            String name = getDir() + File.separator + getGroupsConfFileName();

            File f = new File(name);

            byte[] data = ResourceDownloader.getInstance().getLocalCopy("android/" + getGroupsConfFileName());

            if (data != null && data.length > 0) {
                ResourceDownloader.getInstance().writeLocalCopy(f, data);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void saveGeofence() {
        try {
            if (geofenceFileName != null) {
                String name = getDir() + File.separator + geofenceFileName;

                File f = new File(name);

                byte[] geosData = ResourceDownloader.getInstance().getLocalCopy(geofenceFileName);

                if (geosData != null) {
                    String check = new String(geosData);
                    check = check.toLowerCase();
                    if (!check.equals("") && !check.contains("<html") && !check.contains("<header")) {
                        ResourceDownloader.getInstance().writeLocalCopy(f, geosData);
                    }
                }
            }

        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

//	public void  saveHalfNavSettings(String cont){
//		try {
//			String name =  getDir() + File.separator + "half_nav_settings.json";
//
//			File f = new File(name);
//			if(cont.equals("{}"))
//			{
//				return;
//			}
//			byte[] data = cont.getBytes();
//
//			if (data != null && data.length > 0) {
//				ResourceDownloader.getInstance().writeLocalCopy(f, data);
//			}
//			
//		} catch (Throwable e) {
//			e.printStackTrace();
//		}
//	}

    @Deprecated
    public void saveFacilityLabeles() {
        try {
            String foldername = getDir() + File.separator + "labels";
            File d = new File(foldername);
            if (!d.exists()) {
                d.mkdirs();
            }

            if (FacilityLabelesFileNameList != null && FacilityLabelesFileNameList.size() > 0) {

                for (String lablesFile : FacilityLabelesFileNameList) {

                    try {
                        String name = getDir() + File.separator + lablesFile;

                        File f = new File(name);
                        // if(!f.exists())
                        // // {
                        // // f.mkdirs();
                        // // }
                        byte[] labelsData = ResourceDownloader.getInstance().getLocalCopy(lablesFile);

                        if (labelsData != null) {
                            String check = new String(labelsData);
                            check = check.toLowerCase();
                            if (!check.equals("") && !check.contains("<html") && !check.contains("<header")) {
                                ResourceDownloader.getInstance().writeLocalCopy(f, labelsData);
                            }
                        }
                    } catch (Throwable t) {
                        t.printStackTrace();
                    }
                }
            }

        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public void saveMatrixes() {

        if (PropertyHolder.getInstance().isDownloadMatrixes()) {
            List<FloorData> floors = getFloorDataList();

            for (int i = 0; i < floors.size(); i++) {
                String matrixUrl = floors.get(i).getMatrix();
                byte[] data = ResourceDownloader.getInstance().getLocalCopy(matrixUrl);
                String floor = String.valueOf(i);
                File floordir = new File(PropertyHolder.getInstance().getFacilityDir(), floor);
                File dir = new File(floordir, "scan results");
                String matrixBinFileName = PropertyHolder.getInstance().getMatrixFilePrefix() + "matrix.bin";
                File matrixBinFile = new File(dir, matrixBinFileName);
                dir = matrixBinFile.getParentFile();
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                if (data != null && data.length > 0) {
                    ResourceDownloader.getInstance().writeLocalCopy(matrixBinFile, data);
                }
            }
        }
    }

    public void saveFloorSelectionMatrix() {
        if (PropertyHolder.getInstance().isDownloadMatrixes()) {
            try {
                String name = getDir() + File.separator + getFloorSelectionFileName();

                File f = new File(name);

                byte[] data = ResourceDownloader.getInstance().getLocalCopy(getFloorSelectionFileName());

                if (data != null && data.length > 0) {
                    ResourceDownloader.getInstance().writeLocalCopy(f, data);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    public void saveBeaconsPlacementConf() {
        try {
            String name = getDir() + File.separator + getBeaconsPlacementConfFileName();

            File f = new File(name);

            byte[] data = ResourceDownloader.getInstance().getLocalCopy(getBeaconsPlacementConfFileName());

            if (data != null && data.length > 0) {
                ResourceDownloader.getInstance().writeLocalCopy(f, data);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void saveLabelsConf() {
        try {
            String name = getDir() + File.separator + getLabelsConfFileName();

            File f = new File(name);

            byte[] data = ResourceDownloader.getInstance().getLocalCopy(getLabelsConfFileName());

            if (data != null && data.length > 0) {
                ResourceDownloader.getInstance().writeLocalCopy(f, data);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadCurrentFacility() {
        String jsonTxt = ServerConnection.getInstance().getResources(PropertyHolder.getInstance().getFacilityID(), PropertyHolder.getInstance().getCampusId());
        // "{'facid':'MELLERS','floors':[{'floormap':'http://medical-sentry.com/mlins/maps/floor1.png','floorthumb':'http://medical-sentry.com/mlins/maps/floor1thumb.png','title':'floor1'},{'floormap':'http://medical-sentry.com/mlins/maps/floor1.png','floorthumb':'http://medical-sentry.com/mlins/maps/floor1thumb.png','title':'floor2'}]}";
        Parse(jsonTxt);
        setSelected(entranceFloor);

    }

    public String getBannersconfFileName() {
        return bannersconfFileName;
    }

    public void setBannersconfFileName(String bannersconfFileName) {
        this.bannersconfFileName = bannersconfFileName;
    }

    public String getCostumereventsconfFileName() {
        return costumereventsconfFileName;
    }

    public void setCostumereventsconfFileName(String costumereventsconfFileName) {
        this.costumereventsconfFileName = costumereventsconfFileName;
    }

    public List<String> getIconList() {
        return iconList;
    }

    public void setIconList(List<String> iconList) {
        this.iconList = iconList;
    }

    public String getSwitchconfFileName() {
        return switchconfFileName;
    }

    public void setSwitchconfFileName(String switchconfFileName) {
        this.switchconfFileName = switchconfFileName;
    }

    public int getEntranceFloor() {
        return entranceFloor;
    }

    public void setEntranceFloor(int floor) {
        this.entranceFloor = floor;
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
                dimentionsObj.put("width", dimentionWidth);
                dimentionsObj.put("height", dimentionHeight);
                jsonObj.put("dimentions", dimentionsObj);

            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                JSONArray floors = getFloorsAsJson();
                if (floors != null) {
                    jsonObj.put("floors", floors);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        } catch (Throwable t) {
            t.printStackTrace();
        }

        return jsonObj;

    }

    public boolean addFloorData(FloorData floorData) {
        if (floorData == null)
            return false;
        if (mData.contains(floorData))
            return false;

        return mData.add(floorData);

    }

    private JSONArray getFloorsAsJson() {
        if (mData == null)
            return null;
        if (mData.size() == 0)
            return null;

        JSONArray jsonArr = new JSONArray();

        for (FloorData floorData : mData) {
            JSONObject jsonObj = floorData.getAsJson();
            if (jsonObj != null) {
                jsonArr.put(jsonObj);
            }
        }

        if (jsonArr.length() == 0)
            return null;

        return jsonArr;

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

    public void setCenterLatitude(double centerLatitude) {
        this.centerLatitude = centerLatitude;
    }

    public double getCenterLongtitude() {
        return centerLongtitude;
    }

    public void setCenterLongtitude(double centerLongtitude) {
        this.centerLongtitude = centerLongtitude;
    }

    public float getDimentionWidth() {
        return dimentionWidth;
    }

    public void setDimentionWidth(float dimentionWidth) {
        this.dimentionWidth = dimentionWidth;
    }

    public float getDimentionHeight() {
        return dimentionHeight;
    }

    public void setDimentionHeight(float dimentionHeight) {
        this.dimentionHeight = dimentionHeight;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    // public static PointF getEntranceLocation() {
    // // PointF result = new PointF(752,1054); //office
    // // PointF result = new PointF(154,890); //ushq floor 0
    // // PointF result = new PointF(491, 879); // ushq floor 6
    // PointF result = new PointF(713, 218); //short hills
    // // result = GisData.getInstance().findClosestPointOnLine(result);
    // return result;
    // }

    public Bitmap getFacilityMap() {
        return facilityMap;
    }

    public void setFacilityMap(Bitmap facilityMap) {
        this.facilityMap = facilityMap;
    }

    public String getBssidsFileName() {
        return bssidsFileName;
    }

    public String getLabelsConfFileName() {
        return labelsConfFileName;
    }

    public void setLabelsConfFileName(String labelsConfFileName) {
        this.labelsConfFileName = labelsConfFileName;
    }

    public String getBeaconsPlacementConfFileName() {
        return beaconsPlacementConfFileName;
    }

    public void setBeaconsPlacementConfFileName(String beaconsPlacementConfFileName) {
        this.beaconsPlacementConfFileName = beaconsPlacementConfFileName;
    }

    public String getGroupsConfFileName() {
        return groupsConfFileName;
    }

    public void setGroupsConfFileName(String groupsConfFileName) {
        this.groupsConfFileName = groupsConfFileName;
    }

    public String getPoisGalleryconfFileName() {
        return poisGalleryconfFileName;
    }

    public void setPoisGalleryconfFileName(String poisGalleryconfFileName) {
        this.poisGalleryconfFileName = poisGalleryconfFileName;
    }

    public int getFloorsTopKlevelsThr() {
        return floorsTopKlevelsThr;
    }

    public void setFloorsTopKlevelsThr(int floorsTopKlevelsThr) {
        this.floorsTopKlevelsThr = floorsTopKlevelsThr;
    }

    public int getFloorselectionLevelLowerBound() {
        return floorselectionLevelLowerBound;
    }

    public void setFloorselectionLevelLowerBound(
            int floorselectionLevelLowerBound) {
        this.floorselectionLevelLowerBound = floorselectionLevelLowerBound;
    }

    public int getMinimumDevicesForEntrance() {
        return minimumDevicesForEntrance;
    }

    public void setMinimumDevicesForEntrance(int minimumDevicesForEntrance) {
        this.minimumDevicesForEntrance = minimumDevicesForEntrance;
    }

    public int getBlipLevelForEntrance() {
        return blipLevelForEntrance;
    }

    public void setBlipLevelForEntrance(int blipLevelForEntrance) {
        this.blipLevelForEntrance = blipLevelForEntrance;
    }

    public int getLoggerMode() {
        return loggerMode;
    }

    public void setLoggerMode(int loggerMode) {
        this.loggerMode = loggerMode;
    }

    public int getFloorSelectionBlips() {
        return floorSelectionBlips;
    }

    public void setFloorSelectionBlips(int floorSelectionBlips) {
        this.floorSelectionBlips = floorSelectionBlips;
    }

    public float getLocatorRadius() {
        return locatorRadius;
    }

    public void setLocatorRadius(float locatorRadius) {
        this.locatorRadius = locatorRadius;
    }

    public int getExitNoDetectionCount() {
        return exitNoDetectionCount;
    }

    public void setExitNoDetectionCount(int exitNoDetectionCount) {
        this.exitNoDetectionCount = exitNoDetectionCount;
    }

    public int getExitMinBleDetectionDevices() {
        return exitMinBleDetectionDevices;
    }

    public void setExitMinBleDetectionDevices(int exitMinBleDetectionDevices) {
        this.exitMinBleDetectionDevices = exitMinBleDetectionDevices;
    }

    public int getExitMinBleDetectionLevel() {
        return exitMinBleDetectionLevel;
    }

    public void setExitMinBleDetectionLevel(int exitMinBleDetectionLevel) {
        this.exitMinBleDetectionLevel = exitMinBleDetectionLevel;
    }

    public int getTopKlevelsThr() {
        return topKlevelsThr;
    }

    public void setTopKlevelsThr(int topKlevelsThr) {
        this.topKlevelsThr = topKlevelsThr;
    }

    public void setSoundToFloorMapping(Map<Integer, String> soundToFloorMap) {
        this.soundToFloorMap = soundToFloorMap;

    }

    public String getFloorSoundUri(int floorIndex) {
        return soundToFloorMap.get(floorIndex);
    }

    public float getDistanceFromNavPath() {
        return distanceFromNavPath;
    }

    public void setDistanceFromNavPath(int distanceFromNavPath) {
        this.distanceFromNavPath = distanceFromNavPath;
    }

    public float getRot_angle() {
        return rot_angle;
    }

    public void setRot_angle(float rot_angle) {
        this.rot_angle = rot_angle;
    }

    public float getPoiForInstructionRadius() {
        return poiForInstructionRadius;
    }

    public void setPoiForInstructionRadius(float poiForInstructionRadius) {
        this.poiForInstructionRadius = poiForInstructionRadius;
    }

    public int getNdkCloseRange() {
        return NdkCloseRange;
    }

    public void setNdkCloseRange(int ndkCloseRange) {
        NdkCloseRange = ndkCloseRange;
    }

    public float getEndOfRouteRadius() {
        return endOfRouteRadius;
    }

    public void setEndOfRouteRadius(float endOfRouteRadius) {
        this.endOfRouteRadius = endOfRouteRadius;
    }

    public float getPlayInstructionDistance() {
        return playInstructionDistance;
    }

    public void setPlayInstructionDistance(float playInstructionDistance) {
        this.playInstructionDistance = playInstructionDistance;
    }

    public boolean isProjectLocation() {
        return projectLocation;
    }

    public void setProjectLocation(boolean projectLocation) {
        this.projectLocation = projectLocation;
    }

    public float getPlacementThresh() {
        return placementThresh;
    }

    public void setPlacementThresh(float placementThresh) {
        this.placementThresh = placementThresh;
    }

    public int getLocationLevelThreshold() {
        return locationLevelThreshold;
    }

    public void setLocationLevelThreshold(int locationLevelThreshold) {
        this.locationLevelThreshold = locationLevelThreshold;
    }

    public int getFloorFilterThreshold() {
        return floorFilterThreshold;
    }

    public void setFloorFilterThreshold(int floorFilterThreshold) {
        this.floorFilterThreshold = floorFilterThreshold;
    }

    public void setCampusID(String campusID) {
        this.campusID = campusID;
    }

    public String getCampusID() {
        return campusID;
    }

    public int getDistanceForTurnBack() {
        return distanceForTurnBack;
    }

    public void setDistanceForTurnBack(int distanceForTurnBack) {
        this.distanceForTurnBack = distanceForTurnBack;
    }

    public int getBridgeDevicesForEntrance() {
        return bridgeDevicesForEntrance;
    }

    public void setBridgeDevicesForEntrance(int bridgeDevicesForEntrance) {
        this.bridgeDevicesForEntrance = bridgeDevicesForEntrance;
    }

    public int getBridgeLevelForEntrance() {
        return bridgeLevelForEntrance;
    }

    public void setBridgeLevelForEntrance(int bridgeLevelForEntrance) {
        this.bridgeLevelForEntrance = bridgeLevelForEntrance;
    }
}