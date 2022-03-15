package com.mlins.utils.distance;

import com.mlins.switchfloor.SwitchFloorHolder;
import com.mlins.switchfloor.SwitchFloorObj;
import com.spreo.nav.interfaces.ILocation;

import org.powermock.api.mockito.PowerMockito;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import tests.PointMock;

public class SwitchFloorHolderMock extends SwitchFloorHolder{

    private final Map<String, List<SwitchFloorObj>> facilities = new HashMap<>();

    public static void setup(SwitchFloorHolderMock mock){
        PowerMockito.mockStatic(SwitchFloorHolder.class);

        PowerMockito.when(SwitchFloorHolder.getInstance()).thenReturn(mock);
    }

    @Override
    public List<SwitchFloorObj> getSwichFloorPoints(String Facilityid) {
        return facilities.get(Facilityid);
    }

    public void addFloorSwitch(String facilityID, ILocation location, int from, int to){
        List<SwitchFloorObj> floorSwitches = facilities.get(facilityID);

        if(floorSwitches == null) {
            floorSwitches = new ArrayList<>();
            facilities.put(facilityID, floorSwitches);
        }

        SwitchFloorObj obj = new SwitchFloorObj();
        obj.setFromFloor(Arrays.asList(new Integer[]{from, to}));
        obj.setToFloor(Arrays.asList(new Integer[]{from, to}));
        obj.setPoint(new PointMock((float) location.getX(), (float) location.getY()));

        floorSwitches.add(obj);
    }
}
