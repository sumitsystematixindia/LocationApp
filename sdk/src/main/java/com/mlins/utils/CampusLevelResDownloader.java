package com.mlins.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class CampusLevelResDownloader {

    static CampusLevelResDownloader Cinstance = null;
    protected File mDir;
    protected Bitmap cashedBmp = null;
    protected String cashedUrl = null;
    protected HashMap<String, String> md5LookUpTable = new HashMap<String, String>();
    protected HashMap<String, String> localmd5Table = new HashMap<String, String>();
    List<String> inLine = new ArrayList<String>();
    // static final String dir = "/";
    // SplashScreen mScr;
    // String resDirName;
    String[] mUrls;
    Map<String, String> mFileNames = new HashMap<String, String>();
    private ResListener mListener;
    private File resListFile;
    private boolean mFinished = false;

    public CampusLevelResDownloader() {
        File f = PropertyHolder.getInstance().getCampusDir();
        if (f != null) {
            mDir = new File(f + "/campus_res");
            loadResFile();
        }
    }

    public static CampusLevelResDownloader getCInstance() {
        if (Cinstance == null) {
            Cinstance = new CampusLevelResDownloader();
        }
        return Cinstance;
    }

    public static void releaseCinstance() {
        if (Cinstance != null) {
            Cinstance.saveResFile();
        }
        Cinstance = null;
    }

    public static String bytesToHex(byte[] bytes) {
        StringBuffer hexString = new StringBuffer();
        for (int i = 0; i < bytes.length; i++)
            if (bytes[i] < 16) {
                hexString.append("0" + Integer.toHexString(bytes[i]));
            } else {
                hexString.append(Integer.toHexString(bytes[i]));
            }

        return hexString.toString();
    }

    private byte[] getResConfigFile(String url) {
        byte[] bytes = null;

        if (url != null) {
            String baseResUrl = ServerConnection.getBaseUrlOfCampusResList(PropertyHolder.getInstance().getCampusId());
            try {
                bytes = ServerConnection.getInstance().getResourceBytes(url);

                if (bytes != null && bytes.length > 0) {

                    // check if uptodate
                    String resjson = new String(bytes);

                    if (!resjson.equals("up_to_date")) {

                        String fname = checksum(url.getBytes()) + ".bin";
                        File dir = new File(getdirName());
                        if (!dir.exists()) {
                            dir.mkdirs();
                        }

                        File file = new File(dir, fname);
                        if (writeLocalCopy(file, bytes)) {

                            // mFileNames.put(url, file.getAbsolutePath());
                            // XXX DYNAMIC RELATIVE PATH
                            mFileNames.put(baseResUrl, file.getName());
                        }
                    }

                } else {
                    // return local copy if exists
                    if (mFileNames.containsKey(baseResUrl) && !isChanged(baseResUrl)) {
                        bytes = getLocalCopy(baseResUrl);
                    }
                }
            } catch (Exception e) {
                // return local copy if exists
                if (mFileNames.containsKey(baseResUrl) && !isChanged(baseResUrl)) {
                    bytes = getLocalCopy(baseResUrl);
                }
            }
        }

        return bytes;

    }

    public String getdirName() {
        File dir = new File(PropertyHolder.getInstance().getCampusDir() + "/campus_res");
        return dir.toString();
    }

    public byte[] getUrl(String url) {
        boolean force = false;
        return getUrl(url, force);
    }

    public byte[] getUrl(String url, boolean force) {

        if (PropertyHolder.useZip) {
            ResPathConverter pathConverter = new ResPathConverter();
            byte[] bytes = pathConverter.getData(url);
            if (bytes != null && bytes.length > 0) {
                return bytes;
            }
        }

        // XXX OPTIMIZE LOAD WITH MD5
        if (url != null && url.contains("navserver?req_type=1&campus_id=")) {
            return getResConfigFile(url);
        }

        if (url != null && mFileNames.containsKey(url) && !isChanged(url)) {
            return getLocalCopy(url);

        } else {
            byte[] bytes = new byte[0];
            if (!PropertyHolder.useZip || force) {
                bytes = ServerConnection.getInstance().getResourceBytes(url);
                if (bytes != null && bytes.length > 0 && PropertyHolder.getInstance().getCampusDir() != null) {
                    String fname = checksum(url.getBytes()) + ".bin";
                    File dir = new File(getdirName());
                    if (!dir.exists()) {
                        dir.mkdirs();
                    }

                    File file = new File(dir, fname);
                    if (writeLocalCopy(file, bytes)) {

                        // mFileNames.put(url, file.getAbsolutePath());
                        // XXX DYNAMIC RELATIVE PATH
                        mFileNames.put(url, file.getName());

                        String value = md5LookUpTable.get(url);
                        if (value == null) {
                            value = "null";
                        }
                        localmd5Table.put(url, value);
                    }
                }
            }
            return bytes;
        }

    }

    public void addMd5(String fullpathFileName, String md5) {
        md5LookUpTable.put(ServerConnection.getResourcesUrl() + fullpathFileName, md5);
    }

    public void onDemandDownload(String[] urls) {

        if (!mDir.exists()) {
            mDir.mkdirs();
        }

        for (int i = 0; i < urls.length; i++) {

//			if (mFileNames.containsKey(urls[i])) {
//				continue;
//			}
            ServerConnection.getInstance().getCampusRawResource(urls[i]);
        }

        saveResFile();
    }

    private String getCampusRelativeResDataPath() {
        return PropertyHolder.getInstance().getCampusDir() + "/" + "campus_res" + File.separator;
    }

    // ==================

    public byte[] getLocalCopy(String url) {
        if (url == null)
            return new byte[0];
        String lurl = ServerConnection.getInstance().translateCampusResUrl(url);

        if (PropertyHolder.useZip) {
            ResPathConverter pathConverter = new ResPathConverter();
            byte[] bytes = pathConverter.getData(lurl);
            if (bytes != null && bytes.length > 0) {
                return bytes;
            }
        }

        String absolutePath = getCampusRelativeResDataPath();

        String localUrl = mFileNames.get(lurl);
        if (localUrl == null) {
            return new byte[0];
        }

        String file = absolutePath + localUrl;


        File f = new File(file);
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
            e.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                    stream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return stream.toByteArray();
    }

    public void execute(String[] urls) {

        mUrls = urls;
        Thread t = new Thread(new Runnable() {

            @Override
            public void run() {

                doInBackground(mUrls);
                onPostExecute(mUrls);

            }
        });
        t.start();
    }

    public void registerListener(ResListener listener) {
        mListener = listener;
    }

    // dirName = PropertyHolder.getInstance().getAppDir() +
    // "/clibration_res";

    public boolean isFinished() {
        return mFinished;
    }

    protected String[] doInBackground(String... urls) {
        mFinished = false;

        // resDirName = dir.toString();

        if (!mDir.exists()) {
            mDir.mkdirs();
        }

        String[] files = new String[urls.length];
        for (int i = 0; i < urls.length; i++) {
            if (mFileNames.containsKey(urls[i])) {
                continue;
            }
            ServerConnection.getInstance().downloadFacilityRawResource(urls[i]);

        }

        return files;
    }

    public void loadResFile() {
        String resListFileName = "res_list";
        resListFile = new File(mDir, resListFileName);
        BufferedReader br = null;
        if (!mDir.exists()) {
            mDir.mkdirs();
        }

        if (resListFile.exists()) {
            try {
                br = new BufferedReader(new FileReader(resListFile));
                String line = null;
                while ((line = br.readLine()) != null) {
                    inLine.add(line);
                    String[] vals = line.split("\t");
                    mFileNames.put(vals[0], vals[1]);
                    localmd5Table.put(vals[0], vals[2]);

                }

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (br != null) {
                    try {

                        br.close();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
        } else {
            try {
                resListFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    protected void onPostExecute(String[] result) {

        saveResFile();
        mFinished = true;
        if (mListener != null) {
            mListener.downloadFinished();
        }
    }

    public void saveResFile() {
        if (mDir != null) { //XXX APK
            File resListFile = new File(mDir, "res_list");
            BufferedWriter bw = null;
            try {
                bw = new BufferedWriter(new FileWriter(resListFile, false));

                for (Iterator<String> iterator = mFileNames.keySet().iterator(); iterator
                        .hasNext(); ) {
                    String url = (String) iterator.next();
                    String file = mFileNames.get(url);
                    bw.write(url + "\t" + file + "\t" + getLocalMd5(url) + "\n");
                }
                bw.flush();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } finally {
                if (bw != null) {
                    try {
                        bw.close();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
        }

    }

//	private boolean isChanged(String nameOfFileOnServer,String nameOfLocalFile) {
//
//		String md5OfServerCopy = md5LookUpTable.get(nameOfFileOnServer);
//		
////		if(nameOfFileOnServer.contains("/icons/"))
////		{
////			return true;
////		}
//		
//		if (md5OfServerCopy == null)
//			return false;
//		
//		//XXX add absolute path dynamically
//		String absolutePath=PropertyHolder.getInstance().getRelativeResDataPath();
//		File checkMd5OfLocalCopy = new File(absolutePath+nameOfLocalFile);
//		
//		if (!checkMd5OfLocalCopy.exists())
//			return true;
//		
//		// get md5 of checkMd5OfLocalCopy
//		String localMd5=getMD5ofFile(absolutePath+nameOfLocalFile);
//		
//		if(md5OfServerCopy.equals(localMd5))
//			return false;
//		
//		return true;
//
//	}

    protected boolean isChanged(String nameOfFileOnServer) {
        boolean result = true;
        if (localmd5Table.containsKey(nameOfFileOnServer)) {
            String loacalmd5 = localmd5Table.get(nameOfFileOnServer);
            String remotemd5 = md5LookUpTable.get(nameOfFileOnServer);
            if (loacalmd5.equals(remotemd5)) {
                result = false;
            }
        }
        return result;
    }

    private String getMD5ofFile(String fileName) {
        String checksum = null;
        try {
            File f = new File(fileName);
            String toMD5 = String.valueOf(f.length());// + "" + f.lastModified();
            byte[] bytes = toMD5.getBytes();
            checksum = checksum(bytes);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return checksum;

    }

    public boolean downloadToLocalStorage(String url) {

        if (url != null && url.contains("navserver?req_type=1&facility_id=")) {
            getResConfigFile(url);
            return true;
        }

        if (url != null &&
                mFileNames.containsKey(url) &&
                !isChanged(url)) {
            return false;

        } else {
            byte[] bytes = null;
            if (PropertyHolder.useZip) {
                ResPathConverter pathConverter = new ResPathConverter();
                bytes = pathConverter.getData(url);
            } else {
                bytes = ServerConnection.getInstance().getResourceBytes(url);

                if (bytes != null && bytes.length > 0 && PropertyHolder.getInstance().getFacilityDir() != null) {
                    String fname = checksum(url.getBytes()) + ".bin";
                    File dir = new File(getdirName());
                    if (!dir.exists()) {
                        dir.mkdirs();
                    }

                    File file = new File(dir, fname);
                    if (writeLocalCopy(file, bytes)) {

                        mFileNames.put(url, file.getName());
                        String value = md5LookUpTable.get(url);
                        if (value == null) {
                            value = "null";
                        }
                        localmd5Table.put(url, value);
                    }
                }
            }

            return true;
        }

    }

    public boolean writeLocalCopy(String url, byte[] bytes) {
        if (bytes != null && bytes.length > 0) {
            String fname = checksum(url.getBytes()) + ".bin";
            File dir = new File(getdirName());
            if (!dir.exists()) {
                dir.mkdirs();
            }

            File file = new File(dir, fname);
            if (writeLocalCopy(file, bytes)) {

                //mFileNames.put(url, file.getAbsolutePath());
                // XXX DYNAMIC  RELATIVE PATH
                mFileNames.put(url, file.getName());

                return true;
            } else {
                return false;
            }
        }
        return false;
    }

    public boolean writeLocalCopy(File file, byte[] bytes) {
        // TODO Auto-generated method stub
        FileOutputStream output = null;
        try {
            output = new FileOutputStream(file);
            output.write(bytes);
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return false;
        } finally {
            if (output != null) {
                try {
                    output.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
        return true;

    }

    public Bitmap getLocalBitmap(String url, boolean cashe) {
        if (cashe) {
            if (cashedUrl != null && url.equals(cashedUrl)) {
                return cashedBmp;
            } else {
                cashedBmp = getLocalBitmap(url);
                cashedUrl = url;
                return cashedBmp;
            }
        } else {
            return getLocalBitmap(url);
        }
    }

    public Bitmap getLocalBitmap(String url) {
        //Log.i("getLocalBitmap",url);

        byte[] bytes = getLocalCopy(url);
        Bitmap bmp = null;
        if (bytes != null) {
            Options opts = new Options();
            opts.inPreferredConfig = Bitmap.Config.ARGB_4444; // or RGB_565
            // (without
            // transparency).
            opts.inDither = false;                     //Disable Dithering mode
            opts.inPurgeable = true;                   //Tell to gc that whether it needs free memory, the Bitmap can be cleared
            opts.inInputShareable = true;              //Which kind of reference will be used to recover the Bitmap data after being clear, when it will be used in the future
            opts.inTempStorage = new byte[64 * 1024];

            bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length, opts);
        }
        return bmp;
    }

    public String checksum(byte[] bytes) {
        MessageDigest digester;
        String checksum = null;

        try {
            digester = MessageDigest.getInstance("MD5");
            byte[] digest = digester.digest(bytes);
            checksum = bytesToHex(digest);

        } catch (NoSuchAlgorithmException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return checksum;
    }

    public HashMap<String, String> getLocalmd5Table() {
        return localmd5Table;
    }

    public void setLocalmd5Table(HashMap<String, String> localmd5Table) {
        this.localmd5Table = localmd5Table;
    }

    public String getLocalMd5(String key) {
        return localmd5Table.get(key);
    }


}
