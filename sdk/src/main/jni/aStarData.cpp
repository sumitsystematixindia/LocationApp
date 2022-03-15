#include "aStarData.h"
#include "aStarPoint.h"
#include "aStarMath.h"
#include <sstream>
#include <fstream>
#include <vector>
#include "aStarAlgorithm.h"
#include "Instruction.h"
#include "InstructionBuilder.h"
#include "MathUtils.h"
//#include  <android/log.h>

aStarData *aStarData::instance = NULL;
int aStarData::SegmentId = 0;

aStarData::aStarData() {
}

aStarData &aStarData::getInstance() {
    if (instance == NULL) {
        instance = new aStarData();

    }
    return *instance;
}

void aStarData::releaseInstance() {
    if (instance != NULL) {
        instance->clean();
        instance = NULL;
    }
}

void aStarData::clean() {
    for (list<GisSegment *>::iterator l = segmentTable.begin(); l != segmentTable.end(); l++)
        delete *l;
    segmentTable.clear();

    //    for (list<GisPoint*>::iterator  p  = gPoints.begin() ; p != gPoints.end(); p++ )
    //        delete *p;
    gPoints.clear();

    segmentTree.clear();
}

void aStarData::testFindRouteFromFile() {

    list<GisLine *> lines;
    lines.clear();
    GisLine *gLine;

    ifstream infile;

    /*string fd = "/storage/emulated/0/mlins/gis3.kml";	*/
    string fd = "/storage/emulated/0/mlins/spreo0.line";
    infile.open(fd.c_str());


    if (!infile.is_open())
        return;

    double lp1x = 0.0;
    double lp1y = 0.0;
    //	double lp1z = 0.0;
    double lp2x = 0.0;
    double lp2y = 0.0;
    //	double lp2z = 0.0;
    double lpz = 0.0;

    //			  	kml use this
    //double lp1x = 0.0;
    //double lp1y = 0.0;
    //double lp1z = 0.0;
    //double lp2x = 0.0;
    //double lp2y = 0.0;
    //double lp2z = 0.0;

    string line = "";

    while (!infile.eof()) {
        getline(infile, line);

        vector<string> fields;
        string word;
        stringstream stream(line);
        while (getline(stream, word, '\t')) {
            fields.push_back(word);
        }

        if (fields.size() < 6)
            continue;
        // kml use this
        //		lp1y = atof(fields[0].c_str());
        //		lp1x = atof(fields[1].c_str());
        //		lp1z = atof(fields[2].c_str());
        //		lp2y = atof(fields[3].c_str());
        //		lp2x = atof(fields[4].c_str());
        //		lp2z = atof(fields[5].c_str());

        lp1x = atof(fields[1].c_str());
        lp1y = atof(fields[2].c_str());
        lp2x = atof(fields[3].c_str());
        lp2y = atof(fields[4].c_str());
        lpz = atof(fields[5].c_str());
        //lp2z = atof(fields[5].c_str());

        GisPoint startPoint(lp1x, lp1y, lpz);
        GisPoint endPoint(lp2x, lp2y, lpz);

        //kml use this
        //GisPoint startPoint(lp1x, lp1y, lp1z);
        //GisPoint endPoint(lp2x, lp2y, lp2z);
        gLine = new GisLine(startPoint, endPoint, 0);
        lines.push_back(gLine);

    }

    loadData(lines);

    // Begin & End
    //path with one line
    //GisPoint startPoint(32.66078866,35.10636452, 0);
    //GisPoint endPoint(32.66037750,35.10643378, 0);

    GisPoint startPoint(618, 100, 0);
    GisPoint endPoint(480, 869, 0);
    //kml use tyhis
    //GisPoint startPoint(32.66076066,35.10637533, 0);
    //GisPoint endPoint(32.66131973,35.10688782, 0);
    aStarData::getInstance().setDestination(Location(endPoint));
    list<GisSegment *> results;
    aStarAlgorithm alg(startPoint, endPoint);

    alg.getPath(results);


    NavigationPath navigation;
    navigation.addFloorNavigationPath(endPoint.getZ(), results);
    aStarData::getInstance().setCurrentPath(navigation);

    // for print use this
    //	 for (list<GisSegment*>::iterator l  = results.begin() ; l != results.end() ; l++)
    //	    {
    //	        GisSegment* pSegment = *l;
    //	        GisLine* pLine =(GisLine*) &pSegment->getLine();
    //	        GisPoint* p1=(GisPoint*)&pLine->getPoint1();
    //	        GisPoint* p2=(GisPoint*)&pLine->getPoint2();
    //
    //	    	__android_log_print(ANDROID_LOG_DEBUG, "path","line :p1(x=%f y=%f) p2(x=%f y=%f)", p1->getX(), p1->getY(),p2->getX(), p2->getY());
    //
    //	    }


    list<Instruction *> instList;

    const NavigationPath &navPath = aStarData::getInstance().getCurrentPath();
    InstructionBuilder::getInstance().getInstractions(navPath, instList);

    for (list<Instruction *>::iterator it = instList.begin(); it != instList.end(); it++) {
        Instruction *pIns = *it;
        list<int> instTexts = pIns->getText();
        // for print use this
        // int istraction = instTexts.front();

        // __android_log_print(ANDROID_LOG_DEBUG, "inst","Instruction :%d", istraction);

    }
    instList.clear();


}


GisLine *aStarData::findCloseLine(list<GisLine *> &linesList, GisPoint &p1) {

    GisPoint point;
    GisLine *line = NULL;
    double distance = 0;
    double mindistance = 1000000;

    for (list<GisLine *>::const_iterator it = linesList.begin(); it != linesList.end(); it++) {

        GisLine &s = **it;
        aStarMath::findClosePointOnLine(p1, s, point);
        distance = aStarMath::findDistance(p1, point);
        if (distance < mindistance) {
            line = &s;
            //line.setLine(s.getPoint1(),s.getPoint2(),s.getZ());
            mindistance = distance;
        }
    }

    return line;
}

GisLine *aStarData::findCloseKmlLine(list<GisLine *> &linesList, GisPoint &p1) {

    GisPoint point;
    GisLine *line = NULL;
    double distance = 0;
    double mindistance = 100000000;

    for (list<GisLine *>::const_iterator it = linesList.begin(); it != linesList.end(); it++) {

        GisLine &s = **it;
        aStarMath::findClosePointOnLine(p1, s, point);
        distance = aStarMath::findLatLonDistance(p1, point);
        if (distance < mindistance) {
            line = &s;
            //line.setLine(s.getPoint1(),s.getPoint2(),s.getZ());
            mindistance = distance;
        }
    }

    return line;
}


void aStarData::loadData(GisPoint &start, GisPoint &end, const list<GisLine *> &lines,
                         bool kmlMode) {
    //XXX need to optimize this solution => how to remove element from list! remove is not working!
    //SegmentId = 0;

    // Make a copy of lines list

    list<GisLine *> linesList;
    int index = 0;
    for (list<GisLine *>::const_iterator it = lines.begin(); it != lines.end(); it++) {

        GisLine &l = **it;
        GisLine *line = new GisLine();
        line->id = index;
        line->setLine(l.getPoint1(), l.getPoint2(), l.getZ());
        linesList.push_back(line);
        index++;

    }


    //find the segment that closer to start point
    GisLine startSegment;

    if (kmlMode) {
        startSegment = *findCloseKmlLine(linesList, start);
    }
    else {
        startSegment = *findCloseLine(linesList, start);
    }

    // Projection of start point on gis line
    PointF startP;
    PointF startAsPointF;
    startAsPointF.x = start.getX();
    startAsPointF.y = start.getY();
    MathUtils::findClosestPointOnSegment(startAsPointF, startSegment, startP);
    start.setX(startP.x);
    start.setY(startP.y);


    // split start-line into two lines
    GisLine startLine1;
    startLine1.id = startSegment.id;
    startLine1.setLine(startSegment.getPoint1(), start, startSegment.getZ());
    GisLine startLine2;
    startLine2.id = startSegment.id;
    startLine2.setLine(start, startSegment.getPoint2(), startSegment.getZ());

    // create list of segments except startSegment
    list<GisLine *> nextList;

    for (list<GisLine *>::const_iterator it = linesList.begin(); it != linesList.end(); it++) {

        GisLine &l = **it;
        if (l.id != startSegment.id) {
            GisLine *line = new GisLine();
            line->id = l.id;
            line->setLine(l.getPoint1(), l.getPoint2(), l.getZ());
            nextList.push_back(line);
        }
    }

    // add splitted start line to nextList
    nextList.push_back(&startLine1);
    nextList.push_back(&startLine2);

    //find the segment that closer to end point
    GisLine endSegment;

    if (kmlMode) {
        endSegment = *findCloseKmlLine(nextList, end);
    }
    else {
        endSegment = *findCloseLine(nextList, end);
    }



    // Projection of end point on gis line
    PointF endP;
    PointF endAsPointF;
    endAsPointF.x = end.getX();
    endAsPointF.y = end.getY();
    MathUtils::findClosestPointOnSegment(endAsPointF, endSegment, endP);
    end.setX(endP.x);
    end.setY(endP.y);



    // split end line into two lines
    GisLine endLine1;
    endLine1.id = endSegment.id;
    endLine1.setLine(endSegment.getPoint1(), end, endSegment.getZ());
    GisLine endLine2;
    endLine2.id = endSegment.id;
    endLine2.setLine(end, endSegment.getPoint2(), endSegment.getZ());

    // create final list contains all segments except the original endSegment
    list<GisLine *> finalList;
    for (list<GisLine *>::const_iterator it = nextList.begin(); it != nextList.end(); it++) {

        GisLine &l = **it;
        if (l.id != endSegment.id) {
            GisLine *line = new GisLine();
            line->id = l.id;
            line->setLine(l.getPoint1(), l.getPoint2(), l.getZ());
            finalList.push_back(line);
        }
    }

    // add splitted end line to final result
    finalList.push_back(&endLine1);
    finalList.push_back(&endLine2);


    loadData(finalList);


}

void aStarData::loadData(const list<GisLine *> &lines) {
    cleanAStar();
    for (list<GisLine *>::const_iterator l = lines.begin(); l != lines.end(); l++) {
        int sid = SegmentId++;
        GisSegment *segment = new GisSegment(**l, sid);
        segmentTable.push_back(segment);
    }

    for (list<GisSegment *>::iterator l = segmentTable.begin(); l != segmentTable.end(); l++) {
        GisSegment *pSegment = *l;
        GisLine *pLine = (GisLine *) &pSegment->getLine();
        gPoints.push_back((GisPoint *) &pLine->getPoint1());
        gPoints.push_back((GisPoint *) &pLine->getPoint2());
    }

    for (list<GisPoint *>::iterator p = gPoints.begin(); p != gPoints.end(); p++) {
        aStarPoint *apoint = new aStarPoint(*(*p), segmentTable);
        segmentTree.push_back(apoint);
    }

    for (list<GisSegment *>::iterator s1 = segmentTable.begin(); s1 != segmentTable.end(); s1++) {
        list<GisSegment *> n;
        for (list<GisSegment *>::iterator s2 = segmentTable.begin();
             s2 != segmentTable.end(); s2++) {
            if (aStarMath::isNeighbors(**s1, **s2)) {
                n.push_back(*s2);
            }
        }
        // the n is saved as the object and the HashMap does not copy it -->
        // once you are calling clear to the n you clean the Neighbors list
        // as well
        Neighbors[(*s1)->getId()] = n;

    }
}


void aStarData::cleanAStar() {
    for (list<GisSegment *>::iterator s = segmentTable.begin(); s != segmentTable.end(); s++) {
        (*s)->setParent(-1);
        (*s)->setG(0);
        // s.setId(0);
    }
    clean();
    Neighbors.clear();
}

