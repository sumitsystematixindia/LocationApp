package com.mlins.maping;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;

import com.mlins.orientation.OrientationMonitor;

import java.util.Observable;
import java.util.Observer;

public class CompassDrawable extends BitmapDrawable implements Observer {

    protected int mScreenRotation = 0;

    public CompassDrawable(Resources res, Bitmap bitmap) {
        super(res, bitmap);
    }

    public CompassDrawable(Resources res, String filepath) {
        super(res, filepath);
    }

    public CompassDrawable(Resources res) {
        super(res);
    }

    @Override
    public void draw(Canvas canvas) {
        float px = getBounds().left + getIntrinsicWidth() / 2;
        float py = getBounds().top + getIntrinsicHeight() / 2;
        float degrees = OrientationMonitor.getInstance().getAzimuth(mScreenRotation);
        canvas.save();
        canvas.rotate(-degrees, px, py);
        super.draw(canvas);
        canvas.restore();
    }

    @Override
    public void update(Observable observable, Object data) {
        invalidateSelf();
    }

    public void setScreenRotation(int sr) {
        mScreenRotation = sr;
    }
}
