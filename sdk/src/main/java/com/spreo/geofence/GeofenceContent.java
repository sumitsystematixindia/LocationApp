package com.spreo.geofence;

import android.os.AsyncTask;

import com.mlins.utils.ResourceDownloader;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GeofenceContent {

    private int autoIndex = -1;
    private String name = null;
    private String text = null;
    private byte[] imgBlob = null;
    private String actionUrl = null;
    private Map<String, String> triggersMap = null;
    private String imageUrl = null;
    private String action = null;
    private String lastUpdate = "";

    public GeofenceContent() {
        super();
    }

    public void setTriggersListFromJsonArray(JSONArray triggersJsonArr) {
        try {
            if (triggersMap == null) {
                triggersMap = new HashMap<String, String>();
            }

            triggersMap.clear();

            for (int j = 0; j < triggersJsonArr.length(); j++) {
                String tId = (String) triggersJsonArr.get(j);
                String splitLine[] = tId.split(",");
                for (int i = 0; i < splitLine.length; i++) {
                    String splitId[] = splitLine[i].split("@@");
                    if (splitId.length > 1) {
                        triggersMap.put(splitId[0], splitId[1]);
                    }
                }
            }

        } catch (Throwable t) {
            t.printStackTrace();
        }

    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public int getAutoIndex() {
        return autoIndex;
    }

    public void setAutoIndex(int autoIndex) {
        this.autoIndex = autoIndex;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public byte[] getImgBlob() {
        return imgBlob;
    }

    public void setImgBlob(byte[] imgBlob) {
        this.imgBlob = imgBlob;
    }

    public String getActionUrl() {
        return actionUrl;
    }

    public void setActionUrl(String actionUrl) {
        this.actionUrl = actionUrl;
    }

    public List<String> getTriggersIdList() {
        return triggersMap == null ? null : new ArrayList<String>(
                triggersMap.keySet());
    }

    private boolean isUrlImageValid() {
        return imageUrl != null && imageUrl.startsWith("http");
    }

    public void prepairImgBlob() {

        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {

                try {

                    if (isUrlImageValid()) {

                        ResourceDownloader.getInstance().addMd5ForUrl(imageUrl, lastUpdate);
                        imgBlob = ResourceDownloader.getInstance().getUrl(imageUrl, true);
                        ResourceDownloader.getInstance().saveResFile();


                    }
                } catch (Throwable t) {
                    t.printStackTrace();
                }

                return null;

            }

        };

        task.execute();

    }

    @Override
    public String toString() {
        return "CampaignContent [autoIndex=" + autoIndex + ", name=" + name
                + ", text=" + text + ", actionUrl=" + actionUrl + ", imageUrl="
                + imageUrl + ", action=" + action + "]";
    }

    public void setLastUpdate(String lastUpdate) {
        this.lastUpdate = lastUpdate;
    }


}
