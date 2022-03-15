//AddCatch
package com.mlins.aStar;

import android.graphics.PointF;

import com.mlins.utils.FacilityConf;
import com.mlins.utils.FacilityContainer;
import com.mlins.utils.gis.GisPoint;
import com.mlins.utils.gis.Location;
import com.mlins.utils.logging.Log;

import java.util.ArrayList;
import java.util.List;

public class NavigationPath {
    private static final String TAG = NavigationPath.class.getName();
    //	private List<GisSegment> path = new ArrayList<GisSegment>();
//	private List<GisSegment> secondPath = new ArrayList<GisSegment>();
//	private double z1 = -1;
//	private double z2 = -1;
    private List<FloorNavigationPath> FullPath = new ArrayList<FloorNavigationPath>();
    private Location elevator = new Location();
    private String facilityId = null;

    public NavigationPath() {

    }

    public void addFloorNavigationPath(double floor, List<GisSegment> p) {
        FloorNavigationPath fpath = new FloorNavigationPath(floor, p);
        FullPath.add(fpath);
    }

    public void addFloorNavigationPath(FloorNavigationPath fpath) {
        FullPath.add(fpath);
    }

    public List<FloorNavigationPath> getnavPathByZ(double z) {
        List<FloorNavigationPath> result = new ArrayList<>();
        for (FloorNavigationPath o : FullPath) {
            if (o.getZ() == z) {
                result.add(o);
            }
        }
        return result;
    }

    public List<List<GisSegment>> getPathByZ(double z) {
        List<List<GisSegment>> result = new ArrayList<>();
        for (FloorNavigationPath o : FullPath) {
            if (o.getZ() == z) {
                result.add(o.getPath());
            }
        }
        return result;
    }


//	public void setNavigationPath(List<GisSegment> navigationPath) {
//		this.path = navigationPath;
//		if (navigationPath == null || navigationPath.size() == 0)
//			return;
//		z1 = navigationPath.get(0).getLine().getZ();
//	}

    public GisSegment getClosestSegment(PointF pt) {
        Log.getInstance().debug(TAG, "Enter, GISSegment aStarNavigationPath");
        GisSegment closest = null;
        try {
            FacilityConf facConf = FacilityContainer.getInstance().getSelected();
            int z = facConf.getSelectedFloor();//FacilityConf.getInstance().getSelectedFloor();
            GisPoint p1 = new GisPoint((double) pt.x, (double) pt.y, z);
            List<List<GisSegment>> paths = getPathByZ(z);
            if (paths != null) {
                double distance = 0;
                double mindistance = Double.MAX_VALUE;
                for (List<GisSegment> path : paths) {
                    for (GisSegment s : path) {
                        GisPoint p2 = aStarMath.findClosePointOnSegment(p1, s);
                        distance = aStarMath.findDistance(p1, p2);
                        if (distance < mindistance) {
                            closest = s;
                            mindistance = distance;
                        }
                    }
                }

            }
            Log.getInstance()
                    .debug(TAG, "Exit, GISSegment aStarNavigationPath");
        } catch (Throwable t) {
            Log.getInstance().error(TAG, t.getMessage(), t);
        }
        return closest;
    }


    public GisSegment getNext(GisSegment segment) {
        GisSegment result = null;
        if (FullPath != null && FullPath.size() > 0) {
            for (FloorNavigationPath o : FullPath) {
                if (o.getPath().contains(segment)) {
                    int index = o.getPath().indexOf(segment);
                    if (o.getPath().size() > index + 1) {
                        result = o.getPath().get(index + 1);
                        break;
                    }
                }
            }
        }
        return result;
    }

//	private List<GisSegment> selectPath(int z) {
//		if (z1 == z ) {
//			return path;
//		} else if (z2 == z ) {
//			return secondPath;
//		}
//		return null;
//	}

    public PointF getClosestPointOnPath(PointF pt) {
        FacilityConf facConf = FacilityContainer.getInstance().getSelected();
        int z = facConf.getSelectedFloor();//FacilityConf.getInstance().getSelectedFloor();
        GisPoint p1 = new GisPoint((double) pt.x, (double) pt.y, z);
        PointF result = new PointF();

        GisPoint minpt = null;
        GisPoint point = null;
        List<List<GisSegment>> paths = getPathByZ(z);
        if (paths == null) {
            return pt;
        }

        double distance = 0;
        double mindistance = 1000000;
        for (List<GisSegment> tmpPath : paths) {
            for (GisSegment s : tmpPath) {
                point = aStarMath.findClosePointOnSegment(p1, s);
                distance = aStarMath.findDistance(p1, point);
                if (distance < mindistance) {
                    minpt = point;
                    mindistance = distance;
                }
            }
        }
        if (minpt != null) {
            result.set((float) minpt.getX(), (float) minpt.getY());
        }

        return result;
    }

    public PointF getClosestPointOnPath(PointF pt, int floor) {
//		FacilityConf  facConf = FacilityContainer.getInstance().getCurrent();	
        int z = floor;//FacilityConf.getInstance().getSelectedFloor();
        GisPoint p1 = new GisPoint((double) pt.x, (double) pt.y, z);
        PointF result = new PointF();

        GisPoint minpt = null;
        GisPoint point = null;
        List<List<GisSegment>> paths = getPathByZ(z);
        if (paths == null) {
            return pt;
        }

        double distance = 0;
        double mindistance = 1000000;
        for (List<GisSegment> tmpPath : paths) {
            for (GisSegment s : tmpPath) {
                point = aStarMath.findClosePointOnSegment(p1, s);
                distance = aStarMath.findDistance(p1, point);
                if (distance < mindistance) {
                    minpt = point;
                    mindistance = distance;
                }
            }
        }
        if (minpt != null) {
            result.set((float) minpt.getX(), (float) minpt.getY());
        }

        return result;
    }


    public Location getElevator() {
        return elevator;
    }

    public void setElevator(Location elevator) {
        this.elevator = elevator;
    }

    public List<FloorNavigationPath> getFullPath() {
        return FullPath;
    }

    public void setFullPath(List<FloorNavigationPath> fullPath) {
        FullPath = fullPath;
    }

    public String getFacilityId() {
        return facilityId;
    }

    public void setFacilityId(String facilityId) {
        this.facilityId = facilityId;
    }


}
