package com.spreo.sdk.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.mlins.dualmap.DualMapView;
import com.spreo.interfaces.CameraChangeListener;
import com.spreo.interfaces.SpreoDualMapViewListener;
import com.spreo.interfaces.SpreoNavigationListener;
import com.spreo.nav.interfaces.ILocation;
import com.spreo.nav.interfaces.INavInstruction;
import com.spreo.nav.interfaces.IPoi;
import com.spreo.sdk.label.LabelUIOptions;
import com.spreo.ui.utils.FloorPickerUIOptions;

import java.util.List;

/**
 * This Map View Class shows the indoor and outdoor maps
 * and manages all navigation ui zoom action poi handling sound playing etc ...
 *
 * @author Spreo
 */

public class SpreoDualMapView extends DualMapView {

    public SpreoDualMapView(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
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
     * Navigate to location
     *
     * @param destination
     */

    public void navigateTo(ILocation destination) {
        super.navigateTo(destination);
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
     * Simulate navigation to a specific poi from a specific origin
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
     * Stops navigation
     */

    public void stopNavigation() {
        super.stopNavigation();
    }

    /**
     * Register to map events.
     * <pre>
     *  example:
     * <code>
     * public class MapListener implements SpreoDualMapViewListener {
     * mapView.registerMapListener(this);
     * </code>
     * </pre>
     *
     * @param listener
     */

    public void registerListener(SpreoDualMapViewListener listener) {
        super.registerListener(listener);
    }

    /**
     * Unregister from map events.
     *
     * @param listener
     */

    public void unregisterListener(SpreoDualMapViewListener listener) {
        super.unregisterListener(listener);
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
     * Center mapview to present a location.
     *
     * @param location
     */

    public void presentLocation(ILocation location) {
        super.presentLocation(location);
    }

    /**
     * Present the given facility of campus, show facility, walk way paths and POIs.
     *
     * @param campusId the campus to select
     * @param campusId the facility to show within the campus
     */

    public void presentFacility(String campusId, String facilityId) {
        super.presentFacility(campusId, facilityId);
    }

    /**
     * Presents a campus
     *
     * @param CampusId
     */

    public void presentCampus(String CampusId) {
        super.presentCampus(CampusId);
    }

    /**
     * Shows the user location
     * the map centers around user location
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
     * Gets current presented floor id
     *
     * @param faciltyId
     */

    public int getPresentedFloorId(String facilityId) {
        return super.getPresentedFloorId(facilityId);
    }

    /**
     * Gets Floor Title of a floor
     *
     * @param floorId
     * @param campusId
     * @param faciltyId
     */

    public String getFloorTitleForFloorId(String campusId, String faciltyId, int floorId) {
        return super.getFloorTitleForFloorId(campusId, faciltyId, floorId);
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
     * Sets the parking location.
     *
     * @param location
     */

    public void setLocationAsParking(ILocation location) {
        super.setLocationAsParking(location);
    }

    /**
     * Removes the saved parking location.
     */

    public void removeParkingLocation() {
        super.removeParkingLocation();
    }

    /**
     * Navigate to the saved parking location.
     */

    public void navigateToParking() {
        super.navigateToParking();
    }

    /**
     * register to the navigation events.
     * <pre>
     *  example:
     * <code>
     * public class NavigationListener implements SpreoNavigationListener {
     * mapView.registerNavigationListener(this);
     * }
     * </code>
     * </pre>
     *
     * @param listener
     */

    public void registerNavigationListener(SpreoNavigationListener listener) {
        super.registerNavigationListener(listener);
    }

    /**
     * Unregister from the navigation events.
     *
     * @param listener
     */

    public void unregisterNavigationListener(SpreoNavigationListener listener) {
        super.unregisterNavigationListener(listener);
    }

    /**
     * redraw pois on the map
     */

    public void reDrawPois() {
        super.reDrawPois();
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
     * Closes the bubble of the poi
     *
     * @param poi
     */

    public void closeBubble(IPoi poi) {
        super.closeBubble(poi);
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
     * Sets parking marker icon
     *
     * @param icon
     */

    public void setMyParkingMarkerIcon(Bitmap icon) {
        super.setMyParkingMarkerIcon(icon);
    }

    /**
     * stop the simulation
     */

    public void stopSimulation() {
        super.stopSimulation();
    }

    /**
     * Checks if there is a saved parking location.
     *
     * @return true if mapView has parking Location. Otherwise, return false.
     */

    public boolean hasParkingLocation() {
        return super.hasParkingLocation();
    }

    /**
     * get the parking location
     *
     * @return ILocation
     */

    public ILocation getParkingLocation() {
        return super.getParkingLocation();
    }

    /**
     * Show all pois
     */
    public void showAllPois() {
        super.showAllPois();
    }

    /**
     * Hide all pois
     */
    public void hideAllPois() {
        super.hideAllPois();
    }


    /**
     * Sets a custom icon for an existing IPoi.
     *
     * @param poi  to be set
     * @param icon to be set
     */
    public void setIconForPoi(IPoi poi, Bitmap icon) {
        super.setIconForPoi(poi, icon);
    }

    /**
     * Sets a custom icon for list of existing IPoi.
     *
     * @param poiList to be set
     * @param icon    to be set
     */
    public void setIconForPoiList(List<IPoi> poiList, Bitmap icon) {
        super.setIconForPoiList(poiList, icon);

    }


    /**
     * Set the colors of background and text for floor picker
     */
    public void setFloorPickerUIOptions(FloorPickerUIOptions uioptions) {
        super.setFloorPickerUIOptions(uioptions);
    }


    /**
     * Return to the default zoom of map
     */
    public void returnToDefaultZoom() {
        super.returnToDefaultZoom();
    }

//	/**
//	 * get the presented facility ID
//	 */
//	public String getPresentedFacilityId() {
//		return super.getPresentedFacilityId();
//	}
//	

    /**
     * sets visible pois by the given pois ids list
     *
     * @param poiIdsList
     */
    public void setVisiblePoisWithIds(List<String> poiIdsList) {
        super.setVisiblePoisWithIds(poiIdsList);
    }

    /**
     * Presents a specific floor.
     *
     * @param FacilityId
     * @param floorId
     */
    public void showFloorWithId(String FacilityId, int floorId) {
        super.showFloorWithId(FacilityId, floorId);
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
     * present the points of the multi poi navigation
     *
     * @param the poi list
     * @param the visited poi list
     */

    public void presentMultiPoiRoute(List<IPoi> poilist, List<IPoi> visitedPoiList) {
        super.presentMultiPoiRoute(poilist, visitedPoiList);
    }

    /**
     * remove the points of the multi poi navigation
     */

    public void removeMultiPoiRoute() {
        super.removeMultiPoiRoute();
    }

    /**
     * sets visible labels by the given labels ids list
     *
     * @param the label ids List
     */
    public void setVisibleLabelsWithIds(List<String> labelIdsList) {
        super.setVisibleLabelsWithIds(labelIdsList);
    }

    /**
     * show all labels
     */
    public void showAllLabels() {
        super.showAllLabels();
    }

    /**
     * Hide all labels
     */
    public void hideAllLabels() {
        super.hideAllLabels();
    }

    /**
     * set style for list of labels
     *
     * @param labelIdsList
     * @param styleOptions
     */
    public void setCustomStyleForLabelsList(List<String> labelIdsList, LabelUIOptions uiOptions) {
        if (labelIdsList == null || uiOptions == null) {
            return;
        }

        String font = uiOptions.getFont();
        int foregroundColor = uiOptions.getForegroundColor();
        int backgroundColor = uiOptions.getBackgroundColor();
        int borderWidth = uiOptions.getBorderWidth();
        int borderColor = uiOptions.getBorderColor();
        int borderRoundCournerPx = uiOptions.getBorderRoundCournerPx();
        boolean fontBold = uiOptions.isFontBold();
        boolean fontItalic = uiOptions.isFontItalic();
        boolean fontUnderline = uiOptions.isFontUnderline();

        super.setCustomStyleForLabelsList(labelIdsList, font, foregroundColor,
                backgroundColor, borderWidth, borderColor,
                borderRoundCournerPx, fontBold, fontItalic,
                fontUnderline);
    }

    /**
     * get the map zoom
     */
    public float getZoom() {
        return super.getZoom();
    }

    /**
     * get the map center point
     */
    public LatLng getCenterPoint() {
        return super.getCenterPoint();
    }

    /**
     * get the map center location
     */
    public ILocation getCenterLocation() {
        return super.getCenterLocation();
    }

    public void setMapBounds(LatLng southwest, LatLng northeast) {
        super.setMapBounds(southwest, northeast);
    }

    public void setMapBounds(String facilityId, float bearing) {
        super.setMapBounds(facilityId, bearing);
    }

    public LatLngBounds getMapBounds() {
        return super.getMapBounds();
    }

    public void setMapBounds(String facilityId) {
        super.setMapBounds(facilityId);
    }

    /**
     * set the user location visibility
     *
     * @param visible
     */
    public void setUserLocationVisibilty(boolean visible) {
        super.setUserLocationVisibilty(visible);
    }

    /**
     * Show location of poi on mapview
     *
     * @param poi
     * @param returnToDefaultZoom
     */
    public void showPoi(IPoi poi, boolean returnToDefaultZoom) {
        super.showPoi(poi, returnToDefaultZoom);
    }

    /**
     * Center mapview to present a location.
     *
     * @param location
     * @param returnToDefaultZoom
     */
    public void presentLocation(ILocation location, boolean returnToDefaultZoom) {
        super.presentLocation(location, returnToDefaultZoom);
    }

    public void reDrawPois(boolean considerZoomLevel) {
        super.reDrawPois(considerZoomLevel);
    }

    /**
     *
     * @return total route distance in meters (by default) or -1d if there are no active route.
     * Use {@link com.spreo.sdk.setting.SettingsProvider#setUseFeetForDistance(boolean) SettingsProvider.setUseFeetForDistance(true)} to retrieve distance in feet units
     *
     * */
    @Override
    public double getRouteDistance() {
        return super.getRouteDistance();
    }


    @Override
    public void addCameraChangeListener(CameraChangeListener listener) {
        super.addCameraChangeListener(listener);
    }

    @Override
    public void removeCameraChangeListener(CameraChangeListener listener) {
        super.removeCameraChangeListener(listener);
    }

    public List<INavInstruction> getInstructionsList(){
        return super.getInstructionsList();
    }

    public double getRouteLength(){
        return super.getRouteLength();
    }

}
