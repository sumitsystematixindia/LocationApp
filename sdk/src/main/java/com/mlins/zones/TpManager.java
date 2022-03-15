package com.mlins.zones;

import android.bluetooth.BluetoothAdapter;

import com.mlins.utils.FacilityConf;
import com.mlins.utils.FacilityContainer;
import com.mlins.utils.Lookup;
import com.mlins.utils.PropertyHolder;
import com.mlins.utils.ResourceDownloader;
import com.mlins.utils.ServerConnection;
import com.mlins.wireless.WlBlip;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class TpManager {

    private List<String> bssids = new ArrayList<String>();
    private int threshold = 0;
    private List<List<String>> accumulateScans = new ArrayList<List<String>>();
    private int accumulateCount = 3;

    private TpManager() {
        load();
    }

    public static TpManager getInstance() {
        return Lookup.getInstance().get(TpManager.class);
    }

    public static void releaseInstance() {
        Lookup.getInstance().remove(TpManager.class);
    }

    private void load() {
        FacilityConf fac = FacilityContainer.getInstance().getSelected();
        if (fac != null) {
            threshold = fac.getExitNoDetectionCount();
        }
        loadBssidFile();
        loadGroups();
    }


    private void loadZipFacilityBssids() {

        FacilityConf fac = FacilityContainer.getInstance().getSelected();
        if (fac != null) {
            String facid = fac.getId();
            String url = ServerConnection.getResourcesUrl() + facid + "/" + PropertyHolder.getInstance().getMatrixFilePrefix() + "bssids.txt";
            byte[] bytes = ResourceDownloader.getInstance().getLocalCopy(url);
            if (bytes == null || bytes.length == 0) {
                return;
            }

            String cont = new String(bytes);

            String bssidsArr[] = cont.split("\\n");
            for (String line : bssidsArr) {
                if (!bssids.contains(line)) {
                    bssids.add(line);
                }
            }
        }


    }

    private void loadBssidFile() {

        if (PropertyHolder.useZip) {
            loadZipFacilityBssids();
        } else {
            FacilityConf fac = FacilityContainer.getInstance().getSelected();
            if (fac != null) {
                String facid = fac.getId();
                if (facid != null) {
                    String campusdir = PropertyHolder.getInstance().getCampusDir()
                            .toString();
                    String facilirdir = campusdir + "/" + facid;
                    File dir = new File(facilirdir);
                    String filename = PropertyHolder.getInstance()
                            .getMatrixFilePrefix() + "BSSIDS.txt";
                    File bssidsfile = new File(dir, filename);
                    if (!bssidsfile.exists()) {
                        return;
                    }

                    BufferedReader in = null;
                    try {
                        in = new BufferedReader(new FileReader(bssidsfile));
                        String line = null;
                        while ((line = in.readLine()) != null) {
                            if (!bssids.contains(line)) {
                                bssids.add(line);
                            }
                        }
                    } catch (IOException e) {
                        e.toString();
                    } finally {
                        if (in != null)
                            try {
                                in.close();
                            } catch (Exception e2) {
                                e2.printStackTrace();
                            }
                    }
                }
            }
        }

    }

    private void loadGroups() {
        JSONObject json = getGroupsJson();
        if (json != null) {
            try {
                JSONArray groups = json.getJSONArray("groups");
                for (int i = 0; i < groups.length(); i++) {
                    JSONObject group = groups.getJSONObject(i);
                    JSONArray beacons = group.getJSONArray("beacons");
                    for (int j = 0; j < beacons.length(); j++) {
                        String id = beacons.getString(j);
                        if (id != null && !bssids.contains(id)) {
                            bssids.add(id);
                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private JSONObject getGroupsJson() {
        JSONObject json = null;

        if (PropertyHolder.useZip) {
            try {
                FacilityConf fac = FacilityContainer.getInstance().getSelected();
                if (fac != null) {
                    String facid = fac.getId();
                    String url = ServerConnection.getResourcesUrl() + facid + "/floor_groups.json";
                    byte[] bytes = ResourceDownloader.getInstance().getLocalCopy(url);
                    if (bytes != null && bytes.length > 0) {
                        String cont = new String(bytes);

                        json = new JSONObject(cont);

                    }
                }
            } catch (Throwable e) {
                e.printStackTrace();
            }


        } else {

            FacilityConf fac = FacilityContainer.getInstance().getSelected();
            if (fac != null) {
                String facid = fac.getId();
                if (facid != null) {
                    String campusdir = PropertyHolder.getInstance().getCampusDir()
                            .toString();
                    String facilirdir = campusdir + "/" + facid;
                    File dir = new File(facilirdir);
                    String filename = "floor_groups.json";
                    File groupsfile = new File(dir, filename);
                    if (groupsfile.exists()) {
                        json = new JSONObject();

                        Scanner scanner = null;
                        StringBuffer content = new StringBuffer();

                        try {
                            scanner = new Scanner(groupsfile, "UTF-8");
                            while (scanner.hasNext()) {
                                content.append(scanner.nextLine());
                            }
                            json = new JSONObject(content.toString());
                        } catch (Throwable e) {
                            e.printStackTrace();
                        } finally {
                            if (scanner != null)
                                scanner.close();
                        }

                    }
                }
            }
        }

        return json;
    }

    public boolean isTp(List<WlBlip> results) {
        if (threshold == 0) {
            return true;
        }

        if (!isBlueToothOn()) {
            return false;
        }

        boolean result = false;

        if (!accumulateScans.isEmpty() && accumulateScans.size() >= accumulateCount) {
            accumulateScans.remove(0);
        }

        List<String> currentscan = new ArrayList<String>();

        for (WlBlip o : results) {
            currentscan.add(o.BSSID);
        }

        accumulateScans.add(currentscan);

        List<String> exsitsinaccumulate = new ArrayList<String>();

        for (List<String> list : accumulateScans) {
            for (String o : list) {
                if (bssids.contains(o) && !exsitsinaccumulate.contains(o)) {
                    exsitsinaccumulate.add(o);
                }
            }
        }

        if (exsitsinaccumulate.size() >= threshold) {
            result = true;
        }

        return result;
    }


    private boolean isBlueToothOn() {
        BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
        return btAdapter != null && btAdapter.isEnabled();
    }

}
