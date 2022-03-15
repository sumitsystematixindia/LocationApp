package com.mlins.nav.utils;

import android.graphics.PointF;
import android.text.format.DateFormat;
import android.util.Log;

import com.mlins.utils.PropertyHolder;
import com.mlins.wireless.WlBlip;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.List;

public class SpreoBlipsRecorder {

    private static SpreoBlipsRecorder instance = null;
    File dir = new File(PropertyHolder.getInstance().getAppDir(), "records");
    private String fileName = "";
    private StringBuffer sb = new StringBuffer();
    private int writingInteval = 10;
    private int blipsCounter = 0;

    public SpreoBlipsRecorder() {
        Date d = new Date();
        CharSequence ctime = DateFormat.format("yyyy-MM-dd hh:mm:ss", d.getTime());
        fileName = "record_" + ctime + "_.csv";
    }

    //looks like we don't need to rework this class now
    public static SpreoBlipsRecorder getInstance() {
        if (instance == null) {
            instance = new SpreoBlipsRecorder();
        }
        return instance;
    }

    public void writeBlips() {

        if (!dir.exists()) {
            dir.mkdirs();
        }
        File gfile = new File(dir, fileName);
        if (!gfile.exists()) {
            try {
                gfile.createNewFile();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }

        BufferedWriter out = null;
        try {
            out = new BufferedWriter(new FileWriter(gfile, true));
            out.write(sb.toString());
            out.flush();
        } catch (IOException e) {
            e.toString();
        } finally {
            if (out != null)
                try {
                    out.close();
                } catch (Exception e2) {
                    Log.e("", e2.getMessage());
                    e2.printStackTrace();
                }
        }
    }

    public synchronized void recordBlips(List<WlBlip> results, PointF p) {

        long currenttime = System.currentTimeMillis();
        String line = currenttime + ",";

        if (p != null) {
            line += p.x + ",";
            line += p.y + ",";
        }

        if (!results.isEmpty()) {
            for (WlBlip o : results) {
                line += o.BSSID;
                line += ",";
                line += o.level;
                line += ",";
            }
        }

        sb.append(line);
        sb.append("\n");
        blipsCounter++;
        if (blipsCounter >= writingInteval) {
            writeBlips();
            blipsCounter = 0;
            sb = new StringBuffer();
        }
    }
}
