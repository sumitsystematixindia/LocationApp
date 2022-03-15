package com.mlins.recorder;

import android.graphics.PointF;
import android.os.AsyncTask;
import android.text.format.DateFormat;

import com.mlins.utils.PropertyHolder;
import com.mlins.wireless.WlBlip;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class WlBlipsRecorder {

    private static String sessionId = null;
    private static WlBlipsRecorder instance = null;
    private ArrayList<LocWlBlipsObj> recList = null;
    private int THRESHOLD_SIZE = 1;
    private String recFileName = "locblipsrec.txt";
    private String recDirName = "userblipsrec";
    private long recordingInterval = 1500;
    private long lastRecordTime = 0;

    public WlBlipsRecorder() {
        super();
        recList = new ArrayList<LocWlBlipsObj>();
    }

    //doesn't hold project specific state
    public static WlBlipsRecorder getInstance() {
        if (instance == null) {
            instance = new WlBlipsRecorder();
            generateSessionId();
        }

        return instance;
    }

    private static void generateSessionId() {
        String prefix = "cwdspreo_";
        if (sessionId == null) {
            int min = 1;
            int max = 100000;
            Random r = new Random();
            int rnum = r.nextInt(max - min + 1) + min;
            Date d = new Date();
            CharSequence s = DateFormat.format("yyyy-MM-dd hh:mm:ss", d.getTime());
            sessionId = prefix + s.toString() + " " + rnum;
        }
    }

    public void record(String projectid, String campusid, String facilityid, PointF p, int floor, List<WlBlip> blips) {

        recList.add(new LocWlBlipsObj(projectid, campusid, facilityid, p, floor, blips));
        long currenttime = System.currentTimeMillis();

        if (currenttime - lastRecordTime > recordingInterval) {
            WriteRecordsToFile();
            recList.clear();
            sendToServer();
            lastRecordTime = currenttime;
        }
    }

    private void sendToServer() {

        try {
            SendBlipsToServerTask sendToServerTask = new SendBlipsToServerTask();
            sendToServerTask.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void WriteRecordsToFile() {

        File appDir = PropertyHolder.getInstance().getAppDir();
        File recdir = new File(appDir, recDirName);
        File recFile = new File(recdir, recFileName);

        if (!recdir.exists()) {
            recdir.mkdirs();
        }

        if (!recFile.exists()) {
            try {

                recFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        BufferedWriter out = null;

        try {

            JSONObject jsonRawData = new JSONObject();
            JSONArray rawdataArray = new JSONArray();

            for (LocWlBlipsObj dot : recList) {
                JSONObject jsonObj = new JSONObject();
                JSONArray dataArray = new JSONArray();
                dataArray.put(dot.toJson());
                jsonObj.put("sessionId", sessionId);
                jsonObj.put("data", dataArray);
                rawdataArray.put(jsonObj);
            }
            jsonRawData.put("rawdata", rawdataArray);


            out = new BufferedWriter(new FileWriter(recFile, false));
            out.write(jsonRawData.toString(2));

        } catch (Throwable e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.flush();
                    out.close();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
            out = null;
        }

    }

//	public JSONObject getListAsJson() {
//		
//		JSONObject result = new JSONObject();
//		JSONArray dataArray = new JSONArray();
//		
//		for (LocWlBlipsObj dot : recList) {
//			dataArray.put(dot.toJson());
//		}
//		
//		try {
//			result.put("sessionId", sessionId);
//			result.put("data", dataArray);
//		} catch (JSONException e) {
//			e.printStackTrace();
//		}
//		
//		return result;
//		
//	}

    public void setTHRESHOLD_SIZE(int tHRESHOLD_SIZE) {
        THRESHOLD_SIZE = tHRESHOLD_SIZE;
    }

    private String getBlipsFromFile() {

        File appDir = PropertyHolder.getInstance().getAppDir();
        File recdir = new File(appDir, recDirName);
        File recFile = new File(recdir, recFileName);
        Scanner scanner = null;
        StringBuffer content = new StringBuffer();

        try {
            scanner = new Scanner(recFile, "UTF-8");
            while (scanner.hasNext()) {
                content.append(scanner.nextLine());
            }

        } catch (Throwable e) {
            e.printStackTrace();
        } finally {
            if (scanner != null)
                scanner.close();
        }
        return content.toString();
    }

    public long getRecordingInterval() {
        return recordingInterval;
    }

    public void setRecordingInterval(long recordingInterval) {
        this.recordingInterval = recordingInterval;
    }


    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        WlBlipsRecorder.sessionId = sessionId;
    }

    public long getLastRecordTime() {
        return lastRecordTime;
    }

    public void setLastRecordTime(long lastRecordTime) {
        this.lastRecordTime = lastRecordTime;
    }

    private class SendBlipsToServerTask extends AsyncTask<String, Void, Void> {

        private final String SERVERADDRESS = PropertyHolder.getInstance().getServerName() + "beacons";
//		private final String SERVERADDRESS = "http://10.100.102.10:8080/projSrv/beacons";
//		private final String SEND_BLIPS_ACTION = "update";

        public SendBlipsToServerTask() {
            super();
        }

        @Override
        protected Void doInBackground(String... req) {

            try {

                String recordData = getBlipsFromFile();
                uploadString(SERVERADDRESS, recordData);

            } catch (Throwable e) {
                e.printStackTrace();
            }
            return null;
        }


        public void uploadString(String url, String uploadData) {
            try {

                URL obj = new URL(url);
                HttpURLConnection con = (HttpURLConnection) obj.openConnection();

                // add reuqest header
                con.setRequestMethod("POST");

                con.setRequestProperty("charset", "utf-8");

                con.setRequestProperty("Content-Length",
                        "" + Integer.toString(uploadData.length()));

                // Send post request
                con.setDoOutput(true);
                DataOutputStream wr = new DataOutputStream(con.getOutputStream());
                wr.writeBytes(uploadData);
                wr.flush();
                wr.close();

                int responseCode = con.getResponseCode();
                System.out.println("\nSending 'POST' request to URL : " + url);
                System.out.println("Post parameters : " + uploadData);
                System.out.println("Response Code : " + responseCode);

                BufferedReader in = new BufferedReader(new InputStreamReader(
                        con.getInputStream()));
                String inputLine;
                StringBuffer response = new StringBuffer();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                // print result
                System.out.println(response.toString());

            } catch (Throwable e) {
                e.printStackTrace();
                // something went wrong. connection with the server error
            }

        }

    }

}
