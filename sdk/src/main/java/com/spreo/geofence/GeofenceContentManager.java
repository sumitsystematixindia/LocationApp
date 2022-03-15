package com.spreo.geofence;

import com.mlins.utils.Cleanable;
import com.mlins.utils.Lookup;
import com.mlins.utils.PropertyHolder;
import com.mlins.utils.ResourceDownloader;
import com.mlins.utils.ServerConnection;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class GeofenceContentManager implements Cleanable{

    private final static String uri = "spreo_campaigns.json";
    private Map<String, List<GeofenceContent>> triggerToContentMap = new HashMap<String, List<GeofenceContent>>();

    public static GeofenceContentManager getInstance() {
        return Lookup.getInstance().get(GeofenceContentManager.class);
    }

    public static void releaseInstance() {
        Lookup.getInstance().remove(GeofenceContentManager.class);
    }

    public void clean() {
        triggerToContentMap.clear();
    }

    public boolean addData(String campusId, String facilityId) {
        boolean isSucceded = false;

        try {

            String content = "";
            if (PropertyHolder.useZip) {
                String url = ServerConnection.getResourcesUrl() + facilityId + "/" + uri;
                byte[] bytes = ResourceDownloader.getInstance().getUrl(url);
                if (bytes != null) {
                    content = new String(bytes);
                }
            } else {
                File root = PropertyHolder.getInstance().getProjectDir();
                File campusdir = new File(root, campusId);
                File facilitycDir = new File(campusdir, facilityId);
                File labelsfile = new File(facilitycDir, uri);
                content = getFileContent(labelsfile);
            }

            if (content != null && !content.equals("")) {
                parseJson(facilityId, content);
            }

        } catch (Throwable t) {
            t.printStackTrace();
        }

        return isSucceded;

    }

    private String getFileContent(File file) {

        Scanner scanner = null;
        StringBuffer content = new StringBuffer();

        try {
            scanner = new Scanner(file, "UTF-8");
            while (scanner.hasNext()) {
                content.append(scanner.nextLine());
            }

        } catch (Throwable e) {
            e.printStackTrace();
        } finally {
            if (scanner != null)
                scanner.close();
        }
        return content.toString();
    }

    public void parseJson(String facilityId, String content) {

        try {

            JSONTokener jsonTokener = new JSONTokener(content);

            JSONObject json = (JSONObject) jsonTokener.nextValue();

            JSONArray jArrObj = json.getJSONArray("campaigns");

            for (int i = 0; i < jArrObj.length(); i++) {

                try {

                    JSONObject gObj = jArrObj.getJSONObject(i);

                    GeofenceContent campaign = new GeofenceContent();

                    try {
                        int autoIndex = gObj.getInt("index");
                        campaign.setAutoIndex(autoIndex);
                    } catch (Exception e) {
                        //System.out.println("CampaignContent: error key index");
                        //e.printStackTrace();
                    }

                    try {
                        String id = gObj.getString("name");
                        campaign.setName(id);
                    } catch (Exception e) {
                        //System.out.println("CampaignContent: error key name");
                        //e.printStackTrace();
                    }

                    try {
                        String text = gObj.getString("text");
                        campaign.setText(text);
                    } catch (Exception e) {
                        //System.out.println("CampaignContent: error key text");
                        // e.printStackTrace();
                    }

                    try {
                        String imgUri = gObj.getString("img_uri");
                        String url = ServerConnection.getResourcesUrl() + facilityId + "/" + imgUri;
                        campaign.setImageUrl(url);
                    } catch (Exception e) {
                        //System.out.println("CampaignContent: error key img_uri");
                        // e.printStackTrace();
                    }

                    try {
                        String actionUrl = gObj.getString("action_url");
                        campaign.setActionUrl(actionUrl);
                    } catch (Exception e) {
                        //System.out.println("CampaignContent: error key action_url");
                        // e.printStackTrace();
                    }

                    try {
                        String action = gObj.getString("action");
                        campaign.setAction(action);
                    } catch (Exception e) {
                        //System.out.println("CampaignContent: error key action");
                        // e.printStackTrace();
                    }

                    try {
                        JSONArray triggersJsonArr = gObj.getJSONArray("triggers_list");
                        campaign.setTriggersListFromJsonArray(triggersJsonArr);
                    } catch (Exception e) {
                        //System.out.println("CampaignContent: error key triggers_list");
                        // e.printStackTrace();
                    }

                    try {
                        String lastUpdate = gObj.getString("last_updated");
                        campaign.setLastUpdate(lastUpdate);
                    } catch (Exception e) {
                        //System.out.println("CampaignContent: error key triggers_list");
                        // e.printStackTrace();
                    }


                    campaign.prepairImgBlob();

                    addCampaingContent(campaign);

                } catch (Throwable t) {
                    t.printStackTrace();
                }

            }
            // lastUpdated = new Timestamp(System.currentTimeMillis());

        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    private void addCampaingContent(GeofenceContent campaign) {
        if (campaign == null) {
            return;
        }

        List<String> trList = campaign.getTriggersIdList();

        if (trList != null) {

            for (String triggerId : trList) {

                List<GeofenceContent> contents = triggerToContentMap.get(triggerId);

                if (contents == null) {
                    contents = new ArrayList<GeofenceContent>();
                    triggerToContentMap.put(triggerId, contents);
                }

                contents.add(campaign);
            }

        }

    }

    public List<GeofenceContent> getContentByTriggerId(String triggerId) {
        return triggerToContentMap.get(triggerId);
    }


}
