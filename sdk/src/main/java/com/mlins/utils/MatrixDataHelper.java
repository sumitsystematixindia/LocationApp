package com.mlins.utils;

import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;

import com.mlins.kdtree.KDimensionalTree;
import com.mlins.locator.AsociativeMemoryLocator;
import com.mlins.locator.AssociativeData;
import com.mlins.locator.BaseMatrixDataHelper;
import com.mlins.maping.LayerObject;
import com.mlins.views.TouchImageView;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class MatrixDataHelper extends BaseMatrixDataHelper {

    List<AssociativeData> matrix = new ArrayList<AssociativeData>();
    KDimensionalTree<PointF> kDataTree = null;
    private List<String> mNames;
    private boolean matrixCalShow = false;
    private boolean matrixLocShow = false;
    private AssociativeData currentpoint;

    private String matrixFileName = PropertyHolder.getInstance().getMatrixFilePrefix() + "matrix.txt";
    private File matrixFile;

    private MatrixDataHelper() {
        BaseMatrixDataHelper.setInstance(this);
    }

    public static MatrixDataHelper getInstance() {
        return Lookup.getInstance().get(MatrixDataHelper.class);
    }

    private void setKDataTree() {
        kDataTree = new KDimensionalTree<PointF>();
        try {
            for (int i = 0; i < matrix.size(); i++) {
                AssociativeData assData = matrix.get(i);
                kDataTree.addElement(assData.point, assData.point);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<AssociativeData> getMatrix() {
        return matrix;
    }

    @Override
    public void setMatrix(List<AssociativeData> nmatrix) {
        matrix = nmatrix;
        setKDataTree();
    }

    public List<String> getSSidNames() {
        if (mNames == null)
            return new ArrayList<String>();
        return mNames;
    }

    @Override
    public void setSSIDNames(List<String> names) {
        mNames = names;
    }

    public void addMatrix(TouchImageView tiv) {

        LayerObject matrixlayer = tiv.getLayerByName("marks");
        matrixlayer.clearSprites();
        //AsociativeMemoryLocator.getInstance().load(false);
//		for (AssociativeData pt : matrix) {
//			PointF point = pt.point;
//			int color = getLevelColor(pt.vector);
//			int r = 10;
//			int x = (int) point.x;
//			int y = (int) point.y;
//			ShapeDrawable sd = new ShapeDrawable(new OvalShape());
//			sd.getPaint().setColor(color);
//			sd.setBounds(x - r, y - r, x + r, y + r);
//			tiv.addMark(sd);
//
//		}


        for (int i = 0; i < matrix.size(); i++) {
            //if(i% 2==0){
            AssociativeData pt = matrix.get(i);
            PointF point = pt.point;
            int color = getLevelColor(pt.vector);
            int r = 10;
            int x = (int) point.x;
            int y = (int) point.y;
            ShapeDrawable sd = new ShapeDrawable(new OvalShape());
            sd.getPaint().setColor(color);
            sd.setBounds(x - r, y - r, x + r, y + r);
            tiv.addMark(sd);
            //}

        }

        tiv.invalidate();
    }

    private int getLevelColor(float[] vector) {
        int index;
        float level;
        float min;
        float max;
        float d;
        float delta;
        index = PropertyHolder.getInstance().getSsidindex();
        level = vector[index];
        min = AsociativeMemoryLocator.getInstance().mins[index];
        max = AsociativeMemoryLocator.getInstance().maxs[index];
        d = max - min;
        delta = (255.0f) / d;
        int change = (int) ((level - min) * delta);

        return Color.argb(127 + (change / 2), change, 255 - change, 0);
    }

    public void removeMatrix(TouchImageView tiv) {
        LayerObject matrixlayer = tiv.getLayerByName("marks");
        matrixlayer.clearSprites();
        tiv.invalidate();
    }

    public void clearMatrix() {
        matrix.clear();
    }

    public boolean isMatrixShowCal() {
        return matrixCalShow;
    }

    public void setMatrixShowCal(boolean matrixShow, TouchImageView tiv) {
        if (matrixShow) {
            addMatrix(tiv);
        } else {
            removeMatrix(tiv);
        }
        this.matrixCalShow = matrixShow;
    }

    public boolean isMatrixShowLoc() {
        return matrixLocShow;
    }

    public void setMatrixShowLoc(boolean matrixLocShow, TouchImageView tiv) {
        if (matrixLocShow) {
            addMatrix(tiv);
        } else {
            removeMatrix(tiv);
        }
        this.matrixLocShow = matrixLocShow;
    }

    public AssociativeData pointExists(PointF point) {

        for (AssociativeData dot : matrix) {
            if ((point.x <= (dot.point.x + 24))
                    && (point.x >= (dot.point.x - 24))
                    && (point.y <= (dot.point.y + 24))
                    && (point.y >= (dot.point.y - 24))) {
                return dot;

            }
        }

        return null;

    }

    public AssociativeData getCurrentpoint() {
        return currentpoint;
    }

    public void setCurrentpoint(AssociativeData currentpoint) {
        this.currentpoint = currentpoint;
    }


    public boolean deleteAreaFromMatrix(PointF lowPt, PointF uppPt) {


        try {


            ArrayList<PointF> deletedArea = kDataTree.getObjectsInRange(lowPt, uppPt);


            // update txt matrix and save to txt file
            updateTxt(deletedArea);
            System.out.println("updateTxt done!");
            // reload txt matirx
            AsociativeMemoryLocator.getInstance().load(false);
            System.out.println("load txt done!");
            // save bin matrix
            //AsociativeMemoryLocator.getInstance().saveBin();
            //System.out.println("save bin done!");


        } catch (Exception e) {
            e.printStackTrace();
        }

        return true;
    }


    private void updateTxt(List<PointF> exceptPoints) {

        String fName = AsociativeMemoryLocator.getFileName();
        File dir = PropertyHolder.getInstance().getFloorDir();
        File fileMatrix = new File(dir, fName);
        String timeStamp = "_" + System.currentTimeMillis();
        File newfileMatrix = new File(dir, fName + timeStamp);


        FileWriter fw = null;
        BufferedWriter out = null;
        BufferedReader br = null;

        try {
            br = new BufferedReader(new FileReader(fileMatrix));
            fw = new FileWriter(newfileMatrix, false);
            out = new BufferedWriter(fw);

            String line = null;
            while ((line = br.readLine()) != null) {
                String[] fields = line.split("\t");

                float x = Float.valueOf(fields[0]);
                float y = Float.valueOf(fields[1]);
                PointF p = new PointF(x, y);
                if (!exceptPoints.contains(p)) {
                    out.write(line + "\n");
                } else {
                    System.out.println("deleted point:  " + line);
                }
            }

            try {
                br.close();
                out.close();
                fw.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

            File oldfile = new File(dir, fName + timeStamp + "_bk");
            fileMatrix.renameTo(oldfile);
            File newfile = new File(dir, fName);
            newfileMatrix.renameTo(newfile);

        } catch (IOException e) {
            e.toString();
        } finally {

            try {
                if (out != null) {

                    out.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            out = null;

            try {
                if (fw != null) {
                    fw.close();

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            fw = null;


            try {
                if (br != null)
                    br.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            br = null;
        }

    }


//	public void updateClibrationPoints() {
//
//		File dir = new File(PropertyHolder.getInstance().getFloorDir(),
//				"scan results");
//
//		matrixFile = new File(dir, matrixFileName);
//		if (!matrixFile.exists()) {
//			try {
//				matrixFile.createNewFile();
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//		} else {
//			matrixFile.delete();
//		}
//		BufferedWriter out = null;
//		try {
//			FileWriter fw = new FileWriter(matrixFile, true);
//			out = new BufferedWriter(fw);
//			StringBuffer sb = new StringBuffer();
//
//			Set<String> bssidarr = AsociativeMemoryLocator.getInstance().INDEX_MAP
//					.keySet();
//
//			final float CHUNK_S = 10.0f;
//			int chunkSize = (int) (matrix.size() / CHUNK_S);
//			if (chunkSize == 0)
//				chunkSize = matrix.size() - 1;
//
//			for (int j = 0; j < matrix.size(); j++) {
//
//				AssociativeData dot = matrix.get(j);
//				
//				StringBuffer LineToAdd=null;
//				for (int i = 0; i < dot.vector.length; i++) {
//					LineToAdd=new StringBuffer();
//					LineToAdd.append(dot.point.x + "\t");
//					LineToAdd.append(dot.point.y + "\t");
//					//sb.append(dot.point.x + "\t");
//					//sb.append(dot.point.y + "\t");
//					String bssid = "";
//					for (Iterator<String> iterator = bssidarr.iterator(); iterator
//							.hasNext();) {
//						bssid = (String) iterator.next();
//						Integer index = (Integer) AsociativeMemoryLocator
//								.getInstance().INDEX_MAP.get(bssid);
//						if (index == i) {
//							break;
//						}
//
//					}
//					LineToAdd.append(bssid + "\t");
//					//sb.append(bssid + "\t");
//					LineToAdd.append(mNames.get(i) + "\t");
//					//sb.append(mNames.get(i) + "\t");
//					if(dot.vector[i]!=-127){  // don't add vectors with -127! 
//						LineToAdd.append(((int)dot.vector[i])); //don't forget to convert to int (for file freeing space)
//						
//						//sb.append(dot.vector[i]);
//						LineToAdd.append("\n");
//						sb.append(LineToAdd.toString());
//					}
//				}
//
//				if (j % chunkSize == 0 || (j == matrix.size() - 1)) {
//
//					out.write(sb.toString());
//					sb = new StringBuffer();
//					out.flush();
//
//				}
//			}
//		} catch (IOException e) {
//			e.toString();
//		} finally {
//			try {
//				if (out != null)
//					out.close();
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//			out = null;
//		}
//
//	}

    public void removepoint(AssociativeData currentpoint) {

        File dir = new File(PropertyHolder.getInstance().getFloorDir(),
                "scan results");
        matrixFile = new File(dir, matrixFileName);
        if (!matrixFile.exists()) {
            try {
                matrixFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        matrix.remove(currentpoint);
        StringBuffer sb = new StringBuffer();

        Set<String> bssidarr = AsociativeMemoryLocator.getInstance().INDEX_MAP
                .keySet();

        for (AssociativeData dot : matrix) {
            for (int i = 0; i < dot.vector.length; i++) {
                sb.append(dot.point.x + "\t");
                sb.append(dot.point.y + "\t");
                String bssid = "";
                for (Iterator<String> iterator = bssidarr.iterator(); iterator
                        .hasNext(); ) {
                    bssid = (String) iterator.next();
                    Integer index = (Integer) AsociativeMemoryLocator
                            .getInstance().INDEX_MAP.get(bssid);
                    if (index == i) {
                        break;
                    }

                }
                sb.append(bssid + "\t");
                sb.append(mNames.get(i) + "\t");
                sb.append(dot.vector[i]);
                sb.append("\n");
            }
        }

        BufferedWriter out = null;
        try {
            out = new BufferedWriter(new FileWriter(matrixFile, false));
            out.write(sb.toString());
            out.flush();
        } catch (IOException e) {
            e.toString();
        } finally {
            try {
                if (out != null)
                    out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            out = null;
        }

    }

    public void downloadFloorMatrix(boolean onlyifnotexists) {
        String server = PropertyHolder.getInstance().getServerName();
        String projectn = PropertyHolder.getInstance().getProjectId();
        String campusn = PropertyHolder.getInstance().getCampusId();
        String facilityn = PropertyHolder.getInstance().getFacilityID();
        String url = server + "res/" + projectn + "/" + campusn + "/" + facilityn + "/" + PropertyHolder.getInstance()
                .getMatrixFilePrefix() + "floorselection.bin";
        File dir = PropertyHolder.getInstance().getFacilityDir();
        String floorsmatrixBinFileName = PropertyHolder.getInstance()
                .getMatrixFilePrefix() + "floorselection.bin";
        File floorsmatrixBinFile = new File(dir, floorsmatrixBinFileName);
        dir = floorsmatrixBinFile.getParentFile();
        if (!dir.exists()) {
            dir.mkdirs();
        }
        if (!onlyifnotexists || !floorsmatrixBinFile.exists()) {
            byte[] data = ServerConnection.getInstance().getResourceBytes(url);
            if (data != null && data.length > 0) {
                DownloadUtils.writeLocalCopy(floorsmatrixBinFile, data);
            }
        }
    }

    public void downloadMatrix(int floor) {
        downloadMatrix(floor, false);
    }

    public void downloadMatrix(int floor, boolean onlyifnotexists) {
        String server = PropertyHolder.getInstance().getServerName();
        String projectn = PropertyHolder.getInstance().getProjectId();
        String campusn = PropertyHolder.getInstance().getCampusId();
        String facilityn = PropertyHolder.getInstance().getFacilityID();
        String floorn = String.valueOf(floor);
        String url = server + "res/" + projectn + "/" + campusn + "/" + facilityn + "/matrix/" + floorn
                + "/" + PropertyHolder.getInstance()
                .getMatrixFilePrefix() + "matrix.bin";
        File floordir = new File(
                PropertyHolder.getInstance().getFacilityDir(), floorn);
        File dir = new File(floordir, "scan results");
        String matrixBinFileName = PropertyHolder.getInstance()
                .getMatrixFilePrefix() + "matrix.bin";
        File matrixBinFile = new File(dir, matrixBinFileName);
        dir = matrixBinFile.getParentFile();
        if (!dir.exists()) {
            dir.mkdirs();
        }
        if (!onlyifnotexists || !matrixBinFile.exists()) {
            byte[] data = ServerConnection.getInstance().getResourceBytes(url);
            if (data != null && data.length > 0) {
                DownloadUtils.writeLocalCopy(matrixBinFile, data);
            }
        }
    }


    public boolean isMatrixCalShow() {
        return matrixCalShow;
    }

    public void setMatrixCalShow(boolean matrixCalShow) {
        this.matrixCalShow = matrixCalShow;
    }

}
