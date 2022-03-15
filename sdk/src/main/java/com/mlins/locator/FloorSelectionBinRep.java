package com.mlins.locator;

import android.graphics.PointF;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;


public class FloorSelectionBinRep extends MatrixBinRep {

    public FloorSelectionBinRep(List<AssociativeData> theList,
                                Map<String, Integer> iNDEX_MAP, List<String> ssidnames,
                                float[] mins, float[] maxs) {
        super(theList, iNDEX_MAP, ssidnames, mins, maxs);
        // TODO Auto-generated constructor stub
    }

    @Override
    public void writeObject(OutputStream stream) throws Exception {


        writeInt(stream, theList.size());
        //System.out.println("MatrixBinRep: theList.size() "+theList.size());
        int vsize = theList.get(0).vector.length;
        //System.out.println("MatrixBinRep: vsize "+vsize);
        writeInt(stream, vsize);


        for (AssociativeData al : theList) {

            int x = Math.round(al.getPoint().x);
            writeInt(stream, x);
            //System.out.println("MatrixBinRep: x "+x);

            int y = Math.round(al.getPoint().y);
            writeInt(stream, y);
            //System.out.println("MatrixBinRep: y "+y);


            writeInt(stream, al.getZ());
            //System.out.println("MatrixBinRep: z "+al.getZ());


            float[] v = al.vector;
            int vcnt = countNoN127(v);
            writeInt(stream, vcnt);
            //System.out.println("MatrixBinRep: vcnt "+vcnt);
            for (int i = 0; i < v.length; i++) {
                if (v[i] != -127.0) {
                    writeInt(stream, i);
                    writeInt(stream, (int) v[i]);
                    //System.out.println("MatrixBinRep: vector(i,v)= "+i+", "+v[i]);
                }
            }


            float[] nv = al.normalizedvector;
            int nvcnt = countNoNZeros(nv);
            writeInt(stream, nvcnt);
            //System.out.println("MatrixBinRep: nvcnt "+nvcnt);
            int inv = 0;
            for (int i = 0; i < nv.length; i++) {
                if (nv[i] != 0.0) {
                    inv = (int) (nv[i] * 10000);
                    writeInt(stream, i);
                    writeInt(stream, inv);
                    //System.out.println("MatrixBinRep: normalizedvector(i,v)= "+i+", "+inv);
                }
            }


        }

        for (double mn : mins) {
            writeInt(stream, (int) mn);
            //System.out.println("MatrixBinRep: mn "+(int)mn);
        }

        for (double mx : maxs) {
            writeInt(stream, (int) mx);
            //System.out.println("MatrixBinRep: mx "+(int)mx);
        }


        writeInt(stream, INDEX_MAP.size());
        //System.out.println("MatrixBinRep: INDEX_MAP.size() "+INDEX_MAP.size());
        for (Entry<String, Integer> elm : INDEX_MAP.entrySet()) {
            String key = elm.getKey();
            writeUTF8String(stream, key);
            writeInt(stream, elm.getValue());
            //System.out.println("MatrixBinRep: INDEX_MAP(K,V)= "+elm.getKey()+", "+elm.getValue());
        }


        writeInt(stream, ssidnames.size());
        //System.out.println("MatrixBinRep: ssidnames.size() "+ssidnames.size());
        for (String ssidn : ssidnames) {
            writeUTF8String(stream, ssidn);
            //System.out.println("MatrixBinRep: ssidnames(I,V)= "+ssidn.length()+", "+ssidn);
        }


//		super.writeObject(stream, isFloorSelection);

//		stream.writeInt(theList.size());
//		short vsize =(short) theList.get(0).vector.length;
//		stream.writeShort(vsize);
//		
//		for (AssociativeData al : theList) {
//
//			int x = Math.round(al.getPoint().x);
//			stream.writeShort(x);
//			int y = Math.round(al.getPoint().y);
//			stream.writeShort(y);
//			
//			stream.writeShort(al.getZ());
//
//			//stream.writeInt(al.vector.length);
//			float[] v=al.vector;
//			int vcnt=countNoN127(v); 
//			stream.writeInt(vcnt);
//			for (int i=0; i< v.length;i++) {
//				if(v[i] != -127.0){
//					stream.writeShort(i);
//					stream.writeShort((int)v[i]);	
//				}
//			}
//
//			//stream.writeInt(al.normalizedvector.length);
//			float[] nv=al.normalizedvector;
//			int nvcnt=countNoNZeros(nv);
//			stream.writeInt(nvcnt);
//			int inv=0;
//			for (int i=0;i< nv.length;i++) {
//				if(nv[i]!=0.0){
//					inv=(int)(nv[i]*10000);
//					stream.writeShort(i);
//					stream.writeShort(inv);
//				}
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

    }


    @Override
    public void readObject(InputStream stream) throws Exception, ClassNotFoundException {

        ArrayList<AssociativeData> theListTemp = new ArrayList<AssociativeData>();
        HashMap<String, Integer> INDEX_MAPtemp = new HashMap<String, Integer>();
        ArrayList<String> ssidnamesTemp = new ArrayList<String>();
        String key = null;
        Integer value = null;

        int theListSize = readInt(stream);

        //System.out.println("MatrixBinRep: theList.size() "+theListSize);

        int vsize = readInt(stream);
        //System.out.println("MatrixBinRep: vsize "+vsize);

        PointF p = null;
        float minsTemp[] = null;
        float maxsTemp[] = null;
        float normalizedvector[] = null;
        float vector[] = null;
        int z = -1;
        for (int i = 0; i < theListSize; i++) {

            p = new PointF();

            p.x = readInt(stream);
            //System.out.println("MatrixBinRep: x "+p.x);

            p.y = readInt(stream);
            //System.out.println("MatrixBinRep: y "+p.y);


            z = readInt(stream);
            //System.out.println("MatrixBinRep: z "+z);


            int vectorSize = vsize;
            vector = new float[vectorSize];
            Arrays.fill(vector, -127);
            int non127Cont = readInt(stream);
            //System.out.println("MatrixBinRep: vcnt "+non127Cont);
            if (non127Cont != 0) {
                for (int j = 0; j < non127Cont; j++) {
                    int index = readInt(stream);
                    vector[index] = readInt(stream);
                    //System.out.println("MatrixBinRep: vector(i,v)= "+index+", "+vector[index]);
                }
            }


            int normalizedvectorSize = vsize;
            int nonZerosCont = readInt(stream);
            //System.out.println("MatrixBinRep: nvcnt "+nonZerosCont);
            normalizedvector = new float[normalizedvectorSize];
            if (nonZerosCont != 0) {
                for (int j = 0; j < nonZerosCont; j++) {
                    int index = readInt(stream);
                    int val = readInt(stream);
                    normalizedvector[index] = val / 10000.0f;
                    //System.out.println("MatrixBinRep: normalizedvector(i,v)= "+index+", "+val);
                }
            }

            AssociativeData data = new AssociativeData(p, vector);
            data.normalizedvector = normalizedvector;


            data.setZ(z);


            theListTemp.add(data);

        }

        int minsSize = vsize;
        minsTemp = new float[minsSize];
        for (int i = 0; i < minsTemp.length; i++) {
            minsTemp[i] = readInt(stream);
            //System.out.println("MatrixBinRep: mn "+minsTemp[i]);
        }

        int maxsSize = vsize;
        maxsTemp = new float[maxsSize];
        for (int i = 0; i < maxsTemp.length; i++) {
            maxsTemp[i] = readInt(stream);
            //System.out.println("MatrixBinRep: mx "+maxsTemp[i]);
        }


        int INDEX_MAPsize = readInt(stream);
        //System.out.println("MatrixBinRep: INDEX_MAP.size() "+INDEX_MAPsize);

        for (int i = 0; i < INDEX_MAPsize; i++) {

            key = readUTF8String(stream);
            value = readInt(stream);

            INDEX_MAPtemp.put(key, value);

            //System.out.println("MatrixBinRep: INDEX_MAP(K,V)= "+key+", "+value);
        }

        int ssidnamesSize = readInt(stream);
        //System.out.println("MatrixBinRep: ssidnames.size() "+ssidnamesSize);
        String ssidn = null;

        for (int i = 0; i < ssidnamesSize; i++) {

            ssidn = readUTF8String(stream);
            ssidnamesTemp.add(ssidn);
            //System.out.println("MatrixBinRep: ssidnames(I,V)= "+ssidn.length()+", "+ssidn);

        }

        setINDEX_MAP(INDEX_MAPtemp);
        setSsidnames(ssidnamesTemp);
        setTheList(theListTemp);
        setMaxs(maxsTemp);
        setMins(minsTemp);

//		ArrayList<AssociativeData> theListTemp = new ArrayList<AssociativeData>();
//		HashMap<String, Integer> INDEX_MAPtemp = new HashMap<String, Integer>();
//		ArrayList<String> ssidnamesTemp = new ArrayList<String>();
//		String key = null;
//		Integer value = null;
//
//		int theListSize = stream.readInt();
//
//		int vsize=stream.readShort();
//		
//		PointF p = null;
//		int z = -1;
//		float minsTemp[] = null;
//		float maxsTemp[] = null;
//		float normalizedvector[] = null;
//		float vector[] = null;
//		for (int i = 0; i < theListSize; i++) {
//			p = new PointF();
//			p.x = stream.readShort();
//			p.y = stream.readShort();
//			z = stream.readShort();
//			
//			int vectorSize =vsize;
//			vector = new float[vectorSize];
//            Arrays.fill(vector, -127); 
//			
//            int nonsCont=stream.readInt();
//			if(nonsCont!=0){
//				for(int j = 0; j < nonsCont; j++){
//					int index=stream.readShort();
//					vector[index]=stream.readShort();
//				}
//			}
//			
//			
//			
//			int normalizedvectorSize = vsize;
//			normalizedvector = new float[normalizedvectorSize];
//			int nonZerosCont=stream.readInt();
//			if(nonZerosCont!=0){
//				for (int j = 0; j < nonZerosCont; j++) {
//					int index=stream.readShort();
//					normalizedvector[index] = stream.readShort()/10000.0f;
//				}
//			}
//
//			AssociativeData data = new AssociativeData(p, vector);
//			data.setZ(z);
//			data.normalizedvector = normalizedvector;
//			theListTemp.add(data);
//
//		}
//
//		int minsSize = vsize;
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
//		setINDEX_MAP(INDEX_MAPtemp);
//		setSsidnames(ssidnamesTemp);
//		setTheList(theListTemp);
//		setMaxs(maxsTemp);
//		setMins(minsTemp);		

    }

    private void setMins(float[] minsTemp) {
        mins = minsTemp;

    }

    private void setMaxs(float[] maxsTemp) {
        maxs = maxsTemp;

    }

    private void setTheList(ArrayList<AssociativeData> theListTemp) {
        theList = theListTemp;

    }

    private void setSsidnames(ArrayList<String> ssidnamesTemp) {
        ssidnames = ssidnamesTemp;

    }

    private void setINDEX_MAP(HashMap<String, Integer> iNDEX_MAPtemp) {
        INDEX_MAP = iNDEX_MAPtemp;

    }


}
