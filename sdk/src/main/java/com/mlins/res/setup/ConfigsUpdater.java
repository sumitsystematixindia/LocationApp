package com.mlins.res.setup;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;

import com.mlins.labels.LabelsContainer;
import com.mlins.overlay.FacilityMapTilesManager;
import com.mlins.polygon.FloorPolygonManager;
import com.mlins.project.Campus;
import com.mlins.project.ProjectConf;
import com.mlins.switchfloor.SwitchFloorHolder;
import com.mlins.ui.utils.ProjectIconsManager;
import com.mlins.utils.CampusLevelResDownloader;
import com.mlins.utils.Cleanable;
import com.mlins.utils.DataZipDecompresser;
import com.mlins.utils.ExitsSelectionManager;
import com.mlins.utils.FacilityConf;
import com.mlins.utils.FacilityContainer;
import com.mlins.utils.FacilitySelector;
import com.mlins.utils.FloorData;
import com.mlins.utils.Lookup;
import com.mlins.utils.MatrixDataHelper;
import com.mlins.utils.PoiDataHelper;
import com.mlins.utils.PoiType;
import com.mlins.utils.PoisContainer;
import com.mlins.utils.PropertyHolder;
import com.mlins.utils.ResourceDownloader;
import com.mlins.utils.ResourceTranslator;
import com.mlins.utils.ServerConnection;
import com.mlins.utils.logging.Log;
import com.spreo.enums.ResUpdateStatus;
import com.spreo.geofence.GeoFenceHelper;
import com.spreo.geofence.GeofenceContentManager;
import com.spreo.geofence.LocationBeaconsManager;
import com.spreo.interfaces.ConfigsUpdaterListener;
import com.spreo.nav.interfaces.IPoi;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipInputStream;

//import android.content.res.Resources;
//import android.os.AsyncTask;

public class ConfigsUpdater implements Cleanable {

    final static int SDK_ENABLED = 0;
    final static int ERROR_TYPE_CONNECTION = 1;
    final static int ERROR_TYPE_INVALID_APIKEY = 2;
    final static int NO_RESPONSE = 3;

    private String TAG = "com.mlins.res.setup.ConfigsDownloader";
    private List<ConfigsUpdaterListener> listeners = Collections.synchronizedList(new ArrayList<ConfigsUpdaterListener>());
    private String reqApikey = null;
    private Context context;
    private String APIKEY_RES_FILE_NAME = "project.chk";
    private String ZIP_RES_CONF_FILE_NAME = "ios_spreo_compressed_res_info.json";
    private boolean useLocalRes = false;
    private String apikeyResponse = null;
    private String noMediaFileName = ".nomedia";
    private boolean mapReady;

    public static ConfigsUpdater getInstance() {
        return Lookup.getInstance().get(ConfigsUpdater.class);
    }

    public static void releaseInstance() {
        Lookup.getInstance().remove(ConfigsUpdater.class);
    }

    public void clean() {
        listeners.clear();
    }

    private synchronized void notifyMapReady(final ResUpdateStatus status) {
        mapReady = true;
        Handler mainHandler = new Handler(context.getMainLooper());
        Runnable myRunnable = new Runnable() {
            @Override
            public void run() {
                try {
                    List<ConfigsUpdaterListener> lrs = new ArrayList<>(listeners);
                    for (ConfigsUpdaterListener listener : lrs) {
                        if (listener instanceof DataReadyListener) {
                            ((DataReadyListener) listener).onMapReady(status);
                        }
                    }
                } catch (Throwable t) {
                    t.printStackTrace();
                }
            }
        };
        mainHandler.post(myRunnable);
    }

    private synchronized void notifyDataReady(final ResUpdateStatus status) {
        Handler mainHandler = new Handler(context.getMainLooper());
        Runnable myRunnable = new Runnable() {
            @Override
            public void run() {
                try {
                    List<ConfigsUpdaterListener> lrs = new ArrayList<>(listeners);
                    for (ConfigsUpdaterListener listener : lrs) {
                        if (listener instanceof DataReadyListener) {
                            ((DataReadyListener) listener).onDataReady(status);
                        }
                    }
                } catch (Throwable t) {
                    t.printStackTrace();
                }
            }
        };
        mainHandler.post(myRunnable);
    }

    private synchronized void notifyListeners(ResUpdateStatus status) {


        final ResUpdateStatus stat = status;
        Handler mainHandler = new Handler(context.getMainLooper());

        Runnable myRunnable = new Runnable() {

            @Override
            public void run() {
                try {
                    for (ConfigsUpdaterListener listener : listeners) {
                        listener.onPostConfigsDownload(stat);
                    }
                } catch (Throwable t) {
                    t.printStackTrace();
                }
            }
        };
        mainHandler.post(myRunnable);

    }

    private synchronized void notifyStartDownloadingListeners() {


        Handler mainHandler = new Handler(context.getMainLooper());

        Runnable myRunnable = new Runnable() {

            @Override
            public void run() {
                try {
                    for (ConfigsUpdaterListener listener : listeners) {
                        listener.onPreConfigsDownload();
                    }
                } catch (Throwable t) {
                    t.printStackTrace();
                }
            }
        };
        mainHandler.post(myRunnable);

    }


    private synchronized void notifyInitListeners(ResUpdateStatus status) {

        final ResUpdateStatus stat = status;
        Handler mainHandler = new Handler(context.getMainLooper());

        Runnable myRunnable = new Runnable() {

            @Override
            public void run() {
                try {
                    for (ConfigsUpdaterListener listener : listeners) {
                        listener.onPostConfigsInit(stat);
                    }
                } catch (Throwable t) {
                    t.printStackTrace();
                }
            }
        };
        mainHandler.post(myRunnable);

    }

    private synchronized void notifyStartIntListeners() {

        Handler mainHandler = new Handler(context.getMainLooper());

        Runnable myRunnable = new Runnable() {

            @Override
            public void run() {
                try {
                    for (ConfigsUpdaterListener listener : listeners) {
                        listener.onPreConfigsInit();
                    }
                } catch (Throwable t) {
                    t.printStackTrace();
                }
            }
        };
        mainHandler.post(myRunnable);

    }


    public void setReqApikey(String reqApikey) {
        this.reqApikey = reqApikey;
    }

    public void download(Context context) {


        this.context = context;
        this.mapReady = false;
        Log.getInstance().debug(TAG, "Enter, download()");
        notifyStartDownloadingListeners();


        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {


                checkStorageLoaction();

                //temp fix. delete old folder if nomedia file not exists
//				CheckNoMediaFile();

                CreatNoMediaFile();

                String apikey = reqApikey;

                if (apikey == null) {
                    apikey = getApiKeyFromManifest();
                }

                if (PropertyHolder.useZip) {
                    boolean isOk = checkOutProjectZip(apikey);
                    if (isOk) {
                        notifyListeners(ResUpdateStatus.OK);
                    } else {
                        notifyListeners(ResUpdateStatus.FAILED);
                    }
                } else {
                    boolean isCheckResourceDataOK = checkOutProjectDataResources(apikey);
                    if (!isCheckResourceDataOK) {
                        if (apikey != null && !apikey.isEmpty()) {
                            downloadProjectResourcesByApikey(apikey);

                        } else {
                            notifyListeners(ResUpdateStatus.API_KEY_VALIDATION_FAILD);

                        }
                    }
                }

                return null;

            }

        };

        task.execute();

//		Thread setupThread = new Thread(new Runnable() {
//			
//			@Override
//			public void run() {
//					
//				String apikey = reqApikey;
//				
//				if(apikey == null){
//					apikey = getApiKeyFromManifest();
//				}	
//				
//				boolean isCheckResourceDataOK =checkOutProjectDataResources(apikey);
//
//				if(!isCheckResourceDataOK){
//					
//					
//					if (apikey != null && !apikey.isEmpty()) {
//							downloadProjectResourcesByApikey(apikey);
//							
//					} else {
//							notifyListeners(ResUpdateStatus.API_KEY_VALIDATION_FAILD);
//						
//					}
//				}
//				
//			}
//
//			
//		});
//		
//		//setupThread.start();
//		Handler handler = new Handler(context.getMainLooper());
//		handler.post(setupThread);

        Log.getInstance().debug(TAG, "Exit, appinit()");
    }


    private boolean checkOutProjectZip(String preDefindedApikey) {

        String updateBatchFileName = "ios_update_batch";
        String fullUpdateFileName = "ios_spreo_compressed_res";

        if (PropertyHolder.getInstance().isUseZipWithoutMaps()) {
            updateBatchFileName = "ios_update_batch_no_map";
            fullUpdateFileName = "ios_spreo_compressed_res_no_map";
        }
        boolean checkResult = true;

        String projectGlobalSettings[] = loadApikeyResFile();

        try {
            String localPid = null;
            String apik = null;
            if (projectGlobalSettings != null
                    && projectGlobalSettings.length > 0) {
                localPid = projectGlobalSettings[0];
                apik = projectGlobalSettings[1];
            }

            if (localPid != null && preDefindedApikey.equals(apik)) {  // directory exists

                String version = getZipBatchVersion(localPid);
                // try to download batch update
                downloadProjectZip(localPid, updateBatchFileName, "/" + version, 1);
                // load any way
                zipInitialization(localPid); // load

            } else {
                int islocalZipOk = unzipProjectResFromLocalStorage(fullUpdateFileName);
                if (islocalZipOk == 1) { // if local zip exists
                    String projectParams[] = loadApikeyResFile();
                    String localZipPid = projectParams[0];
                    String localZipApik = projectParams[1];

                    // local zip is for the desired project
                    if (localZipPid != null && preDefindedApikey.equals(localZipApik)) {
                        String version = getZipBatchVersion(localZipPid);
                        downloadProjectZip(localZipPid, updateBatchFileName, "/" + version, 1); // update batch
                        zipInitialization(localZipPid); // load
                    } else { // local zip is not for the requested project
                        // download the correct project
                        String pid = getProjectIdFromServer(preDefindedApikey);

                        if (pid != null) {  // try to download full

                            boolean isDownloadZipOk = downloadProjectZip(pid, fullUpdateFileName, "", 2);

                            if (isDownloadZipOk) {  // Succeeded to download zip
                                zipInitialization(pid);
                            } else {
                                checkResult = false;
                            }
                        }
                    }

                } else { // local zip is not exits

                    String pid = getProjectIdFromServer(preDefindedApikey);

                    if (pid != null) {  // try to download full

                        boolean isDownloadZipOk = downloadProjectZip(pid, fullUpdateFileName, "", 2);

                        if (isDownloadZipOk) {  // Succeeded to download zip
                            zipInitialization(pid);
                        } else {
                            checkResult = false;
                        }
                    } else {
                        checkResult = false;
                    }
//				else{ // load local project settings
//					 zipInitialization(localPid); 
//				}
                }
            }
        } catch (Throwable t) {

        } finally {
            cleanZipFiles(updateBatchFileName, fullUpdateFileName);
        }

        return checkResult;

    }


    private void cleanZipFiles(String updateBatchFileName, String fullUpdateFileName) {
        try {
            File root = new File(PropertyHolder.getInstance().getExternalStoragedir());
            File fullzip = new File(root, fullUpdateFileName + ".zip");

            if (fullzip.exists()) {
                fullzip.delete();
            }
            File batchZip = new File(root, updateBatchFileName + ".zip");

            if (batchZip.exists()) {
                batchZip.delete();
            }

        } catch (Throwable t) {

        }

    }

    private String getZipBatchVersion(String pid) {

        String version = "0";
        File resListFile = new File(PropertyHolder.getInstance().getZipAppdir() + "/" + pid, "version.json");
        if (!resListFile.exists()) {
            return version;
        }

        try {
            BufferedReader br = null;
            StringBuffer sb = new StringBuffer();
            if (resListFile.exists()) {
                try {

                    br = new BufferedReader(new FileReader(resListFile));
                    String line = null;
                    while ((line = br.readLine()) != null) {
                        sb.append(line);
                    }

                    JSONTokener tokener = new JSONTokener(sb.toString());
                    JSONObject json = (JSONObject) tokener.nextValue();

                    version = json.getString("version");

                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (br != null) {
                        try {

                            br.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }

        return version;

    }

    private void downloadProjectResourcesByApikey(String apikey) {

        System.out.println("ConfigsUpdater::Check Apikey");

        int response = NO_RESPONSE;
        String projectId = null;


        String servername = PropertyHolder.getInstance().getServerName();
        String url = servername + "apikey?req=0&apik=" + apikey;
        try {
            byte[] bytes = ServerConnection.getInstance().getResourceBytes(url);
            if (bytes.length == 0) {
                response = ERROR_TYPE_CONNECTION;
            } else {
                String res = new String(bytes);
                String[] params = parseApiKeyResponse(res);
                projectId = params[0];

                if (projectId != null && params[1].equals(apikey)) {
                    response = SDK_ENABLED;
                } else {
                    response = ERROR_TYPE_INVALID_APIKEY;
                }
            }
        } catch (Exception e) {
            Log.getInstance().error("downloadProjectResourcesByApikey", e.getMessage(), e);
            e.printStackTrace();
            response = ERROR_TYPE_CONNECTION;
        }

        apiKeyCheckResult(response, projectId);


    }

    private void CheckNoMediaFile() {
        try {
            File dir = PropertyHolder.getInstance().getAppDir();
            if (dir != null && dir.exists()) {
                File noMediaFile = new File(dir, noMediaFileName);
                if (!noMediaFile.exists()) {
                    deleteRecursive(dir);
                }
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    private void deleteRecursive(File fileOrDirectory) {
        if (fileOrDirectory.isDirectory()) {
            for (File child : fileOrDirectory.listFiles()) {
                deleteRecursive(child);
            }
        }

        fileOrDirectory.delete();
    }

    protected void CreatNoMediaFile() {
        try {
            File dir = PropertyHolder.getInstance().getAppDir();
            if (PropertyHolder.useZip) {
                dir = PropertyHolder.getInstance().getZipAppdir();
            }
            if (dir != null && dir.exists()) {
                File noMediaFile = new File(dir, noMediaFileName);
                if (!noMediaFile.exists()) {
                    noMediaFile.createNewFile();
                }
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    private String getApiKeyFromManifest() {
        Log.getInstance().debug(TAG, "Enter, getApiKeyFromManifest()");
        String result = null;
        try {
            ApplicationInfo ai = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            Bundle bundle = ai.metaData;
            result = bundle.getString("spreo_api_key");
        } catch (NameNotFoundException e) {
            Log.getInstance().error(TAG, e.getMessage(), e);
        } catch (NullPointerException e) {
            Log.getInstance().error(TAG, e.getMessage(), e);
        }
        Log.getInstance().debug(TAG, "Exit, getApiKeyFromManifest()");
        return result;
    }

    public void apiKeyCheckResult(int response, String projectId) {

        switch (response) {
            case SDK_ENABLED:
                downloadProjectResources(projectId);
                break;
            case ERROR_TYPE_INVALID_APIKEY:
                notifyListeners(ResUpdateStatus.API_KEY_ERROR_TYPE_INVALID_APIKEY);
                break;
            case ERROR_TYPE_CONNECTION:
                // continue offline if data exist
                File appDir = PropertyHolder.getInstance().getAppDir();
                File[] contents = appDir.listFiles();
                String result[] = loadApikeyResFile();
                String pid = result[0];
                if (appDir.exists() && contents != null && contents.length > 0 && pid != null) {

                    forceInitialization(pid);
                    notifyListeners(ResUpdateStatus.OK);

                } else {
                    notifyListeners(ResUpdateStatus.API_KEY_ERROR_TYPE_CONNECTION);
                }

                break;
            case NO_RESPONSE:
                notifyListeners(ResUpdateStatus.API_KEY_NO_RESPONSE);
                break;

            default:
                break;
        }

    }

    private void downloadProjectResources(String projectId) {
        Log.getInstance().debug(TAG, "Enter, downloadProjectResources()");
        checkDeveloperMode();
        checkStorageLoaction();
        PropertyHolder.getInstance().setProjectId(projectId);
        PropertyHolder.getInstance().setProjectDir(projectId);
        PropertyHolder.getInstance().loadGlobals();

        ProjectIconsManager.getInstance().downloadIconsRes();

        downloadCampusesResources();

        Log.getInstance().debug(TAG, "Exit, downloadProjectResources()");
    }


    private void downloadCampusesResources() {


        if (useLocalRes) {
            return;
        }

        boolean result = ProjectConf.getInstance().downloadCampusesJsonRes();

        ResUpdateStatus status = null;
        if (result) {
            status = ResUpdateStatus.CAMPUSES_JSON_OK;
        } else {
            status = ResUpdateStatus.CAMPUSES_JSON_FAIL;
        }

        if (status.equals(ResUpdateStatus.CAMPUSES_JSON_OK)) {

            int campusLevelState = 1;

            ProjectConf.getInstance().loadCampuses();
            Map<String, Campus> campuses = ProjectConf.getInstance().getCampusesMap();
            Set<String> ids = campuses.keySet();

            String campusesIds[] = new String[ids.size()];
            campusesIds = ids.toArray(campusesIds);

            try {
                for (int i = 0; i < campusesIds.length; i++) {
                    CampusLevelResDownloader.releaseCinstance();
                    System.out.println("ConfigsUpdater::DownloadCampuse == > " + campusesIds[i]);
                    Campus campus = downloadCampus(campusesIds[i]);
                    if (campus != null) {
                        campus.loadFacilities();
                        Map<String, FacilityConf> facilitiesmap = campus.getFacilitiesConfMap();

                        if (facilitiesmap != null && facilitiesmap.size() > 0) {

                            for (String facilityId : facilitiesmap.keySet()) {
                                System.out.println("ConfigsUpdater::DownloadFacility == > " + facilityId);
                                downloadFacility(campusesIds[i], facilityId);
                            }
                        }
                    }

                }

                //ConfigsLoader.getInstance().loadAllCampusesPoisList();
                loadFacilitiesData();
                //cleanState();

            } catch (Throwable t) {
                t.printStackTrace();
                campusLevelState = 0;
            }

            if (campusLevelState == 1) {
                saveApiKeyResponse();
                notifyListeners(ResUpdateStatus.OK);
            } else {
                notifyListeners(ResUpdateStatus.FAILED);
            }


        } else {
            notifyListeners(ResUpdateStatus.CAMPUSES_JSON_FAIL);
        }

    }


    private void checkDeveloperMode() {
        File dir = PropertyHolder.getInstance().getAppDir();
        String fileName = "admin";
        File file = new File(dir, fileName);
        if (file.exists()) {
            PropertyHolder.getInstance().setDevelopmentMode(true);

        } else {
            PropertyHolder.getInstance().setDevelopmentMode(false);
        }

    }

    public void checkStorageLoaction() {
        // test if sdcard is writable
        BufferedWriter out = null;
        String dirName = "spreo";
        try {
            PropertyHolder.getInstance().getAppDir().mkdirs();
            File t = new File(PropertyHolder.getInstance().getAppDir(), "test.txt");
            out = new BufferedWriter(new FileWriter(t));
            out.write("test");
            out.flush();
            out.close();
            out = null;
        } catch (Exception e) {
            Log.getInstance().error(TAG, e.getMessage(), e);
            String altstorage = PropertyHolder.getInstance().getExternalStoragedir();
            altstorage = altstorage.replaceAll("sdcard", "sdcard2");
            PropertyHolder.getInstance().setAppDir(altstorage + dirName);
            PropertyHolder.getInstance().setExternalStorage(altstorage);
        } finally {
            if (out != null) {
                try {

                    out.close();
                } catch (Exception e) {
                    Log.getInstance().error(TAG, e.getMessage(), e);

                }

            }
        }
    }


    public boolean registerListener(ConfigsUpdaterListener downloadinglistener) {
        try {
            if (!listeners.contains(downloadinglistener)) {
                return listeners.add(downloadinglistener);
            } else {
                return false;
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
        return false;
    }

    public boolean unregisterListener(ConfigsUpdaterListener downloadinglistener) {
        try {
            if (listeners.contains(downloadinglistener)) {
                return listeners.remove(downloadinglistener);
            } else {
                return false;
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
        return false;
    }

    public String[] loadApikeyResFile() {

        String fileUri = APIKEY_RES_FILE_NAME;
        File dir = PropertyHolder.getInstance().getAppDir();
        if (PropertyHolder.useZip) {
            fileUri = ZIP_RES_CONF_FILE_NAME;
            dir = PropertyHolder.getInstance().getZipAppdir();
        }


        File resListFile = new File(dir, fileUri);
        BufferedReader br = null;
        StringBuffer sb = new StringBuffer();
        if (resListFile.exists()) {
            try {

                br = new BufferedReader(new FileReader(resListFile));
                String line = null;
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (br != null) {
                    try {

                        br.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        return parseApiKeyResponse(sb.toString());

    }


    private String[] parseApiKeyResponse(String res) {

        String[] result = new String[2];

        String projectid = null;
        String apikey = null;
        try {
            JSONTokener tokener = new JSONTokener(res);
            JSONObject json = (JSONObject) tokener.nextValue();

            projectid = json.getString("pid");

            // get scan type and uuids
            try {
                String uuidType = json.getString("ble_scan_type");
                PropertyHolder.getInstance().setUuidScanType(uuidType);
            } catch (Throwable t) {
                t.printStackTrace();
            }

            try {

                JSONArray uuidList = json.getJSONArray("uuid_list");
                List<String> uuids = new ArrayList<String>();
                for (int i = 0; i < uuidList.length(); i++) {
                    String uid = uuidList.getString(i);
                    if (uid != null) {
                        uid = fixEscaping(uid);
                        uuids.add(uid);
                    }
                }

                if (uuids.size() > 0) {
                    PropertyHolder.getInstance().setUuidScan(true);
                    PropertyHolder.getInstance().setUuidList(uuids);
                } else {
                    PropertyHolder.getInstance().setUuidScan(false);
                }
            } catch (Throwable t) {
                t.printStackTrace();
            }


            try {
                apikey = json.getString("apikey");
            } catch (Throwable t) {
                t.printStackTrace();
            }

            result[0] = projectid;
            result[1] = apikey;

        } catch (Throwable e) {
            //e.printStackTrace();
        }


        return result;

    }

    public void saveApiKeyResponse() {

        if (apikeyResponse != null) {

            File resListFile = new File(PropertyHolder.getInstance().getAppDir(), APIKEY_RES_FILE_NAME);
            BufferedWriter bw = null;
            try {

                if (!resListFile.exists()) {
                    resListFile.createNewFile();
                }

                bw = new BufferedWriter(new FileWriter(resListFile, false));

                bw.write(apikeyResponse);

                bw.flush();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (bw != null) {
                    try {
                        bw.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

    }


    public boolean writeZipLocalCopy(File file, byte[] bytes) {

        FileOutputStream output = null;
        try {
            output = new FileOutputStream(file);
            output.write(bytes);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            if (output != null) {
                try {
                    output.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return true;

    }


    private String fixEscaping(String content) {
        if (content == null) {
            return content;
        }
        StringBuffer outBuffer = new StringBuffer(content);
        String data = outBuffer.toString();
        try {

            //data = data.replaceAll("(\\s)", "_");
            //data = data.replaceAll("([\\W&^\\.])", "_");
            data = data.replaceAll("-", "");


            //data = data.replaceAll("\\+", "%2B");
            //data = URLDecoder.decode(data, "utf-8");
        } catch (Exception e) {
            e.printStackTrace();
            return content;
        }
        return data;

    }


    public boolean intializeData(Context context) {
        this.context = context;
        notifyStartIntListeners();

        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {

        int resCode = unzipProjectResFromLocalStorage();
        String pid = null;
        if (resCode != 0) {
            String[] result = loadApikeyResFile();
            pid = result[0];
            if (pid == null) { // something went wrong!
                resCode = 0;
            }
        }

        if (resCode != 0) {
            forceInitialization(pid);
            notifyInitListeners(ResUpdateStatus.OK);
            return null;
        } else {
            notifyInitListeners(ResUpdateStatus.FAILED);
            return null;
        }

            }

        };

        task.execute();

        return true;
    }


    private void loadFacilitiesData() {
        try {
            PoisContainer poisContainer = new PoisContainer();
            LabelsContainer labelsContainer = new LabelsContainer();

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

                            LocationBeaconsManager.getInstance();

                            for (String facilityId : facilitiesmap.keySet()) {
                                PropertyHolder.getInstance().setFacilityID(facilityId);
                                FacilityContainer.getInstance().setSelected(facilitiesmap.get(facilityId));
                                loadPois(poisContainer, campusId, facilityId);
                                SwitchFloorHolder.getInstance().addFacility(facilityId);
                                if (!campus.isUsingFloorTiles()) {
                                    FacilityMapTilesManager.releaseInstance();
                                    FacilityMapTilesManager.getInstance().createFacilityFloorsMapTiles(false);
                                }
                                labelsContainer.addFacilityData(campusId, facilityId);

                                GeoFenceHelper.getInstance().addFacilityData(campusId, facilityId);

                                GeofenceContentManager.getInstance().addData(campusId, facilityId);

                                if (PropertyHolder.getInstance().isUseProximityLocation()) {
                                    LocationBeaconsManager.getInstance().loadFacilityHalfNav(campusId, facilityId);
                                }
                            }
                        }
                    }
                }
            }

            ProjectConf.getInstance().setPoisContainer(poisContainer);
            ProjectConf.getInstance().loadBridges();
            ProjectConf.getInstance().loadExternalPoisKDimensionalTree();
            labelsContainer.computeDrawables();
            ProjectConf.getInstance().setLabelsContainer(labelsContainer);
            FacilitySelector.releaseInstance();
            FacilitySelector.getInstance();

        } catch (Throwable t) {
            t.printStackTrace();
        } finally {
            PoiDataHelper.releaseInstance();
        }
        if (PropertyHolder.getInstance().isTrasnlateLabels()) {
            String campusid = PropertyHolder.getInstance().getCampusId();
            if (campusid != null) {
                LabelsContainer labelsContainer = ProjectConf.getInstance().getLabelsContainer();
                if (labelsContainer != null) {
                    labelsContainer.translateLabels();
                }
            }
            PropertyHolder.getInstance().setTrasnlateLabels(false);
        }
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

    private void forceInitialization(String pid) {

        checkDeveloperMode();
        checkStorageLoaction();
        PropertyHolder.getInstance().setProjectId(pid);
        PropertyHolder.getInstance().setProjectDir(pid);
        PropertyHolder.getInstance().loadGlobals();

        ProjectIconsManager.getInstance().downloadIconsRes();

        //ConfigsLoader.getInstance().loadAllCampusesPoisList();
        loadFacilitiesData();


        //ConfigsLoader.getInstance().clearLoadedFacility();
        //ConfigsLoader.releaseInstance();

    }

    private void zipInitialization(String pid) {
        //long time = System.currentTimeMillis();
        checkDeveloperMode();
        checkStorageLoaction();
        PropertyHolder.getInstance().setProjectId(pid);
        PropertyHolder.getInstance().setProjectDir(pid);
        PropertyHolder.getInstance().loadGlobals();
        notifyDataReady(ResUpdateStatus.OK);
        // in case zip it will use a local copy
        ProjectIconsManager.getInstance().downloadIconsRes();

        FloorPolygonManager.getInstance().loadData();
        ExitsSelectionManager.getInstance().load();

        loadFacilitiesData();
        notifyMapReady(ResUpdateStatus.OK);

        //System.out.println("load data take " + (System.currentTimeMillis()- time)/ 1000.0 + " sec");

    }


    private boolean checkOutProjectDataResources(String preDefindedApikey) {

        boolean checkResult = true;
        String[] result = null;
        try {
            result = loadApikeyResFile();  // check if app directory exists and contains data
            String pid = result[0];
            String apik = result[1];

            if (preDefindedApikey != null && apik != null && !preDefindedApikey.equals(apik)) {
                apik = null;
                pid = null;
            }

            if (pid != null && apik != null) { // the res directory exists

                syncDownloadProjectResources(pid, apik); // update resources

                //forceInitialization(pid); // continue to load with new data or previous data

                notifyListeners(ResUpdateStatus.OK);

            } else  // no res directory exists
            {

                int resCode = unzipProjectResFromLocalStorage(); // unzip local zip res file

                if (resCode != 0) { // exists --> continue to download
                    System.out.println("unzipProjectResFromLocalStorage ok");
                    result = loadApikeyResFile();
                    pid = result[0];
                    apik = result[1];

                    // local zip exists but not for the desired project
                    if (preDefindedApikey != null && apik != null && !preDefindedApikey.equals(apik)) {
                        //================================================================================================
                        apik = null;
                        pid = getProjectIdFromServer(preDefindedApikey); // given apikey try to get project id from server
                        if (pid != null) {

                            boolean isZipOk = downloadProjectZip(pid);

                            if (isZipOk) {  // Succeeded to download zip
                                forceInitialization(pid);  //continue to load
                                notifyListeners(ResUpdateStatus.OK);
                            } else {

                                checkResult = false; // try normal download process
                            }
                        } else {
                            checkResult = false; // try normal download process
                        }
                        //================================================================================================
                    } else  // its for the correct project
                    {
                        syncDownloadProjectResources(pid, preDefindedApikey);

                        //forceInitialization(pid);  // continue to load with new data or previous data

                        notifyListeners(ResUpdateStatus.OK);
                    }
                } else // zip not exists
                {
                    //================================================================================================
                    pid = getProjectIdFromServer(preDefindedApikey); // given apikey try to get project id from server
                    if (pid != null) {

                        boolean isZipOk = downloadProjectZip(pid);

                        if (isZipOk) {  // Succeeded to download zip
                            forceInitialization(pid);  //continue to load
                            notifyListeners(ResUpdateStatus.OK);
                        } else {

                            checkResult = false; // try normal download process
                        }
                    } else {
                        checkResult = false; // try normal download process
                    }
                    //================================================================================================

                }
            }

        } catch (Throwable e) {
            checkResult = false;
            e.printStackTrace();
        }

        return checkResult;

    }


    private boolean downloadProjectZip(String pid) {
        long time = System.currentTimeMillis();
        boolean isZipOk = false;
        if (pid == null) {
            return isZipOk;
        }

        InputStream in = null;
        ZipInputStream zin = null;

        try {
            final String ANDROID_ZIP_RES_FILE_NAME = "droid_spreo_res";

            String url = PropertyHolder.getInstance().getServerName() + "res/" + pid + "/" + ANDROID_ZIP_RES_FILE_NAME + ".zip";
            byte[] bytes = ServerConnection.getInstance().getResourceBytes(url);

            if (bytes.length != 0) {

                File root = PropertyHolder.getInstance().getAppDir();
                File f = new File(root, ANDROID_ZIP_RES_FILE_NAME + ".zip");

                if (f.exists()) {
                    f.delete();
                }

                writeZipLocalCopy(f, bytes);
                bytes = null;
                in = new FileInputStream(f);
                zin = new ZipInputStream(in);
                DataZipDecompresser dec = new DataZipDecompresser(zin, PropertyHolder.getInstance().getExternalStoragedir(), ANDROID_ZIP_RES_FILE_NAME);
                if (dec.unzip()) {
                    System.out.println("downloadZipResource unzip project data ==> ok");
                    isZipOk = true;
                }

            }

        } catch (Throwable e) {
            e.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (zin != null) {
                try {
                    zin.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        System.out.println("zip duration time " + (System.currentTimeMillis() - time) / 1000.0);
        return isZipOk;
    }

    private int unzipProjectResFromLocalStorage() {

        int isZipOk = 0;
        InputStream in = null;
        ZipInputStream zin = null;

        String zipname = "ios_spreo_compressed_res";
        if (PropertyHolder.getInstance().isUseZipWithoutMaps()) {
            zipname = "ios_spreo_compressed_res_no_map";
        }

        try {
            int rid = ResourceTranslator.getInstance().getTranslatedResourceId("raw", zipname);

            in = context.getResources().openRawResource(rid);
            zin = new ZipInputStream(in);
            DataZipDecompresser dec = new DataZipDecompresser(zin, PropertyHolder.getInstance().getExternalStoragedir() + "spreo/");
            if (dec.unzip()) {
                System.out.println("unzipProjectResFromLocalStorage unzip project data ==> ok");
                isZipOk = 1;
            } else {
                isZipOk = 0;
            }

        } catch (Throwable e) {
            e.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (zin != null) {
                try {
                    zin.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return isZipOk;

    }


    private String getProjectIdFromServer(String preDefindedApikey) {

        //int response = NO_RESPONSE;
        String projectId = null;

        try {


            if (preDefindedApikey != null) {

                String servername = PropertyHolder.getInstance().getServerName();
                String url = servername + "apikey?req=0&apik=" + preDefindedApikey;
                try {
                    byte[] bytes = ServerConnection.getInstance().getResourceBytes(url);
                    if (bytes.length != 0) {
                        //response = ERROR_TYPE_CONNECTION;
                        //} else {
                        String res = new String(bytes);

                        try {
                            JSONTokener tokener = new JSONTokener(res);
                            JSONObject json = (JSONObject) tokener.nextValue();
                            String status = json.getString("status");
                            if (!status.equals("fail")) {
                                //response = ERROR_TYPE_INVALID_APIKEY;
                                //} else {
                                String projectid = json.getString("pid");

                                // get scan type and uuids
                                try {
                                    String uuidType = json.getString("ble_scan_type");
                                    PropertyHolder.getInstance().setUuidScanType(uuidType);
                                } catch (Throwable t) {
                                    t.printStackTrace();
                                }

                                try {

                                    JSONArray uuidList = json.getJSONArray("uuid_list");
                                    List<String> uuids = new ArrayList<String>();
                                    for (int i = 0; i < uuidList.length(); i++) {
                                        String uid = uuidList.getString(i);
                                        if (uid != null) {
                                            uid = fixEscaping(uid);
                                            uuids.add(uid);
                                        }
                                    }

                                    if (uuids.size() > 0) {
                                        PropertyHolder.getInstance().setUuidScan(true);
                                        PropertyHolder.getInstance().setUuidList(uuids);
                                    } else {
                                        PropertyHolder.getInstance().setUuidScan(false);
                                    }
                                } catch (Throwable t) {
                                    t.printStackTrace();
                                }

                                projectId = projectid;
                                //response = SDK_ENABLED;
                                apikeyResponse = res;
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    //response = ERROR_TYPE_CONNECTION;
                }
            }

        } catch (Throwable e) {
            e.printStackTrace();
            //response = ERROR_TYPE_CONNECTION;
        }

        return projectId;
    }

    private boolean syncDownloadProjectResources(String projectId, String preDefindedApikey) {

        boolean res = false;
        try {

//				PoisContainer poisContainer = new PoisContainer();
            Log.getInstance().debug(TAG, "Enter, syncDownloadProjectResources()");
            checkDeveloperMode();
            checkStorageLoaction();
            PropertyHolder.getInstance().setProjectId(projectId);
            PropertyHolder.getInstance().setProjectDir(projectId);
            PropertyHolder.getInstance().loadGlobals();

            //ProjectIconsManager.getInstance().downloadIconsRes();

            boolean campusesRes = ProjectConf.getInstance().downloadCampusesJsonRes();
            if (campusesRes) {
                ProjectConf.getInstance().loadCampuses();
                Map<String, Campus> campuses = ProjectConf.getInstance().getCampusesMap();
                Set<String> ids = campuses.keySet();

                String campusesIds[] = new String[ids.size()];
                campusesIds = ids.toArray(campusesIds);

                for (int i = 0; i < campusesIds.length; i++) {
                    CampusLevelResDownloader.releaseCinstance();
                    System.out.println("ConfigsUpdater::DownloadCampuse == > " + campusesIds[i]);
                    Campus campus = downloadCampus(campusesIds[i]);
                    if (campus != null) {
                        campus.loadFacilities();
                        Map<String, FacilityConf> facilitiesmap = campus.getFacilitiesConfMap();

                        if (facilitiesmap != null && facilitiesmap.size() > 0) {

                            for (String facilityId : facilitiesmap.keySet()) {
                                System.out.println("ConfigsUpdater::DownloadFacility == > " + facilityId);
                                downloadFacility(campusesIds[i], facilityId);


//									String uri = ServerConnection.getBaseUrlOfFacilityResList(facilityId, campusesIds[i]);
//									byte[] conf = ResourceDownloader.getInstance().getLocalCopy(uri);
//									String resjson = new String(conf);
//		
//									FacilityConf facConf = new FacilityConf(facilityId);
//									facConf.ParseFloors(resjson);
//									PoiDataHelper poiHelper = new PoiDataHelper();
//									List<IPoi> list =  poiHelper.getAllFacilityPois(facConf);
//									
//									poisContainer.addPois(campusesIds[i], facilityId, list);
                            }
                        }
                    }
                }

                //ConfigsLoader.getInstance().loadAllCampusesPoisList();
                //ProjectConf.getInstance().setPoisContainer(poisContainer);
                //loadAllPoisList();

                //cleanState();


                res = true;

                String downlaoderProjectid = getProjectIdFromServer(preDefindedApikey);
                if (downlaoderProjectid != null) {
                    saveApiKeyResponse();
                }


            }
        } catch (Throwable t) {
            t.printStackTrace();
        } finally {
            try {
                checkDeveloperMode();
                checkStorageLoaction();
                PropertyHolder.getInstance().setProjectId(projectId);
                PropertyHolder.getInstance().setProjectDir(projectId);
                PropertyHolder.getInstance().loadGlobals();

                ProjectIconsManager.getInstance().downloadIconsRes();

                //ConfigsLoader.getInstance().loadAllCampusesPoisList();
                loadFacilitiesData();
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }

        Log.getInstance().debug(TAG, "Exit, syncDownloadProjectResources()");

        return res;
    }


//	private void cleanState() {
//		
//		PoiDataHelper.releaseInstance();
//		
//		ConfigsLoader.getInstance().clearLoadedFacility();
//		ConfigsLoader.releaseInstance();
//
//	}

    private Campus downloadCampus(String campusId) {

        Log.getInstance().info("com.mlins.downloading.DownloadAllCampusesTask", "downloadCampus Enter");

        if (campusId == null) {
            return null;
        }

        CampusLevelResDownloader.releaseCinstance();

        String jsonTxt = null;

        PropertyHolder.getInstance().setCampusId(campusId);

        jsonTxt = ServerConnection.getInstance().getResources(campusId);

        // String cid = PropertyHolder.getInstance().getCampusId();
        Campus campus = ProjectConf.getInstance().getCampus(campusId);
        campus.Parse(jsonTxt);

        try {
            campus.downloadFacilitiesJsonRes();
        } catch (Exception e) {
            Log.getInstance().error("com.mlins.downloading.DownloadAllCampusesTask", e.getMessage(), e);
            e.printStackTrace();
        }

        // try {
        // campus.loadFacilities();
        // facilitiesmap = campus.getFacilitiesConfMap();
        // downloadBssidFiles(facilitiesmap);
        // } catch (Exception e) {
        // Log.getInstance().error("com.mlins.downloading.DownloadAllCampusesTask",
        // e.getMessage(), e);
        // e.printStackTrace();
        // }

        try {
            campus.downloadRes();
        } catch (Exception e) {
            Log.getInstance().error("com.mlins.downloading.DownloadAllCampusesTask", e.getMessage(), e);
            e.printStackTrace();
        }

        Log.getInstance().info("com.mlins.downloading.DownloadAllCampusesTask", "downloadCampus Exit");

        return campus;
    }


    private void downloadFacility(String campusId, String facilityId) {
        Log.getInstance().info("com.mlins.downloading.downloadFacility", "downloadFacility Enter");
        String jsonTxt = null;
        PoiDataHelper.releaseInstance();


        PropertyHolder.getInstance().setFacilityID(facilityId);

        FacilityConf facConf = new FacilityConf(facilityId);
        FacilityContainer.getInstance().setSelected(facConf);

        jsonTxt = ServerConnection.getInstance().getResources(facilityId, campusId);

        facConf.Parse(jsonTxt);

        if (!facConf.isUpToDate()) {
            downloadFacilityRes();
        }

        Log.getInstance().info("com.mlins.downloading.downloadFacility", "downloadFacility Exit");
    }


    private void downloadFacilityRes() {
        Log.getInstance().info("com.mlins.downloading.downloadFacilityRes", "downloadFacilityFinished Enter");
        List<FloorData> mapsUrls = FacilityContainer.getInstance().getSelected().getFloorDataList();
        List<PoiType> poiUrls = PoiDataHelper.getInstance().getTypes();
        List<String> allUrls = new ArrayList<String>();
        boolean downloadMatrixes = PropertyHolder.getInstance().isDownloadMatrixes();
        for (FloorData floor : mapsUrls) {
            allUrls.add(floor.mapuri);
            allUrls.add(floor.thumburi);
            allUrls.add(floor.gis);
            if (floor.poi != null && floor.poi.length() > 0) {
                allUrls.add(floor.poi);
            }
            if (floor.poiar != null && floor.poiar.length() > 0) {
                allUrls.add(floor.poiar);
            }
            if (floor.poien != null && floor.poien.length() > 0) {
                allUrls.add(floor.poien);
            }
            if (floor.poihe != null && floor.poihe.length() > 0) {
                allUrls.add(floor.poihe);
            }
            if (floor.poiru != null && floor.poiru.length() > 0) {
                allUrls.add(floor.poiru);
            }

            if (downloadMatrixes && floor.matrix != null && floor.matrix.length() > 0) {
                allUrls.add(floor.matrix);
            }

        }

        allUrls.addAll(PoiDataHelper.getInstance().getAllPoisIcons());

        for (PoiType poi : poiUrls) {
            if (!allUrls.contains(poi.getPoiuri()))
                allUrls.add(poi.getPoiuri());
        }

        FacilityConf facConf = FacilityContainer.getInstance().getSelected();
        allUrls.add(facConf.getParametersConfFileName());
        allUrls.add(facConf.getBssidsFileName());
        allUrls.add(facConf.getCostumereventsconfFileName());
        allUrls.add(facConf.getBannersconfFileName());
        allUrls.add(facConf.getSwitchconfFileName());
        allUrls.add("android/" + facConf.getGroupsConfFileName());
        allUrls.add(facConf.getPoisGalleryconfFileName());

        if (downloadMatrixes) {
            allUrls.add(facConf.getFloorSelectionFileName());
        }

        String geofence = facConf.getGeogfenceConfFileName();
        if (geofence != null) {
            allUrls.add(geofence);
        }

        if (facConf.getBeaconsPlacementConfFileName() != null) {
            allUrls.add(facConf.getBeaconsPlacementConfFileName());
        }

        if (facConf.getLabelsConfFileName() != null) {
            allUrls.add(facConf.getLabelsConfFileName());
        }

        /**
         * List<String> facilitylabelesFilesNamesList =
         * FacilityConf.getInstance().getFacilityLabeles(); if
         * (facilitylabelesFilesNamesList != null &&
         * facilitylabelesFilesNamesList.size() > 0) {
         *
         * for (String lablName : facilitylabelesFilesNamesList) {
         * allUrls.add(lablName); } }
         */
        int numberOfUrls = allUrls.size();
        String[] murls = new String[numberOfUrls];
        for (int i = 0; i < numberOfUrls; i++) {
            murls[i] = allUrls.get(i);
        }

        ResourceDownloader.getInstance().onDemandDownload(murls);
        saveFacilityResLocalCopy();

        Log.getInstance().info("com.mlins.downloading.downloadFacilityRes", "downloadFacilityFinished Exit");
    }

    private void saveFacilityResLocalCopy() {
        Log.getInstance().info("com.mlins.downloading.saveFacilityResLocalCopy", "downloadFinished Enter");
        boolean downloadMatrixes = PropertyHolder.getInstance().isDownloadMatrixes();
        FacilityConf facConf = FacilityContainer.getInstance().getSelected();
        if (downloadMatrixes) {
            facConf.saveMatrixes();
            facConf.saveFloorSelectionMatrix();
        } else {
            MatrixDataHelper.getInstance().downloadFloorMatrix(true);
            List<FloorData> floors = facConf.getFloorDataList();
            // int counter = 0;
            // for (FloorData o : floors) {
            for (int i = 0; i < floors.size(); i++) {
                MatrixDataHelper.getInstance().downloadMatrix(i, true);
                // counter++;
            }
        }


        facConf.saveParametersConf();

        facConf.saveEvents();
        facConf.saveBanners();
        facConf.saveSwitch();
        facConf.saveGeofence();
        facConf.saveGroupsConf();
        facConf.saveGalleryConf();
        /**
         * FacilityConf.getInstance().saveFacilityLabeles();
         */
        facConf.saveBssids();

        facConf.saveBeaconsPlacementConf();
        facConf.saveLabelsConf();

        PoiDataHelper.getInstance().saveIcons();

        Log.getInstance().info("com.mlins.downloading.saveFacilityResLocalCopy", "downloadFinished Exit");
    }


    private byte[] getUrlRes(String uri, int retry) {

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        InputStream in = null;

        try {
            URL url = new URL(uri);


            int timeoutConnection = 20000;

            int timeoutSocket = 20000;

            URLConnection conn = url.openConnection();
            conn.setConnectTimeout(timeoutConnection);
            conn.setReadTimeout(timeoutSocket);
            in = conn.getInputStream();


            byte[] buffer = new byte[4096];
            int n = -1;

            while ((n = in.read(buffer)) != -1) {
                if (n > 0) {
                    out.write(buffer, 0, n);
                }
            }


        } catch (Throwable e) {
            //e.printStackTrace();

            if (retry == 0) {
                return new byte[0];
            }
            //System.out.println("retry ==>"+retry);
            return getUrlRes(uri, retry - 1);
        } finally {
            try {
                if (in != null) in.close();
                if (out != null) out.close();
            } catch (Throwable ignoreMe) {
            }
        }
        return out.toByteArray();

    }

    private boolean downloadProjectZip(String pid, String fileName, String version, int tries) {

        if (PropertyHolder.getInstance().isForceFullZip()) {
            fileName = "ios_spreo_compressed_res";
            if (PropertyHolder.getInstance().isUseZipWithoutMaps()) {
                fileName = "ios_spreo_compressed_res_no_map";
            }
            version = "";
            tries = 2;
        }

        //long time = System.currentTimeMillis();
        boolean isZipOk = false;
        if (pid == null) {
            return isZipOk;
        }

        InputStream in = null;
        ZipInputStream zin = null;

        try {
            String uri = pid + version + "/" + fileName + ".zip";
            //System.out.println("zip started");
            String url = PropertyHolder.getInstance().getServerName() + "res/" + uri;
            byte[] bytes = getUrlRes(url, tries);

            //System.out.println("zip download time " + (System.currentTimeMillis()- time)/ 1000.0 + " sec");
            //time = System.currentTimeMillis();
            if (bytes.length != 0) {

                File root = new File(PropertyHolder.getInstance().getExternalStoragedir());
                File f = new File(root, fileName + ".zip");

                if (f.exists()) {
                    f.delete();
                }

                writeZipLocalCopy(f, bytes);
                //System.err.println("zip save time " + (System.currentTimeMillis()- time)/ 1000.0);
                //time = System.currentTimeMillis();

                bytes = null;
                in = new FileInputStream(f);
                zin = new ZipInputStream(in);
                DataZipDecompresser dec = new DataZipDecompresser(zin, PropertyHolder.getInstance().getExternalStoragedir() + "spreo/", fileName);
                //time = System.currentTimeMillis();
                if (dec.unzip()) {
                    //System.out.println("downloadZipResource unzip project data ==> ok");
                    //System.err.println("zip unzip time " + (System.currentTimeMillis()- time)/ 1000.0);

                    isZipOk = true;
                }

            }

            //System.out.println("zip download take " + (System.currentTimeMillis()- time)/ 1000.0 + " sec");


        } catch (Throwable e) {
            e.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (zin != null) {
                try {
                    zin.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        //System.err.println("zip duration time " + (System.currentTimeMillis()- time)/ 60000.0);
        return isZipOk;
    }


    private int unzipProjectResFromLocalStorage(String zipFileName) {

        int isZipOk = 0;
        InputStream in = null;
        ZipInputStream zin = null;

        try {
            int rid = ResourceTranslator.getInstance().getTranslatedResourceId("raw", zipFileName);
            in = context.getResources().openRawResource(rid);
            zin = new ZipInputStream(in);
            DataZipDecompresser dec = new DataZipDecompresser(zin, PropertyHolder.getInstance().getExternalStoragedir() + "spreo/", zipFileName);
            if (dec.unzip()) {
                //System.out.println("unzipProjectResFromLocalStorage unzip project data ==> ok");
                isZipOk = 1;
            } else {
                isZipOk = 0;
            }

        } catch (Throwable e) {
            //e.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (zin != null) {
                try {
                    zin.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return isZipOk;

    }

    public boolean isMapReady() {
        return mapReady;
    }


    // TASKS

    /**
     private class ApiKeyValidatorTask extends AsyncTask<String, Void, String> {

     private int response = NO_RESPONSE;
     private String projectId = null;

     @Override protected String doInBackground(String... params) {

     Log.getInstance().info("com.mlins.downloading.ApiKeyValidatorTask", "doInBackground Enter");

     String apikey = params[0];
     String servername = PropertyHolder.getInstance().getServerName();
     String url = servername + "apikey?req=0&apik=" + apikey;
     try {
     byte[] bytes = ServerConnection.getInstance().getResourceBytes(url);
     if (bytes.length == 0) {
     response = ERROR_TYPE_CONNECTION;
     } else {
     String res = new String(bytes);
     parse(res);
     }
     } catch (Exception e) {
     Log.getInstance().error("com.mlins.downloading.ApiKeyValidatorTask", e.getMessage(), e);
     e.printStackTrace();
     response = ERROR_TYPE_CONNECTION;
     }
     Log.getInstance().info("com.mlins.downloading.ApiKeyValidatorTask", "doInBackground Exit");
     return "";
     }


     private void parse(String res) {

     Log.getInstance().info("com.mlins.downloading.ApiKeyValidatorTask", "parse Enter");

     try {
     JSONTokener tokener = new JSONTokener(res);
     JSONObject json = (JSONObject) tokener.nextValue();
     String status = json.getString("status");
     if (status.equals("fail")) {
     response = ERROR_TYPE_INVALID_APIKEY;
     } else {
     String projectid = json.getString("pid");

     // get scan type and uuids
     try{
     String uuidType = json.getString("ble_scan_type");
     PropertyHolder.getInstance().setUuidScanType(uuidType);
     }catch(Throwable t){
     t.printStackTrace();
     }

     try{

     JSONArray uuidList= json.getJSONArray("uuid_list");
     List<String> uuids = new ArrayList<String>();
     for(int i=0; i< uuidList.length();i++){
     String uid= uuidList.getString(i);
     if(uid!=null){
     uid = fixEscaping(uid);
     uuids.add(uid);
     }
     }

     if(uuids.size()>0){
     PropertyHolder.getInstance().setUuidScan(true);
     PropertyHolder.getInstance().setUuidList(uuids);
     }
     else{
     PropertyHolder.getInstance().setUuidScan(false);
     }
     }
     catch(Throwable t){
     t.printStackTrace();
     }
     // PropertyHolder.getInstance().setProjectId(projectid);
     projectId = projectid;
     response = SDK_ENABLED;
     apikeyResponse = res;
     }

     } catch (JSONException e) {
     Log.getInstance().error("com.mlins.downloading.ApiKeyValidatorTask", e.getMessage(), e);
     e.printStackTrace();

     } catch (Exception e) {
     Log.getInstance().error("com.mlins.downloading.ApiKeyValidatorTask", e.getMessage(), e);
     e.printStackTrace();
     }
     Log.getInstance().info("com.mlins.downloading.ApiKeyValidatorTask", "parse Exit");
     }

     @Override protected void onPostExecute(String result) {

     super.onPostExecute(result);
     Log.getInstance().info("com.mlins.downloading.ApiKeyValidatorTask", "onPostExecute Enter");
     // if (mListener != null) {
     apiKeyCheckResult(response, projectId);
     // }
     Log.getInstance().info("com.mlins.downloading.ApiKeyValidatorTask", "onPostExecute Exit");
     }
     }

     */
    /**
     // download campuses json
     private class DownloadCampusesFileTask extends AsyncTask<String[], Void, Boolean> {

    @Override protected void onPreExecute() {
    Log.getInstance().info("com.mlins.downloading.DownloadCampusesFileTask", "onPreExecute Enter");

    Log.getInstance().info("com.mlins.downloading.DownloadCampusesFileTask", "onPreExecute Exit");
    }

    @Override protected Boolean doInBackground(String[]... params) {
    if(useLocalRes){
    return true;
    }

    return ProjectConf.getInstance().downloadCampusesJsonRes();
    }

    @Override protected void onPostExecute(Boolean result) {
    super.onPostExecute(result);
    Log.getInstance().info("com.mlins.downloading.DownloadCampusesFileTask", "onPostExecute Enter");

    if (result) {
    onDownloadCampusesConfigFinished(ResUpdateStatus.CAMPUSES_JSON_OK);
    } else {
    onDownloadCampusesConfigFinished(ResUpdateStatus.CAMPUSES_JSON_FAIL);
    }

    Log.getInstance().info("com.mlins.downloading.DownloadCampusesFileTask", "onPostExecute Exit");

    }

    }
     */
/**
 // downlaod all campuses

 private class DownloadAllCampusesTask extends AsyncTask<String, Void, Integer> {

@Override protected void onPreExecute() {
Log.getInstance().info("com.mlins.downloading.DownloadAllCampusesTask", "onPreExecute Enter");

Log.getInstance().info("com.mlins.downloading.DownloadAllCampusesTask", "onPreExecute Exit");
}

@Override protected Integer doInBackground(String... campusesIds) {
Log.getInstance().info("com.mlins.downloading.DownloadAllCampusesTask", "doInBackground Enter");
for (int i = 0; i < campusesIds.length; i++) {
CampusLevelResDownloader.releaseCinstance();
System.out.println("ConfigsUpdater::DownloadCampuse == > " + campusesIds[i]);
Campus campus = downloadCampus(campusesIds[i]);
if (campus != null) {
campus.loadFacilities();
Map<String, FacilityConf> facilitiesmap = campus.getFacilitiesConfMap();

// campus.loadFacilities();
// facilitiesmap = campus.getFacilitiesConfMap();

if (facilitiesmap != null && facilitiesmap.size() > 0) {

for (String facilityId : facilitiesmap.keySet()) {
System.out.println("ConfigsUpdater::DownloadFacility == > " + facilityId);
downloadFacility(campusesIds[i], facilityId);
}
}
}

}

ConfigsLoader.getInstance().loadAllCampusesPoisList();

cleanState();

Log.getInstance().info("com.mlins.downloading.DownloadAllCampusesTask", "doInBackground Exit");
return 1;

}

@Override protected void onPostExecute(Integer result) {
super.onPostExecute(result);
Log.getInstance().info("com.mlins.downloading.DownloadCampusAndFacilityTask", "onPostExecute Enter");

OnDownloadAllCampusesFininshed(result);
Log.getInstance().info("com.mlins.downloading.DownloadCampusAndFacilityTask", "onPostExecute Exit");
}


}

 */

    /**
     private void onDownloadCampusesConfigFinished(ResUpdateStatus status) {
     if (status.equals(ResUpdateStatus.CAMPUSES_JSON_OK)) {

     ProjectConf.getInstance().loadCampuses();
     Map<String, Campus> campuses = ProjectConf.getInstance().getCampusesMap();
     Set<String> ids = campuses.keySet();

     String campusesIds[] = new String[ids.size()];
     campusesIds = ids.toArray(campusesIds);

     DownloadAllCampusesTask downloadAllCampusesTask = new DownloadAllCampusesTask();
     downloadAllCampusesTask.execute(campusesIds);

     } else {
     notifyListeners(ResUpdateStatus.CAMPUSES_JSON_FAIL);
     }

     }
     */

    /**
     private void OnDownloadAllCampusesFininshed(Integer result) {
     if (result == 1) {
     saveApiKeyResponse();
     notifyListeners(ResUpdateStatus.OK);
     } else {
     notifyListeners(ResUpdateStatus.FAILED);
     }

     }
     */

    /**
     private int unzipProjectRawData() {
     int resultCode = 0;
     try{
     File f = new File(PropertyHolder.getInstance().getAppDir(), APIKEY_RES_FILE_NAME);
     if(!f.exists()){
     Resources  res= PropertyHolder.getInstance().getMlinsContext().getResources();
     int resID = res.getIdentifier("mlins", "raw", PropertyHolder.getInstance().getMlinsContext().getPackageName());
     if(resID ==0){ // zip is not exists
     resultCode = 0;
     }
     else{
     InputStream in = PropertyHolder.getInstance().getMlinsContext().getResources().openRawResource(resID);
     ZipInputStream zin = new ZipInputStream(in);
     DataZipDecompresser dec = new DataZipDecompresser(zin, PropertyHolder.getInstance().getExternalStoragedir());
     if (dec.unzip()) {
     System.out.println("unzip project data ==> ok");
     resultCode = 1;
     }
     else{
     System.out.println("unzip project data ==> fail");
     resultCode = 0;
     }
     }
     }
     else{
     System.out.println("unzip project data ==> already done!");
     resultCode = 2;
     }

     }catch(Throwable t){
     t.printStackTrace();
     }

     return resultCode;

     }
     */

}
