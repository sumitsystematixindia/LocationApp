package com.spreo.sdk.setting;


import android.graphics.Bitmap;

import com.mlins.labels.LabelsContainer;
import com.mlins.nav.location.sharing.LocationSharingManager;
import com.mlins.nav.location.sharing.ReportLimitation;
import com.mlins.nav.utils.SoundPlayer;
import com.mlins.project.Campus;
import com.mlins.project.ProjectConf;
import com.mlins.project.bridges.BridgeSelectionType;
import com.mlins.recorder.WlBlipsRecorder;
import com.mlins.utils.FacilityConf;
import com.mlins.utils.PropertyHolder;
import com.spreo.nav.enums.ExitsSelectionType;
import com.spreo.nav.enums.LayerType;
import com.spreo.nav.enums.MapRotationType;
import com.spreo.nav.enums.NavigationType;
import com.spreo.nav.enums.SwitchFloorSelectionType;

import java.util.Map;

/**
 * This class handle the setting parameters of the system
 *
 * @author Spreo
 */
public class SettingsProvider {


    private static SettingsProvider instance = null;

    /**
     * Retrieves the singleton instance
     *
     * @return the singleton instance
     */
    //no need to rework, just a wrapper
    public static SettingsProvider getInstance() {
        if (instance == null) {
            instance = new SettingsProvider();
        }
        return instance;
    }


    /**
     * Sets true to show campaign banners. Otherwise, set to false.
     *
     * @param isShow
     */
    public void showCampaignBanners(boolean isShow) {
        PropertyHolder.getInstance().setBannersShow(isShow);
        PropertyHolder.getInstance().saveGlobals();
    }

    /**
     * Gets show campaign banners state
     *
     * @return campaign banners state
     */
    public boolean isShowCampaignBanners() {
        return PropertyHolder.getInstance().isBannersShow();
    }

    /**
     * Sets the map rotation type {@link com.spreo.nav.enums.MapRotationType}.
     *
     * @param rotationType
     */
    public void setMapRotation(MapRotationType rotationType) {
        PropertyHolder.getInstance().setRotatingMapType(rotationType);
    }

    /**
     * Gets the map rotation type
     *
     * @return map rotation type
     */
    public MapRotationType getMapRotationType() {
        return PropertyHolder.getInstance().getRotatingMapType();

    }


    /**
     * Sets user auto follow time interval (in milliseconds).
     * After the specified time interval the state of auto follow will be reset
     *
     * @param intervalInMilliseconds
     */
    public void setUserAutoFollowTimeInterval(long intervalInMilliseconds) {
        PropertyHolder.getInstance().setUserAutoFollowTimeInterval(intervalInMilliseconds);
    }

	/*
	 * sets user auto follow time interval (in milliseconds)
	 * @param radius
	 
	public void setPoiRegionRadius(float radius) {
		PropertyHolder.getInstance().setBubbleRange(radius);
	}
	*/


//	/**
//	 * 
//	 * @param isAuto
//	 */
//	public void autoSelectFacility(boolean isAuto){
//		PropertyHolder.getInstance().setAutoSelectFacility(isAuto);
//	}
//	
//	/**
//	 * 
//	 * @return
//	 */
//	public boolean isAutoSelectFacility(){
//		return PropertyHolder.getInstance().isAutoSelectFacility();
//	}

//	/**
//	 * 
//	 * @param isAuto
//	 */
//	public void autoSelectFloor(boolean isAuto){
//		PropertyHolder.getInstance().setAutoSelectFloor(isAuto);
//	}

//	/**
//	 * 
//	 * @return
//	 */
//	public boolean isAutoSelectFloor(){
//		return PropertyHolder.getInstance().isAutoSelectFloor();
//	}


    /**
     * Set a user ID for the analytics.
     *
     * @param id
     */
    public void setAnalyticsUserId(String id) {
        if (id != null && !id.isEmpty()) {
            WlBlipsRecorder.getInstance().setSessionId(id);
        }
    }

    /**
     * Set the time (in milliseconds) interval for the analytics report.
     *
     * @param time
     */
    public void setAnalyticsInterval(long time) {
        LocationSharingManager.getInstance().setInterval(time);
    }


    public void setLocationReportLimitation(ReportLimitation type) {
        LocationSharingManager.getInstance().setLocationReportLimitation(type);
    }

    public ReportLimitation getLocationReportLimitation(){
        return LocationSharingManager.getInstance().getLocationReportLimitation();
    }

    public void setUseFeetForDistance(boolean useFootForDistance) {
        PropertyHolder.getInstance().setUseFeetForDistance(useFootForDistance);
    }

//	public void setApiKey(Context context, String apikey, ApiKeyResponseListener apiKeyResponseListener) {
//		ConfigurationUtils.getInstance().setApiKey(context, apikey, apiKeyResponseListener);
//		
//	}

    /**
     * Set the type of Google map.
     * <pre>
     *  example:
     * <code>
     * 			int mapType = GoogleMap.MAP_TYPE_NORMAL; (use the Google map types)
     * 			SettingsProvider.getInstance().setMapType(mapType);
     * </code>
     * </pre>
     *
     * @param mapType
     */
    public void setMapType(int mapType) {
        PropertyHolder.getInstance().setMapType(mapType);
    }

    /**
     * Gets the The hexadecimal color of the route of navigation path
     *
     * @return the The hexadecimal color of the route of navigation path
     */
    public String getRouteColor() {
        return PropertyHolder.getInstance().getNavRouteColor();
    }

    /**
     * Sets the color of the route of navigation path
     *
     * @param routeHexColor - the The hexadecimal color of the route of navigation path
     */
    public void setRouteColor(String routeHexColor) {
        PropertyHolder.getInstance().setNavRouteColor(routeHexColor);
    }

    /**
     * Sets the state of a specified layer on map
     *
     * @param layerType the type of layer
     * @param display   - display of layer on/off
     */
    public void setLayerOnMap(LayerType layerType, boolean display) {
        String layer = null;
        if (layerType == LayerType.POI_BUBLES) {
            layer = "buble";
        } else if (layerType == LayerType.LABLES) {
            layer = "labels_layer";
        } else if (layerType == LayerType.NAVIGATION_PATHS) {
            layer = "path";
            PropertyHolder.getInstance().setLayerOnMap("instructions", display);

        } else if (layerType == LayerType.POI_ICONS) {
            layer = "poi";
        } else if (layerType == LayerType.LOCATION_SHARING) {
            layer = "social";
        }

        PropertyHolder.getInstance().setLayerOnMap(layer, display);
    }

    /**
     * @return poi bubble bitmap
     */
    public Bitmap getPoiBubbleIcon() {
        return PropertyHolder.getInstance().getPoiBubbleIcon();
    }

    /**
     * sets the poi bubble icon bitmap
     *
     * @param bitmap
     */
    public void setPoiBubbleIcon(Bitmap bitmap) {
        PropertyHolder.getInstance().setPoiBubbleIcon(bitmap);
    }

    /**
     * gets the navigation instructions display state
     *
     * @return the navigation instructions display state
     */
    public boolean isDisplayNavigationInstructionsOnMap() {
        return PropertyHolder.getInstance().isDisplayNavigationInstructionsOnMap();
    }

    /**
     * sets whether to display the navigation instructions on map or not
     *
     * @param display
     */
    public void setDisplayNavigationInstructionsOnMap(boolean display) {
        PropertyHolder.getInstance().setDisplayNavigationInstructionsOnMap(display);
    }

    /**
     * Gets the sound of the instructions state
     *
     * @return the sound of the instructions state
     */
    public boolean isNavigationInstructionsSoundMute() {
        return PropertyHolder.getInstance().isNavigationInstructionsSoundMute();
    }

    /**
     * Sets whether to mute the sound of the instructions on navigation
     *
     * @param mute - default mute is equals to false i.e the sound is played
     */
    public void setNavigationInstructionsSoundMute(boolean mute) {
        PropertyHolder.getInstance().setNavigationInstructionsSoundMute(mute);
    }

    /**
     * Gets how many poi bubbles will automatically opened near user location
     */
    public int getPoiBubblesCount() {
        return PropertyHolder.getInstance().getPoiBubblesCount();
    }

    /**
     * Sets how many poi bubbles will automatically opened near user location
     *
     * @param poiBubblesCount
     */
    public void setPoiBubblesCount(int poiBubblesCount) {
        PropertyHolder.getInstance().setPoiBubblesCount(poiBubblesCount);
    }

    /**
     * Set navigation type. The chosen type will be preferred on navigation route. See {@link com.spreo.nav.enums.NavigationType}.
     *
     * @param navigationType
     */
    public void setNavigationType(NavigationType navigationType) {
        PropertyHolder.getInstance().setNavigationType(navigationType);
    }

    /**
     * enable / disable user analytics
     *
     * @param analyticsEnable
     */
    public void setUserAnalyticsEnable(boolean analyticsEnable) {
        LocationSharingManager locationSharingManager = LocationSharingManager.getInstance();
        if(analyticsEnable)
            locationSharingManager.start();
        else
            locationSharingManager.stop();
    }

    public boolean isUserAnalyticsEnabled(){
        return LocationSharingManager.getInstance().isStarted();
    }

    /**
     * @return true if the parking is included in the multiPois. Otherwise, return false
     */
    public boolean isIncludeParkingToMultiPois() {
        return PropertyHolder.getInstance().isAddParkingToMultiPois();
    }

    /**
     * set whether to include the parking in the multiPois or not
     *
     * @param include
     */
    public void setIncludeParkingToMultiPois(boolean include) {
        PropertyHolder.getInstance().setAddParkingToMultiPois(include);
    }


    /**
     * @return true if the entrance is included in the multiPois. Otherwise, return false
     */
    public boolean isIncludeEntrancesToMultiPois() {
        return PropertyHolder.getInstance().isAddEntranceToMultiPois();
    }


    /**
     * set whether to include the entrance in the multiPois or not
     *
     * @param include
     */
    public void setIncludeEntrancesToMultiPois(boolean include) {
        PropertyHolder.getInstance().setAddEntranceToMultiPois(include);
    }


    /**
     * @return true if the switch floors is included in the multiPois. Otherwise, return false
     */
    public boolean isIncludeSwitchFloorsToMultiPois() {
        return PropertyHolder.getInstance().isAddSwitchFloorsToMultiPois();
    }


    /**
     * set whether to include the switch floors in the multiPois or not
     *
     * @param include
     */
    public void setIncludeSwitchFloorsToMultiPois(boolean include) {
        PropertyHolder.getInstance().setAddSwitchFloorsToMultiPois(include);
    }


    /**
     * sets the options of GUI of multi-Poi
     *
     * @param options
     */
    public void setMultiPoiUIOptions(MultiPoiUIOptions options) {

        boolean includeEntrances = options.isIncludeEntrances();
        boolean includeSwitchFloors = options.isIncludeSwitchFloors();
        String multiPoisPointColor = options.getMultiPoisPointColor();
        String multiPoisVisitedPointColor = options.getMultiPoisvisitedPointColor();
        String multiPoisPointNumberColor = options.getMultiPoisPointNumberColor();
        String multiPoisVisitedPointNumberColor = options.getMultiPoisVisitedPointNumberColor();
        Bitmap iconForMultiPointExit = options.getIconForMultiPointExit();
        Bitmap iconForMultiPointSwitchFloor = options.getIconForMultiPointSwitchFloor();

        PropertyHolder.getInstance().setIncludeEntrancesInPoisNumbering(includeEntrances);
        PropertyHolder.getInstance().setIncludeSwitchFloorsInPoisNumbering(includeSwitchFloors);
        if (multiPoisPointColor != null && !multiPoisPointColor.isEmpty()) {
            PropertyHolder.getInstance().setMultiPoisPointColor(multiPoisPointColor);
        }
        if (multiPoisVisitedPointColor != null && !multiPoisVisitedPointColor.isEmpty()) {
            PropertyHolder.getInstance().setMultiPoisVisitedPointColor(multiPoisVisitedPointColor);
        }
        if (multiPoisPointNumberColor != null && !multiPoisPointNumberColor.isEmpty()) {
            PropertyHolder.getInstance().setMultiPoisPointNumberColor(multiPoisPointNumberColor);
        }
        if (multiPoisVisitedPointNumberColor != null && !multiPoisVisitedPointNumberColor.isEmpty()) {
            PropertyHolder.getInstance().setMultiPoisVisitedPointNumberColor(multiPoisVisitedPointNumberColor);
        }

        PropertyHolder.getInstance().setIconForMultiPointExit(iconForMultiPointExit);
        PropertyHolder.getInstance().setIconForMultiPointSwitchFloor(iconForMultiPointSwitchFloor);

    }

    /**
     * Sets the resources-server connection url
     *
     * @param url
     */
    public void setServerConnectionUrl(String url) {
        PropertyHolder.getInstance().setServerName(url);
    }

    /**
     * get the SpreoMapView default zoom level
     */
    public float getDefaultMapZoom() {
        return PropertyHolder.getInstance().getDefaultMapZoom();
    }

    /**
     * Sets the SpreoMapView default zoom level
     *
     * @param zoom level
     */
    public void setDefaultMapZoom(float zoom) {
        PropertyHolder.getInstance().setDefaultMapZoom(zoom);
    }

    /**
     * set whether to present the destination icon
     *
     * @param present
     */
    public void setPresentDestinationIcon(boolean present) {
        PropertyHolder.getInstance().setPresentDestinationIcon(present);
    }

    /**
     * Gets alpha value of the poi bubble.
     */
    public int getPoiBubbleTransparentLevel() {
        return PropertyHolder.getInstance().getPoiBubbleTransparentLevel();
    }

    /**
     * Specify an alpha value for the poi bubble. 0 means fully transparent, and 255 means fully opaque.
     *
     * @param alpha
     */
    public void setPoiBubbleTransparentLevel(int alpha) {
        PropertyHolder.getInstance().setPoiBubbleTransparentLevel(alpha);
    }

    /**
     * @return get the language
     */
    public String getAppLanguage() {
        return PropertyHolder.getInstance().getAppLanguage();
    }

    /**
     * set the language
     *
     * @param language
     */
    public void setAppLanguage(String language) {
        PropertyHolder.getInstance().setAppLanguage(language);
        SoundPlayer.releaseInstance();
        PropertyHolder.getInstance().setTrasnlateLabels(true);
    }

    /**
     * set scale factor for the poi bubble size
     *
     * @param size
     */
    public void setPoiBubbleSize(float size) {
        PropertyHolder.getInstance().setPoiBubbleScaleFactor(size);
    }

    /**
     * lock the app on a spesific facility
     *
     * @param campusID
     * @param facilityId
     */
    public void lockOnFacility(String campusID, String facilityId) {
        if (facilityId != null) {
            Campus campus = ProjectConf.getInstance().getSelectedCampus();
            Map<String, FacilityConf> facilitiesmap = campus
                    .getFacilitiesConfMap();
            if (facilitiesmap.containsKey(facilityId)) {
                PropertyHolder.getInstance().setLockedFacility(facilityId);
                PropertyHolder.getInstance().setLockedOnFacility(true);
            }
        }
    }

    /**
     * unlock the facility
     */
    public void unLockFacility() {
        PropertyHolder.getInstance().setLockedFacility(null);
        PropertyHolder.getInstance().setLockedOnFacility(false);
    }

    /**
     * Stops simulation mode
     */

    public void setSimulationOff() {
        PropertyHolder.getInstance().setLocationPlayer(false);

    }

    /**
     * sets the default zoom for Spreo dual map
     *
     * @param zoom
     */
    public void setDualMapDefaultZoom(float zoom) {
        PropertyHolder.getInstance().setDefaultDualMapZoom(zoom);
    }

    /**
     * sets the visibility of the floor picker. call this method before you initialize the SpreoDualMapView.
     *
     * @param visible
     */

    public void setFloorPickerVisibility(boolean visible) {
        PropertyHolder.getInstance().setFloorPickerVisibile(visible);
    }

    public ExitsSelectionType getExitsSelectionType() {
        return PropertyHolder.getInstance().getExitsSelectionType();
    }

    public void setExitsSelectionType(ExitsSelectionType type) {
        PropertyHolder.getInstance().setExitsSelectionType(type);
    }

    /**
     * set the status of the simplified instruction.
     */
    public void setSimplifiedInstruction(Boolean isSimplified) {
        PropertyHolder.getInstance().setSimplifiedInstruction(isSimplified);
    }

    /**
     * get the zoom level for hiding pois with false on the show on all zoom levels attribute.
     */
    public float getHidingPoisZoomLevel() {
        return PropertyHolder.getInstance().getHidingPoisZoomLevel();
    }

    /**
     * set the zoom level for hiding pois with false on the show on all zoom levels attribute.
     */
    public void setHidingPoisZoomLevel(float zoom) {
        PropertyHolder.getInstance().setHidingPoisZoomLevel(zoom);
    }

    public void setNavPathWidth(float width) {
        PropertyHolder.getInstance().setNavPathWidth(width);
    }

    public void setDrawArrowsOnPath(boolean draw) {
        PropertyHolder.getInstance().setDrawArrowsOnPath(draw);
    }

    public void translateLabels() {
        String campusid = PropertyHolder.getInstance().getCampusId();
        if (campusid != null) {
            LabelsContainer labelsContainer = ProjectConf.getInstance().getLabelsContainer();
            if (labelsContainer != null) {
                labelsContainer.translateLabels();
            }
        }
    }

    public boolean isShowNavigationMarkers() {
        return PropertyHolder.getInstance().isShowNavigationMarkers();
    }

    /**
     * Method is deprecated, use
     *
     * {@link com.mlins.dualmap.DualMapView#showOriginMarker(boolean) DualMapView.showOriginMarker()}
     * {@link com.mlins.dualmap.DualMapView#showSwitchFloorMarkers(boolean)} (boolean) DualMapView.showSwitchFloorMarkers()}
     * {@link com.mlins.dualmap.DualMapView#showNavigatedDestinationMarker(boolean)} (boolean) DualMapView.showNavigatedDestinationMarker()}
     *
     * methods instead.
     */
    @Deprecated
    public void setShowNavigationMarkers(boolean showNavigationMarkers) {
        PropertyHolder.getInstance().setShowNavigationMarkers(showNavigationMarkers);
    }

    public void setDisplayLabelsForPois(boolean display){
        PropertyHolder.getInstance().setDisplayLabelsForPOIs(display);
    }

    public void setDisplayDashedNavigationRoute(boolean display) {
        PropertyHolder.getInstance().setDisplayDashedNavigationRoute(display);
    }

    public void setBridgeSelectionType(BridgeSelectionType bridgeSelectionType) {
        PropertyHolder.getInstance().setBridgeSelectionType(bridgeSelectionType);
    }

    public void setUseRotatingUserIcon(boolean useRotatingUserIcon) {
        PropertyHolder.getInstance().setUseRotatingUserIcon(useRotatingUserIcon);
    }

    public void setMapMaxZoomLimit(float mapMaxZoomLimit) {
        PropertyHolder.getInstance().setMapMaxZoomLimit(mapMaxZoomLimit);
    }

    public void setPlayStraightSound(boolean playStraightSound) {
        PropertyHolder.getInstance().setPlayStraightSound(playStraightSound);
    }

    public void setStayIndoor(boolean stayIndoor) {
        PropertyHolder.getInstance().setStayIndoor(stayIndoor);
    }

    public void setMapAnimation(boolean mapAnimation) {
        PropertyHolder.getInstance().setMapAnimation(mapAnimation);
    }

    public void setSwitchFloorSelectionType(SwitchFloorSelectionType switchFloorSelectionType) {
        PropertyHolder.getInstance().setSwitchFloorSelectionType(switchFloorSelectionType);
    }

    public void setUseZipWithoutMaps(boolean useZipWithoutMaps) {
        PropertyHolder.getInstance().setUseZipWithoutMaps(useZipWithoutMaps);
    }

    public void setUseExitPoiRangeForExit(boolean useExitPoiRangeForExit) {
        PropertyHolder.getInstance().setUseExitPoiRangeForExit(useExitPoiRangeForExit);
    }

    public void setTilesServerName(String tilesServerName) {
        PropertyHolder.getInstance().setTilesServerName(tilesServerName);
    }

    public void setChooseShortestRoute(boolean chooseShortestRoute) {
        PropertyHolder.getInstance().setChooseShortestRoute(chooseShortestRoute);
    }

    public boolean isUseTurnBackInstruction() {
        return PropertyHolder.getInstance().isUseTurnBackInstruction();
    }

    public void setUseTurnBackInstruction(boolean useTurnBackInstruction) {
        PropertyHolder.getInstance().setUseTurnBackInstruction(useTurnBackInstruction);
    }

    public void setPoisZoomFiltering(boolean poisZoomFiltering) {
        PropertyHolder.getInstance().setPoisZoomFiltering(poisZoomFiltering);
    }

    public void setDrawInvisibleFloorsRoute(boolean drawInvisibleFloorsRoute) {
        PropertyHolder.getInstance().setDrawInvisibleFloorsRoute(drawInvisibleFloorsRoute);
    }

    public void setClickableDynamicBubbles(boolean clickableDynamicBubbles) {
        PropertyHolder.getInstance().setClickableDynamicBubbles(clickableDynamicBubbles);
    }

    public void setUseBridgeEntranceParameters(boolean useBridgeEntranceParameters) {
        PropertyHolder.getInstance().setUseBridgeEntranceParameters(useBridgeEntranceParameters);
    }

    public void setDrawInvisibleNavMarkers(boolean drawInvisibleNavMarkers) {
        PropertyHolder.getInstance().setDrawInvisibleNavMarkers(drawInvisibleNavMarkers);
    }

    public void setDrawRouteTails(boolean drawRouteTails) {
        PropertyHolder.getInstance().setDrawRouteTails(drawRouteTails);
    }

    public void setPlaySwitchFloorRadius(int playSwitchFloorRadius) {
        PropertyHolder.getInstance().setPlaySwitchFloorRadius(playSwitchFloorRadius);
    }

    public void setVirtualRouteAlpha(float virtualRouteAlpha) {
        PropertyHolder.getInstance().setVirtualRouteAlpha(virtualRouteAlpha);
    }

    public void setDrawGoogleMapsBuildings(boolean drawGoogleMapsBuildings) {
        PropertyHolder.getInstance().setDrawGoogleMapsBuildings(drawGoogleMapsBuildings);
    }

    public void setHandicappedRouting(boolean handicappedRouting) {
        PropertyHolder.getInstance().setHandicappedRouting(handicappedRouting);
    }

    public void setStaffRouting(boolean staffRouting) {
        PropertyHolder.getInstance().setStaffRouting(staffRouting);
    }

    public String getWebInterfaceUrl() {
        return PropertyHolder.getInstance().getWebInterfaceUrl();
    }
}
