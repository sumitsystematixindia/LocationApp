package com.spreo.interfaces;

import android.view.View;

import com.spreo.nav.enums.LocationMode;
import com.spreo.nav.interfaces.ILabel;
import com.spreo.nav.interfaces.IPoi;

public interface SpreoMapViewListener {
    void mapWillChangeFloor(int floorId);

    void mapDidChangeFloor(int floorId);

    void mapWillSwapTo(LocationMode mode);

    void mapDidSwapTo(LocationMode mode);

    void facilityClickListener(String facilityId);

    void onPoiClick(IPoi poi);

    void onBubbleClick(IPoi poi);

    View aboutToOpenBubble(IPoi poi);

    void onBubbleOpend(IPoi poi);

    void onBubbleClosed(IPoi poi);

    void onMyParkingMarkerClick();

    void onMyParkingBubbleClick();

    void onUserlocationClick();

    void onUserLocationBubbleClick();

    void onLabelClick(ILabel label);
}
