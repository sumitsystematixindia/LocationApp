package com.mlins.ui.utils;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.mlins.utils.CampusLevelResDownloader;
import com.mlins.utils.Cleanable;
import com.mlins.utils.Lookup;
import com.mlins.utils.PropertyHolder;
import com.mlins.utils.ServerConnection;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProjectIconsManager implements Cleanable {


    private static final String TAG = ProjectIconsManager.class.getName();
    private final static String JSON_FILE_NAME = "spreo_project_icons.json";
    private final static String TILES_DIR_NAME = "project_icons";
    private final static String MAP_RES_FILE_NAME = "res_list.data";
    private final static String MD5_RES_FILE_NAME = "md5_list.data";

    private Map<String, String> imgToNameMap = new HashMap<String, String>();

    private HashMap<Integer, String> md5Table = new HashMap<Integer, String>();

    private File currentProjectDir = PropertyHolder.getInstance().getProjectDir();

    private File imgsDir = new File(currentProjectDir, TILES_DIR_NAME);

    private ProjectIconsManager() {
        loadMd5ResListFile();
        loadResFile();
    }

    public static ProjectIconsManager getInstance() {
        return Lookup.getInstance().get(ProjectIconsManager.class);
    }

    public static void releaseInstance() {
        Lookup.getInstance().remove(ProjectIconsManager.class);
    }

    public void clean() {
        saveMd5ResListFile();
        currentProjectDir = null;
        imgToNameMap.clear();
    }


    public Bitmap getLocalBitmapCopy(String uri) {


        Bitmap bitmap = null;

        try {


            String index = imgToNameMap.get(uri);

            if (index != null) {

                if (PropertyHolder.useZip) {
                    String iconUrl = ServerConnection.getProjectResourcesUrl() + "icons/" + uri + ".png";
                    byte[] bytes = CampusLevelResDownloader.getCInstance().getUrl(iconUrl);
                    if (bytes != null && bytes.length > 0) {
                        BitmapFactory.Options options1 = new BitmapFactory.Options();
                        options1.inPreferredConfig = Bitmap.Config.ARGB_8888;
                        bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options1);
                    }
                } else {
                    String f = imgsDir.toString() + "/" + index;

                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                    bitmap = BitmapFactory.decodeFile(f, options);

                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }


    private void loadMd5ResListFile() {


        File projectDir = PropertyHolder.getInstance().getProjectDir();

        if (projectDir == null) {
            return;
        }


        File resListFile = new File(imgsDir, MD5_RES_FILE_NAME);
        BufferedReader br = null;

        if (!projectDir.exists()) {
            projectDir.mkdirs();
        }

        if (resListFile.exists()) {
            try {
                br = new BufferedReader(new FileReader(resListFile));
                String line = null;
                while ((line = br.readLine()) != null) {

                    String[] vals = line.split("\t");
                    md5Table.put(Integer.valueOf(vals[0]), vals[1]);
                }

            } catch (IOException e) {
                //e.printStackTrace();
            } finally {
                if (br != null) {
                    try {
                        br.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        } else {
            try {
                resListFile.createNewFile();
            } catch (IOException e) {
                //e.printStackTrace();
            }
        }

    }

    public void saveMd5ResListFile() {

        File projectDir = PropertyHolder.getInstance().getProjectDir();

        if (projectDir != null) {


            File resListFile = new File(imgsDir, MD5_RES_FILE_NAME);
            BufferedWriter bw = null;
            try {
                bw = new BufferedWriter(new FileWriter(resListFile, false));

                for (Integer index : md5Table.keySet()) {

                    String md5 = md5Table.get(index);
                    bw.write(index + "\t" + md5 + "\n");
                }
                bw.flush();
            } catch (IOException e) {

                e.printStackTrace();
            } finally {
                if (bw != null) {
                    try {
                        bw.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

    }


    public void loadResFile() {

        // reset
        imgToNameMap = new HashMap<String, String>();

        String resListFileName = MAP_RES_FILE_NAME;
        File resListFile = new File(imgsDir, resListFileName);
        BufferedReader br = null;
        if (!imgsDir.exists()) {
            imgsDir.mkdirs();
        }

        if (resListFile.exists()) {
            try {
                br = new BufferedReader(new FileReader(resListFile));
                String line = null;
                while ((line = br.readLine()) != null) {

                    String[] vals = line.split("\t");
                    imgToNameMap.put(vals[0], vals[1]);

                }

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (br != null) {
                    try {

                        br.close();
                    } catch (IOException e) {
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

    public void saveResFile() {

        if (imgsDir != null) {

            File resListFile = new File(imgsDir, MAP_RES_FILE_NAME);
            BufferedWriter bw = null;
            try {
                bw = new BufferedWriter(new FileWriter(resListFile, false));

                for (String url : imgToNameMap.keySet()) {
                    String file = imgToNameMap.get(url);
                    bw.write(url + "\t" + file + "\n");
                }
                bw.flush();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (bw != null) {
                    try {
                        bw.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

    }


    public boolean downloadIconsRes() {

        boolean res = false;
        String server = PropertyHolder.getInstance().getServerName();
        String projectid = PropertyHolder.getInstance().getProjectId();
        String url = server + "res/" + projectid + "/" + JSON_FILE_NAME;

        byte[] data = null;
        if (PropertyHolder.useZip) {
            data = CampusLevelResDownloader.getCInstance().getUrl(url);
        } else {
            data = ServerConnection.getInstance().getResourceBytes(url);
        }

        if (data != null && data.length > 0) {
            String dataSantityCheck = new String(data);
            List<IconRes> icons = IconRes.parseJson(dataSantityCheck);

            if (isMds5Changed(IconRes.MD5)) {
                downloadImgs(icons);
                saveResFile();
                saveMd5ResListFile();
            }
        }

        return res;
    }


    private boolean isMds5Changed(String md5) {

        if (md5 != null) {
            String localMd5 = md5Table.get(-1);
            if (!md5.equals(localMd5)) {
                return true;
            }
        }

        return false;

    }


    private void prepairImgsIndexes(List<IconRes> icons) {

        if (icons != null && icons.size() == 0) {
            return;
        }

        int count = 0;
        for (IconRes icon : icons) {

            if (icon == null) {
                continue;
            }

            String idx = String.valueOf(icon.getAutoIndex());
            imgToNameMap.put(icon.getName(), idx);
            count++;

        }

        if (count == icons.size()) // all imgs were downloaded
        {
            md5Table.put(-1, IconRes.MD5); // currently only one md5 exists
        }

    }

    private void downloadImgs(List<IconRes> icons) {

        if (icons != null && icons.size() == 0) {
            return;
        }

        if (PropertyHolder.useZip) {
            prepairImgsIndexes(icons);
            return;
        }

        File root = new File(currentProjectDir, TILES_DIR_NAME);
        int count = 0;
        for (IconRes icon : icons) {

            if (icon == null) {
                continue;
            }

            byte[] imgBlob = icon.downloadImg();

            if (imgBlob != null && imgBlob.length > 0) {

                String idx = String.valueOf(icon.getAutoIndex());

                File file = new File(root, idx);
                if (writeLocalCopy(file, imgBlob)) {
                    imgToNameMap.put(icon.getName(), idx);
                    count++;
                }
            }

        }

        if (count == icons.size()) // all imgs were downloaded
        {
            md5Table.put(-1, IconRes.MD5); // currently only one md5 exists
        }
    }


    public boolean writeLocalCopy(File file, byte[] bytes) {
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

}
