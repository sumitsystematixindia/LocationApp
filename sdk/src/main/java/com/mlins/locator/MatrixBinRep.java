package com.mlins.locator;

import android.graphics.PointF;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class MatrixBinRep {

    protected List<AssociativeData> theList;
    protected Map<String, Integer> INDEX_MAP;
    protected List<String> ssidnames;
    protected float mins[] = new float[0];
    protected float maxs[] = new float[0];

    public MatrixBinRep(List<AssociativeData> theList,
                        Map<String, Integer> iNDEX_MAP, List<String> ssidnames,
                        float[] mins, float[] maxs) {
        super();
        this.theList = theList;
        INDEX_MAP = iNDEX_MAP;
        this.ssidnames = ssidnames;
        this.mins = mins;
        this.maxs = maxs;
    }

    public MatrixBinRep(Map<String, Integer> iNDEX_MAP, List<String> ssidnames) {
        super();

        INDEX_MAP = iNDEX_MAP;
        this.ssidnames = ssidnames;
    }

    public List<AssociativeData> getTheList() {
        return theList;
    }

    public Map<String, Integer> getINDEX_MAP() {
        return INDEX_MAP;
    }

    public List<String> getSsidnames() {
        return ssidnames;
    }

    public float[] getMins() {
        return mins;
    }

    public float[] getMaxs() {
        return maxs;
    }

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


    }

    public void readObject(InputStream stream) throws Exception, ClassNotFoundException {

        List<AssociativeData> theListTemp = new ArrayList<AssociativeData>();
        Map<String, Integer> INDEX_MAPtemp = new HashMap<String, Integer>();
        List<String> ssidnamesTemp = new ArrayList<String>();
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

        for (int i = 0; i < theListSize; i++) {

            p = new PointF();

            p.x = readInt(stream);
            //System.out.println("MatrixBinRep: x "+p.x);

            p.y = readInt(stream);
            //System.out.println("MatrixBinRep: y "+p.y);


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

        AsociativeMemoryLocator aml = AsociativeMemoryLocator.getInstance();
        aml.setINDEX_MAP(INDEX_MAPtemp);
        aml.setSsidnames(ssidnamesTemp);
        aml.setTheList(theListTemp);
        aml.setMaxs(maxsTemp);
        aml.setMins(minsTemp);

    }

    protected int countNoN127(float[] vector) {
        int c = 0;
        for (float val : vector) {
            if (val != -127.0) {
                c++;
            }
        }
        return c;
    }


    protected int countNoNZeros(float[] vector) {
        int c = 0;
        for (float val : vector) {
            if (val != 0.0) {
                c++;
            }
        }
        return c;
    }


    protected void writeUTF8String(OutputStream stream, String str) throws Exception {

        int len = str.length();
        writeInt(stream, len);

        if (len != 0) {
            byte[] strToByte = str.getBytes(Charset.defaultCharset());
            stream.write(strToByte);
        }
    }

    protected String readUTF8String(InputStream stream) throws Exception {

        String result = "";

        int len = readInt(stream);
        if (len != 0) {
            byte[] buffer = new byte[len];
            stream.read(buffer);
            result = new String(buffer, Charset.defaultCharset());
        }
        return result;

    }


    protected void writeInt(OutputStream stream, int value) throws Exception {
        byte[] buffer = new byte[4];

        buffer[3] = (byte) (value >>> 24);
        buffer[2] = (byte) (value >>> 16);
        buffer[1] = (byte) (value >>> 8);
        buffer[0] = (byte) value;

        stream.write(buffer);
    }

    protected int readInt(InputStream stream) throws Exception {

        byte buffer[] = new byte[4];
        stream.read(buffer);

        int b3 = (buffer[3] & 0xFF);
        int b2 = (buffer[2] & 0xFF);
        int b1 = (buffer[1] & 0xFF);
        int b = (buffer[0] & 0xFF);
        int result = (b3 << 24) | (b2 << 16) | (b1 << 8) | (b);

        return result;

    }

		  
	/*
         public void writeObject(ObjectOutputStream stream) throws IOException {

		stream.writeInt(theList.size());
		short vsize =(short) theList.get(0).vector.length;
		stream.writeShort(vsize);
		

		for (AssociativeData al : theList) {

			int x = Math.round(al.getPoint().x);
			stream.writeShort(x);
			int y = Math.round(al.getPoint().y);
			stream.writeShort(y);
			
			int angle=(int)al.getAngle();
			stream.writeShort(angle);

			//stream.writeInt(al.vector.length);
//			for (double v : al.vector) {
//				stream.writeShort((int)v);
//			}
			
			float[] v=al.vector;
			int vcnt=countNoN127(v); 
			stream.writeInt(vcnt);
			for (int i=0; i< v.length;i++) {
				if(v[i] != -127.0){
					stream.writeShort(i);
					stream.writeShort((int)v[i]);	
				}
			}

			//stream.writeInt(al.normalizedvector.length);
//			int inv=0;
//			for (double nv : al.normalizedvector) {
//				inv=(int)(nv*10000);
//				stream.writeShort(inv);
//			}
			
			float[] nv=al.normalizedvector;
			int nvcnt=countNoNZeros(nv);
			stream.writeInt(nvcnt);
			int inv=0;
			for (int i=0;i< nv.length;i++) {
				if(nv[i]!=0.0){
					inv=(int)(nv[i]*10000);
					stream.writeShort(i);
					stream.writeShort(inv);
				}
			}
			
		

		}

		//stream.writeInt(mins.length);
		for (double mn : mins) {

			stream.writeShort((int)mn);
		}

		//stream.writeInt(maxs.length);
		for (double mx : maxs) {

			stream.writeShort((int)mx);
		}

		stream.writeInt(INDEX_MAP.size());
		for (Entry<String, Integer> elm : INDEX_MAP.entrySet()) {
			stream.writeObject(elm.getKey());
			stream.writeInt(elm.getValue());
		}

		stream.writeInt(ssidnames.size());
		for (String ssidn : ssidnames) {
			stream.writeObject(ssidn);
		}
		
		

	}

	public void readObject(ObjectInputStream stream) throws IOException,
			ClassNotFoundException {

		ArrayList<AssociativeData> theListTemp = new ArrayList<AssociativeData>();
		HashMap<String, Integer> INDEX_MAPtemp = new HashMap<String, Integer>();
		ArrayList<String> ssidnamesTemp = new ArrayList<String>();
		String key = null;
		Integer value = null; 

		int theListSize = stream.readInt();
		int vsize=stream.readShort();

		PointF p = null;
		float minsTemp[] = null;
		float maxsTemp[] = null;
		float normalizedvector[] = null;
		float vector[] = null;
		float angle;
		for (int i = 0; i < theListSize; i++) {
			p = new PointF();
			p.x = stream.readShort();
			p.y = stream.readShort();

			angle=stream.readShort();
			

//			int vectorSize = vsize;
//			vector = new float[vectorSize];
//
//			for (int j = 0; j < vector.length; j++) {
//				vector[j] = stream.readShort();
//			}
			
			int vectorSize =vsize;
			vector = new float[vectorSize];
            Arrays.fill(vector, -127); 
			
            int non127Cont=stream.readInt();
			if(non127Cont!=0){
				for(int j = 0; j < non127Cont; j++){
					int index=stream.readShort();
					vector[index]=stream.readShort();
				}
			}
			
			
//			int normalizedvectorSize = vsize;
//			normalizedvector = new float[normalizedvectorSize];
//
//			for (int j = 0; j < normalizedvector.length; j++) {
//				normalizedvector[j] = stream.readShort()/10000.0f;
//
//			}
			
			int normalizedvectorSize = vsize;
			int nonZerosCont=stream.readInt();
			normalizedvector = new float[normalizedvectorSize];
			if(nonZerosCont!=0){
				for (int j = 0; j < nonZerosCont; j++) {
					int index=stream.readShort();
					normalizedvector[index] = stream.readShort()/10000.0f;
				}
			}

			AssociativeData data = new AssociativeData(p, vector);
			data.normalizedvector = normalizedvector;
			data.setAngle(angle);
			theListTemp.add(data);

		}

		int minsSize =vsize;
		minsTemp = new float[minsSize];
		for (int i = 0; i < minsTemp.length; i++) {
			minsTemp[i] = stream.readShort();
		}
		int maxsSize = vsize;
		maxsTemp = new float[maxsSize];
		for (int i = 0; i < maxsTemp.length; i++) {
			maxsTemp[i] = stream.readShort();
		}

		int INDEX_MAPsize = stream.readInt();
		for (int i = 0; i < INDEX_MAPsize; i++) {
			key = (String) stream.readObject();
			value = stream.readInt();
			INDEX_MAPtemp.put(key, value);
		}

		int ssidnamesSize = stream.readInt();
		String ssidn = null;
		for (int i = 0; i < ssidnamesSize; i++) {
			ssidn = (String) stream.readObject();
			ssidnamesTemp.add(ssidn);
		}

		AsociativeMemoryLocator aml = AsociativeMemoryLocator.getInstance();
		aml.setINDEX_MAP(INDEX_MAPtemp);
		aml.setSsidnames(ssidnamesTemp);
		aml.setTheList(theListTemp);
		aml.setMaxs(maxsTemp);
		aml.setMins(minsTemp);

	}
	 */

}
