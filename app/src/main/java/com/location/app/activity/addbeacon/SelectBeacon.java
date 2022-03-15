package com.location.app.activity.addbeacon;

import static android.content.ContentValues.TAG;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.location.app.R;
import com.location.app.SeekBarHelper;
import com.location.app.adapter.ScanResultListAdapter;
import com.location.app.data.DataManager;
import com.location.app.fragment.notifications.CampusListPresenter;
import com.location.app.fragment.notifications.CampusListPresenterViewModel;
import com.location.app.model.BeaconDataResponse;
import com.location.app.model.BeaconLocation;
import com.location.app.model.CampusDataResponce;
import com.location.app.model.ProximityBeaconLocation;
import com.location.app.utils.NetworkUtils;
import com.mlins.screens.ScanningActivity;
import com.mlins.wireless.IResultReceiver;
import com.mlins.wireless.WlBlip;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

//import adapters.ScanResultListAdapter;
//import data.DataManager;
//import objects.BeaconLocation;
//import objects.ProximityBeaconLocation;

public class SelectBeacon extends ScanningActivity implements IResultReceiver, AddBeaconPresenterViewModel.View {
    public ListView beaconlist;
    public List<WlBlip> getResults = new ArrayList<WlBlip>();
    public ScanResultListAdapter scanResultListAdapter = null;
    public WlBlip itemSelected = null;
    public LinearLayout addDescription;
    private Button addDescriptionBT, back;
    private Context ctx;
    private SelectBeacon mThis;
    private WlBlip beaconSelectedtmp;
    private SeekBar enterLevel, exitLevel;
    private TextView enterLevelText, exitLevelText;
    private BeaconLocation currentBeacon;
    private int currentPersentEnter;
    private int currentPersentExit;
    int currentFloorId;
    double point_X, point_Y;
    AddBeaconPresenter addBeaconPresenter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_select_beacon);

        beaconlist = (ListView) findViewById(R.id.scanBeacons);
        ctx = this;
        mThis = this;
        addDescription = (LinearLayout) findViewById(R.id.add_description);

        beaconlist.setOnItemClickListener(itemlistener);
        scanResultListAdapter = new ScanResultListAdapter(getResults, this);
        beaconlist.setAdapter(scanResultListAdapter);
        addDescriptionBT = (Button) findViewById(R.id.add_DescriptionBT);
        back = (Button) findViewById(R.id.backBT);
        back.setOnClickListener(backListener);
        addDescriptionBT.setOnClickListener(addDescriptionListener);
        currentFloorId = getIntent().getExtras() != null ? getIntent().getExtras().getInt("currentFloorId") : 0;
        point_X = getIntent().getExtras() != null ? getIntent().getExtras().getFloat("pointX") : 0;
        point_Y = getIntent().getExtras() != null ? getIntent().getExtras().getFloat("pointY") : 0;
        addBeaconPresenter = new AddBeaconPresenter(this);
//		List<String> uuids = new ArrayList<String>();
//		uuids.add("f85bcc1f-0626-407a-827f-facc078d3dbe");
//		PropertyHolder.getInstance().setUuidScan(false);
//		PropertyHolder.getInstance().setUuidList(uuids);

    }



    private void createLevelslayout(ProximityBeaconLocation location) {
        enterLevelText = (TextView) findViewById(R.id.enter_levelhText);
        exitLevelText = (TextView) findViewById(R.id.exit_levelText);

        enterLevel = (SeekBar) findViewById(R.id.enter_level);
        enterLevel.setOnSeekBarChangeListener(enterLevelListener);
        SeekBarHelper.setupSeekBar(enterLevel, location.enterLevel);

        exitLevel = (SeekBar) findViewById(R.id.exit_level);
        exitLevel.setOnSeekBarChangeListener(exitLevelListener);
        SeekBarHelper.setupSeekBar(exitLevel, location.exitLevel);
    }

    private SeekBar.OnSeekBarChangeListener enterLevelListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (currentBeacon != null) {
                currentPersentEnter = SeekBarHelper.getValueFromSeekBar(((ProximityBeaconLocation) currentBeacon).enterLevel, seekBar);
                enterLevelText.setText(Integer.toString(currentPersentEnter));
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
        }
    };

    private SeekBar.OnSeekBarChangeListener exitLevelListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (currentBeacon != null) {
                currentPersentExit = SeekBarHelper.getValueFromSeekBar(((ProximityBeaconLocation) currentBeacon).exitLevel, seekBar);
                exitLevelText.setText(Integer.toString(currentPersentExit));
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
        }
    };

    public OnClickListener backListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            finish();
//			Intent myIntent = new Intent(mThis, MainActivity.class);
//			mThis.startActivity(myIntent);
        }
    };
    public OnClickListener addDescriptionListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            EditText descriptionEt = (EditText) findViewById(R.id.descriptionEt);
            try {
                if (descriptionEt.length() > 0
                        && !"".equals(descriptionEt.getText().toString())) {
                    currentBeacon.setMsg(descriptionEt.getText().toString());
                } else {
                    currentBeacon.setMsg("No description");
                }
            } catch (Exception e) {
                currentBeacon.setMsg("No description");
            }

            if (currentBeacon instanceof ProximityBeaconLocation) {
                ((ProximityBeaconLocation) currentBeacon).enterLevel.set(currentPersentEnter);
                ((ProximityBeaconLocation) currentBeacon).exitLevel.set(currentPersentExit);
            }

            List<BeaconLocation> beacons = DataManager.getInstance().getBeaconsLocation();

            if (beaconSelectedtmp != null)
                for (BeaconLocation beaconL : beacons) {
                    if (beaconSelectedtmp.BSSID.equals(beaconL.getId())) {
                        DataManager.getInstance().getBeaconsLocation()
                                .remove(beaconL);
                        break;
                    }
                }
            Log.d(TAG, "onClick: beaconSelectedtmp" + beaconSelectedtmp.BSSID);
            Log.d(TAG, "onClick: beaconSelectedtmp" + beaconSelectedtmp.frequency);
            Log.d(TAG, "onClick: beaconSelectedtmp" + beaconSelectedtmp.timestamp);
            currentBeacon.setTime(String.valueOf(beaconSelectedtmp.timestamp));
            currentBeacon.setTx_power(beaconSelectedtmp.frequency);
            currentBeacon.setMajor(100);
            currentBeacon.setMinar(70);
            currentBeacon.setFloor(currentFloorId);
            currentBeacon.setX((float) point_X);
            currentBeacon.setY((float) point_Y);
            currentBeacon.setLat(Double.parseDouble(String.valueOf(point_X)));
            currentBeacon.setLon(Double.parseDouble(String.valueOf(point_Y)));
            getLatLong(point_X,point_Y);
//			Intent myIntent = new Intent(mThis, MainActivity.class);
//			mThis.startActivity(myIntent);
        }

    };
    private void getLatLong(double x, double y) {
        //z = sqrt(x² + y² - 2 * x * y * cos(γ)) finding z value
        double z = Math.sqrt(x * x + y * y - 2 * x * y * Math.cos(y));
        //let r = sqrt(x * x + y * y + z * z);
        double r = Math.sqrt(x * x + y * y + z * z);
        //    let lat = toDegrees(radians: asin(z / r));
        double lat = (Math.asin(z / r) * (180.0 / Math.PI));
        double lon = (Math.atan2(y, x) * (180.0 / Math.PI));

        currentBeacon.setLat(lat);
        currentBeacon.setLon(lon);

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("floor_id", currentFloorId);
        jsonObject.addProperty("beacon_id", beaconSelectedtmp.BSSID);
        jsonObject.addProperty("lat", currentBeacon.getLat());
        jsonObject.addProperty("lons", currentBeacon.getLon());
//            jsonObject.addProperty("confirm_password", confirm_pass);
        jsonObject.addProperty("TxPower", currentBeacon.getTx_power());
        jsonObject.addProperty("major", currentBeacon.getMajor());
        jsonObject.addProperty("minor", currentBeacon.getMinar());
        jsonObject.addProperty("interval", currentBeacon.getTime());
        jsonObject.addProperty("x", currentBeacon.getX());
        jsonObject.addProperty("y", currentBeacon.getY());
        Log.d(TAG, "beacon  data: " + jsonObject);
        if (NetworkUtils.isNetworkAvailable(ctx)) {
            try {
                DataManager.getInstance().addBeaconsLocation(currentBeacon);
                addBeaconPresenter.requestBeaconData(ctx,jsonObject);
                finish();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(ctx, "network error", Toast.LENGTH_SHORT).show();
        }



    }
    // private void returnToM
    @Override
    protected void onResultsDelivered(List<WlBlip> results) {
        if (results.size() > 0) {
            levelComparator levelcomparator = new levelComparator();
            Collections.sort(results, levelcomparator);
            Log.d(TAG, "onResultsDelivered: ");

            findViewById(R.id.progressBar).setVisibility(View.GONE);
            List<WlBlip> getResultsTmp = new ArrayList<WlBlip>();
            getResultsTmp.addAll(results);
            refreshlist(getResultsTmp);
        }
    }

    @Override
    public void showProgress() {

    }

    @Override
    public void hideProgress() {

    }

    @Override
    public void onResponseFailure(Throwable throwable) {

    }

    @Override
    public void setBeaconData(BeaconDataResponse beaconDataResponse) {

    }


    private class levelComparator implements Comparator<WlBlip> {

        @Override
        public int compare(WlBlip blip1, WlBlip blip2) {
            Integer level1 = blip1.level;
            Integer level2 = blip2.level;
            return level2.compareTo(level1);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        addDescription.setVisibility(View.GONE);
        beaconlist.setVisibility(View.VISIBLE);
        back.setVisibility(View.VISIBLE);

        currentBeacon = DataManager.getInstance().getBeaconSelected();
    }


    private void refreshlist(List<WlBlip> results) {
        getResults.clear();
        getResults.addAll(results);
        scanResultListAdapter.notifyDataSetChanged();

    }

    private OnItemClickListener itemlistener = new OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view,
                                final int position, long id) {
            // LayoutInflater inflater = LayoutInflater.from(getActivity());
            final WlBlip beaconSelected = getResults.get(position);

            beaconSelectedtmp = beaconSelected;
            // View dialog_layout = inflater.inflate(R.layout.dialog,
            // (ViewGroup) findViewById(R.id.dialog_root_layout));
            List<BeaconLocation> beacons = DataManager.getInstance().getBeaconsLocation();
            String titly = "Are you want to select this Beacon? ";
            for (BeaconLocation beaconL : beacons) {
                if (beaconSelected.BSSID.equals(beaconL.getId())) {
                    titly = "WARNING!!! \n Are you want to REPLACE this Beacon?";
                }
            }
            AlertDialog.Builder db = new AlertDialog.Builder(ctx);
            // db.setView(dialog_layout);
            db.setTitle(titly + beaconSelected.BSSID);
            db.setPositiveButton("CANCEL",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
            db.setNegativeButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    Log.d(TAG, "onClick: " + beaconSelected.BSSID);
                    currentBeacon.setId(beaconSelected.BSSID);
                    beaconlist.setVisibility(View.GONE);
                    back.setVisibility(View.GONE);
                    addDescription.setVisibility(View.VISIBLE);

                    if (currentBeacon instanceof ProximityBeaconLocation) {
                        createLevelslayout((ProximityBeaconLocation) currentBeacon);
                    } else {
                        ((LinearLayout) findViewById(R.id.levels_container)).setVisibility(View.GONE);
                    }
                }
            });

            AlertDialog dialog = db.show();

        }
    };

    @Override
    public void onBackPressed() {
        finish();
//		Intent myIntent = new Intent(mThis, MainActivity.class);
//		mThis.startActivity(myIntent);
    }
}
