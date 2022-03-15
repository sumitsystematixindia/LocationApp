package com.mlins.nav.utils;

import android.content.Context;

import com.mlins.utils.logging.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class CustomerAboutInfo {
    private final static String TAG = "ccom.mlins.nav.utils.CustomerAboutInfo";

    private String MlinsLogo;
    private String FacilityLogo;
    private String GroupLogo;
    private List<String> MlinsAboutDetails;
    private String FacilityAboutDetails;
    private List<String> GroupAboutDetails;

    public static List<String> readTextFileAsList(Context ctx, int resId) {
        Log.getInstance().debug(TAG, "Enter, readTextFileAsList()");

        InputStream inputStream = ctx.getResources().openRawResource(resId);
        InputStreamReader inputreader = new InputStreamReader(inputStream);
        BufferedReader bufferedreader = new BufferedReader(inputreader);
        String line;
        List<String> about = new ArrayList<String>();

        try {
            while ((line = bufferedreader.readLine()) != null) {
                about.add(line);
            }
        } catch (IOException e) {
            Log.getInstance().error(TAG, e.getMessage(), e);
            return null;
        }
        Log.getInstance().debug(TAG, "Exit, readTextFileAsList()");
        return about;
    }

    public String getMlinsLogo() {
        return MlinsLogo;
    }

    public void setMlinsLogo(String mlinsLogo) {
        MlinsLogo = mlinsLogo;
    }

    public String getFacilityLogo() {
        return FacilityLogo;
    }

    public void setFacilityLogo(String facilityLogo) {
        FacilityLogo = facilityLogo;
    }

    public String getGroupLogo() {
        return GroupLogo;
    }

    public void setGroupLogo(String groupLogo) {
        GroupLogo = groupLogo;
    }

    public List<String> getMlinsAboutDetails() {
        return MlinsAboutDetails;
    }

    public void setMlinsAboutDetails(List<String> mlinsAboutDetails) {
        MlinsAboutDetails = mlinsAboutDetails;
    }

    public String getFacilityAboutDetails() {
        return FacilityAboutDetails;
    }

    public void setFacilityAboutDetails(String facilityAboutDetails) {
        FacilityAboutDetails = facilityAboutDetails;
    }

    public List<String> getGroupAboutDetails() {
        return GroupAboutDetails;
    }

    public void setGroupAboutDetails(List<String> groupAboutDetails) {
        GroupAboutDetails = groupAboutDetails;
    }

}
