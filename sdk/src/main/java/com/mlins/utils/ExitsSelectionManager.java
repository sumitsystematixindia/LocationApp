package com.mlins.utils;

import com.mlins.utils.logging.Log;
import com.spreo.nav.enums.ExitsSelectionType;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;

public class ExitsSelectionManager {

    private HashMap<String, String> exitSelectionMap = new HashMap<>();
    private static final String FILE_NAME = "spreo_nav_rules.json";

    public static ExitsSelectionManager getInstance() {
        return Lookup.getInstance().get(ExitsSelectionManager.class);
    }

    public void load() {
        exitSelectionMap.clear();
        if (PropertyHolder.useZip) {
            String url = ServerConnection.getProjectResourcesUrl() + FILE_NAME;
            byte[] bytes = ResourceDownloader.getInstance().getUrl(url);
            if (bytes != null && bytes.length > 0) {
                try {
                    String txt = new String(bytes);
                    JSONObject json = new JSONObject(txt);
                    parse(json);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        } else {
            File dir = PropertyHolder.getInstance().getProjectDir();
            File file = new File(dir, FILE_NAME);
            if (file.exists()) {
                String content = FileUtils.getFileContent(file);
                if (!content.isEmpty()) {
                    try {
                        JSONObject json = new JSONObject(content);
                        parse(json);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private void parse(JSONObject json) {
        try {
            JSONArray array = json.getJSONArray("navigation_rules");
            for (int i = 0; i < array.length(); i++) {
                JSONObject facilityjson = array.getJSONObject(i);
                String facility = facilityjson.getString("facility_id");
                String selectiontype = facilityjson.getString("exit_selection_type");
                if (facility != null && selectiontype != null && !facility.isEmpty() && !selectiontype.isEmpty()) {
                    exitSelectionMap.put(facility, selectiontype);
                }
            }
        } catch (JSONException e) {
            Log.e(ExitsSelectionManager.class.getName(), "", e);
        }
    }

    public ExitsSelectionType getExitSelectionType(String facilityId) {
        ExitsSelectionType result = PropertyHolder.getInstance()
                .getExitsSelectionType();
        if (facilityId != null && !facilityId.isEmpty()
                && exitSelectionMap != null && !exitSelectionMap.isEmpty()) {
            String selectiontxt = exitSelectionMap.get(facilityId);
            if (selectiontxt != null && !selectiontxt.isEmpty()) {
                if (selectiontxt.equalsIgnoreCase("CLOSE_TO_DESTINATION")) {
                    result = ExitsSelectionType.CLOSE_TO_DESTINATION;
                } else if (selectiontxt.equalsIgnoreCase("CLOSE_TO_ORIGIN")) {
                    result = ExitsSelectionType.CLOSE_TO_ORIGIN;
                }
            }
        }
        return result;
    }

    public HashMap<String, String> getExitSelectionMap() {
        return exitSelectionMap;
    }

    public void setExitSelectionMap(HashMap<String, String> exitSelectionMap) {
        this.exitSelectionMap = exitSelectionMap;
    }
}
