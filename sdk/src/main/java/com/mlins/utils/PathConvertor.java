package com.mlins.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;

import com.mlins.aStar.GisSegment;
import com.mlins.aStar.NavigationPath;
import com.mlins.maping.InstructionSprite;
import com.mlins.maping.LineSprite;
import com.mlins.maping.MultiPoiLineSprite;

import java.util.ArrayList;
import java.util.List;

public class PathConvertor {
    public static List<Drawable> convertPath(NavigationPath path, Context ctx, int z) {

//		PointF p = aStarData.getInstance().getMyLocation();
//		GisPoint start = new GisPoint(p.x, p.y, z);
//		Location poi = aStarData.getInstance().getPoilocation();
//		GisPoint finish = new GisPoint(poi.getX(), poi.getY(), poi.getZ());

//        List<GisSegment> data = path.getPathByZ(z);

        List<Drawable> result = new ArrayList<Drawable>();
//        if (data != null) {
//            for (GisSegment s : data) {
//                GisSegment nextl = null;
//                if (data.indexOf(s) < data.size() - 1) {
//                    nextl = data.get(data.indexOf(s) + 1);
//                }
//                boolean isfirst = false;
//                if (data.indexOf(s) == 0) {
//                    isfirst = true;
//                }
//                LineSprite l = new LineSprite(s, nextl, isfirst);
//                result.add(l);
//            }
//        }
        return result;
    }

    public static List<Drawable> convertInstructions(NavigationPath path, Context ctx, int z) {

//		PointF p = aStarData.getInstance().getMyLocation();
//		GisPoint start = new GisPoint(p.x, p.y, z);
//		Location poi = aStarData.getInstance().getPoilocation();
//		GisPoint finish = new GisPoint(poi.getX(), poi.getY(), poi.getZ());

//        List<GisSegment> data = path.getPathByZ(z);

        List<Drawable> result = new ArrayList<Drawable>();
//        if (data != null) {
//            for (GisSegment s : data) {
//                GisSegment nextl = null;
//                if (data.indexOf(s) < data.size() - 1) {
//                    nextl = data.get(data.indexOf(s) + 1);
//                }
//                InstructionSprite l = new InstructionSprite(s, nextl);
//                result.add(l);
//            }
//        }
        return result;
    }


    public static List<Drawable> convertMultiPoiPath(NavigationPath path, Context ctx, int z) {


//        List<GisSegment> data = path.getPathByZ(z);

        List<Drawable> result = new ArrayList<Drawable>();
//        if (data != null) {
//            for (GisSegment s : data) {
//                GisSegment nextl = null;
//                if (data.indexOf(s) < data.size() - 1) {
//                    nextl = data.get(data.indexOf(s) + 1);
//                }
//                boolean isfirst = false;
//                if (data.indexOf(s) == 0) {
//                    isfirst = true;
//                }
//                MultiPoiLineSprite l = new MultiPoiLineSprite(s, nextl, isfirst);
//                result.add(l);
//            }
//        }
        return result;
    }


}
