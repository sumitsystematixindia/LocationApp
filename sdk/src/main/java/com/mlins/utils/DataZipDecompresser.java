package com.mlins.utils;

import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class DataZipDecompresser {

    private ZipInputStream zin;
    private String loc;
    private String originZipFileName = null;

    public DataZipDecompresser(ZipInputStream zin, String location) {
        this.zin = zin;
        loc = location;

        dirChecker("");
    }


    public DataZipDecompresser(ZipInputStream zin, String location, String originZipDirName) {
        this.zin = zin;
        loc = location;
        this.originZipFileName = originZipDirName;
        dirChecker("");
    }


    public boolean unzip() {
        try {

            ZipEntry ze = null;
            while ((ze = zin.getNextEntry()) != null) {
                Log.v("Decompress", "Unzipping " + ze.getName());

                if (ze.isDirectory()) {
                    String fn = ze.getName();
//					if(originZipFileName!=null){			
//							fn = fn.replaceAll(originZipFileName,"mlins");
//					}
                    dirChecker(fn);
                } else {
//					if (ze.getName().contains("floorselection.bin") || ze.getName().contains("matrix.bin")) {
//						File f = new File(loc + ze.getName());
//						if (f.exists()) {
//							continue;
//						}
//					}
                    // byte[] buff = new byte[4096];
                    FileOutputStream fout = null;
                    String fn = null;
                    try {
                        fn = ze.getName();
//						if(originZipFileName!=null){
//								fn = fn.replaceAll(originZipFileName,"mlins");	
//						}

                        createDir(fn);

                        fout = new FileOutputStream(loc + fn, false);

                        byte[] buffer = new byte[4096];
                        int n = -1;
                        while ((n = zin.read(buffer)) > 0) {

                            fout.write(buffer, 0, n);
//							if (n < 4096)
//								break;

                        }


                    } catch (Throwable t) {
                        Log.v("ERROR",
                                ": SOME FILES NOT COPIED IN Decompress ZIP");
                    } finally {
                        if (zin != null) {
                            zin.closeEntry();
                        }
                        if (fout != null) {
                            fout.close();
                        }
                    }

                    if (fn != null) {
                        copyFloorGroupsFile(fn);
                    }
                }

            }
            //zin.close();
            return true;

        } catch (Exception e) {
            Log.e("Decompress", "unzip", e);
            return false;
        } finally {
            if (zin != null) {
                try {
                    zin.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    private void createDir(String uri) {

        try {
            int index = uri.lastIndexOf("/");
            String path = uri.substring(0, index);
            dirChecker(path);
        } catch (Throwable t) {
            //System.out.println("DataZipDecompresser::path not exists " + uri);
        }
    }


    private void dirChecker(String dir) {
        File f = new File(loc + dir);

        if (!f.isDirectory()) {
            f.mkdirs();
        }
    }


    private void copyFloorGroupsFile(String filePath) {

        if (PropertyHolder.useZip) {

            try {
                String path = new String(filePath);
                if (path.endsWith("floor_groups.json") && path.contains("/facilities/")
                        && path.contains("/floors/") && path.contains("/android/")) {

                    String appDirPath = PropertyHolder.getInstance().getZipAppdir().getAbsolutePath();
                    String copyPath = path.replaceAll("/android/", "/");
                    String newPath = appDirPath + "/" + copyPath;
                    File originPath = new File(loc + path);
                    byte[] bytes = getFloorGroupsLocalResCopy(originPath);
                    if (bytes != null && bytes.length > 0) {
                        File newFile = new File(newPath);
                        writeFloorGroupsLocalCopy(newFile, bytes);
                    }

                }
            } catch (Throwable e) {
                System.out.println(e.getMessage());
            }

        }

    }

    public boolean writeFloorGroupsLocalCopy(File file, byte[] bytes) {

        FileOutputStream output = null;
        try {
            output = new FileOutputStream(file);
            output.write(bytes);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            if (output != null) {
                try {
                    output.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return true;

    }

    private byte[] getFloorGroupsLocalResCopy(File f) {

        ByteArrayOutputStream stream = new ByteArrayOutputStream(4096 * 2);

        InputStream in = null;
        try {
            in = new FileInputStream(f);
            byte[] buffer = new byte[4096 * 2];
            int n = -1;
            while ((n = in.read(buffer)) != -1) {
                if (n > 0) {
                    stream.write(buffer, 0, n);
                }
            }
        } catch (IOException e) {
            //e.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                    stream.close();
                } catch (IOException e) {
                    //e.printStackTrace();
                }
            }
        }
        return stream.toByteArray();
    }
}