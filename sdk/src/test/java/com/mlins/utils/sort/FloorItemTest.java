package com.mlins.utils.sort;

import com.mlins.utils.PoiData;
import com.mlins.utils.gis.Location;
import com.spreo.nav.interfaces.IPoi;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import tests.PointMock;

import static junit.framework.TestCase.assertTrue;

public class FloorItemTest {

    @Test
    public void floorItem_addToResultsList_Provides_Sorted_List() {
        List<IPoi> sortedResult = new ArrayList<>();
        addPoi(sortedResult, 5, 5);
        addPoi(sortedResult, 4, 6);
        addPoi(sortedResult, 6, 7);
        addPoi(sortedResult, 3, 3);
        addPoi(sortedResult, 9, 7);
        addPoi(sortedResult, 1, 10);

        List<IPoi> shuffled = new ArrayList<>(sortedResult);
        Collections.shuffle(shuffled);

        FloorHelper floor = new FloorHelper("test", "test", 0, shuffled);
        FloorItem itemToTest = new FloorItem(floor, new Location(sortedResult.get(0)));

        List<IPoi> testResult = new ArrayList<>();
        itemToTest.addToResultsList(testResult);

        System.out.println("Expected: " + Arrays.deepToString(sortedResult.toArray()));
        System.out.println("Result: " + Arrays.deepToString(testResult.toArray()));


        assertTrue(sortedResult.equals(testResult));
    }

    static void addPoi(List<IPoi> pois, int x, int y){
        PoiData result = new PoiData(new PointMock(x, y));
        //result.setpoiDescription('(' + x + ", " + y + ')');
        result.setPoiNavigationType("internal");
        pois.add(result);
    }

}

