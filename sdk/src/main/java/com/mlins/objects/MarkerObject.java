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
import com.spreo.nav.interfaces.ILocation;
import com.spreo.nav.interfaces.IPoi;
import com.spreo.spreosdk.R;

public class MarkerObject /*implements OnMarkerClickListener*/ {

    IPoi poi;
    Marker marker;
    private GoogleMap mGooglemap;
    private Context context;
    private OutdoorMapView outdoorMapView;

    public MarkerObject(IPoi o, Context cnx, GoogleMap googlemap, OutdoorMapView outdoorMapView) {
        this.context = cnx;
        this.outdoorMapView = outdoorMapView;
        mGooglemap = googlemap;
        if (o != null) {
            poi = o;
            ILocation loc = o.getLocation();
            if (loc != null) {
                LatLng poiloc = new LatLng(loc.getLat(), loc.getLon());
                // Marker marker = googlemap.addMarker(new MarkerOptions().position(poiloc).title("test"));
                Bitmap bm = o.getIcon();
                if (bm == null) {
                    bm = BitmapFactory.decodeResource(context.getResources(), R.drawable.defualtpoiicon);
                }
                BitmapDescriptor icon = BitmapDescriptorFactory.fromBitmap(bm);
//				mGooglemap.setOnMarkerClickListener(this);
//				mGooglemap.setOnInfoWindowClickListener(infoListener);
                marker = mGooglemap.addMarker(new MarkerOptions().position(poiloc).title(o.getpoiDescription()).anchor(0.5f, 0.5f).icon(icon));
//				
            }
        }
    }

    public void setIcon(Bitmap bm) {

        try {

            if (bm == null) {
                bm = BitmapFactory.decodeResource(context.getResources(), R.drawable.defualtpoiicon);
            }

            BitmapDescriptor icon = BitmapDescriptorFactory.fromBitmap(bm);
            marker.setIcon(icon);
        } catch (Throwable t) {
            t.printStackTrace();
        }
//		
    }

    public void setRotation(float angle) {
        float a = (angle + 360) % 360;
        marker.setRotation(360 - a);
    }

//	private OnInfoWindowClickListener infoListener= new OnInfoWindowClickListener() {
//		
//		@Override
//		public void onInfoWindowClick(Marker mark) {
//			
//			IPoi poi = outdoorMapView.getPoiFromMarker(mark);
//			if (poi != null) {
//				outdoorMapView.onBubbleClick(poi);
//			}
//		}
//	};

//	@Override
//	public boolean onMarkerClick(Marker mark) {
//		
////		showBaubble();
//		// TODO Auto-generated method stub
////		Handler handler = new Handler();
////		handler.postDelayed(new Runnable() {
////			public void run() {
////				arg0.hideInfoWindow();
////			}
////		}, 7000);
//		IPoi poi = outdoorMapView.getPoiFromMarker(mark);
//		if (poi != null) {
//			outdoorMapView.onPoiClick(poi);
//			return true;
//		}
//		return false;
//	}

    public void showBaubble() {
        marker.showInfoWindow();
    }

    public void removeMarkerFromMap() {

        marker.remove();

    }

    public Marker getMarker() {
        // TODO Auto-generated method stub
        return marker;
    }

    public void closeBubble() {
        if (marker != null) {
            marker.hideInfoWindow();
        }
    }

    public void setVisible(boolean visible) {
        if (marker != null) {
            marker.setVisible(visible);
        }
    }

    public void setBubbleView(View v) {
        if (v != null && marker != null && mGooglemap != null) {
            mGooglemap.setInfoWindowAdapter(new CustomInfoWindowAdapter(v, marker));
        }
    }

}
