package com.mlins.nav.location.sharing;

import com.mlins.utils.PropertyHolder;
import com.mlins.utils.gis.Location;
import com.spreo.interfaces.ILocationSharingUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class LocationSharingUserSThread extends Thread {
    public static final int Stop = 0;
    public static final int Play = 1;
    public static final int Pause = 2;
    private int state;
    private boolean mStarted;
    private boolean mRunning = true;

    public LocationSharingUserSThread() {
        super();
        start();
    }

    public void destroy() {
        try {

            System.out.println("LocationSharingUserSThread destroyed...");
            this.stopThread();

        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    @Override
    public void run() {

        while (mRunning) {

            long delay = PropertyHolder.getInstance().getRefreshUsersInterval();

            try {

                UpdateUsersFromServer();

                sleep(delay);

            } catch (Throwable e) {
                e.printStackTrace();
            }

        }
        mStarted = false;

    }

    private void UpdateUsersFromServer() {
        String servername = PropertyHolder.getInstance().getStreamServerName();
        String projectid = PropertyHolder.getInstance().getProjectId();
        String url = servername + "location?req=3" + "&pid=" + projectid;
        JSONObject idlistjson = LocationSharingUsersManger.getInstance().getIdListAsJson();
        if (idlistjson != null) {
            try {
                String uploadData = idlistjson.toString();
                URL obj = new URL(url);
                HttpURLConnection con = (HttpURLConnection) obj.openConnection();

                // add reuqest header
                con.setRequestMethod("POST");

                con.setRequestProperty("charset", "utf-8");

                con.setRequestProperty("Content-Length", "" + Integer.toString(uploadData.length()));

                // Send post request
                con.setDoOutput(true);
                DataOutputStream wr = new DataOutputStream(con.getOutputStream());
                wr.writeBytes(uploadData);
                wr.flush();
                wr.close();

                int responseCode = con.getResponseCode();

                BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String inputLine;
                StringBuffer response = new StringBuffer();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                parseJson(response.toString());

            } catch (Throwable e) {
                e.printStackTrace();
            }
        }

    }

    private void parseJson(String jsonTxt) {
        List<ILocationSharingUser> users = new ArrayList<ILocationSharingUser>();
        try {
            JSONObject usersjson = new JSONObject(jsonTxt);
            JSONArray customerslocation = usersjson.getJSONArray("customerslocation");
            for (int i = 0; i < customerslocation.length(); i++) {
                JSONObject userj = customerslocation.getJSONObject(i);
                LocationSharingUser ls = parseUser(userj);
                if (ls != null) {
                    users.add(ls);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        LocationSharingUsersManger.getInstance().updateUsers(users);

    }

    private LocationSharingUser parseUser(JSONObject userjson) {
        LocationSharingUser result = null;
        String id = null;
        try {
            id = userjson.getString("id");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (id != null) {
            result = new LocationSharingUser(id);
        }

        Location loc = new Location();

        try {
            loc.setCampusId(userjson.getString("campus"));
            loc.setFacilityId(userjson.getString("facility"));
            loc.setType(userjson.getInt("mode"));
            loc.setX(userjson.getDouble("x"));
            loc.setY(userjson.getDouble("y"));
            loc.setZ(userjson.getDouble("floor"));
            loc.setLat(userjson.getDouble("lat"));
            loc.setLon(userjson.getDouble("lon"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        result.setLocation(loc);

        return result;
    }

    public boolean isRunning() {
        return mRunning;
    }

    public void setRunning(boolean mRunning) {
        this.mRunning = mRunning;
    }

    @Override
    public void start() {
        System.out.println("LocationSharingUserSThread started...");
        mStarted = true;
        mRunning = true;

        super.start();
    }

    public boolean getIsStarted() {
        return mStarted;
    }

    public void stopThread() {
        state = Stop;
        mRunning = false;

    }

}
