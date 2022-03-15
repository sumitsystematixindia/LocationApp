package com.mlins.campaign;

import android.graphics.RectF;

import com.mlins.utils.gis.Location;
import com.spreo.geofence.GeoFenceRect;
import com.spreo.nav.interfaces.ILocation;

import java.util.ArrayList;


public class BannerObject implements IBanner {

    private String firma = "";
    private String fileName = "";
    private String URL = "";
    private ArrayList<BannersLocationArea> bannerLocations = new ArrayList<BannersLocationArea>();
    private BannersLocationArea destination = null;
    private ArrayList<String> categories = new ArrayList<String>();
    private String poiName = "";
    private Boolean isFavorite = false;
    private String bannerText = "";

    public BannerObject() {
    }

    public BannerObject(String imgBannerName) {
        fileName = imgBannerName;
        URL = "https://spreo.co/";
    }

    public boolean isInside(ILocation loc) {
        for (BannersLocationArea everyLocation : bannerLocations) {
            if (everyLocation.isInside(loc)) {
                return true;
            }
        }
        return false;

    }

    /**
     * @param rectZone
     * @return
     */
    public boolean isInside(GeoFenceRect rectZone) {
        for (BannersLocationArea everyLocation : bannerLocations) {
            if (RectF.intersects(everyLocation.getBannerRect(), rectZone.getZone())) {
                return true;
            }
        }
        return false;
    }

    public String getFirma() {
        return firma;
    }

    public void setFirma(String firma) {
        this.firma = firma;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getURL() {
        return URL;
    }

    public void setURL(String uRL) {
        URL = uRL;
    }

    public ArrayList<BannersLocationArea> getBannerLocations() {
        return bannerLocations;
    }

    public void setBannerLocations(
            ArrayList<BannersLocationArea> bannerLocations) {
        this.bannerLocations = bannerLocations;
    }

    public BannersLocationArea getDestination() {
        return destination;
    }

    public void setDestination(BannersLocationArea destination) {
        this.destination = destination;
    }

    public ArrayList<String> getCategories() {
        return categories;
    }

    public void setCategories(ArrayList<String> categories) {
        this.categories = categories;
    }

    public void Parse(String line) {
        String[] vals = line.split("\t");

        setFirma(vals[0]);
        setFileName(vals[1]);
        setURL(vals[2]);

        String locs[] = vals[3].split(";");
        for (String loc : locs) {
            String l[] = loc.split("-");

            String pt[] = l[0].split(",");
            Location upLeft = new Location();
            upLeft.setX(Double.valueOf(pt[0]));
            upLeft.setY(Double.valueOf(pt[1]));
            upLeft.setZ(Double.valueOf(pt[2]));

            pt = l[1].split(",");
            Location downRight = new Location();
            downRight.setX(Double.valueOf(pt[0]));
            downRight.setY(Double.valueOf(pt[1]));
            downRight.setZ(Double.valueOf(pt[2]));
            if ((upLeft.getX() != downRight.getX()) || (upLeft.getY() != downRight.getY()) || (upLeft.getZ() != downRight.getZ())) {
                BannersLocationArea obj = new BannersLocationArea(upLeft, downRight);
                bannerLocations.add(obj);
            }
        }

        String destination[] = vals[4].split("-");

        String pt[] = destination[0].split(",");
        Location destUpLeft = new Location();
        destUpLeft.setX(Double.valueOf(pt[0]));
        destUpLeft.setY(Double.valueOf(pt[1]));
        destUpLeft.setZ(Double.valueOf(pt[2]));

        pt = destination[1].split(",");
        Location destDownRight = new Location();
        destDownRight.setX(Double.valueOf(pt[0]));
        destDownRight.setY(Double.valueOf(pt[1]));
        destDownRight.setZ(Double.valueOf(pt[2]));

        setDestination(new BannersLocationArea(destUpLeft, destDownRight));

        String cats[] = vals[5].split(";");
        for (String cat : cats) {
            categories.add(cat);
        }

        if (vals.length >= 7 && vals[6] != null) {
            setBannerText(vals[6]);
        }


        if (vals.length >= 8 && vals[7] != null) {
            poiName = vals[7];
        }
    }

    public Boolean getIsFavorite() {
        return isFavorite;
    }

    public void setIsFavorite(Boolean isFavorite) {
        this.isFavorite = isFavorite;
    }

    public String getPoiName() {
        return poiName;
    }

    public void setPoiName(String poiName) {
        this.poiName = poiName;
    }

    public String getBannerText() {
        return bannerText;
    }

    public void setBannerText(String bannerText) {
        this.bannerText = bannerText;
    }


}
