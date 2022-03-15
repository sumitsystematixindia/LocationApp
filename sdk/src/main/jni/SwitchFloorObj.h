#ifndef SwitchFloorObj__H
#define SwitchFloorObj__H

#include <string>
#include <list>

using namespace std;

#include "PointF.h"
#include "Location.h"

class SwitchFloorObj {
private:
    PointF Point;
    int Z;
    int Id;
    string Description;
    string Type;
    list<int> FromFloor;
    list<int> ToFloor;
    int minFloorDifference;
    int goingToFloor;

    Location asLoc;

public:

    SwitchFloorObj();

    void parse(const string &line);

    const PointF &getPoint() {
        return Point;
    }

    const Location &getAsLocation() {
        asLoc.x = Point.x;
        asLoc.y = Point.y;
        asLoc.z = Z;
        return asLoc;
    }

    void setPoint(PointF point) {
        Point = point;
    }

    void setPoint(float px, float py) {
        Point.set(px, py);
    }

    int getZ() {
        return Z;
    }

    void setZ(int z) {
        Z = z;
    }

    int getId() {
        return Id;
    }

    void setId(int id) {
        this->Id = id;
    }

    const string &getDescription() {
        return Description;
    }

    void setDescription(const string &description) {
        Description = description;
    }

    list<int> getFromFloor() {
        return FromFloor;
    }

    void setFromFloor(list<int> fromFloor) {
        FromFloor = fromFloor;
    }

    list<int> getToFloor() {
        return ToFloor;
    }

    void setToFloor(list<int> toFloor) {
        ToFloor = toFloor;
    }

    void setFromFloor(const string &text);

    void setToFloor(const string &text);

    const string &getType() {
        return Type;
    }

    void setType(const string &type) {
        Type = type;
    }

    int getMinFloorDifference() {
        return minFloorDifference;
    }

    void setMinFloorDifference(int minFloorDifference) {
        this->minFloorDifference = minFloorDifference;
    }

    int getGoingToFloor() {
        return goingToFloor;
    }

    void setGoingToFloor(int goingToFloor) {
        this->goingToFloor = goingToFloor;
    }


};

#endif //SwitchFloorObj__H
