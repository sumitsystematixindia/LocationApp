package com.location.app.model;

import org.json.JSONException;
import org.json.JSONObject;

public class JSONIntParam extends IntParam {

	private final String jsonKey;

	public JSONIntParam(int minValue, int maxValue, int defValue, String jsonKey) {
		super(minValue, maxValue, defValue);
		this.jsonKey = jsonKey;
	}

	public void setFrom(JSONObject jsonObject) throws JSONException {
		value = jsonObject.getInt(jsonKey);
		if(value < minValue) {
			minValue = value;
		} else if(value > maxValue) {
			maxValue = value;
		}
	}

	public void saveTo(JSONObject jsonObject) throws JSONException {
		jsonObject.put(jsonKey, value);
	}

}
