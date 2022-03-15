package com.mlins.views;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.util.AttributeSet;

import com.mlins.locator.AssociativeData;
import com.mlins.locator.AssociativeDataSorter;
import com.mlins.locator.LocationCorrector;
import com.mlins.locator.LocationLocator;
import com.mlins.maping.LayerObject;
import com.mlins.maping.MyLocationDrawable;
import com.spreo.spreosdk.R;

import java.util.List;

public class MyLocationView extends TouchImageView {

    private Drawable myLocationMark;
    private Drawable myLocationRange;
    private PointF mMyLoction;
    private boolean turnMark = true;
    private boolean mylocationvisble = true;
    private Bitmap myLocationBmp = BitmapFactory.decodeResource(getResources(), R.drawable.mylocaion);

    public MyLocationView(Context context) {
        this(context, null);
    }

    public MyLocationView(Context context, AttributeSet attrs) {
        super(context, attrs);
        createClosestPoints();
        createMyLocationMark();
    }

    public boolean isTurnMark() {
        return turnMark;
    }

    public void setTurnMark(boolean turnMark) {
        this.turnMark = turnMark;
        MyLocationDrawable mydraw = (MyLocationDrawable) myLocationMark;
        mydraw.setTurning(turnMark);
    }

    private void createClosestPoints() {
        LayerObject mylayer = new LayerObject();
        mLayers.put("closestpt", mylayer);
    }

    public PointF getMyLocation() {
        // TODO Auto-generated method stub
        return mMyLoction;
    }

    public void setMyLocation(PointF p) {
        if (p != null && mylocationvisble) {
            Rect bounds = new Rect((int) (p.x - 15), (int) (p.y - 15),
                    (int) (p.x + 15), (int) (p.y + 15));
            myLocationMark.setBounds(bounds);
            Rect rbounds = new Rect((int) (p.x - 50), (int) (p.y - 50),
                    (int) (p.x + 50), (int) (p.y + 50));
            myLocationRange.setBounds(rbounds);
            mMyLoction = p;
            //show my location marker
            getLayerByName("myloc").removeSprite(myLocationMark);
            getLayerByName("myloc").addSprite(myLocationMark);
            getLayerByName("myloc").show();
        }
    }

    public void createMyLocationMark() {
        LayerObject mylayer = new LayerObject();
        mLayers.put("myloc", mylayer);
        //hide my location layer
        getLayerByName("myloc").hide();
        // remove location sprite
        if (myLocationMark != null) {
            getLayerByName("myloc").removeSprite(myLocationMark);
        }

        mylayer.setScaleMode(LayerObject.MODE_SCALE_SPRITES);
//		myLocationMark = new ShapeDrawable(new OvalShape());
        Resources res = getResources();
        Bitmap cb = myLocationBmp;
        if (type == IMG_TYPE_ROUTEOVERVIEW) {
            cb = BitmapFactory.decodeResource(res, R.drawable.routelocation);

        }
        myLocationMark = new MyLocationDrawable(res, cb, this);
        if (type == IMG_TYPE_ROUTEOVERVIEW) {
            ((MyLocationDrawable) myLocationMark).setOverview(true);
        }
//		((ShapeDrawable) myLocationMark).getPaint().setColor(
//				Color.parseColor("#0dd256"));
        myLocationRange = new ShapeDrawable(new OvalShape());
        ((ShapeDrawable) myLocationRange).getPaint().setColor(
                Color.parseColor("#0dd256"));
        myLocationRange.setAlpha(128);

//		getLayerByName("myloc").addSprite(myLocationRange);


    }

    public void drawClosestPoints(List<AssociativeDataSorter> pts) {
        LayerObject layer = getLayerByName("closestpt");
        layer.clearSprites();
        if (pts != null || pts.size() > 3) {
            PointF point = null;
            int r = 5;

            if (pts != null && pts.size() > 2) {
                AssociativeData pt = pts.get(0).data;
                if (pt != null) {
                    point = pt.point;
                    r = 5;
                    drawPoint(point, Color.RED, r);
                }
                pt = pts.get(1).data;
                if (pt != null) {
                    point = pt.point;
                    point = pt.point;
                    r = 5;
                    drawPoint(point, Color.BLUE, r);
                }


                pt = pts.get(2).data;
                if (pt != null) {
                    point = pt.point;
                    r = 5;
                    drawPoint(point, Color.GRAY, r);
                }


            }
        }


        drawAvePoint(layer);
        drawDeadReaconing(layer);
    }

    private void drawDeadReaconing(LayerObject layer) {
        PointF apoint = LocationCorrector.getInstance().getDeadReckoning();
        if (apoint != null) {
            drawPoint(apoint, Color.GREEN, 15);
        }

    }

    public void drawAvePoint(LayerObject layer) {
        //PointF apoint = AsociativeMemoryLocator.getInstance().getLastAverage();
        //XXX NDK
//		FLocation loc=new FLocation(); 
//		NdkLocationFinder.getInstance().getLastpt(loc);
//		PointF apoint =new PointF(loc.getX(),loc.getY());
        PointF apoint = LocationLocator.getInstance().getLastAverage();
        if (apoint != null) {
            drawPoint(apoint, Color.RED, 20);
        }
    }

    public void drawPoint(PointF point, int color, int size) {
        if (point != null) {
            ShapeDrawable dot = new ShapeDrawable(new OvalShape());
            dot.getPaint().setColor(color);
            int x = (int) point.x;
            int y = (int) point.y;
            dot.setBounds(x - size, y - size, x + size, y + size);
            getLayerByName("closestpt").addSprite(dot);
        }
    }

    public void showMyLocation() {
        if (mylocationvisble == false) {
            createMyLocationMark();
            //		invalidate();
            mylocationvisble = true;
        }
    }

    public void hideMyLocation() {
        if (mylocationvisble == true) {
            getLayerByName("myloc").removeSprite(myLocationMark);
            //		invalidate();
            mylocationvisble = false;
        }
    }

    public void removeMyLocationMark() {
        getLayerByName("myloc").removeSprite(myLocationMark);
    }

    public Bitmap getMyLocationBmp() {
        return myLocationBmp;
    }

    public void setMyLocationBmp(Bitmap myLocationBmp) {
        this.myLocationBmp = myLocationBmp;
        if (myLocationMark != null) {
            removeMyLocationMark();
            createMyLocationMark();
        }
    }
}
