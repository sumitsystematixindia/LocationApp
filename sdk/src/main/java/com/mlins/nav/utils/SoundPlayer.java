package com.mlins.nav.utils;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;

import com.mlins.utils.Cleanable;
import com.mlins.utils.Lookup;
import com.mlins.utils.PropertyHolder;
import com.mlins.utils.ResourceTranslator;
import com.mlins.utils.logging.Log;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;


public class SoundPlayer implements Cleanable, OnCompletionListener {
    private final static String TAG = "com.mlins.nav.utils.SoundPlayer";

    /**
     * single tone support
     */
    Context context;
    Map<String, Integer> soundMap = new HashMap<String, Integer>();
    private Queue<String> mQueue = new LinkedList<String>();
    private MediaPlayer mPlaying;
    private final static String MinusCharacter = "_99_";

    public SoundPlayer() {
        Log.getInstance().debug(TAG, "Enter, SoundPlayer()");
        createSoundMap();
        Log.getInstance().debug(TAG, "Exit, SoundPlayer()");
    }

    //depends on PropertyHolder, so reworking
    public static SoundPlayer getInstance() {
        return Lookup.getInstance().get(SoundPlayer.class);
    }

    public static void releaseInstance() {
        Lookup.getInstance().remove(SoundPlayer.class);
    }

    public void clean(){
        reset();
    }

    private void createSoundMap() {
        soundMap.put("turn_right", ResourceTranslator.getInstance().getTranslatedResourceId("raw", "turn_right"));
        soundMap.put("turn_left", ResourceTranslator.getInstance().getTranslatedResourceId("raw", "turn_left"));
        soundMap.put("straight", ResourceTranslator.getInstance().getTranslatedResourceId("raw", "straight"));
        soundMap.put("left_hall", ResourceTranslator.getInstance().getTranslatedResourceId("raw", "left_hall"));
        soundMap.put("right_hall", ResourceTranslator.getInstance().getTranslatedResourceId("raw", "right_hall"));
        soundMap.put("destination", ResourceTranslator.getInstance().getTranslatedResourceId("raw", "destination3"));
        soundMap.put("elevator_up", ResourceTranslator.getInstance().getTranslatedResourceId("raw", "elevator_up"));
        soundMap.put("elvator_down", ResourceTranslator.getInstance().getTranslatedResourceId("raw", "elevator_down"));
        soundMap.put("floor0", ResourceTranslator.getInstance().getTranslatedResourceId("raw", "floor0"));
        soundMap.put("floor1", ResourceTranslator.getInstance().getTranslatedResourceId("raw", "floor1"));
        soundMap.put("floor2", ResourceTranslator.getInstance().getTranslatedResourceId("raw", "floor2"));
        soundMap.put("floor3", ResourceTranslator.getInstance().getTranslatedResourceId("raw", "floor3"));
        soundMap.put("floor4", ResourceTranslator.getInstance().getTranslatedResourceId("raw", "floor4"));
        soundMap.put("floor5", ResourceTranslator.getInstance().getTranslatedResourceId("raw", "floor5"));
        soundMap.put("floor6", ResourceTranslator.getInstance().getTranslatedResourceId("raw", "floor6"));
        soundMap.put("floor7", ResourceTranslator.getInstance().getTranslatedResourceId("raw", "floor7"));
        soundMap.put("floor8", ResourceTranslator.getInstance().getTranslatedResourceId("raw", "floor8"));
        soundMap.put("floor9", ResourceTranslator.getInstance().getTranslatedResourceId("raw", "floor9"));
        soundMap.put("floor10", ResourceTranslator.getInstance().getTranslatedResourceId("raw", "floor10"));
        soundMap.put("floor11", ResourceTranslator.getInstance().getTranslatedResourceId("raw", "floor11"));
        soundMap.put("floor12", ResourceTranslator.getInstance().getTranslatedResourceId("raw", "floor12"));
        soundMap.put("floor13", ResourceTranslator.getInstance().getTranslatedResourceId("raw", "floor13"));
        soundMap.put("floor14", ResourceTranslator.getInstance().getTranslatedResourceId("raw", "floor14"));
        soundMap.put("floor15", ResourceTranslator.getInstance().getTranslatedResourceId("raw", "floor15"));
        soundMap.put("turnback", ResourceTranslator.getInstance().getTranslatedResourceId("raw", "turn_back"));
        soundMap.put("and_then", ResourceTranslator.getInstance().getTranslatedResourceId("raw", "and_then"));
        soundMap.put("floor", ResourceTranslator.getInstance().getTranslatedResourceId("raw", "floor"));
        soundMap.put("recalculate", ResourceTranslator.getInstance().getTranslatedResourceId("raw", "recalculate"));
        soundMap.put("continue_to_path", ResourceTranslator.getInstance().getTranslatedResourceId("raw", "continue_to_path"));
        soundMap.put("next_turn", ResourceTranslator.getInstance().getTranslatedResourceId("raw", "next_turn"));
        soundMap.put("floor-1", ResourceTranslator.getInstance().getTranslatedResourceId("raw", "floor" + MinusCharacter + "1"));
        soundMap.put("floor-2", ResourceTranslator.getInstance().getTranslatedResourceId("raw", "floor" + MinusCharacter + "2"));
        soundMap.put("floor-3", ResourceTranslator.getInstance().getTranslatedResourceId("raw", "floor" + MinusCharacter + "3"));
        soundMap.put("floor-4", ResourceTranslator.getInstance().getTranslatedResourceId("raw", "floor" + MinusCharacter + "4"));
        soundMap.put("floor-5", ResourceTranslator.getInstance().getTranslatedResourceId("raw", "floor" + MinusCharacter + "5"));
        soundMap.put("floor-6", ResourceTranslator.getInstance().getTranslatedResourceId("raw", "floor" + MinusCharacter + "6"));
        soundMap.put("floor-7", ResourceTranslator.getInstance().getTranslatedResourceId("raw", "floor" + MinusCharacter + "7"));
        soundMap.put("floor-8", ResourceTranslator.getInstance().getTranslatedResourceId("raw", "floor" + MinusCharacter + "8"));
        soundMap.put("floor-9", ResourceTranslator.getInstance().getTranslatedResourceId("raw", "floor" + MinusCharacter + "9"));
        soundMap.put("floor-10", ResourceTranslator.getInstance().getTranslatedResourceId("raw", "floor" + MinusCharacter + "10"));
    }

    public void play(Collection<String> sounds) {
        Log.getInstance().debug(TAG, "Enter, play()");
        if (!PropertyHolder.getInstance().isSdkObserverMode()) {
            mQueue.addAll(sounds);
            if (mPlaying == null) {
                mPlaying = play(mQueue.poll());
            }
        }
        Log.getInstance().debug(TAG, "Exit, play()");
    }

    private MediaPlayer play(String snd) {
        Log.getInstance().debug(TAG, "Enter, play()");
        MediaPlayer mp = null;
        if (soundMap.containsKey(snd) && context != null) {
            try {
                mp = MediaPlayer.create(context, soundMap.get(snd));
                mp.setOnCompletionListener(this);
                mp.start();
            } catch(Throwable t) {
                t.printStackTrace();
            }
        }
        Log.getInstance().debug(TAG, "Exit, play()");
        return mp;
    }

    public void reset() {
        mQueue.clear();
        mPlaying = null;

    }

    public void setContext(Context ctx) {
        context = ctx;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
//		if (mPlaying != mp) return;
//		mPlaying.release();

        if (!mQueue.isEmpty()) {
            String sound = mQueue.poll();
            if (sound != null)
                mPlaying = play(sound);
            if (mPlaying != null) return;
        }
        mPlaying = null;
        PropertyHolder.getInstance().setPlayingMedia(false);


    }

}
