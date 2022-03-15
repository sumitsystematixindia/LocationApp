package com.mlins.overlay;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.GroundOverlay;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.mlins.project.Campus;
import com.mlins.project.ProjectConf;
import com.mlins.utils.FacilityConf;
import com.mlins.utils.FloorData;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FacilityOverlay {
    List<GroundOverlay> overlays = new ArrayList<GroundOverlay>();
    private String facilityId = "";
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
    private int floor = 0;
    private GroundOverlay overlay = null;

    public FacilityOverlay(String facilityId, double tlLat, double tlLon, double trLat, double trLon, double brLat, double brLon, double blLat, double blLon) {
        this.facilityId = facilityId;
        rectTopLeftLat = tlLat;
        rectTopLeftLon = tlLon;
        rectTopRightLat = trLat;
        rectTopRightLon = trLon;
        rectBottomLeftLat = blLat;
        rectBottomLeftLon = blLon;
        rectBottomRightLat = brLat;
        rectBottomRightLon = brLon;
    }

    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        try {
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
        } catch (Throwable e) {
            e.printStackTrace();
        }

        return inSampleSize;
    }

    public String getFacilityId() {
        return facilityId;
    }

    public void setFacilityIdId(String facilityId) {
        this.facilityId = facilityId;
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


        Bitmap bm = null;
        try {
            // First decode with inJustDecodeBounds=true to check dimensions
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            options.inScaled = false;

            //options.inTempStorage = new byte[16*1024];
            //options.inPurgeable = true;

            BitmapFactory.decodeByteArray(blob, 0, blob.length, options);

            final int height = options.outHeight;
            final int width = options.outWidth;


            int reqWidth = width;
            int reqHeight = height;


            // Calculate inSampleSize
            options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

            // Decode bitmap with inSampleSize set
            options.inJustDecodeBounds = false;
            bm = BitmapFactory.decodeByteArray(blob, 0, blob.length, options);
        } catch (Throwable e) {
            e.printStackTrace();
        }

        return bm;

    }

    public List<OverlayChunk> getChunkedImages() {
        return FacilityMapTilesManager.getInstance().getFloorMapTiles(facilityId, floor);
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


    public int getFloor() {
        return floor;
    }


    public void setFloor(int floor) {

        this.floor = floor;

//		try {
//			Campus campus = ProjectConf.getInstance().getSelectedCampus();
//			if (campus != null) {
//				Map<String, FacilityConf> facilitiesmap = campus.getFacilitiesConfMap();
//				if (facilityId != null) {
//					FacilityConf facility = facilitiesmap.get(facilityId);
//					if (facility != null) {
//						List<FloorData> mapdata = facility.getFloorDataList();
//						uri = mapdata.get(floor).mapuri;
//						String url = ServerConnection.getInstance().translateUrl(uri, facility.getId());
//						blob = ResourceDownloader.getInstance().getUrl(url);
//						
//						this.floor = floor;
//					}
//				}
//			}
//		} catch (Throwable e) {
//			e.printStackTrace();
//		}

    }


    public GroundOverlay getOverlay() {
        return overlay;
    }


    public void setOverlay(GroundOverlay overlay) {
        this.overlay = overlay;
    }

    public void setImage(BitmapDescriptor image) {
        overlay.setImage(image);
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

    public List<LatLng> getPolygone() {
        List<LatLng> result = null;
        try {
            if (facilityId != null) {
                Campus campus = ProjectConf.getInstance().getSelectedCampus();
                if (campus != null) {
                    Map<String, FacilityConf> facilities = campus.getFacilitiesConfMap();
                    FacilityConf fac = facilities.get(facilityId);
                    if (fac != null) {
                        FloorData floordata = fac.getFloor(floor);
                        if (floordata != null) {
                            result = floordata.getPolygon();
                        }
                    }
                }
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
        return result;
    }


}
