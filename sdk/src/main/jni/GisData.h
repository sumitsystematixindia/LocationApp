#ifndef GISDATA__H
#define GISDATA__H

#include <list>
#include <string>
#include <istream>
#include <fstream>

#include "PointF.h"

using namespace std;


class GisLine;

class GisData {
private:
    static GisData *instance;
    list<GisLine *> lines;

    void clean();

public:

    static GisData *getInstance();

    static void releaseInstance();


    void addGisLine(GisLine *l);

    void loadGisLines();

    void loadGisLines(int floor);

    list<GisLine *> &getLines();

    void findClosestLine(PointF &point, GisLine &result);

    PointF findClosestPointOnLine(PointF &p);

    const GisLine &findGisLine(const PointF &p1);
};

#endif // GISDATA__H
