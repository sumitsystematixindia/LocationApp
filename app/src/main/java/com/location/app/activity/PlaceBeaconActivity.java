package com.location.app.activity;


//import androidx.fragment.app.Fragment;

import android.Manifest;
import android.app.Fragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Observable;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.github.twocoffeesoneteam.glidetovectoryou.GlideToVectorYou;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.GroundOverlay;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.location.app.R;
import com.location.app.activity.addbeacon.SelectBeacon;
import com.location.app.data.DataManager;
import com.location.app.data.DataType;
import com.location.app.data.LocationDataType;
import com.location.app.fragment.PlaceBeaconPreviewFragment;
import com.location.app.fragment.ScanBeaconFragment;
import com.location.app.interfaces.OnDataDownloadedCallBack;
import com.location.app.model.BeaconLocation;
import com.location.app.model.BeaconsModel;
import com.location.app.model.Floor;
import com.mlins.ble.BleScanner;
import com.mlins.locator.GpsLocator;
import com.mlins.screens.ScanningActivity;
import com.mlins.utils.FacilityConf;
import com.mlins.utils.FacilityContainer;
import com.mlins.utils.FloorData;
import com.mlins.utils.GisDrawHelper;
import com.mlins.utils.PoiDataHelper;
import com.mlins.utils.PropertyHolder;
import com.mlins.utils.ResourceDownloader;
import com.mlins.utils.gis.GisData;
import com.mlins.views.TouchImageView;
import com.mlins.wireless.WlBlip;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

public class PlaceBeaconActivity extends ScanningActivity implements View.OnClickListener, OnDataDownloadedCallBack {
    public static final String TAG = SettingActivity.class.getSimpleName();
    Context context;
    Fragment fragment = null;
    String tag = "";
    ImageView iv_information, iv_scan_beacon_map;
    LinearLayout bottom;
    RelativeLayout rl_view;


    private TouchImageView mPlanView;
    private MapView mMapView;
    private GoogleMap map;

    private String currentMapUri;
    private int currenFloor = 0;
    private int floorId = 0;
    String[] floorstxtArr = null;
    ProgressDialog dialog;
    private List<FloorData> currentMapData = null;
    private ArrayList<BeaconLocation> beaconsLocation = new ArrayList<BeaconLocation>();
    List<BeaconsModel> beaconsModelList = new ArrayList<>();
    NumberPicker numberPicker;
    Bundle savedInstanceStatetmp;
    private Bitmap floorMapImg = null;
    //  FloorData floorData;
    FacilityConf facilityConf;
    ArrayList<Floor> floorDataList = new ArrayList<>();
    String floor_map_url = "";
    String floor_map_poi_url = "";
    String string_floor_data = "";
    private DataType dataType = new LocationDataType();
    String json = null;
    FusedLocationProviderClient mFusedLocationClient;
    LatLng latLng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_place_beacon);
        context = PlaceBeaconActivity.this;
        floor_map_url = getIntent().getExtras() != null ? getIntent().getExtras().getString("floorMapUrl") : "";
        floor_map_poi_url = getIntent().getExtras() != null ? getIntent().getExtras().getString("floorMapPoiUrl") : "";
        string_floor_data = getIntent().getExtras() != null ? getIntent().getExtras().getString("FloorDataList") : "";
        floorDataList = new Gson().fromJson(string_floor_data, new TypeToken<ArrayList<Floor>>() {
        }.getType());
        //   fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        //  fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

         mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        long time = System.currentTimeMillis() / 1000L;
        Log.d(TAG, "onCreate: "+time);
        iv_information = findViewById(R.id.iv_information);
        iv_scan_beacon_map = findViewById(R.id.iv_scan_beacon_map);
        bottom = findViewById(R.id.bottom);
        rl_view = findViewById(R.id.rl_view);
        bottom.setVisibility(View.VISIBLE);
        iv_information.setOnClickListener(this);
        iv_scan_beacon_map.setOnClickListener(this);

        savedInstanceStatetmp = savedInstanceState;
        mMapView = (MapView) findViewById(R.id.map1);
        mPlanView = (TouchImageView) findViewById(R.id.PlanView);
        numberPicker = (NumberPicker) findViewById(R.id.numberPicker);
        DataManager.getInstance().WriteToFile();
        facilityConf = new FacilityConf();

        List<String> uuids = new ArrayList<String>();
        uuids.add("b2dd3555-ea39-4f08-862a-00fb026a800b");
        //  uuids.add("B2DD3555EA394F08862A00FB026A800B");
        PropertyHolder.getInstance().setUuidScan(true);
        PropertyHolder.getInstance().setUuidList(uuids);
        new BleScanner();

        for (int i = 0; i < floorDataList.size(); i++) {
            facilityConf.addFloor(new FloorData(floorDataList.get(i).getId(), floor_map_url + "/" + floorDataList.get(i).getMapImage(), "", floorDataList.get(i).getFloorName(), 0.0f, 0.0f, ""));
            if (floorDataList.get(i).getBeacons() != null && floorDataList.get(i).getBeacons().size() > 0)
                beaconsModelList = floorDataList.get(i).getBeacons();


        }

        getBeacons();

        onMyCreate();
    }

    public void getBeacons() {


        try {
            JSONArray jsonArray = new JSONArray();
            for (BeaconsModel beaconsModel : beaconsModelList) {

                JSONObject js1 = new JSONObject();
                js1.put("id", String.valueOf(beaconsModel.getBeacon_id()));
                js1.put("name", "");
                // js1.put("x", beaconsModel.getLat());
//                js1.put("x",getxPoint( Double.parseDouble(beaconsModel.getLat()) ,Double.parseDouble(beaconsModel.getLons()) ));
//                js1.put("y", getYPoint( Double.parseDouble(beaconsModel.getLat()) ,Double.parseDouble(beaconsModel.getLons()) ));
                js1.put("x", beaconsModel.getX());
                js1.put("y", beaconsModel.getY());
                js1.put("str_x", String.valueOf(beaconsModel.getLat()));
                js1.put("str_y", String.valueOf(beaconsModel.getLons()));
                js1.put("floor", beaconsModel.getFloor_id());
                js1.put("msg", "");
                js1.put("time", String.valueOf(beaconsModel.getInterval()));
                js1.put("tx_power", beaconsModel.getTxPower());
                js1.put("major", beaconsModel.getMajor());
                js1.put("minar", beaconsModel.getMinor());
                jsonArray.put(js1);
                // convertSphericalToCartesian(Double.parseDouble(beaconsModel.getLat()),Double.parseDouble(beaconsModel.getLons()));
                // new BeaconLocation(beaconsModel.getLat(),beaconsModel.getY())

            }
            JSONObject js = new JSONObject();
            js.put("iterations", 10);
            js.put("topk", 2);
            js.put("decay_step", 0.2);
            js.put("init_decay_percent", 0.1);
            js.put("detect_floor_top_k", 5);
            js.put("beacons_location", jsonArray);


            json = String.valueOf(js);
            //   DataManager.getInstance().addAll(getBeaconsLocationsFromString(json));
            DataManager.getInstance().setBeaconsLocationAll(dataType.getBeaconsLocationsFromString(json));


        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    private void onMyCreate() {

        PropertyHolder.getInstance().setDevelopmentMode(true);
        dialog = new ProgressDialog(this);
        dialog.setCancelable(false);

        currentMapData = facilityConf.getFloorDataList();
        String url = currentMapData.get(currenFloor).mapuri;

        Log.d(TAG, "onMyCreate: "+url);

//        if (url.endsWith(".png")) {
//            Glide.with(context).load(url).sr.into(mPlanView);
//        } else if (url.endsWith(".jpg")) {
//            Glide.with(context).load(url).into(mPlanView);
//        } else {
//            GlideToVectorYou.init().with(this).load(Uri.parse(url), mPlanView);
//        }

       // floorMapImg=  getBitmapFromURL(url);
         new DownloadTask().execute(stringToURL(url));
         //new DownloadTask().execute(stringToURL("https://indoor-location.siplsolutions.com/images/floor/1646382076.svg"));
       // Log.d(TAG, "onMyCreate: "+floorMapImg);
      //  mPlanView.setImageBitmap(floorMapImg);
      //  mPlanView.setMaxZoom(15f);
        createfloorspicker();
       // mPlanView.setMaxZoom(15f);





    }


    private class DownloadTask extends AsyncTask<URL,Void,Bitmap> {
        protected void onPreExecute(){

        }
        protected Bitmap doInBackground(URL...urls){
            URL url = urls[0];
            HttpURLConnection connection = null;
            try{
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                InputStream inputStream = connection.getInputStream();
                BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);

                return BitmapFactory.decodeStream(bufferedInputStream);
            }catch(IOException e){
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(Bitmap result){
            Log.d(TAG, "onPostExecute: "+result);
            if(result!=null){                mPlanView.setOnClickListener(clickListener);

                floorId=floorDataList.get(currenFloor).getId();
                mPlanView.setImageBitmap(result);
                mPlanView.setMaxZoom(15f);
                mPlanView.setZoom(1.01f);
                FacilityConf fac = FacilityContainer.getInstance().getSelected();
                FacilityContainer.getInstance().setCurrent(fac);
                PoiDataHelper.getInstance().loadPois(currenFloor);
                PoiDataHelper.getInstance().drawPois(mPlanView);
                mPlanView.setOnClickListener(clickListener);
                mPlanView.setVisibility(View.VISIBLE);
                mMapView.setVisibility(View.GONE);
                if (PropertyHolder.getInstance().isDevelopmentMode()) {
                    if(floorDataList.get(currenFloor).getIndoorPathways().size()>0)
                    {
                        GisData.getInstance().loadGisLines(currenFloor, floorDataList.get(currenFloor).getIndoorPathways().get(0).getLatLons());
                        GisDrawHelper.getInstance().drawGis(mPlanView);
                    }
                }
                bildOutDoorMap();
                markPoint();
            } else {
                Toast.makeText(PlaceBeaconActivity.this, "Error", Toast.LENGTH_SHORT).show();
            }
        }
    }
    protected URL stringToURL(String url) {
        try {
            URL url1 = new URL(url);
            return url1;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
    }


    private void bildOutDoorMap() {
        try {
            MapsInitializer.initialize(this);
        } catch (Throwable e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        mMapView.onCreate(savedInstanceStatetmp);
      //  mMapView.getMapAsync(mapCallback);
    }

//    private OnMapReadyCallback mapCallback = new OnMapReadyCallback() {
//        @Override
//        public void onMapReady(GoogleMap gm) {
//            map = gm;
//            latLng=new LatLng(22.7436,75.8968);
//
//
//
//
//
//            try {
//                if (ActivityCompat.checkSelfPermission(PlaceBeaconActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(PlaceBeaconActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//                    return;
//                }
//                map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
//                mMapView.invalidate();
//                map.getUiSettings().setZoomControlsEnabled(true);
//                map.getUiSettings().setRotateGesturesEnabled(false);
//                map.getUiSettings().setScrollGesturesEnabled(false);
//                map.getUiSettings().setTiltGesturesEnabled(false);
//                mMapView.setVisibility(View.VISIBLE);
//                mPlanView.setVisibility(View.GONE);
//                Log.d(TAG, "onMapReady: "+latLng);
////                String url = currentMapData.get(currenFloor).mapuri;
////                if (url.endsWith(".png")) {
////                    Glide.with(context).load(url).into(mPlanView);
////                } else if (url.endsWith(".jpg")) {
////                    Glide.with(context).load(url).into(mPlanView);
////                } else {
////                    GlideToVectorYou.init().with(context).load(Uri.parse(url), mPlanView);
////                }
//
//
//
//                BitmapDescriptor image = BitmapDescriptorFactory.fromResource(R.drawable.floor_image);
//                GroundOverlayOptions groundOverlay = new GroundOverlayOptions()
//                        .image(image)
//                        .position(latLng, 40f,80f)
//                        .transparency(0.5f);
//                map.addGroundOverlay(groundOverlay);
//
//                CameraPosition cameraPosition = new CameraPosition.Builder()
//                        .target(new LatLng(22.7436, 75.8968))
//                        .tilt(50)
//                        .zoom(20)
//                        .build();
//             //   map.addMarker(new MarkerOptions().position(latLng).snippet("Coordinates: " + 22.7436 + " " + 75.8968));
//                map.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
//
////                mFusedLocationClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
////                    @Override
////                    public void onSuccess(Location location) {
////                       // latLng=new LatLng(location.getLatitude(),location.getLongitude());
////
////
////                       // Log.d(TAG, "onSuccess: "+22.7436+", "+75.8968);
////                       // Log.d(TAG, "onSuccess: "+latLng);
////                        CameraUpdate center = CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(), location.getLongitude()));
////                        CameraUpdate zoom = CameraUpdateFactory.zoomTo(11);
////                        map.clear();
////
////                        MarkerOptions mp = new MarkerOptions();
////
////                        mp.position(new LatLng(location.getLatitude(), location.getLongitude()));
////
////                        mp.title("my position");
////
////                        map.addMarker(mp);
////                        map.moveCamera(center);
////                        map.animateCamera(zoom);
////
//////                        CameraUpdate cameraUpdate = CameraUpdateFactory
//////                                .newLatLngZoom(latLng, 20);
//////                        map.animateCamera(cameraUpdate);
////                        Toast.makeText(PlaceBeaconActivity.this, "lat " + location.getLatitude() + "\nlong " + location.getLongitude(), Toast.LENGTH_SHORT).show();
////                    }
////                })
////                        .addOnFailureListener(new OnFailureListener() {
////                            @Override
////                            public void onFailure(@NonNull Exception e) {
////                                e.printStackTrace();
////                            }
////                        });
//
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//    };
//
//    private void getLocationPermission() {
//        /*
//         * Request location permission, so that we can get the location of the
//         * device. The result of the permission request is handled by a callback,
//         * onRequestPermissionsResult.
//         */
//        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
//                android.Manifest.permission.ACCESS_FINE_LOCATION)
//                == PackageManager.PERMISSION_GRANTED) {
//            locationPermissionGranted = true;
//        } else {
//            ActivityCompat.requestPermissions(this,
//                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
//                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
//        }
//    }
//    private void updateLocationUI() {
//        if (map == null) {
//            return;
//        }
//        try {
//            if (locationPermissionGranted) {
//                map.setMyLocationEnabled(true);
//                map.getUiSettings().setMyLocationButtonEnabled(true);
//            } else {
//                map.setMyLocationEnabled(false);
//                map.getUiSettings().setMyLocationButtonEnabled(false);
//                lastKnownLocation = null;
//                getLocationPermission();
//            }
//        } catch (SecurityException e)  {
//            Log.e("Exception: %s", e.getMessage());
//        }
//    }
//
//    private void getDeviceLocation() {
//        /*
//         * Get the best and most recent location of the device, which may be null in rare
//         * cases when a location is not available.
//         */
//        try {
//            if (locationPermissionGranted) {
//                Task<Location> locationResult = fusedLocationProviderClient.getLastLocation();
//                locationResult.addOnCompleteListener(this, new OnCompleteListener<Location>() {
//                    @Override
//                    public void onComplete(@NonNull Task<Location> task) {
//                        if (task.isSuccessful()) {
//                            // Set the map's camera position to the current location of the device.
//                            lastKnownLocation = task.getResult();
//                            if (lastKnownLocation != null) {
//                                map.moveCamera(CameraUpdateFactory.newLatLngZoom(
//                                        new LatLng(lastKnownLocation.getLatitude(),
//                                                lastKnownLocation.getLongitude()), DEFAULT_ZOOM));
//                            }
//                        } else {
//                            Log.d(TAG, "Current location is null. Using defaults.");
//                            Log.e(TAG, "Exception: %s", task.getException());
//                            map.moveCamera(CameraUpdateFactory
//                                    .newLatLngZoom(defaultLocation, DEFAULT_ZOOM));
//                            map.getUiSettings().setMyLocationButtonEnabled(false);
//                        }
//                    }
//                });
//            }
//        } catch (SecurityException e)  {
//            Log.e("Exception: %s", e.getMessage(), e);
//        }
//    }

    private void createfloorspicker() {
        try {
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
        } catch (Throwable t) {
            t.printStackTrace();
        }
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
            for (final BeaconLocation beacon : DataManager.getInstance().getBeaconsLocation()) {
                if (beacon.getFloor() == currenFloor) {
                    float x1 = Float.valueOf(beacon.getX());
                    float y1 = Float.valueOf(beacon.getY());
                    p1 = new PointF(x1, y1);
                    float pixeltometer;
                    FacilityConf fac = FacilityContainer.getInstance().getSelected();
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

        DataManager.getInstance().setBeaconSelected(point);
        Intent myIntent = new Intent(this, SelectBeacon.class);
        myIntent.putExtra("currentFloorId", floorId);
        myIntent.putExtra("pointX", point.x);
        myIntent.putExtra("pointY", point.y);
        this.startActivity(myIntent);

    }


    private NumberPicker.OnValueChangeListener numberPickerListener = new NumberPicker.OnValueChangeListener() {

        @Override
        public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
            currenFloor = newVal;
            floorId = floorDataList.get(newVal).getId();
            DataManager.getInstance().setCurrenFloor(currenFloor);
            currentMapUri = currentMapData.get(currenFloor).mapuri;
            if (floorMapImg != null) {
                floorMapImg.recycle();
            }
            String url = currentMapData.get(currenFloor).mapuri;


//            if (url.endsWith(".png")) {
//                Glide.with(context).load(url).into(mPlanView);
//            } else if (url.endsWith(".jpg")) {
//                Glide.with(context).load(url).into(mPlanView);
//            } else {
//                GlideToVectorYou.init().with(context).load(Uri.parse(url), mPlanView);
//            }
            new DownloadTask().execute(stringToURL(url));

//            PoiDataHelper.getInstance().loadPois(currenFloor);
//            PoiDataHelper.getInstance().drawPois(mPlanView);
//            if (PropertyHolder.getInstance().isDevelopmentMode()) {
//                GisData.getInstance().loadGisLines(currenFloor,floorDataList.get(currenFloor).getIndoorPathways().get(0).getLatLons());
//                GisDrawHelper.getInstance().drawGis(mPlanView);
//                Log.d(TAG, "onValueChange: "+floorDataList.get(currenFloor).getIndoorPathways().get(0).getLatLons());
//            }


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

    public void markPoint() {
        clearView();
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                PointF p = new PointF();
                List<BeaconLocation> beacons_location = DataManager.getInstance().getBeaconsLocation();
                Log.d(TAG, "run: " + beacons_location.size());
                for (int i = 0; i < beacons_location.size(); i++) {

                    if (beacons_location != null && beacons_location.size() > 0) {


                        for (BeaconLocation floorImagePoi : beacons_location) {

//                            Log.d(TAG, "run: x value " + floorImagePoi.getX());
//                            Log.d(TAG, "run: floorId" + floorId);
//                            Log.d(TAG, "run: list floor id" + floorImagePoi.getFloor());
                            Log.d(TAG, "run:floorId "+floorId);
                            Log.d(TAG, "run:floorImagePoi "+floorImagePoi.getFloor());
                            if (floorId == floorImagePoi.getFloor()) {
                                p = new PointF();
                                if (currenFloor != -999) {
                                    //    Log.d(TAG, "run: x value "+floorImagePoi.getX());
                                    float x = Float.parseFloat(String.valueOf(floorImagePoi.getX()));
                                    float y = Float.parseFloat(String.valueOf(floorImagePoi.getY()));
                                    p.set(x, y);
                                    markPoint(p, Color.parseColor("#0D42D2"), 5);
                                }
                            }
                        }
                    }


                }


            }

        });

        mPlanView.invalidate();
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


    private void clearView() {
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                mPlanView.clearMarks();
            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();
//		onMyCreate();
        markPoint();
        Log.d(TAG, "onResume: call");
        //mMapView.onResume();
       // GpsLocator.getInstance().init();
    }

    @Override
    protected void onDestroy() {
        //  mMapView.onDestroy();
        super.onDestroy();

        // markPoint();
    }

    @Override
    protected void onResultsDelivered(List<WlBlip> results) {
//        boolean inRange = PlacementStateChecker.getInstance().updateState(results);
//        displayState(inRange);
    }

    @Override
    protected void onPause() {
        //markPoint();
        //mMapView.onPause();
       // GpsLocator.getInstance().stop();

        super.onPause();
    }

    @Override
    public void onLowMemory() {
        //  markPoint();
        // mMapView.onLowMemory();
        super.onLowMemory();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        // mMapView.onSaveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }

    void checkifLocationServicesareenabled() {
        LocationManager lm = (LocationManager) PlaceBeaconActivity.this.getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false;
        boolean network_enabled = false;

        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ex) {
        }

        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception ex) {
        }

        if (!gps_enabled && !network_enabled) {
            // notify user
            AlertDialog.Builder dialog = new AlertDialog.Builder(PlaceBeaconActivity.this);
            dialog.setMessage("GPS network not enabled");
            dialog.setPositiveButton("Open location settings", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    // TODO Auto-generated method stub
                    Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    PlaceBeaconActivity.this.startActivity(myIntent);
                    //get gps
                }
            });
            dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    // TODO Auto-generated method stub

                }
            });
            dialog.show();
        }
    }


    /////////old code/////////////
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_information:
                callFregment();
                break;
            case R.id.iv_scan_beacon_map:
                callFragmentpreview();
                break;
            case R.id.img_back:
                int fragments = getFragmentManager().getBackStackEntryCount();
                //getSupportFragmentManager().getBackStackEntryCount();

                if (fragments == 0) {

                } else if (getFragmentManager().getBackStackEntryCount() > 0) {
                    getFragmentManager().popBackStack();
                } else {
                    new BleScanner().clean();
                    super.onBackPressed();
                }
                break;
        }
    }

    public double distance(PointF p, PointF p1) {

        return Math.sqrt((p.x - p1.x) * (p.x - p1.x) + (p.y - p1.y)
                * (p.y - p1.y));
    }

    public void callFregment() {

        fragment = new Fragment();
        tag = ScanBeaconFragment.class.getName();
        fragmentTransaction(fragment, tag);
    }

    public void callFragmentpreview() {
        fragment = new Fragment();
        tag = PlaceBeaconPreviewFragment.class.getName();
        fragmentTransaction(fragment, tag);
    }

    private void fragmentTransaction(Fragment fragment, String tag) {

        getFragmentManager();
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.addToBackStack(tag);
        fragmentTransaction.replace(R.id.place_frame, fragment, tag);
        fragmentTransaction.commit();

    }

    @Override
    public void OnDataDownloaded() {

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    public double getxPoint(double latitude, double longitude) {

        // Convert from Degrees to Radians
        double latRad = latitude * (Math.PI) / 180;
        double lonRad = longitude * (Math.PI) / 180;

        int earthRadius = 6367; // Radius in km
        double posX = earthRadius * Math.cos(latRad) * Math.cos(lonRad);

        // double  x = Math.sin(Math.PI/2-latitude) * Math.cos(longitude);

        // double posY = earthRadius * Math.cos(latRad) * Math.sin(lonRad);
        Log.d(TAG, "getxPoint: " + posX);
        return posX;
    }

    public double getYPoint(double latitude, double longitude) {

        // Convert from Degrees to Radians
        double latRad = latitude * (Math.PI) / 180;
        double lonRad = longitude * (Math.PI) / 180;

        int earthRadius = 6367; // Radius in km
        //double posX = earthRadius * Math.cos(latRad) * Math.cos(lonRad);
        double posY = earthRadius * Math.cos(latRad) * Math.sin(lonRad);

        // double  y = Math.sin(Math.PI/2-latitude) * Math.sin(longitude);
        Log.d(TAG, "getYPoint: " + posY);
        return posY;
    }
}