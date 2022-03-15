/*
 * MatrixBinRep.cpp
 *
 *  Created on: 29 ���� 2013
 *      Author: Owner
 */
#if  !defined(WIN32) && !defined (__APPLE__)

#include <sys/endian.h>

#endif

#include "MatrixBinRep.h"

#include <fstream>
#include <iostream>
#include <iterator>

#if  defined (__APPLE__)
#else

#endif

#ifdef WIN32
#include <winsock.h>
#endif


class MyJavaOutputStream {
    ostream &m_os;
    char m_buffer[8];
public:
    MyJavaOutputStream(ostream &os);

    void writeInt(int i);

    void writeShort(short sh);

    void writeLong(long l);

    void writeBool(bool b);

    void writeUTF8(const string &str);

};

class MyJavaInputStream {
    istream &m_is;
    char m_buffer[8];
public:
    MyJavaInputStream(istream &is);

    uint32_t readInt();

    short readShort();

    long readLong();

    bool readBool();

    string readUTF8();

};


MatrixBinRep::MatrixBinRep() {
    // TODO Auto-generated constructor stub
}

MatrixBinRep::MatrixBinRep(list<AssociativeData> &theList,
                           map<string, int> &INDEX_MAP, list<string> &ssidnames,
                           vector<float> &mins, vector<float> &maxs) {
//	this->theList = theList;
//	this->INDEX_MAP = INDEX_MAP;
//	this->ssidnames = ssidnames;
//	this->mins = mins;
//	this->maxs=maxs;

}

MatrixBinRep::~MatrixBinRep() {
    // TODO Auto-generated destructor stub
}

int MatrixBinRep::countNoN127(vector<float> &mvector) {
    int c = 0;

    for (vector<float>::iterator itr = mvector.begin(); itr != mvector.end();
         ++itr) {
        float intv = *itr;
        if (intv != -127.0) {
            c++;
        }
    }
    return c;
}


int MatrixBinRep::countNoNZeros(vector<float> &mvector) {

    int c = 0;

    for (vector<float>::iterator itr = mvector.begin(); itr != mvector.end(); ++itr) {
        float intv = *itr;
        if (intv != 0.0) {
            c++;
        }
    }
    return c;
}


void MatrixBinRep::writeSpecialObject(const string &ofile, list<AssociativeData> &theList,
                                      map<string, int> &INDEX_MAP, list<string> &ssidnames,
                                      vector<float> &mins, vector<float> &maxs,
                                      bool isselectfloor) {


    //__android_log_print(ANDROID_LOG_DEBUG, "MatrixBinRep", "ofile :%s",ofile.c_str());
    ofstream stream;
    stream.open(ofile.c_str(), ios::in | ios::out | ios::binary | ios::trunc);


    if (stream.good()) {
        MyJavaOutputStream dout(stream);

//		dout.writeInt(3456789);
//		string key = "abc:34:1s";
//		const char *keyChar = key.c_str();
//		dout.writeUTF8(key);


        int theList_size = (int) theList.size();
        //__android_log_print(ANDROID_LOG_DEBUG, "MatrixBinRep", "theList_size :%d",theList_size);
        dout.writeInt(theList_size);

        int vec_size = (int) theList.front().mvector.size();
        //__android_log_print(ANDROID_LOG_DEBUG, "MatrixBinRep", "vec_size :%d",vec_size);
        dout.writeInt(vec_size);


        for (list<AssociativeData>::iterator it = theList.begin();
             it != theList.end(); it++) {


            AssociativeData &al = *it;
            int x = roundf(al.point.x);
            dout.writeInt(x);
            //__android_log_print(ANDROID_LOG_DEBUG, "MatrixBinRep", "x :%d",x);
            int y = roundf(al.point.y);
            dout.writeInt(y);
            //__android_log_print(ANDROID_LOG_DEBUG, "MatrixBinRep", "y :%d",y);
            int z;
            if (isselectfloor == true) {
                z = (int) al.Z;
                dout.writeInt(z);
                //__android_log_print(ANDROID_LOG_DEBUG, "MatrixBinRep", "z :%d",z);
            }


            int vcnt = countNoN127(al.mvector);
            dout.writeInt(vcnt);
            //__android_log_print(ANDROID_LOG_DEBUG, "MatrixBinRep", "countNoN127 :%d",vcnt);
            int i = 0;
            for (vector<float>::iterator itr = al.mvector.begin();
                 itr != al.mvector.end(); ++itr) {
                int intv = *itr;
                if (intv != -127.0) {
                    dout.writeInt(i);
                    dout.writeInt(intv);
                    //__android_log_print(ANDROID_LOG_DEBUG, "MatrixBinRep", "mvectorTemp (index, intv) :(%d,%d)",i,intv);
                }
                i++;
            }


            int nvcnt = countNoNZeros(al.normalizedvector);
            //__android_log_print(ANDROID_LOG_DEBUG, "MatrixBinRep", "nonZerosCont :%d",nvcnt);
            dout.writeInt(nvcnt);
            int j = 0;
            for (vector<float>::iterator itr = al.normalizedvector.begin();
                 itr != al.normalizedvector.end(); ++itr) {
                float val = *itr;
                if (val != 0.0) {
                    int intv = (int) (val * 10000);
                    dout.writeInt(j);
                    dout.writeInt(intv);
                    //__android_log_print(ANDROID_LOG_DEBUG, "MatrixBinRep", "normalizedvectorTemp (index, intv) :(%d,%d)",j,intv);
                }
                j++;
            }
        }


        for (vector<float>::iterator itr = mins.begin(); itr != mins.end(); ++itr) {
            int mn1 = (int) *itr;
            dout.writeInt(mn1);
            //__android_log_print(ANDROID_LOG_DEBUG, "MatrixBinRep", "mn1 :%d",mn1);
        }


        for (vector<float>::iterator itr = maxs.begin(); itr != maxs.end();
             ++itr) {
            int mx1 = (int) *itr;
            dout.writeInt(mx1);
            //__android_log_print(ANDROID_LOG_DEBUG, "MatrixBinRep", "mx1 :%d",mx1);
        }


        map<string, int>::iterator iter;
        int map_size = (int) INDEX_MAP.size();
        dout.writeInt(map_size);
        //__android_log_print(ANDROID_LOG_DEBUG, "MatrixBinRep", "INDEX_MAPtemp:map_size %d",map_size);
        for (iter = INDEX_MAP.begin(); iter != INDEX_MAP.end(); ++iter) {
            string key = iter->first;
            int value = iter->second;
            //const char *keyChar = key.c_str();
            dout.writeUTF8(key);
            dout.writeInt(value);
            //__android_log_print(ANDROID_LOG_DEBUG, "MatrixBinRep", "INDEX_MAPtemp (K=%s,V=%d)",key.c_str(),value);
        }


        int ssidnames_size = (int) ssidnames.size();
        dout.writeInt(ssidnames_size);
        //__android_log_print(ANDROID_LOG_DEBUG, "MatrixBinRep", "ssidnames_size %d",ssidnames_size);
        for (list<string>::iterator it = ssidnames.begin();
             it != ssidnames.end(); it++) {
            string ssidn = *it;
            dout.writeUTF8(ssidn);
            //__android_log_print(ANDROID_LOG_DEBUG, "MatrixBinRep", "ssidn %s",ssidn.c_str());
        }
    }

    stream.flush();
    stream.close();

}

void MatrixBinRep::readSpecialObject(const string &ofile, list<AssociativeData> &theList,
                                     map<string, int> &INDEX_MAP, list<string> &ssidnames,
                                     vector<float> &mins, vector<float> &maxs, bool isselectfloor) {

    //__android_log_print(ANDROID_LOG_DEBUG, "MatrixBinRep::readObject", "ofile :%s",ofile.c_str());

    ifstream stream(ofile.c_str(), ios::binary | ios::in);
    if (stream.good()) {

        MyJavaInputStream din(stream);

        int theList_size;
        int vec_size;

        theList_size = din.readInt();
        //__android_log_print(ANDROID_LOG_DEBUG, "MatrixBinRep", "theList_size :%d",theList_size);
        vec_size = din.readInt();
        //__android_log_print(ANDROID_LOG_DEBUG, "MatrixBinRep", "vec_size :%d",vec_size);





        Location p;
        for (int i = 0; i < theList_size; i++) {

            int x = din.readInt();
            //__android_log_print(ANDROID_LOG_DEBUG, "MatrixBinRep", "x :%d",x);

            int y = din.readInt();
            //__android_log_print(ANDROID_LOG_DEBUG, "MatrixBinRep", "y :%d",y);

            int z = 0;
            if (isselectfloor == true) {
                z = din.readInt();
                //__android_log_print(ANDROID_LOG_DEBUG, "MatrixBinRep", "z :%d",z);
            }


            vector<float> mvectorTemp(vec_size, -127);
            int non127Cont = din.readInt();
            //__android_log_print(ANDROID_LOG_DEBUG, "MatrixBinRep", "countNoN127 :%d",non127Cont);
            if (non127Cont != 0) {
                for (int j = 0; j < non127Cont; j++) {
                    int idx = din.readInt();
                    int intv = din.readInt();
                    mvectorTemp.at(idx) = intv;
                    //__android_log_print(ANDROID_LOG_DEBUG, "MatrixBinRep", "mvectorTemp (index, intv) :(%d,%f)",idx,mvectorTemp.at(idx));
                }
            }


            vector<float> normalizedvectorTemp(vec_size, 0);
            int nonZerosCont = din.readInt();
            //__android_log_print(ANDROID_LOG_DEBUG, "MatrixBinRep", "nonZerosCont :%d",nonZerosCont);
            if (nonZerosCont != 0) {
                for (int j = 0; j < nonZerosCont; j++) {
                    int idx = din.readInt();
                    int intv = din.readInt();
                    float val = ((float) intv) / 10000.0f;
                    normalizedvectorTemp.at(idx) = val;
                    //__android_log_print(ANDROID_LOG_DEBUG, "MatrixBinRep", "normalizedvectorTemp (index, intv) :(%d,%f)",idx,normalizedvectorTemp.at(idx));
                }
            }

            AssociativeData data;
            data.point.x = x;
            data.point.y = y;

            if (isselectfloor == true) {
                data.Z = z;
            }
            data.mvector = mvectorTemp;
            data.normalizedvector = normalizedvectorTemp;
            theList.push_back(data);
        }


        for (int j = 0; j < vec_size; j++) {
            int mn1 = din.readInt();
            //__android_log_print(ANDROID_LOG_DEBUG, "MatrixBinRep", "mn1 :%d",mn1);

            mins.push_back(mn1);
        }


        for (int j = 0; j < vec_size; j++) {
            int mx1 = din.readInt();
            //__android_log_print(ANDROID_LOG_DEBUG, "MatrixBinRep", "mx1 :%d",mx1);
            maxs.push_back(mx1);
        }

        int map_size = din.readInt();
        //__android_log_print(ANDROID_LOG_DEBUG, "MatrixBinRep", "INDEX_MAPtemp:map_size %d",map_size);

        for (int j = 0; j < map_size; j++) {
            string key = din.readUTF8();
            int value = din.readInt();
            INDEX_MAP[key] = value;
            //__android_log_print(ANDROID_LOG_DEBUG, "MatrixBinRep", "INDEX_MAPtemp (K=%s,V=%d)",key.c_str(),value);
        }

        int ssidnames_size = din.readInt();

        //__android_log_print(ANDROID_LOG_DEBUG, "MatrixBinRep", "ssidnames_size %d",ssidnames_size);

        for (int j = 0; j < ssidnames_size; j++) {
            string ssidn = din.readUTF8();
            // __android_log_print(ANDROID_LOG_DEBUG, "MatrixBinRep", "ssidn %s",ssidn.c_str());
            ssidnames.push_back(ssidn);
        }


    }

    stream.close();

}


void MatrixBinRep::writeObject(const string &ofile, list<AssociativeData> &theList,
                               map<string, int> &INDEX_MAP, list<string> &ssidnames,
                               vector<float> &mins, vector<float> &maxs, bool isselectfloor) {

//	FILE* file = fopen(ofile.c_str(),"w+");
//
//	    if (file != NULL)
//	    {
//	        fputs("HELLO WORLD!\n", file);
//	        fflush(file);
//	        fclose(file);
//	    }

// -B V=1 APP_OPTIM=debug NDK_DEBUG=1
//		ofstream stream;
//
//		stream.open(ofile.c_str(), ios::in | ios::out | ios::binary | ios::trunc);
//		if(stream.good()){
//			int x=2;
//			stream.write(reinterpret_cast<const char *>(&x), sizeof(int));
//         //  stream << "test";
//		}
//		stream.flush();
//		stream.close();



    //cout << "MatrixBinRep::writeObject" << endl;

    ofstream stream;

    stream.open(ofile.c_str(), ios::in | ios::out | ios::binary | ios::trunc);

    if (stream.good()) {
        MyJavaOutputStream dout(stream);
        int theList_size = (int) theList.size();
        short vec_size = theList.front().mvector.size();

        dout.writeInt(theList_size);

        dout.writeShort(vec_size);

        for (list<AssociativeData>::iterator it = theList.begin();
             it != theList.end(); it++) {
            AssociativeData &al = *it;
            short x = roundf(al.point.x);
            dout.writeShort(x);
            short y = roundf(al.point.y);
            dout.writeShort(y);

            short z;
            if (isselectfloor == true) {
                z = (short) al.Z;
                dout.writeShort(z);
            }

            for (vector<float>::iterator itr = al.mvector.begin();
                 itr != al.mvector.end(); ++itr) {
                int intv = *itr;
                dout.writeInt(intv);
            }

            for (vector<float>::iterator itr = al.normalizedvector.begin();
                 itr != al.normalizedvector.end(); ++itr) {
                float val = *itr;
                int intv = (int) (val * 10000);
                dout.writeInt(intv);
            }
        }

        for (vector<float>::iterator itr = mins.begin(); itr != mins.end();
             ++itr) {
            short mn1 = (short) *itr;
            dout.writeShort(mn1);
        }

        for (vector<float>::iterator itr = maxs.begin(); itr != maxs.end();
             ++itr) {
            short mx1 = (short) *itr;
            dout.writeShort(mx1);
        }

        map<string, int>::iterator iter;

        int map_size = (int) INDEX_MAP.size();

        dout.writeInt(map_size);

        for (iter = INDEX_MAP.begin(); iter != INDEX_MAP.end(); ++iter) {
            string key = iter->first;
            int value = iter->second;
            //const char *keyChar = key.c_str();


            //stream << key << std::endl;
            //stream.write(reinterpret_cast<const char *>(&key), sizeof(key));

            //stream.write(keyChar, key.length());
            dout.writeUTF8(key);
            dout.writeInt(value);

        }

        int ssidnames_size = (int) ssidnames.size();

        dout.writeInt(ssidnames_size);

        for (list<string>::iterator it = ssidnames.begin();
             it != ssidnames.end(); it++) {
            string ssidn = *it;
            //stream.write(reinterpret_cast<const char *>(&ssidn), sizeof(ssidn));
            //	stream.write(ssidn.c_str(), sizeof(char)*ssidn.size());

            //stream << ssidn << std::endl;

//			 const char *ssidnChar = ssidn.c_str();
//			 stream.write(ssidnChar, ssidn.length());



            dout.writeUTF8(ssidn);

        }
    }

    stream.flush();
    stream.close();

}

void MatrixBinRep::readObject(const string &ofile, list<AssociativeData> &theList,
                              map<string, int> &INDEX_MAP, list<string> &ssidnames,
                              vector<float> &mins, vector<float> &maxs, bool isselectfloor) {

    //__android_log_print(ANDROID_LOG_DEBUG, "MatrixBinRep::readObject", "ofile :%s",ofile.c_str());

//	fstream stream(ofile.c_str(), ios::binary | ios::in | ios::trunc);
    ifstream stream(ofile.c_str(), ios::binary | ios::in);
    if (stream.good()) {
        MyJavaInputStream din(stream);
        //	list<AssociativeData> theListTemp;
        //	map<string, int> INDEX_MAPtemp;
        //	list<string> ssidnamesTemp;

        int theList_size;
        short vec_size;
        theList_size = din.readInt();

        //	__android_log_print(ANDROID_LOG_DEBUG, "MatrixBinRep::readObject", "theList_size :%d",theList_size);

        vec_size = din.readShort();

        //	__android_log_print(ANDROID_LOG_DEBUG, "MatrixBinRep::readObject", "vec_size :%d",vec_size);

        Location p;

        for (int i = 0; i < theList_size; i++) {

            short x = din.readShort();
            //	__android_log_print(ANDROID_LOG_DEBUG, "MatrixBinRep::readObject", "x :%d",x);

            short y = din.readShort();
            //		__android_log_print(ANDROID_LOG_DEBUG, "MatrixBinRep::readObject", "y :%d",y);

            short z = 0;
            if (isselectfloor == true) {
                z = din.readShort();
                //__android_log_print(ANDROID_LOG_DEBUG, "MatrixBinRep::readObject", "z :%d",z);
            }

            vector<float> mvectorTemp;

            for (int j = 0; j < vec_size; j++) {
                int intv = din.readInt();
                mvectorTemp.push_back(intv);

                //		__android_log_print(ANDROID_LOG_DEBUG, "MatrixBinRep::readObject", "mvectorTemp.intv :%d",intv);

            }

            vector<float> normalizedvectorTemp;
            for (int j = 0; j < vec_size; j++) {
                int intv = din.readInt();
                float val = ((float) intv) / 10000.0f;
                normalizedvectorTemp.push_back(val);

                //		__android_log_print(ANDROID_LOG_DEBUG, "MatrixBinRep::readObject", "normalizedvectorTemp.intv :%f",intv / 10000.0);
            }

            AssociativeData data;
            data.point.x = x;
            data.point.y = y;

            if (isselectfloor == true) {
                data.Z = z;
            }

            //?
            data.mvector = mvectorTemp;
            data.normalizedvector = normalizedvectorTemp;

            //	theListTemp.push_back(data);
            theList.push_back(data);

            //?
            //	delete &mvectorTemp;
            //	delete &normalizedvectorTemp;
            //	delete &data;
        }

        //	vector<float> minsTemp;
        for (int j = 0; j < vec_size; j++) {
            short mn1 = din.readShort();
            //		__android_log_print(ANDROID_LOG_DEBUG, "MatrixBinRep::readObject", "mn1 :%d",mn1);

            //minsTemp.push_back(mn1);
            mins.push_back(mn1);
        }

        //	vector<float> maxsTemp;
        for (int j = 0; j < vec_size; j++) {
            short mx1 = din.readShort();
            //		__android_log_print(ANDROID_LOG_DEBUG, "MatrixBinRep::readObject", "mx1 :%d",mx1);

            //	maxsTemp.push_back(mx1);
            maxs.push_back(mx1);
        }

        int map_size = din.readInt();
        //	__android_log_print(ANDROID_LOG_DEBUG, "MatrixBinRep::readObject", "INDEX_MAPtemp:map_size %d",map_size);

        for (int j = 0; j < map_size; j++) {
            string key = din.readUTF8();
            int value = din.readInt();
            //	stream.read(reinterpret_cast<char *>(&key), sizeof(key));
            //	stream >> key;

            //	INDEX_MAPtemp[key] =  value;
            INDEX_MAP[key] = value;
            //		__android_log_print(ANDROID_LOG_DEBUG, "MatrixBinRep::readObject", "INDEX_MAPtemp (K=%s,V=%d)",key.c_str(),value);
        }

        int ssidnames_size = din.readInt();

        ///	__android_log_print(ANDROID_LOG_DEBUG, "MatrixBinRep::readObject", "ssidnames_size %d",ssidnames_size);

        for (int j = 0; j < ssidnames_size; j++) {
            string ssidn = din.readUTF8();
            //stream.read(reinterpret_cast<char *>(&ssidn), sizeof(ssidn));
            //stream.read(reinterpret_cast<char *>(&ssidn), sizeof(ssidn));

            //	stream >> ssidn;

            //const char *ssidnChar = ssidn.c_str();

            // stream.read(ssidnChar, ssidn.length());


            //	__android_log_print(ANDROID_LOG_DEBUG, "MatrixBinRep::readObject", "ssidn %s",ssidn.c_str());

            //ssidnamesTemp.push_back(ssidn);
            ssidnames.push_back(ssidn);

        }

//		INDEX_MAP = INDEX_MAPtemp;
//		theList = theListTemp;
//		ssidnames = ssidnamesTemp;
//		maxs = maxsTemp;
//		mins = minsTemp;
    }

    //		AsociativeMemoryLocator aml = AsociativeMemoryLocator.getInstance();
    //		aml.setINDEX_MAP(INDEX_MAPtemp);
    //		aml.setSsidnames(ssidnamesTemp);
    //		aml.setTheList(theListTemp);
    //		aml.setMaxs(maxsTemp);
    //		aml.setMins(minsTemp);

//	delete &INDEX_MAPtemp;
//	delete &ssidnamesTemp;
//	delete &theListTemp;
//	delete &maxsTemp;
//	delete &minsTemp;

    //__android_log_print(ANDROID_LOG_DEBUG, "MatrixBinRep::readObject", "INDEX_MAP %d",INDEX_MAP.size());

    //__android_log_print(ANDROID_LOG_DEBUG, "MatrixBinRep::readObject", "theList %d",theList.size());


    //__android_log_print(ANDROID_LOG_DEBUG, "MatrixBinRep::readObject", "ssidnames %d",ssidnames.size());

    //__android_log_print(ANDROID_LOG_DEBUG, "MatrixBinRep::readObject", "maxs %d",maxs.size());
    //__android_log_print(ANDROID_LOG_DEBUG, "MatrixBinRep::readObject", "mins %d",mins.size());

    //stream.flush();
    stream.close();

}


bool MatrixBinRep::WriteUnformatted_string(std::ostream &out, const std::string &s) {
    std::string::size_type n = s.size();
    out.write(reinterpret_cast<const char *>(&n), sizeof(n));

    if (n > 0) {
        out.write(s.c_str(), n);
    }

    return !out.bad();
}


bool MatrixBinRep::ReadUnformatted_string(std::istream &in, std::string &s) {
    // Note : With the upcoming changes to the C++ standard,
    //        you will not need the intermediate vector, since
    //        a std::string will be gaurenteed to have
    //        contigous storage (at least, I saw that posted)

    std::string::size_type n;
    in.read(reinterpret_cast<char *>(&n), sizeof(n));

    if (n > 0) {
        std::vector<char> v(n);
        in.read(&v[0], n);
        s.assign(&v[0], &v[0] + n);
    }
    else {
        s = "";
    }

    return !in.bad();
}

#ifdef WIN32
int roundf(float f)
{
    return floor(f + 0.5);
}
#endif

MyJavaOutputStream::MyJavaOutputStream(ostream &os) :
        m_os(os) {
}

void MyJavaOutputStream::writeInt(int i) {
    char buffer[4];
    int *pInt = (int *) buffer;
    *pInt = i;//htonl(i);

    m_os.write(buffer, 4);
}

void MyJavaOutputStream::writeShort(short sh) {
    short *pNum = (short *) m_buffer;
    *pNum = sh;//htons(sh);
    m_os.write(m_buffer, 2);
}

void MyJavaOutputStream::writeLong(long l) {
    long *pNum = (long *) m_buffer;
    *pNum = l; //htonl(l);
    m_os.write(m_buffer, 8);
}

void MyJavaOutputStream::writeBool(bool b) {
    bool *pNum = (bool *) m_buffer;
    *pNum = b;
    m_os.write(m_buffer, 1);

}

void MyJavaOutputStream::writeUTF8(const string &str) {
    writeInt((int) str.length());
    m_os.write(str.c_str(), str.length());
}


MyJavaInputStream::MyJavaInputStream(istream &is) :
        m_is(is) {
}

uint32_t MyJavaInputStream::readInt() {
    int8_t buffer[4];
    m_is.read((char *) buffer, 4);

    // swap the bytes
    uint32_t result = (*(int32_t *) buffer);

//	int* pNum = (int*)m_buffer;
//	int ret = *pNum; //ntohl(*pNum);


    return result;
}

short MyJavaInputStream::readShort() {
    m_is.read(m_buffer, 2);
    short *pNum = (short *) m_buffer;
    int ret = *pNum; //ntohs(*pNum);

    return ret;
}

long MyJavaInputStream::readLong() {
    m_is.read(m_buffer, 4);
    int *pNum = (int *) m_buffer;
    int ret = *pNum; //ntohl(*pNum);

    return ret;
}

bool MyJavaInputStream::readBool() {
    m_is.read(m_buffer, 1);
    char *pNum = (char *) m_buffer;

    return *pNum;
}

string MyJavaInputStream::readUTF8() {
    int len = readInt();
    char *pBuffer = new char[len];
    m_is.read(pBuffer, len);

    string ret(pBuffer, len);
    delete[] pBuffer;
    return ret;
}


///*
// * MatrixBinRep.cpp
// *
// *  Created on: 29 ���� 2013
// *      Author: Owner
// */
//#if  !defined(WIN32) && !defined (__APPLE__)
//#include <sys/endian.h>
//#endif
//#include "MatrixBinRep.h"
//
//#include <cmath>
//#include <fstream>
//#include <iostream>
//#include <iterator>
//#include <list>
//#include <map>
//#include <string>
//#include <utility>
//#include <vector>
//#include "AssociativeData.h"
//#include "Location.h"
//#if  defined (__APPLE__)
//#else
//#include  <android/log.h>
//#endif
//#include <math.h>
//
//#ifdef WIN32
//#include <winsock.h>
//#endif
//
//
//class MyJavaOutputStream
//{
//	ostream& m_os;
//	char m_buffer[8];
//public:
//	MyJavaOutputStream(ostream& os);
//
//	void writeInt(int i);
//	void writeShort(short sh);
//	void writeLong(long l);
//	void writeBool(bool b);
//	void writeUTF8(const string& str);
//
//};
//class MyJavaInputStream
//{
//	istream& m_is;
//	char m_buffer[8];
//public:
//	MyJavaInputStream(istream& is);
//
//	int readInt();
//	short readShort();
//	long readLong();
//	bool readBool();
//	string readUTF8();
//
//};
//
//
//MatrixBinRep::MatrixBinRep() {
//	// TODO Auto-generated constructor stub
//}
//
//MatrixBinRep::MatrixBinRep(list<AssociativeData> &theList,
//		map<string, int> &INDEX_MAP, list<string> &ssidnames,
//		vector<float> &mins, vector<float> &maxs) {
////	this->theList = theList;
////	this->INDEX_MAP = INDEX_MAP;
////	this->ssidnames = ssidnames;
////	this->mins = mins;
////	this->maxs=maxs;
//
//}
//
//MatrixBinRep::~MatrixBinRep() {
//	// TODO Auto-generated destructor stub
//}
//
//void MatrixBinRep::writeObject(const string &ofile,list<AssociativeData> &theList,
//		map<string, int> &INDEX_MAP, list<string> &ssidnames,
//		vector<float> &mins, vector<float> &maxs, bool isselectfloor) {
//
////	FILE* file = fopen(ofile.c_str(),"w+");
////
////	    if (file != NULL)
////	    {
////	        fputs("HELLO WORLD!\n", file);
////	        fflush(file);
////	        fclose(file);
////	    }
//
//// -B V=1 APP_OPTIM=debug NDK_DEBUG=1
////		ofstream stream;
////
////		stream.open(ofile.c_str(), ios::in | ios::out | ios::binary | ios::trunc);
////		if(stream.good()){
////			int x=2;
////			stream.write(reinterpret_cast<const char *>(&x), sizeof(int));
////         //  stream << "test";
////		}
////		stream.flush();
////		stream.close();
//
//
//
//	//cout << "MatrixBinRep::writeObject" << endl;
//
//	ofstream stream;
//
//	stream.open(ofile.c_str(), ios::in | ios::out | ios::binary | ios::trunc);
//
//	if (stream.good()) {
//		MyJavaOutputStream dout(stream);
//		int theList_size = (int) theList.size();
//		short vec_size = theList.front().mvector.size();
//
//		dout.writeInt(theList_size);
//
//		dout.writeShort(vec_size);
//
//		for (list<AssociativeData>::iterator it = theList.begin();
//				it != theList.end(); it++) {
//			AssociativeData & al = *it;
//			short x = roundf(al.point.x);
//			dout.writeShort(x);
//			short y = roundf(al.point.y);
//			dout.writeShort(y);
//
//			short z;
//			if(isselectfloor==true){
//				z = (short)al.Z;
//				dout.writeShort(z);
//			}
//
//			for (vector<float>::iterator itr = al.mvector.begin();
//					itr != al.mvector.end(); ++itr) {
//				int intv = *itr;
//				dout.writeInt(intv);
//			}
//
//			for (vector<float>::iterator itr = al.normalizedvector.begin();
//					itr != al.normalizedvector.end(); ++itr) {
//				float val = *itr;
//				int intv = (int) (val * 10000);
//				dout.writeInt(intv);
//			}
//		}
//
//		for (vector<float>::iterator itr = mins.begin(); itr != mins.end();
//				++itr) {
//			short mn1 = (short) *itr;
//			dout.writeShort(mn1);
//		}
//
//		for (vector<float>::iterator itr = maxs.begin(); itr != maxs.end();
//				++itr) {
//			short mx1 = (short) *itr;
//			dout.writeShort(mx1);
//		}
//
//		map<string, int>::iterator iter;
//
//		int map_size =(int) INDEX_MAP.size();
//
//		dout.writeInt(map_size);
//
//		for (iter = INDEX_MAP.begin(); iter != INDEX_MAP.end(); ++iter) {
//			string key = iter->first;
//			int value = iter->second;
//			 //const char *keyChar = key.c_str();
//
//
//			//stream << key << std::endl;
//			//stream.write(reinterpret_cast<const char *>(&key), sizeof(key));
//
//			 //stream.write(keyChar, key.length());
//			 dout.writeUTF8(key);
//			 dout.writeInt(value);
//
//		}
//
//		int ssidnames_size = (int) ssidnames.size();
//
//					 dout.writeInt(ssidnames_size);
//
//		for (list<string>::iterator it = ssidnames.begin();
//				it != ssidnames.end(); it++) {
//			string ssidn = *it;
//			//stream.write(reinterpret_cast<const char *>(&ssidn), sizeof(ssidn));
//		//	stream.write(ssidn.c_str(), sizeof(char)*ssidn.size());
//
//			//stream << ssidn << std::endl;
//
////			 const char *ssidnChar = ssidn.c_str();
////			 stream.write(ssidnChar, ssidn.length());
//
//
//
//			dout.writeUTF8(ssidn);
//
//		}
//	}
//
//	stream.flush();
//	stream.close();
//
//}
//
//void MatrixBinRep::readObject(const string &ofile, list<AssociativeData> &theList,
//		map<string, int> &INDEX_MAP, list<string> &ssidnames,
//		vector<float> &mins, vector<float> &maxs, bool isselectfloor) {
//
//	//__android_log_print(ANDROID_LOG_DEBUG, "MatrixBinRep::readObject", "ofile :%s",ofile.c_str());
//
////	fstream stream(ofile.c_str(), ios::binary | ios::in | ios::trunc);
//	ifstream stream(ofile.c_str(), ios::binary | ios::in);
//	if (stream.good()) {
//		MyJavaInputStream din(stream);
//		//	list<AssociativeData> theListTemp;
//		//	map<string, int> INDEX_MAPtemp;
//		//	list<string> ssidnamesTemp;
//
//			int theList_size;
//			short vec_size;
//		theList_size = din.readInt();
//
//	//	__android_log_print(ANDROID_LOG_DEBUG, "MatrixBinRep::readObject", "theList_size :%d",theList_size);
//
//		vec_size = din.readShort();
//
//	//	__android_log_print(ANDROID_LOG_DEBUG, "MatrixBinRep::readObject", "vec_size :%d",vec_size);
//
//		Location p;
//
//		for (int i = 0; i < theList_size; i++) {
//
//			short x = din.readShort();
//		//	__android_log_print(ANDROID_LOG_DEBUG, "MatrixBinRep::readObject", "x :%d",x);
//
//			short y = din.readShort();
//	//		__android_log_print(ANDROID_LOG_DEBUG, "MatrixBinRep::readObject", "y :%d",y);
//
//			short z;
//			if(isselectfloor==true){
//				z=din.readShort();
//				//__android_log_print(ANDROID_LOG_DEBUG, "MatrixBinRep::readObject", "z :%d",z);
//			}
//
//			vector<float> mvectorTemp;
//
//			for (int j = 0; j < vec_size; j++) {
//				int intv = din.readInt();
//				mvectorTemp.push_back(intv);
//
//		//		__android_log_print(ANDROID_LOG_DEBUG, "MatrixBinRep::readObject", "mvectorTemp.intv :%d",intv);
//
//			}
//
//			vector<float> normalizedvectorTemp;
//			for (int j = 0; j < vec_size; j++) {
//				int intv = din.readInt();
//				float val =  ((float)intv) / 10000.0f;
//				normalizedvectorTemp.push_back(val);
//
//		//		__android_log_print(ANDROID_LOG_DEBUG, "MatrixBinRep::readObject", "normalizedvectorTemp.intv :%f",intv / 10000.0);
//			}
//
//			AssociativeData data;
//			data.point.x = x;
//			data.point.y = y;
//
//			if(isselectfloor==true){
//				data.Z = z;
//			}
//
//			//?
//			data.mvector = mvectorTemp;
//			data.normalizedvector = normalizedvectorTemp;
//
//		//	theListTemp.push_back(data);
//			theList.push_back(data);
//
//			//?
//			//	delete &mvectorTemp;
//			//	delete &normalizedvectorTemp;
//			//	delete &data;
//		}
//
//	//	vector<float> minsTemp;
//		for (int j = 0; j < vec_size; j++) {
//			short mn1 = din.readShort();
//	//		__android_log_print(ANDROID_LOG_DEBUG, "MatrixBinRep::readObject", "mn1 :%d",mn1);
//
//			//minsTemp.push_back(mn1);
//			mins.push_back(mn1);
//		}
//
//	//	vector<float> maxsTemp;
//		for (int j = 0; j < vec_size; j++) {
//			short mx1 = din.readShort();
//	//		__android_log_print(ANDROID_LOG_DEBUG, "MatrixBinRep::readObject", "mx1 :%d",mx1);
//
//		//	maxsTemp.push_back(mx1);
//			maxs.push_back(mx1);
//		}
//
//		int map_size = din.readInt();
//	//	__android_log_print(ANDROID_LOG_DEBUG, "MatrixBinRep::readObject", "INDEX_MAPtemp:map_size %d",map_size);
//
//		for (int j = 0; j < map_size; j++) {
//			string key = din.readUTF8();
//			int value = din.readInt();
//		//	stream.read(reinterpret_cast<char *>(&key), sizeof(key));
//		//	stream >> key;
//
//		//	INDEX_MAPtemp[key] =  value;
//			INDEX_MAP[key] =  value;
//	//		__android_log_print(ANDROID_LOG_DEBUG, "MatrixBinRep::readObject", "INDEX_MAPtemp (K=%s,V=%d)",key.c_str(),value);
//		}
//
//		int ssidnames_size = din.readInt();
//
//	///	__android_log_print(ANDROID_LOG_DEBUG, "MatrixBinRep::readObject", "ssidnames_size %d",ssidnames_size);
//
//		for (int j = 0; j < ssidnames_size; j++) {
//			string ssidn = din.readUTF8();
//			//stream.read(reinterpret_cast<char *>(&ssidn), sizeof(ssidn));
//			//stream.read(reinterpret_cast<char *>(&ssidn), sizeof(ssidn));
//
//		 //	stream >> ssidn;
//
//			 //const char *ssidnChar = ssidn.c_str();
//
//			// stream.read(ssidnChar, ssidn.length());
//
//
//		//	__android_log_print(ANDROID_LOG_DEBUG, "MatrixBinRep::readObject", "ssidn %s",ssidn.c_str());
//
//			//ssidnamesTemp.push_back(ssidn);
//			ssidnames.push_back(ssidn);
//
//		}
//
////		INDEX_MAP = INDEX_MAPtemp;
////		theList = theListTemp;
////		ssidnames = ssidnamesTemp;
////		maxs = maxsTemp;
////		mins = minsTemp;
//	}
//
//	//		AsociativeMemoryLocator aml = AsociativeMemoryLocator.getInstance();
//	//		aml.setINDEX_MAP(INDEX_MAPtemp);
//	//		aml.setSsidnames(ssidnamesTemp);
//	//		aml.setTheList(theListTemp);
//	//		aml.setMaxs(maxsTemp);
//	//		aml.setMins(minsTemp);
//
////	delete &INDEX_MAPtemp;
////	delete &ssidnamesTemp;
////	delete &theListTemp;
////	delete &maxsTemp;
////	delete &minsTemp;
//
//	//__android_log_print(ANDROID_LOG_DEBUG, "MatrixBinRep::readObject", "INDEX_MAP %d",INDEX_MAP.size());
//
//	//__android_log_print(ANDROID_LOG_DEBUG, "MatrixBinRep::readObject", "theList %d",theList.size());
//
//
//	//__android_log_print(ANDROID_LOG_DEBUG, "MatrixBinRep::readObject", "ssidnames %d",ssidnames.size());
//
//	//__android_log_print(ANDROID_LOG_DEBUG, "MatrixBinRep::readObject", "maxs %d",maxs.size());
//	//__android_log_print(ANDROID_LOG_DEBUG, "MatrixBinRep::readObject", "mins %d",mins.size());
//
//	//stream.flush();
//	stream.close();
//
//}
//
//
//
//
//bool MatrixBinRep::WriteUnformatted_string(std::ostream & out , const std::string & s)
//{
//    std::string::size_type n = s.size();
//    out.write( reinterpret_cast<const char*>(&n) , sizeof(n) );
//
//    if (n > 0)
//    {
//        out.write( s.c_str() , n );
//    }
//
//    return !out.bad();
//}
//
//
//bool MatrixBinRep::ReadUnformatted_string(std::istream & in , std::string & s)
//{
//    // Note : With the upcoming changes to the C++ standard,
//    //        you will not need the intermediate vector, since
//    //        a std::string will be gaurenteed to have
//    //        contigous storage (at least, I saw that posted)
//
//    std::string::size_type n;
//    in.read( reinterpret_cast<char*>(&n) , sizeof(n) );
//
//    if (n > 0)
//    {
//        std::vector<char> v(n);
//        in.read( &v[0] , n );
//        s.assign(&v[0],&v[0]+n);
//    }
//    else
//    {
//        s = "";
//    }
//
//    return !in.bad();
//}
//#ifdef WIN32
//int roundf(float f)
//{
//	return floor(f + 0.5);
//}
//#endif
//
//	MyJavaOutputStream::MyJavaOutputStream(ostream& os):
//		m_os(os)
//	{
//	}
//
//	void MyJavaOutputStream::writeInt(int i)
//	{
//		int* pInt = (int*)m_buffer;
//		*pInt = i;//htonl(i);
//		m_os.write(m_buffer, 4);
//	}
//	void MyJavaOutputStream::writeShort(short sh)
//	{
//		short* pNum = (short*)m_buffer;
//		*pNum = sh;//htons(sh);
//		m_os.write(m_buffer, 2);
//	}
//	void MyJavaOutputStream::writeLong(long l)
//	{
//		long* pNum = (long*)m_buffer;
//		*pNum = l; //htonl(l);
//		m_os.write(m_buffer, 8);
//	}
//	void MyJavaOutputStream::writeBool(bool b)
//	{
//		bool* pNum = (bool*)m_buffer;
//		*pNum = b;
//		m_os.write(m_buffer, 1);
//
//	}
//	void MyJavaOutputStream::writeUTF8(const string& str)
//	{
//		writeShort(str.length());
//		m_os.write(str.c_str(), str.length());
//	}
//
//
//
//
//
//MyJavaInputStream::MyJavaInputStream(istream& is):
//	m_is(is)
//{
//}
//
//int MyJavaInputStream::readInt()
//{
//	m_is.read(m_buffer, 4);
//	int* pNum = (int*)m_buffer;
//	int ret = *pNum; //ntohl(*pNum);
//
//	return ret;
//}
//short MyJavaInputStream::readShort()
//{
//	m_is.read(m_buffer, 2);
//	short* pNum = (short*)m_buffer;
//	int ret = *pNum; //ntohs(*pNum);
//
//	return ret;
//}
//long MyJavaInputStream::readLong()
//{
//	m_is.read(m_buffer, 4);
//	int* pNum = (int*)m_buffer;
//	int ret = *pNum; //ntohl(*pNum);
//
//	return ret;
//}
//bool MyJavaInputStream::readBool()
//{
//	m_is.read(m_buffer, 1);
//	char* pNum = (char*)m_buffer;
//
//	return *pNum;
//}
//string MyJavaInputStream::readUTF8()
//{
//	short len = readShort();
//	char* pBuffer = new char[len];
//	m_is.read(pBuffer , len);
//
//	string ret(pBuffer, len);
//	delete[] pBuffer;
//	return ret;
//}
