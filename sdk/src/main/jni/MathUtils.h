/*
 * MathUtils.h
 *
 *  Created on: 1 ���� 2013
 *      Author: Owner
 */

#ifndef MATHUTILS_H_
#define MATHUTILS_H_

#include <vector>
#include <list>
#include <string>
#include "PointF.h"
#include "GisLine.h"
#include <time.h>
#include <stdlib.h>

using namespace std;

class MathUtils {

private:


public:
    MathUtils();

    virtual ~MathUtils();

    static double getRandom() {
        //srand((unsigned) time(NULL));
        double randnum = (double) rand() / (double) RAND_MAX;
        return randnum;
    }

    static vector<float> normalizeVector(vector<float> v, vector<float> min, vector<float> max);

    static float WifiThreshold(float v, int index);

    static float WifiThreshold(float v, int index, float closeDevicesThreshold,
                               float closeDeviceWeight);


    static void findClosestPointOnSegment(const PointF &p, const GisLine &l, PointF &closestPoint) {
        closestPoint = PointF((float) -1, (float) -1);

        double x1 = l.getPoint1().getX();
        double y1 = l.getPoint1().getY();
        double x2 = l.getPoint2().getX();
        double y2 = l.getPoint2().getY();
        double px = p.x;
        double py = p.y;
        double xDelta = x2 - x1;
        double yDelta = y2 - y1;

        if ((xDelta == 0) && (yDelta == 0)) {
            // printf("Segment start equals segment end");
            return;
        }

        double u = ((px - x1) * xDelta + (py - y1) * yDelta)
                   / (xDelta * xDelta + yDelta * yDelta);

        if (u < 0) {
            closestPoint = PointF((float) x1, (float) y1);
        } else if (u > 1) {
            closestPoint = PointF((float) x2, (float) y2);
        } else {
            closestPoint = PointF((float) x1 + u * xDelta,
                                  (float) y1 + u * yDelta);
        }

        return;
    }

    static double distance(const PointF &p, const PointF &p1) {

        return sqrt((p.x - p1.x) * (p.x - p1.x) + (p.y - p1.y)
                                                  * (p.y - p1.y));
    }


    static double distance(double p1x, double p1y, double p2x, double p2y) {

        return sqrt((p1x - p2x) * (p1x - p2x) + (p1y - p2y) * (p1y - p2y));
    }

    static double distance(double p1x, double p1y, double p1z, double p2x, double p2y, double p2z) {

        return sqrt(
                (p1x - p2x) * (p1x - p2x) + (p1y - p2y) * (p1y - p2y) + (p1z - p2z) * (p1z - p2z));
    }

};

#endif /* MATHUTILS_H_ */



//package com.com.mlins.utils;
//
//import android.graphics.PointF;
//
//import com.mlins.locator.AsociativeMemoryLocator;
//import com.com.mlins.utils.gis.GisLine;
//
//public class MathUtils {
//
//	static public float[] normalizeVector(float[] v, float[] min,
//			float[] max) {
//		float result[] = new float[v.length];
//
//		for (int i = 0; i < v.length; i++) {
//			float diff = (max[i] - min[i]);
//			if (v[i] == 0) {
//				if (diff != 0) {
//					result[i] = (float) ((AsociativeMemoryLocator.getInstance().getZeroValue() - min[i]) / diff);
//				} else {
//					result[i] = 0;
//				}
//			continue;
//			}
//
//			if (diff != 0) {
//				result[i] = (v[i] - min[i]) / diff;
//			} else {
//				result[i] = 0;
//			}
//		}
//		return result;
//
//	}
//
//	public static float WifiThreshold(float v, int index) {
//		float result = 1.0f;
//		float closewifi;
//		float outrewifi;
//		//double min = AsociativeMemoryLocator.getInstance().mins[index];
//		//double max = AsociativeMemoryLocator.getInstance().maxs[index];
//		//double diff = (max - min);
//		closewifi = -47.0f; // max - (diff / 5);
//		outrewifi = -15.0f; // max + (40 * diff) / 100;
//		if (v > closewifi) {
//			result = 7.0f;
//		}
//		if (v > outrewifi) {
//			result = 0.5f;
//		}
//		return result;
//	}
//
//	public static PointF findClosestPointOnSegment(PointF p, GisLine l) {
//		double x1 = l.getPoint1().getX();
//		double y1 = l.getPoint1().getY();
//		double x2 = l.getPoint2().getX();
//		double y2 = l.getPoint2().getY();
//		double px = p.x;
//		double py = p.y;
//		double xDelta = x2 - x1;
//		double yDelta = y2 - y1;
//
//		if ((xDelta == 0) && (yDelta == 0)) {
//			throw new IllegalArgumentException(
//					"Segment start equals segment end");
//		}
//
//		double u = ((px - x1) * xDelta + (py - y1) * yDelta)
//				/ (xDelta * xDelta + yDelta * yDelta);
//
//		final PointF closestPoint;
//		if (u < 0) {
//			closestPoint = new PointF((float) x1, (float) y1);
//		} else if (u > 1) {
//			closestPoint = new PointF((float) x2, (float) y2);
//		} else {
//			closestPoint = new PointF((int) Math.round(x1 + u * xDelta),
//					(int) Math.round(y1 + u * yDelta));
//		}
//
//		return closestPoint;
//	}
//
//	public static double distance(PointF p, PointF p1) {
//
//		return Math.sqrt((p.x - p1.x) * (p.x - p1.x) + (p.y - p1.y)
//				* (p.y - p1.y));
//	}
//}
