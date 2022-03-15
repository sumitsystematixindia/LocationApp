#include "Instructionobject.h"


const Instruction *Instruction::NULL_INSTRUCTION = new Instructionobject;

Instructionobject::Instructionobject() :
        segment(NULL),
        mPlayed(false) {
    instructions.push_back(this);

}


Instructionobject::~Instructionobject() {
    for (list<Instruction *>::iterator iter = instructions.begin();
         iter != instructions.end(); iter++) {
        if (this != *iter)
            delete *iter;
    }
}

Instructionobject::Instructionobject(list<int> &img, list<int> &t, list<string> &s,
                                     const Location &loc) :
        segment(NULL),
        mPlayed(false) {
    images = img;
    texts = t;
    sounds = s;
    location = loc;
}

void Instructionobject::getImage(list<int> &result) const {
    result.clear();
    for (list<Instruction *>::const_iterator iter = instructions.begin();
         iter != instructions.end(); iter++) {
        const list<int> &img = (*iter)->getSingleImage();
        result.insert(result.end(), img.begin(), img.end());
    }

}


void Instructionobject::getSound(list<string> &result) const {
    result.clear();

    int len = (int) instructions.size();
    if (len > 0) {
        int index = 0;
        list<Instruction *>::const_iterator iter = instructions.begin();
        for (; index < (len - 1); iter++, index++) {

            const list<string> &sounds = (*iter)->getSingleSound();
            result.insert(result.end(), sounds.begin(), sounds.end());
            result.push_back("and_then");

        }
        const list<string> &sounds = (*iter)->getSingleSound();
        result.insert(result.end(), sounds.begin(), sounds.end());

    }
}

void Instructionobject::addSound(const string &sound) {
    sounds.push_back(sound);
}


void Instructionobject::addInstruction(Instruction *instruction) {
    instructions.push_back(instruction);
}

