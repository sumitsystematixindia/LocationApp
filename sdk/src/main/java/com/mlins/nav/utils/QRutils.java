package com.mlins.nav.utils;

import com.mlins.utils.PoiData;
import com.mlins.utils.PoiDataHelper;
import com.mlins.utils.gis.Location;
import com.mlins.utils.logging.Log;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public class QRutils {
    private final static String TAG = "ccom.mlins.nav.utils.EmailUtil";

    public static PoiData parseQrResults(String contents, String format) {
        Log.getInstance().debug(TAG, "Enter, parseQrResults()");
        PoiData result = null;
        List<NameValuePair> parameters = new ArrayList<NameValuePair>();
        try {
            parameters = URLEncodedUtils.parse(new URI(contents), "UTF-8");
        } catch (URISyntaxException e) {
            Log.getInstance().error(TAG, e.getMessage(), e);
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        double x = -1;
        double y = -1;
        double z = -10;

        try {
            for (NameValuePair o : parameters) {
                if (o.getName() != null && o.getValue() != null) {
                    if (o.getName().equals("id")) {
                        if (o.getValue() != null && o.getValue() != "") {
                            String id = o.getValue();
                            result = PoiDataHelper.getInstance().findPoiById(id);
                            if (result != null) {
                                return result;
                            }
                        }
                    }
                    if (o.getName().equals("x")) {
                        x = Double.parseDouble(o.getValue());
                        continue;
                    }
                    if (o.getName().equals("y")) {
                        y = Double.parseDouble(o.getValue());
                        continue;
                    }
                    if (o.getName().equals("z")) {
                        z = Double.parseDouble(o.getValue());
                        continue;
                    }
                }
            }
        } catch (NumberFormatException e) {
            Log.getInstance().error(TAG, e.getMessage(), e);
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        if (x != -1 && y != -1 && z != -10) {
            Location loc = new Location();
            loc.setX(x);
            loc.setY(y);
            loc.setZ(z);
            result = PoiDataHelper.getInstance().findPoiFromLoc(loc);
        }
        Log.getInstance().debug(TAG, "Exit, parseQrResults()");
        return result;
    }
}
