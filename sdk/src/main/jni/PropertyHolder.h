#ifndef PropertyHolder__H
#define PropertyHolder__H

#include <string>

using namespace std;

class PropertyHolder {
    static PropertyHolder *instance;

    PropertyHolder();

public:
    static PropertyHolder &getInstance();

    float getInstructionsDistance();

    float getPixelsToMeter();
};

#endif // PropertyHolder__H