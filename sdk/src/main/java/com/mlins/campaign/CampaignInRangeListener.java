package com.mlins.campaign;

import java.util.List;


public interface CampaignInRangeListener {

    /**
     * This method called when the location is inside the geofence of a banner
     *
     * @param bannersList - the list of all banners in the range of
     */
    void onCampaignInRange(List<IBanner> bannersList);
}
