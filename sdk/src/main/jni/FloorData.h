#ifndef FloorData__H
#define FloorData__H

#include <string>

using namespace std;

class FloorData {

    string mapuri = "";
    string thumburi = "";
    string title = "";
    string gis = "";
    string poi = "";
    string poiar = "";
    string poihe = "";
    string poien = "";
    string poiru = "";
    float pixelsToMeter;
    float rotation;


    FloorData() :
            pixelsToMeter(0),
            rotation(0) {

    }

    FloorData(const string &nmap, const string &nthumb, const string &ntitle, const string &gisdata)
            :
            pixelsToMeter(0),
            mapuri(nmap),
            thumburi(nthumb),
            title(ntitle),
            gis(gisdata),
            rotation(0) {
    }

    FloorData(const string &mapStr, const string &thumb, const string &newTitle, float p2m,
              float rot, string gisdata) :
            pixelsToMeter(0),
            rotation(0),
            title(newTitle),
            pixelsToMeter(p2m),
            rotation(rot),
            gis(gisdata) {
    }


    string &getMapuri() const {
        return mapuri;
    }

    void setMapuri(const string &mapuri) {
        this->mapuri = mapuri;
    }

    const string &getThumburi() const {
        return thumburi;
    }

    void setThumburi(const string &thumburi) {
        this->thumburi = thumburi;
    }

    const string &getTitle() const {
        return title;
    }

    void setTitle(const string &title) {
        this->title = title;
    }

    const string &getGis() const {
        return gis;
    }

    void setGis(const string &gis) {
        this->gis = gis;
    }

    const string &getPoi() const {
        return poi;
    }

    void setPoi(const string &poi) {
        this->poi = poi;
    }

    const string &getPoiar() const {
        return poiar;
    }

    void setPoiar(const string &poiar) {
        this->poiar = poiar;
    }

    const string &getPoihe() const {
        return poihe;
    }

    void setPoihe(const string &poihe) {
        this->poihe = poihe;
    }

    const string &getPoien() const {
        return poien;
    }

    void setPoien(const string &poien) {
        this->poien = poien;
    }

    const string &getPoiru() const {
        return poiru;
    }

    void setPoiru(const string &poiru) {
        this->poiru = poiru;
    }

    float getPixelsToMeter() const {
        return pixelsToMeter;
    }

    void setPixelsToMeter(float pixelsToMeter) {
        this->pixelsToMeter = pixelsToMeter;
    }

    float getRotation() const {
        return rotation;
    }


    void setPixelToMeters(float p2m) {
        pixelsToMeter = p2m;

    }

    void setRotation(float rot) {
        rotation = rot;

    }
}

#endif // FloorData__H
