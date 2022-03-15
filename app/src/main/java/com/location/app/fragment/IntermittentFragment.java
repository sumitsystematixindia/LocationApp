package com.location.app.fragment;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.github.twocoffeesoneteam.glidetovectoryou.GlideToVectorYou;
import com.google.android.gms.maps.MapsInitializer;
import com.location.app.R;
import com.location.app.activity.addbeacon.SelectBeacon;
import com.location.app.data.DataManager;
import com.location.app.model.BeaconLocation;
import com.location.app.model.Floor;
import com.location.app.utils.Preferences;
import com.mlins.utils.FacilityConf;
import com.mlins.utils.FacilityContainer;
import com.mlins.utils.FloorData;
import com.mlins.utils.GisDrawHelper;
import com.mlins.utils.PoiDataHelper;
import com.mlins.utils.PropertyHolder;
import com.mlins.utils.gis.GisData;
import com.mlins.views.TouchImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class IntermittentFragment extends Fragment implements View.OnClickListener {
    public static final String TAG = IntermittentFragment.class.getSimpleName();
    Context context;
    Fragment fragment = null;
    String tag = "";
    String path_type="";
    TextView tv_path_text_name;
    ImageView iv_stop;
    private TouchImageView mPlanView;
    private List<FloorData> currentMapData=null;
    NumberPicker numberPicker;
    private ArrayList<BeaconLocation> beaconsLocation = new ArrayList<BeaconLocation>();
    FacilityConf facilityConf;
    List<Floor> floorDataList=new ArrayList<>();
    String floor_map_url = "";
    String floor_map_poi_url = "";
    public static final String ARG_SECTION_NUMBER = "section_number";


    public IntermittentFragment() {
        // Required empty public constructor
    }

    public static IntermittentFragment newInstance(int number) {
        IntermittentFragment fragment = new IntermittentFragment();
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

        View view = inflater.inflate(R.layout.fragment_intermittent, container, false);
        tv_path_text_name = view.findViewById(R.id.tv_path_text_name);
        iv_stop = view.findViewById(R.id.iv_stop);
        iv_stop.setOnClickListener(this);

        return view;

    }

    public void callbeaconDetails() {
        fragment = new IntermittentPreviewPathFragment();
        Bundle bundle = new Bundle();
        bundle.putString("PathType", path_type);
        System.out.println(bundle.toString());
        fragment.setArguments(bundle);
        tag = IntermittentPreviewPathFragment.class.getName();
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
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_stop:
                callbeaconDetails();
                break;
        }
    }
}