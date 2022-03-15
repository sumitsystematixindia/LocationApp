package com.location.app.fragment.notifications;

import android.content.Context;

import static android.content.ContentValues.TAG;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.google.gson.Gson;
import com.location.app.R;
import com.location.app.activity.PlaceBeaconActivity;
import com.location.app.data.PlacementStateChecker;
import com.location.app.fragment.LoginAndSecurityFregment;
import com.location.app.fragment.PlaceBeacon.PlaceBeaconFregment;
import com.location.app.fragment.ScanBeaconFragment;
import com.location.app.model.Building;
import com.location.app.model.Datum;
import com.location.app.model.CampusDataResponce;
import com.location.app.model.Facility;
import com.location.app.model.Floor;
import com.location.app.utils.NetworkUtils;
import com.location.app.model.BuildingModel;
import com.mlins.res.setup.ConfigsUpdater;
import com.mlins.utils.PropertyHolder;
import com.spreo.enums.LoadStatus;
import com.spreo.enums.ResUpdateStatus;
import com.spreo.interfaces.ConfigsLoadListener;
import com.spreo.interfaces.ConfigsUpdaterListener;
import com.spreo.sdk.data.SpreoDataProvider;
import com.spreo.sdk.data.SpreoResourceConfigsUtils;

import java.util.ArrayList;
import java.util.List;


public class NotificationsFragment extends Fragment implements View.OnClickListener, ConfigsUpdaterListener, ConfigsLoadListener, CampusListPresenterViewModel.View {
    Fragment fragment = null;
    String tag = "";
    private NotificationsViewModel notificationsViewModel;
    CampusListPresenter campusListPresenter;
    List<Datum> campusDataList = new ArrayList<>();
    List<Facility> facilityDataList = new ArrayList<>();
    List<Building> buildingDataList = new ArrayList<>();
    ArrayList<Floor> floorDataList = new ArrayList<>();
    Button btn_load_floor_plan;
    Context context;
    Spinner spinnerCampusId,spinnerFacilityId,spinnerBuildingId,spinnerFloorId;
    int campusId,facilityId,buildingId,floorId;
    public int index = 0;
    public ArrayList<BuildingModel> buildingModelArrayList = new ArrayList<>();
    BuildingModel buildingModel = new BuildingModel();
    public ProgressDialog dialog;
    String floor_map_url = "";
    String floor_map_poi_url = "";


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        context = getActivity();
        campusListPresenter = new CampusListPresenter(this);
        notificationsViewModel =
                new ViewModelProvider(this).get(NotificationsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_place_beacon, container, false);
        btn_load_floor_plan = root.findViewById(R.id.btn_load_floor_plan);
        spinnerCampusId = root.findViewById(R.id.spinnerCampusId);
        spinnerFacilityId = root.findViewById(R.id.spinnerFacilityId);
        spinnerBuildingId = root.findViewById(R.id.spinnerBuildingId);
        spinnerFloorId = root.findViewById(R.id.spinnerFloorId);
        buildingModel.setBuilding_name("Systematix HO");
        buildingModel.setBuilding_key("40fffeef135f4a95a8f2063282074bb21609253548669876981078");
        buildingModelArrayList.add(buildingModel);
        spinnerCampusId.setOnItemSelectedListener(campusListener);
        spinnerFacilityId.setOnItemSelectedListener(facilityListener);
        spinnerBuildingId.setOnItemSelectedListener(buildingListener);
        spinnerFloorId.setOnItemSelectedListener(floorListener);
        btn_load_floor_plan.setOnClickListener(this);
        dialog = new ProgressDialog(getContext());
        dialog.setCancelable(false);
        SpreoResourceConfigsUtils.subscribeToResourceUpdateService(this);

        if (NetworkUtils.isNetworkAvailable(context)) {
            try {
                Log.d(TAG, "onCreateView: notification");
                campusListPresenter.requestCampusData(context);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(context, "network error", Toast.LENGTH_SHORT).show();
        }
        return root;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_load_floor_plan:

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    checkRequiredPermission();
                } else {
                    startInit();
                }


                break;
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void checkRequiredPermission() {
        String requiredpermission = Manifest.permission.WRITE_EXTERNAL_STORAGE;
        boolean hasPermission = (getContext().checkSelfPermission(requiredpermission) == PackageManager.PERMISSION_GRANTED);
        if (!hasPermission) {
            requestPermissions(new String[]{requiredpermission}, 1);
        } else {
            checkOptionalPermissions();
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void checkOptionalPermissions() {

        List<String> optionalPermissions = new ArrayList<String>();

        optionalPermissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        optionalPermissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
        optionalPermissions.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        optionalPermissions.add(Manifest.permission.CAMERA);

        List<String> forRequest = new ArrayList<String>();
        for (String o : optionalPermissions) {
            boolean hasPermission = (getContext().checkSelfPermission(o) == PackageManager.PERMISSION_GRANTED);
            if (!hasPermission) {
                forRequest.add(o);
            }
        }

        if (forRequest.isEmpty()) {
            startInit();
        } else {
            String arr[] = new String[forRequest.size()];
            String[] permissions = (String[]) forRequest.toArray(arr);
            requestPermissions(permissions, 2);
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                checkOptionalPermissions();
            }
        } else {
            startInit();
        }
    }

    private void startInit() {
        SpreoResourceConfigsUtils.unSubscribeFromResourceLoadService(this);
        SpreoResourceConfigsUtils.update(getContext());
        PropertyHolder.getInstance().setDevelopmentMode(true);

        Intent myIntent = new Intent(getContext(), PlaceBeaconActivity.class);
        Gson gson = new Gson();
        String jsonCars = gson.toJson(floorDataList);
        myIntent.putExtra("FloorDataList",jsonCars);
        myIntent.putExtra("floorMapUrl",floor_map_url);
        myIntent.putExtra("floorMapPoiUrl",floor_map_poi_url);
        startActivity(myIntent);
    }

    public void callFregment() {
//        fragment = new PlaceBeaconFregment();
//        tag = PlaceBeaconFregment.class.getName();
        fragment = new ScanBeaconFragment();
        tag = ScanBeaconFragment.class.getName();
        fragmentTransaction(fragment, tag);
    }

    private void fragmentTransaction(Fragment fragment, String tag) {

        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.addToBackStack(tag);
        fragmentTransaction.replace(R.id.place_frame, fragment, tag);
        fragmentTransaction.commit();

    }

    @Override
    public void showProgress() {
     //   Toast.makeText(context, "Campuslist not arrived 123", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void hideProgress() {

    }

    @Override
    public void onResponseFailure(Throwable throwable) {

      //  Toast.makeText(context, "campuslist not arrived 12345665", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void setCampusData(CampusDataResponce campusDataResponce) {
        Log.d(TAG, "setCampusData: "+campusDataResponce);
        ArrayAdapter<String> spinnerCampusAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, android.R.id.text1);
        spinnerCampusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        if (campusDataResponce.getData() != null && campusDataResponce.getData().size() > 0) {
            Log.d(TAG, "setCampusData: "+campusDataResponce.getData().size());
//            Datum campusData=new Datum();
//            campusData.setCampusName("Select Campus");
//            campusData.setId(0);
//            campusDataList.add(campusData);
//            spinnerCampusAdapter.add(campusDataList.get(0).getCampusName());
            campusDataList = campusDataResponce.getData();
            for (Datum campusdata : campusDataList) {

                spinnerCampusAdapter.add(campusdata.getCampusName());
            }
            spinnerCampusId.setAdapter(spinnerCampusAdapter);
            floor_map_url = campusDataList.get(0).getFloorUrl();
            floor_map_poi_url = campusDataList.get(0).getIconsRul();
          //  floorDataList=campusDataList.get(1).getFacilities().get(0).getBuildings().get(0).getFloors();
           // Log.d(TAG, "floorname: "+floorDataList.get(0).getFloorName());
        } else {
            Toast.makeText(getContext(), "Campus not arrived", Toast.LENGTH_SHORT).show();
        }


    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onPreConfigsLoad(LoadStatus loadStatus) {

    }

    @Override
    public void onPostConfigsLoad(LoadStatus status) {

        Log.d(TAG, "onPostConfigsLoad: " + status);
        if (status == LoadStatus.LOAD_FACILITY_SUCCES) {
            if (dialog != null) {
                dialog.hide();
            }
//            SpreoResourceConfigsUtils.unSubscribeFromResourceLoadService(this);
//            PropertyHolder.getInstance().setDevelopmentMode(true);
//            Intent myIntent = new Intent(getContext(), PlaceBeaconActivity.class);
//            startActivity(myIntent);

        }
    }

    @Override
    public void onPreConfigsDownload() {
//        try {
//            dialog.setTitle("Loading....");
//            dialog.show();
//        } catch (Exception e) {
//            // TODO: handle exception
//        }

    }

    @Override
    public void onPostConfigsDownload(ResUpdateStatus status) {
        if (status == ResUpdateStatus.OK) {
            SpreoResourceConfigsUtils.unSubscribeFromResourceUpdateService(this);
            List<String> campusesList = SpreoDataProvider.getCampusesList();
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
    private AdapterView.OnItemSelectedListener campusListener = new AdapterView.OnItemSelectedListener() {

        @Override
        public void onItemSelected(AdapterView<?> parent, View v, int pos,
                                   long id) {
            index = pos;
            // DataManager.getInstance().setProject_name(mProjectsList.get(pos).getName());
            campusId=campusDataList.get(pos).getId();
            Log.d(TAG, "campusId: "+campusId);
            ArrayAdapter<String> spinnerCampusAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, android.R.id.text1);
            spinnerCampusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
           // campusId=campusDataList.get(pos).getId();

//            Facility facilityData=new Facility();
//            facilityData.setFacilityName("Select Facility");
//            facilityData.setId(0);
//            facilityDataList.add(facilityData);
//            spinnerCampusAdapter.add(facilityDataList.get(0).getFacilityName());


            facilityDataList=campusDataList.get(pos).getFacilities();
            for (Facility facilitydata : facilityDataList) {
                spinnerCampusAdapter.addAll(facilitydata.getFacilityName());
            }
            spinnerFacilityId.setAdapter(spinnerCampusAdapter);
        }

        @Override
        public void onNothingSelected(AdapterView<?> arg0) {
            // TODO Auto-generated method stub

        }
    };
    private AdapterView.OnItemSelectedListener facilityListener = new AdapterView.OnItemSelectedListener() {

        @Override
        public void onItemSelected(AdapterView<?> parent, View v, int pos,
                                   long id) {
            index = pos;

           // facilityId=facilityDataList.get(pos).getCampusId();

            Log.d(TAG, "onItemSelected: "+buildingDataList.size());
            ArrayAdapter<String> spinnerCampusAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, android.R.id.text1);
            spinnerCampusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//            Building buildingData=new Building();
//            buildingData.setBuildingName("Select Building");
//            buildingData.setId(0);
//            buildingDataList.add(buildingData);
//            spinnerCampusAdapter.add(buildingDataList.get(0).getBuildingName());
            buildingDataList=facilityDataList.get(pos).getBuildings();
            for (Building buildingdata : buildingDataList) {
                spinnerCampusAdapter.addAll(buildingdata.getBuildingName());
            }
            spinnerBuildingId.setAdapter(spinnerCampusAdapter);
        }

        @Override
        public void onNothingSelected(AdapterView<?> arg0) {
            // TODO Auto-generated method stub

        }
    };
    private AdapterView.OnItemSelectedListener buildingListener = new AdapterView.OnItemSelectedListener() {

        @Override
        public void onItemSelected(AdapterView<?> parent, View v, int pos,
                                   long id) {
            index = pos;
            // DataManager.getInstance().setProject_name(mProjectsList.get(pos).getName());

//            buildingId=campusDataList.get(pos).getId();

            ArrayAdapter<String> spinnerCampusAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, android.R.id.text1);
            spinnerCampusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//            Floor selectFloor=new Floor();
//            selectFloor.setFloorName("Select Floor");
//            selectFloor.setId(0);
//            floorDataList.add(selectFloor);
//            spinnerCampusAdapter.add(floorDataList.get(0).getFloorName());
            floorDataList=buildingDataList.get(pos).getFloors();
         //   Log.d(TAG, "onItemSelected: "+floorDataList.size());
            for (Floor floordata : floorDataList) {
                spinnerCampusAdapter.add(floordata.getFloorName());
            }
            spinnerFloorId.setAdapter(spinnerCampusAdapter);
        }

        @Override
        public void onNothingSelected(AdapterView<?> arg0) {
            // TODO Auto-generated method stub

        }
    };
    private AdapterView.OnItemSelectedListener floorListener = new AdapterView.OnItemSelectedListener() {

        @Override
        public void onItemSelected(AdapterView<?> parent, View v, int pos,
                                   long id) {
            index = pos;
           // floorId=buildingDataList.get(pos).getId();

        }

        @Override
        public void onNothingSelected(AdapterView<?> arg0) {
            // TODO Auto-generated method stub

        }
    };
    @Override
    public void onPreConfigsInit() {

    }

    @Override
    public void onPostConfigsInit(ResUpdateStatus status) {

    }
}