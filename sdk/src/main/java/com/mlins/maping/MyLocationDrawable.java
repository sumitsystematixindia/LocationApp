package com.mlins.maping;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;

import com.mlins.orientation.OrientationMonitor;
import com.mlins.utils.FacilityConf;
import com.mlins.utils.FacilityContainer;
import com.mlins.utils.PropertyHolder;
import com.mlins.views.MyLocationView;
import com.spreo.nav.enums.MapRotationType;

import java.util.Observable;
import java.util.Observer;

public class MyLocationDrawable extends BitmapDrawable implements Observer {

    Bitmap bitmap = null;
    boolean turning = true;
    boolean overview = false;
    private int mScreenRotation = 0;
    private MyLocationView myLocView = null;

    public MyLocationDrawable(Resources res, Bitmap bitmap, MyLocationView v) {
        super(res, bitmap);
        this.bitmap = bitmap;
        myLocView = v;
        // TODO Auto-generated constructor stub
    }

    public static Bitmap RotateBitmap(Bitmap source, double angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate((float) angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(),
                source.getHeight(), matrix, true);
    }

    public boolean isOverview() {
        return overview;
    }

    public void setOverview(boolean overview) {
        this.overview = overview;
    }

    public boolean isTurning() {
        return turning;
    }

    public void setTurning(boolean turning) {
        this.turning = turning;
    }

    @Override
    public void draw(Canvas canvas) {


        Rect bounds = getBounds();
        float px = bounds.exactCenterX();
        float py = bounds.exactCenterY();
        Bitmap bmp = bitmap;
        FacilityConf facConf = FacilityContainer.getInstance().getCurrent();

        if (!PropertyHolder.getInstance().isLocationPlayer() && turning) {
            //if (!PropertyHolder.getInstance().isRotatingMap()) { //|| !myLocView.getFollowMeMode()
            if (PropertyHolder.getInstance().getRotatingMapType() != MapRotationType.COMPASS
                    && PropertyHolder.getInstance().getRotatingMapType() != MapRotationType.STATIC) {


                if (facConf != null) {
                    float degrees = OrientationMonitor.getInstance().getAzimuth(mScreenRotation) - facConf.getFloorRotation() + myLocView.getImageRotation();
                    //			degrees = (degrees % 360.0f);
                    bmp = RotateBitmap(bitmap, degrees);
                }

            } else if (!myLocView.getFollowMeMode() && PropertyHolder.getInstance().getRotatingMapType() != MapRotationType.STATIC) {

                if (facConf != null) {
                    float offset = myLocView.getImageRotation();
                    float degrees = OrientationMonitor.getInstance().getAzimuth(mScreenRotation) - facConf.getFloorRotation();
                    float angle = (degrees - offset) % 360.0f;
                    bmp = RotateBitmap(bitmap, angle);
                }

            }
        }
        px -= bmp.getWidth() / 2;
        py -= bmp.getHeight() / 2;

        canvas.drawBitmap(bmp, px, py, null);


    }

    @Override
    public void update(Observable observable, Object data) {
        invalidateSelf();

    }

}
