package com.mlins.utils;

public class ElevatorDetector {

//	public final static int ELEVATOR_TAG=-989;
//	public final static int INITIAL_TAG= -1000;
//	private static ElevatorDetector instance = null;
//	private int confirm_counter=0; 
//
//	private boolean inEelvator=false;
//
//	private int selectedFloor=INITIAL_TAG;
//	private long startTime = 0;
//	private long timeRange = 5000;
//	
//	public static ElevatorDetector getInstance() {
//		if (instance == null) {
//			instance = new ElevatorDetector();
//
//		}
//		return instance;
//	}
//
//	private ElevatorDetector() {
//		// init
//		startTime  = System.currentTimeMillis();
//	}
//	public void clean(){
//		startTime  = 0;
//		selectedFloor=INITIAL_TAG;
//		inEelvator=false;
//		instance=null;
//	}
//	
//	public int getSelectedFloor() {
//		return selectedFloor;
//	}
//
//	public boolean isInEelvator() {
//		return inEelvator;
//	}
//
//	public void setInEelvator(boolean inEelvator) {
//		this.inEelvator = inEelvator;
//	}
//	
//	
//	
//
//	
//
//	public boolean isInsideElevator(List<WlBlip> results, PointF lastLoc) {
//		
//		
//		if(lastLoc==null){
////			startTime  = System.currentTimeMillis();
//			selectedFloor=INITIAL_TAG;
//			setInEelvator(false);
//			return false;
//		}
//		
//		if (System.currentTimeMillis() - startTime < 10000) {
//			selectedFloor=INITIAL_TAG;
//			setInEelvator(false);
//			return false;
//		}
//		
////		double d = getSwitchFloorDistance(lastLoc);
////		d = d / FacilityConf.getInstance().getPixelsToMeter();
////		double treshold = PropertyHolder.getInstance()
////				.getFloorSelectorDistance();
//	
////		if (d < treshold) {
//			
//			 selectedFloor = FloorSelector.getInstance().getFloorByBlips(results);
//			 
//			 if (selectedFloor == -100) {
//				 selectedFloor=INITIAL_TAG;
//					setInEelvator(false);
//					return false;
//			}
//			
//			if(selectedFloor==ELEVATOR_TAG){
//				
//					// IN ELEVATOR
//					confirm_counter=0;
//					setInEelvator(true);
//					return true;
//			
//			}
//			else{ 
//				// NOT IN ELEVATOR
//				confirm_counter++;
//			}
//	
////		}
//		
//		if(confirm_counter>=2){
//			// not in elevator
//			setInEelvator(false);
//			return false;
//			
//		}
//		else{ 
//			// in elevator
//			return true;
//		}
//		
//		
//	}
//	
//
//	public int getElevatorMovingDirection() {
//
//		if (PropertyHolder.getInstance().isBarometerOn()) {
//			BarometerStatus barometerStatus = Barometer.getInstance()
//					.getTrendStausWithinPeriod();
//			return barometerStatus.getStatus();
//		}
//
//		return 0;
//	}
//	
//	
//	private double getSwitchFloorDistance(PointF p) {
//		
//		double mindistance = 10000;
//		try {
//			List<SwitchFloorObj> switchlist = SwitchFloorHolder.getInstance().getSwichFloorPoints();
//
//			for (SwitchFloorObj o : switchlist) {
//				double d = MathUtils.distance(p, o.getPoint());
//				if (d < mindistance) {
//					mindistance = d;
//				}
//			}
//
//		} catch (Throwable t) {
//			t.printStackTrace();
//		}
//
//		return mindistance;
//	}


}
