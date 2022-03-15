package com.mlins.overlay;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.mlins.project.Campus;
import com.mlins.project.ProjectConf;
import com.mlins.utils.CampusLevelResDownloader;
import com.mlins.utils.Cleanable;
import com.mlins.utils.Lookup;
import com.mlins.utils.PropertyHolder;
import com.mlins.utils.ResPathConverter;
import com.mlins.utils.ServerConnection;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CampusMapTilesManager implements Cleanable {

    private static final String TAG = CampusMapTilesManager.class.getName();
    private final static String TILES_DIR_NAME = "map_tiles";
    private final static String MAP_RES_FILE_NAME = "res_list.data";
    private final static String MD5_RES_FILE_NAME = "md5_list.data";
    private final static float TILE_SIZE = 512.0f;
    private static final String TILE_FILE_TYPE = ".bin";
    private static final int OVERLAPPING_IMG_SPLITTER_PX = 4;

    private Map<String, String> currentOverlayTilesFileNames = new HashMap<String, String>();
    private HashMap<Integer, String> overlaysMd5Table = new HashMap<Integer, String>();
    private int currentOverlayId = -100;
    private String currentCampusId = null;
    private File currentCampusDir = null;
    private File currentOverlayTilesResDir = null;
    private List<OverlayChunk> currentChunkedImages = new ArrayList<OverlayChunk>();

    private CampusMapTilesManager() {
        loadMd5ResListFile();
    }

    public static CampusMapTilesManager getInstance() {
        return Lookup.getInstance().get(CampusMapTilesManager.class);
    }

    public static void releaseInstance() {
        Lookup.getInstance().remove(CampusMapTilesManager.class);
    }

    public void clean() {
        saveMd5ResListFile();
        currentOverlayId = -100;
        currentCampusId = null;
        currentCampusDir = null;
        currentOverlayTilesResDir = null;
        currentChunkedImages.clear();
        currentOverlayTilesFileNames.clear();
    }

    public List<OverlayChunk> getMapTiles(String campusId, int overlayId) {

        List<OverlayChunk> list = new ArrayList<OverlayChunk>();

        if (campusId != null
                && (campusId.equals(currentCampusId) || currentCampusId == null)) {

            if (overlayId != currentOverlayId) {
                currentCampusDir = PropertyHolder.getInstance().getCampusDir();
                currentOverlayTilesResDir = new File(currentCampusDir,
                        TILES_DIR_NAME + "/" + overlayId);

                loadOverlayResFile();
                boolean dirty = false;

                for (String tileFileName : currentOverlayTilesFileNames.keySet()) {
                    File f = new File(currentOverlayTilesResDir, tileFileName + TILE_FILE_TYPE);
                    if (!f.exists()) {
                        dirty = true;
                        break;
                    }
                }
                //some or all of the files are missing create them
                if (dirty) {
                    createMapTiles(true);

                    currentCampusDir = PropertyHolder.getInstance().getCampusDir();
                    currentOverlayTilesResDir = new File(currentCampusDir,
                            TILES_DIR_NAME + "/" + overlayId);

                    loadOverlayResFile();
                }

                currentChunkedImages.clear();

                for (String tileFileName : currentOverlayTilesFileNames
                        .keySet()) {
                    BitmapDescriptor bd = getLocalBitmapDescriptorrCopy(tileFileName);
                    if (bd != null) {
                        String boundsSettings = currentOverlayTilesFileNames
                                .get(tileFileName);
                        OverlayChunk oc = new OverlayChunk(bd);
                        oc.setBoundFromStringLine(boundsSettings);
                        currentChunkedImages.add(oc);

                    }
                }

                currentCampusId = campusId;
                currentOverlayId = overlayId;
                list = currentChunkedImages;

            } else {
                list = currentChunkedImages;
            }
        }

        return list;

    }

    public void createMapTiles(boolean force) {

        clean();

        try {
            currentCampusDir = PropertyHolder.getInstance().getCampusDir();

            if (!currentCampusDir.exists()) {
                currentCampusDir.mkdirs();
            }

            Campus campus = ProjectConf.getInstance().getSelectedCampus();
            if (campus != null) {

                currentCampusId = campus.getId();

                if (PropertyHolder.useZip) {
                    force = ResPathConverter.isOverrideCampusOverlay(currentCampusId);
                }

                List<CampusOverlay> mapdata = campus.getOverlaysList();

                // iterate over overlays and create tiles
                for (CampusOverlay mapOverlay : mapdata) {
                    currentOverlayId = mapOverlay.getId();
                    currentOverlayTilesResDir = new File(currentCampusDir,
                            TILES_DIR_NAME + "/" + currentOverlayId);

                    createOverlayTiles(mapOverlay, force);
                }

                if (PropertyHolder.useZip) {
                    ResPathConverter.deleteOverrideCampusOverlay(currentCampusId);
                }
            }

        } catch (Throwable t) {
            t.printStackTrace();
        } finally {
            saveMd5ResListFile();
            clean(); // release state
        }

    }

    private void createOverlayTiles(CampusOverlay mapOverlay, boolean force) {

        try {

            currentOverlayTilesFileNames = new HashMap<String, String>();

            String uri = mapOverlay.getUri();
            String url = ServerConnection.getInstance().translateCampusResUrl(uri);

            String md5 = mapOverlay.getMd5();
            String currentFloorMd5 = overlaysMd5Table.get(currentOverlayId);


            if (currentFloorMd5 == null || !currentFloorMd5.equals(md5) || force) {

                byte[] blob = CampusLevelResDownloader.getCInstance().getUrl(url);

                Bitmap bm = BitmapFactory.decodeByteArray(blob, 0, blob.length);
                splitImage(mapOverlay, bm);

                if (bm != null) {
                    bm.recycle();
                }

                bm = null;

                overlaysMd5Table.put(currentOverlayId, md5);

                saveOverlayResFile();


            }

        } catch (Throwable t) {
            t.printStackTrace();
        }

    }

    private void splitImage(CampusOverlay mapOverlay, Bitmap bitmap) {
        try {

            if (bitmap == null) {
                return;
            }

            //For the number of rows and columns of the grid to be displayed
            int rows, cols;

            //For height and width of the small image chunks
            int chunkHeight, chunkWidth;

            //To store all the small image chunks in bitmap format in this list
            //removeTiles();
            //chunkedImages.clear();


            //Getting the scaled bitmap of the source image
            // Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, bitmap.getWidth(), bitmap.getHeight(), true);

            rows = Math.round((bitmap.getWidth() / TILE_SIZE));
            cols = Math.round(bitmap.getHeight() / TILE_SIZE); //(int) Math.sqrt(chunkNumbers);
            if (rows == 0) {
                rows = 1;
            }
            if (cols == 0) {
                cols = 1;
            }
            chunkHeight = Math.round(bitmap.getHeight() / (float) rows);
            chunkWidth = Math.round(bitmap.getWidth() / (float) cols);


            // compute overlay bounds params
            double widthInLatLon = Math.abs(mapOverlay.getRectTopRightLon() - mapOverlay.getRectTopLeftLon());
            double heightInLatLon = Math.abs(mapOverlay.getRectBottomLeftLat() - mapOverlay.getRectTopLeftLat());
            double widthInPixel = bitmap.getWidth();
            double heightInPixel = bitmap.getHeight();
            //===

            String mapTileName = "tile_";
            int idx = 1;
            //xCoord and yCoord are the pixel positions of the image chunks
            int yCoord = 0;
            for (int x = 0; x < rows; x++) {
                int xCoord = 0;
                if (x != 0) {
                    yCoord -= OVERLAPPING_IMG_SPLITTER_PX;
                }
                for (int y = 0; y < cols; y++) {


                    if (y != 0) {
                        xCoord -= OVERLAPPING_IMG_SPLITTER_PX;
                    }

                    Bitmap bm = Bitmap.createBitmap(bitmap, xCoord, yCoord, chunkWidth, chunkHeight);

                    if (!isAllBitmapTransparent(bm)) {

                        String fileTileName = mapTileName + idx;

                        if (saveBitmapToFile(fileTileName, bm)) {


                            int southWestX = xCoord + chunkWidth;
                            int southWestY = yCoord;
                            int northEastX = xCoord;
                            int northEastY = yCoord + chunkHeight;

                            double swlongitude = mapOverlay.getRectTopLeftLon() + (southWestX * widthInLatLon / widthInPixel);
                            double swlatitude = mapOverlay.getRectTopLeftLat() - (southWestY * heightInLatLon / heightInPixel);
                            LatLng sw = new LatLng(swlatitude, swlongitude);


                            double nelongitude = mapOverlay.getRectTopLeftLon() + (northEastX * widthInLatLon / widthInPixel);
                            double nelatitude = mapOverlay.getRectTopLeftLat() - (northEastY * heightInLatLon / heightInPixel);
                            LatLng ne = new LatLng(nelatitude, nelongitude);

                            OverlayChunk oc = new OverlayChunk(sw, ne);

                            currentOverlayTilesFileNames.put(fileTileName, oc.getAsStringLine());
                            idx++;
                        }
                    }

                    bm.recycle();
                    bm = null;

                    xCoord += chunkWidth;
                }
                yCoord += chunkHeight;
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }

    }

    private BitmapDescriptor getLocalBitmapDescriptorrCopy(String uri) {

        BitmapDescriptor bd = null;
        try {

            String f = currentOverlayTilesResDir.toString() + "/" + uri
                    + TILE_FILE_TYPE;

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            Bitmap bitmap = BitmapFactory.decodeFile(f, options);

            bd = BitmapDescriptorFactory.fromBitmap(bitmap);

            bitmap.recycle();
            bitmap = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bd;
    }

    private boolean saveBitmapToFile(String filename, Bitmap bitmap) {

        OutputStream outStream = null;
        boolean answer = false;

        currentOverlayTilesResDir.mkdirs();

        File file = new File(currentOverlayTilesResDir, filename + TILE_FILE_TYPE);
        // if (file.exists()) {
        // file.delete();
        // }

        try {

            outStream = new FileOutputStream(file);
            answer = bitmap.compress(Bitmap.CompressFormat.PNG, 100, outStream);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (outStream != null) {
                try {
                    outStream.flush();
                    outStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }
        return answer;

    }

    private boolean isAllBitmapTransparent(Bitmap bitmap) {

        Bitmap bmp = Bitmap.createScaledBitmap(bitmap, 50, 50, true);

        int[] pixels = new int[bmp.getWidth() * bmp.getHeight()];
        bmp.getPixels(pixels, 0, bmp.getWidth(), 0, 0, bmp.getWidth(),
                bmp.getHeight());

        bmp.recycle();
        bmp = null;

        int count = 0;

        for (int i : pixels) {
            if (Color.alpha(i) == 0) {
                count++;
            }
        }

        if (count == pixels.length) {
            return true;
        }
        return false;
    }

    private void loadMd5ResListFile() {

        File campusDir = PropertyHolder.getInstance().getCampusDir();
        String resListFileName = MD5_RES_FILE_NAME;
        File resListFile = new File(campusDir, TILES_DIR_NAME + "/"
                + resListFileName);
        BufferedReader br = null;
        if (!campusDir.exists()) {
            campusDir.mkdirs();
        }

        if (resListFile.exists()) {
            try {
                br = new BufferedReader(new FileReader(resListFile));
                String line = null;
                while ((line = br.readLine()) != null) {

                    String[] vals = line.split("\t");
                    overlaysMd5Table.put(Integer.valueOf(vals[0]), vals[1]);
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
                //e.printStackTrace();
            }
        }

    }

    public void saveMd5ResListFile() {

        File campusDir = PropertyHolder.getInstance().getCampusDir();

        if (campusDir != null) {

            String resListFileName = MD5_RES_FILE_NAME;
            File resListFile = new File(campusDir, TILES_DIR_NAME + "/"
                    + resListFileName);
            BufferedWriter bw = null;
            try {
                bw = new BufferedWriter(new FileWriter(resListFile, false));

                for (Integer floor : overlaysMd5Table.keySet()) {

                    String md5 = overlaysMd5Table.get(floor);
                    bw.write(floor + "\t" + md5 + "\n");
                }
                bw.flush();
            } catch (IOException e) {

                //e.printStackTrace();
            } finally {
                if (bw != null) {
                    try {
                        bw.close();
                    } catch (IOException e) {
                        //e.printStackTrace();
                    }
                }
            }
        }

    }

    public void loadOverlayResFile() {

        // reset
        currentOverlayTilesFileNames = new HashMap<String, String>();

        String resListFileName = MAP_RES_FILE_NAME;
        File resListFile = new File(currentOverlayTilesResDir, resListFileName);
        BufferedReader br = null;
        if (!currentOverlayTilesResDir.exists()) {
            currentOverlayTilesResDir.mkdirs();
        }

        if (resListFile.exists()) {
            try {
                br = new BufferedReader(new FileReader(resListFile));
                String line = null;
                while ((line = br.readLine()) != null) {

                    String[] vals = line.split("\t");
                    currentOverlayTilesFileNames.put(vals[0], vals[1]);

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

    public void saveOverlayResFile() {

        if (currentOverlayTilesResDir != null) {

            File resListFile = new File(currentOverlayTilesResDir,
                    MAP_RES_FILE_NAME);
            BufferedWriter bw = null;
            try {
                bw = new BufferedWriter(new FileWriter(resListFile, false));

                for (String url : currentOverlayTilesFileNames.keySet()) {
                    String file = currentOverlayTilesFileNames.get(url);
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

}
