package com.mlins.locator;

import android.graphics.PointF;

import com.mlins.utils.Cleanable;
import com.mlins.utils.Lookup;
import com.mlins.utils.PropertyHolder;
import com.mlins.wireless.WlBlip;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class WIFILevelMatrix implements Cleanable {

    private static final Float SCALE_X = 1.243f;
    private static final Float SCALE_Y = 1.275f;
    private static final String FILE_NAME = "wifimaps" + File.separator + "wifimatrix0.txt";

    /**
     * data members
     */

    Map<String, WIFIData> theMap = new HashMap<String, WIFIData>();

    public static WIFILevelMatrix getInstance() {
        return Lookup.getInstance().get(WIFILevelMatrix.class);
    }

    public static void releaseInstance() {
        Lookup.getInstance().remove(WIFILevelMatrix.class);
    }

    public void clean() {
        theMap.clear();
    }


    /**
     * functions
     */

    /**
     * loads the matrix data from file
     */
    public void load() {
        File file = new File(PropertyHolder.getInstance().getExternalStoragedir(), FILE_NAME);
        PointF p = null;
        int level = 0;
        String bssid = null;
        WIFIData wfd;
        WIFILevelData wldata;
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(file));
            String line = null;
            while ((line = br.readLine()) != null) {
                String[] fields = line.split("\t");
                if (fields.length != 9)
                    continue;
                bssid = fields[1];
//				XXX: remove scaling with new files.
                p = new PointF(Float.valueOf(fields[2]) * SCALE_X, Float.valueOf(fields[3]) * SCALE_Y);
                level = (int) Float.parseFloat(fields[7]);
                wldata = null;

                if (!theMap.containsKey(bssid)) {
                    WIFIData d = new WIFIData();
                    d.setBssid(bssid);
                    theMap.put(bssid, d);
                }
                wfd = theMap.get(bssid);
                for (WIFILevelData w : wfd.data) {
                    if (w.level == level) {
                        wldata = w;
                        break;
                    }
                }
                if (wldata == null) {
                    wldata = new WIFILevelData();
                    wldata.setLevel(level);
                    wfd.data.add(wldata);
                }
                wldata.add(p);
                wldata = null;

            }
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                br = null;
            }
        }

    }

    public List<PointF> findPossibleLoc(List<WlBlip> spots, boolean intersect) {
        if (spots.isEmpty()) {
            return null;
        }
        Iterator<WlBlip> spiter = spots.iterator();
        WlBlip spot;
        Set<PointF> locations = new HashSet<PointF>(); // shall have no dups.
        for (spot = spiter.next(), locations.addAll(findPossibleLoc(spot.BSSID, spot.level)); spiter.hasNext() && locations.isEmpty(); spot = spiter.next()) { //XXX: for now ignore empty locations.
            locations.addAll(findPossibleLoc(spot.BSSID, spot.level));
        }
        List<PointF> inlocs;
        while (spiter.hasNext()) {
            spot = spiter.next();
            inlocs = findPossibleLoc(spot.BSSID, spot.level);
            if (intersect) {
                if (!inlocs.isEmpty()) {
                    locations.retainAll(inlocs);
                }
            } else {
                locations.addAll(inlocs);
            }
        }
        return new ArrayList<PointF>(locations);
    }

    public List<PointF> findPossibleLoc(String bssid, int level) {
        List<PointF> result = new ArrayList<PointF>();
        if (theMap.containsKey(bssid)) {
            // the bssid is in the map
            WIFIData wifidata = theMap.get(bssid);
            List<WIFILevelData> levs = wifidata.find(level);
            if (levs != null) {
                for (WIFILevelData lev : levs) {
                    result.addAll(lev.getPoints());
                }
            }
        }
        return result;
    }
}
