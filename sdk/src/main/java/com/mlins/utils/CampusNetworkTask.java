package com.mlins.utils;

import android.os.AsyncTask;

import com.mlins.project.Campus;
import com.mlins.project.ProjectConf;

class CampusNetworkTask extends AsyncTask<String[], Void, String> {
    //	private static final String TAG = "NetworkTask";
    public static final String FACILITY_CONF_FILE = "campus.json";
    //	private static final String POI_LIST_FILE = "poi_list.json";
    private campusConfigurationListener mListener;
    //private boolean mFinished = false;

    @Override
    protected String doInBackground(String[]... params) {
        String jsonTxt = null;

        jsonTxt = ServerConnection.getInstance().getResources(PropertyHolder.getInstance().getCampusId());
        String cid = PropertyHolder.getInstance().getCampusId();
        Campus campus = ProjectConf.getInstance().getCampus(cid);
        campus.Parse(jsonTxt);


//		try {
//			jsonTxt = new String(ServerConnection.getInstance().getRawResource(
//							"poi/poi_types.json"), "UTF-8");
//		} catch (UnsupportedEncodingException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		
//		PoiDataHelper.getInstance().Parse(jsonTxt);
        return "";
    }


    public void registerListener(campusConfigurationListener listener) {
        mListener = listener;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);

        if (mListener != null) {
            mListener.campusConfigFinished();
        }
    }
}
