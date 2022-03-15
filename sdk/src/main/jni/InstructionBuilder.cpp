#include "InstructionBuilder.h"
#include "FloorNavigationPath.h"
#include "Instructionobject.h"
#include "PropertyHolder.h"
#include "aStarMath.h"
#include "aStarData.h"
#include "FacilityConf.h"
//#include  <android/log.h>

#include <sstream>

struct R {
    struct drawable {
        static const int continue_straight = 0;
        static const int turn_left = 1;
        static const int turn_right = 3;
        static const int arrow = 4;
        static const int go_up = 5;
        static const int go_down = 6;
        static const int destination = 10;

    };
    struct string {
        static const int continue_straight = 0;
        static const int turn_left = 1;
        static const int left_hall = 2;
        static const int turn_right = 3;
        static const int right_hall = 4;
        static const int elvator_up = 5;
        static const int elvator_down = 6;
        static const int destination = 7;
        static const int empty_instruction = 99;
    };
};

InstructionBuilder *InstructionBuilder::instance = NULL;

InstructionBuilder::InstructionBuilder() :
        nextInstruction(NULL) {

}

InstructionBuilder &InstructionBuilder::getInstance() {
    if (instance == NULL) {
        instance = new InstructionBuilder();

    }
    return *instance;
}

void InstructionBuilder::releaseInstance() {
    if (instance != NULL) {
        delete instance;
        instance = NULL;
    }
}


void InstructionBuilder::getInstractions(const NavigationPath &nav,
                                         list<Instruction *> &instructions) {

//    instructions.clear();
//    getCurrentInstructions().clear();
//
//
//
//    const list<FloorNavigationPath*>& paths = nav.getFullPath();
//    if (paths.size() == 0)
//    {
//        setCurrentInstructions(instructions);
//        return;
//    }
//
//
//
//
//    list<Instruction*> instList;
//    getAngleInstractions((*(paths.begin()))->getPath(), instList);
//
//   // __android_log_print(ANDROID_LOG_DEBUG, "instList.size","%d", instList.size());
//
//    instructions.insert(instructions.end(), instList.begin(), instList.end());
//    int counter = 0;
//    for (list<FloorNavigationPath*>::const_iterator o = paths.begin(); o != paths.end(); o++)
//    {
//        FloorNavigationPath* pFloor = *o;
//        const list<GisSegment*>& path = pFloor->getPath();
//        counter++;
//
//        list<GisSegment*>& Secoendpath = GisSegment::EMPTY_SEGMENT_LIST;
//        FloorNavigationPath* pSecondFloor = NULL;
//
//        if (paths.size() > counter)
//        {
//            // Move to next item
//            o++;
//            pSecondFloor = *o;
//            Secoendpath = pSecondFloor->getPath();
//            // Move back to origin
//            o--;
//        }
//        if (paths.size() > counter && Secoendpath.size() > 0 )
//        {
//
//            int z1 = (int)pFloor->getZ();
//            int z2 = (int) pSecondFloor->getZ();
//            Instructionobject* elavator = new Instructionobject();
//            GisSegment* lastSegment = *path.rbegin();
//
//           if( lastSegment != NULL){
//					elavator->setSegment(lastSegment);
//					//				elavator.location = aStarData.getInstance().getCurrentPath().getElevator();
//					elavator->location.setX(lastSegment->getLine().getPoint2().getX());
//					elavator->location.setY(lastSegment->getLine().getPoint2().getY());
//					elavator->location.setZ(lastSegment->getLine().getZ());
//
//				if (z2 > z1) {
//					elavator->addImage(R::drawable::go_up);
//					elavator->addText(R::string::elvator_up);
//					elavator->addSound("elevator_up");
//					elavator->addSound(string("floor") + to_string(z2));
//				} else if (z2 < z1) {
//					elavator->addImage(R::drawable::go_down);
//					elavator->addText(R::string::elvator_down);
//					elavator->addSound("elvator_down");
//					elavator->addSound("floor" + to_string(z2));
//				}
//
//				instructions.push_back(elavator);
//				list<Instruction*> secondInstructions;
//				getAngleInstractions(Secoendpath, secondInstructions);
//				instructions.insert(instructions.end(), secondInstructions.begin(), secondInstructions.end());
//           }
//            // jusmp ahead as we already consumed this second instruction
//            o++;
//        } else {
//            Instructionobject* end = new Instructionobject();
//            GisSegment* lastSegment = *path.rbegin();
//
//					end->location.setX(lastSegment->getLine().getPoint2().getX());
//					end->location.setY(lastSegment->getLine().getPoint2().getY());
//					end->location.setZ(lastSegment->getLine().getZ());
//					end->setSegment(lastSegment);
//					end->addImage(R::drawable::destination);
//					end->addText(R::string::destination);
//					end->addSound("destination");
//					instructions.push_back(end);
//
//        }
//    }
//
//
//    setCurrentInstructions(instructions);



    // mergeInstructions(instructions);  //XXX ASTAR
    //		List<Instruction> a = getCurrentMergedInstructions();

    getInstractions(nav, instructions, R::string::destination);
}


//XXX new api
void InstructionBuilder::getInstractions(const NavigationPath &nav,
                                         list<Instruction *> &instructions,
                                         int last_instruction_id) {

    instructions.clear();
    getCurrentInstructions().clear();


    const list<FloorNavigationPath *> &paths = nav.getFullPath();
    if (paths.size() == 0) {
        setCurrentInstructions(instructions);
        return;
    }


    list<Instruction *> instList;
    getAngleInstractions((*(paths.begin()))->getPath(), instList);

    // __android_log_print(ANDROID_LOG_DEBUG, "instList.size","%d", instList.size());

    instructions.insert(instructions.end(), instList.begin(), instList.end());
    int counter = 0;
    for (list<FloorNavigationPath *>::const_iterator o = paths.begin(); o != paths.end(); o++) {
        FloorNavigationPath *pFloor = *o;
        const list<GisSegment *> &path = pFloor->getPath();
        counter++;

        list<GisSegment *> &Secoendpath = GisSegment::EMPTY_SEGMENT_LIST;
        FloorNavigationPath *pSecondFloor = NULL;

        if (paths.size() > counter) {
            // Move to next item
            o++;
            pSecondFloor = *o;
            Secoendpath = pSecondFloor->getPath();
            // Move back to origin
            o--;
        }
        if (paths.size() > counter && Secoendpath.size() > 0) {

            int z1 = (int) pFloor->getZ();
            int z2 = (int) pSecondFloor->getZ();
            Instructionobject *elavator = new Instructionobject();
            GisSegment *lastSegment = *path.rbegin();

            if (lastSegment != NULL) {
                elavator->setSegment(lastSegment);
                //				elavator.location = aStarData.getInstance().getCurrentPath().getElevator();
                elavator->location.setX(lastSegment->getLine().getPoint2().getX());
                elavator->location.setY(lastSegment->getLine().getPoint2().getY());
                elavator->location.setZ(lastSegment->getLine().getZ());

                if (z2 > z1) {
                    elavator->addImage(R::drawable::go_up);
                    elavator->addText(R::string::elvator_up);
                    elavator->addSound("elevator_up");
                    elavator->addSound(string("floor") + to_string(z2));
                } else if (z2 < z1) {
                    elavator->addImage(R::drawable::go_down);
                    elavator->addText(R::string::elvator_down);
                    elavator->addSound("elvator_down");
                    elavator->addSound("floor" + to_string(z2));
                }

                instructions.push_back(elavator);
                list<Instruction *> secondInstructions;
                getAngleInstractions(Secoendpath, secondInstructions);
                instructions.insert(instructions.end(), secondInstructions.begin(),
                                    secondInstructions.end());
            }
            // jusmp ahead as we already consumed this second instruction
            o++;
        } else {
            Instructionobject *end = new Instructionobject();
            GisSegment *lastSegment = *path.rbegin();

            end->location.setX(lastSegment->getLine().getPoint2().getX());
            end->location.setY(lastSegment->getLine().getPoint2().getY());
            end->location.setZ(lastSegment->getLine().getZ());
            end->setSegment(lastSegment);
            end->addImage(R::drawable::destination);
            end->addText(R::string::destination);
            end->addSound("destination");
            instructions.push_back(end);

        }
    }

    if (!instructions.empty() && last_instruction_id != R::string::destination) {
        instructions.back()->setLastText(last_instruction_id);
    }

    setCurrentInstructions(instructions);
    // mergeInstructions(instructions);  //XXX ASTAR
    //		List<Instruction> a = getCurrentMergedInstructions();
}


void InstructionBuilder::mergeInstructions(const list<Instruction *> &instructions) {
    getCurrentMergedInstructions().clear();
    Instruction *currentInst = NULL;
    for (list<Instruction *>::const_iterator instruction = instructions.begin();
         instruction != instructions.end(); instruction++) {
        if (currentInst == NULL) {
            currentInst = *instruction;
            continue;
        }

        const GisSegment *s = (*instruction)->getSegment();

        if (getsize(*s) > PropertyHolder::getInstance().getInstructionsDistance()) {
            getCurrentMergedInstructions().push_back(currentInst);
            currentInst = *instruction;
        } else {
            currentInst->addInstruction(*instruction);

        }
//        list<Instruction*>& merged = getCurrentMergedInstructions();
        // Check if this item already exists
//        if (std::find(merged.begin(), merged.end(), currentInst) == merged.end())
//        {
//            merged.push_back(currentInst);
//        }
    }
}

void InstructionBuilder::getAngleInstractions(const list<GisSegment *> &path,
                                              list<Instruction *> &instructions) {

    //__android_log_print(ANDROID_LOG_DEBUG, "path.size","%d", path.size());

    instructions.clear();
    int segmentsangleLength = (int) path.size();
    float *segmentsangle = new float[segmentsangleLength];
    int counter = 0;
    for (list<GisSegment *>::const_iterator s = path.begin(); s != path.end(); s++) {
        segmentsangle[counter] = aStarMath::getSegmentAngle(**s);
        counter++;
    }
    counter = 0;
    Instructionobject *current;     //= new Instructionobject();
    for (list<GisSegment *>::const_iterator ss = path.begin(); ss != path.end(); ss++) {
        GisSegment *s = *ss;
        if (counter < segmentsangleLength - 1) {
            //				if (getsize(s) > PropertyHolder.getInstance().getInstructionsDistance()) {
            current = new Instructionobject();
            current->location.setX(s->getLine().getPoint2().getX());
            current->location.setY(s->getLine().getPoint2().getY());
            current->location.setZ(s->getLine().getZ());
            current->setSegment(s);
            //				}

            float sangle = segmentsangle[counter];
            float nangle = segmentsangle[counter + 1];
            float angle = aStarMath::getAngleToNext(sangle, nangle);

            // __android_log_print(ANDROID_LOG_DEBUG, "angle","%f", angle);

            if (angle >= -15 && angle <= 15) {
                current->addImage(R::drawable::continue_straight);
                current->addText(R::string::continue_straight);
                current->addSound("straight");
                instructions.push_back(current);
                // __android_log_print(ANDROID_LOG_DEBUG, "counter","%d", counter);
            } else if (angle >= -135 && angle <= -45) {
                current->addImage(R::drawable::turn_left);
                current->addText(R::string::turn_left);
                current->addSound("turn_left");
                instructions.push_back(current);
                // __android_log_print(ANDROID_LOG_DEBUG, "counter","%d", counter);
            } else if (angle > -45 && angle < -15) {
                //  if (isJunction(s->getLine().point2)) {
                current->addImage(R::drawable::arrow);
                current->addText(R::string::left_hall);
                current->addSound("left_hall");
                instructions.push_back(current);
                // __android_log_print(ANDROID_LOG_DEBUG, "counter","%d", counter);
                //  }
            } else if (angle >= 45 && angle <= 135) {
                current->addImage(R::drawable::turn_right);
                current->addText(R::string::turn_right);
                current->addSound("turn_right");
                instructions.push_back(current);
                // __android_log_print(ANDROID_LOG_DEBUG, "counter","%d", counter);
            } else if (angle > 15 && angle < 45) {
                //  if (isJunction(s->getLine().point2)) {
                current->addImage(R::drawable::arrow);
                current->addText(R::string::right_hall);
                current->addSound("right_hall");
                instructions.push_back(current);
                // __android_log_print(ANDROID_LOG_DEBUG, "counter","%d", counter);
                //  }
            }
            else { //XXX A_STAR
                current->addText(R::string::empty_instruction);
                instructions.push_back(current);

            }
        }


        counter++;
    }
    delete[] segmentsangle;
}


bool InstructionBuilder::isJunction(const GisPoint &p) {
    bool result = false;
    list<aStarPoint *> tree = aStarData::getInstance().segmentTree;
    aStarPoint *point = NULL;
    for (list<aStarPoint *>::iterator ap = tree.begin(); ap != tree.end(); ap++) {
        if ((*ap)->getPoint() == p) {
            point = *ap;// ADIA Consider breaking out this loop as we find what we searched for...
        }
    }
    if (point != NULL && point->Segments.size() > 2) {
        result = true;
    }
    return result;

}

double InstructionBuilder::getsize(const GisSegment &s) {
    double result = aStarMath::findDistance(s.getLine().getPoint1(), s.getLine().getPoint2());
    return result / PropertyHolder::getInstance().getPixelsToMeter();
}

const Instruction &InstructionBuilder::findCloseInstruction(const PointF &p1) {
    const Instruction &result = *Instruction::NULL_INSTRUCTION;
    GisSegment *segment = findSegment(p1);
    if (segment != NULL) {
        list<Instruction *> &currentInstructions = getCurrentInstructions();

        int id1 = segment->getId();
        for (list<Instruction *>::iterator i = currentInstructions.begin();
             i != currentInstructions.end(); i++) {
            int id2 = (*i)->getSegment()->getId();
            if (id1 == id2) {
                return **i;
                break;
            }
        }
    }

    return result;
}

const Instruction &InstructionBuilder::findCloseMergedInstruction(const PointF &p1) {
    const Instruction &result = *Instruction::NULL_INSTRUCTION;

    GisSegment *segment = findSegment(p1);
    if (segment != NULL) {
        const list<Instruction *> &mergedInst = getCurrentMergedInstructions();
        for (list<Instruction *>::const_iterator i = mergedInst.begin();
             i != mergedInst.end(); i++) {
            int id1 = segment->getId();
            int id2 = (*i)->getSegment()->getId();
            if (id1 == id2) {
                return **i;
            }
        }
    }
    return result;
}

GisSegment *InstructionBuilder::findSegment(const PointF &p1) {
    int floorz = FacilityConf::getInstance().getSelectedFloor();

    const NavigationPath &nav = aStarData::getInstance().getCurrentPath();

    list<GisSegment *> segList = nav.getPathByZ(floorz);

    for (list<GisSegment *>::iterator s = segList.begin(); s != segList.end(); s++) {
        double d = distancefromsegment(p1, **s);
        if (d == 0) {
            return *s;
        }
    }
    return NULL;
}

double InstructionBuilder::distancefromsegment(const PointF &p, const GisSegment &s) {
    double result = 0;
    GisPoint p1(p.x, p.y, s.getLine().getZ());
    GisPoint p2;
    aStarMath::findClosePointOnSegment(p1, s, p2);
    result = aStarMath::findDistance(p1, p2);
    if (result < 1) {
        result = 0;
    }
    return result;
}

string InstructionBuilder::to_string(int num) {
    ostringstream convert; // stream used for the conversion

    convert <<
    num; // insert the textual representation of �Number� in the characters    in the stream

    return convert.str();
}

