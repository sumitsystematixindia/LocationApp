package com.spreo.interfaces;

import android.view.View;

import com.google.android.gms.maps.model.LatLng;
import com.spreo.nav.interfaces.ILabel;
import com.spreo.nav.interfaces.ILocation;
import com.spreo.nav.interfaces.IPoi;

public interface SpreoDualMapViewListener {
    void onPoiClick(IPoi poi);

    void onBubbleClick(IPoi poi);

    View aboutToOpenBubble(IPoi poi);

    View aboutToOpenParkingBubble();

    void onMyParkingMarkerClick();

    void onMyParkingBubbleClick();

    void onUserlocationClick();

    void onUserLocationBubbleClick();

    void onLabelClick(ILabel label);

    void onMapClick(LatLng latlng, String facilityId, int floor);

    void onMapLongClick(LatLng latlng, String facilityId, int floor);

    void mapDidLoadFloor(String campusId, String facilityId, int floorId);

    void mapDidLoad();

    void OnFloorChange(int floor);

    void onMultipointClick(IPoi poi);

    void onZoomChange(float zoom);

    void onMapLongClick(ILocation location);

    View aboutToOpenUserBubble();
}
