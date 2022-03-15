package com.mlins.label;

import android.annotation.SuppressLint;

import com.mlins.maping.LayerObject;
import com.mlins.utils.Cleanable;
import com.mlins.utils.FacilityConf;
import com.mlins.utils.FacilityContainer;
import com.mlins.utils.Lookup;
import com.mlins.utils.ResourceDownloader;
import com.mlins.views.TouchImageView;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Deprecated
public class LabelsInfoHelper implements Cleanable {

    @SuppressLint("UseSparseArrays")
    private Map<Integer, List<LabelData>> labelsToFloorMap = new HashMap<Integer, List<LabelData>>();

    public LabelsInfoHelper() {
        super();
    }

    public static LabelsInfoHelper getInstance() {
        return Lookup.getInstance().get(LabelsInfoHelper.class);
    }

    public static void releaseInstance() {
        Lookup.getInstance().remove(LabelsInfoHelper.class);
    }

    public boolean load() {

        boolean isSucceded = false;
        clean();

        FacilityConf facConf = FacilityContainer.getInstance().getSelected();

        if (facConf == null) {
            return isSucceded;
        }

        String uri = facConf.getLabelsConfFileName();
        byte data[] = ResourceDownloader.getInstance().getLocalCopy(uri);
        if (data != null && data.length > 0) {
            String content = new String(data);
            isSucceded = parseLabelsJson(content);
            if (isSucceded) {
                computeDrawables();
            }
        }
        return isSucceded;

    }


    private void computeDrawables() {


        for (List<LabelData> lblList : labelsToFloorMap.values()) {
            for (LabelData label : lblList) {
                if (label != null /*&& label.getDescription().startsWith("ABERCROMBIE & F")*/) {
                    label.computeState();
                }
            }
        }
    }

    public void clean() {
        labelsToFloorMap.clear();
    }

    private boolean parseLabelsJson(String content) {

        boolean answer = false;
        try {

            JSONTokener jsonTokener = new JSONTokener(content);

            JSONObject json = (JSONObject) jsonTokener.nextValue();

            String labelsFont = null;

            try {
                labelsFont = json.getString("font");
            } catch (Exception e) {
                e.printStackTrace();
            }


            JSONArray jArrObj = json.getJSONArray("labels");

            for (int i = 0; i < jArrObj.length(); i++) {

                try {

                    JSONObject gObj = jArrObj.getJSONObject(i);
                    LabelData o = new LabelData();

                    try {
                        String placeId = gObj.getString("place_id");
                        o.setPlaceId(placeId);
                    } catch (Exception e) {
                        System.out
                                .println("Label:parseAsList error key place_id");
                        e.printStackTrace();
                    }

                    try {
                        double top = gObj.getDouble("top");
                        double left = gObj.getDouble("left");

                        double right = gObj.getDouble("right");
                        double bottom = gObj.getDouble("bottom");

                        o.setRectTop(top);
                        o.setRectLeft(left);
                        o.setRectRight(right);
                        o.setRectBottom(bottom);

                    } catch (Exception e) {
                        System.out
                                .println("Label:parseAsList error keys top/left/bottom/right");
                        e.printStackTrace();
                    }

                    try {
                        int floor = gObj.getInt("floor");
                        o.setFloor(floor);
                    } catch (Exception e) {
                        System.out.println("Label:parseAsList error key floor");
                        e.printStackTrace();
                    }

                    try {
                        String desc = gObj.getString("description");
                        o.setDescription(desc);
                    } catch (Exception e) {
                        System.out
                                .println("Label:parseAsList error key description");
                        e.printStackTrace();
                    }


                    try {
                        double angle = gObj.getDouble("angle");
                        o.setAngle(angle);
                    } catch (Exception e) {
                        System.out
                                .println("Label:parseAsList error key angle");
                        e.printStackTrace();
                    }

                    int fl = o.getFloor();
                    List<LabelData> list = labelsToFloorMap.get(fl);
                    if (list == null) {
                        list = new ArrayList<LabelData>();
                        labelsToFloorMap.put(fl, list);
                    }

                    if (labelsFont != null) {
                        o.setFont(labelsFont);
                    }

                    list.add(o);

                } catch (Throwable t) {
                    t.printStackTrace();
                }

            }

            answer = true;
        } catch (Throwable t) {
            t.printStackTrace();
        }

        return answer;
    }

    public List<LabelData> getLabelsbyFloor(int floor) {
        return labelsToFloorMap.get(floor);
    }

    public List<LabelData> getLabelsOfCurrentFloor() {

        FacilityConf facConf = FacilityContainer.getInstance().getSelected();

        if (facConf == null) {
            return new ArrayList<LabelData>();
        }


        int floor = facConf.getSelectedFloor();


        return labelsToFloorMap.get(floor);
    }

    public void drawLabels(TouchImageView tiv) {


        LayerObject labelsLayer = tiv.getLayerByName("labels_layer");
        labelsLayer.clearSprites();


        FacilityConf facConf = FacilityContainer.getInstance().getSelected();

        if (facConf == null) {
            return;
        }


        int floor = facConf.getSelectedFloor();

        List<LabelData> list = labelsToFloorMap.get(floor);

        if (list == null) {
            return;
        }

        for (LabelData label : list) {
            if (label != null /*&& label.getDescription().startsWith("ABERCROMBIE & F")*/) {
                LabelDrawableSprite ls = new LabelDrawableSprite(label, tiv);
                ls.setAlpha(180);
                tiv.addDrawableLabel(ls);
            }

        }

        tiv.invalidate();
    }


}
