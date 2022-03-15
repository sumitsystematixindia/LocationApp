#ifndef GisLine__H
#define GisLine__H

#include <string>

using namespace std;

#include <math.h>
#include "GisPoint.h"

class GisLine {
public:

    static const GisLine Null;
    GisPoint point1;
    GisPoint point2;
    double Z;
    int id;

    GisLine(const GisPoint &p1, const GisPoint &p2, double z) {
        point1 = p1;
        point2 = p2;
        Z = z;
    }

    GisLine() :
            Z(-1) {

    }


    const GisPoint &getPoint1() const {
        return point1;
    }

    void setPoint1(const GisPoint &point1) {
        this->point1 = point1;
    }

    const GisPoint &getPoint2() const {
        return point2;
    }

    void setPoint2(const GisPoint &point2) {
        this->point2 = point2;
    }

    void loadLine(const string &line) {
        double x1, y1, x2, y2;
        int z;
        sscanf(line.c_str(), "%lf\t%lf\t%lf\t%lf\t%d", &x1, &y1, &x2, &y2, &z);

        point1.setX(x1);
        point1.setY(y1);
        point2.setX(x2);
        point2.setY(y2);
        setZ(z);
        point1.setZ(z);
        point1.setZ(z);
    }

    double getZ() const {
        return Z;
    }

    void setZ(double z) {
        Z = z;
    }

    float getAngle() const {
        double dx = point1.getX() - point2.getX();
        double dy = point1.getY() - point2.getY();
        float angle = (float) atan2(dy, dx) * 180.0 / M_PI;
        return fmodf((angle + 360), 360.0); // range 0 - 360.
    }

    void setLine(const GisPoint &p1, const GisPoint &p2, double z) {
        Z = z;
        point1.setX(p1.getX());
        point1.setY(p1.getY());
        point1.setZ(p1.getZ());
        point2.setX(p2.getX());
        point2.setY(p2.getY());
        point2.setZ(p2.getZ());
    }

    string toString() const;

    string to_string(double num);

};

#endif // GisLine__H
