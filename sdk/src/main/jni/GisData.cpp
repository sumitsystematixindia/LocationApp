#include "GisData.h"
#include "FacilityConf.h"
#include "MathUtils.h"

using namespace std;
GisData *GisData::instance = NULL;


GisData *GisData::getInstance() {
    if (instance == NULL) {
        instance = new GisData();

    }
    return instance;

}

void GisData::releaseInstance() {
    if (instance != NULL) {
        instance->clean();
        instance = NULL;
    }
}

void GisData::clean() {
    for (list<GisLine *>::iterator l = lines.begin(); l != lines.end(); l++) {
        delete *l;
    }
    lines.clear();

}

void GisData::addGisLine(GisLine *l) {
    lines.push_back(l);
}

void GisData::loadGisLines() {
//ADIA TODO NEXT
    int floor = FacilityConf::getInstance().getSelectedFloor();
    loadGisLines(floor);
}

void GisData::loadGisLines(int floor) {
    try {
/*		lines.clear();
		bool islocal = false; //ADIA TODO //PropertyHolder.getInstance().isLocal();
		if (!islocal) {
			// Get from server:
			FloorData* data = FacilityConf::getInstance().getFloor(floor);
			string gisuri("");
			if(data != NULL){
                gisuri = ServerConnection.getInstance().translateUrl(data->gis);
			}
			istream is = ResourceDownloader.getInstance().getUrl(gisuri);
			if (is.good()) {
				try {
					string line;
					while ((getLine(is,line)) && line.length() > 0 ) {
						GisLine* l = new GisLine();
						l->loadLine(line);
						if (!(l->point1.getX() == l->point2.getX() && l->point1
                              .getY() == l->point2.getY())) {
							lines.add(l);
						}
					}
					in.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
                
			}*/ // ADIA TBD
//		}
//        else {
//			// Get from local copy
//			string floordir = PropertyHolder.getInstance().getFacilityDir()
//            + "/" + floor;
//            string dirName = floordir + "/gis";
//            ifstream f(dirName + "/meller1.txt.line")
//            if (f.is_open()) {
//				try {
//					string line;
//					while ((getLine(f, line)) && line.length() > 0) {
//						GisLine* l = new GisLine();
//						l->loadLine(line);
//						if (!(l->point1.getX() == l->point2.getX() && l->point1
//                              .getY() == l->point2.getY())) {
//							lines.add(l);
//						}
//					}
//					inlocal.close();
//				}
//			}
//		}

    } catch (exception e) {
//        e.printStackTrace();
    }

}

list<GisLine *> &GisData::getLines() {
    return lines;

}

void GisData::findClosestLine(PointF &point, GisLine &result) {
    if (lines.size() > 0) {
        double min = DBL_MAX;
        for (list<GisLine *>::iterator l = lines.begin(); l != lines.end(); l++) {
            PointF pl;
            MathUtils::findClosestPointOnSegment(point, *(*l), pl);
            double distance = MathUtils::distance(pl, point);
            if (distance <= min) {
                result = *(*l);
                min = distance;
            }
        }
    }
}

PointF GisData::findClosestPointOnLine(PointF &p) {
    PointF result(-1, -1);
    if (lines.size() == 0) {
        return p;
    }

    double min = 1000000.0;
    for (list<GisLine *>::iterator l = lines.begin(); l != lines.end(); l++) {
        PointF point;
        MathUtils::findClosestPointOnSegment(p, *(*l), point);
        double distance = MathUtils::distance(p, point);
        if (distance <= min) {
            result = point;
            min = distance;
        }

    }
    return result;
}

const GisLine &GisData::findGisLine(const PointF &p1) {
    for (list<GisLine *>::iterator l = lines.begin(); l != lines.end(); l++) {
        PointF point;
        MathUtils::findClosestPointOnSegment(p1, *(*l), point);
        double d = MathUtils::distance(p1, point);
        if (d == 0) {
            return *(*l);
        }
    }
    return GisLine::Null;
}
