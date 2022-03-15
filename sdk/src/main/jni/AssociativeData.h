/*
 * AssociativeData.h
 *
 *  Created on: 29 ���� 2013
 *      Author: Owner
 */

#ifndef ASSOCIATIVEDATA_H_
#define ASSOCIATIVEDATA_H_

#include <string>

using namespace std;

#include "Location.h"
#include <vector>
#include "LevelIndexObj.h"
#include <queue>

//#include "spatial/point_multiset.hpp" //XXX spatial added

class AssociativeData {

private:


public:
    Location point;
    vector<float> mvector;
    vector<float> normalizedvector;
    int Z;
    static vector<float> topK; //private static List<Integer> topK=null;
    static bool isInitialized; //private static boolean isInitialized = false;
    //private Queue<LevelIndexObj> pQ=null;

    AssociativeData();

    virtual ~AssociativeData();

    float normalDistance(vector<float> &other, vector<float> &otherregular);

    float normalDistance(vector<float> &other, vector<float> &otherregular,
                         float closeDevicesThreshold, float closeDeviceWeight, int kTopLevelThr);

    float normalDistance(vector<float> &other, vector<float> &otherregular,
                         float closeDevicesThreshold, float closeDeviceWeight, int kTopLevelThr,
                         int levelLowerBound, bool isFirstTime);

    static void clearTopK();

    void computeTopKlevels(vector<float> &other, int k);
//	//XXX spatial added
//	  int operator() (spatial::dimension_type dim) const
//	  {
//		switch(dim)
//		  {
//		  case 0: return point.x;
//		  case 1: return point.y;
//		  default: return -1;
//		  }
//	  };

};

#endif /* ASSOCIATIVEDATA_H_ */

//package com.mlins.locator;

//import android.graphics.PointF;
//
//import com.com.mlins.utils.MathUtils;
//
//public class AssociativeData {
//	public PointF point;
//	public float[] vector;
//	public float[] normalizedvector;
//	private int Z;
//
//
//
//
//	public AssociativeData(PointF pointF, float[] v) {
//		point = pointF;
//		vector = v;
//
//	}
//
//	public float distance(float[] other)
//	{
//		float result = 0;
//		int len = Math.min(vector.length, other.length);
//		for (int i = 0; i < len; i++) {
//			result += Math.sqrt((other[i]-vector[i])*(other[i]-vector[i]));
//		}
//
//		return result;
//
//	}
//
//	public float normalDistance(float[] other, float[] otherregular )
//	{
//		float w = 1.0f;
//		float result = 0;
//		int len = Math.min(normalizedvector.length, other.length);
//		for (int i = 0; i < len; i++) {
//			w = MathUtils.WifiThreshold(other[i], i);
//			result +=w *  Math.sqrt((otherregular[i]-normalizedvector[i])*(otherregular[i]-normalizedvector[i]));
//			w = 1.0f;
//		}
//		return result;
//	}
//
//	public int getZ() {
//		return Z;
//	}
//
//	public void setZ(int z) {
//		Z = z;
//	}
//
//
//	public double getX(){
//		return (point!=null)? point.x:0.0;
//	}
//
//	public double getY(){
//		return (point!=null)? point.y:0.0;
//	}
//}
