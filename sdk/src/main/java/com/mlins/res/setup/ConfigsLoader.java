package com.mlins.res.setup;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;

import com.mlins.overlay.CampusMapTilesManager;
import com.mlins.project.Campus;
import com.mlins.project.ProjectConf;
import com.mlins.utils.CampusLevelResDownloader;
import com.mlins.utils.Cleanable;
import com.mlins.utils.FacilityConf;
import com.mlins.utils.FacilityContainer;
import com.mlins.utils.Lookup;
import com.mlins.utils.PoiDataHelper;
import com.mlins.utils.PoiType;
import com.mlins.utils.PoisContainer;
import com.mlins.utils.PropertyHolder;
import com.mlins.utils.ResourceDownloader;
import com.mlins.utils.ServerConnection;
import com.mlins.utils.logging.Log;
import com.spreo.enums.LoadStatus;
import com.spreo.interfaces.ConfigsLoadListener;
import com.spreo.interfaces.LanguageLoadListener;
import com.spreo.nav.interfaces.IPoi;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import gps.CampusGisData;

public class ConfigsLoader implements Cleanable {


    final static int SDK_ENABLED = 0;
    final static int ERROR_TYPE_CONNECTION = 1;
    final static int ERROR_TYPE_INVALID_APIKEY = 2;
    final static int NO_RESPONSE = 3;
    private static ConfigsLoader instance = null;
    private String TAG = "com.mlins.res.setup.ConfigsLoader";
    private List<ConfigsLoadListener> listeners = Collections.synchronizedList(new ArrayList<ConfigsLoadListener>());
    private List<LanguageLoadListener> langLoadListeners = Collections.synchronizedList(new ArrayList<LanguageLoadListener>());
    private List<String> loadedCampuses = Collections.synchronizedList(new ArrayList<String>());
    private List<String> loadedFacilities = Collections.synchronizedList(new ArrayList<String>());
    private String loadedFacility = "";


    public static ConfigsLoader getInstance() {
        return Lookup.getInstance().get(ConfigsLoader.class);
    }

    public static void releaseInstance() {
        Lookup.getInstance().remove(ConfigsLoader.class);
    }

    public void clean() {
        listeners.clear();
        langLoadListeners.clear();
        //XXX ??
        //loadedCampuses.clear();
        //loadedFacilities.clear();

    }

    public synchronized boolean registerListener(ConfigsLoadListener configsLoadListener) {
        if (!listeners.contains(configsLoadListener)) {
            return listeners.add(configsLoadListener);
        } else {
            return false;
        }
    }

    public synchronized boolean unregisterListener(ConfigsLoadListener configsLoadListener) {
        if (listeners.contains(configsLoadListener)) {
            return listeners.remove(configsLoadListener);
        } else {
            return false;
        }
    }


    private synchronized void notifyListeners(LoadStatus status) {


        final LoadStatus stat = status;
        Context context = PropertyHolder.getInstance().getMlinsContext();
        if (context == null) {
            return;
        }

        Handler mainHandler = new Handler(context.getMainLooper());

        Runnable myRunnable = new Runnable() {

            @Override
            public void run() {
                try {
                    for (ConfigsLoadListener listener : listeners) {
                        listener.onPostConfigsLoad(stat);
                    }
                } catch (Throwable t) {
                    t.printStackTrace();
                }
            }
        };
        mainHandler.post(myRunnable);


    }

    private synchronized void notifyStartLoadingListeners(LoadStatus loadStatus) {


        final LoadStatus stat = loadStatus;
        Context context = PropertyHolder.getInstance().getMlinsContext();
        if (context == null) {
            return;
        }

        Handler mainHandler = new Handler(context.getMainLooper());

        Runnable myRunnable = new Runnable() {

            @Override
            public void run() {
                try {
                    for (ConfigsLoadListener listener : listeners) {
                        listener.onPreConfigsLoad(stat);
                    }
                } catch (Throwable t) {
                    t.printStackTrace();
                }
            }
        };
        mainHandler.post(myRunnable);

    }


    private synchronized void notifyLoadingLanguageStartedListeners() {


        Context context = PropertyHolder.getInstance().getMlinsContext();
        if (context == null) {
            return;
        }

        Handler mainHandler = new Handler(context.getMainLooper());

        Runnable myRunnable = new Runnable() {

            @Override
            public void run() {
                try {
                    for (LanguageLoadListener listener : langLoadListeners) {
                        listener.onPreLoadLanguage();
                    }
                } catch (Throwable t) {
                    t.printStackTrace();
                }
            }
        };
        mainHandler.post(myRunnable);

    }

    private synchronized void notifyLoadingLanguageFinishedListeners() {

        Context context = PropertyHolder.getInstance().getMlinsContext();
        if (context == null) {
            return;
        }

        Handler mainHandler = new Handler(context.getMainLooper());

        Runnable myRunnable = new Runnable() {

            @Override
            public void run() {
                List<LanguageLoadListener> langLoadListenersCopy = new ArrayList<>();
                langLoadListenersCopy.addAll(langLoadListeners);
                try {
                    for (LanguageLoadListener listener : langLoadListenersCopy) {
                        listener.onPostLoadLanguage();
                    }
                } catch (Throwable t) {
                    t.printStackTrace();
                }
            }
        };
        mainHandler.post(myRunnable);


    }

    public synchronized boolean registerLangLoadListener(LanguageLoadListener configsLoadListener) {

        try {
            if (!langLoadListeners.contains(configsLoadListener)) {
                return langLoadListeners.add(configsLoadListener);
            } else {
                return false;
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return false;

    }

    public synchronized boolean unregisterLangLoadListener(LanguageLoadListener configsLoadListener) {

        try {
            if (langLoadListeners.contains(configsLoadListener)) {
                return langLoadListeners.remove(configsLoadListener);
            } else {
                return false;
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return false;

    }


    private synchronized boolean addCampus(String campusId) {
        boolean res = false;
        if (!loadedCampuses.contains(campusId)) {
            loadedCampuses.add(campusId);
            res = true;
        }
        return res;
    }

    private synchronized boolean addFacility(String campusId, String facilityId) {

//		boolean res = false;
//		String key = campusId+"::"+facilityId;
//		if(!loadedFacilities.contains(key)){
//			loadedFacilities.add(key);
//			res= true;
//		}
//		return true;

        boolean res = false;
        String key = campusId + "::" + facilityId;
        if (!loadedFacility.equals(key)) {
            loadedFacility = key;
            res = true;
        }
        return res;
    }


    public synchronized boolean removeLoadedCampus(String campusId) {
        boolean res = false;
        if (loadedCampuses.contains(campusId)) {
            loadedCampuses.add(campusId);
            res = true;
        }
        return res;
    }


    public synchronized boolean removeLoadedFacility(String campusId, String facilityId) {

        boolean res = false;
        String key = campusId + "::" + facilityId;
        if (loadedFacilities.contains(key)) {
            loadedFacilities.remove(key);
            res = true;
        }
        return res;
    }

    public void loadCampus(String campusId, boolean isNotifyListener) {


        Log.getInstance().debug(TAG, "Enter, loadCampus()");
        boolean res = false;
        try {


            if (addCampus(campusId)) {

                if (isNotifyListener) {
                    notifyStartLoadingListeners(LoadStatus.LOAD_CAMPUS_START);
                }

                //PropertyHolder.getInstance().loadGlobals();

                PropertyHolder.getInstance().setCampusId(campusId);

                String url = ServerConnection.getBaseUrlOfCampusResList(campusId);

                ProjectConf.getInstance().loadCampuses();

                Campus campus = ProjectConf.getInstance().getCampus(campusId);

                CampusLevelResDownloader.releaseCinstance();
                byte[] data = CampusLevelResDownloader.getCInstance().getUrl(url);

                String jsonTxt = new String(data, "UTF-8");

                campus.Parse(jsonTxt);

                campus.loadFacilities();

                CampusMapTilesManager.getInstance().createMapTiles(false);

                CampusGisData.releaseInstance();

                CampusGisData.getInstance().loadGis();


                res = true;
                if (isNotifyListener) {
                    notifyListeners(LoadStatus.LOAD_CAMPUS_SUCCES);
                }
            } else {
                if (isNotifyListener) {
                    notifyStartLoadingListeners(LoadStatus.LOAD_CAMPUS_START);
                    notifyListeners(LoadStatus.LOAD_CAMPUS_SUCCES);
                }
                res = true;
            }


        } catch (Exception e) {
            e.printStackTrace();
            if (isNotifyListener) {
                notifyListeners(LoadStatus.LOAD_CAMPUS_FAILD);
            }
            res = false;
        }

        Log.getInstance().debug(TAG, "Exit, loadCampus()");

        //return res;
    }

    public boolean loadCampus(final String campusId) {

        Log.getInstance().debug(TAG, "Enter, loadCampus()");
        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                loadCampus(campusId, true);
                return null;
            }
        };

        task.execute();
        return true;
    }


    public boolean loadFacility(final String campusId, final String facilityId) {


        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {


                Log.getInstance().debug(TAG, "Enter, loadFacility()");

                try {

                    if (addFacility(campusId, facilityId)) {
                        notifyStartLoadingListeners(LoadStatus.LOAD_FACILITY_START);

                        loadCampus(campusId, false);


                        // XXX ResourceDownloader.releaseInstance();
                        //FacilityConf.releaseInstance();
                        PoiDataHelper.releaseInstance();
                        PropertyHolder.getInstance().setFacilityID(facilityId);
                        String url = ServerConnection.getBaseUrlOfFacilityResList(facilityId, campusId);
                        byte[] data = ResourceDownloader.getInstance().getUrl(url);
                        String jsonTxt = new String(data, "UTF-8");


                        Campus campus = ProjectConf.getInstance().getCampus(campusId);
                        if (campus != null) {
                            Map<String, FacilityConf> facilitiesmap = campus.getFacilitiesConfMap();
                            FacilityConf facility = facilitiesmap.get(facilityId);

                            //FacilityConf.setInstance(facility);
                            //FacilityConf.getInstance().Parse(jsonTxt);
                            FacilityContainer.getInstance().setSelected(facility);
                            facility.Parse(jsonTxt);

                        }

                        /**
                         try {

                         data=ResourceDownloader.getInstance().getLocalCopy("poi/poi_types.json");
                         jsonTxt = new String(data, "UTF-8");
                         PoiDataHelper.getInstance().Parse(jsonTxt);

                         } catch (UnsupportedEncodingException e) {
                         e.printStackTrace();

                         }
                         */
                        //load parameters
                        //				ParametersConfigsUtils.load();


                        //				LocationFinder.getInstance().resetFirstRun();

//									SwitchFloorHolder.getInstance().addFacility(facilityId);

                        /** GeoFenceHelper.Load(); */
                        /** PoiDataHelper.getInstance().getAllPoi(); */

                        //LabelsDataHolder.getInstance().load();

                        // create tiles an save on SD card
//									FacilityMapTilesManager.getInstance().createFacilityFloorsMapTiles(false);

                        //				LocationFinder.getInstance().resetFirstRun();

                        notifyListeners(LoadStatus.LOAD_FACILITY_SUCCES);

                    } else {
                        notifyStartLoadingListeners(LoadStatus.LOAD_FACILITY_START);
                        notifyListeners(LoadStatus.LOAD_FACILITY_SUCCES);
                    }

                } catch (Exception e) {

                    e.printStackTrace();
                    notifyListeners(LoadStatus.LOAD_FACILITY_FALID);

                }

                Log.getInstance().debug(TAG, "Exit, loadFacility()");

                return null;
            }

        };


        task.execute();

        return true;
    }


    public void reloadLangaugeData() {


        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    notifyLoadingLanguageStartedListeners();

                    PoisContainer poisContainer = new PoisContainer();
                    //LabelsContainer labelsContainer = new LabelsContainer();

                    ProjectConf.getInstance().loadCampuses();
                    Map<String, Campus> campuses = ProjectConf.getInstance().getCampusesMap();
                    if (campuses != null) {
                        if (campuses.size() > 0) {
                            for (String campusId : campuses.keySet()) {
                                ConfigsLoader.getInstance().loadCampus(campusId, false);
                                break;
                            }
                        }
                        for (Campus campus : campuses.values()) {

                            if (campus != null) {

                                String campusId = campus.getId();
                                PropertyHolder.getInstance().setCampusId(campusId);
                                campus.loadFacilities();

                                Map<String, FacilityConf> facilitiesmap = campus.getFacilitiesConfMap();

                                if (facilitiesmap != null && facilitiesmap.size() > 0) {

                                    for (String facilityId : facilitiesmap.keySet()) {
                                        PropertyHolder.getInstance().setFacilityID(facilityId);
                                        FacilityContainer.getInstance().setSelected(facilitiesmap.get(facilityId));
                                        loadPois(poisContainer, campusId, facilityId);
                                        //SwitchFloorHolder.getInstance().addFacility(facilityId);
                                        //FacilityMapTilesManager.getInstance().createFacilityFloorsMapTiles(false);
                                        //labelsContainer.addFacilityData(campusId,facilityId);

                                        //GeoFenceHelper.getInstance().addFacilityData(campusId, facilityId);

                                    }
                                }
                            }
                        }
                    }

                    ProjectConf.getInstance().setPoisContainer(poisContainer);
                    ProjectConf.getInstance().loadExternalPoisKDimensionalTree();
                    //labelsContainer.computeDrawables();
                    //ProjectConf.getInstance().setLabelsContainer(labelsContainer);

                    notifyLoadingLanguageFinishedListeners();


                } catch (Throwable t) {
                    t.printStackTrace();
                } finally {
                    PoiDataHelper.releaseInstance();
                }

                return null;

            }
        };

        task.execute();
    }

    private void loadPois(PoisContainer poisContainer, String campusId, String facilityId) {
        try {

            String uri = ServerConnection.getBaseUrlOfFacilityResList(facilityId, campusId);
            byte[] conf = ResourceDownloader.getInstance().getLocalCopy(uri);
            String resjson = new String(conf);

            FacilityConf facConf = new FacilityConf(facilityId);
            facConf.ParseFloors(resjson);
            PoiDataHelper poiHelper = new PoiDataHelper();
            List<IPoi> list = poiHelper.getAllFacilityPois(facConf);

            poisContainer.addPois(campusId, facilityId, list);

            List<PoiType> categories = poiHelper.getPoiCategories();
            poisContainer.addCategoryList(categories);
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

	/*
	public  void loadAllCampusesPoisList() {
			
			notifyLoadingLanguageStartedListeners();
			try
			{
			PoisContainer poisContainer = new PoisContainer();
			
			
				ProjectConf.getInstance().loadCampuses();
				Map<String,Campus> campuses = ProjectConf.getInstance().getCampusesMap();
				if(campuses!=null){
					for(Campus campus: campuses.values()){
						
						if (campus != null) {
							
							String campusId = campus.getId();
							PropertyHolder.getInstance().setCampusId(campusId);
							campus.loadFacilities();
							
							Map<String, FacilityConf> facilitiesmap = campus.getFacilitiesConfMap();
					
							if (facilitiesmap != null && facilitiesmap.size() > 0) {
					
								for (String facilityId : facilitiesmap.keySet()) {
									try{
										
										PropertyHolder.getInstance().setFacilityID(facilityId);
										
										String uri = ServerConnection.getBaseUrlOfFacilityResList(facilityId, campusId);
										byte[] conf = ResourceDownloader.getInstance().getLocalCopy(uri);
										String resjson = new String(conf);
			
										FacilityConf facConf = new FacilityConf(facilityId);
										facConf.ParseFloors(resjson);
										PoiDataHelper poiHelper = new PoiDataHelper();
										List<IPoi> list =  poiHelper.getAllFacilityPois(facConf);
										
										poisContainer.addPois(campusId, facilityId, list);
									}catch(Throwable t){
										  t.printStackTrace();
										}	
								}					
							}
						}	
					}
				}
		
				ProjectConf.getInstance().setPoisContainer(poisContainer);
				notifyLoadingLanguageFinishedListeners();
				
			}catch(Throwable t){
			  t.printStackTrace();
			}
			finally{
			
			}
	
	
	}
	*/

    public void clearLoadedFacility() {
        loadedFacility = "";
    }

}
