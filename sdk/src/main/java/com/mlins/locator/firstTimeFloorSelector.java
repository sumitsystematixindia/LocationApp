package com.mlins.locator;

import com.mlins.utils.PropertyHolder;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

//import java.io.ObjectInputStream;

public class firstTimeFloorSelector extends FloorSelector {

    private static firstTimeFloorSelector instance = null;

    //now usages or state for now, no need to rework it
    public static firstTimeFloorSelector getInstance() {
        if (instance == null) {
            instance = new firstTimeFloorSelector();

        }
        return instance;

    }

    public static void releaseInstance() {
        if (instance != null) {
            instance = null;
        }
    }

//	private firstTimeFloorSelector(){
//		load(true);
//	}

    @Override
    public void load(int floor, boolean isselectfloor) {
        initIndexMap();

        String floordir = PropertyHolder.getInstance().getFacilityDir() + "/"
                + floor;

        if (PropertyHolder.getInstance().isTypeBin()) { // load from matrix.bin
            loadBin(floor);
        } else { // load from matrix.txt

            File file;
            if (isselectfloor) {
                String facilitydir = PropertyHolder.getInstance()
                        .getFacilityDir().toString();
                String selectfile = PropertyHolder.getInstance()
                        .getMatrixFilePrefix() + "firstfloorselection.txt";
                file = new File(facilitydir, selectfile);
            } else {
                file = new File(floordir, getFileName());
            }

            if (!file.isFile())
                return;
            Double level = 0.0;
            String bssid = null;
            String ssid = null;
            BufferedReader br = null;
            Map<String, Map<String, Double>> pointsMap = new HashMap<String, Map<String, Double>>();
            try {
                br = new BufferedReader(new FileReader(file));
                String line = null;
                while ((line = br.readLine()) != null) {
                    String[] fields = line.split("\t");
                    if (fields.length < 5)
                        continue;
                    bssid = fields[2];
                    ssid = fields[3];
                    if (!INDEX_MAP.containsKey(bssid)) {
                        if (ignored(ssid) && isFirstLoad == false) {
                            INDEX_MAP.put(bssid, -1);
                        } else {
                            INDEX_MAP.put(bssid, INDEX_MAP.size());
                        }

                        ssidnames.add(ssid);
                    }
                    level = Double.parseDouble(fields[4]);
                    String key;
                    if (isselectfloor) {
                        key = fields[0] + "," + fields[1] + "," + fields[5];
                    } else {
                        if (fields.length == 6) {
                            key = fields[0] + "," + fields[1] + "," + fields[5];
                        } else {
                            key = fields[0] + "," + fields[1];
                        }
                    }

                    if (!pointsMap.containsKey(key)) {
                        Map<String, Double> idsMap = new HashMap<String, Double>();
                        idsMap.put(bssid, level);
                        pointsMap.put(key, idsMap);
                    } else {
                        pointsMap.get(key).put(bssid, level);
                    }

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

            loadDataList(pointsMap);
            setZeroValue();
            // collectAreaData();
            if (!(this instanceof FloorSelector)) {
                sortTheList();
            }
        }
    }

    @Override
    public void loadBin(int floor) {
        initIndexMap();
        String dir = PropertyHolder.getInstance().getFacilityDir().toString();
        File file = new File(dir, PropertyHolder.getInstance().getMatrixFilePrefix() + "firstfloorselection.bin");

        if (!file.isFile())
            return;
        InputStream buffer = null;
        //ObjectInputStream input=null;
        try {
            FloorSelectionBinRep mbr = new FloorSelectionBinRep(getTheList(), INDEX_MAP, getSsidnames(), mins, maxs);
            buffer = new BufferedInputStream(new FileInputStream(file));
            //input = new ObjectInputStream ( buffer );
            mbr.readObject(buffer);
            INDEX_MAP = mbr.getINDEX_MAP();
            theList = mbr.getTheList();
            ssidnames = mbr.getSsidnames();
            maxs = mbr.getMaxs();
            mins = mbr.getMins();


        } catch (FileNotFoundException e) {

            e.printStackTrace();
        } catch (IOException e) {

            e.printStackTrace();
        } catch (Exception e) {

            e.printStackTrace();
        } finally {
            try {
                if (buffer != null) {
                    buffer.close();
                }
            } catch (IOException e) {

                e.printStackTrace();
            }
        }


        setZeroValue();
    }
}
