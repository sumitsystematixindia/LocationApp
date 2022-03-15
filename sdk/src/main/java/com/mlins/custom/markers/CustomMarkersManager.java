package com.mlins.custom.markers;

import com.mlins.utils.Lookup;
import com.spreo.interfaces.ICustomMarker;

import java.util.ArrayList;
import java.util.List;

public class CustomMarkersManager {

    private List<ICustomMarker> customMarkers = new ArrayList<>();

    public static CustomMarkersManager getInstance() {
        return Lookup.getInstance().get(CustomMarkersManager.class);
    }

    public static void releaseInstance() {
        Lookup.getInstance().remove(CustomMarkersManager.class);
    }

    public void addCustomMarker(ICustomMarker customMarker){
        customMarkers.add(customMarker);
    }

    public boolean removeCustomMarker(ICustomMarker customMarker){
        return customMarkers.remove(customMarker);
    }

    public void addCustomMarkers(List<ICustomMarker> list) {
        customMarkers.addAll(list);
    }

    public boolean removeCustomMarkers(List<ICustomMarker> list){
        return customMarkers.removeAll(list);
    }

    public void setCustomMarkers(List<ICustomMarker> customMarkers) {
        if(customMarkers != null)
            this.customMarkers = customMarkers;
        else
            this.customMarkers.clear();
    }

    public List<ICustomMarker> getCustomMarkers() {
        return customMarkers;
    }

}
