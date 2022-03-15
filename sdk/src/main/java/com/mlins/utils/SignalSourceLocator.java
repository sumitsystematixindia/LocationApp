package com.mlins.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;

public class SignalSourceLocator {

    private static final String FILE_NAME = "scan results" + File.separator
            + PropertyHolder.getInstance().getMatrixFilePrefix() + "matrix.txt";
    public static double THRESHOLD = -70;
    public static double MAX_LEVEL_THR = -90;

    private Map<String, TreeSet<SignalSource>> SIGNALS_MAP = new HashMap<String, TreeSet<SignalSource>>();
    private int LoadedFloor;
    private String LoadedFacility;

    public static SignalSourceLocator getInstance() {
        return Lookup.getInstance().get(SignalSourceLocator.class);
    }

    /**
     * loads the matrix data from file
     */
    public void load() {
        FacilityConf facConf = FacilityContainer.getInstance().getCurrent();

        if (facConf == null) {
            return;
        }

        int floor = facConf.getSelectedFloor();
        String facility = PropertyHolder.getInstance().getFacilityID();
        if (floor == LoadedFloor && facility.equals(LoadedFacility))
            return;
        load(floor);
        LoadedFacility = PropertyHolder.getInstance().getFacilityID();
        LoadedFloor = floor;
    }

    private void load(int floor) {

        initSIGNALS_MAP();

        String floordir = PropertyHolder.getInstance().getFacilityDir() + "/"
                + floor;

        // load from matrix.txt

        File file = new File(floordir, FILE_NAME);

        if (!file.isFile())
            return;
        double x = 0.0;
        double y = 0.0;
        Double level = 0.0;
        String bssid = null;
        String ssid = null;
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(file));
            String line = null;
            while ((line = br.readLine()) != null) {
                String[] fields = line.split("\t");
                if (fields.length < 5)
                    continue;
                x = Double.parseDouble(fields[0]);
                y = Double.parseDouble(fields[1]);
                bssid = fields[2];
                ssid = fields[3];
                String signalSourceId = ssid + "_" + bssid;
                level = Double.parseDouble(fields[4]);

                if (!SIGNALS_MAP.containsKey(signalSourceId)) {

                    TreeSet<SignalSource> tmp = new TreeSet<SignalSource>();

                    tmp.add(new SignalSource(signalSourceId, x, y, level));

                    SIGNALS_MAP.put(signalSourceId, tmp);


                } else {
                    TreeSet<SignalSource> tmp = SIGNALS_MAP.get(signalSourceId);
                    if (tmp != null) {
                        tmp.add(new SignalSource(signalSourceId, x, y, level));
                    }
                }


            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                br = null;
            }
        }

    }

    private void initSIGNALS_MAP() {
        SIGNALS_MAP.clear();

    }


    public ArrayList<SignalSource> getSignalsSourcesLocations() {

        if (SIGNALS_MAP.size() == 0)
            return null;

        ArrayList<SignalSource> sigSrc = new ArrayList<SignalSource>();

        for (TreeSet<SignalSource> src : SIGNALS_MAP.values()) {
            if (src != null) {
                SignalSource sr = getWieghtedAvg(src);
                if (sr.getX() != -1f) {
                    sigSrc.add(sr);
                }
            }
        }
        return sigSrc;

    }

    private SignalSource getWieghtedAvg(TreeSet<SignalSource> src) {
        float i = 1;
        float sumX = 0;
        float sumY = 0;
        String name = "NO_AV";
        Double levelAvg = Double.valueOf(0);

        if (src != null && !src.isEmpty()) {
            SignalSource dummy = src.first();
            if (dummy.getLevel() < MAX_LEVEL_THR) {
                return new SignalSource(name, -1f, -1f, 0.0);
            }
            name = dummy.getSourceName();

        }


        for (SignalSource ss : src) {
            if (ss.getLevel() >= THRESHOLD) {
                sumX += ss.getX();
                sumY += ss.getY();
                levelAvg += ss.getLevel();
                i++;
            }
        }

        if (sumX == 0) {
            return new SignalSource(name, -1f, -1f, 0.0);
        }

        return new SignalSource(name, sumX / i, sumY / i, levelAvg / i);


    }

}
