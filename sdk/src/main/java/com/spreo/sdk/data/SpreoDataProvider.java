package com.spreo.sdk.data;

import android.content.Context;
import android.graphics.Bitmap;

import com.google.android.gms.maps.model.LatLng;
import com.mlins.project.Campus;
import com.mlins.project.ProjectConf;
import com.mlins.res.setup.ProjectsDataProvider;
import com.mlins.ui.utils.ProjectIconsManager;
import com.mlins.utils.FacilityConf;
import com.mlins.utils.FacilityContainer;
import com.mlins.utils.FloorData;
import com.mlins.utils.PropertyHolder;
import com.spreo.geofence.GeoFenceHelper;
import com.spreo.interfaces.ProjectsDataListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class provides information about the current detected campus and facility.
 *
 * @author Spreo
 */
public class SpreoDataProvider {
    private static final String SDK_VERSION = "5.4";

    /**
     * SpreoDataProvider.getCampusId is used to get the campus id
     *
     * @return campus id as string
     */
    public static String getCampusId() {
        return PropertyHolder.getInstance().getCampusId();
    }

    /**
     * SpreoDataProvider.getFacilityId is used to get the facility id
     *
     * @return facility id as string
     */
    public static String getFacilityId() {
        FacilityConf fac = FacilityContainer.getInstance().getSelected();
        if (fac == null) {
            return null;
        }
        return fac.getId(); //PropertyHolder.getInstance().getFacilityID();
    }

    /**
     * SpreoDataProvider.getSdkVersion is used to get the SDK version
     *
     * @return SDK version as string
     */
    public static String getSdkVersion() {
        return SDK_VERSION;
    }

    /**
     * SpreoDataProvider.getCampusesList
     *
     * @return list of strings with available campuses
     */
    public static List<String> getCampusesList() {
        List<String> result = new ArrayList<String>();
        Map<String, Campus> campusesmap = ProjectConf.getInstance()
                .getCampusesMap();
        if (campusesmap != null) {
            for (String o : campusesmap.keySet()) {
                result.add(o);
            }
        }
        return result;
    }

    /**
     * SpreoDataProvider.getCampusInfo is used to get the campus info by campus id.
     *
     * @param campusId
     * @return a map containing the following key/value pairs:
     * key 			value / type
     * id 				id of campus (String)
     * name 			name of campus (String)
     * location 		center location in lat/lon (LatLng)
     */
    public static HashMap<String, Object> getCampusInfo(String campusId) {
        HashMap<String, Object> result = new HashMap<String, Object>();
        Campus campus = ProjectConf.getInstance().getCampus(campusId);
        if (campus != null) {
            String id = campus.getId();
            result.put("id", id);

            String name = campus.getName();
            result.put("name", name);

            double lat = campus.getCenterLatitude();
            double lon = campus.getCenterLongtitude();
            LatLng loc = new LatLng(lat, lon);
            result.put("location", loc);
        }
        return result;
    }

    /**
     * SpreoDataProvider.getCampusFacilities is used to get the list of facilities id of selected campus
     *
     * @param campusId
     * @return the list of facilities id of selected campus
     */
    public static List<String> getCampusFacilities(String campusId) {
        List<String> result = new ArrayList<String>();
        Campus campus = ProjectConf.getInstance().getCampus(campusId);
        if (campus != null) {
            Map<String, FacilityConf> facilitiesmap = campus.getFacilitiesConfMap();
            for (String o : facilitiesmap.keySet()) {
                result.add(o);
            }
        }
        return result;
    }

    /**
     * SpreoDataProvider.getFacilityInfo is used to get the facility information description (if exist if not it will return empty map)
     *
     * @param campusId
     * @param facilityId
     * @return a map containing the following key/value pairs:
     * key  			value / type
     * id    	 		id of facility (String)
     * name 			name of facility (String)
     * location 		center location center location in lat/lon (LatLng)
     * floors 			list of floors numbers (List<Integer>)
     */
    public static HashMap<String, Object> getFacilityInfo(String campusId, String facilityId) {
        HashMap<String, Object> result = new HashMap<String, Object>();
        Campus campus = ProjectConf.getInstance().getCampus(campusId);
        if (campus != null) {
            Map<String, FacilityConf> facilitiesmap = campus.getFacilitiesConfMap();
            FacilityConf facility = facilitiesmap.get(facilityId);
            if (facility != null) {
                String id = facility.getId();
                result.put("id", id);

                String name = facility.getName();
                result.put("name", name);

                double lat = facility.getCenterLatitude();
                double lon = facility.getCenterLongtitude();
                LatLng loc = new LatLng(lat, lon);
                result.put("location", loc);

                List<FloorData> floorDataList = facility.getFloorDataList();

                List<Integer> floors = new ArrayList<Integer>();
                for (FloorData o : floorDataList) {
                    int index = floorDataList.indexOf(o);
                    floors.add(index);
                }
                result.put("floors", floors);

                if(facility.getEntranceFloor() < floorDataList.size()) { // avoiding IndexOutOfBoundsException in case if we don't have floor bounds in project configuration
                    FloorData floorData = floorDataList.get(facility.getEntranceFloor());
                    if(floorData != null) {
                        List<LatLng> bounds = floorData.getPolygon();
                        result.put("bounds", bounds);
                    }
                }
            }
        }
        return result;
    }

    /**
     * SpreoDataProvider.getFloorInfo is being used to get the floor information string in order to get the title the following params should be supplied
     *
     * @param campusId   as string
     * @param facilityId as string
     * @param floorIndex as integer
     * @return a map containing the following key/value pairs:
     * key			value / type
     * title 		title of floor (String)
     */
    public static HashMap<String, Object> getFloorInfo(String campusId, String facilityId, int floorIndex) {
        HashMap<String, Object> result = new HashMap<String, Object>();
        Campus campus = ProjectConf.getInstance().getCampus(campusId);
        if (campus != null) {
            Map<String, FacilityConf> facilitiesmap = campus.getFacilitiesConfMap();
            FacilityConf facility = facilitiesmap.get(facilityId);
            if (facility != null) {
                FloorData floor = facility.getFloor(floorIndex);
                if (floor != null) {
                    String title = floor.getTitle();
                    result.put("title", title);
                }
            }
        }
        return result;

    }

    /**
     * gets the floor title
     *
     * @param campusId
     * @param facilityId
     * @param floorIndex
     * @return string with floor title
     */
    public static String getFloorTitle(String campusId, String facilityId, int floorIndex) {
        String result = null;
        Campus campus = ProjectConf.getInstance().getCampus(campusId);
        if (campus != null) {
            Map<String, FacilityConf> facilitiesmap = campus.getFacilitiesConfMap();
            FacilityConf facility = facilitiesmap.get(facilityId);
            if (facility != null) {
                FloorData floor = facility.getFloor(floorIndex);
                if (floor != null) {
                    result = floor.getTitle();
                }
            }
        }
        return result;

    }


    /**
     * Gets bitmap by name
     *
     * @param imageName
     * @return
     */
    public static Bitmap getImageByName(String imageName) {
        return ProjectIconsManager.getInstance().getLocalBitmapCopy(imageName);
    }

    public static List<String> getGeofencesTypes() {
        List<String> result = new ArrayList<String>();
        result = GeoFenceHelper.getInstance().getAlltypes();
        return result;
    }

    /**
     * gets the floor picker facility
     *
     * @return string with facility id
     */
    public static String getFloorPickerFacilityId() {
        return ProjectConf.getInstance().getFloorPickerFacilityId();
    }

    /**
     * get projects list sorted by location or alphabetically
     *
     * @param ctx      - Context
     * @param key      - customer validation key
     * @param latlng   - (optional)  sort by LatLng
     * @param radius   in meters - (optional) get only projects within radius
     * @param listener - listener for getting results from server
     */
    public static void getProjectsList(Context ctx, String key, LatLng latlng, Double radius, ProjectsDataListener listener) {
        ProjectsDataProvider.getInstance().getProjectsList(ctx, key, latlng, radius, listener);
    }


    public static List<Integer> getFacilityFloorIDs(String campusID, String facilityID){
        HashMap<String, Object> facilityInfo = SpreoDataProvider.getFacilityInfo(campusID, facilityID);
        return (List<Integer>) facilityInfo.get("floors");
    }



}
