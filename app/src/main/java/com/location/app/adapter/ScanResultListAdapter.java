package com.location.app.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.location.app.R;
import com.mlins.wireless.WlBlip;
//import com.spreo.androidbeaconplacement.R;

import java.util.List;

public class ScanResultListAdapter extends BaseAdapter {

	List<WlBlip> wlbipArry;

	Context mContext;

	public ScanResultListAdapter(List<WlBlip> wlbipArry, Context context) {
		mContext=context;
		this.wlbipArry = wlbipArry;
	}

	@Override
	public int getCount() {
		return wlbipArry.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return wlbipArry.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		BeaconGroupsHolder holder = null;
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(R.layout.scan_result_list_row, null);
			holder = new BeaconGroupsHolder();
			holder.beaconId = (TextView) convertView.findViewById(R.id.tvBeaconId);
			holder.level = (TextView) convertView.findViewById(R.id.tvLevel);
			convertView.setTag(holder);

		} else {
			holder = (BeaconGroupsHolder) convertView.getTag();
		}
		WlBlip wlbip = wlbipArry.get(position);
		holder.beaconId.setText(wlbip.BSSID);
		String lavel="-127";
		holder.level.setTextColor(Color.RED);
		if(wlbip.level!=-999){
			lavel = String.valueOf(wlbip.level);
			holder.level.setTextColor(Color.GREEN);

		}
		holder.level.setText(lavel);
		return convertView;
	}

	public static class BeaconGroupsHolder {
		TextView beaconId;
		TextView level;
	}
}
