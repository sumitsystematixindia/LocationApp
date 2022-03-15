// Internal Implementation
#include "GisLine.h"
#include <sstream>

const GisLine GisLine::Null;

string GisLine::toString() const {

    string ret = "GisLine:{point1:" + point1.toString() +
                 ", point2:" + point2.toString() +/* " Z:" + to_string(Z) +*/ "}";
    return ret;
}

string GisLine::to_string(double num) {
    ostringstream convert; // stream used for the conversion

    convert <<
    num; // insert the textual representation of �Number� in the characters    in the stream

    return convert.str();
}
