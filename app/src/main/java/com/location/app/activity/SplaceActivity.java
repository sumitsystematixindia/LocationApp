package com.location.app.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.location.app.R;

public class SplaceActivity extends AppCompatActivity {
    private String TAG = "tag";
    private static int SPLASH_TIME_OUT = 1500;
    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 1;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splace);
        context=SplaceActivity.this;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                ImageView img_splash=findViewById(R.id.img_splash);
                img_splash.setImageDrawable(getResources().getDrawable(R.drawable.splash_1));
                next();
            }
        }, 1000);
    }
    private void next()
    {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
               startActivity(new Intent(context,SplaceActivity2.class));
               finish();
            }
        }, 500);
    }

}