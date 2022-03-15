package com.mlins.locator;

import android.os.AsyncTask;

import com.mlins.ndk.wrappers.NdkLocationFinder;
import com.mlins.utils.FacilityConf;
import com.mlins.utils.FacilityContainer;
import com.mlins.utils.PropertyHolder;

import java.util.ArrayList;

class LoadMatrixTask extends AsyncTask<Void, Void, Boolean> {

    private Exception exception;

    LoadMatrixTask() {
        super();
    }

    protected Boolean doInBackground(Void... noparams) {
        try {
            //XXX NDK
            //AsociativeMemoryLocator.getInstance().load();
            FacilityConf facConf = FacilityContainer.getInstance().getCurrent();
            if (facConf == null) {
                return false;
            }

            int floor = facConf.getSelectedFloor(); //FacilityConf.getInstance().getSelectedFloor();
            String facility = facConf.getId();
            String campus = PropertyHolder.getInstance().getCampusId();
            String project = PropertyHolder.getInstance().getProjectId();
            boolean isBin = PropertyHolder.getInstance().isTypeBin();
            String appDirPath = PropertyHolder.getInstance().getAppDir().getAbsolutePath();
            String scanType = PropertyHolder.getInstance().getMatrixFilePrefix();
            if (PropertyHolder.useZip) {
                appDirPath = PropertyHolder.getInstance().getZipAppdir().getAbsolutePath();
                scanType = "";
            }
            int locationCloseRange = facConf.getNdkCloseRange();
            int k = PropertyHolder.getInstance().getK();
            float pixelsToMeter = facConf.getPixelsToMeter();
            int floorcount = facConf.getFloorDataList().size(); //FacilityConf.getInstance().getFloorDataList().size();
            int averageRange = PropertyHolder.getInstance().getAverageRange();

            ArrayList<String> filter = PropertyHolder.getInstance().getSsidFilter();
            String[] ssidfilter = filter.toArray(new String[filter.size()]);

            float closeDevicesThreshold = PropertyHolder.getInstance().getCloseDeviceThreshold();
            float closeDeviceWeight = PropertyHolder.getInstance().getCloseDeviceWeight();
            int kTopLevelThr = facConf.getTopKlevelsThr();

            NdkLocationFinder.getInstance().initParams(appDirPath, locationCloseRange, k, pixelsToMeter, averageRange, ssidfilter, floorcount, scanType,
                    closeDevicesThreshold, closeDeviceWeight, kTopLevelThr);
            String path = project + "/" + campus + "/" + facility;
            if (PropertyHolder.useZip) {
                path = project + "/" + campus + "/facilities/" + facility + "/floors";
            }
            NdkLocationFinder.getInstance().load(path, floor, isBin);

        } catch (Exception e) {
            this.exception = e;
            e.printStackTrace();
            return false;
        }
        return true;
    }

    protected void onPostExecute(boolean isStoped) {
        // TODO: check this.exception 
        // TODO: do something with here
    }
}
