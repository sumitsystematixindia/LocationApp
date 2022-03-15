package com.mlins.locator;

import android.graphics.PointF;

import com.mlins.utils.FacilityConf;
import com.mlins.utils.FacilityContainer;
import com.mlins.utils.Lookup;
import com.mlins.utils.PropertyHolder;
import com.mlins.wireless.WlBlip;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class FloorSelector extends AsociativeMemoryLocator {

    protected FloorSelector() {
        super();
        load(true);
    }

//	private List<FloorBssid> floorswifi = new ArrayList<FloorBssid>();

    public static FloorSelector getInstance() {
        return Lookup.getInstance().get(FloorSelector.class);

    }

    public static void releaseInstance() {
        Lookup.getInstance().remove(FloorSelector.class);
    }

//	@Override
//	public void load() {
//		boolean old = PropertyHolder.getInstance().isTypeBin();
//		PropertyHolder.getInstance().setTypeBin(false);
//		super.load();
//		PropertyHolder.getInstance().setTypeBin(old);
//	}

    public int getFloorByBlips(List<WlBlip> list) {
        int result = -100;
        findClosestPoint(list);

        FacilityConf facConf = FacilityContainer.getInstance().getCurrent();
        if (facConf == null) {
            return result;
        }


        if (getClosePoints() != null && getClosePoints().size() > 0) {
            List<AssociativeDataSorter> closepoints = getClosePoints();

            int floors = facConf.getFloorDataList().size(); //FacilityConf.getInstance().getFloorDataList().size();
            int max = 0;
            for (int i = 0; i < floors; i++) {
                int counter = 0;
                for (AssociativeDataSorter ads : closepoints) {
                    if (ads.data.getZ() == i) {
                        counter++;
                    }
                    if (counter > max) {
                        result = i;
                        max = counter;
                    }
                }
            }

            int counter = 0;
            for (AssociativeDataSorter ads : closepoints) {
                if (ads.data.getZ() == -989) {
                    counter++;
                }
                if (counter > max) {
                    result = -989;
                    max = counter;
                }
            }
        }
        return result;
    }

    @Override
    public boolean saveBin() {
        boolean matrixwritten = false;
        boolean firstmatrixwritten = false;

        if (theList == null) {
            return false;
        }
        if (theList.size() == 0) {
            return false;
        }

        FloorSelectionBinRep mbr = new FloorSelectionBinRep(getTheList(), INDEX_MAP, getSsidnames(), mins, maxs);

        File dir = new File(PropertyHolder.getInstance().getFacilityDir().toString());

        OutputStream outBin = null;

        File matrixBinFile = new File(dir, PropertyHolder.getInstance().getMatrixFilePrefix() + "floorselection.bin");
        //XXX bin matrix debug
//			String appPath = PropertyHolder.getInstance().getExternalStoragedir();
//			File matrixBinFile = new File(appPath, "test.bin");

        if (!matrixBinFile.exists()) {
            try {
                matrixBinFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {

            outBin = new BufferedOutputStream(new FileOutputStream(matrixBinFile, false));

            //ObjectOutputStream output = new ObjectOutputStream( outBin );

            mbr.writeObject(outBin);

            outBin.flush();
            //output.flush();
            //output.close();

            matrixwritten = true;
//				return true;
        } catch (Exception e) {
            e.toString();
        } finally {
            try {
                if (outBin != null) {
                    outBin.close();
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            outBin = null;
        }

        File firstmatrixBinFile = new File(dir, PropertyHolder.getInstance().getMatrixFilePrefix() + "firstfloorselection.bin");
        //XXX bin matrix debug
        //File firstmatrixBinFile = new File(appPath, "test.txt");
        try {
            copy(matrixBinFile, firstmatrixBinFile);
            firstmatrixwritten = true;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        if (matrixwritten && firstmatrixwritten) {
            return true;
        }
        return false;
    }

    @Override
    public void loadBin(int floor) {
        initIndexMap();
        String dir = PropertyHolder.getInstance().getFacilityDir().toString();
        File file = new File(dir, PropertyHolder.getInstance().getMatrixFilePrefix() + "floorselection.bin");
        //XXX bin matrix debug
        //String appPath = PropertyHolder.getInstance().getExternalStoragedir();
        //File file = new File(appPath, "test.bin");

        if (!file.isFile())
            return;
        InputStream buffer = null;
        //ObjectInputStream input=null;
        try {
            FloorSelectionBinRep mbr = new FloorSelectionBinRep(getTheList(), INDEX_MAP, getSsidnames(), mins, maxs);
            buffer = new BufferedInputStream(new FileInputStream(file));
            // input = new ObjectInputStream ( buffer );
            mbr.readObject(buffer);
            INDEX_MAP = mbr.getINDEX_MAP();
            theList = mbr.getTheList();
            ssidnames = mbr.getSsidnames();
            maxs = mbr.getMaxs();
            mins = mbr.getMins();


        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            try {
                if (buffer != null) {
                    buffer.close();
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }


        setZeroValue();
    }


    public void copy(File src, File dst) throws IOException {
        FileInputStream inStream = new FileInputStream(src);
        FileOutputStream outStream = new FileOutputStream(dst);
        FileChannel inChannel = inStream.getChannel();
        FileChannel outChannel = outStream.getChannel();
        inChannel.transferTo(0, inChannel.size(), outChannel);
        inStream.close();
        outStream.close();
    }


    @Override
    public List<MatrixPoint> convertBinToMatrixPoints() {
        List<MatrixPoint> matrixpoints = new ArrayList<MatrixPoint>();
        loadBin(0);
        for (AssociativeData o : theList) {
            PointF mppoint = o.getPoint();
            List<WlBlip> mpblips = new ArrayList<WlBlip>();
            float[] mpvector = o.vector;
            for (String s : INDEX_MAP.keySet()) {
                int index = INDEX_MAP.get(s);
                int level = (int) mpvector[index];
                if (level != -127) {
                    String SSID = ssidnames.get(index);
                    String BSSID = s;
                    WlBlip blip = new WlBlip(SSID, BSSID, level, 0, 0);
                    mpblips.add(blip);
                }
            }
            MatrixPoint mp = new MatrixPoint();
            mp.setPoint(mppoint);
            mp.setBlips(mpblips);
            mp.setZ(o.getZ());
            matrixpoints.add(mp);
        }
        load(0, true);
        return matrixpoints;
    }


//	public List<FloorBssid> getFloorswifi() {
//		return floorswifi;
//	}
//	
//	public void setFloorswifi() {
//		List<FloorBssid> tmp = new ArrayList<FloorBssid>();
//		int floors = FacilityConf.getInstance().getFloorDataList().size();
//		for (int i = 0; i < floors; i++) {
//			FloorBssid floor = new FloorBssid(i);
//			floor.loadwifilist();
//			tmp.add(floor);
//		}
//		setFloorswifi(tmp);
//	}
//
//	public void setFloorswifi(List<FloorBssid> floorswifi) {
//		this.floorswifi = floorswifi;
//	}
//	
//	public int getFloorByBlips(List<WlBlip> list) {
//		int currentz = FacilityConf.getInstance().getSelectedFloor();
//		int wifiminimum = PropertyHolder.getInstance().getWifilistminimum();
//		if (list.size() < wifiminimum) {
//			return currentz;
//		}
//		int result = -100;
//		List<FloorBssid> candidatefloors = getCandidateFloors(list);
////		int currentz = FacilityConf.getInstance().getSelectedFloor();
////		
////		
//		if (candidatefloors.size() == 1) {
//			return candidatefloors.get(0).getFloornumber();
//		}
//		
//		boolean isonecandidateonly = PropertyHolder.getInstance().isOnecandidatefloor();
//		if (isonecandidateonly) {
//			if (candidatefloors.size() > 1) {
//				return currentz;
//			}
//		}
//		
//		
//		
////		List<FloorBssid> candidatefloors = getFloorswifi();
//		
//		List<Associativefloor> afloors = new ArrayList<Associativefloor>();
//		double min = 100000;
//		for (FloorBssid floor : candidatefloors) {
//			AsociativeMemoryLocator.getInstance().load(floor.getFloornumber());
//			AsociativeMemoryLocator.getInstance().findClosestPoint(list);
////			List<AssociativeDataSorter> closepoints = AsociativeMemoryLocator.getInstance().getClosePoints();
//			List<AssociativeDataSorter> closepoints = AsociativeMemoryLocator.getInstance().getFloorClosePoints();
//			for (AssociativeDataSorter a : closepoints) {
//				Associativefloor f = new Associativefloor(a.data, a.getD());
//				f.setZ(floor.getFloornumber());
//				afloors.add(f);
//			}
//		}
//		
////		Collections.sort(afloors, new CustomComparator());
//		
//		Collections.sort(afloors, new Comparator<AssociativeDataSorter>() {
//			@Override
//			public int compare(AssociativeDataSorter lhs,
//					AssociativeDataSorter rhs) {
//				return lhs.compare(rhs);
//			}
//		});
//		
//		
//		if (PropertyHolder.getInstance().isSelectfloorbysum()) {
//			int minsum = 100000000;
//			for (FloorBssid floor : candidatefloors) {
//				int sumd = 0;
//				for (Associativefloor af : afloors) {
//					if (af.getZ() == floor.getFloornumber()) {
//						sumd += af.getD();
//					}
//				}
//				if (sumd < minsum) {
//					minsum = sumd;
//					result = floor.getFloornumber();
//				}
//			}
//		}
//		
//		
//		if (PropertyHolder.getInstance().isSelectfloorbybonus()) {
//			double max = 0;
//			for (FloorBssid floor : candidatefloors) {
//				double points = 0;
//				int index = 1;
//				for (Associativefloor af : afloors) {
//					if (index >= PropertyHolder.getInstance()
//							.getFloorSelectionK() + 1) {
//						break;
//					}
//					if (af.getZ() == floor.getFloornumber()) {
//						float w = 2.0f / (float) (index / 5 + 1);
//						points += 1 * w;
//					}
//					index++;
//				}
//				if (points > max) {
//					result = floor.getFloornumber();
//					max = points;
//				}
//			}
//		}
//		
//		
////		int maxpoints = 0;
////		for (FloorBssid floor : candidatefloors) {
////			int points = 0;
////			int index = 0;
////			for (Associativefloor af : afloors) {
//////				if (index >= 15) {
////				if (index >= PropertyHolder.getInstance().getFloorSelectionK()) {
////					break;
////				}
////				if (af.getZ() == floor.getFloornumber()) {
////					points++;
////				}
////				index++;
////			}
////			if (points > maxpoints) {
////				result = floor.getFloornumber();
////				maxpoints = points;
////			}
////		}
//		
//		
//		AsociativeMemoryLocator.getInstance().load();
//		for (FloorBssid floor : floorswifi) {
//			floor.setCounter(-1);
//		}
//		return result;
//	}
//	
//	
////	public class CustomComparator implements Comparator<AssociativeDataSorter> 
////	{
////	    @Override
////	    public int compare(AssociativeDataSorter a1, AssociativeDataSorter a2) {
////	    	Integer d1 = (int) a1.getD();
////	    	int d2 = (int) a2.getD();
////	        return d1.compareTo(d2);
////	    }
////	}
//	
////	public int getFloorByBlips(List<WlBlip> list) {
////		int result = -100;
////		List<FloorBssid> candidatefloors = getCandidateFloors(list);
////		int currentz = FacilityConf.getInstance().getSelectedFloor();
////		int index = 0;
////		
////		if (candidatefloors.size() == 1) {
////			return candidatefloors.get(0).getFloornumber();
////		}
////		
//////		if (currentz != -1) {
//////			for (FloorBssid floor : candidatefloors) {
//////				if (floor.getFloornumber() == currentz) {
//////					return currentz;
//////				}
//////			}
//////		}
////		double min = 100000;
////		for (FloorBssid floor : candidatefloors) {
////			AsociativeMemoryLocator.getInstance().load(index);
////			AsociativeMemoryLocator.getInstance().findClosestPoint(list);
////			AssociativeDataSorter data = AsociativeMemoryLocator.getInstance().getBestMatch();
////			if (data != null) {
////				if (min > data.getD()) {
////					min = data.getD();
////					result = index;
////				}
////				index++;
////			}
////		}
////		
////		AsociativeMemoryLocator.getInstance().load();
////		for (FloorBssid floor : floorswifi) {
////			floor.setCounter(-1);
////		}
////		return result;
////	}
//	
//	public List<FloorBssid> getCandidateFloors(List<WlBlip> list) {
//		List<FloorBssid> result = new ArrayList<FloorBssid>();
//		int max = 0;
//		for (FloorBssid floor : floorswifi) {
//			int counter = 0;
//			for (WlBlip blip : list) {
//				String bssid = blip.BSSID;
//				if (floor.getWifiList().contains(bssid)) {
//					counter ++;
//				}
//			}
//			floor.setCounter(counter);
//			
//			if (counter > max) {
//				max = counter;
//			}
//		}
//		
//		for (FloorBssid floor : floorswifi) {
//			if (floor.getCounter() == max) {
//				result.add(floor);
//			}
//		}
//		return result;
//	}

    @Override
    protected void loadDataList(Map<String, Map<String, Double>> pointsMap) {
        Set<String> pts = pointsMap.keySet();
        Map<String, Double> idMap;
        mins = maxs = null;
        for (String pt : pts) {
            idMap = pointsMap.get(pt);
            float[] v = new float[INDEX_MAP.size()];
            for (int i = 0; i < v.length; i++) {
                v[i] = -127;
            }
            for (Entry<String, Double> e : idMap.entrySet()) {
                int i = getArrayPosition(e.getKey());
                if (i != -1) {
                    v[i] = e.getValue().floatValue();
                }
            }
            updateMinMax(v);
            String[] coords = pt.split(",");
            PointF pf = new PointF(Float.valueOf(coords[0]),
                    Float.valueOf(coords[1]));
            AssociativeData data = new AssociativeData(pf, v);
            if (coords.length > 2) {
                data.setZ(Integer.parseInt(coords[2]));
            }
            theList.add(data);
        }
        normalizeList(mins, maxs);
        BaseMatrixDataHelper.getInstance().setMatrix(theList);
        BaseMatrixDataHelper.getInstance().setSSIDNames(ssidnames);
    }
}
