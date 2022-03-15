package com.mlins.locator;

import android.graphics.PointF;

import com.mlins.orientation.OrientationMonitor;
import com.mlins.utils.Lookup;
import com.mlins.utils.gis.GisData;
import com.mlins.utils.gis.GisLine;

import java.util.Observable;
import java.util.Observer;

public class HeadingObserver implements Observer {

    private HeadingObserver(){
        init();
    }

    //depends on OrientationMonitor so needs to be cleared
    public static HeadingObserver getInstance() {
        return Lookup.getInstance().get(HeadingObserver.class);
    }

    /*package*/ void init() {
        OrientationMonitor.getInstance().addObserver(this);
        // TODO Auto-generated method stub

    }

    public float getHeading() {
        PointF location = LocationLocator.getInstance().getCurrentLock();
        GisLine dir = GisData.getInstance().findClosestLine(location);
        float az = OrientationMonitor.getInstance().getAzimuth();
        if (dir != null) {
            float d = Math.abs(az - dir.getAngle());
            if (d < 90 || d > 270) {
                return dir.getAngle();
            }
            return (dir.getAngle() + 180) % 360;
        } else {
            return (az + 180) % 360;
        }

    }

    @Override
    public void update(Observable arg0, Object arg1) {
        // TODO Auto-generated method stub

    }

}
