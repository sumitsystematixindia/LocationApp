package com.mlins.utils;

public class PoiType {
    public String poiuri = "";
    public String poitype = "";
    public String poidescription = "";
    public boolean showInCatgories = true;
    public boolean showInMapFilter = true;
    public PoiType(String icon, String type, String description, boolean showInCatgories, boolean showInMapFilter) {
        poiuri = icon;
        poitype = type;
        poidescription = description;
        this.showInCatgories = showInCatgories;
        this.showInMapFilter = showInMapFilter;
    }

    public String getPoiuri() {

        if (PropertyHolder.useZip) {
            if (!poiuri.contains("/spreo_poi_")) {
                return ServerConnection.getProjectResourcesUrl() + "spreo_poi_" + poiuri;
            }
        }

        return poiuri;
    }

    public void setPoiuri(String poiuri) {
        this.poiuri = poiuri;
    }

    public String getPoitype() {
        return poitype;
    }

    public void setPoitype(String poitype) {
        this.poitype = poitype;
    }

    public String getPoidescription() {
        return poidescription;
    }

    public void setPoidescription(String poidescription) {
        this.poidescription = poidescription;
    }

    public boolean getShowInCatgories(){
        return showInCatgories;
    }

    public void setShowInCatgories(boolean showInCatgories){
        this.showInCatgories = showInCatgories;
    }

    public boolean getShowInMapFilter(){
        return showInMapFilter;
    }

    public void setShowInMapFilter(boolean showInMapFilter){
        this.showInMapFilter = showInMapFilter;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((poitype == null) ? 0 : poitype.hashCode());
        return result;
    }


    @Override
    public boolean equals(Object o) {
        if (o instanceof String) {
            return poitype.equals(o);
        }
        if (o instanceof PoiType) {
            PoiType otype = (PoiType) o;
            return poitype.equals(otype.poitype);
        }

        return false;
    }


}
