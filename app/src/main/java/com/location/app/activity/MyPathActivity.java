package com.location.app.activity;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.location.app.R;
import com.location.app.adapter.MyPathAdapter;
import com.location.app.fragment.PathDetailsFragment;
import com.location.app.model.ModelMyPath;

import java.util.ArrayList;

public class MyPathActivity extends AppCompatActivity implements View.OnClickListener {
    public static final String TAG = MyPathActivity.class.getSimpleName();
    ArrayList<ModelMyPath> myPathList = new ArrayList<>();
    Context context;
    RecyclerView rv_list;
    Fragment fragment;
    String tag = "";
    ImageView img_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_path);
        rv_list = findViewById(R.id.rv_list);
        img_back = findViewById(R.id.img_back);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, true
        );
        rv_list.setLayoutManager(new LinearLayoutManager(this));
        rv_list.setLayoutManager(layoutManager);
        img_back.setOnClickListener(this);
        methodMyPath();
    }

    private void methodMyPath() {

        for (int i = 0; i < 10; i++) {
            ModelMyPath modelMyPath = new ModelMyPath();
            modelMyPath.setSource_name("Indore");
            modelMyPath.setDestination_name("Rajwada");
            modelMyPath.setDate_time("Thu, April 9 2021,12:30:15 PM");
            myPathList.add(modelMyPath);
        }

        MyPathAdapter adapter = new MyPathAdapter(myPathList, context, MyPathActivity.this);
        rv_list.setAdapter(adapter);
        Log.d(TAG, "methodMyPath: " + myPathList.size());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_back:
                int fragments = getSupportFragmentManager().getBackStackEntryCount();

                if (fragments == 0) {
                    finish();
                } else if (getFragmentManager().getBackStackEntryCount() > 0) {
                    getFragmentManager().popBackStack();
                } else {
                    super.onBackPressed();
                }
                break;
        }
    }


    public void callFragment() {
        fragment = new PathDetailsFragment();
        tag = PathDetailsFragment.class.getName();
        fragmentTransaction(fragment, tag);
    }

    private void fragmentTransaction(Fragment fragment, String tag) {

        FragmentManager fragmentManager = this.getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.addToBackStack(tag);
        fragmentTransaction.replace(R.id.frame, fragment, tag);
        fragmentTransaction.commit();

    }
}