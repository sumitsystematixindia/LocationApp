#ifndef LOCATION__H
#define LOCATION__H

#include <string>
#include <list>
#include <math.h>
#include "GisPoint.h"

#ifdef __APPLE__
#else

#include <android/log.h>

#endif

using namespace std;

class PoiData;

class Location {
private:
    double findLatLonDistance(double lat1, double lon1, double lat2, double lon2);

    double toRadians(double degrees);

public:
    double x;
    double y;
    double z;
    string iconUri;
    string description;
    list<string> keywordslist;
    double distance_to_origin;

    double lat;
    double lon;
    int type;  // 0 means indoor 1 means outdoor

    string poiId; // related poi id if exists

    string facilityId;  // related facility id

    int isFacilityCenter; // 0 means facilit center y, otherwise = 0;

    list<string> &getKeywords() {
        return keywordslist;

    }

    Location(double x, double y, double z, double lat, double lon, string poiId, string facilityId);

    Location(double x, double y, double z, double lat, double lon);

    virtual ~Location();

    Location(const GisPoint &gisP) :
            x(gisP.getX()), y(gisP.getY()), z(gisP.getZ()) {

    }


    Location();

    Location(const PoiData &data);

    double getX() const {
        return x;
    }

    void setX(double x) {
        this->x = x;
    }

    double getY() const {
        return y;
    }

    void setY(double y) {
        this->y = y;
    }

    double getZ() const {
        return z;
    }

    void setZ(double z) {
        this->z = z;
    }

    string getDescription() const {
        return description;
    }

    void setDescription(string description) {
        this->description = description;
    }

    string getFacilityId() const {
        return facilityId;
    }

    void setFacilityId(string facilityId) {
        this->facilityId = facilityId;
    }


    string getRelatedPoiId() const {
        return poiId;
    }

    void setRelatedPoiId(string poiId) {
        this->poiId = poiId;
    }

    string getIconUri() const {
        return iconUri;
    }

    void setIconUri(string iconUri) {
        this->iconUri = iconUri;
    }

    string toListstring() const {
        return description;
    }

    double getdistance_to_origin() const {
        return distance_to_origin;
    }

    void setdistance_to_origin(double distance_to_origin) {
        this->distance_to_origin = distance_to_origin;
    }

    double proximity(Location other);

    //void proximity(double x, double y);
    void initProximity(Location other);

    double proximity();

    double getLat() const {
        return lat;
    }

    void setLat(double lat) {
        this->lat = lat;
    }

    double getLon() const {
        return lon;
    }

    void setLon(double lon) {
        this->lon = lon;
    }

    int getType() const {
        return type;
    }

    void setType(int type) {
        this->type = type;
    }

    bool isInternal() {
        if (type == 0) {
            return true;
        }
        else {
            return false;
        }
    }

};

#endif// LOCATION__H
