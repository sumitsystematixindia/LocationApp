package com.mlins.wireless;

import com.mlins.wireless.WlScannerImpl.ResultsFilter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LowPassFilter implements ResultsFilter {

    float mAlfa, mBeta;
    Map<String, Double> mLevels = new HashMap<String, Double>();

    /**
     * @param alfa specifies the coefficient for the filter, must be 0 <= alfa <= 1,
     *             typical values about 0.85, the higher the value the slower the response.
     */
    public LowPassFilter(float alfa) {
//		Assert 0 <= alfa <= 1 ?? 
        mAlfa = alfa;
        mBeta = 1 - alfa;
    }

    @Override
    public void filter(List<WlBlip> detects) {
        for (WlBlip b : detects) {
            if (!mLevels.containsKey(b.BSSID)) {
                mLevels.put(b.BSSID, Double.valueOf(b.level));
            }
            Double l = mLevels.get(b.BSSID);
            l = mAlfa * l + mBeta * b.level;
            b.level = l.intValue();
        }
    }

}
