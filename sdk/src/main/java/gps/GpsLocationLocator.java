package gps;

import android.graphics.PointF;
import android.location.Location;

import com.google.android.gms.maps.model.LatLng;
import com.mlins.locator.LocationLocator;
import com.mlins.utils.Cleanable;
import com.mlins.utils.FacilityConf;
import com.mlins.utils.FacilityContainer;
import com.mlins.utils.Lookup;
import com.mlins.utils.PropertyHolder;

import java.util.ArrayList;
import java.util.List;


public class GpsLocationLocator extends LocationLocator implements Cleanable {

    private PointF currentLock = null;

    private List<Location> gpsLocationsHistory = new ArrayList<Location>();
    private int itemsForGpsAverage = 5;

    public static GpsLocationLocator getInstance() {
        return Lookup.getInstance().get(GpsLocationLocator.class);
    }

    public static void releaseInstance() {
        Lookup.getInstance().remove(GpsLocationLocator.class);
    }

    public void clean() {}

    public LatLng findLocation(Location gpsloc) {
        LatLng result = null;

        if (gpsloc != null) {
            LatLng gpslatlng = new LatLng(gpsloc.getLongitude(), gpsloc.getLatitude());
            gpsLocationsHistory.add(gpsloc);
            if (gpsLocationsHistory.size() > itemsForGpsAverage) {
                gpslatlng = getGpsMovingAverage();
                gpsLocationsHistory.remove(0);
            }

            float x = (float) gpslatlng.longitude;
            float y = (float) gpslatlng.latitude;
            PointF avePt = new PointF(x, y);
            if (currentLock == null) {

                currentLock = avePt;
            }


            GpsLocationCorrector lcr = GpsLocationCorrector.getInstance();
            if (lcr != null) {
                PointF dr = lcr.getDeadReckoning();
                if (dr != null && avePt != null) {

                    float dx = avePt.x - dr.x;
                    float dy = avePt.y - dr.y;

                    float dist = (float) Math.sqrt(dx * dx + dy * dy);


                    FacilityConf facConf = FacilityContainer.getInstance().getCurrent();

                    if (facConf != null) {

                        float actionDist = dist / facConf.getPixelsToMeter();

                        float locatorradius = facConf.getLocatorRadius();


                        if (!isUserIndirection(dr, avePt)) {
                            locatorradius *= 2f;

                        }

                        if (actionDist > locatorradius) {
                            float w = PropertyHolder.getInstance().getLocatorDeadReckoningWeight();

                            float wAvgLocX = w * dr.x + (1.0f - w) * avePt.x;

                            float wAvgLocY = w * dr.y + (1.0f - w) * avePt.y;

                            PointF wAvgLocation = new PointF(wAvgLocX, wAvgLocY);
                            lcr.setLocationPositive(wAvgLocation);

                        }
                    }


                }


                if (!GpsLocationCorrector.getInstance().isInitialized()) {
                    GpsLocationCorrector.getInstance().setPosition(getCurrentLock());
                }
                PointF p = GpsLocationCorrector.getInstance().correctLocation();
                if (p != null) {
                    setCurrentLock(p);
                }
                if (getCurrentLock() == null) {
                    setCurrentLock(new PointF());
                }
//		return getCurrentLock();
            }
            result = new LatLng(getCurrentLock().x, getCurrentLock().y);
        }

        return result;
    }

    private LatLng getGpsMovingAverage() {
        LatLng result = null;
        double lat = 0;
        double lon = 0;
        for (Location o : gpsLocationsHistory) {
            lat += o.getLatitude();
            lon += o.getLongitude();
        }
        lat /= gpsLocationsHistory.size();
        lon /= gpsLocationsHistory.size();
        result = new LatLng(lat, lon);
        return result;
    }

    @Override
    public PointF getCurrentLock() {
        return currentLock;
    }

    @Override
    public void setCurrentLock(PointF currentLock) {
        this.currentLock = currentLock;
    }
}
