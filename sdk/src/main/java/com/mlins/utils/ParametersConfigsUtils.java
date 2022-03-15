package com.mlins.utils;

import android.annotation.SuppressLint;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class ParametersConfigsUtils {
    private final static String paramsFileName = "facility.params";

    @SuppressLint("UseSparseArrays")
    public static void load() {

        try {

            byte[] data = readLocalCopy();

            // parse file
            if (data != null && data.length > 0) {

                String resjson = new String(data);

                JSONTokener tokener = new JSONTokener(resjson);
                JSONObject jsonObj = (JSONObject) tokener.nextValue();

                FacilityConf fac = FacilityContainer.getInstance().getSelected();

                if (fac == null) {
                    return;
                }

                try {
                    int floorSwitchTopLevelThr = jsonObj.getInt("floor_switch_top_level_th");
                    fac.setFloorsTopKlevelsThr(floorSwitchTopLevelThr);

                } catch (Exception e) {

                }
                try {
                    int floorSwitchLevelLowerBound = jsonObj.getInt("floor_switch_level_lower_bound");
                    fac.setFloorselectionLevelLowerBound(floorSwitchLevelLowerBound);
                } catch (Exception e) {

                }
                try {
                    int minBleDetectionDevices = jsonObj.getInt("min_ble_detection_devices");
                    fac.setMinimumDevicesForEntrance(minBleDetectionDevices);
                } catch (Exception e) {

                }

                try {
                    int minBleDetectionLevel = jsonObj.getInt("min_ble_detection_level");
                    fac.setBlipLevelForEntrance(minBleDetectionLevel);
                } catch (Exception e) {

                }
                try {
                    int loggerMode = jsonObj.getInt("logger_mode");
                    fac.setLoggerMode(loggerMode);
                } catch (Exception e) {

                }
                try {
                    int floorMinDetectionCountForSwitch = jsonObj.getInt("floor_min_detection_count_for_switch");
                    fac.setFloorSelectionBlips(floorMinDetectionCountForSwitch);
                } catch (Exception e) {

                }
                try {
                    float stickyRadiusDr = (float) jsonObj.getDouble("sticky_radius_dr");
                    fac.setLocatorRadius(stickyRadiusDr);
                } catch (Exception e) {

                }

                try {
                    int exitNoDetectionCount = (int) jsonObj.getInt("exit_no_detection_count");
                    fac.setExitNoDetectionCount(exitNoDetectionCount);
                } catch (Exception e) {

                }

                try {
                    int exitMinBleDetectionDevices = (int) jsonObj.getInt("exit_min_ble_detection_devices");
                    fac.setExitMinBleDetectionDevices(exitMinBleDetectionDevices);
                } catch (Exception e) {

                }

                try {
                    int exitMinBleDetectionLevel = (int) jsonObj.getInt("exit_min_ble_detection_level");
                    fac.setExitMinBleDetectionLevel(exitMinBleDetectionLevel);
                } catch (Exception e) {

                }

                try {
                    int locationtopk = (int) jsonObj.getInt("location_top_k");
                    fac.setTopKlevelsThr(locationtopk);
                } catch (Exception e) {

                }

                try {
                    int distancefromnavpath = (int) jsonObj.getDouble("reroute_min_distance");
                    fac.setDistanceFromNavPath(distancefromnavpath);
                } catch (Exception e) {

                }

                try {
                    float instructiondistance = (float) jsonObj.getDouble("play_instruction_distance");
                    fac.setPlayInstructionDistance(instructiondistance);
//					PropertyHolder.getInstance().setInstructionsDistance(instructiondistance);
                } catch (Exception e) {

                }

                try {
                    float destinationdistance = (float) jsonObj.getDouble("end_of_route_radius");
                    fac.setEndOfRouteRadius(destinationdistance);
//					PropertyHolder.getInstance().setChkDestReachRectRangeWidthMeters(destinationdistance);
                } catch (Exception e) {

                }

                try {
                    Map<Integer, String> soundToFloorMap = new HashMap<Integer, String>();
                    JSONArray floorsSoundsArray = jsonObj.getJSONArray("floors_sounds");
                    for (int i = 0; i < floorsSoundsArray.length(); i++) {
                        JSONObject jObj = (JSONObject) floorsSoundsArray.get(i);
                        int floor = jObj.getInt("floor");
                        String sound = jObj.getString("sound");
                        soundToFloorMap.put(floor, sound);
                    }

                    fac.setSoundToFloorMapping(soundToFloorMap);

                } catch (Exception e) {

                }

                try {
                    float poiforinstructionradius = (float) jsonObj.getDouble("poi_for_instruction_radius");
                    fac.setPoiForInstructionRadius(poiforinstructionradius);
                } catch (Exception e) {

                }

                try {
                    int ndkcoloserange = jsonObj.getInt("ndk_close_range");
                    fac.setNdkCloseRange(ndkcoloserange);
                } catch (Exception e) {

                }

                try {
                    boolean projectlocation = jsonObj.getBoolean("use_projected_location");
                    fac.setProjectLocation(projectlocation);
                } catch (Exception e) {

                }

                try {
                    float placementthresh = (float) jsonObj.getDouble("placement_threshold");
                    fac.setPlacementThresh(placementthresh);
                } catch (Exception e) {

                }


                try {
                    int locationLevelThreshold = (int) jsonObj.getInt("location_level_threshold");
                    fac.setLocationLevelThreshold(locationLevelThreshold);
                } catch (Exception e) {

                }

                try {
                    int floorFilterThreshold = (int) jsonObj.getInt("floor_filter_threshold");
                    fac.setFloorFilterThreshold(floorFilterThreshold);
                } catch (Exception e) {

                }


                try {
                    int bridgeDevicesForEntrance = (int) jsonObj.getInt("bridge_entrance_devices");
                    fac.setBridgeDevicesForEntrance(bridgeDevicesForEntrance);
                } catch (Exception e) {

                }

                try {
                    int bridgeLevelForEntrance = (int) jsonObj.getInt("bridge_entrance_level");
                    fac.setBridgeLevelForEntrance(bridgeLevelForEntrance);
                } catch (Exception e) {

                }

            }

        } catch (Throwable e) {
            e.printStackTrace();
        }

    }

    public static boolean save() {
        boolean res = false;
        try {
            JSONObject jsonObj = new JSONObject();

            FacilityConf fac = FacilityContainer.getInstance().getSelected();

            if (fac == null) {
                return false;
            }

            jsonObj.put("project_id", PropertyHolder.getInstance().getProjectId());
            jsonObj.put("campus_id", PropertyHolder.getInstance().getCampusId());
            jsonObj.put("facility_id", fac.getId());
            jsonObj.put("floor_switch_top_level_th", fac.getFloorsTopKlevelsThr());
            jsonObj.put("floor_switch_level_lower_bound", fac.getFloorselectionLevelLowerBound());
            jsonObj.put("min_ble_detection_devices", fac.getMinimumDevicesForEntrance());
            jsonObj.put("min_ble_detection_level", fac.getBlipLevelForEntrance());
            jsonObj.put("logger_mode", fac.getLoggerMode());
            jsonObj.put("floor_min_detection_count_for_switch", fac.getFloorSelectionBlips());
            jsonObj.put("sticky_radius_dr", fac.getLocatorRadius());
            jsonObj.put("exit_no_detection_count", fac.getExitNoDetectionCount());
            jsonObj.put("exit_min_ble_detection_devices", fac.getExitMinBleDetectionDevices());
            jsonObj.put("exit_min_ble_detection_level", fac.getExitMinBleDetectionLevel());
            jsonObj.put("location_top_k", fac.getTopKlevelsThr());

            String date = jsonObj.toString();

            String file = PropertyHolder.getInstance().getFacilityDir().getAbsolutePath() + File.separator + paramsFileName;

            File resListFile = new File(file);
            BufferedWriter bw = null;

            try {
                bw = new BufferedWriter(new FileWriter(resListFile, false));
                bw.write(date);

                bw.flush();

                res = true;
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (bw != null) {
                    try {
                        bw.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

        } catch (Throwable e) {
            e.printStackTrace();
            res = false;
        }
        return res;
    }

    public static void upload() {

        byte[] data = readLocalCopy();

        // parse file
        if (data != null && data.length > 0) {

            String resjson = new String(data);
            ResourceUploader uploader = new ResourceUploader();
            uploader.execute("projres", "20", resjson);
        }
    }


    private static byte[] readLocalCopy() {
        byte[] data = null;

        if (PropertyHolder.useZip) {
            String fac = PropertyHolder.getInstance().getFacilityID();
            String url = ServerConnection.getResourcesUrl() + fac + "/" + paramsFileName;
            data = ResourceDownloader.getInstance().getUrl(url);
            return data;
        } else {
            // read file
            String file = PropertyHolder.getInstance().getFacilityDir().getAbsolutePath() + File.separator + paramsFileName;
            File f = new File(file);
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
                data = stream.toByteArray();

            } catch (Throwable e) {
                e.printStackTrace();
            } finally {
                if (in != null) {
                    try {
                        in.close();
                        stream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return data;
    }


}
