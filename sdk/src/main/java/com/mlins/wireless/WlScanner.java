package com.mlins.wireless;

import android.content.Context;
import android.net.wifi.WifiConfiguration;

import com.mlins.utils.Cleanable;
import com.mlins.utils.Lookup;
import com.mlins.utils.PropertyHolder;
import com.mlins.wireless.filerecorder.FileRecorder;

import java.io.File;
import java.util.List;

public class WlScanner implements IScanner, Scannable, Cleanable {

    /**
     * Perform scans 'till further notice.
     */
    public static final int SCAN_INDEFINITE = -1;
    /**
     * Perform scans as often as device allows
     */
    public static final int SCAN_CONTINUOUS = 0;

    private Context mContext;
    private WlScanner() {
        mContext = PropertyHolder.getInstance().getMlinsContext();
    }

    public static WlScanner getInstance() {
        return Lookup.getInstance().get(WlScanner.class);
    }

    public static void releaseInstance() {
        Lookup.getInstance().remove(WlScanner.class);
    }

    public void clean(){
        restoreUserConfig();
    }

    private void restoreUserConfig() {
        WlScannerImpl.getInstance(mContext).RestoreConfiguration();
    }

    /* (non-Javadoc)
     * @see com.mlins.wireless.IScanner#addHidden(java.lang.String, java.lang.String)
     */
    @Override
    public int addHidden(String ssid, String bssid) {
//		if (TextUtils.isEmpty(ssid)) return -1;

        if (PropertyHolder.getInstance().isPlayingState()) {
            return FileScannerImpl.getInstance(mContext).addHidden(ssid, bssid);
        } else {
            return WlScannerImpl.getInstance(mContext).addHidden(ssid, bssid);
        }
    }

    /* (non-Javadoc)
     * @see com.mlins.wireless.IScanner#getConfigueredBeacons()
	 */
    @Override
    public List<WifiConfiguration> getConfiguredBeacons() {
        if (PropertyHolder.getInstance().isPlayingState()) {
            return FileScannerImpl.getInstance(mContext).getConfiguredBeacons();
        } else {
            return WlScannerImpl.getInstance(mContext).getConfiguredBeacons();
        }
    }

    /* (non-Javadoc)
	 * @see com.mlins.wireless.IScanner#getLastResults()
	 */
    @Override
    public List<WlBlip> getLastResults() {
        if (PropertyHolder.getInstance().isPlayingState()) {
            return FileScannerImpl.getInstance(mContext).getLastResults();
        } else {
            return WlScannerImpl.getInstance(mContext).getLastResults();
        }
    }

    /**
     * Start scanning {@code count} times, every {@code interval} seconds.
     * Same as {@code scan(count, interval, 0)}
     *
     * @param count    number of scans to perform or {@link #SCAN_INDEFINITE}
     * @param interval seconds between scans or {@link #SCAN_CONTINUOUS} to scan as quick as possible.
     */
    public void scan(int count, int interval) {
        scan(count, interval, 0);
    }

    /* (non-Javadoc)
     * @see com.mlins.wireless.IScanner#scan(int, int, int)
     */
    @Override
    public void scan(int count, int interval, int delay) {
        if (PropertyHolder.getInstance().isPlayingState()) {
            FileScannerImpl.getInstance(mContext).scan(count, interval, delay);
        } else {
            WlScannerImpl.getInstance(mContext).scan(count, interval, delay);
        }
    }

    /* (non-Javadoc)
     * @see com.mlins.wireless.IScanner#stop()
     */
    @Override
    public void stop() {
        if (PropertyHolder.getInstance().isPlayingState()) {
            FileScannerImpl.getInstance(mContext).stop();
        } else {
            WlScannerImpl.getInstance(mContext).stop();
        }
    }

    /* (non-Javadoc)
     * @see com.mlins.wireless.IScanner#pause()
     */
    @Override
    public void pause() {
        if (PropertyHolder.getInstance().isPlayingState()) {
            FileScannerImpl.getInstance(mContext).pause();
        } else {
            WlScannerImpl.getInstance(mContext).pause();
        }
    }

    /* (non-Javadoc)
     * @see com.mlins.wireless.IScanner#resume()
     */
    @Override
    public boolean resume() {
        if (PropertyHolder.getInstance().isPlayingState()) {
            return FileScannerImpl.getInstance(mContext).resume();
        } else {
            return WlScannerImpl.getInstance(mContext).resume();
        }
    }

    /* (non-Javadoc)
     * @see com.mlins.wireless.IScanner#subscribeForResults(com.mlins.wireless.WlScanner.ResultsReceiver)
     */
    @Override
    public void subscribeForResults(IResultReceiver receiver) {
        if (PropertyHolder.getInstance().isPlayingState()) {
            FileScannerImpl.getInstance(mContext).subscribeForResults(receiver);
        } else {
            WlScannerImpl.getInstance(mContext).subscribeForResults(receiver);
        }
    }

    /* (non-Javadoc)
     * @see com.mlins.wireless.IScanner#unsubscibeForResults(com.mlins.wireless.WlScanner.ResultsReceiver)
     */
    @Override
    public boolean unsubscibeForResults(IResultReceiver receiver) {
        if (PropertyHolder.getInstance().isPlayingState()) {
            return FileScannerImpl.getInstance(mContext).unsubscibeForResults(receiver);
        } else {
            return WlScannerImpl.getInstance(mContext).unsubscibeForResults(receiver);
        }
    }

    public void startRecording() {
        FileRecorder recorder = FileRecorder.getInstance();
        if (!PropertyHolder.getInstance().isPlayingState()) {
            WlScannerImpl.getInstance(mContext).subscribeForResults(recorder);
        }
        recorder.Start();
    }

    public void stopRecording() {
        FileRecorder recorder = FileRecorder.getInstance();
        recorder.stop();
        if (!PropertyHolder.getInstance().isPlayingState()) {
            WlScannerImpl.getInstance(mContext).unsubscibeForResults(recorder);
        }
    }

    public void addHidden(File ssids) { // Tab separated values.
//		if (! ssids.isFile())
//			return;
//		List<String> ids = new ArrayList<String>();
//		BufferedReader r = null;
//			try {
//				r = new BufferedReader(new FileReader(ssids));
//				String line;
//				while ((line = r.readLine()) != null) {
//					String[] names = line.split("\t");
//					ids.addAll(Arrays.asList(names));
//				}
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			} finally {
//				if (r != null) {
//					try {
//						r.close();
//					} catch (IOException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//				}
//			}
//
//		List<WifiConfiguration> cb = getConfiguredBeacons();
//		for (WifiConfiguration b : cb) {
//			if (b.SSID != null){
//				ids.remove(b.BSSID);
//			}
//		}
//		for (String ssid : ids) {
//			addHidden(ssid, null);
//		}
    }

    @Override
    public List<WlBlip> getCurrentResult() {
        return WlScannerImpl.getInstance(mContext).getCurrentResult();
    }

    @Override
    public List<WlBlip> getBlipsList() {

        return getLastResults();
    }

    @Override
    public void startScanning() {
        // TODO Auto-generated method stub

    }

    @Override
    public void stopScanning() {
        if (PropertyHolder.getInstance().isPlayingState()) {
            FileScannerImpl.getInstance(mContext).stop();
        } else {
            WlScannerImpl.getInstance(mContext).stop();
        }

    }

    public static interface ResultsReceiver {
        void onRecieve(List<WlBlip> results);
    }
}
