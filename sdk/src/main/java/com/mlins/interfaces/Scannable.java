package com.mlins.interfaces;

import com.mlins.wireless.IResultReceiver;
import com.mlins.wireless.WlBlip;

import java.util.List;

public interface Scannable {

    List<WlBlip> getBlipsList();

    void subscribeForResults(IResultReceiver receiver);

    boolean unsubscibeForResults(IResultReceiver receiver);

    void startScanning();

    void stopScanning();
}
