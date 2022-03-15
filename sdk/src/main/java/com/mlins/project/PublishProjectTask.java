package com.mlins.project;

import android.os.AsyncTask;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
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
import org.apache.http.util.EntityUtils;

import java.util.ArrayList;
import java.util.List;

public class PublishProjectTask extends AsyncTask<String, Void, Void> {


    private static final String REQ = "req";
    private static final String REQ_JSON = "content";
    private static final String REQ_BUILD_FAC_CONFS = "1";
    //private final String SERVERADDRESS = PropertyHolder.getInstance().getServerName()+"confbuild";
    private final String SERVERADDRESS = "";

    public PublishProjectTask() {
        super();
    }

    @Override
    protected Void doInBackground(String... content) {

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
            nameValuePairs.add(new BasicNameValuePair(REQ, REQ_BUILD_FAC_CONFS));
            nameValuePairs.add(new BasicNameValuePair(REQ_JSON, content[0]));

            httpPostRequest.setEntity(new UrlEncodedFormEntity(nameValuePairs, HTTP.UTF_8));

            // Execute HTTP Post Request
            httpclient.execute(httpPostRequest);

            HttpResponse response = httpclient.execute(httpPostRequest);

            // Get hold of the response entity (-> the data):
            HttpEntity entity = response.getEntity();

            if (entity != null) {
                // Read the content
                EntityUtils.toString(entity);

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
