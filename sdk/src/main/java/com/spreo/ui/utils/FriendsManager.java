package com.spreo.ui.utils;

import android.graphics.Bitmap;

import com.google.android.gms.maps.model.LatLng;
import com.mlins.custom.markers.CustomMarkersManager;
import com.mlins.utils.PropertyHolder;
import com.spreo.interfaces.ICustomMarker;
import com.spreo.nav.enums.LocationMode;
import com.spreo.nav.interfaces.ILocation;
import com.spreo.sdk.view.SpreoDualMapView;

import java.util.ArrayList;
import java.util.List;

public class FriendsManager {

    public static void updateFriend(String id, Bitmap icon, ILocation location, String name) {
        removeFriend(id);
        CustomMarkersManager.getInstance().addCustomMarker(new FriendLocationMarker(id, icon, location, name));
    }
    
    public static void updateFriendsOnMap(SpreoDualMapView dualMapView){
        dualMapView.setCustomMarkers(CustomMarkersManager.getInstance().getCustomMarkers());
    }
    
    public static void removeAllFriends(){
        List<ICustomMarker> markersToRemove = new ArrayList<>();

        CustomMarkersManager customMarkerManager = CustomMarkersManager.getInstance();
        List<ICustomMarker> customMarkers = customMarkerManager.getCustomMarkers();
        for (ICustomMarker marker : customMarkers) {
            if(isFriendMarker(marker)) {
                markersToRemove.add(marker);
            }
        }

        customMarkerManager.removeCustomMarkers(markersToRemove);
    }
    
    public static void removeFriend(String id){
        FriendLocationMarker marker = getFriendMarker(id);
        if(marker != null)
            CustomMarkersManager.getInstance().removeCustomMarker(marker);
    }

    public static Friend getFriend(String id) {
        FriendLocationMarker marker = getFriendMarker(id);
        return marker != null ? new Friend(marker.systemID, marker.icon, marker.location, marker.name) : null;
    }

    private static FriendLocationMarker getFriendMarker(String id){
        List<ICustomMarker> customMarkers = CustomMarkersManager.getInstance().getCustomMarkers();
        for (ICustomMarker marker : customMarkers) {
            if(isFriendMarker(marker)) {
                FriendLocationMarker friendMarker = (FriendLocationMarker) marker;
                if(friendMarker.hasSameSystemID(id))
                    return friendMarker;
            }
        }
        return null;
    }

    private static boolean isFriendMarker(ICustomMarker marker) {
        return marker instanceof FriendLocationMarker;
    }
    
    public static class Friend {

        public final String id;
        public final Bitmap icon;
        public final ILocation location;
        public final String name;

        public Friend(String id, Bitmap icon, ILocation location, String name) {
            this.id = id;
            this.icon = icon;
            this.location = location;
            this.name = name;
        }
    }

    private static class FriendLocationMarker implements ICustomMarker {

        private final String systemID;
        public final ILocation location;
        private final Bitmap icon;
        private final String name;
        
        FriendLocationMarker(String id, Bitmap icon, ILocation location, String name) {
            this.name = name;
            if(id == null || icon == null || location == null)
                throw new NullPointerException("Friend: id == null || icon == null || location == null");
            this.location = location;
            this.icon = icon;
            systemID = id;
        }

        @Override
        public void SetIcon(Bitmap icon) {}

        @Override
        public String getId() {
            return name;
        }

        @Override
        public void setId(String Id) {}

        @Override
        public String getProjectId() {
            return PropertyHolder.getInstance().getProjectId();
        }

        @Override
        public void setProjectId(String projectId) {}

        @Override
        public String getCampusId() {
            return location.getCampusId();
        }

        @Override
        public void setCampusId(String campusId) {}

        @Override
        public String getFacilityId() {
            return location.getFacilityId();
        }

        @Override
        public void setFacilityId(String facilityId) {}

        @Override
        public LocationMode getLocationMode() {
            return location.getLocationType();
        }

        @Override
        public void setLocationMode(LocationMode mode) {}

        @Override
        public float getX() {
            return (float) location.getX();
        }

        @Override
        public void setX(float x) {}

        @Override
        public float getY() {
            return (float) location.getY();
        }

        @Override
        public void setY(float y) {}

        @Override
        public int getFloor() {
            return (int) location.getZ();
        }

        @Override
        public void setFloor(int floor) {}

        @Override
        public LatLng getLatLng() {
            return new LatLng(location.getLat(), location.getLon());
        }

        @Override
        public void setLatLng(LatLng latlng) {}

        @Override
        public Bitmap getIcon() {
            return icon;
        }

        boolean hasSameSystemID(String otherID){
            return systemID.equals(otherID);
        }
    }

}