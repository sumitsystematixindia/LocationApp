package com.mlins.wireless.filePlayer;

import com.mlins.utils.Cleanable;
import com.mlins.utils.Lookup;
import com.mlins.wireless.IResultReceiver;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class FilePlayer implements Cleanable {

    public static final String STEPS = "REC_STEPS";

    List<RecordStep> data = new ArrayList<RecordStep>();
    PlayerThread pThread;
    int mCounter = 0;
    public FilePlayer() {
        initThread();
    }

    public static FilePlayer getInstance() {
        return Lookup.getInstance().get(FilePlayer.class);
    }

    public void clean(){
        if (pThread != null) {
            pThread.mRunning = false;
        }
        stopPlaying();
    }

    public void load(String filename) {
        if (pThread != null) {
            pThread.setRunning(false);
        }

        initThread();
        BufferedReader in = null;
        data.clear();
        // load the data from the file
        try {
            in = new BufferedReader(new FileReader(filename));
            StringBuffer buffer = new StringBuffer();
            String line = "";
            while ((line = in.readLine()) != null) {
                buffer.append(line);
            }
            String json = buffer.toString();
            JSONTokener tokener = new JSONTokener(json);
            JSONObject jsonobj = (JSONObject) tokener.nextValue();

            JSONArray steps = jsonobj.getJSONArray(STEPS);

            for (int i = 0; i < steps.length(); i++) {
                JSONObject step = (JSONObject) steps.get(i);
                RecordStep recstep = new RecordStep();
                recstep.Parse(step);
                data.add(recstep);
            }
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                in = null;
            }
        }

        // then add it to the thread data storage

        pThread.getData().addAll(data);
    }

    void initThread() {
        pThread = new PlayerThread();
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

    public void pause() {
        if (pThread == null) {
            initThread();
        }

        pThread.state = PlayerThread.Pause;
    }

    public void step(int steps) {
        if (pThread == null) {
            initThread();
        }
        pThread.setSteps(steps);
        pThread.state = PlayerThread.Step;
    }

    public void stopPlaying() {
        if (pThread == null) {
            initThread();
        } else {
            pThread.reset();
        }

        pThread.state = PlayerThread.Stop;
    }

    public void addListener(IResultReceiver listener) {
        pThread.addListener(listener);
    }

    public boolean removeListener(IResultReceiver listener) {
        return pThread.removeListener(listener);
    }

}
