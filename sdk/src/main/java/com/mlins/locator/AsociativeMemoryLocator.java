package com.mlins.locator;

import android.annotation.SuppressLint;
import android.graphics.PointF;

import com.mlins.utils.FacilityConf;
import com.mlins.utils.FacilityContainer;
import com.mlins.utils.Lookup;
import com.mlins.utils.MathUtils;
import com.mlins.utils.PropertyHolder;
import com.mlins.wireless.WlBlip;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;

public class AsociativeMemoryLocator {
    private static final String FILE_NAME = "scan results" + File.separator
            + PropertyHolder.getInstance().getMatrixFilePrefix() + "matrix.txt";
    private static final String matrixBinFileName = PropertyHolder
            .getInstance().getMatrixFilePrefix() + "matrix.bin";
    private static final String FILE_BIN_NAME = "scan results" + File.separator
            + matrixBinFileName;
    private static final float DIFF_ANGLE_TRESH = 120;

    public Map<String, Integer> INDEX_MAP = new HashMap<String, Integer>();
    public float[] mins;
    public float[] maxs;
    protected List<String> ssidnames = new ArrayList<String>();
    protected boolean isFirstLoad = false;
    PointF lastpt = null;
    List<AssociativeData> theList = new ArrayList<AssociativeData>();
    private File matrixBinFile;
    private List<AssociativeDataSorter> closePoints = new ArrayList<AssociativeDataSorter>();
    private List<AssociativeDataSorter> floorclosePoints = new ArrayList<AssociativeDataSorter>();
    private float zeroValue;
    private AssociativeDataSorter mBestMatch;
    private AssociativeDataSorter mSecondMatch;
    private PointF lastAverage;
    private int LoadedFloor;
    private String LoadedFacility;


    private ArrayList<PointF> movingAvgSW = new ArrayList<PointF>();

    public static AsociativeMemoryLocator getInstance() {
        return Lookup.getInstance().get(AsociativeMemoryLocator.class);
    }

    public static String getFileName() {
        return FILE_NAME;
    }

    public static String getBinFileName() {
        return FILE_BIN_NAME;
    }

    public PointF getLastAverage() {
        return lastAverage;
    }

    public void setLastAverage(PointF lastAverage) {
        this.lastAverage = lastAverage;
    }

    /**
     * loads the matrix data from file
     */

    public void load(boolean isselectfloor) {
        FacilityConf facConf = FacilityContainer.getInstance().getCurrent();
        if (facConf == null) {
            return;
        }
        int floor = facConf.getSelectedFloor(); //FacilityConf.getInstance().getSelectedFloor();
        load(floor, isselectfloor);

    }

    public void load() {
        FacilityConf facConf = FacilityContainer.getInstance().getCurrent();
        if (facConf == null) {
            return;
        }

        int floor = facConf.getSelectedFloor(); //FacilityConf.getInstance().getSelectedFloor();
        String facility = PropertyHolder.getInstance().getFacilityID();
        if (floor == LoadedFloor && facility.equals(LoadedFacility))
            return;
        load(floor, false);

        // reset when loading new floor
        movingAvgSW = new ArrayList<PointF>();

        LoadedFacility = PropertyHolder.getInstance().getFacilityID();
        LoadedFloor = floor;
    }

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
                        .getMatrixFilePrefix() + "floorselection.txt";
                file = new File(facilitydir, selectfile);
                //XXX bin matrix debug
                //String appPath = PropertyHolder.getInstance().getExternalStoragedir();
                //file = new File(appPath, "test.bin");
            } else {
                file = new File(floordir, getFileName());
            }

            //XXX bin matrix debug
            //String appPath = PropertyHolder.getInstance().getExternalStoragedir();
            //file = new File(appPath, "test.txt");

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

            loadDataList(pointsMap);
            setZeroValue();
            // collectAreaData();
            if (!(this instanceof FloorSelector)) {
                sortTheList();
            }
        }
    }

    protected void sortTheList() {

        if (theList == null)
            return;

        Collections.sort(theList);

    }

    // private void collectAreaData() {
    // for (AssociativeData dot : theList) {
    // for (AssociativeData closedot : theList) {
    // if ((Math.abs(dot.point.x - closedot.point.x) < 100)
    // && (Math.abs(dot.point.y - closedot.point.y) < 100)) {
    // dot.getAreadata().add(closedot);
    // }
    // }
    // }

    // }

    public void loadBin(int floor) {

        initIndexMap();
        String floordir = PropertyHolder.getInstance().getFacilityDir() + "/"
                + floor;
        File file = new File(floordir, getBinFileName());

        //XXX bin matrix debug
        //String appPath = PropertyHolder.getInstance().getExternalStoragedir();
        //file = new File(appPath, "test.bin");

        if (!file.isFile())
            return;
        InputStream buffer = null;
        // ObjectInputStream input = null;
        try {
            MatrixBinRep mbr = new MatrixBinRep(theList, INDEX_MAP, ssidnames,
                    mins, maxs);
            buffer = new BufferedInputStream(new FileInputStream(file));
            // input = new ObjectInputStream(buffer);
            // mbr.readObject(input);
            mbr.readObject(buffer);

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
        // loadDataList(pointsMap);
        setZeroValue();
        // collectAreaData();
    }

    public boolean saveBin() {

        if (theList == null) {
            return false;
        }
        if (theList.size() == 0) {
            return false;
        }

        MatrixBinRep mbr = new MatrixBinRep(theList, INDEX_MAP, ssidnames,
                mins, maxs);

        File dir = new File(PropertyHolder.getInstance().getFloorDir(),
                "scan results");

        OutputStream outBin = null;

        matrixBinFile = new File(dir, matrixBinFileName);

        //XXX bin matrix debug
        //String appPath = PropertyHolder.getInstance().getExternalStoragedir();
        //matrixBinFile = new File(appPath, "test.bin");

        if (!matrixBinFile.exists()) {
            try {
                matrixBinFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {

            outBin = new BufferedOutputStream(new FileOutputStream(
                    matrixBinFile, false));
            // ObjectOutputStream output = new ObjectOutputStream(outBin);
            // mbr.writeObject(output);
            mbr.writeObject(outBin);

            outBin.flush();
            // output.flush();

            return true;
        } catch (Exception e) {
            e.toString();
        } finally {
            try {
                if (outBin != null) {
                    outBin.close();
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            outBin = null;
        }
        return false;

    }

    protected boolean ignored(String ssid) {
        if (PropertyHolder.getInstance().getSsidFilter().contains(ssid)) {
            return true;
        } else {
            return false;
        }

    }

    @SuppressLint("UseValueOf")
    protected void initIndexMap() {
        INDEX_MAP.clear();
        ssidnames.clear();
        theList.clear();
        closePoints.clear();
        mins = maxs = null;
    }

    protected void loadDataList(Map<String, Map<String, Double>> pointsMap) {
        Set<String> pts = pointsMap.keySet();
        Map<String, Double> idMap;
        mins = maxs = null;
        for (String pt : pts) {
            idMap = pointsMap.get(pt);
            float[] v = new float[INDEX_MAP.size()];
            for (int i = 0; i < v.length; i++) {
                v[i] = -127;
            }
            for (Entry<String, Double> e : idMap.entrySet()) {
                int i = getArrayPosition(e.getKey());
                if (i != -1) {
                    v[i] = e.getValue().floatValue();
                }
            }
            updateMinMax(v);
            String[] coords = pt.split(",");
            PointF pf = new PointF(Float.valueOf(coords[0]),
                    Float.valueOf(coords[1]));
            AssociativeData data = new AssociativeData(pf, v);
            if (coords.length > 2) {
                data.setAngle(Float.parseFloat(coords[2]));
            }
            theList.add(data);
        }
        normalizeList(mins, maxs);
        BaseMatrixDataHelper.getInstance().setMatrix(theList);
        BaseMatrixDataHelper.getInstance().setSSIDNames(ssidnames);
    }

    protected void updateMinMax(float[] v) {
        if (maxs == null) {
            maxs = new float[v.length];
            System.arraycopy(v, 0, maxs, 0, v.length);
        } else {
            for (int i = 0; i < v.length; i++) {
                maxs[i] = Math.max(maxs[i], v[i]);
            }
        }
        if (mins == null) {
            mins = new float[v.length];
            System.arraycopy(v, 0, mins, 0, v.length);

        } else {
            for (int i = 0; i < v.length; i++) {
                mins[i] = Math.min(mins[i], v[i]);
            }
        }
    }

    protected void normalizeList(float[] min, float max[]) {
        for (AssociativeData p : theList) {
            p.normalizedvector = MathUtils.normalizeVector(p.vector, min, max);
        }
    }

    // public PointF findClosestPoint(List<WlBlip> blips) {
    //
    // PointF lastLoc = getAvePoint();
    //
    // closePoints.clear();
    // if (theList.isEmpty()) {
    // return null;
    // }
    //
    // float[] v = createVector(blips);
    // float[] nv = MathUtils.normalizeVector(v, mins, maxs);
    //
    //
    // int locCloseRange = PropertyHolder.getInstance()
    // .getLocationCloseRange();
    // List<AssociativeDataSorter> tmpList = new
    // ArrayList<AssociativeDataSorter>();
    // for (Iterator<AssociativeData> iterator = theList.iterator(); iterator
    // .hasNext();) {
    // AssociativeData associativeData = (AssociativeData) iterator.next();
    //
    // // if in range
    // if (lastLoc != null && !(this instanceof FloorSelector)) {
    // if (Math.abs((associativeData.getX() - (lastLoc.x))) <= locCloseRange
    // && Math.abs((associativeData.getY() - (lastLoc.y))) <= locCloseRange) {
    // double d = associativeData.normalDistance(v, nv);
    // tmpList.add(new AssociativeDataSorter(associativeData, d));
    // }
    // } else {
    // double d = associativeData.normalDistance(v, nv);
    // tmpList.add(new AssociativeDataSorter(associativeData, d));
    // }
    // }
    //
    // Collections.sort(tmpList, new Comparator<AssociativeDataSorter>() {
    // @Override
    // public int compare(AssociativeDataSorter lhs,
    // AssociativeDataSorter rhs) {
    // return lhs.compare(rhs);
    // }
    // });
    //
    // setClosePoints(tmpList);
    // setFloorClosePoints(tmpList);
    // mBestMatch = tmpList.get(0);
    // mSecondMatch = tmpList.size() >= 2 ? tmpList.get(1) : null;
    //
    // if (tmpList.size() >= 3) {
    // return findAverage(tmpList.get(0).data, tmpList.get(1).data,
    // tmpList.get(2).data, nv, v);
    // }
    // return mBestMatch.data.point;
    // }

    protected int getArrayPosition(String bssid) {
        Integer index = INDEX_MAP.get(bssid);
        if (index == null) {
            return -1;
        }
        return index;
    }

    public PointF findClosestPoint(List<WlBlip> blips) {
        return findClosestPoint(blips, -999);

    }

    public PointF findClosestPoint(List<WlBlip> blips, float angle) {

        PointF lastLoc = null;
        if (!(this instanceof FloorSelector)) {
            lastLoc = getAvePoint();
        }

        closePoints.clear();
        if (theList.isEmpty()) {
            return null;
        }

        float[] v = createVector(blips);
        float[] nv = MathUtils.normalizeVector(v, mins, maxs);

        double distX = 0;
        double distY = 0;
        double diffAngle = 0;

        FacilityConf facConf = FacilityContainer.getInstance().getCurrent();

        int locCloseRange = 300;

        if (facConf != null) {
            locCloseRange = facConf.getNdkCloseRange();
        }

        // List<AssociativeDataSorter> tmpList = new
        // ArrayList<AssociativeDataSorter>();
        TreeSet<AssociativeDataSorter> tmpList = new TreeSet<AssociativeDataSorter>(
                new Comparator<AssociativeDataSorter>() {
                    @Override
                    public int compare(AssociativeDataSorter lhs,
                                       AssociativeDataSorter rhs) {
                        return lhs.compare(rhs);
                    }
                });

        if (lastLoc == null) {
            for (AssociativeData associativeData : theList) {

                // if in range
                if (this instanceof FloorSelector) { // in case of floor
                    // selection
                    double d = associativeData.normalDistance(v, nv);
                    tmpList.add(new AssociativeDataSorter(associativeData, d));
                } else {
                    if (lastLoc != null) { // Search within radius specified by
                        // locCloseRange

                        float p2m = 1;

                        if (facConf != null) {
                            p2m = facConf.getPixelsToMeter();
                        }
                        distX = Math.abs((associativeData.getX() - (lastLoc.x))) / p2m;
                        distY = Math.abs((associativeData.getY() - (lastLoc.y))) / p2m;
                        diffAngle = Math.abs(angle - associativeData.getAngle());

                        if (distX <= locCloseRange && distY <= locCloseRange) {
                            if ((associativeData.getAngle() != -999 && diffAngle <= DIFF_ANGLE_TRESH)
                                    || associativeData.getAngle() == -999) {
                                double d = associativeData
                                        .normalDistance(v, nv);
                                tmpList.add(new AssociativeDataSorter(
                                        associativeData, d));
                            }
                        }

                    } else { // first location find request
                        double d = associativeData.normalDistance(v, nv);
                        tmpList.add(new AssociativeDataSorter(associativeData,
                                d));
                    }
                }

            }
        } else if (!(this instanceof FloorSelector)) {
            AssociativeData dummySearch = new AssociativeData(lastLoc, null); // dummy
            // used
            // for
            // search
            // only

            int keyToStart = Collections.binarySearch(theList, dummySearch);
            if (keyToStart < 0) {
                keyToStart = keyToStart * -1 + 1;
            }

            int upSearchKey = keyToStart;
            float assoAngle = 0;

            while (upSearchKey < theList.size()) {

                float p2m = 1;

                if (facConf != null) {
                    p2m = facConf.getPixelsToMeter();
                }

                distX = Math.abs((theList.get(upSearchKey).getX() - (lastLoc.x))) / p2m;
                distY = Math.abs((theList.get(upSearchKey).getY() - (lastLoc.y))) / p2m;
                assoAngle = theList.get(upSearchKey).getAngle();
                diffAngle = Math.abs(angle - assoAngle);

                if (distX <= locCloseRange && distY <= locCloseRange) {
                    if ((diffAngle <= DIFF_ANGLE_TRESH && assoAngle != -999)
                            || assoAngle == -999) {
                        double d = theList.get(upSearchKey).normalDistance(v,
                                nv);
                        tmpList.add(new AssociativeDataSorter(theList
                                .get(upSearchKey), d));
                    }

                } else {
                    break;
                }

                upSearchKey++;
            }

            int downSearchKey = keyToStart;

            while (downSearchKey >= 0) {

                float p2m = 1;

                if (facConf != null) {
                    p2m = facConf.getPixelsToMeter();
                }

                distX = Math.abs((theList.get(downSearchKey).getX() - (lastLoc.x))) / p2m;
                distY = Math.abs((theList.get(downSearchKey).getY() - (lastLoc.y))) / p2m;
                assoAngle = theList.get(downSearchKey).getAngle();
                diffAngle = Math.abs(angle - assoAngle);


                if (distX <= locCloseRange && distY <= locCloseRange) {
                    if ((diffAngle <= DIFF_ANGLE_TRESH && assoAngle != -999)
                            || assoAngle == -999) {
                        double d = theList.get(downSearchKey).normalDistance(v,
                                nv);
                        tmpList.add(new AssociativeDataSorter(theList
                                .get(downSearchKey), d));
                    }
                } else {
                    break;
                }
                downSearchKey--;
            }

        }
        // Collections.sort(tmpList, new Comparator<AssociativeDataSorter>() {
        // @Override
        // public int compare(AssociativeDataSorter lhs,
        // AssociativeDataSorter rhs) {
        // return lhs.compare(rhs);
        // }
        // });

        setClosePoints(tmpList);
        setFloorClosePoints(tmpList);
        if (tmpList != null && tmpList.size() > 0) {
            mBestMatch = tmpList.first();

            if (!(this instanceof FloorSelector)
                    && PropertyHolder.getInstance()
                    .isMovingAverageOverLocation()) {
                addToSlidingWindow(mBestMatch.data.getPoint());
                return getMovingAverageLoc();
            } else {
                return mBestMatch.data.getPoint();
            }

        } else {

            return new PointF(0, 0);
        }
        // mSecondMatch = tmpList.size() >= 2 ? tmpList.get(1) : null;
        //
        // if (tmpList.size() >= 3) {
        // return findAverage(tmpList.get(0).data, tmpList.get(1).data,
        // tmpList.get(2).data, nv, v);
        // }

    }

    public AssociativeDataSorter getBestMatch() {
        return mBestMatch;
    }

    public AssociativeDataSorter getSecondMatch() {
        return mSecondMatch;
    }

    private PointF findAverage(AssociativeData result, AssociativeData result1,
                               AssociativeData result2, float[] nv, float[] v) {
        PointF pt = new PointF();
        float d1 = result.normalDistance(v, nv);
        float d2 = result1.normalDistance(v, nv);
        // double d3 = result2.normalDistance(v,nv);
        float sum = d1 + d2;

        // pt.x = (float) (((1.0 - d1 / sum) * result.point.x + (1.0 - d2 / sum)
        // * result1.point.x + (1.0 - d3 / sum) * result2.point.x) / 2.0);
        // pt.y = (float) (((1.0 - d1 / sum) * result.point.y + (1.0 - d2 / sum)
        // * result1.point.y + (1.0 - d3 / sum) * result2.point.y) / 2.0);
        pt.x = (float) ((((sum - d1) / sum) * result.getPoint().x + ((sum - d2) / sum)
                * result1.getPoint().x));
        pt.y = (float) ((((sum - d1) / sum) * result.getPoint().y + ((sum - d2) / sum)
                * result1.getPoint().y));

        return pt; // result.point;
    }

    private float[] createVector(List<WlBlip> blips) {
        float[] result = new float[INDEX_MAP.size()];
        for (int i = 0; i < result.length; i++) {
            result[i] = -127.0f;
        }
        for (WlBlip blip : blips) {
            int index = getArrayPosition(blip.BSSID);
            if (index != -1) {
                result[index] = blip.level;
            }
        }
        return result;
    }

    // public void setClosePoints(List<AssociativeDataSorter> sorted) {
    // closePoints.clear();
    // for (AssociativeDataSorter element : sorted) {
    // closePoints.add(element);
    // if (closePoints.size() == PropertyHolder.getInstance().getK())
    // return;
    // }
    // }

    public List<AssociativeDataSorter> getClosePoints() {
        return closePoints;
    }

    // public void setFloorClosePoints(List<AssociativeDataSorter> sorted) {
    // floorclosePoints.clear();
    // for (AssociativeDataSorter element : sorted) {
    // floorclosePoints.add(element);
    // if (floorclosePoints.size() == PropertyHolder.getInstance()
    // .getFloorSelectionK())
    // break;
    // }
    // }

    public void setClosePoints(TreeSet<AssociativeDataSorter> sorted) {
        closePoints.clear();
        for (AssociativeDataSorter element : sorted) {
            closePoints.add(element);
            if (closePoints.size() == PropertyHolder.getInstance().getK())
                return;
        }
    }

    public List<AssociativeDataSorter> getFloorClosePoints() {
        return floorclosePoints;
    }

    public void setFloorClosePoints(TreeSet<AssociativeDataSorter> sorted) {
        floorclosePoints.clear();
        for (AssociativeDataSorter element : sorted) {
            floorclosePoints.add(element);
            if (floorclosePoints.size() == PropertyHolder.getInstance()
                    .getFloorSelectionK())
                break;
        }
    }

    public float getZeroValue() {
        return zeroValue;
    }

    public void setZeroValue() {
        this.zeroValue = -127.0f;
        // if (mins == null) {
        // this.zeroValue = 1;
        // return;
        // }
        // float result = 1;
        // for (int i = 0; i < mins.length; i++) {
        // if (mins[i] < result) {
        // result = mins[i];
        // }
        // }
        // result -= 10;
        // this.zeroValue = result;
    }

    public float[] getMins() {
        return mins;
    }

    public void setMins(float[] mins) {
        this.mins = mins;
    }

    public float[] getMaxs() {
        return maxs;
    }

    public void setMaxs(float[] maxs) {
        this.maxs = maxs;
    }

    // XXX OPTIMIZED
    public PointF getAvePoint() {
        float sumx = 0;
        float sumy = 0;

        FacilityConf facConf = FacilityContainer.getInstance().getCurrent();

        if (closePoints != null && closePoints.size() > 1) {

            for (AssociativeDataSorter p : closePoints) {
                sumx += p.data.getPoint().x;
                sumy += p.data.getPoint().y;
            }
            float ax = 0;
            float ay = 0;
            if (closePoints.size() > 0) {
                ax = sumx / closePoints.size();
                ay = sumy / closePoints.size();
            }
            PointF avePt = new PointF(ax, ay);
            if (lastAverage != null) {

                float p2m = 1;
                if (facConf != null) {
                    p2m = facConf.getPixelsToMeter();
                }

                double distancefromlast = MathUtils.distance(avePt, lastAverage) / p2m; //FacilityConf.getInstance().getPixelsToMeter();
                int averagerange = PropertyHolder.getInstance().getAverageRange();
                if (distancefromlast > averagerange) {
                    avePt.x = (avePt.x + lastAverage.x) / 2;
                    avePt.y = (avePt.y + lastAverage.y) / 2;
                }


            }
            lastAverage = avePt;
            return lastAverage;
        } else
            return null;

    }

    public List<AssociativeData> getTheList() {
        return theList;
    }

    public void setTheList(List<AssociativeData> theList) {
        this.theList = theList;
    }

    public Map<String, Integer> getINDEX_MAP() {
        return INDEX_MAP;
    }

    // public PointF getAvePoint() {
    // float sumx = 0;
    // float sumy = 0;
    // List<AssociativeDataSorter> points = new
    // ArrayList<AssociativeDataSorter>();
    // if (getClosePoints() != null && getClosePoints().size() > 1) {
    // List<AssociativeDataSorter> lpoints = getClosePoints();
    // if (lpoints != null) {
    // points.addAll(lpoints);
    // }
    // for (AssociativeDataSorter p : points) {
    // sumx += p.data.point.x;
    // sumy += p.data.point.y;
    // }
    // float ax = 0;
    // float ay = 0;
    // if (points.size() > 0) {
    // ax = sumx / points.size();
    // ay = sumy / points.size();
    // }
    // PointF avePt = new PointF(ax, ay);
    // if (lastAverage != null) {
    // double distancefromlast = MathUtils
    // .distance(avePt, lastAverage)
    // / FacilityConf.getInstance().getPixelsToMeter();
    // int averagerange = PropertyHolder.getInstance()
    // .getAverageRange();
    // if (distancefromlast > averagerange) {
    // avePt.x = (avePt.x + lastAverage.x) / 2;
    // avePt.y = (avePt.y + lastAverage.y) / 2;
    // }
    // }
    // lastAverage = avePt;
    // return lastAverage;
    // } else
    // return null;
    //
    // }

    public void setINDEX_MAP(Map<String, Integer> iNDEX_MAP) {
        INDEX_MAP = iNDEX_MAP;
    }

    public List<String> getSsidnames() {
        return ssidnames;
    }

    public void setSsidnames(List<String> ssidnames) {
        this.ssidnames = ssidnames;
    }

    private void addToSlidingWindow(PointF loc) {

        if (movingAvgSW != null) {

            if (movingAvgSW.size() < PropertyHolder.getInstance()
                    .getMovingAverageOverLocationSW()) {
                movingAvgSW.add(loc);
            } else {
                if (movingAvgSW.size() > 0) {

                    movingAvgSW.add(movingAvgSW.size() - 1, loc);
                    movingAvgSW.remove(0);
                }
            }
        }

    }

    private PointF getMovingAverageLoc() {

        if (movingAvgSW.size() == 1) {
            return movingAvgSW.get(0);
        }

        float sumX = 0;
        float sumY = 0;
        for (PointF p : movingAvgSW) {
            sumX += p.x;
            sumY += p.y;
        }

        PointF mvp = new PointF();
        mvp.x = sumX / ((float) movingAvgSW.size());
        mvp.y = sumY / ((float) movingAvgSW.size());

        return mvp;
    }

    public boolean isWifiHidden(int index) {
        if (ssidnames != null) {
            if (index >= 0 && index < ssidnames.size()) {
                if (ssidnames.get(index).equals("")) {
                    return true;
                }
            }
        }
        return false;
    }

    public List<MatrixPoint> convertBinToMatrixPoints() {
        List<MatrixPoint> matrixpoints = new ArrayList<MatrixPoint>();

        FacilityConf facConf = FacilityContainer.getInstance().getCurrent();

        if (facConf == null) {
            return new ArrayList<MatrixPoint>();
        }

        int floor = facConf.getSelectedFloor(); //FacilityConf.getInstance().getSelectedFloor();
        loadBin(floor);
        for (AssociativeData o : theList) {
            PointF mppoint = o.getPoint();
            List<WlBlip> mpblips = new ArrayList<WlBlip>();
            float[] mpvector = o.vector;
            for (String s : INDEX_MAP.keySet()) {
                int index = INDEX_MAP.get(s);
                int level = (int) mpvector[index];
                if (level != -127) {
                    String SSID = ssidnames.get(index);
                    String BSSID = s;
                    WlBlip blip = new WlBlip(SSID, BSSID, level, 0, 0);
                    mpblips.add(blip);
                }
            }
            MatrixPoint mp = new MatrixPoint();
            mp.setPoint(mppoint);
            mp.setBlips(mpblips);
            matrixpoints.add(mp);
        }
        load(floor, false);
        return matrixpoints;
    }

}
