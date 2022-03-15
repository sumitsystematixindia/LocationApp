package com.mlins.nav.location.sharing;

import com.mlins.utils.PropertyHolder;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class LocationSharingReporterThread extends Thread {

    final static long DELAY = 3000;
    private static LocationSharingReporterThread instance = null;
    private final String SERVER_ADDRESS = PropertyHolder.getInstance().getStreamServerName() + "location?req=0";
    public boolean mRunning = true;
    //public static final int Stop = 0;
    //public static final int Play = 1;
    //public static final int Pause = 2;
    //private int state;
    private boolean mStarted;
//	private final String SERVER_ADDRESS = "http://10.100.102.4:8080/projSrv/location?req=0";
    private SharedLocation sharedLoc = null;

    private LocationSharingReporterThread() {
        super("LocationSharingReporterThread");
    }

    // hope we don't use it any more, so didn't rework it
    public static LocationSharingReporterThread getInstance() {
        if (instance == null) {
            instance = new LocationSharingReporterThread();
        }
        return instance;
    }

    public SharedLocation getSharedLoc() {
        return sharedLoc;
    }

    public void setSharedLoc(SharedLocation sharedLoc) {
        this.sharedLoc = sharedLoc;
    }

    @Override
    public void run() {

        while (mRunning) {

            try {

                Thread.sleep(DELAY);

                sendToServer();

            } catch (Throwable e) {

                e.printStackTrace();
            }


        }
        mStarted = false;
    }


    private void sendToServer() {
        try {

            if (sharedLoc == null) {
                return;
            }

            String uploadData = sharedLoc.getAsJsonString();

            // Set the timeout in milliseconds until a connection is established.
            // The default value is zero, that means the timeout is not used.
            int timeoutConnection = 60000;

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
    }

    public boolean isRunning() {
        return mRunning;
    }

    public void setRunning(boolean mRunning) {
        this.mRunning = mRunning;
    }

    @Override
    public synchronized void start() {
        mStarted = true;
        mRunning = true;
        try {
            super.start();
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    public boolean getIsStarted() {
        return mStarted;
    }

    public void stopThread() {
        //state = Stop;
        mRunning = false;
    }
}

