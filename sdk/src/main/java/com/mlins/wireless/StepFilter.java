package com.mlins.wireless;

import com.mlins.wireless.WlScannerImpl.ResultsFilter;

import java.util.List;

public class StepFilter implements ResultsFilter {
    public final static int STEPS = 8;
    public final static int BOTTOM = 20;
    public final static int TOP = 100;

    private static final float RISER = (TOP - BOTTOM) / STEPS;

    @Override
    public void filter(List<WlBlip> detects) {
        for (WlBlip b : detects) {
            float z = Math.round(((float) b.level - BOTTOM) / RISER) * RISER + BOTTOM;
            b.level = (int) z;
        }
    }

}
