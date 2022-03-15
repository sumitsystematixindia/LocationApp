package com.location.app.data;

import android.app.Dialog;
import android.content.Context;
import android.graphics.PointF;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.location.app.model.BeaconLocation;

import java.util.ArrayList;
import java.util.List;

public class DataManager  {

    private static final String TAG = DataManager.class.getName();
    private static DataManager mInstance = null;
    public Dialog mDialog;
    private BeaconLocation beaconSelected;
    private Context context;
    private int currenFloor = 0;
    private List<BeaconLocation> beaconsLocation = new ArrayList<BeaconLocation>();
    private DataType dataType = new LocationDataType();
    public static DataManager getInstance() {
        if (mInstance == null) {
            mInstance = new DataManager();
        }
        return mInstance;
    }
    public BeaconLocation getBeaconSelected() {
        return beaconSelected;
    }

    public void setBeaconSelected(PointF point) {
        this.beaconSelected = dataType.createNewBeaconLocation(point, currenFloor);
        beaconSelected.setFloor(currenFloor);
    }

    public void setBeaconSelected(LatLng loc) {
        this.beaconSelected = dataType.createNewBeaconLocation(loc);
        beaconSelected.setFloor(currenFloor);
    }

    public int getCurrenFloor() {
        Log.e("@@@@ getCurrenFloor ",String.valueOf(currenFloor));
        return currenFloor;
    }

    public void setCurrenFloor(int currenFloor) {
        Log.e("@@@@ setCurrenFloor ",String.valueOf(currenFloor));
        this.currenFloor = currenFloor;
    }

    public List<BeaconLocation> getBeaconsLocation() {
        return beaconsLocation;
    }

//    public void setBeaconsLocation(ArrayList<BeaconLocation> beaconsLocation) {
//        this.beaconsLocation = beaconsLocation;
//    }

    public void setBeaconsLocationAll(List<BeaconLocation> beaconsLocation) {
        this.beaconsLocation = beaconsLocation;
    }

    public void addBeaconsLocation(BeaconLocation beaconLocation) {
        this.beaconsLocation.add(beaconLocation);
    }
    public void WriteToFile() {
       // dataType.saveReportToFile(beaconsLocation, beaconsLocationDeleted);
    }
}
