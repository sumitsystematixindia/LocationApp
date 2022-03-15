package com.spreo.sdk.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.View;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.LatLng;
import com.mlins.instructions.InstructionBuilder;
import com.mlins.nav.utils.ParkingUtil;
import com.mlins.navroute_overview_utils.NavRouteImageDrawer;
import com.mlins.utils.GMapOverlyData;
import com.mlins.utils.PropertyHolder;
import com.mlins.utils.gis.Location;
import com.mlins.views.LocationMapView;
import com.spreo.interfaces.SpreoMapViewListener;
import com.spreo.interfaces.SpreoNavigationListener;
import com.spreo.nav.interfaces.ILocation;
import com.spreo.nav.interfaces.INavInstruction;
import com.spreo.nav.interfaces.IPoi;

import java.util.List;
import java.util.Map;

/**
 * This Map View Class shows indoor and outdoor map according to user location /api calls
 * and manages all navigation ui zoom action poi handling  sound playing etc ...
 *
 * @author Spreo
 */
public class SpreoMapView extends LocationMapView {

    /**
     * Constructor
     *
     * @param context
     * @param attrs
     */
    public SpreoMapView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * Presents a campus
     *
     * @param aCampus
     * @throws Exception
     */
    public void presentCampus(String aCampus) {
        super.presentCampus(aCampus);
    }

    /**
     * Present the given facility of campus, show facility, walk way paths and POIs.
     *
     * @param aCampus   the campus to select
     * @param aFacility the facility to show within the campus
     */
    public void presentFacility(String aCampus, String aFacility) {
        super.presentFacility(aCampus, aFacility);
    }


    /**
     * Shows my location if automatically selects facility and campus based on user location
     * the map centers around user location in default zoom
     */
    public void showMyLocation() {
        super.showMyLocation();
    }

    /**
     * The map will increase the zoom level by one notch, until it gets to the maximum zoom level.
     */
    public void mapZoomIn() {
        super.mapZoomIn();
    }

    /**
     * The map will decrease the zoom level by one notch, until it gets to the minimal zoom level.
     */
    public void mapZoomOut() {
        super.mapZoomOut();
    }

    /**
     * Register to map events.
     * <pre>
     *  example:
     * <code>
     * public class MapListener implements SpreoMapViewListener {
     * mapView.registerMapListener(this);
     * </code>
     * </pre>
     *
     * @param listener
     */
    public void registerMapListener(SpreoMapViewListener listener) {
        super.registerListener(listener);
    }

    /**
     * Unregister from map events.
     *
     * @param listener
     */
    public void unregisterMapListener(SpreoMapViewListener listener) {
        super.unregisterListener(listener);
    }

    @Override
    /**
     register to the navigation events.
     * <pre>
     *  example:
     * <code>
     public class NavigationListener implements SpreoNavigationListener {
     mapView.registerNavigationListener(this);
     }
     * </code>
     * </pre>
     */
    public void registerNavigationListener(SpreoNavigationListener listener) {
        super.registerNavigationListener(listener);
    }

    @Override
    /**
     * Unregister from the navigation events.
     * @param listener
     */
    public void unregisterNavigationListener(SpreoNavigationListener listener) {
        super.unregisterNavigationListener(listener);
    }

    /**
     * Presents a specific floor.
     *
     * @param floorId
     */
    public void showFloorWithId(int floorId) {
        super.showFloorWithId(floorId);
    }

    /**
     * Show location of poi on mapview
     *
     * @param poi
     */
    public void showPoi(IPoi poi) {
        super.showPoi(poi);
    }

    /**
     * Show location of poi on mapview
     *
     * @param poi
     * @param customBubbleText
     */
    public void showPoi(IPoi poi, String customBubbleText) {
        super.showPoi(poi, customBubbleText);
    }

    /**
     * Center mapview to present a location.
     *
     * @param location
     */
    public void presentLocation(ILocation location) {
        Location loc = new Location(location);
        super.presentLocation(loc);
    }

    /**
     * Gets current presented floor id
     */
    public int getPresentedFloorId() {
        return super.getPresentedFloorId();
    }

    /**
     * Gets Floor Title of a floor
     *
     * @param floorId
     */
    public String getFloorTitleForFloorId(int floorId) {
        return super.getFloorTitleForFloorId(floorId);
    }

    /**
     * return to my current location on mapView.

     public void returnToMyLocation() {
     super.returnToMyLocation();
     }
     */

    /**
     * Sets the map background color.
     * * <pre>
     *  example:
     * <code>
     * Set the map background color.
     * int color = Color.parseColor("#668784");
     * mapView.setMapBackgroundColor(color);
     * </code>
     * </pre>
     *
     * @param color
     */
    public void setMapBackgroundColor(int color) {
        super.setMapBackgroundColor(color);
    }

    /**
     * Sets the default user icon.
     *
     * @param userBitmap
     */
    public void setUserIcon(Bitmap userBitmap) {
        super.setUserIcon(userBitmap);
    }

    /**
     * Navigates to a specific IPOi
     *
     * @param poi
     */
    public void navigateTo(IPoi poi) {
        super.navigateTo(poi);
    }

    /**
     * Navigate to destination
     *
     * @param destination
     */
    public void navigateTo(ILocation destination) {
        Location loc = new Location(destination);
        super.navigateTo(loc);
    }

    /**
     * Simulate navigation to specific POI
     *
     * @param poi
     */
    public void simulateNavigationTo(IPoi poi) {
        super.simulateNavigationTo(poi);
    }

    /**
     *  Simulate navigation to a specific poi from a specific origin
     *  @param origin
     *  @param poi

    public void simulateNavigationTo(ILocation origin, IPoi poi) {
    Location loc = new Location(origin);
    super.simulateNavigationTo(loc, poi);
    }
     */

    /**
     * Simulate navigation to a list of POIs
     *
     * @param poislist
     */
    public void navigateTo(List<IPoi> poislist) {
        super.navigateTo(poislist);
    }

    /**
     * Simulate navigation to a list of Pois from a specific origin location.
     *
     * @param origin
     * @param poislist
     */
    public void simulateNavigationTo(ILocation origin, List<IPoi> poislist) {
        Location loc = new Location(origin);
        super.simulateNavigationTo(loc, poislist);
    }

    /**
     * Simulate navigation to a IPoi from a specific origin location.
     *
     * @param origin
     * @param poislist
     */
    public void continueSimulateNavigationTo(IPoi origin, List<IPoi> poislist) {
        super.continueSimulateNavigationTo(origin, poislist);
    }

    /**
     * Continue navigation to a list of POIs.
     *
     * @param poislist
     */
    public void continueNavigationTo(List<IPoi> poislist) {
        super.continueNavigationTo(poislist);
    }

    /**
     * Stops navigation
     */
    public void stopNavigation() {
        super.stopNavigation();
    }

    /**
     * Gets the state of navigation.
     */
    public boolean isNavigationState() {
        return super.isNavigationState();
    }

    /**
     * Sets your current location as parking location.
     */
    public void setCurrentLocationAsParking() {
        super.setCurrentLocationAsParking();
    }

    /**
     * Removes the saved parking location.
     */
    public void removeParkingLocation() {
        super.removeParkingLocation();
    }

    /**
     * Checks if there is a saved parking location.
     *
     * @return true if mapView has parking Location. Otherwise, return false.
     */
    public boolean hasParkingLocation() {
        boolean result = false;
        ILocation ploc = ParkingUtil.getInstance().getParkingLocation();
        if (ploc != null) {
            result = true;
        }
        return result;
    }

    /**
     * Navigate to the saved parking location.
     */
    public void navigateToParking() {
        super.navigateToParking();
    }

    /**
     * Not implemented yet

     public void reloadMapData() throws Exception {
     super.reloadMapData();
     }
     */

    /**
     * sets image overlay for campus
     *
     * @param campusId     is the campus id
     * @param planeImage   is the overlay image
     * @param center       is the center location of campus
     * @param height       overlay
     * @param width        overlay
     * @param bearingAngle is the bearing angle of campus
     * @param transparency is the transparency of the overlay (a value between 0 and 1)
     */
    public void setOverlayForCampus(String campusId, BitmapDescriptor planeImage, LatLng center,
                                    float height, float width,
                                    float bearingAngle, float transparency) {


        GMapOverlyData gmCampusOverlyData = new GMapOverlyData();
        gmCampusOverlyData.campusId = campusId;
        gmCampusOverlyData.planeImage = planeImage;
        gmCampusOverlyData.center = center;
        gmCampusOverlyData.height = height;
        gmCampusOverlyData.width = width;
        gmCampusOverlyData.height = height;
        gmCampusOverlyData.bearingAngle = bearingAngle;
        gmCampusOverlyData.transparency = transparency;
        PropertyHolder.getInstance().setGmCampusOverlyData(gmCampusOverlyData);
        super.setOverlayForCampus(campusId, planeImage, center,
                height, width,
                bearingAngle, transparency);

    }


    /**
     * sets image overlay for facility
     *
     * @param campusId     is the campus id
     * @param facilityId   is the facility id
     * @param planeImage   is the overlay image
     * @param center       is the center location of campus
     * @param height       overlay
     * @param width        overlay
     * @param bearingAngle is the bearing angle of campus
     * @param transparency is the transparency of the overlay (a value between 0 and 1)
     */
    public void setOverlayForFacility(String campusId, String facilityId, BitmapDescriptor planeImage, LatLng center,
                                      float height, float width,
                                      float bearingAngle, float transparency) {
        GMapOverlyData gmFacilityOverlyData = new GMapOverlyData();
        gmFacilityOverlyData.campusId = campusId;
        gmFacilityOverlyData.facilityId = facilityId;
        gmFacilityOverlyData.planeImage = planeImage;
        gmFacilityOverlyData.center = center;
        gmFacilityOverlyData.height = height;
        gmFacilityOverlyData.width = width;
        gmFacilityOverlyData.height = height;
        gmFacilityOverlyData.bearingAngle = bearingAngle;
        gmFacilityOverlyData.transparency = transparency;
        PropertyHolder.getInstance().setGmFacilityOverlyData(gmFacilityOverlyData);
        super.setOverlayForFacility(campusId, facilityId, planeImage, center,
                height, width,
                bearingAngle, transparency);

    }

    /**
     * @return list of the instructions of the current navigation route
     */
    public List<INavInstruction> getNavigationInstructionsList() {
        return InstructionBuilder.getInstance().getNavInstructions();


    }

    /**
     * Gets the current navigation route as bitmaps objects. each bitmap is corresponding to a specific floor.
     * If the state of navigation is idle then this method returns null.
     *
     * @return map of bitmaps where keys are floor indexes and the values are the bitmaps
     */
    public Map<Integer, Bitmap> getNavigationRouteBitmaps() {
        NavRouteImageDrawer d = new NavRouteImageDrawer();
        Map<Integer, Bitmap> map = d.getAllFloorsBitmap();
        return map;
    }

    /**
     * redraw pois on floor map
     */
    public void reDrawPois() {
        super.reDrawPois();
    }

    /**
     * Show all pois
     */
    public void showAllPois() {
        super.displayPois(true);
    }

    /**
     * Hide all pois
     */
    public void hideAllPois() {
        super.displayPois(false);
    }

    /**
     * open poi bubble
     *
     * @param poi
     */

    public void openPoiBubble(IPoi poi) {
        super.openPoiBubble(poi);
    }

    /**
     * open poi bubble
     *
     * @param poi
     * @param customText
     */

    public void openPoiBubble(IPoi poi, String customText) {
        super.openPoiBubble(poi, customText);
    }

    /**
     * get the parking location
     *
     * @return ILocation
     */
    public ILocation getParkingLocation() {
        return ParkingUtil.getInstance().getParkingLocation();
    }

    /**
     * present the navigation route for multi poi navigation
     *
     * @param the poi list
     * @param the visited poi list
     */

    public void presentMultiPoiRoute(List<IPoi> poilist, List<IPoi> visitedPoiList) {
        super.presentMultiPoiRoute(poilist, visitedPoiList);
    }

    /**
     * remove the presented navigation route for multi poi navigation
     */

    public void removeMultiPoiRoute() {
        super.removeMultiPoiRoute();
    }

    /**
     * Closes the bubble of the poi
     *
     * @param poi
     */
    public void closeBubble(IPoi poi) {
        super.closeBubble(poi);
    }

    /**
     * Sets parking bubble text
     *
     * @param txt
     */
    public void setMyParkingBubbleText(String txt) {
        super.setMyParkingBubbleText(txt);
    }

    /**
     * Sets parking marker icon
     *
     * @param icon
     */
    public void setMyParkingMarkerIcon(Bitmap icon) {
        super.setMyParkingMarkerIcon(icon);
    }

    /**
     * open parking bubble
     */
    public void openMyParkingMarkerBubble() {
        super.openMyParkingMarkerBubble();
    }

    /**
     * close parking bubble
     */
    public void closeMyParkingMarkerBubble() {
        super.closeMyParkingMarkerBubble();
    }

    /**
     * close all poi bubbles
     */
    public void closeAllPoiBubbles() {
        super.closeAllPoiBubbles();
    }

    /**
     * simulate navigation from location to poi
     *
     * @param origin
     * @param poi
     */
    public void simulateNavigationTo(ILocation origin, IPoi poi) {
        super.simulateNavigationTo(origin, poi);
    }

    /**
     * simulate navigation from location to the saved parking location
     *
     * @param origin
     */
    public void simulateNavigationToParking(ILocation origin) {
        super.simulateNavigationToParking(origin);
    }

    /**
     * open the bubble for the user location with a custom view
     *
     * @param view
     */
    public void openUserBubble(View view) {
        super.openUserBubble(view);
    }

    /**
     * close the bubble for the user location
     */
    public void closeUserBubble() {
        super.closeUserBubble();
    }

    /**
     * stop the simulation
     */
    public void stopSimulation() {
        super.stopSimulation();
    }


}
