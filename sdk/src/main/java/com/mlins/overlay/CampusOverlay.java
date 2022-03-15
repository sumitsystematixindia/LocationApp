package com.mlins.overlay;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.GroundOverlay;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.List;

public class CampusOverlay {


    List<GroundOverlay> overlays = new ArrayList<GroundOverlay>();
    private int id = -1;
    private String md5 = null;
    private byte[] blob = null;
    private long lastModified = -1;
    private double rectTopLeftLat = 0;
    private double rectTopLeftLon = 0;
    private double rectTopRightLat = 0;
    private double rectTopRightLon = 0;
    private double rectBottomLeftLat = 0;
    private double rectBottomLeftLon = 0;
    private double rectBottomRightLat = 0;
    private double rectBottomRightLon = 0;
    private String uri = null;
    private String campusId = null;
    private GroundOverlay overlay = null;


    public CampusOverlay(int id, String uri, double tlLat, double tlLon, double trLat, double trLon, double brLat, double brLon, double blLat, double blLon) {
        this.id = id;
        this.uri = uri;
        rectTopLeftLat = tlLat;
        rectTopLeftLon = tlLon;
        rectTopRightLat = trLat;
        rectTopRightLon = trLon;
        rectBottomLeftLat = blLat;
        rectBottomLeftLon = blLon;
        rectBottomRightLat = brLat;
        rectBottomRightLon = brLon;

    }


    public CampusOverlay(String content) {

        try {

            JSONTokener jsonTokener = new JSONTokener(content);

            JSONObject gObj = (JSONObject) jsonTokener.nextValue();

            try {
                int autoIndex = gObj.getInt("id");
                setId(autoIndex);
            } catch (Exception e) {
                // System.out.println("MapOverlay: error key index");
                // e.printStackTrace();
            }

            try {
                String md5 = gObj.getString("md5");
                setMd5(md5);
            } catch (Exception e) {
                // System.out.println("MapOverlay: error key name");
                // e.printStackTrace();
            }

            try {
                String uri = gObj.getString("uri");
                setUri(uri);
            } catch (Exception e) {
                // System.out.println("MapOverlay: error key name");
                // e.printStackTrace();
            }

            try {
                setRectTopLeftLat(gObj.getDouble("rect_top_left_lat"));
            } catch (Exception e) {
                System.out.println("MapOverlay: error key rect_top_left_lat");
                e.printStackTrace();
            }
            try {
                setRectTopLeftLon(gObj.getDouble("rect_top_left_lon"));
            } catch (Exception e) {
                System.out.println("MapOverlay: error key rect_top_left_lon");
                e.printStackTrace();
            }
            try {
                setRectTopRightLat(gObj.getDouble("rect_top_right_lat"));
            } catch (Exception e) {
                System.out.println("MapOverlay: error key rect_top_right_lat");
                e.printStackTrace();
            }
            try {
                setRectTopRightLon(gObj.getDouble("rect_top_right_lon"));
            } catch (Exception e) {
                System.out.println("MapOverlay: error key rect_top_right_lon");
                e.printStackTrace();
            }
            try {
                setRectBottomLeftLat(gObj.getDouble("rect_bottom_left_lat"));
            } catch (Exception e) {
                System.out
                        .println("MapOverlay: error key rect_bottom_left_lat");
                e.printStackTrace();
            }
            try {
                setRectBottomLeftLon(gObj.getDouble("rect_bottom_left_lon"));
            } catch (Exception e) {
                System.out
                        .println("MapOverlay: error key rect_bottom_left_lon");
                e.printStackTrace();
            }
            try {
                setRectBottomRightLat(gObj.getDouble("rect_bottom_right_lat"));
            } catch (Exception e) {
                System.out
                        .println("MapOverlay: error key rect_bottom_right_lat");
                e.printStackTrace();
            }
            try {
                setRectBottomRightLon(gObj.getDouble("rect_bottom_right_lon"));
            } catch (Exception e) {
                System.out
                        .println("MapOverlay: error key rect_bottom_right_lon");
                e.printStackTrace();
            }

        } catch (Throwable t) {
            t.printStackTrace();
        }

    }

    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    public String getCampusId() {
        return campusId;
    }

    public void setCampusId(String campusId) {
        this.campusId = campusId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public byte[] getBlob() {
        return blob;
    }

    public void setBlob(byte[] blob) {
        this.blob = blob;
    }

    public long getLastModified() {
        return lastModified;
    }

    public void setLastModified(long lastModified) {
        this.lastModified = lastModified;
    }

    public double getRectTopLeftLat() {
        return rectTopLeftLat;
    }

    public void setRectTopLeftLat(double rectTopLeftLat) {
        this.rectTopLeftLat = rectTopLeftLat;
    }

    public double getRectTopLeftLon() {
        return rectTopLeftLon;
    }

    public void setRectTopLeftLon(double rectTopLeftLon) {
        this.rectTopLeftLon = rectTopLeftLon;
    }

    public double getRectTopRightLat() {
        return rectTopRightLat;
    }

    public void setRectTopRightLat(double rectTopRightLat) {
        this.rectTopRightLat = rectTopRightLat;
    }

    public double getRectTopRightLon() {
        return rectTopRightLon;
    }

    public void setRectTopRightLon(double rectTopRightLon) {
        this.rectTopRightLon = rectTopRightLon;
    }

    public double getRectBottomLeftLat() {
        return rectBottomLeftLat;
    }

    public void setRectBottomLeftLat(double rectBottomLeftLat) {
        this.rectBottomLeftLat = rectBottomLeftLat;
    }

    public double getRectBottomLeftLon() {
        return rectBottomLeftLon;
    }

    public void setRectBottomLeftLon(double rectBottomLeftLon) {
        this.rectBottomLeftLon = rectBottomLeftLon;
    }

    public double getRectBottomRightLat() {
        return rectBottomRightLat;
    }

    public void setRectBottomRightLat(double rectBottomRightLat) {
        this.rectBottomRightLat = rectBottomRightLat;
    }

    public double getRectBottomRightLon() {
        return rectBottomRightLon;
    }

    public void setRectBottomRightLon(double rectBottomRightLon) {
        this.rectBottomRightLon = rectBottomRightLon;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public BitmapDescriptor getBitmapDescriptor() {

        BitmapDescriptor bd = null;

        try {
            Bitmap bm = decodeSampledBitmapFromResource();
            bd = BitmapDescriptorFactory.fromBitmap(bm);
            bm.recycle();
            bm = null;
        } catch (Throwable t) {
            t.printStackTrace();
        }
        return bd;

    }

    public Bitmap decodeSampledBitmapFromResource() {


        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        options.inScaled = false;
        BitmapFactory.decodeByteArray(blob, 0, blob.length, options);

        final int height = options.outHeight;
        final int width = options.outWidth;


        int reqWidth = width;
        int reqHeight = height;

//	    if(reqWidth>MAX_WIDTH || reqHeight> MAX_HIGHT){
//	    	 reqWidth=(int)(0.2 * width);
//	    	 reqHeight=(int)(0.2 * height);
//	    }


        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeByteArray(blob, 0, blob.length, options);
    }

    public LatLngBounds getBounds() {
        LatLngBounds bounds = new LatLngBounds(
                new LatLng(rectBottomLeftLat, rectBottomLeftLon),//40.712216, -74.22655),       // South west corner
                new LatLng(rectTopRightLat, rectTopRightLon)); //40.773941, -74.12544));      // North east corner
        return bounds;
    }

    public LatLng getCenter() {
        double midLat = rectBottomRightLat + (rectTopLeftLat - rectBottomRightLat) / 2.0;
        double midLon = rectTopLeftLon + (rectBottomRightLon - rectTopLeftLon) / 2.0;
        LatLng center = new LatLng(midLat, midLon);
        return center;
    }

//	public LatLng getCorner(){
//		return new LatLng(rectBottomRightLat, rectBottomRightLon);
//	}

    public float getWidth() {
        return distLatLon(rectBottomLeftLat, rectBottomLeftLon, rectBottomRightLat, rectBottomRightLon);
    }

    public float getHeight() {
        return distLatLon(rectTopLeftLat, rectTopLeftLon, rectBottomLeftLat, rectBottomLeftLon);
    }


    private float distLatLon(double lat1, double lon1, double lat2, double lon2) {
        double R = 6378.137; // Radius of earth in KM
        double dLat = (lat2 - lat1) * Math.PI / 180.0;
        double dLon = (lon2 - lon1) * Math.PI / 180.0;
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(lat1 * Math.PI / 180.0) * Math.cos(lat2 * Math.PI / 180.0) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double d = R * c;
        return (float) d * 1000; // meters
    }


    public List<OverlayChunk> getChunkedImages() {
        return CampusMapTilesManager.getInstance().getMapTiles(campusId, id);
    }


    public GroundOverlay getOverlay() {
        return overlay;
    }


    public void setOverlay(GroundOverlay overlay) {
        this.overlay = overlay;
    }


    public void removeTiles() {
        for (GroundOverlay go : overlays) {
            if (go != null) {
                go.remove();
            }
        }
        overlays.clear();

    }


    public void addGroundOverlay(GroundOverlay gOverlay) {
        if (gOverlay != null) {
            overlays.add(gOverlay);
        }
    }


}
