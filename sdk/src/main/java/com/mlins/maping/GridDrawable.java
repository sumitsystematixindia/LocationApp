package com.mlins.maping;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;

public class GridDrawable extends Drawable {

    private static final int COLUMNS = 7;
    private static final int ROWS = 8;


    private Paint mPaint = new Paint();
    private int mNumColumns = COLUMNS;
    private int mNumRows = ROWS;

    @Override
    public void draw(Canvas canvas) {
        mPaint.setColor(Color.WHITE);
        Rect r = getBounds();
        int h = r.bottom - r.top;
        int w = r.right - r.left;
        int count = canvas.save();
        canvas.translate(r.left, r.top);
        canvas.save();
        int i;
        for (i = 0; i < mNumRows; i++) { // draw rows:
            canvas.translate(0, (float) h / mNumRows);
            canvas.drawLine(0, 0, w, 0, mPaint);
        }
        canvas.restore();
        for (i = 0; i < mNumColumns; i++) { // draw columns:
            canvas.translate((float) w / mNumColumns, 0);
            canvas.drawLine(0, 0, 0, h, mPaint);
        }
        canvas.restoreToCount(count);
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
