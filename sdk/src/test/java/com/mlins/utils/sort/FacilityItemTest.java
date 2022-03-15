package com.mlins.utils.sort;

import com.google.android.gms.maps.model.LatLng;
import com.mlins.utils.FacilityConf;
import com.mlins.utils.PoiData;
import com.mlins.utils.gis.Location;
import com.spreo.nav.interfaces.IPoi;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import tests.PointMock;

import static junit.framework.TestCase.assertTrue;
import static org.mockito.Matchers.any;

@RunWith(PowerMockRunner.class)
@PrepareForTest({android.location.Location.class, com.mlins.utils.gis.Location.class})
public class FacilityItemTest {

    @Test
    public void facilityItem_addToResultsList_Provides_Sorted_List() {

        PowerMockito.mockStatic(android.location.Location.class);
        PowerMockito.doNothing().when(android.location.Location.class);
        android.location.Location.distanceBetween(0, 0, 0, 0, new float[1]);

        PowerMockito.mockStatic(com.mlins.utils.gis.Location.class);
        PowerMockito.when(com.mlins.utils.gis.Location.getLatLng(any(com.mlins.utils.gis.Location.class))).thenReturn(new LatLng(0,0));

        List<IPoi> sortedResult = new ArrayList<>();
        //floors from 0 to 8 sorted by distance from third floor
        addPoi(sortedResult, 3);
        addPoi(sortedResult, 2);
        addPoi(sortedResult, 4);
        addPoi(sortedResult, 1);
        addPoi(sortedResult, 5);
        addPoi(sortedResult, 0);
        addPoi(sortedResult, 6);
        addPoi(sortedResult, 7);
        addPoi(sortedResult, 8);

        List<IPoi> shuffled = new ArrayList<>(sortedResult);
        Collections.shuffle(shuffled);


        FacilityHelper facility = new FacilityHelper(FloorHelper.getFloorsFor("test", "test", shuffled), new FacilityConf());
        FacilityItem facilityItem = new FacilityItem(facility, new Location(0, 0, 3), new LatLng(0, 0));

        List<IPoi> testResult = new ArrayList<>();
        facilityItem.addToResultsList(testResult);

        System.out.println("Expected: " + Arrays.deepToString(sortedResult.toArray()));
        System.out.println("Result: " + Arrays.deepToString(testResult.toArray()));


        assertTrue(sortedResult.equals(testResult));
    }

    static void addPoi(List<IPoi> pois, int z){
        PoiData result = new PoiData(new PointMock(0, 0));
        result.setPoiNavigationType("internal");
        result.setZ(z);
        pois.add(result);
    }
}

