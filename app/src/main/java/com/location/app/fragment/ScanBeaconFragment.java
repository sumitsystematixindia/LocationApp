package com.location.app.fragment;

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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.location.app.R;
import com.location.app.adapter.ScanBeaconAdapter;
import com.location.app.model.ModelScamBeacon;

import java.util.ArrayList;
import java.util.List;


public class ScanBeaconFragment extends Fragment implements View.OnClickListener {
    public static final String TAG = ScanBeaconFragment.class.getSimpleName();
    Context context;
    public RecyclerView rv_list;
    List<ModelScamBeacon> scamBeaconList = new ArrayList<>();

    ImageView iv_information;
    ImageView img_back;
    Fragment fragment = null;
    String tag = "";
    public static final String ARG_SECTION_NUMBER = "section_number";

    public ScanBeaconFragment() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static ScanBeaconFragment newInstance(int number) {
        ScanBeaconFragment fragment = new ScanBeaconFragment();
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
        View view = inflater.inflate(R.layout.fragment_scan_beacon, container, false);
        img_back=view.findViewById(R.id.img_back);
        callView(view);
        return view;
    }

    public void callView(View view) {
        rv_list = view.findViewById(R.id.rv_list);
        img_back.setOnClickListener(this);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, true);
        //  rv_list.setLayoutManager(new LinearLayoutManager(getActivity()));
        rv_list.setLayoutManager(layoutManager);
        methodMyPath();
    }

  

    public void callbeaconDetails() {
        fragment = new PlaceBeaconDetailFragment();
        tag = PlaceBeaconDetailFragment.class.getName();
        fragmentTransaction(fragment, tag);
    }

    private void fragmentTransaction(Fragment fragment, String tag) {

        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.addToBackStack(tag);
        fragmentTransaction.replace(R.id.place_frame, fragment, tag);
        fragmentTransaction.commit();

    }

    private void methodMyPath() {
        ModelScamBeacon modelTechnique0 = new ModelScamBeacon();
        modelTechnique0.setScan_beacon_id("00Xr");
        modelTechnique0.setScan_beacon_product("Smart Beacon SB16-2");
        modelTechnique0.setScan_beacon_battery("100%" );
        modelTechnique0.setScan_beacon_interval("350" );
        modelTechnique0.setScan_beacon_order_id("vLQqYd" );
        scamBeaconList.add(modelTechnique0);
        ModelScamBeacon modelTechnique1 = new ModelScamBeacon();
        modelTechnique1.setScan_beacon_id("05QY");
        modelTechnique1.setScan_beacon_product("Smart Beacon SB16-2" );
        modelTechnique1.setScan_beacon_battery("100%" );
        modelTechnique1.setScan_beacon_interval("300" );
        modelTechnique1.setScan_beacon_order_id("vLQqYd" );
        scamBeaconList.add(modelTechnique1);
        ModelScamBeacon modelTechnique2 = new ModelScamBeacon();
        modelTechnique2.setScan_beacon_id("066W");
        modelTechnique2.setScan_beacon_product("Smart Beacon SB16-2" );
        modelTechnique2.setScan_beacon_battery("100%" );
        modelTechnique2.setScan_beacon_interval("100" );
        modelTechnique2.setScan_beacon_order_id("vLQqYd" );
        scamBeaconList.add(modelTechnique2);
        ModelScamBeacon modelTechnique3 = new ModelScamBeacon();
        modelTechnique3.setScan_beacon_id("073b");
        modelTechnique3.setScan_beacon_product("Smart Beacon SB16-2" );
        modelTechnique3.setScan_beacon_battery("100%" );
        modelTechnique3.setScan_beacon_interval("350" );
        modelTechnique3.setScan_beacon_order_id("vLQqYd" );
        scamBeaconList.add(modelTechnique3);
        ModelScamBeacon modelTechnique4 = new ModelScamBeacon();
        modelTechnique4.setScan_beacon_id("09DI");
        modelTechnique4.setScan_beacon_product("Smart Beacon SB16-2" );
        modelTechnique4.setScan_beacon_battery("100%" );
        modelTechnique4.setScan_beacon_interval("100" );
        modelTechnique4.setScan_beacon_order_id("vLQqYd" );
        scamBeaconList.add(modelTechnique4);


        ScanBeaconAdapter adapter = new ScanBeaconAdapter(scamBeaconList, getActivity(),ScanBeaconFragment.this);
        rv_list.setAdapter(adapter);
        Log.d(TAG, "methodMyPath: " + scamBeaconList.size());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_back:
                int fragments = getActivity().getSupportFragmentManager().getBackStackEntryCount();
                if (fragments == 0) {
                    getFragmentManager().popBackStack();
                }
                else if (getFragmentManager().getBackStackEntryCount() > 0) {
                    getFragmentManager().popBackStack();
                } else {
                    super.getActivity().onBackPressed();
                }
                break;
        }
    }
}