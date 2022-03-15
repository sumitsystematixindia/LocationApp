package com.mlins.nav.utils;

import android.os.AsyncTask;

import com.mlins.utils.logging.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.util.ArrayList;
import java.util.List;

public class EventsPostMassegesTask extends AsyncTask<String, Void, String> {//<Params, Progress, Result>{
    private final static String TAG = "ccom.mlins.nav.utils.EventsPostMassegesTask";

    private Exception exception;

    private EventsToServerProvider slip = null;


    public EventsPostMassegesTask(EventsToServerProvider slip) {
        super();
        this.slip = slip;
    }

    protected String doInBackground(String... urls) {
        Log.getInstance().debug(TAG, "Enter, doInBackground()");
        String serverResult = "";
        try {

            HttpPost httpPostRequest = new HttpPost(urls[0]);

            HttpClient httpclient = new DefaultHttpClient();
            // Set HTTP parameters

            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("event_title", slip.getEvent_title()));
            nameValuePairs.add(new BasicNameValuePair("event_description", slip.getEvent_description()));
            nameValuePairs.add(new BasicNameValuePair("event_start", slip.getEvent_start()));
            nameValuePairs.add(new BasicNameValuePair("event_end", slip.getEvent_end()));
            nameValuePairs.add(new BasicNameValuePair("event_picture_path", slip.getEvent_picture_path()));
            nameValuePairs.add(new BasicNameValuePair("event_picture_file", slip.getEvent_picture_file()));
            nameValuePairs.add(new BasicNameValuePair("email_address", slip.getEmail_address()));


            httpPostRequest.setEntity(new UrlEncodedFormEntity(nameValuePairs));

            // Execute HTTP Post Request

            HttpResponse response = (HttpResponse) httpclient
                    .execute(httpPostRequest);

            // Get hold of the response entity (-> the data):
            HttpEntity entity = response.getEntity();

            if (entity != null) {
                // Read the content
                serverResult = EntityUtils.toString(entity);
                return serverResult;
            }
        } catch (Exception e) {
            Log.getInstance().error(TAG, e.getMessage(), e);
            this.exception = e;
            return null;
        }
        Log.getInstance().debug(TAG, "Exit, doInBackground()");
        return serverResult;
    }

    protected void onPostExecute(String sessionId) {
        // TODO: check this.exception 
        // TODO: do something with the sessionId
    }
}

