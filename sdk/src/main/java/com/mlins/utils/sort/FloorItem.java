package com.mlins.utils.sort;

import com.spreo.nav.interfaces.ILocation;
import com.spreo.nav.interfaces.IPoi;

import java.util.Arrays;
import java.util.List;

class FloorItem extends SortResultItem {

    private final PoiItem[] items;

    FloorItem(FloorHelper floor, ILocation indoorLocationToSortPOIsAndWeightFloor) {
        super(calcWeight(floor.getID(), (int) indoorLocationToSortPOIsAndWeightFloor.getZ()));
        List<IPoi> pois = floor.getPOIs();
        items = new PoiItem[pois.size()];
        for (int i = 0; i < items.length; i++) {
            items[i] = new PoiItem(pois.get(i), indoorLocationToSortPOIsAndWeightFloor.getX(), indoorLocationToSortPOIsAndWeightFloor.getY());
        }
        Arrays.sort(items);
    }

    private static double calcWeight(int floorID, int floorToWeightID){
        int floorDistance = floorID - floorToWeightID;
        return floorDistance > 0 ? floorDistance : (Math.abs(floorDistance) - 0.5d);
    }

    @Override
    public void addToResultsList(List<IPoi> list) {
        for (int i = 0; i < items.length; i++) {
            items[i].addToResultsList(list);
        }
    }
}
