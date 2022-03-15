package com.mlins.utils;

import android.os.AsyncTask;

import java.io.UnsupportedEncodingException;


class NetworkTask extends AsyncTask<String[], Void, String> {

    //	private static final String TAG = "NetworkTask";
    public static final String FACILITY_CONF_FILE = "facility.json";
    //	private static final String POI_LIST_FILE = "poi_list.json";
    private ConfigurationListener mListener;
    //private boolean mFinished = false;

    @Override
    protected String doInBackground(String[]... params) {
        String jsonTxt = null;

        jsonTxt = ServerConnection.getInstance().getResources(PropertyHolder.getInstance().getFacilityID(), PropertyHolder.getInstance().getCampusId());
        //"{'facid':'MELLERS','floors':[{'floormap':'http://medical-sentry.com/mlins/maps/floor1.png','floorthumb':'http://medical-sentry.com/mlins/maps/floor1thumb.png','title':'floor1'},{'floormap':'http://medical-sentry.com/mlins/maps/floor1.png','floorthumb':'http://medical-sentry.com/mlins/maps/floor1thumb.png','title':'floor2'}]}";
        FacilityContainer.getInstance().getSelected().Parse(jsonTxt);


        try {
            jsonTxt = new String(ServerConnection.getInstance().getFacilityRawResource(
                    "poi/poi_types.json"), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        PoiDataHelper.getInstance().Parse(jsonTxt);
        return "";
    }

//	private String readFile(File file) {
//		String txt = null;
//		ByteArrayOutputStream out = new ByteArrayOutputStream();
//		BufferedInputStream in = null;
//		try {
//			in = new BufferedInputStream(new FileInputStream(file));
//			byte[] buffer = new byte[4096];
//			int l = -1;
//			while ((l = in.read(buffer)) != -1) {
//				out.write(buffer, 0, l);
//			}
//			txt = new String(out.toByteArray(), "UTF-8");
//		} catch (IOException nuller) {
//			txt = null;
//		} finally {
//			try {
//				in.close();
//				out.close();
//			} catch (IOException ignore) {}
//		}
//		return txt;
//	}

//	private void writeFile(File file, String txt) {
//		FileWriter writer = null;
//		try {
//			file.createNewFile();
//			writer = new FileWriter(file);
//			writer.write(txt);
//			writer.flush();
//		} catch (IOException logMe) {
//			Log.e(TAG, "Failed to write" + file.getPath(), logMe);
//			logMe.printStackTrace();
//		} finally {
//			if (writer != null) {
//				try {
//					writer.close();
//				} catch (IOException ignore) {}
//			}
//		}
//	}

    public void registerListener(ConfigurationListener listener) {
        mListener = listener;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);

        if (mListener != null) {
            mListener.configFinished();
        }
    }
}
