package gps;

import android.graphics.PointF;

import com.mlins.locator.LocationCorrector;
import com.mlins.orientation.OrientationMonitor;
import com.mlins.utils.FacilityConf;
import com.mlins.utils.FacilityContainer;
import com.mlins.utils.Lookup;
import com.mlins.utils.PropertyHolder;

public class GpsLocationCorrector extends LocationCorrector {

    public GpsLocationCorrector(){
        init();
    }

    public static GpsLocationCorrector getInstance() {
        return Lookup.getInstance().get(GpsLocationCorrector.class);
    }

    @Override
    protected PointF correctLocation() {
        PointF p = null;
        // remember(location);
        if (mReckon != null) {
            if (mNavState && PropertyHolder.getInstance().isProjectOnPath()) {
                p = mPath.getClosestPointOnPath(mReckon);
            } else {
                if (PropertyHolder.getInstance().isTurnToClosestGisLineMethod()) {

                    FacilityConf facConf = FacilityContainer.getInstance().getCurrent();
                    float rot = 0;

                    if (facConf != null) {
                        rot = facConf.getFloorRotation();
                    }

                    double angle = OrientationMonitor.getInstance().getAzimuth() - rot;
                    p = CampusGisData.getInstance().findClosestPointOnSegment(mReckon, angle);
                    if (p == null) {
                        p = CampusGisData.getInstance().findClosestPointOnLine(
                                mReckon, angle);
                    }
                    if (p == null) {
                        p = CampusGisData.getInstance().findClosestPointOnLine(mReckon);
                    }
                } else {
                    p = CampusGisData.getInstance().findClosestPointOnLine(mReckon);
                }
            }
            // if (p != null)
            // location.set(p);
        }
        return p;
    }
}
