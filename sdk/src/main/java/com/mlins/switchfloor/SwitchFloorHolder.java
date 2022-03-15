package com.mlins.switchfloor;

import android.util.Log;

import com.mlins.utils.PropertyHolder;
import com.mlins.utils.ResourceDownloader;
import com.mlins.utils.ServerConnection;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SwitchFloorHolder {
    static SwitchFloorHolder instance = null;
    private HashMap<String, List<SwitchFloorObj>> SwichFloorMap = new HashMap<String, List<SwitchFloorObj>>();

    static public SwitchFloorHolder getInstance() {
        if (instance == null) {
            instance = new SwitchFloorHolder();
        }
        return instance;
    }

    public static void releaseInstance() {
        if (instance != null) {
            instance = null;
        }
    }


    public void addFacility(String facilityid) {

        if (PropertyHolder.useZip) {
            addZipResFacility(facilityid);
        } else {

            if (facilityid != null && !SwichFloorMap.containsKey(facilityid)) {
                List<SwitchFloorObj> falciltyPoints = new ArrayList<SwitchFloorObj>();
                try {
                    File campusdir = PropertyHolder.getInstance().getCampusDir();
                    File dir = new File(campusdir, facilityid);
                    if (!dir.exists()) {
                        dir.mkdirs();
                    }
                    File sfile = new File(dir, "switchfloor.txt");
                    if (!sfile.exists()) {
                        return;
                    }
                    BufferedReader in = null;
                    try {
                        in = new BufferedReader(new FileReader(sfile));
                        String line = null;
                        while ((line = in.readLine()) != null) {
                            SwitchFloorObj s = new SwitchFloorObj();
                            s.parse(line);
                            falciltyPoints.add(s);
                        }
                    } catch (IOException e) {
                        e.toString();
                    } finally {
                        if (in != null)
                            try {
                                in.close();
                            } catch (Exception e2) {
                                Log.e("", e2.getMessage());
                                e2.printStackTrace();
                            }
                    }

                } catch (Throwable t) {
                    t.printStackTrace();
                }

                if (!falciltyPoints.isEmpty()) {
                    SwichFloorMap.put(facilityid, falciltyPoints);
                }
            }

        }
    }


    public void addZipResFacility(String facilityid) {

        if (facilityid != null && !SwichFloorMap.containsKey(facilityid)) {

            String url = ServerConnection.getResourcesUrl() + facilityid + "/" + "switchfloor.txt";
            byte[] bytes = ResourceDownloader.getInstance().getUrl(url);
            if (bytes == null || bytes.length == 0) {
                return;
            }

            List<SwitchFloorObj> falciltyPoints = new ArrayList<SwitchFloorObj>();
            try {

                String cont = new String(bytes);

                String switchArr[] = cont.split("\\n");
                for (String line : switchArr) {
                    SwitchFloorObj s = new SwitchFloorObj();
                    s.parse(line);
                    falciltyPoints.add(s);
                }

//				File campusdir = PropertyHolder.getInstance().getZipCampusFacilitiesDir();
//				File dir = new File(campusdir, facilityid);
//				if (!dir.exists()) {
//					dir.mkdirs();
//				}
//				File sfile = new File(dir, "switchfloor.txt");
//				if (!sfile.exists()) {
//					return;
//				}
//				BufferedReader in = null;
//				try {
//					in = new BufferedReader(new FileReader(sfile));
//					String line = null;
//					while ((line = in.readLine()) != null) {
//						SwitchFloorObj s = new SwitchFloorObj();
//						s.parse(line);
//						falciltyPoints.add(s);
//					}
//				} catch (IOException e) {
//					e.toString();
//				} finally {
//					if (in != null)
//						try {
//							in.close();
//						} catch (Exception e2) {
//							Log.e("", e2.getMessage());
//							e2.printStackTrace();
//						}
//				}

            } catch (Throwable t) {
                t.printStackTrace();
            }

            if (!falciltyPoints.isEmpty()) {
                SwichFloorMap.put(facilityid, falciltyPoints);
            }
        }

    }

    public List<SwitchFloorObj> getSwichFloorPoints(String Facilityid) {
        List<SwitchFloorObj> result = new ArrayList<SwitchFloorObj>();
        if (SwichFloorMap.containsKey(Facilityid)) {
            result = SwichFloorMap.get(Facilityid);
        }
        return result;
    }

}
