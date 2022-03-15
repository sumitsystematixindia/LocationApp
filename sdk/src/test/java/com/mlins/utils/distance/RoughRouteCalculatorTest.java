package com.mlins.utils.distance;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.mlins.project.ProjectConf;
import com.mlins.switchfloor.SwitchFloorHolder;
import com.mlins.utils.gis.Location;
import com.spreo.nav.interfaces.ILocation;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.core.classloader.annotations.SuppressStaticInitializationFor;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.List;

import tests.AndroidLogMock;

import static junit.framework.TestCase.assertTrue;

@RunWith(PowerMockRunner.class)
@PrepareForTest({Log.class, ProjectConf.class, SwitchFloorHolder.class, com.mlins.utils.gis.Location.class, android.location.Location.class})
@SuppressStaticInitializationFor("com.mlins.utils.PoisContainer")
public class RoughRouteCalculatorTest {

    private static class Facility {

        private static final String CAMPUS = "campus";

        private final String id;
        private final LatLng latLng;

        private final ILocation floorSwitchLeftTopL0;
        private final ILocation floorSwitchLeftTopL1;

        private final ILocation floorSwitchRightBottomL0;
        private final ILocation floorSwitchRightBottomL1;

        private final ILocation exitLeftSideIndoor;
        private final ILocation exitRightSideIndoor;

        private final ILocation exitLeftSideOutdoor;
        private final ILocation exitRightSideOutdoor;


        private Facility(String id, LatLng lat_lng) {
            this.id = id;
            latLng = lat_lng;

            floorSwitchLeftTopL0 = new Location(this.id, CAMPUS, 0, 0, 0);

            floorSwitchLeftTopL1 = new Location(floorSwitchLeftTopL0);
            floorSwitchLeftTopL1.setZ(1);

            floorSwitchRightBottomL0 = new Location(this.id, CAMPUS, 10, 10, 0);

            floorSwitchRightBottomL1 = new Location(floorSwitchRightBottomL0);
            floorSwitchRightBottomL1.setZ(1);

            exitLeftSideIndoor = new Location(this.id, CAMPUS, 0, 5, 0);
            exitRightSideIndoor = new Location(this.id, CAMPUS, 10, 5, 0);

            exitLeftSideOutdoor = new Location(new LatLng(0, latLng.longitude - 10));
            exitRightSideOutdoor = new Location(new LatLng(0, latLng.longitude + 10));
        }

        private void addExitsToProjectConf(ProjectConfMock conf){
            conf.addExit(CAMPUS, id, exitLeftSideIndoor, Location.getLatLng(exitLeftSideOutdoor));
            conf.addExit(CAMPUS, id, exitRightSideIndoor, Location.getLatLng(exitRightSideOutdoor));
        }

        private void addFloorSwitches(SwitchFloorHolderMock holder){
            holder.addFloorSwitch(id, floorSwitchLeftTopL0, 0, 1);
            holder.addFloorSwitch(id, floorSwitchRightBottomL0, 0, 1);
        }
    }

    private static final Facility F1 = new Facility("facility1", new LatLng(0, 0));
    private static final Facility F2 = new Facility("facility2", new LatLng(0, 90));
    

    @Before
    public void setup(){

        LocationMock.setup();
        AndroidLocationMock.setup();

        ProjectConfMock projectConf= new ProjectConfMock();

        F1.addExitsToProjectConf(projectConf);
        F2.addExitsToProjectConf(projectConf);

        ProjectConfMock.setup(projectConf);

        SwitchFloorHolderMock holder = new SwitchFloorHolderMock();
        F1.addFloorSwitches(holder);
        F2.addFloorSwitches(holder);

        SwitchFloorHolderMock.setup(holder);
    }

    @Before
    public void setUp()  {
        AndroidLogMock.init();
    }

    @Test
    public void testIndoorToIndoorSameFloor(){
        ILocation origin = createLocation(F1.id, 4, 4, 0);
        ILocation destination = createLocation(F1.id, 6, 6, 0);
        List<ILocation> route = RoughRouteCalculator.getRoutePoints(
                origin,
                destination
        );
        assertTrue(Location.areEqual(route.get(0), origin));
        assertTrue(Location.areEqual(route.get(1), destination));
    }

    @Test
    public void testIndoorToIndoorDifferentFloors(){
        ILocation origin = createLocation(F1.id, 4, 4, 0);
        ILocation destination = createLocation(F1.id, 6, 6, 1);
        List<ILocation> route = RoughRouteCalculator.getRoutePoints(
                origin,
                destination
        );
        assertTrue(Location.areEqual(route.get(0), origin));
        assertTrue(Location.areEqual(route.get(1), F1.floorSwitchLeftTopL0));
        assertTrue(Location.areEqual(route.get(2), F1.floorSwitchLeftTopL1));
        assertTrue(Location.areEqual(route.get(3), destination));
    }

    @Test
    public void testIndoorToOutDoor(){
        ILocation origin = createLocation(F1.id, 6, 6, 1);
        ILocation destination = new Location(new LatLng(10, 20));
        List<ILocation> route = RoughRouteCalculator.getRoutePoints(
                origin,
                destination
        );
        assertTrue(Location.areEqual(route.get(0), origin));
        assertTrue(Location.areEqual(route.get(1), F1.floorSwitchRightBottomL1));
        assertTrue(Location.areEqual(route.get(2), F1.floorSwitchRightBottomL0));
        assertTrue(Location.areEqual(route.get(3), F1.exitRightSideIndoor));
        assertTrue(Location.areEqual(route.get(4), F1.exitRightSideOutdoor));
        assertTrue(Location.areEqual(route.get(5), destination));
    }

    @Test
    public void testIndoorToOtherIndoor(){
        ILocation origin = createLocation(F1.id, 4, 4, 1);
        ILocation destination = createLocation(F2.id, 6, 6, 1);
        List<ILocation> route = RoughRouteCalculator.getRoutePoints(
                origin,
                destination
        );

        assertTrue(Location.areEqual(route.get(0), origin));
        assertTrue(Location.areEqual(route.get(1), F1.floorSwitchLeftTopL1));
        assertTrue(Location.areEqual(route.get(2), F1.floorSwitchLeftTopL0));
        assertTrue(Location.areEqual(route.get(3), F1.exitLeftSideIndoor));
        assertTrue(Location.areEqual(route.get(4), F1.exitLeftSideOutdoor));
        assertTrue(Location.areEqual(route.get(5), F2.exitLeftSideOutdoor));;
        assertTrue(Location.areEqual(route.get(6), F2.exitLeftSideIndoor));
        assertTrue(Location.areEqual(route.get(7), F2.floorSwitchLeftTopL0));
        assertTrue(Location.areEqual(route.get(8), F2.floorSwitchLeftTopL1));
        assertTrue(Location.areEqual(route.get(9), destination));

    }


    private static ILocation createLocation(String facilityID,  int x, int y, int z) {
        return new Location(facilityID, Facility.CAMPUS, x, y, z);
    }

}
