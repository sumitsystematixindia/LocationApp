package com.mlins.utils;

/**
 * @author meir
 * <p>
 * the class handles all the cached data
 */

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.PointF;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.mlins.dualmap.DualMapNavUtil;
import com.mlins.instructions.Instruction;
import com.mlins.kdtree.KDimensionalTree;
import com.mlins.maping.IconSprite;
import com.mlins.maping.LayerObject;
import com.mlins.utils.gis.Location;
import com.mlins.views.TouchImageView;
import com.spreo.nav.interfaces.IPoi;
import com.spreo.spreosdk.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@SuppressLint("NewApi")
public class PoiDataHelper implements Cleanable {

    String locale = PropertyHolder.getInstance().getAppLanguage(); //PropertyHolder.getInstance().getUserLaguagePreference();// java.util.Locale.getDefault().getDisplayName();
    Boolean PoisChanged = false;
    List<PoiType> types = new ArrayList<PoiType>();
    List<String> iconslist = new ArrayList<String>();
    // String locale = java.util.Locale.getDefault().getDisplayName();
    List<String> poikeywords = new ArrayList<String>();
    File dir = null;
    String fileName = "poi_list";
    String fileExt = ".txt";
    String fileFullName = "";
    String resFile = "";
    String inputtexttoremove;
    File mFile = null;
    List<PoiData> mPoi = new ArrayList<PoiData>();
    List<PoiData> userPois = new ArrayList<PoiData>();
    List<PoiData> mUserPois = new ArrayList<PoiData>();
    List<PoiData> allPoi = new ArrayList<PoiData>();
    PoiData currentPoi;
    private List<PoiType> poiCategories = new ArrayList<PoiType>();
    private PoiData lastPoiForIntentFlashLight;
    private Location lastPoiLocationForIntents;
    private PoiData lastPoiForIntentNavigationDialog;
    // XXX
    private String currloadedFacName = null;
    private int currloadedFloor = -100;
    private String alreadyLoadedAllPOISfac = null;
    private KDimensionalTree<PoiData> currFloorPoiTree = new KDimensionalTree<PoiData>();
    private PoiData movinPoi = null;
    private List<String> visbleCategories = null;
    //List<GalleryObject> allGalleryImages = new ArrayList<GalleryObject>();
    //List<GalleryObject> poiGalleryImages = new ArrayList<GalleryObject>();

    public PoiDataHelper() {
        super();
    }

    public static PoiDataHelper getInstance() {
        return Lookup.getInstance().get(PoiDataHelper.class);
    }

    public static void releaseInstance() {
        Lookup.getInstance().remove(PoiDataHelper.class);
    }

    public static String escapeTab(String txt) {
        if (txt == null)
            return "";

        String temp = txt.replaceAll("##TAB", "\t");
        temp = temp.replaceAll("##NL", "\n");
        temp = temp.replaceAll("##CRET", "\r");
        return temp;
    }

    // XXX -reset when load new facility
    private void reset() {
        currloadedFacName = null;
        alreadyLoadedAllPOISfac = null;
        currloadedFloor = -100;
        allPoi.clear();
    }

    public List<String> getPoikeywords() {
        return poikeywords;
    }

    public void setPoikeywords(List<String> poikeywords) {
        this.poikeywords = poikeywords;
    }

    public List<PoiType> getTypes() {
        return types;
    }

    public void addType(PoiType type) {
        types.add(type);

    }

    public PoiType getType(int index) {
        PoiType result = null;
        if (index < types.size() && index >= 0) {
            result = types.get(index);
        }
        return result;
    }

    public PoiData getCurrentPoi() {
        return currentPoi;
    }

    public void setCurrentPoi(PoiData currentPoi) {
        this.currentPoi = currentPoi;
    }

    public void clean() {
        mPoi.clear();
    }

    public List<PoiData> getPoiDataList() {
        return mPoi;
    }

    public List<IPoi> getAllPoiAsList() {

        List<IPoi> iPoisList = new ArrayList<IPoi>();
        String facId = PropertyHolder.getInstance().getFacilityID();
        if (facId != null && !facId.equals(alreadyLoadedAllPOISfac)) {
            reset();
            List<PoiData> pois = new ArrayList<PoiData>();

            getPoiCategories().clear();
            int numberoffloors = FacilityContainer.getInstance().getSelected().getFloorDataList()
                    .size();
            for (int i = 0; i < numberoffloors; i++) {
                if (FacilityConf.isCalibrationState()) {
                    loadPois(i);
                    pois.addAll(mPoi);
                } else {
                    List<PoiData> floorPois = loadPoisOfFloor(i);
                    pois.addAll(floorPois);
                }
            }

            setAllPoi(pois);

            alreadyLoadedAllPOISfac = facId;

        }

        iPoisList.addAll(allPoi);

        return iPoisList;

    }

    public List<IPoi> getAllPoiOfFloorAsList(int floor) {

        List<IPoi> iPoisList = new ArrayList<IPoi>();
        String facId = PropertyHolder.getInstance().getFacilityID();
        if (facId != null && !facId.equals(alreadyLoadedAllPOISfac)) {
            reset();
            List<PoiData> pois = new ArrayList<PoiData>();

            getPoiCategories().clear();
            int numberoffloors = FacilityContainer.getInstance().getSelected().getFloorDataList()
                    .size();
            for (int i = 0; i < numberoffloors; i++) {
                if (FacilityConf.isCalibrationState()) {
                    loadPois(i);
                    pois.addAll(mPoi);
                } else {
                    List<PoiData> floorPois = loadPoisOfFloor(i);
                    pois.addAll(floorPois);
                }
            }

            setAllPoi(pois);

            alreadyLoadedAllPOISfac = facId;

        }

        for (PoiData poi : allPoi) {
            if (((int) poi.getZ()) == floor) {
                iPoisList.add(poi);
            }
        }

        return iPoisList;

    }

    public List<PoiData> getAllPoi() {

        // XXX ADDED
        String facId = PropertyHolder.getInstance().getFacilityID();
        if (facId != null && !facId.equals(alreadyLoadedAllPOISfac)) {
            reset();
            List<PoiData> pois = new ArrayList<PoiData>();

            getPoiCategories().clear();
            int numberoffloors = FacilityContainer.getInstance().getSelected().getFloorDataList()
                    .size();
            for (int i = 0; i < numberoffloors; i++) {
                if (FacilityConf.isCalibrationState()) {
                    loadPois(i);
                    pois.addAll(mPoi);
                } else {
                    List<PoiData> floorPois = loadPoisOfFloor(i);
                    pois.addAll(floorPois);
                }
            }
            // XXX COMMENTED
            // loadPois();

//			Collections.sort(pois, new Comparator<PoiData>() {
//				@Override
//				public int compare(PoiData p1, PoiData p2) {
//					String s1 = p1.getpoiDescription();
//					String s2 = p2.getpoiDescription();
//					return s1.compareToIgnoreCase(s2);
//				}
//			});

            setAllPoi(pois);

            // XXX ADDED
            alreadyLoadedAllPOISfac = facId;

        }

        loadPois();
        return allPoi;
        // return pois;
    }

    private void setAllPoi(List<PoiData> allPoi) {
        this.allPoi = allPoi;
    }

    public void addpoi(PoiData poi) {
        if (poi != null) {
            mPoi.add(poi);
            PoisChanged = true;
        }
    }

    public PoiData getPoi(int index) {
        PoiData result = null;
        if (index < mPoi.size()) {
            result = mPoi.get(index);
        }
        return result;
    }

    public PoiData poiExists(PointF point) {
        List<PoiData> inRangePoi = new ArrayList<PoiData>();

        for (PoiData poi : mPoi) {
            if (poi.isPoiClickAble()) {
                PointF poipoint = new PointF(poi.getPoint().x,
                        poi.getPoint().y - 15);
                double d = MathUtils.distance(point, poipoint);
                if (Math.abs(d) < 25) {
                    inRangePoi.add(poi);
                }

            }
        }
        if (inRangePoi.size() > 1) {
            PoiData closepoi = findClsest(inRangePoi, point);
            return closepoi;

        } else if (inRangePoi.size() == 1) {
            return inRangePoi.get(0);
        } else {
            return null;
        }
    }

    private PoiData findClsest(List<PoiData> inRangePoi2, PointF point) {
        double d = Double.MAX_VALUE;
        double dist = 0;
        PoiData result = null;
        for (PoiData poi : inRangePoi2) {
            dist = MathUtils.distance(point, poi.getPoint());
            if (dist < d) {
                d = dist;
                result = poi;
            }
        }


        return result;
    }

    public void drawPois(TouchImageView tiv) {
        LayerObject poilayer = tiv.getLayerByName("poi");
        poilayer.clearSprites();

        float w = 0.9f;
        boolean highdeatails = true;
        if (tiv.getSaveScale() <= TouchImageView.ZOOM_LEVEL_THR) {
            w *= 0.7;
            highdeatails = false;
        }

        List<PoiData> combinedpois = new ArrayList<PoiData>();
        combinedpois.addAll(mPoi);
        combinedpois.addAll(mUserPois);

        for (PoiData poi : combinedpois) {

            if ((poi != null && ((poi.isShowPoiOnMap()) || PropertyHolder
                    .getInstance().isDarwpoiadmin()))
                    && (highdeatails == true || poi.isShowOnZoomLevel() == true)
                    && (!poi.getPoiNavigationType().equals("external"))) {
                Bitmap bm = poi.getIcon();//ResourceDownloader.getInstance().getLocalBitmap(poi.poiuri);
                if (bm == null) {
                    bm = BitmapFactory.decodeResource(tiv.getContext().getResources(), R.drawable.defualtpoiicon);
                }

                IconSprite is = new IconSprite(bm);
                //boolean isuerIcon=poi.isUserIconExists();
                is.scaleBitmap(w);
                is.setAlpha(180);
                PointF poiLoc = poi.getPoint();
                // poiLoc.y=poiLoc.y-20;

                is.setLoc(poiLoc);

                tiv.addPOI(is);
                poi.setPoiClickAble(true);
                poi.setShowPoiBubble(true);
            } else if (poi != null) {
                poi.setPoiClickAble(false);
                poi.setShowPoiBubble(false);
            }

        }

        tiv.invalidate();
    }

    public void drawPois(Canvas canvas) {
        for (PoiData poi : mPoi) {
            if (poi != null
                    && ((poi.isShowPoiOnMap()) || PropertyHolder.getInstance()
                    .isDarwpoiadmin())
                    && (!poi.getPoiNavigationType().equals("external"))) {
                PointF loc = poi.getPoint();
                if (loc == null)
                    return;
                loc = new PointF(loc.x, loc.y); // make local copy.

                Bitmap bm = poi.getIcon(); //ResourceDownloader.getInstance().getLocalBitmap(poi.poiuri);
                if (bm == null)
                    return;

                int h = bm.getHeight();
                int w = bm.getWidth();
                loc.offset((float) -w / 2, (float) -h / 2);
                canvas.drawBitmap(bm, loc.x, loc.y, null);
            }
        }
    }

    private String removesemicolum(String texttoremove) {
        texttoremove = texttoremove.substring(0, texttoremove.length() - 1);
        texttoremove = texttoremove.substring(1, texttoremove.length());
        return texttoremove;
    }

    public void savePois() {
        dir = new File(PropertyHolder.getInstance().getFloorDir(), "poi");
        fileFullName = fileName + resFile + fileExt;
        mFile = new File(dir, fileFullName);
        if (PoisChanged == true) {
            if (!dir.exists()) {
                dir.mkdirs();
            }
            if (mFile.exists()) {
                mFile.delete();
            }
            if (!mFile.exists()) {
                try {
                    mFile.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            BufferedWriter out = null;
            try {
                out = new BufferedWriter(new FileWriter(mFile, true));
                double z = FacilityContainer.getInstance().getSelected().getSelectedFloor();
                for (PoiData poi : mPoi) {
                    out.write(poi.point.x + "\t" + poi.point.y + "\t");
                    out.write(poi.poitype + "\t" + poi.poiuri + "\t" + z + "\t"
                            + poi.poidescription + "\t");
                    out.write(poi.getUrl() + "\t");
                    out.write(removesemicolum(poi.poiKeywords.toString())
                            + "\t");
                    out.write(poi.getDetails() + "\t");
                    out.write(poi.isShowPoiOnMap() + "\t");
                    out.write(poi.isShowPoiOnSearches() + "\t");
                    out.write(poi.isShowPoiBubble() + "\t");
                    out.write(poi.isPoiPlayMultyMedia() + "\t");
                    out.write(poi.getPoiNavigationType() + "\t");
                    out.write(poi.getPoiLatitude() + "\t");
                    out.write(poi.getPoiLongitude() + "\t");
                    out.write(poi.getFutureOptionONE() + "\t");
                    out.write(poi.getFutureOptionTWO() + "\t");
                    out.write(poi.getFutureOptionTHREE() + "\t");
                    out.write(poi.getPoiID() + "\t");
                    out.write(poi.isShowOnZoomLevel() + "\t");
                    out.write(poi.isInstructionsParticipate() + "\t");
                    out.write(removesemicolum(poi.getPhone1().toString())
                            + "\t");
                    out.write(removesemicolum(poi.getPhone2().toString())
                            + "\t");
                    out.write(poi.getEmailaddress() + "\t");
                    out.write(removesemicolum(poi.getPhone2hours().toString())
                            + "\t");
                    out.write(removesemicolum(poi.getActivehours().toString())
                            + "\t");
                    out.write(poi.getPoiofficeinstuctions() + "\t");
                    out.write(poi.isPoishowincategory() + "\t");
                    out.write(poi.getMediaurl() + "\t");
                    out.write("\n");
                    out.flush();
                }

            } catch (IOException e) {
                e.toString();
            } finally {
                if (out != null) {
                    try {
                        out.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    out = null;
                }
            }
        }

    }

    public List<String> getAllPoisIcons() {
        // iconslist.clear();
        // List<FloorData> datalist = FacilityConf.getInstance()
        // .getFloorDataList();
        // for (FloorData floorData : datalist) {
        // int floor = datalist.indexOf(floorData);
        // loadPois(floor);
        // }
        List<String> result = new ArrayList<String>();
        iconslist.clear();
        List<FloorData> datalist = FacilityContainer.getInstance().getSelected()
                .getFloorDataList();
        for (int floor = 0; floor < datalist.size(); floor++) {
            loadPois(floor);
        }

        for (String o : iconslist) {
            File f = new File(
                    PropertyHolder.getInstance().getFacilityDir(), o);
            if (!f.exists()) {
                result.add(o);
            }
        }

        return result;
    }

    public void saveIcons() {
        try {
            File facilityIconsDir = new File(PropertyHolder.getInstance()
                    .getFacilityDir(), "icons");
            facilityIconsDir.mkdirs();
            for (String iconUrl : iconslist) {
                File f = new File(
                        PropertyHolder.getInstance().getFacilityDir(), iconUrl);
                if (!f.exists()) {
                    byte[] iconData = ResourceDownloader.getInstance()
                            .getLocalCopy(iconUrl);
                    ResourceDownloader.getInstance()
                            .writeLocalCopy(f, iconData);
                }
            }

        } catch (Throwable e) {
            e.printStackTrace();
        }

    }

    public void loadPois() {
        // XXX POIS - CHANGED

        String facId = PropertyHolder.getInstance().getFacilityID();
        int floor = FacilityContainer.getInstance().getSelected().getSelectedFloor();
        if (!facId.equals(currloadedFacName)) {
            currloadedFloor = -100;
        }

        if (facId != null) {
            currloadedFacName = facId;
            currloadedFloor = floor;
            if (FacilityConf.isCalibrationState()) {
                loadPois(floor);
            } else {
                loadPoisOfFloor(floor);
            }

        }
        parseGalleryJson();
        // String facId= PropertyHolder.getInstance().getFacilityID();
        // int floor = FacilityConf.getInstance().getSelectedFloor();
        //
        // if(facId!=null && !facId.equals(currloadedFacName)
        // && floor!=currloadedFloor){
        // currloadedFacName=facId;
        // currloadedFloor=floor;
        // loadPois(floor);
        //
        // }
        // else{
        // if(facId!=null && facId.equals(currloadedFacName) &&
        // floor!=currloadedFloor){
        // currloadedFloor=floor;
        // loadPois(floor);
        // }
        // }
        // }

    }

    public void loadPois(int floor) {

        mPoi.clear();

        // XXX WHY DUPLICATE DOWNLOADS!!
        if (PropertyHolder.getInstance().isPoiRemote()) {
            loadPoisFromServer(floor);
        }
        if (PropertyHolder.getInstance().isPoiLocal()) {
            loadPoisFromLocal(floor);
        }
        /**
         * if (PropertyHolder.getInstance().isPoiLocal()){ loadPoisFromLocal(0);
         * }
         **/
        loadUserPois(floor);

        loadPoisKDimensionalTree();

    }

    // public void systenLanguage(){
    // //place holder
    // }

    private void loadUserPois(int floor) {

        for (IPoi poi : userPois) {
            if (poi != null) {
                PoiData p = (PoiData) poi;
                if (p.getZ() == floor && !mUserPois.contains(p)) {
                    mUserPois.add(p);
                }
            }
        }
    }

    private List<PoiData> loadPoisOfFloor(int floor) {
        mPoi.clear();
        if (allPoi.size() == 0) {
            loadPois(floor);
        } else {
            for (PoiData poi : allPoi) {
                if (poi != null && poi.getZ() == floor) {
                    mPoi.add(poi);
                }
            }
            loadPoisKDimensionalTree();
        }
        return mPoi;
    }

    // building = O(n log n)
    private void loadPoisKDimensionalTree() {

        currFloorPoiTree = new KDimensionalTree<PoiData>();

        for (PoiData p : mPoi) {
            try {
                currFloorPoiTree.addElement(p.point, p);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // O(k*n^(1-k/2))
    public ArrayList<PoiData> getInRangePois(PointF myLoc, float pixelToMeter,
                                             float rangeInMeters, int maxReturnedCount) {
        // range in meters = 3
        // PropertyHolder.getInstance().getPixelsToMeter()

        //float rangeRec = pixelToMeter * rangeInMeters;

        //PointF lowk = new PointF(myLoc.x - rangeRec, myLoc.y - rangeRec);
        //PointF uppk = new PointF(myLoc.x + rangeRec, myLoc.y + rangeRec);

        try {
            /**
             ArrayList<PoiData> inRange = (ArrayList<PoiData>)currFloorPoiTree.getObjectsInRange(lowk, uppk);
             KDimensionalTree<PoiData> kd =new KDimensionalTree<PoiData>();
             for(PoiData p:inRange){
             try {
             kd.addElement(p.point, p);
             } catch (Exception e) {
             e.printStackTrace();
             }
             }

             ArrayList<PoiData> nearestList = (ArrayList<PoiData>)kd.nearest(myLoc, maxReturnedCount);//

             return nearestList;
             */
            return (ArrayList<PoiData>) currFloorPoiTree.nearest(myLoc, maxReturnedCount);
        } catch (Exception e) {

            e.printStackTrace();
            return null;
        }

        // //XXX Sort by x coordinate of poi location
        // //Collections.sort(mPoi);
        // tree=new KDTree();
        //
        // for(PoiData p:mPoi){
        // double [] coor=new double[2];
        // coor[0]=p.point.x;
        // coor[1]=p.point.y;
        //
        // try {
        // tree.insert(coor, p);
        // } catch (Exception e) {
        // e.printStackTrace();
        // }
        // }
        //
        // float rangeRec=PropertyHolder.getInstance().getPixelsToMeter()*3;
        // double [] lowk=new double[2];
        // lowk[0]=myLoc.x-rangeRec;
        // lowk[1]=myLoc.y-rangeRec;
        //
        // double [] uppk=new double[2];
        // uppk[0]=myLoc.x+rangeRec;
        // uppk[1]=myLoc.y+rangeRec;
        // Object[] poisArr=null;
        // try {
        // poisArr=tree.range(lowk, uppk);
        // } catch (KeySizeException e) {
        // // TODO Auto-generated catch block
        // e.printStackTrace();
        // }
        // ArrayList<PoiData> inRange=new ArrayList<PoiData>();
        // if(poisArr!=null){
        // for(Object o:poisArr){
        // inRange.add((PoiData)o);
        // }
        // }
        // return inRange;
    }

    public List<PoiData> getInRangeMediaPois(PointF myLoc, float pixelToMeter,
                                             float rangeInMeters) {

        float rangeRec = pixelToMeter * rangeInMeters;

        PointF lowk = new PointF(myLoc.x - rangeRec, myLoc.y - rangeRec);
        PointF uppk = new PointF(myLoc.x + rangeRec, myLoc.y + rangeRec);
        List<PoiData> inRangePois = new ArrayList<PoiData>();
        List<PoiData> inRangeMediaPois = new ArrayList<PoiData>();

        try {

            inRangePois = currFloorPoiTree.getObjectsInRange(lowk, uppk);

            for (PoiData o : inRangePois) {
                if (o.isPoiPlayMultyMedia()
                        && o.getPoiNavigationType().equals("internal")
                        && !o.getMediaurl().isEmpty()
                        && PropertyHolder.getInstance().isPlayingMedia() == false) {
                    inRangeMediaPois.add(o);
                }
            }

            if (inRangeMediaPois.size() > 1) {
                PoiComparator comp = new PoiComparator(myLoc);
                Collections.sort(inRangeMediaPois, comp);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return inRangeMediaPois;

    }

    public List<IPoi> forceLoadAllPois() {

        List<IPoi> iPoisList = new ArrayList<IPoi>();

        try {

            FacilityConf facConf = FacilityContainer.getInstance().getSelected();
            if (facConf != null) {

                String facId = PropertyHolder.getInstance().getFacilityID();
                if (facId != null) {
                    reset();
                    List<PoiData> pois = new ArrayList<PoiData>();

                    getPoiCategories().clear();

                    List<FloorData> floorsList = facConf.getFloorDataList();

                    if (floorsList != null) {
                        int numberoffloors = floorsList.size();
                        for (int i = 0; i < numberoffloors; i++) {
                            if (FacilityConf.isCalibrationState()) {
                                loadPois(i);
                                pois.addAll(mPoi);
                            } else {
                                List<PoiData> floorPois = loadPoisOfFloor(i);
                                pois.addAll(floorPois);
                            }
                        }
                        setAllPoi(pois);
                        parseGalleryJson();
                    }
                }
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }

        iPoisList.addAll(allPoi);

        return iPoisList;
    }

    public List<IPoi> getAllFacilityPois(FacilityConf facConf) {

        List<IPoi> iPoisList = new ArrayList<IPoi>();

        try {

            if (facConf != null) {

                String facId = facConf.getId();
                if (facId != null) {
                    reset();
                    List<PoiData> pois = new ArrayList<PoiData>();

                    getPoiCategories().clear();

                    List<FloorData> floorsList = facConf.getFloorDataList();

                    if (floorsList != null) {

                        for (int i = 0; i < floorsList.size(); i++) {
                            try {
                                mPoi.clear();
                                FloorData f = floorsList.get(i);
                                loadPoisFromServer(i, f);
                                pois.addAll(mPoi);
                            } catch (Throwable t) {
                                t.printStackTrace();
                            }
                        }
                        setAllPoi(pois);
                        parseGalleryJson();
                    }
                }
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }

        iPoisList.addAll(allPoi);

        return iPoisList;
    }

    ;

    private void loadPoisFromServer(int floor) {
        loadPoisFromServer(floor, null);
    }

    private void loadPoisFromServer(int floor, FloorData floorSettings) {

        try {

            FloorData fdata = null;

            if (floorSettings == null) {
                fdata = FacilityContainer.getInstance().getSelected().getFloor(floor);
            } else {
                fdata = floorSettings;
            }

            String poiuri = null;
            if (fdata == null)
                return;
            poiuri = fdata.poi;

            String localeLang = ""; // "en"; //default

            locale = PropertyHolder.getInstance().getAppLanguage();

            if (locale != null) {
                localeLang = locale.toLowerCase();
            }

            if (localeLang.contains("en")) {
                resFile = "-en";
                if (fdata.poien != null && fdata.poien.length() > 0)
                    poiuri = fdata.poien;
            } else if (localeLang.contains("he")) {
                resFile = "-he";
                if (fdata.poihe != null && fdata.poihe.length() > 0)
                    poiuri = fdata.poihe;
            } else if (localeLang.contains("ar")) {
                resFile = "-ar";
                if (fdata.poiar != null && fdata.poiar.length() > 0)
                    poiuri = fdata.poiar;
            } else if (localeLang.contains("ru")) {
                resFile = "-ru";
                if (fdata.poiru != null && fdata.poiru.length() > 0)
                    poiuri = fdata.poiru;
            } else if (localeLang.contains("sp")) {
                resFile = "-es";
                if (fdata.poies != null && fdata.poies.length() > 0)
                    poiuri = fdata.poies;
            } else if (localeLang.contains("ja")) {
                resFile = "-ja";
                if (fdata.poies != null && fdata.poies.length() > 0)
                    poiuri = fdata.poies;
            }
            if (poiuri == null || poiuri.isEmpty()) poiuri = "poi_list-en.txt";

            InputStream ins = null;

            byte[] bytes = ResourceDownloader.getInstance().getUrl(
                    ServerConnection.getInstance().translateUrl(poiuri));
            if (bytes != null && bytes.length > 0) {
                ins = new ByteArrayInputStream(bytes);

            } else {
                poiuri = ServerConnection.getInstance().translateUrl(fdata.poi);
                bytes = ResourceDownloader.getInstance().getUrl(poiuri);
                if (bytes != null && bytes.length > 0) {
                    ins = new ByteArrayInputStream(bytes);

                }
            }
            if (ins == null)
                return;
            BufferedReader in = null;

            try {

                in = new BufferedReader(new InputStreamReader(ins));
                String line = null;
                int rowNum = 0; // for the ID
                while ((line = in.readLine()) != null) {

                    parserFromTXT(line, floor, rowNum);

                    rowNum++;
                }

            } catch (Exception e) {

            } finally {
                if (in != null)
                    try {
                        in.close();
                    } catch (Exception e2) {
                        Log.e("", e2.getMessage());
                        e2.printStackTrace();
                    }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadPoisFromLocal(int floor) {

        // FloorData fdata = FacilityConf.getInstance().getFloor(floor);
        // String poiuri = null;

        locale = PropertyHolder.getInstance().getAppLanguage();
        String localLang = ""; // "en";
        if (locale != null) {
            localLang = locale.toLowerCase();
        }
        try {
            if (localLang.contains("en")) {
                resFile = "-en";
                // poiuri = fdata.poien;
            } else if (localLang.contains("he")) {
                resFile = "-he";
                // poiuri = fdata.poihe;
            } else if (localLang.contains("ar")) {
                resFile = "-ar";
                // poiuri = fdata.poiar;
            } else if (localLang.contains("ru")) {
                resFile = "-ru";
                // poiuri = fdata.poiru;
            } else if (localLang.contains("sp")) {
                resFile = "-es";
            } else if (localLang.contains("ja")) {
                resFile = "-ja";
            } else {
                resFile = "-en";
                // poiuri = fdata.poi;
            }

            // InputStream ins = null;

            // byte[] bytes = ResourceDownloader.getInstance().getUrl(poiuri);
            // if (bytes != null && bytes.length > 0) {
            // ins = new ByteArrayInputStream(bytes);
            //
            // } else {
            // poiuri = ServerConnection.getInstance().translateUrl(fdata.poi);
            // bytes = ResourceDownloader.getInstance().getUrl(poiuri);
            // if (bytes != null && bytes.length > 0) {
            // ins = new ByteArrayInputStream(bytes);
            //
            // }
            // }
            // if (ins == null)
            // return;
            // BufferedReader in = null;

            BufferedReader in = null;
            String floordir = PropertyHolder.getInstance().getFacilityDir()
                    + "/" + floor;

            dir = new File(floordir, "poi");
            fileFullName = fileName + resFile + fileExt;
            mFile = new File(dir, fileFullName);

            /**
             * fileName = "poi_list_test"; fileFullName = fileName + fileExt;
             * mFile = new File(floordir+"/"+fileFullName);
             **/

            if (!mFile.exists())
                return;
            try {
                in = new BufferedReader(new FileReader(mFile));
                String line = "";
                int rowNum = 0; // for the ID
                while ((line = in.readLine()) != null) {

                    {

                        parserFromTXT(line, floor, rowNum);

                        /**
                         * try {
                         *
                         * String keywords = ""; // val0 place1 x String val =
                         * vals[0]; val = removeutfctrlChars(vals[0]); float x =
                         * Float.parseFloat(val.trim()); // val1 place2 y val =
                         * removeutfctrlChars(vals[1]); float y =
                         * Float.parseFloat(vals[1]); // val2 place3 types
                         * String typelist = vals[2]; List<String> types = new
                         * ArrayList<String>(); String[] tmp =
                         * typelist.split(","); if (tmp.length > 0) { tmp[0] =
                         * tmp[0].replaceAll("\\[", ""); tmp[tmp.length - 1] =
                         * tmp[tmp.length - 1] .replaceAll("\\]", ""); for (int
                         * i = 0; i < tmp.length; i++) { String type =
                         * tmp[i].trim(); types.add(type); }
                         *
                         * } // val3 place4 uri(icon) String uri = vals[3]; if
                         * (!iconslist.contains(uri)) iconslist.add(uri); //
                         * val4 place5 z(floornumber) val =
                         * removeutfctrlChars(vals[4]); double z =
                         * Double.parseDouble(val); // val5 place6 description
                         * (place name) if (vals.length >= 6 && vals[5] != null
                         * && !vals[5].equals("")) { description = vals[5]; } //
                         * val7 place8 keywords List<String> keywordslist = new
                         * ArrayList<String>(); if (vals.length >= 8 && vals[7]
                         * != null && (vals[7].length() > 0)) { keywords =
                         * vals[7]; } String[] tmp1 = keywords.split(",");
                         *
                         * for (int i = 0; i < tmp1.length; i++) { String
                         * keyword = tmp1[i].trim(); keywordslist.add(keyword);
                         * // }
                         *
                         * } // val6 place7 webURL PoiData data = new
                         * PoiData(uri, types, description, keywordslist);
                         * data.setPoint(new PointF(x, y)); data.setZ(z); if
                         * (vals.length >= 7 && vals[6] != null &&
                         * !vals[6].equals("")) { data.setUrl(vals[6]); } if
                         * (vals.length >= 9 && vals[8] != null &&
                         * !vals[8].equals("")) { String details = vals[8];
                         * data.setDetails(details); }
                         *
                         *
                         * // val9 place10 showPoiOnMap (boolean b) if
                         * (vals.length >= 10 && vals[9] != null &&
                         * !vals[9].equals("")) { boolean showOnMap =
                         * Boolean.parseBoolean(vals[9]);
                         * data.setShowPoiOnMap(showOnMap); }
                         *
                         * // val10 place11 showPoiOnMap (boolean b) if
                         * (vals.length >= 11 && vals[10] != null &&
                         * !vals[10].equals("")) { boolean showOnSearches =
                         * Boolean.parseBoolean(vals[10]);
                         * data.setShowPoiOnSearches(showOnSearches); }
                         *
                         * // val11 place 12 showPoiBubble (boolean b) if
                         * (vals.length >= 12 && vals[11] != null &&
                         * !vals[11].equals("")) { boolean showbubble =
                         * Boolean.parseBoolean(vals[11]);
                         * data.setShowPoiBubble(showbubble); }
                         *
                         * // val12 place 13 playMultyMediaToPoi (boolean b) if
                         * (vals.length >= 13 && vals[12] != null &&
                         * !vals[12].equals("")) { boolean playMultyMediaToPoi =
                         * Boolean.parseBoolean(vals[12]);
                         * data.setPoiPlayMultyMedia(playMultyMediaToPoi); }
                         *
                         * // val13 place 14 navigationType (String nt) if
                         * (vals.length >= 14 && vals[13] != null &&
                         * !vals[13].equals("")) { String navigationType =
                         * vals[13]; data.setPoiNavigationType(navigationType);
                         * }
                         *
                         * // val14 place 15 lat (double lat) if (vals.length >=
                         * 15 && vals[14] != null && !vals[14].equals("")) {
                         * double lat = Double.parseDouble(vals[14]);
                         * data.setPoiLatitude(lat); }
                         *
                         * // val15 place 16 lon (double lon) if (vals.length >=
                         * 16 && vals[15] != null && !vals[15].equals("")) {
                         * double lon = Double.parseDouble(vals[15]);
                         * data.setPoiLongitude(lon); }
                         *
                         * // val16 place 17 future1 (String nt) if (vals.length
                         * >= 17 && vals[16] != null && !vals[16].equals("")) {
                         * String future1 = vals[16];
                         * data.setFutureOptionONE(future1); } // val17 place 18
                         * future2 (String nt) if (vals.length >= 18 && vals[17]
                         * != null && !vals[17].equals("")) { String future2 =
                         * vals[17]; data.setFutureOptionTWO(future2); } //
                         * val18 place 19 future3 (String nt) if (vals.length >=
                         * 19 && vals[18] != null && !vals[18].equals("")) {
                         * String future3 = vals[18];
                         * data.setFutureOptionTHREE(future3); }
                         *
                         *
                         * // val19 place 20 poiID (String nt) if (vals.length
                         * >= 20 && vals[19] != null && !vals[19].equals("")) {
                         * String poiid = vals[19]; data.setPoiID(poiid); }else{
                         * //in case of noValue provided poiID will be the
                         * cailityname+floor+rowNumber in file //for a
                         * single-non repetitive value String poiid =
                         * "fac"+PropertyHolder.getInstance().getFacilityID()
                         * +"fl"+ floor +"rw"+ rowNum ; data.setPoiID(poiid); }
                         *
                         * mPoi.add(data); } catch (NumberFormatException e) {
                         * e.printStackTrace(); continue; }
                         **/
                        rowNum++;
                    }
                }

            } catch (Exception e) {
                Log.e("", e.getMessage());
                e.printStackTrace();
            } finally {

                if (in != null)
                    try {
                        in.close();
                    } catch (Exception e2) {
                        Log.e("", e2.getMessage());
                        e2.printStackTrace();
                    }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void Parse(String poiData) {
        types.clear();

        try {
            JSONTokener tokener = new JSONTokener(poiData);
            JSONObject json;

            json = (JSONObject) tokener.nextValue();
            // String facid = json.getString("facid");
            JSONArray poilist = json.getJSONArray("poilist");
            for (int i = 0; i < poilist.length(); i++) {
                JSONObject poi = poilist.getJSONObject(i);
                String icon = poi.getString("poiicon");
                String type = poi.getString("poitype");
                String description = poi.getString("poidescription");
                PoiType data = new PoiType(icon, type, description,true,true);
                types.add(data);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public String removeutfctrlChars(String in) {
        String result = "";
        for (int i = 0; i < in.length(); i++) {
            short v = (short) in.charAt(i);
            if (v >= 0) {
                result += in.charAt(i);
            }
        }

        return result;
    }

    public boolean removePoi(PoiData object) {
        PoisChanged = true;
        return mPoi.remove(object);

    }

    public void setClosePoisForInstructions(List<Instruction> instructions) {
        for (Instruction o : instructions) {
            ClosePois(o);
        }
    }

    private void ClosePois(Instruction instruction) {
        if (instruction != null
                && !(instruction.getType() == Instruction.TYPE_SWITCH_FLOOR)
                && !(instruction.getType() == Instruction.TYPE_EXIT)) {
            double z = instruction.getLocation().getZ();
            float x = (float) instruction.getLocation().getX();
            float y = (float) instruction.getLocation().getY();
            PointF instructionpoint = new PointF(x, y);
            int poiforinstructionradius = PropertyHolder.getInstance()
                    .getPoiForInstructionRadius();
            double mindistance = Double.MAX_VALUE;
            String poiname = "";
            for (PoiData poi : allPoi) {
                if (poi.isInstructionsParticipate()) {
                    if (poi.getZ() == z) {
                        double d = MathUtils.distance(instructionpoint,
                                poi.getPoint());
                        d = d / FacilityContainer.getInstance().getSelected().getPixelsToMeter();
                        if (d < poiforinstructionradius && d < mindistance) {
                            mindistance = d;
                            poiname = poi.getpoiDescription();
                        }
                    }
                }
            }
            if (!poiname.equals("")) {
                instruction.setPoiName(poiname);
            }
        }
    }

    public PoiData getLastPoiForIntentFlashLight() {
        return lastPoiForIntentFlashLight;
    }

    public void setLastPoiForIntentFlashLight(PoiData lastPoiForIntentFlashLight) {
        this.lastPoiForIntentFlashLight = lastPoiForIntentFlashLight;
    }

    public PoiData getLastPoiForIntentNavigationDialog() {
        return lastPoiForIntentNavigationDialog;
    }

    public void setLastPoiForIntentNavigationDialog(
            PoiData lastPoiForIntentNavigationDialog) {
        this.lastPoiForIntentNavigationDialog = lastPoiForIntentNavigationDialog;
    }

    public Location getLastPoiLocationForIntents() {
        return lastPoiLocationForIntents;
    }

    public void setLastPoiLocationForIntents(Location lastPoiLocationForIntents) {
        this.lastPoiLocationForIntents = lastPoiLocationForIntents;
    }

    private void parserFromTXT(String line, int floorNum, int index) {
        String[] vals = line.split("\t");


        String campusID = PropertyHolder.getInstance().getCampusId();
        String facilityID = PropertyHolder.getInstance().getFacilityID();
        try {

            for (int i = 0; i < vals.length; i++) {
                vals[i] = escapeTab(vals[i]);

            }
            String description = "";
            String keywords = "";
            // val0 place1 x
            String val = vals[0];
            val = removeutfctrlChars(vals[0]);
            float x = Float.parseFloat(val.trim());
            // val1 place2 y
            val = removeutfctrlChars(vals[1]);
            float y = Float.parseFloat(vals[1]);
            // val2 place3 types
            String typelist = vals[2];
            String showInCatgories = "$$";
            String showInMapFilter = "##";
            Boolean isInCatgories = typelist.contains(showInCatgories) ? false : true;
            if(!isInCatgories){
                typelist = typelist.replace(showInCatgories,"");
            }
            Boolean isInMapFilter = typelist.contains(showInMapFilter) ? false : true;
            if(!isInMapFilter){
                typelist = typelist.replace(showInMapFilter,"");
            }
            String uri = vals[3];
            List<String> types = new ArrayList<String>();
            String[] tmp = typelist.split(",");
            if (tmp.length > 0) {
                tmp[0] = tmp[0].replaceAll("\\[", "");
                tmp[tmp.length - 1] = tmp[tmp.length - 1].replaceAll("\\]", "");
                for (int i = 0; i < tmp.length; i++) {
                    String type = tmp[i].trim();
                    types.add(type);
                    if (!categoriesContains(type)) {
                        PoiType tmptype = new PoiType(uri, type, type, isInCatgories, isInMapFilter);
                        getPoiCategories().add(tmptype);
                    }
                }

            }
            // val3 place4 uri(icon)

            if (!iconslist.contains(uri))
                iconslist.add(uri);
            // val4 place5 z(floornumber)
            val = removeutfctrlChars(vals[4]);
            double z = Double.parseDouble(val);
            // val5 place6 description (place name)
            if (vals.length >= 6 && vals[5] != null && !vals[5].equals("")) {
                description = vals[5];
            }
            // val7 place8 keywords
            List<String> keywordslist = new ArrayList<String>();
            if (vals.length >= 8 && vals[7] != null && (vals[7].length() > 0)) {
                keywords = vals[7];
            }
            String[] tmp1 = keywords.split(",");

            for (int i = 0; i < tmp1.length; i++) {
                String keyword = tmp1[i].trim();
                keywordslist.add(keyword);
                // }

            }
            // val6 place7 webURL
            PoiData data = new PoiData(uri, types, description, keywordslist);
            data.setFacilityID(facilityID);
            data.setCampusID(campusID);
            data.setPoint(new PointF(x, y));
            data.setZ(z);
            if (vals.length >= 7 && vals[6] != null && !vals[6].equals("")) {
                data.setUrl(vals[6]);
            }
            if (vals.length >= 9 && vals[8] != null && !vals[8].equals("")) {
                String details = vals[8];
                data.setDetails(details);
            }

            // val9 place10 showPoiOnMap (boolean b)
            if (vals.length >= 10 && vals[9] != null && !vals[9].equals("")) {
                boolean showOnMap = Boolean.parseBoolean(vals[9]);
                data.setShowPoiOnMap(showOnMap);
            }

            setpoiVisibility(data);

            // val10 place11 showPoiOnMap (boolean b)
            if (vals.length >= 11 && vals[10] != null && !vals[10].equals("")) {
                boolean showOnSearches = Boolean.parseBoolean(vals[10]);
                data.setShowPoiOnSearches(showOnSearches);
            }

            // val11 place 12 showPoiBubble (boolean b)
            if (vals.length >= 12 && vals[11] != null && !vals[11].equals("")) {
                boolean showbubble = Boolean.parseBoolean(vals[11]);
                data.setShowPoiBubble(showbubble);
            }

            // val12 place 13 playMultyMediaToPoi (boolean b)
            if (vals.length >= 13 && vals[12] != null && !vals[12].equals("")) {
                boolean playMultyMediaToPoi = Boolean.parseBoolean(vals[12]);
                data.setPoiPlayMultyMedia(playMultyMediaToPoi);
            }

            // val13 place 14 navigationType (String nt)
            if (vals.length >= 14 && vals[13] != null && !vals[13].equals("")) {
                String navigationType = vals[13];
                data.setPoiNavigationType(navigationType);
            }

            // val14 place 15 lat (double lat)
            if (vals.length >= 15 && vals[14] != null && !vals[14].equals("")) {
                double lat = Double.parseDouble(vals[14]);
                data.setPoiLatitude(lat);
            }

            // val15 place 16 lon (double lon)
            if (vals.length >= 16 && vals[15] != null && !vals[15].equals("")) {
                double lon = Double.parseDouble(vals[15]);
                data.setPoiLongitude(lon);
            }

            // val16 place 17 future1 (String nt)
            if (vals.length >= 17 && vals[16] != null && !vals[16].equals("")) {
                String future1 = vals[16];
                data.setFutureOptionONE(future1);
                data.setDetails(future1);
            }
            // val17 place 18 future2 (String nt)
            if (vals.length >= 18 && vals[17] != null && !vals[17].equals("")) {
                String future2 = vals[17];
                data.setFutureOptionTWO(future2);
            }
            // val18 place 19 future3 (String nt)
            if (vals.length >= 19 && vals[18] != null && !vals[18].equals("")) {
                String future3 = vals[18];
                data.setFutureOptionTHREE(future3);
            }

            // val19 place 20 poiID (String nt)
            if (vals.length >= 20 && vals[19] != null && !vals[19].equals("")) {
                String poiid = vals[19];
                data.setPoiID(poiid);
            } else {
                // in case of noValue provided poiID will be the
                // cailityname+floor+rowNumber in file
                // for a single-non repetitive value
                String poiid = "fac"
                        + PropertyHolder.getInstance().getFacilityID() + "fl"
                        + floorNum + "rw" + index;
                data.setPoiID(poiid);
            }

            // va20 place21 showOnZoomLevel (boolean b)
            if (vals.length >= 21 && vals[20] != null && !vals[20].equals("")) {
                boolean showOnZoomLevel = Boolean.parseBoolean(vals[20]);
                data.setShowOnZoomLevel(showOnZoomLevel);
            }

            // va21 place22 instructionsParticipate (boolean b)
            if (vals.length >= 22 && vals[21] != null && !vals[21].equals("")) {
                boolean instructionsParticipate = Boolean
                        .parseBoolean(vals[21]);
                data.setInstructionsParticipate(instructionsParticipate);
            }

            // va22 place23 phone1 (string s)
            List<String> phone1list = new ArrayList<String>();
            if (vals.length >= 23 && vals[22] != null && vals[22].length() > 0) {
                String phone1 = vals[22];
                String[] tmp2 = phone1.split(",");
                for (int i = 0; i < tmp2.length; i++) {
                    String phone1temp = tmp2[i].trim();
                    phone1list.add(phone1temp);
                }
                data.setPhone1(phone1list);

            }

            // va23 place24 phone2 (string s)
            List<String> phone2list = new ArrayList<String>();
            if (vals.length >= 24 && vals[23] != null && vals[23].length() > 0) {
                String phone2 = vals[23];
                String[] tmp2 = phone2.split(",");
                for (int i = 0; i < tmp2.length; i++) {
                    String phone2temp = tmp2[i].trim();
                    phone2list.add(phone2temp);
                }
                data.setPhone2(phone2list);

            }

            // va24 place25 email (string s)
            if (vals.length >= 25 && vals[24] != null && !vals[24].equals("")) {
                String emailaddress = vals[24];
                data.setEmailaddress(emailaddress);
            }

            // val25 phone hours
            List<String> phone2hourslist = new ArrayList<String>();
            if (vals.length >= 26 && vals[25] != null && vals[25].length() > 0) {
                String phone2hours = vals[25];
                String[] tmp2 = phone2hours.split(",");
                for (int i = 0; i < tmp2.length; i++) {
                    String phone2hourstemp = tmp2[i].trim();
                    phone2hourslist.add(phone2hourstemp);
                }
                data.setPhone2hours(phone2hourslist);
            }

            // active hours

            List<String> activehourslist = new ArrayList<String>();
            if (vals.length >= 27 && vals[26] != null && vals[26].length() > 0) {
                String activehours = vals[26];
                String[] tmp2 = activehours.split(",");
                for (int i = 0; i < tmp2.length; i++) {
                    String activehourstemp = tmp2[i].trim();
                    activehourslist.add(activehourstemp);
                }
                data.setActivehours(activehourslist);
            }

            String officeinstuctions = new String();
            if (vals.length >= 28 && vals[27] != null && !vals[27].equals("")) {
                officeinstuctions = vals[27];
                data.setPoiofficeinstuctions(officeinstuctions);
            }

            if (vals.length >= 29 && vals[28] != null && !vals[28].equals("")) {
                boolean showOncategorysearch = Boolean.parseBoolean(vals[28]);
                data.setPoishowincategory(showOncategorysearch);
            }

            String mediapath = new String();
            if (vals.length >= 30 && vals[29] != null && !vals[29].equals("")) {
                mediapath = vals[29];
                data.setMediaurl(mediapath);
            }
            /**
             * //check if it is working
             * if(data.getpoiDescription().equals("יציאה לפינת עישון"
             * )||data.getpoiDescription().equals("חדר מיון")){ //
             * data.setShowPoiOnMap(false); // data.setShowPoiOnSearches(false);
             * // data.setShowPoiBubble(false); data.setShowOnZoomLevel(false);
             * }
             **/
            mPoi.add(data);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

    }

    private void setpoiVisibility(PoiData data) {
        boolean showonmap = false;
        if (visbleCategories != null) {
            List<String> categories = data.getPoitype();
            for (String o : categories) {
                if (visbleCategories.toString().contains(o)) {
                    showonmap = true;
                    break;
                }
            }
            data.setShowPoiOnMap(showonmap);
            data.setVisible(showonmap);
        }

    }

    private boolean categoriesContains(String type) {
        boolean result = false;
        for (PoiType o : getPoiCategories()) {
            if (o.equals(type)) {
                result = true;
                break;
            }
        }
        return result;
    }

    public List<PoiType> getPoiCategories() {
        return poiCategories;
    }

    public void setPoiCategories(List<PoiType> poiCategories) {
        this.poiCategories = poiCategories;
    }

    public PoiData getMovinPoi() {
        return movinPoi;
    }

    public void setMovinPoi(PoiData movinPoi) {
        this.movinPoi = movinPoi;
    }

    public PoiData findPoiFromLoc(Location loc) {
        if (loc == null)
            return null;

        for (PoiData element : allPoi) {
            float x = element.getPoint().x;
            float y = element.getPoint().y;
            double z = element.getZ();
            if ((float) loc.getX() == x && (float) loc.getY() == y
                    && loc.getZ() == z) {
                return element;
            }

        }
        PointF pt = new PointF((float) loc.getX(), (float) loc.getY());
        PoiData result = new PoiData(pt);
        result.setZ(loc.getZ());
        result.setpoiDescription("");
        return result;
    }

    public PoiData findPoiById(String id) {
        if (id == null || id.equals("")) {
            return null;
        }

        PoiData result = null;
        for (PoiData o : allPoi) {
            if (id.equals(o.getPoiID())) {
                result = o;
                break;
            }
        }
        return result;
    }

    public PoiData getExitPoi(LatLng parking) {
        PoiData result = null;
        List<PoiData> exitpois = new ArrayList<PoiData>();
        for (PoiData o : allPoi) {
            if (o.getPoiID().contains("idr") && o.getPoiNavigationType().equals("internal")) {
                exitpois.add(o);
            }
        }
        if (exitpois.size() > 0) {
            result = findCloseToParkingExit(parking, exitpois);
        }
        return result;
    }

    private PoiData findCloseToParkingExit(LatLng parking, List<PoiData> exitpois) {
        return (PoiData) DualMapNavUtil.findCloseExit(parking, exitpois);
    }

    public PoiData getExitPoi() {
        PoiData result = null;
        List<PoiData> exitpois = new ArrayList<PoiData>();
        for (PoiData o : allPoi) {
            if (o.getPoiID().contains("idr") && o.getPoiNavigationType().equals("internal")) {
                exitpois.add(o);
            }
        }
        if (exitpois.size() > 0) {
            result = exitpois.get(0);
        }
        return result;
    }

    public List<String> getVisbleCategories() {
        return visbleCategories;
    }

    public void setVisbleCategories(List<String> visbleCategories) {
        this.visbleCategories = visbleCategories;
        for (PoiData o : allPoi) {
            setpoiVisibility(o);
        }
    }

    public void addUserPois(List<IPoi> pois) {
        if (pois != null && pois.size() > 0) {
            for (IPoi poi : pois) {
                try {
                    if (poi != null) {
                        PoiData p = (PoiData) poi;
                        userPois.add(p);
                        allPoi.add(p);
                        if (p.getZ() == currloadedFloor) {
                            mUserPois.add(p);
                            currFloorPoiTree.addElement(p.point, p);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void removeUserPois(List<IPoi> pois) {
        if (pois != null && pois.size() > 0) {
            for (IPoi poi : pois) {
                try {
                    if (poi != null) {
                        PoiData p = (PoiData) poi;
                        userPois.remove(p);
                        allPoi.remove(p);
                        if (p.getZ() == currloadedFloor) {
                            mUserPois.remove(p);
                            currFloorPoiTree.delete(p.point);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void parseGalleryJson() {
        try {
            String url = PropertyHolder.getInstance().getServerName() + "res/" + PropertyHolder.getInstance().getProjectId() +
                    "/" + PropertyHolder.getInstance().getCampusId() + "/" +
                    PropertyHolder.getInstance().getFacilityID() + "/"
                    + FacilityContainer.getInstance().getSelected().getPoisGalleryconfFileName();
            byte[] res = ResourceDownloader.getInstance().getLocalCopy(url);

            if (res == null || res.length == 0) {
                return;
            }

            String galleryjson = new String(res, "utf-8");
//			if (galleryjson.length() == 0)
//				return;
            JSONTokener tokener = new JSONTokener(galleryjson);
            JSONObject json;

            json = (JSONObject) tokener.nextValue();
            JSONArray galleryiamges = json.getJSONArray("gallery");
            for (int i = 0; i < galleryiamges.length(); i++) {
                JSONObject goimage = galleryiamges.getJSONObject(i);
                String gname = goimage.getString("name");
                String gpoi_id = goimage.getString("poi_id");
                String guri = goimage.getString("uri");
                String gmd5 = goimage.getString("md5");
                String gtype = goimage.getString("type");
                GalleryObject go = new GalleryObject(gname, gpoi_id, guri, gmd5, gtype);
                PoiData poi = findPoiById(gpoi_id);
                if (poi != null) {
                    if (gtype.equals(GalleryObject.HEAD_TYPE)) {
                        poi.setHeadImage(go);
                    } else {
                        poi.addGalleryImage(go);
                    }
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }

    }

    public List<IPoi> getExternalPois() {
        List<IPoi> result = new ArrayList<IPoi>();
        for (IPoi o : allPoi) {
            String poinavigationtype = o.getPoiNavigationType();
            if (poinavigationtype != null && poinavigationtype.equals("external")) {
                result.add(o);
            }
        }
        return result;
    }

    private class PoiComparator implements Comparator<PoiData> {

        private PointF myLoc = null;

        public PoiComparator(PointF myLoc) {
            this.myLoc = myLoc;
        }

        @Override
        public int compare(PoiData poi1, PoiData poi2) {
            Double d1 = MathUtils.distance(poi1.getPoint(), myLoc);
            Double d2 = MathUtils.distance(poi2.getPoint(), myLoc);
            return d1.compareTo(d2);
        }

    }


//	public void loadPoiGallery(IPoi gpoi){
//		List<String> gallUrls = new ArrayList<String>();
//		for (GalleryObject temppoigallertimages : allGalleryImages){
//			if (temppoigallertimages.gopoi_id.equals(gpoi.getPoiID())){
//				poiGalleryImages.add(temppoigallertimages);
//			}
//		}
//		if (!poiGalleryImages.isEmpty()) {
//			for (GalleryObject tempuri :poiGalleryImages ){
//				gallUrls.add(tempuri.gouri);
//			}
//			int numberOfUrls = gallUrls.size();
//			String[] murls = new String[numberOfUrls];
//			for (int i = 0; i < numberOfUrls; i++) {
//				murls[i] = gallUrls.get(i);
//			}
//			donloadgllaeryimages(murls);
//		}
//	}
//
//	private void donloadgllaeryimages(String[] gurls) {
//		ResourceDownloader.getInstance().onDemandDownload(gurls);
//		
//	}

}