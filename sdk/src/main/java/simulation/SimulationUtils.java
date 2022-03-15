package simulation;

import com.google.android.gms.maps.model.LatLng;
import com.mlins.aStar.GisSegment;
import com.mlins.utils.MathUtils;
import com.mlins.utils.gis.GisPoint;
import com.mlins.utils.gis.Location;
import com.mlins.utils.logging.Log;

import java.util.ArrayList;
import java.util.List;

public class SimulationUtils {
    private final static String TAG = "simulation.SimulationUtils";

    public static List<Location> getPathAsPoints(List<GisSegment> navpath) {
        Log.getInstance().debug(TAG, "Enter, getPathAsPoints()");
        List<Location> dline = new ArrayList<Location>();
        for (GisSegment s : navpath) {
            List<Location> tmp = new ArrayList<Location>();
            tmp = divideLine(s, 10);
            dline.addAll(tmp);
        }
        Log.getInstance().debug(TAG, "Exit, getPathAsPoints()");
        return dline;
    }

    public static List<Location> divideLine(GisSegment s, int pixels) {
        Log.getInstance().debug(TAG, "Enter, divideLine()");
        double z = s.getLine().getZ();
        List<Location> result = new ArrayList<Location>();
        double w = s.getWeight();
//		double x1 = s.getLine().getPoint1().getX();
//		double y1 = s.getLine().getPoint1().getY();
//		double x2 = s.getLine().getPoint2().getX();
//		double y2 = s.getLine().getPoint2().getY();
        int count = (int) (w / pixels);
//		Location p2 = new Location((float) x2, (float) y2, (float) z);

        if (count < 1) {
            count = 1;
        }

        for (int i = 0; i <= count; i++) {

            Location p3 = subPoint(s.getLine().getPoint1(), s.getLine()
                    .getPoint2(), i, count, z);

            result.add(p3);
        }
//		result.add(p2);
        Log.getInstance().debug(TAG, "Exit, divideLine()");
        return result;

    }

    public static Location subPoint(GisPoint startPoint, GisPoint endPoint, int segment,
                                    int totalSegments, double z) {

//		float division = (float) ((float) totalSegments / (float) segment);


        float midX = (float) (startPoint.getX() + ((endPoint.getX() - startPoint
                .getX()) / totalSegments) * segment);
        float midY = (float) (startPoint.getY() + ((endPoint.getY() - startPoint
                .getY()) / totalSegments) * segment);

        Location divPoint = new Location(midX, midY, (float) z);

        return divPoint;
    }

    public static List<Location> divideLine(LatLng l1, LatLng l2, int meters) {
        Log.getInstance().debug(TAG, "Enter, divideLine()");
        List<Location> result = new ArrayList<Location>();
        double w = MathUtils.distance(l1, l2);

        int count = (int) (w / meters);
        if (count < 1) {
            count = 1;
        }
        for (int i = 0; i <= count; i++) {

            Location p3 = subPoint(l1, l2, i, count);

            result.add(p3);
        }

        Log.getInstance().debug(TAG, "Exit, divideLine()");
        return result;

    }

    public static Location subPoint(LatLng l1, LatLng l2, int segment,
                                    int totalSegments) {

        double midX = (l1.latitude + ((l2.latitude - l1.latitude) / totalSegments) * segment);
        double midY = (l1.longitude + ((l2.longitude - l1.longitude) / totalSegments) * segment);

        Location divPoint = new Location(new LatLng(midX, midY));

        return divPoint;
    }
}
