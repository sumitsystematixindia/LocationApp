package com.mlins.campaign;

import com.mlins.utils.Lookup;
import com.spreo.geofence.GeoFenceHelper;
import com.spreo.geofence.GeoFenceObject;
import com.spreo.geofence.ZoneDetection;

import java.util.ArrayList;
import java.util.List;


public class CampaignManager implements ZoneDetection {

    List<CampaignInRangeListener> listeneres = new ArrayList<CampaignInRangeListener>();
    List<String> listeningToZones = new ArrayList<String>();
    private List<IBanner> bannersList = new ArrayList<IBanner>();

    public CampaignManager() {
        super();
        BannersHolder.getInstance().LoadAllBannersConf();

        List<String> listeningto = new ArrayList<String>();
        listeningto.add("elevator"); //XXX ?
        listeningto.add("exit"); //XXX ?
        this.setListeningTo(listeningto);
        GeoFenceHelper.getInstance().subscribeForDetection(this);
    }

    public static CampaignManager getInstance() {
        return Lookup.getInstance().get(CampaignManager.class);
    }

    public boolean subscribeForService(CampaignInRangeListener listener) {
        if (!listeneres.contains(listener)) {
            listeneres.add(listener);
        }
        return true;
    }

    public boolean unsubscibeFromService(CampaignInRangeListener listener) {
        if (listeneres.contains(listener)) {
            listeneres.remove(listener);
        }
        return true;
    }


    private void notifyListeners() {
        for (CampaignInRangeListener listener : listeneres) {
            listener.onCampaignInRange(bannersList);
        }
    }

    private void findBannersInRange(GeoFenceObject zone) {

        bannersList.clear();

        List<IBanner> banners = BannersHolder.getInstance().getBannersInZone(zone);

        bannersList = banners;

    }

    @Override
    public void onZoneEnter(GeoFenceObject zone) {

        findBannersInRange(zone);

        if (bannersList.size() > 0) {
            notifyListeners();
        }

    }

    @Override
    public void onZoneExit(GeoFenceObject zone) {


    }

    @Override
    public List<String> getListeningTo() {
        return listeningToZones;
    }

    @Override
    public void setListeningTo(List<String> to) {
        listeningToZones = to;

    }


}
