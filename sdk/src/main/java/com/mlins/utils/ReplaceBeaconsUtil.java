package com.mlins.utils;

import com.mlins.locator.AsociativeMemoryLocator;
import com.mlins.locator.FloorSelector;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class ReplaceBeaconsUtil {
    private static String floorsMatrixFileName = PropertyHolder.getInstance()
            .getMatrixFilePrefix() + "floorselection.txt";
    ;
    private static String matrixFileName = PropertyHolder.getInstance()
            .getMatrixFilePrefix() + "matrix.txt";

    public static String replaceBeacon(String oldbeacon, String newbeacon) {
        String result = "";
        File facdir = PropertyHolder.getInstance().getFacilityDir();
        File matrixfile = new File(facdir, floorsMatrixFileName);
        int floorsmatrixcounter = 0;
        int floorsbincounter = 0;
        int matrixcounter = 0;
        int bincounter = 0;
        boolean floormatrixsaved = replaceInFile(matrixfile, oldbeacon,
                newbeacon);

        if (floormatrixsaved) {
            floorsmatrixcounter++;
            FloorSelector fml = FloorSelector.getInstance();
            fml.load(true);
            if (fml.saveBin()) {
                floorsbincounter++;
            }
        }

        FacilityConf fConf = FacilityContainer.getInstance().getSelected();
        if (fConf != null) {

            List<FloorData> floorsdata = fConf.getFloorDataList();

            AsociativeMemoryLocator aml = AsociativeMemoryLocator.getInstance();
            for (FloorData o : floorsdata) {
                int floornumber = floorsdata.indexOf(o);
                File floordir = new File(facdir, String.valueOf(floornumber));
                if (floordir.exists()) {
                    File scanresultsdir = new File(floordir, "scan results");
                    if (scanresultsdir.exists()) {
                        matrixfile = new File(scanresultsdir, matrixFileName);
                        boolean matrixsaved = replaceInFile(matrixfile, oldbeacon,
                                newbeacon);
                        if (matrixsaved) {
                            matrixcounter++;
                            aml.load(floornumber, false);
                            if (aml.saveBin()) {
                                bincounter++;
                            }
                        }
                    }
                }

            }
            aml.load(false);
            result = "replaced :" + "\n" + floorsmatrixcounter
                    + " floors txt files" + "\n" + floorsbincounter
                    + " floors bin files" + "\n" + matrixcounter
                    + " floor txt files" + "\n" + bincounter + " floor bin files";
        }
        return result;
    }

    private static boolean replaceInFile(File matrixfile, String oldbeacon,
                                         String newbeacon) {
        boolean result = false;
        String filetxt = getFileContentASString(matrixfile);
        String newtext = filetxt.replaceAll(oldbeacon, newbeacon);
        if (newtext != null && !newtext.isEmpty()) {
            saveFile(matrixfile, newtext);
            result = true;
        }
        return result;
    }

    private static void saveFile(File matrixfile, String filetxt) {
        if (matrixfile.exists()) {
            BufferedWriter out = null;
            try {

                out = new BufferedWriter(new FileWriter(matrixfile, false));
                out.write(filetxt);
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
    }

    private static String getFileContentASString(File matrixfile) {
        String result = "";
        BufferedReader inlocal = null;
        if (matrixfile.exists()) {
            try {
                inlocal = new BufferedReader(new FileReader(matrixfile));
                String line = null;
                while ((line = inlocal.readLine()) != null) {
                    result += line + "\n";
                }
                inlocal.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }
}
