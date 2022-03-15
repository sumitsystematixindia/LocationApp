package com.mlins.utils;

import static android.content.ContentValues.TAG;

import com.mlins.utils.logging.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class ResPathConverter {

    private static final String THUMB_FLOOR_MAP_AUTO_PREFIX = "m_s_auto_thumb_";
    private static final int THUMB_MAP_FILE_REQ_CODE = 9;
    private static final int MAP_FILE_REQ_CODE = 8;
    private static final String FACILITES_LEVEL_DIR_NAME = "facilities";
    private static final String FLOORS_LEVEL_DIR_NAME = "floors";
    private static final int CAMPUSES_JSON_REQ_CODE = 1;
    private static final int FACILITIES_JSON_REQ_CODE = 2;
    private static final int POI_LIST_FILE_REQ_CODE = 4;
    //private static final int GEOFENCE_FILE_REQ_CODE = 5;
    private static final int SWITCHFLOOR_FILE_REQ_CODE = 6;
    private static final int GIS_FILE_REQ_CODE = 7;
    private static final int FACILITY_PARAMS_FILE_REQ_CODE = 10;
    private static final int BLE_FlOOR_GROUPS_FILE_REQ_CODE = 11;
    private static final int FACILITY_GALLERY_JSON_FILE_REQ_CODE = 12;
    private static final int FACILITY_GALLERY_RES_IMG_REQ_CODE = 13;
    private static final int GIS_JSON_FILE_REQ_CODE = 14;
    private static final int GEOFENCE_JSON_REQ_CODE = 17;
    private static final int MAP_OVERLAY_RES_IMG_REQ_CODE = 18;
    private static final int BEACONS_PLACEMENT_JSON_REQ_CODE = 23;
    private static final int LABELS_JSON_REQ_CODE = 24;
    private static final int BLE_FlOOR_GROUPS_BY_TYPE_FILE_REQ_CODE = 25;
    private static final int CAMPUS_NAV_GEOFENCE_SETTING_FILE_REQ_CODE = 26;
    private static final int CAMPAIGN_RES_IMG_REQ_CODE = 27;
    private static final int CAMPAIGNS_JSON_REQ_CODE = 28;
    private static final int PROJECT_ICON_RES_IMG_REQ_CODE = 29;
    private static final int PROJECT_ICONS_JSON_REQ_CODE = 30;
    private static final int POIS_JSON_REQ_CODE = 31;
    private static final int MATRIX_BIN_REQ_CODE = 33;
    private static final int FLOOR_SELECTION_BIN_REQ_CODE = 34;
    private static final int BSSIDS_TXT_REQ_CODE = 35;
    private static final int CAMPUS_KML_REQ_CODE = 36;
    private static final int CAMPUS_RES_LIST_REQ_CODE = 37;
    private static final int FACILITY_RES_LIST_REQ_CODE = 38;
    private static final int SPREO_POI_ICON_REQ_CODE = 39;
    private static final int FLOOR_POLYGONS_FILE_REQ_CODE = 40;
    private static final int NAV_RULES_FILE_REQ_CODE = 41;

    public ResPathConverter() {
    }


    public static boolean isOverrideFloorOverlay(String campus, String facility, int floor) {
        File root = PropertyHolder.getInstance().getProjectDir();
        File floorFolder = new File(root, campus + "/" + FACILITES_LEVEL_DIR_NAME
                + "/" + facility + "/" + FLOORS_LEVEL_DIR_NAME + "/" + floor + "/map");
        File overrideFile = new File(floorFolder, "override");
        if (overrideFile.exists()) {
            return true;
        }
        return false;
    }

    public static void deleteOverrideFloorOverlay(String campus, String facility, int floor) {
        File root = PropertyHolder.getInstance().getProjectDir();
        File floorFolder = new File(root, campus + "/" + FACILITES_LEVEL_DIR_NAME
                + "/" + facility + "/" + FLOORS_LEVEL_DIR_NAME + "/" + floor + "/map");
        File overrideFile = new File(floorFolder, "override");
        if (overrideFile.exists()) {
            overrideFile.delete();
        }

    }

    public static boolean isOverrideCampusOverlay(String campus) {
        File root = PropertyHolder.getInstance().getProjectDir();
        File floorFolder = new File(root, campus + "/" + "campus_overlay_tiles");
        File overrideFile = new File(floorFolder, "override");
        if (overrideFile.exists()) {
            return true;
        }
        return false;
    }

    public static void deleteOverrideCampusOverlay(String campus) {
        File root = PropertyHolder.getInstance().getProjectDir();
        File floorFolder = new File(root, campus + "/" + "campus_overlay_tiles");
        File overrideFile = new File(floorFolder, "override");
        if (overrideFile.exists()) {
            overrideFile.delete();
        }

    }

    private int getResourceReqCode(String uri) {
        if (uri == null) {
            return 0;
        }

        int index = uri.lastIndexOf("/");
        String temp = uri;
        if (index != -1) {
            temp = uri.substring(index, uri.length() - 1);
        }

        if (uri.endsWith("campuses.json")) {

            return CAMPUSES_JSON_REQ_CODE;// 1;

        } else if (uri.endsWith("facilities.json")) {

            return FACILITIES_JSON_REQ_CODE; // 2

        } else if (temp.contains("poi_list")) {

            return POI_LIST_FILE_REQ_CODE; // 4

//		} else if (uri.endsWith("geofence.txt")) {
//
//			return GEOFENCE_FILE_REQ_CODE; // 5

        } else if (uri.endsWith("switchfloor.txt")) {

            return SWITCHFLOOR_FILE_REQ_CODE; // 6

        } else if (uri.endsWith(".line")) {

            return GIS_FILE_REQ_CODE; // 7

        } else if (uri.contains("plans/")) {

            if (uri.contains("/" + THUMB_FLOOR_MAP_AUTO_PREFIX)) {

                return THUMB_MAP_FILE_REQ_CODE; // 9

            } else {

                return MAP_FILE_REQ_CODE; // 8

            }

        } else if (uri.endsWith("facility.params")) {

            return FACILITY_PARAMS_FILE_REQ_CODE; // 10

        } else if (uri.endsWith("floor_groups.json")) { // floor_groups.json

            return BLE_FlOOR_GROUPS_FILE_REQ_CODE; // 11

        } else if (uri.endsWith("gallery.json")) { // gallery.json

            return FACILITY_GALLERY_JSON_FILE_REQ_CODE; // 12

        } else if (uri.contains("/" + "spreo_gallery" + "/")) {

            return FACILITY_GALLERY_RES_IMG_REQ_CODE; // 13
        } else if (uri.endsWith("gis.json") && uri.contains("/gis/")) {

            return GIS_JSON_FILE_REQ_CODE; // 14

        } else if (uri.endsWith("geofence.json")) { // geofence.json

            return GEOFENCE_JSON_REQ_CODE; // 17

        } else if (uri.contains("/" + "spreo_map_overlay" + "/")) {

            return MAP_OVERLAY_RES_IMG_REQ_CODE; // 18

        } else if (uri.endsWith("half_nav_settings.json")) { // "half_nav_settings.json"

            return BEACONS_PLACEMENT_JSON_REQ_CODE; // 23

        } else if (uri.endsWith("spreo_labels.json")) { // "spreo_labels.json"

            return LABELS_JSON_REQ_CODE; // 24

        } else if (uri.endsWith("location_groups.json")) { // "location_groups.json"

            return BLE_FlOOR_GROUPS_BY_TYPE_FILE_REQ_CODE; // 25

        } else if (uri.endsWith("spreo_navgeofences.json")) { // "spreo_navgeofences.json"

            return CAMPUS_NAV_GEOFENCE_SETTING_FILE_REQ_CODE; // 26

        } else if (uri.contains("/" + "spreo_campaign_img" + "/")) {

            return CAMPAIGN_RES_IMG_REQ_CODE; // 27

        } else if (uri.endsWith("spreo_campaigns.json")) { // spreo_campaigns.json

            return CAMPAIGNS_JSON_REQ_CODE; // 28

        } else if (uri.contains("/icons/")) {

            return PROJECT_ICON_RES_IMG_REQ_CODE; // 29

        } else if (uri.endsWith("spreo_project_icons.json")) { // spreo_project_icons.json

            return PROJECT_ICONS_JSON_REQ_CODE; // 30

        } else if (uri.contains("spreo_pois.")) { // "spreo_pois.<lang_code>"

            return POIS_JSON_REQ_CODE; // 31

        } else if (uri.endsWith("matrix.bin")) {  //33
            return MATRIX_BIN_REQ_CODE;
        } else if (uri.endsWith("floorselection.bin")) { // 34
            return FLOOR_SELECTION_BIN_REQ_CODE;
        } else if (uri.endsWith("bssids.txt")) { // 35
            return BSSIDS_TXT_REQ_CODE;
        } else if (uri.endsWith("campus.kml")) { // 36
            return CAMPUS_KML_REQ_CODE;
        } else if (uri.endsWith("campus_res_list.json")) {

            return CAMPUS_RES_LIST_REQ_CODE;// 37;

        } else if (uri.endsWith("fac_res_list.json")) {
            return FACILITY_RES_LIST_REQ_CODE; // 38

        } else if (uri.contains("spreo_poi_icons")) {
            return SPREO_POI_ICON_REQ_CODE; // 39

        } else if (uri.endsWith("spreo_polygons.json")) {
            return FLOOR_POLYGONS_FILE_REQ_CODE; // 40

        } else if (uri.endsWith("spreo_nav_rules.json")) {
            return NAV_RULES_FILE_REQ_CODE; // 41
        }

        return 0;
    }

    public byte[] getData(String url) {

        byte data[] = new byte[0];

        if (url == null || !PropertyHolder.useZip) {
            return data;
        }


        String path = getRelativePath(url);

        int reqCode = getResourceReqCode(path);
        android.util.Log.d(TAG, "getLocalBitmap:4 "+reqCode);
        android.util.Log.d(TAG, "getLocalBitmap:5 "+path);
        //return getResourceCopy(path, reqCode);
        return getResourceCopy(path, 8);
    }

    private byte[] getResourceCopy(String path, int reqCode) {

        byte[] res = new byte[0];
        String projectId = null;
        String campusId = null;
        String facilityId;
        String floorIndex = null;
        String uri = null;
        String values[] = null;
        String fileName = null;
        File file = null;
        File root = PropertyHolder.getInstance().getZipAppdir();
        android.util.Log.d(TAG, "getResourceCopy: "+root);
        String relativeFilePath = null;

//		if(reqCode!=0){
//			Log.e("res","path ==> " + path +" code==> " + reqCode);
//		}

        try {
            switch (reqCode) {
                case MAP_FILE_REQ_CODE: // 8
                {
                    uri = path;
                    values = uri.split("/");
                    relativeFilePath=uri;
                    android.util.Log.d(TAG, "getResourceCopy: "+relativeFilePath);

//                    if (values.length >= 7) {
//
//                        projectId = values[1];
//                        campusId = values[2];
//                        facilityId = values[3];
//                        // values[4] => plans
//                        floorIndex = values[5];
//                        fileName = values[6]; // => *.png
//
//                        relativeFilePath = projectId + "/" + campusId + "/"
//                                + FACILITES_LEVEL_DIR_NAME + "/" + facilityId + "/"
//                                + FLOORS_LEVEL_DIR_NAME + "/" + floorIndex
//                                + "/map/" + fileName;
//                    }
                    break;
                }
                case PROJECT_ICON_RES_IMG_REQ_CODE: {

                    uri = path;
                    values = uri.split("/");

                    if (values.length >= 4) {

                        projectId = values[1];

                        //String folder = value[2]; // spreo_icon;
                        fileName = values[3];

                        relativeFilePath = projectId + "/icons/" + fileName;

                    }

                    break;
                }

                case POI_LIST_FILE_REQ_CODE: // 4
                {

                    uri = path;
                    values = uri.split("/");
                    if (values.length >= 7) {
                        projectId = values[1];
                        campusId = values[2];
                        facilityId = values[3];
                        // values[4] => poi
                        floorIndex = values[5];
                        fileName = values[6]; //=> poi_list.txt
                        relativeFilePath = projectId + "/" + campusId + "/"
                                + FACILITES_LEVEL_DIR_NAME + "/" + facilityId + "/"
                                + FLOORS_LEVEL_DIR_NAME + "/" + floorIndex
                                + "/poi/" + fileName;
                    }


                    break;
                }

                case CAMPUSES_JSON_REQ_CODE: // campuses.json
                {
                    uri = path;
                    values = uri.split("/");
                    if (values.length > 1) {
                        projectId = values[1];
                        fileName = values[2];
                        relativeFilePath = projectId + "/" + fileName;
                    }

                    break;
                }


                case CAMPUS_RES_LIST_REQ_CODE: // campus_res_list.json
                {
                    uri = path;
                    values = uri.split("/");
                    if (values.length > 1) {
                        projectId = values[1];
                        campusId = values[2];
                        fileName = values[3];
                        relativeFilePath = projectId + "/" + campusId + "/" + fileName;
                    }

                    break;
                }

                case FACILITIES_JSON_REQ_CODE: // facilities.json
                {
                    uri = path;
                    values = uri.split("/");
                    projectId = values[1];
                    campusId = values[2];

                    fileName = values[3];
                    relativeFilePath = projectId + "/" + campusId + "/" + fileName;

                    break;
                }

                case CAMPUS_NAV_GEOFENCE_SETTING_FILE_REQ_CODE: // spreo_nav_geofences.json
                {
                    uri = path;
                    values = uri.split("/");
                    projectId = values[1];
                    campusId = values[2];
                    fileName = values[3];
                    relativeFilePath = projectId + "/" + campusId + "/" + fileName;

                    break;
                }

//				case GEOFENCE_FILE_REQ_CODE:
//				{	
//					uri = path;
//					values = uri.split("/");
//					projectId = values[1];
//					campusId = values[2];
//					facilityId = values[3];
//
//					break;
//				}

                case SWITCHFLOOR_FILE_REQ_CODE: {
                    uri = path;
                    values = uri.split("/");
                    projectId = values[1];
                    campusId = values[2];
                    facilityId = values[3];
                    fileName = values[4];
                    relativeFilePath = projectId + "/" + campusId + "/"
                            + FACILITES_LEVEL_DIR_NAME + "/" + facilityId + "/" + fileName;

                    break;
                }

                case GIS_FILE_REQ_CODE: // gis.line
                {
                    uri = path;

                    values = uri.split("/");
                    if (values.length >= 7) {
                        projectId = values[1];
                        campusId = values[2];
                        facilityId = values[3];
                        // values[4] => gis
                        floorIndex = values[5];
                        // values[6] => *.line
                        relativeFilePath = projectId + "/" + campusId + "/"
                                + FACILITES_LEVEL_DIR_NAME + "/" + facilityId + "/"
                                + FLOORS_LEVEL_DIR_NAME + "/" + floorIndex
                                + "/gis/" + "gis" + floorIndex + ".line";

                    }

                    break;
                }

                case GIS_JSON_FILE_REQ_CODE:  // gis.json
                {
                    uri = path;

                    values = uri.split("/");
                    if (values.length >= 7) {
                        projectId = values[1];
                        campusId = values[2];
                        facilityId = values[3];
                        // values[4] => gis
                        floorIndex = values[5];
                        // values[6] => *.line

                    }
                    break;
                }

                case THUMB_MAP_FILE_REQ_CODE: // 9
                {
                    uri = path;

                    values = uri.split("/");

                    if (values.length >= 7) {

                        projectId = values[1];
                        campusId = values[2];
                        facilityId = values[3];
                        // values[4] => plans
                        floorIndex = values[5];
                        // values[6] => *.png

                    }

                    break;
                }

                case FACILITY_PARAMS_FILE_REQ_CODE: // facility.params
                {

                    uri = path;
                    values = uri.split("/");
                    projectId = values[1];
                    campusId = values[2];
                    facilityId = values[3];


                    fileName = values[4];
                    relativeFilePath = projectId + "/" + campusId + "/"
                            + FACILITES_LEVEL_DIR_NAME + "/" + facilityId + "/" + fileName;

                    break;
                }


                case FACILITY_RES_LIST_REQ_CODE: // fac_res_list.json
                {

                    uri = path;
                    values = uri.split("/");
                    projectId = values[1];
                    campusId = values[2];
                    facilityId = values[3];
                    fileName = values[4];
                    relativeFilePath = projectId + "/" + campusId + "/"
                            + FACILITES_LEVEL_DIR_NAME + "/" + facilityId + "/" + fileName;

                    break;
                }

                case BLE_FlOOR_GROUPS_FILE_REQ_CODE: {
                    uri = path;
                    values = uri.split("/");

                    projectId = values[1];
                    campusId = values[2];
                    facilityId = values[3];
                    fileName = values[4];
                    relativeFilePath = projectId + "/" + campusId + "/"
                            + FACILITES_LEVEL_DIR_NAME + "/" + facilityId
                            + "/" + FLOORS_LEVEL_DIR_NAME + "/" + fileName;

                    break;
                }

                case FACILITY_GALLERY_JSON_FILE_REQ_CODE: {
                    uri = path;

                    values = uri.split("/");
                    projectId = values[1];
                    campusId = values[2];
                    facilityId = values[3];
                    fileName = values[4];
                    relativeFilePath = projectId + "/" + campusId + "/"
                            + FACILITES_LEVEL_DIR_NAME + "/" + facilityId + "/" + fileName;

                    break;
                }
                case FACILITY_GALLERY_RES_IMG_REQ_CODE: {

                    uri = path;
                    values = uri.split("/");

                    if (values.length >= 8) {

                        //office_haifa_project/office/office/spreo_gallery/2/head/example1.jpg
                        projectId = values[1];
                        campusId = values[2];
                        facilityId = values[3];
                        //String folder = value[4]; // spreo_gallery;
                        String poid = values[5];
                        String type = values[6];
                        String imgName = values[7];

                    }

                    break;

                }

                case GEOFENCE_JSON_REQ_CODE: {
                    uri = path;
                    values = uri.split("/");
                    projectId = values[1];
                    campusId = values[2];
                    facilityId = values[3];
                    fileName = values[4];
                    relativeFilePath = projectId + "/" + campusId + "/"
                            + FACILITES_LEVEL_DIR_NAME + "/" + facilityId + "/" + fileName;

                    break;
                }

                case MAP_OVERLAY_RES_IMG_REQ_CODE: {
                    uri = path;

                    values = uri.split("/");

                    if (values.length >= 4) {

                        //  office_haifa_project/office/spreo_map_overlay/3
                        projectId = values[1];
                        campusId = values[2];

                        //String folder = value[3]; // spreo_map_overlay;
                        String overlayId = values[4];
                        fileName = "overlay_image.png";

                        relativeFilePath = projectId + "/" + campusId + "/"
                                + "campus_overlay_tiles" + "/" + fileName;

                    }

                    break;
                }

                case CAMPAIGN_RES_IMG_REQ_CODE: {

                    uri = path;

                    values = uri.split("/");

                    if (values.length >= 5) {

                        //  office_haifa_project/office/office/spreo_campaign_img/3
                        projectId = values[1];
                        campusId = values[2];
                        facilityId = values[3];
                        String folder = values[4]; // spreo_campaign_img;
                        fileName = values[5];

                        relativeFilePath = projectId + "/" + campusId + "/" + FACILITES_LEVEL_DIR_NAME + "/" + facilityId + "/" + folder + "/" + fileName;
                    }
                    break;

                }

                case BEACONS_PLACEMENT_JSON_REQ_CODE: // half_nav_settings.json
                {
                    uri = path;
                    values = uri.split("/");

                    projectId = values[1];
                    campusId = values[2];
                    facilityId = values[3];
                    fileName = values[4];
                    relativeFilePath = projectId + "/" + campusId + "/"
                            + FACILITES_LEVEL_DIR_NAME + "/" + facilityId
                            + "/" + FLOORS_LEVEL_DIR_NAME + "/" + fileName;
                    break;
                }

                case CAMPAIGNS_JSON_REQ_CODE:    // spreo_campaigns.json
                {
                    uri = path;
                    values = uri.split("/");

                    projectId = values[1];
                    campusId = values[2];
                    facilityId = values[3];
                    fileName = values[4];
                    relativeFilePath = projectId + "/" + campusId + "/"
                            + FACILITES_LEVEL_DIR_NAME + "/" + facilityId
                            + "/" + fileName;

                    break;
                }

                case LABELS_JSON_REQ_CODE: //spreo_labels.json
                {

                    uri = path;

                    values = uri.split("/");

                    projectId = values[1];
                    campusId = values[2];
                    facilityId = values[3];
                    fileName = values[4];
                    relativeFilePath = projectId + "/" + campusId + "/"
                            + FACILITES_LEVEL_DIR_NAME + "/" + facilityId
                            + "/" + fileName;

                    break;
                }

                case BLE_FlOOR_GROUPS_BY_TYPE_FILE_REQ_CODE: {

                    uri = path;

                    values = uri.split("/");

                    projectId = values[1];
                    campusId = values[2];
                    facilityId = values[3];

                    break;
                }

                case PROJECT_ICONS_JSON_REQ_CODE: {

                    uri = path;
                    values = uri.split("/");

                    projectId = values[1];
                    fileName = values[2];
                    relativeFilePath = projectId + "/icons/" + fileName;


                    break;
                }


                case POIS_JSON_REQ_CODE: {
                    uri = path;
                    values = uri.split("/");

                    projectId = values[1];
                    campusId = values[2];
                    facilityId = values[3];
                    String poiLangFile = values[4];
                    String langFields[] = poiLangFile.split("\\.");
                    String lang = langFields[1];

                    break;
                }


                case MATRIX_BIN_REQ_CODE: // matrix.bin
                {
                    uri = path;
                    values = uri.split("/");

                    projectId = values[1];
                    campusId = values[2];
                    facilityId = values[3];
                    floorIndex = values[4];
                    fileName = values[5];
                    relativeFilePath = projectId + "/" + campusId + "/"
                            + FACILITES_LEVEL_DIR_NAME + "/" + facilityId
                            + "/" + FLOORS_LEVEL_DIR_NAME + "/" + floorIndex + "/scan results/" + fileName;
                    break;
                }

                case FLOOR_SELECTION_BIN_REQ_CODE: // floorselection.bin
                {
                    uri = path;
                    values = uri.split("/");

                    projectId = values[1];
                    campusId = values[2];
                    facilityId = values[3];
                    fileName = values[4];
                    relativeFilePath = projectId + "/" + campusId + "/"
                            + FACILITES_LEVEL_DIR_NAME + "/" + facilityId
                            + "/" + FLOORS_LEVEL_DIR_NAME + "/" + fileName;
                    break;
                }


                case BSSIDS_TXT_REQ_CODE: // bssids.txt
                {
                    uri = path;
                    values = uri.split("/");

                    projectId = values[1];
                    campusId = values[2];
                    facilityId = values[3];
                    fileName = values[4];
                    relativeFilePath = projectId + "/" + campusId + "/"
                            + FACILITES_LEVEL_DIR_NAME + "/" + facilityId
                            + "/" + FLOORS_LEVEL_DIR_NAME + "/" + fileName;
                    break;
                }


                case CAMPUS_KML_REQ_CODE: // campus.kml
                {
                    uri = path;
                    values = uri.split("/");
                    projectId = values[1];
                    campusId = values[2];

                    fileName = values[3];
                    relativeFilePath = projectId + "/" + campusId + "/" + fileName;

                    break;
                }


                case SPREO_POI_ICON_REQ_CODE: // spreo_poi_icons/...
                {
                    uri = path;
                    values = uri.split("/");
                    projectId = values[1];
                    String folder = values[2];

                    fileName = values[3];
                    relativeFilePath = projectId + "/" + folder + "/" + fileName;

                    break;
                }

                case FLOOR_POLYGONS_FILE_REQ_CODE: // spreo_polygons.json
                {
                    uri = path;
                    values = uri.split("/");
                    if (values.length > 1) {
                        projectId = values[1];
                        fileName = values[2];
                        relativeFilePath = projectId + "/" + fileName;
                    }
                    break;
                }


                case NAV_RULES_FILE_REQ_CODE: // spreo_nav_rules.json
                {
                    uri = path;
                    values = uri.split("/");
                    if (values.length > 1) {
                        projectId = values[1];
                        fileName = values[2];
                        relativeFilePath = projectId + "/" + fileName;
                    }
                    break;
                }


            }

            file = new File(root, relativeFilePath);
            android.util.Log.d(TAG, "getResourceCopy: relativeFilePath"+relativeFilePath);
            android.util.Log.d(TAG, "getResourceCopy: root"+root);
            android.util.Log.d(TAG, "getResourceCopy: file"+file);
            res = getLocalResCopy(file);
            android.util.Log.d(TAG, "getResourceCopy:length "+res.length);
        } catch (Throwable t) {

            //System.err.println("resPathConverter::file not found ==>" +file);
            //t.printStackTrace();
        }

        return res;
    }

    private String getRelativePath(String uri) {
        android.util.Log.d(TAG, "getRelativePath: "+uri);
        String path = null;
        try {
//            int index = uri.indexOf("/res/");
//            if (index != -1) {
//                path = uri.substring(index + 4);
//                //System.out.println(path);
//            }
            int index = uri.indexOf("/images/");
            if (index != -1) {
                path = uri.substring(index + 1);
                android.util.Log.d(TAG, "getRelativePath: "+path);
                //System.out.println(path);
            }
        } catch (Throwable t) {

        }
        return path;
    }


    private byte[] getLocalResCopy(File f) {

        ByteArrayOutputStream stream = new ByteArrayOutputStream(4096 * 2);

        InputStream in = null;
        try {
            in = new FileInputStream(f);
            byte[] buffer = new byte[4096 * 2];
            int n = -1;
            while ((n = in.read(buffer)) != -1) {
                if (n > 0) {
                    stream.write(buffer, 0, n);
                }
            }
        } catch (IOException e) {
            Log.e(ResPathConverter.class.getName(), "Can't getLocalResCopy", e);
            //e.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                    stream.close();
                } catch (IOException e) {
                    //e.printStackTrace();
                }
            }
        }
        return stream.toByteArray();
    }


}
