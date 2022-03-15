package com.location.app.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.location.app.R;


public class PlaceBeaconDetailFragment extends Fragment implements View.OnClickListener {
    Context context;
    Fragment fragment = null;
    String tag = "";
    public static final String ARG_SECTION_NUMBER = "section_number";
    ImageView img_back;

    public PlaceBeaconDetailFragment() {
        // Required empty public constructor
    }


    public static PlaceBeaconDetailFragment newInstance(int number) {
        PlaceBeaconDetailFragment fragment = new PlaceBeaconDetailFragment();
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
        View view = inflater.inflate(R.layout.fragment_place_beacon_detail, container, false);
        img_back = view.findViewById(R.id.img_back);
        img_back.setOnClickListener(this);
        return view;
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
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_back:
                int fragments = getActivity().getSupportFragmentManager().getBackStackEntryCount();
                if (fragments == 0) {
                    getFragmentManager().popBackStack();
                } else if (getFragmentManager().getBackStackEntryCount() > 0) {
                    getFragmentManager().popBackStack();
                } else {
                    super.getActivity().onBackPressed();
                }
                break;
        }
    }
}