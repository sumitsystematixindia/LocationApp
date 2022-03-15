package com.mlins.utils.distance;

import com.mlins.utils.MathUtils;
import com.mlins.utils.PropertyHolder;

public class Distance {

    public static double toProjectUnits(double meters) {
        if(PropertyHolder.getInstance().isUseFeetForDistance() )
            meters = MathUtils.metersToFeet(meters);
        return meters;
    }

}
