package com.location.app.data;

import android.graphics.PointF;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.location.app.model.BeaconLocation;
import com.mlins.utils.PropertyHolder;
import com.spreo.sdk.data.SpreoDataProvider;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.util.List;



public abstract class DataType {

	private static final String TAG = DataType.class.getName();

	abstract JSONObject convertToUploadJson(List<BeaconLocation> beaconsLocation, List<BeaconLocation> beaconsLocationDeleted);
	public abstract List<BeaconLocation> getBeaconsLocationsFromString(String string) throws JSONException;

	abstract String getDownloadUrl();

//	public abstract int getID();

//	public abstract String getTitleResID();

	public abstract BeaconLocation createNewBeaconLocation(PointF point, int floor);

	public abstract BeaconLocation createNewBeaconLocation(LatLng loc);

	public abstract String uploadData(List<BeaconLocation> beaconLocation, List<BeaconLocation> deletedBeacons);

	void saveReportToFile(List<BeaconLocation> beaconsLocation, List<BeaconLocation> beaconsLocationDeleted) {
		File file = CommonDataTypeImpl.getReportFile(getClass());

		file.delete();
		file.getParentFile().mkdirs();

		try {
			FileWriter file1 = new FileWriter(file, false);

			String report = convertToUploadJson(beaconsLocation, beaconsLocationDeleted).toString(2);

			file1.write(report);

			file1.flush();
			file1.close();
		} catch (Exception e) {
			Log.e(TAG, "Can't save to file: ", e);
		}
	}

	public static File getReportFile(Class<? extends DataType> clazz) {
		return new File(
				PropertyHolder.getInstance().getExternalStoragedir()
						+ "/AndroidBeaconPlacement/"
						+ SpreoDataProvider.getCampusId() + "_"
						+ SpreoDataProvider.getFacilityId() +"_"
						+ clazz.getSimpleName() + ".txt");

	}
}
