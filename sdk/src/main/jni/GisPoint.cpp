#include "GisPoint.h"

const GisPoint GisPoint::Null(-1.0, -1.0, -1.0);


string GisPoint::toString() const {
    // string ret = "{X:" + std::to_string(X) + " Y:" + std::to_string(Y) + " Z:" + std::to_string(Z);
    return "GisPoint";//ret;
}
