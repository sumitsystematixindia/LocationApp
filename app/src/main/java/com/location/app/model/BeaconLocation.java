package com.location.app.model;

import static android.content.ContentValues.TAG;

import android.graphics.PointF;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

public class BeaconLocation {

	static final String KEY_X_VALUE = "x";
	static final String KEY_Y_VALUE = "y";
	static final String KEY_FLOOR = "floor";

	static final String KEY_DESCRIPTION = "msg";

	private String id;
	private float x;
	private float y;
	private double lat;
	private double lon;
	private int floor;
	private String msg;
	private String time;
	private int tx_power;
	private int major;
	private int minar;

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public int getTx_power() {
		return tx_power;
	}

	public void setTx_power(int tx_power) {
		this.tx_power = tx_power;
	}

	public int getMajor() {
		return major;
	}

	public void setMajor(int major) {
		this.major = major;
	}

	public int getMinar() {
		return minar;
	}

	public void setMinar(int minar) {
		this.minar = minar;
	}


	public BeaconLocation(PointF point, int floor) {
		setPoint(point.x, point.y);
		this.floor = floor;
	}

	public BeaconLocation(LatLng loc) {
		floor = -999;
		setOutDoorPoint(loc.longitude, loc.latitude);
	}

	public BeaconLocation(JSONObject jsonObject) throws JSONException {
		String id = jsonObject.get("id").toString();
		setId(id);
		setFloor(Integer.parseInt(jsonObject.get(KEY_FLOOR).toString()));
		if (getFloor() == -999) {
			Log.d(TAG, "BeaconLocation: true "+jsonObject.getDouble(KEY_X_VALUE));
			setLon(jsonObject.getDouble(KEY_X_VALUE));
			setLat(jsonObject.getDouble(KEY_Y_VALUE));

		} else {
			Log.d(TAG, "BeaconLocation: false "+jsonObject.getDouble(KEY_X_VALUE));
			setX(Float.valueOf(jsonObject.getString(KEY_X_VALUE)));
			setY(Float.valueOf(jsonObject.getString(KEY_Y_VALUE)));
		}
		setMsg(jsonObject.get(KEY_DESCRIPTION).toString());
	}



	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public float getX() {
		return x;
	}
	public void setX(float x) {
		this.x = x;
	}
	public float getY() {
		return y;
	}
	public void setY(float y) {
		this.y = y;
	}
	public int getFloor() {
		return floor;
	}
	public void setFloor(int floor) {
		this.floor = floor;
	}
	public void setPoint(float x2,float y2){
		setX(x2);
		setY(y2);
	}
	

	public String getMsg() {
		return msg;
	}


	public void setMsg(String msg) {
		this.msg = msg;
	}


	public double getLat() {
		return lat;
	}


	public void setLat(double lat) {
		this.lat = lat;
	}


	public double getLon() {
		return lon;
	}


	public void setLon(double lon) {
		this.lon = lon;
	}


	public void setOutDoorPoint(double lon, double lat) {
		// TODO Auto-generated method stub
		setLat(lat);
		setLon(lon);
	}

	public JSONObject toJson(){
		JSONObject result = new JSONObject();
		try {
			result.put(KEY_FLOOR, getFloor()); // z --> floor
			result.put("id", getId());
			result.put(KEY_DESCRIPTION, getMsg());
			if (getFloor() != -999) {
				result.put(KEY_X_VALUE,
						String.valueOf(getX()));
				result.put(KEY_Y_VALUE,
						String.valueOf(getY()));
			} else {
				String valx = String.valueOf(getLon());
				result.put(KEY_X_VALUE, valx);
				String valy = String.valueOf(getLat());
				result.put(KEY_Y_VALUE, valy);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return result;
	}
	

}
