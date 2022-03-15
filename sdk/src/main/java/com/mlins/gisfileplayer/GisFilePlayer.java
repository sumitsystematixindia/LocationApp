package com.mlins.gisfileplayer;

import android.util.Log;

import com.mlins.utils.Cleanable;
import com.mlins.utils.Lookup;
import com.mlins.utils.gis.Location;
import com.mlins.wireless.filePlayer.PlayerThread;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GisFilePlayer implements Cleanable{

    GisPlayerThread pThread;
    int mCounter = 0;
    List<Location> data = new ArrayList<Location>();

    public static GisFilePlayer getInstance() {
        return Lookup.getInstance().get(GisFilePlayer.class);
    }

    public void clean(){
        if (pThread != null) {
            pThread.mRunning = false;
        }
        stopPlaying();
    }

    public static void releaseInstance() {
        Lookup.getInstance().remove(GisFilePlayer.class);
    }

    @SuppressWarnings("deprecation")
    public void load(String filename) {
        if (pThread != null) {
            pThread.setRunning(false);
        }

        initThread();
        BufferedReader in = null;
        data.clear();
        try {
            in = new BufferedReader(new FileReader(filename));
            String line = null;
            while ((line = in.readLine()) != null) {
                Location p = new Location();
                String[] vals = line.split("\t");
                p.setX(Float.valueOf(vals[0]));
                p.setY(Float.valueOf(vals[1]));
                p.setZ(Double.parseDouble(vals[2]));
                data.add(p);
            }
        } catch (IOException e) {
            e.toString();
        } finally {
            if (in != null)
                try {
                    in.close();
                } catch (Exception e2) {
                    Log.e("", e2.getMessage());
                    e2.printStackTrace();
                }
        }
        pThread.setPlayingData(data);
        pThread.setRunning(true);
        pThread.resetPlayData();
    }

    void initThread() {
        pThread = new GisPlayerThread();
        pThread.state = PlayerThread.Stop;

    }

    public void play() {
        if (pThread == null) {
//			XXX: use property holder.
            initThread();
        }

        if (pThread.state == PlayerThread.Play) {
            return;
        }

        if (!pThread.getIsStarted()) {
            pThread.start();
        }

        pThread.state = PlayerThread.Play;

    }

    public void stopPlaying() {
        if (pThread == null) {
            initThread();
        } else {
            pThread.resetPlayData();
        }

        pThread.state = PlayerThread.Stop;
    }

    public void terminate() {
        if (pThread != null) {
            pThread.setRunning(false);
        }
        pThread = null;
    }

    public Location getCurrentPoint() {
        if (pThread != null) {
            return pThread.getCurrentPoint();
        } else {
            return new Location();
        }

    }
}
