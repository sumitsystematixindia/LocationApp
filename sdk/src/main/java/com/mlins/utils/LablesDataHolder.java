package com.mlins.utils;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PointF;

import com.mlins.maping.LableSprite;
import com.mlins.maping.LayerObject;
import com.mlins.utils.gis.Location;
import com.mlins.utils.logging.Log;
import com.mlins.views.TouchImageView;
import com.spreo.spreosdk.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class LablesDataHolder implements Cleanable {

    private static final String TAG = LablesDataHolder.class.getName();

    private static String imagesSuffixes[] = {".png", "jpg", ".gif", ".tiff", "jpeg"};
    Boolean LabelsChanged = false;
    LableObject CurrentLabel;
    File dir = null;
    File mFile = null;
    String resFile = "";
    String fileFullName = "";
    String lang = PropertyHolder.getInstance().getUserLaguagePreference();
    private List<LableObject> allLables;
    private boolean isloaded = false;
    private LableObject movinLabel = null;


    private LablesDataHolder() {
        allLables = new ArrayList<LableObject>();

    }

    public static LablesDataHolder getInstance() {
        LablesDataHolder instance = null;
        try {
            Log.getInstance().debug(TAG,
                    "Enter, GeneralTextsSpritesHolder getInstance()");

            instance = Lookup.getInstance().get(LablesDataHolder.class);

            Log.getInstance()
                    .debug(TAG, "Exit, GeneralTextsSpritesHolder getInstance()");
        } catch (Throwable t) {
            Log.getInstance().error(TAG, t.getMessage(), t);
        }
        if (!instance.isloaded()) {
            instance.LoadLables();
        }
        return instance;
    }

    public static void releaseInstance() {
        Lookup.getInstance().remove(LablesDataHolder.class);
    }

    public boolean isloaded() {
        return isloaded;
    }

    public void setIsloaded(boolean isloaded) {
        this.isloaded = isloaded;
    }

    public void clean() {
        allLables.clear();
        isloaded = false;
    }

    public List<LableObject> getAllLables() {
        return allLables;
    }

    public void setAllLables(
            ArrayList<LableObject> allLables) {
        this.allLables = allLables;
    }


    public void LoadLables() {
        File dir = new File(PropertyHolder.getInstance().getFacilityDir(), "labels");

        String filename = "";
        try {
            if (lang.contains("ar")) {
                filename = "lables_json_ar.txt";
            } else if (lang.contains("En")) {
                filename = "lables_json_en.txt";
            } else if (lang.contains("he")) {
                filename = "lables_json_he.txt";
            } else if (lang.contains("ru")) {
                filename = "lables_json_ru.txt";
            } else {
                filename = "lables_json.txt";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        File file = new File(dir, filename);

        if (file.exists()) {
            BufferedReader in = null;
            try {
                in = new BufferedReader(new FileReader(file));
                String line = null;
                StringBuffer sb = new StringBuffer();
                while ((line = in.readLine()) != null) {
                    sb.append(line);
                }
                parseJSON(sb.toString());
            } catch (IOException e) {
                e.printStackTrace();

            } finally {
                if (in != null)
                    try {
                        in.close();
                    } catch (Exception e2) {
                        Log.getInstance().error("", e2.getMessage());
                        e2.printStackTrace();
                    }
            }

        }
        isloaded = true;
    }


    public void addLablesSprites(TouchImageView tiv, int floor) {
        List<LableObject> sortedByFloor = new ArrayList<LableObject>();
        for (LableObject lable : allLables) {
            if ((int) lable.getLocation().getZ() == floor) {
                sortedByFloor.add(lable);
            }
        }


        Resources res = PropertyHolder.getInstance().getMlinsContext().getResources();
        Bitmap bmp = BitmapFactory.decodeResource(res, R.drawable.poibubble);
        LayerObject layer = tiv.getLayerByName("lables");
        layer.clearSprites();
        for (LableObject lable : sortedByFloor) {
            LableSprite sprite = new LableSprite(lable, bmp, tiv);
            PointF xy = new PointF((float) sprite.getLocation().getX(), (float) sprite.getLocation().getY());
            sprite.setLoc(xy);
            layer.addSprite(sprite);
        }
        layer.show();
    }

    public void parseJSON(String jasonString) {

        if (jasonString != null) {
            try {
                clean();
                JSONTokener tokener = new JSONTokener(jasonString);
                JSONObject json;

                json = (JSONObject) tokener.nextValue();
                JSONArray lables = json.getJSONArray("lables");

                for (int i = 0; i < lables.length(); i++) {
                    JSONObject object = lables.getJSONObject(i);

                    LableObject lable = new LableObject();

                    String lable_txt = object.getString("lable_txt");
                    lable.setTxt(lable_txt);

                    double lable_x_position = object.getDouble("x_position");
                    double lable_y_position = object.getDouble("y_position");
                    double lable_floor_position = object.getDouble("z_position");

                    {
                        Location tempLocation = new Location();
                        tempLocation.setX(lable_x_position);
                        tempLocation.setY(lable_y_position);
                        tempLocation.setZ(lable_floor_position);

                        lable.setLocation(tempLocation);
                    }

                    allLables.add(lable);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

    }


    public void savelabels() throws JSONException, IOException {
        dir = new File(PropertyHolder.getInstance().getFacilityDir(), "labels");
        String filename = "";
        try {
            if (lang.contains("ar")) {
                filename = "lables_json_ar.txt";
            } else if (lang.contains("En")) {
                filename = "lables_json_en.txt";
            } else if (lang.contains("he")) {
                filename = "lables_json_he.txt";
            } else if (lang.contains("ru")) {
                filename = "lables_json_ru.txt";
            } else {
                filename = "lables_json.txt";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        fileFullName = filename + resFile;
        mFile = new File(dir, fileFullName);
//		if (LabelsChanged == true) {			
        if (!dir.exists()) {
            dir.mkdirs();
        }
        if (mFile.exists()) {
            mFile.delete();
        }
        if (!mFile.exists()) {
            try {
                mFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
//			}
            try {
                JSONObject obj = new JSONObject();
                JSONArray labels = new JSONArray();
                for (Iterator iterator = allLables.iterator(); iterator
                        .hasNext(); ) {
                    LableObject type = (LableObject) iterator.next();
                    JSONObject labeljson = type.getAsJson();
                    labels.put(labeljson);
                }

                obj.put("lables", labels);

                try {

                    FileWriter file = new FileWriter(mFile);
                    String jsonstr = obj.toString(2);
                    file.write(jsonstr);
                    file.flush();
                    file.close();

                } catch (IOException e) {
                    e.printStackTrace();
                }

            } finally {

            }
        }

    }


    public LableObject getMovinLabel() {
        return movinLabel;
    }

    public void setMovinLabel(LableObject movinLabel) {
        LabelsChanged = true;
        this.movinLabel = movinLabel;
    }

    public boolean removeLabel(LableObject object) {
        LabelsChanged = true;
        return allLables.remove(object);
    }

    public LableObject getCurrentLabel() {
        return CurrentLabel;
    }

    public void setCurrentLabel(LableObject currentLabel) {
        CurrentLabel = currentLabel;
    }

}
