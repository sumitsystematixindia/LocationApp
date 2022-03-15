package com.mlins.wireless.filePlayer;

import com.mlins.wireless.IResultReceiver;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class PlayerThread extends Thread {

    public static final int Stop = 0;
    public static final int Play = 1;
    public static final int Pause = 2;
    public static final int Step = 3;
    public boolean mRunning = false;
    int state;
    List<RecordStep> data = new ArrayList<RecordStep>();
    List<IResultReceiver> listeners = new ArrayList<IResultReceiver>();
    private boolean mStarted;
    private int steps = 1;
    private int counter = 0;

    public PlayerThread() {
        super("PlayerThread");
    }

    public boolean isRunning() {
        return mRunning;
    }

    public void setRunning(boolean mRunning) {
        this.mRunning = mRunning;
    }

    @Override
    public void run() {
        while (mRunning) {
            long currenttime;
            long delay = 500;
            if (data.size() > 0) {
                switch (state) {
                    case Play:

                        RecordStep thestep = data.get(counter);
                        broadcastStep(thestep);
                        currenttime = thestep.getTimeStamp();

                        counter++;

                        if (counter >= data.size()) {
                            counter = 0;
                            delay = 500;
                        } else {
                            thestep = data.get(counter);
                            delay = thestep.getTimeStamp() - currenttime;
                        }

                        break;
                    case Step:

                        break;
                    case Pause:
                        delay = 500;
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

    private void broadcastStep(RecordStep thestep) {
        List<IResultReceiver> tmplist = new ArrayList<IResultReceiver>();
        tmplist.addAll(listeners);

        for (Iterator<IResultReceiver> iterator = tmplist.iterator(); iterator.hasNext(); ) {
            IResultReceiver iResultReciever = (IResultReceiver) iterator.next();
            iResultReciever.onRecieve(thestep.blips);

        }
    }

    public void addListener(IResultReceiver listener) {
        listeners.add(listener);
    }

    public boolean removeListener(IResultReceiver listener) {
        return listeners.remove(listener);
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

    public List<RecordStep> getData() {

        return data;

    }

    public void setSteps(int steps) {
        this.steps = steps;

    }

    public void reset() {
        this.steps = 1;
        counter = 0;

    }
}
