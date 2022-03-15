package com.mlins.nav.location.sharing;

import com.spreo.interfaces.ILocationSharingUser;
import com.spreo.interfaces.LocationSharingListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class LocationSharingUsersManger {
    private static LocationSharingUsersManger instance = null;
    private List<ILocationSharingUser> users = new ArrayList<ILocationSharingUser>();
    private List<LocationSharingListener> listeners = new ArrayList<LocationSharingListener>();
    private List<String> idList = new ArrayList<String>();
    private LocationSharingUserSThread thread = null;

    // hope we don't use it any more, so didn't rework it
    public static LocationSharingUsersManger getInstance() {
        if (instance == null) {
            instance = new LocationSharingUsersManger();
        }
        return instance;
    }

    public static void releaseInstance() {
        if (instance != null) {
            instance = null;
        }
    }

    private void notifyListeners() {
        for (LocationSharingListener o : listeners) {
            if (o != null) {
                try {
                    o.onLocationsUpdate(users);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void registerListener(LocationSharingListener listener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener);
        }
        if (thread == null) {
            thread = new LocationSharingUserSThread();
        }
    }

    public void unregisterListener(LocationSharingListener listener) {
        if (listeners.contains(listener)) {
            listeners.remove(listener);
        }
        if (listeners.isEmpty() && thread != null) {
            thread.destroy();
            thread = null;
        }
    }

    public List<ILocationSharingUser> getUsers() {
        return users;
    }

    public void setUsers(List<ILocationSharingUser> users) {
        this.users = users;
    }

    public void updateUsers(List<ILocationSharingUser> activeusers) {
        users = activeusers;
        notifyListeners();
    }

    public List<String> getIdList() {
        return idList;
    }

    public void setIdList(List<String> idList) {
        this.idList = idList;
    }

    public void addUserId(String id) {
        if (!idList.contains(id)) {
            idList.add(id);
        }
    }

    public void removeUserId(String id) {
        if (idList.contains(id)) {
            idList.remove(id);
        }
    }

    public JSONObject getIdListAsJson() {
        JSONObject result = null;
        if (idList != null && !idList.isEmpty()) {
            result = new JSONObject();
            JSONArray idarray = new JSONArray();
            for (String o : idList) {
                idarray.put(o);
            }
            try {
                result.put("filtered_list", idarray);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return result;
    }

}
