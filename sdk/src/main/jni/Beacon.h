/*
 * WlBlip.h
 *
 *  Created on: 29 ���� 2013
 *      Author: Owner
 */

#ifndef BEACON_H_
#define BEACON_H_

#include <string>

using namespace std;

class Beacon {
public:

    string BSSID;
    int level;
    float x;
    float y;
    int z;

    Beacon();


};

#endif /* BEACON_H_ */

