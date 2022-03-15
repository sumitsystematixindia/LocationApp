/*
 * MatrixBinRep.h
 *
 *  Created on: 29 ���� 2013
 *      Author: Owner
 */

#ifndef MATRIXBINREP_H_
#define MATRIXBINREP_H_

#include <vector>
#include <map>
#include <list>
#include "AssociativeData.h"
#include <string>

using namespace std;

class MatrixBinRep {

private:
//	list<AssociativeData> theList;
//	map<string, int> INDEX_MAP;
//	list<string> ssidnames;
//	vector<float> mins;
//	vector<float> maxs;

    bool WriteUnformatted_string(std::ostream &out, const std::string &s);

    bool ReadUnformatted_string(std::istream &in, std::string &s);

    int countNoN127(vector<float> &vector);

    int countNoNZeros(vector<float> &vector);

public:
    MatrixBinRep();

    MatrixBinRep(list<AssociativeData> &theList, map<string, int> &INDEX_MAP,
                 list<string> &ssidnames, vector<float> &mins, vector<float> &maxs);

    MatrixBinRep(map<string, int> iNDEX_MAP, list<string> ssidnames);

    void writeObject(const string &ofile, list<AssociativeData> &theList,
                     map<string, int> &INDEX_MAP, list<string> &ssidnames,
                     vector<float> &mins, vector<float> &maxs, bool isselectfloor);

    void readObject(const string &ofile, list<AssociativeData> &theList,
                    map<string, int> &INDEX_MAP, list<string> &ssidnames,
                    vector<float> &mins, vector<float> &maxs, bool isselectfloor);

    void writeSpecialObject(const string &ofile, list<AssociativeData> &theList,
                            map<string, int> &INDEX_MAP, list<string> &ssidnames,
                            vector<float> &mins, vector<float> &maxs, bool isselectfloor);

    void readSpecialObject(const string &ofile, list<AssociativeData> &theList,
                           map<string, int> &INDEX_MAP, list<string> &ssidnames,
                           vector<float> &mins, vector<float> &maxs, bool isselectfloor);

    virtual ~MatrixBinRep();
};

#endif /* MATRIXBINREP_H_ */


//package com.mlins.locator;
//
//import java.io.IOException;
//import java.io.ObjectInputStream;
//import java.io.ObjectOutputStream;
//import java.security.SignedObject;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.Map.Entry;
//
//import android.graphics.PointF;
//
//public class MatrixBinRep {
//
//	protected List<AssociativeData> theList;
//	public List<AssociativeData> getTheList() {
//		return theList;
//	}
//
//	public Map<String, Integer> getINDEX_MAP() {
//		return INDEX_MAP;
//	}
//
//	public List<String> getSsidnames() {
//		return ssidnames;
//	}
//
//	public float[] getMins() {
//		return mins;
//	}
//
//	public float[] getMaxs() {
//		return maxs;
//	}
//
//	protected Map<String, Integer> INDEX_MAP;
//	protected List<String> ssidnames;
//	protected float mins[] = new float[0];
//	protected float maxs[] = new float[0];
//
//	public MatrixBinRep(List<AssociativeData> theList,
//			Map<String, Integer> iNDEX_MAP, List<String> ssidnames,
//			float[] mins, float[] maxs) {
//		super();
//		this.theList = theList;
//		INDEX_MAP = iNDEX_MAP;
//		this.ssidnames = ssidnames;
//		this.mins = mins;
//		this.maxs = maxs;
//	}
//
//	public MatrixBinRep(Map<String, Integer> iNDEX_MAP, List<String> ssidnames) {
//		super();
//
//		INDEX_MAP = iNDEX_MAP;
//		this.ssidnames = ssidnames;
//	}
//
//	public void writeObject(ObjectOutputStream stream) throws IOException {
//
//		stream.writeInt(theList.size());
//		short vsize =(short) theList.get(0).vector.length;
//		stream.writeShort(vsize);
//
//
//		for (AssociativeData al : theList) {
//
//			int x = Math.round(al.point.x);
//			stream.writeShort(x);
//			int y = Math.round(al.point.y);
//			stream.writeShort(y);
//
//			//stream.writeInt(al.vector.length);
//			for (double v : al.vector) {
//				stream.writeShort((int)v);
//			}
//
//			//stream.writeInt(al.normalizedvector.length);
//			int inv=0;
//			for (double nv : al.normalizedvector) {
//				inv=(int)(nv*10000);
//				stream.writeShort(inv);
//			}
//		}
//
//		//stream.writeInt(mins.length);
//		for (double mn : mins) {
//
//			stream.writeShort((int)mn);
//		}
//
//		//stream.writeInt(maxs.length);
//		for (double mx : maxs) {
//
//			stream.writeShort((int)mx);
//		}
//
//		stream.writeInt(INDEX_MAP.size());
//		for (Entry<String, Integer> elm : INDEX_MAP.entrySet()) {
//			stream.writeObject(elm.getKey());
//			stream.writeInt(elm.getValue());
//		}
//
//		stream.writeInt(ssidnames.size());
//		for (String ssidn : ssidnames) {
//			stream.writeObject(ssidn);
//		}
//
//	}
//
//	public void readObject(ObjectInputStream stream) throws IOException,
//			ClassNotFoundException {
//
//		ArrayList<AssociativeData> theListTemp = new ArrayList<AssociativeData>();
//		HashMap<String, Integer> INDEX_MAPtemp = new HashMap<String, Integer>();
//		ArrayList<String> ssidnamesTemp = new ArrayList<String>();
//		String key = null;
//		Integer value = null;
//
//		int theListSize = stream.readInt();
//		int vsize=stream.readShort();
//
//		PointF p = null;
//		float minsTemp[] = null;
//		float maxsTemp[] = null;
//		float normalizedvector[] = null;
//		float vector[] = null;
//		for (int i = 0; i < theListSize; i++) {
//			p = new PointF();
//			p.x = stream.readShort();
//			p.y = stream.readShort();
//
//			int vectorSize = vsize;
//			vector = new float[vectorSize];
//
//			for (int j = 0; j < vector.length; j++) {
//				vector[j] = stream.readShort();
//			}
//			int normalizedvectorSize = vsize;
//			normalizedvector = new float[normalizedvectorSize];
//
//			for (int j = 0; j < normalizedvector.length; j++) {
//				normalizedvector[j] = stream.readShort()/10000.0f;
//
//			}
//
//			AssociativeData data = new AssociativeData(p, vector);
//			data.normalizedvector = normalizedvector;
//			theListTemp.add(data);
//
//		}
//
//		int minsSize =vsize;
//		minsTemp = new float[minsSize];
//		for (int i = 0; i < minsTemp.length; i++) {
//			minsTemp[i] = stream.readShort();
//		}
//		int maxsSize = vsize;
//		maxsTemp = new float[maxsSize];
//		for (int i = 0; i < maxsTemp.length; i++) {
//			maxsTemp[i] = stream.readShort();
//		}
//
//		int INDEX_MAPsize = stream.readInt();
//		for (int i = 0; i < INDEX_MAPsize; i++) {
//			key = (String) stream.readObject();
//			value = stream.readInt();
//			INDEX_MAPtemp.put(key, value);
//		}
//
//		int ssidnamesSize = stream.readInt();
//		String ssidn = null;
//		for (int i = 0; i < ssidnamesSize; i++) {
//			ssidn = (String) stream.readObject();
//			ssidnamesTemp.add(ssidn);
//		}
//
//		AsociativeMemoryLocator aml = AsociativeMemoryLocator.getInstance();
//		aml.setINDEX_MAP(INDEX_MAPtemp);
//		aml.setSsidnames(ssidnamesTemp);
//		aml.setTheList(theListTemp);
//		aml.setMaxs(maxsTemp);
//		aml.setMins(minsTemp);
//
//	}

//}



///*
// * MatrixBinRep.h
// *
// *  Created on: 29 ���� 2013
// *      Author: Owner
// */
//
//#ifndef MATRIXBINREP_H_
//#define MATRIXBINREP_H_
//
//#include <vector>
//#include <map>
//#include <list>
//#include "AssociativeData.h"
//#include <string>
//using namespace std;
//class MatrixBinRep {
//
//private:
////	list<AssociativeData> theList;
////	map<string, int> INDEX_MAP;
////	list<string> ssidnames;
////	vector<float> mins;
////	vector<float> maxs;
//
//	bool WriteUnformatted_string(std::ostream & out , const std::string & s);
//	bool ReadUnformatted_string(std::istream & in , std::string & s);
//
//public:
//	MatrixBinRep();
//	MatrixBinRep(list<AssociativeData> &theList,map<string, int> &INDEX_MAP, list<string> &ssidnames,vector<float> &mins, vector<float> &maxs);
//	MatrixBinRep(map<string, int> iNDEX_MAP, list<string> ssidnames);
//	void writeObject(const string &ofile,list<AssociativeData> &theList,
//			map<string, int> &INDEX_MAP, list<string> &ssidnames,
//			vector<float> &mins, vector<float> &maxs, bool isselectfloor);
//	void readObject(const string &ofile,list<AssociativeData> &theList,
//			map<string, int> &INDEX_MAP, list<string> &ssidnames,
//			vector<float> &mins, vector<float> &maxs, bool isselectfloor);
//	virtual ~MatrixBinRep();
//};
//
//#endif /* MATRIXBINREP_H_ */
//
//
////package com.mlins.locator;
////
////import java.io.IOException;
////import java.io.ObjectInputStream;
////import java.io.ObjectOutputStream;
////import java.security.SignedObject;
////import java.util.ArrayList;
////import java.util.HashMap;
////import java.util.List;
////import java.util.Map;
////import java.util.Map.Entry;
////
////import android.graphics.PointF;
////
////public class MatrixBinRep {
////
////	protected List<AssociativeData> theList;
////	public List<AssociativeData> getTheList() {
////		return theList;
////	}
////
////	public Map<String, Integer> getINDEX_MAP() {
////		return INDEX_MAP;
////	}
////
////	public List<String> getSsidnames() {
////		return ssidnames;
////	}
////
////	public float[] getMins() {
////		return mins;
////	}
////
////	public float[] getMaxs() {
////		return maxs;
////	}
////
////	protected Map<String, Integer> INDEX_MAP;
////	protected List<String> ssidnames;
////	protected float mins[] = new float[0];
////	protected float maxs[] = new float[0];
////
////	public MatrixBinRep(List<AssociativeData> theList,
////			Map<String, Integer> iNDEX_MAP, List<String> ssidnames,
////			float[] mins, float[] maxs) {
////		super();
////		this.theList = theList;
////		INDEX_MAP = iNDEX_MAP;
////		this.ssidnames = ssidnames;
////		this.mins = mins;
////		this.maxs = maxs;
////	}
////
////	public MatrixBinRep(Map<String, Integer> iNDEX_MAP, List<String> ssidnames) {
////		super();
////
////		INDEX_MAP = iNDEX_MAP;
////		this.ssidnames = ssidnames;
////	}
////
////	public void writeObject(ObjectOutputStream stream) throws IOException {
////
////		stream.writeInt(theList.size());
////		short vsize =(short) theList.get(0).vector.length;
////		stream.writeShort(vsize);
////
////
////		for (AssociativeData al : theList) {
////
////			int x = Math.round(al.point.x);
////			stream.writeShort(x);
////			int y = Math.round(al.point.y);
////			stream.writeShort(y);
////
////			//stream.writeInt(al.vector.length);
////			for (double v : al.vector) {
////				stream.writeShort((int)v);
////			}
////
////			//stream.writeInt(al.normalizedvector.length);
////			int inv=0;
////			for (double nv : al.normalizedvector) {
////				inv=(int)(nv*10000);
////				stream.writeShort(inv);
////			}
////		}
////
////		//stream.writeInt(mins.length);
////		for (double mn : mins) {
////
////			stream.writeShort((int)mn);
////		}
////
////		//stream.writeInt(maxs.length);
////		for (double mx : maxs) {
////
////			stream.writeShort((int)mx);
////		}
////
////		stream.writeInt(INDEX_MAP.size());
////		for (Entry<String, Integer> elm : INDEX_MAP.entrySet()) {
////			stream.writeObject(elm.getKey());
////			stream.writeInt(elm.getValue());
////		}
////
////		stream.writeInt(ssidnames.size());
////		for (String ssidn : ssidnames) {
////			stream.writeObject(ssidn);
////		}
////
////	}
////
////	public void readObject(ObjectInputStream stream) throws IOException,
////			ClassNotFoundException {
////
////		ArrayList<AssociativeData> theListTemp = new ArrayList<AssociativeData>();
////		HashMap<String, Integer> INDEX_MAPtemp = new HashMap<String, Integer>();
////		ArrayList<String> ssidnamesTemp = new ArrayList<String>();
////		String key = null;
////		Integer value = null;
////
////		int theListSize = stream.readInt();
////		int vsize=stream.readShort();
////
////		PointF p = null;
////		float minsTemp[] = null;
////		float maxsTemp[] = null;
////		float normalizedvector[] = null;
////		float vector[] = null;
////		for (int i = 0; i < theListSize; i++) {
////			p = new PointF();
////			p.x = stream.readShort();
////			p.y = stream.readShort();
////
////			int vectorSize = vsize;
////			vector = new float[vectorSize];
////
////			for (int j = 0; j < vector.length; j++) {
////				vector[j] = stream.readShort();
////			}
////			int normalizedvectorSize = vsize;
////			normalizedvector = new float[normalizedvectorSize];
////
////			for (int j = 0; j < normalizedvector.length; j++) {
////				normalizedvector[j] = stream.readShort()/10000.0f;
////
////			}
////
////			AssociativeData data = new AssociativeData(p, vector);
////			data.normalizedvector = normalizedvector;
////			theListTemp.add(data);
////
////		}
////
////		int minsSize =vsize;
////		minsTemp = new float[minsSize];
////		for (int i = 0; i < minsTemp.length; i++) {
////			minsTemp[i] = stream.readShort();
////		}
////		int maxsSize = vsize;
////		maxsTemp = new float[maxsSize];
////		for (int i = 0; i < maxsTemp.length; i++) {
////			maxsTemp[i] = stream.readShort();
////		}
////
////		int INDEX_MAPsize = stream.readInt();
////		for (int i = 0; i < INDEX_MAPsize; i++) {
////			key = (String) stream.readObject();
////			value = stream.readInt();
////			INDEX_MAPtemp.put(key, value);
////		}
////
////		int ssidnamesSize = stream.readInt();
////		String ssidn = null;
////		for (int i = 0; i < ssidnamesSize; i++) {
////			ssidn = (String) stream.readObject();
////			ssidnamesTemp.add(ssidn);
////		}
////
////		AsociativeMemoryLocator aml = AsociativeMemoryLocator.getInstance();
////		aml.setINDEX_MAP(INDEX_MAPtemp);
////		aml.setSsidnames(ssidnamesTemp);
////		aml.setTheList(theListTemp);
////		aml.setMaxs(maxsTemp);
////		aml.setMins(minsTemp);
////
////	}
//
////}
