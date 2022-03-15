#ifndef GisSegment__H
#define GisSegment__H

#include "GisLine.h"
#include <string>
#include <list>

using namespace std;

class GisSegment {
private:
    GisLine Line;
    int segmentId;
    double weight;
    int parent;
    double g;
    static const string TAG;

public:

    static list<GisSegment *> EMPTY_SEGMENT_LIST;

    bool operator!=(const GisSegment &other) const {
        return segmentId != other.segmentId;
    }

    GisSegment(const GisLine &l, int sid);

    GisSegment();

    double calcweight();

    const GisLine &getLine() const {
        return Line;
    }

    GisLine &getLine() {
        return Line;
    }


    void setLine(const GisLine &line) {
        Line = line;
    }

    int getId() const {
        return segmentId;
    }

    void setId(int sid) {
        segmentId = sid;
    }

    double getWeight() const {
        return weight;
    }

    void setWeight(double w) {
        weight = w;
    }

    int getParent() const {
        return parent;
    }

    void setParent(int parent) {
        this->parent = parent;
    }

    double getG() const {
        return g;
    }

    void setG(double g) {
        this->g = g;
    }

    string toString() const;
};

typedef list<GisSegment *> GisSegmentList;

#endif // GisSegment__H

