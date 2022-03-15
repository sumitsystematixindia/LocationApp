package com.mlins.res.setup;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;

import com.google.android.gms.maps.model.LatLng;
import com.mlins.utils.Cleanable;
import com.mlins.utils.Lookup;
import com.mlins.utils.ProjectData;
import com.mlins.utils.PropertyHolder;
import com.mlins.utils.ServerConnection;
import com.spreo.interfaces.ProjectsDataListener;
import com.spreo.nav.interfaces.IProject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ProjectsDataProvider implements Cleanable {

    private static final String TAG = "ProjectsDataProvider";

    private List<ProjectsDataListener> listeners = Collections
            .synchronizedList(new ArrayList<ProjectsDataListener>());
    private Context context;

    public static ProjectsDataProvider getInstance() {
        return Lookup.getInstance().get(ProjectsDataProvider.class);
    }

    public static void releaseInstance() {
        Lookup.getInstance().remove(ProjectsDataProvider.class);
    }

    public void clean() {
        listeners.clear();
    }

    public boolean registerListener(ProjectsDataListener downloadinglistener) {
        if (!listeners.contains(downloadinglistener)) {
            return listeners.add(downloadinglistener);
        } else {
            return false;
        }
    }

    public boolean unregisterListener(ProjectsDataListener downloadinglistener) {
        if (listeners.contains(downloadinglistener)) {
            return listeners.remove(downloadinglistener);
        } else {
            return false;
        }
    }

    private synchronized void notifyStartDownloadingListeners() {

        Handler mainHandler = new Handler(context.getMainLooper());

        Runnable myRunnable = new Runnable() {

            @Override
            public void run() {
                try {
                    for (ProjectsDataListener listener : listeners) {
                        listener.onPreProjectsDownload();
                    }
                } catch (Throwable t) {
                    t.printStackTrace();
                }
            }
        };
        mainHandler.post(myRunnable);

    }

    private synchronized void notifyDataReceivedListeners(
            final List<IProject> list) {

        Handler mainHandler = new Handler(context.getMainLooper());

        Runnable myRunnable = new Runnable() {

            @Override
            public void run() {
                try {
                    for (ProjectsDataListener listener : listeners) {
                        List<IProject> copy = new ArrayList<IProject>(
                                list);
                        listener.onProjectsDataRecieved(copy);
                    }
                } catch (Throwable t) {
                    t.printStackTrace();
                }
            }
        };
        mainHandler.post(myRunnable);

    }

    public void getProjectsList(Context ctx, String key, LatLng latlng,
                                Double radius, ProjectsDataListener listener) {

        if (listener == null || ctx == null) {
            return;
        }

        this.context = ctx;

        registerListener(listener);

        DownloadTask task = new DownloadTask();
        task.setKey(key);
        if (latlng != null) {
            task.setLat(latlng.latitude);
            task.setLon(latlng.longitude);
        }
        task.setRadius(radius);
        task.execute();
    }


    private class DownloadTask extends AsyncTask<String[], Void, Boolean> {

        private String key = null;
        private Double lat = null;
        private Double lon = null;
        private Double radius = null;
        private List<IProject> list = null;

        public void setKey(String key) {
            this.key = key;
        }

        public void setLat(Double lat) {
            this.lat = lat;
        }

        public void setLon(Double lon) {
            this.lon = lon;
        }

        public void setRadius(Double radius) {
            this.radius = radius;
        }

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected Boolean doInBackground(String[]... params) {
            return download();
        }

        public boolean download() {

            boolean succes = true;

            try {

                String url = PropertyHolder.getInstance().getServerName()
                        + "apps_res?req=7&key=" + key;

                if (lat != null && lon != null) {
                    url += "&lat=" + lat + "&lon=" + lon;
                }

                if (radius != null) {
                    url += "&radius=" + radius;
                }

                notifyStartDownloadingListeners();

                byte[] bytes = ServerConnection.getInstance().getResourceBytes(url);

                String json = new String(bytes);

                list = ProjectData.parseJson(json);

            } catch (Throwable e) {
                e.printStackTrace();
                succes = false;
            }

            return succes;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);

            if (list == null) {
                list = new ArrayList<IProject>();
            }
            notifyDataReceivedListeners(list);

        }
    }

}
