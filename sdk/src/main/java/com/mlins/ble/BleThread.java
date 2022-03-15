package com.mlins.ble;


import static android.content.ContentValues.TAG;

import android.os.Build;
import android.util.Log;

public class BleThread extends Thread {

    public static final int Stop = 0;
    public static final int Play = 1;
    public static final int Pause = 2;
    public boolean mRunning = true;
    int state;
    private boolean mStarted;
    private int counter = 0;

    private final BleScanner scannerInstance;

    public BleThread(BleScanner scanner) {
        super("BleThread");
        this.scannerInstance = scanner;
    }

    @Override
    public void run() {
        while (mRunning) {
            long delay = 1000;

            try {
                Thread.sleep(delay);
                counter++;
                if (counter > 1) {
                    if(Build.VERSION.SDK_INT < Build.VERSION_CODES.N || counter % 15 == 0) {
                        scannerInstance.stopSingleScan();
                    }
                    scannerInstance.deliverResults();
                }
                if (mRunning) {
                    if(counter == 1 || Build.VERSION.SDK_INT < Build.VERSION_CODES.N || counter % 15 == 0) {
                        scannerInstance.startSingleScan();

                    }
                }

            } catch (Throwable e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }


        }
        mStarted = false;
    }

    public boolean isRunning() {
        return mRunning;
    }

    public void setRunning(boolean mRunning) {
        this.mRunning = mRunning;
    }

    @Override
    public synchronized void start() {
        mStarted = true;
        mRunning = true;
        super.start();
    }

    public boolean getIsStarted() {
        return mStarted;
    }

    public void stopThread() {
        state = Stop;
        mRunning = false;
    }

}
