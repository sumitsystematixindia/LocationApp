#ifndef Instruction__H
#define Instruction__H

#include "Location.h"
#include "GisSegment.h"


class Instruction {
public:
    virtual ~Instruction() { }

    static const Instruction *NULL_INSTRUCTION;

    virtual void getImage(list<int> &) const = 0;

    virtual void addImage(int image) = 0;

    virtual const list<int> &getText() const = 0;

    virtual void addText(int text) = 0;

    virtual void setLastText(int text) = 0;

    virtual void getSound(list<string> &) const = 0;

    virtual void addSound(const string &sound) = 0;

    virtual const Location &getLocation() const = 0;

    virtual void setLocation(const Location &location) = 0;

    virtual const GisSegment *getSegment() const = 0;

    virtual void setSegment(GisSegment *segment) = 0;

    virtual bool hasPlayed() const = 0;

    virtual void setPlayed(bool played) = 0;

    virtual void addInstruction(Instruction *instruction) = 0;

    virtual const list<int> &getSingleImage() const = 0;

    virtual const list<string> &getSingleSound() const = 0;

};

#endif //Instruction__H
