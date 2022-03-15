package com.mlins.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PointF;

import com.google.android.gms.maps.model.LatLng;
import com.mlins.utils.gis.Location;
import com.spreo.nav.enums.LocationMode;
import com.spreo.nav.interfaces.ILocation;
import com.spreo.nav.interfaces.IPoi;
import com.spreo.spreosdk.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class PoiData implements Comparable<PoiData>, IPoi {

    public String poiuri = "";
    public List<String> poitype = new ArrayList<String>();
    public String poidescription = "";
    public List<String> poiKeywords = new ArrayList<String>();
    public PointF point;
    String facilityID;
    String campusID;
    List<GalleryObject> gallery = new ArrayList<GalleryObject>();
    private double Z;
    private String url = "";
    private double dFromMyLocation;
    private String Details = "";
    private boolean showPoiOnMap = true;
    private boolean showPoiOnSearches = true;
    private boolean showPoiBubble = true;
    private boolean poiPlayMultyMedia = false;
    private boolean showOnZoomLevel = true;
    private String poiID = "";
    private String poiNavigationType = "internal";// "internal" or "external"
    private double poiLatitude = -1;// set -1 to location in case of noLocation providing
    private double poiLongitude = -1;// set -1 to location in case of noLocation providing
    private String futureOptionONE = "";
    private String futureOptionTWO = "";
    private String futureOptionTHREE = "";
    private boolean PoiClickAble = true;
    private boolean instructionsParticipate = true;
    private List<String> phone1 = new ArrayList<String>();
    private List<String> phone2 = new ArrayList<String>();
    private String emailaddress = "";
    private List<String> phone2hours = new ArrayList<String>();
    private List<String> activehours = new ArrayList<String>();
    private String poiofficeinstuctions = "";
    private boolean poishowincategory = true;
    private String mediaurl = "";
    private boolean visible = true;
    private GalleryObject headImage = null;

    private Bitmap icon = null;  // icon by user

    private boolean displayLabel = true;

    public PoiData() {
        super();
    }

    public PoiData(PointF point) {
        this.point = point;
    }

    public PoiData(String npoiuri, List<String> npoitype, String desc) {
        init(npoiuri, npoitype, desc);
    }

    public PoiData(String npoiuri, List<String> npoitype, String desc,
                   List<String> keywords) {
        init(npoiuri, npoitype, desc);
        poiKeywords = keywords;
    }

    public List<String> getPhone1() {
        return phone1;
    }

    public void setPhone1(List<String> phone1) {
        this.phone1 = phone1;
    }

    public List<String> getPhone2() {
        return phone2;
    }

    public void setPhone2(List<String> phone2) {
        this.phone2 = phone2;
    }

    public String getEmailaddress() {
        return emailaddress;
    }

    public void setEmailaddress(String emailaddress) {
        this.emailaddress = emailaddress;
    }

    public List<String> getPhone2hours() {
        return phone2hours;
    }

    public void setPhone2hours(List<String> phone2hours) {
        this.phone2hours = phone2hours;
    }

    public List<String> getActivehours() {
        return activehours;
    }

    public void setActivehours(List<String> activehours) {
        this.activehours = activehours;
    }

    public List<String> getPoiKeywords() {
        return poiKeywords;
    }

    public void setPoiKeywords(List<String> poiKeywords) {
        this.poiKeywords = poiKeywords;
    }

    public String getPoiuri() {
        return poiuri;
    }

    public void setPoiuri(String poiuri) {
        this.poiuri = poiuri;
    }

    @Override
    public Bitmap getIcon() {
        if (icon != null) {
            return icon.copy(icon.getConfig(), true);
        } else {
            //return ResourceDownloader.getInstance().getLocalBitmap(poiuri);
            String uri = poiuri;
            if (PropertyHolder.useZip) {
                uri = "spreo_poi_" + poiuri;
            }
            return ResourceDownloader.getInstance().getLocalBitmap(uri, campusID, facilityID, PropertyHolder.getInstance().getScreenDensity());
        }
    }

    @Override
    public void setIcon(Bitmap icon) {
        this.icon = icon;
    }

    public List<String> getPoitype() {
        return poitype;
    }

    public void setPoitype(String poitype) {
        this.poitype.add(poitype);
    }

    public String getpoiDescription() {
        return poidescription;
    }

    public void setpoiDescription(String description) {
        this.poidescription = description;
    }

    public PointF getPoint() {
        return point;
    }

    public void setPoint(PointF point) {
        this.point = point;
    }

    public void init(String npoiuri, List<String> npoitype, String desc) {
        poiuri = npoiuri;
        poitype = npoitype;
        poidescription = desc;
    }

    public double getZ() {
        return Z;
    }

    public void setZ(double z) {
        Z = z;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public double getdFromMyLocation() {
        return dFromMyLocation;
    }

    public void setdFromMyLocation(double dFromMyLocation) {
        this.dFromMyLocation = dFromMyLocation;
    }

    public String getDetails() {
        return Details;
    }

    public void setDetails(String details) {
        Details = details;
    }

    public boolean isShowPoiOnMap() {
        return showPoiOnMap;
    }

    public void setShowPoiOnMap(boolean showPoiOnMap) {
        this.showPoiOnMap = showPoiOnMap;
    }

    public boolean isShowPoiOnSearches() {
        return showPoiOnSearches;
    }

    public void setShowPoiOnSearches(boolean showPoiOnSearches) {
        this.showPoiOnSearches = showPoiOnSearches;
    }

    public boolean isShowPoiBubble() {
        return showPoiBubble;
    }

    public void setShowPoiBubble(boolean showPoiBubble) {
        this.showPoiBubble = showPoiBubble;
    }

    public boolean isPoiPlayMultyMedia() {
        return poiPlayMultyMedia;
    }

    public void setPoiPlayMultyMedia(boolean poiPlayMultyMedia) {
        this.poiPlayMultyMedia = poiPlayMultyMedia;
    }

    public String getPoiID() {
        return poiID;
    }

    public void setPoiID(String poiID) {
        this.poiID = poiID;
    }

    public String getPoiNavigationType() {
        return poiNavigationType;
    }

    public void setPoiNavigationType(String poiNavigationType) {
        this.poiNavigationType = poiNavigationType;
    }

    public double getPoiLatitude() {
        return poiLatitude;
    }

    public void setPoiLatitude(double poiLatitude) {
        this.poiLatitude = poiLatitude;
    }

    public double getPoiLongitude() {
        return poiLongitude;
    }

    public void setPoiLongitude(double poiLongitude) {
        this.poiLongitude = poiLongitude;
    }

    public String getFutureOptionONE() {
        return futureOptionONE;
    }

    public void setFutureOptionONE(String futureOptionONE) {
        this.futureOptionONE = futureOptionONE;
    }

    public String getFutureOptionTWO() {
        return futureOptionTWO;
    }

    public void setFutureOptionTWO(String futureOptionTWO) {
        this.futureOptionTWO = futureOptionTWO;
    }

    public String getFutureOptionTHREE() {
        return futureOptionTHREE;
    }

    public void setFutureOptionTHREE(String futureOptionTHREE) {
        this.futureOptionTHREE = futureOptionTHREE;
    }

    public boolean isShowOnZoomLevel() {
        return showOnZoomLevel;
    }

    public void setShowOnZoomLevel(boolean showOnZoomLevel) {
        this.showOnZoomLevel = showOnZoomLevel;
    }

    public float getX() {
        return (point == null) ? 0 : point.x;
    }

    public float getY() {
        return (point == null) ? 0 : point.y;
    }

    @Override
    public int compareTo(PoiData another) {
        if (another == null)
            return 0;
        Float fstX = Float.valueOf(this.point.x);
        Float secX = Float.valueOf(another.point.x);
        return fstX.compareTo(secX);
    }

    @Override
    public String toString() {
        String result = "";
        if (poiNavigationType.equals("internal")) {
            result = poidescription + "(" + point.x + "," + point.y + "," + Z + ")";
        } else {
            result = poidescription + "(" + poiLatitude + "," + poiLongitude + ")";
        }
        return result;
    }

    public String convertToJson() {

        String jsonToServer = "";
        try {

            JSONObject poiJson = new JSONObject();
            poiJson.put("id", poiID);
            poiJson.put("desc", poidescription);
            poiJson.put("fac", PropertyHolder.getInstance().getFacilityID());
            float x = -1;
            float y = -1;

            try {
                x = point.x;
                y = point.y;
            } catch (Exception e) {
                x = -1;
                y = -1;
            }

            poiJson.put("loc", x + ":" + y + ":" + Z);
            poiJson.put("latlon", poiLatitude + ":" + poiLatitude);

            jsonToServer = poiJson.toString();

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonToServer;
    }

    public boolean isPoiClickAble() {
        return PoiClickAble;
    }

    public void setPoiClickAble(boolean poiClickAble) {
        PoiClickAble = poiClickAble;
    }

    public boolean isInstructionsParticipate() {
        return instructionsParticipate;
    }

    public void setInstructionsParticipate(boolean instructionsParticipate) {
        this.instructionsParticipate = instructionsParticipate;
    }

    public String getPoiofficeinstuctions() {
        return poiofficeinstuctions;
    }

    public void setPoiofficeinstuctions(String poiofficeinstuctions) {
        this.poiofficeinstuctions = poiofficeinstuctions;
    }

    public boolean isPoishowincategory() {
        return poishowincategory;
    }

    public void setPoishowincategory(boolean poishowincategory) {
        this.poishowincategory = poishowincategory;
    }

    public String getMediaurl() {
        return mediaurl;
    }

    public void setMediaurl(String mediaurl) {
        this.mediaurl = mediaurl;
    }

    @Override
    public String getKeyWordsAsString() {
        String result = "";
        if (poiKeywords != null && !poiKeywords.isEmpty()) {
            result = poiKeywords.toString();
        }
        return result;
    }

    @Override
    public ILocation getLocation() {
        Location result = new Location(this);
        return result;
    }

    @Override
    public void setLocation(ILocation location) {
        if (location.getLocationType() != null && location.getLocationType() == LocationMode.OUTDOOR_MODE) {
            this.poiLatitude = location.getLat();
            this.poiLongitude = location.getLon();
            this.campusID = location.getCampusId();
            this.poiNavigationType = "external";
        } else if (location.getLocationType() != null) {
            float px = (float) location.getX();
            float py = (float) location.getY();
            setPoint(new PointF(px, py));
            this.Z = location.getZ();
            this.facilityID = location.getFacilityId();
            this.campusID = location.getCampusId();
            poiNavigationType = "internal";// "internal" or "external"
        }


    }

    public String getFacilityID() {
        // TODO Auto-generated method stub
        return facilityID;
    }

    public void setFacilityID(String facilityID) {
        this.facilityID = facilityID;
    }

    public String getCampusID() {
        // TODO Auto-generated method stub
        return campusID;
    }

    public void setCampusID(String campusID) {
        this.campusID = campusID;
    }

    public boolean isUserIconExists() {
        if (icon == null) return false;
        return true;
    }

    public List<GalleryObject> getGallery() {
        return gallery;
    }

    public void addGalleryImages(List<GalleryObject> imgs) {
        gallery.addAll(imgs);
    }

    @Override
    public void addGalleryImage(GalleryObject g) {
        gallery.add(g);

    }

    @Override
    public GalleryObject getHeadImage() {
        return headImage;
    }

    @Override
    public void setHeadImage(GalleryObject g) {
        this.headImage = g;

    }

    @Override
    public void recycleGallery() {

        if (getGallery() != null && getGallery().size() > 0) {

            for (GalleryObject g : getGallery()) {
                if (g != null) {
                    g.recycleBitmap();
                }
            }

        }

        if (getHeadImage() != null) {

            GalleryObject g = getHeadImage();
            if (g != null) {
                g.recycleBitmap();
            }

        }

    }

    @Override
    public boolean isVisible() {
        return visible;
    }

    @Override
    public void setVisible(boolean visible) {
        this.visible = visible;

    }

    @Override
    public boolean isNavigable() {
        boolean navigable = true;
        if (futureOptionTHREE != null && futureOptionTHREE.equals("false")) {
            navigable = false;
        }
        return navigable;
    }

    @Override
    public void setLabelVisibility(boolean visible) {
        displayLabel = visible;
    }

    @Override
    public boolean shouldDisplayLabel() {
        return displayLabel;
    }

    @Override
    public String getAssociatedParkingId() {
        return futureOptionTWO;
    }


    public static List<IPoi> getExits(List<IPoi> allPois){
        List<IPoi> exitpois = new ArrayList<>();
        for (IPoi o : allPois) {
            if (representsExit(o)) {
                exitpois.add(o);
            }
        }
        return exitpois;
    }

    public static boolean representsExit(IPoi poi){
       return poi.getPoiID().contains("idr") && "internal".equals(poi.getPoiNavigationType());
    }

    public static boolean isExternal(IPoi poi){
        return poi.getPoiNavigationType().equals(IPoi.EXTERNAL);
    }

    public static boolean isInternal(IPoi poi){
        return !isExternal(poi);
    }

    public static void ensureExternal(IPoi poi){
        if(!isExternal(poi))
            throw new IllegalArgumentException("Expected external POI but got: " + poi);
    }

    public static LatLng getLatLng(IPoi poi){
        double poiLatitude = poi.getPoiLatitude();
        double poiLongitude = poi.getPoiLongitude();

        return (poiLatitude != -1 && poiLongitude != -1) ? new LatLng(poiLatitude, poiLongitude) : Location.getLatLng(poi.getX(), poi.getY(), poi.getFacilityID());
    }

    public static Bitmap getIcon(Context context, IPoi poi){
        Bitmap icon = poi.getIcon();
        if (icon == null) {
            icon = BitmapFactory.decodeResource(context.getResources(), R.drawable.defualtpoiicon);
        }
        return icon;
    }
}
