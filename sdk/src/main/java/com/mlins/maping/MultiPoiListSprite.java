package com.mlins.maping;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;

import com.mlins.utils.PropertyHolder;

public class MultiPoiListSprite extends Drawable {

    private float x = -1;
    private float y = -1;
    private int number = -1;
    private Paint mPaint;
    private Boolean isVeseted = false;

    public MultiPoiListSprite(float poix, float poiy, int poinumber, Boolean isVeseted) {
        x = poix;
        y = poiy;
        number = poinumber;
        this.isVeseted = isVeseted;
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    }

    @Override
    public void draw(Canvas canvas) {
        if (x != -1 && y != -1) {
            if (isVeseted) {
                mPaint.setColor(Color.parseColor(PropertyHolder.getInstance().getMultiPoisVisitedPointColor()));
            } else {
                mPaint.setColor(Color.parseColor(PropertyHolder.getInstance().getMultiPoisPointColor()));
            }
            canvas.drawCircle(x, y, 25, mPaint);
            if (number != -1) {
                String text = String.valueOf(number);

//				Paint paint = new Paint();
                mPaint.setColor(Color.parseColor(PropertyHolder.getInstance().getMultiPoisPointNumberColor()));
                mPaint.setStyle(Paint.Style.FILL);
                mPaint.setTextAlign(Paint.Align.CENTER);
                mPaint.setAntiAlias(true);
                mPaint.setTextSize(30f);
                float w = mPaint.measureText(text, 0, text.length());
                mPaint.setTypeface(Typeface.DEFAULT_BOLD);
                canvas.drawText(text, x, y + w / 2, mPaint);
            }
        }

    }

    @Override
    public int getOpacity() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void setAlpha(int arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void setColorFilter(ColorFilter arg0) {
        // TODO Auto-generated method stub

    }

}
