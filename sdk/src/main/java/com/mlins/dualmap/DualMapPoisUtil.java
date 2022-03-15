package com.mlins.dualmap;

import android.graphics.PointF;

import com.mlins.kdtree.KDimensionalTree;
import com.mlins.utils.Lookup;
import com.spreo.nav.interfaces.IPoi;

import java.util.ArrayList;
import java.util.List;

public class DualMapPoisUtil {

    private KDimensionalTree<IPoi> currFloorPoiTree = new KDimensionalTree<IPoi>();

    public static DualMapPoisUtil getInstance() {
        return Lookup.getInstance().get(DualMapPoisUtil.class);
    }

    public static void releaseInstance() {
        Lookup.getInstance().remove(DualMapPoisUtil.class);
    }

    public void loadPoisKDimensionalTree(List<IPoi> pois) {

        try {
            currFloorPoiTree = new KDimensionalTree<IPoi>();

            for (IPoi p : pois) {
                try {
                    currFloorPoiTree.addElement(p.getPoint(), p);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    // O(k*n^(1-k/2))
    public ArrayList<IPoi> getInRangePois(PointF myLoc, float pixelToMeter,
                                          float rangeInMeters, int maxReturnedCount) {

        try {
            return (ArrayList<IPoi>) currFloorPoiTree.nearest(myLoc,
                    maxReturnedCount);
        } catch (Throwable e) {

            e.printStackTrace();
            return null;
        }

    }
}
