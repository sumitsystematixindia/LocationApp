package com.location.app.fragment.dashboard;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.maps.model.LatLng;
import com.location.app.R;
import com.location.app.activity.IntermittentActivity;
import com.location.app.activity.PlaceBeaconActivity;
import com.location.app.data.PlacementStateChecker;
import com.location.app.model.Floor;
import com.location.app.model.IndoorPathway;
import com.location.app.utils.Preferences;
import com.mlins.utils.PropertyHolder;
import com.mlins.utils.gis.GisData;
import com.mlins.utils.gis.GisLine;
import com.mlins.utils.gis.GisPoint;
import com.spreo.enums.LoadStatus;
import com.spreo.enums.ResUpdateStatus;
import com.spreo.interfaces.ConfigsLoadListener;
import com.spreo.interfaces.ConfigsUpdaterListener;
import com.spreo.sdk.data.SpreoDataProvider;
import com.spreo.sdk.data.SpreoResourceConfigsUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.List;

public class DashboardFragment extends Fragment implements View.OnClickListener, ConfigsUpdaterListener, ConfigsLoadListener{
    Fragment fragment = null;
    String tag = "";
    private DashboardViewModel dashboardViewModel;
    Button btn_continiue, btn_intermittent;
    public ProgressDialog dialog;
    List<Floor> floorDataList=new ArrayList<>();

    List<LatLng> latlonglist = new ArrayList<>();
    TextView txtBuilding,txtfloor,txtfacility,txtcampus;
    Context context;
    @SuppressLint("LongLogTag")
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        context = getActivity();
        dashboardViewModel =
                new ViewModelProvider(this).get(DashboardViewModel.class);
        View root = inflater.inflate(R.layout.fragment_traverse_path, container, false);
//        final TextView textView = root.findViewById(R.id.text_dashboard);
//        dashboardViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
//            @Override
//            public void onChanged(@Nullable String s) {
//                textView.setText(s);
//            }
//        }
        SpreoResourceConfigsUtils.subscribeToResourceUpdateService(this);
        btn_intermittent = root.findViewById(R.id.btn_intermittent);
        btn_continiue = root.findViewById(R.id.btn_continiue);
        txtBuilding = (TextView) root.findViewById(R.id.txtBuilding);
        txtfloor = (TextView) root.findViewById(R.id.txtfloor);
        txtfacility = (TextView) root.findViewById(R.id.txtfacility);
        txtcampus = (TextView) root.findViewById(R.id.txtcampus);
        btn_intermittent.setOnClickListener(this);
        btn_continiue.setOnClickListener(this);
        dialog = new ProgressDialog(getContext());
        dialog.setCancelable(false);
        floorDataList = Preferences.getFloorDataArrayList(context, Preferences.KEY_FLOOR_LIST);

            String campus = Preferences.getString1(context, Preferences.KEY_SELECTED_ITEM_CAMPUS_ID);
            if (campus != null) {
                txtcampus.setText(campus);
            } else {
                Toast.makeText(context, "Something Went Wrong", Toast.LENGTH_SHORT).show();
            }

            String facility = Preferences.getString2(context, Preferences.KEY_SELECTED_ITEM_FACILITY_ID);
            if (facility != null) {
                txtfacility.setText(facility);
            } else {
                Toast.makeText(context, "Something Went Wrong", Toast.LENGTH_SHORT).show();
            }

            String building = Preferences.getString3(context, Preferences.KEY_SELECTED_ITEM_BUILDING);
            if (building != null) {
                txtBuilding.setText(building);
            } else {
                Toast.makeText(context, "Something Went Wrong", Toast.LENGTH_SHORT).show();
            }

            String floor = Preferences.getString4(context, Preferences.KEY_SELECTED_ITEM_FLOOR);
            if (floor != null) {
                txtfloor.setText(floor);
            } else {
                Toast.makeText(context, "Something Went Wrong", Toast.LENGTH_SHORT).show();
            }
            return root;

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_intermittent:

                SpreoResourceConfigsUtils.unSubscribeFromResourceLoadService(this);
                PropertyHolder.getInstance().setDevelopmentMode(true);
                Intent myIntent = new Intent(getContext(), IntermittentActivity.class);
                myIntent.putExtra("PathType", "Intermittent");
                startActivity(myIntent);
//        ConfigsUpdater.getInstance().setReqApikey(buildingModelArrayList.get(index).building_key);
                SpreoResourceConfigsUtils.update(getContext());
//                Intent intent = new Intent(getActivity(), IntermittentActivity.class);
//                intent.putExtra("PathType", "Intermittent");
//                startActivity(intent);

                break;
            case R.id.btn_continiue:
                Intent intent1 = new Intent(getActivity(), IntermittentActivity.class);
                intent1.putExtra("PathType", "Continuous");
                startActivity(intent1);
                break;
        }
    }

    @Override
    public void onPreConfigsLoad(LoadStatus loadStatus) {

    }

    @Override
    public void onPostConfigsLoad(LoadStatus status) {

    }

    @Override
    public void onPreConfigsDownload() {

    }

    @Override
    public void onPostConfigsDownload(ResUpdateStatus status) {
        if (status == ResUpdateStatus.OK) {
            SpreoResourceConfigsUtils
                    .unSubscribeFromResourceUpdateService(this);
            List<String> campusesList = SpreoDataProvider.getCampusesList();
            for (int i =0 ; i < campusesList.size();i++){
                Log.d("CAMPUSDATALIST",campusesList.get(i).toString());
            }
            if (SpreoDataProvider.getCampusFacilities(campusesList.get(0)) != null) {
                Log.d(TAG, "onPostConfigsDownload: if");
                if (SpreoDataProvider.getCampusFacilities(campusesList.get(0))
                        .size() > 1) {

                    if (dialog != null) {
                        dialog.hide();
                    }

//                    Intent myIntent = new Intent(this, FacilitiesList.class);
//                    startActivity(myIntent);
//
//                    finish();
                } else {
                    Log.d(TAG, "onPostConfigsDownload: else");
                    SpreoResourceConfigsUtils
                            .subscribeToResourceLoadService(this);
                    List<String> mFacilitiesList = SpreoDataProvider
                            .getCampusFacilities(campusesList.get(0));
                    PlacementStateChecker.releaseInstance();
                    SpreoResourceConfigsUtils.loadFacility(campusesList.get(0),
                            mFacilitiesList.get(0));
                }

            }

        }
    }

    @Override
    public void onPreConfigsInit() {

    }

    @Override
    public void onPostConfigsInit(ResUpdateStatus status) {

    }
}