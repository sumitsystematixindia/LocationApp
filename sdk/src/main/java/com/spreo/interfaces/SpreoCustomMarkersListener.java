package com.spreo.interfaces;

import android.view.View;

public interface SpreoCustomMarkersListener {
    void onCustomMarkerClick(ICustomMarker marker);

    void onCustomMarkerBubbleClick(ICustomMarker marker);

    View aboutToOpenCustomMarkerBubble(ICustomMarker marker);
}
