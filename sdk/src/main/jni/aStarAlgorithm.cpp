#include "aStarAlgorithm.h"
#include "aStarMath.h"
#include "aStarData.h"
#include <sstream>


const string aStarAlgorithm::TAG("aStarAlgorithm");

aStarAlgorithm::aStarAlgorithm(const GisPoint &s, const GisPoint &e) :
        realStart(s),
        realEnd(e) {
    GisPoint p1;
    aStarMath::findPoint(s, p1);
    GisPoint p2;
    aStarMath::findPoint(e, p2);
    setStartPoint(p1);
    setEndPoint(p2);
}

void aStarAlgorithm::getPath(list<GisSegment *> &path) {

    //printf("Start A*");


    GisSegment *startSegment = findStartLowF(getStartPoint().Segments,
                                             getEndPoint());

    //printf("Stat Segment ID:%s", to_string(startSegment->getId()).c_str());

    open.push_back(startSegment);

    //ADIAprintf("Begine Parents:%s", parents);

    // open.remove(startSegment);
    // closed.add(startSegment);

//TODO ADIA    printf("Begine open: %s", open);
//TODO ADIA    printf("Begine closed:" + closed);

    buildpaths(*startSegment, path);

}

void aStarAlgorithm::buildpaths(const GisSegment &startSegment, list<GisSegment *> &minpath) {
    minpath.clear();
    Log::getInstance().debug(TAG,
                             "Enter, List<GisSegment> buildpaths(GisSegment startSegment)");

    GisSegment *Segment;    //= (GisSegment*)&startSegment;
    try {
        while (!(open.size() == 0)) {
            Segment = findLowF(open, getEndPoint());

            //printf("Find Next Candidate ID: %s", to_string(Segment->getId()).c_str());
            if (aStarMath::calcH(*Segment, *endPoint) == 0) {
                //printf("We are at the END :-)");

                // done return path
                minpath.push_back(Segment);
                GisSegment *currentsegment = Segment;
                GisSegment *psegment;   // = currentsegment;
                while (currentsegment != &startSegment) {
                    const GisPoint &p1 = currentsegment->getLine().getPoint1();
                    const GisPoint &p2 = currentsegment->getLine().getPoint2();
                    const GisPoint &startp = getStartPoint().getPoint();
                    psegment = NULL;
                    if (parents.find(currentsegment->getId()) != parents.end())
                        psegment = parents[currentsegment->getId()];
                    // if((psegment == null) || (p1 == startp) || (p2 ==
                    // startp)) {
                    if ((psegment == NULL)
                        || (aStarMath::findDistance(p1, startp) == 0)
                        || (aStarMath::findDistance(p2, startp) == 0)) {
                        break;
                    }
                    minpath.push_front(psegment);
                    currentsegment = psegment;
                }
                //printf("!!!!!!!!!!!!!!!!!!");
                //printf("!!!!!!!!!!!!!!!!!!");
                //printf("!!!!!!!!!!!!!!!!!!");
                //printf("Best path");
//TODO ADIA                printf(minpath);
                //printf("!!!!!!!!!!!!!!!!!!");
                //printf("!!!!!!!!!!!!!!!!!!");

                setSegmentsDirection(minpath);

                //printf("fix direction");
//ADIA                printf(minpath);

                // fixEdges(minpath);
                return;

            }


            open.remove(Segment);
            closed.push_back(Segment);
            list<GisSegment *> &neighbors = aStarData::getInstance().Neighbors[Segment->getId()];
            //printf("Iterating throught neighbors: ");
//ADIA TODO            printf(neighbors);

            for (list<GisSegment *>::iterator s = neighbors.begin(); s != neighbors.end(); s++) {
                double g = (*s)->getG();
                double tentative_g_score = Segment->getG() + (*s)->getWeight();
                if (tentative_g_score >= g &&
                    closed.end() != find(closed.begin(), closed.end(), *s)) {
                    continue;
                } else if ((find(open.begin(), open.end(), *s) == open.end()) ||
                           tentative_g_score < g) {
                    (*s)->setG(tentative_g_score);

                    (*s)->setParent(Segment->getId());
                    parents[(*s)->getId()] = Segment;

                    if (find(open.begin(), open.end(), *s) == open.end()) {
                        open.push_back((*s));
                    }
                }
            }

        }
        Log::getInstance().debug(TAG,
                                 "Exit, List<GisSegment> buildpaths(GisSegment startSegment)");
    } catch (exception t) {

        Log::getInstance().error(TAG, t.what());
    }


}

GisSegment *aStarAlgorithm::findLowF(const list<GisSegment *> &open, const aStarPoint &epoint) {

    double minf = 10000000;
    GisSegment *segment = NULL;
    GisSegment *parent = NULL;
    try {
        for (list<GisSegment *>::const_iterator s = open.begin(); s != open.end(); s++) {
            Log::getInstance()
                    .debug(TAG,
                           "Enter, findLowF(List<GisSegment> open, aStarPoint epoint)");
            // ADIA - Watch the Map in C++ when getting a non existing value the map inseerted the item with NULL value...
            bool parentExists = parents.find((*s)->getId()) != parents.end();
            double g = 0;
            if (parentExists && (parent = parents[(*s)->getId()]) != NULL) {
                g = parent->getG() + (*s)->getWeight();
            } else {
                g = (*s)->getWeight();
            }

            // s.setG(g);
            double h = aStarMath::calcH(*(*s), epoint);
            double f = g + h;
            if (f < minf) {
                minf = f;
                segment = (*s);
            }
            Log::getInstance()
                    .debug(TAG,
                           "Exit, findLowF(List<GisSegment> open, aStarPoint epoint)");
        }
    } catch (exception t) {

        Log::getInstance().error(TAG, t.what());
    }

    return segment;
}

GisSegment *aStarAlgorithm::findStartLowF(const list<GisSegment *> &segments,
                                          const aStarPoint &epoint) {
    double minf = 10000000;
    GisSegment *segment = NULL;
    try {
        Log::getInstance()
                .debug(TAG,
                       "Enter, GisSegment findStartLowF(List<GisSegment> segments)");
        for (list<GisSegment *>::const_iterator s = segments.begin(); s != segments.end(); s++) {
            double g = (*s)->getWeight();
            (*s)->setG(g);
            double h = aStarMath::calcH(*(*s), epoint);
            double f = g + h;
            if (f < minf) {
                minf = f;
                segment = (*s);
            }
            Log::getInstance()
                    .debug(TAG,
                           "Exit, GisSegment findStartLowF(List<GisSegment> segments)");
        }
    } catch (exception t) {

        Log::getInstance().error(TAG, t.what());
    }
    return segment;
}

void aStarAlgorithm::fixEdges(list<GisSegment *> &path) {
    list<GisSegment *> &result = path;
    GisSegment *starts = aStarMath::findCloseSegment(realStart);
    GisSegment *ends = aStarMath::findCloseSegment(realEnd);
    GisPoint startp;
    aStarMath::findClosePointOnSegment(realStart, *starts, startp);
    GisPoint endp;
    aStarMath::findClosePointOnSegment(realEnd, *ends, endp);
    bool samefakepoint = isSameFakePoint();
    if (starts != result.front()) {
        if (samefakepoint && result.size() == 1) {
            result.clear();
        }
        result.push_front(starts);
    }

    if (ends != result.back()) {
        ends->setParent(result.back()->getId());
        parents[ends->getId()] = result.back();
        result.push_back(ends);
    }

    setSegmentsDirection(result);


    list<GisSegment *>::iterator pStart = result.begin();
    list<GisSegment *>::reverse_iterator pEnd = result.rbegin();
    GisSegment *pFirstSeg = *pStart++;

    GisSegment *pSecSeg = *pStart;

    GisSegment *pLastSeg = *pEnd++;
    GisSegment *pLastMinusSeg = *pEnd;

    if (result.size() > 1) {

        GisLine l1(startp, pSecSeg->getLine().getPoint1(), pFirstSeg->getLine().getZ());
        pFirstSeg->setLine(l1);
        pFirstSeg->setWeight(pFirstSeg->calcweight());


        GisLine l2(pLastMinusSeg->getLine().getPoint2(), endp, pLastSeg->getLine().getZ());
        pLastSeg->setLine(l2);
        pLastSeg->setWeight(pLastSeg->calcweight());

    } else if (result.size() == 1) {
        GisLine l(startp, endp, result.front()->getLine().getZ());
        result.front()->setLine(l);
        result.front()->setWeight(result.front()->calcweight());

    }
}

bool aStarAlgorithm::isSameFakePoint() {
    bool result = false;
    if (startPoint->getPoint().getX() == endPoint->getPoint().getX() &&
        startPoint->getPoint().getY() == endPoint->getPoint().getY()) {
        result = true;
    }
    return result;
}

void aStarAlgorithm::setSegmentsDirection(list<GisSegment *> &minpath) {
    try {
        Log::getInstance()
                .debug(TAG,
                       "Enter, List<GisSegment> setSegmentsDirection(List<GisSegment> minpath)");
        for (list<GisSegment *>::iterator s = minpath.begin(); s != minpath.end(); s++) {
            GisSegment *pSeg = *s;
            GisPoint s1 = pSeg->getLine().getPoint1();
            GisPoint s2 = pSeg->getLine().getPoint2();
            if (pSeg->getParent() != -1 && parents.find(pSeg->getId()) != parents.end()) {
                GisSegment *parent = parents[pSeg->getId()];
                const GisPoint &p1 = parent->getLine().getPoint1();
                const GisPoint &p2 = parent->getLine().getPoint2();
                if ((aStarMath::findDistance(s2, p1) == 0)
                    || (aStarMath::findDistance(s2, p2) == 0)) {
                    pSeg->getLine().setPoint1(s2);
                    pSeg->getLine().setPoint2(s1);
                }
            } else {
                const GisPoint &start1 = getStartPoint().getPoint();
                if ((aStarMath::findDistance(s2, start1) == 0)) {
                    pSeg->getLine().setPoint1(s2);
                    pSeg->getLine().setPoint2(s1);
                }
            }
            Log::getInstance()
                    .debug(TAG,
                           "Exit, List<GisSegment> setSegmentsDirection(List<GisSegment> minpath)");
        }
    } catch (exception t) {
        Log::getInstance().error(TAG, t.what());
    }
    return;
}

// public class CustomComparator implements Comparator<GisSegment>
// {
// @Override
// public int compare(GisSegment o1, GisSegment o2) {
// Integer w1 = (int) o1.getWeight();
// int w2 = (int) o2.getWeight();
// return w1.compareTo(w2);
// }
// }

aStarPoint &aStarAlgorithm::getStartPoint() {
    return *startPoint;
}

void aStarAlgorithm::setStartPoint(const GisPoint &p1) {
    aStarPoint *point = NULL;
    try {
        Log::getInstance().debug(TAG, "Enter, setStartPoint(GisPoint p1");
        list<aStarPoint *> &tree = aStarData::getInstance().segmentTree;
        for (list<aStarPoint *>::iterator p = tree.begin(); p != tree.end(); p++) {
            if (p1 == (*p)->getPoint()) {
                point = (*p);
                break;
            }
            Log::getInstance().debug(TAG, "Exit, setStartPoint(GisPoint p1");
        }
    } catch (exception t) {
        Log::getInstance().error(TAG, t.what());
    }
    startPoint = point;
}

aStarPoint &aStarAlgorithm::getEndPoint() {
    return *endPoint;
}

void aStarAlgorithm::setEndPoint(const GisPoint &p2) {
    aStarPoint *point = NULL;
    try {
        Log::getInstance().debug(TAG, "Enter, setEndPoint(GisPoint p2");
        list<aStarPoint *> &tree = aStarData::getInstance().segmentTree;
        for (list<aStarPoint *>::iterator p = tree.begin(); p != tree.end(); p++) {
            if (p2 == (*p)->getPoint()) {
                point = (*p);
                break;
            }
            Log::getInstance().debug(TAG, "Exit, setEndPoint(GisPoint p2");
        }
    } catch (exception t) {
        Log::getInstance().error(TAG, t.what());
    }
    endPoint = point;
}

string aStarAlgorithm::to_string(int num) {
    ostringstream convert; // stream used for the conversion

    convert <<
    num; // insert the textual representation of �Number� in the characters    in the stream

    return convert.str();
}



