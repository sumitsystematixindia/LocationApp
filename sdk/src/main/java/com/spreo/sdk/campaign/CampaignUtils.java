package com.spreo.sdk.campaign;

import com.mlins.campaign.BannerObject;
import com.mlins.campaign.BannersHolder;
import com.mlins.campaign.CampaignInRangeListener;
import com.mlins.campaign.CampaignManager;
import com.mlins.campaign.IBanner;

import java.util.ArrayList;
import java.util.List;

/**
 * This class handle the Campaign utility methods
 *
 * @author Spreo
 */
public class CampaignUtils {

    /**
     * Subscribe for Campaign service
     *
     * @param campaignInRangeListener - the listener of the Campaign service. e.g. an activity or a view that implements the interface campaignInRangeListener
     * @return true if succeeded to subscribe
     */
    public static boolean subscribeToService(CampaignInRangeListener campaignInRangeListener) {
        return CampaignManager.getInstance().subscribeForService(campaignInRangeListener);
    }

    /**
     * unSubscribe from Campaign service
     *
     * @param campaignInRangeListener - the listener of the Campaign service. e.g. an activity or a view that implements the interface campaignInRangeListener
     * @return true if succeeded to unSubscribe
     */
    public static boolean unSubscribeFromService(CampaignInRangeListener campaignInRangeListener) {
        return CampaignManager.getInstance().unsubscibeFromService(campaignInRangeListener);
    }

    /**
     * CampaignUtils.loadCampaigns is used in order to load campaign configuration file.
     */
    public static void loadCampaigns() {
        BannersHolder.getInstance().LoadAllBannersConf();
    }

    /**
     * CampaignUtils.getAllCampaigns is used to load all the campaigns from the campaign configuration
     *
     * @return all the campaign objects for the facility.
     */
    public static List<IBanner> getAllCampaigns() {
        List<BannerObject> result = BannersHolder.getInstance().getAllBanners();
        List<IBanner> res = new ArrayList<IBanner>();
        res.addAll(result);
        return res;
    }

}
