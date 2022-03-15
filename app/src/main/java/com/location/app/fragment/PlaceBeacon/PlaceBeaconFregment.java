package com.location.app.fragment.PlaceBeacon;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.location.app.R;
import com.location.app.fragment.ScanBeaconFragment;


public class PlaceBeaconFregment extends Fragment implements View.OnClickListener{
    public static final String TAG = PlaceBeaconFregment.class.getSimpleName();
    Context context;
    ImageView iv_information,iv_scan_beacon_map;
    Fragment fragment = null;
    String tag = "";

    public static final String ARG_SECTION_NUMBER = "section_number";

    public PlaceBeaconFregment() {
        // Required empty public constructor
    }


    public static PlaceBeaconFregment newInstance(int number) {
        PlaceBeaconFregment fragment = new PlaceBeaconFregment();
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_SECTION_NUMBER, number);
        fragment.setArguments(bundle);


        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            context = getActivity();
            int number = 1;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_place_beacon_fregment, container, false);
        context = getActivity();
        callView(view);



     /*   iv_information.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: ");
            }
        });
*/
        return view;
    }



    public void callView(View view) {
        iv_scan_beacon_map = view.findViewById(R.id.iv_scan_beacon_map);
        iv_information = view.findViewById(R.id.iv_information);
        iv_scan_beacon_map.setOnClickListener(this);
         iv_information.setOnClickListener(this);

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_information:
                Log.d(TAG, "onClick: sumit");
                callFregment();
                break;
            case R.id.iv_scan_beacon_map:
                Log.d(TAG, "onClick: test");
                break;
        }
    }

    public void callFregment() {
        fragment = new ScanBeaconFragment();
        tag = ScanBeaconFragment.class.getName();
        fragmentTransaction(fragment, tag);
    }

    private void fragmentTransaction(Fragment fragment, String tag) {

        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.addToBackStack(tag);
        fragmentTransaction.add(R.id.place_frame, fragment, tag);
        fragmentTransaction.commit();

    }





}