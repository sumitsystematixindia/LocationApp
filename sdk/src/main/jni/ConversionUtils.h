/*
 * MathUtils.h
 *
 *  Created on: 1 ���� 2013
 *      Author: Owner
 */

#ifndef CONVUTILS_H_
#define CONVUTILS_H_


#include "Location.h"
#include <stdlib.h>

using namespace std;

class ConversionUtils {

private:

    static int getSign(double num);

    static Location convertSphericalToCartesian(double latitude, double longitude);

    static Location convertCartesianToSpherical(double x, double y, double z);

    static Location rotPointFromFormula(double a, double b, double c,
                                        double u, double v, double w,
                                        double x, double y, double z,
                                        double theta);

    static double longEnough(double u, double v, double w);

public:
    ConversionUtils();

    virtual ~ConversionUtils();

    // rotation angle in degrees
    static void convertPoint(Location &point, double tlLon, double tlLat,
                             double trLon, double trLat, double blLon, double blLat,
                             double brLon, double brLat, double widthPixels, double heightPixels,
                             double rotationAngle, Location &covertedPoint);

    static void convertLatLonPoint(Location &point, double tlLon, double tlLat,
                                   double trLon, double trLat, double blLon, double blLat,
                                   double brLon, double brLat, double widthPixels,
                                   double heightPixels,
                                   double rotationAngle, Location &covertedPoint);

    static void findClosePointOnSegment(Location &p, Location &segP1, Location &segP2,
                                        Location &closestPoint);

    static double toDegrees(double radians);

    static double toRadians(double degrees);

};

#endif /* CONVUTILS_H_ */

