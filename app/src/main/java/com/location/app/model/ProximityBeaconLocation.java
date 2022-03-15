package com.location.app.model;

import android.graphics.PointF;

import com.google.android.gms.maps.model.LatLng;
import com.mlins.utils.PropertyHolder;
import com.spreo.sdk.data.SpreoDataProvider;

import org.json.JSONException;
import org.json.JSONObject;

public class ProximityBeaconLocation extends BeaconLocation {

	private static final String KEY_PROXIMITY_BEACON_FLOOR = "z";

	public final JSONIntParam enterLevel = new JSONIntParam(-120, -30, -75, "enter_level");
	public final JSONIntParam exitLevel = new JSONIntParam(-120, -30, -80, "exit_level");

	public ProximityBeaconLocation(PointF point, int floor) {
		super(point, floor);
	}

	public ProximityBeaconLocation(LatLng loc) {
		super(loc);
	}

	public ProximityBeaconLocation(JSONObject jsonObject) throws JSONException {
		super(getCompatibleJSON(jsonObject));
		enterLevel.setFrom(jsonObject);
		exitLevel.setFrom(jsonObject);
	}

	private static JSONObject getCompatibleJSON(JSONObject original) throws JSONException {
		original.put(KEY_FLOOR, original.getInt(KEY_PROXIMITY_BEACON_FLOOR));

		if(!original.has(KEY_DESCRIPTION)){
			original.put(KEY_DESCRIPTION, "");
		}

		return original;
	}

	@Override
	public JSONObject toJson() {
		JSONObject locationData = super.toJson();
		try {

			// adapting json according to server request requirements
			locationData.remove(KEY_FLOOR);

			if(getFloor() != -999) {
				locationData.put(KEY_X_VALUE, getX()); // float value
				locationData.put(KEY_Y_VALUE, getY()); // float value
			} else {
				locationData.put(KEY_X_VALUE, getLon()); // double value
				locationData.put(KEY_Y_VALUE, getLat()); // double value
			}

			locationData.put(KEY_PROXIMITY_BEACON_FLOOR, getFloor()); // int value with "z" key

			locationData.put("project", PropertyHolder.getInstance().getProjectId());
			locationData.put("campus", SpreoDataProvider.getCampusId());
			locationData.put("facility", SpreoDataProvider.getFacilityId());

			enterLevel.saveTo(locationData);
			exitLevel.saveTo(locationData);
		} catch (JSONException e) {
			throw new RuntimeException(e);
		}
		return locationData;
	}
}
