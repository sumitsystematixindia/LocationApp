#include "aStarPoint.h"
#include "aStarMath.h"

const string aStarPoint::TAG("aStarPoint");

bool aStarPoint::connectToLine(const GisPoint &p, const GisSegment &line) {
    if ((aStarMath::findDistance(p, line.getLine().getPoint1()) == 0)
        || (aStarMath::findDistance(p, line.getLine().getPoint2()) == 0)) {
        return true;
    }
    return false;
}

aStarPoint::aStarPoint(const GisPoint &p, const list<GisSegment *> &s) {
    setPoint(p);
    try {
        for (list<GisSegment *>::const_iterator line = s.begin(); line != s.end(); line++) {
            if (connectToLine(p, *(*line))) {
                Segments.push_back(*line);
            }
        }
    } catch (exception t) {
        Log::getInstance().error(TAG, "--!!!Attention!!!--");
        Log::getInstance().error(TAG, t.what());
        Log::getInstance().error(TAG, "--End of Error--");
    }
}
    
    
