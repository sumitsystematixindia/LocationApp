/*
 * AssociativeData.h
 * *      Author: Owner
 */

#ifndef LevelIndexObj_H
#define LevelIndexObj_H

// using namespace std;



class LevelIndexObj {

private:


public:
    int indx;
    float level;

    LevelIndexObj();

    virtual ~LevelIndexObj();

//	//Overload the < operator.
//		bool operator< (const LevelIndexObj& structstudent1, const LevelIndexObj &structstudent2)
//		{
//			return structstudent1.level > structstudent2.level;
//		}
//		//Overload the > operator.
//		bool operator> (const LevelIndexObj& structstudent1, const LevelIndexObj &structstudent2)
//		{
//			return structstudent1.level < structstudent2.level;
//		}
};

#endif /* LevelIndexObj_H */

