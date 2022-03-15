package com.location.app.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.location.app.R;
import com.location.app.model.Datum;
import com.location.app.model.CampusDataResponce;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {
    public static final String TAG = MainActivity.class.getSimpleName();
    NavigationView navigationView;
    View headerLayout;
    Toolbar toolbar;
    private DrawerLayout drawerLayout;
    ActionBarDrawerToggle drawerToggle;
    Fragment fragment = null;
    String tag = "";
    Context context;
    ImageView imageCloseMenu;
    CampusDataResponce campusDataResponce = new CampusDataResponce();
    List<Datum> mProductList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        context = MainActivity.this;

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        drawerLayout = findViewById(R.id.drower);
        navigationView = findViewById(R.id.navigationview_id);

        headerLayout = navigationView.inflateHeaderView(R.layout.nav_header_layout);
        imageCloseMenu = headerLayout.findViewById(R.id.imageCloseMenu);

        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_traverse_path, R.id.navigation_place_beacon)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);
        toggleDrawer();

        Drawable wrappedDrawable = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            wrappedDrawable = DrawableCompat.wrap(getDrawable(R.drawable.menu_dashboard));
        }
        drawerToggle.setHomeAsUpIndicator(wrappedDrawable);

        navigationView.setNavigationItemSelectedListener(this);
        imageCloseMenu.setOnClickListener(this);

    }

    private void toggleDrawer() {
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close);
        drawerToggle.setDrawerIndicatorEnabled(false);
        Drawable drawable = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_baseline_dehaze_24, getTheme());
        drawerToggle.setHomeAsUpIndicator(drawable);
        drawerToggle.setToolbarNavigationClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (drawerLayout.isDrawerVisible(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                } else {
                    drawerLayout.openDrawer(GravityCompat.START);
                }
            }
        });
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.navigation_setting:
                Log.d(TAG, "onNavigationItemSelected: call this");
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                drawerLayout.closeDrawer(GravityCompat.START);
                finish();
                break;
            case R.id.my_path:
                startActivity(new Intent(getApplicationContext(), MyPathActivity.class));
                drawerLayout.closeDrawer(GravityCompat.START);
                break;
            case R.id.setting:
                startActivity(new Intent(context, SettingActivity.class));
                drawerLayout.closeDrawer(GravityCompat.START);
                break;
            case R.id.about:
                startActivity(new Intent(getApplicationContext(), AboutActivity.class));
                drawerLayout.closeDrawer(GravityCompat.START);
                break;
            case R.id.feedback:
                startActivity(new Intent(getApplicationContext(), FeedbackActivity.class));
                drawerLayout.closeDrawer(GravityCompat.START);
                break;
            default:

                break;

        }

        return true;
    }

    @Override
    public void onBackPressed() {
        //Checks if the navigation drawer is open -- If so, close it
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        // If drawer is already close -- Do not override original functionality
        else {
            exitApp();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imageCloseMenu:
                drawerLayout.closeDrawer(GravityCompat.START);
                break;
        }
    }

    private void exitApp() {
        new AlertDialog.Builder(this)
                .setMessage("Are you sure you want to exit?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        finishAffinity();
                        System.exit(0);
                    }
                })
                .setNegativeButton("No", null)
                .show();
        }





}



