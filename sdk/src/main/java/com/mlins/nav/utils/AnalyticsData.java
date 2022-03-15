package com.mlins.nav.utils;

import android.os.AsyncTask;
import android.util.Log;

import com.mlins.recorder.WlBlipsRecorder;
import com.mlins.utils.PropertyHolder;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class AnalyticsData {
    JSONObject jsonData = new JSONObject();
    String action;
    String data;

    public AnalyticsData() {
        super();
    }

    public AnalyticsData(String action, String data, String campus, String facility) {

        try {
            jsonData.put("user_id", WlBlipsRecorder.getInstance().getSessionId());
            jsonData.put("data", data);
            jsonData.put("project", PropertyHolder.getInstance().getProjectId());
            jsonData.put("campus", campus);
            jsonData.put("facility", facility);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        this.action = action;
        this.data = data;
        AnalyTheticsDataextends analyTheticsDataextends = new AnalyTheticsDataextends();
        analyTheticsDataextends.execute();
    }

    private class AnalyTheticsDataextends extends AsyncTask<String, Void, String> {
        String servername = PropertyHolder.getInstance().getStreamServerName();
        String url = servername + "analytics?req=1&action=" + action;

        @Override
        protected String doInBackground(String... params) {
            try {
                String text = convertToUTF8(jsonData.toString());
                uploadString(url, text);
            } catch (Throwable e) {
                e.printStackTrace();
            }
            return "";
        }

        public void uploadString(String url, String uploadData) {
            try {

                URL obj = new URL(url);
                HttpURLConnection con = (HttpURLConnection) obj.openConnection();

                // add reuqest header
                con.setRequestMethod("POST");

                con.setRequestProperty("charset", "utf-8");

                con.setRequestProperty("Content-Length", "" + Integer.toString(uploadData.length()));

                int timeout = 5000;
                con.setConnectTimeout(timeout);
                con.setReadTimeout(timeout);

                // Send post request
                con.setDoOutput(true);
                DataOutputStream wr = new DataOutputStream(con.getOutputStream());
                wr.writeBytes(uploadData);
                wr.flush();
                wr.close();

                int responseCode = con.getResponseCode();
//				System.out.println("\nSending 'POST' request to URL : " + url);
//				System.out.println("Post parameters : " + uploadData);
//				System.out.println("Response Code : " + responseCode);

                BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String inputLine;
                StringBuffer response = new StringBuffer();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();
                System.out.println(response.toString());

            } catch (Throwable e) {
                e.printStackTrace();
            }

        }

    }

    public static String convertToUTF8(String s) {
        String out = null;
        try {
            out = new String(s.getBytes("UTF-8"), "ISO-8859-1");
        } catch (java.io.UnsupportedEncodingException e) {
            return null;
        }
        return out;
    }

}
