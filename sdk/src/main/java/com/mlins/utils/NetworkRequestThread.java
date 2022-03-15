package com.mlins.utils;

import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class NetworkRequestThread extends Thread {
    private static final String TAG = null;
    NetworkRequest request;


    public NetworkRequestThread(NetworkRequest req) {
        request = req;

    }

    @Override
    public void run() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        InputStream in = null;

        try {
            URL url = new URL(request.getUrl());

            // Set the timeout in milliseconds until a connection is established.
            // The default value is zero, that means the timeout is not used.
            int timeoutConnection = 10000;
            // Set the default socket timeout (SO_TIMEOUT)
            // in milliseconds which is the timeout for waiting for data.
            int timeoutSocket = 10000;

            URLConnection conn = url.openConnection();
            conn.setConnectTimeout(timeoutConnection);
            conn.setReadTimeout(timeoutSocket);
            in = conn.getInputStream(); //new URL(request.getUrl()).openStream();


            //XXX APK NOT WORKING FAILD ON GLAXSY S
            //	String status  = conn.getHeaderField(null);
            //	if ("HTTP/1.1 200 OK".equals(status))
            //	{
            byte[] buffer = new byte[4096];
            int n = -1;

            while ((n = in.read(buffer)) != -1) {
                if (n > 0) {
                    out.write(buffer, 0, n);
                }
            }
            //	}

        } catch (MalformedURLException e) {
            Log.e(TAG, "Bad url: " + request.getUrl() + ";", e);
        } catch (Throwable e) {
            Log.e(TAG, "", e);
        } finally {
            try {
                if (in != null) in.close();
                if (out != null) out.close();
            } catch (Throwable ignoreMe) {
            }
        }
        request.result = out.toByteArray();

        synchronized (request) {
            request.notifyAll();
        }

    }

}
