package com.mlins.nav.location.sharing;

import android.os.AsyncTask;
import android.text.format.DateFormat;

import com.mlins.locationutils.LocationFinder;
import com.mlins.utils.Lookup;
import com.mlins.utils.PropertyHolder;
import com.spreo.interfaces.MyLocationListener;
import com.spreo.nav.enums.LocationMode;
import com.spreo.nav.interfaces.ILocation;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.Random;

public class LocationSharingManager implements MyLocationListener {

    private static LocationSharingManager instance = null;
    private final String SERVER_ADDRESS = PropertyHolder.getInstance().getStreamServerName() + "location?req=0";
    //public static final int REPORTING_FREQ = 10;
    //private int counter = 0;
    private String tempUserId = null;
    private long interval = 120000;
    private long lastUpdateTime = 0;
    private boolean isStarted = false;

    private ReportLimitation locationReportLimitation = ReportLimitation.IN_CAMPUS;

    //hope we don't use it any more, so didn't rework it
    public static LocationSharingManager getInstance() {
        return Lookup.getInstance().get(LocationSharingManager.class);
    }

    public boolean start() {

        if (tempUserId == null) {
            String id = PropertyHolder.getInstance().getSignInUserName();
            if (id == null || id.isEmpty()) {
                tempUserId = generateSessionId();
            } else {
                tempUserId = id;
            }
        }

        LocationFinder.getInstance().subscribeForLocation(this);
//			LocationSharingReporterThread.getInstance().start();
        isStarted = true;
        return isStarted;
    }

    public boolean isStarted(){
        return isStarted;
    }

    private String generateSessionId() {
        String result = null;
        String prefix = "user_";
        int min = 1;
        int max = 100000;
        Random r = new Random();
        int rnum = r.nextInt(max - min + 1) + min;
        Date d = new Date();
        CharSequence s = DateFormat.format("yyyy-MM-dd hh:mm:ss", d.getTime());
        result = prefix + s.toString() + " " + rnum;
        return result;
    }

    public void stop() {
        LocationFinder.getInstance().unsubscibeForLocation(this);
        isStarted = false;
//		LocationSharingReporterThread.getInstance().stopThread();
    }

    @Override
    public void onLocationDelivered(ILocation location) {

////		//if (counter >= REPORTING_FREQ) {
////			
////		//	counter = 0;
//		
//		
//			SharedLocation sLocation = new SharedLocation(tempUserId, location);
//			LocationSharingReporterThread.getInstance().setSharedLoc(sLocation);
//			
////			SendLocationToServerTask task = new SendLocationToServerTask(sLocation);
////			task.execute();
//			
//		//}
//		
//		//counter++;

        long ctime = System.currentTimeMillis();
        if ((lastUpdateTime == 0 || (ctime - lastUpdateTime) > interval) && locationReportLimitation.pass(location)) {
            SharedLocation sLocation = new SharedLocation(tempUserId, location);
            SendLocationToServerTask task = new SendLocationToServerTask(sLocation);
            task.execute();
            lastUpdateTime = ctime;
        }
    }

    @Override
    public void onCampusRegionEntrance(String campusId) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onFacilityRegionEntrance(String campusId, String facilityId) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onFacilityRegionExit(String campusId, String facilityId) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onFloorChange(String campusId, String facilityId, int floor) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onLocationModeChange(LocationMode locationMode) {}

    public void setUserId(String userId) {
        this.tempUserId = userId;
    }

    public void setInterval(long interval) {
        this.interval = interval;
    }

    public void setLocationReportLimitation(ReportLimitation locationReportLimitation) {
        this.locationReportLimitation = locationReportLimitation;
    }

    public ReportLimitation getLocationReportLimitation() {
        return locationReportLimitation;
    }

    // send to server
    private class SendLocationToServerTask extends
            AsyncTask<String, Void, Void> {

        private SharedLocation sharedLoc = null;

        //
        public SendLocationToServerTask(SharedLocation sharedLoc) {
            super();
            this.sharedLoc = sharedLoc;
        }

        @Override
        protected Void doInBackground(String... req) {

            try {

                if (sharedLoc == null) {
                    return null;
                }

                String uploadData = sharedLoc.getAsJsonString();

                // Set the timeout in milliseconds until a connection is established.
                // The default value is zero, that means the timeout is not used.
                int timeoutConnection = 10000;

                URL obj = new URL(SERVER_ADDRESS);
                HttpURLConnection con = (HttpURLConnection) obj.openConnection();
                con.setConnectTimeout(timeoutConnection);

                // add reuqest header
                con.setRequestMethod("POST");

                con.setRequestProperty("charset", "utf-8");

                con.setRequestProperty("Content-Length", Integer.toString(uploadData.length()));

                // Send post request
                con.setDoOutput(true);
                DataOutputStream wr = new DataOutputStream(con.getOutputStream());
                wr.writeBytes(uploadData);
                wr.flush();
                wr.close();

                int responseCode = con.getResponseCode();
                //System.out.println("\nSending 'POST' request to URL : " + SERVER_ADDRESS);
                //System.out.println("Post parameters : " + uploadData);
                //System.out.println("Response Code : " + responseCode);

                BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String inputLine;
                StringBuffer response = new StringBuffer();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();
                // print result
                //System.out.println(response.toString());

            } catch (Throwable e) {
                e.printStackTrace();
                // something went wrong. connection with the server error
            }
            return null;
        }

    }


}
