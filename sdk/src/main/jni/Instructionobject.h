#ifndef Instructionobject__H
#define Instructionobject__H

#include <vector>
#include "Instruction.h"

class Instructionobject : public Instruction {
private:
    GisSegment *segment;
    list<Instruction *> instructions;
    list<int> images;
    list<int> texts;
    list<string> sounds;
    bool mPlayed;
public:
    Location location;

    Instructionobject();

    virtual ~Instructionobject();

    Instructionobject(list<int> &img, list<int> &t,
                      list<string> &s, const Location &loc);

    virtual void getImage(list<int> &) const;

    virtual void addImage(int image) {
        images.push_back(image);
    }

    virtual const list<int> &getText() const {
        return texts;
    }

    virtual void addText(int text) {
        texts.push_back(text);
    }

    virtual void setLastText(int text) {
        texts.clear();
        texts.push_back(text);
    }

    virtual void getSound(list<string> &result) const;

    virtual void addSound(const string &sound);

    virtual const Location &getLocation() const {
        return location;
    }

    virtual void setLocation(const Location &location) {
        this->location = location;
    }

    virtual GisSegment *getSegment() const {
        return segment;
    }

    virtual void setSegment(GisSegment *segment) {
        this->segment = segment;
    }

    virtual bool hasPlayed() const {
        return mPlayed;
    }

    void setPlayed(bool played) {
        mPlayed = played;
    }

    void addInstruction(Instruction *instruction);

    virtual const list<int> &getSingleImage() const {
        return images;
    }


    virtual const list<string> &getSingleSound() const {

        return sounds;
    }
};

#endif // Instructionobject__H
