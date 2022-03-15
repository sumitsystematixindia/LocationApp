package com.mlins.maping;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;

import com.mlins.aStar.GisSegment;
import com.mlins.utils.PropertyHolder;

public class MultiPoiLineSprite extends Drawable {
    GisSegment line;
    GisSegment nextline;
    boolean isFirst = false;
    private Paint mPaint;

    // AddTry
    public MultiPoiLineSprite(GisSegment l, GisSegment nextl, boolean isfirst) {
        line = l;
        mPaint = new Paint(Paint.FILTER_BITMAP_FLAG);
        mPaint.setAntiAlias(true);
        mPaint.setFilterBitmap(true);

        nextline = nextl;
        isFirst = isfirst;

    }


    public static Bitmap RotateBitmap(Bitmap source, double angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate((float) angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(),
                source.getHeight(), matrix, true);
    }

    @Override
    public void draw(Canvas canvas) {

        float x1 = (float) line.getLine().getPoint1().getX();
        float y1 = (float) line.getLine().getPoint1().getY();
        float x2 = (float) line.getLine().getPoint2().getX();
        float y2 = (float) line.getLine().getPoint2().getY();
        mPaint.setColor(Color.parseColor(PropertyHolder.getInstance().getMultiPoisRouteColor()));
        mPaint.setStrokeWidth(14);
        canvas.drawLine(x1, y1, x2, y2, mPaint);

        if (isFirst) {
            canvas.drawCircle(x1, y1, 7, mPaint);
        }

        canvas.drawCircle(x2, y2, 7, mPaint);

    }


    @Override
    public int getOpacity() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void setAlpha(int alpha) {
        // TODO Auto-generated method stub

    }

    @Override
    public void setColorFilter(ColorFilter cf) {
        // TODO Auto-generated method stub

    }
}
