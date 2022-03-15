package com.mlins.locator;


import android.graphics.PointF;

import com.mlins.utils.FacilityConf;
import com.mlins.utils.FacilityContainer;
import com.mlins.utils.MathUtils;
import com.mlins.utils.PropertyHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;

public class AssociativeData implements Comparable<AssociativeData> {
    private static List<Integer> topK = null;
    private static boolean isInitialized = false;
    public PointF point;
    public float[] vector;
    public float[] normalizedvector;
    private int Z;
    private float angle = -999;
    private Queue<LevelIndexObj> pQ = null;
//	public List<AssociativeData> areadata = new ArrayList<AssociativeData>();

//	public List<AssociativeData> getAreadata() {
//		return areadata;
//	}
//
//	public void setAreadata(List<AssociativeData> areadata) {
//		this.areadata = areadata;
//	}


    public AssociativeData(PointF pointF, float[] v) {
        setPoint(pointF);
        vector = v;

    }

    public AssociativeData() {
        super();
    }

    static public void clearTopK() {
        isInitialized = false;
    }

    public float distance(float[] other) {
        float result = 0;
        int len = Math.min(vector.length, other.length);
        for (int i = 0; i < len; i++) {
            result += Math.sqrt((other[i] - vector[i]) * (other[i] - vector[i]));
        }

        return result;

    }

    private void computeTopKlevels(float[] other, int k) {

        if (k <= 0 || other == null || other.length == 0) {
            return;
        }

        if (topK == null) {
            topK = new ArrayList<Integer>(other.length);
        }
        if (pQ == null) {
            pQ = new PriorityQueue<LevelIndexObj>(other.length);
        }

        topK.clear();
        pQ.clear();

        isInitialized = true;

        if (k >= other.length) { // if k chosen to be bigger than the other vector length
            for (int i = 0; i < other.length; i++) {
                topK.add(i);
            }
            return;
        }


        for (int i = 0; i < other.length; i++) {
            LevelIndexObj lIObj = new LevelIndexObj(i, other[i]);
            pQ.offer(lIObj);
        }
        for (int j = 0; j < k; j++) {
            if (!pQ.isEmpty()) {
                LevelIndexObj lIObj = pQ.poll();
                if (lIObj != null) {
                    topK.add(lIObj.indx);
                }
            }
        }
        //return topK;
    }

    public float normalDistance(float[] other, float[] otherregular) {
        //XXX FOR WIFI READS HIDDENS CHECKS
        String hiddenStatus = PropertyHolder.getInstance().getReadHiddensStatus();
        if (PropertyHolder.getInstance().isTestWifiReadingHiddens() &&
                hiddenStatus.equals(PropertyHolder.READ_HIDDENS_NO)) {
            return normalDistanceWithoutHiddens(other, otherregular);
        }

        float result = 0;

        FacilityConf facConf = FacilityContainer.getInstance().getCurrent();
        if (facConf == null) {
            return result;
        }


        int k = facConf.getTopKlevelsThr();

        if (k > 0 && !isInitialized) {
            computeTopKlevels(other, k);
        }

        float w = 1.0f;

        int len = Math.min(normalizedvector.length, other.length);
        for (int i = 0; i < len; i++) {

            if (k > 0) {
                if (topK != null && topK.contains(i)) {
                    w = MathUtils.WifiThreshold(other[i], i);
                } else {
                    w = 0.0f;
                }
            } else {
                w = MathUtils.WifiThreshold(other[i], i);
                //w = getWByIndex(i);
            }

            result += w * Math.sqrt((otherregular[i] - normalizedvector[i]) * (otherregular[i] - normalizedvector[i]));
            w = 1.0f;

        }

        return result;
    }


    private float normalDistanceWithoutHiddens(float[] other, float[] otherregular) {
        float w = 1.0f;
        float result = 0;
        int len = Math.min(normalizedvector.length, other.length);

        FacilityConf facConf = FacilityContainer.getInstance().getCurrent();

        if (facConf == null) {
            return result;
        }

        int k = facConf.getTopKlevelsThr();


        if (k > 0 && !isInitialized) {
            computeTopKlevels(other, k);
        }

        for (int i = 0; i < len; i++) {

            if (k > 0) {
                if (topK != null && topK.contains(i)) {
                    w = MathUtils.WifiThreshold(other[i], i);
                } else {
                    w = 0.0f;
                }
            } else {
                w = MathUtils.WifiThreshold(other[i], i);
            }

            if (AsociativeMemoryLocator.getInstance().isWifiHidden(i)) {
                w = 0.0f;
            }
            result += w * Math.sqrt((otherregular[i] - normalizedvector[i]) * (otherregular[i] - normalizedvector[i]));
            w = 1.0f;
        }

        return result;
    }


    private float getWByIndex(int i) {
        int result = 0;
        List<String> ssids = AsociativeMemoryLocator.getInstance().getSsidnames();
        if (ssids.get(i).toLowerCase().contains("mlins")) {
            result = 1;
        }
        return result;
    }

    public int getZ() {
        return Z;
    }

    public void setZ(int z) {
        Z = z;
    }


    public double getX() {
        return (getPoint() != null) ? getPoint().x : 0.0;
    }

    public double getY() {
        return (getPoint() != null) ? getPoint().y : 0.0;
    }

    @Override
    public int compareTo(AssociativeData another) {
        Double fstX = Double.valueOf(this.getX());
        Double secX = Double.valueOf(another.getX());

        return fstX.compareTo(secX);

    }

    public float getAngle() {
        return angle;
    }

    public void setAngle(float angle) {
//		this.angle = angle;
        this.angle = -999;
    }

    public PointF getPoint() {
        return point;
    }

    public void setPoint(PointF point) {
        this.point = point;
    }

//	public double areanormalDistance(double[] other)
//	{
//		double result = 0;
//		double closepointssum = 0;
//		double closepointsaverage = 0;
//		for (AssociativeData d : areadata) {
//			closepointssum += d.normalDistance(other);
//		}
//		closepointsaverage = closepointssum / areadata.size();
//		result = 
// + normalDistance(other);
//		
//		return result;
//	}

    private class LevelIndexObj implements Comparable<LevelIndexObj> {
        private Integer indx;
        private Float level;

        public LevelIndexObj(Integer indx, Float level) {
            super();
            this.indx = indx;
            this.level = level;
        }

        @Override
        public int compareTo(LevelIndexObj other) {
            return other.level.compareTo(level);
        }
    }
}
