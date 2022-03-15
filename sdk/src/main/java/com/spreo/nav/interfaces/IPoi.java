package com.spreo.nav.interfaces;

import android.graphics.Bitmap;
import android.graphics.PointF;

import com.mlins.utils.GalleryObject;

import java.util.List;

public interface IPoi {

    String EXTERNAL = "external";
    String INTERNAL = "internal";

    List<String> getPoiKeywords();

    void setPoiKeywords(List<String> poiKeywords);

    String getPoiuri();

    void setPoiuri(String poiuri);

    List<String> getPoitype();

    void setPoitype(String poitype);

    String getpoiDescription();

    void setpoiDescription(String description);

    PointF getPoint();

    void setPoint(PointF point);

    double getZ();

    void setZ(double z);

    String getUrl();

    void setUrl(String url);

    String getDetails();

    void setDetails(String details);

    boolean isShowPoiOnMap();

    void setShowPoiOnMap(boolean showPoiOnMap);

    boolean isShowPoiOnSearches();

    void setShowPoiOnSearches(boolean showPoiOnSearches);

    boolean isShowPoiBubble();

    void setShowPoiBubble(boolean showPoiBubble);

    boolean isPoiPlayMultyMedia();

    void setPoiPlayMultyMedia(boolean poiPlayMultyMedia);

    String getPoiID();

    void setPoiID(String poiID);

    String getPoiNavigationType();

    void setPoiNavigationType(String poiNavigationType);

    double getPoiLatitude();

    void setPoiLatitude(double poiLatitude);

    double getPoiLongitude();

    void setPoiLongitude(double poiLongitude);

    /**
     * true if we should show poi on any zoom level
     * @return
     */
    boolean isShowOnZoomLevel();

    void setShowOnZoomLevel(boolean showOnZoomLevel);

    float getX();

    float getY();

    boolean isPoiClickAble();

    void setPoiClickAble(boolean poiClickAble);

    boolean isInstructionsParticipate();

    void setInstructionsParticipate(boolean instructionsParticipate);

    String getPoiofficeinstuctions();

    void setPoiofficeinstuctions(String poiofficeinstuctions);

    boolean isPoishowincategory();

    void setPoishowincategory(boolean poishowincategory);

    String getMediaurl();

    void setMediaurl(String mediaurl);

    String getKeyWordsAsString();

    ILocation getLocation();

    void setLocation(ILocation location);

    List<String> getActivehours();

    void setActivehours(List<String> activehours);

    List<String> getPhone2hours();

    void setPhone2hours(List<String> Phone2hours);

    List<String> getPhone1();

    void setPhone1(List<String> Phone1);

    List<String> getPhone2();

    void setPhone2(List<String> Phone2);

    String getEmailaddress();

    void setEmailaddress(String Emailaddress);

    Bitmap getIcon();

    void setIcon(Bitmap icon);

    List<GalleryObject> getGallery();

    void addGalleryImage(GalleryObject g);

    GalleryObject getHeadImage();

    void setHeadImage(GalleryObject g);

    void recycleGallery();

    String getCampusID();

    void setCampusID(String campusID);

    String getFacilityID();

    void setFacilityID(String facilityID);

    boolean isVisible();

    void setVisible(boolean visible);

    boolean isNavigable();

    void setLabelVisibility(boolean visible);

    boolean shouldDisplayLabel();

    String getAssociatedParkingId();
}
