/*
 * AssociativeData.cpp
 *
 *      Author: Owner
 */

#include "AssociativeData.h"
#include "MathUtils.h"

bool AssociativeData::isInitialized = false;
vector<float> AssociativeData::topK;

AssociativeData::AssociativeData() {
    // TODO Auto-generated constructor stub
}

AssociativeData::~AssociativeData() {
    // TODO Auto-generated destructor stub
}

float AssociativeData::normalDistance(vector<float> &other, vector<float> &otherregular) {
    float w = 1.0f;
    float result = 0;
    int len = (int) min(normalizedvector.size(), other.size());
    for (int i = 0; i < len; i++) {
        w = 1.0f; //w = MathUtils::WifiThreshold(other[i], i);
        result += w * sqrt((otherregular[i] - normalizedvector[i]) *
                           (otherregular[i] - normalizedvector[i]));
        //	w = 1.0f;
    }
    return result;
}


float AssociativeData::normalDistance(vector<float> &other, vector<float> &otherregular,
                                      float closeDevicesThreshold, float closeDeviceWeight,
                                      int kTopLevelThr) {

    if (kTopLevelThr > 0 && !isInitialized) {
        computeTopKlevels(other, kTopLevelThr);
    }

    float w = 1.0f;
    float result = 0;
    int len = (int) min(normalizedvector.size(), other.size());
    for (int i = 0; i < len; i++) {

        if (kTopLevelThr > 0) {
            if (find(topK.begin(), topK.end(), i) != topK.end()) {
                w = MathUtils::WifiThreshold(other[i], i, closeDevicesThreshold, closeDeviceWeight);
            }
            else {
                w = 0.0f;
            }
        } else {
            w = MathUtils::WifiThreshold(other[i], i, closeDevicesThreshold, closeDeviceWeight);
            //w = getWByIndex(i);
        }

        result += w * sqrt((otherregular[i] - normalizedvector[i]) *
                           (otherregular[i] - normalizedvector[i]));
        //w = 1.0f;
    }
    return result;


}


float AssociativeData::normalDistance(vector<float> &other, vector<float> &otherregular,
                                      float closeDevicesThreshold, float closeDeviceWeight,
                                      int kTopLevelThr, int levelLowerBound, bool isFirstTime) {

    if (kTopLevelThr > 0 && !isInitialized) {
        computeTopKlevels(other, kTopLevelThr);
    }

    float w = 1.0f;
    float result = 0;
    int len = (int) min(normalizedvector.size(), other.size());
    for (int i = 0; i < len; i++) {
        if (other[i] != -127) {
            if (kTopLevelThr > 0) {
                if (find(topK.begin(), topK.end(), i) != topK.end() &&
                    (other[i] > levelLowerBound || isFirstTime)) {
                    w = MathUtils::WifiThreshold(other[i], i, closeDevicesThreshold,
                                                 closeDeviceWeight);
                }
                else {
                    w = 0.0f;
                }
            } else {
                w = MathUtils::WifiThreshold(other[i], i, closeDevicesThreshold, closeDeviceWeight);
                //w = getWByIndex(i);
            }


            result += w * sqrt((otherregular[i] - normalizedvector[i]) *
                               (otherregular[i] - normalizedvector[i]));
            //			w = 1.0f;
        }
    }
    return result;

}


void AssociativeData::clearTopK() {
    isInitialized = false;
}


struct LessThanOperator {
    bool operator()(const LevelIndexObj &lhs, const LevelIndexObj &rhs) const {
        return lhs.level < rhs.level;
    }
};


void AssociativeData::computeTopKlevels(vector<float> &other, int k) {

    if (k <= 0 || other.size() == 0) {
        return;
    }

    topK.clear();


    isInitialized = true;


    std::priority_queue<LevelIndexObj, std::vector<LevelIndexObj>, LessThanOperator> pQ;

    if (k >= other.size()) { // if k chosen to be bigger than the other vector length
        for (int i = 0; i < other.size(); i++) {
            topK.push_back(i);
        }
        return;
    }


    for (int i = 0; i < other.size(); i++) {
        LevelIndexObj lIObj;
        lIObj.indx = i;
        lIObj.level = other[i];
        pQ.push(lIObj);
    }


    for (int j = 0; j < k; j++) {
        if (!pQ.empty()) {
            topK.push_back(pQ.top().indx);
            pQ.pop();
        }

    }
    //return topK;
}

