package com.mlins.interfaces;

import android.graphics.Bitmap;

import com.spreo.interfaces.SpreoNavigationListener;
import com.spreo.nav.interfaces.ILocation;
import com.spreo.nav.interfaces.IPoi;

public interface ILocationMapView {

    public void navigateTo(IPoi poi);

    public void navigateTo(ILocation destination);

    public void simulateNavigationTo(IPoi poi);

    public void simulateNavigationTo(ILocation origin, IPoi poi);

    public void simulateNavigationToParking(ILocation origin);

    public void stopNavigation();

    public void showPoi(IPoi poi);

    public void presentLocation(ILocation location);

    public void presentFacility(String campusId, String facilityId);

    public void presentCampus(String CampusId);

    public void showMyLocation();

    public void mapZoomIn();

    public void mapZoomOut();

    public int getPresentedFloorId(String facilityId);

    public void setUserIcon(Bitmap userBitmap);

    public boolean isNavigationState();

    public void setCurrentLocationAsParking();

    public void removeParkingLocation();

    public void navigateToParking();

    public void registerNavigationListener(SpreoNavigationListener listener);

    public void unregisterNavigationListener(SpreoNavigationListener listener);

    public void reDrawPois();

    public void openPoiBubble(IPoi poi);

    public void closeBubble(IPoi poi);

    public void openMyParkingMarkerBubble();

    public void closeMyParkingMarkerBubble();

    public void setMyParkingMarkerIcon(Bitmap icon);

    public void stopSimulation();

    public void displayPois(boolean display);

    public boolean hasParkingLocation();

    public ILocation getParkingLocation();
}
