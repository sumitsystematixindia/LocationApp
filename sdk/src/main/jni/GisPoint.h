#ifndef GIS_POINT__H
#define GIS_POINT__H
//#include "Location.h"
#include <string>

using namespace std;

class GisPoint {
    double X;
    double Y;
    double Z;

public:
    static const GisPoint Null;

    GisPoint() :
            X(0.0), Y(0.0), Z(0.0) {

    }

    GisPoint(double x, double y, double z) :
            X(x), Y(y), Z(z) {
    }

//	GisPoint(Location& loc):
//    X(loc.getX()),Y(loc.getY()),Z(loc.getZ())
//    {
//	}

    double getX() const {
        return X;
    }

    void setX(double pointX) {
        X = pointX;
    }

    double getY() const {
        return Y;
    }

    void setY(double pointY) {
        Y = pointY;
    }

    double getZ() const {
        return Z;
    }

    void setZ(double z) {
        Z = z;
    }

    bool operator==(const GisPoint &other) const {
        return X == other.X && Y == other.Y && Z == other.Z;
    }

    string toString() const;

};

#endif // GIS_POINT__H
