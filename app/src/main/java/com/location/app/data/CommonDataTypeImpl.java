package com.location.app.data;

import static com.spreo.sdk.data.SpreoDataProvider.getCampusId;
import static com.spreo.sdk.data.SpreoDataProvider.getFacilityId;

import android.graphics.PointF;

import com.google.android.gms.maps.model.LatLng;
import com.location.app.model.BeaconLocation;
import com.mlins.utils.PropertyHolder;
import com.spreo.sdk.data.SpreoDataProvider;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


abstract class CommonDataTypeImpl extends DataType {

	@Override
	public String uploadData(List<BeaconLocation> beaconLocation, List<BeaconLocation> deletedBeacons) {
		try {

			String uploadData = convertToUploadJson(beaconLocation, deletedBeacons).toString();

			URL obj = new URL(getUploadUrl());
			HttpURLConnection con = (HttpURLConnection) obj
					.openConnection();

			// add reuqest header
			con.setRequestMethod("POST");

			con.setRequestProperty("charset", "utf-8");

			con.setRequestProperty("Content-Length",
					"" + Integer.toString(uploadData.length()));

			// Send post request
			con.setDoOutput(true);
			DataOutputStream wr = new DataOutputStream(
					con.getOutputStream());
			wr.writeBytes(uploadData);
			wr.flush();
			wr.close();

			int responseCode = con.getResponseCode();

			System.out.println("Response Code : " + responseCode);

			BufferedReader in = new BufferedReader(new InputStreamReader(
					con.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();

			if(response!=null && responseCode==200){
				String resp = response.toString();
				if(resp.contains("beacons_location")){
					return "Upload succeded!";
				}
				else{
					return "Upload content was empty!";
				}
			}
			else{
				return "Upload faild!";
			}

			// print result
			//System.out.println(response.toString());

		} catch (Throwable e) {
			return "Upload was not completed!";
		}
	}

	@Override
	JSONObject convertToUploadJson(List<BeaconLocation> beaconsLocation, List<BeaconLocation> beaconsLocationDeleted) {

		JSONObject data = new JSONObject();

		JSONArray beaconsLocationArry = new JSONArray();
		for (BeaconLocation beaconLocation : beaconsLocation) {
			beaconsLocationArry.put(beaconLocation.toJson());
		}

		try {
			data.put("beacons_location", beaconsLocationArry);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		JSONArray beaconsdeletedArry = new JSONArray();
		if (beaconsLocationDeleted.size() > 0) {
			for (BeaconLocation beaconLocationDeleted : beaconsLocationDeleted) {
				beaconsdeletedArry.put(beaconLocationDeleted.toJson());
			}

			try {
				data.put("beacons_deleted", beaconsdeletedArry);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		return data;
	}


	@Override
	public List<BeaconLocation> getBeaconsLocationsFromString(String string) throws JSONException {
		ArrayList<BeaconLocation> result = new ArrayList<>();

		JSONObject obj = new JSONObject(string);
		JSONArray m_jArry = obj.getJSONArray("beacons_location");
		for (int i = 0; i < m_jArry.length(); i++) {
			JSONObject tmpObj = m_jArry.getJSONObject(i);

			boolean isExist = false;

//						for (BeaconLocation beaconLocation : beaconsLocation) {
//							if (beaconLocation.getId().equals(id)) {
//
//								beaconLocation.setFloor(Integer.parseInt(tmpObj
//										.getValue("floor").toString()));
//								if (beaconLocation.getFloor() == -999) {
//									beaconLocation.setLon(tmpObj.getDouble("x"));
//									beaconLocation.setLat(tmpObj.getDouble("y"));
//								} else {
//									beaconLocation.setX(Float.valueOf(tmpObj.getString("x")));
//									beaconLocation.setY(Float.valueOf(tmpObj.getString("y")));
//									}
//								beaconLocation.setMsg(tmpObj.getValue("msg")
//										.toString());
//								isExist = true;
//								break;
//							}
//						}
			if (!isExist) {
				BeaconLocation beacon = new BeaconLocation(tmpObj);

				result.add(beacon);
			}
		}
		return result;
	}


	private String getUploadUrl() {
		return PropertyHolder.getInstance().getServerName()
				+ "/upload_beacons_half_nav_res?req=0" +
				"&pid="
				+ PropertyHolder.getInstance().getProjectId()
				+ "&cid="
				+ SpreoDataProvider.getCampusId()
				+ "&fid="
				+ SpreoDataProvider.getFacilityId()
				+ "&state=" + getURLSuffix();
	}

	@Override
	String getDownloadUrl() {
		return PropertyHolder.getInstance().getServerName()
				+ "apps_res?req=1&pid="
				+ PropertyHolder.getInstance().getProjectId()
				+ "&cid="
				+ getCampusId()
				+ "&fid="
				+ getFacilityId() + "&state=" + getURLSuffix();
	}

	@Override
	public BeaconLocation createNewBeaconLocation(PointF point, int floor) {
		return new BeaconLocation(point, floor);
	}

	@Override
	public BeaconLocation createNewBeaconLocation(LatLng loc) {
		return new BeaconLocation(loc);
	}

	public abstract String getURLSuffix();
}
