package com.location.app.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.location.app.R;
import com.location.app.fragment.LoginAndSecurityFregment;
import com.location.app.fragment.TechniqueFregment;

public class SettingActivity extends AppCompatActivity implements View.OnClickListener {
    public static final String TAG = SettingActivity.class.getSimpleName();
    ImageView img_back;
    LinearLayout ll_technique, ll_login, ll_logout;
    Fragment fragment = null;
    String tag = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        img_back = findViewById(R.id.img_back);
        ll_technique = findViewById(R.id.ll_technique);
        ll_login = findViewById(R.id.ll_login);
        ll_logout = findViewById(R.id.ll_logout);
        img_back.setOnClickListener(this);
        ll_technique.setOnClickListener(this);
        ll_login.setOnClickListener(this);
        ll_logout.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Log.d(TAG, "onClick: ");
        switch (v.getId()) {

            case R.id.img_back:
                int fragments = getSupportFragmentManager().getBackStackEntryCount();

                if (fragments == 0) {
                    finish();
                } else if (getFragmentManager().getBackStackEntryCount() > 0) {
                    getFragmentManager().popBackStack();
                } else {
                    super.onBackPressed();
                }
                break;
            case R.id.ll_technique:
                callTechniqueFregment();
                break;
            case R.id.ll_login:
                callLoginFregment();
                break;
            case R.id.ll_logout:
                logout();
                break;
        }
    }

    public void callLoginFregment() {
        fragment = new LoginAndSecurityFregment();
        tag = LoginAndSecurityFregment.class.getName();
        fragmentTransaction(fragment, tag);
    }

    public void callTechniqueFregment() {
        fragment = new TechniqueFregment();
        tag = TechniqueFregment.class.getName();
        fragmentTransaction(fragment, tag);
    }

    private void fragmentTransaction(Fragment fragment, String tag) {

        FragmentManager fragmentManager = this.getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.addToBackStack(tag);
        fragmentTransaction.replace(R.id.frame, fragment, tag);
        fragmentTransaction.commit();

    }
    private void logout()
    {
        new AlertDialog.Builder(this)
                .setMessage("Are you sure you want to logout?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent intent=new Intent(SettingActivity.this,LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }
}