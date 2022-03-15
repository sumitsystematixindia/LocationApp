package com.location.app.fragment;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.location.app.R;


public class PathDetailsFragment extends Fragment implements View.OnClickListener{
    public static final String TAG=PathDetailsFragment.class.getSimpleName();
    Context context;
    Fragment fragment = null;
    String tag = "";
    TextView tv_path_detail;
    public PathDetailsFragment() {
        // Required empty public constructor
    }

    public static final String ARG_SECTION_NUMBER = "section_number";
    // TODO: Rename and change types and number of parameters
    public static PathDetailsFragment newInstance(int number) {
        PathDetailsFragment fragment = new PathDetailsFragment();
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
        View view=inflater.inflate(R.layout.fragment_path_details, container, false);
        tv_path_detail=view.findViewById(R.id.tv_path_detail);
        tv_path_detail.setOnClickListener(this);
        return view;
    }

    public void callFragment() {
        fragment = new PathDetailLatLongFragment();
        tag = PathDetailLatLongFragment.class.getName();
        fragmentTransaction(fragment, tag);
    }
    private void fragmentTransaction(Fragment fragment, String tag) {

        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.addToBackStack(tag);
        fragmentTransaction.replace(R.id.frame, fragment, tag);
        fragmentTransaction.commit();

    }

    @Override
    public void onClick(View v) {
        callFragment();
        int fragments = getActivity().getSupportFragmentManager().getBackStackEntryCount();
        Log.d(TAG, "onClick: "+fragments);
    }
}