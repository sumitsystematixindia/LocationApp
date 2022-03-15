package com.mlins.wireless;

import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;

import com.mlins.wireless.WlScanner.ResultsReceiver;

import java.util.List;

public interface IScanner {

    /**
     * Add an hidden SSID {@link WifiConfiguration} to the set of configured networks
     * so it shall appear in scan results.
     * <p>
     * This operation may take a while, therefore if requesting a single scan make it delayed,
     * typically 500 milliseconds should suffice <{@code scan(0,0,500);}.
     *
     * @param ssid  string containing the service set identification, a.k.a "network name".
     * @param bssid string containing the basic service set identification. May be null or "".
     * @return the integer that identifies the network configuration to the system.
     * @see WifiConfiguration#hiddenSSID
     * @see WifiConfiguration#networkId
     */
    public abstract int addHidden(String ssid, String bssid);

    /**
     * Return a list of all the configured beacons.
     *
     * @return a list of network configurations known to the wpa_supplicant.
     * May be null if WiFi is turned off or other failure.
     * @see WifiManager#getConfiguredNetworks()
     */
    public abstract List<WifiConfiguration> getConfiguredBeacons();

    /**
     * Return the results of the latest scan.
     *
     * @return a list of {@link WlBlip} from the most recent scan.
     */
    public abstract List<WlBlip> getLastResults();

    /**
     * Start scanning {@code count} times, every {@code interval} seconds,
     * starting in {@code delay} milliseconds.
     * <p>
     * Returns immediately. The availability of the results is made known later
     * by means of an asynchronous event sent on completion of the scan.
     *
     * @param count    integer number of scans to perform or {@link #SCAN_INDEFINITE}
     * @param interval integer seconds between scans or {@link #SCAN_CONTINUOUS} to scan as quick as possible.
     * @param delay    integer milliseconds before first scan.
     */
    public void scan(int count, int interval, int delay);

    /**
     * Stop scanning when next results available.
     */
    public void stop();

    /**
     * Pause scanning but keep count and interval.
     */
    public void pause();

    /**
     * Resume a paused scan sequence if not yet finished or stopped.
     *
     * @return true if resumed, false if done.
     */
    public boolean resume();

    /**
     * Subscribe a {@link ResultsReceiver} to accept delivery of scan results
     * whenever available.
     *
     * @param receiver the ResultsReciever to handle asynchronous delivery.
     */
    void subscribeForResults(IResultReceiver receiver);

    /**
     * Stop delivery of scan results to a subscribed {@link ResultsReceiver}.
     *
     * @param receiver the ResultsReceiver to stop receiving results.
     * @return true if receiver was removed by this operation, false otherwise.
     */
    public boolean unsubscibeForResults(IResultReceiver receiver);


    public List<WlBlip> getCurrentResult();


}