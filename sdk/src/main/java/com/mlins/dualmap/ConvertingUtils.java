package com.mlins.dualmap;

import com.google.android.gms.maps.model.LatLng;
import com.mlins.ndk.wrappers.NdkConversionUtils;
import com.mlins.ndk.wrappers.NdkLocation;
import com.mlins.overlay.FacilityOverlay;
import com.mlins.project.Campus;
import com.mlins.project.ProjectConf;
import com.mlins.utils.FacilityConf;
import com.mlins.utils.gis.Location;
import com.spreo.nav.enums.LocationMode;
import com.spreo.nav.interfaces.ILocation;

import java.util.List;
import java.util.Map;

public class ConvertingUtils {


    private static NdkLocation ndkConvertToXY(LatLng latlng, FacilityConf fac) {
        NdkLocation covertedLatLonPoint = null;
        if (fac != null && latlng != null) {
            NdkLocation convertedPoint = new NdkLocation();
            convertedPoint.setLat(latlng.latitude);
            convertedPoint.setLon(latlng.longitude);
            NdkConversionUtils converter = new NdkConversionUtils();
            double rotationAngle = fac.getRot_angle();
            covertedLatLonPoint = new NdkLocation();
            converter.convertLatLonPoint(convertedPoint, fac.getConvRectTLlon(),
                    fac.getConvRectTLlat(), fac.getConvRectTRlon(),
                    fac.getConvRectTRlat(), fac.getConvRectBLlon(),
                    fac.getConvRectBLlat(), fac.getConvRectBRlon(),
                    fac.getConvRectBRlat(), fac.getMapWidth(),
                    fac.getMapHight(), rotationAngle, covertedLatLonPoint);
        }
        return covertedLatLonPoint;
    }

    public static ILocation convertToXYZ(LatLng latlng, FacilityOverlay foverlay) {
        ILocation result = null;
        try {
            if (latlng != null && foverlay != null) {
                String facilityId = foverlay.getFacilityId();
                if (facilityId != null) {
                    Campus campus = ProjectConf.getInstance().getSelectedCampus();
                    if (campus != null) {
                        Map<String, FacilityConf> facilities = campus.getFacilitiesConfMap();
                        FacilityConf fac = facilities.get(facilityId);

                        NdkLocation covertedLatLonPoint = ndkConvertToXY(latlng, fac);

                        if (covertedLatLonPoint != null) {
                            float x = (float) covertedLatLonPoint.getX();
                            float y = (float) covertedLatLonPoint.getY();
                            int z = foverlay.getFloor();
                            result = new Location(x, y, z);
                            String campusId = campus.getId();
                            result.setCampusId(campusId);
                            result.setFacilityId(facilityId);
                        }
                    }
                }

            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
        return result;
    }


    public static boolean isPointInPolygon(List<LatLng> poly, LatLng latlng) {
        int nvert = poly.size();
        int i, j;
        boolean c = false;
        LatLng verti, vertj;
        for (i = 0, j = nvert - 1; i < nvert; j = i++) {
            verti = poly.get(i);
            vertj = poly.get(j);
            if (((verti.latitude > latlng.latitude) != (vertj.latitude > latlng.latitude)) &&
                    (latlng.longitude < (vertj.longitude - verti.longitude) * (latlng.latitude - verti.latitude) / (vertj.latitude - verti.latitude) + verti.longitude))
                c = !c;
        }
        return (c ? true : false);
    }


    public static ILocation convertToXY(LatLng latlng, FacilityConf facConf) {
        ILocation result = null;
        try {
            if (latlng != null && facConf != null) {

                NdkLocation covertedLatLonPoint = ndkConvertToXY(latlng, facConf);

                if (covertedLatLonPoint != null) {

                    float x = (float) covertedLatLonPoint.getX();
                    float y = (float) covertedLatLonPoint.getY();
                    result = new Location();
                    result.setX(x);
                    result.setY(y);
                    result.setType(LocationMode.INDOOR_MODE);

                }
            }

        } catch (Throwable t) {
            t.printStackTrace();
        }
        return result;
    }

    public static LatLng convertToLatlng(double x, double y, int z, String facid) {

        LatLng result = null;

        try {
            Campus c = ProjectConf.getInstance().getSelectedCampus();
            if (c != null) {
                Map<String, FacilityConf> facilitiesmap = c.getFacilitiesConfMap();
                FacilityConf fac = facilitiesmap.get(facid);
                if (fac != null) {
                    NdkLocation point = new NdkLocation(x, y);
                    point.setZ(z);

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
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }

        return result;
    }

}
