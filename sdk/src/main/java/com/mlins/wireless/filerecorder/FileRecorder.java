package com.mlins.wireless.filerecorder;

import com.mlins.utils.PropertyHolder;
import com.mlins.wireless.IResultReceiver;
import com.mlins.wireless.WlBlip;
import com.mlins.wireless.filePlayer.FilePlayer;
import com.mlins.wireless.filePlayer.RecordStep;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FileRecorder implements IResultReceiver {
    private static final int DUMP_SIZE = 50;
    static FileRecorder instance = null;
    BufferedWriter out = null;
    private List<RecordStep> mRecords = new ArrayList<RecordStep>();
    private boolean first;

    //doesn't hold state, needs to be stopped
    public static FileRecorder getInstance() {
        if (instance == null) {
            instance = new FileRecorder();
        }
        return instance;
    }

    public void init() {

    }

    public void Start() {
        File f = PropertyHolder.getInstance().getRecordingFile();
        first = true;
        try {
            if (!f.exists()) {
                f.createNewFile();
            }
            out = new BufferedWriter(new FileWriter(f, false));
            out.write("{\"" + FilePlayer.STEPS + "\":[\n");
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            out = null;
        }

    }

    public void stop() {
        dumpRecords();
        try {
            out.write("]}");
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            try {
                out.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }


    @Override
    public void onRecieve(List<WlBlip> results) {
        long now = System.currentTimeMillis();
        mRecords.add(new RecordStep(now, results));
        if (mRecords.size() >= DUMP_SIZE) {
            dumpRecords();
        }
    }

    private void dumpRecords() {

        try {
            for (RecordStep rs : mRecords) {
                if (!first) {
                    out.write(",\n");
                }
                first = false;
                out.write(rs.toJson());
            }
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        mRecords.clear();
    }
}
