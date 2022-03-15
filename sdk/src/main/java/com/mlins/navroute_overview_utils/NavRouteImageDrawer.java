package com.mlins.navroute_overview_utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;

import com.mlins.aStar.FloorNavigationPath;
import com.mlins.aStar.GisSegment;
import com.mlins.aStar.NavigationPath;
import com.mlins.aStar.aStarData;
import com.mlins.aStar.aStarMath;
import com.mlins.maping.IconSprite;
import com.mlins.maping.LayerObject;
import com.mlins.utils.FacilityContainer;
import com.mlins.utils.FloorData;
import com.mlins.utils.PathConvertor;
import com.mlins.utils.PropertyHolder;
import com.mlins.utils.ResourceDownloader;
import com.mlins.utils.gis.Location;
import com.mlins.views.TouchImageView;
import com.spreo.spreosdk.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NavRouteImageDrawer extends TouchImageView {

    protected static final int ALL_FLOORS = -100;
    //private Context ctx= null;
    //private MyLocationView iv;
    List<Integer> floors = new ArrayList<Integer>();
    Matrix matrix = new Matrix();
    private int floorHeight;

    public NavRouteImageDrawer() {
        super(PropertyHolder.getInstance().getMlinsContext());
        //this.ctx=ctx;
        //clearPoi();
        type = IMG_TYPE_ROUTEOVERVIEW;
    }

    private void clearPoi() {
        LayerObject layer = getLayerByName("poi");
        if (layer != null) {
            layer.clearSprites();
        }

    }

//		public NavRouteImageDrawer(Context context, AttributeSet attrs) {
//			super(context, attrs);
//			clearPoi();
//			type = IMG_TYPE_ROUTEOVERVIEW;
//		}

    public void setFloor(int floor) {
        List<FloorData> currentMapData = FacilityContainer.getInstance().getSelected()
                .getFloorDataList();

        String currentMapUri = currentMapData.get(floor).mapuri;

        Bitmap bm = ResourceDownloader.getInstance().getLocalBitmap(
                currentMapUri, true);
        setImageBitmap(bm);
        clearPoi();
        setNavigationPath(floor);
    }

    private void setNavigationPath(int floor) {
//        NavigationPath navigation = aStarData.getInstance().getCurrentPath(); // new
//        // NavigationPath(p);
//        if (navigation != null) {
//            List<Drawable> sprites = new ArrayList<Drawable>();
//            sprites = PathConvertor
//                    .convertPath(navigation, getContext(), floor);
//
//            LayerObject path = getLayers().get("path");
//            path.clearSprites();
//            path.addAll(sprites);
//            LayerObject instructionslayer = getLayers().get("instructions");
//            instructionslayer.clearSprites();
//            Location dest = aStarData.getInstance().getDestination();
//            PointF myloc = aStarData.getInstance().getMyLocation();
//            int currentz = FacilityContainer.getInstance().getSelected().getSelectedFloor();
////            List<GisSegment> floorpath = navigation.getPathByZ(floor);
//            if (currentz == floor) {
////					Bitmap icon = BitmapFactory.decodeResource(getResources(),
////							R.drawable.routelocation);
////					IconSprite ics = new IconSprite(icon);
////					ics.scaleBitmap(0.2);
////					float myX = (float) myloc.x;
////					float myY = (float) myloc.y;
////					ics.setLoc(new PointF(myX, myY));
////					instructionslayer.addSprite(ics);
//            } else {
//                Bitmap icon = BitmapFactory.decodeResource(getResources(),
//                        R.drawable.elevator);
//                IconSprite ics = new IconSprite(icon);
//                ics.scaleBitmap(0.8);
//                float x = (float) floorpath.get(0).getLine().getPoint1().getX();
//                float y = (float) floorpath.get(0).getLine().getPoint1().getY();
//                PointF switchloc = new PointF(x, y);
//                ics.setLoc(switchloc);
//                instructionslayer.addSprite(ics);
//            }
//            if (dest.getZ() == floor) {
//                Bitmap icon = BitmapFactory.decodeResource(getResources(),
//                        R.drawable.destination);
//                IconSprite ics = new IconSprite(icon);
//                ics.scaleBitmap(0.4);
//                float destX = (float) dest.getX();
//                float destY = (float) dest.getY();
//                ics.setLoc(new PointF(destX, destY));
//                instructionslayer.addSprite(ics);
//
//            } else {
//                Bitmap icon = BitmapFactory.decodeResource(getResources(),
//                        R.drawable.elevator);
//                IconSprite ics = new IconSprite(icon);
//                ics.scaleBitmap(0.8);
//                float x = (float) floorpath.get(floorpath.size() - 1).getLine()
//                        .getPoint2().getX();
//                float y = (float) floorpath.get(floorpath.size() - 1).getLine()
//                        .getPoint2().getY();
//                PointF switchloc = new PointF(x, y);
//                ics.setLoc(switchloc);
//                instructionslayer.addSprite(ics);
//            }
//
//            path.show();
//        }
    }


    public Map<Integer, Bitmap> getAllFloorsBitmap() {

        Map<Integer, Bitmap> map = null;

        NavigationPath nav = aStarData.getInstance().getCurrentPath();

        if (nav != null) {
            map = new HashMap<Integer, Bitmap>();
            List<FloorNavigationPath> fullpath = nav.getFullPath();

            for (FloorNavigationPath o : fullpath) {
                int floor = (int) o.getZ();
                Bitmap tmp = getFloorTempBitmap(floor);
                setFloor(floor);
                layout(0, 0, tmp.getWidth(), tmp.getHeight());
                Bitmap fbm = getFloorBitmap();
                map.put(floor, fbm);
            }
        }
        //for (Integer i : floors) {


        // tmp.recycle();
        //}
/**
 //int listsize = floorsbm.size();
 //			int w = fbm.getWidth();
 floorHeight = fbm.getHeight();
 //			Bitmap br = Bitmap.createBitmap((int) (w * 1.7f), (int) (floorHeight
 //					* listsize * 1.2), Bitmap.Config.ARGB_8888);
 Canvas c = new Canvas(fbm);
 Paint p = new Paint();
 float left = 0;
 float top = 0;

 c.drawBitmap(fbm, left, top, p);

 //			for (Bitmap o : floorsbm) {
 //				c.drawBitmap(o, left, top, p);
 //				// left += w / 2;
 //				top += floorHeight;
 //				o.recycle();
 //			}

 //			List<PointF> pointlist = new ArrayList<PointF>();
 //			if (floors.size() > 1) {
 //				List<Location> SwitchPoint = getSwitchPoint(floors);
 //				for (Location o : SwitchPoint) {
 //					PointF pt = translatePoint(o.getX(), o.getY(),
 //							SwitchPoint.indexOf(o));
 //					pointlist.add(pt);
 //				}
 //				drawelElvatorsLine(c, pointlist);
 //			}

 matrix = new Matrix();
 //			matrix.setSkew(0.5f, 0);
 //			Bitmap b = Bitmap.createBitmap(br.getWidth(), br.getHeight(),
 //					Bitmap.Config.ARGB_8888);
 Canvas c1 = new Canvas(fbm);
 Paint p2 = new Paint();
 c1.drawBitmap(fbm, matrix, p2);
 //playerPoints = new ArrayList<PointF>();
 // top

 int destz = (int) aStarData.getInstance().getDestination().getZ();
 int mylocz = FacilityConf.getInstance().getSelectedFloor();

 //playerPoints.addAll(getPlayerFloorPoints(mylocz));
 //			if (floors.size() > 1) {
 //				// middle
 //
 //				float[] pts = new float[2];
 //				PointF start = pointlist.get(0);
 //				PointF end = pointlist.get(1);
 //				List<PointF> list = aStarMath.divideLine(start, end, 20);
 //				for (PointF pointF : list) {
 //
 //					pts[0] = pointF.x;
 //					pts[1] = pointF.y;
 //					matrix.mapPoints(pts);
 //					pointF.x = pts[0];
 //					pointF.y = pts[1];
 //				}
 //
 //				if (floors.get(0) == destz) {
 //					Collections.reverse(list);
 //				}
 //				//playerPoints.addAll(list);
 //				// bottom
 //				//playerPoints.addAll(getPlayerFloorPoints(destz));
 //
 //				//add origin
 //				//playerPoints.add(playerPoints.get(0));
 //
 //				//if (routeplayer == null) {
 //				//	routeplayer = new RoutePlayer(this);
 //				//}
 //
 //
 //				//routeplayer.setPlayingpoints(playerPoints);
 //				//createMyLocationMark(iv);
 //				//try {
 //				///	routeplayer.start();
 //				//} catch (Throwable t){
 //				//	t.printStackTrace();
 //				//}
 //
 //
 //			}



 // for (PointF f : playerPoints) {
 // c1.drawCircle(f.x, f.y, 10, p2);
 // }
 */
        return map;

    }


    public Bitmap getFloorTempBitmap(int floor) {
        Bitmap result = null;
        List<FloorData> currentMapData = FacilityContainer.getInstance().getSelected()
                .getFloorDataList();

        String currentMapUri = currentMapData.get(floor).mapuri;

        result = ResourceDownloader.getInstance().getLocalBitmap(currentMapUri,
                true);
        return result;
    }


    public Bitmap getFloorBitmap() {
        // Get the dimensions of the view so we can re-layout the view at its
        // current size
        // and create a bitmap of the same size
        int width = getWidth();
        int height = getHeight();

        int measuredWidth = MeasureSpec.makeMeasureSpec(width,
                MeasureSpec.EXACTLY);
        int measuredHeight = MeasureSpec.makeMeasureSpec(height,
                MeasureSpec.EXACTLY);

        // Cause the view to re-layout
        measure(measuredWidth, measuredHeight);
        layout(0, 0, getMeasuredWidth(), getMeasuredHeight());

        // Create a bitmap backed Canvas to draw the view into
        Bitmap b = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);

        // Now that the view is laid out and we have a canvas, ask the view to
        // draw itself into the canvas
        draw(c);

        // Matrix matrix = new Matrix();
        //
        // matrix.setSkew(0.5f, 0);
        // width *= 1.5;
        //
        // Bitmap br = Bitmap.createBitmap(width, height,
        // Bitmap.Config.ARGB_8888);
        // Canvas c1 = new Canvas(br);
        // Paint p = new Paint();
        // c1.drawBitmap(b, matrix, p);
        // return br;
        return b;
    }


    private PointF translatePoint(double x, double y, int floornumberinlist) {
        PointF result = new PointF();
        result.x = (float) (x / 3);
        result.y = (float) (y / 3);
        result.y += floornumberinlist * floorHeight;
        return result;
    }

    private void drawelElvatorsLine(Canvas c, List<PointF> pointlist) {
        float p1x = (float) pointlist.get(0).x;
        float p1y = (float) pointlist.get(0).y;
        float p2x = (float) pointlist.get(1).x;
        float p2y = (float) pointlist.get(1).y;
        Paint p = new Paint();
        p.setColor(Color.parseColor("#701E84"));
        p.setStrokeWidth(14);
        // c.drawCircle(p1x, p1y, 10, p);
        // c.drawCircle(p2x, p2y, 10, p);
        PointF p1 = pointlist.get(0);
        PointF p2 = pointlist.get(1);
        List<PointF> list = aStarMath.divideLine(p1, p2, 20);
        p.setAlpha(120);
        for (PointF pointF : list) {
            c.drawCircle(pointF.x, pointF.y, 5, p);
        }
        // c.drawLine(p1x, p1y, p2x ,p2y , p);
    }

    private List<Location> getSwitchPoint(List<Integer> floors) {
        List<Location> result = new ArrayList<Location>();
        NavigationPath navigation = aStarData.getInstance().getCurrentPath(); // new
        if (navigation != null) {
            Location dest = aStarData.getInstance().getDestination();
            for (Integer o : floors) {
//                List<GisSegment> floorpath = navigation.getPathByZ(o);
//                float x = (float) floorpath.get(floorpath.size() - 1).getLine()
//                        .getPoint2().getX();
//                float y = (float) floorpath.get(floorpath.size() - 1).getLine()
//                        .getPoint2().getY();
//                if (o == dest.getZ()) {
//                    x = (float) floorpath.get(0).getLine().getPoint1().getX();
//                    y = (float) floorpath.get(0).getLine().getPoint1().getY();
//                }
//
//                Location switchloc = new Location(x, y, o.floatValue());
//
//                result.add(switchloc);
            }

        }
        return result;
    }

}
