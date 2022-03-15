package com.mlins.dualmap;

import android.util.Log;

import com.mlins.project.Campus;
import com.mlins.utils.FacilityConf;
import com.mlins.utils.PropertyHolder;

class FloorDisplayPolicy {

	static final String TAG = FloorDisplayPolicy.class.getName();
	private final Campus campus;

	private int currentFloor;

	FloorDisplayPolicy(Campus campus) {
		this.campus = campus;
	}

	void setCurrentFloor(int currentFloor) {
		Log.d(TAG, "Switched to floor: " + currentFloor);
		this.currentFloor = currentFloor;
	}

	int getCurrentFloor() {
		return currentFloor;
	}

	boolean shouldDisplayTopFloorContent() {
		return PropertyHolder.getInstance().shouldDisplayTopFloorContent();
	}

	boolean displayFloorContent(String facilityID, int floor) {
		return displayFloorContent(campus.getFacilityConf(facilityID), floor);
	}

	boolean displayFloorContent(FacilityConf facilityConf, int floor) {
		if(currentFloor == floor)
			return true;

		int topFacilityFloor = getFacilityTopFloor(facilityConf);

		return shouldDisplayTopFloorContent() && floor == topFacilityFloor && currentFloor > topFacilityFloor;
	}

	private int getFacilityTopFloor(FacilityConf facilityConf){
		return facilityConf.getFloorDataList().size()-1;
	}

}
