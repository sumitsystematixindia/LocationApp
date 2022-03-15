#include <cmath>
#include "ConversionUtils.h"
#include  "MathUtils.h"

#ifdef __APPLE__
#else

#endif


ConversionUtils::ConversionUtils() {
}


ConversionUtils::~ConversionUtils() {
}

double ConversionUtils::toDegrees(double radians) {
    return radians * (180.0 / M_PI);
}

double ConversionUtils::toRadians(double degrees) {
    return degrees * (M_PI / 180.0);
}

void ConversionUtils::convertPoint(Location &point, double tlLon, double tlLat,
                                   double trLon, double trLat, double blLon, double blLat,
                                   double brLon, double brLat, double widthPixels,
                                   double heightPixels,
                                   double rotationAngle, Location &covertedPoint) {


    double widthInLatLon = abs(trLon - tlLon);
    double heightInLatLon = abs(blLat - tlLat);


    double longitude = tlLon + (point.x * widthInLatLon / widthPixels);
    double latitude = tlLat - (point.y * heightInLatLon / heightPixels);
    //   System.out.println("point x,y " + lon + ","+ lat);
    // covertedPoint.x = lat;
    // covertedPoint.y = lon;
    //   covertedPoint.z = point.z;
    //   covertedPoint.lat = point.lat;
    //  covertedPoint.lon = point.lon;




    // center
    //double latitude= 32.772686;
    //double longitude= 34.965633499999996;

    //tl
    //double latitude=32.772986;
    //double longitude=34.965353;

    // bl
    //double latitude =32.772386; // lat
    //double longitude =34.965353; // lon


    // br
    //double latitude = 32.772386;
    //double longitude = 34.965914;

    // tr
    //double latitude =32.772986;
    //double longitude = 34.965914;

    //double xyz[] = convertSphericalToCartesian( latitude, longitude);
    Location xyz = convertSphericalToCartesian(latitude, longitude);
    double x = xyz.x;
    double y = xyz.y;
    double z = xyz.z;

    //System.out.println("current xyz =>"+ x+","+y+","+z);

    //double[] latlon=convertCartesianToSpherical(x, y, z);
    //Location latlon=convertCartesianToSpherical(x, y, z);

    //System.out.println("current lat lon =>"+ latlon[0]+","+latlon[1]);


    //   double centerLon = tlLon + widthInLatLon/ 2.0;
    //  double centerLat = tlLat - heightInLatLon  / 2.0;
    //System.out.println("center x,y " + centerLat + ","+ centerLon);

    double clatitude = tlLat - heightInLatLon / 2.0; /// 32.772686
    double clongitude = tlLon + widthInLatLon / 2.0; // 34.965633499999996


    //double centerLT[] = convertSphericalToCartesian( clatitude, clongitude);
    Location centerLT = convertSphericalToCartesian(clatitude, clongitude);
    double a = centerLT.x; //32.772686;
    double b = centerLT.y; //34.965633499999996;
    double c = centerLT.z;

    //  System.out.println("center xyz =>"+ a+","+b+","+c);

    double u = getSign(a);
    double v = getSign(b);
    double w = getSign(c);


    //double rotationAngle = -37.8748;

    double theta1 = toRadians(rotationAngle);


    //double res[] = rotPointFromFormula(a, b, c, u, v, w, x, y, z, theta1);
    Location res = rotPointFromFormula(a, b, c, u, v, w, x, y, z, theta1);
    //System.out.println("rot xyz ==>" +res[0]+","+res[1]+ "," +res[2]);


    Location convlatlon = convertCartesianToSpherical(res.x, res.y, res.z);
    //System.out.println("rot lat lon ==>"+ latlon[0]+","+latlon[1]);

    // reserve what we got
    covertedPoint.x = point.x;
    covertedPoint.y = point.y;
    covertedPoint.z = point.z;

    covertedPoint.lat = convlatlon.lat;
    covertedPoint.lon = convlatlon.lon;
}


int ConversionUtils::getSign(double num) {

    if (num > 0) return 1;
    if (num < 0) return -1;
    return 0;

}

Location ConversionUtils::convertSphericalToCartesian(double latitude, double longitude) {
    double earthRadius = 6367; //radius in km
    double lat = toRadians(latitude);
    double lon = toRadians(longitude);
    double x = earthRadius * cos(lat) * cos(lon);
    double y = earthRadius * cos(lat) * sin(lon);
    double z = earthRadius * sin(lat);
    //double res[] = {x,y,z};
    Location loc;
    loc.x = x;
    loc.y = y;
    loc.z = z;
    return loc;
}

Location ConversionUtils::convertCartesianToSpherical(double x, double y, double z) {
    double r = sqrt(x * x + y * y + z * z);
    double lat = toDegrees(asin(z / r));
    double lon = toDegrees(atan2(y, x));
    //double res[] = {lat,lon};
    Location loc;
    loc.lat = lat;
    loc.lon = lon;
    return loc;
}


Location ConversionUtils::rotPointFromFormula(double a, double b, double c,
                                              double u, double v, double w,
                                              double x, double y, double z,
                                              double theta) {

    Location loc;
    loc.x = -1;
    loc.y = -1;
    loc.z = -1;

    // normalize the direction vector.
    double l;
    if ((l = longEnough(u, v, w)) < 0) {

        // Don't bother.
    }
    else {
        // Normalize the direction vector.
        u = u / l;
        v = v / l;
        w = w / l;
        // Set some intermediate values.
        double u2 = u * u;
        double v2 = v * v;
        double w2 = w * w;
        double cosT = cos(theta);
        double oneMinusCosT = 1 - cosT;
        double sinT = sin(theta);

        // Use the formula in the paper.
        // double[] p = new double[3];
        loc.x = (a * (v2 + w2) - u * (b * v + c * w - u * x - v * y - w * z)) * oneMinusCosT
                + x * cosT
                + (-c * v + b * w - w * y + v * z) * sinT;

        loc.y = (b * (u2 + w2) - v * (a * u + c * w - u * x - v * y - w * z)) * oneMinusCosT
                + y * cosT
                + (c * u - a * w + w * x - u * z) * sinT;

        loc.z = (c * (u2 + v2) - w * (a * u + b * v - u * x - v * y - w * z)) * oneMinusCosT
                + z * cosT
                + (-b * u + a * v - v * x + u * y) * sinT;
    }

    return loc;
}


double ConversionUtils::longEnough(double u, double v, double w) {
    /** How close a double must be to a double to be "equal". */
    double TOLERANCE = 0.000000001; //1E-9;
    double l = sqrt(u * u + v * v + w * w);
    if (l > TOLERANCE) {
        return l;
    } else {
        return -1;
    }
}

void ConversionUtils::convertLatLonPoint(Location &point, double tlLon, double tlLat,
                                         double trLon, double trLat, double blLon, double blLat,
                                         double brLon, double brLat, double widthPixels,
                                         double heightPixels,
                                         double rotationAngle, Location &covertedPoint) {


    Location cartTL = convertSphericalToCartesian(tlLat, tlLon);
    //__android_log_print(ANDROID_LOG_ERROR, "ConversionUtils::convertLatLonPoint", "cartTL:(%f,%f,%f)",cartTL.x, cartTL.y, cartTL.z);

    Location cartTR = convertSphericalToCartesian(trLat, trLon);
    //	__android_log_print(ANDROID_LOG_ERROR, "ConversionUtils::convertLatLonPoint", "cartTR:(%f,%f,%f)",cartTR.x, cartTR.y, cartTR.z);

    Location cartBL = convertSphericalToCartesian(blLat, blLon);
    //	__android_log_print(ANDROID_LOG_ERROR, "ConversionUtils::convertLatLonPoint", "cartBL:(%f,%f,%f)",cartBL.x, cartBL.y, cartBL.z);

    Location cartBR = convertSphericalToCartesian(brLat, brLon);
    //	__android_log_print(ANDROID_LOG_ERROR, "ConversionUtils::convertLatLonPoint", "cartBR:(%f,%f,%f)",cartBR.x, cartBR.y, cartBR.z);

    //	__android_log_print(ANDROID_LOG_ERROR, "ConversionUtils::convertLatLonPoint", "widthPixels,heightPixels:(%f,%f)",widthPixels, heightPixels);

//    		double widthInLatLon = abs(trLon- tlLon);
//    			double heightInLatLon = abs(blLat - tlLat);
//
//    		double clatitude= tlLat - heightInLatLon  / 2.0;
//    		double clongitude = tlLon + widthInLatLon/ 2.0;
    //   		double clatitude =  brLat;
    //   		double clongitude= brLon;
//    		Location cartPt = convertSphericalToCartesian( clatitude, clongitude);
    Location cartPt = convertSphericalToCartesian(point.lat, point.lon);
    //   		__android_log_print(ANDROID_LOG_ERROR, "ConversionUtils::convertLatLonPoint", "cartPt:(%f,%f,%f)",cartPt.x, cartPt.y, cartPt.z);



    double cartWidth = MathUtils::distance(cartTR.x, cartTR.y, cartTL.x, cartTL.y);
    //__android_log_print(ANDROID_LOG_ERROR, "ConversionUtils::convertLatLonPoint", "cartWidth: %f",cartWidth);

    double cartHeight = MathUtils::distance(cartBL.x, cartBL.y, cartTL.x, cartTL.y);
    //__android_log_print(ANDROID_LOG_ERROR, "ConversionUtils::convertLatLonPoint", "cartHeight: %f",cartHeight);



//    		double widthInLatLon = abs(trLon- tlLon);
//    				double heightInLatLon = abs(blLat - tlLat);


    Location closestPointH;
    findClosePointOnSegment(cartPt, cartBL, cartTL, closestPointH);
    //__android_log_print(ANDROID_LOG_ERROR, "ConversionUtils::convertLatLonPoint", "closestPointH: (%f,%f)",closestPointH.x, closestPointH.y);
    double ratioY =
            MathUtils::distance(closestPointH.x, closestPointH.y, cartTL.x, cartTL.y) / cartHeight;
    //__android_log_print(ANDROID_LOG_ERROR, "ConversionUtils::convertLatLonPoint", "ratioY: %f",ratioY);

    Location closestPointW;
    findClosePointOnSegment(cartPt, cartTL, cartTR, closestPointW);
    //__android_log_print(ANDROID_LOG_ERROR, "ConversionUtils::convertLatLonPoint", "closestPointW: (%f,%f)",closestPointW.x, closestPointW.y);
    double ratioX =
            MathUtils::distance(closestPointW.x, closestPointW.y, cartTL.x, cartTL.y) / cartWidth;
    //__android_log_print(ANDROID_LOG_ERROR, "ConversionUtils::convertLatLonPoint", "ratioX: %f",ratioX);




    double px = widthPixels * ratioX;

    //__android_log_print(ANDROID_LOG_ERROR, "ConversionUtils::convertLatLonPoint", "px: %f",px);

    double py = heightPixels * ratioY;

    //__android_log_print(ANDROID_LOG_ERROR, "ConversionUtils::convertLatLonPoint", "py: %f",py);
//
//    		if(px<0 || py<0){
//    			covertedPoint.x = -1;
//    			covertedPoint.y = -1;
//    		}
//    		else{
    covertedPoint.x = px;
    covertedPoint.y = py;
//    		}


    // reserve what we got
    covertedPoint.z = point.z;
    covertedPoint.lat = point.lat;
    covertedPoint.lon = point.lon;
}


void ConversionUtils::findClosePointOnSegment(Location &p, Location &segP1, Location &segP2,
                                              Location &closestPoint) {
    double x1 = segP1.getX();
    double y1 = segP1.getY();
    double x2 = segP2.getX();
    double y2 = segP2.getY();
    double px = p.getX();
    double py = p.getY();


//        //% direction vector of the line
//        double vx = segP2.x; // == line(:, 4);
//        double vy =  segP2.y; // line(:, 5);
//        double vz = segP2.z; //line(:, 6);
//
//        //% difference of point with line origin
//        double dx =  p.x - segP1.x; //point(:,1) - line(:,1);
//        double dy = p.y - segP1.y; //point(:,2) - line(:,2);
//        double dz = p.z - segP1.z; //point(:,3) - line(:,3);
//
//       // % Position of projection on line, using dot product
//        double delta = vx * vx + vy * vy + vz * vz;
//        double tp = (dx * vx + dy * vy + dz * vz) / delta;
//
//       // % convert position on line to cartesian coordinates
//       // point = [line(:,1) + tp .* vx, line(:,2) + tp .* vy, line(:,3) + tp .* vz];
//        closestPoint.x = segP1.x + tp * vx; // line(:,1) + tp .* vx;
//        closestPoint.y = segP1.y + tp * vy;
//        closestPoint.z = segP1.z + tp * vz;


    double xDelta = x2 - x1;
    double yDelta = y2 - y1;

    if ((xDelta == 0) && (yDelta == 0)) {
        closestPoint.x = segP1.x;
        closestPoint.y = segP1.y;
        closestPoint.z = segP1.z;
        return;
    }

    double u = ((px - x1) * xDelta + (py - y1) * yDelta)
               / (xDelta * xDelta + yDelta * yDelta);

    if (u < 0) {
        closestPoint.x = x1;
        closestPoint.y = y1;
        closestPoint.z = segP1.getZ();
    } else if (u > 1) {
        closestPoint.x = x2;
        closestPoint.y = y2;
        closestPoint.z = segP1.getZ();
    } else {
        closestPoint.x = x1 + u * xDelta;
        closestPoint.y = y1 + u * yDelta;
        closestPoint.z = segP1.getZ();
    }

}






