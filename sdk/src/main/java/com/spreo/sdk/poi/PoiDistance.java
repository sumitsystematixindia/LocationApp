package com.spreo.sdk.poi;


import androidx.annotation.NonNull;

import com.mlins.utils.PropertyHolder;
import com.spreo.nav.interfaces.IPoi;

public class PoiDistance implements Comparable<PoiDistance>{

	public final IPoi poi;

	private final double distance;

	public PoiDistance(IPoi poi, double distance) {
		this.poi = poi;
		this.distance = distance;
	}

	public double getDistance() {
		return distance;
	}

	@Override
	public String toString() {
		String units = PropertyHolder.getInstance().isUseFeetForDistance() ? "ft" : "m";
		return String.format("distance to %s: %.2f %s", poi.toString(), distance, units);
	}

	@Override
	public int compareTo(@NonNull PoiDistance o) {
		return (int) (distance - o.distance);
	}
}
