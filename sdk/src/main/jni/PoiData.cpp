#include "PoiData.h"


PoiData::PoiData() :
        Z(0),
        dFromMyLocation(0),
        Details("No details found") {

}

PoiData::PoiData(const string &npoiuri, const list<string> &npoitype, const string &desc) :
        Z(0),
        dFromMyLocation(0),
        Details("No details found") {
    init(npoiuri, npoitype, desc);
}

PoiData::PoiData(const string &npoiuri, const list<string> &npoitype, const string &desc,
                 const list<string> &keywords) {
    init(npoiuri, npoitype, desc);
    poiKeywords = keywords;
}


void PoiData::init(const string &npoiuri, const list<string> &npoitype, const string &desc) {
    poiuri = npoiuri;
    poitype = npoitype;
    poidescription = desc;
}
