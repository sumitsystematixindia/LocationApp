package com.mlins.gisfileplayer;

import com.mlins.utils.gis.Location;

import java.util.ArrayList;
import java.util.List;


public class GisPlayerThread extends Thread {

    public static final int Stop = 0;
    public static final int Play = 1;
    public static final int Pause = 2;
    public boolean mRunning = false;
    int state;
    List<Location> data = new ArrayList<Location>();
    private boolean mStarted;
    private int steps = 1;
    private int counter = 0;
    private Location thestep = new Location();

    public GisPlayerThread() {
        super("GisPlayerThread");
    }

    @Override
    public void run() {
        while (mRunning) {
            long delay = 1000;
            if (data.size() > 0) {
                switch (state) {
                    case Play:

                        if (counter >= data.size()) {
                            counter = 0;

                        }
                        thestep = data.get(counter);
                        counter++;
                        break;
                    case Stop:
                        state = Pause;
                        counter = 0;
                    default:
                        break;
                }
            }


            try {
                Thread.sleep(delay);
            } catch (InterruptedException e) {
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

    public void clearData() {
        data.clear();

    }

    public List<Location> getData() {

        return data;

    }

    public void setSteps(int steps) {
        this.steps = steps;

    }

    public void resetPlayData() {
        this.steps = 1;
        counter = 0;
        state = Stop;

    }

    public void setPlayingData(List<Location> data2) {
        data.clear();
        data.addAll(data2);

    }

    public Location getCurrentPoint() {
        return thestep;
    }

}
