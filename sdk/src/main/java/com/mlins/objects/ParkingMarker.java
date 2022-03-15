package com.mlins.objects;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.mlins.nav.utils.CustomInfoWindowAdapter;
import com.mlins.views.OutdoorMapView;
import com.spreo.nav.enums.LocationMode;
import com.spreo.nav.interfaces.ILocation;
import com.spreo.spreosdk.R;

public class ParkingMarker {
    private GoogleMap mGooglemap;
    private Context ctx;
    private OutdoorMapView outdoorMapView;
    private Marker marker;
    private String bubbleText = null;
    private LatLng position = null;
    private MarkerOptions moptions = null;
    private LocationMode locationMode = LocationMode.OUTDOOR_MODE;
    private String facilityID = null;
    private int floor = 0;
    private boolean visible = false;

    public ParkingMarker(LatLng position, String bubbleText, Context ctx,
                         GoogleMap googlemap, OutdoorMapView outdoorMapView) {
        this.ctx = ctx;
        this.outdoorMapView = outdoorMapView;
        mGooglemap = googlemap;
        this.position = position;
        this.bubbleText = bubbleText;

        if (this.position != null) {

            Bitmap parkingbm = BitmapFactory.decodeResource(
                    ctx.getResources(), R.drawable.parking);
            BitmapDescriptor parkingicon = BitmapDescriptorFactory
                    .fromBitmap(parkingbm);
            if (this.bubbleText == null) {
                this.bubbleText = ctx.getString(R.string.my_parking_info_text);
            }
            moptions = new MarkerOptions()
                    .icon(parkingicon)
                    .position(this.position)
                    .title(this.bubbleText);
            //mGooglemap.setOnMarkerClickListener(this);
            marker = mGooglemap.addMarker(moptions);
        }
    }

    public ParkingMarker(ILocation loc, String bubbleText, Context ctx,
                         GoogleMap googlemap, Bitmap icon) {
        this.ctx = ctx;
        mGooglemap = googlemap;
        this.bubbleText = bubbleText;

        if (loc != null) {
            if (loc.getLocationType() == LocationMode.INDOOR_MODE && loc.getFacilityId() != null) {
                facilityID = loc.getFacilityId();
                floor = (int) loc.getZ();
                locationMode = LocationMode.INDOOR_MODE;
            } else {
                locationMode = LocationMode.OUTDOOR_MODE;
                facilityID = null;
                floor = 0;
            }
        }

        position = new LatLng(loc.getLat(), loc.getLon());

        if (position != null) {
            BitmapDescriptor parkingicon = BitmapDescriptorFactory
                    .fromBitmap(icon);
            if (this.bubbleText == null) {
                this.bubbleText = ctx.getString(R.string.my_parking_info_text);
            }
            moptions = new MarkerOptions()
                    .icon(parkingicon)
                    .position(this.position)
                    .visible(false)
                    .zIndex(2)
                    .title(this.bubbleText);
            //mGooglemap.setOnMarkerClickListener(this);
            marker = mGooglemap.addMarker(moptions);
        }
    }

    public void showBubble() {
        if (marker != null) {
            marker.showInfoWindow();
        }
    }

//	@Override
//	public boolean onMarkerClick(Marker mark) {
//
//		if(outdoorMapView!=null){
//			outdoorMapView.notifyOnParkingMarkerClick();
//			return true;
//		}
//		
//		return false;
//	}

    public Marker getMarker() {
        return marker;
    }

    public void closeBubble() {
        try {
            if (marker != null) {
                marker.hideInfoWindow();
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public void setPosition(ILocation loc) {
        try {
            if (marker != null && loc != null) {
                visible = false;
                marker.setVisible(visible);
                if (loc.getLocationType() == LocationMode.INDOOR_MODE && loc.getFacilityId() != null) {
                    facilityID = loc.getFacilityId();
                    floor = (int) loc.getZ();
                    locationMode = LocationMode.INDOOR_MODE;
                } else {
                    locationMode = LocationMode.OUTDOOR_MODE;
                    facilityID = null;
                    floor = 0;
                }

                position = new LatLng(loc.getLat(), loc.getLon());

                marker.setPosition(position);
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    // support for the old single map SDK
    public void setPosition(LatLng parkingloc) {
        try {
            if (marker != null) {
                marker.setPosition(parkingloc);
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public void remove() {
        try {
            if (marker != null) {
                marker.remove();
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public void setIcon(Bitmap icon) {
        try {
            if (icon != null) {
                BitmapDescriptor parkingicon = BitmapDescriptorFactory.fromBitmap(icon);
                if (marker != null) {
                    marker.setIcon(parkingicon);

                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }

    }

    public String getBubbleText() {
        String title = null;
        if (marker != null) {
            title = marker.getTitle();
        }
        return title;
    }

    public void setBubbleText(String info) {
        if (marker != null) {
            closeBubble();
            marker.setTitle(info);
            showBubble();
        }
    }

    public void setBubbleView(View v) {
        if (v != null && marker != null && mGooglemap != null) {
            mGooglemap.setInfoWindowAdapter(new CustomInfoWindowAdapter(v, marker));
        }
    }

    public LocationMode getLocationMode() {
        return locationMode;
    }

    public void setLocationMode(LocationMode locationMode) {
        this.locationMode = locationMode;
    }

    public String getFacilityID() {
        return facilityID;
    }

    public void setFacilityID(String facilityID) {
        this.facilityID = facilityID;
    }

    public int getFloor() {
        return floor;
    }

    public void setFloor(int floor) {
        this.floor = floor;
    }

    public void setVisibility(boolean isvisible) {
        if (marker != null) {
            try {
                visible = isvisible;
                marker.setVisible(visible);
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }
}
