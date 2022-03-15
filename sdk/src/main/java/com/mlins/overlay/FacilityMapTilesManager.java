package com.mlins.overlay;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.mlins.utils.Cleanable;
import com.mlins.utils.FacilityConf;
import com.mlins.utils.FacilityContainer;
import com.mlins.utils.FloorData;
import com.mlins.utils.Lookup;
import com.mlins.utils.PropertyHolder;
import com.mlins.utils.ResPathConverter;
import com.mlins.utils.ResourceDownloader;
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

public class FacilityMapTilesManager implements Cleanable {

    private static final String TAG = FacilityMapTilesManager.class.getName();
    private final static String TILES_DIR_NAME = "map_tiles";
    private final static String MAP_RES_FILE_NAME = "res_list.data";
    private final static String MD5_RES_FILE_NAME = "md5_list.data";
    private final static float TILE_SIZE = 512.0f;
    private static final String TILE_FILE_TYPE = ".bin";
    private static final int OVERLAPPING_IMG_SPLITTER_PX = 4;

    private Map<String, String> currentFloorTilesFileNames = new HashMap<String, String>();


    private HashMap<Integer, String> floorsMd5Table = new HashMap<Integer, String>();

    private int currentFloor = -100;
    private String currentFacilityId = null;
    private File currentFacDir = null;
    private File currentFloorTilesResDir = null;
    private List<OverlayChunk> currentChunkedImages = new ArrayList<OverlayChunk>();

    private FacilityMapTilesManager() {
        loadMd5ResListFile();
    }

    public static FacilityMapTilesManager getInstance() {
        return Lookup.getInstance().get(FacilityMapTilesManager.class);
    }

    public static void releaseInstance() {
        Lookup.getInstance().remove(FacilityMapTilesManager.class);
    }

    public void clean() {
        currentFloor = -100;
        currentFacilityId = null;
        currentFacDir = null;
        currentFloorTilesResDir = null;
        currentChunkedImages.clear();
        currentFloorTilesFileNames.clear();
    }


    public List<OverlayChunk> getFloorMapTiles(String facilityId, int floor) {

        List<OverlayChunk> list = new ArrayList<OverlayChunk>();

        if (facilityId != null) {

            if (floor != currentFloor || currentFacilityId == null || !facilityId.equals(currentFacilityId)) {
                File campusdir = PropertyHolder.getInstance().getCampusDir();
                currentFacDir = new File(campusdir, facilityId);
                currentFloorTilesResDir = new File(currentFacDir, TILES_DIR_NAME + "/" + floor);

                loadFloorResFile();

                currentChunkedImages.clear();
                boolean dirty = false;

                for (String tileFileName : currentFloorTilesFileNames.keySet()) {
                    File f = new File(currentFloorTilesResDir, tileFileName + TILE_FILE_TYPE);
                    if (!f.exists()) {
                        dirty = true;
                        break;
                    }
                }
                //some or all of the files are missing create them
                if (dirty) {
                    createFacilityFloorsMapTiles(true);

                    currentFacDir = new File(campusdir, facilityId);
                    currentFloorTilesResDir = new File(currentFacDir, TILES_DIR_NAME + "/" + floor);
                    loadFloorResFile();
                }

                for (String tileFileName : currentFloorTilesFileNames.keySet()) {
                    BitmapDescriptor bd = getLocalBitmapDescriptorrCopy(tileFileName);
                    if (bd != null) {
                        String boundsSettings = currentFloorTilesFileNames.get(tileFileName);
                        OverlayChunk oc = new OverlayChunk(bd);
                        oc.setBoundFromStringLine(boundsSettings);
                        currentChunkedImages.add(oc);

                    }
                }


                currentFacilityId = facilityId;
                currentFloor = floor;
                list = currentChunkedImages;

            } else {
                list = currentChunkedImages;
            }
        }

        return list;

    }

    public void createFacilityFloorsMapTiles(boolean force) {

        clean();

        try {
            currentFacDir = PropertyHolder.getInstance().getFacilityDir();

            if (!currentFacDir.exists()) {
                currentFacDir.mkdirs();
            }


            FacilityConf facility = FacilityContainer.getInstance().getSelected();

            currentFacilityId = facility.getId();

            List<FloorData> mapdata = facility.getFloorDataList();


            // iterate over floors and create tiles
            for (int floor = 0; floor < mapdata.size(); floor++) {
                currentFloor = floor;
                currentFloorTilesResDir = new File(currentFacDir, TILES_DIR_NAME + "/" + floor);

                FloorData floorData = mapdata.get(floor);
                createFloorTiles(floorData, force);
            }


        } catch (Throwable t) {
            t.printStackTrace();
        } finally {
            saveMd5ResListFile();
            clean(); // release state
        }

    }

    private void createFloorTiles(FloorData floorData, boolean force) {


        try {

            currentFloorTilesFileNames = new HashMap<String, String>();

            String uri = floorData.mapuri;
            String url = ServerConnection.getInstance().translateUrl(uri, currentFacilityId);

            String md5 = ResourceDownloader.getInstance().getMd5(url);
            String currentFloorMd5 = floorsMd5Table.get(currentFloor);

            if (PropertyHolder.useZip) {
                force = ResPathConverter.isOverrideFloorOverlay(
                        PropertyHolder.getInstance().getCampusId(),
                        currentFacilityId, currentFloor);
            }

            if (currentFloorMd5 == null || !currentFloorMd5.equals(md5) || force) {

                byte[] blob = ResourceDownloader.getInstance().getUrl(url);


                Bitmap bm = BitmapFactory.decodeByteArray(blob, 0, blob.length);
                splitImage(bm);

                if (bm != null) {
                    bm.recycle();
                }

                bm = null;


                saveFloorResFile();
                floorsMd5Table.put(currentFloor, md5);

                if (PropertyHolder.useZip) {
                    // delete override file
                    ResPathConverter.deleteOverrideFloorOverlay(PropertyHolder.getInstance().getCampusId(),
                            currentFacilityId, currentFloor);
                }
            }

        } catch (Throwable t) {
            t.printStackTrace();
        }


    }


    private void splitImage(Bitmap bitmap) {
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


                            OverlayChunk oc = new OverlayChunk(null, southWestX, southWestY, northEastX, northEastY);
                            oc.setBound(currentFacilityId);

                            //chunkedImages.add(oc);

                            currentFloorTilesFileNames.put(fileTileName, oc.getAsStringLine());
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

            String f = currentFloorTilesResDir.toString() + "/" + uri + TILE_FILE_TYPE;

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

        currentFloorTilesResDir.mkdirs();

        File file = new File(currentFloorTilesResDir, filename + TILE_FILE_TYPE);
//	      if (file.exists()) {
//	         file.delete();
//	      }

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
        bmp.getPixels(pixels, 0, bmp.getWidth(), 0, 0, bmp.getWidth(), bmp.getHeight());

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

        File facDir = PropertyHolder.getInstance().getFacilityDir();
        String resListFileName = MD5_RES_FILE_NAME;
        File resListFile = new File(facDir, TILES_DIR_NAME + "/" + resListFileName);
        BufferedReader br = null;
        if (!facDir.exists()) {
            facDir.mkdirs();
        }

        if (resListFile.exists()) {
            try {
                br = new BufferedReader(new FileReader(resListFile));
                String line = null;
                while ((line = br.readLine()) != null) {

                    String[] vals = line.split("\t");
                    floorsMd5Table.put(Integer.valueOf(vals[0]), vals[1]);
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

        File facDir = PropertyHolder.getInstance().getFacilityDir();

        if (facDir != null) {


            String resListFileName = MD5_RES_FILE_NAME;
            File resListFile = new File(facDir, TILES_DIR_NAME + "/" + resListFileName);
            BufferedWriter bw = null;
            try {
                bw = new BufferedWriter(new FileWriter(resListFile, false));

                for (Integer floor : floorsMd5Table.keySet()) {

                    String md5 = floorsMd5Table.get(floor);
                    bw.write(floor + "\t" + md5 + "\n");
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


    public void loadFloorResFile() {

        // reset
        currentFloorTilesFileNames = new HashMap<String, String>();

        String resListFileName = MAP_RES_FILE_NAME;
        File resListFile = new File(currentFloorTilesResDir, resListFileName);
        BufferedReader br = null;
        if (!currentFloorTilesResDir.exists()) {
            currentFloorTilesResDir.mkdirs();
        }

        if (resListFile.exists()) {
            try {
                br = new BufferedReader(new FileReader(resListFile));
                String line = null;
                while ((line = br.readLine()) != null) {

                    String[] vals = line.split("\t");
                    currentFloorTilesFileNames.put(vals[0], vals[1]);


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


    public void saveFloorResFile() {

        if (currentFloorTilesResDir != null) {
            if (!currentFloorTilesResDir.exists()) {
                currentFloorTilesResDir.mkdirs();
            }
            File resListFile = new File(currentFloorTilesResDir, MAP_RES_FILE_NAME);
            BufferedWriter bw = null;
            try {
                bw = new BufferedWriter(new FileWriter(resListFile, false));

                for (String url : currentFloorTilesFileNames.keySet()) {
                    String file = currentFloorTilesFileNames.get(url);
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
