package gps;

import com.google.android.gms.maps.model.LatLng;
import com.mlins.aStar.GisSegment;
import com.mlins.utils.CampusLevelResDownloader;
import com.mlins.utils.Lookup;
import com.mlins.utils.MathUtils;
import com.mlins.utils.PropertyHolder;
import com.mlins.utils.ServerConnection;
import com.mlins.utils.gis.GisData;
import com.mlins.utils.gis.GisLine;
import com.mlins.utils.gis.GisPoint;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class CampusGisData extends GisData {

    private static final String filename = "campus.kml";
    private List<GisSegment> campusRoute = new ArrayList<GisSegment>();

    public static CampusGisData getInstance() {
        return Lookup.getInstance().get(CampusGisData.class);
    }

    public static void releaseInstance() {
        Lookup.getInstance().remove(CampusGisData.class);
    }

    public void clean() {
        getLines().clear();
        getCampusRoute().clear();
    }


    public void loadGis() {
        try {
            InputStream is = null;

            if (PropertyHolder.useZip) {
                String url = ServerConnection.getResourcesUrl() + filename;

                byte[] bytes = CampusLevelResDownloader.getCInstance().getLocalCopy(url);
                is = new ByteArrayInputStream(bytes);

            } else {
                File dir = PropertyHolder.getInstance().getCampusDir();
                File file = new File(dir, filename);
                is = new FileInputStream(file.getPath());
            }

            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(new InputSource(is));
            doc.getDocumentElement().normalize();

            NodeList nodeList = doc.getElementsByTagName("coordinates");

            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                Node parent = node.getParentNode();
                if (parent.getNodeName().equals("LineString")) {
                    String campusline = node.getTextContent();
                    GisLine l = convertToGisLine(campusline);
                    getLines().add(l);
                }
            }

        } catch (Exception e) {
            System.out.println("XML Pasing Excpetion = " + e);
        }
    }

    private GisLine convertToGisLine(String campusline) {
        GisLine result = null;
        int z = 0;
        String[] vals = campusline.trim().split("\\s+");
        String[] fields = vals[0].split(",");
        double x1 = Double.parseDouble(fields[0]);
        double y1 = Double.parseDouble(fields[1]);
        GisPoint p1 = new GisPoint(x1, y1, z);
        fields = vals[1].split(",");
        double x2 = Double.parseDouble(fields[0]);
        double y2 = Double.parseDouble(fields[1]);
        GisPoint p2 = new GisPoint(x2, y2, z);
        result = new GisLine(p1, p2, z);
        return result;
    }

    public LatLng findClosestPointOnLine(LatLng p) {
        LatLng result = null;
        if (getLines().size() == 0) {
            return p;
        }

        List<GisLine> plines = new ArrayList<GisLine>();
        if (getCampusRoute().size() > 0) {
            List<GisLine> campusroutelines = new ArrayList<GisLine>();
            for (GisSegment o : campusRoute) {
                GisLine rline = o.getLine();
                campusroutelines.add(rline);
            }
            plines = campusroutelines;
        } else {
            plines = getLines();
        }

        double min = 1000000.0;
        for (GisLine l : plines) {
            LatLng point = MathUtils.findClosestPointOnSegment(p, l);
            double distance = MathUtils.distance(p, point);
            if (distance <= min) {
                result = point;
                min = distance;
            }

        }

        return result;
    }

    public LatLng findClosestPointOnLine(LatLng p, List<GisSegment> segments) {
        LatLng result = null;

        if (segments.size() == 0) {
            return p;
        }

        List<GisLine> plines = new ArrayList<GisLine>();
        List<GisLine> campusroutelines = new ArrayList<GisLine>();
        for (GisSegment o : segments) {
            GisLine rline = o.getLine();
            campusroutelines.add(rline);
        }
        plines = campusroutelines;

        double min = 1000000.0;
        for (GisLine l : plines) {
            LatLng point = MathUtils.findClosestPointOnSegment(p, l);
            double distance = MathUtils.distance(p, point);
            if (distance <= min) {
                result = point;
                min = distance;
            }

        }

        return result;
    }

    public List<GisSegment> getCampusRoute() {
        return campusRoute;
    }

    public void setCampusRoute(List<GisSegment> campusRoute) {
        this.campusRoute = campusRoute;
    }

    public void clearCampusRoute() {
        campusRoute.clear();
    }

}
