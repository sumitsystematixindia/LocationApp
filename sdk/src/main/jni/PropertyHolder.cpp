#include "PropertyHolder.h"

PropertyHolder *PropertyHolder::instance = NULL;

PropertyHolder::PropertyHolder() {

}

PropertyHolder &PropertyHolder::getInstance() {
    if (instance == NULL) {
        instance = new PropertyHolder();
    }
    return *instance;
}

float PropertyHolder::getInstructionsDistance() {
    return 3;
}

float PropertyHolder::getPixelsToMeter() {
    return 40.58;
}
