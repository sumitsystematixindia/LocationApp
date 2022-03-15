package com.mlins.overlay;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.mlins.ndk.wrappers.NdkConversionUtils;
import com.mlins.ndk.wrappers.NdkLocation;
import com.mlins.project.Campus;
import com.mlins.project.ProjectConf;
import com.mlins.utils.FacilityConf;

import java.util.Map;

public class OverlayChunk {

    // XXX NDK LOAD LIB
    static {
        System.loadLibrary("MlinsLocationFinderUtils");
    }

    private BitmapDescriptor bd = null;
    private int southWestX;
    private int southWestY;
    private int northEastX;
    private int northEastY;

    //private GroundOverlay gOverlay = null;
    private LatLngBounds bound;

    public OverlayChunk(BitmapDescriptor bd, int southWestX, int southWestY,
                        int northEastX, int northEastY) {
        super();
        this.bd = bd;
        this.southWestX = southWestX;
        this.southWestY = southWestY;
        this.northEastX = northEastX;
        this.northEastY = northEastY;

    }

    public OverlayChunk(BitmapDescriptor bd) {
        this.bd = bd;
    }

    public OverlayChunk(LatLng sw, LatLng ne) {
        super();
        LatLngBounds bounds = new LatLngBounds(ne, // North east corner
                sw); // South west corner
        this.bound = bounds;
    }

    private static LatLng convertToLatlng(double x, double y, FacilityConf fac) {

        LatLng result = null;

        try {

            if (fac != null) {
                NdkLocation point = new NdkLocation(x, y);
                point.setZ(-1); // non relevant

                NdkLocation covertedPoint = new NdkLocation();

                NdkConversionUtils converter = new NdkConversionUtils();

                double rotationAngle = fac.getRot_angle();

                converter.convertPoint(point, fac.getConvRectTLlon(),
                        fac.getConvRectTLlat(), fac.getConvRectTRlon(),
                        fac.getConvRectTRlat(), fac.getConvRectBLlon(),
                        fac.getConvRectBLlat(), fac.getConvRectBRlon(),
                        fac.getConvRectBRlat(), fac.getMapWidth(),
                        fac.getMapHight(), rotationAngle, covertedPoint);

                if (covertedPoint != null) {
                    result = new LatLng(covertedPoint.getLat(),
                            covertedPoint.getLon());
                }
            }

        } catch (Throwable t) {
            t.printStackTrace();
        }

        return result;
    }

    public BitmapDescriptor getBd() {
        return bd;
    }

    public LatLngBounds getBound() {
        return bound;
    }


//	public GroundOverlay getgOverlay() {
//		return gOverlay;
//	}
//
//	public void setgOverlay(GroundOverlay gOverlay) {
//		if(gOverlay!=null){
//			if(this.gOverlay!=null){
//				removeGroundOverlay();
//			}
//			this.gOverlay = gOverlay;
//		}
//	}
//	
//	
//	public void removeGroundOverlay() {
//		if(gOverlay!=null){
//			gOverlay.remove();
//		}
//		
//	}

    public void setBound(String facId) {
        try {
            Campus c = ProjectConf.getInstance().getSelectedCampus();
            if (c != null) {
                Map<String, FacilityConf> facilitiesmap = c.getFacilitiesConfMap();

                FacilityConf fac = facilitiesmap.get(facId);
                LatLng sw = convertToLatlng(southWestX, southWestY, fac);
                LatLng ne = convertToLatlng(northEastX, northEastY, fac);

                LatLngBounds bounds = new LatLngBounds(ne, // North east corner
                        sw); // South west corner
                this.bound = bounds;
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }

    }
    // ================= end ndk link ==============

    public String getAsStringLine() {
        //format ==> swLat,swLon,neLat,neLon
        if (bound == null) {
            return null;
        }
        return bound.southwest.latitude + "," + bound.southwest.longitude + "," + bound.northeast.latitude + "," + bound.northeast.longitude;
    }

    public void setBoundFromStringLine(String line) {

        if (line == null) {
            return;
        }

        String data[] = line.split(",");

        if (data.length == 4) {

            double swLat = Double.parseDouble(data[0]);
            double swLon = Double.parseDouble(data[1]);
            double neLat = Double.parseDouble(data[2]);
            double neLon = Double.parseDouble(data[3]);

            LatLng sw = new LatLng(swLat, swLon);
            LatLng ne = new LatLng(neLat, neLon);

            LatLngBounds bound = new LatLngBounds(sw, ne);
            this.bound = bound;
        }

    }


}
