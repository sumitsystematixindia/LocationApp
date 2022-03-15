package com.mlins.dualmap;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.TypedValue;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterItem;
import com.mlins.project.ProjectConf;
import com.mlins.utils.FacilityConf;
import com.mlins.utils.PoiData;
import com.mlins.utils.PropertyHolder;
import com.spreo.nav.interfaces.IPoi;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class PoisClusterHelper {

    private static final String TAG = PoisClusterHelper.class.getName();

    private final int LABEL_WIDTH_DP = 80;
    private final int labelWidthPX;
    private Context context = null;
    private PoisClusterListener listener = null;
    List<IPoi> currentPois = new ArrayList<>();
    private List<IPoi> allPOis = new ArrayList<>();
    float maxZoomLevel;
    poisClusterAlgorithm fullPoisClusterAlgorithm = null;
    poisClusterAlgorithm LowZoomPoisClusterAlgorithm = null;
    HashMap<LatLng, IPoi> poisMap = new HashMap<>();
    PoisClusterTask poisClusterTask;
    private final ReadWriteLock mClusterTaskLock = new ReentrantReadWriteLock();
    private final ReadWriteLock mAlgorithmLock = new ReentrantReadWriteLock();
    private boolean isLoadingFloor = false;
    private float hidingAllZoomLevel = 17.5f;


    public PoisClusterHelper(Context context, PoisClusterListener listener, float maxZoomLevel) {
        this.context = context;
        this.maxZoomLevel = maxZoomLevel;
        this.listener = listener;
        labelWidthPX = Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, LABEL_WIDTH_DP, context.getResources().getDisplayMetrics()));
    }

    public float getHidingAllZoomLevel() {
        return hidingAllZoomLevel;
    }

    public void setHidingAllZoomLevel(float hidingAllZoomLevel) {
        this.hidingAllZoomLevel = hidingAllZoomLevel;
    }

    public void setPOIs(final float currentZoom, FloorDisplayPolicy floorDisplayPolicy) {

        isLoadingFloor = true;

        clearPois();

        Collection allzoomlisct = new ArrayList();
        Collection lowZoomlisct = new ArrayList();

        List<IPoi> poilist = ProjectConf.getInstance().getAllPoisList();

        List<IPoi> availableFloorPOIs = new ArrayList<>();

        for (IPoi o : poilist) {
            if (PoiData.isInternal(o) && o.isShowPoiOnMap() && o.isVisible()) {
                double floor = o.getZ();
                FacilityConf facility = ProjectConf.getInstance().getFacilityConfById(o);
                if (facility != null && floorDisplayPolicy.displayFloorContent(facility, (int) floor)) {

                    availableFloorPOIs.add(o);
                    poisMap.put(new LatLng(o.getPoiLatitude(), o.getPoiLongitude()), o);
                    PoiMarkerItem offsetItem = new PoiMarkerItem(o.getPoiLatitude(), o.getPoiLongitude());
                    allzoomlisct.add(offsetItem);

                    if (o.isShowOnZoomLevel()) {
                        lowZoomlisct.add(offsetItem);
                    }

                }
            } else if (!PoiData.isInternal(o) && o.isShowPoiOnMap() && o.isVisible()) {
                PoiMarkerItem offsetItem = new PoiMarkerItem(o.getPoiLatitude(), o.getPoiLongitude());
                allzoomlisct.add(offsetItem);
                availableFloorPOIs.add(o);
                poisMap.put(new LatLng(o.getPoiLatitude(), o.getPoiLongitude()), o);

                if (o.isShowOnZoomLevel()) {
                    lowZoomlisct.add(offsetItem);
                }

            }

        }

        allPOis = availableFloorPOIs;

        fullPoisClusterAlgorithm = new poisClusterAlgorithm();
        fullPoisClusterAlgorithm.setMaxDistanceBetweenClusteredItems(120);
        fullPoisClusterAlgorithm.addItems(allzoomlisct);

        if (PropertyHolder.getInstance().isPoisZoomFiltering() && !lowZoomlisct.isEmpty()) {
            LowZoomPoisClusterAlgorithm = new poisClusterAlgorithm();
            LowZoomPoisClusterAlgorithm.setMaxDistanceBetweenClusteredItems(120);
            LowZoomPoisClusterAlgorithm.addItems(lowZoomlisct);
        } else {
            LowZoomPoisClusterAlgorithm = null;
        }

        cluster(currentZoom);

    }

    private void cluster(float currentZoom) {
        mClusterTaskLock.writeLock().lock();
        try {
            // Attempt to cancel the in-flight request.
            if (poisClusterTask != null) {
                poisClusterTask.cancel(true);
            }
            poisClusterTask = new PoisClusterTask();

            poisClusterTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, currentZoom);

        } finally {
            mClusterTaskLock.writeLock().unlock();
        }
    }

    private LatLng getLatLng(IPoi poi) {
        return new LatLng(poi.getPoiLatitude(), poi.getPoiLongitude());
    }

    public MarkerOptions getPoiMarkerOptions(IPoi poi, boolean showlabel) {
        MarkerOptions options = null;
        try {
            options = new MarkerOptions();
            Bitmap icon = PoiData.getIcon(context, poi);
            if(poi.shouldDisplayLabel() && showlabel && PropertyHolder.getInstance().displayLabelsForPOIs()) {
                Bitmap iconWithLabel = IconWithLabelGenerator.generate(icon, poi.getpoiDescription(), labelWidthPX);
                options.anchor(0.5f, icon.getHeight()/2.0f/iconWithLabel.getHeight());
                icon.recycle();
                icon = iconWithLabel;
            } else {
                options.anchor(0.5f, 0.5f);
            }

            options.title(poi.getpoiDescription());
            options.icon(BitmapDescriptorFactory.fromBitmap(icon));
            icon.recycle();
            icon = null;
            options.position(getLatLng(poi));
            options.visible(true);
        } catch (Throwable t) {
            t.printStackTrace();
        }

        return options;
    }


    public void cameraMove(float currentZoom) {
        if (listener != null) {
            final List<IPoi> withoutlabels = new ArrayList<>();
            if (currentZoom >= maxZoomLevel) {
                withoutlabels.addAll(allPOis);
                withoutlabels.removeAll(currentPois);
            }

            if (currentZoom <= hidingAllZoomLevel) {
                List<IPoi> empty = new ArrayList<>();
                listener.poiListDelivered(empty, empty);
            } else {
                listener.poiListDelivered(new ArrayList<IPoi>(currentPois), withoutlabels);
            }
        }

//        Log.e(TAG, "camera moved");
    }

    public void clearPois() {

        currentPois.clear();

//        Log.e(TAG, "algo cleared ");
    }

    public boolean havePois() {
        return currentPois.size() > 0;
    }

    public void zoomChange(float currentZoom) {

        if (!isLoadingFloor) {
            clearPois();
            cluster(currentZoom);
        }

    }

    public void unregisterListener() {
        listener = null;
    }


    public class PoiMarkerItem implements ClusterItem {
        private final LatLng mPosition;
        private  String mTitle = null;
        private  String mSnippet = null;

        public PoiMarkerItem(double lat, double lng) {
            mPosition = new LatLng(lat, lng);
        }

        public PoiMarkerItem(double lat, double lng, String title, String snippet) {
            mPosition = new LatLng(lat, lng);
            mTitle = title;
            mSnippet = snippet;
        }

        @Override
        public LatLng getPosition() {
            return mPosition;
        }

        @Override
        public String getTitle() {
            return mTitle;
        }

        @Override
        public String getSnippet() {
            return mSnippet;
        }
    }


    private class PoisClusterTask extends AsyncTask<Float, Void, Set<Cluster>> {


        private float currentZoom;
        @Override
        protected Set<Cluster> doInBackground(Float... zoom) {
            currentZoom = zoom[0];
            mAlgorithmLock.readLock().lock();
            try {
                if (currentZoom < PropertyHolder.getInstance().getHidingPoisZoomLevel() && LowZoomPoisClusterAlgorithm != null) {
                    return LowZoomPoisClusterAlgorithm.getClusters(zoom[0]);
                } else {
                    return fullPoisClusterAlgorithm.getClusters(zoom[0]);
                }
            } finally {
                mAlgorithmLock.readLock().unlock();
            }
        }

        @Override
        protected void onPostExecute(Set<Cluster> clusters) {
            ArrayList<? extends Cluster> clusterslist = new ArrayList<>(clusters);

            final List<IPoi> withlabels = new ArrayList<>();
            for (Cluster o : clusterslist) {
                IPoi p = poisMap.get(o.getPosition());
                if (p != null) {
                    withlabels.add(p);
                }
            }

            final List<IPoi> withoutlabels = new ArrayList<>();
            if (currentZoom >= maxZoomLevel) {
                withoutlabels.addAll(allPOis);
                withoutlabels.removeAll(withlabels);
            }

            if (listener != null) {
                if (currentZoom <= hidingAllZoomLevel) {
                    List<IPoi> empty = new ArrayList<>();
                    listener.poiListDelivered(empty, empty);
                } else {
                    listener.poiListDelivered(withlabels, withoutlabels);
                }
                currentPois.addAll(withlabels);

                if (isLoadingFloor) {
                    isLoadingFloor = false;
                }

//                Log.e(TAG, "pois calculated - " + withlabels.size());
            }



        }
    }
}
