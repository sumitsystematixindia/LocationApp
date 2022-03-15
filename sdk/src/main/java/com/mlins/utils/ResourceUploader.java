package com.mlins.utils;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

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
import org.apache.http.util.EntityUtils;

import java.util.ArrayList;
import java.util.List;

public class ResourceUploader extends AsyncTask<String, Void, String> {

    public static final String RESOURCE_SUCCES_TAG = "{\"status\": \"operation succeed\"}";
    private static final String RESOURCE_FAIL_TAG = "{\"status\": \"operation failed\"}";

    @Override
    protected void onPreExecute() {

    }


    @Override
    protected String doInBackground(String... params) {
        String sResponse = "2";

        try {
            String relativeServerAdress = params[0];
            String reqCode = params[1];
            String data = params[2];
            // parse file
            if (data != null) {

                String url = PropertyHolder.getInstance().getServerName() + relativeServerAdress;

                HttpParams httpParameters = new BasicHttpParams();
                // Set the timeout in milliseconds until a connection is
                // established.
                // The default value is zero, that means the timeout is not
                // used.
                int timeoutConnection = 10000;
                HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);

                HttpClient httpClient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost(url);

                List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
                urlParameters.add(new BasicNameValuePair("req", reqCode));
                urlParameters.add(new BasicNameValuePair("data", data));

                httpPost.setEntity(new UrlEncodedFormEntity(urlParameters));

                HttpResponse servletResponse = httpClient.execute(httpPost);

                String resp = EntityUtils.toString(servletResponse.getEntity());

                if (resp.equals(RESOURCE_SUCCES_TAG)) {
                    sResponse = "1";
                } else if (resp.equals(RESOURCE_FAIL_TAG)) {
                    sResponse = "0";
                }
            }
        } catch (Throwable e) {
            sResponse = "2";
            e.printStackTrace();
        }

        return sResponse;
    }

    @Override
    protected void onPostExecute(String result) {
        Context ctx = PropertyHolder.getInstance().getMlinsContext();
        if (result.equals("1")) {
            Toast.makeText(ctx, "succesfully uploaded", Toast.LENGTH_LONG).show();
        } else if (result.equals("0")) {
            Toast.makeText(ctx, "failed", Toast.LENGTH_LONG).show();
        } else if (result.equals("2")) {
            Toast.makeText(ctx, "Network connection failed", Toast.LENGTH_LONG).show();
        }

    }

}
