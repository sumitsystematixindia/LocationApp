package com.mlins.screens;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.WindowManager;

import com.mlins.utils.PropertyHolder;
import com.mlins.utils.logging.Log;

public class BaseActivity extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (PropertyHolder.getInstance().getMlinsContext() == null) {
            PropertyHolder.getInstance().setMlinsContext(getApplicationContext());
        }
        Log.getInstance();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

//		Use device natural orientation in all descendant activities.
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);

        //for presantations with the nexus 10 use - SCREEN_ORIENTATION_SENSOR_PORTRAIT
    }


}
