package com.location.app.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.bumptech.glide.Glide;
import com.github.twocoffeesoneteam.glidetovectoryou.GlideToVectorYou;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.LatLng;
import com.location.app.R;
import com.location.app.activity.addbeacon.SelectBeacon;
import com.location.app.data.DataManager;
import com.location.app.fragment.IntermittentFragment;
import com.location.app.model.BeaconLocation;
import com.location.app.model.Floor;

import com.location.app.model.IndoorPathway;
import com.location.app.utils.Preferences;
import com.mlins.aStar.NavigationPath;
import com.mlins.utils.FacilityConf;
import com.mlins.utils.FacilityContainer;
import com.mlins.utils.FloorData;
import com.mlins.utils.GisDrawHelper;
import com.mlins.utils.PoiDataHelper;
import com.mlins.utils.PropertyHolder;
import com.mlins.utils.gis.GisData;
import com.mlins.utils.gis.GisLine;
import com.mlins.utils.gis.GisPoint;
import com.mlins.views.TouchImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Node;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class IntermittentActivity extends AppCompatActivity implements View.OnClickListener {
    public static final String TAG = SettingActivity.class.getSimpleName();
    Context context;
    Fragment fragment = null;
    String tag = "";
    String path_type = "";
    ImageView iv_play, iv_scan_beacon_map;
    LinearLayout bottom;
    TextView tv_play_text_name;

    private TouchImageView mPlanView;
    private String currentMapUri;
    private int currenFloor = 0;
    String[] floorstxtArr = null;
    ProgressDialog dialog;
    private List<FloorData> currentMapData=null;
    NumberPicker numberPicker;
    private ArrayList<BeaconLocation> beaconsLocation = new ArrayList<BeaconLocation>();
    Bundle savedInstanceStatetmp;
    private Bitmap floorMapImg=null;
    FloorData floorData;
    FacilityConf facilityConf;
    String floor_map_url = "";
    String floor_map_poi_url = "";
    String string_floor_data = "";
    String json = null;
    List<Floor> floorDataList=new ArrayList<>();
    List<IndoorPathway> indoorPathways =new ArrayList<>();
    private FusedLocationProviderClient fusedLocationClient;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 2;
    private LocationAddressResultReceiver addressResultReceiver;
    LatLng newLatLng ;
    LatLng oldLatLng = null;
    LatLng preflatlong = null;
    private Location currentLocation;
    public static final double RADIUS = 6378137.0;
    static float sPixelDensity = -1f;
    private LocationCallback locationCallback;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_intermittent);
        path_type = getIntent().getExtras() != null ? getIntent().getExtras().getString("PathType") : "";
        context= this;

        floor_map_url = Preferences.getString5(context,Preferences.KEY_FLOOR_URL);
        floor_map_poi_url = Preferences.getString6(context,Preferences.KEY_FLOOR_URL_POI);
        floorDataList = Preferences.getFloorDataArrayList(context, Preferences.KEY_FLOOR_LIST);
        init();
        addressResultReceiver = new LocationAddressResultReceiver(new Handler());
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                currentLocation = locationResult.getLocations().get(0);

                newLatLng = new LatLng(currentLocation.getLatitude(),currentLocation.getLongitude());

                if (oldLatLng == null){

                    oldLatLng = newLatLng;
                    String aLocation = oldLatLng.latitude + "," + oldLatLng.longitude;

                    Log.d(TAG, "Location : oldlatlong" + aLocation );
                    Preferences.saveString7(context,Preferences.KEY_LAT_LONG,aLocation);
                }
                String location = Preferences.getString7(context,Preferences.KEY_LAT_LONG);
                Log.d(TAG, "Location : " + String.valueOf(location) );
                String[] locationArray = location.split(",");
                if(locationArray != null && locationArray.length > 0) {
                    String retrievedLat = locationArray[0];
                    String retrievedLong = locationArray[1];
                    preflatlong = new LatLng(Double.parseDouble(retrievedLat), Double.parseDouble(retrievedLong));
                    distance(preflatlong,newLatLng);
                }else{
                    Toast.makeText(getApplicationContext(), "Something Went Wrong ! please try again ", Toast.LENGTH_SHORT).show();
                }

//
//                Toast.makeText(getApplicationContext(), "onLocationResult: X " + String.valueOf(lon2x(newLatLng.longitude)), Toast.LENGTH_SHORT).show();
//                Toast.makeText(getApplicationContext(), "onLocationResult: Y  "+ String.valueOf(lat2y(newLatLng.latitude)), Toast.LENGTH_SHORT).show();
//                Toast.makeText(getApplicationContext(), String.valueOf(String.valueOf(currentLocation.getLatitude()) +",----,"+String.valueOf(currentLocation.getLongitude())), Toast.LENGTH_SHORT).show();
            }
        };


        startLocationUpdates();

//        String location = Preferences.getString7(context,Preferences.KEY_LAT_LONG);
//        Log.d(TAG, "Location : " + String.valueOf(location) );
//        String[] locationArray = location.split(",");
//        if(locationArray != null && locationArray.length > 0) {
//            String retrievedLat = locationArray[0];
//            String retrievedLong = locationArray[1];
//            preflatlong = new LatLng(Double.parseDouble(retrievedLat), Double.parseDouble(retrievedLong));
//
//            Log.d(TAG, "onCreate: Xpoi" + String.valueOf(getxPointX(preflatlong.latitude,preflatlong.longitude)));
//            Log.d(TAG, "onCreate: ypoi  "+ String.valueOf(getxPointY(preflatlong.latitude,preflatlong.longitude)));
////            Log.d(TAG, "onCreate: Ypoi  "+ String.valueOf(preflatlong.latitude));
////            Log.d(TAG, "onCreate: Ypoi  "+ String.valueOf(preflatlong.latitude));
////            Log.d(TAG, "onCreate: Xpoi" + String.valueOf(meterToPixel((float) lon2x(preflatlong.longitude))));
////            Log.d(TAG, "onCreate: Ypoi  "+ String.valueOf(meterToPixel((float) lat2y(preflatlong.latitude))));
//
//        }else{
//            Toast.makeText(getApplicationContext(), "Something Went Wrong ! please try again ", Toast.LENGTH_SHORT).show();
//        }

    }


    public double getxPointX(double latitude, double longitude) {
//         Convert from Degrees to Radians
         double latRad = latitude * (Math.PI) / 180;
         double lonRad = longitude * (Math.PI) / 180;
         int earthRadius = 6367; // Radius in km
         double posX = earthRadius * Math.cos(latRad) * Math.cos(lonRad);
         double posY = earthRadius * Math.cos(latRad) * Math.sin(lonRad);
         Log.d(TAG, "getxPoint x: " + posX);
         return posX;
    }
    public double getxPointY(double latitude, double longitude) {
//         Convert from Degrees to Radians
        double latRad = latitude * (Math.PI) / 180;
        double lonRad = longitude * (Math.PI) / 180;int earthRadius = 6367; // Radius in km
        double posX = earthRadius * Math.cos(latRad) * Math.cos(lonRad);
        double posY = earthRadius * Math.cos(latRad) * Math.sin(lonRad);
        Log.d(TAG, "getxPoint y: " + posY);
        return posY;
    }
    public static int meterToPixel(float meter) {
        // 1 meter = 39.37 inches, 1 inch = 160 dp.
        return Math.round(dpToPixel(meter * 39.37f * 160));
    }
    public static int dpToPixel(int dp) {
        return Math.round(dpToPixel((float) dp));
    }

    public static float dpToPixel(float dp) {
        return sPixelDensity * dp;
    }


    private boolean distance(LatLng old_latlong, LatLng new_latlong) {
        Location startPoint = new Location("locationA");
        Location endPoint = new Location("locationB");

        startPoint.setLatitude(old_latlong.latitude);
        startPoint.setLongitude(old_latlong.longitude);

        endPoint.setLatitude(new_latlong.latitude);
        endPoint.setLongitude(new_latlong.longitude);
        Toast.makeText(getApplicationContext(), "distance: " + startPoint.distanceTo(endPoint), Toast.LENGTH_SHORT).show();
        Log.d(TAG, "distance: " + startPoint.distanceTo(endPoint));
        return startPoint.distanceTo(endPoint) >= 20;
    }
    @SuppressWarnings("MissingPermission")
    private void startLocationUpdates() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new
                            String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
        else {
            LocationRequest locationRequest = new LocationRequest();
            locationRequest.setInterval(2000);
            locationRequest.setFastestInterval(1000);
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull
            int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startLocationUpdates();
            } else {
                Toast.makeText(this, "Location permission not granted, " + "restart the app if you want the feature", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        startLocationUpdates();
    }
    @Override
    protected void onPause() {
        super.onPause();
        fusedLocationClient.removeLocationUpdates(locationCallback);
    }
    public void init(){
        tv_play_text_name = findViewById(R.id.tv_play_text_name);
        iv_play = findViewById(R.id.iv_play);
        iv_play.setOnClickListener(this);
        if (path_type.equalsIgnoreCase("Intermittent")) {
            tv_play_text_name.setText("Intermittent");

        }
        else {
            tv_play_text_name.setText("Continuous");
        }
        mPlanView = (TouchImageView) findViewById(R.id.PlanView);
        numberPicker = (NumberPicker) findViewById(R.id.numberPicker);

        facilityConf=new FacilityConf();
        getBeacons();
        if(floorDataList != null) {
            for (int i = 0; i < floorDataList.size(); i++) {
                facilityConf.addFloor(new FloorData(floorDataList.get(i).getId(), floor_map_url + "/" + floorDataList.get(i).getMapImage(), "", floorDataList.get(i).getFloorName(), 0.0f, 0.0f, ""));
            }

        }else{
            Toast.makeText(getApplicationContext(), "FloorDataList Is null ", Toast.LENGTH_SHORT).show();
        }
        onMyCreate();
    }

    public void getBeacons(){
        JSONObject js = new JSONObject();
        try {
            JSONArray jsonArray = new JSONArray();
            JSONObject js1 = new JSONObject();
            js1.put("id", "07D0__07D4");
            js1.put("name", "");
            js1.put("x", 1839.4879);
            js1.put("y", 6710.529);
            js1.put("str_x", "344.1860046386719");
            js1.put("str_y", "1018.989990234375");
            js1.put("floor", 1);
            js1.put("msg", "");
            jsonArray.put(js1);
            JSONObject js2 = new JSONObject();
            js2.put("id", "07D0__07D5");
            js2.put("name", "");
            js2.put("x", 1859.4169);
            js2.put("y", 4956.5874);
            js2.put("str_x", "372.9939880371094");
            js2.put("str_y", "1207.5799560546875");
            js2.put("floor", 1);
            js2.put("msg", "");
            jsonArray.put(js2);
            JSONObject js3 = new JSONObject();
            js3.put("id", "03E8__03E9");
            js3.put("name", "");
            js3.put("x", 3341.1665);
            js3.put("y", 1660.8909);
            js3.put("str_x", "414.14300537109375");
            js3.put("str_y", "1124.1700439453125");
            js3.put("floor", 1);
            js3.put("msg", "");
            jsonArray.put(js3);
            js.put("iterations", 10);
            js.put("topk", 2);
            js.put("decay_step", 0.2);
            js.put("init_decay_percent", 0.1);
            js.put("detect_floor_top_k", 5);
            js.put("beacons_location", jsonArray);
            json = String.valueOf(js);

            beaconsLocation.addAll(getBeaconsLocationsFromString(json));

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    List<BeaconLocation> getBeaconsLocationsFromString(String string) throws JSONException {
        ArrayList<BeaconLocation> result = new ArrayList<>();

        JSONObject obj = new JSONObject(string);
        JSONArray m_jArry = obj.getJSONArray("beacons_location");
        for (int i = 0; i < m_jArry.length(); i++) {
            JSONObject tmpObj = m_jArry.getJSONObject(i);

            boolean isExist = false;
            if (!isExist) {
                BeaconLocation beacon = new BeaconLocation(tmpObj);

                result.add(beacon);
            }
        }
        Log.d(TAG, "getBeaconsLocationsFromString: "+result.size());
        return result;
    }


    public void markPoint() {
        clearView();
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                PointF p = new PointF();

//                 List<BeaconLocation> beacons_location = DataManager.getInstance().getBeaconsLocation();
//
//                Log.d(TAG, "run: "+beacons_location.size());
//                if (beacons_location != null && beacons_location.size() > 0) {
//                    for (BeaconLocation beaconLocation : beacons_location) {
//                        if (currenFloor == beaconLocation.getFloor()) {
//                            p = new PointF();
//                            if (currenFloor != -999) {
//                                float x = Float.valueOf(beaconLocation.getX());
//                                float y = Float.valueOf(beaconLocation.getY());
//                                p.set(x, y);
//                                markPoint(p, Color.parseColor("#0D42D2"), 10);
//                            }
//                        }
//                    }
//                }


                for (int i = 0; i < beaconsLocation.size(); i++) {

                    if (beaconsLocation!= null && beaconsLocation.size() > 0) {


                        for (BeaconLocation floorImagePoi : beaconsLocation) {
                            if (currenFloor == floorImagePoi.getFloor()) {
                                p = new PointF();
                                if (currenFloor != -999) {
                                    Log.d(TAG, "run:sdhdshdsh ");

                                    float x = Float.valueOf(floorImagePoi.getX());
                                    float y = Float.valueOf(floorImagePoi.getY());
                                    p.set(x, y);
                                    markPoint(p, Color.parseColor("#0D42D2"), 10);
                                }
                            }
                        }
                    }


                }


            }

        });

        mPlanView.invalidate();
    }

    private void clearView() {
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                mPlanView.clearMarks();
            }
        });


    }
    private void markPoint(PointF point, int color, int size) {
        int r = size;
        int x = (int) point.x;
        int y = (int) point.y;
        ShapeDrawable sd = new ShapeDrawable(new OvalShape());
        sd.getPaint().setColor(color);
        sd.setBounds(x - r, y - r, x + r, y + r);
        mPlanView.addMark(sd);
        mPlanView.invalidate();
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_play:

                callFregment();
                break;
        }
    }

    private void onMyCreate() {

        PropertyHolder.getInstance().setDevelopmentMode(true);
        dialog = new ProgressDialog(this);
        dialog.setCancelable(false);
        currentMapData =facilityConf.getFloorDataList();
        String url = currentMapData.get(currenFloor).mapuri;
        Log.d("Urlsnames",url);
        if (url.endsWith(".png")) {
            Glide.with(context).load(url).into(mPlanView);
        } else if (url.endsWith(".jpg")) {
            Glide.with(context).load(url).into(mPlanView);
        } else {
            GlideToVectorYou.init().with(context).load(Uri.parse(url), mPlanView);
        }
        createfloorspicker();
//        mPlanView.setMaxZoom(5f);
        FacilityConf fac = FacilityContainer.getInstance().getSelected();
        FacilityContainer.getInstance().setCurrent(fac);
        PoiDataHelper.getInstance().loadPois(currenFloor);
        PoiDataHelper.getInstance().drawPois(mPlanView);
        mPlanView.setOnClickListener(clickListener);
        if (PropertyHolder.getInstance().isDevelopmentMode()) {
            GisData.getInstance().loadGisLines(currenFloor,floorDataList.get(currenFloor).getIndoorPathways().get(0).getLatLons());
            GisDrawHelper.getInstance().drawGis(mPlanView);
            Log.d(TAG, "onValueChange: "+floorDataList.get(currenFloor).getIndoorPathways().get(0).getLatLons());
        }


        bildOutDoorMap();
//         markPoint();

        // downloadData();



    }

    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            PointF p, p1;
            boolean isexist = false;
            float x = mPlanView.getTouchLocation().x;
            float y = mPlanView.getTouchLocation().y;
            p = new PointF(x, y);
            Log.d(TAG, "onClick: points" + p);
            for (final BeaconLocation beacon : DataManager.getInstance()
                    .getBeaconsLocation()) {
                if (beacon.getFloor() == currenFloor) {
                    float x1 = Float.valueOf(beacon.getX());
                    float y1 = Float.valueOf(beacon.getY());
                    p1 = new PointF(x1, y1);
                    float pixeltometer;
                    FacilityConf fac = FacilityContainer.getInstance()
                            .getSelected();
                    if (fac != null) {
                        pixeltometer = fac.getPixelsToMeter();
                        double d = distance(p, p1);
                        if ((d / pixeltometer) < 15) {
                            isexist = true;
                            //    fireBeaconDeleteDialog(beacon);
                        }
                    }
                }
            }
            if (!isexist) {
                PointF startPoint = new PointF(x, y);
//				PointF pojectedpoint = GisData.getInstance().findClosestPointOnLine(startPoint);
                addPoint(startPoint);
            }

        }

    };

    private void addPoint(PointF point) {
        Log.d(TAG, "addPoint: "+point);
        // callFregment();
        // DataManager.getInstance().setBeaconSelected(point);
        Intent myIntent = new Intent(this, SelectBeacon.class);
        this.startActivity(myIntent);
    }

    public double distance(PointF p, PointF p1) {

        return Math.sqrt((p.x - p1.x) * (p.x - p1.x) + (p.y - p1.y)
                * (p.y - p1.y));
    }


    private void bildOutDoorMap() {
        try {
            MapsInitializer.initialize(this);
        } catch (Throwable e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    private void createfloorspicker() {
        try{
            floorstxtArr = new String[currentMapData.size()];
            for (int i = 0; i < currentMapData.size(); i++) {
                floorstxtArr[i] = currentMapData.get(i).getTitle();
            }

            int maxfloor = currentMapData.size() - 1;
            int minFloor = 0;
            numberPicker.setMaxValue(maxfloor);
            numberPicker.setMinValue(minFloor);

            numberPicker.setOnValueChangedListener(numberPickerListener);
            numberPicker
                    .setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
            numberPicker.setDisplayedValues(floorstxtArr);

            numberPicker.invalidate();
            setNumberPickerTextColor(numberPicker, Color.parseColor("#ffffff"));
            numberPicker.setValue(currenFloor);
        }
        catch(Throwable t){
            t.printStackTrace();
        }
    }
    private NumberPicker.OnValueChangeListener numberPickerListener = new NumberPicker.OnValueChangeListener() {

        @Override
        public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
            Log.d(TAG, "onValueChange: "+oldVal);
            Log.d(TAG, "onValueChange: "+newVal);
            currenFloor = newVal;
            DataManager.getInstance().setCurrenFloor(currenFloor);
            currentMapUri = currentMapData.get(currenFloor).mapuri;
            if(floorMapImg!=null){
                floorMapImg.recycle();
            }
            String url=currentMapData.get(currenFloor).mapuri;
            if (url.endsWith(".png")) {
                Glide.with(context).load(url).into(mPlanView);
            } else if (url.endsWith(".jpg")) {
                Glide.with(context).load(url).into(mPlanView);
            } else {
                GlideToVectorYou.init().with(context).load(Uri.parse(url), mPlanView);
            }
            mPlanView.setImageBitmap(floorMapImg);
//            mPlanView.setMaxZoom(5f);
            PoiDataHelper.getInstance().loadPois(currenFloor);
            PoiDataHelper.getInstance().drawPois(mPlanView);
            if (PropertyHolder.getInstance().isDevelopmentMode()) {
                GisData.getInstance().loadGisLines(currenFloor,floorDataList.get(currenFloor).getIndoorPathways().get(0).getLatLons());
                GisDrawHelper.getInstance().drawGis(mPlanView);
                Log.d(TAG, "onValueChange: "+floorDataList.get(currenFloor).getIndoorPathways().get(0).getLatLons());
            }


//            String url = ServerConnection.getResourcesUrl()+ SpreoDataProvider.getFacilityId() +"/" + currentMapUri;
//            floorMapImg = ResourceDownloader.getInstance().getLocalBitmap(url);
//            mPlanView.setImageBitmap(floorMapImg);
//            PoiDataHelper.getInstance().loadPois(currenFloor);
//            PoiDataHelper.getInstance().drawPois(mPlanView);
//            if (PropertyHolder.getInstance().isDevelopmentMode()) {
//                GisData.getInstance().loadGisLines(currenFloor);
//                GisDrawHelper.getInstance().drawGis(mPlanView);
//            }

              markPoint();
        }
    };




    public static boolean setNumberPickerTextColor(NumberPicker numberPicker, int color) {
        final int count = numberPicker.getChildCount();
        for (int i = 0; i < count; i++) {
            View child = numberPicker.getChildAt(i);
            if (child instanceof EditText) {
                try {
                    @SuppressLint("SoonBlockedPrivateApi") Field selectorWheelPaintField = numberPicker.getClass()
                            .getDeclaredField("mSelectorWheelPaint");
                    selectorWheelPaintField.setAccessible(true);
                    ((Paint) selectorWheelPaintField.get(numberPicker))
                            .setColor(color);
                    ((EditText) child).setTextColor(color);
                    numberPicker.invalidate();
                    return true;
                } catch (NoSuchFieldException e) {

                } catch (IllegalAccessException e) {

                } catch (IllegalArgumentException e) {

                }
            }
        }
        return false;
    }
    public void callFregment() {

        fragment = new IntermittentFragment();
        Bundle bundle = new Bundle();
        bundle.putString("PathType", path_type);
        System.out.println(bundle.toString());
        fragment.setArguments(bundle);
        tag = IntermittentFragment.class.getName();
        fragmentTransaction(fragment, tag);
    }

    private void fragmentTransaction(Fragment fragment, String tag) {

        FragmentManager fragmentManager = this.getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .add(R.id.place_frame, fragment, tag)
                .addToBackStack(null)
                .commitAllowingStateLoss();

    }

    private class LocationAddressResultReceiver extends ResultReceiver {
        LocationAddressResultReceiver(Handler handler) {
            super(handler);
        }
        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            if (resultCode == 0) {
                Log.d("Address", "Location null retrying");
            }
            if (resultCode == 1) {
                Toast.makeText(IntermittentActivity.this, "Address not found, ", Toast.LENGTH_SHORT).show();
            }
            String currentAdd = resultData.getString("address_result");
            showResults(currentAdd);
        }
        private void showResults(String currentAdd) {
        }
    }

}