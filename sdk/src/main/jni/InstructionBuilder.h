#ifndef InstructionBuilder__H
#define InstructionBuilder__H

#include "NavigationPath.h"
#include "Instruction.h"

class InstructionBuilder {
private:

    static InstructionBuilder *instance;
    list<Instruction *> currentInstructions;
    Instruction *nextInstruction;
    list<Instruction *> currentMergedInstructions;

    InstructionBuilder();

public:
    static InstructionBuilder &getInstance();

    static void releaseInstance();

    void getInstractions(const NavigationPath &nav, list<Instruction *> &instructions);

    void getInstractions(const NavigationPath &nav, list<Instruction *> &instructions,
                         int last_instruction_id);

    void mergeInstructions(const list<Instruction *> &instructions);

    void getAngleInstractions(const list<GisSegment *> &path, list<Instruction *> &);

    bool isJunction(const GisPoint &p);

    double getsize(const GisSegment &s);

    const Instruction &findCloseInstruction(const PointF &p1);

    const Instruction &findCloseMergedInstruction(const PointF &p1);

    GisSegment *findSegment(const PointF &p1);

    double distancefromsegment(const PointF &p, const GisSegment &s);

    list<Instruction *> &getCurrentInstructions() {
        return currentInstructions;
    }

    void setCurrentInstructions(list<Instruction *> &currentInstructions) {
        this->currentInstructions = currentInstructions;
    }

    Instruction *getNextInstruction() {
        return nextInstruction;
    }

    void setNextInstruction(Instruction *nextInstruction) {
        this->nextInstruction = nextInstruction;
    }

    list<Instruction *> &getCurrentMergedInstructions() {
        return currentMergedInstructions;
    }

    void setCurrentMergedInstructions(list<Instruction *> &currentMergedInstructions) {
        this->currentMergedInstructions = currentMergedInstructions;
    }

    string to_string(int num);
};

#endif // InstructionBuilder__H
