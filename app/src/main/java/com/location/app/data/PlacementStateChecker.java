package com.location.app.data;

import com.location.app.model.BeaconLocation;
import com.mlins.utils.FacilityConf;
import com.mlins.utils.FacilityContainer;
import com.mlins.wireless.WlBlip;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;



public class PlacementStateChecker {
	
	private static PlacementStateChecker instance = null;
	private boolean inRange = false;
	private int bleLevel = 0;
	private int devicesCount = 0;
	private int inRangeDevicesCount = 0;
	private int placementThreshold = 0;
	
	public static PlacementStateChecker getInstance() {
		if (instance == null) {
			instance = new PlacementStateChecker();
			instance.init();
		}
		return instance;
	}
	
	private void init() {
		FacilityConf facConf = FacilityContainer.getInstance().getSelected();
		if(facConf!=null){
			bleLevel = facConf.getExitMinBleDetectionLevel();
			devicesCount = facConf.getExitMinBleDetectionDevices();
			placementThreshold  = (int)facConf.getPlacementThresh();
		}
	}
	public static void releaseInstance(){
		if(instance!=null){
			instance.clean();
			instance = null;
		}
	}
	
	private void clean() {
		inRange = false;
		bleLevel = 0;
		devicesCount = 0;
		inRangeDevicesCount = 0;
	}
	
	
//	public boolean updateState(List<WlBlip> results) {
//
//		List<BeaconLocation> currentPlacedBeacons = DataManager.getInstance().getBeaconsLocation();
//		List<WlBlip> scanResults = new ArrayList<WlBlip>(results);
//
//
//		if(scanResults == null || scanResults.size() == 0){
//			inRange = false;
//			inRangeDevicesCount = 0;
//			return inRange;
//		}
//
//		if(currentPlacedBeacons == null || currentPlacedBeacons.size() == 0){
//			return inRange;
//		}
//
//
//
//
//		int count=0;
//
//		// filter placed beacons
//		Set<String> beaconsIdsSet = new HashSet<String>();
//		for(BeaconLocation bec:currentPlacedBeacons){
//			if(bec!=null){
//				beaconsIdsSet.add(bec.getId());
//			}
//		}
//
//		int levelThr = bleLevel + placementThreshold;
//
//		for(WlBlip blip:scanResults){
//			if(blip!=null){
//				String becId = blip.BSSID;
//				if(beaconsIdsSet.contains(becId)){
//					if(blip.level >= levelThr){
//						count++;
//					  }
//				}
//			}
//		}
//
//		 if(count >= devicesCount){
//			 inRange = true;
//		 }
//		 else{
//			 inRange = false;
//		 }
//
//		 inRangeDevicesCount = count;
//
//		return inRange;
//
//
//	}
//

	public String getMessageTxt(){
		return bleLevel + placementThreshold +" " + inRangeDevicesCount + " > " + devicesCount;
	}
	
	
}
