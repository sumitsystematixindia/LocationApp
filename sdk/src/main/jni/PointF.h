#ifndef POINTF__H
#define POINTF__H

class PointF {

public:
    double x;
    double y;

    PointF() :
            x(0.0), y(0.0) {

    }

    PointF(double xx, double yy) :
            x(xx), y(yy) {
    }

    double getX() const {
        return x;
    }

    void setX(double pointX) {
        x = pointX;
    }

    double getY() const {
        return y;
    }

    void setY(double pointY) {
        y = pointY;
    }

    void set(double xx, double yy) {
        x = xx;
        y = yy;
    }

};

#endif // POINTF__H
