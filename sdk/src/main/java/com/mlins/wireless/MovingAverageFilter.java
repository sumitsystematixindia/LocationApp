package com.mlins.wireless;

import com.mlins.wireless.WlScannerImpl.ResultsFilter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MovingAverageFilter implements ResultsFilter {

    private final int mSize;
    private Map<String, AvarageData> mAvs = new HashMap<String, AvarageData>();
    private AvarageData avi;

    public MovingAverageFilter(int size) {
        mSize = size;
    }

    @Override
    public void filter(List<WlBlip> detects) {
        boolean full = true;
        for (WlBlip b : detects) {
            if (!mAvs.containsKey(b.BSSID)) {
                mAvs.put(b.BSSID, new AvarageData(mSize));
            }
            avi = mAvs.get(b.BSSID);
            if (avi.mFull) {
                b.level = avi.average(b.level);
            } else {
                full = false;
            }
            if (!full) {
                detects = null;
            }
        }
    }

    private class AvarageData {
        boolean mFull;
        int mIndex;
        int[] mLevels;
        double mAccumulator;

        public AvarageData(int size) {
            mLevels = new int[size];
        }

        public int average(int value) {
            if (!mFull && mIndex == mLevels.length - 1)
                mFull = true;
            int denominator = mFull ? mLevels.length : mIndex + 1;
            int j = mIndex;
            int k = (j + 1) % mSize;
            mLevels[j] = value;
            mAccumulator += mLevels[j] - mLevels[k];
            mIndex = k;
            return (int) (mAccumulator / denominator);
        }
    }

}
