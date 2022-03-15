package com.mlins.dualmap;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.mlins.locationutils.LocationFinder;
import com.mlins.utils.PoiData;
import com.mlins.utils.PropertyHolder;
import com.mlins.utils.gis.Location;
import com.spreo.enums.NavigationResultStatus;
import com.spreo.nav.enums.LocationMode;
import com.spreo.nav.interfaces.ILocation;
import com.spreo.nav.interfaces.IPoi;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class ServerNavigationTask extends AsyncTask<String, String, NavigationResultStatus> {

    private Context ctx = null;
    private NavCalculationListener navCalculationListener = null;
    private Location origin = null;
    private PoiData destination = null;
    private Location destloc = null;
    private boolean isSimulation = false;
    private boolean isReroute = false;
//    String url = "https://sandbox.spreo.co:443/rest/navi/build-a-star?lang=en"; // "https://developer.spreo.co:443/rest/navi/build?lang=en"; //servername + "rest/navi/build?lang=en";
//    String url = "https://sandbox.spreo.co:443/rest/navi/build-a-star-using-virtual-paths?lang=en";
    String url = PropertyHolder.getInstance().getRestServerName() + "navi/build-a-star-using-virtual-paths" + "?lang=" + getLanguage();

    public ServerNavigationTask(Context ctx, NavCalculationListener navCalculationListener, Location origin, PoiData destination) {
        super();
        this.ctx = ctx;
        this.navCalculationListener = navCalculationListener;
        this.origin = origin;
        this.destination = destination;
    }

    private String getLanguage() {
        String result = "en";
        if (PropertyHolder.getInstance().getAppLanguage().equals("hebrew")) {
            result = "he";
        } else if (PropertyHolder.getInstance().getAppLanguage().equals("arabic")) {
            result = "ar";
        } else if (PropertyHolder.getInstance().getAppLanguage().equals("russian")) {
            result = "ru";
        } else if (PropertyHolder.getInstance().getAppLanguage().equals("spanish")) {
            result = "es";
        }
        return result;
    }

    public ServerNavigationTask(Context ctx, NavCalculationListener navCalculationListener, Location origin, Location destloc) {
        super();
        this.ctx = ctx;
        this.navCalculationListener = navCalculationListener;
        this.origin = origin;
        this.destloc = destloc;
    }

    @Override
    protected void onPreExecute() {

    }

    @Override
    protected NavigationResultStatus doInBackground(String... req) {
//        android.os.Debug.waitForDebugger();

        NavigationResultStatus status = NavigationResultStatus.FAILED_NETWORK;

        try {

            JSONObject json = getRequestJson();
            if (PropertyHolder.getInstance().isHandicappedRouting()) {
                json.put("handicapped", true);
            } else {
                json.put("handicapped", false);
            }
            if (PropertyHolder.getInstance().isStaffRouting()) {
                json.put("authorizationLevel", 1);
            } else {
                json.put("authorizationLevel", 0);
            }

            String uploadData = json.toString();

            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();

            // add reuqest header
            con.setRequestMethod("POST");

            con.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
            con.setRequestProperty("Accept","application/json");

            con.setRequestProperty("Content-Length", "" + Integer.toString(uploadData.length()));

            int timeout = 10000;
            con.setConnectTimeout(timeout);
            con.setReadTimeout(timeout);

            // Send post request
            con.setDoOutput(true);
            DataOutputStream wr = new DataOutputStream(con.getOutputStream());
            wr.writeBytes(uploadData);
            wr.flush();
            wr.close();

            int code = con.getResponseCode();

            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            String result = response.toString();

            if (!result.isEmpty()) {
                status = NavigationResultStatus.SUCCEED;
                RouteCalculationHelper.getInstance().calculatePathFromServer(result, destination, isReroute);
            } else {
                status = NavigationResultStatus.FAILED_SERVER;
            }

        } catch (Throwable e) {
            e.printStackTrace();
        }

        return status;
    }

    @Override
    protected void onPostExecute(NavigationResultStatus status) {
        super.onPostExecute(status);
        if (navCalculationListener != null) {
            try {
                navCalculationListener.OnNavigationCalculationFinished(isSimulation, status, isReroute);
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }
    }


    private JSONObject getRequestJson() {
        JSONObject result = new JSONObject();

        ILocation dloc = null;
        if (destloc != null) {
            dloc = destloc;
        } else {
            dloc = new Location(destination);
            dloc.setCampusId(destination.getCampusID());
        }

        JSONObject from = getLocationAsJson(origin);
        JSONObject to = getLocationAsJson(dloc);
        try {
            result.put("pid", PropertyHolder.getInstance().getProjectId());
            result.put("from", from);
            result.put("to", to);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }


    public JSONObject getLocationAsJson(ILocation loc) {
        JSONObject jsonObj = new JSONObject();

        int mode = 0;
        LocationMode lmode = loc.getLocationType();
        if (lmode == LocationMode.OUTDOOR_MODE) {
            mode = 1;
        }

        try {

            try {
                jsonObj.put("x", loc.getX());
            } catch (Throwable t) {
                t.printStackTrace();
            }

            try {
                jsonObj.put("y", loc.getY());
            } catch (Throwable t) {
                t.printStackTrace();
            }

            try {
                jsonObj.put("latitude", loc.getLat());
            } catch (Throwable t) {
                t.printStackTrace();
            }

            try {
                jsonObj.put("longitude", loc.getLon());
            } catch (Throwable t) {
                t.printStackTrace();
            }

            try {
                jsonObj.put("floor", loc.getZ());
            } catch (Throwable t) {
                t.printStackTrace();
            }

            try {
                jsonObj.put("cid", loc.getCampusId());
            } catch (Throwable t) {
                t.printStackTrace();
            }

            try {
                jsonObj.put("fid", loc.getFacilityId());
            } catch (Throwable t) {
                t.printStackTrace();
            }

            try {
                jsonObj.put("mode", mode);
            } catch (Throwable t) {
                t.printStackTrace();
            }


        } catch (Throwable t) {
            t.printStackTrace();
        }

        return jsonObj;
    }

    public void setSimulation(boolean simulation) {
        isSimulation = simulation;
    }


    public void setReroute(boolean reroute) {
        isReroute = reroute;
    }
}
