package com.mlins.res.setup;

import android.content.Context;
import android.os.AsyncTask;

import com.mlins.utils.Cleanable;
import com.mlins.utils.GalleryObject;
import com.mlins.utils.Lookup;
import com.mlins.utils.PropertyHolder;
import com.mlins.utils.ResourceDownloader;
import com.mlins.utils.ServerConnection;
import com.mlins.utils.logging.Log;
import com.spreo.nav.interfaces.IPoi;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PoiGalleryUpdater implements Cleanable {

    private static final String TAG = "PoiGalleryUpdater";

    private List<GalleryListener> listeners = Collections.synchronizedList(new ArrayList<GalleryListener>());
    private Context context;

    public static PoiGalleryUpdater getInstance() {
        return Lookup.getInstance().get(PoiGalleryUpdater.class);
    }

    public static void releaseInstance() {
        Lookup.getInstance().remove(PoiGalleryUpdater.class);
    }

    public void clean() {
        listeners.clear();
    }

    public boolean registerListener(GalleryListener downloadinglistener) {
        if (!listeners.contains(downloadinglistener)) {
            return listeners.add(downloadinglistener);
        } else {
            return false;
        }
    }

    public boolean unregisterListener(GalleryListener downloadinglistener) {
        if (listeners.contains(downloadinglistener)) {
            return listeners.remove(downloadinglistener);
        } else {
            return false;
        }
    }

    private synchronized void notifyListeners(GalleryUpdateStatus status) {
        try {
            for (GalleryListener listener : listeners) {
                if (listener != null) {
                    listener.onPostDownload(status);
                }
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

//	private synchronized void notifyStartDownloadingListeners() {
//		for (GalleryListener listener : listeners) {
//			listener.onPreConfigsDownload();
//		}
//	}

    public void downloadGallery(IPoi poi, GalleryListener listener) {

        if (listener == null) {
            return;
        }

        registerListener(listener);

        DownloadGalleryFilesTask task = new DownloadGalleryFilesTask();
        task.setPoi(poi);
        task.setTypeToDownload(GalleryObject.GALLERY_TYPE);
        task.execute();
    }


    public void downloadHeadImage(IPoi poi, GalleryListener listener) {


        if (listener == null) {
            return;
        }


        registerListener(listener);

        DownloadGalleryFilesTask task = new DownloadGalleryFilesTask();
        task.setPoi(poi);
        task.setTypeToDownload(GalleryObject.HEAD_TYPE);
        task.execute();
    }


    private class DownloadGalleryFilesTask extends AsyncTask<String[], Void, Boolean> {
        private IPoi poi = null;
        private String typeToDownload = null;

        public void setPoi(IPoi poi) {
            this.poi = poi;
        }


        public void setTypeToDownload(String typeToDownload) {
            this.typeToDownload = typeToDownload;
        }


        @Override
        protected void onPreExecute() {
            Log.getInstance().info("com.mlins.downloading.DownloadCampusesFileTask", "onPreExecute Enter");

            Log.getInstance().info("com.mlins.downloading.DownloadCampusesFileTask", "onPreExecute Exit");
        }

        @Override
        protected Boolean doInBackground(String[]... params) {
            return downloadGallery();
        }

        public boolean downloadGallery() {

            Log.getInstance().debug(TAG, "Enter, download()");
            //notifyStartDownloadingListeners();

            if (poi == null) {
                return false;
            }


            boolean succes = true;
            if (typeToDownload.equals(GalleryObject.HEAD_TYPE)) {     // downloading head image
                try {
                    GalleryObject g = poi.getHeadImage();
                    if (g != null) {
                        String facId = poi.getFacilityID();
                        String uri = g.getUri();
                        String url = ServerConnection.getResourcesUrl() + facId + "/" + uri;
                        ResourceDownloader.getInstance().addMd5(uri, g.getMd5());
                        boolean force = false;
                        if (PropertyHolder.useZip) {
                            force = true;
                        }
                        byte[] imgbytes = ResourceDownloader.getInstance().getLocalCopy(url);

                        if (imgbytes == null || imgbytes.length == 0) {
                            imgbytes = ResourceDownloader.getInstance().getUrl(url, force);
                        }

                        g.setBitmap(imgbytes);

                    } else {
                        succes = false;
                    }
                } catch (Throwable e) {
                    e.printStackTrace();
                    succes = false;
                }
            } else  // downloading gallery images
            {

                List<GalleryObject> galleryList = poi.getGallery();
                for (GalleryObject g : galleryList) {
                    try {
                        String facId = poi.getFacilityID();
                        if (g != null) {
                            //String type= g.getType();
                            //if(type!=null && type.equals(typeToDownload)){
                            String uri = g.getUri();
                            String url = ServerConnection.getResourcesUrl() + facId + "/" + uri;
                            ResourceDownloader.getInstance().addMd5(uri, g.getMd5());
                            boolean force = false;
                            if (PropertyHolder.useZip) {
                                force = true;
                            }
                            byte[] imgbytes = ResourceDownloader.getInstance().getLocalCopy(url);

                            if (imgbytes == null || imgbytes.length == 0) {
                                imgbytes = ResourceDownloader.getInstance().getUrl(url, force);
                            }

                            g.setBitmap(imgbytes);
                            //}
                        }
                    } catch (Throwable e) {
                        e.printStackTrace();
                        succes = false;
                    }

                }

            }
            // save res
            ResourceDownloader.getInstance().saveResFile();


            Log.getInstance().debug(TAG, "Exit, download()");
            return succes;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            Log.getInstance().info("com.mlins.downloading.DownloadCampusesFileTask", "onPostExecute Enter");
            if (result) {
                notifyListeners(GalleryUpdateStatus.OK);
            } else {
                notifyListeners(GalleryUpdateStatus.FAILED);
            }


            Log.getInstance().info("com.mlins.downloading.DownloadCampusesFileTask", "onPostExecute Exit");

        }
    }


}
