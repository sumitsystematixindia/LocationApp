package com.mlins.wireless;

import android.content.Context;
import android.net.wifi.WifiConfiguration;

import com.mlins.utils.Lookup;
import com.mlins.wireless.filePlayer.FilePlayer;

import java.util.ArrayList;
import java.util.List;

/* package */ class FileScannerImpl implements IScanner {

    private List<WlBlip> mLastScan = new ArrayList<WlBlip>();

    public static IScanner getInstance(Context mContext) {
        return Lookup.getInstance().get(FileScannerImpl.class);
    }

    @Override
    public int addHidden(String ssid, String bssid) {
        // Auto-generated method stub
        return 0;
    }

    @Override
    public List<WifiConfiguration> getConfiguredBeacons() {
        // Auto-generated method stub
        return null;
    }

    @Override
    public List<WlBlip> getLastResults() {
        return mLastScan;
    }

    @Override
    public void scan(int count, int interval, int delay) {
//		if (count == WlScanner.SCAN_INDEFINITE) {
        FilePlayer.getInstance().play();
//		} else {
//			FilePlayer.getInstance().step(count);
//		}
    }

    @Override
    public void stop() {
        FilePlayer.getInstance().stopPlaying();
    }

    @Override
    public void pause() {
        FilePlayer.getInstance().pause();
    }

    @Override
    public boolean resume() {
        FilePlayer.getInstance().play();
        return true;
    }

    @Override
    public void subscribeForResults(IResultReceiver receiver) {
        FilePlayer.getInstance().addListener(receiver);
    }

    @Override
    public boolean unsubscibeForResults(IResultReceiver receiver) {
        FilePlayer.getInstance().removeListener(receiver);
        return false;
    }

    @Override
    public List<WlBlip> getCurrentResult() {
        return new ArrayList<WlBlip>();
    }


}
