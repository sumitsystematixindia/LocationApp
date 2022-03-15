package com.mlins.maping;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;

import com.mlins.utils.gis.GisLine;

public class GisSprite extends Drawable {

    GisLine Line;
    private Paint mPaint;

    public GisSprite(GisLine l) {
        Line = l;
        mPaint = new Paint();
    }

    @Override
    public void draw(Canvas canvas) {
        float x1 = (float) Line.point1.getX();
        float y1 = (float) Line.point1.getY();
        float x2 = (float) Line.point2.getX();
        float y2 = (float) Line.point2.getY();
        if (Line.isParticipateInNavigation()) {
            mPaint.setColor(Color.BLUE);
        } else {
            mPaint.setColor(Color.BLUE);
        }
        mPaint.setStrokeWidth(3);
        canvas.drawLine(x1, y1, x2, y2, mPaint);
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
