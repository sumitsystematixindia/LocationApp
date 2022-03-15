package com.mlins.wireless;

import java.util.List;


public interface Scannable {

    List<WlBlip> getBlipsList();

    void subscribeForResults(IResultReceiver receiver);

    boolean unsubscibeForResults(IResultReceiver receiver);

    void startScanning();

    void stopScanning();
}
