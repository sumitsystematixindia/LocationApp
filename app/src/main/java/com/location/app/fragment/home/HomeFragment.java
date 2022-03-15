package com.location.app.fragment.home;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.location.app.R;
import com.location.app.fragment.notifications.CampusListPresenter;
import com.location.app.fragment.notifications.CampusListPresenterViewModel;
import com.location.app.model.Building;
import com.location.app.model.CampusDataResponce;
import com.location.app.model.Datum;
import com.location.app.model.Facility;
import com.location.app.model.Floor;
import com.location.app.model.IndoorPathway;
import com.location.app.utils.NetworkUtils;

import java.util.ArrayList;
import java.util.List;
import com.location.app.utils.Preferences;

public class HomeFragment extends Fragment implements View.OnClickListener, CampusListPresenterViewModel.View {

    private HomeViewModel homeViewModel;
    CampusListPresenter campusListPresenter;
    Spinner spinnerCampusId,spinnerFacilityId,spinnerBuildingId,spinnerFloorId;
    //Spinner spinnerCampusBuilding, spinnerCampusFloor, spinnerCampusFacility, spinnerCampusName;
    Context context;
   // ArrayList<Floor> floorList = new ArrayList<>();
   // ArrayList<Floor> floorDataList = new ArrayList<>();
   // ArrayList<Datum> campusDatalist = new ArrayList<>();
    //ArrayList<Facility> facilityArrayList = new ArrayList<>();
   // ArrayList<Building> buildingDataList = new ArrayList<>();

    List<Datum> campusDataList = new ArrayList<>();
    List<Facility> facilityDataList = new ArrayList<>();
    List<Building> buildingDataList = new ArrayList<>();
    List<Floor> floorDataList = new ArrayList<>();
    ArrayList<IndoorPathway> indoorPathways = new ArrayList<>();

    int campusId, facilityId, buildingId, floorId;
    public int index = 0;
    String floor_map_url = "";
    String floor_map_poi_url = "";


    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);


        context = getActivity();
        campusListPresenter = new CampusListPresenter(this);

        spinnerCampusId = root.findViewById(R.id.spinnerCampusName);
        spinnerFacilityId = root.findViewById(R.id.spinnerCampusFacility);
        spinnerBuildingId = root.findViewById(R.id.spinnerCampusBuilding);
        spinnerFloorId = root.findViewById(R.id.spinnerCampusFloor);
        spinnerCampusId.setOnItemSelectedListener(campusListener);
        spinnerFacilityId.setOnItemSelectedListener(facilityListener);
        spinnerBuildingId.setOnItemSelectedListener(buildingListener);
        spinnerFloorId.setOnItemSelectedListener(floorListener);



        if (NetworkUtils.isNetworkAvailable(context)) {
            try {
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
    public void setCampusData(CampusDataResponce campusDataResponce) {

        if (campusDataResponce.getData() != null && campusDataResponce.getData().size() > 0) {
            campusDataList = campusDataResponce.getData();
            for (int i=0;i<campusDataList.size();i++) {
                floor_map_url = campusDataList.get(i).getFloorUrl();
                floor_map_poi_url = campusDataList.get(i).getIconsRul();
                facilityDataList = campusDataList.get(i).getFacilities();
            }
            for (int i=0;i<facilityDataList.size();i++) {
                buildingDataList.addAll(facilityDataList.get(i).getBuildings());
            }
            floorDataList = buildingDataList.get(0).getFloors();
          //  Log.d(TAG, "setCampusData: Data" + floorDataList.get(1).getIndoorPathways().get(0).getLatLons());

            Preferences.saveString5(context,Preferences.KEY_FLOOR_URL,floor_map_url);
            Preferences.saveString6(context,Preferences.KEY_FLOOR_URL_POI,floor_map_poi_url);
            Preferences.saveFloorDataArrayList(context,Preferences.KEY_FLOOR_LIST,floorDataList);

            ArrayAdapter<String> spinnerCampusAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, android.R.id.text1);
            spinnerCampusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            for (Datum campusdata : campusDataList) {
                spinnerCampusAdapter.addAll(campusdata.getCampusName());
            }
            spinnerCampusId.setAdapter(spinnerCampusAdapter);

            floorDataList=campusDataList.get(1).getFacilities().get(0).getBuildings().get(0).getFloors();
        } else {
            Toast.makeText(getContext(), "Campus not arrived", Toast.LENGTH_SHORT).show();
        }

    }

    private AdapterView.OnItemSelectedListener campusListener = new AdapterView.OnItemSelectedListener() {

        @Override
        public void onItemSelected(AdapterView<?> parent, View v, int pos,
                                   long id) {
            index = pos;
            String selectedItem = spinnerCampusId.getSelectedItem().toString();
            // DataManager.getInstance().setProject_name(mProjectsList.get(pos).getName());
            campusId=campusDataList.get(pos).getId();
            facilityDataList=campusDataList.get(pos).getFacilities();
            Log.d(TAG, "size: "+facilityDataList.size());
            ArrayAdapter<String> spinnerCampusAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, android.R.id.text1);
            spinnerCampusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            for (Facility facilitydata : facilityDataList){
                spinnerCampusAdapter.addAll(facilitydata.getFacilityName());
            }
            spinnerFacilityId.setAdapter(spinnerCampusAdapter);
            Preferences.saveString1(context,Preferences.KEY_SELECTED_ITEM_CAMPUS_ID, selectedItem);
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

            facilityId=facilityDataList.get(pos).getCampusId();
            buildingDataList=facilityDataList.get(pos).getBuildings();
            Log.d(TAG, "onItemSelected: "+buildingDataList.size());
            ArrayAdapter<String> spinnerCampusAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, android.R.id.text1);
            spinnerCampusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            for (Building buildingdata : buildingDataList) {
                spinnerCampusAdapter.addAll(buildingdata.getBuildingName());
            }
            spinnerBuildingId.setAdapter(spinnerCampusAdapter);
            String selectedItem = spinnerFacilityId.getSelectedItem().toString();

            Preferences.saveString2(context,Preferences.KEY_SELECTED_ITEM_FACILITY_ID, selectedItem);
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
            floorDataList=buildingDataList.get(pos).getFloors();
            Log.d(TAG, "onItemSelected: "+floorDataList.size());
            ArrayAdapter<String> spinnerCampusAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, android.R.id.text1);
            spinnerCampusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            for (Floor floordata : floorDataList) {
                spinnerCampusAdapter.addAll(floordata.getFloorName());
            }
            spinnerFloorId.setAdapter(spinnerCampusAdapter);
            String selectedItem = spinnerBuildingId.getSelectedItem().toString();
            Preferences.saveString3(context,Preferences.KEY_SELECTED_ITEM_BUILDING, selectedItem);
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
            String selectedItem = spinnerFloorId.getSelectedItem().toString();
            Preferences.saveString4(context,Preferences.KEY_SELECTED_ITEM_FLOOR, selectedItem);
        }

        @Override
        public void onNothingSelected(AdapterView<?> arg0) {
            // TODO Auto-generated method stub

        }
    };

}
