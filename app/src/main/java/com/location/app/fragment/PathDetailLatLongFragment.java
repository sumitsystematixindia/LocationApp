package com.location.app.fragment;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.location.app.R;

public class PathDetailLatLongFragment extends Fragment implements View.OnClickListener {
    public static final String TAG = PathDetailLatLongFragment.class.getSimpleName();
    Context context;
    Fragment fragment = null;
    String tag = "";
    TextView tv_path_text_name;
    ImageView img_back;
    public static final String ARG_SECTION_NUMBER = "section_number";

    public PathDetailLatLongFragment() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static PathDetailLatLongFragment newInstance(int number) {
        PathDetailLatLongFragment fragment = new PathDetailLatLongFragment();
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
        View view = inflater.inflate(R.layout.fragment_path_detail_lat_long, container, false);

        img_back = view.findViewById(R.id.img_back);
        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_back:
                int fragments = getActivity().getSupportFragmentManager().getBackStackEntryCount();
                Log.d(TAG, "onClick: " + fragments);
//                if (fragments == 0)
//                    getFragmentManager().popBackStack();
//                else if (getFragmentManager().getBackStackEntryCount() > 0) {
//                    getFragmentManager().popBackStack();
//                } else {
//                    super.getActivity().onBackPressed();
//                }
                break;
        }
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
}