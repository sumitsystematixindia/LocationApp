package com.mlins.utils;

import static android.content.ContentValues.TAG;

import android.util.Log;

import com.mlins.maping.GisSprite;
import com.mlins.maping.LayerObject;
import com.mlins.utils.gis.GisData;
import com.mlins.utils.gis.GisLine;
import com.mlins.views.TouchImageView;

import java.util.List;

public class GisDrawHelper {
    private static GisDrawHelper instance = null;

    //doesn't hold any state, no need to rework
    public static GisDrawHelper getInstance() {
        if (instance == null) {
            instance = new GisDrawHelper();

        }
        return instance;

    }

    public static void releaseInstance() {
        if (instance != null) {
            instance = null;
        }
    }

    public void drawGis(TouchImageView tiv) {
        LayerObject Gislayer = tiv.getLayerByName("gis");
        Gislayer.clearSprites();
        boolean isGis = PropertyHolder.getInstance().isDevelopmentMode();
        if (isGis || PropertyHolder.getInstance().isShowGis()) {
            List<GisLine> lines = GisData.getInstance().getLines();
            for (GisLine l : lines) {
                Log.d(TAG, "drawGis: "+l);
                GisSprite gs = new GisSprite(l);
                tiv.addGis(gs);
            }
        }
        tiv.invalidate();
    }
}
