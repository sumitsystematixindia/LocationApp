package com.mlins.utils;

import com.mlins.project.Campus;
import com.mlins.project.ProjectConf;
import com.mlins.wireless.WlBlip;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FacilitySelector implements Cleanable {

    private List<FacilityBSSIDS> facilites = new ArrayList<FacilityBSSIDS>();

    public FacilitySelector() {
        LoadFacilities();
    }

    public static FacilitySelector getInstance() {
        return Lookup.getInstance().get(FacilitySelector.class);
    }

    public static void releaseInstance() {
        Lookup.getInstance().remove(FacilitySelector.class);
    }

    public void clean() {
        facilites.clear();
    }

    public String getFacilityByBlips(List<WlBlip> blips) {
        String result = "unknown";

        List<WlBlip> filteredblips = new ArrayList<WlBlip>(blips);
        List<FacilityBSSIDS> candidates = new ArrayList<FacilityBSSIDS>();

        if (filteredblips != null && filteredblips.size() > 0) {
            // int minimumdevices =
            // PropertyHolder.getInstance().getMinimumDevicesForEntrance();
            // LoadFacilities();
//			int maxwifi = 0;
            for (FacilityBSSIDS f : facilites) {
                Campus ccampus = ProjectConf.getInstance().getSelectedCampus();
                FacilityConf fac = null;
                if (ccampus != null) {
                    Map<String, FacilityConf> campusmap = ccampus
                            .getFacilitiesConfMap();
                    fac = campusmap.get(f.getName());
                    if (fac != null) {
                        int enterminimumdevices = fac.getMinimumDevicesForEntrance();
                        int enterlevel = fac.getBlipLevelForEntrance();
                        f.setExistsWifiCount(filteredblips, enterlevel);
                        int wificount = f.getExistsWifiCount();
                        if (wificount >= enterminimumdevices) {
                            candidates.add(f);
//							result = f.getName();
//							maxwifi = wificount;
                        }
                    }
                }
            }

            if (candidates.size() == 1) {
                result = candidates.get(0).getName();
            } else {
                WlBlip nearestblips = getNearestBlips(blips);
                if (nearestblips != null) {
                    for (FacilityBSSIDS o : candidates) {
                        if (o.getWifiSet().contains(nearestblips.BSSID)) {
                            result = o.getName();
                            break;
                        }
                    }
                }
            }

        }

        return result;

    }

    public String getBridgeFacilityByBlips(List<WlBlip> blips) {
        String result = "unknown";

        List<WlBlip> filteredblips = new ArrayList<WlBlip>(blips);
        List<FacilityBSSIDS> candidates = new ArrayList<FacilityBSSIDS>();

        if (filteredblips != null && filteredblips.size() > 0) {
            // int minimumdevices =
            // PropertyHolder.getInstance().getMinimumDevicesForEntrance();
            // LoadFacilities();
//			int maxwifi = 0;
            for (FacilityBSSIDS f : facilites) {
                Campus ccampus = ProjectConf.getInstance().getSelectedCampus();
                FacilityConf fac = null;
                if (ccampus != null) {
                    Map<String, FacilityConf> campusmap = ccampus
                            .getFacilitiesConfMap();
                    fac = campusmap.get(f.getName());
                    if (fac != null) {
                        int enterminimumdevices = fac.getBridgeDevicesForEntrance();
                        int enterlevel = fac.getBridgeLevelForEntrance();
                        f.setExistsWifiCount(filteredblips, enterlevel);
                        int wificount = f.getExistsWifiCount();
                        if (wificount >= enterminimumdevices) {
                            candidates.add(f);
//							result = f.getName();
//							maxwifi = wificount;
                        }
                    }
                }
            }

            if (candidates.size() == 1) {
                result = candidates.get(0).getName();
            } else {
                WlBlip nearestblips = getNearestBlips(blips);
                if (nearestblips != null) {
                    for (FacilityBSSIDS o : candidates) {
                        if (o.getWifiSet().contains(nearestblips.BSSID)) {
                            result = o.getName();
                            break;
                        }
                    }
                }
            }

        }

        return result;

    }

    private WlBlip getNearestBlips(List<WlBlip> blips) {
        WlBlip result = null;
        int max = -1000;
        for (WlBlip o : blips) {
            if (o.level > max) {
                max = o.level;
                result = o;
            }
        }
        return result;
    }

    private void LoadFacilities() {
        // List<String> facilitynames = new ArrayList<String>();
        // File dir = PropertyHolder.getInstance().getCampusDir();
        // String facilitiestxtfilename = "facilities.txt";
        // File facilityfile = new File(dir, facilitiestxtfilename);
        // if (!facilityfile.exists()) {
        // return;
        // }
        // BufferedReader in = null;
        // try {
        // in = new BufferedReader(new FileReader(facilityfile));
        // String line = null;
        // while ((line = in.readLine()) != null) {
        // String[] vals = line.split("\t");
        // if (vals.length >= 1 && vals[0] != null && !vals[0].equals("")) {
        // String name = vals[0];
        // facilitynames.add(name);
        // }
        // }
        // } catch (IOException e) {
        // e.toString();
        // } finally {
        // if (in != null)
        // try {
        // in.close();
        // } catch (Exception e2) {
        // Log.e("", e2.getMessage());
        // e2.printStackTrace();
        // }
        // }

        // List<String> facilitynames = new ArrayList<String>();
        Campus campus = ProjectConf.getInstance().getSelectedCampus();
        if (campus != null) {
            Map<String, FacilityConf> facilitiesmap = campus.getFacilitiesConfMap();

            if (facilitiesmap != null && facilitiesmap.size() > 0) {
                for (String f : facilitiesmap.keySet()) {
                    loadFacilityBssids(f);
                }
            }
        }

    }

    private void loadZipFacilityBssids(String name) {

        String url = ServerConnection.getResourcesUrl() + name + "/" + PropertyHolder.getInstance().getMatrixFilePrefix() + "bssids.txt";
        byte[] bytes = ResourceDownloader.getInstance().getUrl(url);
        if (bytes == null || bytes.length == 0) {
            return;
        }

        String cont = new String(bytes);

        FacilityBSSIDS fb = new FacilityBSSIDS(name);
        String bssids[] = cont.split("\\n");
        for (String line : bssids) {
            fb.addBSSID(line);
        }

        if (fb.getWifiSet().size() > 0) {
            facilites.add(fb);
        }


    }

    private void loadFacilityBssids(String name) {

        loadZipFacilityBssids(name);

//		String campusdir = PropertyHolder.getInstance().getCampusDir()
//				.toString();
//		String facilirdir = campusdir + "/" + name;
//		File dir = new File(facilirdir);
//		String filename = PropertyHolder.getInstance().getMatrixFilePrefix()
//				+ "BSSIDS.txt";
//		File bssidsfile = new File(dir, filename);
//		if (!bssidsfile.exists()) {
//			return;
//		}
//		FacilityBSSIDS fb = new FacilityBSSIDS(name);
//		BufferedReader in = null;
//		try {
//			in = new BufferedReader(new FileReader(bssidsfile));
//			String line = null;
//			while ((line = in.readLine()) != null) {
//				fb.addBSSID(line);
//			}
//		} catch (IOException e) {
//			e.toString();
//		} finally {
//			if (in != null)
//				try {
//					in.close();
//				} catch (Exception e2) {
//					Log.e("", e2.getMessage());
//					e2.printStackTrace();
//				}
//		}
//		if (fb.getWifiSet().size() > 0) {
//			facilites.add(fb);
//		}

    }
}
