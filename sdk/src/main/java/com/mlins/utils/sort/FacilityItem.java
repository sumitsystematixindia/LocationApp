package com.mlins.utils.sort;

import com.google.android.gms.maps.model.LatLng;
import com.mlins.utils.gis.Location;
import com.spreo.nav.interfaces.ILocation;
import com.spreo.nav.interfaces.IPoi;

import java.util.Arrays;
import java.util.List;

public class FacilityItem extends LocationItem {

    private final FloorItem[] floorItems;

    public FacilityItem(FacilityHelper facility, ILocation exitLocationToSortContent, LatLng outDoorLocationToWeight){
        super(Location.getLatLng(exitLocationToSortContent), outDoorLocationToWeight);
        Location.ensureInDoor(exitLocationToSortContent);
        floorItems = getFloorItems(facility, exitLocationToSortContent);
    }

    @Override
    public void addToResultsList(List<IPoi> list) {
        for (int i = 0; i < floorItems.length; i++) {
            floorItems[i].addToResultsList(list);
        }
    }

    private static FloorItem[] getFloorItems(FacilityHelper facility, ILocation indoorLocationToSortContentAndWeightFloor){
        List<FloorHelper> floors = facility.getFloors();
        FloorItem[] floorItems = new FloorItem[floors.size()];
        for (int floorItemIndex = 0; floorItemIndex < floorItems.length; floorItemIndex++) {
            floorItems[floorItemIndex] = new FloorItem(floors.get(floorItemIndex), indoorLocationToSortContentAndWeightFloor);
        }
        Arrays.sort(floorItems);
        return floorItems;
    }

}
