#ifndef POIDATA__H
#define POIDATA__H

#include <string>
#include <list>

using namespace std;

#include "PointF.h"

class PoiData {
private:
    double Z;
    string url;
    double dFromMyLocation;
    string Details;
public:
    string poiuri;
    list<string> poitype;
    string poidescription;
    list<string> poiKeywords;
    PointF point;

    PoiData();

    PoiData(const string &npoiuri, const list<string> &npoitype, const string &desc);

    PoiData(const string &npoiuri, const list<string> &npoitype, const string &desc,
            const list<string> &keywords);


    const list<string> &getPoiKeywords() const {
        return poiKeywords;
    }

    void setPoiKeywords(const list<string> &poiKeywords) {
        this->poiKeywords = poiKeywords;
    }


    const string &getPoiuri() const {
        return poiuri;
    }

    void setPoiuri(string poiuri) {
        this->poiuri = poiuri;
    }

    const list<string> &getPoitype() const {
        return poitype;
    }

    void setPoitype(const string &poitype) {
        this->poitype.push_back(poitype);
    }

    const string &getpoiDescription() const {
        return poidescription;
    }

    void setpoiDescription(const string &description) {
        this->poidescription = description;
    }

    const PointF &getPoint() const {
        return point;
    }

    void setPoint(const PointF &point) {
        this->point = point;
    }


    void init(const string &npoiuri, const list<string> &npoitype, const string &desc);

    double getZ() const {
        return Z;
    }

    void setZ(double z) {
        Z = z;
    }

    const string &getUrl() const {
        return url;
    }

    void setUrl(const string &url) {
        this->url = url;
    }

    double getdFromMyLocation() const {
        return dFromMyLocation;
    }

    void setdFromMyLocation(double dFromMyLocation) {
        this->dFromMyLocation = dFromMyLocation;
    }

    const string &getDetails() const {
        return Details;
    }

    void setDetails(const string &details) {
        Details = details;
    }

};

#endif // POIDATA__H
