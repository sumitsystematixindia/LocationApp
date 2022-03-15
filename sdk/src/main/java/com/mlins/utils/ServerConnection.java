package com.mlins.utils;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.UnsupportedEncodingException;

public class ServerConnection {
    public static final String PARAM_NAME_LAT = "latitude";
    public static final String PARAM_NAME_LON = "longtitude";
    public static final String PARAM_NAME_REQ_TYPE = "req_type";
    public static final String PARAM_NAME_FACILITY_ID = "facility_id";
    public static final String PARAM_NAME_Campus_ID = "campus_id";
    public static final int FAC_REQ = 0;
    public static final int RESOURCES_REQ = 1;
    private static final String TAG = "ServerConnection";
    private static final String BASE_URL = PropertyHolder.getInstance().getServerName();
    private static final String RESOURCES_URL = BASE_URL + "res/";
    private static final String SERVICE_URL = BASE_URL + "navserver";
    private static final String PARAM_NAME_Project_ID = "project_id";
    private static final String PARAM_NAME_MD5_OF_MD5 = "md5";
    static ServerConnection instance = null;

    //doesn't hold any state, no need to rework
    public static ServerConnection getInstance() {
        if (instance == null) {
            instance = new ServerConnection();
        }
        return instance;
    }

    public static String getProjectResourcesUrl() {
        return getBaseUrl() + "res/" + PropertyHolder.getInstance().getProjectId() + "/";
    }

    public static String getResourcesUrl() {
        return getProjectResourcesUrl() + PropertyHolder.getInstance().getCampusId() + "/";

    }

    public static String getServiceUrl() {
        return getBaseUrl() + "navserver";
    }

    public static String getBaseUrl() {
        return PropertyHolder.getInstance().getServerName();
    }

    public static String getUrlOfResList(String facility, String campus) {


        if (PropertyHolder.useZip) {
            return getBaseUrlOfFacilityResListForZip(campus, facility);
        }

        String url = getServiceUrl() + "?" + PARAM_NAME_REQ_TYPE + "="
                + RESOURCES_REQ + "&" + PARAM_NAME_FACILITY_ID + "=" + facility;
        if (campus != null) {
            url += "&" + PARAM_NAME_Campus_ID + "=" + campus;
        }
        String project = PropertyHolder.getInstance().getProjectId();
        url += "&" + PARAM_NAME_Project_ID + "=" + project;

        String md5Ofmd5 = FacilityContainer.getInstance().getSelected().getMd5Ofmd5();
        if (md5Ofmd5 != null) {
            url += "&" + PARAM_NAME_MD5_OF_MD5 + "=" + md5Ofmd5;
        }

        //url = getBaseUrl() + "res/"+ PropertyHolder.getInstance().getProjectId() + "/" + campus+ "/" + facility + "/" + "fac_res_list.json";

        return url;

    }

    public static String getBaseUrlOfFacilityResList(String facility, String campus) {


        if (PropertyHolder.useZip) {
            return getBaseUrlOfFacilityResListForZip(campus, facility);
        }

        String url = getServiceUrl() + "?" + PARAM_NAME_REQ_TYPE + "="
                + RESOURCES_REQ + "&" + PARAM_NAME_FACILITY_ID + "=" + facility;
        if (campus != null) {
            url += "&" + PARAM_NAME_Campus_ID + "=" + campus;
        }
        String project = PropertyHolder.getInstance().getProjectId();
        url += "&" + PARAM_NAME_Project_ID + "=" + project;


        return url;

    }

    public static String getBaseUrlOfCampusResList(String campus) {

        if (PropertyHolder.useZip) {
            return getBaseUrlOfCampusResListForZip(campus);
        }

        String url = getServiceUrl() + "?" + PARAM_NAME_REQ_TYPE + "="
                + RESOURCES_REQ + "&" + PARAM_NAME_Campus_ID + "=" + campus;
        String project = PropertyHolder.getInstance().getProjectId();
        url += "&" + PARAM_NAME_Project_ID + "=" + project;


        return url;

    }

    private static String getBaseUrlOfFacilityResListForZip(String campus, String facility) {


        String url = getBaseUrl() + "res/" + PropertyHolder.getInstance().getProjectId() + "/" + campus + "/" + facility + "/" + "fac_res_list.json";
        return url;
    }

    private static String getBaseUrlOfCampusResListForZip(String campus) {

        String url = getBaseUrl() + "res/" + PropertyHolder.getInstance().getProjectId() + "/" + campus + "/" + "campus_res_list.json";
        return url;
    }

    public String getFacility() {
        return PropertyHolder.getInstance().getFacilityID();
    }

    public void setFacility(String facility) {
        PropertyHolder.getInstance().setFacilityID(facility);
    }

    public String getFacility(float lat, float lon) {
        String url = getServiceUrl() + "?" + PARAM_NAME_REQ_TYPE + "=" + FAC_REQ
                + "&" + PARAM_NAME_LAT + "=" + lat + "&" + PARAM_NAME_LON + "=" + lon;
        String fac = null;
        try {
            fac = new String(getResourceBytes(url), "UTF-8");
        } catch (UnsupportedEncodingException nuller) {
            Log.e(TAG, "Non UTF-8 encoding of:" + url, nuller);
            fac = null;
        }
        setFacility(fac);
        return fac;
    }

    public String getResources() {
        return getResources(PropertyHolder.getInstance().getFacilityID(), PropertyHolder.getInstance().getCampusId());
    }

    public String getResources(String campus) {
//		String url = getServiceUrl() + "?" + PARAM_NAME_REQ_TYPE + "="
//				+ RESOURCES_REQ + "&" + PARAM_NAME_Campus_ID + "=" + campus;
//		String project = PropertyHolder.getInstance().getProjectId();
//		url += "&" + PARAM_NAME_Project_ID + "=" + project;

        String url = getBaseUrlOfCampusResList(campus);

        String res = "";
        try {

            res = new String(CampusLevelResDownloader.getCInstance().getUrl(url), "UTF-8");
            byte[] olddata = new byte[0];
            if ((PropertyHolder.getInstance().getCampusDir() != null)) {
                olddata = CampusLevelResDownloader.getCInstance().getLocalCopy(url);
            }

            try {
                JSONTokener tokener = new JSONTokener(res);
                JSONObject json = (JSONObject) tokener.nextValue();
                json.getString("project_id");

            } catch (Exception ex) {
                if (olddata.length > 0) {
                    CampusLevelResDownloader.getCInstance().writeLocalCopy(url, olddata);
                    res = new String(olddata, "UTF-8");
                }

            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return res;
    }

    public String getResources(String facility, String campus) {
//		String url = getServiceUrl() + "?" + PARAM_NAME_REQ_TYPE + "="
//				+ RESOURCES_REQ + "&" + PARAM_NAME_FACILITY_ID + "=" + facility;
//		if (campus != null) {
//			url += "&" + PARAM_NAME_Campus_ID + "=" + campus;
//		}
//		String project = PropertyHolder.getInstance().getProjectId();
//		url += "&" + PARAM_NAME_Project_ID + "=" + project;


        String res = "";
        try {

//			String md5Ofmd5=FacilityConf.getInstance().getMd5Ofmd5();
//			if(md5Ofmd5!=null){
//				url +="&" + PARAM_NAME_MD5_OF_MD5 + "=" + md5Ofmd5;
//			}

            String url = getUrlOfResList(facility, campus);

            res = new String(ResourceDownloader.getInstance().getUrl(url), "UTF-8");

            try {


                if (!res.equals("up_to_date")) {
                    JSONTokener tokener = new JSONTokener(res);
                    JSONObject json = (JSONObject) tokener.nextValue();
                    JSONArray floors = json.getJSONArray("floors");
                    JSONObject floor = floors.getJSONObject(0);
                }


//				else {
//					//get the json from the resource downloader and parse
//					 String baseResUrl = ServerConnection.getBaseUrlOfFacilityResList(
//								PropertyHolder.getInstance().getFacilityID(),
//								PropertyHolder.getInstance().getCampusId());
//					byte[] olddata = ResourceDownloader.getInstance().getUrl(baseResUrl);
//					if (olddata.length > 0)
//					{
//
//						res=new String(olddata,"UTF-8");
//					}
//				}

            } catch (Exception ex) {
                try {
                    String baseResUrl = ServerConnection.getBaseUrlOfFacilityResList(
                            PropertyHolder.getInstance().getFacilityID(),
                            PropertyHolder.getInstance().getCampusId());
                    byte[] olddata = ResourceDownloader.getInstance().getUrl(baseResUrl);
                    if (olddata.length > 0) {
                        ResourceDownloader.getInstance().writeLocalCopy(baseResUrl, olddata);
                        res = new String(olddata, "UTF-8");
                    }
                } catch (Throwable t) {
                    t.printStackTrace();
                }

            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return res;
    }

    public byte[] getFacilityRawResource(String name) {

        if (name == null)
            return new byte[0];

        if (name.length() == 0)
            return new byte[0];
        String url = getResourcesUrl() + getFacility() + "/" + name;
        return ResourceDownloader.getInstance().getUrl(url);
    }

    public byte[] getCampusRawResource(String name) {

        if (name == null)
            return new byte[0];

        if (name.length() == 0)
            return new byte[0];
        String url = name;

        if (!url.startsWith("http")) {
            url = getResourcesUrl() + name;
        }

        return CampusLevelResDownloader.getCInstance().getUrl(url);
    }

    public boolean downloadFacilityRawResource(String name) {

        if (name == null)
            return false;

        if (name.length() == 0)
            return false;
        String url = getResourcesUrl() + getFacility() + "/" + name;
        return ResourceDownloader.getInstance().downloadToLocalStorage(url);
    }

    public boolean downloadCampusRawResource(String name) {

        if (name == null)
            return false;

        if (name.length() == 0)
            return false;
        String url = getResourcesUrl() + name;
        return CampusLevelResDownloader.getCInstance().downloadToLocalStorage(url);
    }

    public byte[] getResourceBytes(String url) {
        byte[] result = null;
        NetworkRequest request = new NetworkRequest(url);
        NetworkRequestThread t = new NetworkRequestThread(request);
        try {
            synchronized (request) {
                t.start();
                request.wait();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        result = request.result;
        return result;
    }

    public String translateUrl(String url) {
        if (url.startsWith("http"))
            return url;

        return getResourcesUrl() + getFacility() + "/" + url;
    }

    public String translateCampusResUrl(String url) {
        if (url.startsWith("http"))
            return url;
        return getResourcesUrl() + url;
    }

    public String translateUrl(String url, String facility) {
        return getResourcesUrl() + facility + "/" + url;
    }

}