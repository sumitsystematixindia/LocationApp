package com.mlins.utils;

import com.google.android.gms.maps.model.LatLng;
import com.mlins.project.ProjectConf;
import com.mlins.utils.gis.Location;
import com.mlins.utils.sort.FacilityHelper;
import com.mlins.utils.sort.FacilityItem;
import com.mlins.utils.sort.PoiItem;
import com.mlins.utils.sort.SortResultItem;
import com.spreo.nav.interfaces.ILocation;
import com.spreo.nav.interfaces.IPoi;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class SortingPoiUtil {

    /**
     * Gets poi list sorted in Alphabetical order
     *
     * @param poistosort - the poi list for the sorting
     * @return list of pois objects
     */
    public static List<IPoi> getPoisSortedAlphabetical(List<IPoi> poistosort) {
        List<IPoi> result = new ArrayList<IPoi>();
        result.addAll(poistosort);
        Collections.sort(result, new Comparator<IPoi>() {
            @Override
            public int compare(IPoi p1, IPoi p2) {
                String s1 = p1.getpoiDescription();
                String s2 = p2.getpoiDescription();
                return s1.compareToIgnoreCase(s2);
            }
        });

        return result;
    }

    public static List<IPoi> getPoisSortedByLocation(List<IPoi> pois, ILocation loc) {

        try {
            FacilityHelper.PoisSplitResult splitResult
                    = FacilityHelper.splitByFacilities(ProjectConf.getInstance().getSelectedCampus().getId(), pois);

            FacilityHelper userFacility = null;

            if(Location.isInDoor(loc) ) {
                userFacility = splitResult.facilities.remove(loc.getFacilityId());
            }

            return getPoisSortedByLocation(
                    loc,
                    userFacility,
                    new ArrayList<>(splitResult.facilities.values()),
                    splitResult.outdoorPois);
        } catch (Throwable t) {
            return pois;
        }
    }

    public static List<IPoi> getPoisSortedByLocation(ILocation userLocation, FacilityHelper userFacility, List<FacilityHelper> otherFacilities, List<IPoi> outdoorPois) {
        if(userFacility != null)
            Location.ensureInDoor(userLocation);

        List<SortResultItem> sortItems = new ArrayList<>();

        LatLng userOutdoorLocation = Location.isOutDoor(userLocation) ? Location.getLatLng(userLocation) : new FacilityHelper(userLocation).getCenter();

        if(userFacility != null) {
            sortItems.add(
                    new FacilityItem(
                            userFacility,
                            userLocation,
                            Location.getLatLng(userLocation)
                    )
            );
        }

        if(outdoorPois != null) {
            for (IPoi outdoorPoi : outdoorPois) {
                sortItems.add(new PoiItem(outdoorPoi, userOutdoorLocation));
            }
        }

        if(otherFacilities != null) {
            for (FacilityHelper facility : otherFacilities) {
                IPoi closestExit = facility.getExitClosestTo(userOutdoorLocation);
                ILocation facloc = null;
                if (closestExit != null) {
                    facloc = closestExit.getLocation();
                } else {
                    facloc = new Location(facility.getCenter());
                }
                if(facloc != null) {
                    sortItems.add(
                            new FacilityItem(
                                    facility,
                                    facloc,
                                    userOutdoorLocation
                            )
                    );
                }
            }
        }

        Collections.sort(sortItems);

        List<IPoi> result = new ArrayList<>();
        for (SortResultItem sortItem : sortItems) {
            sortItem.addToResultsList(result);
        }

        return result;
    }

}
