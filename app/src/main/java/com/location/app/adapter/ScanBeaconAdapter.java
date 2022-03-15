package com.location.app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.location.app.R;
import com.location.app.fragment.ScanBeaconFragment;
import com.location.app.model.ModelScamBeacon;

import java.util.ArrayList;
import java.util.List;

public class ScanBeaconAdapter extends RecyclerView.Adapter<ScanBeaconAdapter.ViewHolder> {
    List<ModelScamBeacon> scamBeaconList = new ArrayList<>();
    Context context;
    ScanBeaconFragment fragment;

    public ScanBeaconAdapter(List<ModelScamBeacon> scamBeaconList, Context context, ScanBeaconFragment fragment) {
        this.scamBeaconList = scamBeaconList;
        this.context = context;
        this.fragment=fragment;
    }

    @NonNull
    @Override
    public ScanBeaconAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_scan_beacon, parent, false);
        return new ScanBeaconAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ScanBeaconAdapter.ViewHolder holder, int position) {
        holder.tv_scan_beacon.setText(scamBeaconList.get(position).scan_beacon_id);
        holder.ll_beacondetals.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragment.callbeaconDetails();
            }
        });
    }

    @Override
    public int getItemCount() {
        return scamBeaconList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tv_scan_beacon;
        LinearLayout ll_beacondetals;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_scan_beacon = itemView.findViewById(R.id.tv_scan_beacon);
            ll_beacondetals = itemView.findViewById(R.id.ll_beacondetals);

        }
    }
}
