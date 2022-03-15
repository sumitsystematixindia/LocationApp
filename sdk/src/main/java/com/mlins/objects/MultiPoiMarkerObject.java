package com.mlins.objects;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.view.View;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.mlins.nav.utils.CustomInfoWindowAdapter;
import com.mlins.utils.PropertyHolder;
import com.spreo.nav.interfaces.IPoi;
import com.spreo.spreosdk.R;

public class MultiPoiMarkerObject {
    public final static String TYPE_EXIT = "exit";
    public final static String TYPE_SWITCH = "switch";
    public final static String TYPE_NUMBER = "number";
    IPoi poi;
    Marker marker;
    private GoogleMap mGooglemap;
    private Context context;
    private int pointNumber = -1;
    private boolean visited = false;
    private String type = TYPE_NUMBER;

    public MultiPoiMarkerObject(IPoi o, int number, String type, boolean visitedpoi, Context ctx, GoogleMap googlemap) {
        this.context = ctx;
        this.type = type;
        mGooglemap = googlemap;
        if (o != null) {
            poi = o;
            pointNumber = number;
            visited = visitedpoi;
            LatLng poiloc = new LatLng(poi.getPoiLatitude(), poi.getPoiLongitude());
            Bitmap bm = getAsBitmap();
            if (bm != null) {
                BitmapDescriptor icon = BitmapDescriptorFactory.fromBitmap(bm);
                marker = mGooglemap.addMarker(new MarkerOptions().position(poiloc).title(o.getpoiDescription()).anchor(0.5f, 0.5f).icon(icon).zIndex(4));
            }
        }
    }

    private Bitmap getAsBitmap() {
        Bitmap result = null;
        if (type.equals(TYPE_NUMBER) && pointNumber != -1) {
            int radius = 35;
            int width = radius * 2 + 1;
            int height = radius * 2 + 1;
            float x = width / 2;
            float y = height / 2;
            float textsize = 35f;
            Bitmap image = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(image);
            Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            if (visited) {
                paint.setColor(Color.parseColor(PropertyHolder.getInstance().getMultiPoisVisitedPointColor()));
            } else {
                paint.setColor(Color.parseColor(PropertyHolder.getInstance().getMultiPoisPointColor()));
            }

            canvas.drawCircle(x, y, radius, paint);
            if (pointNumber != -1) {
                String text = String.valueOf(pointNumber);
                if (visited) {
                    paint.setColor(Color.parseColor(PropertyHolder.getInstance().getMultiPoisVisitedPointNumberColor()));
                } else {
                    paint.setColor(Color.parseColor(PropertyHolder.getInstance().getMultiPoisPointNumberColor()));
                }

                paint.setStyle(Paint.Style.FILL);
                paint.setTextAlign(Paint.Align.CENTER);
                paint.setAntiAlias(true);
                paint.setTextSize(textsize);
                float w = paint.measureText(text, 0, text.length());
                paint.setTypeface(Typeface.DEFAULT_BOLD);
                canvas.drawText(text, x, y + w / 2, paint);
            }

            result = image;
        } else if (type.equals(TYPE_EXIT)) {
            try {
                Bitmap userIcon = PropertyHolder.getInstance().getIconForMultiPointExit();
                if (userIcon != null) {
                    result = userIcon;
                } else {
                    result = BitmapFactory.decodeResource(context.getResources(), R.drawable.mp_exit);
                }
            } catch (Throwable t) {
                t.printStackTrace();
            }
        } else if (type.equals(TYPE_SWITCH)) {
            try {
                Bitmap userIcon = PropertyHolder.getInstance().getIconForMultiPointSwitchFloor();
                if (userIcon != null) {
                    result = userIcon;
                } else {
                    result = BitmapFactory.decodeResource(context.getResources(), R.drawable.mp_elevator);
                }
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }

        return result;
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

    }

    public void setRotation(float angle) {
        float a = (angle + 360) % 360;
        marker.setRotation(360 - a);
    }

    public void showBaubble() {
        marker.showInfoWindow();
    }

    public void removeMarkerFromMap() {

        marker.remove();

    }

    public Marker getMarker() {
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

    public int getPointNumber() {
        return pointNumber;
    }

    public void setPointNumber(int pointNumber) {
        this.pointNumber = pointNumber;
    }
}
