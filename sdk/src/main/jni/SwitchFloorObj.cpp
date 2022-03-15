#include "SwitchFloorObj.h"
#include <vector>
#include <sstream>

using namespace std;

static vector<string> &split(const string &s, char delim, vector<string> &elems) {
    elems.clear();
    stringstream ss(s);
    string item;
    while (getline(ss, item, delim)) {
        elems.push_back(item);
    }
    return elems;
}

SwitchFloorObj::SwitchFloorObj() :
        Z(0),
        Id(0),
        minFloorDifference(0),
        goingToFloor(0) {

}

void SwitchFloorObj::parse(const string &line) {
    vector<string> fields;
    fields.reserve(100);
    split(line, '\t', fields);

    Point.x = atof(fields[1].c_str());
    Point.y = atof(fields[2].c_str());

    Z = atoi(fields[3].c_str());
    Id = atoi(fields[4].c_str());
    Description = fields[5];
    Type = fields[6];
    vector<string> vals;
    string from = fields[7];
    vals = split(from, ',', vals);
    for (int i = 0; i < vals.size(); i++) {
        if (!vals[i].empty()) {
            FromFloor.push_back(atoi(vals[i].c_str()));
        }
    }
    string to = fields[8];
    vals = split(to, ',', vals);
    for (int i = 0; i < vals.size(); i++) {
        if (!vals[i].empty()) {
            ToFloor.push_back(atoi(vals[i].c_str()));
        }
    }
}


void SwitchFloorObj::setFromFloor(const string &text) {
    FromFloor.clear();
    vector<string> vals;
    vals = split(text, ',', vals);
    for (int i = 0; i < vals.size(); i++) {
        FromFloor.push_back(atoi(vals[i].c_str()));
    }

}

void SwitchFloorObj::setToFloor(const string &text) {
    ToFloor.clear();
    vector<string> vals;
    vals = split(text, ',', vals);
    for (int i = 0; i < vals.size(); i++) {
        ToFloor.push_back(atoi(vals[i].c_str()));
    }

}


