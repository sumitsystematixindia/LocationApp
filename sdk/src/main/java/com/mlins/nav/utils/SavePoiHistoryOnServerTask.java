package com.mlins.nav.utils;


import android.os.AsyncTask;

import com.mlins.utils.logging.Log;

import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;

import java.util.ArrayList;
import java.util.List;

//import com.com.mlins.utils.PropertyHolder;

public class SavePoiHistoryOnServerTask extends AsyncTask<String, Void, Void> {

    private final static String TAG = "ccom.mlins.nav.utils.EmailUtil";
    private final String SERVERADDRESS = "";
    private final String SAVE_POI_SEARCH_ACTION = "savepoi";

    public SavePoiHistoryOnServerTask() {
        super();
    }

    @Override
    protected Void doInBackground(String... req) {
        Log.getInstance().debug(TAG, "Enter, doInBackground()");

        try {

            HttpPost httpPostRequest = new HttpPost(SERVERADDRESS);
            HttpParams httpParameters = new BasicHttpParams();
            // Set the timeout in milliseconds until a connection is established.
            // The default value is zero, that means the timeout is not used.
            int timeoutConnection = 10000;
            HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
            // Set the default socket timeout (SO_TIMEOUT)
            // in milliseconds which is the timeout for waiting for data.
            int timeoutSocket = 10000;
            HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);


            //HttpProtocolParams.setHttpElementCharset(httpParameters, HTTP.UTF_8);


            HttpClient httpclient = new DefaultHttpClient(httpParameters);

            // Set HTTP parameters


            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair(SAVE_POI_SEARCH_ACTION, req[0]));

            httpPostRequest.setEntity(new UrlEncodedFormEntity(nameValuePairs, HTTP.UTF_8));

            // Execute HTTP Post Request
            httpclient.execute(httpPostRequest);

//			HttpResponse response = httpclient.execute(httpPostRequest);
//			
//			// Get hold of the response entity (-> the data):
//						HttpEntity entity = response.getEntity();
//
//						if (entity != null) {
//							// Read the content
//							 EntityUtils.toString(entity);
//							
//						}

        } catch (Exception e) {
            Log.getInstance().error(TAG, e.getMessage(), e);
            e.printStackTrace();
        }
        Log.getInstance().debug(TAG, "Exit, doInBackground()");
        return null;
    }


}
