package com.mlins.utils.sort;

import com.google.android.gms.maps.model.LatLng;
import com.mlins.utils.PoiData;
import com.spreo.nav.interfaces.IPoi;

import java.util.List;

public class PoiItem extends LocationItem {

    private final IPoi poi;

    PoiItem(IPoi poi, double x, double y) {
        super(poi.getPoint(), x, y);
        this.poi = poi;
    }

    public PoiItem(IPoi poi, LatLng userLocation) {
        super(poi.getPoiLatitude(), poi.getPoiLongitude(), userLocation);
        PoiData.ensureExternal(poi);
        this.poi = poi;
    }

    @Override
    public void addToResultsList(List<IPoi> list){
        list.add(poi);
    }

}
