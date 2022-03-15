package com.mlins.campaign;

import java.util.ArrayList;


public interface IBanner {

    String getFirma();

    void setFirma(String firma);

    String getFileName();

    void setFileName(String fileName);

    String getURL();

    void setURL(String uRL);

    ArrayList<BannersLocationArea> getBannerLocations();

    //XXX BannersLocationArea!
    BannersLocationArea getDestination();

    void setDestination(BannersLocationArea destination);

    ArrayList<String> getCategories();

    void setCategories(ArrayList<String> categories);

    //XXX ??
    String getPoiName();

    void setPoiName(String poiName);

    String getBannerText();

    void setBannerText(String bannerText);

}
