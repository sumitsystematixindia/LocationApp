package com.location.app.fragment;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.location.app.R;
import com.location.app.adapter.TechniqueAdapter;
import com.location.app.model.ModelTechnique;

import java.util.ArrayList;
import java.util.List;

import static com.location.app.activity.SettingActivity.TAG;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TechniqueFregment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TechniqueFregment extends Fragment {
    public RecyclerView rv_list;
    List<ModelTechnique> techniqueList = new ArrayList<>();
    Context context;

    public TechniqueFregment() {
        // Required empty public constructor
    }


    public static final String ARG_SECTION_NUMBER = "section_number";

    public static TechniqueFregment newInstance(int number) {
        TechniqueFregment fragment = new TechniqueFregment();
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_SECTION_NUMBER, number);
        fragment.setArguments(bundle);
        return fragment;
//        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
//        fragment.setArguments(args);
//        return fragment;
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
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_fregment_tchnique, container, false);
        context = getActivity();
        callView(view);

        return view;
    }

    public void callView(View view) {
        rv_list = view.findViewById(R.id.rv_list);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, true);
        //  rv_list.setLayoutManager(new LinearLayoutManager(getActivity()));
        rv_list.setLayoutManager(layoutManager);
        methodMyPath();
    }

    private void methodMyPath() {

        for (int i = 0; i < 10; i++) {
            ModelTechnique modelTechnique = new ModelTechnique();
            modelTechnique.setTechnique_id("" + i);
            modelTechnique.setGetTechnique_name("Technique Name " + i);
            techniqueList.add(modelTechnique);
        }

        TechniqueAdapter adapter = new TechniqueAdapter(techniqueList, getActivity());
        rv_list.setAdapter(adapter);
        Log.d(TAG, "methodMyPath: " + techniqueList.size());
    }
}