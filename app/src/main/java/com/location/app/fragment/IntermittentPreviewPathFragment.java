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

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;
import android.widget.Toast;

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

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link IntermittentPreviewPathFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class IntermittentPreviewPathFragment extends Fragment {
    public static final String TAG = IntermittentPreviewPathFragment.class.getSimpleName();
    Context context;
    RelativeLayout preview_path;


    public IntermittentPreviewPathFragment() {
        // Required empty public constructor
    }

    public static final String ARG_SECTION_NUMBER = "section_number";
    public static IntermittentPreviewPathFragment newInstance(int number) {
        IntermittentPreviewPathFragment fragment = new IntermittentPreviewPathFragment();
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
        View view=inflater.inflate(R.layout.fragment_intermittent_preview_path, container, false);
                preview_path=view.findViewById(R.id.preview_path);

        getBundleData();
        return view;
    }


    void getBundleData() {
        if (getArguments() != null) {
            String path_type = getArguments().getString("PathType");
//            if (path_type.equalsIgnoreCase("Intermittent"))
//                preview_path.setBackgroundResource(R.drawable.preview_path);
//            else
//                preview_path.setBackgroundResource(R.drawable.preview_continuous);
//          //  preview_path.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.preview_continuous));

        }
    }
}