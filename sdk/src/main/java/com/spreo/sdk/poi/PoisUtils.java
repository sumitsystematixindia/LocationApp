package com.spreo.sdk.poi;

import android.graphics.Bitmap;

import com.mlins.locationutils.LocationFinder;
import com.mlins.nav.utils.DualMapOrderPoiUtil;
import com.mlins.nav.utils.OrederPoisUtil;
import com.mlins.project.ProjectConf;
import com.mlins.res.setup.GalleryListener;
import com.mlins.res.setup.PoiGalleryUpdater;
import com.mlins.utils.PoiDataHelper;
import com.mlins.utils.PoiType;
import com.mlins.utils.PropertyHolder;
import com.mlins.utils.SortingPoiUtil;
import com.mlins.utils.distance.Distance;
import com.mlins.utils.distance.RoughRouteCalculator;
import com.mlins.utils.gis.Location;
import com.mlins.utils.sort.FloorHelper;
import com.spreo.nav.enums.LocationMode;
import com.spreo.nav.interfaces.ILocation;
import com.spreo.nav.interfaces.IPoi;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PoisUtils {

    /**
     * Returns new list with the pois which are belong to specified categories.
     *
     * @param poilist - any list with the pois
     * @param categories - list of categories you are interested in (case-insensitive)
     * @return new list of pois which are belong to specified categories
     */
    public static List<IPoi> getPoisWithCategories(List<IPoi> poilist, List<String> categories) {
        List<String> categoriesInLowerCase = new ArrayList<>();
        for (String category : categories) {
            categoriesInLowerCase.add(category != null ? category.toLowerCase() : null);
        }

        List<IPoi> result = new ArrayList<>();
        for (IPoi poi : poilist) {
            for (String category : poi.getPoitype()) {
                if (categoriesInLowerCase.contains(category != null ? category.toLowerCase() : null)) {
                    result.add(poi);
                    break;
                }
            }

        }
        return result;
    }

    /**
     * Gets all pois of a specific floor of a facility.
     *
     * @param campusId   - - the campus id object which contains the id of the campus
     * @param facilityId - the id of the facility
     * @param floor      - the number of floor
     * @return list of pois objects
     */
    public static List<IPoi> getAllFloorPoisList(String campusId,
                                                 String facilityId, int floor) {

        PropertyHolder.getInstance().setCampusId(campusId);
        PropertyHolder.getInstance().setFacilityID(facilityId);

        // XX ConfigurationUtils.getInstance().loadFacility(campusId.getId(),
        // facilityId.getId());
//		SpreoResourceConfigsUtils.loadFacility(campusId, facilityId);
//		return PoiDataHelper.getInstance().getAllPoiOfFloorAsList(floor);

        return ProjectConf.getInstance().getAllFloorPoisList(campusId, facilityId, floor);
    }

    /**
     * Gets all pois of a facility
     *
     * @param campusId   - the id of the campus
     * @param facilityId - the id of the facility
     * @return list of pois objects
     */
    public static List<IPoi> getAllFacilityPoisList(String campusId,
                                                    String facilityId) {
        PropertyHolder.getInstance().setCampusId(campusId);
        PropertyHolder.getInstance().setFacilityID(facilityId);
        // XXX ConfigurationUtils.getInstance().loadFacility(campusId.getId(),
        // facilityId.getId());
        //	ConfigsLoader.getInstance().loadFacility(campusId, facilityId);
        //	return PoiDataHelper.getInstance().getAllPoiAsList();

        return ProjectConf.getInstance().getAllFacilityPoisList(campusId, facilityId);
    }

    /**
     * Gets poi list sorted in Alphabetical order
     *
     * @param poiList - the poi list for the sorting
     * @return list of pois objects
     */

    public static List<IPoi> getPoiListSortedAlphabetical(List<IPoi> poiList) {
        return SortingPoiUtil.getPoisSortedAlphabetical(poiList);
    }

    /**
     * Gets poi list sorted by distance from a specific location
     *
     * @param poiList - the poi list for the sorting
     * @param loc     - the location for the sorting
     * @return list of pois objects
     */

    public static List<IPoi> getPoiListSortedByLocation(List<IPoi> poiList,
                                                        ILocation loc) {
        if (loc == null) {
            loc = LocationFinder.getInstance().getCurrentLocation();
        }
        return SortingPoiUtil.getPoisSortedByLocation(poiList, loc);
    }

    /**
     * Gets all pois under the campus level
     *
     * @param campusId - the id of the campus
     * @return list of pois objects
     */
    public static List<IPoi> getAllCampusPoisList(String campusId) {

        // return ProjectConf.getInstance().getAllPoisList();
        return ProjectConf.getInstance().getAllCampusPoisList(campusId);

    }

    /**
     * Gets all pois categories.
     *
     * @return list of poi categories
     */
    public static List<PoiCategory> getPoiCategoies() {
        //List<PoiType> types = PoiDataHelper.getInstance().getPoiCategories();
        List<PoiType> types = ProjectConf.getInstance().getPoiCategories();
        List<PoiCategory> result = new ArrayList<PoiCategory>();
        for (PoiType o : types) {
            PoiCategory category = new PoiCategory(o.getPoiuri(),
                    o.getPoitype(), o.getPoidescription(), o.getShowInCatgories(), o.getShowInMapFilter());
            result.add(category);
        }
        return result;
    }

    /**
     * Set the list of the visible categories.
     *
     * @param categories to be visible
     */
    public static void setPoiCategoriesVisible(List<PoiCategory> categories) {
        List<String> categoriesnames = new ArrayList<String>();
        for (PoiCategory o : categories) {
            categoriesnames.add(o.getPoitype());
        }
        PoiDataHelper.getInstance().setVisbleCategories(categoriesnames);
        ProjectConf.getInstance().setVisiblePoisCategories(categoriesnames);
    }

    /**
     * set all pois categories to be visible on map
     */
    public static void setAllPoisCategoriesVisible() {
        List<PoiCategory> allcategories = PoisUtils.getPoiCategoies();
        List<PoiCategory> visibleategories = new ArrayList<PoiCategory>();
        visibleategories.addAll(allcategories);
        PoisUtils.setPoiCategoriesVisible(visibleategories);
    }

    /**
     * set all pois categories to be hidden on map
     */
    public static void setAllPoisCategoriesHidden() {
        List<PoiCategory> visibleategories = new ArrayList<PoiCategory>(); // no category
        PoisUtils.setPoiCategoriesVisible(visibleategories);
    }


    /**
     * Adds custom pois to facility Note: when adding custom pois, if needed,
     * redraw map using SpreoMapView method called reDrawPois
     * {@link com.spreo.sdk.view.SpreoMapView#reDrawPois}
     *
     * @param poiList
     */
    public static void addPoiList(List<IPoi> poiList) {
        PoiDataHelper.getInstance().addUserPois(poiList);
    }

    /**
     * Removes custom pois from facility
     *
     * @param poiList
     */
    public static void removePoiList(List<IPoi> poiList) {
        PoiDataHelper.getInstance().removeUserPois(poiList);
    }

    /**
     * Sets a custom icon for an existing IPoi.
     *
     * @param poi  to be set
     * @param icon to be set
     */
    public static void setIconForPoi(IPoi poi, Bitmap icon) {
        if (poi != null) {
            poi.setIcon(icon);
        }
    }

    /**
     * Sets a custom icon for list of existing IPoi.
     *
     * @param poiList to be set
     * @param icon    to be set
     */
    public static void setIconForPoiList(List<IPoi> poiList, Bitmap icon) {
        if (poiList != null) {
            for (IPoi poi : poiList) {
                setIconForPoi(poi, icon);
            }
        }

    }

    /**
     * update gallery for a IPoi.
     *
     * @param poi
     * @param listener
     */
    public static void updateIPoiGallery(IPoi poi, GalleryListener listener) {
        PoiGalleryUpdater.getInstance().downloadGallery(poi, listener);
    }

    public static void updateIPoiHeadImage(IPoi poi, GalleryListener listener) {
        PoiGalleryUpdater.getInstance().downloadHeadImage(poi, listener);
    }

    /**
     * unregister from gallery updater
     *
     * @param listener
     * @return true if succeed otherwise return false
     */
    public static boolean unregisterFromGalleryUpdater(GalleryListener listener) {
        return PoiGalleryUpdater.getInstance().unregisterListener(listener);
    }

    /**
     * get the poi list ordered for navigation
     *
     * @param poilist to order
     * @return the ordered poi list
     */

    public static List<IPoi> getPoisOrederForNavigation(List<IPoi> poilist) {
        List<IPoi> result = new ArrayList<IPoi>();
        try {
            result = OrederPoisUtil.getPoisOrederForNavigation(poilist);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (result == null) {
            result = poilist;
        }

        return result;

    }

    public static IPoi getPoiById(String poiId) {
        List<IPoi> pois = ProjectConf.getInstance().getAllPoisList();
        for (IPoi poi : pois) {
            if (poi == null) {
                continue;
            }
            String poid = poi.getPoiID();
            if (poid != null) {
                if (poid.equals(poiId)) {
                    return poi;
                }
            }
        }

        return null;
    }

    /**
     * get the poi list for navigation without the order
     *
     * @param poilist
     * @return the poi list with the switch floors and parking
     */
    public static List<IPoi> getPoisForNavigation(List<IPoi> poilist) {
        List<IPoi> result = new ArrayList<IPoi>();
        try {
            result = OrederPoisUtil.getPoisOrederForNavigation(poilist, false);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (result == null) {
            result = poilist;
        }

        return result;

    }


    /**
     * get the poi list ordered for navigation
     *
     * @param poilist to order
     * @return the ordered poi list
     */
    public static List<IPoi> getAllPoisOrderForNavigation(List<IPoi> poilist) {

        List<IPoi> result = new ArrayList<IPoi>();

        try {

            // get entrances and exits
            List<IPoi> exitsList = new ArrayList<IPoi>();
            List<IPoi> pois = ProjectConf.getInstance().getAllPoisList();
            for (IPoi poi : pois) {
                if (poi == null) {
                    continue;
                }
                String id = poi.getPoiID();
                if (id != null && id.startsWith("idr") && "internal".equals(poi.getPoiNavigationType())) {
                    exitsList.add(poi);
                }
            }

            if (exitsList.size() == 0) {
                return poilist;
            }

            // try the new method
            boolean order = true;
            result = OrederPoisUtil.getMixedPoisOrderForNavigation(poilist, exitsList, order);

        } catch (Throwable t) {
            t.printStackTrace();
        }

        if (result == null || result.size() == 0) {
            result = poilist;
        }

        return result;
    }

    /**
     * get the poi list for navigation
     *
     * @param poilist
     * @param order
     * @return the ordered poi list
     */
    public static List<IPoi> getDualMapPoisOrder(List<IPoi> poilist, boolean order) {
        return DualMapOrderPoiUtil.getDualMapPoisOrder(poilist, order);
    }

    public static IPoi getClosestParking (IPoi poi) {
        return ProjectConf.getInstance().getClosestParking(poi);
    }

	public static List<PoiDistance> getDistanceToPoiList(ILocation location, List<IPoi> pois, boolean sort){
		ArrayList<PoiDistance> result = new ArrayList<>();
		for (IPoi poi : pois) {
			result.add(new PoiDistance(poi, getDistanceToPoi(location, poi)));
		}
		if(sort)
		    Collections.sort(result);
        return result;
	}

	public static double getDistanceToPoi(ILocation location, IPoi poi){
        List<ILocation> routePoints = RoughRouteCalculator.getRoutePoints(location, new Location(poi));
		return Distance.toProjectUnits(getTotalDistance(routePoints));
	}

	private static double getTotalDistance(List<ILocation> routePoints){
        double totalDistance = 0;

        ILocation previous = null;
        for (ILocation current : routePoints) {
            if(previous != null //not first iteration
                    && (previous.getLocationType() == current.getLocationType()) ) { //calculating distance only between locations of the same type

                double segmentDistance = 0;

                if(previous.getLocationType() == LocationMode.INDOOR_MODE) {
                    if(Location.inTheSameFacility(previous, current) && Location.onTheSameFloor(previous, current)) {
                        segmentDistance = FloorHelper.getDistance(previous, current);
                    }
                } else {
                    segmentDistance = Location.getOutDoorDistance(previous, current);
                }

                totalDistance += segmentDistance;
            }
            previous = current;
        }

        return totalDistance;
    }
}
